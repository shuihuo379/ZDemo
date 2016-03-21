package com.itheima.ui;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.itheima.demo.R;
import com.itheima.fragment.BaseStandardFragment;
import com.itheima.fragment.FiveCarRadioFragment;
import com.itheima.fragment.SevenCarRadioFragment;

public class RadioFragmentActivity extends Activity implements BaseStandardFragment.OnFragmentInteractionListener{
	private RadioGroup rg_tab;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.radio_fragment_activity);
		init();
	}
	
	private void init(){
		rg_tab = (RadioGroup)findViewById(R.id.rg_tab);
		rg_tab.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == R.id.five_radio_btn){  //选中五座车的RadioButton
					replaceFragment(FiveCarRadioFragment.class);
				}else if(checkedId == R.id.seven_radio_btn){  //选中七座车的RadioButton
					replaceFragment(SevenCarRadioFragment.class);
				}
			}
		});
		replaceFragment(FiveCarRadioFragment.class);  //默认是五座车的Fragment
	}
	
	private <T> void replaceFragment(Class<T> cls){
		try{
			T fragment = BaseStandardFragment.newInstance(cls);
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			if(fragment instanceof BaseStandardFragment){
				BaseStandardFragment bs_fragment = (BaseStandardFragment) fragment;
				transaction.replace(R.id.fragment_container,bs_fragment);
			}
			transaction.commit();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
	}
}
