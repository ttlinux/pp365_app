package org.sex.hanker.Bean;

import java.util.concurrent.Future;

/**
 * Created by Administrator on 2018/11/5.
 */
public class MyFutrueBean {

    int status=0;
    Future future;
    OnCancelListener onCancelListener;

    public OnCancelListener getOnCancelListener() {
        return onCancelListener;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    public MyFutrueBean(int status,Future future)
    {
        this.status=status;
        this.future=future;
    }

    public Future getFuture() {
        return future;
    }

    public void setFuture(Future future) {
        this.future = future;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public interface OnCancelListener
    {
        public void OnCancel();
    }
}
