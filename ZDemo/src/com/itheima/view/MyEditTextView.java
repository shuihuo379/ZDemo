package com.itheima.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.EditText;

public class MyEditTextView extends EditText {
	private OnInputCompleteListener mOnInputCompleteListener;

	public MyEditTextView(Context context) {
		super(context);
	}

	public MyEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction,Rect previouslyFocusedRect) {
		super.onFocusChanged(focused, direction, previouslyFocusedRect);
		if (!focused) {
			mOnInputCompleteListener.onInputComplete();
		}
	}

	public void setOnInputCompleteListener(OnInputCompleteListener l) {
		this.mOnInputCompleteListener = l;
	}
	
	public interface OnInputCompleteListener {
	    void onInputComplete();
	}

}
