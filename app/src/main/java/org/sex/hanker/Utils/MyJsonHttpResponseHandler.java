package org.sex.hanker.Utils;

import android.app.Activity;
import android.content.Intent;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.View.PublicDialog;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/4/1.
 */
public class MyJsonHttpResponseHandler extends JsonHttpResponseHandler {

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public Activity context;
    boolean state = false;
    private String textcode = "";
    //    public MyJsonHttpResponseHandler(){}
    PublicDialog publicDialog;

    public MyJsonHttpResponseHandler(Activity context, boolean state) {
        if (state) {
            if (publicDialog != null && context != null) {
                publicDialog.dismiss();
                publicDialog = null;
            }
            publicDialog = new PublicDialog(context);
            publicDialog.show();
        }
        this.context = context;
        this.state = state;
    }

//    public MyJsonHttpResponseHandler(Activity context,String textcode){//测试用的
//        this.context=context;
//        this.textcode=textcode;
//    }


    @Override
    public void onFailure(Throwable throwable, String s) {
        super.onFailure(throwable, s);
        onFailureOfMe(throwable, s);
        if (context != null && !context.isDestroyed() && !context.isFinishing()) {
            if (state) {
                if (publicDialog != null) {
                    publicDialog.dismiss();
                }
            }
            ToastUtil.showMessage(context, context.getString(R.string.connecterr));
        }
        LogTools.e("错误",throwable.toString() + s);
    }

    @Override
    protected Object parseResponse(String s) throws JSONException {
        if (context != null && !context.isDestroyed() && !context.isFinishing()) {
            if (state) {
                if (publicDialog != null) {
                    publicDialog.dismiss();
                }
            }
        }
        return super.parseResponse(s);
    }

    @Override
    public void onSuccess(JSONObject jsonObject) {
        super.onSuccess(jsonObject);

        if (context != null && !context.isDestroyed() && !context.isFinishing()) {
            if (state) {
                if (publicDialog != null) {
                    publicDialog.dismiss();
                }
            }
            //掉线的
            if (jsonObject.optString("errorCode", "").equalsIgnoreCase("000005")) {
                OnError(jsonObject);
                return;
            }
            //登录后被顶下来的
            if (jsonObject.optString("errorCode", "").equalsIgnoreCase("000007")) {
                OnError(jsonObject);
                return;
            }

            if (jsonObject.optString("errorCode", "").equalsIgnoreCase("000008")) {
                Onmaintained(jsonObject.optString("msg", ""));
                return;
            }
//            if(!textcode.equalsIgnoreCase(""))
//            {
//                Onmaintained();
//                return;
//            }
            onSuccessOfMe(jsonObject);
        }

    }

    public void onSuccessOfMe(JSONObject jsonObject) {
    }

    public void onFailureOfMe(Throwable throwable, String s) {
        throwable.printStackTrace();
        LogTools.e("错误",throwable.toString() + s);
    }


    protected void OnError(JSONObject jsonObject) {
        onFailureOfMe(new Throwable(), "登录超时");
        if (context == null) return;
        ToastUtil.showMessage(context, jsonObject.optString("msg", ""));


    }

    private void Onmaintained(String ss) {
        onFailureOfMe(new Throwable(), ss);
        if (context == null) return;
        ToastUtil.showMessage(context, ss);
    }

}
