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

    public static final String Download="Download";
    public static final String Pause="Pause";

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
        String ExcuteType=intent.getStringExtra(BundleTag.ExcuteType);
        if(ExcuteType==null)
            return super.onStartCommand(intent, START_STICKY, startId);

        Serializable obj = intent.getSerializableExtra(BundleTag.Data);
        if (obj == null) return super.onStartCommand(intent, START_STICKY, startId);
        if(ExcuteType.equalsIgnoreCase(Download))
        {
            VideoBean bean = (VideoBean) obj;
            int Code = VideoDownloader.request(bean, this);
            Intent cloneintent = new Intent();
            cloneintent.setAction(BundleTag.VideoStatusAction);
            cloneintent.putExtra(BundleTag.Country, bean.getCountryid());
            cloneintent.putExtra(BundleTag.ID,bean.getPhid());

            if (Code == (VideoDownloader.Success ^ VideoSQL.NewFile) || Code == (VideoDownloader.Success ^ VideoSQL.NotYetFinish)) {
                switch (Code ^ VideoDownloader.Success)
                {
                    case VideoSQL.NewFile:
                        cloneintent.putExtra(BundleTag.Status,BundleTag.Success_NewFile);
                        break;
                    case VideoSQL.NotYetFinish:
                        cloneintent.putExtra(BundleTag.Status,BundleTag.Success_NotYetFinish);
                        break;
                }
            } else {
                switch (Code) {
                    case VideoDownloader.ERROR:
                        cloneintent.putExtra(BundleTag.Status,BundleTag.Failure_ERROR);
                        break;
                    case VideoDownloader.Exist:
                        cloneintent.putExtra(BundleTag.Status,BundleTag.Failure_Exits);
                        break;
                    case VideoDownloader.InLine:
                        cloneintent.putExtra(BundleTag.Status,BundleTag.Failure_InLine);
                        break;
                    case VideoDownloader.Full:
                        cloneintent.putExtra(BundleTag.Status,BundleTag.Failure_Full);
                        break;
                }
            }
            sendBroadcast(cloneintent);
        }
        else if(ExcuteType.equalsIgnoreCase(Pause))
        {
            VideoBean bean = (VideoBean) obj;
            VideoDownloader.CancelTask(bean.getPhid(),bean.getCountryid());
        }

        return super.onStartCommand(intent, START_STICKY, startId);
    }
}
