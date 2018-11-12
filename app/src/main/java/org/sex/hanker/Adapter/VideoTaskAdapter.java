package org.sex.hanker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.BroadcastDataBean;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.ProxyURL.IOUtil;
import org.sex.hanker.Service.DownloadService;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.OnMultiClickListener;
import org.sex.hanker.Utils.VideoDownload.VideoSQL;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/30.
 */
public class VideoTaskAdapter extends RecyclerView.Adapter<VideoTaskAdapter.ViewHolder> {

    Context context;
    SparseArray<BroadcastDataBean> localVideoBeans;
    ImageLoader imageLoader;
    OnHandleItemListener onHandleItemListener;
    Handler handler=new Handler(){
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            if(msg.what==111)
            {
                VideoBean bean=(VideoBean)msg.obj;
                onHandleItemListener.OnHanlder(msg.arg1,bean);
            }
        }
    };

    public OnHandleItemListener getOnHandleItemListener() {
        return onHandleItemListener;
    }

    public void setOnHandleItemListener(OnHandleItemListener onHandleItemListener) {
        this.onHandleItemListener = onHandleItemListener;
    }

    public VideoTaskAdapter(Context context, SparseArray<BroadcastDataBean> localVideoBeans) {
        this.context = context;
        this.localVideoBeans = localVideoBeans;
        imageLoader = ((BaseApplication) context.getApplicationContext()).getImageLoader();
        setHasStableIds(true);
    }

    public void NotifyAdapter(SparseArray<BroadcastDataBean> localVideoBeans) {
        this.localVideoBeans = localVideoBeans;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void NotifyAdapter(SparseArray<BroadcastDataBean> localVideoBeans, int position) {
        this.localVideoBeans = localVideoBeans;
        notifyItemChanged(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_task,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BroadcastDataBean bean = localVideoBeans.valueAt(position);

        imageLoader.displayImage(bean.getVIDEO_PHOTO(), holder.image);
        holder.title.setText(bean.getVIDEO_TITLE());
        holder.progresstext.setText(bean.getPersent() + "%");
        holder.progress.setProgress(bean.getPersent());

        holder.handlerbtn.setTag(position);

        switch (bean.getSTATUS())
        {
            case VideoSQL.Pause:
                holder.speed.setText("");
                holder.remaindata.setText(context.getString(R.string.videopause));
                holder.handlerbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.downloadmovie));
                holder.handlerbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.removeMessages(111);
                        int m_pos = (int) v.getTag();
                        BroadcastDataBean m_bean = localVideoBeans.valueAt(m_pos);
                        m_bean.setSTATUS(VideoSQL.NotYetFinish);
                        localVideoBeans.put(m_bean.getID(), m_bean);
                        notifyItemChanged(m_pos);

                        Message message = new Message();
                        message.obj = VideoBean.ConvertBean(localVideoBeans.valueAt(m_pos));
                        message.what = 111;
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                });
                break;
            case VideoSQL.ERROR:
                holder.speed.setText("");
                holder.remaindata.setText(context.getString(R.string.downloadfail));
                holder.handlerbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.downloadmovie));

                holder.handlerbtn.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        handler.removeMessages(111);
                        int m_pos = (int) v.getTag();
                        BroadcastDataBean m_bean = localVideoBeans.valueAt(m_pos);
                        m_bean.setSTATUS(VideoSQL.NotYetFinish);
                        localVideoBeans.put(m_bean.getID(), m_bean);
                        notifyItemChanged(m_pos);
                        Message message = new Message();
                        message.obj = VideoBean.ConvertBean(localVideoBeans.valueAt(m_pos));
                        message.what = 111;
                        message.arg1 = 1;
                        handler.sendMessage(message);
                    }
                });
                break;
            case VideoSQL.NewFile:
            case VideoSQL.NotYetFinish:
                holder.speed.setText(IOUtil.Formate(bean.getSpeed()));
                if(bean.getSUFFIX().toLowerCase().equalsIgnoreCase("m3u8"))
                {
                    holder.remaindata.setText(bean.getDownloadepisode()+"/"+bean.getEpisodeAmount());
                }
                else
                {
                    holder.remaindata.setText(IOUtil.Formate(bean.getCurrentlength())+"/"+IOUtil.Formate(bean.getContentlength()));
                }
                holder.handlerbtn.setImageDrawable(context.getResources().getDrawable(R.drawable.pause));
                holder.handlerbtn.setOnClickListener(new OnMultiClickListener() {
                    @Override
                    public void onMultiClick(View v) {
                        handler.removeMessages(111);
                        int m_pos = (int) v.getTag();
                        BroadcastDataBean m_bean = localVideoBeans.valueAt(m_pos);
                        m_bean.setSTATUS(VideoSQL.Pause);
                        localVideoBeans.put(m_bean.getID(), m_bean);
                        notifyItemChanged(m_pos);
                        Message message = new Message();
                        message.obj = VideoBean.ConvertBean(localVideoBeans.valueAt(m_pos));
                        message.what = 111;
                        message.arg1 = 0;
                        handler.sendMessage(message);
                    }
                });
                break;
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return localVideoBeans.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image,handlerbtn;
        TextView title, progresstext,remaindata,speed;
        ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            handlerbtn=(ImageView)itemView.findViewById(R.id.handlerbtn);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            remaindata = (TextView) itemView.findViewById(R.id.remaindata);
            speed = (TextView) itemView.findViewById(R.id.speed);
            progresstext = (TextView) itemView.findViewById(R.id.progresstext);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

    public interface OnHandleItemListener
    {
        public void OnHanlder(int type,VideoBean bean);
    }
}
