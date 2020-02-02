package com.powerhigh.gdfas.module.serial;


import java.io.DataInputStream;
import java.nio.ByteBuffer;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.module.SerialBean;
import com.powerhigh.gdfas.module.ThreadPool;
import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.util.Util;
import com.powerhigh.gdfas.util.DataQueue;

public class SerialServer
{
	//加载日志
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.serial.SerialServer.class);
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
	
	private SerialBean serial = null;
	private DataQueue sendData = null; 
	
	private smsReadThread readThread;//读取短信的线程
	private smsSendThread sendThread;//发短信的线程
	private dataProcessThread processThread;//截取完整报文的线程
	
	private ByteBuffer rBuffer = null;

	public SerialServer(int threadNum,int com,int btl,int sjw,int tzw,int jyw){
	    this.threadNum = threadNum;
	    this.COM = com;
	    this.btl = btl;
	    this.sjw = sjw;
	    this.tzw = tzw;
	    this.jyw = jyw;
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
	    
	    rBuffer = ByteBuffer.allocate(1024);
	    

	    this.sendData = new DataQueue();
	    this.readThread = new smsReadThread();
	    this.sendThread = new smsSendThread();
	    this.processThread = new dataProcessThread();
	    
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
	 	//启动完整报文截取线程
	 	this.processThread.start();
	 }
	
	 public void send(DataObject data)
	     throws Exception{
	 	try{ 	
			System.out.println("[SerialServer]sendData.put(data)");
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
	 
	  	 
	 
	 //从串口中读取数据的线程
	 class smsReadThread extends Thread{
	 	DataInputStream dataIn = null;
	 	smsReadThread(){
	 		//dataIn = new DataInputStream(serial.getInputStream());
	 	}
	 	
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(10);
	 				
	 		 		byte[] bt = serial.readHex();
	 		 		
	 		 		synchronized(rBuffer){
		 		 		for(int i=0;i<bt.length;i++){
							rBuffer.put(bt[i]);
						}
	 		 		}
	 		 		
	 		 		cat.info("[SerialServer]ReadDATA:"+Util.bytetostrs(bt));

	 			}catch(Exception e){
	 				e.printStackTrace();
	 			}
	 			
	 		}
	 	}
	 }
	 
	 //往串口发送数据的线程
	 class smsSendThread extends Thread{
	 	smsSendThread(){
	 		
	 	}
	 	
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(10);
	 				//从队列DataQueue中取短信发送
	 				if(sendData.size()>0){
		 				DataObject data = (DataObject)sendData.get();
		 				String sjz = data.sjz;
		 				System.out.println("[SerialServer]sendDATA:"+sjz);
		 				cat.info(("[SerialServer]sendDATA:"+sjz));
		 				serial.WritePort(Util.strstobyte(sjz));
		 				System.out.println("[SerialServer]sendDATA Successfully");
		 				cat.info(("[SerialServer]sendDATA Successfully"));
		 			}
	 				
	 			}catch(Exception e){
	 				cat.error("[SerialServer]sendDATA ERROR!",e);
	 			}
	 			
	 		}
	 	}
	 }
	 
	 //从ByteBuffer中取完整报文解析
	 class dataProcessThread extends Thread{
	 	dataProcessThread(){
	 		
	 	}
	 	
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(10);
	 				
	 				synchronized(rBuffer){
	 					
	 					int bt_length = rBuffer.position();
	 					
	 					if(bt_length == 0){
							continue;
						}
						
						byte[] bt = new byte[bt_length];
						
						rBuffer.rewind();
						rBuffer.get(bt,0,bt_length);
						
						ByteBuffer buffer_left = ByteBuffer.allocate(1024);
						
						
						int idx_68_1 = -1;//第一个68的下标
						int idx_68_2 = -1;//第一个68的下标
						int idx_16 = -1;//结束浮16的下标
						//int idx_len = -1;//长度的下标
						for(int i=0;i<bt.length;i++){
							if(bt[i] == 0x68){
								idx_68_1 = i;
								break;
							}								
						}
						
						if(idx_68_1 != -1){
							idx_68_2 = idx_68_1 + 5;
							if(bt[idx_68_2] != 0x68){
								for(int i=idx_68_1+1;i<bt.length;i++){
									buffer_left.put(bt[i]);
								}
							}else{
								byte[] tempb_len1 = {bt[idx_68_1+2],bt[idx_68_1+1]};
				            	String s_len1 = Util.bytetostrs(tempb_len1);
				            	int data_len = Integer.parseInt(s_len1, 16);
					            data_len = (data_len - 1) / 4;
					            
					            idx_16 = idx_68_2 + data_len + 2;

								if(idx_16>bt.length-1){
									continue;//非完整报文
								}
								if(bt[idx_16] != 0x16){
									for(int i=idx_68_2+1;i<bt.length;i++){
										buffer_left.put(bt[i]);
									}
								}else{
																			
									int sjz_len = data_len + 8;
						            byte[] bt_sjz = new byte[data_len+8];
						            System.arraycopy(bt,idx_68_1,bt_sjz,0,data_len+8);
						            String sjz = Util.bytetostrs(bt_sjz);
						            
						            if(bt.length > idx_16 +1){
										for(int i=idx_16+1;i<bt.length;i++){
											buffer_left.put(bt[i]);
										}
						            }
										
									//数据帧处理		
					 		 		DataObject data = new DataObject();
					 		 		data.sjz = sjz;
					 		 		data.moduleID = CMConfig.SERIAL_MODULE_ID;
					 		 		
					 		 		process(data);
								}
							}
						}
						
						//将剩余的报文放回rBuffer
						rBuffer = buffer_left;
						
						
	 				}
	 				
	 			}catch(Exception e){
	 				e.printStackTrace();
	 			}
	 			
	 		}
	 	}
	 }
}