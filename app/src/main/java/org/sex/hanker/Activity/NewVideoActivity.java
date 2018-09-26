package org.sex.hanker.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.Adapter.VideoMessageAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.ChatBean;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Service.DownloadService;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ChangeOrientationHandler;
import org.sex.hanker.Utils.ChatMethod;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.OrientationSensorListener;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.Utils.VideoDownload.VideoDownloader;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.View.ColorTextview;
import org.sex.hanker.View.NewVideoView;
import org.sex.hanker.mybusiness.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    private ListView listview;
    VideoMessageAdapter videoadapter;
    private int MessagePage = 0;
    private final int pageAmount = 20;
    private VideoBean bean;
    ArrayList<ChatBean> chatbeans = new ArrayList<ChatBean>();
    private static final String Testm3u8 = "http://devimages.apple.com/iphone/samples/bipbop/bipbopall.m3u8";
    private static final String TestMp4 = "http://10.20.20.1/E/%E7%88%B1%E6%83%85%E7%89%87/%E4%BA%B2%E7%88%B1%E7%9A%84,%E6%88%91%E8%A6%81%E5%92%8C%E5%88%AB%E4%BA%BA%E7%BB%93%E5%A9%9A%E4%BA%86/%E4%BA%B2%E7%88%B1%E7%9A%84,%E6%88%91%E8%A6%81%E5%92%8C%E5%88%AB%E4%BA%BA%E7%BB%93%E5%A9%9A%E4%BA%86.mkv";

    //测试m3u8 http://playertest.longtailvideo.com/adaptive/bipbop/gear4/prog_index.m3u8
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

        handler = new ChangeOrientationHandler(this);
        listview = FindView(R.id.listview);
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
                intent.putExtra(BundleTag.Data, bean);
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
                    bean=VideoBean.AnalynsisData(datas.optJSONObject("videos"));
                    bean.setCountryid(Country);
                    videoview.download.setVisibility(View.VISIBLE);
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
                SetAdapter(chatbeans);

            }
        });
    }

    public void SetAdapter(ArrayList<ChatBean> chatbeans) {
        if (videoadapter == null) {
            videoadapter = new VideoMessageAdapter(this, chatbeans);
            listview.setAdapter(videoadapter);
        } else {
            videoadapter.notifyData(chatbeans);
        }
    }

}
