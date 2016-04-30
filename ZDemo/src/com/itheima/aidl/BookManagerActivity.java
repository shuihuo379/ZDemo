package com.itheima.aidl;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class BookManagerActivity extends Activity{
	private IBookManager bookManager;
	
	//注:onServiceConnected和onServiceDisconnected两个方法是运行在主线程中
	private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			Log.d("test","onServiceConnected...");
			bookManager = IBookManager.Stub.asInterface(iBinder); //将服务端返回的binder对象转化成接口实例
			try {
				iBinder.linkToDeath(mDeathRecipient,0); //注:此处是为binder设置死亡代理,当binder死亡时会回调下面的binderDied方法
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			
			//最好将调用服务端的getBookList和addBook方法放置在线程中执行,避免出现ANR
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Log.i("test","query bookList,listType: "+bookManager.getBookList().getClass().getCanonicalName());
						Log.i("test","bookList: "+bookManager.getBookList().toString());
						
						bookManager.addBook(new Book("104","C++"));
						Log.i("test","bookList: "+bookManager.getBookList().toString());
						
						bookManager.registerListener(mListener);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}).start();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("test","onServiceDisconnected...");
			bookManager = null;
			mConnection = null;
		}
	};
	
	private IOnNewBookArrivedListener mListener = new IOnNewBookArrivedListener.Stub() {
		@Override
		public void onNewBookArrived(final Book newBook) throws RemoteException {
			//服务端会回调此客户端的IOnNewBookArrivedListener对象中的onNewBookArrived方法，此方法是在客户端的Binder线程池中执行
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Log.i("test","receive new book: "+newBook.toString());
				}
			});
		}
	};
	
	private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
		@Override
		public void binderDied() {
			//binder意外死亡时(远程服务端突然崩溃,服务进程挂掉)回调此方法,重新绑定远程服务service
			Log.i("test","binder died,try to connect remote service...");
			if(bookManager==null){
				return;
			}
			bookManager.asBinder().unlinkToDeath(mDeathRecipient,0); //binder意外死亡时,为binder断开死亡代理连接
			bookManager = null;
			
			Intent intent = new Intent(BookManagerActivity.this,BookManagerService.class); 
			bindService(intent,mConnection,Context.BIND_AUTO_CREATE); //再次绑定远程服务
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("test","client onCreate...");
		Intent intent = new Intent(this,BookManagerService.class);
		bindService(intent,mConnection,Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onDestroy() {
		if(bookManager!=null && bookManager.asBinder().isBinderAlive()){
			Log.d("test","client unregisterListener...");
			try {
				bookManager.unregisterListener(mListener);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		unbindService(mConnection);
		super.onDestroy();
	}
}
