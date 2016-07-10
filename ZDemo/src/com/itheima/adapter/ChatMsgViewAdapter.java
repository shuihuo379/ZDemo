package com.itheima.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itheima.demo.R;
import com.itheima.model.ChatMsgEntity;

public class ChatMsgViewAdapter extends BaseAdapter{
	public static interface IMsgViewType{
        int IMVT_COM_MSG = 0;
        int IMVT_TO_MSG = 1;
    }
    private List<ChatMsgEntity> data;
    private LayoutInflater mInflater;

    public ChatMsgViewAdapter(Context context, List<ChatMsgEntity> data) {
        this.data = data;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public int getItemViewType(int position) {
        ChatMsgEntity entity = data.get(position);
        if (entity.getMsgType()){
            return IMsgViewType.IMVT_COM_MSG;
        }else{
            return IMsgViewType.IMVT_TO_MSG;
        }
    }
    
    public int getViewTypeCount() {
        return 2;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMsgEntity entity = data.get(position);
        boolean isComMsg = entity.getMsgType();
        ViewHolder viewHolder = null;
        
        if (convertView == null){
            if (isComMsg){
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_left, null);
            }else{
                convertView = mInflater.inflate(R.layout.chatting_item_msg_text_right, null);
            }
            viewHolder = new ViewHolder();
            viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
            viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
            viewHolder.isComMsg = isComMsg;

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSendTime.setText(entity.getDate());
        viewHolder.tvUserName.setText(entity.getName());
        viewHolder.tvContent.setText(entity.getText());

        return convertView;
    }

    static class ViewHolder {
        public TextView tvSendTime;
        public TextView tvUserName;
        public TextView tvContent;
        public boolean isComMsg = true;
    }
}
