package com.itheima.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.itheima.demo.R;

public class AddPictureGridAdapter extends BaseAdapter {
	public List<Bitmap> picList = new ArrayList<Bitmap>();
	private Context mContext;
	private int maxPic;
	private boolean reachMax = false;
	private static Bitmap addButton = null;

	public Bitmap getAddPic() {
		return addButton;
	}

	public AddPictureGridAdapter(Context mContext) {
		this.mContext = mContext;
	}

	public AddPictureGridAdapter(Context mContext, int maxPic) {
		this(mContext);
		this.maxPic = maxPic;
		addButton = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.smiley_add_btn_pressed);
		picList.add(addButton);
	}

	public boolean isReachMax() {
		return reachMax;
	}

	public int getMaxPic() {
		return maxPic;
	}

	public void setMaxPic(int maxPic) {
		this.maxPic = maxPic;
	}

	@Override
	public int getCount() {
		return picList.size();
	}

	@Override
	public Object getItem(int position) {
		return picList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			convertView = View.inflate(mContext,R.layout.add_single_pic_item,null);
		}
		final ImageView iv_single_pic= (ImageView)convertView.findViewById(R.id.iv_single_pic);
		iv_single_pic.setImageBitmap(picList.get(position));
		
		ImageView iv_del_single_pic = (ImageView)convertView.findViewById(R.id.iv_delete_single_pic);
		if(position == getCount()-1 && !reachMax){
			iv_del_single_pic.setVisibility(View.INVISIBLE);
		}else{
			iv_del_single_pic.setVisibility(View.VISIBLE);
		}
		iv_del_single_pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				removePic(position);  //移除指定位置的图片
				Log.i("test","移除第"+(position+1)+"张图片");
			}
		});
		return convertView;
	}

	public void addPic(Bitmap pic) {
		int idx = getCount() - 1;
		if (idx < 0) {
			idx = 0;
		}
		if (shouldReplaceLastItem()) {
			picList.remove(idx);
			reachMax = true;
		}
		picList.add(idx, pic);
		notifyDataSetChanged();
	}

	private boolean shouldReplaceLastItem() {
		return getCount() == this.maxPic;
	}

	public void removePic(int idx) {
		picList.remove(idx);
		if (idx + 1 == this.maxPic) {
			picList.add(idx, getAddPic());
			reachMax = false;
		} else if ((getCount() + 1 == this.maxPic) && isReachMax()) {
			picList.add(getCount(), getAddPic());
			reachMax = false;
		}
		notifyDataSetChanged();
	}
}
