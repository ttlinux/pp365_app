package org.sex.hanker.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import org.sex.hanker.Utils.LogTools;


/**
 * Created by Administrator on 2017/10/25.
 */
public class MyRelativeLayout extends RelativeLayout {

    private Bitmap bitmap;
    float scale = 0;
    boolean ischanged = false;

    public MyRelativeLayout(Context context) {
        super(context);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        ischanged = changed;
        LogTools.e("MyRelativeLayoutchanged" + ischanged, "" + changed);
        postInvalidate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (bitmap != null && getHeight()>0) {
//            WeakReference<Bitmap> weakReference=new WeakReference<Bitmap>(bitmap);
            Matrix matrix = new Matrix();
            scale = getHeight() * 0.01f / bitmap.getHeight() * 100;
//            ischanged = false;
//            if (scale == 0 || ischanged) {
//                scale = getHeight() * 0.01f / bitmap.getHeight() * 100;
//                ischanged = false;
//            }
//            else {
//                LogTools.e("scale",scale+"-->"+getHeight()+"--->"+bitmap.getHeight());
//            }

            matrix.postScale(scale, scale); //长和宽放大缩小的比例
            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            canvas.drawBitmap(resizeBmp, getWidth() - resizeBmp.getWidth(), 0, null);
        }

    }

    public void setImage(Drawable drawable) {
        this.bitmap = ((BitmapDrawable) drawable).getBitmap();
        postInvalidate();

    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        postInvalidate();
    }
}
