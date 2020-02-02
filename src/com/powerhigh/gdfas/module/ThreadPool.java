package com.powerhigh.gdfas.module;

import java.util.*;

/**
 * Description: 线程池 <p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-2
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
public class ThreadPool{
	
	private Stack waiting;
	private int max;
	private Class workerClass;	
	private LinkedList performWorkDataPool;
	private DispatchThread dispatchThread;
	private boolean dispatchWaitting = false;
    private boolean noThreadWaitting = false;
    
    
    /**
     * 方法简述：ThreadPool的构造函数
     * @param max int 需启动业务处理线程的数量
     * @param workerClass Class 业务处理类的名称
     * @throws Exception
     */
    public ThreadPool(int max, Class workerClass,Object instance) throws
      	Exception {
    	this.max = max;
    	waiting = new Stack();
	    performWorkDataPool = new LinkedList();
	    this.workerClass = workerClass;
	    PoolWorker worker;
	    WorkerThread wThread;	    
	    dispatchThread = new DispatchThread("dispatch");
	    dispatchThread.start();
	    for (int i = 0; i < max; i++) {
	      worker = (PoolWorker) workerClass.newInstance();
	      worker.setModule(instance);
	      wThread = new WorkerThread("Worker#" + i, worker);
	      wThread.start();
	      waiting.push(wThread);
	    }
    }

    /**
     * 方法简述：接收待处理的数据对象，并分发给业务处理线程处理
     * @param data Object 数据对象
     * @return void
     * @throws InstantiationException 
     */ 
    public void performWork(Object data) throws InstantiationException {
    	synchronized (performWorkDataPool) {
    		performWorkDataPool.addLast(data);
    	}
    	if (dispatchWaitting) {
    		dispatchThread.wake();
    	}
    }
  
    /**
     * 方法简述：往可用线程堆栈waiting中加入可用的业务处理线程对象
     * @param worker WorkerThread 业务处理线程对象
     * @return boolean 
     */
    private boolean push(WorkerThread worker) {
    	boolean stayAround = false;
    	synchronized (waiting) {
    		stayAround = true;
    		waiting.push(worker);
	    }
	    if (noThreadWaitting) {
	      dispatchThread.wake();
	    }
	    return stayAround;
    }
    
    
    
    /**
     * 业务处理线程，通过PoolWorker来进行业务处理
     */
    class WorkerThread
      	extends Thread {
	    private PoolWorker worker;
	    private Object data;
	
	    /**
	     * 方法简述：WorkerThread的构造函数
	     * @param id String 线程ID
	     * @param worker PoolWorker 业务处理类
	     */
	    WorkerThread(String id, PoolWorker worker) {
	      super(id);
	      this.worker = worker;
	      data = null;
	    }
	
	    /**
	     * 方法简述：唤醒线程
	     * @param data Object 待处理的数据对象
	     * @return void
	     */
	    synchronized void wake(Object data) {
	      this.data = data;
	      notify();
	    }
	
	    synchronized void rest() {
	      try {
	        wait(100);
	      }
	      catch (Exception e) {
	        e.printStackTrace();
	      }
	    }
	
	    /**
	     * run
	     */
	    public void run(){
	      boolean stop = false;
	      while (!stop) {
	        Object processData;
	        synchronized (this) {
	          processData = data;
	          data = null;
	        }
	        if (processData == null) {
	          rest();
	        }
	        else {
	          worker.run(processData);
	          stop = ! (push(this));
	        }
	      }
	    }
    };

  

    /**
     * 数据分发线程，将数据分发给业务处理线程处理
     */
    class DispatchThread
      	extends Thread {

	    synchronized void wake() {
	      notify();
	    }
	
	    synchronized void rest() {
	      try {
	        wait();
	      }
	      catch (InterruptedException e) {
	        e.printStackTrace();
	      }
	    }
	
	    public DispatchThread(String id) {
	      super(id);
	    }
	
	    public void run() {
	      while (true) {
	        WorkerThread thread = null;
	        Object data = null;
	
	        synchronized (performWorkDataPool) {
	          if (!performWorkDataPool.isEmpty()) {
	            data = performWorkDataPool.removeFirst();
	          }
	        }
	        if (data != null) {
	          while (true) {
	            synchronized (waiting) {
	              if (!waiting.isEmpty()) {
	                thread = (WorkerThread) waiting.pop();
	              }
	            }
	            if (thread != null) {
	              thread.wake(data);
	              break;
	            }
	            else {
	              noThreadWaitting = true;
	              rest();
	              noThreadWaitting = false;
	            }
	          }
	        }
	        else {
	          dispatchWaitting = true;
	          rest();
	          dispatchWaitting = false;
	        }
	
	      }
	    }
    }
    
}
