package org.sex.hanker.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.SparseArray;
import android.widget.ListView;

import com.google.gson.Gson;

import org.sex.hanker.Adapter.VideoTaskAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/30.
 */
public class VideoTaskActivity extends BaseActivity {

    RecyclerView recycleview;
    VideoTaskAdapter videoTaskAdapter;
    SparseArray<BroadcastDataBean> localVideoBeans;
    final int MessageRepeat=0;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_task);
        setBacktitleText(getResources().getString(R.string.downloadManage));
        setBacktitleFinish();
        Initview();
    }

    @Override
    protected void onStart() {
        super.onStart();
        localVideoBeans= VideoSQL.getColumnData(false);
        LogTools.e("localVideoBeans",new Gson().toJson(localVideoBeans));
        setVideoTaskAdapter(-1);
        RegisterBoardcast();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if(broadcastReceiver!=null)
        {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
    }

    private void Initview()
    {
        recycleview=FindView(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recycleview.getItemAnimator().setAddDuration(0);
        recycleview.getItemAnimator().setChangeDuration(0);
        recycleview.getItemAnimator().setMoveDuration(0);
        recycleview.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recycleview.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null)
        {
            unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }

    }

    private void setVideoTaskAdapter(int index)
    {
        if(videoTaskAdapter==null)
        {
            videoTaskAdapter=new VideoTaskAdapter(this,localVideoBeans);
            recycleview.setAdapter(videoTaskAdapter);
        }
        else
        {
            if(index>-1)
            {
                videoTaskAdapter.NotifyAdapter(localVideoBeans,index);
            }
            else
            {
                videoTaskAdapter.NotifyAdapter(localVideoBeans);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void RegisterBoardcast()
    {
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equalsIgnoreCase(BundleTag.VideoProcessAction))
                {
                    BroadcastDataBean bean=null;
                    bean=(BroadcastDataBean)intent.getSerializableExtra(BundleTag.Data);
                    if(bean==null)
                        bean=(BroadcastDataBean)intent.getSerializableExtra(BundleTag.CreateTask);
                    localVideoBeans.put(bean.getID(), bean);
                    setVideoTaskAdapter( localVideoBeans.indexOfKey(bean.getID()));
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BundleTag.VideoProcessAction);
        registerReceiver(broadcastReceiver,intentFilter);
    }
}
