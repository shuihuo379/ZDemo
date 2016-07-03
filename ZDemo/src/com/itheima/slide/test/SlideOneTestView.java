package com.itheima.slide.test;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

public class SlideOneTestView extends View{
	private int lastX,lastY;
	
	public SlideOneTestView(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//记录触摸点的坐标
			lastX = x;
			lastY = y; 
			break;
		case MotionEvent.ACTION_MOVE:
			int offsetX = x-lastX;
			int offsetY = y-lastY;
			//在当前的left,top,right,bottom的基础上加上偏移量,可以使用以下两种方式,这里使用第二种方式
			//layout(getLeft()+offsetX,getTop()+offsetY,getRight()+offsetX,getBottom()+offsetY);
			
			offsetLeftAndRight(offsetX);
			offsetTopAndBottom(offsetY);
			
			lastX = x;
			lastY = y;
			break;
		}
		return true;
	}
}
