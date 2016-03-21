package com.itheima.ui;

import com.itheima.bar.achartengine.PolygonalLineView;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public class PolygonalLineActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PolygonalLineView polygonalLineView = new PolygonalLineView(getApplicationContext());
		View view = polygonalLineView.getPolygonalLineView();
		setContentView(view);
	}
}
