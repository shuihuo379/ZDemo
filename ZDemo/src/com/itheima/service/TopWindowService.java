package com.itheima.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.itheima.demo.R;
import com.itheima.ui.FloatingWindowActivity;

/**
 * 悬浮窗显示模拟顶层状态栏服务
 * @author zhangming
 * @date 2016/12/06
 */
public class TopWindowService extends Service{
	public static final String OPERATION = "operation";
	public static final int OPERATION_SHOW = 100;
	public static final int OPERATION_HIDE = 101;

	private static final int HANDLE_CHECK_ACTIVITY = 200;

	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private LinearLayout floating_window;  // 悬浮窗体

	private List<String> homeList; // 桌面应用程序包名列表
	private ActivityManager mActivityManager;
	
	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		homeList = getHomes();
		createFloatView();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);

		int operation = intent.getIntExtra(OPERATION, OPERATION_SHOW);
		switch (operation)
		{
		case OPERATION_SHOW:
			if(mHandler.hasMessages(HANDLE_CHECK_ACTIVITY)){
				mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
			}
			mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
			break;
		case OPERATION_HIDE:
			if(mHandler.hasMessages(HANDLE_CHECK_ACTIVITY)){
				mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
			}
			break;
		}
	}

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case HANDLE_CHECK_ACTIVITY:
				if (isHome())
				{
					if (!isAdded)
					{
						wm.addView(floating_window, params);
						isAdded = true;
					}
				} else
				{
					if (isAdded)
					{
						wm.removeView(floating_window);
						isAdded = false;
					}
				}
				mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
				break;
			}
		}
	};

	/**
	 * 创建悬浮窗
	 */
	private void createFloatView()
	{
		View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.top_window,null);
		floating_window = (LinearLayout) view.findViewById(R.id.ll_floating_window);
		
		wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		params = new WindowManager.LayoutParams();

		// 设置window type
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		/*
		 * 如果设置为params.type = WindowManager.LayoutParams.TYPE_PHONE; 那么优先级会降低一些,
		 * 即拉下通知栏不可见
		 */
		params.format = PixelFormat.RGBA_8888; // 设置图片格式,效果为背景透明

		// 设置Window flag
		//params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
			//	| WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
		
		params.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN;
		/*
		 * 下面的flags属性的效果形同“锁定”。 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
		 * wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL |
		 * LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_NOT_TOUCHABLE;
		 */
		
		// 获取状态栏的高度
		int statusBarHeight = -1;
		int resourceId = getResources().getIdentifier("status_bar_height","dimen","android");
		if(resourceId>0){
			statusBarHeight = getResources().getDimensionPixelSize(resourceId);
		}
		Log.d("zhangming","statusBarHeight: "+statusBarHeight);  //statusBarHeight: 50px

		// 设置悬浮窗的长和宽
		DisplayMetrics  dm = new DisplayMetrics(); //定义DisplayMetrics 对象        
	    wm.getDefaultDisplay().getMetrics(dm); //取得窗口属性    
		params.width = dm.widthPixels;
		params.height = statusBarHeight*5; //250px
		params.x = 0;
		params.y = (-1)*((dm.heightPixels - statusBarHeight*3)/2+statusBarHeight); //窗口位置偏移量
		
		// 设置悬浮窗的Touch监听
		/**
		floating_window.setOnTouchListener(new OnTouchListener()
		{
			int lastX, lastY;
			int paramX, paramY;

			public boolean onTouch(View v, MotionEvent event)
			{
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
					lastX = (int) event.getRawX();
					lastY = (int) event.getRawY();
					paramX = params.x;
					paramY = params.y;
					break;
				case MotionEvent.ACTION_MOVE:
					int dx = (int) event.getRawX() - lastX;
					int dy = (int) event.getRawY() - lastY;
					params.x = paramX + dx;
					params.y = paramY + dy;
					// 更新悬浮窗位置
					wm.updateViewLayout(floating_window, params);
					break;
				}
				return true;
			}
		});
		**/
		
		floating_window.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				// 开启应用程序主页
				Log.d("zhangming","Open Floating WindowActivity...");
				Intent intent = new Intent(TopWindowService.this,FloatingWindowActivity.class); 
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
	
		wm.addView(floating_window, params);
		isAdded = true;
	}

	/**
	 * 获得属于桌面的应用的应用包名称
	 * @return 返回包含所有包名的字符串列表
	 */
	private List<String> getHomes()
	{
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		// 属性
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(
				intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo)
		{
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

	/**
	 * 判断当前界面是否是桌面
	 */
	public boolean isHome()
	{
		if (mActivityManager == null)
		{
			mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		}
		List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
		//return true;
		return homeList.contains(rti.get(0).topActivity.getPackageName());
	}
}
