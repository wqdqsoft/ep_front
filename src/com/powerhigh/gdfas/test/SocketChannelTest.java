package com.powerhigh.gdfas.test;

import com.powerhigh.gdfas.util.*;

import java.io.IOException;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;
import java.util.*;

public class SocketChannelTest{
	private Selector readSelector = null;
	private int port = 0;
	private ConnectQueue queue = null;
	private HashMap zdMap = null;//key:[xzqxm+zddz]
								 //value:[SocketChannel]
	
	private AcceptThread acceptThread = null;
	private ProcessThread processThread = null;
	
	
	public SocketChannelTest(int pt) throws Exception{
		this.readSelector = Selector.open();
		this.port = pt;
		this.queue = new ConnectQueue();
		
		this.acceptThread = new AcceptThread();
		this.processThread = new ProcessThread();
		this.acceptThread.start();
		this.processThread.start();
		
		this.zdMap = new HashMap();
	}
	
	//ConnectQueue
	class ConnectQueue extends CMQueue{
     	
     	 private Selector selector;
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
                     
                     sleep(1);  //休眠
                 } catch(Exception ex) {
                    ex.printStackTrace();
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
                      System.out.println("Client Coming!");
                 }
              }
    	   
    	  }
    }
	

    //ProcessThread
    class ProcessThread extends Thread{
    	  SelectionKey selKey;
         private ByteBuffer readBuffer=ByteBuffer.allocate(1024);
         
    	  public ProcessThread() throws Exception{
    	  	 
    	  }
    	  
    	  public void run(){
    	      while(true) {
                 try {
                      sleep(2);                         
                      int keysReady = readSelector.select();
                      
                      registerNewChannels();
                      
                      if(keysReady > 0) {
                      	  
                          acceptRequests();
                      }
                 } catch(Exception ex) {
                     ex.printStackTrace();
                 }
             }	
    	  }
    	  
    	  //注册为read事件
    	  protected void registerNewChannels() throws Exception {
            SocketChannel channel;
            if (queue.size()<=0) return;
            
            while(null != (channel = (SocketChannel)queue.get())) {
            	System.out.println("channel.register!");
                channel.configureBlocking(false);
                channel.register(readSelector, SelectionKey.OP_READ);
                
               
           }  
        }
        
    	//从Selector中取已准备好的连接
        protected void acceptRequests() throws Exception {
        	 System.out.println("||Read from Clients||");
            Set readyKeys = readSelector.selectedKeys();
            
            System.out.println("readyKeys.size():"+readyKeys.size());
           for(Iterator i = readyKeys.iterator(); i.hasNext(); ) {
               SelectionKey key = (SelectionKey)i.next();
               i.remove();
               processRequest(key);
               
               sleep(1);
          }
      }
      
      //接收并处理Client发过来的数据
      protected void processRequest(SelectionKey key) throws Exception {
      	   String state = null;      	   
           SocketChannel incomingChannel = (SocketChannel)key.channel();
           
           Socket incomingSocket = incomingChannel.socket();
           int client_port = incomingSocket.getPort();
           InetAddress addr = incomingSocket.getInetAddress();
           String client_ip = addr.getHostAddress();
           System.out.println("client_ip:"+client_ip);
           System.out.println("client_port:"+client_port);
           
           readBuffer.clear();
         
           try {
           
           	
               
           
               int bytesRead= incomingChannel.read(readBuffer);
               
               
               if (bytesRead==-1){  //连接异常关闭
               	System.out.println("连接异常关闭!");
                    incomingChannel.close();
                    	
                    key.cancel();  
                    return;
               }
               readBuffer.flip();  
               
               int len = readBuffer.remaining();
               if (len==0) return;
               System.out.println("len:"+len);
               
               byte[] bt = new byte[len];
               readBuffer.get(bt, 0, len);
               
               System.out.println("bt[0]:"+bt[0]);
               System.out.println("bt[1]:"+bt[1]);
               String sSJZ = Util.bytetostrs(bt);
               int sjz_len = sSJZ.length();
               
               System.out.println("readData:"+sSJZ);
               
               //一、数据帧有效性判断
               
               String s_begin1 = sSJZ.substring(0, 2);
               if (!s_begin1.equals("68")) {
               	System.out.println("begin1 is error:" + s_begin1);
                 return;
               }
               System.out.println("sjz_len:" + sjz_len + "s_begin1:" + s_begin1);
               String s_len1 = sSJZ.substring(2, 6);
               s_len1 = Util.convertStr(s_len1);
               String s_len2 = sSJZ.substring(6, 10);
               s_len2 = Util.convertStr(s_len2);
               System.out.println("len1:" + s_len1 + "len2:" + s_len2);
               if (!s_len1.equals(s_len2)) {
               	System.out.println("len1!=len2:" + "len1:" + s_len1 + "len2:" + s_len2);
                 return;
               }
               String s_begin2 = sSJZ.substring(10, 12);
               if (!s_begin2.equals("68")) {
               	System.out.println("begin2 is error:" + s_begin2);
                 return;
               }
               String s_end = sSJZ.substring(sjz_len - 2, sjz_len);
               if (!s_end.equals("16")) {
               	System.out.println("end is error:" + s_end);
                 return;
               }

               int data_len = Integer.parseInt(s_len1, 16);
               data_len = (data_len - 1) / 4;
               data_len = data_len * 2;
               if (sjz_len != (data_len + 16)) {
               	System.out.println("sjz_len != (data_len + 16)");
               	System.out.println("sjz_len:" + sjz_len + "data_len:" + data_len);
                 return;
               }

               String s_csdata = sSJZ.substring(12, sjz_len - 4);
               System.out.println("s_csdata:" + s_csdata);
               String s_cs = sSJZ.substring(sjz_len - 4, sjz_len - 2);
               if (! (Util.getCS(s_csdata)).equalsIgnoreCase(s_cs)) {
               	System.out.println("CS is error!" + "s_cs:" + s_cs);
                 return;
               }
               
               
               
               //二、取数据帧基本信息
               
               //行政区县码
               String s_xzqxm = s_csdata.substring(2, 6);
               s_xzqxm = Util.convertStr(s_xzqxm);
               System.out.println("s_xzqxm:" + s_xzqxm);

               //终端地址
               String s_zddz = s_csdata.substring(6, 10);
               s_zddz = Util.convertStr(s_zddz);
               System.out.println("s_zddz:" + s_zddz);
               

               //应用功能码AFN
               String s_afn = s_csdata.substring(12, 14);
               System.out.println("s_afn:" + s_afn);
               
               String s_dadt = s_csdata.substring(16, 24);
               System.out.println("s_dadt:" + s_dadt);
               
               //信息点Pn
               String s_da = s_dadt.substring(0,4);
               s_da = Util.tranDA(Util.convertStr(s_da));
               String s_Pda = "P" + s_da;
               //信息类Fn
               String s_dt = s_dadt.substring(4,8);
               s_dt = Util.tranDT(Util.convertStr(s_dt));
               String s_Fdt = "F" + s_dt;
               //PnFn
               String s_PF = s_Pda + s_Fdt;
               
               System.out.println("s_PF:" + s_PF);
               
               
               
               String returnSJZ = "";
               String returnCsData = "";
               String returnCs = "";
               //三、逻辑判断
               if(s_afn.equals("02")){
               	//-----------链路检测-------------
               	if(s_PF.equals("P0F1")){
               		//-------------登录--------------
               		System.out.println("登录请求");
               		//683100310068C911111111000270000001008016
               		state = "state,"+s_xzqxm+","+s_zddz+",online";//状态
               		//发送给主站....
               		               		
               		
               	}else if(s_PF.equals("P0F2")){
               		//-------------退出登录--------------
               		System.out.println("退出登录请求");
               		//683100310068C911111111000270000002008116
               		state = "state,"+s_xzqxm+","+s_zddz+",outline";//状态
               		//发送给主站....
               		
               		
               	}else if(s_PF.equals("P0F3")){
               		//-------------心跳--------------
               		System.out.println("心跳请求");
               		//683100310068C911111111000270000004008316
               		
               	}
               	
               	//响应终端
           		returnSJZ = "683100310068";
           		returnCsData = "0B";//C
           		returnCsData += s_csdata.substring(2,12);//地址域
           		returnCsData += "00";//AFN=00,确认/否认
           		returnCsData += "6" + s_csdata.substring(15, 16);//SEQ
           		returnCsData += "00000100";
           		
           		returnCs = Util.getCS(returnCsData);
           		
           		returnSJZ += returnCsData + returnCs + "16";
           		
           		incomingChannel.write(Util.strstobytebuf(returnSJZ));
               	
               }else{
               	//-----------非链路检测-------------
               	//发送给主站....
               }
               
               //3、刷新"终端-连接"列表
               zdMap.put(s_xzqxm+s_zddz,incomingChannel);
               
               
               //<test
              
//               readBuffer.rewind();
//               incomingChannel.write(readBuffer);
               
               System.out.println("echoed");
               //test>
               
             }catch(IOException ioe){
                   
                   incomingChannel.close();
                   key.cancel();
                  
                   throw ioe;
                
             }catch(Exception e) {   
                  throw e;
             }
         } 	
    }
    
    public static void main(String[] args){
    	try{
    		System.out.print("Begin");
    	SocketChannelTest test = new SocketChannelTest(12345);
    	}catch(Exception e){
    		System.out.print("main error:");
    		e.printStackTrace();
    	}
    }
}