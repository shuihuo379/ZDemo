package com.itheima.net;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.itheima.demo.R;
import com.itheima.util.CommonUtil;

/**
 * 测试HTTP封装的方法
 * @author zhangming
 * @date 2016/02/05
 */
public class NetMainActivity extends Activity implements OnClickListener{
	private Button post_upload_file_btn,get_download_file_btn;
	private Context context;
	
	private static final String BASE_URL = "http://192.168.1.101:8080/MyWebProject/net/";
	private static final String UPLOAD_URL = BASE_URL+"uploadFile.action";
	private static final String DOWNLOAD_URL = BASE_URL+"downloadFile.action";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_main_activity);
		initView();
		setListener();
	}
	
	private void initView() {
		context = this;
		post_upload_file_btn = (Button) findViewById(R.id.post_upload_file_btn);
		get_download_file_btn = (Button) findViewById(R.id.get_download_file_btn);
	}
	
	private void setListener() {
		post_upload_file_btn.setOnClickListener(this);
		get_download_file_btn.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.post_upload_file_btn:
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(CommonUtil.ExistSDCard()){
						String json = FileHttpClient.PostUploadByHttpClient(getApplicationContext(),UPLOAD_URL,new File("/storage/sdcard1","Vlog.xml"));
						Log.i("test","返回的json字符串: "+json);
					}
				}
			}).start();
			break;
		case R.id.get_download_file_btn:
			new Thread(new Runnable() {
				@Override
				public void run() {
					File dir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
					if(!dir.exists()){
						dir.mkdirs();
					}
					try {
						File outFile = new File(dir,"Vlog_Copy.xml");
						boolean isSuccess = outFile.createNewFile();
						FileHttpClient.getDownloadByHttpClient(getApplicationContext(), DOWNLOAD_URL, "Vlog.xml", outFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			break;
		}
	}
}
