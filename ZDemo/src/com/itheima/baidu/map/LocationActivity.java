package com.itheima.baidu.map;

import android.os.Bundle;
import android.view.KeyEvent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.itheima.demo.R;

public class LocationActivity extends BaseBaiduMapActivity {
	public LocationClient mLocationClient;
	public BDLocationListener myListener;
	private BitmapDescriptor geo;
	private static int count;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		locate();
	}

	/**
	 * 定位
	 */
	private void locate() {
		mLocationClient = new LocationClient(getApplicationContext());
		myListener = new MyListener();
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(5000);// 设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
		geo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);
		MyLocationConfiguration configuration = new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, geo);
		baiduMap.setMyLocationConfigeration(configuration);// 设置定位显示的模式
		baiduMap.setMyLocationEnabled(true);// 打开定位图层
	}
	
	@Override
	protected void onStart() {
		mLocationClient.start();
		super.onStart();
	}

	@Override
	protected void onPause() {
		mLocationClient.stop();
		super.onPause();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			switch (count%3) {
			case 0:
				// 正常
				baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
						MyLocationConfiguration.LocationMode.NORMAL, true, geo));// 设置定位显示的模式
				count++;
				break;
			case 1:
				// 罗盘
				baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
						MyLocationConfiguration.LocationMode.COMPASS, true, geo));// 设置定位显示的模式
				count++;
				break;
			case 2:
				// 跟随
				baiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
						MyLocationConfiguration.LocationMode.FOLLOWING, true, geo));// 设置定位显示的模式
				count++;
				break;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	class MyListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation result) {
			if (result != null) {
				MyLocationData data = new MyLocationData.Builder()
						.latitude(result.getLatitude())
						.longitude(result.getLongitude()).build();
				baiduMap.setMyLocationData(data);
			}
		}
	}
}
