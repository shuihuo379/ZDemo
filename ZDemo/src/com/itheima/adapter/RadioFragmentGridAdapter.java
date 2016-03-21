package com.itheima.adapter;

import java.util.List;

import com.itheima.demo.R;
import com.itheima.ui.RadioFragmentActivity;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

public class RadioFragmentGridAdapter extends BaseAdapter{
	private String[] names;
	private Activity mActivity;
	
	public RadioFragmentGridAdapter(Activity mActivity,String[]names){
		this.names = names;
		this.mActivity = mActivity;
	}
	
	@Override
	public int getCount() {
		return names.length;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view=View.inflate(mActivity,R.layout.rg_tab_grid_item, null);
		Button btn = (Button)view.findViewById(R.id.rg_btn);
		btn.setText(names[position]);
		return view;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}
