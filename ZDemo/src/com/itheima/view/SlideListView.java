package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Scroller;

import com.itheima.demo.R;

public class SlideListView extends ListView{
	/**
	 * 当前滑动的ListView　position
	 */
	private int slidePosition;
	/**
	 * 手指按下Y的坐标
	 */
	private int downY;
	/**
	 * 手指按下X的坐标
	 */
	private int downX;
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;
	
	/**
	 * 临界位置
	 */
	private int criticalPos; 
	/**
	 * ListView的item
	 */
	private View itemView;
	/**
	 * 滑动类
	 */
	private Scroller scroller;
	/**
	 * 是否响应滑动，默认为不响应
	 */
	private boolean isSlide = false;
	/**
	 * 认为是用户滑动的最小距离
	 */
	private int mTouchSlop;
	
	/**
	 * 用来指示item滑出屏幕的方向,向左或者向右,用一个枚举值来标记
	 */
	private RemoveDirection removeDirection;

	// 滑动删除方向的枚举值
	public enum RemoveDirection {
		RIGHT, LEFT;
	}


	public SlideListView(Context context) {
		this(context, null);
	}

	public SlideListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SlideListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
		criticalPos = screenWidth/3;  //初始化临界值
 		scroller = new Scroller(context);
		//此变量表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件，如viewpager就是用这个距离来判断用户是否翻页
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}
	
	/**
	 * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {
			// 假如scroller滚动还没有结束，我们直接返回
			if (!scroller.isFinished()) {
				return super.dispatchTouchEvent(event);
			}
			downX = (int) event.getX();
			downY = (int) event.getY();

			slidePosition = pointToPosition(downX, downY);

			// 无效的position, 不做任何处理
			if (slidePosition == AdapterView.INVALID_POSITION) {
				return super.dispatchTouchEvent(event);
			}

			// 获取我们点击的item view
			itemView = getChildAt(slidePosition - getFirstVisiblePosition());
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			if (Math.abs(event.getX() - downX) > mTouchSlop && Math.abs(event.getY() - downY) < mTouchSlop) {
				isSlide = true;
			}
			break;
		}
		case MotionEvent.ACTION_UP:
			break;
		}

		return super.dispatchTouchEvent(event);
	}
	
	/**
	 * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
	 */
	private void scrollRight() {
		removeDirection = RemoveDirection.RIGHT;
		final int delta = (screenWidth + itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数,执行动画效果,在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, -delta, 0, Math.abs(delta));
		postInvalidate(); // 刷新itemView,手动调用computeScroll方法,前提是首先需要调用startScroll方法
	}

	/**
	 * 向左滑动，根据上面我们知道向左滑动为正值
	 */
	private void scrollLeft() {
		removeDirection = RemoveDirection.LEFT;
		final int delta = (screenWidth - itemView.getScrollX());
		// 调用startScroll方法来设置一些滚动的参数,执行动画效果,在computeScroll()方法中调用scrollTo来滚动item
		scroller.startScroll(itemView.getScrollX(), 0, delta, 0, Math.abs(delta));
		postInvalidate(); // 刷新itemView,手动调用computeScroll方法
	}


	/**
	 * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
	 */
	private void scrollByDistanceX() {
		if (itemView.getScrollX() >= criticalPos/2) { //滑动的距离超过最大值(临界值)的一半
			scrollLeft();  //向左滚动
		} else if (itemView.getScrollX() <= -criticalPos/2) {
			scrollRight(); //向右滚动 
		} else {
			itemView.scrollTo(0, 0); // 滚回到原始位置
		}
	}

	/**
	 * 处理我们拖动ListView item的逻辑
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
			final int action = ev.getAction();
			int x = (int) ev.getX();
			switch (action) {
			case MotionEvent.ACTION_MOVE:
				int deltaX = downX - x;  //右滑值为负,左滑值为正
				downX = x;
				if (itemView.getScrollX() >= criticalPos && deltaX>0){
					deltaX = 0;
				}else if(itemView.getScrollX() <= -criticalPos && deltaX<0){
					deltaX = 0;
				}
				// 手指拖动itemView滚动,deltaX大于0向左滚动,小于0向右滚
				itemView.scrollBy(deltaX, 0);
				break;
			case MotionEvent.ACTION_UP:
				scrollByDistanceX();
				// 手指离开的时候就不响应左右滚动
				isSlide = false;
				break;
			}
			return true; // 拖动的时候ListView不滚动
		}

		//否则直接交给ListView来处理onTouchEvent事件
		return super.onTouchEvent(ev);
	}

	/**
	 * 当我们执行onTouch()或invalidate()或postInvalidate()都会导致这个方法的执行
	 */
	@Override
	public void computeScroll() {
		// 当startScroll执行过程中即在duration时间内,computeScrollOffset方法会一直返回false,但当动画执行完成后会返回true
		if (scroller.computeScrollOffset()) {
			int dx = scroller.getCurrX();
			if(dx >= criticalPos){
				dx = criticalPos;
			}else if(dx <= -criticalPos){
				dx = -criticalPos;
			}
			// 让ListView item根据当前的滚动偏移量进行滚动
			itemView.scrollTo(dx, scroller.getCurrY());
			postInvalidate();

			// 滚动动画结束的时候调用回调接口
			if (scroller.isFinished()) {
				if(removeDirection == RemoveDirection.LEFT){
					itemView.scrollTo(criticalPos,0);  //以初始点为原点,向左偏移量为正
				}else if(removeDirection == RemoveDirection.RIGHT){
					itemView.scrollTo(-criticalPos,0); //以初始点为原点,向右偏移量为负
				}
			}
		}
	}
}
