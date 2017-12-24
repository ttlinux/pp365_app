package org.sex.hanker.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/12/21.
 */
public class Car extends LinearLayout{

    TextView jiasu,benqi;
    ImageView car;
    int carwith,carheight;
    boolean status;

    public int getCarwith()
    {
        return carwith;
    }

    public int getCarheight()
    {
        return carheight;
    }

    public Car(Context context) {
        super(context);
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

    public void jiasu(Boolean status)
    {
        if(this.status==status)return;
        this.status=status;
        jiasu.setVisibility(status?VISIBLE:GONE);
        benqi.setVisibility(status?VISIBLE:GONE);
    }

    private void Initview(Context context)
    {
        setOrientation(HORIZONTAL);
        jiasu=new TextView(context);
        jiasu.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        jiasu.setText("加速");
        jiasu.setVisibility(GONE);
        jiasu.setTextColor(Color.RED);
        jiasu.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

        benqi=new TextView(context);
        benqi.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        benqi.setText("喷气");
        benqi.setVisibility(GONE);
        benqi.setTextColor(Color.RED);
        benqi.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);

        car=new ImageView(context);
        car.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        car.setScaleType(ImageView.ScaleType.FIT_CENTER);
        car.setAdjustViewBounds(true);
        Bitmap carbitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.car4);
        carheight=carbitmap.getHeight();
        carwith=carbitmap.getWidth();
        car.setImageBitmap(carbitmap);

        addView(jiasu);
        addView(car);
        addView(benqi);
    }
}
