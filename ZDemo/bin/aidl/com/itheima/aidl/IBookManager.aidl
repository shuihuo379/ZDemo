package com.itheima.aidl;

import java.util.List;
import com.itheima.aidl.Book;
import com.itheima.aidl.IOnNewBookArrivedListener;

interface IBookManager {
	List<Book> getBookList();
	void addBook(in Book book); //在AIDL文件中使用实现了Parcelable接口的对象,此处参数为Book实例
	void registerListener(IOnNewBookArrivedListener listener); //在AIDL文件中使用其它AIDL接口,此处引入IOnNewBookArrivedListener接口
	void unregisterListener(IOnNewBookArrivedListener listener);
}