package org.sex.hanker.Activity;

import android.app.Activity;
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
import android.widget.TextView;

import org.sex.hanker.BaseParent.BaseActivity;
import org.sex.hanker.Utils.LogTools;
import org.sex.hanker.Utils.ScreenUtils;
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
    LinearLayout betbuttons1,betbuttons2;
    TextView money,start,winmoneyView,relayout;
    int hasmoney=100;
    int betmoney=0;
    int winmoney;
    ArrayList<TextView> textviews=new ArrayList<>();
    HashMap<String,Integer> hashmap=new HashMap<>();
    String tag[]={"big","small","single","double"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);
        srollroad=FindView(R.id.srollroad);
        srollroad.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 9; i++) {
            Car car=new Car(this);
            LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ScreenUtils.getDIP2PX(this,15));
            ll.topMargin=10;
            car.setLayoutParams(ll);
            srollroad.addView(car);
        }

        srollroad.setRunTime(5 * 1000);
//        srollroad.sethasend(true);

        srollroad.setOnchangrank(new ScrollRoadView.OnChangRankListener() {
            @Override
            public void Rank(int[] rank) {
                String rankstr = "";
                for (int i = 0; i < rank.length; i++) {
                    rankstr = rankstr + " " + rank[i];
                }
                LogTools.e("rank", rankstr);
            }
        });
        srollroad.setEndlistener(new ScrollRoadView.EndListener() {
            @Override
            public void Over(int rank[]) {
                winmoney=0;
                betmoney=0;
                winmoneyView.setText("");
                hashmap.clear();
                Clearview();
                HandlerResult(rank);
            }
        });

        betbuttons1=FindView(R.id.betbuttons1);
        betbuttons2=FindView(R.id.betbuttons2);
        winmoneyView=FindView(R.id.winmoney);
        start=FindView(R.id.start);
        start.setOnClickListener(this);
        relayout=FindView(R.id.relayout);
        relayout.setOnClickListener(this);
        money=FindView(R.id.money);
        CreateBetButton();
        Fourbtns();
        money.setText("目前有"+hasmoney+"分");

    }

    private void HandlerResult(int rank[])
    {
        int first=rank[0];
        if(first>4)//大
        {
            if(hashmap.get(tag[0])!=null)
                winmoney=winmoney+hashmap.get(tag[0]);
        }
        else
        {
           if(hashmap.get(tag[1])!=null)
               winmoney=winmoney+hashmap.get(tag[1]);
        }
        if(first%2!=0)//大
        {
            if(hashmap.get(tag[2])!=null)
                winmoney=winmoney+hashmap.get(tag[2]);
        }
        else
        {
            if(hashmap.get(tag[3])!=null)
                winmoney=winmoney+hashmap.get(tag[3]);
        }
        if(hashmap.get(rank[0]+rank[1])!=null)
        {
            winmoney=winmoney+hashmap.get(rank[0]+rank[1]);
        }
        winmoney=winmoney*2;
        hasmoney=hasmoney+winmoney;
        winmoneyView.setTextColor(Color.RED);
        winmoneyView.setText("赢了" + winmoney);
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

    private void CreateBetButton()
    {
        int temp[]=new int[9];
        for (int i = 1; i < 10; i++) {
            temp[i-1]=i;
        }
        ArrayList<String> strs=new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            int num=temp[i];
            for (int j = i+1; j < 9; j++) {
                strs.add(num+" - "+temp[j]);
            }
        }

        for (int i = 0; i <strs.size()/4 ; i++) {
            LinearLayout horlayout=new LinearLayout(this);
            horlayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams ll1=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            ll1.topMargin=1;
            horlayout.setLayoutParams(ll1);
            for (int j = 0; j < 4; j++) {
                LinearLayout item= (LinearLayout)View.inflate(this,R.layout.layout_betitem,null);
                LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.weight=1;
                ll.leftMargin=ScreenUtils.getDIP2PX(this,5);
                item.setLayoutParams(ll);
                TextView key=(TextView)item.getChildAt(0);
                key.setText(strs.get(4 * i + j));
                String first=String.valueOf(key.getText().toString().charAt(0));
                String end=String.valueOf(key.getText().toString().charAt(key.getText().length()-1));
                item.setTag(first+end);
                horlayout.addView(item);
                textviews.add((TextView)item.getChildAt(1));
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TextView betview=(TextView)((LinearLayout)v).getChildAt(1);
                        int count;
                        if(betview.getTag()==null)
                        {
                            count=1;
                            betview.setTag(count);
                            betview.setText("1分");
                        }
                        else
                        {
                            count=(int)betview.getTag() +1;
                            betview.setTag(count);
                            betview.setText(count+"分");
                        }
                        hasmoney--;
                        betmoney++;
                        money.setText("目前有"+hasmoney+"分");
                        hashmap.put((String)v.getTag(),count);
                    }
                });
            }
            betbuttons2.addView(horlayout);
        }
    }

    private void Fourbtns()
    {

        for (int i = 0; i < betbuttons1.getChildCount(); i++) {
            LinearLayout ll=(LinearLayout)betbuttons1.getChildAt(i);
            ll.setTag(tag[i]);
            textviews.add((TextView) ll.getChildAt(1));
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView betview = (TextView) ((LinearLayout) v).getChildAt(1);
                    int count;
                    if (betview.getTag() == null) {
                        count = 1;
                        betview.setTag(count);
                        betview.setText("1分");
                    } else {
                        count = (int) betview.getTag() + 1;
                        betview.setTag(count);
                        betview.setText(count + "分");
                    }
                    hasmoney--;
                    betmoney++;
                    money.setText("目前有" + hasmoney + "分");
                    hashmap.put((String) v.getTag(), count);
                }
            });
        }
    }

    private void Clearview()
    {
        for (int i = 0; i < textviews.size(); i++) {
            textviews.get(i).setText("");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.relayout:
                break;
            case R.id.start:
                srollroad.setResult(createArray(9), 2000);
                srollroad.start();
                break;
        }
    }
}
