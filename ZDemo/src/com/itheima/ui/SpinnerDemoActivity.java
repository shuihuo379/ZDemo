package com.itheima.ui;

import java.util.Arrays;



import com.itheima.demo.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;


public class SpinnerDemoActivity extends Activity {
	private String[] grades = new String[] { "aaa", "bbb", "ccc", "ddd", "eee" };
	private Spinner gradeSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.spinner_activity);
		gradeSpinner = (Spinner) findViewById(R.id.gradeSpinner);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				R.layout.spinner_check_text, Arrays.asList(grades)) {
			@Override
			public View getDropDownView(int position, View convertView,
					ViewGroup parent) {
				View view = View.inflate(getContext(),R.layout.spinner_item_layout, null);
				TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
				label.setText(Arrays.asList(grades).get(position));
				if (gradeSpinner.getSelectedItemPosition() == position) {  //选中变色
					view.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
				} else {
					view.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
				}
				return view;
			}
		};
		adapter.setDropDownViewResource(R.layout.spinner_item_layout);
		
		gradeSpinner.setAdapter(adapter);
	}
}
