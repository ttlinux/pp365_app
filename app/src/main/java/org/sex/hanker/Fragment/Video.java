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
import org.sex.hanker.Adapter.fragment_Video_Adapter;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Utils.Country;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.View.Lottery_RandomNumber_Dialog;
import org.sex.hanker.View.Lottery_more_Dialog;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Video extends BaseFragment implements View.OnClickListener{

    public static Video video;
    private GridView gridView;
    private HorizontalScrollView hsv;
    private TextView country;
    private ArrayList<ArrayList<String>> titls=new ArrayList<>();
    fragment_Video_Adapter video_adapter;

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
        gridView=(GridView)FindView(R.id.gridview);
        country=(TextView)FindView(R.id.country);
        country.setOnClickListener(this);
        hsv=(HorizontalScrollView)FindView(R.id.titles);
        getVideoMenu(0);
    }

    private void Showdialog(View v)
    {
        final String strs[]=getResources().getStringArray(R.array.country);
        final Lottery_more_Dialog dialog=new Lottery_more_Dialog(getActivity(),strs);
        dialog.showDropdown(v);
        dialog.setOnDiaClickitemListener(new Lottery_more_Dialog.OnDiaClickitemListener() {
            @Override
            public void Onclickitem(int index) {
                dialog.hide();
                country.setText(strs[index]);
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
        if(titls.size()<=index)
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
                                        buttonView.setTextColor(getResources().getColor(R.color.red2));
                                        getVideolist(String.valueOf(buttonView.getTag()+""),index<1? Country.US:Country.ASIA);
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
    }

    private void getVideolist(String id,String country)
    {
        RequestParams requestParams=new RequestParams();
        requestParams.put("id",id);
        requestParams.put("country",country);
        Httputils.PostWithBaseUrl(Httputils.login,requestParams,new MyJsonHttpResponseHandler(getActivity(),true)
        {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if(jsonObject.optString("status","").equalsIgnoreCase("000000"))
                {
                    gridView.setAdapter(new fragment_Video_Adapter(getActivity()));
                }

            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }
        });
    }
}
