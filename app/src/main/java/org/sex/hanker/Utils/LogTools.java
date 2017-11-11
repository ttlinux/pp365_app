package org.sex.hanker.Utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/3/27.
 */
public class LogTools {
//        public static boolean isShow = true;//上线模式
     public static boolean isShow = false;//开发模式
    private static int LOG_MAXLENGTH = 2000;

    public static void ee(String tag,String msg) {
        if(!isShow){

            int strLength = msg.length();
            int start = 0;
            int end = LOG_MAXLENGTH;
            for (int i = 0; i < 100; i++) {
                if (strLength > end) {
                    Log.e(tag, msg.substring(start, end));
                    start = end;
                    end = end + LOG_MAXLENGTH;
                } else {
                    Log.e(tag, msg.substring(start, strLength));
                    break;
                }
            }
        }
    }
    public static void e(String tag,String value)
    {
        if(!isShow){
            Log.e(tag,value);
        }
    }

    public static void v(String tag,String value)
    {
            if(!isShow){
                Log.v(tag, value);
            }
    }

    public static void i(String tag,String value)
    {
          if(!isShow){
              Log.i(tag, value);
          }
    }

    public static void d(String tag,String value)
    {
         if(!isShow){
             Log.d(tag, value);
         }
    }

    public static void w(String tag,String value)
    {
       if(!isShow){
           Log.w(tag, value);
       }
    }
}
