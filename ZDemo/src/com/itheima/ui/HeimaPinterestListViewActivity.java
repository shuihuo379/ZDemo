package com.itheima.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.itheima.demo.R;

/**
 * 黑马版瀑布流效果,研究touch事件的分发机制
 * @author zhangming
 * @date 2016/06/26
 */
public class HeimaPinterestListViewActivity extends Activity{
	private int ids[] = new int[] { R.drawable.a1, R.drawable.a2,
			R.drawable.a3, R.drawable.a4,R.drawable.a5,R.drawable.a6};
	private ListView lv1,lv2,lv3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.heima_pinterest_listview_activity);
		initView();
	}

	private void initView() {
		lv1 = (ListView) findViewById(R.id.lv1);
		lv2 = (ListView) findViewById(R.id.lv2);
		lv3 = (ListView) findViewById(R.id.lv3);
	
		lv1.setAdapter(new MyAdapter());
		lv2.setAdapter(new MyAdapter());
		lv3.setAdapter(new MyAdapter());
	}
	
	class MyAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = (ImageView) View.inflate(getApplicationContext(),R.layout.heima_pinterest_item, null);
			int resId = (int) (Math.random() * (ids.length));
			iv.setImageResource(ids[resId]);
			return iv;
		}
	}
}
