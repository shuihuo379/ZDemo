package com.itheima.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itheima.demo.R;

public class MyTopBar extends RelativeLayout {
	private ImageView left_img, right_img;
	private TextView tv_title;
	private TopBarClickListener listener;

	public MyTopBar(Context context,AttributeSet attrs) {
		super(context,attrs);
		
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.MyTopBar);
		Drawable left_src = ta.getDrawable(R.styleable.MyTopBar_left_src);
		Drawable right_src = ta.getDrawable(R.styleable.MyTopBar_right_src);
		String title = ta.getString(R.styleable.MyTopBar_title_text);
		//默认设置13sp
		float size = ta.getDimension(R.styleable.MyTopBar_title_text_size,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,13,getResources().getDisplayMetrics()));
		int color = ta.getColor(R.styleable.MyTopBar_title_text_color,0);
		ta.recycle();
		
		tv_title = new TextView(context);
		left_img = new ImageView(context);
		right_img = new ImageView(context);
		tv_title.setText(title);
		tv_title.setTextColor(color);
		tv_title.setTextSize(size);
		tv_title.setGravity(Gravity.CENTER_VERTICAL);
		left_img.setImageDrawable(left_src);
		right_img.setImageDrawable(right_src);
		
		setBackgroundColor(getResources().getColor(R.color.blue)); //设置界面背景色
		
		LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		leftParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
		leftParams.setMargins(getResources().getDimensionPixelSize(R.dimen.dp20),0,0,0);
		addView(left_img, leftParams);  //添加一个ImageView控件到父容器左边
		
		LayoutParams rightParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		rightParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
		rightParams.setMargins(0,0,getResources().getDimensionPixelSize(R.dimen.dp20),0);
	    addView(right_img, rightParams); //添加一个ImageView控件到父容器右边
	    
	    LayoutParams titleParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	    titleParams.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
	    addView(tv_title,titleParams);  //添加title到父容器中间
	    
	    left_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.leftClick();  //点击回调方法leftClick
			}
		});
	    
	    right_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.rightClick(); //点击回调方法rightClick
			}
		});
	}
	
	//单击左右图片的回调接口
	public interface TopBarClickListener{
		void leftClick();
		void rightClick();
	}
	
	public void setOnTopBarClickListener(TopBarClickListener listener){
		this.listener = listener;
	}
}
