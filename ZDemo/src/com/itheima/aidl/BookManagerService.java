package com.itheima.aidl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * AIDL服务端
 * @author zhangming
 */
public class BookManagerService extends Service{
	private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
	
	private Binder mBinder = new IBookManager.Stub() {
		@Override
		public List<Book> getBookList() throws RemoteException {
			Log.d("test","getBookList...");
			return mBookList;
		}

		@Override
		public void addBook(Book book) throws RemoteException {
			Log.d("test","addBook...");
			mBookList.add(book);
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d("test","onBind...");
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d("test","service onCreate...");
		//初始化数据
		mBookList.add(new Book("101","android"));
		mBookList.add(new Book("102","ios"));
		mBookList.add(new Book("103","java"));
	}
}
