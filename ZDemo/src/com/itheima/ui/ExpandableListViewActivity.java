package com.itheima.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itheima.demo.R;
import com.itheima.expandablelistview.PinnedHeaderExpandableListView;
import com.itheima.expandablelistview.PinnedHeaderExpandableListView.HeaderAdapter;

public class ExpandableListViewActivity extends Activity{
	private PinnedHeaderExpandableListView explistview;
	private PinnedHeaderExpandableAdapter adapter;
	
	private String[] groupData=new String[]{"文艺体育类","专业学习类","兴趣爱好类","科技学术类","公益服务类"};
	private String[][]childrenData={
			{"蓝爵台球协会","象棋协会","葫芦丝协会","瑜伽协会"},
			{"俄语协会","法律协会","广场英语","历史协会","白桦林文学社","会计协会","心理协会","新闻协会",
			"乒乓球协会","网球协会","营销协会","摄影协会","舞蹈协会","英语协会","诗歌朗诵协会","影像协会",
			"书法协会","经典研读","武术协会"},
			{"口才协会","同缘会","新浪微博协会","健康与安全协会","晨读协会","飘e族","迷你软件办公协会",
			 "魔术爱好者协会","成功协会","共知社","国学与现代人生协会","北极星青年协会","花艺协会","动漫社",
			 "手绘POP爱好者协会","时光计算机网络","智联协会","手语协会","未来教师发展协会","绿园环保协会",
			 "创业协会","e族街舞","项目策划协会","情商培养协会","电影协会","青年旅游协会","广告创意协会",
			 "读书协会","演说协会"},
			 {"电子竞技协会"},
			 {"大学生社会实践协会","公益创意协会"}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.expandable_listview_activity);
		explistview = (PinnedHeaderExpandableListView)findViewById(R.id.explistview);
		
		//设置悬浮头部VIEW
		explistview.setHeaderView(getLayoutInflater().inflate(R.layout.expandable_listview_header, explistview,false));
		adapter = new PinnedHeaderExpandableAdapter(childrenData, groupData, getApplicationContext(),explistview);
		explistview.setAdapter(adapter);
	}
	
	class PinnedHeaderExpandableAdapter extends BaseExpandableListAdapter implements HeaderAdapter{
		private String[] groupData;
		private String[][] childrenData;
		private PinnedHeaderExpandableListView listView;
		private LayoutInflater inflater;
		private Context context;
		
		public PinnedHeaderExpandableAdapter(String[][] childrenData,String[] groupData
				,Context context,PinnedHeaderExpandableListView listView){
			this.groupData = groupData; 
			this.childrenData = childrenData;
			this.context = context;
			this.listView = listView;
			inflater = LayoutInflater.from(this.context);
		}
		
		@Override
		public int getGroupCount() {
			return groupData.length;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childrenData[groupPosition].length;
		}

		@Override
		public Object getGroup(int groupPosition) {
			return groupData[groupPosition];
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return childrenData[groupPosition][childPosition];
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = null;  
	        if (convertView != null) {  
	            view = convertView;  
	        } else {  
	            view = inflater.inflate(R.layout.expandable_listview_group, null);
	        } 
	        
	        ImageView iv = (ImageView)view.findViewById(R.id.groupIcon);
			if (isExpanded) {
				iv.setImageResource(R.drawable.pull_refresh_arrow);
			}
			else{
				iv.setImageResource(R.drawable.right_arrow);
			}
	        
	        TextView text = (TextView)view.findViewById(R.id.groupto);
	        text.setText(groupData[groupPosition]);  
	        return view;  
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = null;  
	        if (convertView != null) {  
	            view = convertView;  
	        } else {  
	            view = inflater.inflate(R.layout.expandable_listview_child, null);
	        }  
	        TextView text = (TextView)view.findViewById(R.id.childto);
	        text.setText(childrenData[groupPosition][childPosition]); 
	        return view;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		@Override
		public int getHeaderState(int groupPosition, int childPosition) {
			final int childCount = getChildrenCount(groupPosition);
			if (childPosition == childCount - 1) {
				return PINNED_HEADER_PUSHED_UP;
			} else if (childPosition == -1
					&& !listView.isGroupExpanded(groupPosition)) {
				return PINNED_HEADER_GONE;
			} else {
				return PINNED_HEADER_VISIBLE;
			}
		}

		@Override
		public void configureHeader(View header, int groupPosition,
				int childPosition, int alpha) {
			String groupData =  this.groupData[groupPosition];
			((TextView) header.findViewById(R.id.groupto)).setText(groupData);
		}

		private SparseIntArray groupStatusMap = new SparseIntArray(); //取代HashMap
		
		@Override
		public void setGroupClickStatus(int groupPosition, int status) {
			groupStatusMap.put(groupPosition, status); 
		}

		@Override
		public int getGroupClickStatus(int groupPosition) {
			if (groupStatusMap.keyAt(groupPosition)>=0) {
				return groupStatusMap.get(groupPosition);
			} else {
				return 0;
			}
		}
	}
}
