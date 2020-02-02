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
	//������־
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.cmsms.CmsmsServer.class);
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
	private String dxzxhm;//�������ĺ���
	
	private SerialBean serial = null;
	private DataQueue sendData = null; 
	
	private smsReadThread readThread;//��ȡ���ŵ��߳�
	private smsSendThread sendThread;//�����ŵ��߳�

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
	    
	    //�򿪴���
	    serial = new SerialBean(COM,btl,sjw,tzw,jyw);
	    int flag = serial.Initialize();
	    if(flag==-1){
	    	throw new Exception("�򿪶���ģ�鴮��ʧ��!");
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
	 	//������ȡ�����߳�
	 	this.readThread.start();
	 	//�������Ͷ����߳�
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
	 
	  
	 //���Ͷ���Ϣ
	 private synchronized void sendSMS(DataObject data) throws Exception{
	 	String sjz = data.sjz;
	 	String SIM = data.SIM;			//�ն�SIM����
	 	
 	 	//�ڱ���ǰ��Ӷ���ͷ
	 	sjz = "0080" + sjz;
	 	
	 	SIM = Util.addAfter(SIM,7,"F");
	 	dxzxhm = Util.addAfter(dxzxhm,7,"F");
	 	
	 	int len = sjz.length()/2+14;
	 	String sLEN = Util.decStrToHexStr(len,2);
	 	sLEN = Util.convertStr(sLEN);
	 	
	 	String SMS = "";//����ģ�鱨��
	 	SMS = "AABB43000000010210"+sLEN+dxzxhm+SIM+sjz;//���������֣�1002
	 	String cs = Util.getCS(SMS);
	 	SMS = SMS+cs+"DDEE";
	 	
	 	//1������
	 	this.serial.WritePort(Util.strstobyte(SMS));
	 	System.out.println("[CmsmsServer]sendDATA:"+SMS);
	 	//2�����ܷ���״̬
	 	DataInputStream dataIn = new DataInputStream(this.serial.getInputStream());
	 	//��ʱ10s
	 	int max = 10;
	 	while(max>0){
	 		//System.out.println("[CmsmsServer]max="+max);
	 		Thread.sleep(2000);//����2s
	 		//������ʽ
	 		if(dataIn.available()<=0){
	 			max--;
	 			continue;
	 		}else{
	 			byte[] bt = this.serial.readHex();
	 			String R_SMS = Util.bytetostrs(bt);//���յ��Ķ��ű���
	 			String R_order = R_SMS.substring(14,18);//����������:2002
	 			if(R_order.equals("0220")){
	 				//���ŷ��͵�״̬����
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
	 		//���ŷ��ͳ�ʱ
	 		data.sjz = "outTime";
	 		process(data);
	 	}
	 }
	 
	 //��ȡ����Ϣ
	 private synchronized void readSMS() throws Exception{
	 	
	 	String SMS = "";//����ģ�鱨��
	 	//����ǰһ������Ϣ
	 	SMS = "AABB43000000010110010000";//���������֣�1001
	 	String cs = Util.getCS(SMS);
	 	SMS = SMS+cs+"DDEE";
	 	
	 	//1������
	 	this.serial.WritePort(Util.strstobyte(SMS));
	 	
	 	//2�����Ž���
	 	DataInputStream dataIn = new DataInputStream(this.serial.getInputStream());
	 	//��ʱ10s
	 	int max = 5;
	 	while(max>0){
	 		Thread.sleep(2000);//����2s
	 		int aLen = dataIn.available();
	 		if(aLen<=0){
	 			max--;
	 			continue;
	 		}else{
	 			byte[] bt = this.serial.readHex();
	 			String R_SMS = Util.bytetostrs(bt);//���յ��Ķ��ű���
	 			String R_order = R_SMS.substring(14,18);//����������:2001
	 			
	 			if(R_order.equals("0120")){
	 				if(R_SMS.length()<=28){
	 					//��ǰ�޶���
	 					return;	 					
	 				}else{
	 					//�յ���ǰһ������
	 					DataObject data = new DataObject();
	 					data.sjz = R_SMS;
	 					process(data);
	 					
	 					//ɾ����ǰһ������
	 					deleteSMS();
	 				}
	 			}
	 			
	 		}
	 		
	 	}	 	
	 	
	 }
	 
	 
	 //ɾ������Ϣ
	 private void deleteSMS() throws Exception{
	 	
	 	String SMS = "";//����ģ�鱨��
	 	//ɾ��ǰһ������Ϣ
	 	SMS = "AABB4300000001011A010000";//���������֣�1A01
	 	String cs = Util.getCS(SMS);
	 	SMS = SMS+cs+"DDEE";
	 	
	 	//1������
	 	this.serial.WritePort(Util.strstobyte(SMS));
	 	
	 	//2�����Ž���
	 	DataInputStream dataIn = new DataInputStream(this.serial.getInputStream());
	 	//��ʱ10s
	 	int max = 5;
	 	while(max>0){
	 		Thread.sleep(1000);//����1000ms
	 		if(dataIn.available()<=0){
	 			max--;
	 			continue;
	 		}else{
	 			byte[] bt = this.serial.readHex();
	 			String R_SMS = Util.bytetostrs(bt);//���յ��Ķ��ű���
	 			String R_order = R_SMS.substring(14,18);//����������:2A01
	 			
	 			if(R_order.equals("012A")){
	 				//��ɾ��
	 				break;
	 			}
	 			
	 		}
	 		
	 	}	 	
	 	
	 }
	 
	 //�Ӷ���ģ����ȡ���ŵ��߳�
	 class smsReadThread extends Thread{
	 	smsReadThread(){
	 		
	 	}
	 	
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(5000);
	 				//��ȡ����
	 				readSMS();
	 			}catch(Exception e){
	 				e.printStackTrace();
	 			}
	 			
	 		}
	 	}
	 }
	 
	 //���Ͷ��ŵ��߳�
	 class smsSendThread extends Thread{
	 	smsSendThread(){
	 		
	 	}
	 	
	 	public void run(){
	 		while(true){
	 			try{
	 				sleep(10);
	 				//�Ӷ���DataQueue��ȡ���ŷ���
	 				
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