package com.itheima.qq.slideview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.itheima.demo.R;

/**
 * 封装滑动视图的线性布局容器 
 * @author zhangming
 */
public class SlideView extends LinearLayout {
	private Context mContext;
	private LinearLayout mViewContent;
	private Scroller mScroller;
	private OnSlideListener mOnSlideListener;
	
	private int mLastX = 0;
	private int mLastY = 0;  //记录上次手指抬起时的位置(即最后的移动位置)
	private int mHolderWidth = 120;  //删除标签的宽度,可动态调整 

	public SlideView(Context context) {
		super(context);
		initView();
	}

	public SlideView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	private void initView() {
		mContext = getContext();
		mScroller = new Scroller(mContext);  //初始化Scroller类实例 

		setOrientation(LinearLayout.HORIZONTAL);
		View.inflate(mContext, R.layout.slide_list_view_merge, this);
		mViewContent = (LinearLayout) findViewById(R.id.view_content);
		
		//对应的值转化为实际屏幕上的点值,也就是像素值(如果是TypedValue.COMPLEX_UNIT_DIP，则乘以显示密度density;而如果是TypedValue.COMPLEX_UNIT_SP，则乘以像素密度scaledDensity)
		mHolderWidth = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mHolderWidth,getResources().getDisplayMetrics()));
	}
	
	public void setButtonText(CharSequence text) {
	    ((TextView)findViewById(R.id.delete)).setText(text);
	}
	
	public void setContentView(View view) {
        mViewContent.addView(view);
    }
	
	public void shrinkZero() {
		if(getScrollX()!=0){
			 this.smoothScrollTo(0, 0);
		}
	}
	
	/**
	 * 缓慢滚动到指定的位置
	 * @param destX
	 * @param destY
	 */
	private void smoothScrollTo(int destX, int destY){
		int startX = getScrollX(); //开始位置
		int dx = destX - startX; //偏移量
		mScroller.startScroll(startX,destY, dx, 0, Math.abs(dx)*3);
		invalidate(); //这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
	}
	
	@Override
	public void computeScroll() {
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(),mScroller.getCurrY()); //参数为偏移量
			postInvalidate();
		}
	}
	
	public void onRequireTouchEvent(MotionEvent event) {
		 int x = (int) event.getX();
	     int y = (int) event.getY();
	     int scrollX = getScrollX();

        switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN: {
	            if (!mScroller.isFinished()) {
	                mScroller.abortAnimation();
	            }
	            if (mOnSlideListener != null) {
	                mOnSlideListener.onSlide(this,OnSlideListener.SLIDE_STATUS_START_SCROLL);
	            }
	            break;
	        }
	        case MotionEvent.ACTION_MOVE: { //此处会多次调用
	            int deltaX = x - mLastX;
	            int deltaY = y - mLastY;
	            if (Math.abs(deltaX) < Math.abs(deltaY) * 2) {
	                break;
	            }
	
	            int newScrollX = scrollX - deltaX;
	            if (deltaX != 0) {
	                if (newScrollX < 0) {
	                    newScrollX = 0;
	                } else if (newScrollX > mHolderWidth) {
	                    newScrollX = mHolderWidth;
	                }
	                this.scrollTo(newScrollX, 0); //移动的时候滑到指定的位置
	            }
	            break;
	        }
	        case MotionEvent.ACTION_UP: {
	            int newScrollX = 0;
	            if (scrollX - mHolderWidth * 0.75 > 0) { //规则:如果大于宽度(设定为120)的75%,则滑动到最大偏移值,否则滑到最初位置(dx=0)
	                newScrollX = mHolderWidth;
	            }
	            this.smoothScrollTo(newScrollX, 0); //手抬起的时候,执行动画效果
	            if (mOnSlideListener != null) {
	                mOnSlideListener.onSlide(this,newScrollX == 0 ? OnSlideListener.SLIDE_STATUS_OFF : OnSlideListener.SLIDE_STATUS_ON);
	            }
	            break;
	        }
        }
        mLastX = x;
        mLastY = y;
	}
	
	public interface OnSlideListener{
		 public static final int SLIDE_STATUS_OFF = 0;  //关状态,即删除图标未出现
	     public static final int SLIDE_STATUS_START_SCROLL = 1;  //手指触摸滑动状态
	     public static final int SLIDE_STATUS_ON = 2;  //开状态,即删除图标出现
	     
	    /**
         * @param view current SlideView
         * @param status SLIDE_STATUS_ON or SLIDE_STATUS_OFF
         */
	     public void onSlide(View view, int status);
	}
	
	public void setOnSlideListener(OnSlideListener onSlideListener) {
	     mOnSlideListener = onSlideListener;
	}
}
