package com.itheima.util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.itheima.application.MyApplication;
import com.itheima.util.DiskLruCache.Snapshot;

/**
 * 图片缓存工具类 包含内存缓存LruCache和磁盘缓存DiskLruCache
 * @author zhangming
 * @date 2016/03/15
 */
public class ImageCacheUtil implements ImageCache {
	private static String TAG = ImageCacheUtil.class.getSimpleName();
	private static LruCache<String, Bitmap> mLruCache; 
	private static DiskLruCache mDiskLruCache;  //缓存类
	private static final int DISKMAXSIZE = 10 * 1024 * 1024; //磁盘缓存大小

	public ImageCacheUtil() {
		//设置图片缓存大小为程序最大可用内存的1/8
		int maxSize = (int) (Runtime.getRuntime().maxMemory() / 8);
		//实例化LruCaceh对象
		mLruCache = new LruCache<String, Bitmap>(maxSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				//return bitmap.getByteCount();  
				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
		try {
            File cacheDir = getDiskCacheDir(MyApplication.newInstance(), "thumb");  //获取图片缓存路径  
            if (!cacheDir.exists()) {  
                cacheDir.mkdirs();  
            }  
			// 获取DiskLruCahce对象
			mDiskLruCache = DiskLruCache.open(cacheDir,getAppVersion(MyApplication.newInstance()), 1, DISKMAXSIZE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
     * 从缓存（内存缓存，磁盘缓存）中获取Bitmap
     */
    @Override
    public Bitmap getBitmap(String url) {
        if (mLruCache.get(url) != null) {
            Log.i(TAG,"从LruCahce获取");   //从LruCache缓存中取
            return mLruCache.get(url);
        } else {
            String key = StringUtil.MD5Encode(url);
            try {
                if (mDiskLruCache.get(key) != null) {
                    Snapshot snapshot = mDiskLruCache.get(key); //从DiskLruCache获取缩略图
                    Bitmap bitmap = null;
                    if (snapshot != null) {
                        bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                        // 存入LruCache缓存
                        mLruCache.put(url, bitmap);
                        Log.i(TAG,"从DiskLruCahce获取");
                    }
                    return bitmap;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 存入缓存（内存缓存，磁盘缓存）
     */
    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        // 存入LruCache缓存
        mLruCache.put(url, bitmap);
        // 判断是否存在DiskLruCache缓存，若没有存入
        String key = StringUtil.MD5Encode(url);
        try {
            if (mDiskLruCache.get(key) == null) {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                if (editor != null) {
                    OutputStream outputStream = editor.newOutputStream(0);
                    if (bitmap.compress(CompressFormat.JPEG, 100, outputStream)) {
                        editor.commit();
                    } else {
                        editor.abort();
                    }
                }
                mDiskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 该方法会判断当前SD卡是否存在,然后选择缓存地址
     * @param context
     * @param uniqueName
     * @return
     */
    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();  //内置SD卡存在可用,缓存到此
        } else {
            cachePath = context.getCacheDir().getPath();  //缓存到内存中
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取应用版本号
     * @param context
     * @return
     */
    public int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

}
