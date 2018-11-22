package org.sex.hanker.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;

import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.mybusiness.R;
import org.sex.hanker.View.GestureLockView.Mode;
import java.util.ArrayList;
import java.util.List;

/**
 * 整体包含n*n个GestureLockView,每个GestureLockView间间隔mMarginBetweenLockView，
 * 最外层的GestureLockView与容器存在mMarginBetweenLockView的外边距
 * <p/>
 * 关于GestureLockView的边长（n*n）： n * mGestureLockViewWidth + ( n + 1 ) *
 * mMarginBetweenLockView = mWidth ; 得：mGestureLockViewWidth = 4 * mWidth / ( 5
 * * mCount + 1 ) 注：mMarginBetweenLockView = mGestureLockViewWidth * 0.25 ;
 *
 * @author zhy
 */
public class GestureLockViewGroup extends RelativeLayout {

    private static final String TAG = "GestureLockViewGroup";
    /**
     * 保存所有的GestureLockView
     */
    private GestureLockView[] mGestureLockViews;
    /**
     * 每个边上的GestureLockView的个数
     */
    private int mCount = 4;
    /**
     * 存储答案
     */
    private int[] mAnswer = {};

    private boolean isFrist = false;
    private boolean isEditpassword = false;
    /**
     * 保存用户选中的GestureLockView的点的id
     */
    private List<Integer> mChoose = new ArrayList<Integer>();

    /**
     * 保存用户选中的GestureLockView的id 和上面的不同，跨点的时候这个会记录，上面只记录画线的点
     */
    private List<Integer> mr_Choose = new ArrayList<Integer>();

    private Paint mPaint;
    /**
     * 每个GestureLockView中间的间距 设置为：mGestureLockViewWidth * 25%
     */
    private int mMarginBetweenLockView = 30;
    /**
     * GestureLockView的边长 4 * mWidth / ( 5 * mCount + 1 )
     */
    private int mGestureLockViewWidth;

    /**
     * GestureLockView无手指触摸的状态下内圆的颜色
     */
    private int mNoFingerInnerCircleColor = 0xFF939090;
    /**
     * GestureLockView无手指触摸的状态下外圆的颜色
     */
    private int mNoFingerOuterCircleColor = 0xFFE0DBDB;
    /**
     * GestureLockView手指触摸的状态下内圆和外圆的颜色
     */
    private int mFingerOnColor[] = {0xFFffcc6a, 0xffff7701};
    /**
     * GestureLockView手指抬起的状态下内圆和外圆的颜色
     */
    private int mFingerUpColor[] = {0xFFffcc6a, 0xffff7701};

    /**
     * 宽度
     */
    private int mWidth;
    /**
     * 高度
     */
    private int mHeight;

    private Path mPath;
    /**
     * 指引线的开始位置x
     */
    private int mLastPathX;
    /**
     * 指引线的开始位置y
     */
    private int mLastPathY;
    /**
     * 指引下的结束位置
     */
    private Point mTmpTarget = new Point();

    /**
     * 最大尝试次数
     */
    private int mTryTimes = 4;
    /**
     * 回调接口
     */
    private int record = -1;

    int minsize, maxsize;
    onLimitListener onLimitListener;

    private OnGestureLockViewListener mOnGestureLockViewListener;

