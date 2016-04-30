package com.itheima.aidl;
import com.itheima.aidl.Book;

interface IOnNewBookArrivedListener{
	void onNewBookArrived(in Book newBook);
}