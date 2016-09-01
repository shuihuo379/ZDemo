package h264.com;

import java.io.File;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.itheima.demo.R;
import com.itheima.util.CommonUtil;

/**
 * 视频采用H264进行软编码(即使用cpu编码)
 * @author zhangming
 * @date 2016/09/02
 */
public class H264EncodeActivity extends Activity implements SurfaceHolder.Callback,Camera.PictureCallback{
	private SurfaceView mSurfaceView = null;
	private SurfaceHolder mSurfaceHolder = null;
	private Camera mCamera = null;
	private boolean mPreviewRunning = false;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.surface_encode_activity);

	    mSurfaceView = (SurfaceView) this.findViewById(R.id.surface_camera);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
	

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		if (mPreviewRunning) {
			mCamera.stopPreview();
		}
		Camera.Parameters p = mCamera.getParameters();
		p.setPreviewSize(352, 288);
		mCamera.setPreviewCallback(new H264Encoder(352, 288));
		mCamera.setParameters(p);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (Exception ex) {
		}
		mCamera.startPreview();
		mPreviewRunning = true;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mCamera = Camera.open();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		if (mCamera != null) {
			mCamera.setPreviewCallback(null);
			mCamera.stopPreview();
			mPreviewRunning = false;
			mCamera.release();
			mCamera = null;
		}
	}

	public void onConfigurationChanged(Configuration newConfig) {
		try {
			super.onConfigurationChanged(newConfig);
			if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			}
		} catch (Exception ex) {
		}
	}
}

class H264Encoder implements Camera.PreviewCallback {
	private long encoder=0;
	private RandomAccessFile raf=null;
	private byte[] h264Buff =null;
	
	private static final String H264_FILE_NAME = "camera.h264";
	private static final String LIB_NAME = "H264Android";

	static {
		System.loadLibrary(LIB_NAME);
	}
	private H264Encoder(){};

	
	public H264Encoder(int width, int height) {
		encoder = CompressBegin(width, height);
		h264Buff = new byte[width * height *8];
		try {
			String dirPath = CommonUtil.getNormalSDPath();
			File file = new File(dirPath,H264_FILE_NAME);
			raf = new RandomAccessFile(file, "rw");
		} catch (Exception ex) {
			Log.v("System.out", ex.toString());
		}
		
	};
	protected void finalize()
    {
		CompressEnd(encoder);
		if (null != raf)
		{
			try {
				raf.close();
			} catch (Exception ex) {
				Log.v("System.out", ex.toString());
			}
		}
		try {
			super.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }
	
	private native long CompressBegin(int width,int height);
	private native int CompressBuffer(long encoder, int type,byte[] in, int insize,byte[] out);
	private native int CompressEnd(long encoder);
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {	
		
		int result=CompressBuffer(encoder, -1, data, data.length,h264Buff);
		try {
			if (result>0)
				raf.write(h264Buff, 0, result);
		} catch (Exception ex) {
			Log.v("System.out", ex.toString());
		}
	}
}


