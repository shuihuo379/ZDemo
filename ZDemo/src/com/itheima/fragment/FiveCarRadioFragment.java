package com.itheima.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.itheima.adapter.RadioFragmentGridAdapter;
import com.itheima.demo.R;


public class FiveCarRadioFragment extends BaseStandardFragment{
	private Activity mActivity;
	private GridView gv_tag;
	private View rootView;
	private RadioFragmentGridAdapter mAdapter;
	private static String[] names={
		"手机防盗","通讯卫士","软件管理",
		"进程管理","流量统计","手机杀毒",
		"缓存清理","高级工具","设置中心"
	};
	
	//TODO 覆写抽象类BaseStandardFragment中的方法
	@Override
	public View onFragmentCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
	    if(null == rootView) {
            rootView = inflater.inflate(R.layout.car_radio_tab_fragment, container, false);
        }
        if(null != rootView) {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if(parent != null) {
                parent.removeView(rootView);
            }
        }
		this.initView();
		return rootView;
	}
	
	private void initView(){
		mActivity = getActivity();
		gv_tag = (GridView)rootView.findViewById(R.id.gv_tag);
		mAdapter = new RadioFragmentGridAdapter(mActivity,names);
		gv_tag.setAdapter(mAdapter);
	}
}
