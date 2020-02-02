package com.powerhigh.gdfas.module.gprs;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.module.ThreadPool;
import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.module.ModuleContainer;
import com.powerhigh.gdfas.util.CMQueue;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.util.DataQueue;
import com.powerhigh.gdfas.util.Util;
import com.powerhigh.gdfas.util.CMXmlR;
import com.powerhigh.gdfas.util.CMConfig;

public class GprsServer
{
	//������־
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.gprs.GprsServer.class);
//	static {
//	    PropertyConfigurator.configure(resource);
//	}
	
	private AbstractModule module;	
	private ThreadPool pool;
	private int threadNum;
	private int port;
	
	private Selector readSelector = null;
	private ConnectQueue queue = null;
	private HashMap zdChannel = null;//key:[xzqxm+zddz]
								 	 //value:[SocketChannel]
	private HashMap zdBuffer = null; //key:[SocketChannel]
									 //value:[ByteBuffer]
	private DataQueue zdData = null;
	
	private AcceptThread acceptThread = null;
	private ProcessThread processThread = null;
	private dpThread dpThread = null;
	
	public static String isDebug = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_DEBUG_KEY);//�Ƿ���ʾ������Ϣ(true����ʾ������������ʾ)
	public static String read_buffer = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_READBUFFER_KEY);//��socke���ݵ�buffer��С
	public static String queue_buffer = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_QUEUEBUFFER_KEY);//ƴ��ʱΪÿ���ն˿�����buffer��С

	public GprsServer(int threadNum, int serverPort)
 {
     this.threadNum = threadNum;
     this.port = serverPort;
 }



 	public void init(Class c, Object instance)
     throws Exception
 {
     module = (AbstractModule)instance;     
     pool = new ThreadPool(threadNum, c, instance);
     
     this.readSelector = Selector.open();
		
	 this.queue = new ConnectQueue();
		
	 this.acceptThread = new AcceptThread();
	 this.processThread = new ProcessThread();	
	 this.dpThread = new dpThread();
		
	 this.zdChannel = new HashMap();
	 this.zdBuffer = new HashMap();
     
	 this.zdData = new DataQueue();
 }

 
 	private void process(DataObject data)
 {
 	try{
 		pool.performWork(data);
 	}catch(Exception e){
 		this.module.getContext().handException(e);
 	}     
 }

 	public void run()
 	{
 		this.acceptThread.start();
 		this.processThread.start();
 		this.dpThread.start();
 	}

 	public void send(DataObject data)
     	throws Exception
	 {
 		try{ 		
 			String xzqxm = data.xzqxm;
 			String zddz = data.zddz;
 			SocketChannel channel = (SocketChannel)zdChannel.get(xzqxm+zddz);
 		
 			ByteBuffer buffer = Util.strstobytebuf(data.sjz);
 			channel.write(buffer);
 		
 			System.out.println("[GprsServer]sendDATA:"+data.sjz);
 		}catch(Exception e){	   
 			throw e;
 		}
    
 	}
 
 	public void kickClient(){
 	synchronized(this.zdChannel){
	 	Set key = zdChannel.keySet();
	 	Object[] o_key = key.toArray();
	 	for(int i=0;i<o_key.length;i++){
	 		try{
	 			//�ͷ�Buffer
	 			zdBuffer.remove(o_key[i]);
	 			
	 			//�ر�socket
		 		SocketChannel client = (SocketChannel)zdChannel.get(o_key[i]);
		 		client.close();
		 		zdChannel.remove(o_key[i]);
	 		}catch(Exception e){
	 			System.out.println("[GprsServer]kickClient Error!");
	 			e.printStackTrace();
	 		}
	 	}
	 	
	 	zdChannel = new HashMap();
	 	zdBuffer = new HashMap();
	 	
	 	System.out.println("[GprsServer]kickClient Successfully");
 	}
 }

 	public void stop() throws Exception{
 		this.acceptThread.wait();
 		this.processThread.wait();
 	}
 
 	public void close(){
 		this.acceptThread.destroy();
 		this.processThread.destroy();
 	}
 

	//ConnectQueue
	class ConnectQueue extends CMQueue{
  	
		
		ConnectQueue(){
			
		}
  	 	 
		public void put(SocketChannel obj){
			super.put(obj);
			readSelector.wakeup();
		}
      
		public synchronized Object get(){
			return super.get();	
		}	
	}
	
	
	//AcceptThread
	class AcceptThread extends Thread{
		
	 private ServerSocketChannel ssc;
	 private Selector acceptSelector;  
	 
	 
	 AcceptThread() throws Exception{
 	  	   ssc=ServerSocketChannel.open();
           ssc.configureBlocking(false);
           InetSocketAddress address = new InetSocketAddress(port);
           ssc.socket().bind(address);
           acceptSelector=Selector.open();
           ssc.register(acceptSelector, SelectionKey.OP_ACCEPT);
          
          
 	  }
 	  
 	  public void run(){
 	  	  while(true) {
              try {
                  acceptSelector.select();
                  
                  acceptConnections();
                  
                  sleep(1);  //����
              } catch(Exception e) {
                 cat.error(e);
                 e.printStackTrace();
              }
           }
 	  	
 	  }
 	  
 	  public void acceptConnections() throws Exception{
 	  	  Set readyKeys = acceptSelector.selectedKeys();
          
 	  	
          for(Iterator i = readyKeys.iterator(); i.hasNext(); ) {
              SelectionKey key = (SelectionKey)i.next();
              i.remove();

              ServerSocketChannel readyChannel = (ServerSocketChannel)key.channel();
              
              SocketChannel incomingChannel =readyChannel.accept();
                             
              
              synchronized(queue){
                   queue.put(incomingChannel);
                   cat.info("[GprsServer.AcceptThread]Client Coming!");
                   System.out.println("[GprsServer.AcceptThread]Client Coming!");
              }
           }
 	   
 	  }
 }
	

	//ProcessThread
	class ProcessThread extends Thread{
 	  SelectionKey selKey;
      private ByteBuffer readBuffer=ByteBuffer.allocate(Integer.parseInt(read_buffer));
      
 	  public ProcessThread() throws Exception{
 	  	 
 	  }
 	  
 	  public void run(){
 	      while(true) {
              try {
                   sleep(2);                         
                   int keysReady = readSelector.select();
                   if(isDebug.equalsIgnoreCase("true")){
                   	System.out.println("[GprsServer.ProcessThread]keysReady="+keysReady);
                   }
                   registerNewChannels();
                   
                   //��readSelector.wakeup(),��keysReady=0
                   if(keysReady > 0) {
                   	  
                       acceptRequests();
                   }
              } catch(Exception e) {
                  cat.error(e);
                  e.printStackTrace();
              }
          }	
 	  }
 	  
 	  //ע��Ϊread�¼�
 	  protected void registerNewChannels() throws Exception {
         SocketChannel channel;
         if (queue.size()<=0) return;
         
         while(null != (channel = (SocketChannel)queue.get())) {
         	cat.info("[GprsServer.ProcessThread]channel.register!");
         	System.out.println("[GprsServer.ProcessThread]channel.register!");
            channel.configureBlocking(false);
            channel.register(readSelector, SelectionKey.OP_READ);
             
            
        }  
     }
     
 	  //��Selector��ȡ��׼���õ�����
 	  protected void acceptRequests() throws Exception {
     	 cat.info("[GprsServer.ProcessThread]||Read from Clients||");
     	 System.out.println("[GprsServer.ProcessThread]||Read from Clients||");
         Set readyKeys = readSelector.selectedKeys();
         if(isDebug.equalsIgnoreCase("true")){
         	cat.info("[GprsServer.ProcessThread]readyKeys.size():"+readyKeys.size());
         	System.out.println("[GprsServer.ProcessThread]readyKeys.size():"+readyKeys.size());
         }
        for(Iterator i = readyKeys.iterator(); i.hasNext(); ) {
            SelectionKey key = (SelectionKey)i.next();
            i.remove();
            processRequest(key);
            
            sleep(1);
       }
      }
   
 	  //���ղ�����Client������������
 	  protected void processRequest(SelectionKey key) throws Exception {
   	    String state = null;  
   	    DataObject data = null;
        SocketChannel incomingChannel = (SocketChannel)key.channel();
        
        Socket incomingSocket = incomingChannel.socket();
        int client_port = incomingSocket.getPort();
        InetAddress addr = incomingSocket.getInetAddress();
        String client_ip = addr.getHostAddress();
        cat.info("[GprsServer.ProcessThread]clientIP="+client_ip+":"+client_port);
        System.out.println("[GprsServer.ProcessThread]clientIP="+client_ip+":"+client_port);
        
        readBuffer.clear();
      
        try {   
            int bytesRead= incomingChannel.read(readBuffer);
            
            
            if (bytesRead==-1){  //�ͻ��������ر�����
            	cat.info("[GprsServer.ProcessThread]�ͻ��������ر�����!");
            	System.out.println("[GprsServer.ProcessThread]�ͻ��������ر�����!");
            	
            	//�ն˵��ߴ���          	
        		outLine(incomingChannel);
            	
                incomingChannel.close();                 	
                key.cancel();  
                return;
            }
            readBuffer.flip();  
            
            int len = readBuffer.remaining();
            if (len==0){
            	return;
            }
                       
            byte[] bt = new byte[len];
            readBuffer.get(bt, 0, len);
				

            cat.info("[GprsServer]ReadDATA:"+Util.bytetostrs(bt));

            //<----���ݱ��Ĳ��---->
            
            //1���������յ������ݷ����SocketChannel��Buffer��
			ByteBuffer buffer = (ByteBuffer)zdBuffer.get(incomingChannel);
			if(buffer == null){
				buffer = ByteBuffer.allocate(Integer.parseInt(queue_buffer));
			}
			try{
				buffer.put(bt);
				
				zdBuffer.put(incomingChannel,buffer);	
			}catch(Exception e){
				//����queue_buffer��С,��ʼ����Ӧ��BUFFER
				zdBuffer.put(incomingChannel,ByteBuffer.allocate(Integer.parseInt(queue_buffer)));
	            cat.error("[GprsServer.ProceesThread]ByteBuffer put ERROR:",e);		            	
	            return;
			}
			
			//2��ȡ��������
			try{
					int bt_length = buffer.position();
					
					if(bt_length == 0){
						return;
					}
				
					byte[] bt_new = new byte[bt_length];
				
					buffer.rewind();
					buffer.get(bt_new,0,bt_length);
					
					ByteBuffer buffer_left = ByteBuffer.allocate(Integer.parseInt(queue_buffer));

					int idx_begin = 0;
					boolean flag = false;
					while(true){
						if(flag == true){
							break;
						}
						while(true){
							int idx_68_1 = -1;//��һ��68���±�
							int idx_68_2 = -1;//��һ��68���±�
							int idx_16 = -1;//������16���±�
							//int idx_len = -1;//���ȵ��±�
							for(int i=idx_begin;i<bt_new.length;i++){
								if(bt_new[i] == 0x68){
									idx_68_1 = i;
									break;
								}								
							}
							if(idx_68_1 == -1){
								zdBuffer.put(incomingChannel,buffer_left);
								//��������
								buffer_left = null;
								flag = true;
								break;
							}
	
							idx_68_2 = idx_68_1 + 5;
							if(idx_68_2>bt_new.length-1){
								//����������
									
								//���ӵ�һ��68��ʼ�ı��ķŻ�zdBuffer
								for(int i=idx_68_1;i<bt_new.length;i++){
									buffer_left.put(bt_new[i]);
								}
								zdBuffer.put(incomingChannel,buffer_left);
								//��������
								buffer_left = null;
								flag = true;
								break;
							}
							
							if(bt_new[idx_68_2] != 0x68){
								//����������
								//���ӵ�һ��68��ʼ�ı��ķŻ�zdBuffer
//								for(int i=idx_68_1+1;i<bt_new.length;i++){
//									buffer_left.put(bt_new[i]);
//								}
//								zdBuffer.put(incomingChannel,buffer_left);
//								//��������
//								buffer_left = null;

								idx_begin = idx_68_1+1;
								break;
								
							}
							
							byte[] tempb_len1 = {bt_new[idx_68_1+2],bt_new[idx_68_1+1]};
					        String s_len1 = Util.bytetostrs(tempb_len1);
					        int data_len = Integer.parseInt(s_len1, 16);
						    data_len = (data_len - 1) / 4;
						            
						    idx_16 = idx_68_2 + data_len + 2;
		
						    if(idx_16>bt_new.length-1){
								//����������
						    	//���ӵ�һ��68��ʼ�ı��ķŻ�zdBuffer
								for(int i=idx_68_1;i<bt_new.length;i++){
									buffer_left.put(bt_new[i]);
								}
								zdBuffer.put(incomingChannel,buffer_left);
								//��������
								buffer_left = null;
								flag = true;
								break;
							}
						    
							if(bt_new[idx_16] != 0x16){
								//����������
								//���ӵڶ���68��ʼ�ı��ķŻ�zdBuffer
//								for(int i=idx_68_2+1;i<bt_new.length;i++){
//									buffer_left.put(bt_new[i]);
//								}
//								zdBuffer.put(incomingChannel,buffer_left);
//								//��������
//								buffer_left = null;
								
								idx_begin = idx_68_1+1;
								break;
								
							}
							
							//����������֡
							int sjz_len = data_len + 8;
							byte[] bt_sjz = new byte[data_len+8];
							System.arraycopy(bt_new,idx_68_1,bt_sjz,0,data_len+8);
							    
							//���Ĵ���
							termDP(bt_sjz,incomingChannel);
							
					        //��������
					        bt_sjz = null;
							
	                        //�±����
							idx_begin = idx_16 + 1;
									
								
							
						}
					}
					
					//��������
					bt_new = null;
				
				
			}catch(Exception e){
				cat.error("[GprsServer.ProceesThread]���ݲ�ֳ���:",e);
				return;
			}
                      
            
          }catch(IOException ioe){
            //�ն˵��ߴ���          	
    		outLine(incomingChannel);
    		
            incomingChannel.close();
            key.cancel();
                            
          }catch(Exception e) {   
            throw e;
          }
      } 

 	  private void termDP(byte[] bt_sjz,SocketChannel incomingChannel) throws Exception{

        //1��ˢ��"�ն�-����"�б�
        //����������
        String s_xzqxm = Util.bytetostrs(new byte[]{bt_sjz[8],bt_sjz[7]});
        //�ն˵�ַ
        String s_zddz = Util.bytetostrs(new byte[]{bt_sjz[10],bt_sjz[9]});
        String zdljdz = s_xzqxm + s_zddz;
        
        Object obj_old = zdChannel.get(zdljdz);
	    if(obj_old==null || !obj_old.equals(incomingChannel)){
	        try{
	        	if(obj_old != null){
	        		//�ͷ�zdChannel
	        		zdChannel.remove(obj_old);
	     	  		    
	        		//�ر�ԭ����socket
	        		SocketChannel oldChannel = (SocketChannel)obj_old;
	        		SelectionKey oldKey = null;
	        		Set set = readSelector.keys();
	        		Object[] obj = set.toArray();
	        		for(int i=0;i<obj.length;i++){
	        			SelectionKey tempKey = (SelectionKey)obj[i];
	        			if(tempKey.channel().equals(oldChannel)){
	        				oldKey = tempKey;
	        				break;
	        			}
	        		}
	        		oldChannel.close();
	        		cat.info("[GprsServer.ProceesThread]oldChannel.close();");
	        		if(oldKey != null){
	        			oldKey.cancel();
	        		}
	        	}
	        }catch(Exception e){
	        	cat.error("[GprsServer.ProceesThread]Close Old_SocketChannel error:",e);
	        }
	        					        	
	        zdChannel.put(zdljdz,incomingChannel);
	    }
	     
        
        //2�������ķ������
        DataObject data = new DataObject();
        data.sjz = Util.bytetostrs(bt_sjz);
        data.xzqxm = s_xzqxm;
        data.zddz = s_zddz;
        
		zdData.put(data);
 	  }
 	  
 	  protected void outLine(SocketChannel incomingChannel){
 	  	//�ն˵���          	
		String dx_xzqxm = "";
		String dx_zddz = "";
		//�ͷ�buffer
		synchronized(zdBuffer){
			zdBuffer.remove(incomingChannel);
		}
		
		//�ر�socket
		synchronized(zdChannel){
        	Object[] obj = zdChannel.keySet().toArray();
        	for(int i=0;i<obj.length;i++){
        		SocketChannel tempChannel = (SocketChannel)zdChannel.get(obj[i]);
        		if(tempChannel.equals(incomingChannel)){
        			String zd = String.valueOf(obj[i]);
        			dx_xzqxm = zd.substring(0,4);
        			dx_zddz = zd.substring(4,8);
        			
        			zdChannel.remove(obj[i]);
        			
        			break;
        		}
        	}
        }
		String state = "state,"+dx_xzqxm+","+dx_zddz+",outline";//����
		//������״̬���͸���վ....
		DataObject data = new DataObject("up",state);
        ModuleContainer container = (ModuleContainer)module.getContext();
        data.setModuleID(container.moduleID);//����ģ��ID
        
        process(data);
        cat.info("[GprsServer]�ն˵���(xzqxm="+dx_xzqxm+",zddz="+dx_zddz+")");
		
 	  }
 }
	
	
	//���ݶ��д����߳�
	class dpThread extends Thread {
		public void run(){
			while(true){
				try{
					sleep(5);
					if(zdData.size() <= 0){
						continue;
					}
					DataObject data = (DataObject)zdData.get();
					
					//һ��ȡ����֡������Ϣ
					String sSJZ = data.sjz;					
				    cat.info("[GprsServer.dpThread]s_sjz:"+sSJZ);
				
				    String s_csdata = sSJZ.substring(12, sSJZ.length() - 4);
				    cat.info("[GprsServer.dpThread]s_csdata:" + s_csdata);
				        				            
				     //����������
				     String s_xzqxm = s_csdata.substring(2, 6);
				     s_xzqxm = Util.convertStr(s_xzqxm);
				     cat.info("[GprsServer.dpThread]s_xzqxm:" + s_xzqxm);
				
				     //�ն˵�ַ
				     String s_zddz = s_csdata.substring(6, 10);
				     s_zddz = Util.convertStr(s_zddz);
				     cat.info("[GprsServer.dpThread]s_zddz:" + s_zddz);
				            
				
				     //Ӧ�ù�����AFN
				     String s_afn = s_csdata.substring(12, 14);
				     cat.info("[GprsServer.dpThread]s_afn:" + s_afn);
				            
				     String s_dadt = s_csdata.substring(16, 24);
				     cat.info("[GprsServer.dpThread]s_dadt:" + s_dadt);
				            
				     //��Ϣ��Pn
				     String s_da = s_dadt.substring(0,4);
				     s_da = Util.tranDA(Util.convertStr(s_da));
				     String s_Pda = "P" + s_da;
				     //��Ϣ��Fn
				     String s_dt = s_dadt.substring(4,8);
				     s_dt = Util.tranDT(Util.convertStr(s_dt));
				     String s_Fdt = "F" + s_dt;
				     //PnFn
				     String s_PF = s_Pda + s_Fdt;
				         
				     cat.info("[GprsServer.dpThread]s_PF:" + s_PF);
				           
				       				            
				     //�����߼��ж�
				     String returnSJZ = "";
				     String returnCsData = "";
				     String returnCs = "";
				     String state = "";       
				     //�ն˶�Ӧ��SocketChannel
				     SocketChannel channel = (SocketChannel)zdChannel.get(s_xzqxm+s_zddz);
				     if(s_afn.equals("02")){
				     	
				        //-----------��·���-------------
				        //1����Ӧ�ն�
				      	returnSJZ = "683100310068";
				       	returnCsData = "0B";//C
				        returnCsData += s_csdata.substring(2,12);//��ַ��
				        returnCsData += "00";//AFN=00,ȷ��/����
				        returnCsData += "6" + s_csdata.substring(15, 16);//SEQ
				        returnCsData += "00000100";
				        		
				        returnCs = Util.getCS(returnCsData);
				        		
				        returnSJZ += returnCsData + returnCs + "16";
				        		
				        channel.write(Util.strstobytebuf(returnSJZ));
				        		
				        		
				        //2���������� 
				        if(s_PF.equals("P0F1")){
				            //-------------��¼--------------
				            cat.info("[GprsServer]��¼����");
				            //683100310068C911111111000270000001008016
				            state = "state,"+s_xzqxm+","+s_zddz+",online";//״̬
				            //��״̬���͸���վ....
				            data = new DataObject("up",state);
				        	ModuleContainer container = (ModuleContainer)module.getContext();
				        	data.setModuleID(container.moduleID);//����ģ��ID
				        	        
				        	process(data);               		
				            		
				        }else if(s_PF.equals("P0F2")){
				            //-------------�˳���¼--------------
				            cat.info("[GprsServer]�˳���¼����");
				            //683100310068C911111111000270000002008116
				            state = "state,"+s_xzqxm+","+s_zddz+",outline";//״̬
				            //��״̬���͸���վ....
				            data = new DataObject("up",state);
				        	ModuleContainer container = (ModuleContainer)module.getContext();
				        	data.setModuleID(container.moduleID);//����ģ��ID
				        	        
				        	process(data);  
				            		
				        }else if(s_PF.equals("P0F3")){
				            //-------------����--------------
				            cat.info("[GprsServer]��������");
				            //683100310068C911111111000270000004008316
				            state = "state,"+s_xzqxm+","+s_zddz+",online";//״̬
				            //��״̬���͸���վ....
				            data = new DataObject("up",state);
				        	ModuleContainer container = (ModuleContainer)module.getContext();
				        	data.setModuleID(container.moduleID);//����ģ��ID
				        	        
				        	process(data);
				        }
				            	
				            	
				            	
				    }else{
				            //-----------����·���-------------
				            //���͸���վ....
				            cat.info("[GprsServer]����·��������ϱ�");
				            data = new DataObject("up",sSJZ);
				    	    ModuleContainer container = (ModuleContainer)module.getContext();
				    	    data.setModuleID(container.moduleID);//����ģ��ID
				    	        
				    	    process(data);
				    }
				     
				}catch(Exception e){
					
				}
			}
		}
	}
	
	//���ݲ���߳�
	class SplitThread1 extends Thread {
		public void run(){
			DataObject data = null;
			String state = "";
			while(true){
				try{
					sleep(5);
 				
					synchronized(zdBuffer){
						Set set = zdBuffer.keySet();
						Object[] o_key = set.toArray();
						for(int k=0;k<o_key.length;k++){
	 						//---------------ÿ���ն˵�buffer------------------
	
							Object obj = o_key[k];//�ն�SocketChannel����
							ByteBuffer buffer = (ByteBuffer)zdBuffer.get(obj);
							
	 						int bt_length = buffer.position();
	 	 					
	 	 					if(bt_length == 0){
	 							continue;
	 						}
 						
	 	 					byte[] bt_new = new byte[bt_length];
 						
	 						buffer.rewind();
	 						buffer.get(bt_new,0,bt_length);
	 						
	 						ByteBuffer buffer_left = ByteBuffer.allocate(Integer.parseInt(queue_buffer));
	 						
	 						
	 						int idx_68_1 = -1;//��һ��68���±�
	 						int idx_68_2 = -1;//��һ��68���±�
	 						int idx_16 = -1;//������16���±�
	 						//int idx_len = -1;//���ȵ��±�
	 						for(int i=0;i<bt_new.length;i++){
	 							if(bt_new[i] == 0x68){
	 								idx_68_1 = i;
	 								break;
	 							}								
	 						}
	 						
	 						if(idx_68_1 != -1){
	 							idx_68_2 = idx_68_1 + 5;
	 							if(bt_new[idx_68_2] != 0x68){
	 								for(int i=idx_68_2+1;i<bt_new.length;i++){
	 									buffer_left.put(bt_new[i]);
	 								}
	 							}else{
	 								byte[] tempb_len1 = {bt_new[idx_68_1+2],bt_new[idx_68_1+1]};
	 				            	String s_len1 = Util.bytetostrs(tempb_len1);
	 				            	int data_len = Integer.parseInt(s_len1, 16);
	 					            data_len = (data_len - 1) / 4;
	 					            
	 					            idx_16 = idx_68_2 + data_len + 2;
	
	 								if(idx_16>bt_new.length-1){
	 									continue;//����������
	 								}
	 								if(bt_new[idx_16] != 0x16){
	 									for(int i=idx_68_2+1;i<bt_new.length;i++){
	 										buffer_left.put(bt_new[i]);
	 									}
	 								}else{
	 																			
	 									int sjz_len = data_len + 8;
	 						            byte[] bt_sjz = new byte[data_len+8];
	 						            System.arraycopy(bt_new,idx_68_1,bt_sjz,0,data_len+8);
	 						            
	 						            //ʣ������
	 						            if(bt_new.length > idx_16 +1){
	 						            	for(int i=idx_16+1;i<bt_new.length;i++){
	 						            		buffer_left.put(bt_new[i]);
	 						            	}
	 						            }
	 									
	 						            String sSJZ = Util.bytetostrs(bt_sjz);
	 							        cat.info("[GprsServer]s_sjz:"+sSJZ);
	 							
	 							        String s_csdata = sSJZ.substring(12, sjz_len*2 - 4);
	 							        cat.info("[GprsServer]s_csdata:" + s_csdata);
	 							        
	 							        String s_cs = sSJZ.substring(sjz_len*2 - 4, sjz_len*2 - 2);
	 							        if (! (Util.getCS(s_csdata)).equalsIgnoreCase(s_cs)) {	            	
	 							        	//��ʣ��ı��ķŻ�zdBuffer
					 						zdBuffer.put(obj,buffer_left);
					 						
					 						continue;
	 							        }
	 							            
	 							            
	 							            
	 							        //һ��ȡ����֡������Ϣ
	 							            
	 							        //����������
	 							        String s_xzqxm = s_csdata.substring(2, 6);
	 							        s_xzqxm = Util.convertStr(s_xzqxm);
	 							        cat.info("[GprsServer]s_xzqxm:" + s_xzqxm);
	 							
	 							        //�ն˵�ַ
	 							        String s_zddz = s_csdata.substring(6, 10);
	 							        s_zddz = Util.convertStr(s_zddz);
	 							        cat.info("[GprsServer]s_zddz:" + s_zddz);
	 							            
	 							
	 							        //Ӧ�ù�����AFN
	 							        String s_afn = s_csdata.substring(12, 14);
	 							        cat.info("[GprsServer]s_afn:" + s_afn);
	 							            
	 							        String s_dadt = s_csdata.substring(16, 24);
	 							        cat.info("[GprsServer]s_dadt:" + s_dadt);
	 							            
	 							        //��Ϣ��Pn
	 							        String s_da = s_dadt.substring(0,4);
	 							        s_da = Util.tranDA(Util.convertStr(s_da));
	 							        String s_Pda = "P" + s_da;
	 							        //��Ϣ��Fn
	 							        String s_dt = s_dadt.substring(4,8);
	 							        s_dt = Util.tranDT(Util.convertStr(s_dt));
	 							        String s_Fdt = "F" + s_dt;
	 							        //PnFn
	 							        String s_PF = s_Pda + s_Fdt;
	 							            
	 							        cat.info("[GprsServer]s_PF:" + s_PF);
	 							            
	 							        //����ˢ��"�ն�-����"�б�
	 							        synchronized(zdChannel){
	 							        	Object obj_old = zdChannel.get(s_xzqxm+s_zddz);
	 							        	if(obj_old==null || !obj_old.equals(obj)){
	 							        		try{
	 							        			if(obj_old != null){
	 							        				((SocketChannel)obj_old).close();
	 							        			}
	 							        		}catch(Exception e){
	 							        			cat.error("[GprsServer]Close Old_SocketChannel error:",e);
	 							        		}
	 							        		zdChannel.put(s_xzqxm+s_zddz,obj);
	 							        	}
	 							        }
	 							            
	 							        //�����߼��ж�
	 							        String returnSJZ = "";
	 							        String returnCsData = "";
	 							        String returnCs = "";
	 							            
	 							        if(s_afn.equals("02")){
	 							            //-----------��·���-------------
	 							            //1����Ӧ�ն�
	 							        	returnSJZ = "683100310068";
	 							        	returnCsData = "0B";//C
	 							        	returnCsData += s_csdata.substring(2,12);//��ַ��
	 							        	returnCsData += "00";//AFN=00,ȷ��/����
	 							        	returnCsData += "6" + s_csdata.substring(15, 16);//SEQ
	 							        	returnCsData += "00000100";
	 							        		
	 							        	returnCs = Util.getCS(returnCsData);
	 							        		
	 							        	returnSJZ += returnCsData + returnCs + "16";
	 							        		
	 							        	((SocketChannel)obj).write(Util.strstobytebuf(returnSJZ));
	 							        		
	 							        		
	 							        	//2���������� 
	 							            if(s_PF.equals("P0F1")){
	 							            	//-------------��¼--------------
	 							            	cat.info("[GprsServer]��¼����");
	 							            	//683100310068C911111111000270000001008016
	 							            	state = "state,"+s_xzqxm+","+s_zddz+",online";//״̬
	 							            	//��״̬���͸���վ....
	 							            	data = new DataObject("up",state);
	 							        	    ModuleContainer container = (ModuleContainer)module.getContext();
	 							        	    data.setModuleID(container.moduleID);//����ģ��ID
	 							        	        
	 							        	    process(data);               		
	 							            		
	 							            }else if(s_PF.equals("P0F2")){
	 							            	//-------------�˳���¼--------------
	 							            	cat.info("[GprsServer]�˳���¼����");
	 							            	//683100310068C911111111000270000002008116
	 							            	state = "state,"+s_xzqxm+","+s_zddz+",outline";//״̬
	 							            	//��״̬���͸���վ....
	 							            	data = new DataObject("up",state);
	 							        	    ModuleContainer container = (ModuleContainer)module.getContext();
	 							        	    data.setModuleID(container.moduleID);//����ģ��ID
	 							        	        
	 							        	    process(data);  
	 							            		
	 							            }else if(s_PF.equals("P0F3")){
	 							            	//-------------����--------------
	 							            	cat.info("[GprsServer]��������");
	 							            	//683100310068C911111111000270000004008316
	 							            	state = "state,"+s_xzqxm+","+s_zddz+",online";//״̬
	 							            	//��״̬���͸���վ....
	 							            	data = new DataObject("up",state);
	 							        	    ModuleContainer container = (ModuleContainer)module.getContext();
	 							        	    data.setModuleID(container.moduleID);//����ģ��ID
	 							        	        
	 							        	    process(data);
	 							            }
	 							            	
	 							            	
	 							            	
	 							         }else{
	 							            	//-----------����·���-------------
	 							            	//���͸���վ....
	 							            	cat.info("[GprsServer]����·��������ϱ�");
	 							            	data = new DataObject("up",sSJZ);
	 							    	        ModuleContainer container = (ModuleContainer)module.getContext();
	 							    	        data.setModuleID(container.moduleID);//����ģ��ID
	 							    	        
	 							    	        process(data);
	 							         }
	 									
	 								}
	 							}
	 						}
	 						
	 						//��ʣ��ı��ķŻ�zdBuffer
	 						zdBuffer.put(obj,buffer_left);
	 					}
					
					
					}
 				
				}catch(Exception e){
		            cat.error("[GprsServer]���ݲ�ֳ���:",e);		            	
				}
 			
			}
		}
	}
 
}