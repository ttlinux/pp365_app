package org.sex.hanker.Utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.view.Window;

/**
 * Created by Administrator on 2018/1/31.
 */
public class ChangeOrientationHandler extends Handler {

    private Activity activity;
    private boolean available=true;

    public ChangeOrientationHandler(Activity ac) {
        super();
        activity = ac;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 888 && available) {
            int orientation = msg.arg1;
            if (orientation > 45 && orientation < 135) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                Log.d(MainActivity.TAG, "横屏翻转: ");
            } else if (orientation > 135 && orientation < 225) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                Log.d(MainActivity.TAG, "竖屏翻转: ");
            } else if (orientation > 225 && orientation < 315) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//                Log.d(MainActivity.TAG, "横屏: ");
            } else if ((orientation > 315 && orientation < 360) || (orientation > 0 && orientation < 45)) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                Log.d(MainActivity.TAG, "竖屏: ");
            }
        }
        super.handleMessage(msg);
    }
}
