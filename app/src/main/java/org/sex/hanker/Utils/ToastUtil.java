package org.sex.hanker.Utils;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import org.sex.hanker.mybusiness.R;


/**
 * @category Toast工具类，防止重复弹出
 * 
 * @author limingfang
 */
public class ToastUtil {

	private static Toast mToast;

	private static Handler mHandler = new Handler();
	private static Runnable r = new Runnable() {
		public void run() {
			mToast.cancel();
			mToast = null;
		}
	};

	/**
	 * 短时间的显示
	 * 
	 * @param context
	 * @param message
	 */
	public static void showMessage(Context context, String message) {
		mHandler.removeCallbacks(r);
		if (mToast == null) {
			//加载Toast布局
			View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
			//初始化布局控件
			TextView mTextView = (TextView) toastRoot.findViewById(R.id.message);
			//为控件设置属性
			mTextView.setText(message);
			//Toast的初始化
//			 mToast = new Toast(context);
						mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
			//获取屏幕高度
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			int height = wm.getDefaultDisplay().getHeight();
			mToast.setGravity(Gravity.TOP, 0, height / 2);
			mToast.setDuration(Toast.LENGTH_LONG);
			mToast.setView(toastRoot);
			mToast.show();
			// 只有mToast==null时才重新创建，否则只需更改提示文字
//			mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
		}
		// 延迟1秒隐藏toast
		mHandler.postDelayed(r, 2000);
	}

	/**
	 * 长时间的显示
	 * 
	 * @param context
	 * @param message
	 */
	public static void showLongTimeMessage(Context context, String message,
			int time) {
		mHandler.removeCallbacks(r);
		if (mToast == null) {
			// 只有mToast==null时才重新创建，否则只需更改提示文字
			//加载Toast布局
			View toastRoot = LayoutInflater.from(context).inflate(R.layout.toast, null);
			//初始化布局控件
			TextView mTextView = (TextView) toastRoot.findViewById(R.id.message);
			//为控件设置属性
			mTextView.setText(message);
			//Toast的初始化
			mToast =  Toast.makeText(context, message, time);
			//获取屏幕高度
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			int height = wm.getDefaultDisplay().getHeight();
			mToast.setGravity(Gravity.TOP, 0, height /2);
			mToast.setDuration(Toast.LENGTH_LONG);
			mToast.setView(toastRoot);
			mToast.show();
//			mToast = Toast.makeText(context, message, time);
		}
		// 延迟1秒隐藏toast
		mHandler.postDelayed(r, time);
//		mToast.show();
	}

}
