package com.itheima.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.itheima.demo.R;

public class WebServiceDemoActivity extends Activity{
	private String BASE＿URI = "http://www.webxml.com.cn/WebServices/WeatherWebService.asmx";
	private String WSDL_URI = BASE＿URI + "?WSDL"; //wsdl 的uri  
	private String namespace = "http://WebXml.com.cn/"; //namespace  
	private String methodName = "getWeatherbyCityName"; //要调用的方法名称
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏状态
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 高亮显示
		window.setFormat(PixelFormat.TRANSLUCENT); // 设置窗口为半透明
		
		ScrollView scrollView = new ScrollView(this);
		LinearLayout ll_layout = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll_layout.setLayoutParams(params);
		ll_layout.setOrientation(LinearLayout.VERTICAL);
		ll_layout.setBackgroundColor(getResources().getColor(R.color.main_green));
		
		Button btn = new Button(this);
		btn.setWidth(300);
		btn.setHeight(100);
		ll_layout.addView(btn);
		scrollView.addView(ll_layout);
		setContentView(scrollView);
		
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//getWhetherInfo();
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							String result = getWhetherInfoByHttp(BASE＿URI+"/"+methodName);
						}catch (Exception e) {
							e.printStackTrace();
						}
					}
				}).start();
			
			}
		});
	}
	
	private String getWhetherInfoByHttp(String url) throws Exception{
		HttpClient httpClient = new DefaultHttpClient();
		
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		NameValuePair bv_city = new BasicNameValuePair("theCityName","武汉");
		params.add(bv_city);
		HttpEntity entity = new UrlEncodedFormEntity(params,"UTF-8");  //构造表单实体
		
		HttpPost httpPost = new HttpPost(url);  //构造post方式,参数为url
		httpPost.setEntity(entity);  //设置请求的实体内容
		
		HttpResponse response = httpClient.execute(httpPost); //执行
		if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
			//TODO
			InputStream in = response.getEntity().getContent();
		}
		return "";
	}
	
	private void getWhetherInfo(){
		new Thread(new Runnable() {
			@Override
			public void run() {
			  try {
				    SoapObject request = new SoapObject(namespace,methodName);
					request.addProperty("theCityName","武汉");
					
				  	//创建SoapSerializationEnvelope对象，同时指定soap版本号(之前在wsdl中看到的)  
					SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapSerializationEnvelope.VER12); //soap12
					envelope.bodyOut = request;  //由于是发送请求，所以是设置bodyOut  
				    envelope.dotNet = true;  //由于是.net开发的webservice,所以这里要设置为true
				    envelope.setOutputSoapObject(request);
				    
			    	HttpTransportSE ht = new HttpTransportSE(WSDL_URI);
					ht.call(namespace+methodName, envelope);
					SoapObject response = (SoapObject) envelope.getResponse();//获得返回对象
					if(response!=null){
						Log.i("test",response.toString());
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
