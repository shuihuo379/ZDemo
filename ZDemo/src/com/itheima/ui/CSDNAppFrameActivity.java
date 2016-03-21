package com.itheima.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.itheima.adapter.CSDNTabPageAdapter;
import com.itheima.demo.R;
import com.viewpager.indicator.TabPageIndicator;

/**
 * CSDN APP UI框架研究(使用ViewPagerIndicator,Fragment实现)
 * @author zhangming
 * @date 2016/03/13
 */
public class CSDNAppFrameActivity extends FragmentActivity{
	private TabPageIndicator mPageIndicator;
	private ViewPager mViewPager;
	private CSDNTabPageAdapter mAdapter;
	public static final String[] TITLES = new String[] { "业界", "移动", "研发", "程序员", "云计算" };
	public List<String> tabList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.csdn_app_frame_activity);
		
		tabList.addAll(Arrays.asList(TITLES));
		mPageIndicator = (TabPageIndicator) findViewById(R.id.id_indicator);
		mViewPager = (ViewPager) findViewById(R.id.id_pager);
		mAdapter = new CSDNTabPageAdapter(getSupportFragmentManager(),tabList);
		mViewPager.setAdapter(mAdapter);
		mPageIndicator.setViewPager(mViewPager,0);
	}
}
