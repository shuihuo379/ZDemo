package com.itheima.ui;

import java.util.ArrayList;
import java.util.List;

import com.itheima.adapter.RadioButtonAdapter;
import com.itheima.adapter.RadioButtonAdapter.RadioButtonItem;
import com.itheima.demo.R;
import com.itheima.view.RadioButtonListDialog;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/**
 * 含单选框的对话框的测试程序
 * @author zhangming
 * @date 2017/05/22
 */
public class RadioButtonListActivity extends Activity{
	private RadioButtonListDialog dialog;
	private Button okBtn,cancelBtn;
	private List<RadioButtonItem> mList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.radio_button_list_activity);
		initData();
	}
	
	private void initData() {
		mList = new ArrayList<RadioButtonItem>();
		RadioButtonItem item01 = new RadioButtonItem();
		RadioButtonItem item02 = new RadioButtonItem();
		RadioButtonItem item03 = new RadioButtonItem();
		item01.content = "abcd";
		item02.content = "efgh";
		item03.content = "wxyz";
		mList.add(item01);
		mList.add(item02);
		mList.add(item03);
	}

	public void btnPopupWindow(View view){
		dialog = new RadioButtonListDialog(this,mList);
		okBtn = (Button) dialog.findViewById(R.id.btn_ok);
		cancelBtn = (Button) dialog.findViewById(R.id.btn_cancel);
		
		okBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
				String choiceItem = "";
				for(int i=0;i<mList.size();i++){
					if(mList.get(i).isChoice){ //ischoice=true
						choiceItem = mList.get(i).content;
						RadioButtonAdapter.lastPosition = i;
					}
				}
				if(!TextUtils.isEmpty(choiceItem)){
					Toast.makeText(RadioButtonListActivity.this, choiceItem, Toast.LENGTH_SHORT).show();
				}
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
}
