package com.itheima.property.animator;

import com.itheima.demo.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

/**
 * 使用属性动画模拟小球自由落体和抛物线轨迹,并伴随淡入淡出效果
 * @author zhangming
 * @date 2016/06/15
 */
public class ValueAnimatorActivity extends Activity{
	private ImageView mBlueBall;
	private float mScreenHeight;
	private ViewGroup parent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.value_animator_activity);
		
		mBlueBall = (ImageView) findViewById(R.id.id_ball);
		parent = (ViewGroup) mBlueBall.getParent(); //获取View的父控件
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		
		mScreenHeight = metrics.heightPixels;
	}
	
	/**
	 * 自由落体
	 * @param view
	 */
	public void verticalRun(View view){
		showBall();
		ValueAnimator anim = ValueAnimator.ofFloat(0,mScreenHeight-mBlueBall.getHeight());
		anim.setTarget(mBlueBall);  //设置目标对象
		anim.setInterpolator(new LinearInterpolator());
		anim.setDuration(3000).start();
		anim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mBlueBall.setTranslationY((Float)animation.getAnimatedValue());
			}
		});
	}
	
	/**
	 * 抛物线
	 * @param view
	 */
	public void paowuxian(View view){
		showBall();
		ValueAnimator anim = new ValueAnimator();
		anim.setDuration(3000);
		anim.setObjectValues(new PointF(0, 0));
		anim.setInterpolator(new LinearInterpolator());
		anim.setEvaluator(new TypeEvaluator<PointF>() {
			@Override
			public PointF evaluate(float fraction, PointF startValue,PointF endValue) {
				Log.e("test", fraction * 3 + "");
				// x方向200px/s ，则y方向0.5 * g * t (g = 100px / s*s)
				PointF point = new PointF();
				point.x = 200 * fraction * 3;
				point.y = 0.5f * 100 * (fraction * 3) * (fraction * 3);
				return point;
			}
		});
		anim.start();
		anim.addUpdateListener(new AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				PointF point = (PointF) animation.getAnimatedValue();
				mBlueBall.setX(point.x);
				mBlueBall.setY(point.y);
			}
		});
	}
	
	/**
	 * 淡入淡出效果
	 * @param view
	 */
	public void fadeOut(View view){
		ObjectAnimator anim = ObjectAnimator.ofFloat(mBlueBall,"alpha",0.5f);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				hiddenBall();
			}
		});
		anim.start();
	}
	
	private void hiddenBall(){
		if(parent!=null && parent.getChildCount()==2){
			parent.removeView(mBlueBall); //通过父控件移除自身
		}
	}
	
	private void showBall(){
		if(parent!=null && mBlueBall!=null && parent.getChildCount()==1){
			mBlueBall.setAlpha(1f);
			parent.addView(mBlueBall);
			mBlueBall.layout(0,0,mBlueBall.getWidth(),mBlueBall.getHeight());
		}
	}
}
