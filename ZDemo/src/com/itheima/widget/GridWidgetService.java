package com.itheima.widget;

import java.util.ArrayList;
import java.util.HashMap;

import com.itheima.demo.R;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * 自定义appwidget服务,继承RemoteViewsService
 * 一般,当App Widget 中包含"GridView、ListView、StackView等"集合视图时,
 * 才需要使用RemoteViewsService来进行更新、管理(集合视图是指GridView、ListView、StackView等包含子元素的视图)
 * @author zhangming
 */
public class GridWidgetService extends RemoteViewsService{
	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new GridRemoteViewsFactory(this,intent);
	}
	
	/**
	 *RemoteViewsFactory是RemoteViewsService中的一个接口。RemoteViewsFactory提供了一系列的方法管理“集合视图”中的每一项。例如：
	 *RemoteViews getViewAt(int position)
     *通过getViewAt()来获取“集合视图”中的第position项的视图，视图是以RemoteViews的对象返回的。
     *通过getCount()来获取“集合视图”中所有子项的总数。
	 */
	private class GridRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory{
		private Context mContext;
		private int mAppWidgetId;
		
		private String IMAGE_ITEM = "imgage_item";
	    private String TEXT_ITEM = "text_item";
	    private ArrayList<HashMap<String, Object>> data ;
		
	    private String[] arrText = new String[]{ 
	            "Picture 1", "Picture 2", "Picture 3", 
	            "Picture 4", "Picture 5", "Picture 6",
	            "Picture 7", "Picture 8", "Picture 9"
	            };
	    private int[] arrImages=new int[]{
	    		R.drawable.p1, R.drawable.p2, R.drawable.p3, 
	            R.drawable.p4, R.drawable.p5, R.drawable.p6, 
	            R.drawable.p7, R.drawable.p8, R.drawable.p9
	           	};
		
		/**
		 * 构造GridRemoteViewsFactory
		 */
		public GridRemoteViewsFactory(Context context, Intent intent) {
			mContext = context;
			mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID); //defaultValue=0
			Log.i("test","myGridAppWidgetId===>"+mAppWidgetId);
		}
		
		 /**
	     * 初始化GridView的数据
	     */
	    private void initGridViewData() {
	    	data = new ArrayList<HashMap<String, Object>>();
	        
	        for (int i=0; i<9; i++) {
	            HashMap<String, Object> map = new HashMap<String, Object>(); 
	            map.put(IMAGE_ITEM, arrImages[i]);
	            map.put(TEXT_ITEM, arrText[i]);
	            data.add(map);
	        }
	    }
		
		
		@Override
		public void onCreate() {
			initGridViewData();  //初始化“集合视图”中的数据
		}

		@Override
		public void onDestroy() {
			data.clear();
		}

		@Override
		public int getCount() {
			return data.size();  // 返回“集合视图”中的数据的总数
		}

		@Override
		public RemoteViews getViewAt(int position) {
			// 获取每个条目(my_grid_appwidget_item.xml)对应的RemoteViews
			RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.my_grid_appwidget_item);
			
			// 设置第position位的“视图”的数据
			HashMap<String, Object> map = (HashMap<String, Object>) data.get(position);
			rv.setImageViewResource(R.id.itemImage, ((Integer)map.get(IMAGE_ITEM)).intValue());
			rv.setTextViewText(R.id.itemText, (String)map.get(TEXT_ITEM));
			
			// 设置第position位的“视图”对应的响应事件
			Intent fillIntent = new Intent();
			fillIntent.putExtra(GridWidgetProvider.GRID_VIEW_EXTRA,position);
			rv.setOnClickFillInIntent(R.id.itemLayout, fillIntent);
			
			return rv;  //返回RemoteViews的实例
		}

		@Override
		public RemoteViews getLoadingView() {
			return null;
		}
		
		@Override
		public void onDataSetChanged() {
			
		}

		@Override
		public int getViewTypeCount() {
			return 1;  //只有一类 GridView
		}

		@Override
		public long getItemId(int position) {
			return position;  //返回当前项在“集合视图”中的位置
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}
}
