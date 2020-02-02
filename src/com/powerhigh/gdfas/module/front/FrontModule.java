package com.powerhigh.gdfas.module.front;

import org.apache.log4j.Category;

import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.util.Util;
import com.powerhigh.gdfas.module.ThreadPool;

public class FrontModule extends AbstractModule implements Runnable{
	
    //加载日志
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.front.FrontModule.class);
	
	private Thread runThread = null;
	private ThreadPool pool = null;
	
	
	public void send(DataObject data) throws Exception{
		cat.info("send data:"+data.sjz);
		System.out.println("[FrontModule]send data:"+data.sjz);

	    //2009-10-18加上与通信前置机通信的前导字符
	    String sSJZ = Util.addFront(data.sjz, data.gylx,data.txfs); 
		communicateWithFront.SendHexToFront(sSJZ);
	}
	
	public void init() throws Exception{
		String front_ip = this.getParameter("ip");
		String front_port = this.getParameter("port");
		String workerThreadNum = this.getParameter("workerThreadNum");
		String account = this.getParameter("account");
		
		cat.info("FrontModule:ip="+front_ip);
		cat.info("FrontModule:port="+front_port);
		cat.info("FrontModule:workerThreadNum="+workerThreadNum);
		
		communicateWithFront.frontIP = front_ip;
		communicateWithFront.frontPort = Integer.parseInt(front_port);
		communicateWithFront.ping = "FE00"+account+"0000";
		
		pool = new ThreadPool(Integer.parseInt(workerThreadNum),FrontWorker.class,this);
		
		runThread = new Thread(this); 
	}
	public void run(){
		//一、建立与前置机的连接
	    int iFlag = communicateWithFront.Initialize();
	    if(iFlag == 1){
	    	cat.info("FrontModule-->ConnectToFront Successfully");
	    }else if(iFlag == -1){
	    	cat.info("FrontModule-->ConnectToFront Error");
	    }
	    

	    //二、启动接收前置机数据线程
	    try{
		    readFromFront rff = new readFromFront(pool,this);
		    rff.start();  
		    cat.info("FrontModule-->readFromFront Initialized Successfully");
	    }catch(Exception e2){
	  		cat.info("FrontModule-->readFromFront Initialized Error:",e2);
	  		this.getContext().handException(e2);
	    }
	    
	    //三、发送心跳
	    long pingInterval = Long.valueOf(this.getParameter("pingInterval"));
	    pingFront ping = new pingFront(pingInterval);
	    ping.start();
	}
	
	public void start() throws Exception {
	  runThread.start();
	}

	public void stop() throws Exception {
	  
	}

	public void destory() {
	  runThread.destroy();
	  
	}
	
	
	
}