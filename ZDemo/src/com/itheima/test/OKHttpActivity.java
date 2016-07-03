package com.itheima.test;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.itheima.demo.R;
import com.itheima.util.T;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Headers;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class OKHttpActivity extends Activity implements OnClickListener{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(initView());
	}
	
	private View initView(){
		LinearLayout ll = new LinearLayout(this);
		
		Button btn1 = new Button(this);
		Button btn2 = new Button(this);
		Button btn3 = new Button(this);
		
		btn1.setId(R.id.btn1);
		btn2.setId(R.id.btn2);
		btn3.setId(R.id.btn3);
		
		LinearLayout.LayoutParams params= new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		ll.setOrientation(LinearLayout.VERTICAL);
		btn1.setLayoutParams(params);
		btn2.setLayoutParams(params);
		btn3.setLayoutParams(params);
		
		btn1.setText("异步get方式");
		btn2.setText("post表单提交");
		btn3.setText("post上传文件");
		
		btn1.setPadding(0,10,0,10);
		btn2.setPadding(0,10,0,10);
		btn3.setPadding(0,10,0,10);
		
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		
		ll.addView(btn1);
		ll.addView(btn2);
		ll.addView(btn3);
		
		ll.setBackground(getResources().getDrawable(R.color.main_green));
		
		return ll;
	}
	
	/**
	 * 异步GET请求
	 */
	private void AsyncGet(){
		//Request request = new Request.Builder().url("http://apis.baidu.com/txapi/tiyu/tiyu?num=10&&page=1")
		//		.addHeader("apikey","81b7db6295121b4954d115d410cce7ae").build();
		Request request = new Request.Builder().url("http://121.40.157.200:12345/api/mobile/technician")
				.addHeader("Cookie","autoken=\"technician:FnSjze8vqfzrnbkncHFurA==\"").build();
		OkHttpClient client = new OkHttpClient();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onResponse(Response response) throws IOException {
				 if (!response.isSuccessful()) {
					 throw new IOException("Unexpected code " + response);
				 }
				 Headers headers = response.headers();
				 for(int i=0;i<headers.size();i++){
					Log.i("test",headers.name(i)+":"+headers.value(i));
				 }
				 Log.i("test",response.body().string());
			}
			
			@Override
			public void onFailure(Request request, IOException exception) {
				exception.printStackTrace();
			}
		});
	}
	
	/**
	 * POST方式提交表单
	 */
	private void postSubmitForm(){
		final OkHttpClient client = new OkHttpClient();
		RequestBody formBody = new FormEncodingBuilder().add("phone","15972219148")
				.add("password", "qazwsx123").add("verifySms","123456").build();
		final Request request = new Request.Builder().url("http://121.40.157.200:12345/api/mobile/technician/register").post(formBody).build();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try{
					Response response = client.newCall(request).execute();
					if (!response.isSuccessful()) {
						throw new IOException("Unexpected code " + response);
					}
					Log.i("test",response.body().string());
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	private void postuploadFile(){
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			File file = new File(Environment.getExternalStorageDirectory(),"mypic.PNG");
			MediaType MEDIA_TYPE_TEXT = MediaType.parse("image/png; charset=UTF-8");
			final OkHttpClient client = new OkHttpClient();
			final Request request = new Request.Builder().url("http://121.40.157.200:12345/api/mobile/technician/avatar")
					.post(RequestBody.create(MEDIA_TYPE_TEXT, file)).addHeader("Cookie","autoken=\"technician:FnSjze8vqfzrnbkncHFurA==\"").build();
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Response response = client.newCall(request).execute();
						if (!response.isSuccessful()) {
							throw new IOException("Unexpected code " + response);
						}
						Log.i("test",response.body().string());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}else{
			T.show(this,"没有检测到SD卡...");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn1:  //异步get方式
			Log.i("test","Async...");
			AsyncGet();
			break;
		case R.id.btn2: //post表单提交
			postSubmitForm();
			break;
		case R.id.btn3: //post上传文件
			postuploadFile();
			break;
		}
	}
}
