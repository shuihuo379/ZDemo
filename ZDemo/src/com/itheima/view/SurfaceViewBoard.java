package com.itheima.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.itheima.demo.R;

/**
 * 使用SurfaceView控件模拟绘图板效果
 * @author zhangming
 */
public class SurfaceViewBoard extends SurfaceView implements SurfaceHolder.Callback,Runnable{
	private SurfaceHolder mHolder;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mPaint;
	private boolean isDrawing;
	private long lastDown = -1;
	private static final long DOUBLE_TIME = 200;  
	
	public SurfaceViewBoard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public SurfaceViewBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public SurfaceViewBoard(Context context) {
		super(context);
		init();
	}
	
	public void init(){
		mCanvas = new Canvas();
		mPath = new Path();
		mHolder = getHolder();
		mHolder.addCallback(this);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2f);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.BLUE);
		//mPaint.setShader(new LinearGradient(0, 0, this.getWidth(), this.getHeight(), Color.BLUE, Color.YELLOW, TileMode.REPEAT));
		
		setFocusable(true);
		setFocusableInTouchMode(true);
		setKeepScreenOn(true);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			long nowDown = System.currentTimeMillis();  
			if(nowDown - this.lastDown <= DOUBLE_TIME){
				Log.i("test","双击了,移除canvas中的所有内容...");
				mPath.reset(); //重置路径,即清除canvas中所有绘制的路径
			}else{
				mPath.moveTo(x, y);
				this.lastDown = nowDown;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(x, y);
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return true;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		while(isDrawing){
			draw();
		}
		long end = System.currentTimeMillis();
		if(end-start < 100){
			try {
				Thread.sleep(100-(end-start));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void draw(){
		try{
			mCanvas = mHolder.lockCanvas();
			//do something...
			mCanvas.drawColor(getResources().getColor(R.color.main_gray)); //设置canvas画布的颜色为主题灰
			mCanvas.drawPath(mPath,mPaint);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(mCanvas!=null){
				mHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		isDrawing = true;
		new Thread(this).start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isDrawing = false;  //恢复标志位
	}
}
