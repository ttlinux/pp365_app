package org.sex.hanker.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import org.sex.hanker.Utils.BitmapHandler;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Administrator on 2017/12/20.
 */
public class ScrollRoadView extends LinearLayout {

    int test = 0;
    private int endtime = 1500;
    private final long speed = 15;//匀速运动的速度
    private int times = 25;//匀速运动次数
    private boolean hasend = false;
    private Bitmap roadpic,beijinpic,leftline;
    int ScreenWidth, picwidth, picheight, carwidth, carheight;
    long left = 0;
    int carCount;//能画几个车
    boolean begin = true;
    boolean loaddirection = true;//路的方向 false是左，true是右
    Context context;
    Integer result[];
    int resulttime = 4000;
    int RunTime = 20000;//动画时间
    int carpianyi = 0;
    long linepianyi = 0;
    OnChangRankListener onchangrank;
int imgtop=5;
int imgbottom=45;
    Random radom = new Random();
    int m_times = 0;
    EndListener endlistener;

    int[] zubiaos;//车的坐标
    SparseArray<Integer> Speeds = new SparseArray<>();//车突然加速的距离
    SparseArray<Integer> dintance = new SparseArray<>();//是否让车后退的距离
    int[] tags;//撞墙倒退标志

    ArrayList<Car> cars = new ArrayList<>();
    long begintime = 0;
    boolean isinresulttime = false;//设置开奖
    boolean starline = true;//设置开始的线
    long linespeed = 0;
    long lineX = 0;
    boolean end = false;
    boolean isrunning=false;



    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0:
                    sendEmptyMessageDelayed(0, 30);
                    if (m_times == 0) {
                        m_times = times;
                        if (result != null && result.length > 0) {
                            if (RunTime - (System.currentTimeMillis() - begintime) <= resulttime + times * 30 && isinresulttime == false) {
                                //设置开奖
                                isinresulttime = true;
                                times = resulttime / 30;
                                m_times = times;//设定最后的速度为5
                            }
                        }

                    } else {
                        m_times--;
                        if (isinresulttime == true && m_times == 0) {
                            end = true;
                            isrunning=false;
                        }
                    }

                    if (hasend && end) {
                        if (Continue()) {
                            removeMessages(0);
                            return;
                        }
                    }

                    if (!end) {
                        if (!isinresulttime)
                            normal();
                        else
                            Onresult();
                    }
                    if (onchangrank != null && zubiaos!=null) {
                        int result[] = new int[carCount];
                        SparseArray<Integer> temp = new SparseArray<>();
                        for (int i = 0; i < carCount; i++) {
                            temp.put(zubiaos[i], i);
                        }
                        Arrays.sort(zubiaos);
                        for (int i = 0; i < result.length; i++) {
                            result[i] = temp.get(zubiaos[i]) + 1;
                            LogTools.e("zuobiao", zubiaos[i] + "  " + temp.get(zubiaos[i]));
                        }
                        onchangrank.Rank(result);
                        if(!hasend && end && endlistener!=null)
                        {
                            endlistener.Over(result);
                        }
                    }
//                    postInvalidate();
                    break;
                case 1:
                    removeMessages(0);
                    break;
                case 3:

