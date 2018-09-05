package org.sex.hanker.ProxyURL;

import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import org.sex.hanker.Utils.LogTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

//http://10.20.20.1/E/%E7%88%B1%E6%83%85%E7%89%87/%E4%BA%B2%E7%88%B1%E7%9A%84,%E6%88%91%E8%A6%81%E5%92%8C%E5%88%AB%E4%BA%BA%E7%BB%93%E5%A9%9A%E4%BA%86/%E4%BA%B2%E7%88%B1%E7%9A%84,%E6%88%91%E8%A6%81%E5%92%8C%E5%88%AB%E4%BA%BA%E7%BB%93%E5%A9%9A%E4%BA%86.mkv
public class DownLoadAndTransfer {

    Socket socket;
    GetRequest getRequest;
    final int DEFAULT_BUFFER_SIZE = 1024;



    public DownLoadAndTransfer(Socket socket, GetRequest getRequest) {
        this.socket = socket;
        this.getRequest = getRequest;
    }

    public void OpenConnection() {

        Cache cache = new Cache();
        cache.CreateFile(getRequest);
        try {
            if (OnComplete(cache, socket.getOutputStream())) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getSupposablyMime(String url) {
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

    private String Code200_Header(long contentLength, String mimetype) {
        // 不是断点续传
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 200 OK\n");
        sb.append("Accept-Ranges: bytes\n");
        sb.append(String.format("Content-Length: %d\n", contentLength));
        sb.append(String.format("Content-Type: %s\n", mimetype));
        sb.append("\n");
        return sb.toString();
    }


    private String Code206_Header(long contentLength, String mimetype) {
        // 不是断点续传
        StringBuilder sb = new StringBuilder();
        sb.append("HTTP/1.1 206 PARTIAL CONTENT\n");
        sb.append("Accept-Ranges: bytes\n");
        sb.append(String.format("Content-Length: %d\n", contentLength));
        sb.append(String.format("Content-Type: %s\n", mimetype));
        sb.append("\n");
        return sb.toString();
    }

    public void HandleOutPutData(InputStream is, OutputStream socketoutput, Cache cache) {
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int readBytes;
        long offset = getRequest.rangeOffset;
        try {
            while ((readBytes = is.read(buffer, 0, DEFAULT_BUFFER_SIZE)) > -1) {
                cache.writeToFile(offset, buffer);
                socketoutput.write(buffer, 0, readBytes);
                offset = offset + readBytes;
            }
            socketoutput.flush();
            socketoutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean OnComplete(Cache cache, final OutputStream socketoutput) {
        boolean isComplete = cache.isComplete && !cache.isM3U8;
        if(!isComplete)
        {
            return isComplete;
        }
        WriteHeader(cache.getFileLength(),getSupposablyMime(getRequest.uri),socketoutput);
        cache.readFile(getRequest.rangeOffset, new Cache.OnReadFullListener() {
            @Override
            public void onReadFull(byte[] data, int dataExactlyLength) {
                try {
                    socketoutput.write(data, 0, dataExactlyLength);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        cache.close();
        return isComplete;
    }


    public void WriteHeader(long length,String mime,OutputStream socketoutput)
    {
        try {
            if (!getRequest.partial) {

                String header = Code200_Header(
                        length,
                        mime);

                socketoutput.write(header.getBytes("UTF-8"));

            } else {
                //断点续传类型

                String header = Code206_Header(
                        length,
                        mime);
                socketoutput.write(header.getBytes("UTF-8"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void RequestNetData(Cache cache)
    {
        try {
            URL murl = new URL(getRequest.uri);
            HttpURLConnection httpUrlConnection = (HttpURLConnection) murl
                    .openConnection();

            // 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在
            // http正文内，因此需要设为true, 默认情况下是false;
            // Post 请求不能使用缓存
            // 设定传送的内容类型是可序列化的java对象
            // (如果不设此项,在传送序列化对象时,当WEB服务默认的不是这种类型时可能抛java.io.EOFException)
            httpUrlConnection.setRequestProperty("Content-type",
                    getSupposablyMime(getRequest.uri));
            httpUrlConnection.setUseCaches(false);
            httpUrlConnection.setRequestMethod("GET");
            if (getRequest.partial)
            {
                long offset=getRequest.rangeOffset;
                httpUrlConnection.setRequestProperty("Range", "bytes=" + getRequest.rangeOffset + "-");
            }

            httpUrlConnection.connect();

            if (getRequest.partial) {
                getRequest.setContentLength(getRequest.rangeOffset + httpUrlConnection.getContentLength());
            } else {
                //200code 的 ContentLength是完整的
                getRequest.setContentLength(httpUrlConnection.getContentLength());
            }
            System.err.println("open " + httpUrlConnection.getResponseCode());

            final OutputStream socketoutput = socket.getOutputStream();
            final InputStream is=httpUrlConnection.getInputStream();
            WriteHeader(httpUrlConnection.getContentLength(),httpUrlConnection.getContentType(),socketoutput);


            if (!cache.isM3U8) {
                //取出缓存
                cache.readFile(getRequest.rangeOffset, new Cache.OnReadFullListener() {
                    @Override
                    public void onReadFull(byte[] data, int dataExactlyLength) {
                        try {
                            socketoutput.write(data, 0, dataExactlyLength);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                //刷入缓存和回传数据
                HandleOutPutData(is,socketoutput,cache);

            } else {
                //m3u8

            }

            cache.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            cache.close();
        }
    }
}
