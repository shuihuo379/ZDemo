package com.itheima.application;

import android.app.Application;
import cn.jpush.android.api.JPushInterface;

import com.baidu.mapapi.SDKInitializer;
import com.itheima.reboot.app.CrashHandler;

/**
 * Application类,提供全局上下文对象
 * @author zhangming
 */
public class MyApplication extends Application{
	public static MyApplication myApplication;
	
	@Override
    public void onCreate() {
        super.onCreate();
        myApplication = this; //初始化上下文 
        
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        SDKInitializer.initialize(getApplicationContext());
        
        CrashHandler handler = CrashHandler.getInstance();
		handler.init(this);
	}
	
	/**
	 * 用于产生上下文对象
	 * @return myApplication
	 */
	public static MyApplication newInstance(){
		return myApplication;  
	}
}
