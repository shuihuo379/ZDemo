package com.itheima.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.itheima.service.MessengerService;
import com.itheima.util.Constants;
import com.itheima.view.FiveStarView;

/**
 * IPC机制中使用Messenger来实现通信
 * 此Activity为客户端
 * @author zhangming
 */
public class MessengerIPCDemoActivity extends Activity{
	//接收服务端返回的消息
	private Messenger clientMessenger = new Messenger(new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constants.MSG_FROM_SERVER:
				Log.i("test","receive replyMsg from Server: "+msg.getData().getString("replyMsg"));
				break;
			}
		}
	});
	
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			Message msg = Message.obtain();
			msg.what = Constants.MSG_FROM_CLIENT;
			Bundle bundle = new Bundle();
			bundle.putString("sendMsg","hello,this is client...");
			msg.setData(bundle);
			msg.replyTo = clientMessenger; //注意：不要忘记此句,如果没有这句,服务端进程发送回复消息时会报空指针
			
			try {
				new Messenger(iBinder).send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mConnection = null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new FiveStarView(this));
		
		//绑定服务(第3个参数Context.BIND_AUTO_CREATE表明只要绑定存在，就自动建立 )
		Intent intent = new Intent(this,MessengerService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		unbindService(mConnection);
		super.onDestroy();
	}
}
