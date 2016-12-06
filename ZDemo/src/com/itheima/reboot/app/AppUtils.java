package com.itheima.reboot.app;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;

public class AppUtils {
	public static boolean isActivityRunning(Context mContext,String activityClassName){  
	    ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);  
	    List<RunningTaskInfo> info = activityManager.getRunningTasks(1);  
	    if(info != null && info.size() > 0){  
	        ComponentName component = info.get(0).topActivity;  
	        if(activityClassName.equals(component.getClassName())){  
	            return true;  
	        }  
	    }  
	    return false;  
	}
	
	public static boolean isServiceRunning(Context mContext,String className) {  
        boolean isRunning = false;  
        ActivityManager activityManager = (ActivityManager)  
        mContext.getSystemService(Context.ACTIVITY_SERVICE);   
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(30);  
  
        if (!(serviceList.size()>0)) {  
            return false;  
        }  
  
        for (int i=0; i<serviceList.size(); i++) {  
            if (serviceList.get(i).service.getClassName().equals(className) == true) {  
                isRunning = true;  
                break;  
            }  
        }  
        return isRunning;  
	}  
}
