package org.sex.hanker.Activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.View.Car;
import org.sex.hanker.View.ScrollRoadView;
import org.sex.hanker.mybusiness.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/12/20.
 */
public class Testcar extends BaseActivity implements View.OnClickListener{

    ScrollRoadView srollroad;
    TextView start,relayout;
    int hasmoney=100;
    int betmoney=0;
    int winmoney;
    ArrayList<TextView> textviews=new ArrayList<>();
    HashMap<String,Integer> hashmap=new HashMap<>();
    String tag[]={"big","small","single","double"};
    private Bitmap roadpic,beijinpic;
    RelativeLayout aaa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        srollroad=FindView(R.id.srollroad);
        aaa=FindView(R.id.aaa);
        aaa.setVisibility(View.VISIBLE);
        srollroad.setOrientation(LinearLayout.VERTICAL);
        BitmapFactory.Options bfoOptions = new BitmapFactory.Options();
        bfoOptions.inScaled = false;
        beijinpic= BitmapFactory.decodeResource(this.getResources(), R.drawable.shan,bfoOptions);
        roadpic = BitmapFactory.decodeResource(this.getResources(), R.drawable.gamepic,bfoOptions);
        for (int i = 0; i < 10; i++) {
            Car car=new Car(this,i);
            LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, car.getViewHeight());
            if(i==0)
            ll.topMargin=beijinpic.getHeight();
            if(i==9)
            ll.bottomMargin=40;
            car.setLayoutParams(ll);
            srollroad.addView(car);
        }

        srollroad.setRunTime(8 * 1000);
        srollroad.setOnchangrank(new ScrollRoadView.OnChangRankListener() {
            @Override
            public void Rank(int[] rank) {
                String rankstr = "";
                for (int i = 0; i < rank.length; i++) {
                    rankstr = rankstr + "," + rank[i];
                }
                LogTools.e("rank", rankstr);
            }
        });


        start=FindView(R.id.start);
        start.setOnClickListener(this);
        relayout=FindView(R.id.relayout);
        relayout.setOnClickListener(this);

    }



    public Integer[] createArray(int len) {
        Integer solutionArr[] = new Integer[len];
        List list=new ArrayList<Integer>();
        for (int i = 0; i < len; i++)
            list.add(i+1);
        Collections.shuffle(list);
        list.toArray(solutionArr);
        return solutionArr;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.start:
                srollroad.setResult(createArray(10), 2000);
                srollroad.start();
                break;
        }
    }
}
