package org.sex.hanker.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Receiver.VideoProcessReceiver;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.Utils.VideoDownload.VideoDownloader;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/30.
 */
public class DownloadService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogTools.e("DownloadService", "onStartCommand"+ (intent==null?"intent_null":"intent not null"));
        if (intent == null) return super.onStartCommand(intent, START_STICKY, startId);
        Serializable obj = intent.getSerializableExtra(BundleTag.Data);
        if (obj == null) return super.onStartCommand(intent, START_STICKY, startId);
        VideoBean bean = (VideoBean) obj;
        int Code = VideoDownloader.request(bean, this);
        if (Code == (VideoDownloader.Success | VideoSQL.NewFile)) {
            Intent cloneintent = new Intent();
            cloneintent.setAction(BundleTag.CreateTaskAction);
            cloneintent.putExtra(BundleTag.CreateTask, bean);
            sendBroadcast(cloneintent);
            ToastUtil.showMessage(this, "已加入下载队列");
        } else {
            switch (Code) {
                case VideoDownloader.ERROR:
                    ToastUtil.showMessage(this, "下载出错 -_-");
                    break;
                case VideoDownloader.Exits:
                    ToastUtil.showMessage(this, "文件已下载完成");
                    break;
                case VideoDownloader.Full:
                    ToastUtil.showMessage(this, "下载最大任务数为" + VideoDownloader.MaxCount);
                    break;
            }
        }

        return super.onStartCommand(intent, START_STICKY, startId);
    }
}
