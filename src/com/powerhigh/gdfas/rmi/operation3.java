package com.powerhigh.gdfas.rmi;

import java.util.*;

import org.apache.log4j.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.powerhigh.gdfas.rmi.parse;


/**
 * Description: 数据下行处理类 <p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-2
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class operation3 {
	 
  private static parse p = null;
  //加载日志
  private static final String resource = "log4j.properties";
  private static Category cat =
      Category.getInstance(com.powerhigh.gdfas.rmi.operation3.class);
//  static {
//    PropertyConfigurator.configure(resource);
//  }

  private static ApplicationContext ctx = null;
  //Construct
  public operation3() {

  }



  private static parse getParse() throws Exception{
//  	if(p == null){
//  		ApplicationContext ctx = 
//			new FileSystemXmlApplicationContext("rmi-client.xml");
//		p = (parse)ctx.getBean("myServiceClient");
//  	}
//  	if(ctx == null){
//  		ctx = new FileSystemXmlApplicationContext("epfront-client.xml");
////  	}
//  	
//  	p = (parse)ctx.getBean("myServiceClient");
	  
//	  ctx = new ClassPathXmlApplicationContext("epfront-client.xml");
//      p = (parse)ctx.getBean("myServiceClient");
      
      
	  if(p==null){
			ctx = new ClassPathXmlApplicationContext("epfront-client.xml");
			p = (parse) ctx.getBean("myServiceClient3");
	  }
  	  return p;
  }
  
  /**
	 * 方法简述：查询终端参数配置(AFN=0AH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param ksxh
	 *            int 开始装置序号
	 * @param jsxh
	 *            int 结束装置序号
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public static String sendAFN0AF10(String txfs, String xzqxm, String zddz, 
			int ksxh,int jsxh)
			throws Exception {
		 try{  	 
			  return getParse().sendAFN0AF10(txfs,xzqxm,zddz,ksxh,jsxh);
		  }catch(Exception e){
			  e.printStackTrace();
			  return null;
		  }
	}
	
  /**方法简述：终端复位：F1硬件初始化(AFN=01H)
   * @param xzqxm 		String 	行政区县码
   * @param zddz  		String 	终端地址
   * @param csz			String  参数值(预留)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN01F1(String txfs,String xzqxm, String zddz,
		  				     String csz){
	  try{  	 
		  return getParse().sendAFN01F1(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  

  /**方法简述：终端复位：F2数据区初始化(AFN=01H)
   * @param xzqxm 		String 	行政区县码
   * @param zddz  		String 	终端地址
   * @param csz			String  参数值(预留)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN01F2(String txfs,String xzqxm, String zddz,
		  				     String csz){
	  try{  	 
		  return getParse().sendAFN01F2(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  

  /**方法简述：终端复位：F3参数及全体数据区初始化（即恢复至出厂配置）(AFN=01H)
   * @param xzqxm 		String 	行政区县码
   * @param zddz  		String 	终端地址
   * @param csz			String  参数值(预留)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN01F3(String txfs,String xzqxm, String zddz,
		  				     String csz){
	  try{  	 
		  return getParse().sendAFN01F3(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  

  /**方法简述：终端复位：F4参数（除与系统主站通信有关的）及全体数据区初始化(AFN=01H)
   * @param xzqxm 		String 	行政区县码
   * @param zddz  		String 	终端地址
   * @param csz			String  参数值(预留)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN01F4(String txfs,String xzqxm, String zddz,
		  				     String csz){
	  try{  	 
		  return getParse().sendAFN01F4(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  
  /**方法简述：终端通信参数设置F1(AFN=04H)
   * @param xzqxm 		String 	行政区县码
   * @param zddz  		String 	终端地址
   * @param csz			String  参数值(cs1;cs2;cs3;cs4;cs5;cs6;cs7)
   * 					cs1:数传机延时时间,单位:20ms
   * 					cs2:作为启动站允许发送传输延时时间,单位:分钟
   * 					cs3:等待从动站响应的超时时间,0-4095,单位:秒
   * 					cs4:重发次数,0-3;0表示不允许重发
   * 					cs5:1类数据自动上报的确认标志,1表示允许终端需要主站确认
   * 					cs6:2类数据自动上报的确认标志,1表示允许终端需要主站确认
   * 					cs7:3类数据自动上报的确认标志,1表示允许终端需要主站确认
   * 					cs8:心跳周期:1-60分
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F1(String txfs,String xzqxm, String zddz,
		  				     String csz){
	  try{  	 
		  return getParse().sendAFN04F1(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：主站IP地址和端口设置F3(AFN=04H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param csz  		String 		参数值(cs1;cs2;cs3)
   * 					cs1:主用IP(xxx.xxx.xxx.xxx:nnnnn)
   * 					cs2:备用IP(xxx.xxx.xxx.xxx:nnnnn)
   * 					cs3:APN(16字节；ASCII;低位补00H；按正序传)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F3(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F3(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：主站电话号码和短信中心号码设置F4(AFN=04H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param csz  		String 		参数值(cs1;cs2)
   * 					cs1:主站电话号码
   * 					cs2:短信中心号码
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F4(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F4(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**
	 * 方法简述：风机水泵控制参数F5(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;cs2;cs3;cs4;cs5)
	 *            cs1:风机开启相对于水泵开启的提前量 单位：分钟
	 *            cs2:风机水泵自动控制允许标志 0x55允许自动控制；0xAA禁止自动控制
	 *            cs3:污泥回流泵自动控制允许标志   0x55允许自动控制；0xAA禁止自动控制
	 *            cs4:污泥回流泵周期运行开启时长 单位：分钟
	 *            cs5:污泥回流泵周期运行停止时长 单位：分钟
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
  public static String sendAFN04F5(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F5(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端组地址设置F6(AFN=04H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param csz  		String 		参数值(cs1;...;cs8)
   * 					cs1:组地址1(组地址为0时，表示不设组地址)
   * 					...
   * 					cs8:组地址8(组地址为0时，表示不设组地址)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F6(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F6(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端IP地址和端口 F7(AFN=04H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param csz  		String 		参数值(cs1;...;cs9)
   * 					cs1:终端IP地址(xxx.xxx.xxx.xxx)
   * 					cs2:子网掩码地址(xxx.xxx.xxx.xxx)
   * 					cs3:网关地址(xxx.xxx.xxx.xxx)
   * 					cs4:代理类型(0~3,依次表示;不使用代理、http connect代理、socks4代理、socks5代理)
   * 					cs5:代理服务器地址及端口号(xxx.xxx.xxx.xxx:nnnnn)
   * 					cs6:代理服务器连接方式(0~1,依次表示:无需验证、需要用户名/密码)
   * 					cs7:用户名(ASCII)
   * 					cs8:密码(ASCII)
   * 					cs9:终端侦听端口号(nnnnn)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F7(String txfs,String xzqxm, String zddz, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F7(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端上行通信工作方式F8(AFN=04H)
   * @param 	xzqxm 	String 行政区县码
   * @param 	zddz  	String 终端地址
   * @param 	csz  	String 参数值(cs1;...;cs7)
   * 					cs1:TCP/UDP(0:TCP;1:UDP)
   * 					cs2:工作模式(1:混合模式;2:客户机模式;3:服务器模式)
   * 					cs3:在线模式(1:永久在线模式;2:被动激活模式;3:时段在线模式)
   * 					cs4:重拨间隔(单位:秒,取值0~65535)--永久在线模式、时段在线模式
   * 					cs5:重拨次数(0~255)--被动激活模式
   * 					cs6:无通信自动断线时间(单位:min,0~255)--被动激活模式
   * 					cs7:允许在线时段标志(0-23点,中间以"#"隔开)--时段在线模式
   * 						比如"0#3#5"表示0点、3点和5点允许在线
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F8(String txfs,String xzqxm, String zddz, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F8(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：水泵水位控制参数（针对收集池智能终端）F9(AFN=04H)
   * @param 	xzqxm 	String 行政区县码
   * @param 	zddz  	String 终端地址
   * @param 	csz  	String 参数值(cs1;cs2;cs3;cs4)
   * 				   cs1:主备切换设置1-使用1号水泵2-使用2号水泵3- 1号2号互为主
					   cs2:主备切换时间  一个字节 小时
					   cs3:启动时间 两个字节 分钟
					   cs4:停止时间   两个字节 分钟';
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F9(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F9(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端电能表/交流采样装置参数配置F10(AFN=04H)
   * @param 	xzqxm 	String 	行政区县码
   * @param 	zddz  	String 	终端地址
   * @param 	csz  	String 参数值(cs1;...;csn)--N个测量点的ID						
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F10(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F10(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端脉冲参数配置F11(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	csz		String 		参数值(cs1;...;csn)--N个脉冲配置
   * 					csn:电能表配置(pz1#pz2#pz3#pz4)
   * 						pz1:输入端口号
   * 						pz2:所属测量点(1-64)
   * 						pz3:脉冲属性(0~3依次表示正向有功、正向无功、反向有功、反向无功)
   * 						pz4:电表常数
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F11(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F11(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：开关量类输入设备装置配置参数F12(AFN=04H)
   * @param xzqxm		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param csz  		String 参数值(cs1;...;csn)--N个配置的ID		
   * 					
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F12(String txfs,String xzqxm, String zddz, 
  								String csz) {
	  try{  	 
		  return getParse().sendAFN04F12(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：电气设备控制点参数F13(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	csz		String 参数值(cs1;...;csn)--N个配置的ID	
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F13(String txfs,String xzqxm, String zddz,
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F13(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**
	 * 方法简述：电气设备启停控制参数F14(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号           
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个电气设备启停控制参数配置
	 *            csn:总加组配置(pz1,...,pz7)--N电气设备启停控制参数配置 
	 *                pz1:第n套控制参数执行依据
	 *                pz2:第n套控制参数最小温度
	 *                pz3:第n套控制参数最大温度
	 *                pz4:第n套控制参数生效起始日期
	 *                pz5:第n套控制参数生效截止日期
	 *                pz6:第n套控制参数启动时长
	 *                pz7:第n套控制参数停止时长
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
  public static String sendAFN04F14(String txfs,String xzqxm, String zddz,String cldh, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F14(txfs,xzqxm,zddz,cldh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**
	 * 方法简述：水泵水位控制参数F15(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号           
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个水泵水位控制参数配置
	 *            csn:控制参数配置(pz1,...,pz14)--第N水泵水位控制参数配置 
	 *                pz1:第n套控制参数-输出硬件号
	 *                pz2:第n套控制参数-备用输出硬件号
	 *                pz3:第n套控制参数-同时工作使能
	 *                pz4:第n套控制参数-主备切换时间
	 *                pz5:第n套控制参数-池体号码
	 *                pz6:第n套控制参数-水位档位
	 *                pz7:第n套控制参数-逻辑关系
	 *                pz8:第n套控制参数-池体号码(另一个池体)
	 *                pz9:第n套控制参数-水位档位(另一个池体)
	 *                pz10:第n套控制参数-控制动作
	 *                pz11:第n套控制参数-最小温度
	 *                pz12:第n套控制参数-最大温度
	 *                pz13:第n套控制参数-启动时间
	 *                pz14:第n套控制参数-停止时间
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public static String sendAFN04F15(String txfs, String xzqxm, String zddz,String cldh,
			String csz){
		 try{  	 
			  return getParse().sendAFN04F15(txfs,xzqxm,zddz,cldh,csz);
		  }catch(Exception e){
			  e.printStackTrace();
			  return null;
		  }
	}
  
	/**
	 * 方法简述：风机电磁阀控制参数F16(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号           
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个风机控制参数配置
	 *            csn:控制参数配置(pz1,...,pz10)--第N台风机控制参数配置 
	 *                pz1:第n套控制参数-输出硬件号
	 *                pz2:第n套控制参数-备用输出硬件号
	 *                pz3:第n套控制参数-主备切换时间
	 *                pz4:第n套控制参数-与水泵联动使能
	 *                pz5:第n套控制参数-最小温度
	 *                pz6:第n套控制参数-最大温度
	 *                pz7:第n套控制参数-启动时间
	 *                pz8:第n套控制参数-停止时间
	 *                pz9:第n套控制参数-频率
	 *                pz10:第n套控制参数-控制动作
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public static String sendAFN04F16(String txfs, String xzqxm, String zddz,String cldh,
			String csz){
		 try{  	 
			  return getParse().sendAFN04F16(txfs,xzqxm,zddz,cldh,csz);
		  }catch(Exception e){
			  e.printStackTrace();
			  return null;
		  }
	}
	
	/**
	 * 方法简述：F17：ORP,HP 上下限设置F17(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--	NORP,HP 上下限设置的ID  N<=5
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public static String sendAFN04F17(String txfs, String xzqxm, String zddz,
			String csz){
		 try{  	 
			  return getParse().sendAFN04F17(txfs,xzqxm,zddz,csz);
		  }catch(Exception e){
			  e.printStackTrace();
			  return null;
		  }
	}
	
	/**
	 * 方法简述：F18：超声波水位上下限设置(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--	站点池体的ID  N<=4
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public static String sendAFN04F18(String txfs, String xzqxm, String zddz,
			String csz){
		 try{  	 
			  return getParse().sendAFN04F18(txfs,xzqxm,zddz,csz);
		  }catch(Exception e){
			  e.printStackTrace();
			  return null;
		  }
	}
  
  /**方法简述：测量点基本参数配置F25(AFN=04H)
   * @param 	xzqxm 	String 	行政区县码
   * @param 	zddz  	String 	终端地址
   * @param 	cldh  	int 	测量点号
   * @param 	csz  	String 	参数值(cs1;cs2)
   * 					cs1:PT
   * 					cs2:CT
   * 					
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   * @throws Exception
   */
  public static String sendAFN04F25(String txfs,String xzqxm, String zddz, 
		  int cldh,String csz){
	  try{  	 
		  return getParse().sendAFN04F25(txfs,xzqxm,zddz,cldh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：神经网络算法使能F26(AFN=04H)
   * @param 	xzqxm	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	csz  	String		参数值 55使能  AA不使能
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F26(String txfs,String xzqxm, String zddz, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F26(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  /**方法简述：神经网络训练样本参数F27(AFN=04H)
   * @param 	xzqxm	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	csz  	String		参数值 cs1;...;cs3
   *                                    其中cs1：温度
   *                                       cs2:ORP
   *                                       cs3:风机频率
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F27(String txfs,String xzqxm, String zddz, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F27(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：测量点功率因数分段限值设置F28(AFN=04H)
   * @param 	xzqxm 		String 		行政区县码
   * @param 	zddz  		String 		终端地址
   * @param 	cldh  		String 		测量点号
   * @param 	csz  		String 		参数值(cs1;cs2)
   * 						cs1:功率因数分段限值1(pz1#pz2)
   * 							pz1:符号(0:正;1:负)
   * 							pz2:限值
   * 						cs2:功率因数分段限值2(pz1#pz2)
   * 							pz1:符号(0:正;1:负)
   * 							pz2:限值
   * 							
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F28(String txfs,String xzqxm, String zddz,
		  String cldh,String csz) {
	  try{  	 
		  return getParse().sendAFN04F28(txfs,xzqxm,zddz,cldh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端抄表运行参数设置F33(AFN=04H)
   * @param 	xzqxm 	String 	行政区县码
   * @param 	zddz  	String 	终端地址
   * @param 	csz  	String 	参数值(cs1;...;csn)--N个参数块
   * 					csn:参数块(pz1#...#pz7)
   * 						pz1:终端通信端口号(1个字节,1-31)
   * 						pz2:台区集中抄表运行控制字(2个字节,D5D4D3D2D1D0,如：100101,D15-D6为备用)
   *							D5->是否要求终端抄读“电表状态字”(1:要求;0:不要求)
   *							D4->是否要求终端搜寻新增或更换的电表(1:要求;0:不要求)
   *							D3->是否要求终端定时对电表广播校时(1:要求;0:不要求)
   *							D2->要求终端采用广播冻结抄表(1:要求;0:不要求)
   *							D1->是否要求终端只抄重点表(1:要求;0:抄所有表)
   *							D0->是否允许自动抄表(1:不允许自动抄表;0: 要求终端根据抄表时段自动抄表)
   *						pz3:允许抄表时段(12个字节,D95D94...D1D0,如:10...11)
   *							D95->时段23:45~24:00自动抄表状态(1:不允许自动抄表;0:允许)
   *							D94->时段23:30~23:45自动抄表状态(1:不允许自动抄表;0:允许)
   *							...
   *							D1->时段00:15~00:30自动抄表状态(1:不允许自动抄表;0:允许)
   *							D0->时段00:00~00:15自动抄表状态(1:不允许自动抄表;0:允许)
   *						pz4:抄表日-日期(4个字节,D30D29...D1D0,如:11...01,D31备用)
   *							D30->每月31日的抄表状态(1:有效;0:无效)
   *							D29->每月30日的抄表状态(1:有效;0:无效)
   *							...
   *							D1->每月2日的抄表状态(1:有效;0:无效)
   *							D0->每月1日的抄表状态(1:有效;0:无效)
   *						pz5:抄表日-时间(2个字节,时分，hhmm,如:0930表示9点30分)
   *						pz6:终端抄表间隔(1个字节,1-60)
   *						pz7:对电表广播校时定时时间(3个字节，日时分,ddhhmm,当日为00时表示每天校时,如000930表示每天9点30分校时)
   *							
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   * @throws Exception
   */
  public static String sendAFN04F33(String txfs,String xzqxm, String zddz, 
		  				String csz) {
	  try{  	 
		  return getParse().sendAFN04F33(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：与终端接口的通信模块的参数设置F34(AFN=04H)
   * @param 	xzqxm 	String 	行政区县码
   * @param 	zddz  	String 	终端地址
   * @param 	csz  	String 	参数值(cs1;...;csn)--N个参数块
   * 					csn:参数块(pz1#...#pz7)
   * 						pz1:终端通信端口号(1个字节,1-31)
   * 						pz2:通信波特率(0-7分别表示300,600,1200,2400,4800,7200,9600,19200)
   * 						pz3:停止位(0:1位停止位;1:2位停止位)
   * 						pz4:有无校验(0:无;1:有)
   * 						pz5:奇偶校验(0:偶;1:奇)
   * 						pz6:位数(0~3分别表示5-8位)
   *						pz7:与终端接口对应端的通信速率
   *							
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   * @throws Exception
   */
  public static String sendAFN04F34(String txfs,String xzqxm, String zddz, 
		  				String csz) {
	  try{  	 
		  return getParse().sendAFN04F34(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端上行通信流量门限设置F36(AFN=04H)
   * @param 	xzqxm 	String 行政区县码
   * @param 	zddz  	String 终端地址
   * @param 	csz  	String 参数值(cs1)
   * 					cs1:月通信流量门限(0表示系统不需要终端进行流量控制)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F36(String txfs,String xzqxm, String zddz, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F36(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端声音告警标志设置F57(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	csz  	String 		参数值(cs1) 
   * 					cs1:允许在线时段标志(0-23点,中间以"#"隔开)--时段在线模式
   * 						比如"0#3#5"表示0点、3点和5点允许在线
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F57(String txfs,String xzqxm, String zddz, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F57(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：电能表异常判别阈值设定F59(AFN=04H)
   * @param 	xzqxm		String		行政区县码
   * @param 	zddz  		String 		终端地址
   * @param 	csz  		String 		参数值(cs1;cs2;cs3;cs4)
   * 						cs1:电能量超差阀值(数据格式22,x.x)
   * 						cs2:电能表飞走阀值(数据格式22,x.x)
   * 						cs3:电能表停走阀值(单位:15min)
   * 						cs4:电能表校时阀值(单位:min)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   * @throws Exception
   */
  public static String sendAFN04F59(String txfs,String xzqxm, String zddz,
  				String csz) {
	  try{  	 
		  return getParse().sendAFN04F59(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：谐波限值设置F60(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	csz  	String 		参数值(cs1;...;cs8)
   * 					cs1:总畸变电压含有率上限值(格式05,xxx.x,单位:%)
   * 					cs2:奇次谐波电压含有率上限值(格式05,xxx.x,单位:%)
   * 					cs3:偶次谐波电压含有率上限值(格式05,xxx.x,单位:%)
   * 					cs4:各偶次谐波电压含有率上限值(pz2#pz4#pz6#...#pz18)
   * 					cs5:各奇次谐波电压含有率上限值(pz3#pz5#pz6#...#pz19)
   * 					cs6:总畸变电流有效值上限值(格式06,xx.xx,单位:A)
   * 					cs7:各偶次谐波电流有效值上限值(pz2#pz4#pz6#...#pz18)
   * 					cs8:各奇次谐波电流有效值上限值(pz3#pz5#pz6#...#pz19)
   * 
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F60(String txfs,String xzqxm, String zddz, 
		  String csz) {
	  try{  	 
		  return getParse().sendAFN04F60(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：直流模拟量接入参数F61(AFN=04H)
   * @param 	xzqxm		String 		行政区县码
   * @param 	zddz		String 		终端地址
   * @param 	csz			String 		参数值(cs1)
   * 						cs1:直流模拟量接入标志(1-8路接入标志,1:接入;0:不接入;如：10100001表示第1/2/8路接入)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F61(String txfs,String xzqxm, String zddz,
		  String csz){
	  try{  	 
		  return getParse().sendAFN04F61(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端1类数据任务设置F65(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	rwh  	String 		任务号
   * @param 	csz  	String 		参数值(cs1;...;cs4)
   * 					cs1:上报周期(0-31)
   * 					cs2:上报周期单位(0~3依次表示分、时、日、月)
   * 					cs3:上报基准时间(年月日时分秒,yymmddhhmmss)
   * 					cs4:启用标志(55启用AA禁用)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F65(String txfs,String xzqxm, String zddz, 
		  					String rwh,String csz) {
	  try{  	 
		  return getParse().sendAFN04F65(txfs,xzqxm,zddz,rwh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：终端2类数据任务设置F66(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	rwh  	String 		任务号
   * @param 	csz  	String 		参数值(cs1;...;cs5)
   * 					cs1:上报周期(0-31)
   * 					cs2:上报周期单位(0~3依次表示分、时、日、月)
   * 					cs3:上报基准时间(年月日时分秒,yymmddhhmmss)
   * 					cs4:抽取倍率(1-96)
   * 					cs5:任务数据项(Pm@Fm#...#Pn@Fn)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F66(String txfs,String xzqxm, String zddz, 
		  					String rwh,String csz) {
	  try{  	 
		  return getParse().sendAFN04F66(txfs,xzqxm,zddz,rwh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：1类数据任务启动/停止设置F67(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	rwh  	String 		任务号
   * @param 	csz  	String 		参数值(cs1)
   * 					cs1:任务启动标志(55:启动；AA：停止)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendAFN04F67(String txfs,String xzqxm, String zddz, 
  							String rwh,String csz){
	  try{  	 
		  return getParse().sendAFN04F67(txfs,xzqxm,zddz,rwh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：2类数据任务启动/停止设置F68(AFN=04H)
   * @param 	xzqxm 	String 		行政区县码
   * @param 	zddz  	String 		终端地址
   * @param 	rwh  	String 		任务号
   * @param 	csz  	String 		参数值(cs1)
   * 					cs1:任务启动标志(55:启动；AA：停止)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendAFN04F68(String txfs,String xzqxm, String zddz, 
  							String rwh,String csz){
	  try{  	 
		  return getParse().sendAFN04F68(txfs,xzqxm,zddz,rwh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  							
  
  
  /**方法简述：直流模拟量输入变比F81(AFN=04H)
   * @param 	xzqxm			String 		行政区县码
   * @param 	zddz			String 		终端地址
   * @param 	zlmnldkh		String 		直流模拟量端口号
   * @param 	csz				String 		参数值(cs1;cs2)
   * 							cs1:直流模拟量量程起始值(数据格式02;pz1#pz2#pz3)
   * 								pz1:正负标志(0:正;1:负)
   * 								pz2:系数(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:数值(xxx)
   * 							cs2:直流模拟量量程终止值(数据格式02;pz1#pz2#pz3)
   * 								pz1:正负标志(0:正;1:负)
   * 								pz2:系数(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:数值(xxx)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F81(String txfs,String xzqxm, String zddz,
		  String zlmnldkh,String csz) {
	  try{  	 
		  return getParse().sendAFN04F81(txfs,xzqxm,zddz,zlmnldkh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：直流模拟量输入变比F82(AFN=04H)
   * @param 	xzqxm			String 		行政区县码
   * @param 	zddz			String 		终端地址
   * @param 	zlmnldkh		String 		直流模拟量端口号
   * @param 	csz				String 		参数值(cs1;cs2)
   * 							cs1:直流模拟量上限(数据格式02;pz1#pz2#pz3)
   * 								pz1:正负标志(0:正;1:负)
   * 								pz2:系数(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:数值(xxx)
   * 							cs2:直流模拟量下限(数据格式02;pz1#pz2#pz3)
   * 								pz1:正负标志(0:正;1:负)
   * 								pz2:系数(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:数值(xxx)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F82(String txfs,String xzqxm, String zddz,
		  String zlmnldkh,String csz) {
	  try{  	 
		  return getParse().sendAFN04F82(txfs,xzqxm,zddz,zlmnldkh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**方法简述：直流模拟量冻结参数F83(AFN=04H)
   * @param 	xzqxm			String 		行政区县码
   * @param 	zddz			String 		终端地址
   * @param 	zlmnldkh		String 		直流模拟量端口号
   * @param 	csz				String 		参数值(cs1)
   * 							cs1:直流模拟量冻结密度
   * 								0:表示不冻结
   * 								1:表示15分钟冻结一次
   * 								2:表示30分钟冻结一次
   * 								3:表示60分钟冻结一次
   * 								254:表示5分钟冻结一次
   * 								255:表示1分钟冻结一次
   * 								
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 * @throws Exception
   */
  public static String sendAFN04F83(String txfs,String xzqxm, String zddz,
		  String zlmnldkh,String csz){
	  try{  	 
		  return getParse().sendAFN04F83(txfs,xzqxm,zddz,zlmnldkh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**
	 * 方法简述：遥控跳闸F1(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号(十进制)
	 * @param csz
	 *            String csz 0x33:开启，0xCC:关闭
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
  public static String sendAFN05F1(String txfs,String xzqxm, String zddz,String cldh,
		  String csz){
	  try{  	 
		  return getParse().sendAFN05F1(txfs,xzqxm,zddz,cldh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**
	 * 方法简述：变频器控制F2(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号(十进制)
	 * @param csz
	 *            String csz 运行频率
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
  public static String sendAFN05F2(String txfs,String xzqxm, String zddz,String cldh,String csz){
	  try{  	 
		  return getParse().sendAFN05F2(txfs,xzqxm,zddz,cldh,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**
 	 * 方法简述：变频器控制F2(AFN=05H)
 	 * 
 	 * @param xzqxm
 	 *            String 行政区县码
 	 * @param zddz
 	 *            String 终端地址
 	 * @param cldh
 	 *            String 测量点号(十进制)
 	 * 
 	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
 	 */
   public static String sendAFN05F3(String txfs,String xzqxm, String zddz,String cldh){
 	  try{  	 
 		  return getParse().sendAFN05F3(txfs,xzqxm,zddz,cldh);
 	  }catch(Exception e){
 		  e.printStackTrace();
 		  return null;
 	  }
   }
  

  /**方法简述：允许终端主动上报F29(AFN=05H)
   * @param xzqxm 	String 行政区县码
   * @param zddz  	String 终端地址
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendAFN05F29(String txfs,String xzqxm, String zddz){
	  try{  	 
		  return getParse().sendAFN05F29(txfs,xzqxm,zddz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  

  /**方法简述：终端对时F31(AFN=05H)
   * @param xzqxm 	String 行政区县码
   * @param zddz  	String 终端地址
   * @param csz  	String 日期(格式：yymmddhhmmss，"XX"表示系统时间)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendAFN05F31(String txfs,String xzqxm, String zddz,
		  String csz){
	  try{  	 
		  return getParse().sendAFN05F31(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }

  /**方法简述：终端对时F31(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param rq  	String 日期 XX表示系统时间
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdds(String txfs,String xzqxm, String zddz,String rq) {
  	try{  	 
  	 return getParse().sendZdds(txfs,xzqxm,zddz,rq);
  	}catch(Exception e){
  		e.printStackTrace();
  		return null;
  	}
  }
  
  /**方法简述：复位命令F1/F2/F3(AFN=01H)
   * @param xzqxm 	String 行政区县码
   * @param zddz  	String 终端地址
   * @param fwlx  	String 复位类型(F1/F2/F3)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdfw(String txfs,String xzqxm, String zddz,String fwlx){
	  try{  	 
		  return getParse().sendZdfw(txfs,xzqxm,zddz,fwlx);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }

  /**方法简述：是否允许终端与主站通话设置F27/F35(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sfyx  String 是否允许:1:允许；0：禁止
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendSfyxzdyzzth(String txfs,String xzqxm, String zddz, String sfyx) {
  	try{  	 
  	  return getParse().sendSfyxzdyzzth(txfs,xzqxm,zddz,sfyx);
  	}catch(Exception e){
  	  e.printStackTrace();
  	  return null;
  	 }
  }

  /**方法简述：是否终端剔除投入设置F28/F36(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sftctr  String 是否剔除投入:1:投入；0：剔除
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendSfzdtctr(String txfs,String xzqxm, String zddz, String sftctr) {
  	try{  		
  		return getParse().sendSfzdtctr(txfs,xzqxm,zddz,sftctr);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**方法简述：是否允许终端主动上报设置F29/F37(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sfyxzdsb  String 是否允许主动上报:1:允许；0：禁止
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendSfyxzdzdsb(String txfs,String xzqxm, String zddz, String sfyxzdsb) {
  	try{  		
  		return getParse().sendSfyxzdzdsb(txfs,xzqxm, zddz, sfyxzdsb);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：1/2类数据任务启动/停止设置F67/F68(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param rwlx  String 任务类型（1：1类；2：2类）
   * @param rwh  String 任务号 
   * @param rwqdbz  String 任务启动标志:55:启动；AA：停止
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdrwqybz(String txfs,String xzqxm, String zddz, 
  									String rwlx,String rwh,String rwqybz) {
  	try{  		
  		return getParse().sendZdrwqybz(txfs,xzqxm, zddz, rwlx,rwh, rwqybz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**方法简述：终端保电设置F25(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param bdsj  String 保电时间 数值范围：0－48；单位：0.5h；0表示无限期保电；AA：保电撤出
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdbd(String txfs,String xzqxm, String zddz, String bdsj) {
  	try{  		
  		return getParse().sendZdbd(txfs,xzqxm, zddz, bdsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**方法简述：终端通信参数设置F1(AFN=04H)
   * @param xzqxm 				String 	行政区县码
   * @param zddz  				String 	终端地址
   * @param scjyssj  			String 	数传机延时时间,单位:20ms
   * @param fscsyxyssj  		String 	作为启动站允许发送传输延时时间,单位:分钟
   * @param ddcdzxycssj  		String 	等待从动站响应的超时时间,0-4095,单位:秒
   * @param cfcs  				String 	重发次数,0-3;0表示不允许重发
   * @param zdsbzysjjlqrbz  	String 	主动上报重要事件记录的确认标志,1表示需要主站确认
   * @param zdsbybsjjlqrbz  	String 	主动上报一般事件记录的确认标志,1表示需要主站确认
   * @param xtzq  				String 	心跳周期:1-60分
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdtxcs(String txfs,String xzqxm, String zddz, String scjyssj,
  				String fscsyxyssj,String ddcdzxycssj,String cfcs,
				String zdsbzysjjlqrbz,String zdsbybsjjlqrbz,String xtzq){
  	try{  		
  		return getParse().sendZdtxcs(txfs,xzqxm, zddz, scjyssj,
  				fscsyxyssj,ddcdzxycssj,cfcs,
  				zdsbzysjjlqrbz,zdsbybsjjlqrbz,xtzq);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：主站IP地址和端口设置F2(AFN=04H)
   * @param xzqxm 				String 		行政区县码
   * @param zddz  				String 		终端地址
   * @param zyip  				String[] 	主用IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param byip  				String[] 	备用IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param wgip  				String[] 	网关IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param dlip  				String[] 	代理IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param apn  				String 		APN(16字节；ASCII;低位补00H；按正序传)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZzip(String txfs,String xzqxm, String zddz, String[] zyip,
  			String[] byip,String[] wgip,String[] dlip,String apn){
  	try{  		
  		return getParse().sendZzip(txfs,xzqxm, zddz, zyip,byip,wgip,dlip,apn);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：主站电话号码和短信中心号码设置F4(AFN=04H)
   * @param xzqxm 				String 		行政区县码
   * @param zddz  				String 		终端地址
   * @param zzdhhm 				String 		主站电话号码
   * @param dxzxhm 				String	 	短信中心号码
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZzdhhm(String txfs,String xzqxm, String zddz, String zzdhhm,String dxzxhm){
  	try{  		
  		return getParse().sendZzdhhm(txfs,xzqxm, zddz, zzdhhm,dxzxhm);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端状态量输入参数设置F12(AFN=04H)
   * @param xzqxm 				String 		行政区县码
   * @param zddz  				String 		终端地址
   * @param ztljrbz				String 		状态量接入标志(D0-D7表示1-8路,置1:接入;置0:不接入)
   * @param ztlsxbz				String	 	状态量属性标志(D0-D7表示1-8路,置1:a型触点;置0:b型触点)
   * @param ztlgjbz				String	 	状态量告警标志(D0-D7表示1-8路,置1:重要事件;置0:一般事件)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZtlsrcs(String txfs,String xzqxm, String zddz, 
  		String ztljrbz,String ztlsxbz,String ztlgjbz){
  	try{  		
  		return getParse().sendZtlsrcs(txfs,xzqxm, zddz, ztljrbz,ztlsxbz,ztlgjbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：电能表异常判别阀值设定F59(AFN=04H)
   * @param xzqxm 				String 	行政区县码
   * @param zddz  				String 	终端地址
   * @param dnlccfz  			String 	电能量超差阀值x.x
   * @param dnbfzfz		  		String 	电能表飞走阀值x.x
   * @param dnbtzfz		  		String 	电能表停走阀值,单位:15min
   * @param dnbjsfz 			String 	电能表校时阀值,单位:min
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDnbycpbfz(String txfs,String xzqxm, String zddz,
  				String dnlccfz,String dnbfzfz,String dnbtzfz,String dnbjsfz){
  	try{  		
  		return getParse().sendDnbycpbfz(txfs,xzqxm, zddz, dnlccfz,dnbfzfz,dnbtzfz,dnbjsfz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**方法简述：终端配置数量表设置F9(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param dnbsl  int 电能表数量
   * @param mcsl  int 脉冲数量
   * @param mnlsl  int 模拟量数量
   * @param zjzsl  int 总加组数量
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdpzslb(String txfs,String xzqxm, String zddz,
                                   int dnbsl, int mcsl, int mnlsl, int zjzsl) {
  	try{  		
  		return getParse().sendZdpzslb(txfs,xzqxm, zddz, dnbsl, mcsl, mnlsl, zjzsl);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**方法简述：终端抄表间隔设置F24(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param cbjg  int 抄表间隔（单位：分钟）
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdcbjg(String txfs,String xzqxm, String zddz, int cbjg) {
  	try{  		
  		return getParse().sendZdcbjg(txfs,xzqxm, zddz, cbjg);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  
  /**方法简述：终端保安定值设置F17(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param badz  String 保安定值（>=1,<=999）
   * @param xs  Sting 系数（遵照规约,如：000=10E4...）
   * @param zf  String 正负：0：正；1：负
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdbadz(String txfs,String xzqxm, String zddz, String badz,String xs,String zf) {
  	try{  		
  		return getParse().sendZdbadz(txfs,xzqxm, zddz, badz, xs, zf);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
//  /**方法简述：终端功控时段设置F18(AFN=04H)
//   * @param xzqxm String 行政区县码
//   * @param zddz  String 终端地址
//   * @param sd  String[][] 时段{sd[i][0]:时段(x-y,0-48);
//   * 							sd[i][1]:控制状态(00:不控制；01：控制1；10：控制2；11：保留)}
//   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
//   */
//  public static String sendAFN04F18(String txfs,String xzqxm, String zddz, String[][] sd) {
//  	try{  		
//  		return getParse().sendAFN04F18(txfs,xzqxm, zddz, sd);
//    }catch(Exception e){
//    	e.printStackTrace();
//    	return null;
//    }
//  }
  

 
  
  /**方法简述：终端抄表日设置F7(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param day   String 抄表日（32个长度的二进制字符）
   * @param time  String 抄表时间（单位：HHMM）
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdcbr(String txfs,String xzqxm, String zddz, String day,
                                 String time) {
  	try{  		
  		return getParse().sendZdcbr(txfs,xzqxm, zddz, day, time);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端事件记录配置设置F8(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sjjlyxbz  String 事件记录有效标志（64个长度的二进制字符,由高到低）
   * @param sjzyxdjbz  String 事件重要性等级标志（64个长度的二进制字符,由高到低）
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdsjjl(String txfs,String xzqxm, String zddz, String sjjlyxbz,
                                 String sjzyxdjbz) {
  	try{  		
  		return getParse().sendZdsjjl(txfs,xzqxm, zddz, sjjlyxbz, sjzyxdjbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**方法简述：终端1/2类数据任务设置F65/F66(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param rwh   String 任务号
   * @param fszq  String 发送周期
   * @param zqdw  String 周期单位(00：分；01：时；10：日；11：月)
   * @param fsjzsj  String 发送基准时间(年月日时分秒)
   * @param cqbl  String 抽取倍率
   * @param rwsjx  String[][] 任务数据项(String[i][0]:信息点Pn;String[i][1]:信息类Fn)
   * @param rwlx  String 任务类型(1:1类数据任务;2:2类数据任务)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdrw(String txfs,String xzqxm, String zddz, String rwh,
                                 String fszq,String zqdw,String fsjzsj,
								 String cqbl,String[][] rwsjx,String rwlx) {
  	try{  		
  		return getParse().sendZdrw(txfs,xzqxm,zddz,rwh,fszq,zqdw,fsjzsj,cqbl,rwsjx,rwlx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端电能表/交流采样装置参数配置F10(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param dnbxx  ArrayList 终端所配电能表信息（电能表序号、所属测量点、端口号、规约类型、
   *                         通讯地址、通讯密码、费率个数、整数位个数[4-7]、小数位个数[1-4]）(均为string型)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZddnbpz(String txfs,String xzqxm, String zddz, ArrayList dnbxx) {
  	try{  		
  		return getParse().sendZddnbpz(txfs,xzqxm, zddz, dnbxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**方法简述：终端脉冲参数配置F11(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param mcxx  ArrayList 终端所配脉冲的信息（端口号、测量点、脉冲属性、电表常数）(均为string型)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdmcpz(String txfs,String xzqxm, String zddz, ArrayList mcxx) {
  	try{  		
  		return getParse().sendZdmcpz(txfs,xzqxm, zddz, mcxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  
  /**方法简述：终端总加组配置F14(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzxx  ArrayList 终端所配总加组信息
   * 				[1、HashMap:key   = 总加组号(String)；
   * 						   	value = 总加测量点信息(ArrayList)
   * 				 2、总加测量点信息ArrayList里是HashMap,
   * 					包括：测量点号(cldh:String);
   * 						 正反向标志(zfxbz:String)<0:正向；1:反向>;
   * 						 运算符标志(ysfbz:String)<0:加；1:减>;
   * 				]
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdzjzpz(String txfs,String xzqxm, String zddz, ArrayList zjzxx) {
  	try{  		
  		return getParse().sendZdzjzpz(txfs,xzqxm, zddz, zjzxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**方法简述：查询终端事件F1/F2(AFN=0EH)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sjlx  String 事件类型(1:重要事件；2：一般事件)
   * @param sjqszz  int 事件起始指针
   * @param sjjszz  int 事件结束指针
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String queryZdsj(String txfs,String xzqxm, String zddz,String sjlx,int sjqszz,int sjjszz) {
  	try{  		
  		return getParse().queryZdsj(txfs,xzqxm, zddz,sjlx,sjqszz,sjjszz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
    

  /**方法简述：测量点基本参数配置F25(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param cldh  int 测量点号
   * @param cldjbcs  HashMap 测量点基本参数（PT、CT、额定电压、最大电流、接线方式）[均为String型]
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCldjbcspz(String txfs,String xzqxm, String zddz, int cldh,
                                     HashMap cldjbcs) {
  	try{  		
  		return getParse().sendCldjbcspz(txfs,xzqxm, zddz, cldh, cldjbcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：遥控F1/F2(AFN=05H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param lch   	String 	轮次号(1-8轮)
   * @param ykbz  	String 	遥控标志(55:遥控跳闸；AA：允许合闸)
   * @param xdsj 	String 	限电时间(0-15),单位:0.5h
   * @param gjyssj 	String 	告警延时时间(0-15),单位:1min
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendYk(String txfs,String xzqxm, String zddz, String lch,
  			String ykbz,String xdsj,String gjyssj) {
  	try{  		
  		return getParse().sendYk(txfs,xzqxm,zddz,lch,ykbz,xdsj,gjyssj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
    
  /**方法简述：终端电能量费率F22(AFN=04H)
   * @param xzqxm 	String 			行政区县码
   * @param zddz  	String 			终端地址
   * @param fl 		String[14][3] 	fl[0][0]:费率1的符号(0:正;1:负)
   * 								fl[0][1]:费率1的单位(0:厘;1:元)
   * 								fl[0][2]:费率1的值
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDnlfl(String txfs,String xzqxm, String zddz, String[][] fl){
  	try{  		
  		return getParse().sendDnlfl(txfs,xzqxm,zddz,fl);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端催费告警参数F23(AFN=04H)
   * @param xzqxm 	String 			行政区县码
   * @param zddz  	String 			终端地址
   * @param cfgjcs 	String		 	催费告警参数:24位(D23-D0),每位对应1小时,
   * 								置1告警,置0不告警,
   * 								比如:D0=1表示00:00-01:00告警
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCfgjcs(String txfs,String xzqxm, String zddz, String cfgjcs){
  	try{  		
  		return getParse().sendCfgjcs(txfs,xzqxm,zddz,cfgjcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端催费告警投入标志F26/F34(AFN=05H)
   * @param xzqxm 	String 		行政区县码
   * @param zddz  	String 		终端地址
   * @param trbz 	String		投入标志：55:投入;AA:解除 
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCfgjtrbz(String txfs,String xzqxm, String zddz, String trbz){
  	try{  		
  		return getParse().sendCfgjtrbz(txfs,xzqxm,zddz,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端电能量费率时段和费率数设置F21(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sd  String[][] 时段{sd[i][0]:时段(x-y,0-48);
   * 						   sd[i][1]:费率(0000:费率1;0001:费率2;...;1101:费率14)}
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDnlflsd(String txfs,String xzqxm, String zddz, String[][] sd) {
  	try{  		
  		return getParse().sendDnlflsd(txfs,xzqxm,zddz,sd);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：电控轮次设定F48(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  int 总加组号
   * @param lc String[8] 轮次受控情况(lc[0]-lc[7]:第1轮-第8轮,0:不受控，1：受控)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDklc(String txfs,String xzqxm, String zddz, int zjzh,String[] lc){
  	try{  		
  		return getParse().sendDklc(txfs,xzqxm,zddz,zjzh,lc);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：月电量控定值设定F46(AFN=04H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param zjzh  	int 	总加组号
   * @param dz 		String 	定值
   * @param dzfh 	String 	定值符号:0:正;1:负
   * @param dzdw	String 	定值单位:0:kWh;1:MWh
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendYdkdz(String txfs,String xzqxm, String zddz, int zjzh,
  		String dz,String dzfh,String dzdw){
  	try{  		
  		return getParse().sendYdkdz(txfs,xzqxm,zddz,zjzh,dz,dzfh,dzdw);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：购电量控定值设定F47(AFN=04H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param zjzh  	int 	总加组号
   * @param gddh 	String 	购电单号
   * @param bz	 	String 	标志:55:追加;AA:刷新
   * @param gdfh	String 	购电符号:0:正;1:负
   * @param gdz		String 	购电值
   * @param bjmxfh	String 	报警门限符号:0:正;1:负
   * @param bjmxz	String 	报警门限值
   * @param tzmxfh	String 	跳闸门限符号:0:正;1:负
   * @param tzmxz	String 	跳闸门限值
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendGdkdz(String txfs,String xzqxm, String zddz, int zjzh,
  		String gddh,String bz,String gdfh,String gdz,
  		String bjmxfh,String bjmxz,String tzmxfh,String tzmxz){
  	try{  		
  		return getParse().sendGdkdz(txfs,xzqxm,zddz,zjzh,gddh,bz,gdfh,gdz,bjmxfh,bjmxz,tzmxfh,tzmxz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：月电控投入标志设定F15/F23(AFN=05H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param zjzh  	int 	总加组号
   * @param trbz 	String 	投入标志:55:投入;AA:解除
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendYdktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz){
  	try{  		
  		return getParse().sendYdktrbz(txfs,xzqxm,zddz,zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：购电控投入标志设定F16/F24(AFN=05H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param zjzh  	int 	总加组号
   * @param trbz 	String 	投入标志:55:投入;AA:解除
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendGdktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz){
  	try{  		
  		return getParse().sendGdktrbz(txfs,xzqxm,zddz,zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：功控轮次设定F45(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  int 总加组号
   * @param lc String[8] 轮次受控情况(lc[0]-lc[7]:第1轮-第8轮,0:不受控，1：受控)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendGklc(String txfs,String xzqxm, String zddz, int zjzh,String[] lc) {
  	try{  		
  		return getParse().sendGklc(txfs,xzqxm, zddz, zjzh,lc);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：功率控制的功率计算滑差时间设置F43(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  int 总加组号
   * @param hcsj  String 滑差时间(1~60)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendAFN04F43(String txfs,String xzqxm, String zddz, int zjzh,String hcsj) {
  	try{  		
  		return getParse().sendAFN04F43(txfs,xzqxm, zddz, zjzh,hcsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**方法简述：时段功控投入标志设置F9/F17(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  int 总加组号
   * @param trbz  String 投入标志(55:投入；AA：解除)
   * @param fabh  String 方案编号(当trbz=AA时，fabh=null)
   * @param trsd  String[] 投入时段(当trbz=AA时，trsd=null)(trsd[i]表示已投入的时段号)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendAFN05F9F17(String txfs,String xzqxm, String zddz, int zjzh,String trbz,String fabh,String[] trsd) {
  	try{  		
  		return getParse().sendAFN05F9F17(txfs,xzqxm, zddz, zjzh,trbz,fabh,trsd);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  
  /**方法简述：厂休功控投入标志设置F10/F18(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  int 总加组号
   * @param trbz  String 投入标志(55:投入；AA：解除)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCxgktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz) {
  	try{  		
  		return getParse().sendCxgktrbz(txfs,xzqxm, zddz, zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：营业报停控投入标志设置F11/F19(AFN=05H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  int 总加组号
   * @param trbz  String 投入标志(55:投入；AA：解除)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendYybtktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz) {
  	try{  		
  		return getParse().sendYybtktrbz(txfs,xzqxm, zddz, zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：时段功控定值设置F41(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  int 总加组号
   * @param sd HashMap 时段功控定值{key=fah(方案号 String:1-3);
   * 							  value=sddz(时段定值 String[][])
   * 									<
   * 									 须按时段号升序排;
   * 									 sddz[i][0]:时段号；
   * 									 sddz[i][1]:正负(0：正；1：负)
   * 									 sddz[i][2]:时段定值（>=1,<=999）
   * 									 sddz[i][3]:系数(遵照规约,如：000=10E4...）
   * 									>}
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendAFN04F41(String txfs,String xzqxm, String zddz, int zjzh,HashMap sd) {
  	try{  		
  		return getParse().sendAFN04F41(txfs,xzqxm, zddz, zjzh,sd);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**方法简述：厂休功控参数设置F42(AFN=04H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param zjzh  	int 	总加组号
   * @param cxkdz  	String 	厂休控定值（>=1,<=999）
   * @param dzzf  	String 	厂休控定值正负(0：正；1：负)
   * @param dzxs  	String 	厂休控定值系数(遵照规约,如：000=10E4...）
   * @param xdqssj  String 	限电起始时间(hhmm)
   * @param xdyxsj  String 	限电延续时间(1~48,单位：0.5h)
   * @param mzxdr  	String 	每周限电日(7位字符串,D7~D1分别表示周日到周一)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCxgkcs(String txfs,String xzqxm, String zddz, int zjzh,
  								  String cxkdz,String dzzf,String dzxs,
								  String xdqssj,String xdyxsj,String mzxdr) {
  	try{  		
  		return getParse().sendCxgkcs(txfs,xzqxm, zddz, zjzh,cxkdz,dzzf,dzxs,xdqssj,xdyxsj,mzxdr);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**方法简述：营业报停控参数设置F44(AFN=04H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param zjzh  	int 	总加组号
   * @param btqssj  	String 	报停起始时间(yymmdd)
   * @param btjssj  	String 	报停结束时间(yymmdd)
   * @param btkgldz  	String 	报停控功率定值（>=1,<=999）
   * @param dzzf  	String 	报停控功率定值正负(0：正；1：负)
   * @param dzxs  	String 	报停控功率定值系数(遵照规约,如：000=10E4...）
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendYybtkcs(String txfs,String xzqxm, String zddz, int zjzh,
  								  String btqssj,String btjssj,
  								  String btkgldz,String dzzf,String dzxs) {
  	try{  		
  		return getParse().sendYybtkcs(txfs,xzqxm,zddz,zjzh,btqssj,btjssj,btkgldz,dzzf,dzxs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

    
  
  /**方法简述：查询任务数据F1/F2(AFN=0BH)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param rwlx  String 任务类型(1:1类；2:2类)
   * @param rwh  int 任务号
   * @param qssj  String 起始时间(格式:yymmddhhmm)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String queryRwsj(String txfs,String xzqxm, String zddz, String rwlx,int rwh,String qssj) {
  	try{  		
  		return getParse().queryRwsj(txfs,xzqxm, zddz, rwlx,rwh,qssj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  

  
  /**方法简述：下载通知F2(AFN=0FH) 命令字＝01
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param wjm  String 文件名
   * @param wjnr  byte[] 文件内容
   * @param ip  String IP地址
   * @param port  String 端口
   * @param cxmklx  String 程序模块类型（01：主CPU;02：交采CPU）
   * @param cxjhsj  String 程序激活时间 YYMMDDhhmm
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendXztz(String txfs,String xzqxm, String zddz,String wjm,byte[] wjnr,String ip,
  					String port,String cxmklx,String cxjhsj) {
  	try{  		
  		return getParse().sendXztz(txfs,xzqxm, zddz,wjm,wjnr,ip,port,cxmklx,cxjhsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：下载取消F2(AFN=0FH) 命令字＝02
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param tdlx  String 通道类型 01：串口 ；02：GPRS
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static   String sendXzqx(String txfs,String xzqxm, String zddz) {
  	try{  		
  		return getParse().sendXzqx(txfs,xzqxm, zddz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：下载更改程序激活时间F2(AFN=0FH) 命令字＝03
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param cxjhsj  String 程序激活时间 YYMMDDhhmm
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendXzggcxjhsj(String txfs,String xzqxm, String zddz,String cxjhsj) {
  	try{  		
  		return getParse().sendXzggcxjhsj(txfs,xzqxm, zddz,cxjhsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：下载程序版本切换F2(AFN=0FH) 命令字＝04
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static  String sendXzcxbbqh(String txfs,String xzqxm, String zddz) {
  	try{  		
  		return getParse().sendXzcxbbqh(txfs,xzqxm, zddz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：查询1类数据(AFN=0AH)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sjxxx  String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号）
   * 								    sjxxx[i][1] 信息类（Fn）
   * 信息点号为65535表示P(FF)
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String query_1lsj(String txfs,String xzqxm, String zddz,String[][] sjxxx) {
  	try{  		
  		return getParse().query_1lsj(txfs,xzqxm,zddz,sjxxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：查询所有在线终端的0CF2(AFN=0CH)
   * @return seq_sjzfs String 数据发送表序列（null：失败；1：成功）
   */
  public static String query_allzd_0cf2() {
  	try{  		
  		return getParse().query_allzd_0cf2();
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：查询2数据_曲线(AFN=0DH)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sjxxx  String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号）
   * 								    sjxxx[i][1] 信息类（Fn）
   * 信息点号为65535表示P(FF)
   * @param qssj  String 起始时间 yymmddhhmm
   * @param sjmd  String 数据密度 1：15分钟；2：30分钟；3：60分钟
   * @param sjds  String 数据点数
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String query_2lsj_qx(String txfs,String xzqxm, String zddz,
  				String[][] sjxxx,String qssj,String sjmd,String sjds) {
  	try{  		
  		return getParse().query_2lsj_qx(txfs,xzqxm,zddz,sjxxx,qssj,sjmd,sjds);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：查询2数据_日冻结(AFN=0DH)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sjxxx  String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号）
   * 								    sjxxx[i][1] 信息类（Fn）
   * 信息点号为65535表示P(FF)
   * @param rdjsj  String 日冻结时间 yymmdd
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String query_2lsj_rdj(String txfs,String xzqxm, String zddz,
  				String[][] sjxxx,String rdjsj) {
  	try{  		
  		return getParse().query_2lsj_rdj(txfs,xzqxm,zddz,sjxxx,rdjsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：查询2数据_月冻结(AFN=0DH)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sjxxx  String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号）
   * 								    sjxxx[i][1] 信息类（Fn）
   * 信息点号为65535表示P(FF)
   * @param ydjsj  String 月冻结时间 yymm
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String query_2lsj_ydj(String txfs,String xzqxm, String zddz,
  				String[][] sjxxx,String ydjsj) {
  	try{  		
  		return getParse().query_2lsj_ydj(txfs,xzqxm,zddz,sjxxx,ydjsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：查询终端参数配置(AFN=0AH)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param sjxxx  String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号）
   * 								    sjxxx[i][1] 信息类（Fn）
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String query_zdcspz(String txfs,String xzqxm, String zddz,String[][] sjxxx) {
  	try{  		
  		return getParse().query_zdcspz(txfs,xzqxm,zddz,sjxxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：查询中继数据(AFN=10H)
   * @param txfs 		String 通信方式(01:短信;02:GPRS;06:串口)
   * @param xzqxm 		String 行政区县码
   * @param zddz  		String 终端地址
   * @param dbgylx 		String 电表规约类型
   * @param dnbdz  		String 电能表地址
   * @param dnbsjxdm  	String 电能表数据项代码
   * @param btl			String 波特率(000:表示300;...111:表示19200)
   * @param tzw  		String 停止位(0:1位;1:2位)
   * @param jym  		String 校验码(00:无校验;10:偶校验;11:齐校验)
   * @param ws  		String 位数(00-11:表示5-8)
   * @param bwcssj 		String 报文超时时间(单位:10ms)
   * @param zjcssj 		String 字节超时时间(单位:10ms)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   * @throws Exception
   */
  public static String query_zj(String txfs, String xzqxm, String zddz,
			String dbgylx, String dnbdz, String dnbsjxdm, String btl, 
			String tzw, String jym,
			String ws,String bwcssj,String zjcssj) throws Exception {
  	try{  		
  		return getParse().query_zj(txfs,xzqxm,zddz,dbgylx,dnbdz,dnbsjxdm,
  				btl,tzw,jym,ws,bwcssj,zjcssj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端声音告警标志设置F57(AFN=04H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param sygjbz  String	声音告警标志:D0-D23按位表示0-23点,
   * 						每位表示一个小时,如：0表示00:00-01:00;
   * 						置1表示相应时段允许告警,置0表示相应时段不允许告警
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendSygjbz(String txfs,String xzqxm, String zddz, String sygjbz){
  	try{  		
  		return getParse().sendSygjbz(txfs,xzqxm,zddz,sygjbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：谐波限值设置F60(AFN=04H)
   * @param xzqxm 	String 		行政区县码
   * @param zddz  	String 		终端地址
   * @param xbxz	String[][2]	谐波限值,依次为:
   * 							总畸变电压含有率上限值#符号; 
   * 							奇次谐波电压含有率上限值#符号;  
   * 							偶次谐波电压含有率上限值#符号; 
   * 							总畸变电流有效值上限值#符号;  
   * 							2次谐波电流有效值上限值#符号;  
   * 								...  
   * 							18次谐波电流有效值上限值#符号;  
   * 							3次谐波电流有效值上限值#符号;  
   * 								...  
   * 							19次谐波电流有效值上限值#符号; 
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendXbxz(String txfs,String xzqxm, String zddz, String[][] xbxz){
  	try{  		
  		return getParse().sendXbxz(txfs,xzqxm,zddz,xbxz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：测量点限值参数设置F26(AFN=04H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param cldh  		String 		测量点号
   * @param dyhglsx		String 		电压合格率上限
   * @param dyhglxx		String 		电压合格率下限
   * @param dydxmx		String 		电压断相门限
   * @param gymx		String 		过压门限
   * @param qymx		String 		欠压门限
   * @param glmx		String 		过流门限#符号
   * @param eddlmx		String 		额定电流门限#符号
   * @param lxdlsx		String 		零序电流上限#符号
   * @param szglssx		String 		视在功率上上限
   * @param szglsx		String 		视在功率上限
   * @param sxdybphxz	String 		三相电压不平衡限值#符号
   * @param sxdlbphxz	String 		三相电流不平衡限值#符号
   * @param lxsysjxz	String 		连续失压时间限值
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCldxzcs(String txfs,String xzqxm, String zddz, String cldh,
  		String dyhglsx,String dyhglxx,String dydxmx,String gymx,
		String qymx,String glmx,String eddlmx,String lxdlsx,String szglssx,
		String szglsx,String sxdybphxz,String sxdlbphxz,String lxsysjxz){
  	try{  		
  		return getParse().sendCldxzcs(txfs,xzqxm,zddz,cldh,
  		  		dyhglsx,dyhglxx,dydxmx,gymx,
				qymx,glmx,eddlmx,lxdlsx,szglssx,
				szglsx,sxdybphxz,sxdlbphxz,lxsysjxz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：测量点功率因数分段限值设置F28(AFN=04H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param cldh  		String 		测量点号
   * @param xz1			String 		限值1#符号
   * @param xz2			String 		限值2#符号
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCldglysfdxz(String txfs,String xzqxm, String zddz, String cldh,
  		String xz1,String xz2){
  	try{  		
  		return getParse().sendCldglysfdxz(txfs,xzqxm,zddz,cldh,xz1,xz2);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：电容器控制投入标志F41/F42(AFN=05H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param cldh  		String 		测量点号
   * @param trbz  		String 		投入标志(55:投入;AA:切除)
   * @param drqz		String 		电容器组(D15-D0,置1表示投入或切除,置0表示保持原状)
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDrqkztrbz(String txfs,String xzqxm,String zddz,String cldh,
  			String trbz,String drqz){
  	try{  		
  		return getParse().sendDrqkztrbz(txfs,xzqxm,zddz,cldh,trbz,drqz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：中文信息F32(AFN=05H)
   * @param xzqxm 		String 		行政区县码
   * @param zddz  		String 		终端地址
   * @param zl  		String 		中文信息种类
   * @param bh			String 		中文信息编号
   * @param hzxx		String 		汉字信息
   *
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZwxx(String txfs,String xzqxm,String zddz,String zl,String bh,String hzxx){
  	try{  		
  		return getParse().sendZwxx(txfs,xzqxm,zddz,zl,bh,hzxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**方法简述：终端自动保电设置F58(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zdbdsj  int 自动保电时间（单位：小时）
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdzdbd(String txfs,String xzqxm, String zddz, int zdbdsj) {
  	try{  		
  		return getParse().sendZdzdbd(txfs,xzqxm,zddz,zdbdsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：总加组数据冻结参数设置F33(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param zjzh  String 总加组号
   * @param djcs  String[4] 冻结参数（0：不冻结；1：15分；2：30分；3：60分）
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZjzsjdjcs(String txfs,String xzqxm, String zddz,String zjzh, String[] djcs) {
  	try{  		
  		return getParse().sendZjzsjdjcs(txfs,xzqxm,zddz,zjzh,djcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：测量点数据冻结参数设置F27(AFN=04H)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @param cldh  String 测量点号
   * @param djcs  String[][2] 数据项代码和对应的冻结参数（0：不冻结；1：15分；2：30分；3：60分）
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendCldsjdjcs(String txfs,String xzqxm, String zddz,String cldh, String[][] djcs) {
  	try{  		
  		return getParse().sendCldsjdjcs(txfs,xzqxm,zddz,cldh,djcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：直流模拟量接入参数F61(AFN=04H)
   * @param xzqxm 				String 		行政区县码
   * @param zddz  				String 		终端地址
   * @param jrbz				String 		直流模拟量接入标志(D0-D7表示1-8路,置1:接入;置0:不接入)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZlmnljrcs(String txfs,String xzqxm, String zddz,String jrbz){
  	try{  		
  		return getParse().sendZlmnljrcs(txfs,xzqxm,zddz,jrbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：电容器参数F73(AFN=04H)
   * @param xzqxm 	String 			行政区县码
   * @param zddz  	String 			终端地址
   * @param cldh  	String 			测量点号
   * @param drqcs	String[9][5] 	电容器参数(1-9组),以组1为例:
   * 								drqcs[0][0]:共分标志
   * 								drqcs[0][1]:分补相标志
   * 								drqcs[0][2]:电容装见容量
   * 								drqcs[0][3]:电容装见容量系数
   * 								drqcs[0][4]:电容装见容量符号
   * 								
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDrqcs(String txfs,String xzqxm, String zddz,String cldh,String[][] drqcs){
  	try{  		
  		return getParse().sendDrqcs(txfs,xzqxm,zddz,cldh,drqcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：电容器投切运行参数F74(AFN=04H)
   * @param xzqxm 		String 			行政区县码
   * @param zddz  		String 			终端地址
   * @param cldh  		String 			测量点号
   * @param mbglys		String			目标功率因数
   * @param mbglysfh	String			目标功率因数符号(0:正;1:负)
   * @param trwgglmx	String			投入无功功率门限
   * @param qcwgglmx	String			切除无功功率门限
   * @param yssj		String			延时时间
   * @param dzsjjg		String			动作时间间隔
   * 								
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDrqtqyxcs(String txfs,String xzqxm, String zddz,String cldh,
  		String mbglys,String mbglysfh,String trwgglmx,String qcwgglmx,
		String yssj,String dzsjjg){
  	try{  		
  		return getParse().sendDrqtqyxcs(txfs,xzqxm,zddz,cldh,mbglys,mbglysfh,
  				trwgglmx,qcwgglmx,yssj,dzsjjg);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：电容器保护参数F75(AFN=04H)
   * @param xzqxm 		String 			行政区县码
   * @param zddz  		String 			终端地址
   * @param cldh  		String 			测量点号
   * @param gdy			String			过电压
   * @param gdyhcz		String			过电压回差值
   * @param qdy			String			欠电压
   * @param qdyhcz		String			欠电压回差值
   * @param dlsx		String			总畸变电流含有率上限
   * @param dlsxfh		String			总畸变电流含有率上限符号
   * @param dlyxhc		String			总畸变电流含有率越限回差值
   * @param dlyxhcfh	String			总畸变电流含有率越限回差值符号
   * @param dysx		String			总畸变电压含有率上限
   * @param dysxfh		String			总畸变电压含有率上限符号
   * @param dyyxhc		String			总畸变电压含有率越限回差值
   * @param dyyxhczfh	String			总畸变电压含有率越限回差值符号
   * 								
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDrqbhcs(String txfs,String xzqxm, String zddz,String cldh,
  		String gdy,String gdyhcz,String qdy,String qdyhcz,
  		String dlsx,String dlsxfh,String dlyxhc,String dlyxhcfh,
  		String dysx,String dysxfh,String dyyxhc,String dyyxhcfh){
  	try{  		
  		return getParse().sendDrqbhcs(txfs,xzqxm,zddz,cldh,
  				gdy,gdyhcz,qdy,qdyhcz,
  				dlsx,dlsxfh,dlyxhc,dlyxhcfh,
  				dysx,dysxfh,dyyxhc,dyyxhcfh);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：电容器投切控制方式F76(AFN=04H)
   * @param xzqxm 		String 			行政区县码
   * @param zddz  		String 			终端地址
   * @param cldh  		String 			测量点号
   * @param kzfs		String			控制方式:1:当地控制;2:远方遥控;3:闭锁;4:解锁
   * 	
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendDrqtqkzfs(String txfs,String xzqxm, String zddz,String cldh,String kzfs){
  	try{  		
  		return getParse().sendDrqtqkzfs(txfs,xzqxm,zddz,cldh,kzfs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：功控告警时间F49(AFN=04H)
   * @param xzqxm 	String 	行政区县码
   * @param zddz  	String 	终端地址
   * @param lch   	String 	轮次号(1-8轮)
   * @param gkgjsj 	String 	功控告警时间(0-60min)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendGkgjsj(String txfs,String xzqxm,String zddz,String lch,String gkgjsj){
  	try{  		
  		return getParse().sendGkgjsj(txfs,xzqxm,zddz,lch,gkgjsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端电压/电流模拟量参数配置F13(AFN=04H)
   * @param xzqxm 	String 		行政区县码
   * @param zddz  	String 		终端地址
   * @param mnlxx  	ArrayList 	终端所配模拟量信息（端口号、测量点号、模拟量属性）(均为string型)
   * 
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZddydlmnlpz(String txfs,String xzqxm, String zddz, ArrayList mnlxx){
  	try{  		
  		return getParse().sendZddydlmnlpz(txfs,xzqxm,zddz,mnlxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**方法简述：终端有功总电能量差动越限事件参数配置F15(AFN=04H)
   * @param xzqxm 	String 			行政区县码
   * @param zddz  	String 			终端地址
   * @param cs  	String[][7] 	参数
   * 								cs[i][0]:对比总加组号
   * 								cs[i][1]:参照总加组号
   * 								cs[i][2]:时间区间
   * 								cs[i][3]:对比方法(0:相对;1:绝对)
   * 								cs[i][4]:差动越限相对偏差值(%)
   * 								cs[i][5]:差动越限绝对偏差值(kWh)
   * 								cs[i][6]:差动越限绝对偏差值符号(0:正;1:负)
   * 				
   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
   */
  public static String sendZdygzdnlcdyxsjcspz(String txfs,String xzqxm, String zddz, String[][] cs){
  	try{  		
  		return getParse().sendZdygzdnlcdyxsjcspz(txfs,xzqxm,zddz,cs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**
	 * 方法简述：程序升级请求及信息 F1(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值   文件的绝对路径
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
  public static String sendAFN0FF1(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN0FF1(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  
  /**
	 * 方法简述：程序文件传输F2(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;cs2;cs3;cs4) 
	 *             cs1:总段数n;
	 *             cs2:第i段标识或偏移（i=0~n）;
	 *             cs3:第i段数据长度Lf;
	 *             cs4:文件数据;
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
public static String sendAFN0FF2(String txfs,String xzqxm, String zddz, 
		  String csz){
	  try{  	 
		  return getParse().sendAFN0FF2(txfs,xzqxm,zddz,csz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
}
    
  

}
