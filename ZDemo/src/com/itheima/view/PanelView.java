package com.itheima.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.demo.R;
import com.itheima.util.PxUtils;

/**
 * 自定义仪表盘控件
 * @author zhangming
 */
public class PanelView extends View{
	private Context mContext;
	private int mWidth;
	private int mHeight;
	private int mPercent;  
	private int mMinCircleRadius;
    private float mTikeWidth; //刻度宽度  

	//设置文字颜色  
    private int mTextColor;  
    private int mArcColor;  
    //小圆和指针颜色  
    private int mMinCircleColor;  
    //刻度的个数  
    private int mTikeCount;  
    //文字的大小  
    private int mTextSize;  
    //文字内容  
    private String mText = ""; 
    //第二个弧的宽度  
    private int mSecondArcWidth;  
	
	public PanelView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;  
		TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.PanelView,defStyleAttr,0);
		mArcColor = ta.getColor(R.styleable.PanelView_arcColor,Color.parseColor("#5FB1ED"));
		mMinCircleColor = ta.getColor(R.styleable.PanelView_pointerColor,Color.parseColor("#C9DEEE"));
		mTikeCount = ta.getInt(R.styleable.PanelView_tikeCount,12);  
	    mTextSize = ta.getDimensionPixelSize(PxUtils.spToPx(R.styleable.PanelView_android_textSize,mContext),24);  
	    mText = ta.getString(R.styleable.PanelView_android_text);  
	    mSecondArcWidth = 50;  
	}

	public PanelView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public PanelView(Context context) {
		this(context,null);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY){
            mWidth = widthSize;
        }else {
            mWidth = PxUtils.dpToPx(200,mContext);
        }
        if(heightMode == MeasureSpec.EXACTLY){
            mHeight = heightSize;
        }else {
            mHeight = PxUtils.dpToPx(200,mContext);
        }
        setMeasuredDimension(mWidth, mHeight);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//初始化画笔
		Paint paint = new Paint();   
		int strokeWidth = 3;  
		paint.setStrokeWidth(strokeWidth);  
		paint.setAntiAlias(true);  
		paint.setStyle(Paint.Style.STROKE);  
		paint.setColor(mArcColor);  
		
		//画最外面线条  
		canvas.drawArc(new RectF(strokeWidth, strokeWidth, mWidth - strokeWidth, mHeight - strokeWidth),145,250,false, paint);
		paint.setStrokeWidth(mSecondArcWidth);
		
		//绘制粗线
        RectF secondRectF = new RectF(strokeWidth + 50, strokeWidth + 50, mWidth - strokeWidth - 50, mHeight - strokeWidth - 50);
        float secondRectWidth = mWidth - strokeWidth - 50 - (strokeWidth + 50);
        float secondRectHeight = mHeight - strokeWidth - 50 - (strokeWidth + 50);
        float percent = mPercent / 100f;
        
        //充满的圆弧的度数    -5是大小弧的偏差
        float fill = 250 * percent ;
        //空的圆弧的度数
        float empty = 250 - fill;
        if(percent==0){
           paint.setColor(Color.WHITE);
        }
        //画粗弧突出部分左端
        canvas.drawArc(secondRectF,135,11,false,paint);
        //画粗弧的充满部分
        canvas.drawArc(secondRectF, 145, fill, false, paint);
        paint.setColor(Color.WHITE);
        //画粗弧的未充满部分
        canvas.drawArc(secondRectF, 145 + fill, empty, false, paint);
        //画粗弧突出部分右端
        if(percent == 1){
        	paint.setColor(mArcColor);
        }
        canvas.drawArc(secondRectF,144+fill+empty,10,false,paint);
        
        //绘制小圆外圈
        paint.setColor(mArcColor);
        paint.setStrokeWidth(3);
        canvas.drawCircle(mWidth / 2, mHeight / 2, 30, paint);

        //绘制小圆内圈
        paint.setColor(mMinCircleColor);
        paint.setStrokeWidth(8);
        mMinCircleRadius = 15;
        canvas.drawCircle(mWidth / 2, mHeight / 2, mMinCircleRadius,paint);
        
        //绘制刻度
        paint.setColor(mArcColor);  
        mTikeWidth = 20;  
        paint.setStrokeWidth(3);  
        canvas.drawLine(mWidth / 2, 0, mWidth / 2, mTikeWidth, paint);  //绘制第一条最上面的刻度  
        float rAngle = 250f / mTikeCount;  //旋转的角度
        //通过旋转画布 绘制右面的刻度
        for (int i = 0; i < mTikeCount / 2; i++) {
            canvas.rotate(rAngle, mWidth / 2, mHeight / 2);
            canvas.drawLine(mWidth / 2, 0, mWidth / 2, mTikeWidth,paint);
        }
        //现在需要将将画布旋转回来
        canvas.rotate(-rAngle * mTikeCount / 2, mWidth / 2, mHeight / 2);
        
        //通过旋转画布 绘制左面的刻度
        for (int i = 0; i < mTikeCount / 2; i++) {
            canvas.rotate(-rAngle, mWidth / 2, mHeight / 2);
            canvas.drawLine(mWidth / 2, 0, mWidth / 2, mTikeWidth, paint);
        }
        //现在需要将将画布旋转回来
        canvas.rotate(rAngle * mTikeCount / 2, mWidth / 2, mHeight / 2);
        
        //绘制指针
        paint.setColor(mMinCircleColor);
        paint.setStrokeWidth(4);
        //按照百分比绘制刻度
        canvas.rotate(( 250 * percent - 250/2), mWidth / 2, mHeight / 2);
        canvas.drawLine(mWidth / 2, (mHeight / 2 - secondRectHeight / 2) + mSecondArcWidth / 2 + 2, mWidth / 2, mHeight / 2 - mMinCircleRadius, paint);
        //将画布旋转回来
        canvas.rotate(-( 250 * percent - 250/2), mWidth / 2, mHeight / 2);
	}
	
	/**
     * 设置百分比
     * @param percent
     */
    public void setPercent(int percent) {
        mPercent = percent;
        invalidate();
    }

    /**
     * 设置文字
     * @param text
     */
    public void setText(String text){
        mText = text;
        invalidate();
    }

    /**
     * 设置圆弧颜色
     * @param color
     */

    public void setArcColor(int color){
        mArcColor = color;
        invalidate();
    }


    /**
     * 设置指针颜色
     * @param color
     */
    public void setPointerColor(int color){
        mMinCircleColor = color;
        invalidate();
    }

    /**
     * 设置文字大小
     * @param size
     */
    public void setTextSize(int size){
        mTextSize = size;
        invalidate();
    }

    /**
     * 设置粗弧的宽度
     * @param width
     */
    public void setArcWidth(int width){
        mSecondArcWidth = width;
        invalidate();
    }
}
