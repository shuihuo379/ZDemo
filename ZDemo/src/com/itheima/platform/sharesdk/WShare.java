package com.itheima.platform.sharesdk;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.SnsPostListener;
import com.umeng.socialize.media.QQShareContent;
import com.umeng.socialize.media.QZoneShareContent;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.umeng.socialize.weixin.media.CircleShareContent;
import com.umeng.socialize.weixin.media.WeiXinShareContent;

/**
 * 自定义友盟分享集成
 * @author zhangming
 * @date 2016/02/05
 */
public class WShare {
	private UMSocialService mController;
	private Activity mActivity; 
	
	public WShare(Activity activity,UMSocialService mController) {
		this.mActivity = activity;
		this.mController = mController;
	}
	

	/**
	 * 分享到QQ好友
	 * @param title 
	 * @param titleUrl
	 * @param content
	 * @param imageUrl
	 * @return false 未找到客户端
	 */
	public boolean shareQQ(String title, String targetUrl, String content, String imageUrl){
//		UMQQSsoHandler handler = new UMQQSsoHandler(mActivity,ShareConstant.QQ_APP_ID,ShareConstant.QQ_APP_KEY);
//		if(!handler.isClientInstalled()){  //没有安装客户端
//			return false;
//		}
		QQShareContent qqShareContent = new QQShareContent();
		//设置分享文字
		qqShareContent.setShareContent(content);
		//设置分享title
		qqShareContent.setTitle(title);
		//设置分享图片
		qqShareContent.setShareImage(new UMImage(mActivity,imageUrl));
		//设置点击消息的跳转URL
		qqShareContent.setTargetUrl(targetUrl);
		mController.setShareMedia(qqShareContent);
		addShareCallBack(SHARE_MEDIA.QQ);  //添加QQ分享回调
		
		return true;
	}



	/**
	 * 分享到QQ空间
	 * @param title
	 * @param titleUrl
	 * @param content
	 * @param imageUrl
	 * @param site
	 * @param siteUrl
	 */
	public boolean shareQZone(String title, String targetUrl, String content, String imageUrl){
		QZoneSsoHandler handler = new QZoneSsoHandler(mActivity,ShareConstant.QQ_APP_ID, ShareConstant.QQ_APP_KEY);
		if(!handler.isClientInstalled()){  //没有安装客户端
			return false;
		}
		QZoneShareContent qzone = new QZoneShareContent();
		//设置分享文字
		qzone.setShareContent(content);
		//设置点击消息的跳转URL
		qzone.setTargetUrl(targetUrl);
		//设置分享内容的标题
		qzone.setTitle(title);
		//设置分享图片
		qzone.setShareImage(new UMImage(mActivity,imageUrl));
		mController.setShareMedia(qzone);
		addShareCallBack(SHARE_MEDIA.QZONE);  //添加QQ空间分享回调
		
		return true;
	}



	/**
	 * 分享到微信好友
	 * @param title
	 * @param titleUrl
	 * @param content
	 * @param imageUrl
	 * @param url
	 * @return false 未找到客户端
	 */
	public boolean shareWechat(String title, String targetUrl, String content, String imageUrl){
		UMWXHandler handler = new UMWXHandler(mActivity,ShareConstant.WEI_XIN_APP_ID,ShareConstant.WEI_XIN_APP_SECRET);
		if(!handler.isClientInstalled()){  //没有安装客户端
			return false;
		}
		WeiXinShareContent weixinContent = new WeiXinShareContent();
		//设置分享文字
		weixinContent.setShareContent(content);
		//设置title
		weixinContent.setTitle(title);
		//设置分享内容跳转URL
		weixinContent.setTargetUrl(targetUrl);
		//设置分享图片
		weixinContent.setShareImage(new UMImage(mActivity,imageUrl));
		mController.setShareMedia(weixinContent);
		addShareCallBack(SHARE_MEDIA.WEIXIN);  //添加微信分享回调
		
		return true;
	}

	/**
	 * 分享到微信朋友圈
	 * @param title
	 * @param titleUrl
	 * @param content
	 * @param imageUrl
	 * @param url
	 * @return false 未找到客户端
	 */
	public boolean shareWechatMoment(String title, String targetUrl, String content, String imageUrl){
		UMWXHandler handler = new UMWXHandler(mActivity,ShareConstant.WEI_XIN_APP_ID,ShareConstant.WEI_XIN_APP_SECRET);
		if(!handler.isClientInstalled()){  //没有安装客户端
			return false;
		}
		CircleShareContent circleMedia = new CircleShareContent();
		circleMedia.setShareContent(content);
		//设置朋友圈title
		circleMedia.setTitle(title);
		circleMedia.setShareImage(new UMImage(mActivity,imageUrl));
		circleMedia.setTargetUrl(targetUrl);
		mController.setShareMedia(circleMedia);
		addShareCallBack(SHARE_MEDIA.WEIXIN_CIRCLE);  //添加微信朋友圈分享回调
		
		return true;
	}
	
	
	/**
	 * 自定义各平台分享回调事件
	 * @param sharePlatform
	 */
	private void addShareCallBack(SHARE_MEDIA sharePlatform){
		mController.postShare(mActivity,sharePlatform, new SnsPostListener() {
            @Override
            public void onStart() {
                Toast.makeText(mActivity, "开始分享", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onComplete(SHARE_MEDIA platform, int eCode,SocializeEntity entity) {
                 if (eCode == 200) {
                     Toast.makeText(mActivity, "分享成功", Toast.LENGTH_SHORT).show();
                 }else {
                      String eMsg = "";
                      if (eCode == -101){
                          eMsg = "没有授权";
                      }
                      Log.i("test", "分享失败[" + eCode + "] " + eMsg);
                      Toast.makeText(mActivity, "分享失败" + eMsg,Toast.LENGTH_SHORT).show();
                 }
            }
		});
	}
}
