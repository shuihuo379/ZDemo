package com.itheima.net;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

/**
 * HTTP有关文件上传下载封装
 * @author zhangming
 * @date 2016/02/05
 */
public class FileHttpClient {
	/**
	 * HttpClient post方法上传文件
	 * @param url 基地址
	 * @param filePath 文件目录路径
	 * @param filename 文件名
	 * @return json字符串
	 */
	public static String PostUploadByHttpClient(Context context, String url,File mFile) {
		HttpClient client = new DefaultHttpClient();  //创建默认的HttpClient实例
		try{
			InputStream input = null;
			if(mFile.exists()){  //判断文件是否存在,不存在返回null
				InputStream in = new FileInputStream(mFile);
				byte[] b = new byte[(int)mFile.length()];
				while (in.read(b) != -1) {
					in.read(b);
				}
				in.close();
			    input = new ByteArrayInputStream(b);  //将文件转化为二进制输入流
			}else{
				return null;
			}
			
			InputStreamEntity entity = new InputStreamEntity(input,mFile.length());
			entity.setContentType("binary/octet-stream"); //二进制流方式
			//当不能预先确定报文体的长度时，不可能在头中包含Content-Length域来指明报文体长度，此时就需要通过Transfer-Encoding域来确定报文体长度。
		    //通常情况下，Transfer-Encoding域的值应当为chunked,表明采用chunked编码方式来进行报文体的传输。chunked编码是HTTP/1.1 RFC里定义的一种编码方式
			entity.setChunked(false);  
			StringBuilder fileUrl = new StringBuilder(url);
			fileUrl.append("?resourceFileName=" +mFile.getName());
			
        	HttpPost httpPost = new HttpPost(fileUrl.toString());
			httpPost.setEntity(entity);  //设置实体
			HttpResponse response = client.execute(httpPost);
			if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				throw new RuntimeException("请求失败");
			}
			HttpEntity resEntity = response.getEntity();  //获取服务器返回的实体数据
			return (resEntity == null)? null :EntityUtils.toString(resEntity,HTTP.UTF_8);
			
		}catch (UnsupportedEncodingException e) {
			Log.w("test", e.getMessage());
			return null;
		} catch (ClientProtocolException e) {
			Log.w("test", e.getMessage());
			return null;
		} catch (IOException e) {
			throw new RuntimeException("连接失败", e);
		} finally{
			client.getConnectionManager().shutdown();
		}
	}
	
	/**
	 * @param context
	 * @param url 基地址
	 * @param paramFileName 要获取的文件名
	 * @param outFile 输出的文件
	 * @return
	 */
	public static void getDownloadByHttpClient(Context context, String url,String paramFileName,File outFile){
		HttpClient client = new DefaultHttpClient();
		InputStream in = null;
		OutputStream out = null;
		try{
			HttpGet httpGet = new HttpGet(url+"?resourceFileName="+paramFileName);
			HttpResponse response = client.execute(httpGet);
			if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK){
				throw new RuntimeException("请求失败");
			}
			
			HttpEntity entity = response.getEntity(); //返回的是下载的二进制文件流
			in = entity.getContent();
			if(in == null){
				throw new IOException("文件不存在");
			}
			out = new FileOutputStream(outFile);
			IOUtils.copy(in, out);
		}catch(ClientProtocolException e){
			Log.w("test", e.getMessage());
		}catch (IOException e) {
			Log.w("test",e.getMessage());
		}finally{
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
			client.getConnectionManager().shutdown();
		}
	}
}
