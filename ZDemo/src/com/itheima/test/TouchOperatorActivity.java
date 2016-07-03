package com.itheima.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;

import com.itheima.view.TouchOperatorView;
import com.itheima.view.TouchOperatorView.Point;

/**
 * 基本的触摸操作
 * @author zhangming
 */
public class TouchOperatorActivity extends Activity{
	private TouchOperatorView view;
	private int curLength = 0;
	private int dragIndex = -1;
	private long lastDown = -1;
	private float lastClickX,lastClickY = -1;
	
	private static final long DOUBLE_TIME = 1000;  
	private static final int maxLength = 5;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		view = new TouchOperatorView(this);
		view.initAllPoint(maxLength);
		view.initPaint();
		view.setBackgroundColor(Color.rgb(204,232,207));
		setContentView(view);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float posX = event.getX();
		float posY = event.getY();
		
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:{
				dragIndex = -1;
				
				if(curLength < maxLength){
					view.allPoints[curLength].posX = posX;
					view.allPoints[curLength].posY = posY;
					view.drawPoint(posX, posY,Point.DRAW_POINT);
					if(curLength>0){
						view.drawLine(Point.DRAW_LINE);
					}
				}else{ 
					dragIndex = Point.isDrag(posX,posY); //拖拽时返回索引号大于等于0,否则为-1
					
					long nowDown = System.currentTimeMillis();    
					if(nowDown - this.lastDown <= DOUBLE_TIME && posX <= this.lastClickX+Point.dp && posX >= this.lastClickX-Point.dp
							&& posY <= this.lastClickY+Point.dp && posY >= this.lastClickY-Point.dp){ //双击清除所有点
						Point.removeAllPoint();
						for(int i=0;i<view.allPoints.length;i++){
							view.allPoints[i].posX = -1;
							view.allPoints[i].posY = -1; //重置初始位置
						}
						curLength = -1; 
						view.invalidate();
					}else{
						this.lastDown = nowDown;
						this.lastClickX = posX;
						this.lastClickY = posY;
					}
				}
				break;
			}
			case MotionEvent.ACTION_MOVE:{
				if(dragIndex != -1){ //拖动效果
					view.allPoints[dragIndex].posX = posX;
					view.allPoints[dragIndex].posY = posY;
					view.drawPoint(posX, posY,Point.DRAW_POINT);
					view.drawLine(Point.DRAW_LINE);
				}
				break;
			}
			case MotionEvent.ACTION_UP:{
				if(curLength < maxLength){
				   curLength++;
				}
				break;
			}
		}
		return super.onTouchEvent(event);
	}
}
