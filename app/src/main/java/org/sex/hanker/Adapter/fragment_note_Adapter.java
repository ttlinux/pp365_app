package org.sex.hanker.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.sex.hanker.Bean.NoteBean;
import org.sex.hanker.Bean.PictureEPBean;
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
    public fragment_note_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context, R.layout.model2_vertical_note,null);
        return new fragment_note_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(fragment_note_Adapter.ViewHolder holder, int position) {
        holder.title.setText(noteBeans.get(position).getTitle());
        holder.content.setText(noteBeans.get(position).getContent());
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
