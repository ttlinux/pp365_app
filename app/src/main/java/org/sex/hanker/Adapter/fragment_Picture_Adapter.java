package org.sex.hanker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.ImageDownLoader;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/11/9.
 */
public class fragment_Picture_Adapter extends RecyclerView.Adapter<fragment_Picture_Adapter.ViewHolder>{

    String pics[]={"http://img4.imgtn.bdimg.com/it/u=938233766,3996707670&fm=11&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=2502676275,1983903295&fm=11&gp=0.jpg"
    ,"http://img2.imgtn.bdimg.com/it/u=4280466255,3827944749&fm=11&gp=0.jpg"};

    Context context;
    ImageLoader imageLoader;
    ImageDownLoader imageDownLoader2;
    static int itemwidth=0;
    public fragment_Picture_Adapter(Context context) {
        super();
        this.context=context;
        Activity act=(Activity)context;
        imageLoader=((BaseApplication)act.getApplication()).getImageLoader();
        imageDownLoader2=new ImageDownLoader(act);
        itemwidth= (ScreenUtils.getScreenWH(act)[0]-ScreenUtils.getDIP2PX(act,5)*4)/3;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(context, R.layout.model3_galley_picture,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.itemView.setBackground(context.getResources().getDrawable(R.drawable.radiu_gray_rangle));
        TextView textView=(TextView)holder.itemView.findViewById(R.id.title);
        textView.setVisibility(View.GONE);
        final ImageView imageView=(ImageView)holder.itemView.findViewById(R.id.image);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.getLayoutParams().width=itemwidth;
        imageDownLoader2.showImage(context,pics[position % 3],imageView,ImageDownLoader.SRC);
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
        return 99;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(View itemView) {
            super(itemView);

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
}
