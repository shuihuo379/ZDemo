package yzriver.avc.avccodec;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.itheima.demo.R;
import com.itheima.util.CommonUtil;

/**
 * AVC编码视频录制功能
 * @author zhangming
 * @date 2016/09/18
 */
public class AvcRecActivity extends Activity implements OnClickListener{
	private VideoCameraView avcRecVideoCameraView;
	private AvcThread avcThread;
	private Button btnRecStart;
	private Button btnRecStop;	
	private TextView avcRecFrameTextView;
	
	private static String sddir;
	
	private final Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
            	case AvcThread.MSG_AVCREC_FINISH: {
            		btnRecStart.setEnabled(true) ;	
            		break;
            	}
			}
		}
	};
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		InitUtil.PrepareRawData(this);
		setContentView(R.layout.avc_rec_activity);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,"avcThread");
		
		avcRecVideoCameraView =(VideoCameraView)findViewById(R.id.lsurfaceViewAvcRec);
		SurfaceHolder surfaceHolder = avcRecVideoCameraView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		avcThread = new AvcThread(wakeLock);
		
		btnRecStart  = (Button)findViewById(R.id.avcRec_start_button);
		btnRecStart.setOnClickListener(this);
		btnRecStop =  (Button)findViewById(R.id.avcRec_stop_button);
		btnRecStop.setOnClickListener(this);		

		avcRecFrameTextView = (TextView) findViewById(R.id.avcRecFramerate);
	}
	
	protected void onPause() {
		avcThread.stopAvcRec();	
        super.onPause();
    }
		
    public void onClick(View v){
    	int id = v.getId() ; 
    	switch ( id ){
    		case R.id.avcRec_start_button:
		    	Calendar cal = Calendar.getInstance() ;
		
		    	int mon = cal.get(Calendar.MONTH) ;
		    	String mm ;
		    	if( mon < 9 )
		    		mm = "0"+(mon+1) ;
		    	else
		    		mm = Integer.toString(mon+1) ;
		    	mon = cal.get(Calendar.DAY_OF_MONTH) ;
		    	if( mon < 10 )
		    		mm += "0"+(mon) ;
		    	else
		    		mm += (mon) ;
		    	mon = cal.get(Calendar.HOUR_OF_DAY) ;
		    	if( mon < 10 )
		    		mm += "0"+(mon) ;
		    	else
		    		mm += (mon) ;		    	
		    	mon = cal.get(Calendar.MINUTE) ;
		    	if( mon < 10 )
		    		mm += "0"+(mon) ;
		    	else
		    		mm += (mon) ;
		    	mon = cal.get(Calendar.SECOND) ;
		    	if( mon < 10 )
		    		mm += "0"+(mon) ;
		    	else
		    		mm += (mon) ;		    	
		    	
		    	//经过InitUtil.PrepareRawData(this); 此时目录sddir已经是存在的
		    	sddir = CommonUtil.getNormalSDPath()+File.separator+"ZDemo"; 
		    	String avc = sddir+File.separator+cal.get(Calendar.YEAR)+mm+".avc" ;
    			
		    	avcThread.setFrameRateTextView(avcRecFrameTextView) ;
		    	avcThread.startAvcRec(avcRecVideoCameraView , avc , 352 , 288 , handler);
    			v.setEnabled(false) ;    		 	 
    			break;
    		case R.id.avcRec_stop_button:
    			avcThread.stopAvcRec() ;
    			break;
    	}  	
    } 
}
