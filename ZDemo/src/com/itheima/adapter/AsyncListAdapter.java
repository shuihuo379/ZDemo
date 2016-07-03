package com.itheima.adapter;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.itheima.demo.R;
import com.itheima.model.WeatherInfo;
import com.itheima.util.DiskLruCache;
import com.itheima.util.DiskLruCache.Snapshot;
import com.itheima.util.StringUtil;

public class AsyncListAdapter extends BaseAdapter{
	private String[] imageList;
	private Context context;
	private RequestQueue queue;
	private ImageLoader imageLoader;
	private Map<Integer,WeatherInfo> weatherInfoMap;
	
	public AsyncListAdapter(Context context,String[] imageList,Map<Integer,WeatherInfo> weatherInfoMap){
		this.context = context;
		this.imageList = imageList;
		this.weatherInfoMap = weatherInfoMap;
		queue = Volley.newRequestQueue(context);
		imageLoader = new ImageLoader(queue,new BitmapCache());
	}
	
	@Override
	public int getCount() {
		return imageList.length;
	}

	@Override
	public Object getItem(int position) {
		return imageList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.async_volley_listview_item,null);
			holder.imgView = (NetworkImageView) convertView.findViewById(R.id.net_image);
			holder.tv_city = (TextView) convertView.findViewById(R.id.tv_city);
			holder.tv_weather_info = (TextView) convertView.findViewById(R.id.tv_weather_info);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		final String netImgUrl = imageList[position];
		if(StringUtil.isNotEmpty(netImgUrl)){
			holder.imgView.setDefaultImageResId(R.drawable.ic_launcher);
			holder.imgView.setErrorImageResId(R.drawable.ic_launcher);
			holder.imgView.setImageUrl(netImgUrl, imageLoader);  //异步加载图片
		}
		if(weatherInfoMap!=null){
			WeatherInfo weatherInfo = weatherInfoMap.get(position);
			if(weatherInfo!=null){
				holder.tv_city.setText(weatherInfo.getCity());
				holder.tv_weather_info.setText(weatherInfo.getTemp2()+"~"+weatherInfo.getTemp1());
			}
		}
		
		return convertView;
	}
	
	class ViewHolder{
		NetworkImageView imgView;
		TextView tv_city;
		TextView tv_weather_info;
	}
	
	class BitmapCache implements ImageCache{
		private LruCache<String,Bitmap> mCache;
		
		public BitmapCache(){
			int maxSize = 4 * 1024 * 1024;
			mCache = new LruCache<String, Bitmap>(maxSize){
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getByteCount();
				}
			};
		}
		
		@Override
		public Bitmap getBitmap(String url) {
			Log.i("test", "get cache " + url);
			return mCache.get(url);
		}

		@Override
		public void putBitmap(String url, Bitmap bitmap) {
			Log.i("test", "add cache " + url);
			mCache.put(url, bitmap);
		}
	}
	
	/**
	 * Json缓存工具类
	 * @author zhangming
	 */
	public class JsonCache{
		private LruCache<String, String> mJsonCache; //内存缓存json数据
		private DiskLruCache mDiskLruCache;  // 图片硬盘缓存核心类,硬盘缓存json数据
		
		public JsonCache(){
			mJsonCache = new LruCache<String, String>(4 * 1024 * 1024); //内存缓存初始值为4M
			
            try {
                File cacheDir = getDiskCacheDir(context, "json_thumb");  
                if (!cacheDir.exists()) {  
                    cacheDir.mkdirs();  
                }  
                // 创建DiskLruCache实例，初始化缓存数据 ,最大值为10M
				mDiskLruCache = DiskLruCache.open(cacheDir, getAppVersion(context), 1, 10 * 1024 * 1024);
			} catch (IOException e) {
				e.printStackTrace();
			}  
		}
		
		/**
		 * json数据添加进入内存缓存列表
		 * @param key
		 * @param value
		 */
		public void addJsonByLruCache(String key, String value) {
			mJsonCache.put(key, value);
			Log.i("test","LruCache================>添加key="+key);
		}
		
		/**
		 * 从内存缓存列表中取出json数据
		 * @param key
		 * @return
		 */
		public String getJsonByLruCache(String key) {
			Log.i("test","LruCache================>获取key="+key);
			return mJsonCache.get(key);
		}
		
		/**
		 * json数据添加进入磁盘缓存列表
		 * @param key
		 * @param value
		 */
		public void addJsonByDiskLruCache(String key, String value){
			try{
				DiskLruCache.Editor editor = mDiskLruCache.edit(String.valueOf(key));  
	            if (editor != null) {  
	                editor.set(0,value); 
	            } 
	            editor.commit(); //提交数据
	            Log.i("test","DiskLruCache================>添加key="+key);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		/**
		 * 从磁盘缓存列表中取出json数据
		 * @param key
		 * @return
		 */
		public String getJsonByDiskLruCache(String key){
			String json = null;
			try {
				Snapshot snapShot = mDiskLruCache.get(key);
				if (snapShot != null) {  
					json = snapShot.getString(0);
				}
				Log.i("test","DiskLruCache================>获取key="+key);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return json;
		}
		
		/** 
	     * 根据传入的uniqueName获取硬盘缓存的路径地址。 
	     */  
	    private File getDiskCacheDir(Context context, String uniqueName) {  
	        String cachePath;  
	        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())  
	                || !Environment.isExternalStorageRemovable()) {  
	            cachePath = context.getExternalCacheDir().getPath();  
	        } else {  
	            cachePath = context.getCacheDir().getPath();  
	        }  
	        return new File(cachePath + File.separator + uniqueName);  
	    }  
	    
	    /** 
	     * 获取当前应用程序的版本号。 
	     */  
	    private int getAppVersion(Context context) {  
	        try {  
	            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),0);  
	            return info.versionCode;  
	        } catch (NameNotFoundException e) {  
	            e.printStackTrace();  
	        }  
	        return 1;  
	    }  
	  
	}

}
