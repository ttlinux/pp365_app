package org.sex.hanker.View;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.TimeUtils;
import org.sex.hanker.mybusiness.R;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.widget.CenterLayout;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Administrator on 2018/1/1.
 */
public class Videoview extends RelativeLayout implements MediaPlayer.OnInfoListener,
        MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener, GestureDetector.OnGestureListener {
    AudioManager am;
    private CenterLayout clayout;
    private VideoView buffer;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView,duration;
    private String path = Environment.getExternalStorageDirectory() + "/UCDownloads/VideoData/kk.mp4";//UCDownloads/VideoData/kk.mp4
    private Uri uri;
    private SeekBar seekbar, volunmseekbar;
    private RelativeLayout sidebar;
    private RelativeLayout bottomview;
    private CheckBox lock;
    private ImageView bright, volume, imagetag,pauseorstart,fullscreen;
    private boolean istoolViewShow = false;
    private final int DisapearToolView = 0, ShowToolView=1,Play = 2,Pause=3;
    VolumeReceiver receiver;
    Context context;
    private final static int Mode_Bright = 1;
    private final static int Mode_Volume = 0;
    private final static int PMode_Pause = 1;
    private final static int PMode_Play = 0;
    private final static int FullScreen = 0;
    private final static int PartScreen = 1;
    private final static int ToolviewDisappearTime=8000;
    int vSeekMode = Mode_Volume;
    int pMode=PMode_Pause;
    int ScreenMode=PartScreen;
    int Bright_process = 0;
    long Duration=0;
    private GestureDetectorCompat mDetector;
    public OnLockScreenListener onLockScreenListener;

    public interface OnLockScreenListener
    {
        public void onlock(boolean lock);
    }
    public void setOnLockScreenListener(OnLockScreenListener onLockScreenListener) {
        this.onLockScreenListener = onLockScreenListener;
    }

    private Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case DisapearToolView:
                    disappearToolView();
                    break;
                case ShowToolView:
                    showappearToolView();
                    sendEmptyMessageDelayed(DisapearToolView,ToolviewDisappearTime);
                    break;
                case Play:
                    if(Duration>0)
                    {
                        duration.setText(TimeUtils.FormatTime(buffer.getCurrentPosition()) + "/" + TimeUtils.FormatTime(Duration));
                        seekbar.setProgress((int) (buffer.getCurrentPosition() * seekbar.getMax() / Duration));
                    }

                    sendEmptyMessageDelayed(Play,1000);
                    break;
                case Pause:
                    removeMessages(Play);
                    break;
            }
        }
    };

    public VolumeReceiver getReceiver() {
        return receiver;
    }

    public String getPath() {
        return path;
    }

    public void setLandscapeOrPortrait(Boolean boo)
    {
        //true land 横 portrait 竖
        if(boo)
        {
            ScreenMode=FullScreen;
            ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            clayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        else
        {
            ScreenMode=PartScreen;
            ((Activity) context).getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((Activity) context).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            clayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(context,200)));

        }
        if(ScreenMode==FullScreen)
        {
            fullscreen.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_playerfloat));
        }
        else
        {
            fullscreen.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_smallplayer_back));
        }

    }
    public void setPathAndPlay(String path) {
        this.path = path;
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
        buffer.setVideoURI(Uri.parse(path));
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

    public void Initview(Context context) {
        this.context = context;
        mDetector = new GestureDetectorCompat(context,this);
        this.addView(View.inflate(context, R.layout.videoview_layout, null));
        buffer = (VideoView) findViewById(R.id.buffer);
        bright = (ImageView) findViewById(R.id.bright);
        volume = (ImageView) findViewById(R.id.volume);
        fullscreen=(ImageView)findViewById(R.id.fullscreen);
        fullscreen.setOnClickListener(this);
        pauseorstart=(ImageView) findViewById(R.id.pauseorstart);
        pauseorstart.setOnClickListener(this);
        imagetag = (ImageView) findViewById(R.id.imagetag);
        duration=(TextView)findViewById(R.id.duration);
        bright.setOnClickListener(this);
        volume.setOnClickListener(this);
        imagetag.setOnClickListener(this);
        bottomview = (RelativeLayout) findViewById(R.id.bottomview);
        sidebar = (RelativeLayout) findViewById(R.id.sidebar);
        clayout = (CenterLayout) findViewById(R.id.clayout);
        clayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LogTools.e("onScroll", event.getAction() + " ");
                return mDetector.onTouchEvent(event);
            }
        });
        pb = (ProgressBar) findViewById(R.id.probar);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        seekbar.setEnabled(false);
        volunmseekbar = (SeekBar) findViewById(R.id.volunmseekbar);
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //获取系统最大音量
        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volunmseekbar.setMax(maxVolume);
        //获取当前音量
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        volunmseekbar.setProgress(currentVolume);
        seekbar.setOnSeekBarChangeListener(this);
        volunmseekbar.setOnSeekBarChangeListener(this);
        lock = (CheckBox) findViewById(R.id.lock);
        lock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(onLockScreenListener!=null)onLockScreenListener.onlock(!isChecked);
            }
        });

        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);

        uri = Uri.parse(path);
        buffer.requestFocus();


