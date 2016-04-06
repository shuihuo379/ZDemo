package com.itheima.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;

/**
 * 初步了解openGL函数库,绘制三角形和正方形
 * @author zhangming
 */
public class OpenGLOneActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView glView = new GLSurfaceView(this);
		Renderer renderer = new MyOpenGLRenderer();  //创建render  
		glView.setRenderer(renderer);
		setContentView(glView);
	}
	
	class MyOpenGLRenderer implements GLSurfaceView.Renderer{
		private FloatBuffer mTriangleBuffer;
	    private FloatBuffer quateBuffer ;  
		private FloatBuffer mColorBuffer;
		
		private float[] mTriangleArray = {  
	         0f,1f,0f,  
	         -1f,-1f,0f,  
	         1f,-1f,0f  
		};

		//正方形的四个顶点  
	    private float[] mQuateArray = {  
	         -1f, -1f, 0f,  
	         1f, -1f, 0f,  
	         -1f, 1f, 0f,  
	         1f, 1f, 0f,  
	    };  
		
		private float[] mColorArray={  
	         1f,0f,0f,1f,   //红  
	         0f,1f,0f,1f,   //绿  
	         0f,0f,1f,1f    //蓝  
		};
		
		@Override
		public void onDrawFrame(GL10 gl) { //绘图的代码
			 gl.glClear(GL10.GL_COLOR_BUFFER_BIT|GL10.GL_DEPTH_BUFFER_BIT);  
		     
		     //绘制小三角形  
		     gl.glLoadIdentity();  //将当前点移到了屏幕中心：类似于一个复位操作(X坐标轴从左至右，Y坐标轴从下至上，Z坐标轴从里至外,OpenGL屏幕中心的坐标值是X和Y轴上的0.0f点)
		     gl.glTranslatef(-1.5f, 0.0f, -6.0f);  //将你绘点坐标的原点在当前原点的基础上平移一个(x,y,z)向量
		     gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  //设置定点数组  
		     gl.glEnableClientState(GL10.GL_COLOR_ARRAY);   //设置颜色数组 -- 开启颜色渲染功能.  
		     gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);   //使用数组作为颜色(即采用平滑着色)  
		     gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mTriangleBuffer);  //数组指向三角形顶点buffer
		     
		     /**
		      * 参数1:模式有三种取值
		      * @type GL_TRIANGLES,代表每三个顶之间绘制三角形,之间不连接
		      * @type GL_TRIANGLE_FAN,以V0V1V2,V0V2V3,V0V3V4，……的形式绘制三角形
		      * @type GL_TRIANGLE_STRIP,顺序在每三个顶点之间均绘制三角形。这个方法可以保证从相同的方向上所有三角形均被绘制。以V0V1V2,V1V2V3,V2V3V4……的形式绘制三角形
		      *	参数2:从数组缓存中的哪一位开始绘制，一般都定义为0
		      * 参数3:顶点的数量
		      */
		     gl.glDrawArrays(GL10.GL_TRIANGLES, 0, 3); 
		     gl.glDisableClientState(GL10.GL_COLOR_ARRAY);  //关闭颜色数组 -- 关闭颜色渲染功能 
		     gl.glFinish();
		     
		     //绘制正方形  
		     gl.glColor4f(0.2f, 0.5f, 0.8f, 0.8f);  //红，绿，蓝，透明度(设置当前色为蓝色,采用单调着色)
	         gl.glLoadIdentity();  
	         gl.glTranslatef(1.5f, 0.0f,-6.0f);  
	         gl.glRotatef(90.0f,0.0f,1.0f,0.0f);
	         gl.glVertexPointer(3, GL10.GL_FLOAT, 0, quateBuffer);  
	         gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);  
//	         gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);   //取消顶点数组
	         gl.glFinish();  
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {  //屏幕发生变化时的操作，旋转
			 gl.glViewport(0, 0, width, height);  
		     float ratio = (float) width / height;  
		     
		     gl.glMatrixMode(GL10.GL_PROJECTION);  
		     gl.glLoadIdentity();  
		     gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10); //实现了Surface和坐标系之间的映射关系。它是以透视投影的方式来进行映射的
		     gl.glMatrixMode(GL10.GL_MODELVIEW);  
		     gl.glLoadIdentity();  
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {  //初始化工作
			gl.glShadeModel(GL10.GL_SMOOTH);  //启用smooth shading(阴影平滑),阴影平滑通过多边形精细的混合色彩,并对外部光进行平滑
			gl.glClearColor(0.8f, 0.3f, 0.2f, 0f); //设置清除屏幕时所用的颜色,0.0f代表最黑的情况,1.0f就是最亮的情况
			
			/**
			gl.glEnable(GL10.GL_BLEND); // 打开混合
			gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE); // 基于源象素alpha通道值的半透明混合函数
			gl.glDisable(GL10.GL_DEPTH_TEST); //关闭深度测试
			**/
			
			gl.glClearDepthf(1.0f);  
	        gl.glEnable(GL10.GL_DEPTH_TEST); //开启深度测试(那么这就不适用于同时绘制不透明物体)
	        gl.glDepthFunc(GL10.GL_LEQUAL); //深度缓存不断的对物体进入屏幕内部有多深进行跟踪,它的排序决定那个物体先画。这样您就不会将一个圆形后面的正方形画到圆形上来
	        
	        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); //告诉OpenGL我们希望进行最好的透视修正。这会十分轻微的影响性能。但使得透视图看起来好一点
		
	        gl.glDisable(GL10.GL_DITHER);  // 禁止抖动以取得更好的性能  
	        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);  //表示启用顶点数组
	        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);  
	        
	        mTriangleBuffer = BufferUtil.floatToBuffer(mTriangleArray);  
	        quateBuffer = BufferUtil.floatToBuffer(mQuateArray);  
	        mColorBuffer = BufferUtil.floatToBuffer(mColorArray);
		}
	}
	
	/**
	 * 工具类,负责将数组转成buffer
	 * @author zhangming
	 */
	public static class BufferUtil {  
	    public static FloatBuffer floatToBuffer(float[] arr){  
	        ByteBuffer mb = ByteBuffer.allocateDirect(arr.length*4); //先初始化buffer，数组的长度*4，因为一个float占4个字节  
	        mb.order(ByteOrder.nativeOrder());   //数组排序用nativeOrder  
	        FloatBuffer floatBuffer = mb.asFloatBuffer();  
	        floatBuffer.put(arr);  
	        floatBuffer.position(0);  
	        return floatBuffer;  
	    }  
	      
	    public static IntBuffer intToBuffer(int[] arr){  
	        ByteBuffer mb = ByteBuffer.allocateDirect(arr.length*4);  //先初始化buffer，数组的长度*4，因为一个int占4个字节  
	        mb.order(ByteOrder.nativeOrder());   //数组排序用nativeOrder  
	        IntBuffer intBuffer = mb.asIntBuffer();  
	        intBuffer.put(arr);  
	        intBuffer.position(0);  
	        return intBuffer;  
	    }  
	}
}
