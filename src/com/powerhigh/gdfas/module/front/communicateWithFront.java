package com.powerhigh.gdfas.module.front;

import java.io.*;
import java.net.*;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.util.*;

/**
 * Description: ��ǰ�û�ͨѶ��<p>
 * Copyright:    Copyright   2004 <p>
 * ��дʱ��: 2015-04-05
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class communicateWithFront {

	private static final String resource = "log4j.properties";
    private static Category cat =
                    Category.getInstance(com.powerhigh.gdfas.module.front.communicateWithFront.class);
   
    
  public static String frontIP = "192.168.0.23";
  public static int frontPort = 10000;
  public static String ping = "FE0000000000000100";
  
  private static Socket socketToFront;
  private static OutputStream out;
  private static InputStream in;

  private static boolean isConnected = false;

  /**
   *������飺���캯��
   *@param
   *@return
   */
  public communicateWithFront() {

  }

  /**
   *������飺��ʼ��
   *@return  1:�ɹ���-1��ʧ��
   */
  public static synchronized int Initialize() {
    int InitSuccess = 1;
    int InitFail = -1;
    try {
      //1��������ǰ�û���socket����
      try{
        socketToFront = new Socket(frontIP,frontPort);

      }catch(Exception e1){
        isConnected = false;
        cat.error("��ǰ�û������쳣:",e1);
        return InitFail;
      }
      //2��ȡ�ø�Socket���������������
      try {
        in = socketToFront.getInputStream();
        out = socketToFront.getOutputStream();
      }
      catch (Exception e2) {
        isConnected = false;
        cat.error("��ǰ�û������쳣:",e2);
        return InitFail;
      }

    }
    catch (Exception e3) {
      isConnected = false;
      cat.error("��ǰ�û������쳣:",e3);
      return InitFail;
    }

    isConnected = true;

    //5�����سɹ�
    return InitSuccess;
  }

  public static synchronized boolean isConnected(){
  	//cat.info("isConnected():"+isConnected);
    return isConnected;

  }

  public static synchronized void setIsConnected(boolean flag){
  	
    isConnected = flag;
    //cat.info("setIsConnected():"+isConnected);
 }

  /**
   *������飺�����ݷ��͵�ǰ�û���ASCII�룩
   *@param  Msg String �ַ���
   *@return return iReturn boolean (�ɹ�:true;ʧ��:�׳��쳣)
   */
  public static synchronized boolean SendAsciiToFront(String Msg) throws Exception{

    try {
      int i = 0;
      //�ж������Ƿ�����
      if(!isConnected()){
        i = Initialize();
      }
      if(i == -1){
        isConnected = false;
        throw new Exception("SendAsciiToFront:��ǰ�û������쳣!");
      }

      out.write(Msg.getBytes());
      isConnected = true;
      return true;
    }
    catch (Exception e) {
      isConnected = false;
      throw e;
    }
  }

  /**
   *������飺�����ݷ��͵�ǰ�û���ʮ�������ַ���
   *@param  Msg String ʮ�������ַ�
   *@return iReturn boolean (�ɹ�:true;ʧ��:�׳��쳣)
   */
  public static synchronized boolean SendHexToFront(String Msg) throws Exception{

    try {
    		  
      int i = 0;
      //�ж������Ƿ�����
      if(!isConnected()){
        i = Initialize();
      }
      if(i == -1){
        isConnected = false;
        throw new Exception("SendHexToFront:��ǰ�û������쳣!");
      }
      byte[] bt = Util.str2bytes(Msg);
      out.write(bt);
      cat.info("[toFront]send data:"+Msg);
      //2017-05-18�����ظ��ն˵Ŀ���̨��ӡ
//      System.out.println("[toFront]send data:"+Msg);
      isConnected = true;
      return true;
    }
    catch (Exception e) {
      isConnected = false;
      throw e;
    }
  }



  /**
   *������飺�����ݷ��͵�ǰ�û�������������
   *@param  Msg String ��������
   *@return iReturn boolean (�ɹ�:true;ʧ��:�׳��쳣)
   */
  public static synchronized boolean SendByteToFront(byte[] bt) throws Exception{

    try {
      int i = 0;
      //�ж������Ƿ�����
      if(!isConnected()){
        i = Initialize();
      }
      if(i == -1){
        isConnected = false;
        throw new Exception("SendByteToFront:��ǰ�û������쳣!");
      }


      out.write(bt);
      isConnected = true;
      return true;
    }
    catch (Exception e) {
      isConnected = false;
      throw e;
    }
  }


  /**
   *������飺�õ�Socket��������
   *@param
   *@return InputStream
   */
  public static synchronized InputStream getInputStream() {
    return in;
  }



  /**
   *������飺�ر���ǰ�û�������
   *@param
   *@return  void
   */
  public static synchronized void Close() {
    try{
      socketToFront.close();
    }catch(Exception e){
        cat.error("��ǰ�û������쳣:",e);
    }
  }
}
