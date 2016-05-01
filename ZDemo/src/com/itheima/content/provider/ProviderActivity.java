package com.itheima.content.provider;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class ProviderActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Uri bookUri = Uri.parse("content://com.itheima.content.provider/book");
		/**
		ContentValues values = new ContentValues();
		values.put("_id",4);
		values.put("name","IOS");
		getContentResolver().insert(bookUri, values);
		**/
		Cursor bookCursor = getContentResolver().query(bookUri,new String[]{"_id","name"},
				"_id=? and name=?",new String[]{"2","java"},null);
		while(bookCursor.moveToNext()){
			int bookId = bookCursor.getInt(0);
			String bookName = bookCursor.getString(1);
			Log.i("test","bookId:"+bookId+" bookName:"+bookName);
		}
		bookCursor.close();
		
		Uri userUri = Uri.parse("content://com.itheima.content.provider/user");
		Cursor userCursor = getContentResolver().query(userUri, new String[]{"_id","name","sex"},null, null, null);
		while (userCursor.moveToNext()) {
			int userId = userCursor.getInt(0);
			String userName = userCursor.getString(1);
			boolean isMale = "1".equals(userCursor.getString(2));
			if(isMale){
				Log.i("test","userId:"+userId+" userName:"+userName+" isMale:"+"true");
			}else{
				Log.i("test","userId:"+userId+" userName:"+userName+" isMale:"+"false");
			}
		}
		userCursor.close();
	}
}
