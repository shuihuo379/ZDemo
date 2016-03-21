package com.itheima.fragment;

import com.itheima.demo.R;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/** Ƕ��Fragment */
public class FragmentParent extends Fragment{
	public static final FragmentParent newInstance(int position) {
		FragmentParent f = new FragmentParent();
		Bundle args = new Bundle(2);
		args.putInt("position", position);
		f.setArguments(args);
		return f;
	}

	@SuppressLint("ValidFragment")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View convertView = inflater.inflate(R.layout.viewpager_fragments,container, false);
		ViewPager pager = (ViewPager) convertView.findViewById(R.id.pager);

		final int parent_position = getArguments().getInt("position");
		// ע������Ĵ���
		pager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
			@Override
			public Fragment getItem(final int position) {
				return new Fragment() {
					@Override
					public View onCreateView(LayoutInflater inflater,
							ViewGroup container, Bundle savedInstanceState) {
						TextView convertView = new TextView(getActivity());
						convertView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.FILL_PARENT));
						convertView.setGravity(Gravity.CENTER);
						convertView.setTextSize(30);
						convertView.setTextColor(Color.BLACK);
						convertView.setText("PageParent " + parent_position+"\n"+"PageChild "+position);
						return convertView;
					}
				};
			}

			@Override
			public int getCount() {
				return 3;
			}

			@Override
			public CharSequence getPageTitle(int position) {
				return "Page " + parent_position + " - " + position;
			}

		});

		return convertView;
	}
}
