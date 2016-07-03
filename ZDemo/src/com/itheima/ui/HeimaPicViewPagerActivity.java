package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.itheima.demo.R;
import com.itheima.view.HeimaPicScrollView;
import com.itheima.view.HeimaPicScrollView.MyPageChangedListener;

/**
 * 研究黑马课程中仿ViewPager实现图像切换
 * 重点 在自定义控件的几个核心API
 * @author zhangming
 * @date 2016/06/25
 */
public class HeimaPicViewPagerActivity extends Activity{
	private HeimaPicScrollView picScrollView;
	private RadioGroup radioGroup;
	//图片资源ID数组
	private int[] ids = new int[]{R.drawable.a1,R.drawable.a2,R.drawable.a3,R.drawable.a4,R.drawable.a5,R.drawable.a6};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heima_pic_viewpager_activity);
		initView();
		setListener();
	}
	
	private void initView(){
		picScrollView = (HeimaPicScrollView) findViewById(R.id.pic_scrollview);
		radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
		
		//添加图片
		for (int i = 0; i < ids.length; i++) {
			ImageView image = new ImageView(this);
			image.setBackgroundResource(ids[i]);
			picScrollView.addView(image);
		}
		
		//给自定义viewGroup添加测试的布局
		View tempView = getLayoutInflater().inflate(R.layout.heima_touch_temp_page, null);
		picScrollView.addView(tempView,2);
		
		//添加radioButton,数量为picScrollView孩子的数目
		for (int i = 0; i < picScrollView.getChildCount(); i++) {
			RadioButton rbtn = new RadioButton(this);
			rbtn.setId(i);
			if(i == 0){
			   rbtn.setChecked(true);
			}
			radioGroup.addView(rbtn); 
		}
	}
	
	private void setListener(){
		picScrollView.setPageChangedListener(new MyPageChangedListener() {
			@Override
			public void moveToDest(int currid) {
				RadioButton rbtn = (RadioButton) radioGroup.getChildAt(currid);
				rbtn.setChecked(true);  //修改对应的RadioButton的选中焦点
			}
		});
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				picScrollView.moveToDest(checkedId);
			}
		});
	}
}
