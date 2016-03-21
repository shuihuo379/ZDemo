package com.itheima.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

public class MyPushReceiver extends BroadcastReceiver{
	private static final String TAG = "test";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
	    Log.d(TAG, "onReceive - " + intent.getAction());
	
	    if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
	    }else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
	    	Log.d(TAG,"收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
	        // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
	    } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
	    	 Log.d(TAG,"收到了通知");
	        // 在这里可以做些统计，或者做些其他工作
	    } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
	        System.out.println("用户点击打开了通知");
    	    String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
	        String content = bundle.getString(JPushInterface.EXTRA_ALERT);
	        Log.d(TAG,"通知信息,title: "+title+"content: "+content); 
	    } else {
	        Log.d(TAG, "Unhandled intent - " + intent.getAction());
	    }
	}
}
