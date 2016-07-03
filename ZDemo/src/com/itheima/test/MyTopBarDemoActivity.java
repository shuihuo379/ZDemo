package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;

import com.itheima.demo.R;
import com.itheima.util.T;
import com.itheima.view.MyTopBar;
import com.itheima.view.MyTopBar.TopBarClickListener;

/**
 * 自定义组合控件实现TopBar测试类
 * @author zhangming
 */
public class MyTopBarDemoActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_top_bar);
		
		MyTopBar topBar = (MyTopBar)findViewById(R.id.my_top_bar);
		topBar.setOnTopBarClickListener(new TopBarClickListener() {
			@Override
			public void leftClick() {
				T.show(getApplicationContext(),"点击了左边图片...");
			}
			
			@Override
			public void rightClick() {
				T.show(getApplicationContext(),"点击了右边图片...");
			}
		});
	}
}
