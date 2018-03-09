package org.sex.hanker.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sex.hanker.Activity.NoteDetailActivity;
import org.sex.hanker.Bean.NoteBean;
import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/9.
 */
public class fragment_note_Adapter extends RecyclerView.Adapter<fragment_note_Adapter.ViewHolder>{

    Context context;
    ArrayList<NoteBean> noteBeans;

    public fragment_note_Adapter(Context context,ArrayList<NoteBean> noteBeans)
    {
        this.context=context;
        this.noteBeans=noteBeans;
        setHasStableIds(true);
    }

    public void NotifyData(ArrayList<NoteBean> noteBeans)
    {
        this.noteBeans=noteBeans;
        if(this.noteBeans.size()>0)
            notifyItemInserted(getItemCount());
        else
            notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public fragment_note_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context, R.layout.model2_vertical_note,null);
        return new fragment_note_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(fragment_note_Adapter.ViewHolder holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(context, NoteDetailActivity.class);
                String url= Httputils.ImgBaseUrl+noteBeans.get(position).getContentpath()+noteBeans.get(position).getNotepath();
                intent.putExtra(BundleTag.URL,url);
                intent.putExtra(BundleTag.Title,noteBeans.get(position).getTitle());
                context.startActivity(intent);
            }
        });
        TextView title=(TextView)holder.itemView.findViewById(R.id.title);
        TextView content=(TextView)holder.itemView.findViewById(R.id.content);
        title.setText(noteBeans.get(position).getTitle());
        content.setText(Html.fromHtml(noteBeans.get(position).getContent()));
    }

    @Override
    public int getItemCount() {
        return noteBeans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title,content;
        public ViewHolder(View itemView) {
            super(itemView);
            title=(TextView)itemView.findViewById(R.id.title);
            content=(TextView)itemView.findViewById(R.id.content);
        }
    }

    // View.inflate(context, R.layout.model2_vertical_note,null);
}
