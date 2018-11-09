package org.sex.hanker.Activity.VideoTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.SparseArray;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.sex.hanker.Adapter.VideoTaskAdapter;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Service.DownloadService;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/8/30.
 */
public class VideoTaskActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_task);
        setBacktitleText(getResources().getString(R.string.downloadManage));
        setBacktitleFinish();
    }



//    private void setMoveline(int index) {
//        if (moveline == null) {
//            moveline = new ImageView(this);
//            moveline.setLayoutParams(new RelativeLayout.LayoutParams(Screenwidth / 4, ScreenUtils.getDIP2PX(this, 5)));
//            moveline.setBackgroundColor(getResources().getColor(R.color.loess4));
//            ScrollerParentView.addView(moveline);
//        }
//
//        AnimationSet animationSet = new AnimationSet(true);
//        TranslateAnimation translateAnimation;
//        translateAnimation = new TranslateAnimation(mCurrentCheckedRadioLeft, Screenwidth / 8 + Screenwidth / 2 * index, 0f, 0f);
//        animationSet.addAnimation(translateAnimation);
//        animationSet.setFillBefore(true);
//        animationSet.setFillAfter(true);
//        animationSet.setDuration(100);
//        moveline.startAnimation(animationSet);//开始上面红色横条图片的动画切换
//        mCurrentCheckedRadioLeft=Screenwidth / 8 + Screenwidth / 2 * index;
//
//        setFragment(index);
//    }
}
