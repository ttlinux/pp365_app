package org.sex.hanker.Utils;

import android.os.Environment;

/**
 * Created by Administrator on 2017/12/28.
 */
public class BundleTag {

    public static final  String Dsuffix="XK";
    public static final String CreateTask="CreateTask";
    public static final String VideoProcessAction="org.sex.hanker.VideoProcess";
    public static final String CreateTaskAction="org.sex.hanker.CreateTask";
    public static final String XoKong="XoKong";
    public static final int NotificationTag=XoKong.hashCode();
    public static final int LoginSuccess=123213;
    public static final int Status=2;
    public static final String StatusTag="StatusTag";
    public static final String ProductId="pid";
    public static final String Country="Country";
    public static final String US="US";
    public static final String ASIA="ASIA";
    public static final String Postion="Postion";
    public static final String Data="Data";
    public static final String Index="Index";
    public static final String ID="ID";
    public static final String URL="URL";
    public static final String Title="Title";
    public static final String NoteRecord="NoteRecord";
    public static final String PageRecord="PageRecord";
    public static final String PicSavePath="/PP365/Picture/";
    public static final String VideoCachePath= Environment.getExternalStorageDirectory().getPath()+"/Xokong/";
}
