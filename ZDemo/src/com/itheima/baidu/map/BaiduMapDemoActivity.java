package com.itheima.baidu.map;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
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
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.MapViewLayoutParams.ELayoutMode;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.core.VehicleInfo;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
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
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.itheima.demo.R;
import com.itheima.util.SharedPre;
import com.itheima.util.StringUtil;

/**
 * 百度地图定位，搜索，路径规划等
 */
public class BaiduMapDemoActivity extends BaseBaiduMapActivity{
	public LocationClient mLocationClient;
	private ShareUrlSearch shareUrlSearch;
	private RoutePlanSearch routePlanSearch;
	private GeoCoder mSearch; 
	public BDLocationListener myListener;
	private BitmapDescriptor geo;
	private double latitude;
	private double longitude;
	private List<LatLng> mList;
	private Button searchBtn,cityBtn,locationBtn,poi_share,add_marker,routeByBus;
	private EditText et_text,et_city,et_key,et_two_city,et_location;
	private LinkedList<PoiSearch> mPoiList;  //收集PoiSearch变量的引用,最后一并回收
	private boolean isFirst = true;
	private static int curPage = 1;
	private static final int cityLevel = 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		init();
		setListener();
		locate();
	}

	private void init() {
		mMapView.showScaleControl(true);  //显示标尺
		
		mList = new ArrayList<LatLng>();
		mPoiList = new LinkedList<PoiSearch>(); 
		et_text = (EditText) findViewById(R.id.et_text);
		et_city = (EditText) findViewById(R.id.et_city);
		et_key = (EditText) findViewById(R.id.et_key);
		et_two_city = (EditText) findViewById(R.id.et_two_city);
		et_location = (EditText) findViewById(R.id.et_location);
		
		searchBtn = (Button) findViewById(R.id.searchBtn);
		cityBtn = (Button) findViewById(R.id.cityBtn);
		locationBtn = (Button) findViewById(R.id.locationBtn);
		poi_share = (Button) findViewById(R.id.poi_share);
		add_marker = (Button) findViewById(R.id.add_marker);
		routeByBus = (Button) findViewById(R.id.routeByBus);
		
		shareUrlSearch = ShareUrlSearch.newInstance();  //短串分享
		
		//测试路径规划中公交线路
		routePlanSearch = RoutePlanSearch.newInstance();
		routePlanSearch.setOnGetRoutePlanResultListener(new MyRouteListener());
	}
	
	private void setListener() {
		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchInBounds();
			}
		});
		cityBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchInCity(curPage,10);
				curPage++;
			}
		});
		locationBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchByGeocode();
			}
		});
		poi_share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//请求位置信息分享
				String latitude = SharedPre.getString(getApplicationContext(),"latitude","");
				String longitude = SharedPre.getString(getApplicationContext(),"longitude","");
				shareUrlSearch.requestLocationShareUrl(new LocationShareURLOption().location(
			    new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude)))
			    .name("位置信息").snippet("latitude:"+latitude+" longitude:"+longitude));
			}
		});
		add_marker.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//添加标志物
				baiduMap = mMapView.getMap();  
				LatLng latLng = baiduMap.getMapStatus().target;  //以地图控件中间的经纬度作为添加物的标志
				//准备 marker 的图片  
				BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);  
				//准备 marker option 添加 marker使用  
				MarkerOptions markerOptions = new MarkerOptions().icon(bitmap).position(latLng);  
				//获取添加的 marker 这样便于后续的操作  
				Marker marker = (Marker) baiduMap.addOverlay(markerOptions);  
			}
		});
		routeByBus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PlanNode stNode = PlanNode.withCityNameAndPlaceName("武汉","武胜路");
				PlanNode enNode = PlanNode.withCityNameAndPlaceName("武汉","光谷广场");
				routePlanSearch.transitSearch(new TransitRoutePlanOption().from(stNode).city("武汉").to(enNode));
			}
		});
		shareUrlSearch.setOnGetShareUrlResultListener(new MyShareUrlResultListener());
		
		//地图双击事件监听
		/**
		baiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			@Override
			public void onMapDoubleClick(LatLng latlng) {
				Log.i("test","地图双击事件,定位点击的经纬度坐标...");
				locate(latlng.latitude,latlng.longitude);
			}
		});
		*/
		
		//地图长按事件监听
		baiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latlng) {
				Log.i("test","地图自动定位执行...");
				locate();
			}
		});
	}

	/**
	 * 自定义标志物(替换系统的默认搜索图标)
	 */
	private void drawMarker(final View pop,LatLng latLng) {
		ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
		MarkerOptions markerOptions = new MarkerOptions();
		bitmaps.add(BitmapDescriptorFactory.fromResource(R.drawable.eat_icon));
		markerOptions.position(latLng).icons(bitmaps).draggable(true).title("标志物");
		baiduMap.addOverlay(markerOptions);
		
		//标志物点击事件监听
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
				
				if(pop.getVisibility() == View.INVISIBLE){ //隐藏,点击显示
					pop.setVisibility(View.VISIBLE);
				}else{
					pop.setVisibility(View.INVISIBLE);
				}
				return true;
			}
		});
		pop.setVisibility(View.INVISIBLE);
	}
	
	private View initPop(LatLng latLng) {
		View pop = View.inflate(getApplicationContext(),R.layout.pop,null);
		LayoutParams params = new MapViewLayoutParams.Builder().layoutMode(ELayoutMode.mapMode)  //按照经纬度设置位置(设置成mapMode时,此时position传值不能为空)
				.position(latLng).width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT)
				.yOffset(-10).build();  //距离position的像素,向下是正值,向上是负值
		mMapView.addView(pop,params);
		pop.setVisibility(View.INVISIBLE);
		return pop;
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
//		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.setNeedDeviceDirect(false);
		option.setOpenGps(true);  //设置打开GPS
		
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		geo = BitmapDescriptorFactory.fromResource(R.drawable.icon_geo);  //更换定位时的图标
		MyLocationConfiguration configuration = new MyLocationConfiguration(
				MyLocationConfiguration.LocationMode.FOLLOWING, true, geo);
		baiduMap.setMyLocationConfigeration(configuration);// 设置定位显示的模式
		baiduMap.setMyLocationEnabled(true);// 打开定位图层p
		baiduMap.getUiSettings().setCompassEnabled(false);  //不显示指南针
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(defaultLevel));  //定位恢复自定义的默认级别
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
	
	/**
	 * 某个范围内搜索(经纬度限制)
	 */
	private void searchInBounds() {
		PoiSearch poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(new MyPoiSearchListener());
		String keyword = et_text.getText().toString().trim();
		
		//某个区域内搜索
		PoiBoundSearchOption option = new PoiBoundSearchOption();
		LatLngBounds bounds = new LatLngBounds.Builder()
		.include(new LatLng(latitude-0.005,longitude-0.005))  //西南点
		.include(new LatLng(latitude+0.005,longitude+0.005)).build();  //东北点
		option.bound(bounds);
		option.keyword(keyword);
		poiSearch.searchInBound(option);  //只是把范围能搜索的poi设置为地图的中心
		mPoiList.add(poiSearch);
	}
	
	/**
	 * 城市范围内搜索标志物
	 */
	private void searchInCity(int page,int pageSize) {
		String city = et_city.getText().toString().trim();
		String keyword = et_key.getText().toString().trim();
		if(StringUtil.isEmpty(city)){
			Toast.makeText(getApplicationContext(), "城市名不能为空", 0).show();
			return;
		}
		if(StringUtil.isEmpty(keyword)){
			Toast.makeText(getApplicationContext(), "搜索词不能为空", 0).show();
			return;
		}
		
		PoiSearch poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(new MyPoiCitySearchListener());
		PoiCitySearchOption cityOption = new PoiCitySearchOption();
		cityOption.city(city);
		cityOption.keyword(keyword);
		cityOption.pageNum(page-1);
		cityOption.pageCapacity(pageSize);
		poiSearch.searchInCity(cityOption);
		mPoiList.add(poiSearch);
	}
	
    private void searchByGeocode() {
    	mSearch = GeoCoder.newInstance();
    	MyGeoCoderListener listener = new MyGeoCoderListener();
    	mSearch.setOnGetGeoCodeResultListener(listener);
    	
    	String city = et_two_city.getText().toString().trim();
    	String address = et_location.getText().toString().trim();
    	if(StringUtil.isEmpty(city)){
    		Toast.makeText(getApplicationContext(), "城市名不能为空", 0).show();
    		return;
    	}
    	if(StringUtil.isEmpty(address)){
    		mSearch.geocode(new GeoCodeOption().city(city).address(city));
        	MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(cityLevel);  //系统默认级别12,自定义默认的级别15,将尺度缩小到200公里,显示城市
        	baiduMap.setMapStatus(mapStatusUpdate);
    	}else{
    		mSearch.geocode(new GeoCodeOption().city(city).address(address));
    		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(defaultLevel);  //系统默认级别12,自定义默认的级别15,将尺度缩小到200公里,显示城市
        	baiduMap.setMapStatus(mapStatusUpdate);
    	}
	}
	
	/**
	 * 路由线路(步行路线)
	 */
	private void routeByWalk(List<LatLng> mList){
		routePlanSearch = RoutePlanSearch.newInstance();
		routePlanSearch.setOnGetRoutePlanResultListener(new MyRouteListener());
		
		WalkingRoutePlanOption walkOption = new WalkingRoutePlanOption();
		LatLng fromLatLng = new LatLng(latitude,longitude);
		PlanNode from = PlanNode.withLocation(fromLatLng); // 创建起点
		
		double minDistence = Integer.MAX_VALUE;
		int index = 0;
		for(int i=0;i<mList.size();i++){
			double distence = getDistance(fromLatLng,mList.get(i));
			if(distence < minDistence){
				minDistence = distence;
				index = i;
			}
		}
		PlanNode to = PlanNode.withLocation(mList.get(index)); // 创建终点(最短距离,单位为m)
		
		walkOption.from(from);
		walkOption.to(to);
		routePlanSearch.walkingSearch(walkOption);
		
		Toast.makeText(getApplicationContext(),"最短路径长度:"+(int)minDistence+"米",0).show();
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
	protected void onDestroy() {
		if(mSearch!=null){
			mSearch.destroy();
		}
		if(routePlanSearch!=null){
			routePlanSearch.destroy();
		}
		if(shareUrlSearch!=null){
			shareUrlSearch.destroy();
		}
		if(mPoiList.size()>0){
			for(PoiSearch poiSearch:mPoiList){
				poiSearch.destroy();  //迭代销毁
			}
		}
		super.onDestroy();
	}
	
	class MyListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation result) {
			if (result != null) {
				latitude = result.getLatitude();
				longitude = result.getLongitude();
				Log.i("test","latitude: "+latitude+", longitude: "+longitude);
				locate(latitude, longitude);
				
				if(isFirst){  //初次进来,保存当前的经纬度信息
					SharedPre.setSharedPreferences(getApplicationContext(),"latitude",String.valueOf(latitude));
					SharedPre.setSharedPreferences(getApplicationContext(),"longitude",String.valueOf(longitude));
					isFirst = false;
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
		    	baiduMap.clear();  // 清空地图所有的 Overlay 覆盖物以及 InfoWindow
		        PoiOverlay overlay = new MyPoiOverlay(baiduMap);   //创建PoiOverlay  
		        baiduMap.setOnMarkerClickListener(overlay);    //把事件分发给overlay,设置overlay可以处理标注点击事件  
		      
		        overlay.setData(result);   //设置PoiOverlay数据  
		        overlay.addToMap();   //添加PoiOverlay到地图中  
		        overlay.zoomToSpan();  //缩放地图,使所有overlay都能显示在视图内
		        
		        List<PoiInfo> poiList = result.getAllPoi();
		        mList.clear();
		        for(int i=0;i<poiList.size();i++){
		        	LatLng latLng = poiList.get(i).location;
		        	mList.add(latLng);
		        	View pop = initPop(latLng);
		        	drawMarker(pop,latLng);
		        }
		        routeByWalk(mList);
		        return;  
		    }  
		}
	}
	
	class MyPoiCitySearchListener implements OnGetPoiSearchResultListener{
		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {

		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
				Toast.makeText(getApplicationContext(), "未搜索到结果", 0).show();
				return;
			}
			String text = "共" + result.getTotalPageNum() + "页，共"
					+ result.getTotalPoiNum() + "条，当前第"
					+ result.getCurrentPageNum() + "页，当前页"
					+ result.getCurrentPageCapacity() + "条";
//			Toast.makeText(getApplicationContext(), text, 1).show();
			baiduMap.clear();  // 清空地图所有的 Overlay 覆盖物以及 InfoWindow
			
			PoiOverlay overlay = new MyPoiOverlay(baiduMap); // 搜索poi的覆盖物
			baiduMap.setOnMarkerClickListener(overlay);  // 把事件分发给overlay，overlay才能处理点击事件
			overlay.setData(result);  // 设置结果
			overlay.addToMap();// 把搜索的结果添加到地图中
			overlay.zoomToSpan();// 缩放地图，使所有Overlay都在合适的视野内  注:该方法只对Marker类型的overlay有效
			
			List<PoiInfo> mList = result.getAllPoi();
			for(int i=0;i<mList.size();i++){
				LatLng latLng = mList.get(i).location;
				View pop = initPop(latLng);
				drawMarker(pop,latLng);
			}
		}
	}
	
	class MyRouteListener implements OnGetRoutePlanResultListener{
		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) {  //公交路径规划
			if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
				Toast.makeText(getApplicationContext(), "未搜索到结果", 0).show();
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {  
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
	  	            }
	            	Log.i("test","=============================================");
	            }
		    }  
		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			if (result == null || SearchResult.ERRORNO.RESULT_NOT_FOUND == result.error) {
				Toast.makeText(getApplicationContext(), "未搜索到结果", 0).show();
				return;
			}
			baiduMap.clear();
			WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
			baiduMap.setOnMarkerClickListener(overlay);  // 把事件传递给overlay
			overlay.setData(result.getRouteLines().get(0));  // 设置线路为第一条
			overlay.addToMap();
			overlay.zoomToSpan();
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
	        //获取地理编码结果,并执行定位
			locate(result.getLocation().latitude,result.getLocation().longitude);
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {  
	            //没有找到检索结果  
	        }  
	        //获取反向地理编码结果  
		}
	}
	
	class MyShareUrlResultListener implements OnGetShareUrlResultListener{
		@Override
		public void onGetLocationShareUrlResult(ShareUrlResult result) {
			if (result.error != SearchResult.ERRORNO.NO_ERROR) {  
                Toast.makeText(getApplicationContext(),"抱歉,未搜索到短串",Toast.LENGTH_SHORT).show();  
            } else {  
                Toast.makeText(getApplicationContext(), "poi详情分享url："+result.getUrl(),Toast.LENGTH_LONG).show();  
            }  
		}

		@Override
		public void onGetPoiDetailShareUrlResult(ShareUrlResult result) {
			if (result.error != SearchResult.ERRORNO.NO_ERROR) {  
                Toast.makeText(getApplicationContext(),"抱歉,未搜索到短串",Toast.LENGTH_SHORT).show();  
            } else {  
                Toast.makeText(getApplicationContext(), "poi详情分享url："+result.getUrl(),Toast.LENGTH_LONG).show();  
            }  
		}
	}
	
	
	private class MyPoiOverlay extends PoiOverlay {  
	    public MyPoiOverlay(BaiduMap baiduMap) {  
	        super(baiduMap);  
	    }  
	    @Override  
	    public boolean onPoiClick(int index) {  
	    	PoiResult result = getPoiResult();
	    	PoiInfo poiInfo = result.getAllPoi().get(index);  //得到点击的那个poi信息
	    	String text = poiInfo.name+"===>"+poiInfo.address;
	    	Toast.makeText(getApplicationContext(), text, 0).show();
	        return super.onPoiClick(index);  
	    }  
	}
	
	public double getDistance(LatLng start,LatLng end){  
        double lat1 = (Math.PI/180)*start.latitude;  
        double lat2 = (Math.PI/180)*end.latitude;  
        double lon1 = (Math.PI/180)*start.longitude;  
        double lon2 = (Math.PI/180)*end.longitude;  
          
//      double Lat1r = (Math.PI/180)*(gp1.getLatitudeE6()/1E6);  
//      double Lat2r = (Math.PI/180)*(gp2.getLatitudeE6()/1E6);  
//      double Lon1r = (Math.PI/180)*(gp1.getLongitudeE6()/1E6);  
//      double Lon2r = (Math.PI/180)*(gp2.getLongitudeE6()/1E6);  
        
        double R = 6371;  //地球半径  
        double d =  Math.acos(Math.sin(lat1)*Math.sin(lat2)+Math.cos(lat1)*Math.cos(lat2)*Math.cos(lon2-lon1))*R;  //两点间距离 km，如果想要米的话，结果*1000就可以了    
          
        return d*1000;  
    }  
}
