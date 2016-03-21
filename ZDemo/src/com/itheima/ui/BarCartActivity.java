package com.itheima.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.itheima.bar.achartengine.BarChartView;
import com.itheima.demo.R;

public class BarCartActivity extends Activity{
	private FrameLayout fl_view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		BarChartView view = new BarChartView(getApplicationContext());
//		LinearLayout ll = new LinearLayout(getApplicationContext());
//		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//		ll.setLayoutParams(params);
//		ll.setBackgroundColor(Color.WHITE);
//		ll.addView(view.getBarChartView("日里程(km)",5000));
//		setContentView(ll);
		
		setContentView(R.layout.barchart_activity);
		fl_view = (FrameLayout) findViewById(R.id.fl_view);
		
		BarChartView view = new BarChartView(getApplicationContext());
		TextView tv = new TextView(getApplicationContext());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		params.setMargins(80,10,0,0);
		tv.setLayoutParams(params);
		tv.setText("日里程(km)");
		tv.setTextSize(15);
		tv.setTextColor(Color.GRAY);
		
		fl_view.addView(view.getBarChartView("日里程(km)",5500),0);
		fl_view.addView(tv,1);
	}
}
