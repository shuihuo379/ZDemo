package com.itheima.slide.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.itheima.demo.R;

/**
 * 第一个滑动案例测试
 * 效果:view跟随手指的滑动而滑动
 * @author zhangming
 */
public class SlideOneTestActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.main_gray)); //设置根视图的背景色
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		SlideOneTestView slideOneTestView = new SlideOneTestView(this);
		LayoutParams params = new LayoutParams(getResources().getDimensionPixelSize(R.dimen.dp100),
				getResources().getDimensionPixelSize(R.dimen.dp100));
		slideOneTestView.setLayoutParams(params);
		slideOneTestView.setBackgroundColor(getResources().getColor(R.color.blue));
		
		ll.addView(slideOneTestView);
		setContentView(ll);
	}
}