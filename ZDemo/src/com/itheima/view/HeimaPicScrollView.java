package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

public class HeimaPicScrollView extends ViewGroup{
	private Context context;
	private GestureDetector detector; //手势识别的工具类
	private int mTouchSlop;
	private MyPageChangedListener pageChangedListener; //页面切换时回调监听类(主要用于改变对应的RadioButton的焦点)

	public HeimaPicScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initView();
	}
	
	/**
	 * 计算ViewGroup中每一个子view的大小
	 * 注: 如果该ViewGroup所有孩子中有ViewGroup,则需重写此方法,测量此孩子(ViewGroup)中所有的子view的大小
	 * 该案例中,下标i=2时,此ViewGroup获取到的孩子不是一个ImageView(属于View的范畴),而是一个LinearLayout(属于容器的范畴),它需要测量其中的Button,TextView控件的大小
	 * 若不重写此方法,在第三个界面中,只能看到一片主题灰的背景,没有任何控件显示
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			v.measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			//指定子view的位置,左,上,右,下,是指在viewGroup坐标系中的位置
			view.layout(0+i*getWidth(), 0, getWidth()+i*getWidth(), getHeight());	
		}
	}
	
	private void initView() {
		this.myScroller = new Scroller(context);
		//此变量表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件,如viewpager就是用这个距离来判断用户是否翻页
		this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		
		detector = new GestureDetector(context, new OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				return false;
			}
			
			@Override
			public void onShowPress(MotionEvent e) {
				
			}
			
			/**
			 * 响应手指在屏幕上的滑动事件
			 */
			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,float distanceY) {
				//注意: 向左滑,图片向左移动,distanceX为正值;向右滑,图片向右滑动,distanceX为负值
				//Log.i("test","distanceX===>"+distanceX);
				scrollBy((int) distanceX, 0);
				
				return false;
			}
			
			@Override
			public void onLongPress(MotionEvent e) {
				
			}
		
			/**
			 * 发生快速滑动时的回调
			 */
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
				isFling = true; //修改标志位
				
				if(velocityX>0 && currId>0){ // 快速向右滑动
					currId--;
				}else if(velocityX<0 && currId<getChildCount()-1){ // 快速向左滑动
					currId++;
				}
				moveToDest(currId);
				
				return false;
			}
			
			@Override
			public boolean onDown(MotionEvent e) {
				return false;
			}
		});
	}
	
	public MyPageChangedListener getPageChangedListener() {
		return pageChangedListener;
	}

	public void setPageChangedListener(MyPageChangedListener pageChangedListener) {
		this.pageChangedListener = pageChangedListener;
	}
	
	/**
	 * 页面改时时的监听接口(可以用于实现Activity中的RadioButton切换)
	 */
	public interface MyPageChangedListener{
		void moveToDest(int currid);
	}
	
	
	private int firstX = 0;
	private int firstY = 0;
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		boolean result = false;
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				detector.onTouchEvent(ev); //注:解决图片跳动问题
				
				firstX = (int) ev.getX();
				firstY = (int) ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				int distanceX = (int) Math.abs(ev.getX()-firstX); //手指水平方向滑动的距离
				int distanceY = (int) Math.abs(ev.getY()-firstY); //手指竖直方向滑动的距离
				if(distanceX>mTouchSlop && distanceX>distanceY){  //认定为水平滑动
					result = true;
				}else{ //认定为垂直滑动
					result = false;
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return result;
	}
	

	private Scroller myScroller;  //计算位移的工具类
	private boolean isFling;  //判断是否发生快速滑动
	private int downX = 0;  //手指按下的X坐标
	private int currId = 0; //显示在屏幕上的子View的下标(当前的ID值)
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);  //由GestureDetector这个工具类实例来帮忙解析手指触摸滑动事件
		
		//还需为down和up事件添加自己的事件解析,move事件已经通过GestureDetector工具类处理掉了
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = (int) event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if(!isFling){  // 在没有发生快速滑动的时候,才执行按位置判断currid
				int nextId = 0;
				if(event.getX()-downX > getWidth()/2){  // 手指向右滑动,超过屏幕的1/2     当前的currid-1
					nextId = currId-1;
				}else if(downX-event.getX() > getWidth()/2){ // 手指向左滑动,超过屏幕的1/2     当前的currid+1
					nextId = currId+1;
				}else{
					nextId = currId;
				}
				moveToDest(nextId);
			}
			isFling = false;  //一次滑动结束,重置标志位
			break;
		}
		
		return true;
	}
	
	@Override
	public void computeScroll() {
		if(myScroller.computeScrollOffset()){
			int currX = (int) myScroller.getCurrX();
			scrollTo(currX, 0);
			invalidate();
		}
	}
	
	/**
	 * 移动到指定的屏幕上
	 * @param nextId  屏幕 的下标
	 */
	public void moveToDest(int nextId) {
		currId = (nextId>=0)?nextId:0;  //确保 currId>=0
		currId = (nextId<=getChildCount()-1)?nextId:(getChildCount()-1);  //确保 currId<=getChildCount()-1
	
		if(pageChangedListener!=null){
			pageChangedListener.moveToDest(currId); //在此处回调Activity中写好的方法
		}
		
		int distance = currId*getWidth() - getScrollX();   // 最终的位置 - 现在的位置 = 要移动的距离
		myScroller.startScroll(getScrollX(),0,distance,0);  //起始Scroller坐标系坐标为(getScrollX(),0)
		
		invalidate();
	}
}
