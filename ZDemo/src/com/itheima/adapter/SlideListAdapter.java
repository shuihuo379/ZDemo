package com.itheima.adapter;

import java.util.List;

import com.itheima.demo.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

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
			holder.del_one_btn = (Button) convertView.findViewById(R.id.del_one_btn);
			holder.del_two_btn = (Button) convertView.findViewById(R.id.del_two_btn);
			convertView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.tv_list_item.setText(mList.get(position));
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView tv_list_item;
		Button del_one_btn;
		Button del_two_btn;
	}
}


