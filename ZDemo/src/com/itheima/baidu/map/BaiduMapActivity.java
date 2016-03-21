package com.itheima.baidu.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapLoadedCallback;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
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
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.itheima.demo.R;
import com.itheima.util.StringUtil;

public class BaiduMapActivity extends Activity implements OnClickListener{
	public LocationClient mLocationClient;
	protected MyBaiduSDKReceiver receiver;
	protected MapView mMapView;
	protected BaiduMap baiduMap;
	private MyListener myListener;
	private Button locationBtn,distanceBtn,showBtn,hiddenBtn,routeBtn,okBtn;
	private LinearLayout ll_one_position,ll_two_position;
	private EditText et_addrname,et_target_addrname,et_route;
	private Spinner spinnerCity;
	private ImageView iv_locate;
	private List<GeoCodeResult> mList;
	private Overlay[] markOverlay;  //标志物图层
	private Overlay[] popOverlay;  //信息框图层
	private LatLng[] latLngArray;  //位置信息记录
	private String[] windowInfo;  //窗体信息记录 
	private boolean isOnePoint = true;  //是否一个点(标志位)
	private String[] cities = new String[] {"武汉","北京","郑州","石家庄","广州"};
	private String choiceCity;
	
	//常量字段
	private static final int markZIndex = 1;
	private static final int popZIndex = 2;
	private static final int length = 4; 
	private static final int defaultLevel = 15; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.baidumap_two_activity);
		initView();
		initData();
		setListener();
	}
	
	private void initManager() {
		receiver = new MyBaiduSDKReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);  //注册网络错误
		filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR); //注册key校验结果
		registerReceiver(receiver, filter);
	}
	
	private void initView() {
		locationBtn = (Button) findViewById(R.id.locationBtn);
		distanceBtn = (Button) findViewById(R.id.distanceBtn);
		showBtn = (Button) findViewById(R.id.show);
		hiddenBtn = (Button) findViewById(R.id.hidden);
		routeBtn = (Button) findViewById(R.id.route);
		
		okBtn = (Button) findViewById(R.id.ok_one);
		et_addrname = (EditText) findViewById(R.id.et_addrname);
		et_target_addrname = (EditText) findViewById(R.id.et_target_addrname);
		iv_locate = (ImageView) findViewById(R.id.iv_locate);
		
		ll_one_position = (LinearLayout) findViewById(R.id.ll_position);
		ll_two_position = (LinearLayout) findViewById(R.id.ll_target_position);
		et_route = (EditText) findViewById(R.id.et_route);
		spinnerCity = (Spinner) findViewById(R.id.spin_city);
		
		mMapView = (MapView) findViewById(R.id.bmapView);  	// 获取地图控件引用
		baiduMap = mMapView.getMap();  //管理具体的某一个MapView对象,缩放,旋转,平移
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(defaultLevel);  //默认级别12
		baiduMap.setMapStatus(mapStatusUpdate);  //设置缩放级别
		
		hiddenBaiduLogo();  //隐藏百度广告图标
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);  //默认是true,显示标尺
	}
	
	private void initData() {
		mList = new ArrayList<GeoCodeResult>();
		markOverlay = new Overlay[length];
		popOverlay = new Overlay[length];
		latLngArray = new LatLng[length];
		windowInfo = new String[length];
	}
	
	private void setListener() {
		locationBtn.setOnClickListener(this);
		distanceBtn.setOnClickListener(this);
		showBtn.setOnClickListener(this);
		hiddenBtn.setOnClickListener(this);
		routeBtn.setOnClickListener(this);
		
		baiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				locate();  //地图加载完成后自动定位
			}
		});
	}
	
	/**
	 * 自动定位当前位置
	 */
	private void locate() {
		mLocationClient = new LocationClient(getApplicationContext());
		myListener = new MyListener();
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
		option.setScanSpan(100);// 设置发起定位请求的间隔时间
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(false);
		option.setOpenGps(true);  //设置打开GPS
		
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		MyLocationConfiguration configuration = new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, 
				BitmapDescriptorFactory.fromResource(R.drawable.eat_icon));
		baiduMap.setMyLocationConfigeration(configuration);// 设置定位显示的模式
		baiduMap.setMyLocationEnabled(true);// 打开定位图层p
		baiduMap.getUiSettings().setCompassEnabled(false);  //不显示指南针
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(defaultLevel));  //定位恢复自定义的默认级别
	}
	
	/**
	 * 附近搜索
	 */
	private void NearBySearch(LatLng centerPoint,String searchName,int radius) {
		PoiSearch mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(new MyPoiSearchListener());
		PoiNearbySearchOption options = new PoiNearbySearchOption();
		options.location(centerPoint).keyword(searchName).radius(radius).pageCapacity(10).pageNum(0);
		mPoiSearch.searchNearby(options);
	}
	
	/**
	 * 标注两点
	 */
	private void drawTwoPoint(String p1,String p2) {
		mList.clear();
		SearchPoint(GeoCoder.newInstance(),new MyGeoCoderListener(),p1,false);
		SearchPoint(GeoCoder.newInstance(),new MyGeoCoderListener(),p2,false);
	}
	
	/**
	 * @param isOnePoint 是否单点搜索
	 */
	private void SearchPoint(GeoCoder mSearch,MyGeoCoderListener listener, String mAddress,boolean isOnePoint) {
		this.isOnePoint = isOnePoint;
		mSearch.setOnGetGeoCodeResultListener(listener);
		mSearch.geocode(new GeoCodeOption().city(mAddress).address(mAddress)); 
	}

	private Overlay drawMarker(LatLng latLng,BitmapDescriptor descriptor,int zIndex) {
		MarkerOptions markerOptions = new MarkerOptions();
		ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
		bitmaps.add(descriptor);
		markerOptions.position(latLng).icons(bitmaps).draggable(false);
		Overlay overlay = baiduMap.addOverlay(markerOptions);
		overlay.setZIndex(zIndex);
		return overlay;  //返回添加的图层,便于移除 
	}
	
	private Overlay drawPopWindow(LatLng latLng,String address,int zIndex){
		MarkerOptions markerOptions = new MarkerOptions();
		ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
		bitmaps.add(BitmapDescriptorFactory.fromView(initPop(latLng,address)));
		markerOptions.position(latLng).icons(bitmaps).draggable(false);
		Overlay overlay = baiduMap.addOverlay(markerOptions);
		overlay.setZIndex(zIndex);
		return overlay;  //返回添加的图层,便于移除 
	}
	
	private View initPop(LatLng latLng,final String address) {
		View pop = View.inflate(getApplicationContext(),R.layout.pop,null);
		TextView title = (TextView) pop.findViewById(R.id.title);
		title.setText(address);
		return pop;
	}

	/**
	 * 单点缩放至中心
	 */
	private void zoomByOneCenterPoint(LatLng centerPoint,float level){
		MapStatus mapStatus = new MapStatus.Builder().target(centerPoint).zoom(level).build();
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mapStatus);
		baiduMap.setMapStatus(mapStatusUpdate);
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
	
	class MyBaiduSDKReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String result = intent.getAction();
			if(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR.equals(result)){
				//网络错误
				Toast.makeText(getApplicationContext(),"无网络",Toast.LENGTH_SHORT).show();
			}else if(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR.equals(result)){
				//key校验失败
				Toast.makeText(getApplicationContext(),"key校验失败",Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class MyListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation result) {
			if (result != null) {
				final double latitude = result.getLatitude();
				final double longitude = result.getLongitude();
				final LatLng latLng = new LatLng(latitude, longitude);
				
				markOverlay[0] = drawMarker(latLng,BitmapDescriptorFactory.fromResource(R.drawable.eat_icon),markZIndex);
				popOverlay[0] = drawPopWindow(latLng,result.getAddrStr(),popZIndex);
				latLngArray[0] = latLng;
				windowInfo[0] = result.getAddrStr();
				zoomByOneCenterPoint(latLng,18);  //点中心缩放
				//NearBySearch(latLng,"公交站",2000); //附近两公里公交站
				
				iv_locate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						locate();
					}
				});
			}
		}
	}
	
	class MyGeoCoderListener implements OnGetGeoCoderResultListener{
		@Override
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //没有检索到结果  
				Toast.makeText(getApplicationContext(), "未搜索到结果", Toast.LENGTH_SHORT).show();
				return;
	        }
			if(isOnePoint){  //发起定位请求 
				markOverlay[1] = drawMarker(result.getLocation(),BitmapDescriptorFactory.fromResource(R.drawable.eat_icon),markZIndex);
				popOverlay[1] = drawPopWindow(result.getLocation(),result.getAddress(),popZIndex);
				latLngArray[1] = result.getLocation();
				windowInfo[1] = result.getAddress();
				zoomByOneCenterPoint(result.getLocation(),defaultLevel);
				
				final String info = result.getAddress();
				iv_locate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						removeAllOverLay();
						SearchPoint(GeoCoder.newInstance(), new MyGeoCoderListener(),info,true);
					}
				});
				return;
			}
			mList.add(result);
			
			if(mList.size() == 2){
				markOverlay[2] = drawMarker(mList.get(0).getLocation(),BitmapDescriptorFactory.fromResource(R.drawable.eat_icon),markZIndex);
				popOverlay[2] = drawPopWindow(mList.get(0).getLocation(),mList.get(0).getAddress(),popZIndex); //画标志物和弹出窗
				latLngArray[2] = mList.get(0).getLocation();
				windowInfo[2] = mList.get(0).getAddress();
				
				markOverlay[3] = drawMarker(mList.get(1).getLocation(),BitmapDescriptorFactory.fromResource(R.drawable.eat_icon),markZIndex);
				popOverlay[3] = drawPopWindow(mList.get(1).getLocation(),mList.get(1).getAddress(),popZIndex); //画标志物和弹出窗
				latLngArray[3] = mList.get(1).getLocation();
				windowInfo[3] = mList.get(1).getAddress();
				zoomByTwoPoint(mList.get(0).getLocation(),mList.get(1).getLocation());  //两点缩放
				
				double distance = getDistance(mList.get(0).getLocation(),mList.get(1).getLocation());
				Toast.makeText(getApplicationContext(),String.valueOf((int)distance)+"米",Toast.LENGTH_SHORT).show();
				
				iv_locate.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String p1= mList.get(0).getAddress();
						String p2= mList.get(1).getAddress();
						removeAllOverLay();
						drawTwoPoint(p1, p2);
					}
				});
			}
		}
		
		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			
		}
	}
	
	class MyRouteListener implements OnGetRoutePlanResultListener{
		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) { //公交路径规划
			if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
				Toast.makeText(getApplicationContext(), "未搜索到结果", Toast.LENGTH_SHORT).show();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {  
				et_route.setText(""); 
				baiduMap.clear();
	            TransitRouteOverlay overlay = new TransitRouteOverlay(baiduMap);  
	            baiduMap.setOnMarkerClickListener(overlay);  
	            overlay.setData(result.getRouteLines().get(0));  
	            overlay.addToMap();  
	            overlay.zoomToSpan(); 
	            
	            List<TransitRouteLine> lines = result.getRouteLines();
	            for(int i=0;i<lines.size();i++){
	            	List<TransitStep> tList = lines.get(i).getAllStep();
	            	for(int j=0;j<tList.size();j++){  
	  	            	//每段起始与终止地理坐标
	  	            	LatLng enterLatLng = tList.get(j).getEntrace().getLocation(); 
	  		            LatLng exitLatLng = tList.get(j).getExit().getLocation();
	  		            String instruction = tList.get(j).getInstructions();  //获取路线建议
	  		            Log.i("test","enterLatLng===>"+enterLatLng+",exitLatLng===>"+exitLatLng+",instruction"+instruction);
	  		            et_route.append(instruction);
	            	}
	            	Log.i("test","=============================================");
	            	et_route.append("\n==========================================\n");
	            }
		    }
		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
		
		}
	}
	
	class MyPoiSearchListener implements OnGetPoiSearchResultListener{
		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {
			
		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			if(result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {  
				Toast.makeText(getApplicationContext(), "未找到结果",Toast.LENGTH_SHORT).show();
			    return;  
			}
		    if (result.error == SearchResult.ERRORNO.NO_ERROR) {  
		    	List<PoiInfo> mList = result.getAllPoi();
		    	for(int i=0;i<mList.size();i++){
		    		LatLng latLng = mList.get(i).location;
		    		String name = mList.get(i).name;
		    		drawMarker(latLng,BitmapDescriptorFactory.fromResource(R.drawable.icon_geo),markZIndex);
		    		drawPopWindow(latLng,name, popZIndex);
		    	}
		    }
		}
	}
	
	private void showHiddenLayout(String operatorFlag){
		ll_one_position.setVisibility(View.VISIBLE);
		if("distance".equals(operatorFlag) || "route".equals(operatorFlag)){
			ll_two_position.setVisibility(View.VISIBLE);
			if("route".equals(operatorFlag)){
				et_route.setVisibility(View.VISIBLE);
				spinnerCity.setVisibility(View.VISIBLE);
			}else{
				spinnerCity.setVisibility(View.GONE);
			}
		}else{
			ll_two_position.setVisibility(View.GONE);
			et_route.setVisibility(View.GONE);
			spinnerCity.setVisibility(View.GONE);
		}
		operator(operatorFlag);
	}
	
	/**
	 * 不同的操作
	 * @param operatorFlag
	 */
	private void operator(String operatorFlag){
		if("location".equals(operatorFlag)){  //定位
			okBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String targetAddress = et_addrname.getText().toString().trim();
					if(StringUtil.isEmpty(targetAddress)){
			    		Toast.makeText(getApplicationContext(), "位置不为空", Toast.LENGTH_SHORT).show();
			    		return;
			    	}
					removeAllOverLay();
					SearchPoint(GeoCoder.newInstance(), new MyGeoCoderListener(),targetAddress,true);
					ll_one_position.setVisibility(View.GONE);
					ll_two_position.setVisibility(View.GONE);  
					et_route.setVisibility(View.GONE);  //隐藏状态
				}
			});
		}else if("distance".equals(operatorFlag)){  //测距
			okBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String targetoneAddress = et_addrname.getText().toString().trim();
					String targetTwoAddress = et_target_addrname.getText().toString().trim();
					if(StringUtil.isEmpty(targetoneAddress) || StringUtil.isEmpty(targetTwoAddress)){
			    		Toast.makeText(getApplicationContext(), "位置不为空", Toast.LENGTH_SHORT).show();
			    		return;
			    	}
					removeAllOverLay();
					drawTwoPoint(targetoneAddress,targetTwoAddress);
					ll_one_position.setVisibility(View.GONE);
					ll_two_position.setVisibility(View.GONE);  
					et_route.setVisibility(View.GONE);   //隐藏状态
				}
			});
			
		}else if("showPop".equals(operatorFlag)){  //显示信息窗
			ll_one_position.setVisibility(View.GONE);
			ll_two_position.setVisibility(View.GONE);  
			et_route.setVisibility(View.GONE);  //隐藏状态
			showPop();
			
		}else if("hiddenPop".equals(operatorFlag)){  //隐藏信息窗
			ll_one_position.setVisibility(View.GONE);
			ll_two_position.setVisibility(View.GONE);  
			et_route.setVisibility(View.GONE);  //隐藏状态
			hiddenPop();
			
		}else if("route".equals(operatorFlag)){  //路径规划
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					R.layout.spinner_check_text, Arrays.asList(cities)) {
				@Override
				public View getDropDownView(int position, View convertView,
						ViewGroup parent) {
					View view = View.inflate(getContext(),R.layout.spinner_item_layout, null);
					TextView label = (TextView) view.findViewById(R.id.spinner_item_label);
					label.setText(Arrays.asList(cities).get(position));
					if (spinnerCity.getSelectedItemPosition() == position) {  //选中变色
						view.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
					} else {
						view.setBackgroundColor(getResources().getColor(android.R.color.white));
					}
					return view;
				}
			};
			adapter.setDropDownViewResource(R.layout.spinner_item_layout);
			spinnerCity.setAdapter(adapter);
			
			spinnerCity.setOnItemSelectedListener(new OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					choiceCity = cities[position];
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {
					
				}
			});
			
			okBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String targetoneAddress = et_addrname.getText().toString().trim();
					String targetTwoAddress = et_target_addrname.getText().toString().trim();
					if(StringUtil.isEmpty(targetoneAddress)){
			    		Toast.makeText(getApplicationContext(), "起点不为空", Toast.LENGTH_SHORT).show();
			    		return;
			    	}
					if(StringUtil.isEmpty(targetTwoAddress)){
						Toast.makeText(getApplicationContext(), "终点不为空", Toast.LENGTH_SHORT).show();
			    		return;
					}
					
					RoutePlanSearch routePlanSearch = RoutePlanSearch.newInstance();
					routePlanSearch.setOnGetRoutePlanResultListener(new MyRouteListener());
					PlanNode stNode = PlanNode.withCityNameAndPlaceName(targetoneAddress,targetoneAddress);
					PlanNode etNode = PlanNode.withCityNameAndPlaceName(targetTwoAddress,targetTwoAddress);
					routePlanSearch.transitSearch(new TransitRoutePlanOption().from(stNode).city(choiceCity).to(etNode));
				}
			});
		}
	}
	
	/**
	 * 隐藏信息框
	 */
	private void hiddenPop() {
		for(int i=0;i<length;i++){
			if(popOverlay[i]!=null){
				popOverlay[i].remove();
				popOverlay[i] = null;
			}
		}
	}
	
	/**
	 * 显示信息框
	 */
	private void showPop(){
		for(int i=0;i<length;i++){
			if(markOverlay[i]!=null && popOverlay[i]==null){  //表明有标志物,无信息框
				popOverlay[i] = drawPopWindow(latLngArray[i],windowInfo[i],popZIndex);  //实例化一个图层并赋值
			}
		}
	}
	
	/**
	 * 移除所有自定义的图层
	 */
	private void removeAllOverLay(){
		baiduMap.clear();  //成功定位后清理之前的覆盖物
		for(int i=0;i<length;i++){
			popOverlay[i] = null;
			markOverlay[i] = null;
		}
	}
	
	/**
	 * 获取两点间距离
	 * @param start
	 * @param end
	 * @return
	 */
	public double getDistance(LatLng start,LatLng end){  
        double lat1 = (Math.PI/180)*start.latitude;  
        double lat2 = (Math.PI/180)*end.latitude;  
        double lon1 = (Math.PI/180)*start.longitude;  
        double lon2 = (Math.PI/180)*end.longitude;  
          
        double R = 6371;  //地球半径  
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;  //两点间距离 km，如果想要米的话，结果*1000就可以了    
          
        return d*1000;  
    }
	
	/**
	 * 隐藏百度logo
	 */
	private void hiddenBaiduLogo(){
		View child = mMapView.getChildAt(1);
		if (child != null && (child instanceof ImageView || child instanceof ZoomControls)){            
		     child.setVisibility(View.INVISIBLE);           
		}
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

	/**
	 * 操作入口
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.locationBtn:  //定位
				showHiddenLayout("location");
				break;
			case R.id.distanceBtn:  //测距
				showHiddenLayout("distance");
				break;
			case R.id.show:  //显示pop
				showHiddenLayout("showPop");
				break;
			case R.id.hidden:  //隐藏pop
				showHiddenLayout("hiddenPop");
				break;
			case R.id.route: //路径规划
				showHiddenLayout("route");
				break;
		}
	}
}
