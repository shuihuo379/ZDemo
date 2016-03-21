package com.itheima.baidu.map;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.MapViewLayoutParams.ELayoutMode;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.itheima.demo.R;

public class MarkerDemoActivity extends Activity{
	public LocationClient mLocationClient;
	protected MapView mMapView;
	protected BaiduMap baiduMap;
	protected MyBaiduSDKReceiver receiver;
	protected MyListener myListener;
	private LatLng mLatLng;
	private View pop;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.location_demo);
		initView();
		locate();
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
		
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(19);  //默认级别12
		baiduMap.setMapStatus(mapStatusUpdate);  //设置缩放级别
		
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);  //默认是true,显示标尺
	}
	
	private void locate() {
		mLocationClient = new LocationClient(getApplicationContext());
		myListener = new MyListener();
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(100);// 设置发起定位请求的间隔时间
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
//		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.setNeedDeviceDirect(false);
		option.setOpenGps(true);  //设置打开GPS
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		
		MyLocationConfiguration configuration = new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true,
				BitmapDescriptorFactory.fromResource(R.drawable.icon_geo));
		baiduMap.setMyLocationConfigeration(configuration);// 设置定位显示的模式
		baiduMap.setMyLocationEnabled(true);// 打开定位图层p
		baiduMap.getUiSettings().setCompassEnabled(false);  //不显示指南针
	}
	
	/**
	 * 根据经纬度定位
	 * @param latitude
	 * @param longitude
	 */
	private void locate(double latitude,double longitude){
		baiduMap.clear();
	    MyLocationData data = new MyLocationData.Builder().latitude(latitude).longitude(longitude).accuracy(0).build();
		baiduMap.setMyLocationData(data);
	}
	
	private void initPop(LatLng latLng,boolean isVisible) {
		pop = View.inflate(getApplicationContext(),R.layout.pop,null);
		LayoutParams params = new MapViewLayoutParams.Builder().layoutMode(ELayoutMode.mapMode)  //按照经纬度设置位置(设置成mapMode时,此时position传值不能为空)
				.position(latLng).width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT)
				.yOffset(-10).build();  //距离position的像素,向下是正值,向上是负值
		mMapView.addView(pop,params);
		if(isVisible){
			pop.setVisibility(View.VISIBLE);
		}else{
			pop.setVisibility(View.INVISIBLE);
		}
	}
	
	private void drawMarker(LatLng latLng,final String address,boolean isDraw) {
		TextView title = (TextView) pop.findViewById(R.id.title);
		title.setText(address);
		
		if(isDraw){
			baiduMap.clear();
			ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
			MarkerOptions markerOptions = new MarkerOptions();
			bitmaps.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_geo));
			markerOptions.position(latLng).icons(bitmaps).draggable(true).title(address);
			baiduMap.addOverlay(markerOptions);
		}
	}
	
	class MyListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation result) {
			if (result != null) {
				mLatLng = new LatLng(result.getLatitude(), result.getLongitude());
				locate(result.getLatitude(),result.getLongitude());
				//手动添加定位的弹出窗
				initPop(mLatLng,true);
				Log.i("test",result.getAddrStr());
				drawMarker(mLatLng,result.getAddrStr(),false);
			}
		}
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
	protected void onDestroy() {
		super.onDestroy();
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
