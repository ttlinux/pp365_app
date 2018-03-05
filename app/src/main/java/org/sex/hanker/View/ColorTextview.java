package org.sex.hanker.View;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2018/2/1.
 */
public class ColorTextview extends TextView{

    Context context;
    ColorStateList textcolor;
    OnUpListener listener;

    public void setListener(OnUpListener listener) {
        this.listener = listener;
    }

    public ColorTextview(Context context) {
        super(context);
        Init(context);
    }

    public ColorTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public ColorTextview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    public void Init(Context context)
    {
        this.context=context;
        textcolor=getTextColors();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                setTextColor(context.getResources().getColor(R.color.red));
                return true;
            case MotionEvent.ACTION_UP:
                setTextColor(textcolor);
                if(listener!=null)
                    listener.onClick(this);
                break;
        }
        return super.onTouchEvent(event);
    }

    public interface OnUpListener
    {
        public void onClick(ColorTextview view);
    }
}
