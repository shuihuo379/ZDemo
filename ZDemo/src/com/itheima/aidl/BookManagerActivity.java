package com.itheima.aidl;

import java.util.List;

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
	private ServiceConnection mConnection = new ServiceConnection(){
		@Override
		public void onServiceConnected(ComponentName name, IBinder iBinder) {
			Log.d("test","onServiceConnected...");
			IBookManager bookManager = IBookManager.Stub.asInterface(iBinder); //将服务端返回的binder对象转化成接口实例
			try {
				List<Book> bookList = bookManager.getBookList();
				Log.i("test","query bookList,listType: "+bookList.getClass().getCanonicalName());
				Log.i("test","bookList: "+bookList.toString());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d("test","onServiceDisconnected...");
			mConnection = null;
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
		unbindService(mConnection);
		super.onDestroy();
	}
}
