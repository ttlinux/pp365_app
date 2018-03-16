package org.sex.hanker.Utils;

import android.app.Activity;

import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Administrator on 2018/3/16.
 */
public class ClearCache {

    public static final int Fiction=0;
    public static final int Picture=1;
    public static final int DownloadMovie=2;
    public static void Clear(int type,Activity activity)
    {
        switch (type)
        {
            case Fiction:
                break;
            case Picture:
                deleteDir(StorageUtils.getOwnCacheDirectory(
                        activity, BundleTag.PicSavePath));
                break;
            case DownloadMovie:
                break;
        }
        ToastUtil.showMessage(activity,"清除完成");
    }

    private static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }
}
