package com.itheima.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;

import com.itheima.demo.R;

public class SurfaceDragAndZoomActivity extends Activity implements OnTouchListener{
	private SurfaceHolder mSurfaceHolder;
	private Bitmap mBitmap;
	
	/** 记录是拖拉照片模式还是放大缩小照片模式 */
	private static int mode = 0;// 初始状态  
	/** 拖拉照片模式 */
	private static final int MODE_DRAG = 1;
	/** 放大缩小照片模式 */
	private static final int MODE_ZOOM = 2;
	
	/** 用于记录开始时候的坐标位置 */
	private PointF startPoint = new PointF();
	/** 用于记录拖拉图片移动的坐标位置 */
	private Matrix dragMatrix = new Matrix();
	/** 用于记录图片要进行拖拉时候的坐标位置 */
	private Matrix currentMatrix = new Matrix();
	/** 用于记录上次图片进行缩放移动后的位置 **/
	private Matrix lastMatrix = new Matrix();
	
	/** 两个手指的开始距离 */
	private float startDis;
	/** 两个手指的中间点 */
	private PointF midPoint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.surface_drag_zoom_activity);
		SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.my_surfaceview);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceView.setOnTouchListener(this);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus){
			mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.init_pic);
			// 锁定整个SurfaceView
			Canvas mCanvas = mSurfaceHolder.lockCanvas();
			// 画图
			mCanvas.drawBitmap(mBitmap,mCanvas.getMatrix(),null);
			// 绘制完成，提交修改
			mSurfaceHolder.unlockCanvasAndPost(mCanvas);
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Canvas mCanvas = mSurfaceHolder.lockCanvas();
		/** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mode = MODE_DRAG;
				currentMatrix.set(lastMatrix); // 记录SurfaceView当前的移动位置
				startPoint.set(event.getX(), event.getY()); //记录最初手指按下时的坐标位置
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode == MODE_DRAG) { //拖拉图片
					float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
					float dy = event.getY() - startPoint.y; // 得到y轴的移动距离
					dragMatrix.set(currentMatrix); 
					dragMatrix.postTranslate(dx, dy);  // 在没有移动之前的位置上开始移动
				}else if (mode == MODE_ZOOM) { //放大缩小图片
					float endDis = fingerDistance(event);// 计算结束距离
					if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
						float scale = endDis / startDis;// 得到缩放倍数
						float [] values = new float[9];
						dragMatrix.getValues(values); 
						Log.i("test","translateX: "+values[2]+" translateY: "+values[5]);
						if(values[2] <= 0 && values[5] <= 0 || scale > 1){  //value[2],values[5]值分别为x方向和y方向的平移量
							dragMatrix.set(currentMatrix);
							dragMatrix.postScale(scale, scale,midPoint.x,midPoint.y);
						}
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				mode = 0; //恢复初始状态
				break; 
			// 当触点离开屏幕，但是屏幕上还有触点(手指)
			case MotionEvent.ACTION_POINTER_UP:
				mode = 0;  //恢复初始状态
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				mode = MODE_ZOOM;
				startDis = fingerDistance(event);  //计算两个手指间的距离
				if(startDis > 10f){
					midPoint = midFingerPoint(event);  //记录两个手指间的中间点(目的:Matrix缩放是按中心点进行,故需要记录中心点的位置坐标)
					currentMatrix.set(lastMatrix); //记录当前SurfaceView的缩放倍数
				}
				break;
		 }
		 if(mCanvas!=null){
			 Paint mPaint = new Paint();
			 mPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			 mCanvas.drawPaint(mPaint);
		 }
		 lastMatrix.set(dragMatrix);
		 mCanvas.drawBitmap(mBitmap, dragMatrix, null);
		 mSurfaceHolder.unlockCanvasAndPost(mCanvas);
		 
		 return true;
	}
	
	/** 计算两个手指间的距离 */
	private float fingerDistance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		/** 使用勾股定理返回两点之间的距离 */
		return FloatMath.sqrt(dx * dx + dy * dy);
	}
	
	/** 计算两个手指间的中间点 */
	private PointF midFingerPoint(MotionEvent event) {
		float midX = (event.getX(1) + event.getX(0)) / 2;
		float midY = (event.getY(1) + event.getY(0)) / 2;
		return new PointF(midX, midY);
	}
}
