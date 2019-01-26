package org.sex.hanker.View;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler2;
import org.sex.hanker.Utils.OnMultiClickListener;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.mybusiness.R;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/11/23.
 */
public class ScreenLockDialog extends RelativeLayout {

    GestureLockViewGroup mGestureLockViewGroup;
    View view;
    StringBuilder pass = new StringBuilder();
    WindowManager windowManager;
    OnUnLockListener onUnLockListener;

    public OnUnLockListener getOnUnLockListener() {
        return onUnLockListener;
    }

    public void setOnUnLockListener(OnUnLockListener onUnLockListener) {
        this.onUnLockListener = onUnLockListener;
    }

    public ScreenLockDialog(Context context) {
        super(context);
        InitView(context);
    }

    public ScreenLockDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public ScreenLockDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    private void InitView( Context context)
    {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        view=View.inflate(context,R.layout.dialog_screenlock,null);
        addView(view);
        Init(context);
    }

    private void Init(final Context context)
    {
        mGestureLockViewGroup = (GestureLockViewGroup) findViewById(R.id.id_gestureLockViewGroup);
        int width= ScreenUtils.getScreenWH((Activity)context)[0]-ScreenUtils.getDIP2PX(context,20)*2;
        int margin= ScreenUtils.getDIP2PX(context, 20);
        LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(width,width);
        ll.setMargins(margin,margin,margin,margin);
        mGestureLockViewGroup.setLayoutParams(ll);
//        mGestureLockViewGroup.setAnswer(new int[]{2, 4, 8, 6, 2, 5, 8, 4, 5, 6});
        mGestureLockViewGroup.setUnMatchExceedBoundary(4);
        mGestureLockViewGroup.setLimitListener(4, 9, new GestureLockViewGroup.onLimitListener() {
            @Override
            public void onLimit() {
                ToastUtil.showMessage(context, getResources().getString(R.string.screenlocktips));
            }
        });
        mGestureLockViewGroup.setOnGestureLockViewListener(new GestureLockViewGroup.OnGestureLockViewListener() {
            @Override
            public void onBlockSelected(int cId) {
                pass.append(String.valueOf(cId));
            }

            @Override
            public void oncheck(int oncheck) {

            }

            @Override
            public void onGestureEvent(boolean matched) {
                VerifyScreenPassword(pass.toString(),context);
            }

            @Override
            public void onUnmatchedExceedBoundary() {

            }

            @Override
            public void onTouchEvent(MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    pass.delete(0, pass.length());
            }
        });
    }

    public void show()
    {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.FILL_PARENT
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.format = PixelFormat.A_8; // 设置图片格式，效果为背景透明
        lp.gravity= Gravity.TOP;
        lp.y=0;
        setBackgroundColor(getResources().getColor(R.color.white));
        windowManager.addView(this, lp);
    }

    public void dismiss()
    {
        windowManager.removeView(this);
    }

    public void VerifyScreenPassword(String password,Context context)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("phone", ((BaseApplication) context.getApplicationContext()).getUsername());
        requestParams.put("Screenpassword",password);
        Httputils.PostWithBaseUrl(Httputils.VerifyScreenPassword, requestParams, new MyJsonHttpResponseHandler2(context, true) {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if (jsonObject.optString("status", "").equalsIgnoreCase("000000")) {
                    dismiss();
                    if(onUnLockListener!=null)
                        onUnLockListener.Onunlock();
                }
                else
                {
                    pass.delete(0,pass.length());
                    mGestureLockViewGroup.resetAndRefresh();
                }
            }
        });
    }

    public interface OnUnLockListener
    {
        public void Onunlock();
    }
}
