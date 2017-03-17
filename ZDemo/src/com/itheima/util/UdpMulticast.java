package com.itheima.util;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Java UDP组播(多播)代码
 * @author zhangming
 * @date 2017/03/17
 */
public class UdpMulticast {
	private static String multicastAddress = "239.0.0.1";
	private static int localPort = 9998;
	
	static class UdpMulticastSender{
		//每一个ip数据报文中都包含一个TTL,每当有路由器转发该报文时,TTL减1,直到减为0时,生命周期结束
		private static int ttlTime = 4; //TTL生存期
		
		public static void main(String[] args) throws Exception {
			InetAddress inetAddress = InetAddress.getByName(multicastAddress);
			if(!inetAddress.isMulticastAddress()){ //检测该地址是否是多播地址  
				 throw new Exception("地址不是多播地址");  
			}
			
			//创建多播(组播)套接字,并设置生存期ttlTime
			MulticastSocket sendMultiSocket = new MulticastSocket();
			sendMultiSocket.setTimeToLive(ttlTime);
			
			//发送组播包
			String sendMsg = "Hello World!!!";
			DatagramPacket packet = new DatagramPacket(sendMsg.getBytes(),sendMsg.length(),inetAddress,localPort);
			sendMultiSocket.send(packet);
		}
	}
	
	static class UdpMulticastReceiver{
		public static void main(String[] args) throws Exception {
			InetAddress inetAddress = InetAddress.getByName(multicastAddress);
			if(!inetAddress.isMulticastAddress()){ //检测该地址是否是多播地址
				throw new Exception("地址不是多播地址");  
			}
			
			//将多播地址multicastAddress="224.0.0.1"加入到组中
			MulticastSocket recvMultiSocket = new MulticastSocket(localPort);
			recvMultiSocket.joinGroup(inetAddress);
			
			//接收组内发过来的数据包
			byte[] buffer = new byte[1024];
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
			recvMultiSocket.receive(packet);
			
			//打印数据包内容到控制台
			System.out.println(new String(packet.getData()).trim());
			
			//关闭组播Socket
			recvMultiSocket.close();
		}
	}
}
