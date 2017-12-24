package org.sex.hanker.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
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

/**
 * Created by Administrator on 2017/12/20.
 */
public class Testcar extends BaseActivity {

    ScrollRoadView srollroad;
    LinearLayout betbuttons1,betbuttons2;


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
        int result[]={4,2,1,3,6,5,7,8,9};
        srollroad.setResult(result,2000);
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
        srollroad.start();
        betbuttons1=FindView(R.id.betbuttons1);
        betbuttons2=FindView(R.id.betbuttons2);
        CreateBetButton();
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
                TextView button=new TextView(this);
                LinearLayout.LayoutParams ll=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ll.weight=1;
                ll.leftMargin=ScreenUtils.getDIP2PX(this,5);
                button.setLayoutParams(ll);
                button.setGravity(Gravity.CENTER);
                button.setPadding(0, ScreenUtils.getDIP2PX(this,10), 0, ScreenUtils.getDIP2PX(this,10));
                button.setBackgroundColor(getResources().getColor(R.color.gray4));
                button.setText(strs.get(4*i+j));
                horlayout.addView(button);
            }
            betbuttons2.addView(horlayout);
        }

    }
}
