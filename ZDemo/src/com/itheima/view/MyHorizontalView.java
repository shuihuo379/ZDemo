package com.itheima.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import com.itheima.demo.R;

/**
 * 水平滑动的容器
 * @author zhangming
 */
public class MyHorizontalView extends ViewGroup{
	private Context context;
	private Scroller mScroller;
	private int mTouchSlop; //用户滑动的最小距离
	private int downX;
	private int downY;
	private boolean isSlide = false;
	private boolean isChange = false;
	private int curScrollX = 0;
	private Oratation slideOratation;
	private int myHorizontalViewHeight; //ViewGroup的高度
	
	public MyHorizontalView(Context context) {
		this(context,null);
		this.context = context;
		//mScroller = new Scroller(context);
		mScroller = new Scroller(context,new DecelerateInterpolator(2.0f)); //可以设置平滑减速器
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		myHorizontalViewHeight = getResources().getDimensionPixelSize(R.dimen.dp150);
	}

	public MyHorizontalView(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}
	
	public MyHorizontalView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int measureWidth = measureWidth(widthMeasureSpec);
//		int measureHeight = measureHeight(heightMeasureSpec); //测量viewGroup的宽高(此处手机测试为:measureWidth=720dp,measureHeight=1230dp)
		int measureHeight = myHorizontalViewHeight; 
		Log.i("test","measureWidth===>"+measureWidth+",measureHeight===>"+measureHeight);  
		measureChildren(measureWidth,measureHeight); 
		setMeasuredDimension(measureWidth,measureHeight); //存储viewGroup的宽高,这里存储的高度为150dp
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int mTotalWidth = 0; 
		for(int i=0;i<getChildCount();i++){
			View childView = getChildAt(i);
			//int measuredWidth = childView.getMeasuredWidth();
			//int measureHeight = childView.getMeasuredHeight();
			int measuredWidth = getScreenWidthAndHeight((Activity)context)[0];
			int measureHeight = this.myHorizontalViewHeight;
			childView.layout(l+mTotalWidth, t, l+mTotalWidth+measuredWidth, t+measureHeight);
			mTotalWidth += measuredWidth;
		}
	}
	
	/**
	 * 获取窗口的宽高
	 * @return
	 */
	public int[] getScreenWidthAndHeight(Activity activity){
		 DisplayMetrics dm = new DisplayMetrics();    
	     activity.getWindowManager().getDefaultDisplay().getMetrics(dm); //取得窗口属性    
	     int screenWidth = dm.widthPixels;  //窗口宽度
	     int screenHeight = dm.heightPixels;  //窗口高度   
	     return new int[]{screenWidth,screenHeight};
	}
	
	public int measureWidth(int pWidthMeasureSpec) {  
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
	
	public int measureHeight(int pHeightMeasureSpec) {  
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
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 假如scroller滚动还没有结束，我们直接返回
			if (!mScroller.isFinished()) {
				return super.dispatchTouchEvent(event);
			}
			downX = (int)event.getX();
			downY = (int)event.getY();
			break;
		case MotionEvent.ACTION_MOVE:  
			final float dx = (int)(event.getX()-downX); //手指水平移动的距离
			final float dy = (int)(event.getY()-downY);
			if(Math.abs(dx) > mTouchSlop && Math.abs(dy) < mTouchSlop){
				isSlide = true;
			}
			break;
		case MotionEvent.ACTION_UP:
			break;
		}
		return super.dispatchTouchEvent(event);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(isSlide){
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_MOVE:
				final float scrollDistance = getScreenWidthAndHeight((Activity)context)[0];
				final float dx = (int)(event.getX()-downX); //手指水平移动的距离
				if(dx>0 && getScrollX()==0){ //第一张图片,且向右滑动
					return true;
				}
				if(dx<0 && getScrollX()==scrollDistance*(getChildCount()-1)){ //最后一张图片,且向左滑动
					return true;
				}
				if(Math.abs(dx) > scrollDistance/3){ //滑动的距离超过屏幕的三分之一
					isChange = true;
				}else{
					isChange = false;
				}
				slideOratation = (dx>0)?Oratation.RIGHT:Oratation.LEFT;
				smoothScrollTo(getScrollX()+(-1)*((int)dx),0);
				break;
			case MotionEvent.ACTION_UP:
				isSlide = false;
				break;
			}
		}
		return true;
	}
	
	private void smoothScrollTo(int destX,int destY){
		int scrollX = getScrollX(); //相对于母视图(view本身)的位置,初始一般重合,值为0,是个不断变化的元素
		int deltX = destX - scrollX;
		mScroller.startScroll(scrollX,0, deltX,0,1000); //仅仅是保存所有参数的初始值
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(),mScroller.getCurrY());
			postInvalidate();
			if(mScroller.isFinished()){
				final float scrollDistance = getScreenWidthAndHeight((Activity)context)[0];
				if(isChange){
					if(slideOratation == Oratation.LEFT){ //从右向左滑动
						curScrollX+=scrollDistance;
					}else if(slideOratation == Oratation.RIGHT){ //从左向右滑动
						curScrollX-=scrollDistance;
					}
				}
				scrollTo(curScrollX,0);
				Log.i("test","curScrollX===>"+curScrollX);
			}
		}
	}
	
	static enum Oratation{
		LEFT,RIGHT; //LEFT表示从右向左滑动,RIGHT表示从左向右滑动
	}
}
