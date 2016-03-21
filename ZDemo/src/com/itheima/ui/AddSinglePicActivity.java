package com.itheima.ui;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;

import com.itheima.adapter.AddPictureGridAdapter;
import com.itheima.demo.R;
import com.itheima.util.CommonUtil;
import com.itheima.util.T;

public class AddSinglePicActivity extends Activity{
	private GridView gv_single_pic;
	private AddPictureGridAdapter mAdapter;
	private static final int MAX_PICS = 5;
	
	private File tempFile;
	private String fileName = "";
	private Bitmap resizeBitmap;
	private static final int OPEN_GALLERY_CODE = 1;
	private static final int CROP_PHOTO_CODE = 2;
	private static final int CROP = 120;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_single_pic);
        initView();
    }
	
	private void initView() {
		gv_single_pic = (GridView)findViewById(R.id.gv_single_pic);
		mAdapter = new AddPictureGridAdapter(this,MAX_PICS);
		gv_single_pic.setAdapter(mAdapter);
		
		gv_single_pic.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if(position == mAdapter.getCount()-1 && !mAdapter.isReachMax()){
					//Bitmap bitmap = BitmapFactory.decodeResource(AddSinglePicActivity.this.getResources(),picImgsId[position]);
					//mAdapter.addPic(bitmap);
					initFile();
					openGallery(); //从相册选取照片,显示到界面上
				}else{
					resizeBitmap = showBigImage(AddSinglePicActivity.this,mAdapter.picList.get(position));
				}
			}
		});
	}
	
	private void initFile() {
        if(fileName.equals("")) {
            if(CommonUtil.ExistSDCard()) {
                String path = Environment.getExternalStorageDirectory() + File.separator + "my_picture";
                File dir = new File(path);
                if(!dir.exists()){
                	dir.mkdirs();
                }
                fileName = path + File.separator +"my_photo.jpg";
                tempFile = new File(fileName);
            } else {
               T.show(getApplicationContext(), "请插入SD卡");
            }
        }
    }
	
	private void openGallery(){
		Intent intent = new Intent(Intent.ACTION_PICK);// 打开相册
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile)); //将选中的相册的图像结果写入到临时文件tempFile
        startActivityForResult(intent, OPEN_GALLERY_CODE);
	}
	
	/**
     * 裁剪图片
     * @param uri
     * @param crop 裁剪大小
     */
    public void cropPhoto(Uri uri,int crop) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", Uri.fromFile(tempFile));
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX",crop);
        intent.putExtra("outputY",crop);
        startActivityForResult(intent, CROP_PHOTO_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1){
           return;
        }
        switch (requestCode) {
	        case OPEN_GALLERY_CODE:
	        	if(data!=null){
	        	   cropPhoto(data.getData(),CROP);
	        	}
	            break;
	        case CROP_PHOTO_CODE:
	            try{
	                BitmapFactory.Options options = new BitmapFactory.Options();
	                options.inJustDecodeBounds = true;  // 先设置为TRUE不加载到内存中，但可以得到宽和高
	                
	                Bitmap bitmap = BitmapFactory.decodeFile(fileName,options);
	                options.inJustDecodeBounds = false;
	                int be = (int) (options.outHeight / (float) 200);  // 计算缩放比
	                if (be <= 0){
	                	be = 1;
	                }
	                options.inSampleSize = be;
	                
	                // 这样就不会内存溢出了
	                bitmap = BitmapFactory.decodeFile(fileName, options);
	                mAdapter.addPic(bitmap);
	            }catch (Exception e) {
	                e.printStackTrace();
	            }
	            break;
	        default:
	            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
	/**
	 * 展示放大后图片
	 */
	private Bitmap showBigImage(Activity activity,final Bitmap bitmap) {
		View imgEntryView =  LayoutInflater.from(activity).inflate(R.layout.dialog_picture_entry,null); 
		final AlertDialog dialog = new AlertDialog.Builder(activity).create();
		ImageView img = (ImageView) imgEntryView.findViewById(R.id.large_image);
		Bitmap resizeBitmap = null;
		if (bitmap != null) {
			Display display = activity.getWindowManager().getDefaultDisplay();
			int scaleWidth = display.getWidth();
			int height = bitmap.getHeight(); //图片的真实高度
			int width = bitmap.getWidth(); //图片的真实宽度
			Log.i("test","img_real_width:"+width+"===>img_real_height:"+height);
			/**
			LayoutParams lp = (LayoutParams) img.getLayoutParams();
			lp.width = scaleWidth;// 调整宽度
			lp.height = (height * scaleWidth)/width; //调整高度
			img.setLayoutParams(lp);
			img.setImageBitmap(bitmap);
			**/
			float scaleBit = scaleWidth / width;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleBit,scaleBit);
			resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0,width,height, matrix, true);
			img.setImageBitmap(resizeBitmap);
			dialog.setView(imgEntryView); //自定义dialog
			dialog.show();
		}
		//点击布局文件（也可以理解为点击大图）后关闭dialog，这里的dialog不需要按钮
		imgEntryView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View paramView) {
				if (dialog.isShowing()) {
					dialog.cancel();
				}
			}
		});
		return resizeBitmap;
	}
    
	@Override
    protected void onDestroy() {
    	super.onDestroy();
    	//清除临时图片文件
    	if(tempFile!=null && tempFile.exists()){
    		tempFile.delete();
    		tempFile = null;
    	}
    	//释放bitmap资源
    	if(resizeBitmap!=null && !resizeBitmap.isRecycled()){
    		resizeBitmap.isRecycled();  
    	}
    }
}
