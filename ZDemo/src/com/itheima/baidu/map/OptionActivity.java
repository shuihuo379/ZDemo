package com.itheima.baidu.map;

import java.util.ArrayList;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapViewLayoutParams;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MapViewLayoutParams.ELayoutMode;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.itheima.demo.R;

/**
 * 覆盖物(圆形,文字)
 */
public class OptionActivity extends BaseBaiduMapActivity {
	private View pop;
	private TextView title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//drawCircle();  //圆形覆盖物
		//drawText();  //文字覆盖物
		drawMarker();  //标志物
		initPop();  //初始化弹出窗
	}


	private void drawCircle() {
		CircleOptions circleOptions = new CircleOptions();
		circleOptions.center(latLng).radius(1000)
		.fillColor(getResources().getColor(R.color.main_green))
		.stroke(new Stroke(5,getResources().getColor(R.color.darkgray)));
		baiduMap.addOverlay(circleOptions);
	}
	
	private void drawText() {
		TextOptions textOptions = new TextOptions();
		textOptions.fontColor(getResources().getColor(R.color.main_gray)).text("黑马程序员")
		.position(latLng).fontSize(25).typeface(Typeface.SERIF).rotate(30);
		baiduMap.addOverlay(textOptions);
	}
	
	private void drawMarker() {
		ArrayList<BitmapDescriptor> bitmaps = new ArrayList<BitmapDescriptor>();
		MarkerOptions markerOptions = new MarkerOptions();
		bitmaps.add(BitmapDescriptorFactory.fromResource(R.drawable.eat_icon));
		bitmaps.add(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher));
//		markerOptions.position(latLng).icon(descriptor).draggable(true).title("标志物");
		markerOptions.position(latLng).icons(bitmaps).draggable(true).title("标志物").period(100); // 设置多少帧刷新一次图片资源，Marker动画的间隔时间，值越小动画越快
		baiduMap.addOverlay(markerOptions);
		
		baiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(Marker result) {
				Log.i("test","title===>"+result.getTitle());
				LayoutParams params = new MapViewLayoutParams.Builder().layoutMode(ELayoutMode.mapMode)  //按照经纬度设置位置,不能传null 设置为mapMode时 必须设置position
						.position(result.getPosition()).width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT)
						.yOffset(-10).build(); //距离position的像素,向下是正值,向上是负值
				title.setText(result.getTitle());
				mMapView.updateViewLayout(pop, params);
				pop.setVisibility(View.VISIBLE);
				return true;
			}
		});
	}
	
	private void initPop() {
		pop = View.inflate(getApplicationContext(),R.layout.pop, null);
		LayoutParams params = new MapViewLayoutParams.Builder().layoutMode(ELayoutMode.mapMode)  //按照经纬度设置位置(设置成mapMode时,此时position传值不能为空)
				.position(latLng).width(MapViewLayoutParams.WRAP_CONTENT).height(MapViewLayoutParams.WRAP_CONTENT)
				.yOffset(-10).build();  //距离position的像素,向下是正值,向上是负值
		mMapView.addView(pop,params);
		pop.setVisibility(View.INVISIBLE);
		title = (TextView) pop.findViewById(R.id.title);
	}
}
