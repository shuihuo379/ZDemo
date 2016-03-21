package com.itheima.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.itheima.demo.R;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.util.Log;


public class ContactSmsUtil {
	/**
	 * 得到手机通讯录联系人信息
	 */  
	public static void getPhoneContacts(Context context){
		//Phone.CONTENT_URI,它所指向的其实是“content:// com.android.contacts/data/phones”,这个url 对应着contacts表 和   raw_contacts表 以及 data表
		String[] PHONES_PROJECTION = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  //显示名称  电话号码   头像ID 联系人ID
		int PHONES_DISPLAY_NAME_INDEX = 0;  //联系人显示名称
	    int PHONES_NUMBER_INDEX = 1;  //电话号码
	    int PHONES_PHOTO_ID_INDEX = 2;  //头像ID
	    int PHONES_CONTACT_ID_INDEX = 3; //联系人的ID
	    
	    List<String> mContactsName = new ArrayList<String>();  //联系人名称
	    List<String> mContactsNumber = new ArrayList<String>(); //联系人号码
	    List<Bitmap> mContactsPhonto = new ArrayList<Bitmap>(); //联系人头像
		
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,PHONES_PROJECTION,null,null,null);  //(content://com.android.contacts/directories)
		if (phoneCursor != null){  
			while(phoneCursor.moveToNext()){  
			    String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);  //得到手机号码  
			    if(StringUtil.isEmpty(phoneNumber)){
			    	continue;
			    }
			    String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX); //得到联系人名称  
			    long contactId = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX); //得到联系人ID  
			    long photoId = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX); //头像ID
			    
			    //得到联系人头像Bitamp  
			    Bitmap contactPhoto = null;  
			    if(photoId > 0 ) {  
			    	Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactId);
			    	InputStream in = ContactsContract.Contacts.openContactPhotoInputStream(resolver,uri);
			    	contactPhoto = BitmapFactory.decodeStream(in);
			    }else{
			    	contactPhoto = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_launcher);
			    }
			    
			    mContactsName.add(contactName);  
		        mContactsNumber.add(phoneNumber);  
		        mContactsPhonto.add(contactPhoto);  
			}
		}
		Log.i("test","mContactsName======>"+mContactsName);
		Log.i("test","mContactsNumber======>"+mContactsNumber);
	}
	
	/**
	 * 得到手机SIM卡联系人信息
	 */
	public static void getSIMContacts(Context context){
		String[] PHONES_PROJECTION = new String[] {Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,Phone.CONTACT_ID };  //显示名称  电话号码   头像ID 联系人ID
		int PHONES_DISPLAY_NAME_INDEX = 0;  //联系人显示名称
		int PHONES_NUMBER_INDEX = 1;  //电话号码
		
		List<String> mContactsName = new ArrayList<String>();  //联系人名称
		List<String> mContactsNumber = new ArrayList<String>(); //联系人号码
		
		ContentResolver resolver = context.getContentResolver();
		Cursor phoneCursor = resolver.query(Uri.parse("content://icc/adn"),PHONES_PROJECTION,null,null,null);
		if (phoneCursor != null){  
		    while (phoneCursor.moveToNext()){  
		        String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX); //得到手机号码  
		        if(StringUtil.isEmpty(phoneNumber)){
			    	continue;
			    }
		        String contactName = phoneCursor.getString(PHONES_DISPLAY_NAME_INDEX); //得到联系人名称  
		        mContactsName.add(contactName);  //Sim卡中没有联系人头像  
		        mContactsNumber.add(phoneNumber);  
		    }
		}
	}
}
