package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/**
 * 自定义Button控件的测量方式
 * @author zhangming
 */
public class MyButtonView extends Button{
	public MyButtonView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyButtonView(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measuredWidth(widthMeasureSpec), measuredHeight(heightMeasureSpec));
	}
	
	private int measuredWidth(int widthMeasureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(widthMeasureSpec);
		int specSize = MeasureSpec.getSize(widthMeasureSpec);
		
		if(specMode == MeasureSpec.EXACTLY){
			//一般是设置了明确的值或者是MATCH_PARENT
			Log.i("test","精确模式...");
			result = specSize;
		}else if(specMode == MeasureSpec.AT_MOST){
			//子视图的大小最多是specSize中指定的值,也就是说不建议子视图的大小超过specSize中给定的值(一般为WARP_CONTENT)
			Log.i("test","最大模式...");
			result = 240;
			result = Math.min(result, specSize);
		}else if(specMode == MeasureSpec.UNSPECIFIED){
			//表示子布局想要多大就多大,很少使用
			Log.i("test","随意模式...");
		}
		return result;
	}
	
	private int measuredHeight(int heightMeasureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(heightMeasureSpec);
		int specSize = MeasureSpec.getSize(heightMeasureSpec);
		
		if(specMode == MeasureSpec.EXACTLY){
			Log.i("test","精确模式...");
			result = specSize;
		}else if(specMode == MeasureSpec.AT_MOST){
			Log.i("test","最大模式...");
			result = 100;
			result = Math.min(result, specSize);
		}else if(specMode == MeasureSpec.UNSPECIFIED){
			Log.i("test","随意模式...");
		}
		return result;
	}
}
