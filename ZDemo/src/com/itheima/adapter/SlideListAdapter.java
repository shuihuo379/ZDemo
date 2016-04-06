package com.itheima.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itheima.demo.R;

public class SlideListAdapter extends BaseAdapter{
	private Context context;
	private List<String> mList;
	
	public SlideListAdapter(Context context,List<String> mList) {
		this.context = context;
		this.mList = mList;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if(convertView == null){
			convertView = LayoutInflater.from(context).inflate(R.layout.slide_list_view_item,parent,false);
			holder = new ViewHolder();
			holder.tv_list_item = (TextView) convertView.findViewById(R.id.list_item);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv_list_item.setText(mList.get(position));
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView tv_list_item;
	}
}