//        buffer.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
//        buffer.pause();

        receiver = new VolumeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        ((Activity) context).registerReceiver(receiver, filter);

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        //两秒 这接口会被调用一次
//        LogTools.e("sadsad", TimeUtils.FormatTime(buffer.getCurrentPosition()) + " " + Duration);

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
                handler.sendEmptyMessage(Play);
                pMode=PMode_Play;
                pauseorstart.setImageDrawable(getResources().getDrawable(R.drawable.player_pause_normal));
                handler.sendEmptyMessageDelayed(DisapearToolView, ToolviewDisappearTime);
                istoolViewShow = true;
                Duration=buffer.getDuration();
                seekbar.setEnabled(true);
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

    private void disappearToolView() {
        istoolViewShow = false;
        bottomview.setVisibility(GONE);
        seekbar.setVisibility(GONE);
        sidebar.setVisibility(GONE);
        lock.setVisibility(GONE);
    }

    private void showappearToolView() {
        istoolViewShow = true;
        bottomview.setVisibility(VISIBLE);
        seekbar.setVisibility(VISIBLE);
        sidebar.setVisibility(VISIBLE);
        lock.setVisibility(VISIBLE);

    }

    @Override
    public void onClick(View v) {
        handler.removeMessages(DisapearToolView);
        handler.sendEmptyMessageDelayed(DisapearToolView, ToolviewDisappearTime);
        switch (v.getId()) {
            case R.id.clayout:
//                LogTools.e("buffer","buffer "+istoolViewShow);
//                if (!istoolViewShow) {
//                    showappearToolView();
//                } else {
//                    disappearToolView();
//                }
                break;
            case R.id.bright:
                vSeekMode = Mode_Bright;
                imagetag.setImageDrawable(context.getResources().getDrawable(R.drawable.player_brightness));
//                boolean autoBrightness = isAutoBrightness(context.getContentResolver());
//                if (autoBrightness) {
//                    stopAutoBrightness((Activity)context);
//                }
                if(Bright_process<1)
                {
                    Bright_process = getSystemBrightness();
                }

                volunmseekbar.setProgress(Bright_process * volunmseekbar.getMax() / 255);
                break;
            case R.id.volume:
                vSeekMode = Mode_Volume;
                imagetag.setImageDrawable(context.getResources().getDrawable(R.drawable.player_volume));
                int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                volunmseekbar.setProgress(currentVolume);
                break;
            case R.id.pauseorstart:
                if(pMode==PMode_Pause)
                {
                    pMode=PMode_Play;
                    pauseorstart.setImageDrawable(getResources().getDrawable(R.drawable.player_pause_normal));
                    buffer.start();
                    handler.sendEmptyMessage(Play);
                }
                else
                {
                    pMode=PMode_Pause;
                    pauseorstart.setImageDrawable(getResources().getDrawable(R.drawable.player_play_normal));
                    buffer.pause();
                    handler.sendEmptyMessage(Pause);
                }
                break;
            case R.id.fullscreen:
                if(ScreenMode==FullScreen)
                {
                    ScreenMode=PartScreen;
                    fullscreen.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_smallplayer_back));
                    setLandscapeOrPortrait(false);
                }
                else
                {
                    ScreenMode=FullScreen;
                    fullscreen.setImageDrawable(context.getResources().getDrawable(R.drawable.btn_playerfloat));
                    setLandscapeOrPortrait(true);
                }
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        LogTools.e("onStartTrackingTouch","onStartTrackingTouch");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        LogTools.e("onStopTrackingTouch","onStopTrackingTouch");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        LogTools.e("onProgressChanged","onProgressChanged  "+progress+"   "+fromUser);
        handler.removeMessages(DisapearToolView);
        handler.sendEmptyMessageDelayed(DisapearToolView,ToolviewDisappearTime);
        switch (seekBar.getId()) {
            case R.id.seekbar:
                if(fromUser)
                buffer.seekTo(buffer.getDuration()*progress/seekBar.getMax());
                break;
            case R.id.volunmseekbar:
                switch (vSeekMode) {
                    case Mode_Bright:

                        setScreenBrightness(progress * 255 / seekBar.getMax());
                        break;
                    case Mode_Volume:
                        setVolume(progress);
                        break;
                }

                break;
        }

    }


    private class VolumeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")) {
                if (vSeekMode == Mode_Volume) {
                    int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
                    volunmseekbar.setProgress(currentVolume);
                }
            }
        }
    }

