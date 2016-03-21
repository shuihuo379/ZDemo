package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;

import com.itheima.demo.R;

public class ViewToBitmapActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_view_group);
		
		/**
		View view = getLayoutInflater().inflate(R.layout.user_set,null);
		view.setDrawingCacheEnabled(true); //打开图像缓存
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED)); //测量view的大小
		view.layout (0, 0, view.getMeasuredWidth(), view.getMeasuredHeight()); 
		setContentView(view);
		
		Bitmap bitmap = view.getDrawingCache();
		try {
			File file = new File("/storage/sdcard1/aa.png");
			OutputStream out = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG,80,out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}**/
	}
}
