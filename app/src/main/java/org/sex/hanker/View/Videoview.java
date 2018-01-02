package org.sex.hanker.View;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.mybusiness.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.CenterLayout;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Administrator on 2018/1/1.
 */
public class Videoview extends RelativeLayout implements MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener,View.OnClickListener{

    private CenterLayout clayout;
    private VideoView buffer;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView;
    private String path = Environment.getExternalStorageDirectory() + "/UCDownloads/VideoData/kk.mp4";//UCDownloads/VideoData/kk.mp4
    private Uri uri;
    private SeekBar seekbar;
    private RelativeLayout sidebar;
    private RelativeLayout bottomview;
    private CheckBox lock;
    private boolean istoolViewShow=false;
    private final int DisapearToolView=0,Play=1;
    private Handler handler=new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what)
            {
                case DisapearToolView:
                    disappearToolView();
                    break;
                case Play:
                    break;
            }
        }
    };

    public String getPath() {
        return path;
    }

    public void setPathAndPlay(String path) {
        this.path = path;
        buffer.start();
    }

    public Videoview(Context context) {
        super(context);
        Initview(context);
    }

    public Videoview(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initview(context);
    }

    public Videoview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initview(context);
    }

    public void Initview(Context context)
    {
        this.addView(View.inflate(context,R.layout.videoview_layout,null));
        buffer=(VideoView)findViewById(R.id.buffer);
        bottomview=(RelativeLayout)findViewById(R.id.bottomview);
        sidebar=(RelativeLayout)findViewById(R.id.sidebar);
        clayout=(CenterLayout)findViewById(R.id.clayout);
        pb = (ProgressBar) findViewById(R.id.probar);
        seekbar=(SeekBar)findViewById(R.id.seekbar);
        lock=(CheckBox)findViewById(R.id.lock);

        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);

        uri = Uri.parse(path);
        buffer.requestFocus();
        buffer.setOnClickListener(this);
        buffer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW); //设置 MediaPlayer.VIDEOQUALITY_LOW  VIDEOQUALITY_MEDIUM VIDEOQUALITY_HIGH
        buffer.setOnInfoListener(this);
        buffer.setOnBufferingUpdateListener(this);
        buffer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                // optional need Vitamio 4.0
                mediaPlayer.setPlaybackSpeed(1.0f);
            }
        });
        buffer.setVideoURI(uri);
        ((Activity)context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        clayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        buffer.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
//        buffer.pause();

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        LogTools.e("sadsad", what + " " + extra);
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (buffer.isPlaying()) {
                    buffer.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    downloadRateView.setVisibility(View.VISIBLE);
                    loadRateView.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                buffer.start();
                handler.sendEmptyMessageDelayed(DisapearToolView, 5000);
                LogTools.e("bufferyy",buffer.getDuration()+"");
                pb.setVisibility(View.GONE);
                downloadRateView.setVisibility(View.GONE);
                loadRateView.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //播放完成
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        //播放出错
        return false;
    }

    private void disappearToolView()
    {
        bottomview.setVisibility(GONE);
        seekbar.setVisibility(GONE);
        sidebar.setVisibility(GONE);
    }

    private void showappearToolView()
    {
        bottomview.setVisibility(VISIBLE);
        seekbar.setVisibility(VISIBLE);
        sidebar.setVisibility(VISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.buffer:
                if(!istoolViewShow)
                {
                    lock.setVisibility(VISIBLE);
                    if(!lock.isChecked())
                    {
                        showappearToolView();
                    }
                    istoolViewShow=true;
                }
                else
                {
                    lock.setVisibility(GONE);
                    if(!lock.isChecked())
                    {
                        disappearToolView();
                    }
                    istoolViewShow=false;
                }
                break;
        }
    }
}
