package com.itheima.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.Volley;
import com.itheima.application.MyApplication;

/**
 * 图片缓存管理类,获取ImageLoader对象
 * @author zhangming
 * @date 2016/03/15
 */
public class ImageCacheManager {
	private static String TAG = ImageCacheManager.class.getSimpleName();
	// 获取图片缓存类对象
	private static ImageCache mImageCache = new ImageCacheUtil();
	// 获取ImageLoader对象
	public static ImageLoader mImageLoader = new ImageLoader(
			VolleyRequestQueueManager.mRequestQueue, mImageCache);

	/**
	 * 获取ImageListener
	 * @param view
	 * @param defaultImage
	 * @param errorImage
	 * @return
	 */
	public static ImageListener getImageListener(final ImageView view,
			final Bitmap defaultImage, final Bitmap errorImage) {
		return new ImageListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// 回调失败
				if (errorImage != null) {
					Bitmap bitmap = BitmapUtil.compressBitmap(errorImage);
					view.setImageBitmap(bitmap);
				}
			}
			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				// 回调成功
				if (response.getBitmap() != null) {
					try{
						Bitmap bitmap = BitmapUtil.compressBitmap(response.getBitmap());
						view.setImageBitmap(bitmap);
						Log.i("test","bitmap===>width: "+bitmap.getWidth()+",height:"+bitmap.getHeight()+",size:"+bitmap.getByteCount()/1024+"KB");
					}catch(OutOfMemoryError e){
						e.printStackTrace();
					}
				} else if (defaultImage != null) {
					try{
						Bitmap bitmap = BitmapUtil.compressBitmap(defaultImage);
						view.setImageBitmap(bitmap);
						Log.i("test","bitmap===>width: "+bitmap.getWidth()+",height:"+bitmap.getHeight()+",size:"+bitmap.getByteCount()/1024+"KB");
					}catch(OutOfMemoryError e){
						e.printStackTrace();
					}
				}
			}
		};

	}

	/**
	 * 提供给外部调用方法
	 * @param url
	 * @param view
	 * @param defaultImage
	 * @param errorImage
	 */
	public static void loadImage(String url, ImageView view, Bitmap defaultImage, Bitmap errorImage) {
		mImageLoader.get(url, ImageCacheManager.getImageListener(view,defaultImage,errorImage), 0, 0);
	}

	/**
	 * 提供给外部调用方法
	 * @param url
	 * @param view
	 * @param defaultImage
	 * @param errorImage
	 */
	public static void loadImage(String url, ImageView view,
			Bitmap defaultImage, Bitmap errorImage, int maxWidth, int maxHeight) {
		mImageLoader.get(url, ImageCacheManager.getImageListener(view, defaultImage, errorImage), maxWidth, maxHeight);
	}
	
	/**
	 * 请求队列处理类
	 * 获取RequestQueue对象
	 */
	public static class VolleyRequestQueueManager {
	    //获取请求队列类
	    public static RequestQueue mRequestQueue = Volley.newRequestQueue(MyApplication.newInstance());
	    //添加任务进任务队列
	    public static void addRequest(Request<?> request, Object tag) {
	        if (tag != null) {
	            request.setTag(tag);
	        }
	        mRequestQueue.add(request);
	    }
	    //取消任务
	    public static void cancelRequest(Object tag){
	        mRequestQueue.cancelAll(tag);
	    }
	} 
}
