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

import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ToastUtil;
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
    SharedPreferences sharedPreferences;
    String password;
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

        sharedPreferences=((BaseApplication)getApplication()).getSharedPreferences();
        RefreshStatus();
    }

    private void RefreshStatus()
    {
        password=sharedPreferences.getString(BundleTag.ScreenLockPassword, "");
        status=sharedPreferences.getInt(BundleTag.ScreenLockStatus,Status.Start.getIndex());

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
                ((BaseApplication)getApplication()).setScreenLockOpenStatus(Status.Off.getIndex());
                sharedPreferences.edit().putInt(BundleTag.ScreenLockStatus,Status.Off.getIndex()).commit();
                RefreshStatus();
                ToastUtil.showMessage(this,getResources().getString(R.string.screenlocktips2));
                break;
            case R.id.edit:
                intent=new Intent();
                intent.setClass(this,ScreenLockActivity.class);
                intent.putExtra(BundleTag.ScreenLockPassword, password);
                intent.putExtra(BundleTag.ScreenLockStatus, Status.Edit.getIndex());
                intent.putExtra(BundleTag.Title,getResources().getString(R.string.editscreenlock));
                startActivityForResult(intent, BundleTag.RequestCode);
                break;
            case R.id.start:
                if(password!=null && password.length()>0)
                {
                    ((BaseApplication)getApplication()).setScreenLockOpenStatus(Status.On.getIndex());
                    sharedPreferences.edit().putInt(BundleTag.ScreenLockStatus,Status.On.getIndex()).commit();
                    RefreshStatus();
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
                        String temp=String.valueOf(min)+getResources().getString(R.string.time_minute);
                        SpannableStringBuilder spannableStringBuilder=new SpannableStringBuilder();
                        spannableStringBuilder.append(temp);
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red3)), 0, min>9?2:1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        hanguptime.setText(spannableStringBuilder);
                    }
                });
                wheelDialog.show();
            break;
            case R.id.switch_onexit:
                SwitchView switchView=(SwitchView)v;
                switchView.toggleSwitch(!switchView.isCheck());
                break;
            case R.id.switch_onhome:
                SwitchView switchView2=(SwitchView)v;
                switchView2.toggleSwitch(!switchView2.isCheck());

                break;
        }
    }


}
