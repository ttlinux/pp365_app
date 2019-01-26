package org.sex.hanker.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.loopj.android.http.AsyncHttpClient;

import org.sex.hanker.BaseParent.BaseApplication;

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
        BaseApplication baseApplication=((BaseApplication)context.getApplicationContext());
        SharedPreferences sharedPreferences=baseApplication.getSharedPreferences();
        addHeader("token",sharedPreferences.getString(BundleTag.Token,""));
        addHeader("username",baseApplication.getUsername());

        LogTools.e("MyAsyncHttpClient","token "+sharedPreferences.getString(BundleTag.Token,"")+"  username   "+baseApplication.getUsername());
    }

}
