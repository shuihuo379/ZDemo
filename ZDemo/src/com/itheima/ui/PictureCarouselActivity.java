package com.itheima.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itheima.adapter.ImagePagerAdapter;
import com.itheima.demo.R;
import com.itheima.model.Images;
import com.itheima.util.ImageCacheManager;

/**
 * 图片轮播图
 * @author zhangming
 */
public class PictureCarouselActivity extends Activity{
	private ViewPager mviewPager;
	private LinearLayout dotLayout; //小圆点的LinearLayout
	
	private List<ImageView> dotViewList; //存放小圆点图片的集合
	private List<ImageView> imageList; //存放轮播效果图片的集合
	
	private ScheduledExecutorService executorService;
	private boolean isPlay = true;
	private int currentItem  = 0;//当前页面
	private static final int ImgCount = 6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pic_carouse_activity);
		
		mviewPager = (ViewPager) findViewById(R.id.myviewPager);
		dotLayout = (LinearLayout)findViewById(R.id.dotLayout);
		dotLayout.removeAllViews();
		
		initView();
		startPlay();
	}
	
	private void startPlay(){
		executorService = Executors.newSingleThreadScheduledExecutor();
		//command：执行线程,initialDelay：初始化延时,period：前一次执行结束到下一次执行开始的间隔时间（间隔执行延迟时间),unit：计时单位
		executorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if(isPlay){
					synchronized(this){
						currentItem = (currentItem+1)%imageList.size();
			            runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mviewPager.setCurrentItem(currentItem,false);
							}
						});
					}
				}
			}
		},1,4,TimeUnit.SECONDS);
	}
	
	public void initView(){
		dotViewList = new ArrayList<ImageView>();
		imageList = new ArrayList<ImageView>();
		
		for (int i = 0; i < ImgCount; i++) {
			ImageView dotView = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			
			params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dp5);//设置小圆点的外边距
			params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dp5);
			params.height = getResources().getDimensionPixelSize(R.dimen.dp15); //设置小圆点的大小
			params.width = getResources().getDimensionPixelSize(R.dimen.dp15);
			
			if(i == 0){
				dotView.setBackgroundResource(R.drawable.point_pressed);
			}else{
				dotView.setBackgroundResource(R.drawable.point_unpressed);
			}
			dotLayout.addView(dotView, params);
			dotViewList.add(dotView);
		}
		
		LayoutInflater inflater = LayoutInflater.from(PictureCarouselActivity.this);
		for(int i=0;i<ImgCount;i++){
			ImageView img = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
			ImageCacheManager.loadImage(Images.imageThumbUrls[i],img,
					BitmapFactory.decodeResource(getResources(),R.drawable.offline),
					BitmapFactory.decodeResource(getResources(),R.drawable.offline));
			imageList.add(img);
		}
		
		/**
		ImageView img1 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
		ImageView img2 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
		ImageView img3 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
		ImageView img4 = (ImageView) inflater.inflate(R.layout.scroll_vew_item, null);
		img1.setBackgroundResource(R.drawable.main_img1);
		img2.setBackgroundResource(R.drawable.main_img2);
		img3.setBackgroundResource(R.drawable.main_img3);
		img4.setBackgroundResource(R.drawable.main_img4);
		
		imageList.add(img1);
		imageList.add(img2);
		imageList.add(img3);
		imageList.add(img4);
		**/
		
		ImagePagerAdapter adapter = new ImagePagerAdapter(imageList);
		mviewPager.setAdapter(adapter);
		mviewPager.setCurrentItem(0);
		mviewPager.setOnPageChangeListener(new MyViewPagerChangeListener());
	}
	
	private class MyViewPagerChangeListener implements OnPageChangeListener{
		boolean isAutoPlay = false;
		
		/**
		 * onPageScrollStateChanged方法中status参数为1的时候表示开始滑动，为2的时候表示手指松开了页面自动滑动，为0的时候表示停止在某页
		 */
		@Override
		public void onPageScrollStateChanged(int status) {
			switch (status) {
			case 0: // 滑动结束,即切换完毕或者加载完毕
				if(mviewPager.getCurrentItem() == mviewPager.getAdapter().getCount()-1 && !isAutoPlay){
				   mviewPager.setCurrentItem(0,false); //解决界面切换时,图片滑动存在现象,将平滑属性设置为false状态即可
				}else if(mviewPager.getCurrentItem() == 0 && !isAutoPlay){
				   mviewPager.setCurrentItem(mviewPager.getAdapter().getCount() - 1,false);
				}
				currentItem = mviewPager.getCurrentItem();
				isPlay = true;
				break;
			case 1: // 手势滑动,空闲中
				isAutoPlay = false;
				isPlay = false;
				break;
			case 2: // 界面切换中
				isAutoPlay = true;
				isPlay = false;
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
			
		}

		@Override
		public void onPageSelected(int pos) {
			for(int i=0;i<dotViewList.size();i++){
				if(i==pos){
					dotViewList.get(i).setBackground(getResources().getDrawable(R.drawable.point_pressed));
				}else{
					dotViewList.get(i).setBackground(getResources().getDrawable(R.drawable.point_unpressed));
				}
			}
		}
	}
}
