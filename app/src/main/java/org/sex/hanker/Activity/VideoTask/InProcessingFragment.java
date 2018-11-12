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
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/11/9.
 */
public class InProcessingFragment extends BaseFragment{

    RecyclerView recycleview;
    VideoTaskAdapter videoTaskAdapter;
    SparseArray<BroadcastDataBean> localVideoBeans;
    final int MessageRepeat=0;
    BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver VideoStatusRecevicer;
    HashMap<String,VideoBean> hashMaps=new HashMap<>();
    VideoTaskAdapter.OnHandleItemListener itemlistener;

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
        localVideoBeans= VideoSQL.getColumnData(false);
        LogTools.e("localVideoBeans", new Gson().toJson(localVideoBeans));
        setVideoTaskAdapter(-1);
        RegisterBoardcast();
        RegisterReceiver();
    }

    @Override
    public void onStop() {
        super.onStop();
        unRegisterReceiver();
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
                    if(bean==null)
                        bean=(BroadcastDataBean)intent.getSerializableExtra(BundleTag.CreateTask);

                    if(bean.getSTATUS()!=VideoSQL.Finished)
                    {
                        localVideoBeans.put(bean.getID(), bean);
                        setVideoTaskAdapter( localVideoBeans.indexOfKey(bean.getID()));
                    }
                    else
                    {
                        localVideoBeans.remove(bean.getID());
                        setVideoTaskAdapter(-1);
                    }
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BundleTag.VideoProcessAction);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    private void RegisterReceiver()
    {
        VideoStatusRecevicer=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String country=intent.getStringExtra(BundleTag.Country);
                String id=intent.getStringExtra(BundleTag.ID);
                int status=intent.getIntExtra(BundleTag.Status, 0);

                StringBuilder sb=new StringBuilder();
                sb.append(id).append(country);
                VideoBean vbean=hashMaps.get(sb.toString());
                if(vbean!=null)
                {
                    switch (status)
                    {
                        case BundleTag.Success_NewFile:
                        case BundleTag.Success_NotYetFinish:
                        case BundleTag.Failure_InLine:
                            break;
                        case BundleTag.Failure_ERROR:
                            //不可能出现这个值 放着看的
                            ToastUtil.showMessage(getActivity(), getString(R.string.Failure_ERROR));
                            break;
                        case BundleTag.Failure_Exits:
                            //基本不可能出现
                            ToastUtil.showMessage(getActivity(), getString(R.string.unknowerrorExits));
                            break;
                        case BundleTag.Failure_Full:
                            //有一点点可能出现
                            ToastUtil.showMessage(getActivity(), String.format(getString(R.string.Failure_Full), BundleTag.MaxCount + ""));
                            break;


                    }
                }
            }
        };
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BundleTag.VideoStatusAction);
        getActivity().registerReceiver(VideoStatusRecevicer, intentFilter);
    }

    private void unRegisterReceiver()
    {
        if(VideoStatusRecevicer!=null)
        {
            getActivity().unregisterReceiver(VideoStatusRecevicer);
            VideoStatusRecevicer=null;
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

    private void setVideoTaskAdapter(int index)
    {
        if(videoTaskAdapter==null)
        {
            videoTaskAdapter=new VideoTaskAdapter(getActivity(),localVideoBeans);
            recycleview.setAdapter(videoTaskAdapter);
            itemlistener=new VideoTaskAdapter.OnHandleItemListener() {
                @Override
                public void OnHanlder(int type, VideoBean bean) {
                    LogTools.e("MMMMMM",type+"  ");
                    if(type==0)
                    {
                        //0 暂停
                        Intent intent = new Intent(getActivity(), DownloadService.class);
                        intent.putExtra(BundleTag.ExcuteType,DownloadService.Pause);
                        intent.putExtra(BundleTag.Data, bean);
                        getActivity().startService(intent);
                    }
                    else
                    {
                        //1 下载
                        Intent intent = new Intent(getActivity(), DownloadService.class);
                        intent.putExtra(BundleTag.ExcuteType,DownloadService.Download);
                        intent.putExtra(BundleTag.Data, bean);
                        getActivity().startService(intent);
                        hashMaps.put(bean.getPhid()+bean.getCountryid(),bean);
                    }
                }
            };
            videoTaskAdapter.setOnHandleItemListener(itemlistener);
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
}
