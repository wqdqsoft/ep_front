package com.powerhigh.gdfas.module.front;

import java.io.*;
import java.net.*;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.util.*;

/**
 * Description: 与前置机通讯；<p>
 * Copyright:    Copyright   2004 <p>
 * 编写时间: 2015-04-05
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
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
   *方法简介：构造函数
   *@param
   *@return
   */
  public communicateWithFront() {

  }

  /**
   *方法简介：初始化
   *@return  1:成功；-1：失败
   */
  public static synchronized int Initialize() {
    int InitSuccess = 1;
    int InitFail = -1;
    try {
      //1、建立与前置机的socket连接
      try{
        socketToFront = new Socket(frontIP,frontPort);

      }catch(Exception e1){
        isConnected = false;
        cat.error("与前置机连接异常:",e1);
        return InitFail;
      }
      //2、取得该Socket的输入流和输出流
      try {
        in = socketToFront.getInputStream();
        out = socketToFront.getOutputStream();
      }
      catch (Exception e2) {
        isConnected = false;
        cat.error("与前置机连接异常:",e2);
        return InitFail;
      }

    }
    catch (Exception e3) {
      isConnected = false;
      cat.error("与前置机连接异常:",e3);
      return InitFail;
    }

    isConnected = true;

    //5、返回成功
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
   *方法简介：将数据发送到前置机（ASCII码）
   *@param  Msg String 字符串
   *@return return iReturn boolean (成功:true;失败:抛出异常)
   */
  public static synchronized boolean SendAsciiToFront(String Msg) throws Exception{

    try {
      int i = 0;
      //判断连接是否正常
      if(!isConnected()){
        i = Initialize();
      }
      if(i == -1){
        isConnected = false;
        throw new Exception("SendAsciiToFront:与前置机连接异常!");
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
   *方法简介：将数据发送到前置机（十六进制字符）
   *@param  Msg String 十六进制字符
   *@return iReturn boolean (成功:true;失败:抛出异常)
   */
  public static synchronized boolean SendHexToFront(String Msg) throws Exception{

    try {
    		  
      int i = 0;
      //判断连接是否正常
      if(!isConnected()){
        i = Initialize();
      }
      if(i == -1){
        isConnected = false;
        throw new Exception("SendHexToFront:与前置机连接异常!");
      }
      byte[] bt = Util.str2bytes(Msg);
      out.write(bt);
      cat.info("[toFront]send data:"+Msg);
      //2017-05-18新增回复终端的控制台打印
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
   *方法简介：将数据发送到前置机（二进制流）
   *@param  Msg String 二进制流
   *@return iReturn boolean (成功:true;失败:抛出异常)
   */
  public static synchronized boolean SendByteToFront(byte[] bt) throws Exception{

    try {
      int i = 0;
      //判断连接是否正常
      if(!isConnected()){
        i = Initialize();
      }
      if(i == -1){
        isConnected = false;
        throw new Exception("SendByteToFront:与前置机连接异常!");
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
   *方法简介：得到Socket的输入流
   *@param
   *@return InputStream
   */
  public static synchronized InputStream getInputStream() {
    return in;
  }



  /**
   *方法简介：关闭与前置机的连接
   *@param
   *@return  void
   */
  public static synchronized void Close() {
    try{
      socketToFront.close();
    }catch(Exception e){
        cat.error("与前置机连接异常:",e);
    }
  }
}
