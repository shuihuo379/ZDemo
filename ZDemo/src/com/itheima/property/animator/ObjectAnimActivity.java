package com.itheima.property.animator;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.itheima.demo.R;

public class ObjectAnimActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.object_animator_activity);
	}
	
	public void rotateyAnimRun(final View view){
		//围绕X轴旋转360度持续3秒
		//ObjectAnimator.ofFloat(view,"rotationX",0f,360f).setDuration(3000).start();
		
		/**
		//使用ObjectAnimator实现多动画执行(透明度从1变成0,再从0变成1;大小从最大到最小,再从最小到最大,历时1s)
		//把设置属性的那个字符串,随便写一个该对象没有的属性,咱们只需要它按照时间插值和持续时间计算的那个值,我们自己手动调用
		ObjectAnimator anim = ObjectAnimator.ofFloat(view,"zhangming",1.0f,0.0f,1.0f).setDuration(1000);
		anim.start();
		anim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				 float cVal = (Float) animation.getAnimatedValue();  
				 view.setAlpha(cVal);
				 view.setScaleX(cVal);
				 view.setScaleY(cVal);
			}
		});
		**/
		
		
		/**
		ObjectAnimator anim = ObjectAnimator.ofFloat(view,"zhangming",1.0f,0.0f).setDuration(1000);
		anim.start();
		anim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				 float cVal = (Float) animation.getAnimatedValue();  
				 view.setAlpha(cVal);
				 view.setScaleX(cVal);
				 view.setScaleY(cVal);
			}
		});
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				Log.i("test","animation end...");
				view.setAlpha(1f);
				view.setScaleX(1f);
				view.setScaleY(1f);  //从不透明突变到透明状态,从最小值突变到最大值
			} 
		});
		**/
		
		//使用PropertyValuesHolder类实现
		PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha",1f,0f,1f);
		PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX",1f,0f,1f);  
		PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY",1f,0f,1f);  
		ObjectAnimator.ofPropertyValuesHolder(view,pvhX,pvhY,pvhZ).setDuration(1000).start();
	}
}
