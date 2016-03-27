package com.itheima.qq.slideview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

public class SlideListView extends ListView {
	private SlideView mSlideView; //线性布局容器实例
	
	public SlideListView(Context context) {
		super(context);
	}

	public SlideListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SlideListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
        	case MotionEvent.ACTION_DOWN:
        		int x = (int) event.getX();
                int y = (int) event.getY();
                int position = pointToPosition(x, y);  //获取触摸的ListView的条目的位置
                
                if (position != INVALID_POSITION) {
                	SlideListViewAdapter.MessageItem data = (SlideListViewAdapter.MessageItem) getItemAtPosition(position);
                	mSlideView = data.slideView;
                }
        		break;
		}
		if (mSlideView != null) {
        	mSlideView.onRequireTouchEvent(event);
        }
		return super.onTouchEvent(event);
	}
}
