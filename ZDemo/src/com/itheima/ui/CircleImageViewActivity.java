package com.itheima.ui;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;

import com.itheima.demo.R;
import com.itheima.util.CommonUtil;
import com.itheima.util.SharedPre;
import com.itheima.util.T;
import com.itheima.view.CircleImageView;

/**
 * 拍照或从相册选取后显示指定的图片到圆形的ImageView中
 * @author zhangming
 * @date 2016/02/18
 */
public class CircleImageViewActivity extends Activity{
	private static final int OPEN_CAMERA_CODE = 0;
    private static final int OPEN_GALLERY_CODE = 1;
    private static final int CROP_PHOTO_CODE = 2;
    private static final int CLOSE_DIALOG_CODE = 3;
    private static final int crop = 300;  // 裁剪大小
    
    private CircleImageView iv_circle;
    private File tempFile;
    private String fileName = "";
    private Uri photoUri;
  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.circle_image);
		initView();
	}

	private void initView() {
		iv_circle = (CircleImageView) findViewById(R.id.iv_circle);
		final AlertDialog.Builder builder = new AlertDialog.Builder(CircleImageViewActivity.this);
		String[] strs = { "拍照上传", "相册选取", "取消" };
		builder.setItems(strs, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case OPEN_CAMERA_CODE:
					initFile();
					openCamera();
					break;
				case OPEN_GALLERY_CODE:
					initFile();
					openGallery();
					break;
				case CLOSE_DIALOG_CODE:
					T.show(getApplicationContext(),"关闭对话框");
					break;
				}
			}
		});
		builder.setCancelable(false);
		builder.create().show();
	}
	
	public void initFile() {
        if(fileName.equals("")) {
            if(CommonUtil.ExistSDCard()) {
                String path = Environment.getExternalStorageDirectory() + File.separator + "my_picture";
                File dir = new File(path);
                if(!dir.exists()){
                	dir.mkdirs();
                }
                fileName = path + File.separator +"my_photo.jpg";
                tempFile = new File(fileName);
                
//            	fileName = getExternalFilesDir(null)+File.separator+"my_photo.jpg";
//            	tempFile = new File(fileName);
            } else {
               T.show(getApplicationContext(), "请插入SD卡");
            }
        }
    }

	
	/**
     * 调用相机
     */
    public void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 打开相机
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile)); //将拍照后的图像结果写入到临时文件tempFile
        
        //针对一些机型无法从getData中获取uri,则需手动指定拍照后存储照片的Uri
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String filename = timeStampFormat.format(new Date());
        ContentValues values = new ContentValues();
        values.put(Media.TITLE, filename);
        photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        
        startActivityForResult(intent, OPEN_CAMERA_CODE);
    }

    /**
     * 打开相册
     */
    public void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);// 打开相册
        intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile)); //将选中的相册的图像结果写入到临时文件tempFile
        startActivityForResult(intent, OPEN_GALLERY_CODE);
    }

    /**
     * 裁剪图片
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("output", Uri.fromFile(tempFile));
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", crop);
        intent.putExtra("outputY", crop);
        startActivityForResult(intent, CROP_PHOTO_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 1){
           return;
        }
        switch (requestCode) {
	        case OPEN_CAMERA_CODE:
//	            cropPhoto(Uri.fromFile(tempFile));
	        	if(data!=null){
	        		cropPhoto(data.getData());
	        	}else{
	        		if(photoUri!=null){
	        			cropPhoto(photoUri);
	        		}
	        	}
	            break;
	        case OPEN_GALLERY_CODE:
	        	if(data!=null){
	        		cropPhoto(data.getData());
	        	}
	            break;
	        case CROP_PHOTO_CODE:
	            try{
	                BitmapFactory.Options options = new BitmapFactory.Options();
	                // 先设置为TRUE不加载到内存中，但可以得到宽和高
	                options.inJustDecodeBounds = true;
	                Bitmap bitmap = BitmapFactory.decodeFile(fileName,options);
	                options.inJustDecodeBounds = false;
	                // 计算缩放比
	                int be = (int) (options.outHeight / (float) 200);
	                if (be <= 0){
	                	be = 1;
	                }
	                options.inSampleSize = be;
	                // 这样就不会内存溢出了
	                bitmap = BitmapFactory.decodeFile(fileName, options);
	
	                if(bitmap != null) {
	                   iv_circle.setImageBitmap(bitmap);
	                   SharedPre.setSharedPreferences(getApplicationContext(),"user_photo",fileName);
	                }
	            }catch (Exception e) {
	                e.printStackTrace();
	            }
	            break;
	        default:
	            break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	//清除临时图片文件
    	if(tempFile!=null && tempFile.exists()){
    		tempFile.delete();
    		tempFile = null;
    	}
    }
}
