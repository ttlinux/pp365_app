package org.sex.hanker.Utils.VideoDownload;

import android.content.Context;
import android.content.Intent;

import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Fragment.Video;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyRunnable;
import org.sex.hanker.Utils.OnReceiveDataListener;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.mybusiness.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

/**
 * Created by Administrator on 2018/7/31.
 */
public class VideoDownloader {

    public static final int Success = 1;
    public static final int NewTask = 2;
    public static final int MaxCount = 10;
    public static final int Exits = -1;
    public static final int ERROR = -2;
    public static final int Full = -3;
    public static HashMap<String, Future> fhashMap = new HashMap<>();


    private static ThreadPoolExecutor socketProcessor = (ThreadPoolExecutor)
            Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());//处理视频请求

    private static final String RootPath = IOUtil.getRootpath();

    public static int activeThreadCount() {
        return socketProcessor.getActiveCount();
    }

    public static long allThreadCount() {
        return socketProcessor.getTaskCount();
    }

    public static int request(final VideoBean videoBean, final Context context
    ) {

        if (socketProcessor.getActiveCount() == MaxCount) {
            return Full;
        }

        int status = 0;
        if (videoBean.getVideotype().toLowerCase().equalsIgnoreCase("m3u8")) {
            final LocalVideoBean Localbean = PrepareForM3U8(context, videoBean);
            if ((status = Localbean.getSTATUS()) == VideoSQL.Finished) return Exits;
            MyRunnable runnable = new MyRunnable() {
                @Override
                public void run() {
                    super.run();
                    DownloadM3U8(context, Localbean);
                }
            };
            fhashMap.put(Localbean.getVIDEO_ID() + Localbean.getCOUNTRY(), socketProcessor.submit(runnable));
        } else {
            final LocalVideoBean Localbean = PrepareForSingleFileDownload(context, videoBean);
            if ((status = Localbean.getSTATUS()) == VideoSQL.Finished) return Exits;
            MyRunnable runable = new MyRunnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    Localbean.setSTATUS(VideoSQL.Runing);
                    DownloadVideo(context, Localbean);
                }
            };
            fhashMap.put(Localbean.getVIDEO_ID() + Localbean.getCOUNTRY(), socketProcessor.submit(runable));
        }
        return Success | status;
    }

    private static LocalVideoBean PrepareForSingleFileDownload(Context context, VideoBean videoBean) {
        LocalVideoBean bean = VideoSQL.getColumnData(context, videoBean.getPhid(), videoBean.getCountryid());
        if (bean != null) {
            long status = IOUtil.isComplete(bean.getLocalPath());
            switch (bean.getSTATUS()) {
                case VideoSQL.Finished:
                    if (status == IOUtil.FileMiss) {
                        //可能出现文件被删除了的情况 刷新数据库
                        bean = MakeNewLocalVideoBean(videoBean);
                        VideoSQL.updateSingleColumn(context, bean);
                    }
                    break;
                case VideoSQL.ERROR:
                    VideoSQL.delateSingleColumn(bean);
                    bean = MakeNewLocalVideoBean(videoBean);
                    VideoSQL.insertData(context, bean);
                    break;
                case VideoSQL.NotYetFinish:
                    if (status == IOUtil.Complete) {
                        //可能出现下载完了但是数据库没有同步的情况 刷新数据库
                        bean.setSTATUS(VideoSQL.Finished);
                        bean.setPersent(100);
                        bean.setLocalPath(IOUtil.removeSuffix(bean.getLocalPath()));
                        VideoSQL.updateSingleColumn(context, bean);
                        return bean;
                    }
                    if (status == IOUtil.FileMiss) {
                        //可能出现文件被删除了的情况 刷新数据库
                        bean = MakeNewLocalVideoBean(videoBean);
                        VideoSQL.updateSingleColumn(context, bean);
                    }
                    break;
            }
        } else {
            bean = MakeNewLocalVideoBean(videoBean);
            VideoSQL.insertData(context, bean);
        }
        if (bean.getID() == 0) {
            bean.setID(VideoSQL.getColumnData(context, bean.getVIDEO_ID(), bean.getCOUNTRY()).getID());
        }
        return bean;
    }

    private static LocalVideoBean MakeNewLocalVideoBean(VideoBean videoBean) {
        LocalVideoBean bean = new LocalVideoBean();
        bean.setVIDEO_TITLE(videoBean.getVideoTitle());
        bean.setVIDEO_ID(videoBean.getPhid());
        bean.setSUFFIX(videoBean.getVideotype());
        bean.setSTATUS(VideoSQL.NewFile);
        bean.setVIDEO_PHOTO(videoBean.getImageUrl());
        if (videoBean.getTimelinecount().length() > 0)
            bean.setTimeLineCount(Integer.valueOf(videoBean.getTimelinecount()));
        bean.setTimeLineImageIype(videoBean.getTimelineimagetype());
        bean.setTimeLineUrl(videoBean.getTimelineurl());
        if (videoBean.getVideotype().toLowerCase().equalsIgnoreCase("m3u8")) {
            bean.setLocalPath(RootPath + "/" + BundleTag.XoKong + "/" + System.currentTimeMillis() + "/" + videoBean.getVideoTitle() + ".ts" + "." + BundleTag.Dsuffix);
        } else {
            bean.setLocalPath(RootPath + "/" + BundleTag.XoKong + "/" + videoBean.getVideoTitle() + "." + videoBean.getVideotype() + "." + BundleTag.Dsuffix);
        }


        bean.setCOLUMN_URL(videoBean.getQuality480p());
        bean.setCOUNTRY(videoBean.getCountryid());
        bean.setPersent(0);
        return bean;
    }

    public static LocalVideoBean PrepareForM3U8(Context context, VideoBean videoBean) {
        LocalVideoBean bean = VideoSQL.getColumnData_M3U8(context, videoBean.getPhid(), videoBean.getCountryid());
        if (bean != null) {
            long status = IOUtil.isComplete(bean.getLocalPath());
            switch (bean.getSTATUS()) {
                case VideoSQL.Finished:
                    if (status == IOUtil.FileMiss) {
                        //可能出现文件被删除了的情况 刷新数据库
                        bean = MakeNewLocalVideoBean(videoBean);
                        VideoSQL.updateSingleColumn(context, bean);
                        VideoSQL.DeleteM3U8Item(context, bean.getID());
                        bean.setM3U8_items(null);
                    }
                    break;
                case VideoSQL.ERROR:
                    VideoSQL.delateSingleColumn(bean);
                    bean = MakeNewLocalVideoBean(videoBean);
                    VideoSQL.insertData(context, bean);
                    VideoSQL.DeleteM3U8Item(context, bean.getID());
                    bean.setM3U8_items(null);
                    break;
                case VideoSQL.NotYetFinish:
                    if (status == IOUtil.Complete) {
                        //可能出现下载完了但是数据库没有同步的情况 刷新数据库
                        bean.setSTATUS(VideoSQL.Finished);
                        bean.setPersent(100);
                        bean.setLocalPath(IOUtil.removeSuffix(bean.getLocalPath()));
                        VideoSQL.updateSingleColumn(context, bean);
                        return bean;
                    }
                    if (status == IOUtil.FileMiss) {
                        //可能出现文件被删除了的情况 刷新数据库
                        bean = MakeNewLocalVideoBean(videoBean);
                        VideoSQL.updateSingleColumn(context, bean);
                        VideoSQL.DeleteM3U8Item(context, bean.getID());
                        bean.setM3U8_items(null);
                    }
                    break;
            }
        } else {
            bean = MakeNewLocalVideoBean(videoBean);
            VideoSQL.insertData(context, bean);
            //再搜索一次 为了填充ID 写入文件需要关联ID
            bean = VideoSQL.getColumnData_M3U8(context, videoBean.getPhid(), videoBean.getCountryid());
        }
        if (bean.getID() == 0) {
            bean.setID(VideoSQL.getColumnData(context, bean.getVIDEO_ID(), bean.getCOUNTRY()).getID());
        }
        return bean;
    }

    private static void DownloadM3U8(Context context, LocalVideoBean bean) {
        if (bean.getM3U8_items() == null || bean.getM3U8_items().size() == 0) {
            //下载全部的ts文件地址
            ArrayList<RequestM3U8Data.M3U8URLbean> m3U8URLbeans = RequestM3U8Data.getInstance().Excute(bean.getCOLUMN_URL());
            File parentfile = new File(new File(bean.getLocalPath()).getParent());
            if (parentfile == null || !parentfile.exists()) {
                parentfile.mkdirs();
            }
            ArrayList<LocalVideoBean.M3U8_ITEM> m3U8_items = LocalVideoBean.M3U8_ITEM.swip(m3U8URLbeans, bean.getID(), parentfile.getPath());
            bean.setM3U8_items(m3U8_items);
            VideoSQL.InsertM3U8Item_Range(context, m3U8_items);
        } else {
            //ts文件完整性校验
            LogTools.e("TESTING", "文件完整性校验");
            for (int i = 0; i < bean.getM3U8_items().size(); i++) {
                LocalVideoBean.M3U8_ITEM m3u8item = bean.getM3U8_items().get(i);
                if (m3u8item.getSTATUS() == VideoSQL.Finished) {
                    File file = new File(bean.getLocalPath());
                    if (file == null || !file.exists()) {
                        //文件丢失
                        LogTools.e("TESTING", "文件丢失 PID:" + m3u8item.getParent_ID() + " FILE_INDEX:" + m3u8item.getFILE_INDEX());
                        m3u8item.setSTATUS(VideoSQL.NotYetFinish);
                        m3u8item.setLocalPath(m3u8item.getLocalPath() + "." + BundleTag.Dsuffix);
                        VideoSQL.UpdateM3U8Item(context, m3u8item);
                    }
                } else {
                    break;
                }
            }
        }
        DownloadTs(context, bean);
    }

    private static void DownloadTs(final Context context, final LocalVideoBean bean) {

        VideoHTTPMethod.ProcessHttpConnectForVideo(context, bean, new OnReceiveDataListener() {
            @Override
            public void OnSuccess(long contentlength, InputStream in, LocalVideoBean bean, int listindex) {
                super.OnSuccess(contentlength, in, bean, listindex);
                HandlerTsData(context, contentlength, in, bean, listindex);
            }

            @Override
            public void OnFail(String fail) {
                super.OnFail(fail);
                VideoStatusSave(VideoSQL.ERROR, context, bean);
            }
        });
    }

    private static void DownloadVideo(final Context context, final LocalVideoBean bean) {
        VideoHTTPMethod.ProcessHttpConnectForVideo(context, bean, new OnReceiveDataListener() {
            @Override
            public void OnSuccess(long contentlength, InputStream in, LocalVideoBean bean) {
                super.OnSuccess(contentlength, in, bean);
                if(contentlength<0)
                {
                    //未知情况
                    VideoStatusSave(VideoSQL.ERROR, context, bean);
                }
                else
                {
                    HandlerVideoData(context, contentlength, in, bean);
                }

            }
            @Override
            public void OnFail(String fail) {
                super.OnFail(fail);
                VideoStatusSave(VideoSQL.ERROR, context, bean);
            }
        });
    }


    private static void HandlerTsData(Context context, long ContentLength, InputStream in, LocalVideoBean localVideoBean, int listindex) {
        byte[] buffer = new byte[VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE];
        int readBytes;
        try {
            RandomAccessFile file = new RandomAccessFile(localVideoBean.getM3U8_items().get(listindex).getLocalPath(), "rw");
            file.seek(file.length());
            long time=System.currentTimeMillis();
            long bufferlength=0;
            while ((readBytes = in.read(buffer, 0, VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE)) > -1) {
                if (Thread.currentThread().isInterrupted()) {
                    //点击后暂停走这里
                    VideoStatusSave(VideoSQL.Pause, context, localVideoBean);
                    return;
                }
                file.write(buffer, 0, readBytes);
                bufferlength=bufferlength+readBytes;
            }
            time=System.currentTimeMillis()-time;
            file.close();
            in.close();
            localVideoBean.getM3U8_items().get(listindex).setSTATUS(VideoSQL.Finished);
            String itempath = localVideoBean.getM3U8_items().get(listindex).getLocalPath();
            //删除xk后缀
            File m_file = new File(itempath);
            itempath = IOUtil.removeSuffix(itempath);
            localVideoBean.getM3U8_items().get(listindex).setLocalPath(itempath);
            m_file.renameTo(new File(itempath));

            //刷新数据库
            VideoSQL.UpdateM3U8Item(context, localVideoBean.getM3U8_items().get(listindex));

            int famount = CaculateM3U8FinishedAmount(localVideoBean);
            if (famount == localVideoBean.getM3U8_items().size()) {
                //m3u8全部下载完成
                localVideoBean.setPersent(99);
                localVideoBean.setSTATUS(VideoSQL.NotYetFinish);
                Intent intent = new Intent(BundleTag.VideoProcessAction);
                intent.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
                intent.putExtra(BundleTag.Title, context.getResources().getString(R.string.finishwithMerge));
                context.sendBroadcast(intent);
                //ts文件合成
                int status = Filemerge(context, localVideoBean);
                if (status == 1) {
                    //合并完成
                    Intent intent2 = new Intent(BundleTag.VideoProcessAction);
                    intent2.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
                    intent2.putExtra(BundleTag.Title, context.getResources().getString(R.string.complelete));
                    intent2.putExtra(BundleTag.StatusTag, BundleTag.Complete);
                }

            } else {
                //计算进度
                localVideoBean.setPersent((int) (famount * 0.01d / localVideoBean.getM3U8_items().size() * 10000));
                localVideoBean.setSTATUS(VideoSQL.NotYetFinish);
                VideoSQL.updateSingleColumn(context, localVideoBean);
                Intent intent = new Intent(BundleTag.VideoProcessAction);
                intent.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
                intent.putExtra(BundleTag.Speed,bufferlength/time);
                intent.putExtra(BundleTag.RemainData,famount+"/"+localVideoBean.getM3U8_items().size());
                context.sendBroadcast(intent);
                DownloadTs(context, localVideoBean);
            }

        } catch (IOException e) {
            e.printStackTrace();
            //网络断开或者其他情况
            localVideoBean.setSTATUS(VideoSQL.ERROR);
            VideoSQL.updateSingleColumn(context, localVideoBean);
            Intent intent = new Intent(BundleTag.VideoProcessAction);
            intent.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
            intent.putExtra(BundleTag.Title, context.getResources().getString(R.string.connecterr));
            context.sendBroadcast(intent);
        }
    }

    private static void HandlerVideoData(Context context, long ContentLength, InputStream in, LocalVideoBean localVideoBean) {
        byte[] buffer = new byte[VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE];
        int readBytes;
        try {
            File parentfile = new File(new File(localVideoBean.getLocalPath()).getParent());
            if (parentfile == null || !parentfile.exists()) {
                parentfile.mkdirs();
            }
            RandomAccessFile file = new RandomAccessFile(localVideoBean.getLocalPath(), "rw");
            file.seek(file.length());
            long time=System.currentTimeMillis();
            long sumtime=0;
            long bufferlength=0;
            long filelength=file.length();
            while ((readBytes = in.read(buffer, 0, VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE)) > -1) {
                if (Thread.currentThread().isInterrupted()) {
                    //点击后暂停走这里
                    VideoStatusSave(VideoSQL.Pause, context, localVideoBean);
                    return;
                }
                file.write(buffer, 0, readBytes);

                long ntime=System.currentTimeMillis();
                sumtime=sumtime+ntime-time;
                bufferlength=readBytes+bufferlength;
                if(sumtime>1000)
                {
                    filelength=filelength+bufferlength;
                    sumtime=0;
                    bufferlength=0;
                    localVideoBean.setSTATUS(VideoSQL.NotYetFinish);
                    localVideoBean.setPersent((int) (file.length() * 0.01d / ContentLength * 10000));
                    VideoSQL.updateSingleColumn(context, localVideoBean);
                    Intent intent = new Intent(BundleTag.VideoProcessAction);
                    intent.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
                    intent.putExtra(BundleTag.Speed, bufferlength / sumtime);
                    long length=IOUtil.isComplete(localVideoBean.getLocalPath());
                    long famount=0;
                    if(length!=IOUtil.Complete && length!=IOUtil.FileMiss)
                    {
                        famount=ContentLength-length;
                        intent.putExtra(BundleTag.RemainData,IOUtil.Formate(famount)+"/"+IOUtil.Formate(ContentLength));
                    }
                    context.sendBroadcast(intent);
                }
                time=ntime;
            }
            file.close();
            in.close();

            localVideoBean.setSTATUS(VideoSQL.Finished);
            localVideoBean.setPersent(100);
            File m_file = new File(localVideoBean.getLocalPath());
            localVideoBean.setLocalPath(IOUtil.removeSuffix(localVideoBean.getLocalPath()));
            m_file.renameTo(new File(localVideoBean.getLocalPath()));
            VideoSQL.updateSingleColumn(context, localVideoBean);
            Intent intent = new Intent(BundleTag.VideoProcessAction);
            intent.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
            context.sendBroadcast(intent);
        } catch (IOException e) {
            e.printStackTrace();
            localVideoBean.setSTATUS(VideoSQL.ERROR);
            VideoSQL.updateSingleColumn(context, localVideoBean);
            Intent intent = new Intent(BundleTag.VideoProcessAction);
            intent.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
            intent.putExtra(BundleTag.Title, context.getResources().getString(R.string.connecterr));
            context.sendBroadcast(intent);
        }
    }

    private static int CaculateM3U8FinishedAmount(LocalVideoBean localVideoBean) {
        int amount = 0;
        for (int i = 0; i < localVideoBean.getM3U8_items().size(); i++) {
            if (localVideoBean.getM3U8_items().get(i).getSTATUS() == VideoSQL.NotYetFinish) {
                break;
            } else {
                amount++;
            }
        }
        return amount;
    }

    private static int Filemerge(Context context, LocalVideoBean localVideoBean) {
        //完整性校验
        for (int i = 0; i < localVideoBean.getM3U8_items().size(); i++) {
            LocalVideoBean.M3U8_ITEM m3u8item = localVideoBean.getM3U8_items().get(i);
            if (m3u8item.getSTATUS() == VideoSQL.Finished) {
                File file = new File(m3u8item.getLocalPath());
                if (file == null || !file.exists()) {
                    ToastUtil.showMessage(context, "合并文件过程中出现文件缺失");
                    return -1;
                }
            } else {
                ToastUtil.showMessage(context, "文件检验：有未完成的ts文件");
                return -2;
            }
        }

        try {
            RandomAccessFile rfile = new RandomAccessFile(localVideoBean.getLocalPath(), "rw");
            for (int i = 0; i < localVideoBean.getM3U8_items().size(); i++) {
                LocalVideoBean.M3U8_ITEM m3u8item = localVideoBean.getM3U8_items().get(i);
                File file = new File(m3u8item.getLocalPath());
                FileInputStream input = new FileInputStream(m3u8item.getLocalPath());
                byte[] buffer = new byte[VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE];
                int readBytes;
                while ((readBytes = input.read(buffer, 0, VideoHTTPMethod.Video_DEFAULT_BUFFER_SIZE)) > -1) {
                    rfile.write(buffer, 0, readBytes);
                }
                input.close();
                file.delete();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(localVideoBean.getLocalPath());
        String newname = IOUtil.removeSuffix(localVideoBean.getLocalPath());
        file.renameTo(new File(newname));
        localVideoBean.setPersent(100);
        localVideoBean.setSTATUS(VideoSQL.Finished);
        localVideoBean.setLocalPath(newname);
        //更新数据库 删除分支ts数据
        VideoSQL.updateSingleColumn(context, localVideoBean);
        VideoSQL.DeleteM3U8Item(context, localVideoBean.getID());
        LogTools.e("loggggggg", "文件合并完成");
        return 1;
    }

    private static void VideoStatusSave(int status, Context context, LocalVideoBean localVideoBean) {
        localVideoBean.setSTATUS(status);
        VideoSQL.updateSingleColumn(context, localVideoBean);
        Intent intent = new Intent(BundleTag.VideoProcessAction);
        intent.putExtra(BundleTag.Data, BroadcastDataBean.ConverData(localVideoBean));
        context.sendBroadcast(intent);
    }

}
