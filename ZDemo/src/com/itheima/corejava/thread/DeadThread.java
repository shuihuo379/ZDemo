package com.itheima.corejava.thread;

/**
 * 死锁线程案例
 * @author zhangming
 * @date 2017/05/18
 */
public class DeadThread {
	class MyThread implements Runnable{
		private Object lock1 = new Object();
		private Object lock2 = new Object();
		
		public MyThread(){}
		
		@Override
		public void run() {
			if("1".equals(Thread.currentThread().getName())){
				synchronized(lock1){
					try {
						System.out.println("线程"+Thread.currentThread().getName()+"执行任务1");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					synchronized(lock2){
						try {
							System.out.println("线程"+Thread.currentThread().getName()+"执行任务2");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}else if("2".equals(Thread.currentThread().getName())){
				synchronized(lock2){
					try {
						System.out.println("线程"+Thread.currentThread().getName()+"执行任务(2)");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					synchronized(lock1){
						try {
							System.out.println("线程"+Thread.currentThread().getName()+"执行任务(1)");
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		MyThread mt = new DeadThread().new MyThread();
		for(int i=1;i<3;i++){
			Thread t = new Thread(mt,String.valueOf(i));
			t.start();
		}
	}
}
