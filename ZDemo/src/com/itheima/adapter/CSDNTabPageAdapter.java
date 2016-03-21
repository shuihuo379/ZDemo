package com.itheima.adapter;

import java.util.List;

import com.itheima.fragment.CSDNAppFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CSDNTabPageAdapter extends FragmentPagerAdapter{
	public static List<String> tabList;
	
	public CSDNTabPageAdapter(FragmentManager fm,List<String> tabList){
		this(fm);
		this.tabList = tabList;
	}
	
	public CSDNTabPageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		CSDNAppFragment mFragment = new CSDNAppFragment(position);
		return mFragment;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return tabList.get(position % getCount());
	}

	@Override
	public int getCount() {
		return tabList.size();
	}
}
