package yzriver.avc.avccodec;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class VideoCameraView extends SurfaceView 
	implements SurfaceHolder.Callback,android.hardware.Camera.PreviewCallback{
	
	private static final String TAG = "VideoCameraView";
	private android.hardware.Camera mCamera;
	private SurfaceHolder surfaceHolder;
	
	private boolean mRec = false ; //录制标志位,true代表录制
	private int numBuffFilled = 0; 
	private int currentBuffToRead = 0 ;
	
	private int preview_w;
	private int preview_h;
	private int preview_yuvbytes; 
	private YzrAvcEnc yzrAvcEnc; 
	private double mAspectRatio = 3.0 / 3.0;
	
	private byte[] avcBitStream;
	private int[] avcBitStreamLength = new int[1]; 
	private int[] nalType = new int[1];
	private byte[][] previewBuffer;
	
	public VideoCameraView(Context context ){
		super(context) ;
		Log.v(TAG,"VideoCameraView( "+context+" )" );
	}

	public VideoCameraView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    
	    surfaceHolder = this.getHolder();
	    surfaceHolder.addCallback(this);
	    Log.v(TAG,"VideoCameraView( "+context+" "+ attrs + ")" );
	}
	
	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		if(mRec){
			synchronized (this) {
				numBuffFilled++;
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.v(TAG, "surfacedCreated ");
		surfaceHolder =  holder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,int height) {
		surfaceHolder = holder;
		Log.v(TAG, "surfacedChanged " + "width="+width+"height="+height+"Format="+format);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.v(TAG, "surfacedDestroyed " );
	}
	
	public double getAspectRatio() {
		return mAspectRatio ;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		int previewWidth = MeasureSpec.getSize(widthMeasureSpec);
	    int previewHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (previewWidth > previewHeight * mAspectRatio) {
            previewWidth = (int) (previewHeight * mAspectRatio + .5);
        } else {
            previewHeight = (int) (previewWidth / mAspectRatio + .5);
        }
	    // Ask children to follow the new preview dimension.
	    super.onMeasure(MeasureSpec.makeMeasureSpec(previewWidth, MeasureSpec.EXACTLY),
	        MeasureSpec.makeMeasureSpec(previewHeight, MeasureSpec.EXACTLY));
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@SuppressWarnings("static-access")
	public void openCamera(int w , int h){
		mRec = false ;
		if( surfaceHolder == null){
			return ;
		}
		mCamera = android.hardware.Camera.open() ;
		try {
			mCamera.setPreviewDisplay(surfaceHolder);
		}catch(IOException e ){
			Log.e(TAG,"mCamera.setPreviewDisplay( " + surfaceHolder +") fail"  ) ;
			return ;
		}

		android.hardware.Camera.Parameters p = mCamera.getParameters() ;
		List<android.hardware.Camera.Size>  listPreview = p.getSupportedPreviewSizes() ;
		Log.v(TAG, "preview size is "+listPreview) ;
		int ii = -1 ; 
		int delta = 0x7fffff ;
		for( int i = 0 ; i < listPreview.size() ; i ++) {
			android.hardware.Camera.Size size = listPreview.get(i) ;
			String ws = Integer.toString(size.width);
			String hs = Integer.toString(size.height) ;			
			Log.v(TAG, "elements "+i+":"+ws+"x"+hs) ;
			if( java.lang.Math.abs(size.width - w ) < delta ) {
				delta = java.lang.Math.abs(size.width - w ) ;
				ii = i ;
			}
		}
		
		preview_w = listPreview.get(ii).width ;
		preview_h = listPreview.get(ii).height ;
		preview_yuvbytes = preview_w*preview_h*3/2 ;

		mAspectRatio = (double)preview_w / preview_h;
		p.setPreviewSize( preview_w , preview_h ) ;
		List<int[]>  fpRange = p.getSupportedPreviewFpsRange() ;
		for(int i = 0  ; i < fpRange.size() ; i ++ ) {
			int[] fpr = fpRange.get(i) ;
			Log.v(TAG, "min "+ fpr[0]+ " max " + fpr[1]);			
		}
		
		mCamera.setParameters(p);
		mCamera.setPreviewCallbackWithBuffer(this);
		
		android.hardware.Camera.CameraInfo cameraInfo = new android.hardware.Camera.CameraInfo() ; 
		mCamera.getCameraInfo(0,cameraInfo);
		Log.v(TAG,"Camera RotateAngle="+ cameraInfo.orientation);		
		
		requestLayout() ;		
		mCamera.startPreview();
	}
	
	public void setYzrAvcEncHandle(YzrAvcEnc yzrAvcEnc ) {
		this.yzrAvcEnc = yzrAvcEnc ;
	}
	
	public int getPreviewWidth(){
		return preview_w ;
	}

	public int getPreviewHeight(){
		return preview_h ;
	}
	
	public void startRec() {
		mRec = true ;
		previewBuffer = new byte[2][] ;
		previewBuffer[0]  = new byte[preview_yuvbytes] ;
		previewBuffer[1]  = new byte[preview_yuvbytes] ;
		numBuffFilled = 0  ;
		currentBuffToRead = 0 ;
		mCamera.addCallbackBuffer( previewBuffer[0] ) ;
		mCamera.addCallbackBuffer( previewBuffer[1] ) ;
	}
	
	public void stopRec() {
		mRec = false ;
	}
		
	public int encodeOneFrame(byte[] bitstream , int bitStreamLength){
		int i = 0 ;
		while( (i++ < 10) && ( numBuffFilled < 1) ) {
			try {
				Thread.sleep(10) ;
			}catch( InterruptedException e) {
				e.printStackTrace();
			}			
		}
		if( numBuffFilled < 1 ){
			return 0;
		}

		byte[] bu = previewBuffer[currentBuffToRead] ;
		currentBuffToRead = (currentBuffToRead+1)%2 ;
		avcBitStream = bitstream ;
		avcBitStreamLength[0] = bitStreamLength ;
		//Log.v(TAG, "start YzrAvcEncEncodeOneFrame") ;
		yzrAvcEnc.YzrAvcEncEncodeOneFrame(bu, avcBitStream, avcBitStreamLength, nalType);
		synchronized ( this ) {
			numBuffFilled -- ;
		}
		mCamera.addCallbackBuffer( bu ) ;
		
		return avcBitStreamLength[0] ;
	}
	
	public void stopCamera(){
		if( mCamera == null ) {
			return ;
		}
		mCamera.setPreviewCallback(null) ;
		mCamera.stopPreview() ;
		mCamera.release() ;
		mCamera = null ;
	}
}
