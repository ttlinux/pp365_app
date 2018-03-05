package org.sex.hanker.Fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sex.hanker.Adapter.fragment_Picture_Adapter;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Utils.Country;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.Mydecoration;
import org.sex.hanker.View.PullLoadMoreRecyclerView;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Picture extends BaseFragment{

    PullLoadMoreRecyclerView pullLoadMoreRecyclerView;
    private HorizontalScrollView hsv;
    private TextView title;
    public static Picture picture;
    private int index=0;
    private final int count=20;
    private ArrayList<PictureEPBean> pepbeans=new ArrayList<>();
    private JSONArray jsonarr=new JSONArray();
    private fragment_Picture_Adapter fpa;
    private RadioButton recordbutton;
    private boolean hasmoredata=true;
    public static Picture GetInstance(Bundle arg) {
        if (picture == null)
            picture = new Picture();
        if (arg != null)
            picture.setArguments(arg);
        return picture;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_picture, null);
    }

    private void Init()
    {
        hsv=(HorizontalScrollView)FindView(R.id.titles);
        title=(TextView)FindView(R.id.title);
        pullLoadMoreRecyclerView=(PullLoadMoreRecyclerView)FindView(R.id.pullLoadMoreRecyclerView);
        title.setText("图片");
        //mPullLoadMoreRecyclerView.setRefreshing(true);
        pullLoadMoreRecyclerView.setStaggeredGridLayout(3);
        pullLoadMoreRecyclerView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getPicturelist(String.valueOf(recordbutton.getTag() + ""), true);
            }

            @Override
            public void onLoadMore() {
                if(hasmoredata)
                    getPicturelist(String.valueOf(recordbutton.getTag() + ""), false);
                else
                    pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        });
        RequestMenulist();
    }

    public void RequestMenulist()
    {
        RequestParams requestParams=new RequestParams();
        Httputils.PostWithBaseUrl(Httputils.picturemenu, requestParams, new MyJsonHttpResponseHandler(getActivity(), false) {

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);

                if (!jsonObject.optString("status", "").equalsIgnoreCase("000000")) return;
                JSONObject datas = jsonObject.optJSONObject("datas");
                JSONArray picmenu = datas.optJSONArray("picmenu");
                RadioGroup videomain = new RadioGroup(getActivity());
                videomain.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mainv.setMargins(0,ScreenUtils.getDIP2PX(getActivity(),10),0,0);
                videomain.setLayoutParams(mainv);
                for (int i = 0; i < picmenu.length(); i++) {
                    RadioButton textview = new RadioButton(getActivity());
                    textview.setButtonDrawable(new ColorDrawable(0));
                    textview.setGravity(Gravity.CENTER);
                    textview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    int padding = ScreenUtils.getDIP2PX(getActivity(), 10);
                    int padding2 = ScreenUtils.getDIP2PX(getActivity(), 5);
                    textview.setPadding(padding, padding2, padding, padding2);
                    textview.setText(picmenu.optJSONObject(i).optString("title", ""));
                    textview.setTag(picmenu.optJSONObject(i).optString("id", ""));
                    textview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                recordbutton=(RadioButton)buttonView;
                                buttonView.setTextColor(getResources().getColor(R.color.red2));
                                hasmoredata=true;
                                getPicturelist(String.valueOf(buttonView.getTag() + ""), true);
                            } else {
                                buttonView.setTextColor(getResources().getColor(R.color.black));
                            }
                        }
                    });
                    videomain.addView(textview);
                }
                hsv.removeAllViews();
                hsv.addView(videomain);
                ((RadioButton) videomain.getChildAt(0)).setChecked(true);
            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }
        });
    }

    private void getPicturelist(String id,final boolean isinit)
    {
        if(isinit)
        {
            pepbeans.clear();
            jsonarr=null;
            jsonarr=new JSONArray();
            index=0;
            setAdapter();

        }
        else
        {
            index=index+count;
        }

        RequestParams requestParams=new RequestParams();
        requestParams.put("typeid",id);
        requestParams.put("index",index+"");
        requestParams.put("count",count+"");
        Httputils.PostWithBaseUrl(Httputils.picture,requestParams,new MyJsonHttpResponseHandler(getActivity(),false)
        {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                if(!jsonObject.optString("status","").equalsIgnoreCase("000000"))return;

                JSONObject datas=jsonObject.optJSONObject("datas");
                JSONArray pictures=datas.optJSONArray("pictures");
                if(pictures.length()<1)
                {
                    ToastUtil.showMessage(getActivity(),getString(R.string.nomoredata));
                    index=index-count;
                    hasmoredata=false;
                   return;
                }

                for (int i = 0; i <pictures.length() ; i++) {
                    JSONObject jsonobj=pictures.optJSONObject(i);
                    jsonarr.put(jsonobj);
                    PictureEPBean pebean=PictureEPBean.AnalysisData(jsonobj);
                    pepbeans.add(pebean);
                }
                setAdapter();
            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
                pullLoadMoreRecyclerView.setPullLoadMoreCompleted();
            }
        });
    }

    private void setAdapter()
    {
        if(fpa==null)
        {
            fpa=new fragment_Picture_Adapter(getActivity(),pepbeans);
            fpa.setJsonarr(jsonarr);
            fpa.SetIndex(index);
            pullLoadMoreRecyclerView.setAdapter(fpa);
        }
        else
        {
            fpa.setJsonarr(jsonarr);
            fpa.SetIndex(index);
            fpa.NotifyList(pepbeans);
        }

    }
}
