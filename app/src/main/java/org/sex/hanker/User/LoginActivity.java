package org.sex.hanker.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.AES;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.TimeUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/12/27.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    TextView title;
    AutoCompleteTextView name;
    EditText password;
    TextView registered,comit;
    SharedPreferences sharedPreferences;
    CheckBox remeberpassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==BundleTag.ResultCode && data!=null)
        {
            String userName=data.getStringExtra(BundleTag.userName);
            String password=data.getStringExtra(BundleTag.password);
            name.setText(userName);
            this.password.setText(password);
            auth();
        }
    }

    public void Initview()
    {
        sharedPreferences=((BaseApplication)getApplication()).getSharedPreferences();
        remeberpassword=FindView(R.id.remeberpassword);
        comit=FindView(R.id.comit);
        comit.setOnClickListener(this);
        title=FindView(R.id.title);
        title.setText(getResources().getString(R.string.login_name));
        name=FindView(R.id.name);
        password=FindView(R.id.password);
        registered=FindView(R.id.registered);
        registered.setOnClickListener(this);
        String username=sharedPreferences.getString(BundleTag.Username,"");
        String Password=sharedPreferences.getString(BundleTag.Password,"");
        password.setText(Password);
        name.setText(username);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.registered:
                startActivityForResult(new Intent(this, org.sex.hanker.User.RegisterActivity.class), BundleTag.RequestCode);
                break;
            case R.id.comit:
                auth();
                break;
        }
    }


    private void auth()
    {
        if(name.length()<1)
        {
            ToastUtil.showMessage(this, getString(R.string.plzphonenum));
            return;
        }
        if(password.length()==0)
        {
            ToastUtil.showMessage(this,getString(R.string.plzpassword));
            return;
        }
        comit.setEnabled(false);
        RequestParams requestParams=new RequestParams();
        requestParams.put("phone",name.getText().toString());
        Httputils.PostWithBaseUrl(Httputils.Auth,requestParams,new MyJsonHttpResponseHandler(this,true){
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
                comit.setEnabled(true);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if(jsonObject.optString("status","").equalsIgnoreCase("000000"))
                {
                    String token=jsonObject.optJSONObject("datas").optString("token","");
                    sharedPreferences.edit().putString(BundleTag.Token,token).commit();
                    Login();
                }
                else
                {
                    comit.setEnabled(true);
                    ToastUtil.showMessage(LoginActivity.this, jsonObject.optString("info",""));
                }
            }
        });
    }

    private void Login()
    {
        final String username=name.getText().toString();
        final String password=AES.encode(this.password.getText().toString());
        RequestParams requestParams=new RequestParams();
        requestParams.put("phone",name.getText().toString());
        requestParams.put("password", password);
        requestParams.put("logintype",0+"");
        Httputils.PostWithBaseUrl(Httputils.login, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                comit.setEnabled(true);
                ToastUtil.showMessage(LoginActivity.this, jsonObject.optString("info", ""));
                if (jsonObject.optString("status").equalsIgnoreCase("000000")) {
                    ((BaseApplication)getApplication()).setUsername(name.getText().toString());
                    JSONObject datas=jsonObject.optJSONObject("datas");
                    if(remeberpassword.isChecked())
                    {
                        sharedPreferences.edit()
                                .putString(BundleTag.UserInfo, datas.toString())
                                .putString(BundleTag.Username, username)
                                .putString(BundleTag.Password, LoginActivity.this.password.getText().toString())
                                .putString(BundleTag.ScreenLockStatus,datas.optString("handpasswordstate",""))
                                .putBoolean(BundleTag.EnbleAPPScreenLock,datas.optBoolean("screenlockstatusOnpresshome",false))
                                .putBoolean(BundleTag.EnbleResumeScreenLock,datas.optBoolean("screenlockstatusOnexit",false))
                                .putString(BundleTag.EnbleAutoScreenLock,datas.optString("screenlockstatusAutolock",""))
                                .commit();
                    }
                    else
                    {
                        sharedPreferences.edit().putString(BundleTag.Token, "")
                                .putString(BundleTag.Username, "")
                                .putString(BundleTag.Password, "")
                                .putString(BundleTag.ScreenLockStatus, "")
                                .putBoolean(BundleTag.EnbleAPPScreenLock, false)
                                .putBoolean(BundleTag.EnbleResumeScreenLock, false)
                                .putString(BundleTag.EnbleAutoScreenLock,"0")
                                .commit();
                    }
                    setResult(BundleTag.LoginSuccess);
                    finish();
                }
            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
                comit.setEnabled(true);
            }
        });
    }
}
