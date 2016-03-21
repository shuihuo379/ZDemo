package com.itheima.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.itheima.demo.R;
import com.itheima.fragment.DownOneFragment;
import com.itheima.fragment.DownThreeFragment;
import com.itheima.fragment.DownTwoFragment;
import com.itheima.fragment.UpOneFragment;
import com.itheima.fragment.UpThreeFragment;
import com.itheima.fragment.UpTwoFragment;

public class FramePagerActivity extends FragmentActivity{
	private RadioGroup upRadioGroup,bottomRadioGroup;
	
	private UpOneFragment upOneFragment;
	private UpTwoFragment upTwoFragment;
	private UpThreeFragment upThreeFragment;
	
	private DownOneFragment downOneFragment;
	private DownTwoFragment downTwoFragment;
	private DownThreeFragment downThreeFragment;
	
	private List<Fragment> upFragments;
	private List<Fragment> bottomFragments;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frame_pager);
		init();
		initFragment();
		//initPager();
		selectFragment(1);  //默认选择第一个
	}

	private void init() {
		upRadioGroup = (RadioGroup) findViewById(R.id.up_radio_group);
		bottomRadioGroup = (RadioGroup)findViewById(R.id.bottom_radio_group);
		
		upRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int check_btnId = group.getCheckedRadioButtonId();
				switch (check_btnId) {
				case R.id.up_radio1:
					selectFragment(1);
					break;
				case R.id.up_radio2:
					selectFragment(2);
					break;
				case R.id.up_radio3:
					selectFragment(3);
					break;
				}
			}
		});
		
		bottomRadioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int check_btnId = group.getCheckedRadioButtonId();
				switch (check_btnId) {
				case R.id.bottom_radio1:
					selectFragment(1);
					break;
				case R.id.bottom_radio2:
					selectFragment(2);
					break;
				case R.id.bottom_radio3:
					selectFragment(3);
					break;
				}
			}
		});
	}
	
	private void initFragment() {
		upOneFragment = new UpOneFragment();
		upTwoFragment = new UpTwoFragment();
		upThreeFragment = new UpThreeFragment();
		
		downOneFragment = new DownOneFragment();
		downTwoFragment = new DownTwoFragment();
		downThreeFragment = new DownThreeFragment();
		
		upFragments = new ArrayList<Fragment>();  
        upFragments.add(upOneFragment);  
        upFragments.add(upTwoFragment);  
        upFragments.add(upThreeFragment);   
        
        bottomFragments = new ArrayList<Fragment>();
        bottomFragments.add(downOneFragment);  
        bottomFragments.add(downTwoFragment);  
        bottomFragments.add(downThreeFragment);   
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.add(R.id.up_fl_content, upOneFragment);
		transaction.add(R.id.up_fl_content, upTwoFragment);
		transaction.add(R.id.up_fl_content, upThreeFragment);
		transaction.add(R.id.bottom_fl_content, downOneFragment);
		transaction.add(R.id.bottom_fl_content, downTwoFragment);
		transaction.add(R.id.bottom_fl_content, downThreeFragment);
		transaction.commit();
	}
	/**
	private void initPager() {
        MyFragmentPagerAdapter upAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(),upFragments);  
        MyFragmentPagerAdapter bottomAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), bottomFragments);
        ViewPager up_vp = (ViewPager)findViewById(R.id.up_vp);  
        ViewPager bottom_vp = (ViewPager) findViewById(R.id.bottom_vp);
        
        up_vp.setAdapter(upAdapter);  
        bottom_vp.setAdapter(bottomAdapter);  //设定适配器  
	}**/
	
	private void selectFragment(int index){
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		switch (index) {
		case 1:
			transaction.show(upOneFragment).show(downOneFragment).hide(upTwoFragment).hide(downTwoFragment).hide(upThreeFragment).hide(downThreeFragment);
			break;
		case 2:
			transaction.hide(upOneFragment).hide(downOneFragment).show(upTwoFragment).show(downTwoFragment).hide(upThreeFragment).hide(downThreeFragment);
			break;
		case 3:
			transaction.hide(upOneFragment).hide(downOneFragment).hide(upTwoFragment).hide(downTwoFragment).show(upThreeFragment).show(downThreeFragment);
			break;
		}
		transaction.commit();
	}
	
	class MyFragmentPagerAdapter extends FragmentPagerAdapter{
		private List<Fragment> mFragments;  
	      
	    public MyFragmentPagerAdapter(FragmentManager fm,List<Fragment> fragments) {  
	        super(fm);  
	        mFragments=fragments;  
	    }  
		  
	    @Override  
	    public Fragment getItem(int position) {  
	        return mFragments.get(position);  
	    }  
	  
	    @Override  
	    public int getCount() {  
	        return mFragments.size();  
	    }  
	}
}
