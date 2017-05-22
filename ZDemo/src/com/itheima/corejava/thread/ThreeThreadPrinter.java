package com.itheima.corejava.thread;

/**
 * 三线程同步打印数据
 * @author zhangming
 * @date 2017/05/18
 */
public class ThreeThreadPrinter implements Runnable{
	private String name;     
    private Object prev;     
    private Object self;    
    //private static int count = 10*3;
    
    private ThreeThreadPrinter(String name, Object prev, Object self) {     
        this.name = name;     
        this.prev = prev;     
        this.self = self;     
    }     
    
    @Override    
    public void run() {     
    	int count = 10;     
        while (count > 0) {     
            synchronized (prev) {     
                synchronized (self) {     
                    System.out.print(name);     
                    count--;    
                    try{  
                    	Thread.sleep(1);  
                    }  
                    catch (InterruptedException e){  
                    	e.printStackTrace();  
                    }  
                    self.notify();  //随机通知一个正在等待此锁的线程(wait),唤醒,终止阻塞状态  
                }     
                try {     
                    prev.wait();  //释放此对象锁,同时使该线程处于阻塞状态，直到其他线程持有该锁去调用notify方法,当其他线程退出同步块，释放该锁时终止等待   
                } catch (InterruptedException e) {     
                    e.printStackTrace();     
                }     
            }     
        }     
    }     
    
    public static void main(String[] args) throws Exception {     
        Object lockA = new Object();     
        Object lockB = new Object();     
        Object lockC = new Object();    
        
        ThreeThreadPrinter pa = new ThreeThreadPrinter("A", lockC, lockA);     
        ThreeThreadPrinter pb = new ThreeThreadPrinter("B", lockA, lockB);     
        ThreeThreadPrinter pc = new ThreeThreadPrinter("C", lockB, lockC);     
             
        new Thread(pa).start();  
        Thread.sleep(10);  
        new Thread(pb).start();  
        Thread.sleep(10);  
        new Thread(pc).start();  
        Thread.sleep(10);  
    }
}
