package com.powerhigh.gdfas.module.ascendsms;

import org.apache.log4j.Category;
import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.module.AscendBean;
import com.powerhigh.gdfas.module.ThreadPool;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.util.DataQueue;

public class AscendsmsServer {
	//������־
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.ascendsms.AscendsmsModule.class);
	
	private AbstractModule module;	
	private ThreadPool pool;
	private int threadNum;
	private int COM;//���ں�
	private int btl;//������
	private int sjw;//����λ
	private int tzw;//ֹͣλ
	private int jyw;//У��λ
	private String dxzxhm;//�������ĺ���
	
	private AscendBean ascend = null;
	private DataQueue sendData = null; 
	
	private smsReadThread readThread;//��ȡ���ŵ��߳�
	private smsSendThread sendThread;//�����ŵ��߳�

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
		//�������ն��ŵ��س�
	    pool = new ThreadPool(threadNum, c, instance);
	    
	    //�򿪶���ģ��
	    ascend = new AscendBean(COM,btl,sjw,tzw,jyw);
	    int flag = ascend.Initialize();
	    if(0==flag){
	    	System.out.println("�򿪰����¶���ģ�鴮�ڳɹ�!--COM"+COM);
	    }else if(-5==flag){
	    	throw new Exception("ģ��ע��ʧ��!--COM"+COM);
	    }else if(-6==flag){
	    	throw new Exception("��COM��ʧ��!--COM"+COM);
	    }else if(-9==flag){
	    	throw new Exception("��ⲻ��GSMģ��!--COM"+COM);
	    }else if(-10==flag){
	    	throw new Exception("����ģ�����ʧ��!--COM"+COM);
	    }else if(-11==flag){
	    	throw new Exception("����ģ�����ʧ��!--COM"+COM);
	    }else{
	    	throw new Exception("�򿪶���ģ�鴮��ʧ��!--COM"+COM);
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
	 	//������ȡ�����߳�
//	 	this.readThread.start();
	 	//�������Ͷ����߳�
	 	this.sendThread.start();
	 }
	
	 //���������ݷ��뷢�Ͷ���
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
	 
	  
	 //���Ͷ���Ϣ
	 private synchronized void sendSMS(DataObject data) throws Exception{
	 	 Integer port=data.port;
	 	 String content=data.content;
		 String SIM = data.SIM;	
		 Boolean isChinese=data.isChinses;
		 
	 	 //�ն�SIM����
	 	Integer bac=SmsLibrary.INSTANCE.SendMsg(port, content, SIM, 0, isChinese);
	 	System.out.println("���ζ��ŷ��ͷ��أ�"+bac);
	 }
	 
	 //��ȡ����Ϣ
	 private synchronized void readSMS() throws Exception{
	 	
	 }
	 
	 
	 //ɾ������Ϣ
	 private void deleteSMS() throws Exception{
	 	
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
