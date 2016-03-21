package com.itheima.baidu.map;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.itheima.demo.R;

public class LayerBaiduMapActivity extends Activity{
	private BaiduMap baiduMap;
	private MapView mapview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.base_baidu_map);
		init();
	}

	private void init() {
		mapview = (MapView) findViewById(R.id.bmapView);
		baiduMap = mapview.getMap();
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(15));
	}

	private void initManager() {
		SDKInitializer.initialize(getApplicationContext());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 底图 交通图 卫星图
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			// 底图
			// 设置地图类型 MAP_TYPE_NORMAL 普通图； MAP_TYPE_SATELLITE 卫星图
			baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
			baiduMap.setTrafficEnabled(false);
			break;
		case KeyEvent.KEYCODE_2:
			// 卫星图
			baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
			baiduMap.setTrafficEnabled(false);
			break;
		case KeyEvent.KEYCODE_3:
			// 交通图
			// 是否打开交通图层
			baiduMap.setTrafficEnabled(true);
			break;

		default:
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		mapview.onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		mapview.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		mapview.onPause();
		super.onPause();
	}
}
