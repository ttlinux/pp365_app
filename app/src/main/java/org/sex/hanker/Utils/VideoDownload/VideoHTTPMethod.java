package org.sex.hanker.Utils.VideoDownload;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.iheartradio.m3u8.Encoding;
import com.iheartradio.m3u8.Format;
import com.iheartradio.m3u8.ParseException;
import com.iheartradio.m3u8.PlaylistException;
import com.iheartradio.m3u8.PlaylistParser;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;

import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.OnReceiveDataListener;
import org.sex.hanker.Utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2018/8/2.
 */
public class VideoHTTPMethod {

    private final static int Text_DEFAULT_BUFFER_SIZE = 1024;
    public final static int Video_DEFAULT_BUFFER_SIZE = 1024*1024;

    public static void ProcessHttpsConnectForVideo(final Context context, final LocalVideoBean localVideoBean,
                                                   final OnReceiveDataListener listener) {
        String requesturl=localVideoBean.getCOLUMN_URL();
        boolean isM3U8=localVideoBean.getSUFFIX().toLowerCase().equalsIgnoreCase("m3u8");
        long offset= 0;
        int index = 0;
        if(isM3U8)
        {
            if(localVideoBean.getM3U8_items()==null || localVideoBean.getM3U8_items().size()==0)
            {
                if(listener!=null)
                {
                    listener.OnFail("TS文件列表为空");
                }
                return;
            }

            while (localVideoBean.getM3U8_items().get(index).getSTATUS() == VideoSQL.Finished) {
                index++;
            }
            LocalVideoBean.M3U8_ITEM m3U8_item=localVideoBean.getM3U8_items().get(index);
            requesturl=m3U8_item.getTS_URL();
            offset=IOUtil.isComplete(m3U8_item.getLocalPath());
        }
        else
        {
            offset= IOUtil.isComplete(localVideoBean.getLocalPath());

        }
        if(offset==IOUtil.Complete){
            if(listener!=null)
                listener.OnFail("文件已经下载到SD卡，如需重新下载请删除原文件");
            return;
        }

        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{new TrustAnyTrustManager()},
                    new java.security.SecureRandom());

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (KeyManagementException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        InputStream in = null;
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        try {
            URL url = new URL(requesturl);
            LogTools.e("VideoHTTPMethod","Https请求的地址" + url.toString());
            // HttpURLConnection conn = (HttpURLConnection)
            // url.openConnection(proxy);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
//			conn.setFollowRedirects(true);
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setRequestMethod("GET");
            if(offset>-1)
            conn.setRequestProperty("Range", "bytes=" + offset + "-");
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestProperty("Cookie", BOUNDARY);
            conn.setRequestProperty("Referer", requesturl);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
            // conn.addRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-type",
                    getSupposablyMime(requesturl));
            in = conn.getInputStream();

            if (listener != null) {
                if(isM3U8)
                {
                    listener.OnSuccess(conn.getContentLength(),in,localVideoBean,index);
                }
                else
                {
                    listener.OnSuccess(conn.getContentLength(),in,localVideoBean);
                }
            }
        } catch (MalformedURLException e) {
            LogUtil.log("目标地址 " + requesturl);
            LogUtil.log("url格式不规范:" + e.toString());
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if (listener != null)
                listener.OnFail("视频地址错误");
        } catch (IOException e) {
            LogUtil.log("IO操作错误：" + e.toString());
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if (listener != null)
                listener.OnFail("文件操作错误");
        }
    }

    public static void ProcessHttpConnectForVideo(final Context context, final LocalVideoBean localVideoBean,
                                                 final OnReceiveDataListener listener) {
        String requesturl=localVideoBean.getCOLUMN_URL();
        boolean isM3U8=localVideoBean.getSUFFIX().toLowerCase().equalsIgnoreCase("m3u8");
        long offset= 0;
        int index = 0;
        if(isM3U8)
        {
            if(localVideoBean.getM3U8_items()==null || localVideoBean.getM3U8_items().size()==0)
            {
                if(listener!=null)
                {
                  listener.OnFail("TS文件列表为空");
                }
                return;
            }

            while (localVideoBean.getM3U8_items().get(index).getSTATUS() == VideoSQL.Finished) {
                index++;
            }
            LocalVideoBean.M3U8_ITEM m3U8_item=localVideoBean.getM3U8_items().get(index);
            requesturl=m3U8_item.getTS_URL();
            offset=IOUtil.isComplete(m3U8_item.getLocalPath());
        }
        else
        {
            offset= IOUtil.isComplete(localVideoBean.getLocalPath());

        }
        if(offset==IOUtil.Complete){
            if(listener!=null)
                listener.OnFail("文件已经下载到SD卡，如需重新下载请删除原文件");
            return;
        }


        InputStream in = null;
        try {
            URL url = new URL(requesturl);
            LogTools.e("VideoHTTPMethod", "Http请求的地址" + url.toString());
            // HttpURLConnection conn = (HttpURLConnection)
            // url.openConnection(proxy);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            if(offset>-1)
                conn.setRequestProperty("Range", "bytes=" + offset + "-");
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestProperty("Cookie", System.currentTimeMillis() + "");
            conn.setRequestProperty("Referer", requesturl);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)");
            conn.setRequestProperty("Content-type",
                    getSupposablyMime(requesturl));
            // conn.addRequestProperty("connection", "keep-alive");

            in = conn.getInputStream();


            if (listener != null) {
                if(isM3U8)
                {
                    listener.OnSuccess(conn.getContentLength(),in,localVideoBean,index);
                }
                else
                {
                    listener.OnSuccess(conn.getContentLength(),in,localVideoBean);
                }
            }
        } catch (MalformedURLException e) {
            LogUtil.log("目标地址 " + requesturl);
            LogUtil.log("url格式不规范:" + e.toString());
            if (listener != null)
            listener.OnFail("视频地址错误");
        } catch (IOException e) {
            LogUtil.log("IO操作错误：" + e.toString());
            if (listener != null)
                listener.OnFail("文件操作错误");
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public static String getSupposablyMime(String url) {
        MimeTypeMap mimes = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (TextUtils.isEmpty(extension)) {
            return "application/octet-stream";
        }
        String mime = mimes.getMimeTypeFromExtension(extension);
        if (TextUtils.isEmpty(mime)) {
            return "application/octet-stream";
        }
        return mime;
    }

    static class LogUtil {
        public static void log(String values) {
            LogTools.e("VideoHTTPMethod", values);
        }
    }

    private static class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {

        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }



}
