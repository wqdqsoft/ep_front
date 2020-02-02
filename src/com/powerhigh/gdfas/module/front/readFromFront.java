package com.powerhigh.gdfas.module.front;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.*;
import org.jfree.data.DataUtilities;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.util.*;
import com.powerhigh.gdfas.module.ThreadPool;
import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.module.ModuleContainer;

/**
 * Description: 线程:从前置机读取数据<p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-5
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class readFromFront extends Thread {
	
	@SuppressWarnings("unused")
	private static final String  resource = "log4j.properties";
    private static Category cat =
                    Category.getInstance(com.powerhigh.gdfas.module.front.readFromFront.class);

    private ThreadPool pool = null;
    private AbstractModule module = null;
  
    public readFromFront(Object obj1,Object obj2) {
    	this.pool = (ThreadPool)obj1;
    	this.module= (AbstractModule)obj2;
    }
    
    public readFromFront() {
	}
    
//    private DataSource dataSource = null;
//    public DataSource getDataSource() {
//  	return dataSource;
//    }
//    public void setDataSource(DataSource ds) {
//  	dataSource = ds;
//    }


    /**
     *方法简介：从前置机接收数据(ASCII码字符)
     *
     *@return s String ASCII码字符
     */
  @SuppressWarnings("unused")
private synchronized String getAsciiFromFront() {
    String s = "";
    try {
      byte[] bt = new byte[4096];
      int tempi = (communicateWithFront.getInputStream()).read(bt);
      if(tempi == -1){
      	communicateWithFront.setIsConnected(false);
        return "";
      }
      for (int i = 0; i < tempi; i++) {
        int ii = (int) bt[i];
        Character d = new Character( (char) ii);
        s = s.concat(d.toString());
      }

    }
    catch (Exception e) {
      System.out.println("getAsciiFromFront Error");
      communicateWithFront.setIsConnected(false);
      e.printStackTrace();
      return "";
    }
    communicateWithFront.setIsConnected(true);
    return s;
  }

  /**
  *方法简介：从前置机接收数据(十六进制字符)
  *
  *@return s String 十六进制码字符
  */
  private synchronized String getHexFromFront() {
    String s = "";
    try {
      byte[] bt = new byte[4096];
      int tempi = (communicateWithFront.getInputStream()).read(bt);
      if(tempi == -1){
      	communicateWithFront.setIsConnected(false);
        return "";
      }
      byte[] temp_bt = new byte[tempi];
      System.arraycopy(bt,0,temp_bt,0,tempi);
      s = Util.bytetostrs(temp_bt);

    }
    catch (Exception e) {
    	communicateWithFront.setIsConnected(false);
      e.printStackTrace();
      return "";
    }
    communicateWithFront.setIsConnected(true);
    return s;
  }


  public void run() {
    try {
      int i = 0;
      while(true){
    	  try{
    	        sleep(1);
    	        //cat.info("readFromFront");
    	        //判断连接是否正常
    	        if(!communicateWithFront.isConnected()){
    	          System.out.println("连接前置机失败");
    	          cat.info("连接前置机失败");
    	          i = communicateWithFront.Initialize();
    	          if (i == -1) {
    	            //重连失败，等10秒钟后重连
    	            System.out.println("重连前置机失败，等10秒钟后重连");
    	            cat.info("重连前置机失败，等10秒钟后重连");
    	            sleep(10000);
    	            continue;
    	          }

    	        }

    	        String s_receive = getHexFromFront();
    	        
    	        System.out.println("Front receive:"+s_receive);
    	        if(s_receive==null||s_receive.equals(""))
    	          continue;
    	        if(s_receive.equalsIgnoreCase(communicateWithFront.ping)){
    	        	//ping响应
    	        	System.out.println("pingFromFront:"+s_receive);
    	        	continue;
    	        }
    	        
    	        
    	        //过滤终端链路检测报文
    	        DataObject data = filterAFN02(s_receive);
    	        
    	        if(null!=data){
    	        	 try{
    	    		        //处理
    	    		        ModuleContainer container = (ModuleContainer)this.module.getContext();
    	    		        data.setModuleID(container.moduleID);//设置模块ID
    	    		        pool.performWork(data);
    	    	        }catch(Exception e){
    	    	        	cat.error("Process Error:",e);
    	    	        	continue;
    	    	        }
    	        }
    	        
    	       
    		  
    	  }catch(Exception e){
    		  e.printStackTrace();
    	  }
      }
    }catch (Exception e) {
      e.printStackTrace();
    }

  }
  
  /**
   * 2017-05-18新增回复确认帧前的合法性校验
   * @return
   */
  public  boolean beforFilterAFN02(String sSJZ){
	  int sjz_len = 0;
	  sjz_len = sSJZ.length();
	  String s_begin1 = sSJZ.substring(0, 2);
		if (!s_begin1.equals("68")) {
			cat.error("[beforFilterAFN02]begin1 is error:" + s_begin1);
			return false;
		}
		// cat.info("sjz_len:" + sjz_len + "s_begin1:" + s_begin1);
		String s_len1 = sSJZ.substring(2, 6);
		try {
			s_len1 = Util.convertStr(s_len1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		String s_len2 = sSJZ.substring(6, 10);
		try {
			s_len2 = Util.convertStr(s_len2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		// cat.info("len1:" + s_len1 + "len2:" + s_len2);
		if (!s_len1.equals(s_len2)) {
			cat.error("[beforFilterAFN02]len1!=len2:" + "len1:" + s_len1 + "len2:" + s_len2);
			return false;
		}
		String s_begin2 = sSJZ.substring(10, 12);
		if (!s_begin2.equals("68")) {
			cat.error("[beforFilterAFN02]begin2 is error:" + s_begin2);
			return false;
		}
		String s_end = sSJZ.substring(sjz_len - 2, sjz_len);
		if (!s_end.equals("16")) {
			cat.error("[beforFilterAFN02]end is error:" + s_end);
			return false;
		}

		int data_len = Integer.parseInt(s_len1, 16);
		data_len = (data_len - 1) / 4;
		data_len = data_len * 2;
		if (sjz_len != (data_len + 16)) {
			cat.error("[beforFilterAFN02]sjz_len != (data_len + 16)");
			return false;
		}

		String s_csdata = sSJZ.substring(12, sjz_len - 4);
		// cat.info("s_csdata:" + s_csdata);
		String s_cs = sSJZ.substring(sjz_len - 4, sjz_len - 2);
		if (!(Util.getCS(s_csdata)).equalsIgnoreCase(s_cs)) {
			cat.error("[beforFilterAFN02]CS is error!" + "s_cs:" + s_cs);
			return false;
		}
	  return true;
  }
  
  public DataObject filterAFN02(String sSJZ) throws Exception{
//	  System.out.println("2------------"+sSJZ);
	  
	  //前导
	  String front = sSJZ.substring(0,20);
	  
	  //去除前导
	  String sjz = sSJZ.substring(20);
	  
	  //2017-05-18新增，进行回复终端前的合法性校验
	  boolean flag=beforFilterAFN02(sjz);
	  
	  if(flag){
		  //20170216统一规约类型为3
//	      String gylx = front.substring(2,3);//规约类型:1:浙规;2:国规;3:浙版国规
		  String gylx ="3";
	      
	      String txfs = front.substring(3,4);//0:不发送;1:COM;2:平台;3:GPRS;4:SMS
	      
			//一、取数据帧基本信息	
		    String s_csdata = sjz.substring(12, sjz.length() - 4);
		        				            
		     //行政区县码
		     String s_xzqxm = s_csdata.substring(2, 6);
		     s_xzqxm = Util.convertStr(s_xzqxm);
		
		     //终端地址
		     String s_zddz = s_csdata.substring(6, 10);
		     s_zddz = Util.convertStr(s_zddz);
		     
		     //20170606记录收到报文
//	        String file_url = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
//			        CMConfig.SYSTEM_FILE_URL);//文件存放目录
//	        File filenum_r = new File(file_url+s_xzqxm+s_zddz+"_SendHexToFront_receive"+".txt");
//			BufferedWriter fwnum_r = null;
//			fwnum_r = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum_r, true), "UTF-8")); //
//			//指定编码格式，以免读取时中文字符异常
//			fwnum_r.append(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+":"+sjz);
//			fwnum_r.newLine();
//			fwnum_r.flush();
//			fwnum_r.close();
			//end20170606记录收到报文
		            
		
		     //应用功能码AFN
		     String s_afn = s_csdata.substring(12, 14);
		            
		     String s_dadt = s_csdata.substring(16, 24);
		            
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
		         
		           
		       				            
		     //三、逻辑判断
		     String returnSJZ = "";
		     String returnCsData = "";
		     String returnCs = "";
		     String state = "";  
		     String newSjz = "";
		     if(s_afn.equals("02")){
		     	
//		        //2017年6月6日更改，对链路确认帧进行分别回复，其中心跳报文的seq回复92-----------链路检测-------------
//		        //1、响应终端
//		      	returnSJZ = "683200320068";
//		       	returnCsData = "0B";//C
//		        returnCsData += s_csdata.substring(2,12);//地址域
//		        returnCsData += "00";//AFN=00,确认/否认
//		        returnCsData += "6" + s_csdata.substring(15, 16);//SEQ
//		        returnCsData += "00000100";
//		        		
//		        returnCs = Util.getCS(returnCsData);
//		        		
//		        returnSJZ += returnCsData + returnCs + "16";
//
//		        if(gylx.equals("3")){
//			        //包浙规头
//			        returnSJZ = Util.addSG(s_xzqxm, s_zddz, returnSJZ);
//		        }
//		        
//		        //包前导
//		        returnSJZ = Util.addFront(returnSJZ, gylx,txfs);
		    	
//			    //end-----------链路检测-------------
		        
		       
		        
		        
		        
		
		        
//		        communicateWithFront.SendHexToFront(returnSJZ);
		        
		        		
		        		
		        //2、处理数据 
		        if(s_PF.equals("P0F1")){
		            //-------------登录--------------
		        	//-----------链路检测-------------
			        //1、响应终端
			      	returnSJZ = "683200320068";
			       	returnCsData = "0B";//C
			        returnCsData += s_csdata.substring(2,12);//地址域
			        returnCsData += "00";//AFN=00,确认/否认
			        returnCsData += "6" + s_csdata.substring(15, 16);//SEQ
			        returnCsData += "00000100";
			        		
			        returnCs = Util.getCS(returnCsData);
			        		
			        returnSJZ += returnCsData + returnCs + "16";

			        if(gylx.equals("3")){
				        //包浙规头
				        returnSJZ = Util.addSG(s_xzqxm, s_zddz, returnSJZ);
			        }
			        
			        //包前导
			        returnSJZ = Util.addFront(returnSJZ, gylx,txfs);
			        communicateWithFront.SendHexToFront(returnSJZ);
			      //-----------链路检测-------------
		        	
		        	
		            cat.info("[GprsServer]登录请求");
		            //683100310068C911111111000270000001008016
		            state = "state,"+s_xzqxm+","+s_zddz+",online";//状态
		            
		        }else if(s_PF.equals("P0F2")){
		            //-------------退出登录--------------
		        	
		        	//-----------链路检测-------------
			        //1、响应终端
			      	returnSJZ = "683200320068";
			       	returnCsData = "0B";//C
			        returnCsData += s_csdata.substring(2,12);//地址域
			        returnCsData += "00";//AFN=00,确认/否认
			        returnCsData += "6" + s_csdata.substring(15, 16);//SEQ
			        returnCsData += "00000100";
			        		
			        returnCs = Util.getCS(returnCsData);
			        		
			        returnSJZ += returnCsData + returnCs + "16";

			        if(gylx.equals("3")){
				        //包浙规头
				        returnSJZ = Util.addSG(s_xzqxm, s_zddz, returnSJZ);
			        }
			        
			        //包前导
			        returnSJZ = Util.addFront(returnSJZ, gylx,txfs);
			        communicateWithFront.SendHexToFront(returnSJZ);
			      //-----------链路检测-------------
			        
			        
		            cat.info("[GprsServer]退出登录请求");
		            //683100310068C911111111000270000002008116
		            
		            state = "state,"+s_xzqxm+","+s_zddz+",outline";//状态
		            
//		            CMContext context = new CMContext();
		            
		            
//		            2017-04-07 注释掉记录通信记录表的插入操作 放到receiveDispose.gdProcess中处理
//		            ApplicationContext ctx = 
//		    				new FileSystemXmlApplicationContext("rmi-server.xml");
//		    			Context.ctx = ctx;
//		    			
//		    			CMContext context = (CMContext)ctx.getBean("contextService");
//					context.saveLogOut(s_xzqxm, s_zddz);
//		            end 2017-04-07 注释掉记录通信记录表的插入操作
		            
		            		
		        }else if(s_PF.equals("P0F3")){
		            //-------------心跳--------------
		        	//-----------链路检测-------------
			        //1、响应终端
			      	returnSJZ = "683200320068";
			       	returnCsData = "0B";//C
			        returnCsData += s_csdata.substring(2,12);//地址域
			        returnCsData += "00";//AFN=00,确认/否认
			        returnCsData += "9" + s_csdata.substring(15, 16);//SEQ
			        returnCsData += "00000100";
			        		
			        returnCs = Util.getCS(returnCsData);
			        		
			        returnSJZ += returnCsData + returnCs + "16";

			        if(gylx.equals("3")){
				        //包浙规头
				        returnSJZ = Util.addSG(s_xzqxm, s_zddz, returnSJZ);
			        }
			        
			        //包前导
			        returnSJZ = Util.addFront(returnSJZ, gylx,txfs);
			        communicateWithFront.SendHexToFront(returnSJZ);
			      //-----------链路检测-------------
		        	
		            cat.info("[GprsServer]心跳请求");
		            //683100310068C911111111000270000004008316
		            state = "state,"+s_xzqxm+","+s_zddz+",online";//状态
		        }
		        
		        
		        //20170606记录回复报文
//		        String file_url = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
//				        CMConfig.SYSTEM_FILE_URL);//文件存放目录
//		        File filenum2 = new File(file_url+s_xzqxm+s_zddz+"_SendHexToFront"+".txt");
//				BufferedWriter fwnum2 = null;
//				fwnum2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum2, true), "UTF-8")); //
//				//指定编码格式，以免读取时中文字符异常
//				fwnum2.append(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+":"+returnSJZ);
//				fwnum2.newLine();
//				fwnum2.flush();
//				fwnum2.close();
				//end20170606记录回复报文
				
				
		            	
		        //将状态发送给主站....
	    		newSjz = state;
		        
		            	
		            	
		    }else{
		            //-----------非链路检测-------------
		    		//判断CON
//		    	System.out.println("1-------"+s_csdata);
		    		String s_seq = s_csdata.substring(14, 16);
		    	    String s_con = Util.hexStrToBinStr(s_seq,1);
		    	    s_con = s_con.substring(3,4);
		    	    if(s_con.equals("1")){
		    	    	//响应终端
		    	    	returnSJZ = "683200320068";
		    	       	returnCsData = "00";//C
		    	        returnCsData += s_csdata.substring(2,12);//地址域
		    	        returnCsData += "00";//AFN=00,确认/否认
		    	        returnCsData += "6" + s_csdata.substring(15, 16);//SEQ
//		    	        returnCsData += "6" + (s_csdata.substring(15, 16).equals("C")?"D":s_csdata.substring(15, 16));//SEQ
		    	        returnCsData += "00000100";
		    	        		
		    	        returnCs = Util.getCS(returnCsData);
		    	        		
		    	        returnSJZ += returnCsData + returnCs + "16";

		    	        if(gylx.equals("3")){
		    		        //包浙规头
		    		        returnSJZ = Util.addSG(s_xzqxm, s_zddz, returnSJZ);
		    	        }
		    	        
		    	        //包前导
		    	        returnSJZ = Util.addFront(returnSJZ, gylx,txfs);
		    	        
		    	      //测试记录--------------------------------------------------------------
				    	// 行政区县码
						
//						 String pnfn=s_csdata.substring(16, 24);
//						 if(pnfn.equalsIgnoreCase("00000101")){
//							 String zdh=s_csdata.substring(24, 28);
//							// 当前段号
//							String s_dqdh = s_csdata.substring(28, 32);
//							s_dqdh = Util.convertStr(s_dqdh);
//							Integer dqdh;
//							s_dqdh = Util.hexStrToDecStr(s_dqdh);
//							dqdh = Integer.parseInt(s_dqdh);
//								
							 
//							File filenum = new File(file_url+s_xzqxm+s_zddz+"_readFromFront_num"+".txt");
//							BufferedWriter fwnum = null;
//							fwnum = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum, true), "UTF-8")); //
//							//指定编码格式，以免读取时中文字符异常
//							fwnum.append(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+":"+zdh+"----"+dqdh);
//							fwnum.newLine();
//							fwnum.flush();
//							fwnum.close();
							
							//20170606记录回复报文
					        
//					        File filenum1 = new File(file_url+s_xzqxm+s_zddz+"_SendHexToFront"+".txt");
//							BufferedWriter fwnum1 = null;
//							fwnum1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum1, true), "UTF-8")); //
//							//指定编码格式，以免读取时中文字符异常
//							fwnum1.append(DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss")+":"+returnSJZ);
//							fwnum1.newLine();
//							fwnum1.flush();
//							fwnum1.close();
							//end20170606记录回复报文
//						 }
						
						//end测试记录--------------------------------------------------------------
		    	        
		    	        //回复通信前置机
		    	        communicateWithFront.SendHexToFront(returnSJZ);
		    	    }
		    	    
		            //发送给主站....
		            newSjz = sjz;
		    }
		     DataObject data = new DataObject("up",newSjz);
		     data.xzqxm = s_xzqxm;
		     data.zddz = s_zddz;
		     data.txfs = txfs;
		     data.gylx = gylx;
		     
		     return data;
	  }else{
		  return null;
	  }
//	  System.out.println("2------------"+sjz);

	
  }
}
