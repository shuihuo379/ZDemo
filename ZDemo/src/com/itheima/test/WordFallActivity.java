package com.itheima.test;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.widget.EditText;

import com.itheima.demo.R;
import com.itheima.util.SoftKeyboardUtil;
import com.itheima.util.StringUtil;
import com.itheima.util.SoftKeyboardUtil.OnSoftKeyboardChangeListener;
import com.itheima.view.WordFallView;

public class WordFallActivity extends Activity{
	private EditText et_word;
	private WordFallView wordView;
	private Timer timer;
	private TimerTask timerTask;
	
	private static float[] initX;
	private static float[] curY;
	private static int keyboardHeight;  //键盘的高度
	private static int screenHeight;  //手机屏幕的高度
	private static int scanTime = 200; //单位毫秒
	private static float curSpeed = 0.1f; //下落的速度(px/ms)
	
	//private static int prevLength;
	//private Handler handler = new Handler();
	//private Runnable runnable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		setContentView(R.layout.word_fall_activity);
		init();
		setListener();
		
	    /**
		runnable = new Runnable() {
			@Override
			public void run() {
				int curLength = et_word.getText().toString().trim().length();
				Log.i("test","curLength:"+curLength);
				if(prevLength!=0 && prevLength == curLength){
					//wordView.myPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			        //wordView.myCanvas.drawPaint(wordView.myPaint);
			        //wordView.myPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
				}else{
					prevLength = curLength;
				}
				handler.postDelayed(this, 2000);
			}
		};
		handler.postDelayed(runnable,0);
		**/
	}
	
	private void init(){
		et_word = (EditText)findViewById(R.id.et_word);
		wordView = (WordFallView)findViewById(R.id.word_content);
		
		initX = new float[]{50,100,150,200,250};
		curY = new float[initX.length];
		for(int i=0;i<curY.length;i++){
			curY[i] = new Random().nextInt(200);  //初始化坐标位置
		}
		int[] screenInfos = getScreenWidthAndHeight(this);
		screenHeight = screenInfos[1];  //记录屏幕的宽度和高度,单位为像素
		
		timer = new Timer();
		timerTask = new MyTask();
	}
	
	@Override
	protected void onResume() {
		timer.schedule(timerTask,0,scanTime); //启动定时任务
		super.onResume();
	}
	
	private void setListener(){
		et_word.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				if(StringUtil.isEmpty(et_word.getText().toString().trim())){
					Log.i("test","restart...");
					for(int i=0;i<curY.length;i++){
						curY[i] = new Random().nextInt(200); //重新初始化坐标位置
					}
				}
			}
			
			@Override
			public void afterTextChanged(Editable s) { 
				wordView.init(et_word.getText().toString().trim(),initX,curY);
				wordView.invalidate();
			}
		});
		
		/**
		et_word.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				 if(actionId == EditorInfo.IME_ACTION_SEARCH){  
					 return true;
				 }
				return false;
			}
		});
		**/
		
		et_word.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){  
					curSpeed+=0.01f;
					return true;
				}
				return false;
			}
		});
		
		SoftKeyboardUtil.observeSoftKeyboard(this,new OnSoftKeyboardChangeListener() {
			@Override
			public void onSoftKeyBoardChange(int softKeyboardHeight, boolean visible) {
				keyboardHeight = softKeyboardHeight;  //回调初始化
			}
		});
	}
	
	/**
	 * 获取窗口的宽高
	 * @return
	 */
	public int[] getScreenWidthAndHeight(Activity activity){
		 DisplayMetrics dm = new DisplayMetrics();    
	     activity.getWindowManager().getDefaultDisplay().getMetrics(dm); //取得窗口属性    
	     int screenWidth = dm.widthPixels;  //窗口宽度
	     int screenHeight = dm.heightPixels;  //窗口高度   
	     
	     return new int[]{screenWidth,screenHeight};
	}
	
	class MyTask extends TimerTask{
		@Override
		public void run() {
			for(int i=0;i<curY.length;i++){
				if(curY[i]>=screenHeight-keyboardHeight){
					curY[i] = 0;  //重置坐标 
				}
				curY[i] = curY[i] + curSpeed*scanTime;
			}
			wordView.init(et_word.getText().toString().trim(),initX,curY);
			wordView.postInvalidate();  //子线程中刷新View
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		timerTask.cancel();  //关闭定时器
	}
}
