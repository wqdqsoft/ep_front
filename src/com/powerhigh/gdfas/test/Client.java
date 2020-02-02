package com.powerhigh.gdfas.test;


import java.nio.channels.SocketChannel;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.powerhigh.gdfas.util.*;

public class Client{
 private SocketChannel sc; 
 private final int MAX_LENGTH = 1024;
 private ByteBuffer r_buff = ByteBuffer.allocate(MAX_LENGTH);
 private ByteBuffer w_buff = null;//ByteBuffer.allocate(MAX_LENGTH);
 private static String host = "127.0.0.1";
 private static int port = 1234;
 
 class sendThread extends Thread{
 	String loginStr = "683100310068C911111111000270000001008016";//国电登陆
 	String pingStr =  "683100310068C911111111000270000004008316";//国电ping
 	String downloadStr = "68C100C10068C911111111000F700000040001016E69622E74756F79616C2020202020202020202020202020202020202020202001005816";
// 	String loginStr = "6891100500410068A10300112233BD16";
// 	String pingStr = "6891100500410068A400005716";
 	boolean flag = true;
 	public void destroy(){
 		flag = false;
 	}
 	public sendThread(String xzqxm,String zddz){
 		//登录
 		try{
 			String login_csdata = "C9"+Util.convertStr(xzqxm)
				+Util.convertStr(zddz)+"00027000000100";
 			loginStr = "683100310068" + login_csdata
				+Util.getCS(login_csdata)+"16";
 			
 			String ping_csdata = "C9"+Util.convertStr(xzqxm)
				+Util.convertStr(zddz)+"00027000000400";
			pingStr = "683100310068" + ping_csdata
				+Util.getCS(ping_csdata)+"16";
			
 			System.out.println("发送登录");
 			sc.write(Util.strstobytebuf("68"+"68"+"68"+loginStr+"68")); 
 			sc.write(Util.strstobytebuf("68"+loginStr+"68")); 			
 			
 		}catch(Exception e){
 			System.out.println("登录出错");
 			e.printStackTrace();
 		}
 	}
 	
 	public void run(){
 		try{
 		while(true){
 			if(flag){
 			//心跳
 				System.out.println("发送心跳");
 				sc.write(Util.strstobytebuf(pingStr));
 			
// 				System.out.println("发送登录");
// 				sc.write(Util.strstobytebuf(loginStr)); 
 			    sleep(6000);
// 			    System.out.println("请求下载");
// 	 			sc.write(Util.strstobytebuf(downloadStr));
 			}else{
 				break;
 			}
 		}
 	 		}catch(Exception e){
 	 			System.out.println("发送心跳出错");
 	 			e.printStackTrace();
 	 		}
 		
 	}
 }
 
 class receiveThread extends Thread{
 	boolean flag = true;
 	public void destroy(){
 		flag = false;
 	}
 	public receiveThread(){
 		
 	}
 	
