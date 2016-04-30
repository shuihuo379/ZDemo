package com.itheima.sortlistview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.itheima.demo.R;
import com.itheima.sortlistview.SideBarView.OnTouchingLetterChangedListener;
import com.itheima.util.T;

/**
 * 按字母先后顺序呈现信息
 * @author zhangming
 */
public class SortListViewActivity extends Activity{
	private ListView sortListView;
	private SideBarView sideBar;
	private TextView dialog;
	private SortAdapter mAdapter;
	private ClearEditTextView mClearEditText;
	
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sort_list_view_activity);
		initViews();
		setListener();
	}
	
	private void initViews() {
		characterParser = CharacterParser.getInstance(); //实例化汉字转拼音类
		sortListView = (ListView) findViewById(R.id.country_lvcountry);
		
		SourceDateList = filledData(getResources().getStringArray(R.array.date));
		sortCollections(SourceDateList); // 根据a-z进行排序源数据
		mAdapter = new SortAdapter(this, SourceDateList);
		sortListView.setAdapter(mAdapter);
		
		sideBar = (SideBarView) findViewById(R.id.sideBar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		
		mClearEditText = (ClearEditTextView) findViewById(R.id.filter_edit);
	}
	
	private void setListener(){
		sortListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//这里要利用adapter.getItem(position)来获取当前position所对应的对象
				T.show(getApplication(),((SortModel)mAdapter.getItem(position)).getName());
			}
		});
		
		//根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				//当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
		//设置右侧字母条触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				int firstPosition = mAdapter.getPositionForSection(s.charAt(0)); //该字母首次出现的位置
				if(firstPosition != -1){
					sortListView.setSelection(firstPosition);
				}
			}
		});
	}
	
	private void sortCollections(List<SortModel> mList){
		// 根据a-z进行排序
		Collections.sort(mList,new Comparator<SortModel>() {
			@Override
			public int compare(SortModel o1, SortModel o2) {
				//'@'排在最前面,'#'排在最后面
				if (o1.getSortLetters().equals("@")
						|| o2.getSortLetters().equals("#")) {
					return -1;
				} else if (o1.getSortLetters().equals("#")
						|| o2.getSortLetters().equals("@")) {
					return 1;
				} else {
					return o1.getSortLetters().compareTo(o2.getSortLetters());
				}
			}
		});
	}
	
	/**
	 * 为ListView填充数据
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String[] date){
		List<SortModel> mSortList = new ArrayList<SortModel>();
		
		for(int i=0; i<date.length; i++){
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			//汉字转换成拼音
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			
			// 正则表达式，判断首字母是否是英文字母
			if(sortString.matches("[A-Z]")){
				sortModel.setSortLetters(sortString.toUpperCase());
			}else{
				sortModel.setSortLetters("#");
			}
			
			mSortList.add(sortModel);
		}
		return mSortList;
		
	}
	
	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * @param filterStr
	 */
	private void filterData(String filterStr){
		List<SortModel> filterDateList = new ArrayList<SortModel>();
		
		if(TextUtils.isEmpty(filterStr)){
			filterDateList = SourceDateList;
		}else{
			filterDateList.clear();
			for(SortModel sortModel : SourceDateList){
				String name = sortModel.getName();
				if(name.indexOf(filterStr.toString()) != -1 || characterParser.getSelling(name).startsWith(filterStr.toString())){
					filterDateList.add(sortModel);
				}
			}
		}
		
		sortCollections(filterDateList); // 根据a-z进行排序
		mAdapter.updateListView(filterDateList); //刷新适配器,更新数据
	}
}
