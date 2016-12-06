package com.itheima.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.itheima.demo.R;
import com.itheima.service.TopWindowService;

/**
 * 悬浮窗显示模拟顶层状态栏测试
 * Home键,任务键监听
 * @author zhangming
 * @date 2016/12/06
 */
public class FloatingWindowActivity extends Activity implements OnClickListener{
	private Button btn_show;
	private Button btn_hide;
	private MyReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.floating_window_activity);
		
		receiver = new MyReceiver();  
	    IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);  
	    registerReceiver(receiver, homeFilter); 

		btn_show = (Button) findViewById(R.id.btn_show);
		btn_hide = (Button) findViewById(R.id.btn_hide);
		btn_show.setOnClickListener(this);
		btn_hide.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);  
		super.onDestroy();
	}

	public void onClick(View v){
		switch (v.getId()){
		case R.id.btn_show:
			Intent show = new Intent(this, TopWindowService.class);
			show.putExtra(TopWindowService.OPERATION,TopWindowService.OPERATION_SHOW);
			startService(show);
			break;
		case R.id.btn_hide:
			Intent hide = new Intent(this, TopWindowService.class);
			hide.putExtra(TopWindowService.OPERATION,TopWindowService.OPERATION_HIDE);
			startService(hide);
			break;
		}
	}
	
	private class MyReceiver extends BroadcastReceiver {  
        private final String SYSTEM_DIALOG_REASON_KEY = "reason";  
        private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";  
        private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action = intent.getAction();  
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {  
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);  
                if (reason == null){ 
                    return;  
                }
                //Home键  
                if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {  
                    Toast.makeText(getApplicationContext(), "按了Home键", Toast.LENGTH_SHORT).show();  
                }  
                //最近任务列表键  
                if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {  
                    Toast.makeText(getApplicationContext(), "按了最近任务列表", Toast.LENGTH_SHORT).show();  
                }  
           }  
        }
    }  
}
