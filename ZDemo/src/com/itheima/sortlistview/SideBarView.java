package com.itheima.sortlistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.itheima.demo.R;

public class SideBarView extends View{
	public static String[] letters = { "A", "B", "C", "D", "E", "F", "G", "H", "I",
		"J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V","W", "X", "Y", "Z", "#" }; //26个字母
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener; // 触摸事件
	private int choose = -1;  // 选中
	private TextView mTextDialog;
	private Paint paint = new Paint();

	public void setTextView(TextView mTextDialog) {
		this.mTextDialog = mTextDialog;
	}

	public SideBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SideBarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SideBarView(Context context) {
		super(context);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		// 获取焦点改变背景颜色.
		int height = getHeight();// 获取对应高度
		int width = getWidth(); // 获取对应宽度
		int singleHeight = height / letters.length;// 获取每一个字母的高度

		for (int i = 0; i < letters.length; i++) {
			paint.setColor(Color.rgb(33, 65, 98));
			// paint.setColor(Color.WHITE);
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(20);
			// 选中的状态
			if (i == choose) {
				paint.setColor(Color.parseColor("#3399ff"));
				/**
				 * 在xml布局文件中使用android:textStyle=”bold”可以将英文设置成粗体，但是不能将中文设置成粗体，
				 * 将中文设置成粗体的方法是：使用TextPaint的仿“粗体”设置setFakeBoldText为true
				 */
				paint.setFakeBoldText(true); 
			}
			// x坐标等于中间-字符串宽度的一半.
			float xPos = width / 2 - paint.measureText(letters[i]) / 2; //起始字符串所在的x轴坐标
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(letters[i], xPos, yPos, paint);
			paint.reset();  // 重置画笔
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();// 点击y坐标
		final int oldChoose = choose;
		final OnTouchingLetterChangedListener listener = onTouchingLetterChangedListener;
		final int c = (int) (y / getHeight() * letters.length);// 点击y坐标所占总高度的比例*letters数组的长度就等于点击letters中的个数.

		switch (action) {
			case MotionEvent.ACTION_UP:
				setBackgroundDrawable(new ColorDrawable(0x00000000));
				choose = -1;
				invalidate();
				if (mTextDialog != null) {
					mTextDialog.setVisibility(View.INVISIBLE);
				}
				break;
			default:
				setBackgroundResource(R.drawable.sidebar_background);
				if (oldChoose != c) {
					if (c >= 0 && c < letters.length) {
						if (listener != null) {
							listener.onTouchingLetterChanged(letters[c]);
						}
						if (mTextDialog != null) {
							mTextDialog.setText(letters[c]); //对话框显示选中字母值
							mTextDialog.setVisibility(View.VISIBLE);
						}
						choose = c;
						invalidate();
					}
				}
				break;
		}
		return true;
	}
	
	/**
	 * 向外公开的方法
	 * @param onTouchingLetterChangedListener
	 */
	public void setOnTouchingLetterChangedListener(
			OnTouchingLetterChangedListener onTouchingLetterChangedListener) {
		this.onTouchingLetterChangedListener = onTouchingLetterChangedListener;
	}

	public interface OnTouchingLetterChangedListener {
		public void onTouchingLetterChanged(String s);
	}

}
