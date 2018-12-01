package org.sex.hanker.Fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.sex.hanker.Activity.LoginActivity;
import org.sex.hanker.Activity.NewVideoActivity;
import org.sex.hanker.Adapter.MainbannerPagerAdapter;
import org.sex.hanker.BaseParent.BaseApplication;
import org.sex.hanker.BaseParent.BaseFragment;
import org.sex.hanker.User.RegisterActivity;
import org.sex.hanker.Utils.BundleTag;
import org.sex.hanker.Utils.Httputils;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.MyJsonHttpResponseHandler;
import org.sex.hanker.Utils.ScreenUtils;
import org.sex.hanker.View.MyViewPager;
import org.sex.hanker.mybusiness.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by Administrator on 2017/11/3.
 */
public class Home extends BaseFragment implements View.OnClickListener {

    private static Home home;
    MyViewPager viewpager;
    ArrayList<ImageView> imageviews = new ArrayList<ImageView>();
    LinearLayout linearLayout;
    TextView register, login;
    LinearLayout videomain, notemain, imagmain;
    HorizontalScrollView scorll;
    private ImageLoader mImageDownLoader;

    public static Home GetInstance(Bundle arg) {
        if (home == null)
            home = new Home();
        if (arg != null)
            home.setArguments(arg);
        return home;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setNeedCallBack(true);
        Init();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(), R.layout.fragment_home, null);
    }

    @Override
    public void OnViewShowOrHide(boolean state) {
        super.OnViewShowOrHide(state);

    }


    private void Init() {
        //////banner
//        getActivity().startService(new Intent(getActivity(), TestService.class));
        mImageDownLoader = ((BaseApplication) getActivity().getApplication())
                .getImageLoader();
        register = (TextView) FindView(R.id.register);
        login = (TextView) FindView(R.id.login);
        register.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
        register.setOnClickListener(this);
        login.setOnClickListener(this);
        linearLayout = (LinearLayout) FindView(R.id.mainll);
        viewpager = (MyViewPager) FindView(R.id.viewpager);
        viewpager.setOffscreenPageLimit(2);
        viewpager.setOnMeasureListener(new MyViewPager.onMeasureListener() {

            @Override
            public void onmeaure(int height) {

            }
        });

        //////banner

        //////video
        videomain = new LinearLayout(getActivity());
        videomain.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainv.setMargins(0, ScreenUtils.getDIP2PX(getActivity(), 10), 0, 0);
        videomain.setLayoutParams(mainv);

        TextView textView = new TextView(getActivity());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(getActivity(), 40));

        textView.setLayoutParams(ll);
        textView.setText("最新视频");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //测试
                Intent intent = new Intent(getActivity(), NewVideoActivity.class);
                startActivity(intent);
            }
        });
        textView.setPadding(ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10));
        textView.setBackgroundColor(0xffffffff);
        textView.setGravity(Gravity.LEFT | Gravity.CENTER);
        textView.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_backindicator), null);
        videomain.addView(textView);

        linearLayout.addView(videomain);
        //////video

        //////note
        notemain = new LinearLayout(getActivity());
        notemain.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams note_mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        note_mainv.setMargins(0, ScreenUtils.getDIP2PX(getActivity(), 10), 0, 0);
        notemain.setLayoutParams(note_mainv);

        TextView notetitle = new TextView(getActivity());
        notetitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout.LayoutParams notetitle_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(getActivity(), 40));

        notetitle.setLayoutParams(notetitle_ll);
        notetitle.setText("最新辣文");
        notetitle.setPadding(ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10));
        notetitle.setBackgroundColor(0xffffffff);
        notetitle.setGravity(Gravity.LEFT | Gravity.CENTER);
        notetitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_backindicator), null);
        notemain.addView(notetitle);

        linearLayout.addView(notemain);
        //////note

        //////image
        imagmain = new LinearLayout(getActivity());
        imagmain.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams image_mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        image_mainv.setMargins(0, ScreenUtils.getDIP2PX(getActivity(), 10), 0, 0);
        imagmain.setLayoutParams(image_mainv);

        TextView imagtitle = new TextView(getActivity());
        imagtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        LinearLayout.LayoutParams imagtitle_ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getDIP2PX(getActivity(), 40));

        imagtitle.setLayoutParams(imagtitle_ll);
        imagtitle.setText("最新艳图");
        imagtitle.setPadding(ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10), ScreenUtils.getDIP2PX(getActivity(), 10));
        imagtitle.setBackgroundColor(0xffffffff);
        imagtitle.setGravity(Gravity.LEFT | Gravity.CENTER);
        imagtitle.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.icon_backindicator), null);
        imagmain.addView(imagtitle);

        scorll = new HorizontalScrollView(getActivity());
        scorll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imagmain.addView(scorll);
        linearLayout.addView(imagmain);
        //////image

        Mainrequest();
    }

    private void AddButton(RelativeLayout relayout, int size) {
        imageviews.clear();
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        linearLayout.setPadding(0, 0, 0, ScreenUtils.getDIP2PX(getActivity(), 10));
        linearLayout.setLayoutParams(params);

        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(getActivity());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 10;
            imageView.setLayoutParams(layoutParams);
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.yindaocheck));
            imageviews.add(imageView);
            linearLayout.addView(imageView);
        }
        relayout.addView(linearLayout);
    }

    private void AddVideoItem(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length() / 3; i++) {
            LinearLayout view = (LinearLayout) View.inflate(getActivity(), R.layout.model1_horizontal_video, null);
            for (int j = 0; j < 3; j++) {
                final JSONObject jsob = jsonArray.optJSONObject(i * 3 + j);
                LinearLayout linlayout = (LinearLayout) view.getChildAt(j);
                linlayout.setTag(i * 3 + j);
                ImageView imageview = (ImageView) linlayout.getChildAt(0);
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWH(getActivity())[0] / 3 + 10);
                ll.weight = 1;
                imageview.setLayoutParams(ll);
                imageview.setScaleType(ImageView.ScaleType.FIT_XY);
                TextView title = (TextView) linlayout.getChildAt(1);
                title.setText(jsob.optString("title"));
                imageview.setImageDrawable(new ColorDrawable(0x783748));
                linlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), NewVideoActivity.class);
                        intent.putExtra(BundleTag.ProductId, jsob.optString("modelid", ""));
                        intent.putExtra(BundleTag.Country, jsob.optString("arg", ""));
                        startActivity(intent);
                    }
                });
