package org.sex.hanker.Activity.VideoTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.sex.hanker.Adapter.VideoTaskAdapter;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Service.DownloadService;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;

/**
 * Created by Administrator on 2018/11/9.
 */
public class DownloadedFragment extends BaseFragment {

    BroadcastReceiver broadcastReceiver;
    RecyclerView recycleview;
    SparseArray<BroadcastDataBean> localVideoBeans;
    VideoTaskAdapter videoTaskAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recycleview=new RecyclerView(getActivity());
        recycleview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        recycleview.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycleview.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recycleview.getItemAnimator().setAddDuration(0);
        recycleview.getItemAnimator().setChangeDuration(0);
        recycleview.getItemAnimator().setMoveDuration(0);
        recycleview.getItemAnimator().setRemoveDuration(0);
        ((SimpleItemAnimator) recycleview.getItemAnimator()).setSupportsChangeAnimations(false);
        return recycleview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void OnViewShowOrHide(boolean state) {
        super.OnViewShowOrHide(state);
    }

    @Override
    public void onStart() {
        super.onStart();
        localVideoBeans= VideoSQL.getColumnData(true);
        LogTools.e("localVideoBeans", new Gson().toJson(localVideoBeans));
        setVideoTaskAdapter(-1);
        RegisterBoardcast();
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterBoardcast();
    }

    public void RegisterBoardcast()
    {
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equalsIgnoreCase(BundleTag.VideoProcessAction))
                {
                    BroadcastDataBean bean=null;
                    bean=(BroadcastDataBean)intent.getSerializableExtra(BundleTag.Data);
                    if(bean!=null && bean.getSTATUS()==VideoSQL.Finished)
                    {
                        localVideoBeans.put(bean.getID(), bean);
                        setVideoTaskAdapter( localVideoBeans.indexOfKey(bean.getID()));
                    }
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BundleTag.VideoProcessAction);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void setVideoTaskAdapter(int index)
    {
        if(videoTaskAdapter==null)
        {
            videoTaskAdapter=new VideoTaskAdapter(getActivity(),localVideoBeans);
            recycleview.setAdapter(videoTaskAdapter);
        }
        else
        {
            if(index>-1)
            {
                videoTaskAdapter.NotifyAdapter(localVideoBeans,index);
            }
            else
            {
                videoTaskAdapter.NotifyAdapter(localVideoBeans);
            }
        }
    }

    private void unRegisterBoardcast()
    {
        if(broadcastReceiver!=null)
        {
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
    }
}
