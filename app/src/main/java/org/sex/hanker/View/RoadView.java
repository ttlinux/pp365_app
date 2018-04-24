package org.sex.hanker.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.LinearLayout;

import org.sex.hanker.Utils.BitmapHandler;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2018/4/24.
 */
public class RoadView extends LinearLayout{

    public static final int Left=0;
    public static final int Right=1;
    public static final int Start=0;
    public static final int Runing=1;
    public static final int End=2;
    public int Direction;
    private int BackgroundSpeed=15;//背景图速度
    private int BackgroundRefreshTime=30;
    private long CurrentTime=0;
    private Bitmap roadpic, beijinpic, leftline;
    int ScreenWidth, picwidth, picheight, carwidth, carheight;
    int carCount;//能画几个车
    Context context;
    long left = 0;
    long Runtime;//动画时间


    private Handler handler=new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case Start:
                    CurrentTime=CurrentTime+BackgroundRefreshTime;
                    sendEmptyMessageDelayed(Runing,BackgroundRefreshTime);
                    postInvalidate();
                    break;
                case Runing:
                    sendEmptyMessageDelayed(Runing, BackgroundRefreshTime);
                    postInvalidate();
                    CurrentTime=CurrentTime+BackgroundRefreshTime;
                    if(CurrentTime>=Runtime)
                    {
                        removeMessages(Runing);
                        sendEmptyMessage(End);
                    }
                    break;
                case End:
                    break;
            }
        }
    };

    ArrayList<Car> cars = new ArrayList<>();
    public RoadView(Context context) {
        super(context);
        this.context = context;
    }

    public RoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public RoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Initview();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(picheight, MeasureSpec.AT_MOST));
    }

    public void setDirection(int Direction)
    {
        this.Direction=Direction;
    }
    private void Initview() {
        if (picwidth > 0) return;
        ScreenWidth = ScreenUtils.getScreenWH((Activity) context)[0];
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        beijinpic = BitmapFactory.decodeResource(context.getResources(), R.drawable.shan, bfoOptions);
        Bitmap temp = BitmapFactory.decodeResource(context.getResources(), R.drawable.leftlinepic, bfoOptions);
        Bitmap road = BitmapFactory.decodeResource(context.getResources(), R.drawable.gamepic, bfoOptions);
        leftline = BitmapHandler.zoomImg_Height(temp, 600);
        roadpic = BitmapHandler.zoomImg(road, road.getWidth(), 700);
        picwidth = roadpic.getWidth();
        picheight = roadpic.getHeight() + beijinpic.getHeight();
        int idnx = 0;
        int idnx2 = 0;

        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof Car) {
                Car car = (Car) getChildAt(i);
                if (carwidth == 0 || carheight == 0) {
                    carwidth = car.getCarwith();
                    carheight = car.getCarheight();
                }
                LayoutParams ll = (LayoutParams) car.getLayoutParams();
                ll.leftMargin = ScreenWidth - carwidth - idnx;
                car.setLayoutParams(ll);
                cars.add(car);
                idnx = idnx + 20;
            } else {
                try {
                    throw new ExceptionInInitializerError("只能用Car做子view");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        carCount = getChildCount();

    }


    @Override
    public void onDraw(Canvas canvas) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (Direction==Left)
            left(canvas, paint);
        else
            right(canvas, paint);
        super.onDraw(canvas);
    }

    private void left(Canvas canvas, Paint paint) {

        if (picwidth - left <= ScreenWidth) {
            //end
            LogTools.e("ScrollRoad", "end");
            if (picwidth - left <= 0) {
                //disapper
                LogTools.e("ScrollRoad", "disapper");
                left = BackgroundSpeed;
                canvas.drawBitmap(roadpic, -left, beijinpic.getHeight(), paint);
                canvas.drawBitmap(beijinpic, -left, 0, paint);
            } else {
                canvas.drawBitmap(roadpic, -left, beijinpic.getHeight(), paint);
                canvas.drawBitmap(beijinpic, -left, 0, paint);
                canvas.drawBitmap(roadpic, picwidth - left, beijinpic.getHeight(), paint);
                canvas.drawBitmap(beijinpic, picwidth - left, 0, paint);
            }
        } else {
            canvas.drawBitmap(roadpic, -left, beijinpic.getHeight(), paint);
            canvas.drawBitmap(beijinpic, -left, 0, paint);

        }
        left = left + BackgroundSpeed;
    }

    private void right(Canvas canvas, Paint paint) {

        if (-(picwidth - ScreenWidth) + left >= 0) {
            //end

            if (left >= picwidth) {
                //disapper
                LogTools.e("ScrollRoad", "disapper");
                left = BackgroundSpeed;
                canvas.drawBitmap(roadpic, -(picwidth - ScreenWidth) + left, beijinpic.getHeight(), paint);
                canvas.drawBitmap(beijinpic, -(picwidth - ScreenWidth) + left, 0, paint);
            } else {
                canvas.drawBitmap(roadpic, -(picwidth - ScreenWidth) + left, beijinpic.getHeight(), paint);
                canvas.drawBitmap(beijinpic, -(picwidth - ScreenWidth) + left, 0, paint);
                long juli = -(picwidth - ScreenWidth) + left;
                canvas.drawBitmap(roadpic, -(picwidth - juli), beijinpic.getHeight(), paint);
                canvas.drawBitmap(beijinpic, -(picwidth - juli), 0, paint);
            }
        } else {
            canvas.drawBitmap(roadpic, -(picwidth - ScreenWidth) + left, beijinpic.getHeight(), paint);
            canvas.drawBitmap(beijinpic, -(picwidth - ScreenWidth) + left, 0, paint);
        }
        left = left + BackgroundSpeed;
    }

    public void Start(int Runtime)
    {
        this.Runtime=Runtime;
        handler.sendEmptyMessage(Start);
    }
}
