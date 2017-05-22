package com.itheima.view;

import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ListView;

import com.itheima.adapter.RadioButtonAdapter;
import com.itheima.demo.R;

/**
 * 包含单选框的弹出式对话框Dialog
 * 使用ListView和RadioButton构造
 * @author zhangming
 * @date 2017/05/22
 */
public class RadioButtonListDialog extends Dialog{
	private Activity mActivity;
	private RadioButtonAdapter mRadioBtnAdaper;
	
	public RadioButtonListDialog(Activity mActivity,final List<RadioButtonAdapter.RadioButtonItem> dataList) {
		this(mActivity,R.style.radio_button_dialog,dataList);
	}
	
	public RadioButtonListDialog(Activity mActivity,int theme,final List<RadioButtonAdapter.RadioButtonItem> dataList){
		super(mActivity, theme);
		this.mActivity = mActivity;
		initView(dataList);
	}

	private void initView(final List<RadioButtonAdapter.RadioButtonItem> dataList) {
		setContentView(R.layout.radio_list_dialog);
		
		//listview设置
		ListView radioButtonList = (ListView)findViewById(R.id.list_dialog);
		mRadioBtnAdaper = new RadioButtonAdapter(mActivity,dataList);
		radioButtonList.setAdapter(mRadioBtnAdaper);
		setListViewHeightBasedOnChildren(radioButtonList);
		
		Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        WindowManager wm = mActivity.getWindowManager();
        params.width = (int) (wm.getDefaultDisplay().getWidth() * 0.85); // 宽度设置为屏幕的85%
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
	}
	
	public void setListViewHeightBasedOnChildren(ListView listView) {  
	    Adapter listAdapter = listView.getAdapter();  
	    if (listAdapter == null) {  
	        return;  
	    }
	    int totalHeight = 0;  
	    int viewCount = listAdapter.getCount();  
	    for (int i = 0; i < viewCount; i++) {  
	        View listItem = listAdapter.getView(i, null, listView);  
	        listItem.measure(0, 0);  
	        totalHeight += listItem.getMeasuredHeight();  
	    }  
	    ViewGroup.LayoutParams params = listView.getLayoutParams();  
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount()-1)) + 10; //加10是为了适配自定义背景  
	    listView.setLayoutParams(params);  
	}
}
