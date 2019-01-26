package org.sex.hanker.BaseParent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.sex.hanker.Activity.NewVideoActivity;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.View.ScreenLockDialog;
import org.sex.hanker.mybusiness.R;

import java.util.Locale;

/**
 * Created by Administrator on 2017/3/28.
 */
public class BaseActivity extends Activity{


    public boolean isneedback() {
        return isneedback;
    }

    public void setIsneedback(boolean isneedback) {
        this.isneedback = isneedback;
    }

    boolean isneedback=true;

    boolean isFront;

    static int ischina=0;
    SharedPreferences sharedPreferences;
    private boolean isLeave=false;
    public boolean ActivityResumeScreenLockStatus=false;

    public int AutoScreenLock=0;

    private Handler handler=new Handler(){

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what)
            {
                case 110:
                    String username=((BaseApplication)getApplication()).getUsername();
                    if(!isFinishing() && !isDestroyed() && !TextUtils.isEmpty(username))
                    {
                        ScreenLockDialog dialog=new ScreenLockDialog(BaseActivity.this);
                        dialog.setOnUnLockListener(new ScreenLockDialog.OnUnLockListener() {
                            @Override
                            public void Onunlock() {
                                handler.sendEmptyMessageDelayed(110, 1000 * 60 * AutoScreenLock);
                            }
                        });
                        dialog.show();
                    }
                    break;
            }
        }
    };

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public boolean isActivityResumeScreenLockStatus() {
        return ActivityResumeScreenLockStatus;
    }

    public int getAutoScreenLock() {
        return AutoScreenLock;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);//slide_right_in
        sharedPreferences=((BaseApplication)getApplication()).getSharedPreferences();
        String time=sharedPreferences.getString(BundleTag.EnbleAutoScreenLock,"");
        try {
            AutoScreenLock=Integer.valueOf(time);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            AutoScreenLock=0;
        }
        ActivityResumeScreenLockStatus=sharedPreferences.getBoolean(BundleTag.EnbleResumeScreenLock,false);


        if(ischina==0)
        {
            ischina= Locale.getDefault().getLanguage().trim().toLowerCase().equalsIgnoreCase("ZH")?1:2;
        }
    }

    public static boolean getIsChina() {
        return ischina==1;
    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);//slide_left_out
    }

    public boolean isFront()
    {
        return isFront;
    }

    public void setActivityTitle(String title)
    {
        View view=FindView(R.id.title);
        if( view!=null)
        {
            view.setVisibility(View.VISIBLE);
            ((TextView)view).setText(title);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        isFront=true;
        View view=FindView(R.id.back);
        if(isneedback && view!=null)
        {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if(isLeave)
        {
            String username=((BaseApplication)getApplication()).getUsername();
            if(ActivityResumeScreenLockStatus && !TextUtils.isEmpty(username))
            {
                LogTools.e("DDDDDDDDD", "MMMMMMMMMMM1111");
                ScreenLockDialog dialog=new ScreenLockDialog(this);
                dialog.setOnUnLockListener(new ScreenLockDialog.OnUnLockListener() {
                    @Override
                    public void Onunlock() {
                        isLeave=false;
                    }
                });
                dialog.show();
            }
        }
        if(this instanceof NewVideoActivity)
        {
            // do nothing
        }
        else
        {
            if(AutoScreenLock>0 && AutoScreenLock<60)
            {
                handler.sendEmptyMessageDelayed(110, AutoScreenLock * 60 * 1000);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isFront=false;
        handler.removeMessages(110);
    }
    public void setneedback(boolean needback)
    {
        this.isneedback=needback;
    }

    public void setBacktitleFinish()
    {
        View view=FindView(R.id.backtitle);
        if( view!=null)
        {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void setBacktitleText(String str)
    {
        View view=FindView(R.id.backtitle);
        if( view!=null && view instanceof TextView)
        {
            view.setVisibility(View.VISIBLE);
            ((TextView)view).setText(str);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public <T> T FindView(int id)
    {
        return (T)findViewById(id);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        handler.removeMessages(110);
        if(AutoScreenLock>0 && AutoScreenLock<60)
        {
            handler.sendEmptyMessageDelayed(110,AutoScreenLock*60*1000);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        isLeave=true;
    }
}
