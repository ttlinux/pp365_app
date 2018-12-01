package org.sex.hanker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.user;
import org.sex.hanker.User.*;
import org.sex.hanker.Utils.AES;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Initview();
    }

    public void Initview()
    {
        comit=FindView(R.id.comit);
        comit.setOnClickListener(this);
        title=FindView(R.id.title);
        title.setText("用户登陆");
        name=FindView(R.id.name);
        password=FindView(R.id.password);
        registered=FindView(R.id.registered);
        registered.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.registered:
                startActivity(new Intent(this, org.sex.hanker.User.RegisterActivity.class));
                break;
            case R.id.comit:
                Login(name.getText().toString(),password.getText().toString());
                break;
        }
    }


    private void Login(String name,String password)
    {
        if(name.length()<1)
        {
            ToastUtil.showMessage(this,"请填写用户名");
            return;
        }
        if(password.length()<6)
        {
            ToastUtil.showMessage(this,"至少6位密码");
            return;
        }

        RequestParams requestParams=new RequestParams();
        requestParams.put("name",name);
        requestParams.put("password", AES.encode(password));
        Httputils.PostWithBaseUrl(Httputils.login, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                comit.setEnabled(true);
                ToastUtil.showMessage(LoginActivity.this, jsonObject.optString("info", ""));
                if (jsonObject.optString("status").equalsIgnoreCase("000000")) {
                    BaseApplication base= ((BaseApplication)getApplication());
                    base.setUser(user.analysis(jsonObject.optJSONObject("user")));
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
