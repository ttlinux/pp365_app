package org.sex.hanker.Utils;

import android.content.Context;
import android.text.Html;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2018/3/7.
 */
public class NoteDownloader {

    OnReceiveListener onReceiveListener;
    Context context;

    public OnReceiveListener getOnReceiveListener() {
        return onReceiveListener;
    }

    public void setOnReceiveListener(OnReceiveListener onReceiveListener) {
        this.onReceiveListener = onReceiveListener;
    }

    public  void Download(final Context context,final String url,final String title,final OnReceiveListener onReceiveListener)
    {
        this.context=context;
        new Thread(){

            @Override
            public void run() {
                super.run();
                ProcessHttpconnect(url, title, onReceiveListener);
            }
        }.start();
    }

    public interface OnReceiveListener
    {
        public void OnReceive(String content);
        public void OnFail(Exception reson);
    }

    public  void ProcessHttpconnect(final String downurl,final String title,final OnReceiveListener onReceiveListener) {
        LogTools.e("NoteDownloader",downurl);
        final StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        InputStream in = null;
        String BOUNDARY = java.util.UUID.randomUUID().toString();
        String MULTIPART_FROM_DATA = "text/html";
        try {
            URL url = new URL(downurl);
            // HttpURLConnection conn = (HttpURLConnection)
            // url.openConnection(proxy);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setUseCaches(false); // 不允许使用缓存
            conn.setRequestProperty("Cookie", System.currentTimeMillis() + "");
            conn.setRequestProperty("Referer", downurl);
            conn.setRequestProperty("User-Agent",
                    "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; CIBA)");
            conn.setRequestProperty("Content-Type", "text/plain");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.addRequestProperty("connection", "keep-alive");

//            if(Unicode!=null)
//                conn.addRequestProperty("Content-Type", "text/html; charset="+Unicode);
//            else
//                conn.addRequestProperty("Content-Type", "text/html; charset=UTF-8");

            in = conn.getInputStream();
//            if(Unicode!=null)
//                br = new BufferedReader(new InputStreamReader(in, Unicode));
//            else
                br = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line = null;
//            if (!br.ready()) {
//                LogTools.e("NoteDownloader","BufferedReader准备中");
//            }
//            while (!br.ready()) {
//
//            }
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\r\n");
            }
        } catch (MalformedURLException e) {
            LogTools.e("NoteDownloader", "目标地址 " + downurl);
            LogTools.e("NoteDownloader", "url格式不规范:" + e.toString());
            if (br != null) {
                try {
                    br.close();
                    if (in != null)
                        in.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if (onReceiveListener != null)
                onReceiveListener.OnFail(e);
        } catch (IOException e) {
            LogTools.e("NoteDownloader", "IO操作错误：" + e.toString());
            if (br != null) {
                try {
                    br.close();
                    if (in != null)
                        in.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
            if (onReceiveListener != null)
                onReceiveListener.OnFail(e);
        }
        finally
        {
            try {
                if (br != null)
                    br.close();
                if (in != null)
                    in.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (onReceiveListener != null && sb.length()>0)
            {
                TextView hh=new TextView(context);
                hh.setText(Html.fromHtml(sb.toString()));
                onReceiveListener.OnReceive(hh.getText().toString());
            }
        }
    }

}
