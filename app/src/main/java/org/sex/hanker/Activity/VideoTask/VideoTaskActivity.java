package org.sex.hanker.Activity.VideoTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.sex.hanker.Adapter.VideoTaskAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.BaseParent.BaseFragmentActivity;
import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Service.DownloadService;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/8/30.
 */
public class VideoTaskActivity extends BaseFragmentActivity implements View.OnClickListener{

    RadioGroup radiogroup_main;
    int Screenwidth, mCurrentCheckedRadioLeft=0;
    RelativeLayout ScrollerParentView;
    ImageView moveline,freespace;
    ArrayList<BaseFragment> baseFragments=new ArrayList<>();
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    TextView spaceconetnt,delete,selectAll,edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_task);
        TextView backtitle=FindView(R.id.backtitle);
        backtitle.setText(getString(R.string.downloadManage));
        backtitle.setVisibility(View.VISIBLE);
        backtitle.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        InitView();
    }


    public void InitView()
    {
        freespace=FindView(R.id.freespace);
        spaceconetnt=FindView(R.id.spaceconetnt);
        delete=FindView(R.id.delete);
        selectAll=FindView(R.id.selectAll);
        edit=FindView(R.id.edit);
        selectAll.setVisibility(View.INVISIBLE);
        delete.setVisibility(View.INVISIBLE);
        delete.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        edit.setOnClickListener(this);

        long freesize=IOUtil.getSDAvailableSize();
        long totalsize=IOUtil.getRomTotalSize();
        double persent= freesize/totalsize;
        Screenwidth = ScreenUtils.getScreenWH(this)[0];
        int width=(int)((Screenwidth-ScreenUtils.getDIP2PX(this,15)*2)*persent);
        freespace.setLayoutParams(new RelativeLayout.LayoutParams(width, ScreenUtils.getDIP2PX(this,20)));
        spaceconetnt.setText(String.format(getString(R.string.spaceshowdetail),IOUtil.Formate(freesize),IOUtil.Formate(totalsize)));

        ScrollerParentView=FindView(R.id.ScrollerParentView);
        baseFragments.add(new InProcessingFragment());
        baseFragments.add(new DownloadedFragment());
        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout,baseFragments.get(0));
        fragmentTransaction.add(R.id.framelayout, baseFragments.get(1));
        if(baseFragments.isEmpty())return;
        fragmentTransaction.hide(baseFragments.get(0));
        fragmentTransaction.commitAllowingStateLoss();


        radiogroup_main=FindView(R.id.radiogroup_main);
        ScrollerParentView=FindView(R.id.ScrollerParentView);
        radiogroup_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.depositrecord:
                        setMoveline(0);
                        break;
                    case R.id.withdrawrecord:
                        setMoveline(1);
                        break;
                }
            }
        });
        ((RadioButton)radiogroup_main.getChildAt(0)).setChecked(true);
    }
    public  void setFragment(int index) {
        for (int i = 0; i < baseFragments.size(); i++) {
            Fragment fragment = baseFragments.get(i);

            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (index == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commitAllowingStateLoss();
        }
    }
    private void setMoveline(int index) {
        if (moveline == null) {
            moveline = new ImageView(this);
            moveline.setLayoutParams(new RelativeLayout.LayoutParams(Screenwidth / 4, ScreenUtils.getDIP2PX(this, 5)));
            moveline.setBackgroundColor(getResources().getColor(R.color.loess4));
            ScrollerParentView.addView(moveline);
        }

        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation;
        translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, Screenwidth / 8 + Screenwidth / 2 * index, 0f, 0f);
        animationSet.addAnimation(translateAnimation);
        animationSet.setFillBefore(true);
        animationSet.setFillAfter(true);
        animationSet.setDuration(100);
        moveline.startAnimation(animationSet);//开始上面红色横条图片的动画切换
        mCurrentCheckedRadioLeft=Screenwidth / 8 + Screenwidth / 2 * index;

        setFragment(index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.selectAll:

                break;
            case R.id.delete:

                break;
            case R.id.edit:

                break;
        }
    }
}
