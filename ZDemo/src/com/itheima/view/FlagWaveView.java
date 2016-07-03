package com.itheima.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.itheima.demo.R;

/**
 * 旗帜图片效果实现(正弦函数图片)
 * 研究像素块分析问题
 * @author zhangming
 */
public class FlagWaveView extends View{
	private Context context;
	private int HorizontalCount = 14; //横向的网格数目,此处为(14+1=15)个横向点
	private int VerticalCount = 9; //纵向的网格数目此处为(9+1=10)个纵向点
	private Bitmap bitmap;
	private float orig[] = new float[300];
	private float verts[] = new float[300]; //总共存储15*10*2=300个点
	private static final int A = 20;
	
	public FlagWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.context = context;
		initData();
	}

	public FlagWaveView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		initData();
	}

	public FlagWaveView(Context context) {
		super(context);
		this.context = context;
		initData();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		flagWave(canvas);
	}
	
	private void initData() {
		bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.wall);
		float bitmapWidth = bitmap.getWidth();
		float bitmapHeight = bitmap.getHeight();
		
		int index = 0;
		for(int y=0;y<=VerticalCount;y++){
			float fy = bitmapHeight*y/VerticalCount;
			for(int x=0;x<=HorizontalCount;x++){
				float fx = bitmapWidth*x/HorizontalCount;
				orig[index*2+0]= verts[index*2+0]=fx;
				orig[index*2+1]= verts[index*2+1]=fy+200; //图片整体向下移动200px,避免图片扭曲的效果被屏幕遮挡
				index = index+1;
			}
		}
	}
	
	private void flagWave(Canvas canvas){
		for(int j=0;j<=VerticalCount;j++){
			for(int i=0;i<=HorizontalCount;i++){
				verts[(j*(HorizontalCount+1)+i)*2+0]+=0;
				float offsetY = (float) Math.sin((float)i/HorizontalCount*2*Math.PI);
				verts[(j*(HorizontalCount+1)+i)*2+1]= orig[(j*(HorizontalCount+1)+i)*2+1]+offsetY*A;
			}
		}
		canvas.drawBitmapMesh(bitmap,HorizontalCount, VerticalCount, verts, 0 , null, 0, null);
	}
}
