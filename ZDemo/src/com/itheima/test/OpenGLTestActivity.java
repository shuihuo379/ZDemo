package com.itheima.test;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.os.Bundle;

/**
 * 初步了解openGL
 * @author zhangming
 */
public class OpenGLTestActivity extends Activity{
	private Renderer renderer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GLSurfaceView glView = new GLSurfaceView(this);
		renderer = new MyOpenGLRenderer();  //创建render  
		glView.setRenderer(renderer);
		setContentView(glView);
	}
	
	class MyOpenGLRenderer implements GLSurfaceView.Renderer{
		@Override
		public void onDrawFrame(GL10 gl) { //绘图的代码
			
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {  //屏幕发生变化时的操作，旋转
			gl.glViewport(0,0, width, height);  
			gl.glMatrixMode(GL10.GL_PROJECTION); //设置投影矩阵  
			gl.glLoadIdentity(); //重置投影矩阵  
			gl.glFrustumf(-10,10, -1,1,1,10); //设置视口大小 
			gl.glMatrixMode(GL10.GL_MODELVIEW); //选择模型观察矩阵  
			gl.glLoadIdentity(); //重置模型观察矩阵  
		}

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {  //初始化工作
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); //设置透视性能
		}
	}
}
