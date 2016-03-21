package com.itheima.baidu.map;

import java.util.ArrayList;
import java.util.List;

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
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.MapViewLayoutParams.ELayoutMode;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.itheima.demo.R;
import com.itheima.util.SharedPre;

public class LocationDemoActivity extends Activity{
	public LocationClient mLocationClient;
	protected MapView mMapView;
	protected BaiduMap baiduMap;
	protected MyBaiduSDKReceiver receiver;
	protected MyListener myListener;
	private PoiSearch poiSearch;
	private LatLng mlatLng;  //自己所处的位置
	private View pop;
	protected static final int defaultLevel = 18;  //自定义默认的级别18
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.location_demo);
		initView();
		locate();  //定位自己
	}
	
	private void initView() {
		mMapView = (MapView) findViewById(R.id.bmapView);  	// 获取地图控件引用
		baiduMap = mMapView.getMap();  //管理具体的某一个MapView对象,缩放,旋转,平移
		
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(defaultLevel);  //默认级别12
		baiduMap.setMapStatus(mapStatusUpdate);  //设置缩放级别
		
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);  //默认是true,显示标尺
		
		baiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
		   /** 
		    * 手势操作地图，设置地图状态等操作导致地图状态开始改变。 
		    * @param status 地图状态改变开始时的地图状态 
		    */  
			@Override
			public void onMapStatusChangeStart(MapStatus status) {
				
			}
			
		   /** 
		    * 地图状态改变结束 
		    * @param status 地图状态改变结束后的地图状态 
		    */ 
			@Override
			public void onMapStatusChangeFinish(MapStatus status) {
				
			}
			
		   /** 
		    * 地图状态变化中 
		    * @param status 当前地图状态 
		    */  
			@Override
			public void onMapStatusChange(MapStatus status) {
				if(pop!=null && pop.getVisibility() == View.VISIBLE){
					pop.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		//标志物拖拽事件监听
		baiduMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {
				pop.setVisibility(View.INVISIBLE);
			}
			
			@Override
			public void onMarkerDragEnd(Marker marker) {
				String targetAddress = SharedPre.getString(getApplicationContext(), "target_address", "");
				initPop(marker.getPosition(),targetAddress,true);  //重新初始化pop弹出框
				int newDistence = (int)getDistance(mlatLng, marker.getPosition());
				Toast.makeText(getApplicationContext(),String.valueOf(newDistence),0).show();
			}
			
			@Override
			public void onMarkerDrag(Marker marker) {
				pop.setVisibility(View.INVISIBLE);
			}
		});
		
		//地图长按事件监听
		baiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latlng) {
				Log.i("test","地图自动定位执行...");
				locate();  //定位
			}
		});
	}
	
	private void initManager() {
		receiver = new MyBaiduSDKReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);  //注册网络错误
		filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR); //注册key校验结果
		registerReceiver(receiver, filter);
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
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(defaultLevel));  //定位恢复自定义的默认级别
	}
	
	private void locateOther(double latitude,double longitude,String keyword,int radius) {
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(new MyPoiSearchListener());
		
		PoiNearbySearchOption options = new PoiNearbySearchOption();
		options.location(new LatLng(latitude, longitude)).keyword(keyword).radius(radius).pageCapacity(20).pageNum(0);
		poiSearch.searchNearby(options);
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
		
		//手动添加定位的弹出窗
		String targetAddress = SharedPre.getString(getApplicationContext(), "target_address", "");
		initPop(mlatLng,targetAddress,true);
		
		/**
	    MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_geo)).draggable(true).title("我的位置").position(mlatLng);  //更换定位时的图标
		baiduMap.addOverlay(markerOptions);
		pop.setVisibility(View.INVISIBLE);
		*/
	}
	
	private void initPop(LatLng latLng,String address,boolean isVisible) {
		pop = View.inflate(getApplicationContext(),R.layout.pop,null);
		TextView tv = (TextView) pop.findViewById(R.id.title);
		tv.setText(address);
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
	
	private void drawMarker(LatLng latLng,String address) {
	    baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker result) {
				Log.i("test","title===>"+result.getTitle());
				LayoutParams params = new MapViewLayoutParams.Builder().layoutMode(ELayoutMode.mapMode)  //按照经纬度设置位置,不能传null 设置为mapMode时 必须设置position
						.position(result.getPosition()).width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT)
						.yOffset(-10).build(); //距离position的像素,向下是正值,向上是负值
				TextView title = (TextView) pop.findViewById(R.id.title);
				title.setText(result.getTitle());
				mMapView.updateViewLayout(pop, params);
				
				if(pop.getVisibility() == View.VISIBLE){  //显示,点击隐藏
					pop.setVisibility(View.INVISIBLE);
				}else{
					pop.setVisibility(View.VISIBLE);
				}
				return true;
			}
		});
		
		ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
		MarkerOptions markerOptions = new MarkerOptions();
		bitmaps.add(BitmapDescriptorFactory.fromResource(R.drawable.eat_icon));
		markerOptions.position(latLng).icons(bitmaps).draggable(true).title(address);
		baiduMap.addOverlay(markerOptions);
		pop.setVisibility(View.INVISIBLE);
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
				mlatLng = new LatLng(result.getLatitude(), result.getLongitude());
				locate(result.getLatitude(),result.getLongitude());
				locateOther(result.getLatitude(),result.getLongitude(),"餐厅",1000);  //检索另一方(精确定位)
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
	
	/**
	 * 取出最小距离,并用标志物标注
	 * @param poiList
	 */
	public void setMinDistance(List<PoiInfo> poiList){
		 double minDistence = Long.MAX_VALUE;
		 int index = 0;
		 for(int i=0;i<poiList.size();i++){
	        double distence = getDistance(mlatLng,poiList.get(i).location);
			if(distence < minDistence){
				minDistence = distence;
				index = i;
			}
	     }
		 
		 LatLng minDistanceLatLng = poiList.get(index).location;
		 String address = poiList.get(index).address;
		 SharedPre.setSharedPreferences(getApplicationContext(), "target_address",address);
		 Log.i("test","最短距离: "+minDistence+" 距离最小的位置: 纬度:"+minDistanceLatLng.latitude+" 经度:"+minDistanceLatLng.longitude);
		 Toast.makeText(getApplicationContext(),"两点之间的距离: "+String.valueOf((int)minDistence),0).show();
		 
		 initPop(minDistanceLatLng,address,false);
      	 drawMarker(minDistanceLatLng,address);  //手动添加标志物
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
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		if(poiSearch!=null){
			poiSearch.destroy();
		}
		baiduMap.setMyLocationEnabled(false);  //关闭定位图层  
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
