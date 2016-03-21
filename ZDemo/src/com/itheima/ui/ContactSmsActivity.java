package com.itheima.ui;

import com.itheima.test.FiveStarActivity;
import com.itheima.test.ViewToBitmapActivity;
import com.itheima.util.ContactSmsUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class ContactSmsActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ContactSmsUtil.getPhoneContacts(this);
		
		//Intent intent = new Intent(this,FiveStarActivity.class);
		//startActivity(intent);
		//Log.i("test","one===>"+this.getTaskId());
	}
}
