package com.itheima.ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.itheima.adapter.AsyncListAdapter;
import com.itheima.adapter.AsyncListAdapter.JsonCache;
import com.itheima.demo.R;
import com.itheima.model.WeatherEntity;
import com.itheima.model.WeatherInfo;
import com.itheima.util.StringUtil;

/**
 * 使用Volley框架实现异步加载图片的ListView(默认图片已实现缓存)
 * 在原有的基础上,增加JSON数据三级缓存功能
 * @author zhangming
 */
public class AsyncVolleyListViewActivity extends Activity{
	private ListView asyncListView;
	private ExecutorService mExecutor;
	private JsonCache jsonCache;
	private AsyncListAdapter asyncListAdapter;
	private Map<Integer,WeatherInfo> weatherInfoMap; //key表示索引号position,value表示天气信息
	private boolean isEnd = false;
	
	//以下依次为:北京,上海,天津,重庆,哈尔滨,南京,济宁,武汉,合肥,长沙
	private static String[] cityWeatherCode = new String[]{
		"101010100","101020100","101030100","101040100","101050101",
		"101190101","101120701","101200101","101220101","101250101"
	};
	private static String[] imageUrls = new String[]{
		"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg",
        "http://img.my.csdn.net/uploads/201407/26/1406383275_3977.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383265_8550.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383264_3954.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383264_4787.jpg",  
        "http://img.my.csdn.net/uploads/201407/26/1406383264_8243.jpg"
	};
	private static final String Base_URL = "http://www.weather.com.cn/adat/cityinfo/";
	
  /**
   *由于每一个城市的天气为一个json串,本例中若使用同一个key,则会覆盖之前的json串,本例中应当改为以城市天气code作为key来存储
   *private static final String LruCache_Json_Key = "Json_LruCache";
   *private static final String DiskLruCache_Json_Key = "Json_DiskLruCache";
   **/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.async_volley_listview_activity);
		asyncListView = (ListView)findViewById(R.id.async_pic_listview);
		
		mExecutor = Executors.newFixedThreadPool(5);
		weatherInfoMap = new HashMap<Integer,WeatherInfo>();
		weatherInfoMap.clear(); //清空集合
		isEnd = false;
		
		asyncListAdapter = new AsyncListAdapter(this,imageUrls,weatherInfoMap);
		jsonCache = asyncListAdapter.new JsonCache();
		asyncListView.setAdapter(asyncListAdapter);
		
		for(int i=0;i<cityWeatherCode.length;i++){
			if(i==cityWeatherCode.length-1){
				isEnd = true;
			}
			
			String LruCache_Json_Key = cityWeatherCode[i]+"_LruCache";
			String DiskLruCache_Json_Key = cityWeatherCode[i]+"_DiskLruCache";
			if(jsonCache.getJsonByLruCache(LruCache_Json_Key)==null && jsonCache.getJsonByDiskLruCache(DiskLruCache_Json_Key) == null){
				mExecutor.execute(new MyExecutorTask(cityWeatherCode[i],i));
			}else{
				//本地有缓存,则先从内存中取出缓存的json数据,若内存没有,再从磁盘上取出缓存的json数据
				String lruCacheJson = jsonCache.getJsonByLruCache(LruCache_Json_Key);
				if(lruCacheJson!=null){
					parseJson(lruCacheJson,i);
				}else{
					String diskLruCacheJson = jsonCache.getJsonByDiskLruCache(DiskLruCache_Json_Key);
					parseJson(diskLruCacheJson,i);
				}
			}
		}
			
	}
	
	private String getWeatherUrl(String weatherCode){
		StringBuilder sb = new StringBuilder();
		sb.append(Base_URL).append(weatherCode).append(".html");
		return sb.toString();
	}
	
	class MyExecutorTask implements Runnable{
		private String code;
		private Integer position;
		public MyExecutorTask(String code,Integer position){
			this.code = code;
			this.position = position;
		}
		
		@Override
		public void run() {
			try {
//				String netWorkJson = loadDataFromServer(getWeatherUrl(this.code),"");
				String netWorkJson = downloadJson(getWeatherUrl(this.code),"");
				parseJson(netWorkJson,this.position);
				if(StringUtil.isNotEmpty(netWorkJson)){
					//保存json串到内存和磁盘上
					jsonCache.addJsonByLruCache(this.code+"_LruCache",netWorkJson);
					jsonCache.addJsonByDiskLruCache(this.code+"_DiskLruCache",netWorkJson); 
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	} 
	
	public String loadDataFromServer(String url,String param) throws Exception{
		URL dataUrl = new URL(url+param);
		URLConnection connection = dataUrl.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection)connection;
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        StringBuffer resultBuffer = new StringBuffer();
        String tempLine = null;
        
        if (httpURLConnection.getResponseCode() >= 300) {
            throw new Exception("HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
        }
        try {
            inputStream = httpURLConnection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
            while ((tempLine = reader.readLine()) != null) {
                resultBuffer.append(tempLine);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        
        return resultBuffer.toString();
	}
	
	public String downloadJson(String url,String param){
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		URL dataUrl=null;
		try {
			dataUrl = new URL(url+param);
			HttpURLConnection conn = (HttpURLConnection) dataUrl.openConnection();
			InputStream inStream = conn.getInputStream();
			while ((len = inStream.read(data)) != -1) {
				outStream.write(data, 0, len);
			}
			inStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(outStream.toByteArray());
	}
	
	/**
	 * 解析天气信息Json串
	 * @param json
	 */
	private void parseJson(String json,Integer position){
		WeatherEntity weatherEntity = JSON.parseObject(json,WeatherEntity.class);
		if(weatherEntity!=null){
			WeatherInfo weatherInfo = weatherEntity.getWeatherinfo();
			weatherInfoMap.put(position,weatherInfo);
		}
		if(isEnd){
			asyncListAdapter.notifyDataSetChanged(); //结束时刷新适配器
		}
	}
}
