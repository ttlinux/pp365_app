package org.sex.hanker.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.View.MyRelativeLayout;
import org.sex.hanker.mybusiness.R;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Setting extends BaseFragment{

    public static Setting setting;
    private LinearLayout list_item;
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

    private void Init()
    {
        list_item=(LinearLayout)FindView(R.id.list_item);
        String titles[]={"屏幕锁","缓存路径设置","收藏夹","正在缓存","缓存清除"};
        for (int i = 0; i < titles.length; i++) {
            MyRelativeLayout Item_view=(MyRelativeLayout)View.inflate(getActivity(),R.layout.item_fragment_setting,null);
            TextView textview=(TextView)Item_view.findViewById(R.id.title);
            textview.setText(titles[i]);
            list_item.addView(Item_view);
        }

    }
}
