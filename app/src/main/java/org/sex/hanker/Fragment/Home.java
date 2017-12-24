package org.sex.hanker.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.sex.hanker.Activity.Testcar;
import org.sex.hanker.Adapter.MainbannerPagerAdapter;
import org.sex.hanker.Adapter.ScrollAdapter;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.View.MyViewPager;
import org.sex.hanker.View.RoundScroller;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Home extends BaseFragment {

    private static Home home;
    MyViewPager viewpager;
    ArrayList<ImageView> imageviews = new ArrayList<ImageView>();
    LinearLayout linearLayout;

    public static Home GetInstance(Bundle arg) {
        if (home == null)
            home = new Home();
        if (arg != null)
            home.setArguments(arg);
        return home;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_home, null);
    }

    private void Init() {
        //////banner
        linearLayout=(LinearLayout)FindView(R.id.mainll);
        viewpager = (MyViewPager) FindView(R.id.viewpager);
        viewpager.setOffscreenPageLimit(2);
        viewpager.setOnMeasureListener(new MyViewPager.onMeasureListener() {

            @Override
            public void onmeaure(int height) {

            }
        });
        RelativeLayout testlayout = (RelativeLayout) FindView(R.id.testlayout);
        String strs[] = {"http://img.ybres.net//m//site//a8//banner//homebanner//1501153509548.jpg", "http://img.ybres.net//m//site//a8//banner//homebanner//1501153509548.jpg"};
        viewpager.setAdapter(new MainbannerPagerAdapter(getActivity(), strs));
        AddButton(testlayout, 2);
        //////banner

        //////video
        LinearLayout videomain = new LinearLayout(getActivity());
        videomain.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mainv= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainv.setMargins(0,ScreenUtils.getDIP2PX(getActivity(),10),0,0);
        videomain.setLayoutParams(mainv);

        TextView textView = new TextView(getActivity());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(getActivity(),40));

        textView.setLayoutParams(ll);
        textView.setText("最新视频");
        textView.setPadding(ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10));
        textView.setBackgroundColor(0xffffffff);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_backindicator), null);
        videomain.addView(textView);

        AddVideoItem(videomain);
        linearLayout.addView(videomain);
        //////video

        //////note
        LinearLayout notemain = new LinearLayout(getActivity());
        notemain.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams note_mainv= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        note_mainv.setMargins(0,ScreenUtils.getDIP2PX(getActivity(),10),0,0);
        notemain.setLayoutParams(note_mainv);

        TextView notetitle = new TextView(getActivity());
        notetitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout.LayoutParams notetitle_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(getActivity(),40));

        notetitle.setLayoutParams(notetitle_ll);
        notetitle.setText("最新辣文");
        notetitle.setPadding(ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10));
        notetitle.setBackgroundColor(0xffffffff);
        notetitle.setGravity(Gravity.LEFT | Gravity.CENTER);
        notetitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_backindicator), null);
        notemain.addView(notetitle);

        AddNoteitem(notemain);
        linearLayout.addView(notemain);
        //////note

        //////image
        LinearLayout imagmain = new LinearLayout(getActivity());
        imagmain.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams image_mainv= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        image_mainv.setMargins(0,ScreenUtils.getDIP2PX(getActivity(),10),0,0);
        imagmain.setLayoutParams(image_mainv);

        TextView imagtitle = new TextView(getActivity());
        imagtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout.LayoutParams imagtitle_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(getActivity(),40));

        imagtitle.setLayoutParams(imagtitle_ll);
        imagtitle.setText("最新艳图");
        imagtitle.setPadding(ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10));
        imagtitle.setBackgroundColor(0xffffffff);
        imagtitle.setGravity(Gravity.LEFT | Gravity.CENTER);
        imagtitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_backindicator), null);
        imagmain.addView(imagtitle);

        HorizontalScrollView scorll=new HorizontalScrollView(getActivity());
        scorll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        AddPictureItem(scorll);
        imagmain.addView(scorll);
        linearLayout.addView(imagmain);
        //////image
    }

    private void AddButton(RelativeLayout relayout, int size) {
        imageviews.clear();
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        linearLayout.setPadding(0, 0, 0, ScreenUtils.getDIP2PX(getActivity(), 10));
        linearLayout.setLayoutParams(params);

        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 10;
            imageView.setLayoutParams(layoutParams);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.yindaocheck));
            imageviews.add(imageView);
            linearLayout.addView(imageView);
        }
        relayout.addView(linearLayout);
    }

    private void AddVideoItem(LinearLayout linearLayout)
    {
        for (int i = 0; i < 2; i++) {
            View view=View.inflate(getActivity(),R.layout.model1_horizontal_video,null);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), Testcar.class));
                }
            });
            linearLayout.addView(view);
        }
    }


    private void AddPictureItem(HorizontalScrollView round)
    {
        LinearLayout  videomain = new LinearLayout(getActivity());
        videomain.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainv.topMargin=ScreenUtils.getDIP2PX(getActivity(),10);
        videomain.setLayoutParams(mainv);

        for (int i = 0; i < 30; i++) {
            RelativeLayout llview=(RelativeLayout)View.inflate(getActivity(),R.layout.model3_galley_picture,null);
                TextView title=(TextView)llview.getChildAt(1);
                title.setText("我是 "+(i));
            videomain.addView(llview);
        }
        round.addView(videomain);
    }
    private void AddNoteitem(LinearLayout linearLayout)
    {
        for (int i = 0; i < 2; i++) {
            View view=View.inflate(getActivity(),R.layout.model2_vertical_note,null);
            LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.setMargins(0,1,0,0);
            linearLayout.addView(view,ll);
        }
    }



}
