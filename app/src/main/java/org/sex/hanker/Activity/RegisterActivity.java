package org.sex.hanker.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.user;
import org.sex.hanker.Utils.AES;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.AppAlertDialog;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/12/27.
 */
public class RegisterActivity extends BaseActivity {

    TextView title;
    CheckBox state;
    EditText name,password,repeatpwd,phone,wechat,qqedit;
    TextView sexual;
    int sexualIndex=-1;
    Button comit;
    String sexs[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initview();
    }

    public void Initview()
    {
        sexs=getResources().getStringArray(R.array.sexuals);
        title=FindView(R.id.title);
        title.setText("用户注册");
        state=FindView(R.id.state);
        name=FindView(R.id.name);
        password=FindView(R.id.password);
        repeatpwd=FindView(R.id.repeatpwd);
        phone=FindView(R.id.wechat);
        wechat=FindView(R.id.wechat);
        qqedit=FindView(R.id.qqedit);
        sexual=FindView(R.id.sexual);
        sexual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppAlertDialog aad = new AppAlertDialog(RegisterActivity.this);
                aad.setConetnt(sexs);
                aad.setOnChooseListener(new AppAlertDialog.OnChooseListener() {
                    @Override
                    public void Onchoose(boolean confirm, AppAlertDialog dialog, int sexuals) {
                        if (confirm) {
                            sexual.setText(sexs[sexuals]);
                            sexualIndex = sexuals;
                        }
                        dialog.hide();
                    }
                });
                aad.show();
            }
        });
        comit=FindView(R.id.comit);
        comit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Oncommit();
            }
        });
    }

    private void Oncommit()
    {
        if(name.getText().toString().length()<1)
        {
            ToastUtil.showMessage(this,"请填写用户名");
            return;
        }
        if(password.getText().toString().length()<6)
        {
            ToastUtil.showMessage(this,"至少6位密码");
            return;
        }
        if(!repeatpwd.getText().toString().equalsIgnoreCase(password.getText().toString()))
        {
            ToastUtil.showMessage(this,"两次密码输入不一致");
            return;
        }
        if(sexual.getText().toString().length()<1)
        {
            ToastUtil.showMessage(this,"请选择性别");
            return;
        }
        comit.setEnabled(false);
        LogTools.e("eeee", password.getText().toString() + "   " + repeatpwd.getText().toString());
        final String passwordstr=AES.encode(password.getText().toString());
        final String repasswordstr=AES.encode(repeatpwd.getText().toString());

        RequestParams requestParams=new RequestParams();
        requestParams.put("name",name.getText().toString());
        requestParams.put("password", passwordstr);
        requestParams.put("repassword",repasswordstr
        );
        requestParams.put("sexual",sexualIndex+"");
        requestParams.put("showstate",""+(state.isChecked()?1:0));
        requestParams.put("qq",qqedit.getText().toString());
        requestParams.put("weixin", wechat.getText().toString());
        requestParams.put("phone",phone.getText().toString());
        Httputils.PostWithBaseUrl(Httputils.register, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                LogTools.e("register", jsonObject.toString());

                if (jsonObject.optString("status").equalsIgnoreCase("000000")) {
                    Login(name.getText().toString(),passwordstr);
                }
                else
                {
                    comit.setEnabled(true);
                    ToastUtil.showMessage(RegisterActivity.this, jsonObject.optString("info"));
                }

            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
                comit.setEnabled(true);
            }
        });
    }

    private void Login(String name,String password)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("name",name);
        requestParams.put("password", password);
        Httputils.PostWithBaseUrl(Httputils.login,requestParams,new MyJsonHttpResponseHandler(this,true){
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                LogTools.e("Login", jsonObject.toString());
                comit.setEnabled(true);
                ToastUtil.showMessage(RegisterActivity.this,jsonObject.optString("info",""));
                if(jsonObject.optString("status").equalsIgnoreCase("000000"))
                {
                    ((BaseApplication)getApplication()).setUser(user.analysis(jsonObject.optJSONObject("user")));
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
