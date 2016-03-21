package com.itheima.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.itheima.adapter.CSDNTabPageAdapter;
import com.itheima.demo.R;

public class CSDNAppFragment extends Fragment{
	private int newsType;

	public CSDNAppFragment(int newsType){
		this.newsType = newsType;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.csdn_app_item_fragment_content, null);
		TextView tip = (TextView) view.findViewById(R.id.id_tip);
		tip.setText(CSDNTabPageAdapter.tabList.get(newsType));
		return view;
	}
}
