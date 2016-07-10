package com.itheima.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.itheima.util.Constants;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 自定义蓝牙通信服务程序
 * 思路:使用多个线程并发操作相关事务
 * @author zhangming
 */
public class BluetoothChatService {
	private final BluetoothAdapter mAdapter;
	private final Handler mHandler;
	
    // Name for the SDP record when creating server socket
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    
	private AcceptThread mSecureAcceptThread;
    private AcceptThread mInsecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    
	private int mState;
	
    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;       // we're doing nothing
    public static final int STATE_LISTEN = 1;     // now listening for incoming connections
    public static final int STATE_CONNECTING = 2; // now initiating an outgoing connection
    public static final int STATE_CONNECTED = 3;  // now connected to a remote device
	
	public BluetoothChatService(Context context, Handler handler) {
		this.mAdapter = BluetoothAdapter.getDefaultAdapter();
		this.mHandler = handler;
		this.mState = STATE_NONE; //设置初始状态
	}
	
	 /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    private synchronized void setState(int state) {
        mState = state;
        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }
    
    /**
     * Start the chat service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume()
     */
    public synchronized void start() {
    	// Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        setState(STATE_LISTEN);

        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread(true);
            mSecureAcceptThread.start();
        }
        if (mInsecureAcceptThread == null) {
            mInsecureAcceptThread = new AcceptThread(false);
            mInsecureAcceptThread.start();
        }
    }
    
    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device The BluetoothDevice to connect
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    public synchronized void connect(BluetoothDevice device, boolean secure) {
        // Cancel any thread attempting to make a connection make it secure
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, secure);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }
    
    
    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device, final String socketType) {
	  Log.d("test", "connected, Socket Type:" + socketType);
      // 已经完成连接，所以结束ConnectThread
      if (mConnectThread != null) {
          mConnectThread.cancel();
          mConnectThread = null;
      }

      // 结束所有正在运行的ConnectedThread
      if (mConnectedThread != null) {
          mConnectedThread.cancel();
          mConnectedThread = null;
      }

      // 结束所有正在运行的AcceptThread因为我们只要和一个蓝牙设备通信！
      if (mSecureAcceptThread != null) {
          mSecureAcceptThread.cancel();
          mSecureAcceptThread = null;
      }
      if (mInsecureAcceptThread != null) {
          mInsecureAcceptThread.cancel();
          mInsecureAcceptThread = null;
      }
      
      // 开启一个新的ConnectedThread用于管理消息的读写
      mConnectedThread = new ConnectedThread(socket, socketType);
      mConnectedThread.start();
      
      // 把已经连上的蓝牙设备名发给UI Activity
      Message msg = mHandler.obtainMessage(Constants.MESSAGE_DEVICE_NAME);
      Bundle bundle = new Bundle();
      bundle.putString(Constants.DEVICE_NAME, device.getName());
      msg.setData(bundle);
      mHandler.sendMessage(msg);
      
      setState(STATE_CONNECTED);
    }
   
    /**
     * Stop 所有线程
     */
    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }

        if (mInsecureAcceptThread != null) {
            mInsecureAcceptThread.cancel();
            mInsecureAcceptThread = null;
        }
        setState(STATE_NONE);
    }
    
    private void connectionFailed(){
    	// Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        
        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }
    
    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost() {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.TOAST, "Device connection was lost");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothChatService.this.start();
    }
    
    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (mState != STATE_CONNECTED) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }


    
    
    /***---------------------- 三类线程定义开始 ---------------------------***/
    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     * 监听来自其他设备的蓝牙连接，
     */
    private class AcceptThread extends Thread {
        private BluetoothServerSocket mServerSocket; //The local server socket
        private String mSocketType;
        
        public AcceptThread(boolean secure) {
        	mSocketType = secure ? "Secure" : "Insecure";
        	try{
        		if(secure){
        			mServerSocket = mAdapter.listenUsingRfcommWithServiceRecord(NAME_SECURE,MY_UUID_SECURE);
            	}else{
            		mServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
            	}
        	}catch(IOException e){
        		e.printStackTrace();
        	}
        }
        
        @Override
        public void run() {
        	setName("AcceptThread " + mSocketType);
        	BluetoothSocket socket = null;
        	while (mState != STATE_CONNECTED && mServerSocket!=null) {
        		try {
					socket = mServerSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}
        		
        		if(socket!=null){
        			synchronized (BluetoothChatService.this) {
						switch (mState) {
						case STATE_LISTEN:
						case STATE_CONNECTING:
							// Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice(),mSocketType);
                            break;
						case STATE_NONE:
						case STATE_CONNECTED:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e("test", "Could not close unwanted socket", e);
                            }
                            break;
						}
					}
        		}
        	}
        }
        
        public void cancel() {
        	try {
				mServerSocket.close(); //断开连接
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
    
    
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
	    private final BluetoothSocket mSocket;
        private final BluetoothDevice mDevice;
        private String mSocketType;
        
        public ConnectThread(BluetoothDevice device, boolean secure) {
        	mDevice = device;
        	BluetoothSocket tmp = null;
            mSocketType = secure ? "Secure" : "Insecure";
            try{
            	if (secure) {
                   tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
                } else {
                   tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                }
            }catch(Exception e){
            	e.printStackTrace();
            }
            mSocket = tmp;
        }
        
        @Override
        public void run() {
        	// 在执行连接时务必关闭蓝牙发现以提高效率
            mAdapter.cancelDiscovery();
            // 创建一个 BluetoothSocket 连接
            try {
                mSocket.connect();
            } catch (IOException e) {
                // Close the socket
                try {
                    mSocket.close();
                } catch (IOException e2) {
                	e2.printStackTrace();
                }
                connectionFailed();
                return;
            }
            
            // 已经完成蓝牙连接，重置ConnectThread
            synchronized (BluetoothChatService.this) {
                mConnectThread = null;
            }
            // 连接完成，开启监听
            connected(mSocket, mDevice, mSocketType);
        }
        
        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
    }
    
    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     * 连接完成，进行读写
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        
        public ConnectedThread(BluetoothSocket socket, String socketType) {
        	mSocket = socket;
        	InputStream tmpIn = null;
            OutputStream tmpOut = null;
            
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("test", "temp sockets not created", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }
        
        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;
            
            // 当已经连接上蓝牙设备后保持连接
            while (true) {
                try {
                    // 读InputStream
                    bytes = mmInStream.read(buffer);
                    // 发送读取的消息到UI Activity
                    mHandler.obtainMessage(Constants.MESSAGE_READ, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    connectionLost();
                    // Start the service over to restart listening mode
                    BluetoothChatService.this.start();
                    break;
                }
            }
        }
        
        /**
         * Write to the connected OutStream.
         *
         * @param buffer The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(Constants.MESSAGE_WRITE, -1, -1, buffer).sendToTarget();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                mSocket.close();
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }
    }
    /***---------------------- 三类线程定义结束 ---------------------------***/
}
