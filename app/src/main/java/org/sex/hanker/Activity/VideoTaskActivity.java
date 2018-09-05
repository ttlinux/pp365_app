package org.sex.hanker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import org.sex.hanker.Adapter.VideoTaskAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/30.
 */
public class VideoTaskActivity extends BaseActivity {

    ListView listview;
    VideoTaskAdapter videoTaskAdapter;
    ArrayList<LocalVideoBean> localVideoBeans;
    final int MessageRepeat=0;
    Handler handler=new Handler(){

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what)
            {
                case MessageRepeat:
                    localVideoBeans= VideoSQL.getColumnData(VideoTaskActivity.this, false);
                    setVideoTaskAdapter();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_task);
        setBacktitleText(getResources().getString(R.string.downloadManage));
        Initview();
    }

    private void Initview()
    {
        listview=(ListView)findViewById(R.id.listview);
         localVideoBeans= VideoSQL.getColumnData(this, false);
        setVideoTaskAdapter();
        handler.sendEmptyMessageDelayed(MessageRepeat, 500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MessageRepeat);
    }

    private void setVideoTaskAdapter()
    {
        if(videoTaskAdapter==null)
        {
            videoTaskAdapter=new VideoTaskAdapter(this,localVideoBeans);
            listview.setAdapter(videoTaskAdapter);
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
}
