package com.itheima.fragment;

import com.itheima.demo.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class FragmentNestActivity extends FragmentActivity implements OnClickListener {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.nested_fragments);

		findViewById(R.id.btnModule1).setOnClickListener(this);
		findViewById(R.id.btnModule2).setOnClickListener(this);
		findViewById(R.id.btnModule3).setOnClickListener(this);

		findViewById(R.id.btnModule1).performClick();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnModule1:
			addFragmentToStack(FragmentParent.newInstance(0));
			break;
		case R.id.btnModule2:
			addFragmentToStack(FragmentParent.newInstance(1));
			break;
		case R.id.btnModule3:
			addFragmentToStack(FragmentParent.newInstance(2));
			break;
		}
	}

	private void addFragmentToStack(Fragment fragment) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		// ft.setCustomAnimations(android.R.anim.slide_in_left,
		// android.R.anim.slide_in_left);
		ft.replace(R.id.fragment_container, fragment);
		ft.commit();
	}
}
