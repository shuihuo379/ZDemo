package com.itheima.test;

import android.app.Activity;
import android.os.Bundle;
import android.widget.SeekBar;

import com.itheima.demo.R;
import com.itheima.view.PanelView;

/**
 * 表盘转动测试类
 * @author zhangming
 */
public class PanelViewActivity extends Activity{
	private PanelView mPanelView;
	private SeekBar mSeekBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.panel_view_activity);
		mPanelView = (PanelView) findViewById(R.id.panelView);
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mPanelView.setPercent(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
	    });
	}
}
