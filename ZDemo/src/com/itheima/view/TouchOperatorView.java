package com.itheima.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class TouchOperatorView extends View{
	public static Paint myPaint;
	public static Point[] allPoints;
	private float posX=-1,posY=-1;
	private int operNumber;
	
	public TouchOperatorView(Context context) {
		super(context);
	}
	
	public void initAllPoint(int maxLength){
		allPoints = new Point[maxLength];
		
		for(int i=0;i<maxLength;i++){
			allPoints[i] = new Point(); 
			allPoints[i].posX = -1;
			allPoints[i].posY = -1;
		}
	}
	
	public void initPaint(){
		myPaint = new Paint();
		myPaint.setColor(Color.BLUE);
		myPaint.setStyle(Paint.Style.STROKE);
		myPaint.setStrokeWidth(1);
		myPaint.setAntiAlias(true);
	}
	
	public void drawPoint(float posX,float posY,int operNumber){
		this.posX = posX;
		this.posY = posY;
		this.operNumber = operNumber;
		this.invalidate();
	}
	
	public void drawLine(int operNumber){
		this.operNumber = operNumber;
		this.invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(this.posX == -1 && this.posY==-1){ //初始化view时默认会执行一次onDraw方法
			return;
		}
		if(operNumber == Point.DRAW_POINT){
			Point.drawPoint(canvas,this.posX, this.posY);
		}else if(operNumber == Point.DRAW_LINE){
			Point.drawLine(canvas);
		}
	}
	
	public static class Point{
		public float posX;
		public float posY;
		public static int DRAW_POINT = 1;
		public static int DRAW_LINE = 2;
		public static final int dp = 50;
		
		//画点
		public static void drawPoint(Canvas canvas,float posX,float posY){
			canvas.drawPoint(posX,posY,myPaint);
		}
				
		//两两画线
		public static void drawLine(Canvas canvas){
			for(int i=0;i<allPoints.length;i++){
				for(int j=0;j<allPoints.length;j++){
					if(i==j){
						continue;
					}
					if(allPoints[i].posX == -1 || allPoints[j].posX == -1){
						continue;
					}
					canvas.drawLine(allPoints[i].posX,allPoints[i].posY,allPoints[j].posX,allPoints[j].posY,myPaint);
				}
			}
		}
				
		//清除所有点
		public static void removeAllPoint(){
			for(int i=0;i<allPoints.length;i++){
				allPoints[i].posX = -1;
				allPoints[i].posY = -1;
			}
		}
		
		//是否拖拽
		public static int isDrag(float pressX,float pressY){
			for(int i=0;i<allPoints.length;i++){
				if(pressX<=allPoints[i].posX+dp && pressX>=allPoints[i].posX-dp 
						&& pressY<=allPoints[i].posY+dp && pressY>=allPoints[i].posY-dp){
					return i;
				}
			}
			return -1;
		} 
	}
}
