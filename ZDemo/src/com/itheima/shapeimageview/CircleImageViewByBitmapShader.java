package com.itheima.shapeimageview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.demo.R;

public class CircleImageViewByBitmapShader extends View{
	public CircleImageViewByBitmapShader(Context context, AttributeSet attrs,int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public CircleImageViewByBitmapShader(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public CircleImageViewByBitmapShader(Context context) {
		this(context,null);
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas); 
		
		Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.p6);
		//CLAMP拉伸模式,REPEAT重复模式,MIRROR镜像模式
		BitmapShader mBitmapShader = new BitmapShader(mBitmap,TileMode.CLAMP,TileMode.CLAMP); 
		Paint mPaint = new Paint();
		mPaint.setShader(mBitmapShader);
		canvas.drawCircle(300,300,200,mPaint);
	}
}
