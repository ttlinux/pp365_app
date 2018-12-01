package org.sex.hanker.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.mybusiness.R;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


/**
 * Created by Administrator on 2018/11/27.
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0:
                    getauth.setEnabled(false);
                    getauth.setText(TimeCount - count + "秒后重新获取");
                    count++;
                    if (count == 61) {
                        getauth.setEnabled(true);
                        getauth.setText("获取验证码");
                        count=1;
                    } else {
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                    break;
            }
        }
    };
    static int count = 1;
    static int TimeCount = 60;
    private EditText phonenumber,verifycode,password;
    TextView getauth,comit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitView();
    }

    private void InitView()
    {
        setActivityTitle(getResources().getString(R.string.reg_name));
        comit=FindView(R.id.comit);
        getauth=FindView(R.id.getauth);
        phonenumber=FindView(R.id.phonenumber);
        verifycode=FindView(R.id.verifycode);
        password=FindView(R.id.password);
        getauth.setOnClickListener(this);
        comit.setOnClickListener(this);

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(count>1)
        {
            getauth.setEnabled(false);
            handler.sendEmptyMessage(0);
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(0);
    }

    private void Register()
    {
        if(phonenumber.getText().toString().length() ==0)
        {
            ToastUtil.showMessage(this, "请输入正确的手机号码");
            return;
        }
        if(verifycode.getText().toString().length() ==0)
        {
            ToastUtil.showMessage(this, "请输入正确的手机验证码");
            return;
        }
        if(password.getText().toString().length() ==0)
        {
            ToastUtil.showMessage(this, "请输入密码");
            return;
        }
        RequestParams requestParams=new RequestParams();
        requestParams.put("areacode","63");
        requestParams.put("phone",phonenumber.getText().toString());
        requestParams.put("code",verifycode.getText().toString());
        requestParams.put("password",password.getText().toString());
        requestParams.put("sexual","0");
        Httputils.PostWithBaseUrl(Httputils.register, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                ToastUtil.showMessage(RegisterActivity.this, jsonObject.optString("msg", ""));
                if (jsonObject.optString("errorCode", "").equalsIgnoreCase("000000")) {
                    getauth.setEnabled(true);
                    count = 1;
                    Intent intent = new Intent();
                    intent.putExtra(BundleTag.userName, phonenumber.getText().toString());
                    intent.putExtra(BundleTag.password, password.getText().toString());
                    setResult(BundleTag.LoginSuccess, intent);
                    finish();
                }
            }
        });
    }

    private void SendAuthCode()
    {
        if(phonenumber.getText().toString().length() ==0)
        {
            ToastUtil.showMessage(this, "请输入正确的手机号码");
            return;
        }

        getauth.setEnabled(false);
        EventHandler eventHandler = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        LogTools.ee("EEEEE1", new Gson().toJson(data));
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        LogTools.ee("EEEEE2", new Gson().toJson(data));
                        getauth.setEnabled(false);
                        handler.sendEmptyMessageDelayed(0, 1000);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        LogTools.ee("EEEEE3", new Gson().toJson(data));
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    LogTools.ee("EEEEE4", ((Throwable) data).toString());
                }
            }
        };
// 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
        SMSSDK.getSupportedCountries();
// 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
        SMSSDK.getVerificationCode("63", phonenumber.getText().toString());

//        Httputils.PostWithBaseUrl(Httputils.sendsms,requestParams,new MyJsonHttpResponseHandler(this,true){
//
//            @Override
//            public void onFailureOfMe(Throwable throwable, String s) {
//                super.onFailureOfMe(throwable, s);
//                getauth.setEnabled(true);
//            }
//
//            @Override
//            public void onSuccessOfMe(JSONObject jsonObject) {
//                super.onSuccessOfMe(jsonObject);
//
//
//                ToastUtil.showMessage(RegisterActivity.this, jsonObject.optString("msg",""));
//                if(jsonObject.optString("errorCode","").equalsIgnoreCase("000000"))
//                {
//                    getauth.setEnabled(false);
//                    handler.sendEmptyMessageDelayed(0, 1000);
//                }
//                else
//                {
//                    getauth.setEnabled(true);
//                }
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.getauth:
                SendAuthCode();
                break;
            case R.id.comit:
                Register();
                break;
        }
    }
}
