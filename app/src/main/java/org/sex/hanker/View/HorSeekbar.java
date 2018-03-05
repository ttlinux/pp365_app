package org.sex.hanker.View;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;



/**
 * Created by Administrator on 2018/1/31.
 */
public class HorSeekbar extends SeekBar {

    float x=0;
    int ScreenX=0;
    boolean ismove=false;
    public HorSeekbar(Context context) {
        super(context);
        init(context);
    }

    public HorSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HorSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    public void init(Context contex)
    {
        ScreenX= ScreenUtils.getScreenWH((Activity)contex)[0];
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            x=event.getX();
            ismove=false;
            LogTools.e("ACTION_DOWN",event.getX()+"");
        }
        if(event.getAction()==MotionEvent.ACTION_MOVE)
        {
            LogTools.e("ACTION_MOVE",event.getX()+"");
            if((int)x!=(int)event.getX())
                ismove=true;
            else
                ismove=false;
        }
        if(event.getAction()==MotionEvent.ACTION_UP && !ismove)
        {

            int progress=(int) (event.getX() * getMax() / ScreenX);
            setProgress(progress);
            OnSeekBarChangeListener listener=(OnSeekBarChangeListener)ReflectionUtils.getFieldValue(this, "mOnSeekBarChangeListener");
            listener.onProgressChanged(this,progress,true);
            LogTools.e("ACTION_UP", event.getX() + "  " + getMax()+"  "+(int)(event.getX()*getMax()/ScreenX));
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_CANCEL && !ismove)
        {
            LogTools.e("ACTION_CANCEL", event.getX() + "");
            int progress=(int) (event.getX() * getMax() / ScreenX);
            setProgress(progress);
            OnSeekBarChangeListener listener=(OnSeekBarChangeListener)ReflectionUtils.getFieldValue(this, "mOnSeekBarChangeListener");
            listener.onProgressChanged(this, progress, true);
            return true;
        }
        return super.onTouchEvent(event);
    }
}
