package org.sex.hanker.Utils;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.View.UPdataNoticeDialog;
import org.sex.hanker.mybusiness.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import cn.jpush.android.d.e;

/**
 * Created by Administrator on 2017/5/4.
 */
public class UpdateManager {

    private boolean mBackKeyPressed = false; // 记录是否有首次按键
    private Context mContext;
    private String apkname="/BiYing/biying.apk";

    // 提示语
    private String updateMsg = "";

    // 返回的安装包url
    private String apkUrl = "";

    private Dialog  noticeDialog;

    public static int versioncode=0;

    private Dialog downloadDialog;
    /* 下载包安装路径 */
    private static String savePath = "";

    private static String saveFileName = "";

    /* 进度条与通知ui刷新的handler和msg常量 */
    private ProgressBar mProgress;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private int progress;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    public  StartAniEndListener getListener() {
        return listener;
    }

    public  void setListener(StartAniEndListener mlistener) {
        listener = mlistener;
    }

    private  StartAniEndListener listener;

    PackageInfo info;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    break;
                case DOWN_OVER:

                    installApk();
                    break;
                default:
                    break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.mContext = context;
    }

    // 外部接口让主Activity调用
    public void checkUpdateInfo() {
        RequestUpdate();
    }

    private void showNoticeDialog(String title,String msg) {
        noticeDialog= new UPdataNoticeDialog(mContext, R.style.loading_dialog);
        noticeDialog.setContentView(R.layout.dialog_notice2);
        TextView titleview=(TextView)noticeDialog.findViewById(R.id.title);
        titleview.setText(title);
        TextView contentview=(TextView)noticeDialog.findViewById(R.id.content);
        contentview.setText(msg);
        Button btn_cli2=(Button)noticeDialog.findViewById(R.id.btn_cli2);
        Button btn_cli=(Button)noticeDialog.findViewById(R.id.btn_cli);
        btn_cli.setText("立即更新");
        btn_cli2.setText("以后再说");
        btn_cli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
                showDownloadDialog(false);
                if (listener != null) listener.onStartAniEnd(true);
            }
        });
        btn_cli2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
                if (listener != null) listener.onStartAniEnd(false);
            }
        });
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
    }
    private void showNoticeDialog2(String title,String msg) {
        noticeDialog= new UPdataNoticeDialog(mContext,R.style.loading_dialog);
        noticeDialog.setContentView(R.layout.dialog_notice2);
        TextView titleview=(TextView)noticeDialog.findViewById(R.id.title);
        titleview.setText(title);
        TextView contentview=(TextView)noticeDialog.findViewById(R.id.content);
        ImageView line=(ImageView)noticeDialog.findViewById(R.id.line3);
        line.setVisibility(View.GONE);
        contentview.setText(msg);
        Button btn_cli2=(Button)noticeDialog.findViewById(R.id.btn_cli2);
        Button btn_cli=(Button)noticeDialog.findViewById(R.id.btn_cli);
        btn_cli.setText("立即更新");
        btn_cli2.setVisibility(View.GONE);
        btn_cli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeDialog.dismiss();
                showDownloadDialog(false);
            }
        });
        noticeDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogTools.e("onStartAniEnd", "7");
                if (listener != null) listener.onStartAniEnd(true);
            }
        });
        noticeDialog.setCanceledOnTouchOutside(false);
        noticeDialog.show();
    }
    private void showDownloadDialog(boolean noCancle) {
        Builder builder = new Builder(mContext);
        builder.setTitle("软件版本更新,请稍等...");

        final LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.progress, null);
        mProgress = (ProgressBar) v.findViewById(R.id.progress);

        builder.setView(v);
        downloadDialog = builder.create();
        if(noCancle)
        {
            //如果不是强制跟新
            builder.setNegativeButton("取消", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
                    String FileName = sdCard +"/"+info.packageName.substring(7,info.packageName.length())+ ".apk";
                    File file=new File(FileName);
                    if(file!=null)file.delete();
                    dialog.dismiss();
                    if (listener != null) listener.onStartAniEnd(true);
                    interceptFlag = true;
                }
            });
        }
        else
        {
            //如果是强制跟新 没有取消 能取消下载 退出程序
            downloadDialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                    if(event.getAction()==KeyEvent.KEYCODE_BACK||keyCode==event.KEYCODE_HOME)
                    {
                        LogTools.e("mBackKeyPressed",mBackKeyPressed+"  kkkk");
                        if (!mBackKeyPressed) {
                            ToastUtil.showMessage(mContext, "再按一次退出程序");
                            mBackKeyPressed = true;
                            new Timer().schedule(new TimerTask() {
                                // 延时两秒，如果超出则擦错第一次按键记录
                                @Override
                                public void run() {
                                    mBackKeyPressed = false;
                                }
                            }, 2000);
                            return true;
                        } else {
                            // 退出程序
                            ((Activity)mContext).finish();
                        }
                    }
                    return false;
                }
            });
            downloadDialog.setCancelable(false);
        }



        downloadDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogTools.e("onStartAniEnd","0");
                if (listener != null) listener.onStartAniEnd(true);
            }
        });
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();
        downloadApk();
    }
    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {

            File ApkFile =null;
            try {
                URL url = new URL(apkUrl);

                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdir();
                }
                ApkFile = new File(saveFileName);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte buf[] = new byte[1024];

                do {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载.

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                if(ApkFile!=null) {
                    ApkFile.delete();
                    downloadDialog.dismiss();
                }
            } catch (IOException e) {
                e.printStackTrace();
                if(ApkFile!=null) {
                    ApkFile.delete();
                    downloadDialog.dismiss();
                }
            }

        }
    };

    /**
     * 下载apk
     *
     * @param url
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }


    /**
     * 安装apk
     *
     * @param url
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        ((Activity)mContext).startActivityForResult(i, 123456789);

    }

    private void RequestUpdate() {

        if (!ExistSDCard())
        {
            LogTools.e("onStartAniEnd","1");
            if(listener!=null)listener.onStartAniEnd(false);
            return;
        }
        PackageManager manager = mContext
                .getPackageManager();

        try {
            info = manager.getPackageInfo(
                    mContext.getPackageName(), 0);
        } catch (NameNotFoundException e) {

        }
        savePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        saveFileName = savePath +apkname;
        RequestParams params=new RequestParams();
        //http://up.djr158.com/App/config.json

//        Httputils.Post(Httputils.BaseUrl+ Httputils.MobileVersion, null, new MyJsonHttpResponseHandler(((Activity) mContext), false) {
//
//            @Override
//            public void onSuccessOfMe(JSONObject jsonObject) {
//                super.onSuccessOfMe(jsonObject);
//                LogTools.e("jsonObject.....", jsonObject.toString());
//            }
//
//            @Override
//            public void onFailureOfMe(Throwable throwable, String s) {
//                super.onFailureOfMe(throwable, s);
//            }
//        });
        Httputils.PostWithBaseUrl(Httputils.httpurlAndroidUpdate(), params,
                new MyJsonHttpResponseHandler(((Activity) mContext), false) {

                    @Override
                    public void onFailureOfMe(Throwable throwable, String s) {
                        super.onFailureOfMe(throwable, s);
                        LogTools.e("onStartAniEnd", "2");
                        if (listener != null) listener.onStartAniEnd(false);
                    }

                    @Override
                    public void onSuccessOfMe(JSONObject jsonobj) {

                        super.onSuccessOfMe(jsonobj);
                        Log.e("PostWithBaseUrl", jsonobj.toString());

                        if (!jsonobj.optString("errorCode", "").equalsIgnoreCase("000000")){
                            if (listener != null) listener.onStartAniEnd(false);
                        return;
                        }

                        JSONObject json=jsonobj.optJSONObject("datas");
                        if (json.has("upgradeDesc"))
                        updateMsg=json.optString("upgradeDesc","");
                        String versionName = info.versionName;
                        String Package = info.packageName;
                        if (!json.has("versionCode") || !json.has("updatePackage")) {
                            LogTools.e("onStartAniEnd","3");
                            if (listener != null) listener.onStartAniEnd(false);
                            return;
                        }
                        versioncode = info.versionCode;
                        int D_versioncode =0;
                        if(Httputils.isNumber(json.optString("versionCode", "")))
                            D_versioncode = Integer.valueOf(json.optString("versionCode", ""));
                        else {
                            if (listener != null) listener.onStartAniEnd(false);
                            return;
                        }
                        if (!Package.equalsIgnoreCase(json.optString("updatePackage", ""))) {
                            LogTools.e("onStartAniEnd","4");
                            if (listener != null) listener.onStartAniEnd(false);
                            return;
                        }
                        if (versioncode < D_versioncode) {
                            apkUrl = json.optString("updateLink","");
                            Httputils.AndroidApkPath=apkUrl;
                            if (isaviableUpdatafromSDcard())//如果已经下载到sd卡了
                            {
                                CreateDialog();
                            } else {
                                String title =updateMsg.substring(0, updateMsg.indexOf("|"));
                                if (json.has("updateMode") && json.optString("updateMode", "").equalsIgnoreCase("1")) {

                                    showNoticeDialog(title, updateMsg.substring(title.length()+1));
                                } else if(json.has("updateMode") && json.optString("updateMode", "").equalsIgnoreCase("2")){
                                    showNoticeDialog2(title, updateMsg.substring(title.length() + 1));
                                }
                            }
                        } else {
                            LogTools.e("onStartAniEnd","5");
                            if (listener != null) listener.onStartAniEnd(false);
                        }

                    }
                });
    }

    private boolean isaviableUpdatafromSDcard()
    {
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
        String FileName = sdCard +apkname;
        File file=new File(FileName);
        if(file==null || file.exists()==false)return false;

        PackageManager pm =mContext.getPackageManager();
        PackageInfo	info2 = pm.getPackageArchiveInfo(FileName, PackageManager.GET_ACTIVITIES);
        if(info2==null)
        {
            //下载的包没下载完会出现的问题
            file.delete();
            return false;
        }
        int code=Integer.valueOf(info.versionCode);
        int code2=Integer.valueOf(info2.versionCode);
        if(info.packageName.equalsIgnoreCase(info2.packageName) && code2>code)
        {
            saveFileName = savePath +apkname;
            return true;
        }
        return false;
    }

    private void CreateDialog()
    {
        Builder  builder = new Builder(mContext);
        builder.setTitle("软件已经下载完成，是否更新(不消耗流量)");
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (listener != null) {
                    LogTools.e("lisss", "lisss");
                }
                installApk();
            }
        });
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogTools.e("onStartAniEnd","6");
                if (listener != null) listener.onStartAniEnd(true);
            }
        });
        builder.create().show();
    }
    private boolean ExistSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    public  interface StartAniEndListener
    {
        public abstract void onStartAniEnd(boolean hasupdate);
    }

    public void setStartAniEndListener(StartAniEndListener m_listener)
    {
        listener=m_listener;
    }
}
