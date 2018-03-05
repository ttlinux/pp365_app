package org.sex.hanker.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.ImageDownLoader;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/3.
 */
public class HorlistAdapterBottom extends BaseAdapter{

    Context context;
    JSONArray pictureEPBeans;
    ImageDownLoader imageDownLoader2;
    OnSelectItemListener onSelectItemListener;
    String cstr;

    public OnSelectItemListener getOnSelectItemListener() {
        return onSelectItemListener;
    }

    public void setOnSelectItemListener(OnSelectItemListener onSelectItemListener) {
        this.onSelectItemListener = onSelectItemListener;
    }

    public HorlistAdapterBottom(JSONArray pictureEPBeans,String cstr,Context context)
    {
        this.context=context;
        this.pictureEPBeans=pictureEPBeans;
        imageDownLoader2=new ImageDownLoader(context);
        this.cstr=cstr;
    }

    public void NotifyData(JSONArray pictureEPBeans,String cstr)
    {
        this.pictureEPBeans=pictureEPBeans;
        this.cstr=cstr;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pictureEPBeans.length();
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

        String url= Httputils.ImgBaseUrl+cstr+pictureEPBeans.optString(position);
        holder.imageView.setTag(url);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onSelectItemListener!=null)
                    onSelectItemListener.OnSelectitem(position,(String)v.getTag());
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
        public void OnSelectitem(int position,String url);
    }
}
