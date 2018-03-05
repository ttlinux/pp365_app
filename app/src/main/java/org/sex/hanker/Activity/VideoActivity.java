package org.sex.hanker.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.Adapter.VideoMessageAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.ChatBean;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ChangeOrientationHandler;
import org.sex.hanker.Utils.ChatMethod;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.OrientationSensorListener;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.ColorTextview;
import org.sex.hanker.View.Videoview;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/29.
 */
public class VideoActivity extends BaseActivity implements Videoview.OnLockScreenListener{

    private String ProductId,Country;
    Videoview videoview;

    private ChangeOrientationHandler handler;
    private OrientationSensorListener listener;
    private SensorManager sm;
    private Sensor sensor;
    private ColorTextview send;
    private EditText comment;
    private ChatMethod chatMethod;
    private ListView listview;
    VideoMessageAdapter videoadapter;
    private int MessagePage=0;
    private final int pageAmount=20;
    ArrayList<ChatBean> chatbeans = new ArrayList<ChatBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);
        Initview();
    }

    private void Initview()
    {
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
        listview=FindView(R.id.listview);
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        listener = new OrientationSensorListener(handler);
//        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI); 放开了就有重力旋转效果
        ProductId=getIntent().getStringExtra(BundleTag.ProductId);
        Country=getIntent().getStringExtra(BundleTag.Country);
        videoview=FindView(R.id.videoview);
        comment=FindView(R.id.comment);
        comment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    BaseApplication base=(BaseApplication)getApplication();
                    if(base.getUser()==null)
                    {
                        startActivity(new Intent(VideoActivity.this,LoginActivity.class));
                    }
                    else
                    {
                        comment.setCursorVisible(true);
                        InitChat();
                    }
                }
                return false;
            }
        });
        send=FindView(R.id.send);
        send.setListener(new ColorTextview.OnUpListener() {
            @Override
            public void onClick(ColorTextview view) {
                BaseApplication base=(BaseApplication)getApplication();
                if(base.getUser()==null)
                {
                    startActivity(new Intent(VideoActivity.this,LoginActivity.class));
                    return;
                }
                InitChat();
                if(chatMethod.sendMessage(comment.getText().toString()))
                {
                    comment.setText("");
                }
            }
        });
        videoview.setOnLockScreenListener(this);
        InitChat();
        RequestUrl();
        RequestHistory(Country,ProductId);
    }

    private void RequestUrl()
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("id",ProductId);
        requestParams.put("country", Country.length() > 0 ? BundleTag.ASIA:BundleTag.US);
        Httputils.PostWithBaseUrl(Httputils.Video, requestParams, new MyJsonHttpResponseHandler(this, true) {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                LogTools.e("jssss", jsonObject.toString());

                if (jsonObject.optString("status").equalsIgnoreCase("000000")) {
                    JSONObject datas = jsonObject.optJSONObject("datas");
                    JSONObject videos = datas.optJSONObject("videos");
                    //http://cdn.can.cibntv.net/12/201702161000/rexuechangan01/1.m3u8 videos.optString("quality480p","")
                    videoview.setPathAndPlay(videos.optString("quality480p",""));
                } else {
                    ToastUtil.showMessage(VideoActivity.this, jsonObject.optString("info"));
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
        if(videoview.getReceiver()!=null)
        unregisterReceiver(videoview.getReceiver());
        if(chatMethod!=null)
        {
            unregisterReceiver(chatMethod);
            chatMethod.setContext(null);
            chatMethod.CloseConnect();
            chatMethod=null;
        }

    }

    public void onConfigurationChanged(Configuration newConfig) {
// TODO Auto-generated method stubsuper.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
        if(!handler.isAvailable())return;
        if (newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE){
            videoview.setLandscapeOrPortrait(true);
        } else {
            videoview.setLandscapeOrPortrait(false);
        }
    }


    private void InitChat()
    {
        BaseApplication base=(BaseApplication)getApplication();
        if(chatMethod==null && base.getUser()!=null)
        {
            String country=Country.length()>0?BundleTag.ASIA:BundleTag.US;
            chatMethod=new ChatMethod(this,base.getUser().getUsename(),base.getUser().getSessionid(),ProductId,country);
            chatMethod.setChatlistener(new ChatMethod.ChatListener() {
                @Override
                public void OnReceviceMessage(ChatBean cbean) {
                    chatbeans.add(cbean);
                    SetAdapter(chatbeans);
                    listview.setSelection(chatbeans.size()-1);
                }
            });
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(chatMethod, filter);
        }


    }
    @Override
    public void onlock(boolean lock) {
        LogTools.e("lock",lock+"");
        handler.setAvailable(lock);
    }

    public void RequestHistory(String country,String videoid)
    {
        String countrystr=country.length()>0?BundleTag.ASIA:BundleTag.US;
        RequestParams requestParams=new RequestParams();
        requestParams.put("country",countrystr);
        requestParams.put("videoid",videoid);
        requestParams.put("index",MessagePage+"");
        requestParams.put("amount", pageAmount + "");
        requestParams.put("sequence","ASC");//ASC 升序 DESC 降序
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

    public void SetAdapter(ArrayList<ChatBean> chatbeans)
    {
        if(videoadapter==null)
        {
            videoadapter=new VideoMessageAdapter(this,chatbeans);
            listview.setAdapter(videoadapter);
        }
        else
        {
            videoadapter.notifyData(chatbeans);
        }
    }
}
