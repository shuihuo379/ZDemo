package com.itheima.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.itheima.demo.R;
import com.itheima.test.HuanXinMenuActivity;

/**
 * 自定义三层环形菜单,使用SurfaceView绘图机制
 * @author zhangming
 * @date 2016/06/30
 */
public class MyHuanXinMenu extends SurfaceView implements SurfaceHolder.Callback,Runnable{
	private int mBackGroundColor;
	private int allBackGroundColor; 
//	private int mArcColor = Color.parseColor("#5FB1ED");
	private Context context;
	private Canvas mCanvas;
	private Paint mPaint;
	private SurfaceHolder mHolder;
	
	public int huanxinBackgroundColor;
	public int orderNum;
	
	public MyHuanXinMenu(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		init();
	}

	public MyHuanXinMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.HuanxinMenuView);
		huanxinBackgroundColor = ta.getColor(R.styleable.HuanxinMenuView_huanxinBackgroundColor,getResources().getColor(R.color.main_gray));
		orderNum = ta.getColor(R.styleable.HuanxinMenuView_orderNum, 0);
		
		init();
	}

	public MyHuanXinMenu(Context context) {
		super(context);
		this.context = context;
		init();
	}
	
	
	private void init() {
		mBackGroundColor = Color.DKGRAY;
		allBackGroundColor = getResources().getColor(R.color.main_gray);
		
		if(orderNum==1){
			mCanvas = new Canvas();
			HuanXinMenuActivity.oneSingleInstanceMenu = this;
			HuanXinMenuActivity.oneSingleInstanceMenu.setmCanvas(mCanvas);
		}else{
			mCanvas = HuanXinMenuActivity.oneSingleInstanceMenu.getmCanvas();
		}
		
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(3);
		mPaint.setStyle(Paint.Style.FILL);
	}

	public Canvas getmCanvas() {
		return mCanvas;
	}

	public void setmCanvas(Canvas mCanvas) {
		this.mCanvas = mCanvas;
	}

	/**
	 * 获取窗口的宽高
	 * @return
	 */
	public int[] getScreenWidthAndHeight(Activity activity){
		 DisplayMetrics dm = new DisplayMetrics();    
	     activity.getWindowManager().getDefaultDisplay().getMetrics(dm); //取得窗口属性    
	     int screenWidth = dm.widthPixels;  //窗口宽度
	     int screenHeight = dm.heightPixels;  //窗口高度   
	     
	     return new int[]{screenWidth,screenHeight};
	}

	@Override
	public void run() {
		try{
			mCanvas = mHolder.lockCanvas();
			
			int screenWidth = getScreenWidthAndHeight((Activity)context)[0];
			int screenHeight = getScreenWidthAndHeight((Activity)context)[1];
			
			mPaint.setColor(allBackGroundColor);
			mCanvas.drawRect(new Rect(0,0,screenWidth,screenHeight), mPaint); //此处画的背景宽度为屏幕的宽度,高度450是计算得到的
			mPaint.setColor(mBackGroundColor);
			mCanvas.drawRect(new Rect(0,0,screenWidth,450), mPaint); //此处画的背景宽度为屏幕的宽度,高度450是计算得到的
			
			/**
			mPaint.setColor(Color.parseColor("#5FB1ED"));
			mCanvas.drawArc(new RectF(50,150,650,750),180,180, false, mPaint);  //半径r=(650-50)/2=300   150+300=450
			mPaint.setColor(getResources().getColor(R.color.main_green));
			mCanvas.drawArc(new RectF(130,230,570,670),180,180, false, mPaint); //半径r=(570-130)/2=220  230+220=450
			mPaint.setColor(getResources().getColor(R.color.btn_yellow_color));
			mCanvas.drawArc(new RectF(210,310,490,590),180,180, false, mPaint); //半径r=(490-210)/2=140  310+140=450
			**/
			
			//改为动态调用绘图
			drawFillArc(mCanvas,180,180, false); 
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(mCanvas!=null){
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}
	
	private void drawFillArc(Canvas canvas,int startAngle,int sweepAngle,boolean useCenter){
		Paint paint = new Paint();
		paint.setColor(huanxinBackgroundColor);
		RectF rectF = null;
		if(orderNum == 1){
			rectF = new RectF(210,310,490,590);
		}else if(orderNum == 2){
			rectF = new RectF(130,230,570,670);
		}else if(orderNum == 3){
			rectF = new RectF(50,150,650,750);
		}
		canvas.drawArc(rectF, startAngle, sweepAngle, useCenter, paint);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		new Thread(this).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if(orderNum == 3){
			HuanXinMenuActivity.oneSingleInstanceMenu = null;  //销毁静态变量
		}
	}
}
