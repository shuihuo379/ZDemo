package com.itheima.net;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.itheima.util.CommonUtil;

public class NetTask implements Runnable{
	private Context context;
	private String strUrl;
	private Handler handler;
	private int what;
	
	public NetTask(Context context, String strUrl,Handler handler,int what) {
		this.context = context;
		this.strUrl = strUrl;
		this.handler = handler;
		this.what = what;
	}
	
	@Override
	public void run() {
		switch (what) {
		case HttpUtil.UPLOAD_FILE:
			if(CommonUtil.ExistSDCard()){
				String json = FileHttpClient.PostUploadByHttpClient(context,strUrl,new File("/storage/sdcard1","Vlog.xml"));
				Log.i("test","返回的json字符串: "+json);
			}else{
				Log.i("test","SD卡不存在");
			}
			break;
			
		case HttpUtil.DOWNLOAD_FILE:
			File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
			if(!dir.exists()){
				dir.mkdirs();
			}
			FileHttpClient.getDownloadByHttpClient(context,strUrl,"Vlog.xml",new File(dir,"Vlog_Copy.xml"));
			break;
		}
	}
}
