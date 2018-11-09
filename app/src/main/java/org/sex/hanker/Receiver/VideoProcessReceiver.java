package org.sex.hanker.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;

import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/2.
 */
public class VideoProcessReceiver extends BroadcastReceiver {

    public interface VideoProcessListener {
        public void onProcess(BroadcastDataBean bean);
    }

    public static ArrayList<VideoProcessListener> listeners = new ArrayList<>();

    public static void addListeners(VideoProcessListener listener) {
        listeners.add(listener);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            if (intent.getAction().equalsIgnoreCase(BundleTag.VideoProcessAction)) {
                BroadcastDataBean bean = null;
                bean = (BroadcastDataBean) intent.getSerializableExtra(BundleTag.CreateTask);
                if (bean == null)
                    bean = (BroadcastDataBean) intent.getSerializableExtra(BundleTag.Data);
                if (bean == null) return;
                LogTools.e("bbbb222", new Gson().toJson(bean));
                for (int i = 0; i < listeners.size(); i++) {
                    listeners.get(i).onProcess(bean);
                }
            }

        }
    }

//    private void setNotification(Context context)
//    {
//        Intent intentClick = new Intent(context, VideoTaskActivity.class);
//        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(context, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService
//                (context.NOTIFICATION_SERVICE);
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
//        mBuilder.setContentTitle(context.getResources().getString(R.string.this_app_name))
//                //设置内容
//                .setContentText(Message)
//                        //设置大图标
//                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.appicon))
//                        //设置小图标
//                .setSmallIcon(R.drawable.appicon)
//                        //设置通知时间
//                .setWhen(System.currentTimeMillis())
//                .setAutoCancel(true)
//                        //首次进入时显示效果
//                        //设置通知方式，声音，震动，呼吸灯等效果，这里通知方式为声音
//                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
//                .setContentIntent(pendingIntentClick);
//    }
}
