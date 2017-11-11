package org.sex.hanker.View;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;

import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/11/6.
 */
public class RoundScroller extends HorizontalScrollView {

    String tag = "RoundScroller";
    private ArrayList<View> arrayList = new ArrayList<View>();
    private ArrayList<Integer> witharr = new ArrayList<Integer>();
    private Scroller mScroller;
    private int mLastX = 0;
    boolean isMove = false;
    LinearLayout videomain;
    Context context;
    int distance =-5000;//滚动距离
    int speed=150;//滚动速度
    int autoScroll=-12345;
    int SpaceTime=10;
    int speedmin=30;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case -12345://自动滚动
                    int add = distance > 0 ? 1 : -1;//左边右边滚动的判断
                    //每次滚动speed的距离

                    if (Math.abs(distance) > speedmin) {
                        if(speed>speedmin)speed--;
                        smoothScrollTo(speed * add + getScrollX(), 0);
                        LogTools.e(tag, Math.abs(distance) + "");
                        sendEmptyMessageDelayed(autoScroll, SpaceTime);
                        distance -= speed * add;
                    } else {
                        smoothScrollTo(distance * add + getScrollX(), 0);
                        distance = 0;
                    }
                    break;
            }
        }
    };

    private int mLastY = 0;
    int x;
    int y;

    public RoundScroller(Context context) {
        super(context);
        Initview(context);
    }

    public RoundScroller(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initview(context);
    }

    public RoundScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initview(context);
    }


    private void Initview(Context context) {
        this.context=context;
        mScroller = new Scroller(getContext());

        videomain = new LinearLayout(context);
        videomain.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        videomain.setLayoutParams(mainv);
        addView(videomain);


    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

//        LogTools.e(tag, (widthMode >> 30) + "  " + widthSize);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(MeasureSpec.makeMeasureSpec(widthMeasureSpec,MeasureSpec.AT_MOST),heightMeasureSpec);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int scrollX = getScrollX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                LogTools.e(tag, "ACTION_MOVE");
                x = (int) event.getX();
                if (isMove == false) {
                    isMove = true;
                    mLastX = x;
                    break;
                }
                if (x == mLastX) break;
                int deltaX;
//                deltaX = (Math.abs(x) - Math.abs(mLastX));
                if (mLastX > x) {
                    //向左
                    deltaX = scrollX + (Math.abs(Math.abs(x) - Math.abs(mLastX)));
                } else {
                    //向右
                    deltaX = scrollX - (Math.abs(Math.abs(mLastX) - Math.abs(x)));
                    if(scrollX==0)
                    {
                        LinearLayout ll=(LinearLayout)getChildAt(0);
                        LogTools.e(tag, x + "  " + scrollX);
                        ll.removeView(arrayList.get(arrayList.size() - 1));
                        ll.addView(arrayList.get(arrayList.size() - 1), 0);

                        if(ll.getChildCount()>arrayList.size())
                        {
                            ll.removeViewAt(ll.getChildCount() - 1);
                        }
                        TextView textView=new TextView(context);
                        LinearLayout.LayoutParams viewl=new LinearLayout.LayoutParams(witharr.get(witharr.size() - 1), ViewGroup.LayoutParams.MATCH_PARENT);
                        textView.setLayoutParams(viewl);
                        textView.setText("zxxzczxc");
                        ll.addView(textView);

                        setScrollX(witharr.get(witharr.size() - 1));

                        LogTools.e(tag, "smoothScrollTo");
                        Changeposition();

                    }
                }
//                LogTools.e(tag, scrollX + " " + deltaX);
//                this.smoothScrollTo(scrollX + deltaX, 0);
                mLastX = x;
                break;
            case MotionEvent.ACTION_UP:
                isMove = false;
                LogTools.e(tag, "ACTION_UP");
//                distance=5000;
                handler.sendEmptyMessageDelayed(autoScroll, SpaceTime);

                break;
        }
        return super.onTouchEvent(event);
    }

//    @Override
//    public void fling(int velocityX) {
//        super.fling(velocityX/400);
//
//    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        LogTools.e(tag,"onScrollChanged"+x+" "+oldx);
        if(getScrollX()==0)
        {
            LinearLayout ll=(LinearLayout)getChildAt(0);
            LogTools.e(tag, x + "  " + getScrollX());
            ll.removeView(arrayList.get(arrayList.size() - 1));
            ll.addView(arrayList.get(arrayList.size() - 1), 0);

            if(ll.getChildCount()>arrayList.size())
            {
                ll.removeViewAt(ll.getChildCount() - 1);
            }
            TextView textView=new TextView(context);
            LinearLayout.LayoutParams viewl=new LinearLayout.LayoutParams(witharr.get(witharr.size() - 1), ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(viewl);
            textView.setText("zxxzczxc");
            ll.addView(textView);

            setScrollX(witharr.get(witharr.size() - 1));

            LogTools.e(tag, "smoothScrollTo");
            Changeposition();

        }
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void addScrollViews(View view) {
        view.measure(0, 0);
        witharr.add(view.getMeasuredWidth());
        arrayList.add(view);
        videomain.addView(arrayList.get(arrayList.size() - 1));
    }

    public void Changeposition()
    {
//        int intfrom=witharr.get(0);
        int intto=witharr.get(witharr.size()-1);
        witharr.remove(witharr.size()-1);
        witharr.add(0,intto);


//        View viewfrom=arrayList.get(from);
        View viewto=arrayList.get(arrayList.size()-1);
        arrayList.remove(arrayList.size() - 1);
        arrayList.add(0, viewto);
    }
    @Override
    public void addView(View child) {
        super.addView(child);
    }

}
