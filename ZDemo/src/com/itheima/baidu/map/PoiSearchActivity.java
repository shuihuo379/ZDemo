package com.itheima.baidu.map;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

public class PoiSearchActivity extends BaseBaiduMapActivity{
	private PoiSearch poiSearch;
	private int currentPageIndex = 0;
	private boolean isFlag; //搜索城市isFlag=true
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		searchInBounds();
		searchInCity();
	}
	
	@Override
	protected void onDestroy() {
		poiSearch.destroy();
		super.onDestroy();
	}

	private void searchInBounds() {
		isFlag = false;
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(new myPoiSearchListener());
		
		//某个区域内搜索
		PoiBoundSearchOption option = new PoiBoundSearchOption();
		LatLngBounds bounds = new LatLngBounds.Builder()
		.include(new LatLng(40.049233,116.302675))  //西南点
		.include(new LatLng(40.050645,116.303695)).build();  //东北点
		option.bound(bounds);
		option.keyword("加油站");
		poiSearch.searchInBound(option);  //只是把范围能搜索的poi设置为地图的中心
	}
	
	private void searchInCity() {
		isFlag = true;
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(new myPoiCitySearchListener());
		
		PoiCitySearchOption cityOption = new PoiCitySearchOption();
		cityOption.city("北京");
		cityOption.keyword("加油站");
		cityOption.pageNum(currentPageIndex);
		poiSearch.searchInCity(cityOption);
	}
	
	class myPoiSearchListener implements OnGetPoiSearchResultListener{
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
		        
		        return;  
		    }  
		}
	}
	
	class myPoiCitySearchListener implements OnGetPoiSearchResultListener{
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
			Toast.makeText(getApplicationContext(), text, 1).show();
			baiduMap.clear();  // 清空地图所有的 Overlay 覆盖物以及 InfoWindow
			
			PoiOverlay overlay = new MyPoiOverlay(baiduMap); // 搜索poi的覆盖物
			baiduMap.setOnMarkerClickListener(overlay);  // 把事件分发给overlay，overlay才能处理点击事件
			overlay.setData(result);  // 设置结果
			overlay.addToMap();// 把搜索的结果添加到地图中
			overlay.zoomToSpan();// 缩放地图，使所有Overlay都在合适的视野内  注:该方法只对Marker类型的overlay有效
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(isFlag && keyCode==KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			currentPageIndex++;
			searchInCity();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
