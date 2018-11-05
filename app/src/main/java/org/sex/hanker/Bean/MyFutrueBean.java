package org.sex.hanker.Bean;

import java.util.concurrent.Future;

/**
 * Created by Administrator on 2018/11/5.
 */
public class MyFutrueBean {

    int status;
    Future future;

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
}
