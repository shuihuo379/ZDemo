package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.widget.ViewFlipper;

/**
 * 自定义ViewFlipper控件
 * @author zhangming
 * @date 2016/06/24
 */
public class MyViewFlipper extends ViewFlipper{
	private float mLastMotionX;
	private boolean flag = false;
	
	private TurnPageAnimation mTurnPageAnimation;
	private OnPageChangeListener mOnPageChanageListener;
	
	public MyViewFlipper(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewFlipper(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			final float x = event.getX();
			final float dx = x - mLastMotionX;
			if(dx>0){ //向右滑,显示左边的图像,并伴随左侧进入右侧退出时的动画
				if (!flag && getDisplayedChild() != 0) {  //右滑,且不是第一张图像(每次手指不停移动时,只执行一次,直到手指抬起时,下次移动再执行)
					if(mTurnPageAnimation!=null) {
						mTurnPageAnimation.showPreviousAnimation();
					}
					showPrevious();
				}
			}else{  //向左滑,显示右边的图像,并伴随右侧进入左侧退出时的动画
				if (!flag && getDisplayedChild() != getChildCount() - 1) {  //左滑,且不是最后一张图像(每次手指不停移动时,只执行一次)
					if(mTurnPageAnimation!=null) {
						mTurnPageAnimation.showNextAnimation();
					}
					showNext();
				}
			}
			if (dx != 0) {
				if(!flag) { //保证只执行一次
					if(mOnPageChanageListener!=null) {
						mOnPageChanageListener.onPageSelected(getDisplayedChild());
					}
				}
				flag = true; 
			}
			break;
		case MotionEvent.ACTION_UP:
			flag = false;  //重置标志位
			break;
		}
		return true;
	}
	
	public void showAnimation(Animation in,Animation out){
		if(in!=null){
			setInAnimation(in);
		}
		if(out!=null){
			setOutAnimation(out);
		}
	}
	
	public interface TurnPageAnimation{
		void showPreviousAnimation(); 
		void showNextAnimation();  //左切与右切动画显示回调方法
	}
	
	public interface OnPageChangeListener{
		public void onPageSelected(int pageIndex);
	}
	
	public void setTurnPageAnimation(TurnPageAnimation mTurnPageAnimation) {
		this.mTurnPageAnimation = mTurnPageAnimation;
	}
	
	public void setOnPageChangeListener(OnPageChangeListener pageChangeListener) {
		this.mOnPageChanageListener = pageChangeListener;
	}
}