//    //设置屏幕亮度的函数
//    private void setScreenBrightness(float num){
//        WindowManager.LayoutParams layoutParams=((Activity)context).getWindow().getAttributes();
//        LogTools.e("bright",layoutParams.screenBrightness+"  "+num);
//        layoutParams.screenBrightness=num;//设置屏幕的亮度
//        ((Activity)context).getWindow().setAttributes(layoutParams);
//    }

    /**
     * 改变App当前Window亮度
     *
     * @param brightness
     */
    public void setScreenBrightness(int brightness) {
        Bright_process=brightness;
        Window window = ((Activity) context).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (brightness <= 0 ? 1 : brightness) / 255f;
        }
        window.setAttributes(lp);
    }

    private void setVolume(int progress)
    {
        am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        int currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        volunmseekbar.setProgress(currentVolume);
    }

    /**
     * 获得系统亮度
     *
     * @return
     */
    private int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(((Activity) context).getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }

//    /**
//     * 停止自动亮度调节
//     *
//     * @param activity
//     */
//    public static void stopAutoBrightness(Activity activity) {
//
//        Settings.System.putInt(activity.getContentResolver(),
//
//                Settings.System.SCREEN_BRIGHTNESS_MODE,
//
//                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//    }
//
//    /**
//     * 判断是否开启了自动亮度调节
//     */
//
//    public static boolean isAutoBrightness(ContentResolver aContentResolver) {
//        boolean automicBrightness = false;
//        try {
//            automicBrightness = Settings.System.getInt(aContentResolver,
//                    Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
//        } catch (Settings.SettingNotFoundException e) {
//            e.printStackTrace();
//        }
//        return automicBrightness;
//    }


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        handler.removeMessages(DisapearToolView);
        handler.sendEmptyMessageDelayed(DisapearToolView,ToolviewDisappearTime);
        if (!istoolViewShow) {
            showappearToolView();
        } else {
            disappearToolView();
        }
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        LogTools.e("onScroll",distanceX+" "+distanceY);
        float beginX = e1.getX();
        float endX = e2.getX();
        float beginY = e1.getY();
        float endY = e2.getY();

        Display disp = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        disp.getSize(size);
        int windowWidth = size.x;
        int windowHeight = size.y;
        if (Math.abs(endX - beginX) < Math.abs(beginY - endY)) {//上下滑动
//            int distance=Integer.valueOf(((beginY - endY) / windowHeight)+"");
//            if (beginX > windowWidth * 3.0 / 4.0) {// 右边滑动 屏幕3/5
//                setScreenBrightness(distance);
//                onVolumeSlide((beginY - endY) / windowHeight);
//            } else if (beginX < windowWidth * 1.0 / 4.0) {// 左边滑动 屏幕2/5
//                onBrightnessSlide((beginY - endY) / windowHeight);
//            }
        }else {//左右滑动
            buffer.seekTo(buffer.getCurrentPosition()+(int)(endX - beginX) / 30*1000);
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }
}
