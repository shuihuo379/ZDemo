package com.itheima.view;

import com.itheima.demo.R;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * ViewDragHelper类的使用
 * @author zhangming
 */
public class DragViewGroup extends FrameLayout{
	private ViewDragHelper mViewDragHelper;
	private LinearLayout mMenuView,mMainView;
	public int mWidth;  //侧滑菜单的宽度
	
	public DragViewGroup(Context context){
		super(context);
		initView();
	}
	
	public DragViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public DragViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
	//布局加载完成后回调此方法
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mMenuView = (LinearLayout) getChildAt(0);
		mMainView = (LinearLayout) getChildAt(1);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = mMenuView.getMeasuredWidth(); //测量宽度
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		return mViewDragHelper.shouldInterceptTouchEvent(event); //事件拦截
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mViewDragHelper.processTouchEvent(event);  //触摸事件传递给ViewDragHelper来处理
		return true;
	}

	public void initView(){
		mViewDragHelper = ViewDragHelper.create(this,new ViewDragHelper.Callback() {
			@Override
			public boolean tryCaptureView(View child, int pointerId) {
				//当前ViewGroup中定义了两个子View,一个是MenuView,另一个是MainView,当指定如下代码时,则只有MainView可以被拖动
				return mMainView == child;
			}
			
			//处理水平方向移动
			@Override
			public int clampViewPositionHorizontal(View child, int left, int dx) {
				return left;  //参数left代表水平方向上child移动的距离,直接返回参数left即可
			}
			
			@Override
			public int clampViewPositionVertical(View child, int top, int dy) {
				//return super.clampViewPositionVertical(child, top, dy); //默认值为0,即在此方向上不发生滑动
				return 0;
			}
			
			//拖动结束后调用
			@Override
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				super.onViewReleased(releasedChild, xvel, yvel);
				if(mMainView.getLeft()<mWidth){
					//关闭菜单,相当于Scroller的startScroll方法
					closeMenu();
				}else{
					//打开菜单
					openMenu();
				}
			}
			
			//用户触摸到View后回调
			@Override
			public void onViewCaptured(View capturedChild, int activePointerId) {
				Log.i("test","触摸到View...");
				super.onViewCaptured(capturedChild, activePointerId);
			}
			
			//拖拽状态改变时回调
			@Override
			public void onViewDragStateChanged(int state) {
				//ViewDragHelper.STATE_IDLE,ViewDragHelper.STATE_DRAGGING...
				super.onViewDragStateChanged(state);
			}
			
			//位置改变时回调,常用于滑动时更改scale进行缩放等效果
			@Override
			public void onViewPositionChanged(View changedView, int left,int top, int dx, int dy) {
				if(left > mWidth){
					//Log.i("test","width===>"+mMainView.getWidth()+" measureWidth===>"+mMainView.getMeasuredWidth()); //720,720
					mMainView.layout(mWidth,0, mWidth+mMainView.getWidth(),mMainView.getHeight());
				}
				super.onViewPositionChanged(changedView, left, top, dx, dy);
			}
		});
	}
	
	@Override
	public void computeScroll() {
		if(mViewDragHelper.continueSettling(true)){
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}
	
	public void openMenu(){
		mViewDragHelper.smoothSlideViewTo(mMainView,mWidth,0);
		ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
	}
	
	public void closeMenu(){
		mViewDragHelper.smoothSlideViewTo(mMainView, 0, 0);
		ViewCompat.postInvalidateOnAnimation(DragViewGroup.this);
	}
}
