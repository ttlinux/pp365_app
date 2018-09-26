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
import android.widget.ListView;

import org.sex.hanker.Adapter.VideoTaskAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/30.
 */
public class VideoTaskActivity extends BaseActivity {

    RecyclerView recycleview;
    VideoTaskAdapter videoTaskAdapter;
    ArrayList<LocalVideoBean> localVideoBeans;
    final int MessageRepeat=0;
    BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_task);
        setBacktitleText(getResources().getString(R.string.downloadManage));
        Initview();
    }

    @Override
    protected void onStart() {
        super.onStart();
        localVideoBeans= VideoSQL.getColumnData(this, false);
        setVideoTaskAdapter();
        RegisterBoardcast();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    private void Initview()
    {
        recycleview=FindView(R.id.recycleview);
        recycleview.setLayoutManager(new LinearLayoutManager(this));
        recycleview.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void setVideoTaskAdapter()
    {
        if(videoTaskAdapter==null)
        {
            videoTaskAdapter=new VideoTaskAdapter(this,localVideoBeans);
            recycleview.setAdapter(videoTaskAdapter);
        }
        else
        {
            videoTaskAdapter.NotifyAdapter(localVideoBeans);
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
                if(intent.getAction().equalsIgnoreCase(BundleTag.CreateTaskAction))
                {
//                    VideoBean videoBean=(VideoBean)intent.getSerializableExtra(BundleTag.CreateTask);
                    localVideoBeans= VideoSQL.getColumnData(VideoTaskActivity.this, false);
                }
                if(intent.getAction().equalsIgnoreCase(BundleTag.VideoProcessAction))
                {
                    LocalVideoBean bean=(LocalVideoBean)intent.getSerializableExtra(BundleTag.Data);

                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BundleTag.CreateTaskAction);
        intentFilter.addAction(BundleTag.VideoProcessAction);
        registerReceiver(broadcastReceiver,intentFilter);
    }
}
