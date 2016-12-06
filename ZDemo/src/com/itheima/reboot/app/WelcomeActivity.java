package com.itheima.reboot.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class WelcomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("ServiceTest","WelcomeActivity onCreate...");
		Intent i = new Intent(this,MyService.class);	
		startService(i);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		Log.d("ServiceTest","WelcomeActivity onStart...");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d("ServiceTest","WelcomeActivity onDestroy...");
		if(!AppUtils.isServiceRunning(this, MyService.class.getSimpleName())){
			Intent i = new Intent(this,MyService.class);	
			startService(i);
		}
	}
}
