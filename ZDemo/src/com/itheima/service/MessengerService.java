package com.itheima.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.itheima.util.Constants;

/**
 * 此service为服务端进程
 * @author zhangming
 */
public class MessengerService extends Service{
	//接收客户端发来的消息
	private Messenger serviceMessenger = new Messenger(new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_FROM_CLIENT:
				Log.i("test","receive msg from Client: "+msg.getData().getString("sendMsg"));
				
				Message replyMsg = Message.obtain(null,Constants.MSG_FROM_SERVER);
				Bundle bundle = new Bundle();
				bundle.putString("replyMsg","message has received...");
				replyMsg.setData(bundle);
				
				try {
					msg.replyTo.send(replyMsg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	});
	
	@Override
	public IBinder onBind(Intent intent) {
		return serviceMessenger.getBinder(); //返回IBinder对象(客户端用到Messenger发送消息需要传递参数为service服务返回的IBinder对象)
	}
}
