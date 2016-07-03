package com.itheima.test;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.itheima.demo.R;
import com.itheima.util.T;
import com.itheima.view.MyProgressWebView;
import com.itheima.view.MyProgressWebView.OnContentChangeListener;

public class WebViewActivity extends Activity implements OnClickListener{
	private EditText et_address;
	private Button btn_go;
	private MyProgressWebView myWebView;
	private ImageView iv_back,iv_forward,iv_flush;
	
	private static final String TAG = "test";
	private static final String APP_CACHE_DIRNAME = "/webcache"; // web缓存目录
	private static final String WEBVIEW_CACHE_DIRNAME = "/webviewCache"; // webview缓存目录
	private static final String IP_ADDRESS = "http://www.hao123.com";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview_activity);
		init();
		setListener();
		myWebView.loadUrl(IP_ADDRESS); //加载初始界面
	}
	
	private void init(){
		et_address = (EditText) findViewById(R.id.et_address);
		btn_go = (Button)findViewById(R.id.btn_go);
		iv_back = (ImageView)findViewById(R.id.iv_back);
		iv_forward = (ImageView)findViewById(R.id.iv_forward);
		iv_flush = (ImageView)findViewById(R.id.iv_flush);
		myWebView = (MyProgressWebView)findViewById(R.id.myWebView);
		
		WebSettings settings = myWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(false); //支持缩放,但不显示缩放控制按钮
		settings.setRenderPriority(RenderPriority.HIGH);
		
		//设置加载进来的页面自适应手机屏幕 
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
//		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); //有时候会让你的页面布局走样,只能显示中间那一块,超出屏幕的部分都不能显示
		
		// 建议缓存策略为，判断是否有网络，有的话，使用LOAD_DEFAULT,无网络时，使用LOAD_CACHE_ELSE_NETWORK
		settings.setCacheMode(WebSettings.LOAD_DEFAULT);
		settings.setDomStorageEnabled(true); //开启 DOM storage API功能 
		settings.setDatabaseEnabled(true); //开启database storage API功能
		settings.setAppCacheEnabled(true); //开启Application Cache功能
		
		String cacheDirPath = getFilesDir().getAbsolutePath() + APP_CACHE_DIRNAME;
		Log.i(TAG, "cachePath===>"+cacheDirPath);
		settings.setAppCachePath(cacheDirPath);
		settings.setDatabasePath(cacheDirPath); // 设置数据库缓存路径
		Log.i(TAG, "databasePath===>"+myWebView.getSettings().getDatabasePath());
	}
	
	private void setListener(){
		btn_go.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		iv_forward.setOnClickListener(this);
		iv_flush.setOnClickListener(this);
		myWebView.setOnContentChangeListener(new OnContentChangeListener() {
			@Override
			public void setEditTextUrl() {
				//点击链接跳转页面时,EditText中填入新的链接标题
				et_address.setText(myWebView.getTitle());
			}
		});
	}
	
	public void clearWebViewCache() {
	    // 清理WebView缓存数据库
	    try {
	        deleteDatabase("webview.db");
	        deleteDatabase("webviewCache.db");
	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    // WebView缓存文件
	    File appCacheDir = new File(getFilesDir().getAbsolutePath()+ APP_CACHE_DIRNAME);
	    Log.e(TAG, "appCacheDir path=" + appCacheDir.getAbsolutePath());

	    File webviewCacheDir = new File(getCacheDir().getAbsolutePath() + WEBVIEW_CACHE_DIRNAME);
	    Log.e(TAG, "appCacheDir path=" + webviewCacheDir.getAbsolutePath());

	    // 删除webView缓存目录
	    if (webviewCacheDir.exists()) {
	        deleteFile(webviewCacheDir);
	    }
	    // 删除webView缓存，缓存目录
	    if (appCacheDir.exists()) {
	        deleteFile(appCacheDir);
	    }
	}

	public void deleteFile(File file) {
	    Log.i(TAG, "delete file path=" + file.getAbsolutePath());
	    if (file.exists()) {
	        if (file.isFile()) {
	            file.delete();
	        } else if (file.isDirectory()) {
	            File files[] = file.listFiles();
	            for (int i = 0; i < files.length; i++) {
	                deleteFile(files[i]);
	            }
	        }
	        file.delete();
	    } else {
	        Log.e(TAG, "delete file no exists " + file.getAbsolutePath());
	    }
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && myWebView.canGoBack()){
			myWebView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_go:
			String address = et_address.getText().toString().trim();
			if(TextUtils.isEmpty(address)){
				address = IP_ADDRESS;
			}
			myWebView.loadUrl(address);
			break;
		case R.id.iv_back:
			if(myWebView.canGoBack()){
				myWebView.goBack();
			}else{
				T.show(this,"不能后退了");
			}
			break;
		case R.id.iv_forward:
			if(myWebView.canGoForward()){
				myWebView.goForward();
			}else{
				T.show(this,"不能前进了");
			}
			break;
		case R.id.iv_flush:
			myWebView.reload();
			break;
		}
	}
}
