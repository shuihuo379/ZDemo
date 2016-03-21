package com.itheima.test;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.itheima.demo.R;
import com.itheima.model.Images;
import com.itheima.util.ImageCacheManager;

/**
 * 图片缓存测试类
 * @author zhangming
 */
public class ImageCacheActivity extends Activity implements View.OnTouchListener {
	private ScrollView scrollView;
	private LinearLayout ll_layout;
	private static int curPage = 1;
	private static final int pageSize = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPicView();
		setContentView(scrollView);
		scrollView.setOnTouchListener(this);
	}

	private void setPicView() {
		Window window = getWindow();
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 全屏状态
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 高亮显示
		window.setFormat(PixelFormat.TRANSLUCENT); // 设置窗口为半透明
		
		scrollView = new ScrollView(this);
		ll_layout = new LinearLayout(this);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		ll_layout.setLayoutParams(params);
		ll_layout.setOrientation(LinearLayout.VERTICAL);
		ll_layout.setBackgroundColor(getResources().getColor(R.color.main_gray));

		batchLoadData(curPage,pageSize); 	//分批加载的形式
		scrollView.addView(ll_layout);
	}

	/**
	 * 分批加载数据
	 */
	private void batchLoadData(int page, int pageSize) {
		int startIndex = (page - 1) * pageSize;
		int endIndex = page * pageSize;
		if(Images.imageThumbUrls.length < startIndex){
			return;
		}
		if(Images.imageThumbUrls.length >= startIndex && Images.imageThumbUrls.length < endIndex){
			endIndex = Images.imageThumbUrls.length;
		}
		for (int i = startIndex; i < endIndex; i++) {
			LinearLayout.LayoutParams ll_params = new LinearLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, 200);
			MyImageView iv = new MyImageView(this);  //使用重写的ImageView类,避免回收Bitmap时报异常,及时捕获异常
			ImageCacheManager.loadImage(Images.imageThumbUrls[i], iv,
					BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),
					BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher));
			ll_params.setMargins(0, 10, 0, 0);
			iv.setLayoutParams(ll_params);
			iv.setScaleType(ScaleType.CENTER_CROP);
			ll_layout.addView(iv, i);
		}
	}

	
	/**
	 * 1 mScrollView.getChildAt(0).getMeasuredHeight()表示:
	 *   ScrollView所占的高度.即ScrollView内容的高度.常常有一部分内容要滑动后才可见,
	 *   这部分的高度也包含在了 mScrollView.getChildAt(0).getMeasuredHeight()中
	 * 2 view.getScrollY表示: ScrollView顶端已经滑出去的高度
	 * 3 view.getHeight()表示: ScrollView的可见高度
	 */
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			int scrollY = view.getScrollY();
			int height = view.getHeight();
			int scrollViewMeasuredHeight = scrollView.getChildAt(0).getMeasuredHeight();
			if (scrollY == 0) {
				Log.i("test","滑动到了顶端 view.getScrollY()=" + scrollY);
			}
			if ((scrollY + height) == scrollViewMeasuredHeight) {
				Log.i("test","滑动到了底部 scrollY=" + scrollY + "滑动到了底部 height=" + height 
						+ "滑动到了底部 scrollViewMeasuredHeight="+ scrollViewMeasuredHeight);
				releaseBitmapResource(curPage,pageSize);  //回收上一页bitmap图像所占用的内存资源
				batchLoadData(++curPage, pageSize);
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	private void releaseBitmapResource(int page,int pageSize){
		int startIndex = (page - 1) * pageSize;
		int endIndex = page * pageSize;
		if(Images.imageThumbUrls.length < startIndex){
			return;
		}
		if(Images.imageThumbUrls.length >= startIndex && Images.imageThumbUrls.length < endIndex){
			endIndex = Images.imageThumbUrls.length;
		}
		for(int i=startIndex;i<endIndex;i++){
			ImageView image = (ImageView)ll_layout.getChildAt(i);
			BitmapDrawable bitmapDrawable = (BitmapDrawable)image.getDrawable();
			if(bitmapDrawable != null){
				Bitmap bitmap = bitmapDrawable.getBitmap();
				if(bitmap!=null && !bitmap.isRecycled()){
					Log.i("test","bitmap资源回收...");
					//bitmap.recycle();
					bitmap = null;
				}
				System.gc();
			}
		}
	}
	
	class MyImageView extends ImageView{
		public MyImageView(Context context){
			super(context);
		}
		
		public MyImageView(Context context,AttributeSet attrs){  
	        super(context, attrs);  
	    }  
		
	    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
		}

		@Override  
	    protected void onDraw(Canvas canvas) {  
	        try {  
	            super.onDraw(canvas);  
	        } catch (Exception e) {  //重写此方法,捕获一下异常  
	            Log.i("test","MyImageView===>onDraw(),Canvas: trying to use a recycled bitmap");  
	        }  
	    }  
	}
}
