package org.sex.hanker.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

/*
 * 手机屏幕相关参数
 */
public class ScreenUtils {

	public static final int HORIZONTAL = 0;
	public static final int VERTICAL = 1;

	public static DisplayMetrics getDisplayMetrics(Context context) {
		return context.getResources().getDisplayMetrics();
	}

	/**
	 * 根据手机的分辨率�?px(像素) 的单�?转成�?dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 单位转换：dp转Px
	 * 
	 * @param i
	 * @param context
	 * @return
	 */
	public static int getDIP2PX(Context context,float i) {
		float scale = context.getResources().getDisplayMetrics().density;
		int size = (int) (i * scale + 0.5f);
		return size;

	}
	public static int getDensity(Context context) {
		DisplayMetrics displayMetrics =context. getResources().getDisplayMetrics();
		int size=(int)(1*displayMetrics.densityDpi/160);
		return size;
	}
	public static int getDipTopx(Context context, float dipValue, int orientation) {
		if (orientation == HORIZONTAL) {
			return (int) (dipValue * (getDisplayMetrics(context).widthPixels / 320.0));
		} else {
			return (int) (dipValue * (getDisplayMetrics(context).heightPixels / 480.0));
		}
	}

	public static int getPxTodip(Context context, float pxValue, int orientation) {
		if (orientation == HORIZONTAL) {
			return (int) (pxValue / (getDisplayMetrics(context).widthPixels / 320.0));
		} else {
			return (int) (pxValue / (getDisplayMetrics(context).heightPixels / 480.0));
		}
	}

	public static int getDpToPx(Resources res, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				res.getDisplayMetrics());
	}
	
	public static float applyDimension(Context context, int unit, float size) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 *
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 *
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

	// SDTableView 右区的列�?
	public static int getRealItemWidth(Context context, int width) {
		int deviceWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		int deviceHeight = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getHeight();
		int realWidth;
		if (deviceWidth < deviceHeight) {
			realWidth = deviceWidth * width / 480;
		} else {
			realWidth = deviceHeight * width / 480;
		}
		return realWidth;
	}

	/**
	 * �?800*480 密度�?1.5为标准按比例调整textsize
	 * 
	 * @param context
	 * @return
	 */
	public static float getMultiple(Context context) {
		float mitiple;
		final float density = context.getResources().getDisplayMetrics().density;
		final float F15 = 1.5f;
		int xWidth = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		int xHeight = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getHeight();
		if (xWidth < xHeight) {
			if (density == 1f) {
				mitiple = ((float) xWidth) / 480;
			} else {
				mitiple = ((float) xWidth) / 480 / density * F15;
			}
		} else {
			if (density == 1f) {
				mitiple = ((float) xHeight) / 480;
			} else {
				mitiple = ((float) xHeight) / 480 / density * F15;
			}
		}

		return mitiple;
	}

	/**
	 * 按分辨率来调整字体大�?
	 * 
	 * @param context
	 * @param s
	 * @return
	 */
	public static int getTextSizeByResolution(Context context, int s) {
		int textSize = s;
		float mitiple = 0;
		mitiple = ScreenUtils.getMultiple(context);
		textSize = (int) (textSize * mitiple);
		return textSize;
	}

	/**
	 * 按分辨率来调整字体大�?
	 * 
	 * @param context
	 * @param s
	 * @return
	 */
	public static int getTextSize4TableByResolution(Context context, int s) {
		int textSize = s;
		if (context instanceof Activity) {
			int width = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getWidth();
			int height = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getHeight();
			if (height > 1900) {
				return (int) (textSize * 1.1);
			}
			if (height == 1280 && width == 720) {
				return (int) (textSize * 1.2);
			}
			if (width > 1200 || height > 1200) {
				return (int) (textSize * 2);
			}
			if (width > 1000 || height > 1000) {
				return (int) (textSize * 1.6); // 1.6 ��
			}
		}
		// Log.i("superdata", "" + textSize);
		return textSize;
	}

	/**
	 * �?800*480 密度�?1.5为标准按比例调整width、height�?
	 * 
	 * @param context
	 * @return
	 */
	public static int getDIP2PXByResolution(int s, Context context) {
		return (int) (((float) ScreenUtils.getDIP2PX(context,s )) * ScreenUtils
				.getMultiple(context));
	}

	/**
	 * 判断 手机/平板
	 */
	public static boolean isTablet(Context context) {
		if (context instanceof Activity) {
			int width = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getWidth();
			int height = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getHeight();
			if (width > 1000 || height > 1000) {
				return true;
			}
		}
		return false;
	}

	// 是否调整字体大小( 1920x1080.440dpi 的特别处�?
	public static boolean isScaleSize(Context context) {
		if (context instanceof Activity) {
			int width = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getWidth();
			int height = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getHeight();
			if (height > 1900) { // �߷ֱ��� ,���ǲ���ƽ�塣
				return false;
			}
			if (width > 1000 || height > 1000) {
				return true;
			}
		}
		return false;
	}

	// 是否调整列宽( 1920x1080.440dpi 的特别处�?
	public static boolean isScaleWidth(Context context) {
		if (context instanceof Activity) {
			int width = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getWidth();
			int height = ((Activity) context).getWindowManager()
					.getDefaultDisplay().getHeight();
			if (height > 1900) { //
				return false;
			}
		}
		return true;
	}

	public static int[] getScreenWH(Activity activity) {
		DisplayMetrics metrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int[] wh = new int[2];
		wh[0] = metrics.widthPixels;
		wh[1] = metrics.heightPixels;
		return wh;
	}

	public static void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
//		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
			View listItem = listAdapter.getView(0, null, listView);
			listItem.measure(0,0);
			totalHeight += listItem.getMeasuredHeight()*listAdapter.getCount();
//		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}

	public static void setGridHeightBasedOnChildren(GridView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
//		for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
		View listItem = listAdapter.getView(0, null, listView);
		listItem.measure(0,0);
		totalHeight += listItem.getMeasuredHeight()*listAdapter.getCount();
//		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight + (listView.getVerticalSpacing() * (listAdapter.getCount() - 1));
		listView.setLayoutParams(params);
	}
}
