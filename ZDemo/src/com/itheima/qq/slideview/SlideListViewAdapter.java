package com.itheima.qq.slideview;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itheima.demo.R;
import com.itheima.qq.slideview.SlideView.OnSlideListener;

public class SlideListViewAdapter extends BaseAdapter{
	private Activity mActivity;
	private List<MessageItem> mList;
	private SlideView mLastSlideViewStatusOn;
	private onRemoveItemListener listener;
	
	public SlideListViewAdapter(Activity mActivity,List<MessageItem> mList) {
		this.mActivity = mActivity;
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
		SlideView slideView = (SlideView) convertView;
		if(slideView == null){
			View itemView = mActivity.getLayoutInflater().inflate(R.layout.slide_list_view_item, null); //条目view
			slideView = new SlideView(mActivity);
			slideView.setContentView(itemView);  //添加每个条目到自定义LinearLayout容器中 
			holder = new ViewHolder(slideView);
			slideView.setOnSlideListener(new OnSlideListener() {
				@Override
				public void onSlide(View view, int status) {
					if (mLastSlideViewStatusOn != null && mLastSlideViewStatusOn != view) {
						mLastSlideViewStatusOn.shrinkZero();
			        }
			        if (status == SLIDE_STATUS_ON) {
			        	mLastSlideViewStatusOn = (SlideView) view;
			        }
				}
			});
			slideView.setTag(holder);
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.deleteHolder.setTag(position);  //为每个条目的删除标签添加一个位置tag标记(每次删除后刷新视图,会重新设置Tag标记的值,其值为相对于传入进来的List集合中的索引位置)
		
		MessageItem item = mList.get(position);
        item.slideView = slideView;  //初始化每个条目slideView(LinearLayout)
        item.slideView.shrinkZero();
		
		holder.tv_list_item.setText(mList.get(position).info);
		holder.deleteHolder.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (view.getId() == R.id.holder) {
			        int pos = ((Integer)view.getTag()).intValue();
			        listener.removeItem(pos);
			    }
			}
		});
		return slideView;
	}
	
	static class MessageItem {
		String info;
		SlideView slideView;
		
		MessageItem(String info){
			this.info = info;
		}
	}
	
	static class ViewHolder{
		TextView tv_list_item;
		ViewGroup deleteHolder;
		
		ViewHolder(View view) {
			tv_list_item = (TextView)view.findViewById(R.id.list_item);
			deleteHolder = (ViewGroup)view.findViewById(R.id.holder);
		}
	}
	
	/**
	 * 自定义回掉接口,移除指定位置的条目
	 * @author zhangming
	 */
	public interface onRemoveItemListener{
		public void removeItem(int position);
	}
	
	public void setOnRemoveItemListener(onRemoveItemListener listener){
		this.listener = listener;
	}
}
