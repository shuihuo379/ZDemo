package com.itheima.content.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class BookProvider extends ContentProvider{
	private static final String AUTHORITY = "com.itheima.content.provider";
	public static final Uri BOOK_CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/book");
	public static final Uri USER_CONTENT_URI = Uri.parse("content://"+ AUTHORITY +"/user");
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	private static final int BOOK_URI_CODE = 0;
	private static final int USER_URI_CODE = 1;
	
	private Context context;
	private SQLiteDatabase sdb;
	
	static{
		uriMatcher.addURI(AUTHORITY, "book", BOOK_URI_CODE);
		uriMatcher.addURI(AUTHORITY,"user",USER_URI_CODE);
	}
	
	@Override
	public boolean onCreate() {
		Log.d("test","onCreate current thread:"+Thread.currentThread().getName());
		context = getContext();
		initProviderData(); //ContentProvider创建时，初始化数据库
		return true;
	}
	
	private void initProviderData(){
		sdb = new DBOpenHelper(context).getWritableDatabase();
		sdb.execSQL("replace into user values(101,'jake',0)");
		sdb.execSQL("replace into user values(102,'sundy',1)");
		sdb.execSQL("replace into book values(1,'android')");
		sdb.execSQL("replace into book values(2,'java')");
		sdb.execSQL("replace into book values(3,'html5')");
	}
	
	private String getTableName(Uri uri){
		String tableName = null;
		switch (uriMatcher.match(uri)) {
		case BOOK_URI_CODE:
			tableName = DBOpenHelper.BOOK_TABLE_NAME;
			break;
		case USER_URI_CODE:
			tableName = DBOpenHelper.USER_TABLE_NAME;
			break;
		}
		return tableName;
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.d("test","query current thread:"+Thread.currentThread().getName());
		String tableName = getTableName(uri);
		if(tableName == null){
			throw new IllegalArgumentException("Unsupported URI： "+uri);
		}
		return sdb.query(tableName, projection, selection, selectionArgs, null, sortOrder, null);
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}
}
