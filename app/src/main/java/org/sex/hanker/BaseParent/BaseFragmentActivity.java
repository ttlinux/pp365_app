package org.sex.hanker.BaseParent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;

import org.sex.hanker.Activity.NewVideoActivity;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.View.ScreenLockDialog;
import org.sex.hanker.mybusiness.R;

import java.util.Locale;


/**
 * Created by Administrator on 2017/3/28.
 */
public class BaseFragmentActivity extends FragmentActivity {

    public int AutoScreenLock = 0;
    private boolean isLeave = false;
    public boolean ActivityResumeScreenLockStatus = false;
    HomeWatcherReceiver mHomeWatcherReceiver;

    private Handler handler = new Handler() {

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 110:
                    String username=((BaseApplication)getApplication()).getUsername();
                    if (!isFinishing() && !isDestroyed() && !TextUtils.isEmpty(username)) {
                        ScreenLockDialog dialog = new ScreenLockDialog(BaseFragmentActivity.this);
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


    public boolean isneedback() {
        return isneedback;
    }

    public void setIsneedback(boolean isneedback) {
        this.isneedback = isneedback;
    }

    boolean isneedback = true;
    static int ischina = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogTools.e("TTTTT", "onCreate");
        SharedPreferences sharedPreferences = ((BaseApplication) getApplication()).getSharedPreferences();
        String time = sharedPreferences.getString(BundleTag.EnbleAutoScreenLock, "");
        try {
            AutoScreenLock = Integer.valueOf(time);
        } catch (Exception ex) {
            ex.printStackTrace();
            AutoScreenLock = 0;
        }
        ActivityResumeScreenLockStatus = sharedPreferences.getBoolean(BundleTag.EnbleResumeScreenLock, false);


        if (ischina == 0) {
            ischina = Locale.getDefault().getLanguage().trim().toLowerCase().equalsIgnoreCase("ZH") ? 1 : 2;
        }
        mHomeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mHomeWatcherReceiver, filter);
    }

    public static boolean getIsChina() {
        return ischina == 1;
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogTools.e("TTTTT", "onStart");
        View view = FindView(R.id.back);

        if (view != null && isneedback) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (isLeave) {
            String username=((BaseApplication)getApplication()).getUsername();
            if (ActivityResumeScreenLockStatus && !TextUtils.isEmpty(username)) {
                LogTools.e("DDDDDDDDD", "MMMMMMMMMMM222");
                ScreenLockDialog dialog = new ScreenLockDialog(this);
                dialog.setOnUnLockListener(new ScreenLockDialog.OnUnLockListener() {
                    @Override
                    public void Onunlock() {
                        isLeave = false;
                    }
                });
                dialog.show();
            }
        }
        if (AutoScreenLock > 0 && AutoScreenLock < 60) {
            handler.sendEmptyMessageDelayed(110, AutoScreenLock * 60 * 1000);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public <T> T FindView(int id) {
        return (T) findViewById(id);
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(110);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        handler.removeMessages(110);
        if (AutoScreenLock > 0 && AutoScreenLock < 60) {
            handler.sendEmptyMessageDelayed(110, AutoScreenLock * 60 * 1000);
        }
        return super.dispatchTouchEvent(ev);
    }


    public class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (TextUtils.equals(intentAction, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.equals(SYSTEM_DIALOG_REASON_HOME_KEY, reason)) {
                    LogTools.e("DDDDDDDDD", "onUserLeaveHint");
                    isLeave = true;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHomeWatcherReceiver != null) {
            try {
                unregisterReceiver(mHomeWatcherReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
