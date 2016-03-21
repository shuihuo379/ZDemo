package com.itheima.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.util.StringUtil;

public class WordFallView extends View{
	public Canvas myCanvas;
	public Paint myPaint;
	private String content;  //输入框中输入的文本内容 
	
	private float[] initX;
	private float[] curY;
	private static float word_spacing = 40;
	
	public WordFallView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public WordFallView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WordFallView(Context context) {
		super(context);
	}
	
	public void init(String content,float[] initX,float[] curY){
		this.content = content;
		this.initX = initX;
		this.curY = curY;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		this.myCanvas = canvas;
		if(StringUtil.isEmpty(this.content)){
			return;
		}
		initPaint();
		//drawText(canvas,content, initX, initY, paint,90);
		for(int i=0;i<initX.length;i++){
			canvas.drawPosText(content,getWordPosition(initX[i],curY[i]),myPaint);
		}
	}
	
	private void initPaint(){
		myPaint = new Paint(); // 创建画笔  
		myPaint.setColor(Color.YELLOW); // 设置颜色
		myPaint.setAntiAlias(true); // 给Paint加上抗锯齿标志
		myPaint.setTextSize(36);
	}
	
	/**
	 * 设置每个文字在坐标点的位置
	 * @return 坐标点的集合
	 */
	private float[] getWordPosition(float curX,float curY){
		float[] pos = new float[content.length()*2];
		for(int i=0;i<content.length();i++){
			pos[i*2]=curX;
			pos[i*2+1]=curY + word_spacing*i;
		}
		return pos;
	}
	
	public void drawText(Canvas canvas,String text,float x,float y,Paint paint,float angle){
        if(angle != 0){
            canvas.rotate(angle, x, y); 
        }
        canvas.drawText(text, x, y, paint);
        if(angle != 0){
            canvas.rotate(-angle, x, y); 
        }
	}
}
