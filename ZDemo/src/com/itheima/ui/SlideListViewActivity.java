package com.itheima.ui;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.itheima.adapter.SlideListAdapter;
import com.itheima.demo.R;
import com.itheima.view.SlideListView;

/**
 * 仿QQ消息列表的效果,可左右滑动的ListView案例,点击按钮删除该条目
 * @author zhangming
 */
public class SlideListViewActivity extends Activity{
	private SlideListView slideListView;
	private List<String> dataSourceList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slide_list_view_activity);
		init();
	}
	
	private void init(){
		slideListView = (SlideListView)findViewById(R.id.slideListView);
		for(int i=0; i<20; i++){
			dataSourceList.add("滑动条目" + i); 
		}
		SlideListAdapter mAdapter = new SlideListAdapter(this,dataSourceList);
		slideListView.setAdapter(mAdapter);
		
		slideListView.setOnScrollListener(new OnScrollListener() {
		    /** 
		     * ListView的状态改变时触发 
		     * @param view 
		     * @param scrollState 
		     */  
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: //空闲状态
					break;
				case OnScrollListener.SCROLL_STATE_FLING: //滑动状态
					resetInitStatus();  
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: //触摸后滚动
					resetInitStatus();  //恢复初始状态,滚动到(0,0)
					break;
				}
			}
			
			 /** 
		     * 正在滚动 
		     * firstVisibleItem第一个Item的位置 
		     * visibleItemCount 可见的Item的数量 
		     * totalItemCount item的总数 
		     */  
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}
	
	private void resetInitStatus(){
		int beginIndex = slideListView.getFirstVisiblePosition();
		int endIndex = slideListView.getLastVisiblePosition();
		int visibleLength = endIndex-beginIndex;
		
		for(int i=0;i<visibleLength;i++){
			View itemView = slideListView.getChildAt(i);
			if(itemView.getScrollX()!=0){
				itemView.scrollTo(0, 0);
			}
		}
	}
}
