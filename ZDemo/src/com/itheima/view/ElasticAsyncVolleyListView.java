package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.ListView;

/**
 * 具有弹性的ListView
 * @author zhangming
 *
 */
public class ElasticAsyncVolleyListView extends ListView{
	private Context mContext;
	private static int mMaxOverDistance = 100; //ListView弹性值为100px
	
	public ElasticAsyncVolleyListView(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public ElasticAsyncVolleyListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public ElasticAsyncVolleyListView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}
	
	private void init() {
		DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
		float density = metrics.density;
		mMaxOverDistance = (int)(density * mMaxOverDistance);
	}

	//重写此方法,将maxOverScrollY值设置为mMaxOverDistance=50,maxOverScrollY的默认值为0
	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
			int scrollY, int scrollRangeX, int scrollRangeY,
			int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX,
				scrollRangeY, maxOverScrollX, mMaxOverDistance, isTouchEvent);
	}
}
