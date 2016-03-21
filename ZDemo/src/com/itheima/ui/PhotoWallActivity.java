package com.itheima.ui;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.GridView;

import com.itheima.adapter.PhotoWallAdapter;
import com.itheima.demo.R;
import com.itheima.model.Images;
import com.itheima.network.HttpClientInCar;

/**
 * 照片墙
 */
public class PhotoWallActivity extends Activity{
	 /** 
     * 用于展示照片墙的GridView 
     */  
    private GridView mPhotoWall;  
  
    /** 
     * GridView的适配器 
     */  
    private PhotoWallAdapter mAdapter;  
  
    private static final int mImageThumbSize = 100;  
    private static final int mImageThumbSpacing = 5;  
    private Context context;
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.photo_wall_activity);  
        context = this;
        
        mPhotoWall = (GridView) findViewById(R.id.photo_wall);  
        mAdapter = new PhotoWallAdapter(this, 0, Images.imageThumbUrls, mPhotoWall);  
        mPhotoWall.setAdapter(mAdapter);  
        
        mPhotoWall.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {  
            @Override  
            public void onGlobalLayout() {  
                final int numColumns = (int) Math.floor(mPhotoWall.getWidth()/(mImageThumbSize + mImageThumbSpacing));  
                if (numColumns > 0) {  
                    int columnWidth = (mPhotoWall.getWidth() / numColumns) - mImageThumbSpacing;  
                    mAdapter.setItemHeight(columnWidth + 20);  //调整高度
                    mPhotoWall.getViewTreeObserver().removeGlobalOnLayoutListener(this);  
                }  
            }  
       });  
    }  
      
    @Override  
    protected void onPause() {  
        super.onPause();  
        mAdapter.fluchCache();  
    }  
  
    @Override  
    protected void onDestroy() {  
        super.onDestroy();  
        mAdapter.cancelAllTasks();  //退出程序时结束所有的下载任务  
    }  
}
