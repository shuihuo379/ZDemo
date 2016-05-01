package com.itheima.content.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper{
	private static final String DB_NAME = "book_provider.db";
	public static final String BOOK_TABLE_NAME = "book";
	public static final String USER_TABLE_NAME = "user";
	private static final int DB_VERSION = 1;
	
	//图书和用户信息表
	private String CREATE_BOOK_TABLE = "create table if not exists "+ BOOK_TABLE_NAME +"(_id integer primary key,name text)";
	private String CREATE_USER_TABLE = "create table if not exists "+ USER_TABLE_NAME +"(_id integer primary key,name text,sex text)";
	
	public DBOpenHelper(Context context) {
		super(context, DB_NAME,null, DB_VERSION);
	}

	/**
	 * 在调用getReadableDatabase或getWritableDatabase方法时,会判断指定的数据库是否存在,
	 * 不存在则调用SQLiteDatabase.create创建,onCreate只在数据库第一次创建时才执行
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BOOK_TABLE);
		db.execSQL(CREATE_USER_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
}
