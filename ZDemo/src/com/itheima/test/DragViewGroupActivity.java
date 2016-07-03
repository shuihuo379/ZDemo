package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.itheima.demo.R;
import com.itheima.view.DragViewGroup;
import com.itheima.view.MyButtonView;

public class DragViewGroupActivity extends Activity implements OnClickListener{
	private DragViewGroup rootView;
	private LinearLayout mMainView;
	private MyButtonView close_menu_btn,open_menu_btn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drag_view_group_activity);
		initView();
	}
	
	private void initView(){
		close_menu_btn = (MyButtonView)findViewById(R.id.close_menu_btn);
		open_menu_btn = (MyButtonView)findViewById(R.id.open_menu_btn);
		
		FrameLayout fl = (FrameLayout) getWindow().getDecorView().findViewById(android.R.id.content);
		rootView = (DragViewGroup)fl.getChildAt(0);
		mMainView = (LinearLayout) rootView.getChildAt(1);
		
		close_menu_btn.setOnClickListener(this);
		open_menu_btn.setOnClickListener(this);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(hasFocus){
			Log.i("test","close_menu_btn width===>"+close_menu_btn.getWidth()); //240px
			Log.i("test","close_menu_btn measureWidth===>"+close_menu_btn.getMeasuredWidth()); //240px(最大模式)
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_menu_btn:
			rootView.closeMenu();
			break;
		case R.id.open_menu_btn:
			if(mMainView.getLeft() == 0){
				rootView.openMenu();
			}
			break;
		}
	}
}
