package com.powerhigh.gdfas.module;

import java.util.*;

/**
 * Description: �̳߳� <p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-4-2
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
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
     * ����������ThreadPool�Ĺ��캯��
     * @param max int ������ҵ�����̵߳�����
     * @param workerClass Class ҵ�����������
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
     * �������������մ���������ݶ��󣬲��ַ���ҵ�����̴߳���
     * @param data Object ���ݶ���
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
     * �����������������̶߳�ջwaiting�м�����õ�ҵ�����̶߳���
     * @param worker WorkerThread ҵ�����̶߳���
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
     * ҵ�����̣߳�ͨ��PoolWorker������ҵ����
     */
    class WorkerThread
      	extends Thread {
	    private PoolWorker worker;
	    private Object data;
	
	    /**
	     * ����������WorkerThread�Ĺ��캯��
	     * @param id String �߳�ID
	     * @param worker PoolWorker ҵ������
	     */
	    WorkerThread(String id, PoolWorker worker) {
	      super(id);
	      this.worker = worker;
	      data = null;
	    }
	
	    /**
	     * ���������������߳�
	     * @param data Object ����������ݶ���
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
     * ���ݷַ��̣߳������ݷַ���ҵ�����̴߳���
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
