package com.itheima.property.animator;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;

import com.itheima.demo.R;

/**
 * 有关属性动画中一个比较综合的案例
 * 使用LayoutTransition为布局的容器设置动画,当容器中的视图层次发生变化时存在过渡的动画效果
 * @author zhangming
 */
public class ViewLayoutAnimActivity extends Activity implements CompoundButton.OnCheckedChangeListener{
	private ViewGroup viewGroup;
	private CheckBox mAppear, mChangeAppear, mDisAppear, mChangeDisAppear;
	private GridLayout mGridLayout;
	private LayoutTransition mTransition;
	private int mVal;
	
	/**
	 * 知识点说明,过渡的动画类型一共有四种：
	 * LayoutTransition.APPEARING  当一个View在ViewGroup中出现时,对此View设置的动画
	 * LayoutTransition.CHANGE_APPEARING  当一个View在ViewGroup中出现时,对此View对其他View位置造成影响,对其他View设置的动画
	 * LayoutTransition.DISAPPEARING  当一个View在ViewGroup中消失时,对此View设置的动画
	 * LayoutTransition.CHANGE_DISAPPEARING  当一个View在ViewGroup中消失时,对此View对其他View位置造成影响,对其他View设置的动画
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_layout_animator_activity);
		
		viewGroup = (ViewGroup) findViewById(R.id.id_container);

		mAppear = (CheckBox) findViewById(R.id.id_appear);
		mChangeAppear = (CheckBox) findViewById(R.id.id_change_appear);
		mDisAppear = (CheckBox) findViewById(R.id.id_disappear);
		mChangeDisAppear = (CheckBox) findViewById(R.id.id_change_disappear);
		
		mAppear.setOnCheckedChangeListener(this);
		mChangeAppear.setOnCheckedChangeListener(this);
		mDisAppear.setOnCheckedChangeListener(this);
		mChangeDisAppear.setOnCheckedChangeListener(this);
		
		// 创建一个GridLayout
		mGridLayout = new GridLayout(this);
		mGridLayout.setColumnCount(5); //设置每列5个按钮
		viewGroup.addView(mGridLayout); //添加到布局中
		
		// 默认动画全部开启
		mTransition = new LayoutTransition();
		mTransition.setAnimator(LayoutTransition.APPEARING,
				(mAppear.isChecked()?ObjectAnimator.ofFloat(this,"scaleX",0f,1f):null));
		mGridLayout.setLayoutTransition(mTransition);
	}
	
	/**
	 * 添加按钮
	 * @param view
	 */
	public void addBtn(View view){
		final Button button = new Button(this);
		button.setWidth(getResources().getDimensionPixelSize(R.dimen.dp70));
		button.setHeight(getResources().getDimensionPixelSize(R.dimen.dp30));
		/**
		button.setBackgroundColor(getResources().getColor(R.color.main_green));
		button.setPadding(getResources().getDimensionPixelSize(R.dimen.dp20),getResources().getDimensionPixelSize(R.dimen.dp5),
				getResources().getDimensionPixelSize(R.dimen.dp20), getResources().getDimensionPixelSize(R.dimen.dp5));
		**/
		button.setText((++mVal) + "");
		button.setGravity(Gravity.CENTER);
		mGridLayout.addView(button,Math.min(1,mGridLayout.getChildCount()));
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mGridLayout.removeView(v);
			}
		});
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		mTransition = new LayoutTransition();  //每次改变时需要重新开始new一个布局变化变量
		mTransition.setAnimator(LayoutTransition.APPEARING,
			(mAppear.isChecked()?ObjectAnimator.ofFloat(this,"scaleX",0f,1f):null));
		mTransition.setAnimator(LayoutTransition.CHANGE_APPEARING,
			(mChangeAppear.isChecked()?mTransition.getAnimator(LayoutTransition.CHANGE_APPEARING):null));
		mTransition.setAnimator(LayoutTransition.DISAPPEARING,
			(mDisAppear.isChecked()?mTransition.getAnimator(LayoutTransition.DISAPPEARING):null));
		mTransition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING,
			(mChangeDisAppear.isChecked()?mTransition.getAnimator(LayoutTransition.CHANGE_DISAPPEARING):null));
		mGridLayout.setLayoutTransition(mTransition);
	}
}
