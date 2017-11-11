package org.sex.hanker.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.sex.hanker.Adapter.fragment_Picture_Adapter;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.View.Mydecoration;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Picture extends BaseFragment{

    RecyclerView recycleview;
    private HorizontalScrollView hsv;
    private TextView title;
    public static Picture picture;
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
        recycleview=(RecyclerView)FindView(R.id.recycleview);
        title.setText("图片");
        LinearLayout videomain = new LinearLayout(getActivity());
        videomain.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams mainv= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        mainv.setMargins(0,ScreenUtils.getDIP2PX(getActivity(),10),0,0);
        videomain.setLayoutParams(mainv);
        String titles[]={"夫妻情感","情感","情感","情感","情感","情感","情感","情感","情感","情感"};
        for (int i = 0; i <titles.length ; i++) {
            TextView textview=new TextView(getActivity());
            textview.setGravity(Gravity.CENTER);
            textview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            int padding= ScreenUtils.getDIP2PX(getActivity(), 10);
            int padding2= ScreenUtils.getDIP2PX(getActivity(),5);
            textview.setPadding(padding,padding2,padding,padding2);
            textview.setText(titles[i]);
            videomain.addView(textview);
        }
        hsv.addView(videomain);
        recycleview.setHasFixedSize(true);
        recycleview.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        recycleview.setAdapter(new fragment_Picture_Adapter(getActivity()));

    }
}