    public GestureLockViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GestureLockViewGroup(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        /**
         * 获得所有自定义的参数的值
         */
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
                R.styleable.GestureLockViewGroup, defStyle, 0);
        int n = a.getIndexCount();

        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.GestureLockViewGroup_color_no_finger_inner_circle:
                    mNoFingerInnerCircleColor = a.getColor(attr,
                            mNoFingerInnerCircleColor);
                    break;
                case R.styleable.GestureLockViewGroup_color_no_finger_outer_circle:
                    mNoFingerOuterCircleColor = a.getColor(attr,
                            mNoFingerOuterCircleColor);
                    break;
                case R.styleable.GestureLockViewGroup_color_finger_on:
//                    mFingerOnColor = a.getColor(attr, mFingerOnColor);
                    break;
                case R.styleable.GestureLockViewGroup_color_finger_up:
//                    mFingerUpColor = a.getColor(attr, mFingerUpColor);
                    break;
                case R.styleable.GestureLockViewGroup_count:
                    mCount = a.getInt(attr, mCount);
                    break;
                case R.styleable.GestureLockViewGroup_tryTimes:
                    mTryTimes = a.getInt(attr, mTryTimes);
                default:
                    break;
            }
        }

        a.recycle();

        // 初始化画笔
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        // mPaint.setStrokeWidth(20);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        // mPaint.setColor(Color.parseColor("#aaffffff"));
        mPath = new Path();
    }

    public void setViewColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        LogTools.e(TAG + " mWidth", mWidth + "");
        LogTools.e(TAG + " mHeight", mHeight + "");
        if (mWidth == 0 || mHeight == 0) return;
        mHeight = mWidth = mWidth < mHeight ? mWidth : mHeight;

        // setMeasuredDimension(mWidth, mHeight);

        // 初始化mGestureLockViews
        if (mGestureLockViews == null) {
            mGestureLockViews = new GestureLockView[mCount * mCount];
            // 计算每个GestureLockView的宽度
            mGestureLockViewWidth = (int) (mCount * mWidth * 1.0f / (5 * mCount + 1));
            //计算每个GestureLockView的间距
            mMarginBetweenLockView = (int) (mGestureLockViewWidth * 0.33);
            // 设置画笔的宽度为GestureLockView的内圆直径稍微小点（不喜欢的话，随便设）
            mPaint.setStrokeWidth(mGestureLockViewWidth * 0.15f);

            for (int i = 0; i < mGestureLockViews.length; i++) {
                //初始化每个GestureLockView
                mGestureLockViews[i] = new GestureLockView(getContext(),
                        mNoFingerInnerCircleColor, mNoFingerOuterCircleColor,
                        mFingerOnColor, mFingerUpColor);
                mGestureLockViews[i].setId(i + 1);
                //设置参数，主要是定位GestureLockView间的位置
                final LayoutParams lockerParams = new LayoutParams(
                        mGestureLockViewWidth, mGestureLockViewWidth);

                // 不是每行的第一个，则设置位置为前一个的右边
                if (i % mCount != 0) {
                    lockerParams.addRule(RelativeLayout.RIGHT_OF,
                            mGestureLockViews[i - 1].getId());
                }
                // 从第二行开始，设置为上一行同一位置View的下面
                if (i > mCount - 1) {
                    lockerParams.addRule(RelativeLayout.BELOW,
                            mGestureLockViews[i - mCount].getId());
                }
                //设置右下左上的边距
                int rightMargin = mMarginBetweenLockView;
                int bottomMargin = mMarginBetweenLockView;
                int leftMagin = mMarginBetweenLockView;
                int topMargin = mMarginBetweenLockView;
                /**
                 * 每个View都有右外边距和底外边距 第一行的有上外边距 第一列的有左外边距
                 */
                if (i >= 0 && i < mCount)// 第一行
                {
                    topMargin = mMarginBetweenLockView;
                }
                if (i % mCount == 0)// 第一列
                {
                    leftMagin = mMarginBetweenLockView + mMarginBetweenLockView / 2;
                }

                lockerParams.setMargins(leftMagin, topMargin, rightMargin,
                        bottomMargin);
                mGestureLockViews[i].setMode(Mode.STATUS_NO_FINGER);
                final GestureLockView glv = mGestureLockViews[i];
                this.post(new Runnable() {
                    @Override
                    public void run() {
                        addView(glv, lockerParams);
                    }
                });

            }

            Log.e(TAG, "mWidth = " + mWidth + " ,  mGestureViewWidth = "
                    + mGestureLockViewWidth + " , mMarginBetweenLockView = "
                    + mMarginBetweenLockView);
        } else {
//            for (int i = 0; i < mGestureLockViews.length; i++) {
//                mGestureLockViews[i].postInvalidate();
//            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        if (mOnGestureLockViewListener != null) {
            mOnGestureLockViewListener.onTouchEvent(event);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // 重置
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                //画时候的线样式和图形路径
                mPaint.setColor(mFingerOnColor[1]);
//			mPaint.setAlpha(50);
                GestureLockView child = getChildIdByPos(x, y);

                if (child != null) {
                    int cId = child.getId();
                    if (record < 0 || record != cId) {
                        child.setMode(Mode.STATUS_FINGER_ON);
                        LogTools.e("geeeee111", new Gson().toJson(mChoose));
                        if (mChoose.size() == 0)// 当前添加为第一个
                        {
//                                LogTools.e("geeeee","negative");
                            mLastPathX = child.getLeft() / 2 + child.getRight() / 2;
                            mLastPathY = child.getTop() / 2 + child.getBottom() / 2;
                            mPath.moveTo(mLastPathX, mLastPathY);
                        } else
                        // 非第一个，将两者使用线连上
                        {
//                                LogTools.e("geeeee","qqqq");
//                                LogTools.e("geee00",mLastPathX+"  "+mLastPathY);
                            Point point1 = new Point(mLastPathX, mLastPathY);

                            int mLastPathX_temp = child.getLeft() / 2 + child.getRight() / 2;
                            int mLastPathY_temp = child.getTop() / 2 + child.getBottom() / 2;
                            Point point2 = new Point(mLastPathX_temp, mLastPathY_temp);
//                                LogTools.e("geee",FigureDistance(point1,point2)+"  "+(getHeight()/3));
//                                LogTools.e("geee11",new Gson().toJson(point1)+"   "+new Gson().toJson(point2));
//                                LogTools.e("geee222",FigureCentralPoint(point1,point2)+"");
                            if (FigureDistance(point1, point2) > getHeight() / 3) {
                                Point point = FigureCentralPoint(point1, point2);
                                GestureLockView m_child = getChildIdByPos(point.x, point.y);
                                if (m_child != null) {
//                                        mChoose.add(m_child.getId());
                                    mr_Choose.add(m_child.getId());
                                    m_child.setMode(Mode.STATUS_FINGER_ON);
                                    if (mOnGestureLockViewListener != null)
                                        mOnGestureLockViewListener.onBlockSelected(m_child.getId());
                                }
                            }
                            mLastPathX = mLastPathX_temp;
                            mLastPathY = mLastPathY_temp;
                            mPath.lineTo(mLastPathX, mLastPathY);
                        }
                        mChoose.add(cId);
                        mr_Choose.add(cId);
                        record = cId;
                        if (mOnGestureLockViewListener != null)
                            mOnGestureLockViewListener.onBlockSelected(cId);
                        // 设置指引线的起点

                    }

//                    if (!mChoose.contains(cId)) {
//                    }
                }
                // 指引线的终点
                mTmpTarget.x = x;
                mTmpTarget.y = y;
                break;
            case MotionEvent.ACTION_UP:
                //画完后的线样式和图形路径
                if (mr_Choose.size() > maxsize || mr_Choose.size() < minsize) {
                    reset();
                    if (onLimitListener != null) onLimitListener.onLimit();
                    return true;
                }
                if (isFrist) {
//                    mPaint.setColor(mFingerOnColor);
                    mAnswer = new int[mr_Choose.size()];
                    for (int i = 0; i < mr_Choose.size(); i++) {
                        mAnswer[i] = mr_Choose.get(i);
                    }
                    setAnswer(mAnswer);
                    setIsFrist(false);
                } else {
                    // 回调是否成功
                    if (mOnGestureLockViewListener != null && mChoose.size() > 0) {
                        mOnGestureLockViewListener.onGestureEvent(checkAnswer());
                        if (this.mTryTimes == 0) {
                            mOnGestureLockViewListener.onUnmatchedExceedBoundary();
                        }
                    }
//                    mPaint.setColor(mFingerUpColor);
                }
//			mPaint.setAlpha(50);

                this.mTryTimes--;
                mOnGestureLockViewListener.oncheck(mTryTimes);
                Log.e(TAG, "mUnMatchExceedBoundary = " + mTryTimes);
                Log.e(TAG, "mChoose = " + mChoose);
                // 将终点设置位置为起点，即取消指引线
                mTmpTarget.x = mLastPathX;
                mTmpTarget.y = mLastPathY;
                // 改变子元素的状态为UP
                changeItemMode();
//                // 计算每个元素中箭头需要旋转的角度
//                for (int i = 0; i + 1 < mChoose.size(); i++) {
//                    int childId = mChoose.get(i);
//                    int nextChildId = mChoose.get(i + 1);
//
//                    GestureLockView startChild = (GestureLockView) findViewById(childId);
//                    GestureLockView nextChild = (GestureLockView) findViewById(nextChildId);
//
//                    int dx = nextChild.getLeft() - startChild.getLeft();
//                    int dy = nextChild.getTop() - startChild.getTop();
//                    // 计算角度
//                    int angle = (int) Math.toDegrees(Math.atan2(dy, dx)) + 90;
//                    startChild.setArrowDegree(angle);
//                }
                break;
        }

        invalidate();
        return true;
    }

    public double FigureDistance(Point point1, Point point2) {
        double _x = Math.abs(point1.x - point2.x);
        double _y = Math.abs(point1.y - point2.y);
        return Math.sqrt(_x * _x + _y * _y);
    }

    public Point FigureCentralPoint(Point point1, Point point2) {
        Point point = new Point();
        point.set((point1.x + point2.x) / 2, (point1.y + point2.y) / 2);
        return point;
    }

    private void changeItemMode() {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (mChoose.contains(gestureLockView.getId())) {
                gestureLockView.setMode(Mode.STATUS_FINGER_UP);
            }
        }
    }


    /**
     * 少于limitsize 就重置 不算
     */
    public void setLimitListener(int minsize, int maxsize, onLimitListener onLimitListener) {
        this.minsize = minsize;
        this.maxsize = maxsize;
        this.onLimitListener = onLimitListener;
    }

    public interface onLimitListener {
        public void onLimit();
    }

    /**
     * 做一些必要的重置
     */
    public void reset() {
        mChoose.clear();
        mr_Choose.clear();
        mPath.reset();
        record = -1;
        for (GestureLockView gestureLockView : mGestureLockViews) {
            gestureLockView.setMode(Mode.STATUS_NO_FINGER);
//            gestureLockView.setArrowDegree(-1);
        }
    }

    public void resetAndRefresh() {
        mChoose.clear();
        mr_Choose.clear();
        mPath.reset();
        record = -1;
        for (GestureLockView gestureLockView : mGestureLockViews) {
            gestureLockView.setMode(Mode.STATUS_NO_FINGER);
//            gestureLockView.setArrowDegree(-1);
        }
        invalidate();
    }

    /**
     * 检查用户绘制的手势是否正确
     *
     * @return
     */
    private boolean checkAnswer() {
        LogTools.e("mmmmmmmmm", new Gson().toJson(mAnswer));
        LogTools.e("mmmmmmmmm22", new Gson().toJson(mr_Choose));
        if (mAnswer.length != mr_Choose.size()) {
            return false;
        }
        for (int i = 0; i < mAnswer.length; i++) {
            if (mAnswer[i] != mr_Choose.get(i)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 检查当前左边是否在child中
     *
     * @param child
     * @param x
     * @param y
     * @return
     */
    private boolean checkPositionInChild(View child, int x, int y) {

        //设置了内边距，即x,y必须落入下GestureLockView的内部中间的小区域中，可以通过调整padding使得x,y落入范围不变大，或者不设置padding
        int padding = (int) (mGestureLockViewWidth * 0.15);

        if (x >= child.getLeft() + padding && x <= child.getRight() - padding
                && y >= child.getTop() + padding
                && y <= child.getBottom() - padding) {
            return true;
        }
        return false;
    }

    /**
     * 通过x,y获得落入的GestureLockView
     *
     * @param x
     * @param y
     * @return
     */
    private GestureLockView getChildIdByPos(int x, int y) {
        for (GestureLockView gestureLockView : mGestureLockViews) {
            if (checkPositionInChild(gestureLockView, x, y)) {
                return gestureLockView;
            }
        }

        return null;

    }

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public void setOnGestureLockViewListener(OnGestureLockViewListener listener) {
        this.mOnGestureLockViewListener = listener;
    }

    /**
     * 对外公布设置答案的方法
     *
     * @param answer
     */
    public void setAnswer(int[] answer) {
        this.mAnswer = answer;
    }

    //是否是第一次设置手势密码
    public void setIsFrist(boolean isFrist) {
        this.isFrist = isFrist;
    }

    //是否修改手势密码
    public void setIsEditpassword(boolean isEditpassword) {
        this.isEditpassword = isEditpassword;
    }

    /**
     * 设置最大实验次数
     *
     * @param boundary
     */
    public void setUnMatchExceedBoundary(int boundary) {
        this.mTryTimes = boundary;
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //绘制GestureLockView间的连线
        LogTools.e(TAG, "dispatchDraw");
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
        //绘制指引线
        if (mChoose.size() > 0) {
            if (mLastPathX != 0 && mLastPathY != 0)
                canvas.drawLine(mLastPathX, mLastPathY, mTmpTarget.x,
                        mTmpTarget.y, mPaint);
        }

    }

    public interface OnGestureLockViewListener {
        /**
         * 单独选中元素的Id
         *
         * @param position
         */
        public void onBlockSelected(int cId);

        /**
         * 单独选中元素的Id
         *
         * @param position
         */
        public void oncheck(int oncheck);

        /**
         * 是否匹配
         *
         * @param matched
         */
        public void onGestureEvent(boolean matched);

        /**
         * 超过尝试次数
         */
        public void onUnmatchedExceedBoundary();

        public void onTouchEvent(MotionEvent event);
    }
}
