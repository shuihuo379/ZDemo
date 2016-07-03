package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 自定义黑马式的瀑布流容器LinearLayout
 * @author zhangming
 * @date 2016/06/26
 */
public class HeimaPinterestLinearLayout extends LinearLayout{
	public HeimaPinterestLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
   /**
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;  //拦截事件,此时会调用此类中onTouchEvent
	}
	**/
	
	//事件分发,最新执行的方法
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		int width = getWidth()/getChildCount();  //每个ListView的宽度
		int height = getHeight();
		
		float eventX = event.getX();
		if (eventX<width){	// 滑动左边的 listView
			event.setLocation(width/2, event.getY());  //重新设置event事件的触摸坐标,这是一个相对坐标,其横坐标为该ListView宽度的一半,0<X<width
			getChildAt(0).dispatchTouchEvent(event); //为左边的listview分发事件
			return true;
		}else if (eventX > width && eventX < 2 * width) { //滑动中间的 listView  
			float eventY = event.getY();
			event.setLocation(width/2,eventY);
			if (eventY < height / 2) { //触摸的高度处在该ListView一半分界线的上方,所有ListView整体移动
				for(int i=0;i<getChildCount();i++){
					getChildAt(i).dispatchTouchEvent(event); //为每个ListView分发事件
				}
			}else{ //触摸的高度处在该ListView一半分界线的下方,只移动中间的ListView
				getChildAt(1).dispatchTouchEvent(event);
			}
			return true;
		}else if (eventX>2*width){
			event.setLocation(width/2, event.getY()); 
			getChildAt(2).dispatchTouchEvent(event);
			return true;
		}
		
		return super.dispatchTouchEvent(event);
	}
	
	
   /**
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int width = getWidth()/getChildCount();  //每个ListView的宽度
		int height = getHeight();
		
		float eventX = event.getX();
		if (eventX<width){	// 滑动左边的 listView
			event.setLocation(width/2, event.getY());  //重新设置event事件的触摸坐标,这是一个相对坐标,其横坐标为该ListView宽度的一半,0<X<width
			getChildAt(0).dispatchTouchEvent(event); //为左边的listview分发事件
			return true;
		}else if (eventX > width && eventX < 2 * width) { //滑动中间的 listView  
			float eventY = event.getY();
			event.setLocation(width/2,eventY);
			if (eventY < height / 2) { //触摸的高度处在该ListView一半分界线的上方,所有ListView整体移动
				for(int i=0;i<getChildCount();i++){
					getChildAt(i).dispatchTouchEvent(event); //为每个ListView分发事件
				}
			}else{ //触摸的高度处在该ListView一半分界线的下方,只移动中间的ListView
				getChildAt(1).dispatchTouchEvent(event);
			}
			return true;
		}else if (eventX>2*width){
			event.setLocation(width/2, event.getY()); 
			getChildAt(2).dispatchTouchEvent(event);
			return true;
		}
		
		return super.onTouchEvent(event);
	}
	**/
}
