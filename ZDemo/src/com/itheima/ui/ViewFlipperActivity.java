package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.itheima.demo.R;
import com.itheima.view.MyViewFlipper;
import com.itheima.view.MyViewFlipper.OnPageChangeListener;
import com.itheima.view.MyViewFlipper.TurnPageAnimation;

/**
 * 使用ViewFlipper实现页面水平滑动切换效果
 * @author zhangming
 * @date 2016/06/24
 */
public class ViewFlipperActivity extends Activity{
	private int[] imgIds = new int[] { R.drawable.main_img1, R.drawable.main_img2,
			R.drawable.main_img3, R.drawable.main_img4};
	private MyViewFlipper viewFlipper;
	private LinearLayout ll_container;
	private ImageView[] dotImagesArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_view_flipper);
		getWindow().getDecorView().setBackgroundColor(getResources().getColor(R.color.main_gray));
		initView();
		setListener();
	}

	private void initView() {
		viewFlipper = (MyViewFlipper) findViewById(R.id.viewFlipper);
		//viewFlipper.setFlipInterval(2000);  //设置Flipper切换时间为2s
		//viewFlipper.startFlipping(); //开启Fliiper切换
		
		for (int i = 0; i < imgIds.length; i++) {
			//TODO 暂时采用ImageView初始化条目
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setImageResource(imgIds[i]);
			viewFlipper.addView(imageView);
		}
		
		dotImagesArray = new ImageView[viewFlipper.getChildCount()];
		ll_container = (LinearLayout) findViewById(R.id.ll_container);
		
		for (int i = 0; i < dotImagesArray.length; i++) {
			ImageView dotImageView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30,30);
			if(i>0){
				params.leftMargin = 20;  //动态设置圆点左边距
			}
			dotImageView.setLayoutParams(params);
			dotImageView.setPadding(20, 0, 20, 0);
			dotImagesArray[i] = dotImageView;

			if (i == 0) {
				dotImagesArray[i].setBackgroundResource(R.drawable.point_pressed);
			} else {
				dotImagesArray[i].setBackgroundResource(R.drawable.point_unpressed);
			}
			ll_container.addView(dotImagesArray[i]);
		}
	}
	
	private void setListener() {
		//添加页面切换监听,增加切换动画
		viewFlipper.setTurnPageAnimation(new TurnPageAnimation() {
			@Override
			public void showPreviousAnimation() { //加载右滑动画
				viewFlipper.showAnimation(AnimationUtils.loadAnimation(viewFlipper.getContext(),R.anim.flipper_right_slide_in),
						AnimationUtils.loadAnimation(viewFlipper.getContext(),R.anim.flipper_right_slide_out));
			}
			
			@Override
			public void showNextAnimation() {  //加载左滑动画
				viewFlipper.showAnimation(AnimationUtils.loadAnimation(viewFlipper.getContext(),R.anim.flipper_left_slide_in), 
						AnimationUtils.loadAnimation(viewFlipper.getContext(),R.anim.flipper_left_slide_out));
			}
		});
		
		viewFlipper.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pageIndex) {
				for (int i = 0; i < dotImagesArray.length; i++) {
					dotImagesArray[pageIndex].setBackgroundResource(R.drawable.point_pressed);
					if (pageIndex != i) {
						dotImagesArray[i].setBackgroundResource(R.drawable.point_unpressed);
					}
				}
			}
		});
	}
}
