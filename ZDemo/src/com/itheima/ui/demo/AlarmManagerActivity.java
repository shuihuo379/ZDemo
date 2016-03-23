package com.itheima.ui.demo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.itheima.demo.R;

/**
 * AlarmManager全局定时器的使用
 * @author zhangming
 */
public class AlarmManagerActivity extends Activity implements OnClickListener{
	private AlarmManager alarmManager;
	private Button btnActivity;
	private Button btnService;
	private Button btnBroadCast;
	private PendingIntent pendingIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_manager_activity);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		
		btnActivity= (Button) findViewById(R.id.btn1);
		btnService = (Button) findViewById(R.id.btn2);
		btnBroadCast = (Button) findViewById(R.id.btn3);
		
		btnActivity.setOnClickListener(this);
		btnService.setOnClickListener(this);
		btnBroadCast.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		long now = System.currentTimeMillis();
		switch (v.getId()) {
		case R.id.btn1:
			pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,
					new Intent(getApplicationContext(), ActionActivity.class),Intent.FLAG_ACTIVITY_NEW_TASK); 
			break;
		case R.id.btn2:
			pendingIntent = PendingIntent.getService(getApplicationContext(),0,
					new Intent(getApplicationContext(), ActionService.class),Intent.FLAG_ACTIVITY_NEW_TASK);
			break;
		case R.id.btn3:
			pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0, 
					new Intent(getApplicationContext(),ActionBroadCast.class),Intent.FLAG_ACTIVITY_NEW_TASK);
			break;
		}
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,now,3000,pendingIntent);
	}
	
	@Override
	protected void onDestroy() {
		if(alarmManager!=null){
			alarmManager.cancel(pendingIntent);
			alarmManager = null;
		}
		super.onDestroy();
	}
}
