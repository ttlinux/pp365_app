package org.sex.hanker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Bean.VideoBean;
import org.sex.hanker.Bean.ViewRound;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.ImageDownLoader;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/8.
 */
public class fragment_Video_Adapter extends RecyclerView.Adapter<fragment_Video_Adapter.ViewHolder>{

    Context context;
    ArrayList<VideoBean> videoBeans;
    ImageDownLoader imageDownLoader2;
    static int itemwidth=0;
    SparseArray<ViewRound> sparseArray=new SparseArray<>();

    public fragment_Video_Adapter(Context context,ArrayList<VideoBean> videoBeans)
    {
        this.context=context;
        this.videoBeans=videoBeans;
        Activity act=(Activity)context;
        itemwidth= (ScreenUtils.getScreenWH(act)[0]-ScreenUtils.getDIP2PX(act,5)*4)/3;
        imageDownLoader2=new ImageDownLoader(act);
    }

    public void NotifyList(ArrayList<VideoBean> videoBeans)
    {
        this.videoBeans=videoBeans;
        if(this.videoBeans.size()>0)
            notifyItemInserted(getItemCount());
        else
            notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context, R.layout.model3_galley_picture,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView textView=(TextView)holder.itemView.findViewById(R.id.title);
        textView.setVisibility(View.GONE);
        final ImageView imageView=(ImageView)holder.itemView.findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.getLayoutParams().width=itemwidth;
        if(sparseArray.get(position)!=null)
        {
            ViewRound viewRound=sparseArray.get(position);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(itemwidth,itemwidth*viewRound.getHeight()/viewRound.getWidth()));
        }
        else
        {
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(itemwidth,itemwidth));
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        imageDownLoader2.showImageForSpecialListview(context,videoBeans.get(position).getImageUrl(),imageView, ImageDownLoader.SRC,sparseArray,position,itemwidth);
    }

    @Override
    public int getItemCount() {
        return videoBeans.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
