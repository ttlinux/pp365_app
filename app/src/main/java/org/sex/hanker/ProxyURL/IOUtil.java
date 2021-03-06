package org.sex.hanker.ProxyURL;


import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;

import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ToastUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/6/28.
 */
public class IOUtil {

    private final String suffix = "XK";
    private static final String PING_REQUEST = "ping";
    private static final String PING_RESPONSE = "ping ok";
    public static final int Complete = -1;
    public static final int FileMiss = -2;

    public static String read(InputStream inputStream)  {
        BufferedReader reader = null;
        StringBuilder stringRequest = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()).length()>0) { // until new line (headers ending)
                stringRequest.append(line).append('\n');
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return stringRequest.toString();
    }

    public static void responseToPing(Socket socket)  {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();
            out.write("HTTP/1.1 200 OK\n\n".getBytes());
            out.write(PING_RESPONSE.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isM3U8(String name)
    {
        return getSuffixName(name).toLowerCase().equalsIgnoreCase("m3u8");
    }


    public static String getSuffixName(String name)
    {
        int index=name.lastIndexOf(".");
        if(index<0)
        {
            return "";
        }
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String GetUrl(String name)
    {
        return name.substring(0, name.lastIndexOf("/") + 1);
    }
    
    public static String decode(String url) {
        try {
            return URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error decoding url", e);
        }
    }
    
    public static String encode(String url) {
    	try {
			return URLEncoder.encode(url, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return url;
    }

    public static String getRootpath()
    {
        String root_path= Environment.getExternalStorageDirectory().getPath();
        if (root_path == null || root_path.equalsIgnoreCase("")) {
            List<String> lResult = new ArrayList<String>();
            try {
                Runtime rt = Runtime.getRuntime();
                Process proc = rt.exec("mount");
                InputStream is = proc.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("extSdCard")) {
                        String[] arr = line.split(" ");
                        String path = arr[1];
                        File file = new File(path);
                        if (file.isDirectory()) {
                            lResult.add(path);
                        }
                    }
                }
                isr.close();
            } catch (Exception e) {
            }

            if (lResult.size() > 0)
                root_path = lResult.get(0);
            else {
                LogTools.e("错误","没手机卡");
                return null;
            }
        }
        return root_path;
    }

    public Boolean isDownload(String FilenameWithSuffixOrUrl)
    {
        String FilenameWithSuffix=null;
        if(FilenameWithSuffixOrUrl.toLowerCase().startsWith("http"))
        {
            FilenameWithSuffix=getSuffixName(FilenameWithSuffixOrUrl);
        }
        else
        {
            FilenameWithSuffix=FilenameWithSuffixOrUrl;
        }
        String RootPath=getRootpath()+"/"+BundleTag.XoKong+"/";
        File file=new File(RootPath);
        if(file==null || !file.exists())
        {
            file.mkdirs();
        }
        if(!isM3U8(FilenameWithSuffix))
        {
            String cpath=RootPath+"/"+FilenameWithSuffix;
            File rfile=new File(cpath);
            if(rfile!=null && rfile.exists())
            {
                return true;
            }
        }
        else
        {

        }
        return false;
    }


    public static String repalceSuffix(String urlpath,String suffix)
    {
        return urlpath.substring(0, urlpath.indexOf(".")+1)+suffix;
    }

    public static String removeSuffix(String urlpath)
    {
        int position=urlpath.lastIndexOf(".");
        String suffix=urlpath.substring(position+1,urlpath.length());
        if(!suffix.equalsIgnoreCase(BundleTag.Dsuffix))
        {
            return urlpath;
        }
        return urlpath.substring(0, position);
    }

    public static long isComplete(String filepath)
    {
        String rsuffix=getSuffixName(filepath);
        if(rsuffix.equalsIgnoreCase(BundleTag.Dsuffix))
        {
            File rfile=new File(filepath);
            if(rfile!=null && rfile.exists())
            {
                return rfile.length();
            }
            else
            {
                File file=new File(removeSuffix(filepath));
                if(file!=null && file.exists())
                {
                    return Complete;
                }
            }
        }
        else
        {
            File file=new File(removeSuffix(filepath));
            if(file!=null && file.exists())
            {
                return Complete;
            }
        }
        return FileMiss;
    }

    public static String Formate(long numbers) {
        StringBuilder formatstr = new StringBuilder();
        BigDecimal biglong = new BigDecimal(numbers);
        int firstcompare = new BigDecimal(1000).compareTo(biglong);
        switch (firstcompare) {
            case 1:
                formatstr.append(numbers).append("B");
                return formatstr.toString();
            case 0:
                formatstr.append("1K");
                return formatstr.toString();

        }
        int secondcompare = new BigDecimal(1000*1000).compareTo(biglong);
        switch (secondcompare) {
            case 1:
                formatstr.append(new DecimalFormat("#0.00").format(0.1*numbers/100)).append("K");
                return formatstr.toString();
            case 0:
                formatstr.append("1M");
                return formatstr.toString();
        }
        int thirdcompare = new BigDecimal(1000*1000*1000).compareTo(biglong);
        switch (thirdcompare) {
            case 1:
                formatstr.append(new DecimalFormat("#0.00").format(0.1*numbers/100/1000)).append("M");
                return formatstr.toString();
            case 0:
                formatstr.append("1G");
                return formatstr.toString();
        }

        int fourthcompare = new BigDecimal(1000*1000*1000*1000).compareTo(biglong);
        switch (fourthcompare) {

            case 0:
                formatstr.append("1T");
                return formatstr.toString();
            case -1:
                formatstr.append(new DecimalFormat("#0.00").format(0.1*numbers/100/1000/1000)).append("G");
                return formatstr.toString();
            case 1:
                formatstr.append("文件过大已超出手机容量");
                return formatstr.toString();
        }

        return null;
    }

    /**
     * 获得sd卡剩余容量，即可用大小
     *
     * @return
     */
    public static long getSDAvailableSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return blockSize * availableBlocks;
    }

    /**
     * 获得机身内存总大小
     *
     * @return
     */
    public static long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return blockSize * totalBlocks;
    }
}
