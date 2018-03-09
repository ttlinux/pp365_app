package org.sex.hanker.Fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sex.hanker.Adapter.fragment_note_Adapter;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Bean.NoteBean;
import org.sex.hanker.Bean.NoteMenuBean;
import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.PullLoadMoreRecyclerView;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

import io.vov.vitamio.utils.Log;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Note extends BaseFragment{

    public static Note note;
    private HorizontalScrollView hsv;
    private PullLoadMoreRecyclerView listView;
    private TextView title;
    SparseArray<ArrayList<NoteMenuBean>> notemenubeans=new SparseArray<>();
    ArrayList<NoteBean> notebeans=new ArrayList<>();
    private RadioButton recordbutton;
    private boolean hasmoredata=true;
    private int index=0;
    private final int count=20;
    fragment_note_Adapter fna;
    ImageView bookshelf;

    public static Note GetInstance(Bundle arg) {
        if (note == null)
            note = new Note();
        if (arg != null)
            note.setArguments(arg);
        return note;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Initview();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_note, null);
    }

    public void Initview()
    {
        listView=(PullLoadMoreRecyclerView)FindView(R.id.listview);
        bookshelf=(ImageView)FindView(R.id.bookshelf);
        listView.setLinearLayout();
        listView.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                RequestNotelist(String.valueOf(recordbutton.getTag() + ""), true);
            }

            @Override
            public void onLoadMore() {
                if(hasmoredata)
                    RequestNotelist(String.valueOf(recordbutton.getTag() + ""), false);
                else
                    listView.setPullLoadMoreCompleted();
            }
        });
        hsv=(HorizontalScrollView)FindView(R.id.titles);
        title=(TextView)FindView(R.id.title);
        title.setText("小说");
        RequestMenu();
    }

    public void RequestMenu()
    {
        RequestParams requestParams=new RequestParams();
        Httputils.PostWithBaseUrl(Httputils.notemenu,requestParams,new MyJsonHttpResponseHandler(getActivity(),false)
        {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if(!jsonObject.optString("status","").equalsIgnoreCase("000000"))return;
                JSONObject datas=jsonObject.optJSONObject("datas");
                JSONArray notemenu=datas.optJSONArray("notemenu");
                RadioGroup videomain = new RadioGroup(getActivity());
                videomain.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                videomain.setLayoutParams(mainv);
                for (int i = 0; i <notemenu.length() ; i++) {
                    NoteMenuBean notebean= NoteMenuBean.Analysis(notemenu.optJSONObject(i));
                    if(notebean.getParentid()==0)
                    {
                        ArrayList<NoteMenuBean> arrnotebean=new ArrayList<NoteMenuBean>();
                        arrnotebean.add(notebean);
                        notemenubeans.put(notebean.getId(),arrnotebean);

                        RadioButton textview = new RadioButton(getActivity());
                        textview.setButtonDrawable(new ColorDrawable(0));
                        textview.setGravity(Gravity.CENTER);
                        textview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        int padding = ScreenUtils.getDIP2PX(getActivity(), 10);
                        int padding2 = ScreenUtils.getDIP2PX(getActivity(), 5);
                        textview.setPadding(padding, padding2, padding, padding2);
                        textview.setText(notebean.getTitle());
                        textview.setTag(notebean.getId());
                        textview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    recordbutton=(RadioButton)buttonView;
                                    buttonView.setTextColor(getResources().getColor(R.color.red2));
                                    hasmoredata=true;
                                    RequestNotelist(String.valueOf(buttonView.getTag() + ""), true);
                                } else {
                                    buttonView.setTextColor(getResources().getColor(R.color.black));
                                }
                            }
                        });
                        videomain.addView(textview);
                    }
                    else
                    {
                        if(notemenubeans.get(notebean.getParentid())!=null)
                            notemenubeans.get(notebean.getParentid()).add(notebean);
                    }
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

    public void RequestNotelist(String id,final boolean isinit)
    {
        if(isinit)
        {
            notebeans.clear();
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
        Httputils.PostWithBaseUrl(Httputils.note,requestParams,new MyJsonHttpResponseHandler(getActivity(),false){

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
                listView.setPullLoadMoreCompleted();
            }

            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                listView.setPullLoadMoreCompleted();
                if(!jsonObject.optString("status","").equalsIgnoreCase("000000"))return;

                JSONObject datas=jsonObject.optJSONObject("datas");
                JSONArray notes=datas.optJSONArray("notes");
                if(notes.length()<1)
                {
                    ToastUtil.showMessage(getActivity(), getString(R.string.nomoredata));
                    index=index-count;
                    hasmoredata=false;
                    return;
                }
                for (int i = 0; i <notes.length() ; i++) {
                    JSONObject jsonobj=notes.optJSONObject(i);
                    NoteBean pebean=NoteBean.AnalysisData(jsonobj);
                    notebeans.add(pebean);
                }
                setAdapter();
            }
        });
    }

    public void setAdapter()
    {
        if(fna==null)
        {
            fna=new fragment_note_Adapter(getActivity(),notebeans);
            listView.setAdapter(fna);
        }
        else
        {
            fna.NotifyData(notebeans);
        }
    }
}
