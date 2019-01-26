package org.sex.hanker.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.GestureLockViewGroup;
import org.sex.hanker.mybusiness.R;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/11/22.
 */
public class ScreenLockActivity extends BaseActivity {

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
    TextView tips;
    GestureLockViewGroup mGestureLockViewGroup;
    String passd;
    StringBuilder pass = new StringBuilder();
    int status;
    boolean isVerify=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenlock);
        InitView();
    }

    private void InitView()
    {
        tips=FindView(R.id.tips);
        sharedPreferences=((BaseApplication)getApplication()).getSharedPreferences();
        status=getIntent().getIntExtra(BundleTag.ScreenLockStatus, -1);

        switch (status)
        {
            case 0:
                tips.setText(getResources().getString(R.string.screenlocktips));
                break;
            case 2:
            case 3:
                tips.setText(getResources().getString(R.string.screenlocktips6));
                break;
        }
        setActivityTitle(getIntent().getStringExtra(BundleTag.Title));
        initGesture();
    }

    private void initGesture() {
//        mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.gesturelock);
//        gestureEventListener();
//        gesturePasswordSettingListener();
//        gestureRetryLimitListener();

        mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
        int width=ScreenUtils.getScreenWH(this)[0]-ScreenUtils.getDIP2PX(this,20)*2;
        int margin= ScreenUtils.getDIP2PX(this, 20);
        LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(width,width);
        ll.setMargins(margin,margin,margin,margin);
        mGestureLockViewGroup.setLayoutParams(ll);
//        mGestureLockViewGroup.setAnswer(new int[]{2, 4, 8, 6, 2, 5, 8, 4, 5, 6});
        mGestureLockViewGroup.setUnMatchExceedBoundary(4);
        mGestureLockViewGroup.setLimitListener(4, 9, new GestureLockViewGroup.onLimitListener() {
            @Override
            public void onLimit() {
                ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips));
            }
        });
        mGestureLockViewGroup
                .setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {

                    @Override
                    public void onTouchEvent(MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                            pass.delete(0, pass.length());
                    }

                    @Override
                    public void oncheck(int event) {
                    }

                    @Override
                    public void onUnmatchedExceedBoundary() {

                    }

                    @Override
                    public void onGestureEvent(boolean matched) {

                        switch (status) {
                            case 0:
                                //开启
                                if (passd != null) {
                                    if (passd.equalsIgnoreCase(pass.toString())) {
                                        EnableScreenLock(passd);
                                    } else {
                                        passd = null;
                                        mGestureLockViewGroup.resetAndRefresh();
                                        tips.setText(getResources().getString(R.string.screenlocktips));
                                        ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips4));
                                    }
                                    return;
                                }
                                passd = pass.toString();
                                mGestureLockViewGroup.resetAndRefresh();
                                pass.delete(0, pass.length());
                                tips.setText(getResources().getString(R.string.screenlocktips3));
                                break;
                            case 2:
                                //关闭
                                OffScreenPassword(pass.toString());
                                break;
                            case 3:
                                //修改
                                if (isVerify) {
                                    if (passd != null) {
                                        if (passd.equalsIgnoreCase(pass.toString())) {
                                            EnableScreenLock(passd);
                                        } else {
                                            passd = null;
                                            mGestureLockViewGroup.resetAndRefresh();
                                            tips.setText(getResources().getString(R.string.screenlocktips8));
                                            tips.setTextColor(getResources().getColor(R.color.red3));
                                            ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips4));
                                        }
                                    }
                                    else
                                    {
                                        VerifyScreenPassword(pass.toString(), 1);
                                    }

                                } else {
                                    VerifyScreenPassword(pass.toString(), 0);
                                }
                                break;
                        }


                    }

                    @Override
                    public void onBlockSelected(int cId) {
                        pass.append(String.valueOf(cId));
                    }
                });
    }

    private void EnableScreenLock(final String password)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("phone", ((BaseApplication) getApplication()).getUsername());
        requestParams.put("Screenpassword", password);
        Httputils.PostWithBaseUrl(Httputils.EnableOrEditScreenPassword, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                ToastUtil.showMessage(ScreenLockActivity.this, jsonObject.optString("info", ""));
                if (jsonObject.optString("status", "").equalsIgnoreCase("000000")) {
                    sharedPreferences.edit().putString(BundleTag.ScreenLockStatus, Status.On.getIndex() + "").commit();
                    setResult(BundleTag.ResultCode);
                    finish();
                }
            }
        });
    }

    public void VerifyScreenPassword(final String password,final int type)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("phone",((BaseApplication)getApplication()).getUsername());
        requestParams.put("Screenpassword",password);
        Httputils.PostWithBaseUrl(Httputils.VerifyScreenPassword, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if (jsonObject.optString("status", "").equalsIgnoreCase("000000")) {
                    HandleType(true,type,password);
                }
                else
                {
                    HandleType(false,type,password);
                }
            }
        });
    }

    private void HandleType(boolean status,int type,String password)
    {
        LogTools.e("ccccccc",type+"  "+status);
        if(status)
        {
            switch (type)
            {
                case 0:
                    isVerify=true;
                    mGestureLockViewGroup.resetAndRefresh();
                    pass.delete(0, pass.length());
                    tips.setText(getResources().getString(R.string.screenlocktips8));
                    tips.setTextColor(getResources().getColor(R.color.black2));
                    break;
                case 1:
                    mGestureLockViewGroup.resetAndRefresh();
                    pass.delete(0, pass.length());
                    tips.setText(getResources().getString(R.string.screenlocktips8));
                    tips.setTextColor(getResources().getColor(R.color.red3));
                    break;
            }
        }
        else
        {
            switch (type)
            {
                case 0:
                    isVerify=false;
                    mGestureLockViewGroup.resetAndRefresh();
                    pass.delete(0, pass.length());
                    tips.setText(getResources().getString(R.string.screenlocktips7));
                    tips.setTextColor(getResources().getColor(R.color.red3));
                    break;
                case 1:
                    passd=password;
                    mGestureLockViewGroup.resetAndRefresh();
                    pass.delete(0, pass.length());
                    tips.setText(getResources().getString(R.string.screenlocktips3));
                    tips.setTextColor(getResources().getColor(R.color.black2));
                    break;
            }
        }

    }

    private void OffScreenPassword(String password)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("phone",((BaseApplication)getApplication()).getUsername());
        requestParams.put("originalpassword",password);
        Httputils.PostWithBaseUrl(Httputils.OffScreenPassword, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                ToastUtil.showMessage(ScreenLockActivity.this, jsonObject.optString("info", ""));
                if(jsonObject.optString("status", "").equalsIgnoreCase("000000"))
                {
                    sharedPreferences.edit().putString(BundleTag.ScreenLockStatus, Status.Off.getIndex() + "").commit();
                    setResult(BundleTag.ResultCode);
                    finish();
                }
                else
                {
                    mGestureLockViewGroup.resetAndRefresh();
                    pass.delete(0, pass.length());
                    ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips7));
                }
            }
        });
    }
}
