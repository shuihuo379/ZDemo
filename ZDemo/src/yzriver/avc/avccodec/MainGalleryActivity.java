package yzriver.avc.avccodec;

import com.itheima.demo.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * AVC编解码演示案例入口界面
 * @author zhangming
 */
public class MainGalleryActivity extends Activity implements OnClickListener{

	static{
		System.loadLibrary("avccodec");
		System.loadLibrary("avccodecjni");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_gallery_codec_activity);
		
		Button btn_avc_rec = (Button) findViewById(R.id.btn_avc_rec);
		Button btn_avc_list_play = (Button) findViewById(R.id.btn_avc_list_play);
		btn_avc_rec.setOnClickListener(this);
		btn_avc_list_play.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_avc_rec:
			intent = new Intent(this,AvcRecActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_avc_list_play:
			break;
		}
	}
}
