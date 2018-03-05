package org.sex.hanker.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.sex.hanker.Bean.ChatBean;
import org.sex.hanker.Utils.ImageDownLoader;
import org.sex.hanker.Utils.TimeUtils;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/2/28.
 */
public class VideoMessageAdapter extends BaseAdapter {

    Context context;
    ArrayList<ChatBean> chatBeans;
    ImageDownLoader imageDownLoader;
    Drawable defaultimage;
    public VideoMessageAdapter(Context context,ArrayList<ChatBean> chatBeans)
    {
        this.chatBeans=chatBeans;
        this.context=context;
        imageDownLoader=new ImageDownLoader(context);
         defaultimage=context.getResources().getDrawable(R.drawable.user_info);
    }

    public void notifyData(ArrayList<ChatBean> chatBeans)
    {
        this.chatBeans=chatBeans;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return chatBeans.size();
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
        Holder holder=null;
        if(convertView==null)
        {
            convertView=View.inflate(context, R.layout.item_message_list,null);
            holder=new Holder();
            convertView.setTag(holder);
        }
        else
        {
            holder=(Holder)convertView.getTag();
        }

        holder.content=(TextView)convertView.findViewById(R.id.content);
        holder.datetime=(TextView)convertView.findViewById(R.id.datetime);
        holder.name=(TextView)convertView.findViewById(R.id.name);
        holder.userimage=(ImageView)convertView.findViewById(R.id.userimage);

        ChatBean chatBean=chatBeans.get(position);

        imageDownLoader.showImage_SetdefaultImage(context,chatBean.getUserpicpath(),holder.userimage,ImageDownLoader.SRC,defaultimage);
        holder.content.setText(chatBean.getMessage());
        holder.datetime.setText(TimeUtils.LongtoString(chatBean.getChattime()));
        holder.name.setText(chatBean.getUsername());
        return convertView;
    }

    class Holder
    {
        ImageView userimage;
        TextView name,datetime,content;
    }
}