                    break;
            }

        }
    };


    private void normal() {
        if (m_times == times) {
            if (begin) {
                for (int i = 0; i < Speeds.size(); i++) {
                    int m_distance = (int) (ScreenWidth / (radom.nextInt(2) + 2));
                    dintance.put(i, m_distance);
                    int speed = radom.nextInt(6);
                    if (speed > 2) {
                        //负数
                        speed = speed - 2 + 1;
                    } else {
                        //整数
                        speed = speed + 1;
                    }
                    Speeds.put(i, speed);
                }
            } else {
                for (int i = 0; i < Speeds.size(); i++) {
                    int m_distance = (int) (ScreenWidth / (radom.nextInt(2) + 2));
                    dintance.put(i, m_distance);
                    int kill = radom.nextInt(8);
                    if (kill > 3) {
                        Speeds.put(i, 0);
                        continue;
                    }
                    int speed = radom.nextInt(6);
                    if (speed > 2) {
                        //负数
                        speed = -(speed - 2) - 1;
                    } else {
                        //整数
                        speed = speed + 1;
                    }
                    Speeds.put(i, speed);
                }
            }
        }

        for (int i = 0; i < cars.size(); i++) {
            LayoutParams ll = (LayoutParams) cars.get(i).getLayoutParams();
            int speed = Speeds.get(i);
            if (ll.leftMargin <= dintance.get(i) && m_times == times) {
                switch (tags[i]) {
                    case 0://第一次超越边界 减缓加速
                        speed = speed / 2;
                        tags[i] = 1;
                        break;
                    case 1://第二次超越边界 停一下
                        speed = 0;
                        tags[i] = 2;
                        break;
                    case 2://然后 倒退
                        speed = -Math.abs(speed);
                        tags[i] = 0;
                        break;
                }
            }

            if (ll.leftMargin >= ScreenWidth / 5 * 4) {
                speed = Math.abs(speed);
            }
            Speeds.put(i, speed);
            ll.leftMargin = ll.leftMargin - speed;
            cars.get(i).jiasu(speed>0,i);
            cars.get(i).setLayoutParams(ll);
            zubiaos[i] = ll.leftMargin;
        }

    }

    private boolean Continue() {
        boolean hascarnotfinish = false;
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            LayoutParams ll = (LayoutParams) car.getLayoutParams();

            ll.leftMargin = ll.leftMargin - 8;
            car.setLayoutParams(ll);
            if (ll.leftMargin > -carwidth*2)
                hascarnotfinish = true;
            zubiaos[i] = ll.leftMargin;
        }
        return !hascarnotfinish;
    }

    private void Onresult() {
        //结束的时候
        if (m_times == times) {
            Random radom = new Random();
            int endline = ScreenWidth /7;//终点线
            int carmagin = radom.nextInt(5);//设置随机车的间隔
            for (int i = 0; i < result.length; i++) {
                Car car = cars.get(result[i] - 1);
                LayoutParams ll = (LayoutParams) car.getLayoutParams();
                int leftmagin = ll.leftMargin;
                int needmagrin = i * (carwidth + carmagin) + endline;
                int speed = Math.abs(leftmagin - needmagrin) / m_times;
                if (i == 0)
                    carpianyi = Math.abs(leftmagin - needmagrin) % m_times;
                Speeds.put(result[i] - 1, leftmagin > needmagrin ? -speed : speed);
            }
        }
        //重新开始的时候
        int pianyiadd = 0;
        int numberone = result[0] - 1;
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            LayoutParams ll = (LayoutParams) car.getLayoutParams();
            double speed = Speeds.get(i);

            if (carpianyi > 0 && i == numberone) {
                pianyiadd = Speeds.get(result[0] - 1) > 0 ? 1 : -1;
                speed = speed + pianyiadd;
                carpianyi--;
            }
            if (i == numberone) {
                LogTools.e("speed", speed + "  " + carpianyi);
            }

            ll.leftMargin = ll.leftMargin + (int) speed;
            car.setLayoutParams(ll);
            zubiaos[i] = ll.leftMargin;
        }
    }

    public ScrollRoadView(Context context) {
        super(context);
        this.context = context;
    }

    public ScrollRoadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public ScrollRoadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setResult(Integer result[], int resulttime) {
        //设置最后开奖结果 resulttime是调整车的位置到开奖结果的时间
        this.result = result;
        this.resulttime = resulttime;
        if (carCount>0 && carCount != result.length) {
            throw new ExceptionInInitializerError("设置结果位数和车的数量不匹配");
        }
    }

    public void setRunTime(int RunTime) {
        this.RunTime = RunTime;
    }

    private void Initview() {
        if (picwidth > 0) return;
        ScreenWidth = ScreenUtils.getScreenWH((Activity) context)[0];
        linespeed = speed;
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        roadpic = BitmapFactory.decodeResource(context.getResources(), R.drawable.gamepic,bfoOptions);
        beijinpic= BitmapFactory.decodeResource(context.getResources(), R.drawable.shan, bfoOptions);
        Bitmap temp=BitmapFactory.decodeResource(context.getResources(), R.drawable.leftlinepic,bfoOptions);
        leftline= BitmapHandler.zoomImg(temp,temp.getWidth(),600);
        picwidth = roadpic.getWidth();
        picheight = roadpic.getHeight()+beijinpic.getHeight();
        linepianyi = ScreenWidth /7 % linespeed;
        int idnx=0;
        int idnx2=0;

        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof Car) {
                Car car = (Car) getChildAt(i);
                if (carwidth == 0 || carheight == 0) {
                    carwidth = car.getCarwith();
                    carheight = car.getCarheight();
                }
                LayoutParams ll = (LayoutParams) car.getLayoutParams();
                ll.leftMargin = ScreenWidth -carwidth/2- idnx;
                car.setLayoutParams(ll);
                cars.add(car);
                idnx=idnx+20;
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

        tags = new int[carCount];
        zubiaos = new int[carCount];
        for (int i = 0; i < carCount; i++) {
            zubiaos[i] = ScreenWidth-carwidth/2-idnx2 ;
            Speeds.put(i, 0);
            tags[i] = 0;
            idnx2=idnx2+20;
        }
        handler.sendEmptyMessageDelayed(1, 1000 * 20);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Initview();
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(picheight, MeasureSpec.AT_MOST));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        Paint paint = new Paint();
        paint.setAntiAlias(true);

        if (!loaddirection)
            left(canvas, paint);
        else
            right(canvas, paint);

    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        //1080
        Paint paint = new Paint();
        //画开始的线
        if(starline ) {
            LogTools.e("lineX", lineX + " " );
            Rect src2 = new Rect();// 图片
            Rect dst2 = new Rect();// 屏幕位置及尺寸
//                canvas.drawBitmap(leftline, lineX, beijinpic.getHeight(), paint);
            src2 = new Rect(0, 0, leftline.getWidth(), leftline.getHeight());
            dst2 = new Rect(ScreenWidth - carwidth-leftline.getWidth(), beijinpic.getHeight() + imgtop, ScreenWidth - carwidth, picheight - imgbottom);
            canvas.drawBitmap(leftline, src2, dst2, paint);
            LogTools.e("argg", ScreenWidth + " " + carwidth + " " + leftline.getWidth());
            starline=false;
        }


        //画结束的线
        if (isinresulttime) {
            starline=false;
            if (m_times <= ScreenWidth / 7 / linespeed + 1) {
                Rect src = new Rect();// 图片
                Rect dst = new Rect();// 屏幕位置及尺寸
//                canvas.drawBitmap(leftline, lineX, beijinpic.getHeight(), paint);
                src = new Rect(0,0,leftline.getWidth(), leftline.getHeight());
                dst = new Rect( carwidth/2, beijinpic.getHeight() + imgtop,carwidth/2 + leftline.getWidth(), picheight - imgbottom);
//                dst = new Rect((int)lineX-leftline.getWidth()/2, beijinpic.getHeight()+imgtop, (int)lineX+leftline.getWidth(),picheight-imgbottom);
                canvas.drawBitmap(leftline, src,dst, paint);
                LogTools.e("dispatchDraw", lineX + " " + linepianyi);
                if (!end) {
                    lineX = linespeed + lineX;
                    if (linepianyi > 0) {
                        lineX = lineX + 1;
                        linepianyi--;
                    }
                }

                src = null;
                dst = null;

            }
            else
            {
                if(hasend && end)
                {
                    Rect src = new Rect();// 图片
                    Rect dst = new Rect();// 屏幕位置及尺寸

                    src = new Rect(0,0,leftline.getWidth(), leftline.getHeight());
                    dst = new Rect((int)lineX, beijinpic.getHeight()+imgtop, (int)lineX+leftline.getWidth(),picheight-imgbottom);
                    canvas.drawBitmap(leftline, src,dst, paint);
                }
            }
        }

    }

    private void left(Canvas canvas, Paint paint) {

        if (picwidth - left <= ScreenWidth) {
            //end
            LogTools.e("ScrollRoad", "end");
            if (picwidth - left <= 0) {
                //disapper
                LogTools.e("ScrollRoad", "disapper");
                left = speed;
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
        left = left + speed;
    }

    private void right(Canvas canvas, Paint paint) {

        if (-(picwidth - ScreenWidth) + left >= 0) {
            //end

            if (left >= picwidth) {
                //disapper
                LogTools.e("ScrollRoad", "disapper");
                left = speed;
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
        left = left + speed;
    }

    public void start() {
        if(isrunning)return;
        end=false;
        lineX=0;
        isinresulttime=false;
        starline=true;

        begintime = System.currentTimeMillis();
        handler.sendEmptyMessageDelayed(0, 30);
        handler.removeMessages(1);
        handler.sendEmptyMessageDelayed(1, RunTime);
        isrunning=true;
    }

    public void sethasend(boolean hasend) {
        if (hasend)
            handler.removeMessages(1);
        this.hasend = hasend;
    }

    public interface OnChangRankListener {
        public void Rank(int rank[]);
    }

    public OnChangRankListener getOnchangrank() {
        return onchangrank;
    }

    public void setOnchangrank(OnChangRankListener onchangrank) {
        this.onchangrank = onchangrank;
    }

    public interface EndListener
    {
        public void Over(int rank[]);
    }

    public EndListener getEndlistener() {
        return endlistener;
    }

    public void setEndlistener(EndListener endlistener) {
        this.endlistener = endlistener;
    }
}
