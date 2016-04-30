package com.itheima.aidl;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * model Book类实现Parcelable接口
 * @author zhangming
 */
public class Book implements Parcelable{
	private String bookId;
	private String bookName;
	
	public Book(){
		
	}
	
	public Book(String bookId,String bookName){
		this.bookId = bookId;
		this.bookName = bookName;
	}
	
	private Book(Parcel in){
		this.bookId = in.readString();
		this.bookName = in.readString(); //注意：此处赋值给全局变量
		Log.d("test","bookId="+bookId+" bookName="+bookName);
	}
	
	@Override
	public String toString() {
		return "bookId="+this.bookId+" bookName="+this.bookName;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(bookId);
		out.writeString(bookName);
	}
	
	public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
		@Override
		public Book createFromParcel(Parcel in) {
			return new Book(in);
		}

		@Override
		public Book[] newArray(int size) {
			return new Book[size];
		}
	};
}
