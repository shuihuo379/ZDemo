package com.itheima.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.itheima.demo.R;

/**
 * 含有集合控件(诸如GridView,ListView,StackView等)的AppWidget的使用
 * @author zhangming
 */
public class GridWidgetProvider extends AppWidgetProvider{
	public static final String BT_REFRESH_ACTION = "com.itheima.widget.BT_REFRESH_ACTION";
	public static final String GRID_VIEW_ACTION = "com.itheima.widget.GRID_VIEW_ACTION";
	public static final String GRID_VIEW_EXTRA = "com.itheima.widget.GRID_VIEW_EXTRA";
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		for (int appId:appWidgetIds) {
			// 通过传入layout文件的id,获取AppWidget对应的视图实例
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.my_grid_appwidget);
            rv.setTextViewText(R.id.tv_head,"集合视图的AppWidget");  //设置TextView的内容
            
            // 设置响应"按钮(bt_refresh)"的intent
            Intent intent = new Intent();
            intent.setAction(BT_REFRESH_ACTION);
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
            rv.setOnClickPendingIntent(R.id.bt_refresh,pi);
            
            // 设置"GridView(gridview)"的adapter
            Intent serviceIntent = new Intent(context, GridWidgetService.class);
            rv.setRemoteAdapter(R.id.gridview, serviceIntent);
            
            // 说明:"集合控件(如GridView、ListView、StackView等)"中包含很多子元素,如GridView包含很多格子。
	        //     它们不能像普通的按钮一样通过 setOnClickPendingIntent 设置点击事件，必须先通过两步。
	        //     (01) 通过 setPendingIntentTemplate 设置 “intent模板”，这是比不可少的！
	        //     (02) 然后在处理该“集合控件”的RemoteViewsFactory类的getViewAt()接口中,通过 setOnClickFillInIntent 设置“集合控件的某一项的数据”
            Intent gridIntent = new Intent();
            gridIntent.setAction(GRID_VIEW_ACTION);
            gridIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,appId); //传递每个Widget的id到WidgetService
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, gridIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            rv.setPendingIntentTemplate(R.id.gridview,pendingIntent);  //设置intent模板
            appWidgetManager.updateAppWidget(appId,rv); // 调用集合管理器对集合进行更新
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
	
	/**
	 * 接收AppWidget的广播事件
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(action.equals(GRID_VIEW_ACTION)){  //接受"gridview"的点击事件的广播
			 int myGridAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID);
			 Log.i("test","myGridAppWidgetId===>"+myGridAppWidgetId);
			 int viewIndex = intent.getIntExtra(GRID_VIEW_EXTRA,0);
			 Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
			 
		}else if(action.equals(BT_REFRESH_ACTION)){  //接受"bt_refresh"的点击事件的广播
			 Toast.makeText(context,"Click Button",Toast.LENGTH_SHORT).show();
		}
		super.onReceive(context, intent);
	}
}
