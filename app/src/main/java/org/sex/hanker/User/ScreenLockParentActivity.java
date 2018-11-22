package org.sex.hanker.User;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.mybusiness.BuildConfig;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2018/11/21.
 */
public class ScreenLockParentActivity extends BaseActivity implements View.OnClickListener{

    public enum Status{
        Start(0),On(1),Off(2),Edit(3);
        public  int index;

        // 构造方法
        private Status(int index) {
            this.index = index;
        }
        public int getIndex() {
            return this.index;
        }
    }
    SharedPreferences sharedPreferences;
    String password;
    LinearLayout mainll;
    TextView off,edit,start;
    int status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenlock_parent);
        InitView();
    }

    private void InitView()
    {
        mainll=FindView(R.id.mainll);
        off=FindView(R.id.off);
        edit=FindView(R.id.edit);
        start=FindView(R.id.start);
        off.setOnClickListener(this);
        edit.setOnClickListener(this);
        start.setOnClickListener(this);

        sharedPreferences=((BaseApplication)getApplication()).getSharedPreferences();
        password=sharedPreferences.getString(BundleTag.ScreenLockPassword, "");
        status=sharedPreferences.getInt(BundleTag.ScreenLockStatus,Status.Start.getIndex());

        switch (status)
        {
            case 1:
                for (int i = 0; i <2 ; i++) {
                    mainll.getChildAt(i).setVisibility(View.GONE);
                }
                break;
            case 0:
            case 2:
                for (int i = 2; i <mainll.getChildCount()-1 ; i++) {
                    mainll.getChildAt(i).setVisibility(View.GONE);
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId())
        {
            case R.id.off:
                ((BaseApplication)getApplication()).setScreenLockOpenStatus(Status.Off.getIndex());
                sharedPreferences.edit().putInt(BundleTag.ScreenLockStatus,Status.Off.getIndex()).commit();
                ToastUtil.showMessage(this,getResources().getString(R.string.screenlocktips2));
                break;
            case R.id.edit:
                intent=new Intent();
                intent.setClass(this,ScreenLockActivity.class);
                intent.putExtra(BundleTag.ScreenLockPassword,password);
                intent.putExtra(BundleTag.ScreenLockStatus, Status.Edit.getIndex());
                startActivity(intent);
                break;
            case R.id.start:
                intent=new Intent();
                intent.setClass(this,ScreenLockActivity.class);
                intent.putExtra(BundleTag.ScreenLockStatus,Status.Start.getIndex());
                startActivity(intent);
                break;
        }
    }
}
