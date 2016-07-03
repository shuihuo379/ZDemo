package com.itheima.test;

import com.itheima.demo.R;
import com.itheima.view.MyHuanXinMenu;

import android.app.Activity;
import android.os.Bundle;

/**
 * 环形菜单测试类
 * @author zhangming
 */
public class HuanXinMenuActivity extends Activity{
	public static MyHuanXinMenu oneSingleInstanceMenu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_huanxin_menu_activity);
	}
}
