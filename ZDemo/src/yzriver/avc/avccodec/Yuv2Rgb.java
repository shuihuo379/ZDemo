package yzriver.avc.avccodec;

/**
 * 调用C++接口,负责将YUV数据转成RGB数据,只有RGB数据才好显示
 * @author zhangming
 * @date 2016/09/18
 */
public class Yuv2Rgb {
	static public final int YUVFORMAT_YUV420P = 1 ;
	static public final int YUVFORMAT_YUV420SP = 2 ;

	private int width ;
	private int height ;
	private int yuvType ;
	private int yuv2RgbHandle ; 
	
	private native int  nativeYuv2RgbInit(int width , int height , int yuvType) ;
	private native void nativeYuv420p2Argb(int handle, byte[] yuv, int[] argb , int width , int height) ;
	private native void nativeYuv420sp2Argb(int handle, byte[] yuv, int[] argb, int width , int height) ;
	private native void nativeYuv420pApart2Argb(int handle, int yPointer, int uPointer, int vPointer, int[] argb, int width , int height) ;	
	private native void nativeYuv2RgbDestroy(int handle) ;
	
	public Yuv2Rgb(int w , int h , int yuvType) {
		width = w; 
		height = h ;  
		this.yuvType = yuvType ;
		yuv2RgbHandle = nativeYuv2RgbInit(w ,  h ,  yuvType) ;
	}
	public void Yuv2RgbOneFrame(byte[] yuv , int[] argb ){
		if( yuvType == YUVFORMAT_YUV420P  )
			nativeYuv420p2Argb(yuv2RgbHandle , yuv,argb, width, height ) ;
		else if( yuvType == YUVFORMAT_YUV420SP )
			nativeYuv420sp2Argb(yuv2RgbHandle ,yuv,argb, width , height ) ;
	}
	public void Yuv2RgbOneFrame(int yPointer  ,int uPointer  , int vPointer  , int[] argb ){ 
		nativeYuv420pApart2Argb( yuv2RgbHandle ,  yPointer  , uPointer  , vPointer  ,  argb , width , height ) ;
	}
	public void Yuv2RgbDestroy(){
		nativeYuv2RgbDestroy( yuv2RgbHandle ) ;		
	}	
}
