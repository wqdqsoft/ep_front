package com.powerhigh.gdfas.module;

import java.io.*;
import javax.comm.*;

/**
 * Description: 初始化串口；<p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-6-20
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class SerialBean {
  private CommPortIdentifier portId;
  private SerialPort serialPort;
  private OutputStream out;
  private InputStream in;
  public int port;	//端口号
  private int btl;	//波特率
  private int sjw;	//数据位(5;6;7;8)
  private int tzw;	//停止位(1；2)
  private int jyw;	//校验位(0:无；1：奇；2：偶)

  /**
	*方法简介：构造函数
	*@param  port  int 串口
	*@param  btl  int 波特率
	*@param  sjw  int 数据位
	*@param  tzw  int 停止位
	*@param  jyw  int 校验位
	*@return
	*/
  public SerialBean(int port,int btl,int sjw,int tzw,int jyw){
    this.port = port;
    this.btl = btl;
    this.sjw = sjw;
    this.tzw = tzw;
    this.jyw = jyw;
  }

  /**
   *方法简介：构造函数
   *@param
   *@return
   */
  public SerialBean() {

  }

  /**
   *方法简介：初始化串口
   *@param  PortID  int 串口1：1，串口2：2，......
   *@return  1:成功；-1：失败
   */
  public int Initialize() {
    int InitSuccess = 1;
    int InitFail = -1;
    try {
    	
    	//1、打开串口
    	String PortName = "COM" + this.port;
    	System.out.println("55----"+CommPortIdentifier.getPortIdentifiers());
    	portId = CommPortIdentifier.getPortIdentifier(PortName);
    	//System.out.println("portName="+portId.getName());
    	if(portId.getPortType() != CommPortIdentifier.PORT_SERIAL){
    		System.out.print(portId.getName()+"is not PORT_SERIAL!");
    	}
    	try {
    		serialPort = (SerialPort)
	    			portId.open("CM_Serial", 2000);
    	}catch(PortInUseException e){
    		e.printStackTrace();
    		return InitFail;
    	}
      
    	//2、取得该串口的输入流和输出流
    	try {
    		this.in = serialPort.getInputStream();
    		this.out = serialPort.getOutputStream();
    	}catch(IOException e){
    		e.printStackTrace();
    		return InitFail;
    	}
      
    	//3、初始化串口参数( 19200, 8, 1, none )
    	try {
      	
    		serialPort.setSerialPortParams(this.btl,this.sjw,this.tzw,this.jyw);
    		
      	
    	}catch (UnsupportedCommOperationException e){
    		e.printStackTrace();
    		//关闭串口资源
//    		try{
//	    		this.in.close();
//	    		this.out.close();
//	    		serialPort.close();
//    		}catch(Exception ex){
//    			ex.printStackTrace();
//    		}
    		//return InitFail;
    	}
      
    }catch (NoSuchPortException e) {
    	e.printStackTrace();
    	return InitFail;
    }


    //4、返回成功
    return InitSuccess;
  }


  /**
   *方法简介：往串口中写数据
   *@param  Msg String  需要写入串口的字符串
   *@return void
   */
  public void WritePort(String Msg) throws Exception{
    try{
    	    	
    	this.out.write(Msg.getBytes());
      
    }catch(Exception e){
    	throw e;
    }
  }

  /**
  *方法简介：往串口中写数据
  *@param  bt byte[]  需要写入串口的字串
  *@return void
  */
 public void WritePort(byte[] bt) throws Exception {
   try{
   	
     this.out.write(bt);
     
   }catch(Exception e){	
     throw e;
   }
 }

  /**
   *方法简介：得到串口的输入流
   *@param
   *@return InputStream
   */
  public  InputStream getInputStream() {
    return this.in;
  }
  
  /**
   *方法简介：从串口接收数据(十六进制字符)
   *
   *@return s String 十六进制码字符
   */
  public  byte[] readHex() {
	  	byte[] bt = null;
	  	byte[] bt_return = null;
	    try {
	      bt = new byte[1024];
	      int tempi = (this.in).read(bt);
	      if(tempi == -1){
	        return null;
	      }
	      bt_return = new byte[tempi];
	      for(int i=0;i<tempi;i++){
	      	bt_return[i] = bt[i];
	      }

	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      return null;
	    }
	    return bt_return;
  }
  
  /**
   *方法简介：关闭串口
   *@param
   *@return  void
   */
  public void Close() throws Exception{
  	if(this.in != null){
  		try{
  			this.in.close();
  			this.in = null;
  		}catch(Exception e1){
  			throw e1;
  		}
  	}
  	
  	if(this.out != null){
  		try{
  			this.out.close();
  			this.out = null;
  		}catch(Exception e2){
  			throw e2;
  		}
  	}
  	
  	if(this.serialPort != null){
  		try{
  			this.serialPort.close();
  			this.serialPort = null;
  		}catch(Exception e3){
  			throw e3;
  		}
  	}
  }
}
