package org.sex.hanker.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.ScreenLockDialog;
import org.sex.hanker.View.SwitchView;
import org.sex.hanker.View.WheelDialog;
import org.sex.hanker.mybusiness.BuildConfig;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2018/11/21.
 */
public class ScreenLockParentActivity extends BaseActivity implements View.OnClickListener{

    public enum Status{
        Start(0),On(1),Off(2),Edit(3);
        public  int index;

        // 构造方法
        private Status(int index) {
            this.index = index;
        }
        public int getIndex() {
            return this.index;
        }
    }
    LinearLayout mainll,tipslayout,settinglayout;
    TextView off,edit,start,hanguptime;
    int status;
    RelativeLayout hanguptimelayout;
    SwitchView switch_onexit,switch_onhome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenlock_parent);
        InitView();
        ScreenLockDialog dialog=new ScreenLockDialog(this);
        dialog.show();
    }

    private void InitView()
    {
        switch_onexit=FindView(R.id.switch_onexit);
        switch_onhome=FindView(R.id.switch_onhome);

        hanguptime=FindView(R.id.hanguptime);
        hanguptimelayout=FindView(R.id.hanguptimelayout);
        mainll=FindView(R.id.mainll);
        tipslayout=FindView(R.id.tipslayout);
        settinglayout=FindView(R.id.settinglayout);
        off=FindView(R.id.off);
        edit=FindView(R.id.edit);
        start=FindView(R.id.start);
        off.setOnClickListener(this);
        edit.setOnClickListener(this);
        start.setOnClickListener(this);
        switch_onhome.setOnClickListener(this);
        switch_onexit.setOnClickListener(this);
        hanguptimelayout.setOnClickListener(this);
        setActivityTitle(getIntent().getStringExtra(BundleTag.Title));

        RefreshStatus();
    }

    private void RefreshStatus()
    {
        status=Integer.valueOf(getSharedPreferences().getString(BundleTag.ScreenLockStatus, Status.Start.getIndex() + ""));
        boolean homeswitch=getSharedPreferences().getBoolean(BundleTag.EnbleAPPScreenLock,false);
        switch_onexit.toggleSwitch(isActivityResumeScreenLockStatus());
        switch_onhome.toggleSwitch(homeswitch);
        String temp=String.valueOf(getAutoScreenLock())+getResources().getString(R.string.time_minute);
        SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder();
        spannableStringBuilder.append(temp);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red3)), 0, getAutoScreenLock() > 9 ? 2 : 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        hanguptime.setText(spannableStringBuilder);
        switch (status)
        {
            case 1:
                for (int i = 0; i <mainll.getChildCount()-1 ; i++) {
                    if(i<2)
                        mainll.getChildAt(i).setVisibility(View.GONE);
                    else
                        mainll.getChildAt(i).setVisibility(View.VISIBLE);
                }
                tipslayout.setVisibility(View.GONE);
                settinglayout.setVisibility(View.VISIBLE);
                break;
            case 0:
                for (int i = 2; i <mainll.getChildCount()-1 ; i++) {
                    mainll.getChildAt(i).setVisibility(View.GONE);
                }
                tipslayout.setVisibility(View.VISIBLE);
                settinglayout.setVisibility(View.GONE);
                break;
            case 2:
                for (int i = 0; i <mainll.getChildCount()-1 ; i++) {
                    if(i<2)
                    {
                        mainll.getChildAt(i).setVisibility(View.VISIBLE);
                    }
                    else
                    mainll.getChildAt(i).setVisibility(View.GONE);
                }
                tipslayout.setVisibility(View.GONE);
                settinglayout.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==BundleTag.ResultCode)
        {
            RefreshStatus();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.off:
                intent=new Intent();
                intent.setClass(this, ScreenLockActivity.class);
                intent.putExtra(BundleTag.ScreenLockStatus, Status.Off.getIndex());
                intent.putExtra(BundleTag.Title, getResources().getString(R.string.switchoffscreenlock));
                startActivityForResult(intent, BundleTag.RequestCode);
                break;
            case R.id.edit:
                intent=new Intent();
                intent.setClass(this, ScreenLockActivity.class);
                intent.putExtra(BundleTag.ScreenLockStatus, Status.Edit.getIndex());
                intent.putExtra(BundleTag.Title,getResources().getString(R.string.editscreenlock));
                startActivityForResult(intent, BundleTag.RequestCode);
                break;
            case R.id.start:
                status=Integer.valueOf(getSharedPreferences().getString(BundleTag.ScreenLockStatus, String.valueOf(Status.Start.getIndex())));
                if(status==Status.Off.getIndex())
                {
                    ScreenLockControll(3,0,null);
                }
                else
                {
                    intent=new Intent();
                    intent.setClass(this,ScreenLockActivity.class);
                    intent.putExtra(BundleTag.ScreenLockStatus, Status.Start.getIndex());
                    intent.putExtra(BundleTag.Title,getResources().getString(R.string.switchonscreenlock));
                    startActivityForResult(intent, BundleTag.RequestCode);
                }

                break;
            case R.id.hanguptimelayout:
                WheelDialog wheelDialog=new WheelDialog(this);
                wheelDialog.setOnSelectListener(new WheelDialog.OnSelectListener() {
                    @Override
                    public void OnSelect(int min) {
                        ScreenLockControll(2,min,hanguptime);
                    }
                });
                wheelDialog.show();
            break;
            case R.id.switch_onexit:
                SwitchView switchView=(SwitchView)v;
                ScreenLockControll(0,switchView.isCheck()?0:1,v);
                break;
            case R.id.switch_onhome:
                SwitchView switchView2=(SwitchView)v;
                ScreenLockControll(1,switchView2.isCheck()?0:1,v);
                break;
        }
    }

    private void ScreenLockControll(final int type,final int value,final View view)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("phone", ((BaseApplication) getApplication()).getUsername());
        switch (type)
        {
            case 0:
                requestParams.put("Homreturnlock",value+"");
                break;
            case 1:
                requestParams.put("Applock",value+"");
                break;
            case 2:
                requestParams.put("Autolock",value+"");
                break;
            case 3:
                requestParams.put("openlock","1");
        }
        Httputils.PostWithBaseUrl(Httputils.ScreenLockControll,requestParams,new MyJsonHttpResponseHandler(this,true){
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);

            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if (jsonObject.optString("status", "").equalsIgnoreCase("000000")) {
                    switch (type)
                    {
                        case 0:
                            SwitchView switchView=(SwitchView)view;
                            switchView.toggleSwitch(value>0);
                            ActivityResumeScreenLockStatus=switchView.isCheck();
                            LogTools.e("ActivityResumeScreenLockStatus",ActivityResumeScreenLockStatus+"");
                            getSharedPreferences().edit().putBoolean(BundleTag.EnbleResumeScreenLock,ActivityResumeScreenLockStatus).commit();
                            break;
                        case 1:
                            SwitchView switchView2=(SwitchView)view;
                            switchView2.toggleSwitch(value>0);
                            getSharedPreferences().edit().putBoolean(BundleTag.EnbleAPPScreenLock,ActivityResumeScreenLockStatus).commit();
                            break;
                        case 2:
                            TextView textView=(TextView)view;
                            AutoScreenLock=value;
                            String temp=String.valueOf(value)+getResources().getString(R.string.time_minute);
                            SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder();
                            spannableStringBuilder.append(temp);
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red3)), 0, value > 9 ? 2 : 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            textView.setText(spannableStringBuilder);
                            getSharedPreferences().edit().putString(BundleTag.EnbleAutoScreenLock, String.valueOf(value)).commit();
                            break;
                        case 3:
                            getSharedPreferences().edit().putString(BundleTag.ScreenLockStatus, Status.On.getIndex()+"").commit();
                            RefreshStatus();
                            break;
                    }
                }
                else
                {
                    ToastUtil.showMessage(ScreenLockParentActivity.this,jsonObject.optString("info",""));
                }
            }
        });
    }
}
