package org.sex.hanker.Utils.VideoDownload;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.OnReceiveDataListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/8/2.
 */
public class VideoHTTPMethod {

    private final static int Text_DEFAULT_BUFFER_SIZE = 1024;
    public final static int Video_DEFAULT_BUFFER_SIZE = 1024 * 1024;
    public final static int ConnectTimeout=15000;
    public final static int ReadTimeout=15000;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static SSLConnectionSocketFactory sslsf = null;
    private static PoolingHttpClientConnectionManager cm = null;
    private static SSLContextBuilder builder = null;
    static {
        try {
            builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates,
                                         String s) throws CertificateException {
                    return true;
                }
            });
            sslsf = new SSLConnectionSocketFactory(builder.build(),
                    new String[] { "SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2" },
                    null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder
                    .<ConnectionSocketFactory> create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslsf).build();
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);// max connection
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void ProcessHttpConnectForVideo(final Context context, final LocalVideoBean localVideoBean,
                                                  final OnReceiveDataListener listener) {
        String requesturl = localVideoBean.getCOLUMN_URL();
        boolean isM3U8 = localVideoBean.getSUFFIX().toLowerCase().equalsIgnoreCase("m3u8");
        long offset = 0;
        int index = 0;
        if (isM3U8) {
            if (localVideoBean.getM3U8_items() == null || localVideoBean.getM3U8_items().size() == 0) {
                if (listener != null) {
                    listener.OnFail("TS文件列表为空");
                }
                return;
            }

            while (localVideoBean.getM3U8_items().get(index).getSTATUS() == VideoSQL.Finished) {
                index++;
            }
            LocalVideoBean.M3U8_ITEM m3U8_item = localVideoBean.getM3U8_items().get(index);
            requesturl = m3U8_item.getTS_URL();
            offset = IOUtil.isComplete(m3U8_item.getLocalPath());
        } else {
            offset = IOUtil.isComplete(localVideoBean.getLocalPath());

        }
        if (offset == IOUtil.Complete) {
            if (listener != null)
                listener.OnFail("文件已经下载到SD卡，如需重新下载请删除原文件");
            return;
        }

        Map<String, String> header=new HashMap<>();
        header.put("Cookie", java.util.UUID.randomUUID().toString());
        header.put("Referer", requesturl);
        header.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        header.put("Accept-Encoding", "identity");
        if (offset > -1)
        header.put("Range", "bytes=" + offset + "-");
        header.put("Content-type", getSupposablyMime(requesturl));

        try {
            get(requesturl,header,null,localVideoBean,listener,isM3U8,index);
        } catch (Exception e) {
            e.printStackTrace();
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


    /**
     * httpClient post请求
     *
     * @param url
     *            请求url
     * @param header
     *            头部信息
     * @param param
     *            请求参数 form提交适用
     * @param entity
     *            请求实体 json/xml提交适用
     * @return 可能为空 需要处理
     * @throws Exception
     *
     */
    public static String post(String url, Map<String, String> header,
                              Map<String, String> param, HttpEntity entity) throws Exception {
        String result = "";
        CloseableHttpClient httpClient = null;
        try {
            httpClient = getHttpClient();
            HttpPost httpPost = new HttpPost(url);
            // 设置头信息
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpPost.addHeader(entry.getKey(), entry.getValue());
                }
            }
            // 设置请求参数
            if (param != null) {
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    // 给参数赋值
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry
                            .getValue()));
                }
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                        formparams, Consts.UTF_8);
                httpPost.setEntity(urlEncodedFormEntity);
            }
            // 设置实体 优先级高
            if (entity != null) {
                httpPost.setEntity(entity);
            }
            HttpResponse httpResponse = httpClient.execute(httpPost);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity resEntity = httpResponse.getEntity();
                result = EntityUtils.toString(resEntity);
            } else {
                result = readHttpResponse(httpResponse);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
        return result;
    }

    /**
     * httpClient post请求
     *
     * @param url
     *            请求url
     * @param header
     *            头部信息
     * @param param
     *            请求参数 form提交适用
     * @param entity
     *            请求实体 json/xml提交适用
     * @return 可能为空 需要处理
     * @throws Exception
     *
     */
    public static void  get(String url, Map<String, String> header,
                             Map<String, String> param,final LocalVideoBean localVideoBean,
                            final OnReceiveDataListener listener,final boolean isM3U8,int index) throws Exception {
        CloseableHttpClient httpClient = null;
        try {
            httpClient = getHttpClient();
            HttpGet httpget = new HttpGet();

            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(ConnectTimeout).setConnectTimeout(ConnectTimeout).setConnectionRequestTimeout(ReadTimeout).build();// 设置请求和传输超时时间

            httpget.setConfig(requestConfig);
            // 设置头信息
            if (header != null) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    httpget.addHeader(entry.getKey(), entry.getValue());
                }

            }
            // 设置请求参数
            if (param != null) {
                List<NameValuePair> formparams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    // 给参数赋值
                    formparams.add(new BasicNameValuePair(entry.getKey(), entry
                            .getValue()));
                }
                UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
                        formparams, Consts.UTF_8);
                List<NameValuePair> getParams = new ArrayList<NameValuePair>();
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    getParams.add(new BasicNameValuePair(entry.getKey(), entry
                            .getValue()));
                }
                httpget.setURI(URI.create(url += "?"
                        + EntityUtils.toString(new UrlEncodedFormEntity(
                        getParams), "UTF-8")));
            } else {
                httpget.setURI(URI.create(url));
            }

            HttpResponse httpResponse = httpClient.execute(httpget);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            System.err.println(statusCode);
            if (statusCode == HttpStatus.SC_OK || statusCode == 206) {
                if (listener != null) {
                    if (isM3U8) {
                        listener.OnSuccess(httpResponse.getEntity().getContentLength(), httpResponse.getEntity().getContent(), localVideoBean, index);
                    } else {
                        listener.OnSuccess(httpResponse.getEntity().getContentLength(), httpResponse.getEntity().getContent(), localVideoBean);
                    }
                }
            } else {
                if (listener != null)
                    listener.OnFail("文件操作错误");
                LogUtil.log(readHttpResponse(httpResponse));
            }
        } catch (Exception e) {
            if (listener != null)
                listener.OnFail("视频地址错误");
            throw e;
        } finally {
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }

    public static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf).setConnectionManager(cm)
                .setConnectionManagerShared(true).build();
        return httpClient;
    }

    public static String readHttpResponse(HttpResponse httpResponse)
            throws ParseException, IOException {
        StringBuilder builder = new StringBuilder();
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        builder.append("status:" + httpResponse.getStatusLine().getStatusCode());
        builder.append("headers:");
        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            builder.append("\t" + iterator.next());
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            builder.append("response length:" + responseString.length());
            builder.append("response content:"
                    + responseString.replace("\r\n", ""));
        }
        return builder.toString();
    }
}
