package yzriver.avc.avccodec;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.itheima.util.CommonUtil;

/**
 * 初始化工具类,在SD卡上建立工作目录,生成yuv数据文件
 * @author zhangming
 * @date 2016/09/18
 */
public class InitUtil {
	private static String TAG="InitUtil" ;
	private static String yuvFileName = "init.yuv";

	public static void PrepareRawData( Context  context ) {
    	String sddir = CommonUtil.getNormalSDPath()+File.separator+"ZDemo";
    	File file = new File(sddir) ;
    	if( !file.exists() ){
    		try{
    			file.mkdir() ;
    		}
    		catch(SecurityException e){
    			e.printStackTrace();
    		}
    	}
    	if( !file.exists() ){
    		return ;
    	}
    	file = new File(sddir,yuvFileName) ;
    	if( file.exists() ){
    		return ;
    	}
    	FileOutputStream fo = null ;
    	try {
    		fo = new FileOutputStream(file) ;
    	}
    	catch(FileNotFoundException e){
           	Log.v(TAG,"Init yuv data file fails" ) ;    		
    	}
    	
    	AssetManager asm = context.getResources().getAssets() ;
    	InputStream fi = null ;
    	try {
    		fi = asm.open(yuvFileName);
    	}
    	catch (IOException e ) {    		
    		e.printStackTrace();
    	}
    	byte[] buf = new byte[1024] ;
    	int readb ;
    	while (true) {
    		try {
    			readb =  fi.read(buf) ;
    			if( readb <= 0 ){
    				break ;
    			}
    			fo.write(buf , 0, readb) ;
    		}
    		catch(IOException e){
    			break ;
    		}    		
    	}
    	try {
    		fo.close() ;
    		fi.close() ;
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    } 
}
