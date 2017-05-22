package com.itheima.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.itheima.demo.R;

/**
 * 含RadioButton的ListView适配器
 * @author zhangming
 * @date 2017/05/22
 */
public class RadioButtonAdapter extends BaseAdapter{
	private Context mContext; 
	private List<RadioButtonItem> mList;
	private HashMap<String,Boolean> mStates = new HashMap<String,Boolean>(); //用于记录每个RadioButton的状态，并保证只可选一个  
	public static int lastPosition = 0; 
	
	public RadioButtonAdapter(Context context,List<RadioButtonItem> mList){
		this.mContext = context;
		this.mList = mList;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder;  
		if (convertView == null) {  
	        convertView = LayoutInflater.from(mContext).inflate(R.layout.radio_button_view, null);  
	        holder = new ViewHolder();  
	        holder.rootView = (LinearLayout) convertView.findViewById(R.id.radio_button_view);  
	        holder.content = (TextView) convertView.findViewById(R.id.tv_choice_content);  
	        convertView.setTag(holder);  
	    }else{  
	        holder=(ViewHolder) convertView.getTag();  
	    }  
		final String content = mList.get(position).content;
	    holder.content.setText(content);
	    
	    final RadioButton radio=(RadioButton) convertView.findViewById(R.id.radio_btn);  
	    holder.radioButton = radio;
	    holder.radioButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
			   //重置,确保最多只有一项被选中  
               for(String key:mStates.keySet()){  
            	   mStates.put(key, false);  
               }
               for(int i=0;i<mList.size();i++){
            	   mList.get(i).isChoice = false;
               }
               mStates.put(String.valueOf(position), radio.isChecked());  
               if(mStates.get(String.valueOf(position))){
            	   mList.get(position).isChoice = true;
               }
               RadioButtonAdapter.this.notifyDataSetChanged();
			}
		});
	    
	    boolean state=false;  
		if(mStates.get(String.valueOf(position)) == null){
			if(position == lastPosition){
				state = true;
				mStates.put(String.valueOf(lastPosition),true);
				mList.get(lastPosition).isChoice = true;
			}else{
				state = false;  
				mStates.put(String.valueOf(position),false);  
				mList.get(position).isChoice = false;
			}
		}else if(mStates.get(String.valueOf(position)) == false){
			state = false;  
			mStates.put(String.valueOf(position),false);  
		}else{  
			state = true;  
		}
	    holder.radioButton.setChecked(state);  
	    
	    return convertView;
	}
	
	static class ViewHolder {  
	   LinearLayout rootView;  
	   TextView content;  
	   RadioButton radioButton;  
	}
	
	public static class RadioButtonItem{
		public String content;    //内容
		public boolean isChoice;  //状态:是否被选中
	}
}
