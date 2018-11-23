package org.sex.hanker.View;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.OnMultiClickListener;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.mybusiness.R;

import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * Created by Administrator on 2018/11/23.
 */
public class WheelDialog extends RelativeLayout {

    private WheelView wheelView;
    WindowManager windowManager;
    OnSelectListener onSelectListener;
    TextView confirm;

    public OnSelectListener getOnSelectListener() {
        return onSelectListener;
    }

    public void setOnSelectListener(OnSelectListener onSelectListener) {
        this.onSelectListener = onSelectListener;
    }

    public WheelDialog(Context context) {
        super(context);
        OnInitView(context);
    }

    public WheelDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        OnInitView(context);
    }

    public WheelDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        OnInitView(context);
    }

    public void OnInitView(Context context)
    {
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        View view=View.inflate(context, R.layout.dialog_wheel_selector,null);
        int width= ScreenUtils.getScreenWH((Activity)context)[0];
        RelativeLayout.LayoutParams rl=new LayoutParams(width/2+ScreenUtils.getDIP2PX(context,20), ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        view.setLayoutParams(rl);
        addView(view);

        wheelView=(WheelView)view.findViewById(R.id.wheel);
        wheelView.SetNoBackgound();
        wheelView.SetMoreWheelbackgound();
        wheelView.setCurrentItem(0);
        wheelView.setCyclic(true);
        wheelView.setNoitemline();
        wheelView.setViewAdapter(new NumericWheelAdapter(context, 1, 59));

        confirm=(TextView)view.findViewById(R.id.confirm);
        confirm.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                dismiss();
                if (onSelectListener != null) {
                    onSelectListener.OnSelect(wheelView.getCurrentItem() + 1);
                }
            }
        });

    }

    public void show()
    {
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.FILL_PARENT
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.format = PixelFormat.A_8; // 设置图片格式，效果为背景透明

        windowManager.addView(this, lp);
        setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                dismiss();
            }
        });
        setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dismiss();
                    return true;
                }
                return false;
            }
        });

    }

    public void dismiss()
    {
        windowManager.removeView(this);
        windowManager=null;
    }

    public interface OnSelectListener
    {
        public void OnSelect(int min);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_DOWN)
        {
            dismiss();
        }
        return super.dispatchKeyEvent(event);
    }

}
