package com.itheima.widget;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyAppWidgetService extends Service{
	private UpdateThread mUpdateThread; //周期性更新 widget 的线程
	private Context mContext;
	private int count=0;  //更新周期的计数
	
	@Override
	public void onCreate() {
		// 创建并开启线程UpdateThread
		mUpdateThread = new UpdateThread();
		mUpdateThread.start();
		mContext = this.getApplicationContext();
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		//中断线程，即结束线程。
        if (mUpdateThread != null) {
        	mUpdateThread.interrupt();
        }
		super.onDestroy();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/*
	 * 服务开始时，即调用startService()时，onStartCommand()被执行。
	 * onStartCommand() 这里的主要作用：
	 * (01) 将 appWidgetIds 添加到队列sAppWidgetIds中
	 * (02) 启动线程
	 * 
	 * 说明: 2.0 API level之后,实现onStart等同于重写onStartCommand并返回START_STICKY
	 * (1)START_STICKY: 如果service进程被kill掉，保留service的状态为开始状态，但不保留递送的intent对象。
	 * 随后系统会尝试重新创建service，由于服务状态为开始状态，所以创建服务后一定会调用onStartCommand(Intent,int,int)方法。
	 * 如果在此期间没有任何启动命令被传递到service，那么参数Intent将为null
	 * 
	 * (2)START_NOT_STICKY:"非粘性的",使用这个返回值时，如果在执行完onStartCommand后,服务被异常kill掉,系统不会自动重启该服务
	 * 
	 * (3)START_REDELIVER_INTENT: 重传Intent。使用这个返回值时，如果在执行完onStartCommand后,
	 * 服务被异常kill掉,系统会自动重启该服务，并将Intent的值传入
	 * 
	 * (4)START_STICKY_COMPATIBILITY: START_STICKY的兼容版本,但不保证服务被kill后一定能重启
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(MyAppWidgetConstants.TAG, "onStartCommand");		
		super.onStartCommand(intent, flags, startId);
		
	    return START_STICKY;
	}
	
	
	class UpdateThread extends Thread{
		@Override
        public void run() {
            try {
	            count = 0;
	            while (true) {
	            	Log.i(MyAppWidgetConstants.TAG, "run ... count:"+count);
	            	count++;
	        		Intent updateIntent=new Intent(MyAppWidgetConstants.ACTION_UPDATE);
	        		mContext.sendBroadcast(updateIntent);  //发送更新广播
	                Thread.sleep(MyAppWidgetConstants.UPDATE_TIME);
	            } 
            }catch (InterruptedException e) {
            	// 将 InterruptedException 定义在while循环之外，意味着抛出 InterruptedException 异常时，终止线程。
                e.printStackTrace();
            }
        }
	}

}
