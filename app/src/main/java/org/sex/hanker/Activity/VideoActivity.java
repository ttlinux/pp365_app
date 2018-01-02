package org.sex.hanker.Activity;

import android.os.Bundle;

import com.loopj.android.http.RequestParams;

import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/12/29.
 */
public class VideoActivity extends BaseActivity{

    private String ProductId,Country;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Initview();
        setContentView(R.layout.activity_videoview);
    }

    private void Initview()
    {
        ProductId=getIntent().getStringExtra(BundleTag.ProductId);
        Country=getIntent().getStringExtra(BundleTag.Country);
        RequestUrl();
    }

    private void RequestUrl()
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("id",ProductId);
        requestParams.put("country",Country.length()>0?"asia":"Americas");
        Httputils.PostWithBaseUrl(Httputils.Video,requestParams,new MyJsonHttpResponseHandler(this,true){
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                LogTools.e("jssss",jsonObject.toString());
                if(jsonObject.optString("status").equalsIgnoreCase("000000"))
                {

                }
                else
                {
                    ToastUtil.showMessage(VideoActivity.this,jsonObject.optString("info"));
                }
            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }
        });
    }
}
