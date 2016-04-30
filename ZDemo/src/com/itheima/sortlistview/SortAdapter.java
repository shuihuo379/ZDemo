package com.itheima.sortlistview;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.itheima.demo.R;

/**
 * 做ListView的字母索引需要用到SectionIndexer接口
 * @author zhangming
 */
public class SortAdapter extends BaseAdapter implements SectionIndexer{
	private List<SortModel> list;
	private Context mContext;
	
	public SortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.sort_list_item, null);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.content);
			viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		//根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		
		//如果当前位置等于该分类首字母的Char的位置 ,则认为是第一次出现(即显示目录字母)
		if(position == getPositionForSection(section)){
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		}else{
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		viewHolder.tvContent.setText(mContent.getName());
		
		return convertView;
	}
	
	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * @param list
	 */
	public void updateListView(List<SortModel> list){
		this.list = list;
		notifyDataSetChanged();
	}
	
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvContent;
	}
	

	@Override
	public Object[] getSections() {
		return null;
	}
	
	/**
	 * 通过该项的位置，获得所在分类组的索引号
	 * @return 返回的索引号为ListView的当前位置获取分类的首字母的Char ascii值
	 */
	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().toUpperCase().charAt(0);
	}

	/**
	 * 根据分类列的索引号获得该序列的首个位置
	 * 索引号为同一个的一组元素可能有多个,取第一个元素所在的位置position作为返回值
	 */
	@Override
	public int getPositionForSection(int sectionIndex) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == sectionIndex) {
				return i;
			}
		}
		return -1;
	}
}
