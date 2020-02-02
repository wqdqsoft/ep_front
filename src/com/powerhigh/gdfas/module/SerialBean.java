package com.powerhigh.gdfas.module;

import java.io.*;
import javax.comm.*;

/**
 * Description: ��ʼ�����ڣ�<p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-6-20
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class SerialBean {
  private CommPortIdentifier portId;
  private SerialPort serialPort;
  private OutputStream out;
  private InputStream in;
  public int port;	//�˿ں�
  private int btl;	//������
  private int sjw;	//����λ(5;6;7;8)
  private int tzw;	//ֹͣλ(1��2)
  private int jyw;	//У��λ(0:�ޣ�1���棻2��ż)

  /**
	*������飺���캯��
	*@param  port  int ����
	*@param  btl  int ������
	*@param  sjw  int ����λ
	*@param  tzw  int ֹͣλ
	*@param  jyw  int У��λ
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
   *������飺���캯��
   *@param
   *@return
   */
  public SerialBean() {

  }

  /**
   *������飺��ʼ������
   *@param  PortID  int ����1��1������2��2��......
   *@return  1:�ɹ���-1��ʧ��
   */
  public int Initialize() {
    int InitSuccess = 1;
    int InitFail = -1;
    try {
    	
    	//1���򿪴���
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
      
    	//2��ȡ�øô��ڵ��������������
    	try {
    		this.in = serialPort.getInputStream();
    		this.out = serialPort.getOutputStream();
    	}catch(IOException e){
    		e.printStackTrace();
    		return InitFail;
    	}
      
    	//3����ʼ�����ڲ���( 19200, 8, 1, none )
    	try {
      	
    		serialPort.setSerialPortParams(this.btl,this.sjw,this.tzw,this.jyw);
    		
      	
    	}catch (UnsupportedCommOperationException e){
    		e.printStackTrace();
    		//�رմ�����Դ
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


    //4�����سɹ�
    return InitSuccess;
  }


  /**
   *������飺��������д����
   *@param  Msg String  ��Ҫд�봮�ڵ��ַ���
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
  *������飺��������д����
  *@param  bt byte[]  ��Ҫд�봮�ڵ��ִ�
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
   *������飺�õ����ڵ�������
   *@param
   *@return InputStream
   */
  public  InputStream getInputStream() {
    return this.in;
  }
  
  /**
   *������飺�Ӵ��ڽ�������(ʮ�������ַ�)
   *
   *@return s String ʮ���������ַ�
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
   *������飺�رմ���
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
