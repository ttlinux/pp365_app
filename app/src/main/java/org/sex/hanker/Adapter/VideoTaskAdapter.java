package org.sex.hanker.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.LocalVideoBean;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/30.
 */
public class VideoTaskAdapter extends RecyclerView.Adapter<VideoTaskAdapter.ViewHolder> {

    Context context;
    ArrayList<LocalVideoBean> localVideoBeans;
    ImageLoader imageLoader;

    public VideoTaskAdapter(Context context, ArrayList<LocalVideoBean> localVideoBeans) {
        this.context = context;
        this.localVideoBeans = localVideoBeans;
        imageLoader = ((BaseApplication) context.getApplicationContext()).getImageLoader();
        setHasStableIds(true);
    }

    public void NotifyAdapter(ArrayList<LocalVideoBean> localVideoBeans) {
        this.localVideoBeans = localVideoBeans;
        notifyItemRangeChanged(0, getItemCount());
    }

    public void NotifyAdapter(ArrayList<LocalVideoBean> localVideoBeans, int position) {
        this.localVideoBeans = localVideoBeans;
        notifyItemChanged(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(View.inflate(context, R.layout.item_video_task, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        LocalVideoBean bean = localVideoBeans.get(position);
        imageLoader.displayImage(bean.getVIDEO_PHOTO(), holder.image);
        holder.title.setText(bean.getVIDEO_TITLE());
        holder.progresstext.setText(bean.getPersent() + "%");
        holder.progress.setProgress(bean.getPersent());
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
        ImageView image;
        TextView title, progresstext;
        ProgressBar progress;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            progresstext = (TextView) itemView.findViewById(R.id.progresstext);
            progress = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}
