package org.sex.hanker.ProxyURL;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;


import org.sex.hanker.Utils.ToastUtil;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by Administrator on 2018/6/28.
 */
public class Cache {

    public final String suffix = ".oo";
    public final String M3U8 = "m3u8";
    public final String Savepath = "Xkong";
    GetRequest getRequest;
    RandomAccessFile file;
    boolean isComplete;
    boolean isM3U8;
    final int DEFAULT_BUFFER_SIZE = 8 * 1024;


    public void CreateFile(GetRequest getRequest) {
        this.getRequest = getRequest;
        String url = getRequest.uri;
        String filepath = IOUtil.getRootpath();
        if (filepath == null || filepath.length() == 0) return;
        filepath = filepath + File.separator + Savepath;
        File dirfile = new File(filepath);
        if (dirfile == null || !dirfile.exists()) {
            dirfile.mkdirs();
        }
        String urlsuffix = getRequest.FileSuffix;

        if (urlsuffix != null && urlsuffix.length() > 0 && urlsuffix.toLowerCase().equalsIgnoreCase(M3U8)) {
            try {
                File file = new File(filepath + File.separator + MD5Util.GetMD5(url));
                file.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
            isM3U8 = true;
        } else {
            try {
                File file = new File(filepath + File.separator + MD5Util.GetMD5(url) + "." + urlsuffix);
                if (file == null || !file.exists()) {
                    this.file = new RandomAccessFile(filepath + File.separator + MD5Util.GetMD5(url) + suffix, "rw");
                    isComplete = false;
                } else {
                    this.file = new RandomAccessFile(file.getAbsolutePath(), "rw");
                    isComplete = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void writeToFile(long offset, byte[] data) {
        try {
            this.file.seek(offset);
            this.file.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void readFile(long offset, OnReadFullListener onReadFullListener) {
        if (onReadFullListener == null) return;

        try {
            this.file.seek(offset);
            byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
            int readBytes;
            while ((readBytes = file.read(buffer, 0, DEFAULT_BUFFER_SIZE)) > -1) {
                if (onReadFullListener != null)
                    onReadFullListener.onReadFull(buffer, readBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized Long getFileLength() {
        if (file == null) return -1l;
        long length = 0;
        try {
            length = file.length();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return length;
    }

    public interface OnReadFullListener {
        public void onReadFull(byte[] data, int dataExactlyLength);
    }

    public synchronized void close() {
        long Filelength = 0;
        try {
            Filelength = file.length();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getRequest.getContentLength() == Filelength) {
            //下载完成 把文件名字改了
            String filepath = IOUtil.getRootpath();
            filepath = filepath + File.separator + Savepath;
            try {
                File file = new File(filepath + File.separator + MD5Util.GetMD5(getRequest.uri) + suffix);
                file.renameTo(new File(filepath + File.separator + getRequest.FileName + "." + getRequest.FileSuffix));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
