package com.itheima.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.demo.R;

public class FiveStarView extends View{
	private MyPoint AP,BP,CP,DP,EP,FP,GP,HP,IP,JP;
	private static double defaultX = 100;
	private static double defaultY = 100;
	private static double defaultlRadius = 50;
	
	public FiveStarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initPoint(defaultlRadius,new MyPoint(defaultX,defaultY));
	}

	public FiveStarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.MyFiveStarView);  
		int radius = (int)ta.getDimension(R.styleable.MyFiveStarView_lRadius,(int)defaultlRadius);
		int centerX = ta.getInteger(R.styleable.MyFiveStarView_x,(int)defaultX);
		int centerY = ta.getInteger(R.styleable.MyFiveStarView_y,(int)defaultY);
		initPoint(radius,new MyPoint(centerX,centerY));
	}

	public FiveStarView(Context context) {
		super(context);
		initPoint(defaultlRadius,new MyPoint(defaultX,defaultY));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        Paint paint = new Paint(); // 创建画笔  
        paint.setColor(Color.YELLOW); // 设置颜色
        paint.setAntiAlias(true); // 给Paint加上抗锯齿标志
        //PaintFlagsDrawFilter filter = new PaintFlagsDrawFilter(0,Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
        
        Path path = new Path();
        path.moveTo((int)AP.x,(int)AP.y);
        path.lineTo((int)BP.x,(int)BP.y);
        path.lineTo((int)CP.x,(int)CP.y);
        path.lineTo((int)DP.x,(int)DP.y);
        path.lineTo((int)EP.x,(int)EP.y);
        path.lineTo((int)FP.x,(int)FP.y);
        path.lineTo((int)GP.x,(int)GP.y);
        path.lineTo((int)HP.x,(int)HP.y);
        path.lineTo((int)IP.x,(int)IP.y);
        path.lineTo((int)JP.x,(int)JP.y);
        path.close();
        
        canvas.drawPath(path, paint);
	}
	
	private void initPoint(double lRadius,MyPoint centerPoint){
		double mRadius = (Math.sin(Math.PI/10))/(Math.cos(Math.PI/5))*lRadius;
		
		AP = new MyPoint(centerPoint.x,centerPoint.y - lRadius);
		BP = new MyPoint(centerPoint.x + Math.sin(Math.PI/5)*mRadius, centerPoint.y - Math.cos(Math.PI/5)*mRadius);
		CP = new MyPoint(centerPoint.x + Math.cos(Math.PI/10)*lRadius, centerPoint.y - Math.sin(Math.PI/10)*lRadius);
		DP = new MyPoint(centerPoint.x + Math.cos(Math.PI/10)*mRadius, centerPoint.y + Math.sin(Math.PI/10)*mRadius);
		EP = new MyPoint(centerPoint.x + Math.sin(Math.PI/5)*lRadius, centerPoint.y + Math.cos(Math.PI/5)*lRadius);
		FP = new MyPoint(centerPoint.x, centerPoint.y + mRadius);
		GP = new MyPoint(centerPoint.x - Math.sin(Math.PI/5)*lRadius,centerPoint.y + Math.cos(Math.PI/5)*lRadius);
		HP = new MyPoint(centerPoint.x - Math.cos(Math.PI/10)*mRadius, centerPoint.y + Math.sin(Math.PI/10)*mRadius);
		IP = new MyPoint(centerPoint.x - Math.cos(Math.PI/10)*lRadius, centerPoint.y - Math.sin(Math.PI/10)*lRadius);
		JP = new MyPoint(centerPoint.x - Math.sin(Math.PI/5)*mRadius, centerPoint.y - Math.cos(Math.PI/5)*mRadius);
	}
	
	static class MyPoint{
		public double x;
		public double y;
		
		public MyPoint(double x,double y){
			this.x = x;
			this.y = y;
		}
	}
}
