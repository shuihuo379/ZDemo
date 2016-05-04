package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itheima.demo.R;
import com.itheima.view.MyHorizontalView;

public class SlideConflictDemoActivity extends Activity{
	private int[] ImageIds = new int[]{R.drawable.p1,R.drawable.p2,R.drawable.p3,
			R.drawable.p4,R.drawable.p5};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(initView());
	}
	
	private View initView(){
		LinearLayout ll = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
		ll.setLayoutParams(params);
		ll.setBackgroundColor(getResources().getColor(R.color.main_gray));
		
		MyHorizontalView horizontalView = new MyHorizontalView(this);
		for(int i=0;i<ImageIds.length;i++){
			ImageView iv = new ImageView(this);
			iv.setBackgroundResource(ImageIds[i]);
			horizontalView.addView(iv);
		}
		ll.addView(horizontalView);
		
		return ll;
	}
}
