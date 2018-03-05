package org.sex.hanker.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;


//左右方向的不写了。。
public class BubbleView extends LinearLayout {

	private int Swidth = 20;// 三角形宽度
	private int SHeight = 20;// 三角形高度
	private int initwidth = -1;
	private float raidus = 20;// 圆角弧度
	private int color=0x00000000;//控件颜色
	private boolean Viewstyle=true;//设置实心还是空心
	private int BackgroudColkor=0;
	private int CoverColor=0;

	public static final int Side_Top = 1;
	public static final int Side_Bot = 2;
	public static final int Side_Left = 3;
	public static final int Side_Right = 4;

	public static final int Style_Left = 1;
	public static final int Style_Middle = 2;
	public static final int Style_Right = 3;

	private int Side = Side_Bot;// 默认向下
	private int Style = Style_Right;// 默认向右

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		DrawTriangleSide(canvas);
	}

	public BubbleView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		Oninit(context);
	}

	public BubbleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		Oninit(context);
	}

	public BubbleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		// 新建一只画笔，并设置为绿色属性
		Oninit(context);
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		postInvalidate();
	}

	public void SetViewColor(int color)
	{
		this.color=color;
		postInvalidate();
	}
	private void Oninit(Context context) {
		setWillNotDraw(false);
	}


	@Override
	public void setBackgroundColor(int color) {
		// TODO Auto-generated method stub
		super.setBackgroundColor(color);
		BackgroudColkor=color;
	}


	public void SetCoverColor(int CoverColor)
	{
		this.CoverColor=CoverColor;
	}

	public void setBotWidth(int width) {
		Swidth = width;
		postInvalidate();
	}

	public void setBotHeight(int height) {
		SHeight = height;
		postInvalidate();
	}

	public void setRaidus(float Raidus) {
		raidus = Raidus;
		postInvalidate();
	}

	public void SetViewStyle(boolean isfill)//设置实心还是空心
	{
		Viewstyle=isfill;
		postInvalidate();
	}
	
	public void SetLayoutPosition(int side, int style)// 第一个参数 上下左右 第二个参数 左 中 右
	{
		Side = side;
		Style = style;
		postInvalidate();
	}

	private void DrawTriangleSide(Canvas canvas) {
		switch (Side) {
		case 1:
			if (initwidth < 0) {
				initwidth = getHeight() + SHeight;
				getLayoutParams().height = initwidth;
				setPadding(0, SHeight, 0, 0);
			}
			DrawTriangleStyle_topOrBot(canvas,SHeight,initwidth);
			break;
		case 2:
			if (initwidth < 0) {
				initwidth = getHeight() + SHeight;
				ViewGroup. LayoutParams layoutParams= getLayoutParams();
				layoutParams.height = initwidth;
				setLayoutParams(layoutParams);
//				setPadding(0, 0, 0, SHeight);
			}
			DrawTriangleStyle_topOrBot(canvas,0,initwidth-SHeight);
			break;
		case 3:

			break;
		case 4:

			break;
		default:
			break;
		}
	}

	private void DrawTriangleStyle_topOrBot(Canvas canvas,int top,int bottom) {


		float middleX = 0;
		switch (Style) {
			case 1:
				middleX =  Swidth * 2.5f;
				break;
			case 2:
				middleX = getWidth()/2;
				break;
			case 3:
				middleX = getWidth() - Swidth * 1.5f;//1.5是参数，懒得写代码了
				break;
			default:
				break;
		}

		Paint _paint = new Paint();
		if(Viewstyle)
			_paint.setStyle(Paint.Style.FILL);
		else
		_paint.setStyle(Paint.Style.STROKE);

		_paint.setAntiAlias(true);
		// 新建矩形r2
		RectF r2 = new RectF();
		r2.left = 2;
		r2.right = getWidth()-2;
		r2.top = top+2;
		r2.bottom = bottom;

		_paint.setColor(color);
		_paint.setStrokeWidth(3);

		canvas.drawRoundRect(r2, raidus, raidus, _paint);



		if(!Viewstyle)
		{
			Path path = new Path();
			path.moveTo(middleX, top>0?0:getHeight());// 此点为多边形的起点
			path.lineTo(middleX+Swidth * 0.5f, top>0?SHeight:initwidth - SHeight);
			path.lineTo(middleX-Swidth * 0.5f, top>0?SHeight:initwidth - SHeight);
			path.close(); // 使这些点构成封闭的多边形
			canvas.drawPath(path, _paint);

			_paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
			_paint.setColor(CoverColor);
			_paint.setStrokeWidth(4);
			canvas.drawLine(middleX+Swidth * 0.5f-2, top>0?SHeight:initwidth - SHeight, middleX-Swidth * 0.5f+2, top>0?SHeight:initwidth - SHeight, _paint);
		}
		else
		{
			Path path = new Path();
			path.moveTo(middleX, top>0?0:getHeight());// 此点为多边形的起点
			path.lineTo(middleX + Swidth * 0.5f, top > 0 ? SHeight+10 : initwidth - SHeight);
			path.lineTo(middleX-Swidth * 0.5f, top>0?SHeight+10:initwidth - SHeight);
			path.close(); // 使这些点构成封闭的多边形
			canvas.drawPath(path, _paint);
		}
		// 画出圆角矩形r2
	}

	//画左右的
	private void DrawTriangleStyle_LeftorRight(Canvas canvas) {

		switch (Style) {
		case 1:

			break;
		case 2:

			break;
		case 3:

			break;

		default:
			break;
		}

		Paint _paint = new Paint();
	
		_paint.setAntiAlias(true);
		// 新建矩形r2
		RectF r2 = new RectF();
		r2.left = 0;
		r2.right = getWidth();
		r2.top = 0;
		r2.bottom = initwidth - SHeight;

		_paint.setColor(Color.GREEN);
		canvas.drawRoundRect(r2, raidus, raidus, _paint);

		Path path = new Path();
		float middleX = getWidth() - Swidth * 1.5f;
		path.moveTo(middleX, initwidth);// 此点为多边形的起点
		path.lineTo(getWidth() - Swidth * 2, initwidth - SHeight);
		path.lineTo(getWidth() - Swidth, initwidth - SHeight);
		path.close(); // 使这些点构成封闭的多边形
		canvas.drawPath(path, _paint);
		
		
		// 画出圆角矩形r2
	}
}
