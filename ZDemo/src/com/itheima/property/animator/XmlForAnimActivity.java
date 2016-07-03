package com.itheima.property.animator;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.itheima.demo.R;

public class XmlForAnimActivity extends Activity{
	private ImageView mMv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.xml_for_anim_activity);
		mMv = (ImageView) findViewById(R.id.id_mv);
	}
	
	public void scaleX(View view){
		Animator anim = AnimatorInflater.loadAnimator(this,R.anim.scale_x_anim);
		mMv.setPivotX(mMv.getWidth()/2);
		mMv.setPivotY(mMv.getHeight()/2); //设置以图像中心进行缩放
		anim.setTarget(mMv);
		anim.start();
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				// 动画结束时,从当前缩放值scaleX,scaleY变化到初始值1f,1f
				PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("scaleX",1f); //终止时scaleX=1f
				PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleY",1f); //终止时scaleY=1f
				ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(mMv,pvhX,pvhY);
				objectAnimator.setDuration(1000).start();
			}
		});
	}
	
	public void scaleXandScaleY(View view){
		Animator anim = AnimatorInflater.loadAnimator(this,R.anim.scale_x_y_anim);
		mMv.setPivotX(0);
		mMv.setPivotY(0);  //设置以图像左上角进行缩放
		mMv.invalidate();  //显示的调用invalidate
		anim.setTarget(mMv);
		anim.start();
	}
}
