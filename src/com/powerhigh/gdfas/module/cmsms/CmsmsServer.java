package com.powerhigh.gdfas.module.cmsms;


import java.io.DataInputStream;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.module.SerialBean;
import com.powerhigh.gdfas.module.ThreadPool;
import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.util.Util;
import com.powerhigh.gdfas.util.DataQueue;

public class CmsmsServer
{
	//加载日志
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.cmsms.CmsmsServer.class);
//	static {
//	    PropertyConfigurator.configure(resource);
//	}
	
	private AbstractModule module;	
	private ThreadPool pool;
	private int threadNum;
	private int COM;//串口号
	private int btl;//波特率
	private int sjw;//数据位
	private int tzw;//停止位
	private int jyw;//校验位
	private String dxzxhm;//短信中心号码
	
	private SerialBean serial = null;
	private DataQueue sendData = null; 
	
	private smsReadThread readThread;//读取短信的线程
	private smsSendThread sendThread;//发短信的线程

	public CmsmsServer(int threadNum,int com,int btl,int sjw,int tzw,int jyw,String dxzxhm){
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
	    pool = new ThreadPool(threadNum, c, instance);
	    
	    //打开串口
	    serial = new SerialBean(COM,btl,sjw,tzw,jyw);
	    int flag = serial.Initialize();
	    if(flag==-1){
	    	throw new Exception("打开短信模块串口失败!");
	    }
	     
	    this.readThread = new smsReadThread();
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
	 	this.readThread.start();
	 	//启动发送短信线程
	 	this.sendThread.start();
	 }
	
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
	 	this.serial.Close();
	 	this.readThread.destroy();
	 }
	 
	  
	 //发送短消息
	 private synchronized void sendSMS(DataObject data) throws Exception{
	 	String sjz = data.sjz;
	 	String SIM = data.SIM;			//终端SIM卡号
	 	
 	 	//在报文前面加短信头
	 	sjz = "0080" + sjz;
	 	
	 	SIM = Util.addAfter(SIM,7,"F");
	 	dxzxhm = Util.addAfter(dxzxhm,7,"F");
	 	
	 	int len = sjz.length()/2+14;
	 	String sLEN = Util.decStrToHexStr(len,2);
	 	sLEN = Util.convertStr(sLEN);
	 	
	 	String SMS = "";//短信模块报文
	 	SMS = "AABB43000000010210"+sLEN+dxzxhm+SIM+sjz;//发送命令字：1002
	 	String cs = Util.getCS(SMS);
	 	SMS = SMS+cs+"DDEE";
	 	
	 	//1、发送
	 	this.serial.WritePort(Util.strstobyte(SMS));
	 	System.out.println("[CmsmsServer]sendDATA:"+SMS);
	 	//2、接受发送状态
	 	DataInputStream dataIn = new DataInputStream(this.serial.getInputStream());
	 	//超时10s
	 	int max = 10;
	 	while(max>0){
	 		//System.out.println("[CmsmsServer]max="+max);
	 		Thread.sleep(2000);//休眠2s
	 		//非阻塞式
	 		if(dataIn.available()<=0){
	 			max--;
	 			continue;
	 		}else{
	 			byte[] bt = this.serial.readHex();
	 			String R_SMS = Util.bytetostrs(bt);//接收到的短信报文
	 			String R_order = R_SMS.substring(14,18);//返回命令字:2002
	 			if(R_order.equals("0220")){
	 				//短信发送的状态返回
	 				data.sjz = R_SMS;
	 				process(data);
	 				break;
	 			}else{
	 				max--;
	 				continue;
	 			}
	 		}
	 		
	 	}
	 	
	 	if(max==0){
	 		//短信发送超时
	 		data.sjz = "outTime";
	 		process(data);
	 	}
	 }
	 
	 //读取短消息
	 private synchronized void readSMS() throws Exception{
	 	
	 	String SMS = "";//短信模块报文
	 	//读当前一条短消息
	 	SMS = "AABB43000000010110010000";//发送命令字：1001
	 	String cs = Util.getCS(SMS);
	 	SMS = SMS+cs+"DDEE";
	 	
	 	//1、发送
	 	this.serial.WritePort(Util.strstobyte(SMS));
	 	
	 	//2、短信接收
	 	DataInputStream dataIn = new DataInputStream(this.serial.getInputStream());
	 	//超时10s
	 	int max = 5;
	 	while(max>0){
	 		Thread.sleep(2000);//休眠2s
	 		int aLen = dataIn.available();
	 		if(aLen<=0){
	 			max--;
	 			continue;
	 		}else{
	 			byte[] bt = this.serial.readHex();
	 			String R_SMS = Util.bytetostrs(bt);//接收到的短信报文
	 			String R_order = R_SMS.substring(14,18);//返回命令字:2001
	 			
	 			if(R_order.equals("0120")){
	 				if(R_SMS.length()<=28){
	 					//当前无短信
	 					return;	 					
	 				}else{
	 					//收到当前一条短信
	 					DataObject data = new DataObject();
	 					data.sjz = R_SMS;
	 					process(data);
	 					
	 					//删除当前一条短信
	 					deleteSMS();
	 				}
	 			}
	 			
	 		}
	 		
	 	}	 	
	 	
	 }
	 
	 
	 //删除短消息
	 private void deleteSMS() throws Exception{
	 	
	 	String SMS = "";//短信模块报文
	 	//删除前一条短消息
	 	SMS = "AABB4300000001011A010000";//发送命令字：1A01
	 	String cs = Util.getCS(SMS);
	 	SMS = SMS+cs+"DDEE";
	 	
	 	//1、发送
	 	this.serial.WritePort(Util.strstobyte(SMS));
	 	
	 	//2、短信接收
	 	DataInputStream dataIn = new DataInputStream(this.serial.getInputStream());
	 	//超时10s
	 	int max = 5;
	 	while(max>0){
	 		Thread.sleep(1000);//休眠1000ms
	 		if(dataIn.available()<=0){
	 			max--;
	 			continue;
	 		}else{
	 			byte[] bt = this.serial.readHex();
	 			String R_SMS = Util.bytetostrs(bt);//接收到的短信报文
	 			String R_order = R_SMS.substring(14,18);//返回命令字:2A01
	 			
	 			if(R_order.equals("012A")){
	 				//已删除
	 				break;
	 			}
	 			
	 		}
	 		
	 	}	 	
	 	
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