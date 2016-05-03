package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
		
		//获取TextView控件,并监听点击事件
		ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        LinearLayout rl = (LinearLayout) viewGroup.getChildAt(0);
        TextView tv_content =(TextView) rl.getChildAt(1);
        final String content = tv_content.getText().toString().trim();
        
        tv_content.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), content, 0).show();
				return;
			}
		});
	}
	
	private View initView(){
		LinearLayout ll = new LinearLayout(this);
		LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(ll_params);
		ll.setBackgroundColor(getResources().getColor(R.color.main_gray));
		ll.setOrientation(LinearLayout.VERTICAL);
		
		//控件1
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
		
		//控件2
		TextView tv = new TextView(this);
		LinearLayout.LayoutParams _params = new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(R.dimen.dp50), getResources().getDimensionPixelSize(R.dimen.dp50));
		params.setMargins(0,getResources().getDimensionPixelSize(R.dimen.dp50),0,0);
		tv.setLayoutParams(_params);
		tv.setBackgroundColor(getResources().getColor(R.color.main_bg_color));
		tv.setText("kokoko");
		tv.setTextSize(15);
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(getResources().getColor(R.color.black));
		
		ll.addView(slideView);
		ll.addView(tv);
		
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
