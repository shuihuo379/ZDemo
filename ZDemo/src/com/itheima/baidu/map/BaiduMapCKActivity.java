package com.itheima.baidu.map;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.itheima.demo.R;
import com.itheima.util.StringUtil;

/**
 * 综合案例
 * @author zhangming
 * @date 2016/02/03 17:26
 */
public class BaiduMapCKActivity extends Activity{
	protected MyBaiduSDKReceiver receiver;
	private MapView mMapView;
	private BaiduMap baiduMap;
	public LocationClient mLocationClient;
	
	private EditText et_address;
	private Button btn_search,open_gps_btn;
	private TextView tv_distance;
	private ImageView iv_locate;
	private View pop; 
	private MyListener myListener;
	
	private Overlay[] markOverlay;  //标志物图层
	private Overlay[] popOverlay;  //信息框图层
	private LatLng[] latLngArray;  //位置信息记录
	private String[] windowInfo;  //窗体信息记录 
	private boolean isFirst = true; 
	private boolean isZoomCenter = true;
	
	//常量字段
	private static final int markZIndex = 1;
	private static final int popZIndex = 2;
	private static final int length = 4; 
	private static final int defaultLevel = 15;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initManager();
		setContentView(R.layout.baidumap_ck_activity);
		initView();
		initData();
		setListener();
		checkGpsEnabled();  //检查GPS状态
	}
	
	private void initManager() {
		receiver = new MyBaiduSDKReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);  //注册网络错误
		filter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR); //注册key校验结果
		registerReceiver(receiver, filter);
	}
	
	private void initView() {
		et_address = (EditText) findViewById(R.id.et_address);
		btn_search = (Button) findViewById(R.id.btn_search);
		tv_distance = (TextView) findViewById(R.id.tv_distance);
		iv_locate = (ImageView) findViewById(R.id.iv_locate);
		open_gps_btn = (Button) findViewById(R.id.open_gps_btn);
		
		mMapView = (MapView) findViewById(R.id.bmapView);  	// 获取地图控件引用
		baiduMap = mMapView.getMap();  //管理具体的某一个MapView对象,缩放,旋转,平移
		MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.zoomTo(defaultLevel);  //默认级别12
		baiduMap.setMapStatus(mapStatusUpdate);  //设置缩放级别
		
		hiddenBaiduLogo();  //隐藏百度广告图标
		mMapView.showZoomControls(false);
		mMapView.showScaleControl(true);  //默认是true,显示标尺
	}
	
	private void initData() {
		markOverlay = new Overlay[length];
		popOverlay = new Overlay[length];
		latLngArray = new LatLng[length];
		windowInfo = new String[length];
	}
	
	/**
	 * 检查GPS开关状态
	 */
	private void checkGpsEnabled() {
		String str = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);  
        if (str != null && str.contains("gps")) {  
        	Log.i("test","用户打开了GPS");
        }else{ //尚未打开
             AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒").setMessage("打开GPS能使定位更准确,是否打开")
            		 .setPositiveButton("是",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							//打开GPS设置界面
							Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent,0);
						}
					}).setNegativeButton("否",new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					}).create();
             alertDialog.show();
        }
	}
	
	private void setListener() {
		btn_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String address = et_address.getText().toString().trim();
				if(StringUtil.isEmpty(address)){
		    		Toast.makeText(getApplicationContext(), "地名不为空", Toast.LENGTH_SHORT).show();
		    		return;
		    	}
				drawOnePoint(address);
			}
		});
		
		iv_locate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				locate();
				zoomByOneCenterPoint(latLngArray[0],baiduMap.getMapStatus().zoom);
			}
		});
		
		baiduMap.setOnMapLoadedCallback(new OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				locate();  //地图加载完成后自动定位
			}
		});
		
		open_gps_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {  //打开gps
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent,0);
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i("test","GPS定位已打开");
	}
	
	protected void drawOnePoint(String address) {
		GeoCoder mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new MyGeoCoderListener());
		mSearch.geocode(new GeoCodeOption().city(address).address(address)); 
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
		option.setScanSpan(1000);
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
		baiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(baiduMap.getMapStatus().zoom));  //定位后更新缩放级别
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
				if(markOverlay[0]!=null){
					TextView tv = (TextView) pop.findViewById(R.id.title);
					tv.setText(result.getAddrStr());
				}else{
					markOverlay[0] = drawMarker(latLng,BitmapDescriptorFactory.fromResource(R.drawable.eat_icon),markZIndex);
					popOverlay[0] = drawPopWindow(latLng,result.getAddrStr(),popZIndex);
					latLngArray[0] = latLng;
					windowInfo[0] = result.getAddrStr();
				}
				if(isZoomCenter){
					zoomByOneCenterPoint(latLngArray[0],defaultLevel);
					isZoomCenter = false;
				}
				if(markOverlay[1] == null){
					tv_distance.setText("0米");
				}else{
					if(isFirst){  //点击一次Button时,才缩放两点
						zoomByTwoPoint(latLngArray[0],latLngArray[1]); 
						isFirst = false;
					}
					int distance = (int) getDistance(latLngArray[0],latLngArray[1]);
					tv_distance.setText(String.valueOf(distance)+"米");
				}
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
			if(markOverlay[1]!=null){
				markOverlay[1].remove();
			}
			if(popOverlay[1]!=null){
				popOverlay[1].remove();
			}
			
			markOverlay[1] = drawMarker(result.getLocation(),BitmapDescriptorFactory.fromResource(R.drawable.eat_icon),markZIndex);
			popOverlay[1] = drawPopWindow(result.getLocation(),result.getAddress(),popZIndex);
			latLngArray[1] = result.getLocation();
			windowInfo[1] = result.getAddress();
			zoomByTwoPoint(latLngArray[0],latLngArray[1]);
			double distance = getDistance(latLngArray[0], latLngArray[1]);
			
			tv_distance.setText(String.valueOf((int)distance)+"米");
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			
		}
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
		pop = View.inflate(getApplicationContext(),R.layout.pop,null);
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
		
		float level = baiduMap.getMapStatus().zoom - 1;
		MapStatus mapStatus = new MapStatus.Builder().zoom(level).build();
		MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(mapStatus);
		baiduMap.setMapStatus(update);
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
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLocationClient.stop();
		unregisterReceiver(receiver);
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
		mMapView = null;
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