 	public void run(){
 		int count = 0;
// 		接收
			try{
 		while(true){
 			sleep(1);
 			if(flag){
 				r_buff.clear();  
 				
 				count= sc.read(r_buff);
 				r_buff.flip();  
 				r_buff.rewind();
 				  
 				int len = r_buff.remaining();
 				if (len==0){
 					continue;
 				}
 				  
 				String receive = Util.bytebuf2str(r_buff);
 				  
 				System.out.println("Received Data: " );
 				System.out.println(receive);
 				String afn = receive.substring(24,26);
 				if(afn.equals("0F")){
 					//远程下载
 					String cs = receive.substring(receive.length()-4,receive.length()-2);
 					receive = receive.substring(0,32)+"01"+receive.substring(34);
 					String Fn = receive.substring(32,34);
 					
 					int i_cs = Integer.parseInt(cs,16);
 					cs = Util.decStrToHexStr(i_cs-1,1);
 					
 					receive = receive.substring(0,receive.length()-4)+cs+"16";
 					
 					sc.write(Util.strstobytebuf(receive));
 	 				
 	 				System.out.println("远程下载返回:"+receive);
 	 				
 				}else if(afn.equals("0C")){
 					//1类数据返回
 					String seq = receive.substring(26,28);
 					String xzqxm_zddz = receive.substring(14,22);
 					
 					String dadt = "00004000";//P0F7
 					String data = "1112"; 					
 				    
 				    String cs_data = "80"+xzqxm_zddz+"000C" 
 				   			+"6"+seq.substring(1,2) +dadt+ data;
 				    
 				    int iLEN = cs_data.length();
					iLEN = iLEN * 2 + 1;
				    String sLEN = Util.decStrToHexStr(iLEN,2);
				    sLEN = Util.convertStr(sLEN);
 				    receive = "68"+sLEN+sLEN+"68"+cs_data+Util.getCS(cs_data)+"16";
 				    
 				    sc.write(Util.strstobytebuf(receive));
	 				
	 				System.out.println("1类数据返回:"+receive);
 				}else if(afn.equals("0D")){
 					//2类数据返回
 					String seq = receive.substring(26,28);
 					String xzqxm_zddz = receive.substring(14,22);
 					
 					String dadt = "01010109";
 					String data = "00011709050130158020807080308005801080158020807080308045801080158020807080308045801080058120807080308045801080158020802081308045801080158020807080308045801080058020807080308025811080158020807080308045801081";
 					int iLEN = 230;//csdata.length
 					iLEN = iLEN * 2 + 1;
 				    String sLEN = Util.decStrToHexStr(iLEN,2);
 				    sLEN = Util.convertStr(sLEN);
 				    
 				    String cs_data = "80"+xzqxm_zddz+"000D" 
 				   			+"6"+seq.substring(1,2) +dadt+ data;
 				    
 				    receive = "68"+sLEN+sLEN+"68"+cs_data+Util.getCS(cs_data)+"16";
 				    
 				    sc.write(Util.strstobytebuf(receive));
	 				
	 				System.out.println("2类数据返回:"+receive);
 				}else if(afn.equals("04")){
 					//设置命令返回
 					String seq = receive.substring(26,28);
 					String xzqxm_zddz = receive.substring(14,22);
 					String sDA = Util.getDA("P0");
 				    sDA = Util.convertStr(sDA);
 				    String sDT = Util.getDT("F1");
 				    sDT = Util.convertStr(sDT); 				    
 					String dadt = sDA + sDT;//P0F1
 					
 					String cs_data = "80" + xzqxm_zddz+"00"+"00"
							+"0"+seq.substring(1,2)+dadt;
 					int iLEN = cs_data.length();
 					iLEN = iLEN * 2 + 1;
 				    String sLEN = Util.decStrToHexStr(iLEN,2);
 				    sLEN = Util.convertStr(sLEN);
 				    
 				    receive = "68"+sLEN+sLEN+"68"+cs_data+Util.getCS(cs_data)+"16";
				    
				    sc.write(Util.strstobytebuf(receive));
	 				
	 				System.out.println("设置命令返回:"+receive);
 				}else if(afn.equals("05")){
 					//设置命令返回
 					String seq = receive.substring(26,28);
 					String xzqxm_zddz = receive.substring(14,22);
 					String sDA = Util.getDA("P0");
 				    sDA = Util.convertStr(sDA);
 				    String sDT = Util.getDT("F1");
 				    sDT = Util.convertStr(sDT); 				    
 					String dadt = sDA + sDT;//P0F1
 					
 					String cs_data = "80" + xzqxm_zddz+"00"+"00"
							+"0"+seq.substring(1,2)+dadt;
 					int iLEN = cs_data.length();
 					iLEN = iLEN * 2 + 1;
 				    String sLEN = Util.decStrToHexStr(iLEN,2);
 				    sLEN = Util.convertStr(sLEN);
 				    
 				    receive = "68"+sLEN+sLEN+"68"+cs_data+Util.getCS(cs_data)+"16";
				    
				    sc.write(Util.strstobytebuf(receive));
	 				
	 				System.out.println("控制命令返回:"+receive);
 				}else if(afn.equals("0A")){
 					//参数查询返回
 					String seq = receive.substring(26,28);
 					String xzqxm_zddz = receive.substring(14,22);
 					String dadt = receive.substring(28, 36);
 		            
 		            //信息点Pn
 		            String s_da = dadt.substring(0,4);
 		            s_da = Util.tranDA(Util.convertStr(s_da));
 		            String s_Pda = "P" + s_da;
 		            //信息类Fn
 		            String s_dt = dadt.substring(4,8);
 		            s_dt = Util.tranDT(Util.convertStr(s_dt));
 		            String s_Fdt = "F" + s_dt;
 		            //PnFn
 		            String s_PF = s_Pda + s_Fdt;
 		            String data = "";
 		            if(s_Fdt.equals("F7")){
	 					data = "FF00007F3012";	
	 					
 		            }else if(s_Fdt.equals("F8")){
	 					data = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";	
	 					
 		            }else if(s_Fdt.equals("F9")){
	 					data = "10101010";	 					
 		            }else if(s_Fdt.equals("F10")){
	 					data = "0201010401120000000000010000000000FF02030401120000000000010000000000FF";	 					
 		            }else if(s_Fdt.equals("F11")){
	 					data = "0201050300190106010010";	 					
 		            }else if(s_Fdt.equals("F14")){
	 					data = "020102C1030302C2C4";	 					
 		            }else if(s_Fdt.equals("F17")){
	 					data = "45C3";	 					
 		            }else if(s_Fdt.equals("F18")){
	 					data = "A5A55A5A0000000000000000";	 					
 		            }else if(s_Fdt.equals("F19")){
	 					data = "77";	 					
 		            }else if(s_Fdt.equals("F24")){
	 					data = "11";	 					
 		            }else if(s_Fdt.equals("F25")){
	 					data = "1111111111111111";	 					
 		            }else if(s_Fdt.equals("F65")){
	 					data = "CF151515210905110101010103";	 					
 		            }else if(s_Fdt.equals("F66")){
	 					data = "CF151515210905110101010109";	 					
 		            }else if(s_Fdt.equals("F67")){
	 					data = "55";	 					
 		            }else if(s_Fdt.equals("F68")){
	 					data = "AA";	 					
 		            }
 		            
 		            String cs_data = "80" + xzqxm_zddz+"00"+"0A"
						+"0"+seq.substring(1,2)+dadt+data;
 		            int iLEN = cs_data.length();
 		            iLEN = iLEN * 2 + 1;
 		            String sLEN = Util.decStrToHexStr(iLEN,2);
 		            sLEN = Util.convertStr(sLEN);
			    
 		            receive = "68"+sLEN+sLEN+"68"+cs_data+Util.getCS(cs_data)+"16";
			    
 		            sc.write(Util.strstobytebuf(receive));
				
 		            System.out.println("参数查询返回:"+receive);
 		            
 				}else if(afn.equals("0E")){
 					//事件查询返回
 					String seq = receive.substring(26,28);
 					String xzqxm_zddz = receive.substring(14,22);
 					String dadt = receive.substring(28, 36);
 		            
 		            String data = "11220002"
 		            		+"010E0012260905034141414142424242"
							+"1E0C001227090501123456789011";
 		            
 		            String cs_data = "80" + xzqxm_zddz+"00"+"0E"
//						+"0"+seq.substring(1,2)+dadt+data;
 		           		+seq+dadt+data;
 		            int iLEN = cs_data.length();
 		            iLEN = iLEN * 2 + 1;
 		            String sLEN = Util.decStrToHexStr(iLEN,2);
 		            sLEN = Util.convertStr(sLEN);
			    
 		            receive = "68"+sLEN+sLEN+"68"+cs_data+Util.getCS(cs_data)+"16";
			    
 		            sc.write(Util.strstobytebuf(receive));
				
 		            System.out.println("事件查询返回:"+receive);
 		            
 				}else if(afn.equals("0B")){
 					//任务查询返回
 					String seq = receive.substring(26,28);
 					String xzqxm_zddz = receive.substring(14,22);
 					String dadt = receive.substring(28, 36);
 		            //信息点Pn
 		            String s_da = dadt.substring(0,4);
 		            s_da = Util.tranDA(Util.convertStr(s_da));
 		            String s_Pda = "P" + s_da;
 		            //信息类Fn
 		            String s_dt = dadt.substring(4,8);
 		            s_dt = Util.tranDT(Util.convertStr(s_dt));
 		            String s_Fdt = "F" + s_dt;
 		            //PnFn
 		            String s_PF = s_Pda + s_Fdt;
 		            String data = "";
 		            if(s_Pda.equals("P1")){
	 		            data = "0012260905"
	 		            		+"123495"
	 		            		+"123495"
	 		            		+"123495"
	 		            		+"123495"
	 		            		+"123495"
	 		            		+"123495"
	 		            		+"123495"
	 		            		+"123495"
	 		            		+"1234"
	 		            		+"1234"
	 		            		+"1234"
	 		            		+"1234"
	 		            		+"3412"
	 		            		+"3412"
	 		            		+"3412"
	 		            		+"1293"
	 		            		+"1293"
	 		            		+"1293"
	 		            		+"1293"
								+"110030170900";
 		            }else if(s_Pda.equals("P3")){
 		            	data = "12803280"
 		            			+"110030190900";
 		            }
 		            
 		            String cs_data = "80" + xzqxm_zddz+"00"+"0B"
//						+"0"+seq.substring(1,2)+dadt+data;
 		           		+seq+dadt+data;
 		            int iLEN = cs_data.length();
 		            iLEN = iLEN * 2 + 1;
 		            String sLEN = Util.decStrToHexStr(iLEN,2);
 		            sLEN = Util.convertStr(sLEN);
			    
 		            receive = "68"+sLEN+sLEN+"68"+cs_data+Util.getCS(cs_data)+"16";
			    
 		            sc.write(Util.strstobytebuf(receive));
				
 		            System.out.println("任务查询返回:"+receive);
 				}
 				
 		}else{
 			break;
 		}
 		}
 	 		}catch(Exception e){
 	 			System.out.println("接收出错");
 	 			e.printStackTrace();
 	 		}
 		
 	}
 }
 
 public Client(String xzqxm,String zddz){
  try {
   InetSocketAddress addr = new InetSocketAddress(host,port);
   //生成一个socketchannel
   sc = SocketChannel.open();
         
   //连接到server
   sc.connect(addr);
   sc.configureBlocking(false);
   
   System.out.println("connection has been established!...");
    
   
   sendThread send = new sendThread(xzqxm,zddz);
   receiveThread receive = new receiveThread();
   
   send.start();
   receive.start();
   
     
//   send.destroy();
//   receive.destroy();   
//   Thread.sleep(70000);   
//   sc.close();

  }catch(Exception ioe){
   ioe.printStackTrace();
  }
    
 }

 
 public static void main(String args[]){
  String xzqxm = "3505";
  String zddz = "004B";
  new Client(xzqxm,zddz);
 }
}



