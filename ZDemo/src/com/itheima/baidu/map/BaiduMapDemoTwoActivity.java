package com.itheima.baidu.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.itheima.demo.R;
import com.itheima.util.StringUtil;

public class BaiduMapDemoTwoActivity extends Activity implements OnClickListener{
	public LocationClient mLocationClient;
	protected MapView mMapView;
	protected BaiduMap baiduMap;
	protected MyBaiduSDKReceiver receiver;
	private double latitude = -1;
	private double longitude = -1;
	private MyListener myListener;
	private Button locationBtn,distanceBtn,showBtn,hiddenBtn,ok_oneBtn,ok_twoBtn;
	private LatLng minDistanceLatLng;
	private String localAddr;  //自己定位的位置
	private String mAddress;  //另一方的地址
	private Map<LatLng,View> popViews = new HashMap<LatLng,View>();  //弹出窗引用收集
	private EditText et_addrname,et_target_addrname,et_geo;
	private String flag;  //标志位,标志点击确定是执行哪一项
	private LinearLayout ll_position,ll_geo,ll_target_position;
	private List<GeoCodeResult> mList;  //测距时两个点的信息
	private LatLng la1,la2;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.baidumap_two_activity);
		initView();
	}
	
	private void initView() {
		mList = new ArrayList<GeoCodeResult>();
		
		locationBtn = (Button) findViewById(R.id.locationBtn);
		distanceBtn = (Button) findViewById(R.id.distanceBtn);
		showBtn = (Button) findViewById(R.id.show);
		hiddenBtn = (Button) findViewById(R.id.hidden);
		ok_oneBtn = (Button) findViewById(R.id.ok_one);
		et_addrname = (EditText) findViewById(R.id.et_addrname);
		et_target_addrname = (EditText) findViewById(R.id.et_target_addrname);
		ll_position = (LinearLayout) findViewById(R.id.ll_position);
		ll_target_position = (LinearLayout) findViewById(R.id.ll_target_position);
		
		mMapView = (MapView) findViewById(R.id.bmapView);  	// 获取地图控件引用
		baiduMap = mMapView.getMap();  //管理具体的某一个MapView对象,缩放,旋转,平移
		
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(baiduMap.getMaxZoomLevel());  //默认级别12
		baiduMap.setMapStatus(mapStatusUpdate);  //设置缩放级别
		
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);  //默认是true,显示标尺
		
		locationBtn.setOnClickListener(this);
		distanceBtn.setOnClickListener(this);
		showBtn.setOnClickListener(this);
		hiddenBtn.setOnClickListener(this);
		ok_oneBtn.setOnClickListener(this);
		ok_twoBtn.setOnClickListener(this);
		
		/**
		baiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			@Override
			public void onMapStatusChangeStart(MapStatus result) {
				if(mList!=null && mList.size()>0){
					la1 = mList.get(0).getLocation();
					la2 = mList.get(1).getLocation();
				}
			}
			
			@Override
			public void onMapStatusChangeFinish(MapStatus result) {
				if(la1!=null && la2!=null){
					if(popViews.size()>0){
						hiddenPop();
						popViews.clear();
					}
					initPop(la1,mList.get(0).getAddress(),true);
					initPop(la2,mList.get(1).getAddress(),true);
				}
			}
			
			@Override
			public void onMapStatusChange(MapStatus result) {
				if(la1!=null && la2!=null){
					if(popViews.size()>0){
						hiddenPop();
						popViews.clear();
					}
					initPop(la1,mList.get(0).getAddress(),true);
					initPop(la2,mList.get(1).getAddress(),true);
				}
			}
		});**/
	}

	private void initManager() {
		receiver = new MyBaiduSDKReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);  //注册网络错误
		filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR); //注册key校验结果
		registerReceiver(receiver, filter);
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
	
	class MyListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation result) {
			if (result != null) {
				latitude = result.getLatitude();
				longitude = result.getLongitude();
				localAddr = result.getAddrStr();  
				Log.i("test","latitude: "+latitude+", longitude: "+longitude);
				locate(latitude, longitude);
				if(mLocationClient!=null && mLocationClient.isStarted()){
					Log.i("test","收到定位地址信息,停止...");
					mLocationClient.stop();
				}
			}
		}
	}
	
	class MyPoiSearchListener implements OnGetPoiSearchResultListener{
		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {
			
		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			if(result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {  
				Toast.makeText(getApplicationContext(), "未找到结果",0).show();
			    return;  
			}  
		    if (result.error == SearchResult.ERRORNO.NO_ERROR) {  
		        List<PoiInfo> poiList = result.getAllPoi();
		        setMinDistance(poiList);
		    }
		}
	}
	
	class MyGeoCoderListener implements OnGetGeoCoderResultListener{
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //没有检索到结果  
				Toast.makeText(getApplicationContext(), "未搜索到结果", 0).show();
				return;
	        }
			if("location".equals(flag)){  //定位
				latitude = result.getLocation().latitude; 
				longitude = result.getLocation().longitude;  //初始化搜索的位置的经纬度
		        //获取地理编码结果,并执行定位
				locate(result.getLocation().latitude,result.getLocation().longitude);
			}else if("distance".equals(flag)){ //测距
				if(mList.size()<2){
					mList.add(result);
				}
				return;
			}
			ll_position.setVisibility(View.GONE);
			ll_target_position.setVisibility(View.GONE);
			ll_geo.setVisibility(View.GONE);  //定位成功后恢复隐藏状态
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			
		}
	}
	
	/**
	 * 取出最小距离,并用标志物标注
	 * @param poiList
	 */
	public void setMinDistance(List<PoiInfo> poiList){
		 double minDistence = Long.MAX_VALUE;
		 int index = 0;
		 for(int i=0;i<poiList.size();i++){
	        double distence = getDistance(new LatLng(latitude, longitude),poiList.get(i).location);
			if(distence < minDistence){
				minDistence = distence;
				index = i;
			}
	     }
		 
		 minDistanceLatLng = poiList.get(index).location;
		 mAddress = poiList.get(index).address;
		 baiduMap.clear();
		 MarkerOptions markerOptions = new MarkerOptions();
		 drawMarker(minDistanceLatLng,markerOptions);
		 
		 Log.i("test","最短距离: "+minDistence+" 距离最小的位置: 纬度:"+minDistanceLatLng.latitude+" 经度:"+minDistanceLatLng.longitude);
		 Toast.makeText(getApplicationContext(),"两点之间的距离: "+String.valueOf((int)minDistence),0).show();
	}
	
	private void initPop(LatLng latLng,final String address,boolean isVisible) {
		View pop = View.inflate(getApplicationContext(),R.layout.pop,null);
		TextView title = (TextView) pop.findViewById(R.id.title);
		title.setText(address);
//		LayoutParams params = new MapViewLayoutParams.Builder().layoutMode(ELayoutMode.mapMode)  //按照经纬度设置位置(设置成mapMode时,此时position传值不能为空)
//				.position(latLng).width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT)
//				.yOffset(-5).build();  //距离position的像素,向下是正值,向上是负值
//		mMapView.addView(pop,params);
//		if(isVisible){
//			pop.setVisibility(View.VISIBLE);
//		}else{
//			pop.setVisibility(View.INVISIBLE);
//		}
		InfoWindow infoWindow = new InfoWindow(pop,latLng,-10);
		baiduMap.showInfoWindow(infoWindow);
		popViews.put(latLng, pop);  //添加弹出窗引用
	}
	
	private void drawMarker(LatLng latLng,MarkerOptions markerOptions) {
		ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
		bitmaps.add(BitmapDescriptorFactory.fromResource(R.drawable.eat_icon));
		markerOptions.position(latLng).icons(bitmaps).draggable(false);
		baiduMap.addOverlay(markerOptions);
	}
	
	
	/**
	 * 根据经纬度定位
	 * @param latitude
	 * @param longitude
	 */
	private void locate(double latitude,double longitude){
		baiduMap.clear();
	    MyLocationData data = new MyLocationData.Builder().latitude(latitude).longitude(longitude).build();
		baiduMap.setMyLocationData(data);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		if(popViews.size()>0){
			popViews.clear();
			popViews = null;
		}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.locationBtn:  //定位
			mList.clear();
			if(popViews.size()>0){
				hiddenPop();
			}
			et_addrname.setText("");
			et_geo.setText("");
			locate(); 
			showHiddenLayout("location");
			break;
		case R.id.distanceBtn:  //测距
//			distance("餐厅",1000);
			mList.clear();
			if(popViews.size()>0){
				hiddenPop();
			}
			et_addrname.setText("");
			et_target_addrname.setText("");
			et_geo.setText("");
			showHiddenLayout("distance");
			break;
		case R.id.show:  //显示pop
//			showPop();
			if(mList.size() != 2){
				Toast.makeText(getApplicationContext(), "请先测距",0).show();
				return;
			}
			if(popViews.size()>0){
				hiddenPop();
				popViews.clear();
			}
			initPop(mList.get(0).getLocation(),mList.get(0).getAddress(),true);
			initPop(mList.get(1).getLocation(),mList.get(1).getAddress(),true);
			break;
		case R.id.hidden:  //隐藏pop
			if(popViews.size()>0){
				hiddenPop();
			}
			break;
		case R.id.ok_one: //地名搜索
			SearchByPosition(this.flag);
			break;
		}
	}
	
	private void showHiddenLayout(String flag){
		ll_position.setVisibility(View.VISIBLE);
		ll_geo.setVisibility(View.VISIBLE);
		if("distance".equals(flag)){
			ll_target_position.setVisibility(View.VISIBLE);
		}else{
			ll_target_position.setVisibility(View.GONE);
		}
		this.flag = flag;
	}
	
	
	/**
	 * 通过地名定位，测距等
	 * @param flag
	 */
	private void SearchByPosition(String flag){
		if("location".equals(flag)){  //定位
			String targetAddress = et_addrname.getText().toString().trim();
			if(StringUtil.isEmpty(targetAddress)){
	    		Toast.makeText(getApplicationContext(), "位置不为空", 0).show();
	    		return;
	    	}
			GeoCoder mSearch = GeoCoder.newInstance();
			MyGeoCoderListener listener = new MyGeoCoderListener();
	    	mSearch.setOnGetGeoCodeResultListener(listener);
			mSearch.geocode(new GeoCodeOption().city(targetAddress).address(targetAddress));  //发起位置请求
		
		}else if("distance".equals(flag)){ //测距
			String targetAddress = et_addrname.getText().toString().trim();
			String targetTwoAddress = et_target_addrname.getText().toString().trim();
			if(StringUtil.isEmpty(targetAddress) || StringUtil.isEmpty(targetTwoAddress)){
	    		Toast.makeText(getApplicationContext(), "位置不为空", 0).show();
	    		return;
	    	}
			if(mList.size()==2){  //集合满，两点取到
				baiduMap.clear();
				MarkerOptions markerOptions = new MarkerOptions();
				drawMarker(mList.get(0).getLocation(),markerOptions);
				drawMarker(mList.get(1).getLocation(),markerOptions);
				
				int distance = (int)getDistance(mList.get(0).getLocation(), mList.get(1).getLocation());
				Log.i("test","===>"+mList.get(0).getLocation().latitude+" "+mList.get(1).getLocation().latitude);
				Toast.makeText(getApplicationContext(),"两点距离: "+String.valueOf(distance)+"米",0).show();
				
				//根据两点设置缩放范围,并更新
				zoomByTwoPoint(mList.get(0).getLocation(),mList.get(1).getLocation());
				
				ll_position.setVisibility(View.GONE);
				ll_target_position.setVisibility(View.GONE);
				ll_geo.setVisibility(View.GONE);  //定位成功后恢复隐藏状态
				return;
			}
			GeoCoder mSearch = GeoCoder.newInstance();
			MyGeoCoderListener listener = new MyGeoCoderListener();
	    	mSearch.setOnGetGeoCodeResultListener(listener);
			mSearch.geocode(new GeoCodeOption().city(targetAddress).address(targetAddress));  //发起位置请求
			
			GeoCoder mSearch2 = GeoCoder.newInstance();
			MyGeoCoderListener listener2 = new MyGeoCoderListener();
	    	mSearch2.setOnGetGeoCodeResultListener(listener2);
			mSearch2.geocode(new GeoCodeOption().city(targetTwoAddress).address(targetTwoAddress));  //发起第二次位置请求
		}
	}

	/**
	 * 百度地图根据两点缩放
	 * @param onePoint
	 * @param twoPoint
	 */
	private void zoomByTwoPoint(LatLng onePoint, LatLng twoPoint) {
		LatLngBounds bounds = new LatLngBounds.Builder().include(onePoint).include(twoPoint).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLngBounds(bounds);  
		baiduMap.setMapStatus(mapStatusUpdate);
	}

	/**
	private void showPop() {
	   if(minDistanceLatLng==null){
			Toast.makeText(getApplicationContext(), "请先测距",0).show();
			return;
	   }
	   popViews.clear();  //清除之前的垃圾变量引用
	   initPop(new LatLng(latitude, longitude),localAddr,true);  //自己所处的定位位置弹出popwindow
	   initPop(minDistanceLatLng,mAddress, true);  //测距后变量minDistanceLatLng,mAddress均不为空
	}**/
	
	private void hiddenPop() {
	    //迭代隐藏
		Collection<View> views = popViews.values();
		Iterator<View> iter = views.iterator();
		while(iter.hasNext()){
			View pop = iter.next();
			pop.setVisibility(View.INVISIBLE);
		}
	}
	
	private void locate() {
		Log.i("test","初次定位...");
		mLocationClient = new LocationClient(getApplicationContext());
		myListener = new MyListener();
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
//		option.setScanSpan(100);// 设置发起定位请求的间隔时间
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.setOpenGps(true);  //设置打开GPS
		
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		BitmapDescriptor geo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);  //更换定位时的图标
		MyLocationConfiguration configuration = new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, geo);
		baiduMap.setMyLocationConfigeration(configuration);// 设置定位显示的模式
		baiduMap.setMyLocationEnabled(true);// 打开定位图层p
		baiduMap.getUiSettings().setCompassEnabled(false);  //不显示指南针
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(19));  //定位恢复自定义的默认级别
	}
	
	private void distance(String name,int radius) {
		locateOther(latitude,longitude,name,radius);  //检索另一方(精确定位)
	}

	private void locateOther(double latitude,double longitude,String keyword,int radius) {
		if(latitude==-1 && longitude == -1){
			Toast.makeText(getApplicationContext(), "请先定位", 0).show();
			return;
		}
		PoiSearch poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(new MyPoiSearchListener());
		
		PoiNearbySearchOption options = new PoiNearbySearchOption();
		options.location(new LatLng(latitude, longitude)).keyword(keyword).radius(radius).pageCapacity(20).pageNum(0);
		poiSearch.searchNearby(options);
	}
	
	public double getDistance(LatLng start,LatLng end){  
        double lat1 = (Math.PI/180)*start.latitude;  
        double lat2 = (Math.PI/180)*end.latitude;  
        double lon1 = (Math.PI/180)*start.longitude;  
        double lon2 = (Math.PI/180)*end.longitude;  
          
        double R = 6371;  //地球半径  
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;  //两点间距离 km，如果想要米的话，结果*1000就可以了    
          
        return d*1000;  
    }  
}
