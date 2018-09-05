package org.sex.hanker.Utils;

import org.sex.hanker.Bean.LocalVideoBean;

import java.io.InputStream;

/**
 * Created by Administrator on 2018/8/2.
 */
public class OnReceiveDataListener {

    public void  OnSuccess(long contentlenght,InputStream in,LocalVideoBean bean){};
    public void  OnSuccess(long contentlenght,InputStream in,LocalVideoBean bean,int M3U8InListIndex){};
    public void OnFail(String fail){};
}
