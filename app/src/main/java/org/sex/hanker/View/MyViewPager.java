package org.sex.hanker.View;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import org.sex.hanker.Utils.ScreenUtils;

/**
 * Created by Administrator on 2017/3/28.
 */
public class MyViewPager extends ViewPager {

    Context context;

    public MyViewPager.onMeasureListener getOnMeasureListener() {
        return onMeasureListener;
    }

    public void setOnMeasureListener(MyViewPager.onMeasureListener onMeasureListener) {
        this.onMeasureListener = onMeasureListener;
    }

    onMeasureListener onMeasureListener;

    public MyViewPager(Context context) {
        super(context);
        this.context = context;
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if (h > height )
                height = h;
        }
        int hhh= ScreenUtils.getDIP2PX(context, 150);
        height=height>hhh?hhh:height;//限制最大
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getOnMeasureListener() != null)
            onMeasureListener.onmeaure(height);
    }

    public interface onMeasureListener {
        public void onmeaure(int height);
    }
}
