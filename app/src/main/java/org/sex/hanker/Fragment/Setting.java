package org.sex.hanker.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.BaseParent.BaseFragmentActivity;
import org.sex.hanker.User.LoginActivity;
import org.sex.hanker.Activity.VideoTask.VideoTaskActivity;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.User.ScreenLockParentActivity;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.ClearCache;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.TimeUtils;
import org.sex.hanker.Utils.ToastUtil;
import org.sex.hanker.View.MyRelativeLayout;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Setting extends BaseFragment implements View.OnClickListener{

    public static Setting setting;
    private LinearLayout list_item,logonlayout,notyetloginlayout;
    private ImageView imagehead;
    private TextView levelname,expiretime,username,country;
    boolean islogin=false;
    SharedPreferences sharedPreferences;


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
        if(!state && islogin==false && ((BaseApplication)getActivity().getApplication()).getUsername()!=null)
        {
            Onlogin();
        }

    }

    private void Init()
    {
        sharedPreferences=((BaseApplication)getActivity().getApplication()).getSharedPreferences();
        levelname=(TextView)getView().findViewById(R.id.levelname);
        expiretime=(TextView)getView().findViewById(R.id.expiretime);
        username=(TextView)getView().findViewById(R.id.username);
        country=(TextView)getView().findViewById(R.id.country);
        imagehead=(ImageView)FindView(R.id.imagehead);
        logonlayout=(LinearLayout)FindView(R.id.logonlayout);
        notyetloginlayout=(LinearLayout)FindView(R.id.notyetloginlayout);
        notyetloginlayout.setOnClickListener(this);
        imagehead.setOnClickListener(this);
        list_item=(LinearLayout)FindView(R.id.list_item);
        String titles[]=getResources().getStringArray(R.array.settingmenu);
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
        if(v.getTag()!=null)
        {
            String Username=((BaseApplication)getActivity().getApplication()).getUsername();
            if(Username==null || Username.length()==0)
            {
                ToastUtil.showMessage(getActivity(),getString(R.string.plzlogin));
                return;
            }
            Intent intent;
            TextView textView=(TextView)v;
            switch ((int)v.getTag())
            {
                case 1000:
                    intent=new Intent(getActivity(), ScreenLockParentActivity.class);
                    intent.putExtra(BundleTag.Title,textView.getText().toString());
                    startActivity(intent);
                    break;
                case 1001:
                    break;
                case 1002:
                    startActivity(new Intent(getActivity(), VideoTaskActivity.class));
                    break;
                case 1003:
                    ClearCache.Clear(ClearCache.Picture,getActivity());
                    ClearCache.Clear(ClearCache.DownloadMovie,getActivity());
                    break;
            }
        }
        else
        {
            switch (v.getId())
            {
                case R.id.imagehead:
                case R.id.notyetloginlayout:
                    startActivityForResult(new Intent(getActivity(), LoginActivity.class), BundleTag.RequestCode);
                    break;
            }
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

        logonlayout.setVisibility(View.VISIBLE);
        notyetloginlayout.setVisibility(View.GONE);
        String userinfo=sharedPreferences.getString(BundleTag.UserInfo,"");
        if(userinfo!=null && userinfo.length()>0)
        {
            islogin=true;
            try {
                JSONObject jsonObject=new JSONObject(userinfo);
                long time=Long.valueOf(jsonObject.optString("remainoperationtime", ""));
                TimeUtils.setM_time(time);
                TimeUtils.setSixtySecondInterface(new TimeUtils.SixtySecondInterface() {
                    @Override
                    public void onTik(long mtime) {
                        expiretime.setText(getString(R.string.remaintritrialtime)+"  "+ TimeUtils.FormatTime2(mtime));
                    }
                });
                expiretime.setText(getString(R.string.remaintritrialtime)+"  "+ TimeUtils.FormatTime2(time));
                levelname.setText(jsonObject.optString("levelname", ""));
                username.setText(jsonObject.optString("usename", ""));
                String countrys[]=jsonObject.optString("countryname","").split("\\|");
                BaseFragmentActivity baseActivity=(BaseFragmentActivity)getActivity();
                if(countrys!=null && countrys.length>1)
                {
                    if(baseActivity.getIsChina())
                    {
                        country.setText(countrys[1]);
                    }
                    else
                    {
                        country.setText(countrys[0]);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        TimeUtils.setSixtySecondInterface(null);
    }
}
