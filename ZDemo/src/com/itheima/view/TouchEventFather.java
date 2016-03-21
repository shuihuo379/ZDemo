package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.itheima.test.TouchEventUtil;

public class TouchEventFather extends LinearLayout{
	public TouchEventFather(Context context) {
		super(context);
	}

	public TouchEventFather(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Log.e("test", "TouchEventFather | dispatchTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		/**
		 * (1)若返回false,表示对获取到的事件停止向下传递，同时也不对该事件进行消费; 
		 * (2)若返回true，表示分发事件到 TouchEventFather 控件并由该控件的 dispatchTouchEvent 进行消费;
		 *    TouchEventActivity 不断的分发事件到 TouchEventFather 控件的 dispatchTouchEvent，
		 *    而 TouchEventFather 控件的 dispatchTouchEvent 也不断的将获取到的事件进行消费
		 * (3)默认为super.dispatchTouchEvent(ev),表示去调用本类中的onIterceptTouchEvent方法
		 */
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 默认返回false,若是此种状态,如果是点击本类的view,则调用本类onTouchEvent方法;
	 * 如果点击子类的view，则事件向下分发到子view的dispatchTouchEvent方法
	 * 若返回true,表示此事件在此视图中拦截,事件不会走到本类的OnTouchEvent方法,会向上传递给Activity类中
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		Log.i("test", "TouchEventFather | onInterceptTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		return super.onTouchEvent(ev);
	}

	/**
	 * 采用冒泡上传机制(注意:本方法的执行取决于触摸的view)
	 * 默认返回false,则本view不处理事件,那点击的是本view,则事件向上上传,
	 * 点击是子view,只要onInterceptTouchEvent返回值不是true,则事件向下分发到dispatchTouchEvent,
	 * 不会执行本类的onTouchEvent,毕竟触摸的控件是子view,而不是本view
	 * 如果返回true,只要点击的是本view,则此事件将会被消费
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		Log.d("test","TouchEventFather | onTouchEvent --> " + TouchEventUtil.getTouchAction(ev.getAction()));
		return super.onTouchEvent(ev);
	}
}
