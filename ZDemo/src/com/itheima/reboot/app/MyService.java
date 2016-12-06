package com.itheima.reboot.app;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service{
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("ServiceTest","onCreate...");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("ServiceTest","onDestroy...");
		stopSelf();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("ServiceTest","onStartCommand...");
		if(AppUtils.isActivityRunning(this,RebootAppActivity.class.getSimpleName())){
			Log.d("ServiceTest","RebootAppActivity is running...");
		}else{
			Log.d("ServiceTest","RebootAppActivity is stop,restart MainActivity page...");
			Intent i = new Intent(this,RebootAppActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(i);
		}
		return START_STICKY;
	}
}
