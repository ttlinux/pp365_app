package org.sex.hanker.BaseParent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import org.sex.hanker.Activity.NewVideoActivity;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/3/28.
 */
public class BaseActivity extends Activity{


    public boolean isneedback() {
        return isneedback;
    }

    public void setIsneedback(boolean isneedback) {
        this.isneedback = isneedback;
    }

    boolean isneedback=true;

    boolean isFront;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);//slide_right_in
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);//slide_left_out
    }

    public boolean isFront()
    {
        return isFront;
    }

    public void setActivityTitle(String title)
    {
        View view=FindView(R.id.title);
        if( view!=null)
        {
            view.setVisibility(View.VISIBLE);
            ((TextView)view).setText(title);
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        isFront=true;
        View view=FindView(R.id.back);
        if(isneedback && view!=null)
        {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isFront=false;
    }

    public void setneedback(boolean needback)
    {
        this.isneedback=needback;
    }

    public void setBacktitleFinish()
    {
        View view=FindView(R.id.backtitle);
        if( view!=null)
        {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    public void setBacktitleText(String str)
    {
        View view=FindView(R.id.backtitle);
        if( view!=null && view instanceof TextView)
        {
            view.setVisibility(View.VISIBLE);
            ((TextView)view).setText(str);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public <T> T FindView(int id)
    {
        return (T)findViewById(id);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(this instanceof NewVideoActivity)
        {
            return super.dispatchTouchEvent(ev);
        }
        else
        {

        }
        return super.dispatchTouchEvent(ev);
    }
}
