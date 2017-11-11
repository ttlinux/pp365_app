package org.sex.hanker.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/3/28.
 */
public class MainbannerPagerAdapter extends PagerAdapter {

    String[] strs;
    Context co;
    ArrayList<RelativeLayout> relativeLayouts = new ArrayList<RelativeLayout>();
    public SparseArray<View> mListViews = new SparseArray<View>();
    private ImageLoader mImageDownLoader;

    public MainbannerPagerAdapter(Context co, String[] strs) {
        this.co = co;
        this.strs = strs;
        mImageDownLoader = ((BaseApplication) ((Activity) co).getApplication())
                .getImageLoader();
    }

    @Override
    public int getCount() {
        return strs.length;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView(mListViews.get(position));
        mListViews.remove(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        if (mListViews.get(position) == null) {
            RelativeLayout relativeLayout = new RelativeLayout(co);
            relativeLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ImageView imageView = new ImageView(co);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setTag(position);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int tag = Integer.valueOf(v.getTag() + "");
                }
            });
            mImageDownLoader.displayImage(strs[position], imageView, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    if (bitmap == null) return;
                    int width = ScreenUtils.getScreenWH((Activity) co)[0] + 2;
                    double height = 0.001 * width / bitmap.getWidth() * 1000.00 * bitmap.getHeight();
                    LogTools.e("bitmap22", bitmap.getWidth() + " " + bitmap.getHeight() + " " + height);
                    ImageView image = (ImageView) view;
                    image.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, (int) height));
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
            relativeLayout.addView(imageView);
            container.addView(relativeLayout);
            mListViews.put(position, relativeLayout);
        }


        return mListViews.get(position);
    }

}
