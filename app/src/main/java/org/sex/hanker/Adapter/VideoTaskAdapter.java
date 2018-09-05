package org.sex.hanker.Adapter;

import android.content.Context;
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
public class VideoTaskAdapter extends BaseAdapter {

    Context context;
    ArrayList<LocalVideoBean> localVideoBeans;
    ImageLoader imageLoader;

    public VideoTaskAdapter(Context context,ArrayList<LocalVideoBean> localVideoBeans)
    {
        this.context=context;
        this.localVideoBeans=localVideoBeans;
        imageLoader=((BaseApplication)context.getApplicationContext()).getImageLoader();
    }

    public void NotifyAdapter(ArrayList<LocalVideoBean> localVideoBeans)
    {
        this.localVideoBeans=localVideoBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return localVideoBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null)
        {
            holder=new Holder();
            convertView=View.inflate(context, R.layout.item_video_task,null);
            convertView.setTag(holder);
        }
        else
        {
            holder=(Holder)convertView.getTag();
        }
        holder.image=(ImageView)convertView.findViewById(R.id.image);
        holder.title=(TextView)convertView.findViewById(R.id.title);
        holder.progresstext=(TextView)convertView.findViewById(R.id.progresstext);
        holder.progress=(ProgressBar)convertView.findViewById(R.id.progress);

        LocalVideoBean bean=localVideoBeans.get(position);
        imageLoader.displayImage(bean.getVIDEO_PHOTO(),holder.image);
        holder.title.setText(bean.getVIDEO_TITLE());
        holder.progresstext.setText(bean.getPersent()+"%");
        holder.progress.setProgress(bean.getPersent());

        return convertView;
    }

    class Holder
    {
        ImageView image;
        TextView title,progresstext;
        ProgressBar progress;
    }
}
