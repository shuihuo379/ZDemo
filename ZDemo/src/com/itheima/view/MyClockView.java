package com.itheima.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * 绘制时钟,掌握canvas绘图机制中常用的API
 * @author zhangming
 */
public class MyClockView extends View{
	private Context context;
	private int screenWidth,screenHeight;
	
	public MyClockView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	public MyClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public MyClockView(Context context) {
		super(context);
		this.context = context;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		getScreenWidthAndHeight((Activity)context);
		
		//创建并初始化画笔
		Paint paintCircle = new Paint();
		paintCircle.setStyle(Paint.Style.STROKE);
		paintCircle.setAntiAlias(true);
		paintCircle.setStrokeWidth(5);
		canvas.drawCircle(screenWidth/2,screenHeight/2,screenWidth/2,paintCircle);
		
		//画刻度,并标明刻度值即时间值(通过画布的旋转来实现)
		Paint paintDegree = new Paint();
		paintCircle.setStrokeWidth(3);
		for(int i=12;i<=24;i++){
			int j = i%13;
			if(j==0){
				continue;
			}
			if(j==6 || j==12){
				paintDegree.setStrokeWidth(5); //画时钟刻度
				paintDegree.setTextSize(30);
				canvas.drawLine(screenWidth/2,screenHeight/2-screenWidth/2,screenWidth/2,screenHeight/2-screenWidth/2+60,paintDegree);
				String degreeValue = String.valueOf(j);
				canvas.drawText(degreeValue,screenWidth/2-paintDegree.measureText(degreeValue)/2,screenHeight/2-screenWidth/2+90, paintDegree);
			}else{
				paintDegree.setStrokeWidth(3);
				paintDegree.setTextSize(15);
				canvas.drawLine(screenWidth/2,screenHeight/2-screenWidth/2,screenWidth/2,screenHeight/2-screenWidth/2+30,paintDegree);
				String degreeValue = String.valueOf(j);
				canvas.drawText(degreeValue,screenWidth/2-paintDegree.measureText(degreeValue)/2,screenHeight/2-screenWidth/2+60, paintDegree);
			}
			canvas.rotate(30,screenWidth/2, screenHeight/2);
		}
		
		for(int i=0;i<72;i++){
			if(i%6 != 0){
				paintDegree.setStrokeWidth(2); //画分钟刻度
				canvas.drawLine(screenWidth/2,screenHeight/2-screenWidth/2,screenWidth/2,screenHeight/2-screenWidth/2+15,paintDegree);
			}
			canvas.rotate(5, screenWidth/2, screenHeight/2);
		}
		
		//画指针
		Paint paintHour = new Paint(); //时针
		paintHour.setStrokeWidth(20);
		Paint paintMinute = new Paint(); //分针
		paintMinute.setStrokeWidth(10);
		canvas.save();  //保存画布,将之前的所有已绘制的图像保存起来
		canvas.translate(screenWidth/2,screenHeight/2);  //调用canvas平移函数,可以用来平移坐标原点,将原点坐标调整为圆心所在位置
		canvas.drawLine(0, 0, 100,100, paintHour);
		canvas.drawLine(0, 0, 100,200, paintMinute);
		canvas.restore();
	}
	
	/**
	 * 获取窗口的宽高
	 * @return
	 */
	private void getScreenWidthAndHeight(Activity activity){
		 DisplayMetrics dm = new DisplayMetrics();    
	     activity.getWindowManager().getDefaultDisplay().getMetrics(dm); //取得窗口属性,值保存在变量dm中    
	     screenWidth = dm.widthPixels;  //窗口宽度
	     screenHeight = dm.heightPixels;  //窗口高度   
	}
}
