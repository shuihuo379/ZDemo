package com.itheima.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.itheima.demo.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Bundle;

/**
 * 纹理正方体旋转案例
 * @author zhangming
 */
public class OpenGLTwoActivity extends Activity{
	private GLSurfaceView glView;	// 使用GLSurfaceView 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        glView = new GLSurfaceView(this); //创建一个GLSurfaceView
        glView.setRenderer(new MyGLRenderer(this)); //使用定制的渲染器
        setContentView(glView);		
	}
	
    @Override
    protected void onPause(){
    	super.onPause();
    	glView.onPause();
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	glView.onResume();
    }
    
	public class MyGLRenderer implements GLSurfaceView.Renderer{
		private Context context;	// 应用的上下文句柄
		private TextureCube cube; //纹理正方体模型实例
		
		// 旋转角度
		private float xrot;	//X 旋转
		private float yrot;	//Y 旋转
		private float zrot;	//Z 旋转
		
		public MyGLRenderer(Context context){
			this.context = context;
			cube = new TextureCube();  //设置所用图形的数据数组缓冲区
		}
		
		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// 启用阴影平滑
			gl.glShadeModel(GL10.GL_SMOOTH);
			
			// 设置背景颜色
			gl.glClearColor(0.2f, 0.4f, 0.52f, 1.0f);
			
			// 设置深度缓存
			gl.glClearDepthf(1.0f);
			
			// 启用深度测试(启用了深度测试，那么这就不适用于同时绘制不透明物体)
			gl.glEnable(GL10.GL_DEPTH_TEST);
			
			/**
			 * 所作深度测试的类型,参数的值可以为 GL_NEVER（没有处理）、GL_ALWAYS（处理所有）、GL_LESS（小于）
			 * GL_LEQUAL（小于等于）、GL_EQUAL（等于）、GL_GEQUAL（大于等于）、GL_GREATER（大于）
			 * 或GL_NOTEQUAL（不等于），其中默认值是GL_LESS
			 * 一般来将，使用glDepthFunc(GL_LEQUAL);来表达一般物体之间的遮挡关系
			 */
			gl.glDepthFunc(GL10.GL_LEQUAL);
			
			// 告诉系统对透视进行修正
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
			
			// 禁止抖动以取得更好的性能
			gl.glDisable(GL10.GL_DITHER);
			
			//设置纹理
			cube.loadTexture(gl, context);		// 加载一种纹理
			gl.glEnable(GL10.GL_TEXTURE_2D);	// 启用2D纹理贴图  
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
			if(height == 0)	{ 
				height = 1;	// 防止被零除
			}
			
			// 重置当前的视图区域
			gl.glViewport(0, 0, width, height);
			
			// 选择投影矩阵
			gl.glMatrixMode(GL10.GL_PROJECTION);
			
			// 重置投影矩阵
			gl.glLoadIdentity();
			
			// 设置视图区域的大小
			GLU.gluPerspective(gl, 45.0f, (float)width/(float)height,0.1f,100.0f);
			
			// 选择模型观察矩阵
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			
			// 重置模型观察矩阵
			gl.glLoadIdentity();
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			// 清除屏幕和深度缓存
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			//gl.glColor4f(0.2f,0.8f,0.25f,0.8f);	
			
			// 重置当前的模型观察矩阵
			gl.glLoadIdentity();
			gl.glTranslatef(0.0f, 0.0f, -5.0f); // 向屏幕里移动5个单位
			gl.glScalef(0.8f, 0.8f, 0.8f);	// 缩小80%
			// 绕X轴旋转立方体 
			gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
			// 绕Y轴旋转立方体
			gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
			// 绕Z轴旋转立方体
			gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);
			
		     // 正方体
			cube.draw(gl);
			
			// 每次刷新之后更新旋转角度
			xrot += 0.3f;
			yrot += 0.2f;
			zrot += 0.4f;
		}
	}
	
	/**
	 * 生成一个带纹理的立方体
	 * 这里指定义一个面的顶点，立方体的其他面通过平移和旋转这个面来渲染
	 * @author zhangming
	 */
	public class TextureCube{
		private FloatBuffer vertexBuffer;	// 顶点数组缓冲区
		private FloatBuffer texBuffer;	// 纹理坐标数据缓冲区
		private int[] textureIDs = new int[1]; // 纹理-ID数组
		
		private float[] vertices = { // 定义一个面的顶点坐标
			-1.0f, -1.0f, 0.0f,  // 0. 左-底-前
			1.0f, -1.0f, 0.0f,   // 1. 右-底-前
			-1.0f,  1.0f, 0.0f,  // 2. 左-顶-前
			1.0f,  1.0f, 0.0f    // 3. 右-顶-前
		};
		
		private float[] texCoords = { // 定义上面的面的纹理坐标
	        0.0f, 1.0f,  // A. 左-下
	        1.0f, 1.0f,  // B. 右-下 
	        0.0f, 0.0f,  // C. 左-上 
	        1.0f, 0.0f   // D. 右-上
		};
		
		public TextureCube(){
			// 设置顶点数组，顶点数据为浮点数据类型。一个浮点类型的数据长度为四个字节
		    ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
		    vbb.order(ByteOrder.nativeOrder()); // 使用原生字节顺序
		    vertexBuffer = vbb.asFloatBuffer(); // 将字节类型缓冲区转换成浮点类型
		    vertexBuffer.put(vertices);         // 将数据复制进缓冲区
		    vertexBuffer.position(0);           // 定位到初始位置
		    
		    // 设置纹理坐标数组缓冲区，数据类型为浮点数据
		    ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4);
		    tbb.order(ByteOrder.nativeOrder());
		    texBuffer = tbb.asFloatBuffer();
		    texBuffer.put(texCoords);
		    texBuffer.position(0);
		}
		
		// 绘图
		public void draw(GL10 gl){
			gl.glFrontFace(GL10.GL_CCW);    // 正前面为逆时针方向
		    gl.glEnable(GL10.GL_CULL_FACE); // 使能剔除面
		    gl.glCullFace(GL10.GL_BACK);    // 剔除背面（不显示）
		    
		    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY); //使用顶点坐标数组
		    gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);  //使用纹理坐标数组
		    /**
		     * 定义纹理坐标数组缓冲区(参数 : size：纹理顶点坐标的分量个数; type：纹理坐标的数据类型;
		     * stride：位图的宽度，可以理解为相邻的两个纹理之间跨多少个字节，一般为0，因为一般不会在纹理中再添加其他的信息;
		     * pointer：存放纹理坐标的数组，指明将绘制的第i个点（i<count）分别对应着贴图的哪一个角，四个角分别用（0,1)（左上角）、(1,1)(右上角)、(1,0)(右下角)、（0，0）(左下角）表示)
		     */
		    gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuffer); 
		    
		    // 前
		    gl.glPushMatrix();
		    gl.glTranslatef(0.0f, 0.0f, 1.0f);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();
		    
		    // 左
		    gl.glPushMatrix();
		    gl.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
		    gl.glTranslatef(0.0f, 0.0f, 1.0f);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();
		    
		    // 后
		    gl.glPushMatrix();
		    gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		    gl.glTranslatef(0.0f, 0.0f, 1.0f);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();

		    // 右
		    gl.glPushMatrix();
		    gl.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
		    gl.glTranslatef(0.0f, 0.0f, 1.0f);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();

		    // 顶
		    gl.glPushMatrix();
		    gl.glRotatef(270.0f, 1.0f, 0.0f, 0.0f);
		    gl.glTranslatef(0.0f, 0.0f, 1.0f);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();

		    // 底
		    gl.glPushMatrix();
		    gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		    gl.glTranslatef(0.0f, 0.0f, 1.0f);
		    gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		    gl.glPopMatrix();
		    
		    // 恢复原来的状态
		    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY); 
		    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		    gl.glDisable(GL10.GL_CULL_FACE);
		}
		
		// 加载一个图像到GL纹理
		public void loadTexture(GL10 gl, Context context) {
			gl.glGenTextures(1, textureIDs, 0);	// 生成纹理ID数组
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[0]);	// 绑定到纹理ID
			// 设置纹理过滤方式
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			
			// 构造一个输入流来加载纹理文件"res/drawable/nehe.bmp"
			InputStream ins = context.getResources().openRawResource(R.drawable.nehe);
			Bitmap bmp;
			try {
				// 读取并将输入流解码成位图
				bmp = BitmapFactory.decodeStream(ins);
			} finally {
				try {
					ins.close();
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
			// 根据加载的位图为当前绑定的纹理ID建立纹理
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);
		}
	} 
}
