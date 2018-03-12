package org.sex.hanker.Fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sex.hanker.Adapter.fragment_Picture_Adapter;
import org.sex.hanker.Adapter.fragment_Video_Adapter;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Utils.Country;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.Lottery_RandomNumber_Dialog;
import org.sex.hanker.View.Lottery_more_Dialog;
import org.sex.hanker.View.PullLoadMoreRecyclerView;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Video extends BaseFragment implements View.OnClickListener{

    public static Video video;
    private PullLoadMoreRecyclerView pullLoadMoreRecyclerView ;
    private HorizontalScrollView hsv;
    private TextView country;
    fragment_Video_Adapter video_adapter;
    String CountryArg;
    String strs[],MenuIndex;
    private boolean hasmoredata=true;
    private int index=0;
    private final int count=20;
    private ArrayList<VideoBean> videoBeans=new ArrayList<>();


    public static Video GetInstance(Bundle arg) {
        if (video == null)
            video = new Video();
        if (arg != null)
            video.setArguments(arg);
        return video;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Initview();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_video, null);
    }

    public void Initview()
    {
        strs=getResources().getStringArray(R.array.country);
        pullLoadMoreRecyclerView=(PullLoadMoreRecyclerView)FindView(R.id.pullLoadMoreRecyclerView);
        pullLoadMoreRecyclerView.setStaggeredGridLayout(3);
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getVideolist(MenuIndex, CountryArg,true);
            }

            @Override
            public void onLoadMore() {
                if(hasmoredata)
                    getVideolist(MenuIndex, CountryArg, false);
                else
                    pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        });
        country=(TextView)FindView(R.id.country);
        country.setOnClickListener(this);
        hsv=(HorizontalScrollView)FindView(R.id.titles);
        CountryArg=Country.US;
        getVideoMenu(0);
    }

    private void Showdialog(View v)
    {

        final Lottery_more_Dialog dialog=new Lottery_more_Dialog(getActivity(),strs);
        dialog.showDropdown(v);
        dialog.setOnDiaClickitemListener(new Lottery_more_Dialog.OnDiaClickitemListener() {
            @Override
            public void Onclickitem(int index) {
                dialog.hide();
                country.setText(strs[index]);
                CountryArg = index < 1 ? Country.US : Country.ASIA;
                getVideoMenu(index);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.country:
                Showdialog(country);
                break;
        }
    }

    private void getVideoMenu(final int index)
    {
            RequestParams requestParams=new RequestParams();
            requestParams.put("country",index<1? Country.US:Country.ASIA);
            Httputils.PostWithBaseUrl(Httputils.Videomenu,requestParams,new MyJsonHttpResponseHandler(getActivity(),true)
            {
                @Override
                public void onFailureOfMe(Throwable throwable, String s) {
                    super.onFailureOfMe(throwable, s);
                }

                @Override
                public void onSuccessOfMe(JSONObject jsonObject) {
                    super.onSuccessOfMe(jsonObject);
                    LogTools.e("jsonObject",jsonObject.toString());
                    if(jsonObject.optString("status","").equalsIgnoreCase("000000"))
                    {
                        JSONObject datas=jsonObject.optJSONObject("datas");
                        JSONArray Videomenu=datas.optJSONArray("Videomenu");
                        RadioGroup videomain = new RadioGroup(getActivity());
                        videomain.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams mainv= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mainv.setMargins(0,ScreenUtils.getDIP2PX(getActivity(),10),0,0);
                        videomain.setLayoutParams(mainv);
                        for (int i = 0; i <Videomenu.length(); i++) {
                            RadioButton textview=new RadioButton(getActivity());
                            textview.setButtonDrawable(new ColorDrawable(0));
                            textview.setGravity(Gravity.CENTER);
                            textview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            int padding= ScreenUtils.getDIP2PX(getActivity(),10);
                            int padding2= ScreenUtils.getDIP2PX(getActivity(),5);
                            textview.setPadding(padding, padding2, padding,padding2);
                            textview.setText(Videomenu.optJSONObject(i).optString("rname", ""));
                            textview.setTag(Videomenu.optJSONObject(i).optString("id", ""));
                            textview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    if (isChecked) {
                                        MenuIndex=String.valueOf(buttonView.getTag() + "");
                                        buttonView.setTextColor(getResources().getColor(R.color.red2));
                                        getVideolist(MenuIndex,index<1? Country.US:Country.ASIA,true);
                                    } else {
                                        buttonView.setTextColor(getResources().getColor(R.color.black));
                                    }
                                }
                            });
                            videomain.addView(textview);
                        }
                        hsv.removeAllViews();
                        hsv.addView(videomain);
                        ((RadioButton)videomain.getChildAt(0)).setChecked(true);
                    }
                }
            });
    }

    private void getVideolist(String id,String country,final boolean isinit)
    {
        if(isinit)
        {
            videoBeans.clear();
            index=0;
            hasmoredata=true;
            setAdapter();

        }
        else
        {
            index=index+count;
        }

        RequestParams requestParams=new RequestParams();
        requestParams.put("typeid",id);
        requestParams.put("country",country);
        requestParams.put("index",index+"");
        requestParams.put("count",count+"");
        Httputils.PostWithBaseUrl(Httputils.Video,requestParams,new MyJsonHttpResponseHandler(getActivity(),true)
        {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                if(!jsonObject.optString("status","").equalsIgnoreCase("000000"))return;
                JSONObject datas=jsonObject.optJSONObject("datas");
                JSONArray videos=datas.optJSONArray("videos");
                if(videos.length()<1)
                {
                    ToastUtil.showMessage(getActivity(), getString(R.string.nomoredata));
                    index=index-count;
                    hasmoredata=false;
                    return;
                }
                for (int i = 0; i <videos.length() ; i++) {
                    VideoBean vbean=VideoBean.AnalynsisData(videos.optJSONObject(i));
                    videoBeans.add(vbean);
                }
                setAdapter();
            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }
        });
    }

    private void setAdapter()
    {
        if(video_adapter==null)
        {
            video_adapter=new fragment_Video_Adapter(getActivity(),videoBeans);
            pullLoadMoreRecyclerView.setAdapter(video_adapter);
        }
        else
        {
            video_adapter.NotifyList(videoBeans);
        }

    }
}
