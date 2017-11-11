package org.sex.hanker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by Administrator on 2017/4/3.
 */
public class MyAsyncHttpClient extends AsyncHttpClient {

    public MyAsyncHttpClient(Context context)
    {
        if(context==null)
        {
            LogTools.e("错误提示","Context是空");
            return;
        }

    }
}
