package org.sex.hanker.View;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import org.sex.hanker.mybusiness.R;


/**
 * Created by gd on 2017/2/22.
 */
public class PublicDialog extends Dialog{

    boolean Cancancel=true;
    boolean noStyle=false;
    public  Runnable runable=new Runnable() {
        @Override
        public void run() {
            PublicDialog.this.dismiss();
        }
    };

    public PublicDialog(Context context) {
        super(context);
        InitView(context);
    }
    public PublicDialog(Context context, boolean Cancancel) {
        super(context);
        this.Cancancel=Cancancel;
        InitView(context);
    }

    public PublicDialog(Context context, boolean Cancancel, boolean noStyle) {
        super(context);
        this.Cancancel=Cancancel;
        this.noStyle=noStyle;
        InitView(context);
    }

    public PublicDialog(Context context, int themeResId) {
        super(context, themeResId);
        InitView(context);
    }

    protected PublicDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        InitView(context);
    }

    private void InitView(Context context)
    {
        getContext().setTheme(R.style.loading_dialog);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_loading, null);// 得到加载view
        if(noStyle)
        {
          View progressBar=view.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
        }
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.dialog_loading_view);// 加载布局.
        setCancelable(Cancancel); // 是否可以按“返回键”消失
        setCanceledOnTouchOutside(Cancancel); // 点击加载框以外的区域
        setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局

        Window window = getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setGravity(Gravity.CENTER);
        window.setAttributes(lp);


    }
}
