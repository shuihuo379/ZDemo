package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

/**
 * 概念理解:
 1，在方法onMeasure中调用setMeasuredDimension方法
   void android.view.View.setMeasuredDimension(int measuredWidth, int measuredHeight)
        在onMeasure(int, int)中，必须调用setMeasuredDimension(int width, int height)来存储测量得到的宽度和高度值，如果没有这么去做会触发异常IllegalStateException。

 2，在方法onMeasure中调用孩子的measure方法
 void android.view.View.measure(int widthMeasureSpec, int heightMeasureSpec)
 这个方法用来测量出view的大小。父view使用width参数和height参数来提供constraint信息。实际上，view的测量工作在onMeasure(int, int)方法中完成。因此，只有onMeasure(int, int)方法可以且必须被重写。参数widthMeasureSpec提供view的水平空间的规格说明，参数heightMeasureSpec提供view的垂直空间的规格说明。

 3，解析onMeasure(int, int)方法
 void android.view.View.onMeasure(int widthMeasureSpec, int heightMeasureSpec)
 测量view及其内容来确定view的宽度和高度。这个方法在measure(int, int)中被调用，必须被重写来精确和有效的测量view的内容。

 在重写这个方法时，必须调用setMeasuredDimension(int, int)来存储测量得到的宽度和高度值。执行失败会触发一个IllegalStateException异常。调用父view的onMeasure(int, int)是合法有效的用法。
view的基本测量数据默认取其背景尺寸，除非允许更大的尺寸。子view必须重写onMeasure(int, int)来提供其内容更加准确的测量数值。如果被重写，子类确保测量的height和width至少是view的最小高度和宽度(通过getSuggestedMinimumHeight()和getSuggestedMinimumWidth()获取)。

 4，解析onLayout(boolean, int, int, int, int)方法
 void android.view.ViewGroup.onLayout(boolean changed, int l, int t, int r, int b)
 调用场景：在view给其孩子设置尺寸和位置时被调用。子view，包括孩子在内，必须重写onLayout(boolean, int, int, int, int)方法，并且调用各自的layout(int, int, int, int)方法。
 参数说明：参数changed表示view有新的尺寸或位置；参数l表示相对于父view的Left位置；参数t表示相对于父view的Top位置；参数r表示相对于父view的Right位置；参数b表示相对于父view的Bottom位置。.

 5，解析View.MeasureSpec类
 android.view.View.MeasureSpec
 MeasureSpec对象，封装了layout规格说明，并且从父view传递给子view。每个MeasureSpec对象代表了width或height的规格。
 MeasureSpec对象包含一个size和一个mode，其中mode可以取以下三个数值之一：
 UNSPECIFIED，1073741824 [0x40000000]，未加规定的，表示没有给子view添加任何规定。
 EXACTLY，0 [0x0]，精确的，表示父view为子view确定精确的尺寸。
 AT_MOST，-2147483648 [0x80000000]，子view可以在指定的尺寸内尽量大。
 **/


/**
 * 自定义ViewGroup:学习onMeasure和onLayout方法的使用
 * @author zhangming
 */
public class MyViewGroup extends ViewGroup{
	public MyViewGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyViewGroup(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measureWidth = measureWidth(widthMeasureSpec);
		int measureHeight = measureHeight(heightMeasureSpec); //测量view的宽高
		Log.i("test","measureWidth===>"+measureWidth+",measureHeight===>"+measureHeight);  //此方法执行了三次(此处真机设置全屏显示的主题,测出宽度为720px,高度为1280px)
		measureChildren(measureWidth,measureHeight);  //计算自定义的ViewGroup中所有子控件的大小  
		setMeasuredDimension(measureWidth,measureHeight); //存储测量得到的宽度和高度值  (720px=match_parent,600px = 300*2) 注：此处为自定义容器MyViewGroup的宽度和高度
	}

	/** 
     * 覆写onLayout，其目的是为了指定视图的显示位置，方法执行的前后顺序是在onMeasure之后，因为视图肯定是只有知道大小的情况下， 
     * 才能确定怎么摆放 
     */  
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int mTotalHeight = 0;  // 记录总高度 
		int childCount = getChildCount();
		
		for (int i = 0; i < childCount; i++) {  
            View childView = getChildAt(i);  
            // 获取在onMeasure中计算的视图尺寸  
            int measureHeight = childView.getMeasuredHeight();  //200px,400px
            int measuredWidth = childView.getMeasuredWidth();   //400px,200px(测试机屏幕密度=2)
            childView.layout(l, t+mTotalHeight, l+measuredWidth, t+mTotalHeight + measureHeight); //l=50,t=100
            mTotalHeight += measureHeight;  
	    }  
	}
	
	private int measureWidth(int pWidthMeasureSpec) {  
        int result = 0;  
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式  
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸  
  
        switch (widthMode) {  
        /** 
         * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY, 
         * MeasureSpec.AT_MOST。 
         *  
         * MeasureSpec.EXACTLY是精确尺寸， 
         * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid 
         * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。 
         *  
         * MeasureSpec.AT_MOST是最大尺寸， 
         * 当控件的layout_width或layout_height指定为WRAP_CONTENT时 
         * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可 
         * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。 
         *  
         * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView， 
         * 通过measure方法传入的模式。 
         */  
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
