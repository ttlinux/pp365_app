package org.sex.hanker.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.VideoDownload.VideoHTTPMethod;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/11/13.
 */
public class TestService extends Service {

    private static final String TestMp4 = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    private static ThreadPoolExecutor socketProcessor = (ThreadPoolExecutor)
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());//处理视频请求
    public static int cccc=0;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(cccc<1)
        {
            socketProcessor.submit(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                            .connectTimeout(15000, TimeUnit.MILLISECONDS)
                            .readTimeout(15000, TimeUnit.MILLISECONDS)
                            .build();

                    Request.Builder builder = new Request.Builder();
                    builder.addHeader("Cookie", java.util.UUID.randomUUID().toString());
                    builder.addHeader("Referer", TestMp4);
                    builder.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
                    builder.addHeader("Accept-Encoding", "identity");

                    builder.addHeader("Content-type", getSupposablyMime(TestMp4));
                    builder.url(TestMp4);
                    builder.get();
                    Request request = builder.build();

                    try {
                        Response response= okHttpClient.newCall(request).execute();
                        if(response.isSuccessful())
                        {
                            long time=System.currentTimeMillis();
                            long sumtime=0;
                            long bufferlength=0;
                            byte[] buffer = new byte[VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE];
                            int readBytes;
                            while ((readBytes = response.body().byteStream().read(buffer, 0, VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE)) > -1) {
                                if (Thread.currentThread().isInterrupted()) {
                                    //点击后暂停走这里
                                    LogTools.e("TestService","isInterrupted");
                                    return;
                                }

                                long ntime=System.currentTimeMillis();
                                sumtime=sumtime+ntime-time;
                                bufferlength=readBytes+bufferlength;
                                if(sumtime>=1000)
                                {
                                    LogTools.e("TestService","速度 "+(IOUtil.Formate(bufferlength*1000 / sumtime) ));
                                    sumtime=0;
                                    bufferlength=0;
                                }
                                time=ntime;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        LogTools.e("TestService", "IOException");
                    }
                }
            });
            cccc=1;
        }

        return super.onStartCommand(intent, START_STICKY, startId);
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
}
