package org.sex.hanker.Utils.VideoDownload;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;


import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.OnReceiveDataListener;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/8/2.
 */
public class VideoHTTPMethod {

    private final static int Text_DEFAULT_BUFFER_SIZE = 1024;
    public final static int Video_DEFAULT_BUFFER_SIZE = 1024 * 1024;
    public final static int ConnectTimeout = 15000;
    public final static int ReadTimeout = 15000;
    private static VideoHTTPMethod videoHTTPMethod;

    int index = 0;

    public static VideoHTTPMethod getInstance() {
        return new VideoHTTPMethod();
    }


    public void ProcessHttpConnectForVideo(final Context context, final LocalVideoBean localVideoBean,
                                           final OnReceiveDataListener listener) {
        String requesturl = localVideoBean.getCOLUMN_URL();
        final boolean isM3U8 = localVideoBean.getSUFFIX().toLowerCase().equalsIgnoreCase("m3u8");
        long offset = 0;
        long filelength=0;

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
            try
            {
                if(localVideoBean.getFileLength()!=null && localVideoBean.getFileLength().length()>0)
                filelength=Long.valueOf(localVideoBean.getFileLength());
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        if (offset == IOUtil.Complete) {
            if (listener != null)
                listener.OnFail("文件已经下载到SD卡，如需重新下载请删除原文件");
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(ConnectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(ReadTimeout, TimeUnit.MILLISECONDS)
                .build();

        Request.Builder builder = new Request.Builder();
        builder.addHeader("Cookie", java.util.UUID.randomUUID().toString());
        builder.addHeader("Referer", requesturl);
        builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
        builder.addHeader("Accept-Encoding", "identity");
        if (offset > 0)
            builder.addHeader("Range", "bytes=" + offset + "-");

        builder.addHeader("Content-type", getSupposablyMime(requesturl));
        builder.url(requesturl);
        builder.get();
        Request request = builder.build();

        try {
            Response response= okHttpClient.newCall(request).execute();
            if(response.isSuccessful())
            {
                if(filelength==0)
                {
                    filelength=response.body().contentLength();
                    localVideoBean.setFileLength(String.valueOf(filelength));
                    VideoSQL.updateSingleColumn(context,localVideoBean);
                }
                if (isM3U8) {
                    listener.OnSuccess(filelength, response.body().byteStream(), localVideoBean, index);
                } else {
                    listener.OnSuccess(filelength, response.body().byteStream(), localVideoBean);
                }
            }
            else
            {
                if (listener != null)
                    listener.OnFail(requesturl+" 下载失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Call call = okHttpClient.newCall(request);
//        call.enqueue(new Callback() {
//            //请求失败执行的方法
//            @Override
//            public void onFailure(Call call, IOException e) {
//                if (listener != null)
//                    listener.OnFail(call.request().url().toString()+" "+call.toString());
//            }
//
//            //请求成功执行的方法
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//
//                while (!Thread.currentThread().isInterrupted())
//                {
//                    LogTools.e("CancelTaskTest1111","NNNNNN");
//                    if (isM3U8) {
//                        listener.OnSuccess(response.body().contentLength(), response.body().byteStream(), localVideoBean, index);
//                    } else {
//                        listener.OnSuccess(response.body().contentLength(), response.body().byteStream(), localVideoBean);
//                    }
//                }
//                LogTools.e("CancelTaskTest","YYYYY");
//            }
//        });
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
}
