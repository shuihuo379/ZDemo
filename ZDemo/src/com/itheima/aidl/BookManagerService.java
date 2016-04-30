package com.itheima.aidl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

/**
 * AIDL服务端
 * @author zhangming
 */
public class BookManagerService extends Service{
	private CopyOnWriteArrayList<Book> mBookList = new CopyOnWriteArrayList<Book>();
	//private CopyOnWriteArrayList<IOnNewBookArrivedListener> mListenerList = new CopyOnWriteArrayList<IOnNewBookArrivedListener>();
	//使用RemoteCallbackList代替,是专门提供的用于删除跨进程listener的接口
	private RemoteCallbackList<IOnNewBookArrivedListener> mListenerList = new RemoteCallbackList<IOnNewBookArrivedListener>();
	private AtomicBoolean isServiceDestroy = new AtomicBoolean(false);
	
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

		@SuppressLint("NewApi") 
		@Override
		public void registerListener(IOnNewBookArrivedListener listener)
				throws RemoteException {
			boolean isSuccess = mListenerList.register(listener);
			if(isSuccess){
				Log.i("test","register Listener success,current size= "+mListenerList.getRegisteredCallbackCount());
			}
			
			/**
			if(!mListenerList.contains(listener)){ //mListenerList集合数量范围在0-1
				mListenerList.add(listener);
			}else{
				Log.d("test","already exists...");
			}**/
		}

		@SuppressLint("NewApi") 
		@Override
		public void unregisterListener(IOnNewBookArrivedListener listener)
				throws RemoteException {
			boolean isSuccess = mListenerList.unregister(listener);
			if(isSuccess){
				Log.i("test","unregister Listener success,current size= "+mListenerList.getRegisteredCallbackCount());
			}
			
			/**
			if(mListenerList.contains(listener)){
				mListenerList.remove(listener);
				Log.d("test","unregister listener succeed...");
			}else{
				Log.d("test","not found,can not unregister...");
			}**/
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
		new Thread(new ServiceWorker()).start();
	}
	
	@Override
	public void onDestroy() {
		isServiceDestroy.set(true);
		super.onDestroy();
	}
	
	private void onNewBookArrived(Book newBook) throws RemoteException{
		mBookList.add(newBook);
		//遍历RemoteCallBackList接口对象,其中beginBroadcast和finishBroadcast方法必须配对使用
		final int N = mListenerList.beginBroadcast();
		for(int i=0;i<N;i++){
			IOnNewBookArrivedListener listener = mListenerList.getBroadcastItem(i);
			if(listener!=null){
				listener.onNewBookArrived(newBook); //远程服务端调用客户端的listener中的方法(此时服务端的这个方法本身处在binder线程池中运行,没必要再开一个线程)
			}
		}
		mListenerList.finishBroadcast();
		
		/**
		for(int i=0;i<mListenerList.size();i++){
			IOnNewBookArrivedListener mListener = mListenerList.get(i);
			mListener.onNewBookArrived(newBook);
		}**/
	}
	
	private class ServiceWorker implements Runnable{
		@Override
		public void run() {
			while(!isServiceDestroy.get()){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int bookId = mBookList.size()+1; //此时初始时mBookList.size()=4,bookId=5
				Book newBook = new Book(String.valueOf(bookId),"new book#"+bookId);
				try {
					onNewBookArrived(newBook);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
