package com.danikula.videocache.M3u8;

/**
 * Created by Administrator on 2018/6/28.
 */
public class M3U8StringUtils {


    public static boolean isM3U8(String name)
    {
        return getSuffixName(name).toLowerCase().equalsIgnoreCase("m3u8");
    }


    public static String getSuffixName(String name)
    {
        return name.substring(name.lastIndexOf(".") + 1);
    }

    public static String GetUrl(String name)
    {
        return name.substring(0, name.lastIndexOf("/") + 1);
    }
}