//                mImageDownLoader.displayImage(jsob.optString("pictureurl"), imageview);
            }
            videomain.addView(view);
        }
    }

    private void AddPictureItem(JSONArray jsonarr) {
        LinearLayout videomain = new LinearLayout(getActivity());
        videomain.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams mainv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mainv.topMargin = ScreenUtils.getDIP2PX(getActivity(), 10);
        videomain.setLayoutParams(mainv);

        for (int i = 0; i < jsonarr.length(); i++) {
            RelativeLayout llview = (RelativeLayout) View.inflate(getActivity(), R.layout.model3_galley_picture, null);
            ImageView image = (ImageView) llview.getChildAt(0);
            TextView title = (TextView) llview.getChildAt(1);
            title.setVisibility(View.GONE);
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            mImageDownLoader.displayImage(jsonarr.optJSONObject(i).optString("pictureurl"), image);
            videomain.addView(llview);
        }
        scorll.addView(videomain);
    }

    private void AddNoteitem(JSONArray jsonarr) {
        for (int i = 0; i < jsonarr.length(); i++) {
            JSONObject json = jsonarr.optJSONObject(i);
            LinearLayout view = (LinearLayout) View.inflate(getActivity(), R.layout.model2_vertical_note, null);
            TextView text1 = (TextView) view.findViewById(R.id.title);
            TextView text2 = (TextView) view.findViewById(R.id.content);
            text1.setText(json.optString("title"));
            text2.setText(Html.fromHtml(json.optString("content")));
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll.setMargins(0, 1, 0, 0);
            notemain.addView(view, ll);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register:
                startActivityForResult(new Intent(getActivity(), RegisterActivity.class), BundleTag.RequestCode);
                break;
            case R.id.login:
                startActivityForResult(new Intent(getActivity(), LoginActivity.class), BundleTag.RequestCode);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        LogTools.e("sadsadsad",requestCode+" "+resultCode);
        if (resultCode == BundleTag.LoginSuccess) {
            register.setVisibility(View.GONE);
            login.setVisibility(View.GONE);
        }
    }

    public void Mainrequest() {
        RequestParams requestParams = new RequestParams();
        Httputils.PostWithBaseUrl(Httputils.Home, requestParams, new MyJsonHttpResponseHandler(getActivity(), true) {
            @Override
            public void onSuccessOfMe(JSONObject jsonObject) {
                super.onSuccessOfMe(jsonObject);
                if (!jsonObject.optString("status").equalsIgnoreCase("000000")) return;
                JSONObject datas = jsonObject.optJSONObject("datas");

                RelativeLayout testlayout = (RelativeLayout) FindView(R.id.testlayout);
                JSONArray jsonarr = datas.optJSONArray("banner");
                viewpager.setAdapter(new MainbannerPagerAdapter(getActivity(), jsonarr));
                AddButton(testlayout, jsonarr.length());

                AddVideoItem(datas.optJSONArray("video"));

                AddNoteitem(datas.optJSONArray("note"));

                AddPictureItem(datas.optJSONArray("picture"));
            }

            @Override
            public void onFailureOfMe(Throwable throwable, String s) {
                super.onFailureOfMe(throwable, s);
            }
        });
    }

    /**
     * 获得SD卡总大小
     *
     * @return
     */
    private String getSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(getActivity(), blockSize * totalBlocks);
    }


}
