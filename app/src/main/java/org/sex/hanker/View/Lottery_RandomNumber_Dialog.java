package org.sex.hanker.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;


/**
 * Created by Administrator on 2017/12/15.
 */
public class Lottery_RandomNumber_Dialog extends RelativeLayout {

    String[] titles;
    BubbleView bubbleView;
    WindowManager windowManager;
    Context context;
    OnDiaClickitemListener onDiaClickitemListener;

    public OnDiaClickitemListener getOnDiaClickitemListener() {
        return onDiaClickitemListener;
    }

    public void setOnDiaClickitemListener(OnDiaClickitemListener onDiaClickitemListener) {
        this.onDiaClickitemListener = onDiaClickitemListener;
    }

    public Lottery_RandomNumber_Dialog(Context context) {
        super(context);
        MakeView(context);
    }

    public Lottery_RandomNumber_Dialog(Context context, String[] titles) {
        super(context);
        this.titles=titles;
        MakeView(context);
    }

    public void setTitles(String[] titles) {
        this.titles = titles;
    }

    public Lottery_RandomNumber_Dialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        MakeView(context);
    }

    public Lottery_RandomNumber_Dialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        MakeView(context);
    }

    private void MakeView(Context context)
    {
        this.context=context;
        bubbleView = new BubbleView(context);
        bubbleView.setOrientation(BubbleView.VERTICAL);

//        bubbleView.SetLayoutPosition(BubbleView.Side_Top, BubbleView.Style_Right);
        bubbleView.SetLayoutPosition(BubbleView.Side_Bot, BubbleView.Style_Left);
        bubbleView.setBotWidth(50);
        bubbleView.setBotHeight(30);

        bubbleView.SetViewStyle(true);
        bubbleView.SetViewColor(0xFF272C2F);

        if(titles!=null)
        {
            for (int i = 0; i <titles.length; i++) {
                TextView textview=new TextView(context);
                textview.setText(titles[i]);
                textview.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(context, 45)));
                textview.setTextColor(0xFFFFFFFF);
                textview.setGravity(Gravity.CENTER);
                textview.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textview.setPadding(ScreenUtils.getDIP2PX(context, 8),ScreenUtils.getDIP2PX(context,13),ScreenUtils.getDIP2PX(context,8),ScreenUtils.getDIP2PX(context,13));
                textview.setSingleLine(true);
                textview.setTag(i);
                textview.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onDiaClickitemListener != null)
                            onDiaClickitemListener.Onclickitem((int) v.getTag());
                    }
                });
                bubbleView.addView(textview);
                if(i<titles.length-1)
                {
                    ImageView imagview=new ImageView(context);
                    imagview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1));
                    imagview.setBackgroundColor(0xFF6D777E);
                    bubbleView.addView(imagview);
                }
            }
        }
    }

    public void showDropUp(View view)
    {
        if(getChildCount()<1)
        {
            LayoutParams params=new LayoutParams(ScreenUtils.getDIP2PX(context,80), ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.leftMargin= ScreenUtils.getDIP2PX(context, 20);
//        params.rightMargin=ScreenUtils.getDIP2PX(context,20);
//        params.topMargin=ScreenUtils.getDIP2PX(context,20);
            Rect rectangle= new Rect();
            ((Activity)context).getWindow().getDecorView().getWindowVisibleDisplayFrame(rectangle);
            int location[]=new int[2];
            view.getLocationInWindow(location);
            params.topMargin=location[1]-ScreenUtils.getDIP2PX(context, 45)*titles.length-30-rectangle.top+4;
            params.leftMargin=location[0]+ScreenUtils.getDIP2PX(context, 5);
            LogTools.e("showDropdown", view.getTop() + "  " + view.getHeight() + "  " + view.getLeft());
            bubbleView.setLayoutParams(params);
            addView(bubbleView);
        }
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hide();
            }
        });
        windowManager=((Activity)context).getWindowManager();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        //WindowManager.LayoutParams.FILL_PARENT,WindowManager.LayoutParams.FILL_PARENT
        lp.width=WindowManager.LayoutParams.MATCH_PARENT;
        lp.height=WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity= Gravity.CENTER;
        lp.format = PixelFormat.A_8; // 设置图片格式，效果为背景透明
        windowManager.addView(this, lp);
    }

    public void hide()
    {
        windowManager.removeView(this);
    }

    public interface OnDiaClickitemListener
    {
        public void Onclickitem(int index);
    }
}
