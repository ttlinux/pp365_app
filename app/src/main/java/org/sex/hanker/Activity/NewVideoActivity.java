package org.sex.hanker.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.ChatBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Service.DownloadService;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ChangeOrientationHandler;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.OrientationSensorListener;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.NewVideoView;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/20.
 */
public class NewVideoActivity extends BaseActivity implements NewVideoView.OnLockScreenListener {

    private String ProductId, Country;
    NewVideoView videoview;

    private ChangeOrientationHandler handler;
    private OrientationSensorListener listener;
    private SensorManager sm;
    private Sensor sensor;
    private int MessagePage = 0;
    private final int pageAmount = 20;
    private VideoBean bean;
    ArrayList<ChatBean> chatbeans = new ArrayList<ChatBean>();
    private static final String Testm3u8 = "http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8";
    //http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8
    //http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8
    private static final String TestMp4 = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4
    //测试m3u8 http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8
    private LinearLayout mainlist;
    private ImageLoader imageLoader;
    private BroadcastReceiver VideoStatusRecevicer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_videoview);
        Initview();
    }


    private void Initview() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (!Settings.System.canWrite(this)) {
//                Toast.makeText(this,"请打开权限:修改系统设置",Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                intent.setData(Uri.parse("package:" + getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//        }
        imageLoader=((BaseApplication)getApplication()).getImageLoader();
        mainlist=FindView(R.id.mainlist);
        handler = new ChangeOrientationHandler(this);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(handler);
//        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI); 放开了就有重力旋转效果
        ProductId = getIntent().getStringExtra(BundleTag.ProductId);
        Country = getIntent().getStringExtra(BundleTag.Country);
        videoview = FindView(R.id.videoview);

        videoview.setOnLockScreenListener(this);
        videoview.setOnButtonClickListener(new NewVideoView.OnButtonClickListener() {
            @Override
            public void OnBack() {
                finish();
            }

            @Override
            public void OnDownload() {
                Intent intent = new Intent(NewVideoActivity.this, DownloadService.class);
                bean.setQuality480p(Testm3u8);
                bean.setVideotype("M3U8");
                intent.putExtra(BundleTag.Data, bean);
                intent.putExtra(BundleTag.ExcuteType,DownloadService.Download);
                NewVideoActivity.this.startService(intent);
            }

            @Override
            public void OnShwolist() {

            }
        });
//        videoview.setPathAndPlay(Testm3u8);
        RequestUrl();
//        RequestHistory(Country, ProductId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RegisterReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unRegisterReceiver();
    }

    private void RequestUrl() {
        if(Country==null || ProductId==null)return;
        RequestParams requestParams = new RequestParams();
        requestParams.put("id", ProductId);
        requestParams.put("country", Country);
        videoview.download.setVisibility(View.INVISIBLE);
        Httputils.PostWithBaseUrl(Httputils.VideoDetail, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                LogTools.e("jssss", jsonObject.toString());

                if (jsonObject.optString("status").equalsIgnoreCase("000000")) {
                    JSONObject datas = jsonObject.optJSONObject("datas");
                    bean = VideoBean.AnalynsisData(datas.optJSONObject("videos"));
                    bean.setCountryid(Country);
                    videoview.download.setVisibility(View.VISIBLE);
                    JSONArray similar = datas.optJSONArray("similar");
                    for (int i = 0; i < similar.length(); i++) {
                        VideoBean bean = VideoBean.AnalynsisData(similar.optJSONObject(i));
                        View view = View.inflate(NewVideoActivity.this, R.layout.item_video_relative, null);
                        ImageView imageView = (ImageView) view.findViewById(R.id.picture);
                        TextView videotitle = (TextView) view.findViewById(R.id.videotitle);
                        imageLoader.displayImage(bean.getImageUrl(), imageView);
                        videotitle.setText(bean.getVideoTitle());
                        mainlist.addView(view);
                    }
                    //http://cdn.can.cibntv.net/12/201702161000/rexuechangan01/1.m3u8 videos.optString("quality480p","")
//                    videoview.setPathAndPlay(Environment.getExternalStorageDirectory() + "/kk.mp4");//videos.optString("quality480p", "")
//                    videoview.setPathAndPlay(TestMp4);
                } else {
                    ToastUtil.showMessage(NewVideoActivity.this, jsonObject.optString("info"));
                }
            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoview!=null)
        {
            videoview.OnDestory();
            if (videoview.getReceiver() != null)
                unregisterReceiver(videoview.getReceiver());
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
// TODO Auto-generated method stubsuper.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
        if (!handler.isAvailable()) return;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            videoview.setLandscapeOrPortrait(true);
        } else {
            videoview.setLandscapeOrPortrait(false);
        }
    }



    @Override
    public void onlock(boolean lock) {
        LogTools.e("lock", lock + "");
        handler.setAvailable(lock);
    }

    public void RequestHistory(String country, String videoid) {
        String countrystr = country.length() > 0 ? BundleTag.ASIA : BundleTag.US;
        RequestParams requestParams = new RequestParams();
        requestParams.put("country", countrystr);
        requestParams.put("videoid", videoid);
        requestParams.put("index", MessagePage + "");
        requestParams.put("amount", pageAmount + "");
        requestParams.put("sequence", "ASC");//ASC 升序 DESC 降序
        Httputils.PostWithBaseUrl(Httputils.MessageRecord, requestParams, new MyJsonHttpResponseHandler(this, false) {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if (!jsonObject.optString("status", "").equalsIgnoreCase("000000")) return;
                JSONObject datas = jsonObject.optJSONObject("datas");

                try {
                    JSONArray Messagelist = datas.getJSONArray("Messagelist");
                    for (int i = 0; i < Messagelist.length(); i++) {
                        JSONObject jsonobj = Messagelist.optJSONObject(i);
                        ChatBean cbean = ChatBean.Analysis(jsonobj);
                        chatbeans.add(cbean);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void RegisterReceiver()
    {
        VideoStatusRecevicer=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String country=intent.getStringExtra(BundleTag.Country);
                String id=intent.getStringExtra(BundleTag.ID);
                int status=intent.getIntExtra(BundleTag.Status,0);
                if(bean.getCountryid().equalsIgnoreCase(country) && bean.getPhid().equalsIgnoreCase(id))
                {
                    switch (status)
                    {
                        case BundleTag.Success_NewFile:
                            ToastUtil.showMessage(NewVideoActivity.this,getString(R.string.Success_NewFile));
                            break;
                        case BundleTag.Success_NotYetFinish:
                            ToastUtil.showMessage(NewVideoActivity.this,getString(R.string.Success_NotYetFinish));
                            break;
                        case BundleTag.Failure_ERROR:
                            ToastUtil.showMessage(NewVideoActivity.this,getString(R.string.Failure_ERROR));
                            break;
                        case BundleTag.Failure_Exits:
                            ToastUtil.showMessage(NewVideoActivity.this,getString(R.string.Failure_Exits));
                            break;
                        case BundleTag.Failure_Full:
                            ToastUtil.showMessage(NewVideoActivity.this,String.format(getString(R.string.Failure_Full),BundleTag.MaxCount+"") );
                            break;
                        case BundleTag.Failure_InLine:
                            ToastUtil.showMessage(NewVideoActivity.this,getString(R.string.Failure_InLine));
                            break;
                    }
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BundleTag.VideoStatusAction);
        registerReceiver(VideoStatusRecevicer, intentFilter);
    }

    private void unRegisterReceiver()
    {
        if(VideoStatusRecevicer!=null)
        {
            unregisterReceiver(VideoStatusRecevicer);
            VideoStatusRecevicer=null;
        }
    }
}
