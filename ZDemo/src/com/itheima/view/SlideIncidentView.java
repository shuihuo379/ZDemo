package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.Scroller;
import android.widget.TextView;

/**
 * 滑动事件及view滑动(平滑)中的API方法的使用Demo
 * @author zhangming
 */
public class SlideIncidentView extends TextView{
	private Scroller scroller;
	private int mTouchSlop; //用户滑动的最小距离
	private int downX;
	
	public SlideIncidentView(Context context) {
		this(context,null);
	}
	
	public SlideIncidentView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public SlideIncidentView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		scroller = new Scroller(context);
		//scroller = new Scroller(context,new DecelerateInterpolator(2.0f)); //可以设置平滑减速器
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		Log.i("test","mTouchSlop:"+mTouchSlop); //8dp
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 假如scroller滚动还没有结束，我们直接返回
			if (!scroller.isFinished()) {
				return super.dispatchTouchEvent(event);
			}
			downX = (int)event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.dispatchTouchEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE:
				final float dx = (int)(event.getX()-downX); //手指水平移动的距离
				if(Math.abs(dx) > mTouchSlop){
					smoothScrollTo((-1)*((int)dx),0); //平滑滚动
					
					//方式二:使用动画方式实现平滑
					/**
					ValueAnimator animator = ValueAnimator.ofInt(0,1).setDuration(1000);
					animator.addUpdateListener(new AnimatorUpdateListener() {
						@Override
						public void onAnimationUpdate(ValueAnimator animation) {
							float fraction = animation.getAnimatedFraction();
							scrollTo((-1)*(int)((dx)*fraction),0);
						}
					});
					animator.start();
					**/
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return true;
	}
	
	private void smoothScrollTo(int destX,int destY){
		int scrollX = getScrollX(); //相对于母视图(view本身)的位置,初始一般重合,值为0,是个不断变化的元素
		int deltX = destX - scrollX;
		scroller.startScroll(scrollX,0, deltX,0,1000); //仅仅是保存所有参数的初始值
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		if(scroller.computeScrollOffset()){
			scrollTo(scroller.getCurrX(),scroller.getCurrY());
			postInvalidate();
			
			if(scroller.isFinished()){ //滚动动画结束的时候调用此代码块
				scrollTo(0, 0); //reset
			}
		}
	}
}
