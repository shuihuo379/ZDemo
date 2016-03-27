package com.itheima.qq.slideview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;

import com.itheima.demo.R;
import com.itheima.qq.slideview.SlideListViewAdapter.MessageItem;

public class SlideListActivity extends Activity{
	private SlideListView slideView;
	private List<MessageItem> msgList = new ArrayList<MessageItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slide_list_activity);
		init();
	}
	
	private void init(){
		slideView = (SlideListView)findViewById(R.id.slideListView);
		for(int i=0; i<20; i++){
			MessageItem item = new MessageItem("滑动条目" + i);
			msgList.add(item); 
		}
		SlideListViewAdapter mAdapter = new SlideListViewAdapter(this,msgList);
		slideView.setAdapter(mAdapter);
	}
}
