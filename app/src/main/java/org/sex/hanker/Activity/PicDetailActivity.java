package org.sex.hanker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.Adapter.HorlistAdapterBottom;
import org.sex.hanker.Adapter.HorlistAdapterTop;
import org.sex.hanker.Adapter.PictureViewpagerAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.View.HorizontalListView;
import org.sex.hanker.View.MyViewPager;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/2.
 */
public class PicDetailActivity extends BaseActivity{

    HorizontalListView toplistview,bottomlistview;
    MyViewPager viewpager;
    int Postion;
    int Index,ID;
    JSONArray jsonArray;
    HorlistAdapterTop horlistAdapterTop;
    HorlistAdapterBottom horlistAdapterBottom;
    PictureViewpagerAdapter pictureViewpagerAdapter;
    ArrayList<PictureEPBean> list=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picdetail);
        Initview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void Initview()
    {
        toplistview=FindView(R.id.toplistview);
        bottomlistview=FindView(R.id.bottomlistview);
        viewpager=FindView(R.id.viewpager);
        try {
            jsonArray=new JSONArray(getIntent().getStringExtra(BundleTag.Data));
            for (int i = 0; i <jsonArray.length() ; i++) {
                PictureEPBean pbean=PictureEPBean.AnalysisData(jsonArray.optJSONObject(i));
                list.add(pbean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Index=getIntent().getIntExtra(BundleTag.Index,0);
        Postion=getIntent().getIntExtra(BundleTag.Postion,0);
        ID=getIntent().getIntExtra(BundleTag.ID,0);


        setHorlistviewTopAdapter(list);
        RequestDetail(ID+"");
    }

    private void RequestDetail(String id)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("id",id);
        Httputils.PostWithBaseUrl(Httputils.picturedetail,requestParams,new MyJsonHttpResponseHandler(this,false)
        {
            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);

            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if(!jsonObject.optString("status","").equalsIgnoreCase("000000"))return;
                JSONObject datas=jsonObject.optJSONObject("datas");
                JSONObject picture=datas.optJSONObject("picture");
                String cstr=picture.optString("contentpath","")+picture.optString("picpath","").replaceAll(" ","%20")+"/";
                JSONArray images=picture.optJSONArray("images");
                    setViewPagerAdapter(images,cstr);
                setHorlistviewBotAdapter(images,cstr);
            }
        });
    }

    private void setHorlistviewTopAdapter(ArrayList<PictureEPBean> list)
    {
        if(horlistAdapterTop==null)
        {
            horlistAdapterTop=new HorlistAdapterTop(list,this);
            horlistAdapterTop.setOnSelectItemListener(new HorlistAdapterTop.OnSelectItemListener() {
                @Override
                public void OnSelectitem(int position, PictureEPBean pbean) {

                }
            });
            toplistview.setAdapter(horlistAdapterTop);
        }
        else
        {
            horlistAdapterTop.NotifyData(list);
        }
    }

    private void setViewPagerAdapter(JSONArray jsonArray,String cstr)
    {
        if(pictureViewpagerAdapter==null)
        {
            pictureViewpagerAdapter=new PictureViewpagerAdapter(this,jsonArray,cstr);
            viewpager.setAdapter(pictureViewpagerAdapter);
        }
        else
        {
            viewpager.removeAllViews();
            pictureViewpagerAdapter.NotifyData(jsonArray,cstr);
        }
    }

    private void setHorlistviewBotAdapter(JSONArray jsonArray,String cstr)
    {
        if(horlistAdapterBottom==null)
        {
            horlistAdapterBottom=new HorlistAdapterBottom(jsonArray,cstr,this);
            horlistAdapterBottom.setOnSelectItemListener(new HorlistAdapterBottom.OnSelectItemListener() {
                @Override
                public void OnSelectitem(int position, String url) {

                }
            });
            bottomlistview.setAdapter(horlistAdapterBottom);
        }
        else
        {
            horlistAdapterBottom.NotifyData(jsonArray,cstr);
        }
    }
}
