package org.sex.hanker.mybusiness;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.BaseParent.BaseFragmentActivity;
import org.sex.hanker.Fragment.Home;
import org.sex.hanker.Fragment.Note;
import org.sex.hanker.Fragment.Picture;
import org.sex.hanker.Fragment.Setting;
import org.sex.hanker.Fragment.Video;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ToastUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends BaseFragmentActivity implements RadioGroup.OnCheckedChangeListener{

    FrameLayout layout;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ArrayList<BaseFragment> fragments=new ArrayList<BaseFragment>();
    RadioGroup bottomview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager=getSupportFragmentManager();
        fragmentTransaction=fragmentManager.beginTransaction();

        fragments.add(Home.GetInstance(null));
        fragmentTransaction.add(R.id.framelayout,fragments.get(0));

        fragments.add(Video.GetInstance(null));
        fragmentTransaction.add(R.id.framelayout,fragments.get(1));

        fragments.add(Picture.GetInstance(null));
        fragmentTransaction.add(R.id.framelayout,fragments.get(2));

        fragments.add(Note.GetInstance(null));
        fragmentTransaction.add(R.id.framelayout,fragments.get(3));

        fragments.add(Setting.GetInstance(null));
        fragmentTransaction.add(R.id.framelayout,fragments.get(4));



        layout=FindView(R.id.framelayout);



        if(fragments.isEmpty())return;
        fragmentTransaction.hide(fragments.get(0));
        fragmentTransaction.commitAllowingStateLoss();
        bottomview=(RadioGroup)findViewById(R.id.bottomview);
        bottomview.setOnCheckedChangeListener(this);
        ((RadioButton)bottomview.getChildAt(0)).performClick();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(checkedId==R.id.home){
            setFragment(0);
        }
        if(checkedId==R.id.video){
            setFragment(1);
        }
        if(checkedId==R.id.picture){
            setFragment(2);
        }
        if(checkedId==R.id.note){
            setFragment(3);
        }
        if(checkedId==R.id.setting){
            setFragment(4);
        }


    }

    public  void setFragment(int index) {
        for (int i = 0; i < fragments.size(); i++) {
            Fragment fragment = fragments.get(i);

            FragmentTransaction ft = fragmentManager.beginTransaction();
            if (index == i) {
                ft.show(fragment);
            } else {
                ft.hide(fragment);
            }
            ft.commitAllowingStateLoss();
        }
    }

    private boolean mBackKeyPressed = false; // 记录是否有首次按键
    @Override
    public void onBackPressed() {
        if (!mBackKeyPressed) {
            ToastUtil.showMessage(this, "再按一次退出程序");
            mBackKeyPressed = true;
            new Timer().schedule(new TimerTask() {
                // 延时两秒，如果超出则擦错第一次按键记录
                @Override
                public void run() {
                    mBackKeyPressed = false;
                }
            }, 2000);
        } else {
            // 退出程序
            finish();
            System.exit(0);
        }
    }
}
