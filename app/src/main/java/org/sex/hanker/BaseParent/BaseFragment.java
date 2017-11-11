package org.sex.hanker.BaseParent;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import org.sex.hanker.Fragment.Home;


/**
 * Created by Administrator on 2017/3/28.
 */
public  class BaseFragment<T> extends Fragment {

    private boolean isShowAlready=false;//如果跳自己 那么过了OnViewShowOrHide（false）就没有clickfragment，否则继续OnViewShowOrHide（false）

    private boolean isOpenRefresh=false;//打开功能开关，跳自己需要刷新的时候打开

    private boolean ifjumptoanothoract=false;////如果跳转到activity 标志

    private boolean isResumeCallBack=false;//打开功能开关，当activity回来时候show一遍

    private boolean NeedCallBack=false;

    private boolean showstate=false;//显示的状态

    public boolean isNeedCallBack() {
        return NeedCallBack;
    }

    public void setNeedCallBack(boolean needCallBack) {
        NeedCallBack = needCallBack;
    }

    public void OnViewShowOrHide(boolean state)//hidden
    {

        if(NeedCallBack)
        {
            clickfragment();
            NeedCallBack=false;
        }

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        boolean state=getUserVisibleHint();
        if(state) {
            OnViewShowOrHide(false);//show
        }
            else
        {
            OnViewShowOrHide(true);//dismiss
        }
        if(isOpenRefresh)
        {
            if(!state)
            {
                isShowAlready=false;//hidden
            }
            else
            {
                isShowAlready=true;//show
            }
        }
        showstate=state;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        OnViewShowOrHide(hidden);
        if(isOpenRefresh)
        {
            if(hidden)
            {
                isShowAlready=false;//hidden
            }
            else
            {
                isShowAlready=true;//show
            }
        }
        showstate=!hidden;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        View mview=FindView(R.id.back);
//        if(mview!=null)
//        {
//            mview.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    finish();
//                }
//            });
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    public <T> T FindView(int id)
    {
        return getView()!=null?(T)getView().findViewById(id):null;
    }

    public void clickfragment() {
        if (!isShowAlready && isOpenRefresh) {
            OnViewShowOrHide(false);
        }
        else
            isShowAlready=false;

    }

    public void CallbackFunction(){

    }

    public static  enum BALL_TYPE
    {
        FootBall_GuoGuan ( 0),				//足球过关
        BasketBall_GuoGuan (1),				//篮球过关    l

        FootBall_Normal ( 2),			//足球单式 等
        BasketBall_Normal ( 3),		//篮球单式等

//        BasketBall_Result ( 4),			//篮球赛果
//        FootBall_Result ( 5),		//足球赛果

        FootBall_HBoDan ( 4),			//足球半场波胆
        FootBall_BoDan ( 5),			//足球波胆

        FootBall_ZongRuQiu ( 6),			//足球总入球
        FootBall_BanQuan ( 7),			//足球半场全场

        BasketBall_Re_Main ( 8),			//篮球滚球
        FootBall_Re ( 9);			//足球滚球

        public int _nCode;
        private BALL_TYPE(int _nCode) {
            this._nCode=_nCode;
        }

        @Override
        public String toString() {
            return super.toString();
        }
        public final int value() {
            return this._nCode;
        }

    };

    public static  enum BALL_RESULT_TYPE
    {

        BasketBall_Result ( 0),			//篮球赛果
        FootBall_Result ( 1);	//足球赛果


        public int _nCode;
        private BALL_RESULT_TYPE(int _nCode) {
            this._nCode=_nCode;
        }

        @Override
        public String toString() {
            return super.toString();
        }
        public final int value() {
            return this._nCode;
        }

    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        ifjumptoanothoract=false;
    }

    @Override
    public void onPause() {
        super.onPause();
        if(isResumeCallBack && showstate)
        ifjumptoanothoract=true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ifjumptoanothoract && isResumeCallBack && showstate)
        {
            OnViewShowOrHide(false);
            ifjumptoanothoract=false;
        }

    }


    public boolean isResumeCallBack() {
        return isResumeCallBack;
    }

    public void setResumeCallBack(boolean isResumeCallBack) {
        this.isResumeCallBack = isResumeCallBack;
    }

    public boolean isOpenRefresh() {
        return isOpenRefresh;
    }

    public void setOpenRefresh(boolean isOpenRefresh) {
        this.isOpenRefresh = isOpenRefresh;
    }
}
