package com.itheima.reboot.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.itheima.demo.R;

public class RebootAppActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reboot_app_activity);
	}
	
	public void btnOnClickCrash(View view){
		String str  = null;
		str.charAt(0);
	}
}
