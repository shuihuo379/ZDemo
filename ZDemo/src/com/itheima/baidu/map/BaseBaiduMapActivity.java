package com.itheima.baidu.map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.itheima.demo.R;

public class BaseBaiduMapActivity extends Activity{
	protected MapView mMapView;
	protected BaiduMap baiduMap;
	protected MyBaiduSDKReceiver receiver;
	protected LatLng latLng = new LatLng(40.065796,116.349868);  //参数1纬度,参数2经度
	protected static final int defaultLevel = 15;  //自定义默认的级别15
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.base_baidu_map);
		initView();
	}


	private void initManager() {
		receiver = new MyBaiduSDKReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);  //注册网络错误
		filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR); //注册key校验结果
		registerReceiver(receiver, filter);
	}
	
	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapView);  	// 获取地图控件引用
		baiduMap = mMapView.getMap();  //管理具体的某一个MapView对象,缩放,旋转,平移
		
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(defaultLevel);  //默认级别12
		baiduMap.setMapStatus(mapStatusUpdate);  //设置缩放级别
		
		mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);  //参数1纬度,参数2经度
		baiduMap.setMapStatus(mapStatusUpdate);
		
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(false);  //默认是true,显示标尺
	}
	
	class MyBaiduSDKReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getAction();
			if(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR.equals(result)){
				//网络错误
				Toast.makeText(getApplicationContext(),"无网络",0).show();
			}else if(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR.equals(result)){
				//key校验失败
				Toast.makeText(getApplicationContext(),"key校验失败",0).show();
			}
		}
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 旋转，移动，缩放
		switch (keyCode) {
		case KeyEvent.KEYCODE_1:
			//放大地图缩放级别 每次放大一个级别
			MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomIn();
			baiduMap.setMapStatus(mapStatusUpdate);
			break;
		case KeyEvent.KEYCODE_2:
			//缩小地图缩放级别 每次缩小一个级别
			MapStatusUpdate zooOutStatus = MapStatusUpdateFactory.zoomOut();
			baiduMap.setMapStatus(zooOutStatus);
			break;
		case KeyEvent.KEYCODE_3:
			// 以一个点为中心 旋转
			MapStatus mapStatus = baiduMap.getMapStatus();// 获取地图的当前状态
			float rotate = mapStatus.rotate;
			Log.d("test", "rotate:" + rotate);
			MapStatus rotateStatus = new MapStatus.Builder().rotate(rotate+30).build(); // 旋转 范围0~360
			MapStatusUpdate rotateStatusUpdate = MapStatusUpdateFactory.newMapStatus(rotateStatus);
			baiduMap.setMapStatus(rotateStatusUpdate);
		case KeyEvent.KEYCODE_4:
			// 已一条直线 为轴 旋转 Overlooking 俯角
			MapStatus mapStatusOver = baiduMap.getMapStatus();// 获取地图的当前状态
			float overlook = mapStatusOver.overlook;
			Log.d("test", "overlook:" + overlook);
			MapStatus overStatus = new MapStatus.Builder().overlook(overlook-5).build();  	// 0~45
			MapStatusUpdate overStatusUpdate = MapStatusUpdateFactory.newMapStatus(overStatus);
			baiduMap.setMapStatus(overStatusUpdate);
			break;
		case KeyEvent.KEYCODE_5:
			// 移动
			MapStatusUpdate moveStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
			baiduMap.animateMapStatus(moveStatusUpdate); // 带动画的更新地图状态 耗时300毫秒
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}
}
