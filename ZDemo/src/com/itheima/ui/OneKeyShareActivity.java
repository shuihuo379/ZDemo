package com.itheima.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.itheima.demo.R;
import com.itheima.platform.sharesdk.OnClickSharePlatformSelect;
import com.itheima.platform.sharesdk.ShareConstant;
import com.itheima.platform.sharesdk.SharePlatform;
import com.itheima.platform.sharesdk.SharePopWindow;
import com.itheima.platform.sharesdk.WShare;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SinaSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 友盟一键分享
 */
public class OneKeyShareActivity extends Activity implements OnClickListener{
	private UMSocialService mController;
	private TextView tv_one,tv_two;
	private SharePopWindow shareWindow;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.one_key_share);
		initManager();
		init();
	}
	
	private void initManager() {
		// 首先在您的Activity中添加如下成员变量
		mController = UMServiceFactory.getUMSocialService("com.umeng.share");
		
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this,ShareConstant.WEI_XIN_APP_ID,ShareConstant.WEI_XIN_APP_SECRET); //在微信开放平台申请的appkey
		wxHandler.addToSocialSDK();
		
		// 添加微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this,ShareConstant.WEI_XIN_APP_ID,ShareConstant.WEI_XIN_APP_SECRET);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();
		
		
		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, ShareConstant.QQ_APP_ID, ShareConstant.QQ_APP_KEY);
		qqSsoHandler.addToSocialSDK(); 
		
		//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, ShareConstant.QQ_APP_ID, ShareConstant.QQ_APP_KEY);
		qZoneSsoHandler.addToSocialSDK();
		
		//设置新浪SSO handler
		mController.getConfig().setSsoHandler(new SinaSsoHandler());
	}

	private void init() {
		tv_one = (TextView) findViewById(R.id.tv_one);
		tv_two = (TextView) findViewById(R.id.tv_two);
		tv_one.setOnClickListener(this);
		tv_two.setOnClickListener(this);
	}

	private void oneKeyShare() {
		// 设置分享内容
		mController.setShareContent("百度图片全球通用,点击进入百度页面");
		// 设置分享图片, 参数2为图片的url地址
		UMImage mUmImage = new UMImage(this,"http://imgsrc.baidu.com/baike/pic/item/2cb4fefe075a5a7f5d60086b.jpg");
		mUmImage.setTitle("百度风景图");
		mUmImage.setTargetUrl("http://www.baidu.com");
		mController.setShareMedia(mUmImage);
		
		
		// 设置分享图片，参数2为本地图片的资源引用
		//mController.setShareMedia(new UMImage(getActivity(), R.drawable.icon));
		// 设置分享图片，参数2为本地图片的路径(绝对路径)
		//mController.setShareMedia(new UMImage(getActivity(), BitmapFactory.decodeFile("/mnt/sdcard/icon.png")));

		
		// 设置分享音乐
		//UMusic uMusic = new UMusic("http://sns.whalecloud.com/test_music.mp3");
		//uMusic.setAuthor("GuGu");
		//uMusic.setTitle("天籁之音");
		// 设置音乐缩略图
		//uMusic.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
		//mController.setShareMedia(uMusic);

		
		// 设置分享视频
		//UMVideo umVideo = new UMVideo("http://v.youku.com/v_show/id_XNTE5ODAwMDM2.html?f=19001023");
		// 设置视频缩略图
		//umVideo.setThumb("http://www.umeng.com/images/pic/banner_module_social.png");
		//umVideo.setTitle("友盟社会化分享!");
		//mController.setShareMedia(umVideo); 
		
        mController.openShare(this, false); //使用友盟默认的分享面板
	}
	
	
	@Override 
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    /**使用SSO授权必须添加如下代码 */
	    UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode) ;
	    if(ssoHandler != null){
	       ssoHandler.authorizeCallBack(requestCode, resultCode, data);
	    }
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_one:  //使用友盟分享自带的对话框集成分享
			oneKeyShare();
			break;
		case R.id.tv_two: //使用自定义的弹出对话框集成友盟分享
			showSharePopWindow();
			break;
		}
	}
	
	/**
	 * 显示分享PopWindow
	 */
	private void showSharePopWindow() {
		if (shareWindow == null) {
			shareWindow = new SharePopWindow(this);
			shareWindow.init();
		}
		shareWindow.setListener(new OnClickSharePlatformSelect() {
			@Override
			public void OnClick(View v, SharePlatform platform) {
				share(platform);
			}
		});
		shareWindow.showAtLocation(this.findViewById(R.id.fl_content), Gravity.BOTTOM, 0, 0);
	}

	/**
	 * 分享平台
	 * @param platform
	 */
	protected void share(SharePlatform platform) {
		WShare wShare = new WShare(this,mController);
		switch (platform) {
		case QQ:
			if(!wShare.shareQQ(ShareConstant.TITLE, ShareConstant.URL, ShareConstant.CONTENT, ShareConstant.IMAGE_URL)){
				Toast.makeText(getApplicationContext(), "请先安装QQ客户端", 0).show();
			}
			break;
		case QZONE:
			if(!wShare.shareQZone(ShareConstant.TITLE, ShareConstant.URL, ShareConstant.CONTENT, ShareConstant.IMAGE_URL)){
				Toast.makeText(getApplicationContext(), "请先安装QQ客户端", 0).show();
			}
			break;
		case WECHAT:
			if(!wShare.shareWechat(ShareConstant.TITLE, ShareConstant.URL, ShareConstant.CONTENT, ShareConstant.IMAGE_URL)){
				Toast.makeText(getApplicationContext(), "请先安装微信客户端", 0).show();
			}
			break;
		case WECHAT_MOMENT:
			if(!wShare.shareWechatMoment(ShareConstant.TITLE, ShareConstant.URL, ShareConstant.CONTENT, ShareConstant.IMAGE_URL)){
				Toast.makeText(getApplicationContext(), "请先安装微信客户端", 0).show();
			}
			break;
		case SINA_WEIBO:
			break;
		}
	}
}
