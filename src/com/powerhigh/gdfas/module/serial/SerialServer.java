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
	//������־
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.serial.SerialServer.class);
//	static {
//	    PropertyConfigurator.configure(resource);
//	}
	
	private AbstractModule module;	
	private ThreadPool pool;
	private int threadNum;
	private int COM;//���ں�
	private int btl;//������
	private int sjw;//����λ
	private int tzw;//ֹͣλ
	private int jyw;//У��λ
	
	private SerialBean serial = null;
	private DataQueue sendData = null; 
	
	private smsReadThread readThread;//��ȡ���ŵ��߳�
	private smsSendThread sendThread;//�����ŵ��߳�
	private dataProcessThread processThread;//��ȡ�������ĵ��߳�
	
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
	    
	    //�򿪴���
	    serial = new SerialBean(COM,btl,sjw,tzw,jyw);
	    int flag = serial.Initialize();
	    if(flag==-1){
	    	throw new Exception("�򿪶���ģ�鴮��ʧ��!");
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
	 	//������ȡ�����߳�
	 	this.readThread.start();
	 	//�������Ͷ����߳�
	 	this.sendThread.start();
	 	//�����������Ľ�ȡ�߳�
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
	 
	  	 
	 
	 //�Ӵ����ж�ȡ���ݵ��߳�
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
	 
	 //�����ڷ������ݵ��߳�
	 class smsSendThread extends Thread{
	 	smsSendThread(){
	 		
	 	}
	 	
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(10);
	 				//�Ӷ���DataQueue��ȡ���ŷ���
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
	 
	 //��ByteBuffer��ȡ�������Ľ���
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
						
						
						int idx_68_1 = -1;//��һ��68���±�
						int idx_68_2 = -1;//��һ��68���±�
						int idx_16 = -1;//������16���±�
						//int idx_len = -1;//���ȵ��±�
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
									continue;//����������
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
										
									//����֡����		
					 		 		DataObject data = new DataObject();
					 		 		data.sjz = sjz;
					 		 		data.moduleID = CMConfig.SERIAL_MODULE_ID;
					 		 		
					 		 		process(data);
								}
							}
						}
						
						//��ʣ��ı��ķŻ�rBuffer
						rBuffer = buffer_left;
						
						
	 				}
	 				
	 			}catch(Exception e){
	 				e.printStackTrace();
	 			}
	 			
	 		}
	 	}
	 }
}