package com.itheima.qq.slideview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import com.itheima.demo.R;
import com.itheima.qq.slideview.SlideListViewAdapter.MessageItem;
import com.itheima.qq.slideview.SlideListViewAdapter.onRemoveItemListener;

public class SlideListActivity extends Activity{
	private SlideListView slideView;
	private SlideListViewAdapter mAdapter;
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
		mAdapter = new SlideListViewAdapter(this,msgList);
		slideView.setAdapter(mAdapter);
		
		mAdapter.setOnRemoveItemListener(new onRemoveItemListener() {
			@Override
			public void removeItem(int position) {
				Log.i("test","移除条目的所处List集合位置索引===>"+position);
				msgList.remove(position);
				mAdapter.notifyDataSetChanged();
			}
		});
	}
}
