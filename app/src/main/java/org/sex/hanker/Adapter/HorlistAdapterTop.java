package org.sex.hanker.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.ImageDownLoader;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/2.
 */
public class HorlistAdapterTop extends BaseAdapter{

    Context context;
    ArrayList<PictureEPBean> pictureEPBeans;
    ImageDownLoader imageDownLoader2;
    OnSelectItemListener onSelectItemListener;

    public OnSelectItemListener getOnSelectItemListener() {
        return onSelectItemListener;
    }

    public void setOnSelectItemListener(OnSelectItemListener onSelectItemListener) {
        this.onSelectItemListener = onSelectItemListener;
    }

    public HorlistAdapterTop(ArrayList<PictureEPBean> pictureEPBeans,Context context)
    {
        this.context=context;
        this.pictureEPBeans=pictureEPBeans;
        imageDownLoader2=new ImageDownLoader(context);
    }

    public void NotifyData(ArrayList<PictureEPBean> pictureEPBeans)
    {
        this.pictureEPBeans=pictureEPBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pictureEPBeans.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null)
        {
            convertView=View.inflate(context, R.layout.model3_galley_picture,null);
            holder=new Holder();
            convertView.setTag(holder);
        }
        else
        {
            holder=(Holder)convertView.getTag();
        }

        holder.textView=(TextView)convertView.findViewById(R.id.title);
        holder.textView.setVisibility(View.GONE);
        holder.imageView=(ImageView)convertView.findViewById(R.id.image);
        holder.imageView.setTag(pictureEPBeans.get(position));
        String url= Httputils.ImgBaseUrl+pictureEPBeans.get(position).getContentpath()+pictureEPBeans.get(position).getSmallpic();
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectItemListener!=null)
                    onSelectItemListener.OnSelectitem(position,(PictureEPBean)v.getTag());
            }
        });
        imageDownLoader2.showImage(context,url,holder.imageView, ImageDownLoader.SRC);
        return convertView;
    }

    class Holder
    {
        TextView textView;
        ImageView imageView;
    }

    public interface OnSelectItemListener
    {
        public void OnSelectitem(int position,PictureEPBean pbean);
    }
}
