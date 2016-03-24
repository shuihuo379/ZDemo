package com.itheima.widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.itheima.demo.R;


public class MyAppWidgetProvider extends AppWidgetProvider{
	private static Set idsSet = new HashSet(); // 保存 widget的id的HashSet,每新建一个 widget,都会为该 widget分配一个 id
	private boolean DEBUG = false; 
    //启动MyAppWidgetService服务对应的action
    private final Intent MY_APP_SERVICE_INTENT = new Intent(MyAppWidgetConstants.MY_APP_SERVICE_INTENT);
	
	// 图片数组
    private static final int[] ARR_IMAGES = { 
    	R.drawable.sample_0, 
    	R.drawable.sample_1, 
    	R.drawable.sample_2, 
    	R.drawable.sample_3, 
    	R.drawable.sample_4, 
    	R.drawable.sample_5,
    	R.drawable.sample_6,
    	R.drawable.sample_7,
    };
    
    /**
     * 说明:当 widget更新时被执行
     * 当用户首次添加 widget时,onUpdate() 也会被调用，这样 widget就能进行必要的设置工作
     * 但是，如果定义了 widget的 configure属性(即android:config),
     * 那么当用户首次添加 widget时,onUpdate()不会被调用;之后更新 widget 时，onUpdate才会被调用
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
    		int[] appWidgetIds) {
    	super.onUpdate(context, appWidgetManager, appWidgetIds);
    	// 每次 widget 被创建时，对应的将widget的id添加到set中
		for (int appWidgetId : appWidgetIds) {
			idsSet.add(Integer.valueOf(appWidgetId));
		}
		printSet();
    }
    
    //当 widget被初次添加 或者 当 widget的大小被改变时,被调用 
    @Override
    public void onAppWidgetOptionsChanged(Context context,
    		AppWidgetManager appWidgetManager, int appWidgetId,
    		Bundle newOptions) {
    	Log.i(MyAppWidgetConstants.TAG, "onAppWidgetOptionsChanged...");
    	super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,newOptions);
    }
    
    //当widget被删除时调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    	Log.i(MyAppWidgetConstants.TAG, "onDeleted(): appWidgetIds.length="+appWidgetIds.length);
		//当 widget被删除时,对应的删除set中保存的widget的id
		for (int appWidgetId : appWidgetIds) {
			idsSet.remove(Integer.valueOf(appWidgetId));
		}
		printSet();
    	super.onDeleted(context, appWidgetIds);
    }
    
    //第一个widget被创建时调用(仅仅当我们创建第一个widget时才会启动服务，因为onEnabled()只会在第一个widget被创建时才执行。)    
    @Override
    public void onEnabled(Context context) {
    	Log.i(MyAppWidgetConstants.TAG, "onEnabled...");
    	// 在第一个 widget 被创建时，开启服务
    	context.startService(MY_APP_SERVICE_INTENT);
    	super.onEnabled(context);
    }
    
    //最后一个widget被删除时调用(仅仅当我们删除最后一个widget时才会终止服务，因为onDisabled()只会在最后一个widget被删除时才执行。)
    @Override
    public void onDisabled(Context context) {
    	Log.i(MyAppWidgetConstants.TAG, "onDisabled...");
    	// 在最后一个 widget被删除时，终止服务
    	context.stopService(MY_APP_SERVICE_INTENT);
    	super.onDisabled(context);
    }
    
    //接收广播的回调函数
    @Override
    public void onReceive(Context context, Intent intent) {
    	final String action = intent.getAction();
    	Log.i(MyAppWidgetConstants.TAG, "OnReceive:Action: " + action);
    	if (MyAppWidgetConstants.ACTION_UPDATE_ALL.equals(action)) {
    		//"更新"广播
    		updateAllAppWidgets(context,AppWidgetManager.getInstance(context),idsSet);
    	}
    	super.onReceive(context, intent);
    }
    
    private void updateAllAppWidgets(Context context,AppWidgetManager manager,Set set){
    	Iterator it = set.iterator();
    	while (it.hasNext()) {
    		int appID = ((Integer)it.next()).intValue();
    		int index = (new Random().nextInt(ARR_IMAGES.length)); // 随机获取一张图片的索引号
    		if (DEBUG) {
    			Log.i(MyAppWidgetConstants.TAG, "onUpdate(): index="+index);
    		}
    		// 获取 my_appwidget.xml对应的RemoteViews
    		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.my_appwidget);
    		remoteViews.setImageViewResource(R.id.iv_show,ARR_IMAGES[index]);  //设置显示图片
    		remoteViews.setOnClickPendingIntent(R.id.btn_show,getPendingIntent(context,MyAppWidgetConstants.Button_Show_Flag));
    		manager.updateAppWidget(appID,remoteViews);
    	} 
    }
    
    private PendingIntent getPendingIntent(Context context, int resourceId){
    	Intent intent = new Intent();
    	intent.setClass(context,MyAppWidgetProvider.class);
    	intent.addCategory(Intent.CATEGORY_ALTERNATIVE);
    	intent.setData(Uri.parse("custom:" + resourceId));
    	return PendingIntent.getBroadcast(context, 0, intent, 0);
    }
    
    
    //调试用：遍历set
    private void printSet() {
    	if (DEBUG) {
	    	int index = 0;
	    	int size = idsSet.size();
	    	Iterator it = idsSet.iterator();
	    	Log.i(MyAppWidgetConstants.TAG, "total:"+size);
	    	while (it.hasNext()) {
	    		Log.i(MyAppWidgetConstants.TAG, index + " -- " + ((Integer)it.next()).intValue());
	    	}
    	}
    }
}
