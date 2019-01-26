package org.sex.hanker.User;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Utils.AES;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.PublicDialog;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

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
    TextView getauth,comit,countrycode;
    private ArrayList<String> counrycodes=new ArrayList<>();
    private RelativeLayout countryselector;
    private RadioGroup radiogroup;
    private int sexualindex=-1;
    PublicDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==BundleTag.ResultCode && data!=null)
        {
            try {
                JSONObject jsonObject=new JSONObject(data.getStringExtra(BundleTag.Data));
                StringBuilder sb=new StringBuilder();
                sb.append(jsonObject.optString(getIsChina()?"countryname_cn":"countryname_en",""));
                sb.append("  (+");
                sb.append(jsonObject.optString("areacode",""));
                sb.append(")");
                countrycode.setText(sb.toString());
                sb.delete(0, sb.length());
                sb.append(jsonObject.optString("pinyin", ""));
                sb.append("|");
                sb.append(jsonObject.optString("areacode",""));
                countrycode.setTag(sb.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void InitView()
    {
        setActivityTitle(getResources().getString(R.string.reg_name));

        radiogroup=FindView(R.id.radiogroup);
        countryselector=FindView(R.id.countryselector);
        countrycode=FindView(R.id.countrycode);
        countrycode.setText(getResources().getString(R.string.fetchcountrycode));
        comit=FindView(R.id.comit);
        getauth=FindView(R.id.getauth);
        phonenumber=FindView(R.id.phonenumber);
        verifycode=FindView(R.id.verifycode);
        password=FindView(R.id.password);

        getauth.setOnClickListener(this);
        comit.setOnClickListener(this);

        for (int i = 0; i < radiogroup.getChildCount(); i++) {
            RadioButton rbtn=(RadioButton)radiogroup.getChildAt(i);
            rbtn.setTag(i);
            rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    sexualindex=(int)buttonView.getTag();
                }
            });
        }
        EventHandler eventHandler = new EventHandler() {

            @Override
            public void afterEvent(final int event, final int result, final Object data) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            //回调完成
                            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                                //提交验证码成功
                                LogTools.ee("EEEEE1", new Gson().toJson(data));
                            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                                //获取验证码成功
                                LogTools.ee("EEEEE2", new Gson().toJson(data));
                                if(result==SMSSDK.RESULT_ERROR)
                                {
                                    getauth.setEnabled(true);
                                }
                                else if(result==SMSSDK.RESULT_COMPLETE)
                                {
                                    getauth.setEnabled(false);
                                    handler.sendEmptyMessageDelayed(0, 1000);
                                }
                                if(!isFinishing() && !isDestroyed())
                                {
                                    dialog.dismiss();
                                }
                            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                                //返回支持发送验证码的国家列表
                                if(data==null)
                                {
                                    countrycode.setText(getResources().getString(R.string.fetchcountrycodefail));
                                    if(!isFinishing() && !isDestroyed())
                                    {
                                        dialog.dismiss();
                                    }
                                    return;
                                }
                                Map<Character, ArrayList<String[]>> ent =SMSSDK.getGroupedCountryList();
                                for (Map.Entry<Character, ArrayList<String[]>> temp : ent.entrySet()) {
                                    ArrayList<String[]> cl = temp.getValue();
                                    for (String[] paire : cl) {
                                        if(paire[1].equalsIgnoreCase("1"))
                                        {
                                            if(paire[2].equalsIgnoreCase("2"))
                                            {
                                                counrycodes.add("1M");
                                            }
                                            else
                                            {
                                                counrycodes.add("1J");
                                            }
                                        }
                                        else
                                        {
                                            counrycodes.add(paire[1]);
                                        }


                                        LogTools.ee("EEEEE3", new Gson().toJson(paire));
                                    }
                                }
                                countryselector.setOnClickListener(RegisterActivity.this);
                                countrycode.setText(getResources().getString(R.string.placeholdata));
                                countrycode.setTag("ZG|86");
                                getauth.setEnabled(true);
                                if(!isFinishing() && !isDestroyed())
                                {
                                    dialog.dismiss();
                                }

                            }
                        } else {
                            ((Throwable) data).printStackTrace();
                            LogTools.ee("EEEEE4", ((Throwable) data).toString());
                            if(counrycodes.size()==0)
                            {
                                getauth.setEnabled(true);
                                countrycode.setText(getResources().getString(R.string.fetchcountrycodefail));
                            }
                            else
                            {
                                ToastUtil.showMessage(RegisterActivity.this,((Throwable) data).getMessage().toString());
                            }
                            if(!isFinishing() && !isDestroyed())
                            {
                                dialog.dismiss();
                            }
                        }
                    }
                });

            }
        };
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);
        SMSSDK.getSupportedCountries();
        if(dialog==null)
        {
            dialog=new PublicDialog(this,false);
        }
        dialog.show();
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
        if(countrycode.getTag()==null)
        {
            ToastUtil.showMessage(this, getResources().getString(R.string.plzselectcountry));
            return;
        }
        if(phonenumber.getText().toString().length() ==0)
        {
            ToastUtil.showMessage(this, getResources().getString(R.string.plzinputphonenum));
            return;
        }
        if(verifycode.getText().toString().length() ==0)
        {
            ToastUtil.showMessage(this, getResources().getString(R.string.plzinputauthnum));
            return;
        }
        if(password.getText().toString().length() ==0)
        {
            ToastUtil.showMessage(this, getResources().getString(R.string.plzinputpassword));
            return;
        }
        if(sexualindex<0)
        {
            ToastUtil.showMessage(this, getResources().getString(R.string.plzselectsexual));
            return;
        }
        String tag=(String)countrycode.getTag();
        String args[]=tag.split("\\|");
        RequestParams requestParams=new RequestParams();
        requestParams.put("areacode",args[1]);
        requestParams.put("phone",phonenumber.getText().toString());
        requestParams.put("code",verifycode.getText().toString());
        requestParams.put("password", AES.encode(password.getText().toString()) );
        requestParams.put("pinyin",args[0]);
        requestParams.put("sexual",sexualindex+"");
        Httputils.PostWithBaseUrl(Httputils.register, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                ToastUtil.showMessage(RegisterActivity.this, jsonObject.optString("info", ""));
                if (jsonObject.optString("status", "").equalsIgnoreCase("000000")) {
                    getauth.setEnabled(true);
                    count = 1;
                    Intent intent = new Intent();
                    intent.putExtra(BundleTag.userName, phonenumber.getText().toString());
                    intent.putExtra(BundleTag.password, password.getText().toString());
                    setResult(BundleTag.ResultCode, intent);
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
// 请求验证码，其中country表示国家代码，如“86”；phone表示手机号码，如“13800138000”
        SMSSDK.getVerificationCode("63", phonenumber.getText().toString());
        if(dialog==null)
        {
            dialog=new PublicDialog(this,false);
        }
        dialog.show();
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
            case R.id.countryselector:
                Intent intent=new Intent();
                intent.setClass(this,SelectCountryActivity.class);
                intent.putStringArrayListExtra(BundleTag.Data,counrycodes);
                startActivityForResult(intent, BundleTag.RequestCode);
                break;
        }
    }
}
