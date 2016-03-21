package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout{
	private int measureWidth;
	private int measureHeight;
	
	public MyFrameLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyFrameLayout(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		measureWidth = measureWidth(widthMeasureSpec);
		measureHeight = measureHeight(heightMeasureSpec); //测量view的宽高
		Log.i("test","measureWidth===>"+measureWidth+",measureHeight===>"+measureHeight); //此方法执行了三次
		measureChildren(measureWidth,measureHeight);  //计算自定义的ViewGroup中所有子控件的大小  
		setMeasuredDimension(measureWidth,measureHeight); //存储测量得到的宽度和高度值
	}

	/** 
     * 覆写onLayout，其目的是为了指定视图的显示位置，方法执行的前后顺序是在onMeasure之后，因为视图肯定是只有知道大小的情况下， 
     * 才能确定怎么摆放 
     */  
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childCount = getChildCount();
		for (int i = 0; i < childCount; i++) {  
            View childView = getChildAt(i);  
            //int measureHeight = childView.getMeasuredHeight();   
            //int measuredWidth = childView.getMeasuredWidth();  //获取在onMeasure中计算的视图尺寸  
            childView.layout(l, t, l+this.measureWidth, t+this.measureHeight); 
	    }  
	}
	
	private int measureWidth(int pWidthMeasureSpec) {  
        int result = 0;  
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式  
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸  
  
        switch (widthMode) {  
        case MeasureSpec.AT_MOST:  
        case MeasureSpec.EXACTLY:  
            result = widthSize;  
            break;  
        }  
        return result;  
	 } 
	
	 private int measureHeight(int pHeightMeasureSpec) {  
		 int result = 0;  
		 int heightMode = MeasureSpec.getMode(pHeightMeasureSpec); // 得到模式  
		 int heightSize = MeasureSpec.getSize(pHeightMeasureSpec); // 得到尺寸
		 
		 switch (heightMode) {
		 case MeasureSpec.AT_MOST:  
         case MeasureSpec.EXACTLY:  
            result = heightSize;  
            break;  
		 }
		 return result;
	 }
}
