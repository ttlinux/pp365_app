package org.sex.hanker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.sex.hanker.Activity.PicDetailActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Bean.PictureEPBean;
import org.sex.hanker.Bean.ViewRound;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.ImageDownLoader;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/9.
 */
public class fragment_Picture_Adapter extends RecyclerView.Adapter<fragment_Picture_Adapter.ViewHolder>{


    Context context;
    ImageLoader imageLoader;
    ImageDownLoader imageDownLoader2;
    static int itemwidth=0;
    ArrayList<PictureEPBean> pepbeans;
    JSONArray jsonArray;
    int index;
    SparseArray<ViewRound> sparseArray=new SparseArray<>();

    public fragment_Picture_Adapter(Context context,ArrayList<PictureEPBean> pepbeans) {
        super();
        this.context=context;
        Activity act=(Activity)context;
        imageLoader=((BaseApplication)act.getApplication()).getImageLoader();
        imageDownLoader2=new ImageDownLoader(act);
        itemwidth= (ScreenUtils.getScreenWH(act)[0]-ScreenUtils.getDIP2PX(act,5)*4)/3;
        this.pepbeans=pepbeans;
        setHasStableIds(true);
    }

    public void setJsonarr(JSONArray jsonArray)
    {
        this.jsonArray=jsonArray;
    }
    public void NotifyList(ArrayList<PictureEPBean> pepbeans)
    {
        this.pepbeans=pepbeans;
        if(this.pepbeans.size()>0)
            notifyItemInserted(getItemCount());
        else
           notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context, R.layout.model3_galley_picture,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.radiu_gray_rangle));
        TextView textView=(TextView)holder.itemView.findViewById(R.id.title);
        textView.setVisibility(View.GONE);
        holder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if(sparseArray.get(position)!=null)
        {
//            ViewRound viewRound=sparseArray.get(position);
//            holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(itemwidth,itemwidth*viewRound.getHeight()/viewRound.getWidth()));
        }
        else
        {
            holder.imageView.setLayoutParams(new RelativeLayout.LayoutParams(itemwidth,300));
        }
//        String url= Httputils.ImgBaseUrl+pepbeans.get(position).getContentpath()+pepbeans.get(position).getSmallpic();
        String url= pepbeans.get(position).getSmallpic();
        LogTools.e("urlurl",position+holder.imageView.toString());
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartActivity(position);
            }
        });
        imageDownLoader2.showImageForSpecialListview(context,url,holder.imageView,ImageDownLoader.SRC,sparseArray,position,itemwidth);

//        imageLoader.loadImage(pics[position % 3], new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String s, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String s, View view, FailReason failReason) {
//
//            }
//
//            @Override
//            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
//                LogTools.e("bitmap",bitmap.getWidth()+" "+itemwidth);
//                Bitmap map=big(bitmap,itemwidth);
//                holder.imageView.setAdjustViewBounds(true);
//                holder.imageView.setImageBitmap(map);
//                holder.imageView.setBackgroundColor(Color.RED);
//            }
//
//            @Override
//            public void onLoadingCancelled(String s, View view) {
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return pepbeans.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.image);
        }
    }


    public static Bitmap big(Bitmap b,float x)
    {
        int w=b.getWidth();
        int h=b.getHeight();
        float sx=1;
        if(w>x)
             sx=(float)w/x;//要强制转换，不转换我的在这总是死掉。
        else
            sx=(float)x/w;
//        float sy=(float)y/h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sx); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
                h, matrix, true);
        return resizeBmp;
    }

    public void SetIndex(int index)
    {
        this.index=index;
    }
    private void StartActivity(int position)
    {
        LogTools.e("uuuuu",position+"");
        Intent intent=new Intent();
        intent.putExtra(BundleTag.Postion,position);
        intent.putExtra(BundleTag.Data,jsonArray.toString());
        intent.putExtra(BundleTag.Index,index);
        intent.putExtra(BundleTag.ID,pepbeans.get(position).getId());
        intent.setClass(context, PicDetailActivity.class);
        context.startActivity(intent);
    }

}
