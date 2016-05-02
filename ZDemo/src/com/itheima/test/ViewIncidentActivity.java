package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.itheima.demo.R;
import com.itheima.view.SlideIncidentView;

/**
 * view的滑动事件处理Demo案例
 * @author zhangming
 */
public class ViewIncidentActivity extends Activity{
	private SlideIncidentView slideView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(initView());
	}
	
	private View initView(){
		LinearLayout ll = new LinearLayout(this);
		LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(ll_params);
		ll.setBackgroundColor(getResources().getColor(R.color.main_gray));
		
		slideView = new SlideIncidentView(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(R.dimen.dp260), getResources().getDimensionPixelSize(R.dimen.dp260));
		params.setMargins(getResources().getDimensionPixelSize(R.dimen.dp50),
				getResources().getDimensionPixelSize(R.dimen.dp50),0,0);
		slideView.setLayoutParams(params);
		slideView.setBackgroundColor(getResources().getColor(R.color.main_green));
		slideView.setText("hahahahaha");
		slideView.setTextSize(15);
		slideView.setGravity(Gravity.CENTER);
		slideView.setTextColor(getResources().getColor(R.color.black));
	
		slideView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				int width = slideView.getRight()-slideView.getLeft();
				int height = slideView.getBottom()-slideView.getTop();
				//Log.i("test","view width:"+width+" view height:"+height);
			}
		});
		
		ll.addView(slideView);
		
		return ll;
	}
	
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		/** 注:两种方式均可获取
		if(hasFocus){
			int width = slideView.getRight()-slideView.getLeft();
			int height = slideView.getBottom()-slideView.getTop();
			Log.i("test","width:"+width+" height:"+height);
		}**/
		super.onWindowFocusChanged(hasFocus);
	}
}
