package org.sex.hanker.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sex.hanker.Activity.LoginActivity;
import org.sex.hanker.Activity.RegisterActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.Bean.user;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ClearCache;
import org.sex.hanker.View.MyRelativeLayout;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Setting extends BaseFragment implements View.OnClickListener{

    public static Setting setting;
    private LinearLayout list_item,loginlayout;
    private ImageView imagehead;
    private TextView textview1, textview2, textview3, textview4;

    public static Setting GetInstance(Bundle arg) {
        if (setting == null)
            setting = new Setting();
        if (arg != null)
            setting.setArguments(arg);
        return setting;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_setting,null);
    }

    @Override
    public void OnViewShowOrHide(boolean state) {
        super.OnViewShowOrHide(state);
        if(!state)
            Onlogin();
    }

    private void Init()
    {
        textview1 = (TextView) FindView(R.id.textview1);
        textview2 = (TextView) FindView(R.id.textview2);
        textview3 = (TextView) FindView(R.id.textview3);
        textview4 = (TextView) FindView(R.id.textview4);
        imagehead=(ImageView)FindView(R.id.imagehead);
        loginlayout=(LinearLayout)FindView(R.id.loginlayout);
        loginlayout.setOnClickListener(this);
        imagehead.setOnClickListener(this);
        list_item=(LinearLayout)FindView(R.id.list_item);
        String titles[]={"屏幕锁","缓存路径设置","收藏夹","正在缓存","缓存清除"};
        for (int i = 0; i < titles.length; i++) {
            MyRelativeLayout Item_view=(MyRelativeLayout)View.inflate(getActivity(),R.layout.item_fragment_setting,null);
            TextView textview=(TextView)Item_view.findViewById(R.id.title);
            textview.setText(titles[i]);
            textview.setTag(1000+i);
            textview.setOnClickListener(this);
            list_item.addView(Item_view);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.imagehead:
            case R.id.loginlayout:
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), BundleTag.Status);
                break;
        }
        switch ((int)v.getTag())
        {
            case 1000:
                break;
            case 1001:
                break;
            case 1002:
                break;
            case 1003:
                break;
            case 1004:
                ClearCache.Clear(ClearCache.Picture,getActivity());
                ClearCache.Clear(ClearCache.DownloadMovie,getActivity());
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==BundleTag.LoginSuccess)
        {
            Onlogin();
        }
    }

    private void Onlogin()
    {
        BaseApplication app=(BaseApplication)getActivity().getApplication();
        user user=app.getUser();
        if(user==null)return;
        textview1.setText(getString(R.string.account) + ": " + user.getUsename());
        textview3.setVisibility(View.GONE);
        textview4.setVisibility(View.GONE);
        switch (user.getLevel())
        {
            case 0:
                textview2.setText("普通会员");
                break;
            case 1:
                textview2.setText("付费会员");
                break;
        }

    }
}
