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
import android.view.WindowManager;

import com.itheima.demo.R;
import com.itheima.util.SoftKeyboardUtil;
import com.itheima.util.SoftKeyboardUtil.OnSoftKeyboardChangeListener;
import com.itheima.view.MyEditTextView;
import com.itheima.view.WordFallView;

public class WordFallActivity extends Activity{
	private MyEditTextView et_word;
	private WordFallView wordView;
	private Timer timer;
	private TimerTask timerTask;
	
	private static float[] initX;
	private static float[] curY;
	private static int keyboardHeight;  //键盘的高度
	private static int screenHeight;  //手机屏幕的高度
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
					wordView.myPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
			        wordView.myCanvas.drawPaint(wordView.myPaint);
			        wordView.myPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
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
		et_word = (MyEditTextView)findViewById(R.id.et_word);
		wordView = (WordFallView)findViewById(R.id.word_content);
		
		initX = new float[]{100,200,300,400,500,600};
		curY = new float[initX.length];
		int[] screenInfos = getScreenWidthAndHeight(this);
		screenHeight = screenInfos[1];  //记录屏幕的宽度和高度,单位为像素
		
		timer = new Timer();
		timerTask = new MyTask();
		timer.schedule(timerTask,0,200); //启动定时任务
	}
	
	private void setListener(){
		et_word.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) { 
				wordView.init(et_word.getText().toString().trim(),initX,curY);
				wordView.invalidate();
			}
		});
		
		et_word.setOnInputCompleteListener(new MyEditTextView.OnInputCompleteListener() {
			@Override
			public void onInputComplete() {
				Log.i("test", "input is :"+et_word.getText().toString().trim());
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
				curY[i] = curY[i] + new Random().nextInt(50)+50;
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
