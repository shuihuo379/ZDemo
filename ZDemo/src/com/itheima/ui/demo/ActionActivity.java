package com.itheima.ui.demo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

/***
 * @author zhangming
 * @summary Activiy
 */
public class ActionActivity extends Activity {
	private static int index = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Button button = new Button(this);
		button.setText("AlarmManager启动第" + index++ + "次");
		setContentView(button);
	}
}
