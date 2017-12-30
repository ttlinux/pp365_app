package org.sex.hanker.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.mybusiness.R;


/**
 * Created by Administrator on 2017/12/16.
 */
public class AppAlertDialog extends RelativeLayout implements View.OnClickListener{

    public String titles,cancelstr,confirmstr;
    String contents[];
    private TextView title,cancel,confirm;
    WindowManager windowManager;
    Context context;
    RadioGroup radiogroup;
    OnChooseListener onChooseListener;
    int sexual=-1;


    public void setOnChooseListener(OnChooseListener onChooseListener) {
        this.onChooseListener = onChooseListener;
    }

    public void setConfirmstr(String confirmstr) {
        this.confirmstr = confirmstr;
        confirm.setText(confirmstr);
    }

    public void setCancelstr(String cancelstr) {
        this.cancelstr = cancelstr;
        cancel.setText(cancelstr);
    }

    public void setTitle(String titles) {
        this.titles = titles;
        title.setText(titles);
    }

    public void setConetnt(String conetnts[]) {
        this.contents = conetnts;
        if(radiogroup!=null )
        {
            if(radiogroup.getChildCount()<1)
            for (int i = 0; i <conetnts.length ; i++) {
               RadioButton rb=new RadioButton(context);
                rb.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                rb.setTag(i);
                rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                        sexual=(int)buttonView.getTag();
                    }
                });
                radiogroup.addView(rb);
            }

            for (int i = 0; i <radiogroup.getChildCount() ; i++) {
               RadioButton rab=(RadioButton)radiogroup.getChildAt(i);
                rab.setText(contents[i]);
            }
        }
    }

    public AppAlertDialog(Context context) {
        super(context);
        InitView(context);
    }

    public AppAlertDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public AppAlertDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    public void InitView(Context context)
    {
        this.context=context;
        View view =View.inflate(context, R.layout.dialog_appalert,null);
        title=(TextView)view.findViewById(R.id.title);
        radiogroup=(RadioGroup)view.findViewById(R.id.radiogroup);
        cancel=(TextView)view.findViewById(R.id.cancel);
        confirm=(TextView)view.findViewById(R.id.confirm);
        cancel.setOnClickListener(this);
        confirm.setOnClickListener(this);

        LayoutParams rl=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        rl.leftMargin= ScreenUtils.getDIP2PX(context,15);
        rl.rightMargin= ScreenUtils.getDIP2PX(context, 15);
        addView(view, rl);

        setBackgroundColor(0xaa000000);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void show()
    {
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

    @Override
    public void onClick(View v) {
        if(onChooseListener==null)return;
        switch (v.getId())
        {
            case R.id.cancel:
                onChooseListener.Onchoose(false,this,sexual);
                break;
            case R.id.confirm:
                if(sexual<0)
                {
                    ToastUtil.showMessage(context,"请选择性别");
                    return;
                }
                onChooseListener.Onchoose(true,this,sexual);
                break;
        }
    }

    public interface OnChooseListener
    {
        public void Onchoose(boolean confirm, AppAlertDialog dialog,int sexual);
    }
}
