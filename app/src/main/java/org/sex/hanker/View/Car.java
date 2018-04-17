package org.sex.hanker.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sex.hanker.mybusiness.R;


/**
 * Created by Administrator on 2017/12/21.
 */
public class Car extends LinearLayout{
    private Bitmap roadpic;
    RelativeLayout relayout;
    ImageView jiasu,benqi;
    ImageView car;
    int carwith,carheight;
    boolean status;
    int  inedx=0;
    public int getCarwith()
    {
        return carwith;
    }


    public int getCarheight()
    {
        return carheight;
    }

    public Car(Context context,int inedx) {
        super(context);
        this.inedx=inedx;
        Initview(context);
    }

    public Car(Context context, AttributeSet attrs) {
        super(context, attrs);
        Initview(context);
    }

    public Car(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Initview(context);
    }

    public void jiasu(Boolean status,int inedx)
    {
        if(this.status==status)return;
        this.status=status;
        jiasu.setVisibility(status?VISIBLE:GONE);
        benqi.setVisibility(status?VISIBLE:GONE);
    }

    private void Initview(Context context)
    {

        setOrientation(HORIZONTAL);
        roadpic = BitmapFactory.decodeResource(context.getResources(), R.drawable.gamepic);
        relayout=new RelativeLayout(context);
        relayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        jiasu=new ImageView(context);
        jiasu.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//       jiasu.setText("加速");
        jiasu.setBackground(context.getResources().getDrawable(+R.anim.animation_jiasu));
        jiasu.setVisibility(GONE);
        //获得动画对象
        AnimationDrawable    _animaition = (AnimationDrawable)jiasu.getBackground();
        //是否仅仅启动一次？
        _animaition.setOneShot(false);
        if(_animaition.isRunning())//是否正在运行？
        {
            _animaition.stop();//停止
        }
        _animaition.start();//启动


        benqi=new ImageView(context);
        benqi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        benqi.setText("喷气");

        benqi.setImageDrawable(context.getResources().getDrawable(+R.anim.animation_weiqi));
        benqi.setVisibility(GONE);
        //获得动画对象
        AnimationDrawable    _animaition2 = (AnimationDrawable)benqi.getDrawable();
        //是否仅仅启动一次？
        _animaition2.setOneShot(false);
        if(_animaition2.isRunning())//是否正在运行？
        {
            _animaition2.stop();//停止
        }
        _animaition2.start();//启动


        car=new ImageView(context);
        car.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        Bitmap carbitmap  = BitmapFactory.decodeResource(context.getResources(), R.drawable.car1,bfoOptions);;
        if(inedx==0)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car1,bfoOptions);
        if(inedx==1)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car2,bfoOptions);
        if(inedx==2)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car3,bfoOptions);
        if(inedx==3)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car4,bfoOptions);
        if(inedx==4)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car5,bfoOptions);
        if(inedx==5)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car6,bfoOptions);
        if(inedx==6)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car7,bfoOptions);
        if(inedx==7)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car8,bfoOptions);
        if(inedx==8)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car9,bfoOptions);
        if(inedx==9)
          carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car10,bfoOptions);


        carheight=carbitmap.getHeight();
        carwith=carbitmap.getWidth();
        car.setImageBitmap(carbitmap);
//        relayout.addView(car);
////        relayout.addView(jiasu);
//        addView(relayout);
        addView(car);
    }
}
