package com.itheima.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Scroller;

import com.itheima.demo.R;

public class MyProgressWebView extends WebView{
	public ProgressBar progressBar;  //webView进度条
	private Scroller scroller;
	private OnContentChangeListener listener;
	
	public MyProgressWebView(Context context){
		this(context,null);
	}

	public MyProgressWebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		scroller = new Scroller(context);
		
		progressBar = new ProgressBar(context, null,android.R.attr.progressBarStyleHorizontal);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,context.getResources().getDimensionPixelSize(R.dimen.dp5));
		params.setMargins(0, context.getResources().getDimensionPixelSize(R.dimen.dp5),0,0);
		progressBar.setLayoutParams(params);
		addView(progressBar);
		
		setWebViewClient(new WebViewClient());
		
		setWebChromeClient(new WebChromeClient(){
			@Override
	        public boolean onJsAlert(WebView view, String url, String message,JsResult result) {
	            Log.e("test", "onJsAlert " + message);
	            result.confirm();
	            return super.onJsAlert(view, url, message, result);
	        }

	        @Override
	        public boolean onJsConfirm(WebView view, String url,String message, JsResult result) {
	            Log.e("test", "onJsConfirm " + message);
	            return super.onJsConfirm(view, url, message, result);
	        }

	        @Override
	        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
	            Log.e("test", "onJsPrompt " + url);
	            return super.onJsPrompt(view, url, message, defaultValue,result);
	        }
	        
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					progressBar.setVisibility(GONE);
				}else{
					if(progressBar.getVisibility() == GONE){
					   progressBar.setVisibility(VISIBLE);
					}
					progressBar.setProgress(newProgress);
					listener.setEditTextUrl();
				}
				super.onProgressChanged(view, newProgress);
			}
		});
	}
	
	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		//int dy = t-oldt;
		//smoothYScrollTo(0,(-1)*((int)dy)); //垂直平滑滚动
	}
	
	private void smoothYScrollTo(int destX,int destY){
		int scrollY = getScrollY(); //相对于母视图(view本身)的位置,初始一般重合,值为0,是个不断变化的元素
		int deltY = destY - scrollY;
		scroller.startScroll(0,scrollY,0,deltY,1000); //仅仅是保存所有参数的初始值
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		if(scroller.computeScrollOffset()){
			scrollTo(scroller.getCurrX(),scroller.getCurrY());
			postInvalidate();
		}
	}
	
	public void setOnContentChangeListener(OnContentChangeListener listener){
		this.listener = listener;
	}
	
	public interface OnContentChangeListener{
		public void setEditTextUrl();
	}
}
