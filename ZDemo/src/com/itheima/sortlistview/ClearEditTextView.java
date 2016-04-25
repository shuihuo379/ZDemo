package com.itheima.sortlistview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;

import com.itheima.demo.R;

public class ClearEditTextView extends EditText implements OnFocusChangeListener, TextWatcher {
    private Drawable mClearDrawable; //删除按钮的引用
	
	public ClearEditTextView(Context context) {
		this(context, null);
	}

	public ClearEditTextView(Context context, AttributeSet attrs) {
		// 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, android.R.attr.editTextStyle);
	}

	public ClearEditTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	private void init() { 
		//获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
    	mClearDrawable = getCompoundDrawables()[2]; 
        if (mClearDrawable == null) { 
        	mClearDrawable = getResources().getDrawable(R.drawable.emotionstore_progresscancelbtn); 
        }
        mClearDrawable.setBounds(0,0,mClearDrawable.getIntrinsicWidth(),mClearDrawable.getIntrinsicHeight());
        setClearIconVisible(false); //默认初始进页面不显示
        setOnFocusChangeListener(this); 
        addTextChangedListener(this); 
	}
	
    /**
     * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
     * @param visible
     */
    protected void setClearIconVisible(boolean visible) { 
        Drawable right = visible ? mClearDrawable : null; 
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]); 
    } 
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,int after) {
		
	}
	
    /**
     * 当输入框里面内容发生变化的时候回调的方法
     */
	@Override
	public void onTextChanged(CharSequence s, int start,int lengthBefore, int lengthAfter) {
		setClearIconVisible(s.length() > 0); 
	}

	@Override
	public void afterTextChanged(Editable s) {
		
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(hasFocus){
			setClearIconVisible(getText().length()>0);
		}else{
			setClearIconVisible(false);
		}
	}
	
	/**
     * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
     * 当我们按下的位置 在  EditText的宽度 - 图标到控件右边的间距 - 图标的宽度(图标左侧的坐标) 和
     * EditText的宽度 - 图标到控件右边的间距(图标右侧的坐标)之间我们就算点击了图标，竖直方向没有考虑
     */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (getCompoundDrawables()[2] != null) { 
            if (event.getAction() == MotionEvent.ACTION_UP) { 
            	boolean touchable = event.getX() > (getWidth() - getPaddingRight() - mClearDrawable.getIntrinsicWidth()) 
                        && (event.getX() < ((getWidth() - getPaddingRight())));
                if(touchable){ 
                   this.setText(""); 
                } 
            } 
        }
		return super.onTouchEvent(event);
	}
	
	 /**
     * 设置晃动动画
     */
    public void setShakeAnimation(){
    	this.setAnimation(shakeAnimation(5));
    }
    
    
    /**
     * 晃动动画
     * @param counts 1秒钟晃动多少下
     * @return
     */
    public static Animation shakeAnimation(int counts){
    	Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
    	translateAnimation.setInterpolator(new CycleInterpolator(counts));
    	translateAnimation.setDuration(1000);
    	return translateAnimation;
    }
}
