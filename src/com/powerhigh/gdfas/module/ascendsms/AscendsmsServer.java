package com.powerhigh.gdfas.module.ascendsms;

import org.apache.log4j.Category;
import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.module.AscendBean;
import com.powerhigh.gdfas.module.ThreadPool;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.util.DataQueue;

public class AscendsmsServer {
	//加载日志
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.ascendsms.AscendsmsModule.class);
	
	private AbstractModule module;	
	private ThreadPool pool;
	private int threadNum;
	private int COM;//串口号
	private int btl;//波特率
	private int sjw;//数据位
	private int tzw;//停止位
	private int jyw;//校验位
	private String dxzxhm;//短信中心号码
	
	private AscendBean ascend = null;
	private DataQueue sendData = null; 
	
	private smsReadThread readThread;//读取短信的线程
	private smsSendThread sendThread;//发短信的线程

	public AscendsmsServer(int threadNum,int com,int btl,int sjw,int tzw,int jyw,String dxzxhm){
	    this.threadNum = threadNum;
	    this.COM = com;
	    this.btl = btl;
	    this.sjw = sjw;
	    this.tzw = tzw;
	    this.jyw = jyw;
	    this.dxzxhm = dxzxhm;
	}
	
	
	
	public void init(Class c, Object instance)
	     throws Exception{
		module = (AbstractModule)instance; 
		//启动接收短信的县城
	    pool = new ThreadPool(threadNum, c, instance);
	    
	    //打开短信模块
	    ascend = new AscendBean(COM,btl,sjw,tzw,jyw);
	    int flag = ascend.Initialize();
	    if(0==flag){
	    	System.out.println("打开爱赛德短信模块串口成功!--COM"+COM);
	    }else if(-5==flag){
	    	throw new Exception("模块注册失败!--COM"+COM);
	    }else if(-6==flag){
	    	throw new Exception("打开COM口失败!--COM"+COM);
	    }else if(-9==flag){
	    	throw new Exception("检测不到GSM模块!--COM"+COM);
	    }else if(-10==flag){
	    	throw new Exception("设置模块参数失败!--COM"+COM);
	    }else if(-11==flag){
	    	throw new Exception("保存模块参数失败!--COM"+COM);
	    }else{
	    	throw new Exception("打开短信模块串口失败!--COM"+COM);
	    }
	    this.sendData = new DataQueue();
	    this.sendThread = new smsSendThread();
	 }
	
	 
	 private void process(DataObject data){	 	
	 	try{
	 		pool.performWork(data);
	 	}catch(Exception e){
	 		this.module.getContext().handException(e);
	 	}     
	 }
	
	 public void run(){
	 	//启动读取短信线程
//	 	this.readThread.start();
	 	//启动发送短信线程
	 	this.sendThread.start();
	 }
	
	 //将短信内容放入发送队列
	 public void send(DataObject data)
	     throws Exception{
	 	try{ 		
	 		sendData.put(data);
	 	}catch(Exception e){	   
		    throw e;
	 	}
	    
	 }
	
	 public void stop() throws Exception{
	 	this.readThread.wait();
	 }
	 
	 public void close() throws Exception{
	 	this.ascend.Close();
	 	this.readThread.interrupt();
	 }
	 
	  
	 //发送短消息
	 private synchronized void sendSMS(DataObject data) throws Exception{
	 	 Integer port=data.port;
	 	 String content=data.content;
		 String SIM = data.SIM;	
		 Boolean isChinese=data.isChinses;
		 
	 	 //终端SIM卡号
	 	Integer bac=SmsLibrary.INSTANCE.SendMsg(port, content, SIM, 0, isChinese);
	 	System.out.println("本次短信发送返回："+bac);
	 }
	 
	 //读取短消息
	 private synchronized void readSMS() throws Exception{
	 	
	 }
	 
	 
	 //删除短消息
	 private void deleteSMS() throws Exception{
	 	
	 }
	 
	 //从短信模块中取短信的线程
	 class smsReadThread extends Thread{
	 	smsReadThread(){
	 		
	 	}
	 	
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(5000);
	 				//读取短信
	 				readSMS();
	 			}catch(Exception e){
	 				e.printStackTrace();
	 			}
	 			
	 		}
	 	}
	 }
	 
	 //发送短信的线程
	 class smsSendThread extends Thread{
	 	smsSendThread(){
	 		
	 	}
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(10);
	 				//从队列DataQueue中取短信发送
	 				if(sendData.size()>0){
	 					sendSMS((DataObject)sendData.get());
	 				}
	 			}catch(Exception e){
	 				e.printStackTrace();
	 			}
	 			
	 		}
	 	}
	 }
}
