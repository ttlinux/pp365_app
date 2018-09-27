package com.danikula.videocache.M3u8;

import android.os.Environment;
import android.text.TextUtils;

import com.danikula.videocache.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2018/4/3.
 */
public class M3u8Cache {

    private static final int MAX_EXTENSION_LENGTH = 4;

    public static void CacheM3u8Xml(final String url,Config config)
    {
        final String rootpath=config.cacheRoot.getAbsolutePath()+"/"+ProxyCacheUtils.computeMD5(url);
        final String m3u8_path=generate(url);
        HttpProxyCacheServer.getSocketProcessor().submit(new Runnable() {
            @Override
            public void run() {
                Download(url,rootpath,m3u8_path);
            }
        });
    }

    public static String generate(String url) {
        String extension = getExtension(url);//后缀名
        String name = ProxyCacheUtils.computeMD5(url);
        return TextUtils.isEmpty(extension) ? name : name + "." + extension;
    }

    private static String getExtension(String url) {
        int dotIndex = url.lastIndexOf('.');
        int slashIndex = url.lastIndexOf('/');
        return dotIndex != -1 && dotIndex > slashIndex && dotIndex + 2 + MAX_EXTENSION_LENGTH > url.length() ?
                url.substring(dotIndex + 1, url.length()) : "";
    }

    private static void Download(String reurl,String rootpath,String m3u8_path)
    {
        URL url = null;
        File file=null;
        try {
            url = new URL(reurl);
            HttpURLConnection conn = (HttpURLConnection) url
                    .openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();

            file = new File(rootpath,m3u8_path);
            if (!file.exists() || !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(file);

            byte buf[] = new byte[1024];

            do {
                int numread = is.read(buf);

                if (numread <= 0) {

                    break;
                }
                fos.write(buf, 0, numread);
            } while (true);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
