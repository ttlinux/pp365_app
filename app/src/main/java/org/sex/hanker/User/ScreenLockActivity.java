package org.sex.hanker.User;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.GestureLockViewGroup;
import org.sex.hanker.mybusiness.R;

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
    String originalpassword;
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
        originalpassword=getIntent().getStringExtra(BundleTag.ScreenLockPassword);
        switch (status)
        {
            case 0:
                tips.setText(getResources().getString(R.string.screenlocktips));
                break;
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
        mGestureLockViewGroup.setLimitListener(4,9, new GestureLockViewGroup.onLimitListener() {
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

                        switch (status)
                        {
                            case 0:
                                if(passd!=null)
                                {
                                    if(passd.equalsIgnoreCase(pass.toString()))
                                    {
                                        sharedPreferences.edit().putString(BundleTag.ScreenLockPassword,passd).putInt(BundleTag.ScreenLockStatus, Status.On.getIndex()).commit();
                                        ((BaseApplication)getApplication()).setScreenLockOpenStatus(Status.On.getIndex());
                                        ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips5));
                                        setResult(BundleTag.ResultCode);
                                        finish();

                                    }
                                    else
                                    {
                                        passd=null;
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
                            case 3:
                                if(isVerify)
                                {
                                    if(originalpassword.equalsIgnoreCase(pass.toString()))
                                    {
                                        mGestureLockViewGroup.resetAndRefresh();
                                        pass.delete(0, pass.length());
                                        tips.setText(getResources().getString(R.string.screenlocktips8));
                                        ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips8));
                                        return;
                                    }
                                    if(passd!=null)
                                    {
                                        if(passd.equalsIgnoreCase(pass.toString()))
                                        {
                                            sharedPreferences.edit().putString(BundleTag.ScreenLockPassword,passd).putInt(BundleTag.ScreenLockStatus, Status.On.getIndex()).commit();
                                            ((BaseApplication)getApplication()).setScreenLockOpenStatus(Status.On.getIndex());
                                            ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips5));
                                            setResult(BundleTag.ResultCode);
                                            finish();

                                        }
                                        else
                                        {
                                            passd=null;
                                            mGestureLockViewGroup.resetAndRefresh();
                                            tips.setText(getResources().getString(R.string.screenlocktips8));
                                            ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips4));
                                        }
                                        return;
                                    }
                                    passd = pass.toString();
                                    mGestureLockViewGroup.resetAndRefresh();
                                    pass.delete(0, pass.length());
                                    tips.setText(getResources().getString(R.string.screenlocktips3));
                                }
                                else
                                {
                                    if(originalpassword.equalsIgnoreCase(pass.toString()))
                                    {
                                        mGestureLockViewGroup.resetAndRefresh();
                                        pass.delete(0, pass.length());
                                        tips.setText(getResources().getString(R.string.screenlocktips8));
                                        isVerify=true;
                                    }
                                    else
                                    {
                                        isVerify=false;
                                        ToastUtil.showMessage(ScreenLockActivity.this, getResources().getString(R.string.screenlocktips7));
                                    }
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
}
