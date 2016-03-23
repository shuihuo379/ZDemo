package com.itheima.ui.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/***
 * @author zhangming
 * @summary 广播
 */
public class ActionBroadCast extends BroadcastReceiver {

	private static int index = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		Toast.makeText(context, "我是第" + index++ + "个广播", Toast.LENGTH_SHORT).show();
	}

}
