package com.itheima.ui;

import com.itheima.demo.R;
import com.itheima.model.Tag;
import com.itheima.view.TagCloudLinkView;
import com.itheima.view.TagCloudLinkView.OnTagDeleteListener;
import com.itheima.view.TagCloudLinkView.OnTagSelectListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * 标签云实现
 * @author zhangming
 */
public class TagCloudActivity extends Activity{
	private TagCloudLinkView tc;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tag_cloud_activity);
		init();
		setListener();
	}
	
	private void init(){
		tc = (TagCloudLinkView)findViewById(R.id.tag_cloud_view);
		
		tc.add(new Tag(1,"这是你好呀这是你好呀"));
		tc.add(new Tag(2,"这是你好呀这是你好呀这是你好呀这是你好呀"));
		tc.add(new Tag(3,"welcome to me..."));
		tc.add(new Tag(4,"hello world..."));
		tc.add(new Tag(5,"哈哈哈哈哈哈哈哈哈哈"));
		tc.add(new Tag(6,"你好"));
		tc.add(new Tag(7,"fasdfadfa..."));
		tc.add(new Tag(8,"中国"));
		tc.add(new Tag(9,"柴静"));
		
		tc.drawTags();
	}
	
	private void setListener(){
		tc.setOnTagSelectListener(new OnTagSelectListener() {
			@Override
			public void onTagSelected(Tag tag, int position) {
				Log.i("test","position===>"+position+",tagId===>"+tag.getId()+",tagText===>"+tag.getText());
			}
		});
		
		tc.setOnTagDeleteListener(new OnTagDeleteListener() {
			@Override
			public void onTagDeleted(Tag tag, int position) {
				Log.i("test","delete======>"+tag.getId()+","+tag.getText());
			}
		});
	}
}
