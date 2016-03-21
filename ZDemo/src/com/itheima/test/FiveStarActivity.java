package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;

import com.itheima.demo.R;

/**
 * 自定义国旗测试类
 * @author zhangming
 */
public class FiveStarActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.star_activity);
		
		/**
		View view = new FiveStarView(this);
		view.setBackgroundColor(Color.WHITE);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
		view.setLayoutParams(params);
		setContentView(view);
		**/
	}
}
