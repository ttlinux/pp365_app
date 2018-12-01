package org.sex.hanker.Utils;

import android.os.Environment;

/**
 * Created by Administrator on 2017/12/28.
 */
public class BundleTag {

    public static final int RequestCode=998;
    public static final int ResultCode=1001;
    public static final int MaxCount = 10;
    public static final String Dsuffix = "XK";
    public static final String CreateTask = "CreateTask";
    public static final String VideoProcessAction = "org.sex.hanker.VideoProcess";
    public static final String VideoStatusAction = "org.sex.hanker.VideoStatus";
    public static final String XoKong = "XoKong";
    public static final int NotificationTag = XoKong.hashCode();
    public static final int LoginSuccess = 123213;
    public static final String Status = "Status";
    public static final String StatusTag = "StatusTag";
    public static final String ProductId = "pid";
    public static final String Country = "Country";
    public static final String IsfirstTime = "IsfirstTime";
    public static final String US = "US";
    public static final String ASIA = "ASIA";
    public static final String ScreenLockPassword = "ScreenLockPassword";
    public static final String ScreenLockStatus = "ScreenLockStatus";
    public static final String Postion = "Postion";
    public static final String Data = "Data";
    public static final String ExcuteType = "ExcuteType";
    public static final String Speed = "Speed";
    public static final String RemainData = "RemainData";
    public static final String VideoStatus = "VideoStatus";
    public static final String Index = "Index";
    public static final String ID = "ID";
    public static final String URL = "URL";
    public static final String Title = "Title";
    public static final int Complete = 666;
    public static final String NoteRecord = "NoteRecord";
    public static final String PageRecord = "PageRecord";
    public static final String password = "password";
    public static final String userName = "userName";

    public static final String PicSavePath = "/PP365/Picture/";
    public static final String VideoCachePath = Environment.getExternalStorageDirectory().getPath() + "/Xokong/";

    public static final int Success_NewFile = 0;
    public static final int Success_NotYetFinish = 1;
    public static final int Failure_Exits = -1;
    public static final int Failure_ERROR = -2;
    public static final int Failure_Full = -3;
    public static final int Failure_InLine = -4;
}
