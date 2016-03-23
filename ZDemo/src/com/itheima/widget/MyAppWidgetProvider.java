package com.itheima.widget;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.itheima.demo.R;


public class MyAppWidgetProvider extends AppWidgetProvider{
	private static final String TAG = "test";
	private static Set idsSet = new HashSet(); // 保存 widget的id的HashSet,每新建一个 widget,都会为该 widget分配一个 id
	private boolean DEBUG = false; 
    //启动ExampleAppWidgetService服务对应的action
    private final Intent MY_APP_SERVICE_INTENT = new Intent("android.appwidget.action.MY_APP_WIDGET_SERVICE");
	
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
    	Log.i(TAG, "onAppWidgetOptionsChanged...");
    	super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId,newOptions);
    }
    
    //当widget被删除时调用
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
    	Log.i(TAG, "onDeleted(): appWidgetIds.length="+appWidgetIds.length);
		//当 widget被删除时,对应的删除set中保存的widget的id
		for (int appWidgetId : appWidgetIds) {
			idsSet.remove(Integer.valueOf(appWidgetId));
		}
		printSet();
    	super.onDeleted(context, appWidgetIds);
    }
    
    //第一个widget被创建时调用  
    @Override
    public void onEnabled(Context context) {
    	Log.i(TAG, "onEnabled...");
    	// 在第一个 widget 被创建时，开启服务
    	context.startService(MY_APP_SERVICE_INTENT);
    	super.onEnabled(context);
    }
    
    //最后一个widget被删除时调用
    @Override
    public void onDisabled(Context context) {
    	Log.i(TAG, "onDisabled...");
    	// 在最后一个 widget被删除时，终止服务
    	context.stopService(MY_APP_SERVICE_INTENT);
    	super.onDisabled(context);
    }
    
    //接收广播的回调函数
    @Override
    public void onReceive(Context context, Intent intent) {
    	super.onReceive(context, intent);
    }
    
    
    //调试用：遍历set
    private void printSet() {
    	if (DEBUG) {
	    	int index = 0;
	    	int size = idsSet.size();
	    	Iterator it = idsSet.iterator();
	    	Log.d(TAG, "total:"+size);
	    	while (it.hasNext()) {
	    		Log.d(TAG, index + " -- " + ((Integer)it.next()).intValue());
	    	}
    	}
    }
}
