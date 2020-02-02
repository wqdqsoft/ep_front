package com.powerhigh.gdfas.rmi;

import java.util.*;

import org.apache.log4j.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.powerhigh.gdfas.rmi.parse;


/**
 * Description: �������д����� <p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-4-2
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class operation3 {
	 
  private static parse p = null;
  //������־
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
	 * ������������ѯ�ն˲�������(AFN=0AH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param ksxh
	 *            int ��ʼװ�����
	 * @param jsxh
	 *            int ����װ�����
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	
  /**�����������ն˸�λ��F1Ӳ����ʼ��(AFN=01H)
   * @param xzqxm 		String 	����������
   * @param zddz  		String 	�ն˵�ַ
   * @param csz			String  ����ֵ(Ԥ��)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  

  /**�����������ն˸�λ��F2��������ʼ��(AFN=01H)
   * @param xzqxm 		String 	����������
   * @param zddz  		String 	�ն˵�ַ
   * @param csz			String  ����ֵ(Ԥ��)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  

  /**�����������ն˸�λ��F3������ȫ����������ʼ�������ָ����������ã�(AFN=01H)
   * @param xzqxm 		String 	����������
   * @param zddz  		String 	�ն˵�ַ
   * @param csz			String  ����ֵ(Ԥ��)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  

  /**�����������ն˸�λ��F4����������ϵͳ��վͨ���йصģ���ȫ����������ʼ��(AFN=01H)
   * @param xzqxm 		String 	����������
   * @param zddz  		String 	�ն˵�ַ
   * @param csz			String  ����ֵ(Ԥ��)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  
  /**�����������ն�ͨ�Ų�������F1(AFN=04H)
   * @param xzqxm 		String 	����������
   * @param zddz  		String 	�ն˵�ַ
   * @param csz			String  ����ֵ(cs1;cs2;cs3;cs4;cs5;cs6;cs7)
   * 					cs1:��������ʱʱ��,��λ:20ms
   * 					cs2:��Ϊ����վ�����ʹ�����ʱʱ��,��λ:����
   * 					cs3:�ȴ��Ӷ�վ��Ӧ�ĳ�ʱʱ��,0-4095,��λ:��
   * 					cs4:�ط�����,0-3;0��ʾ�������ط�
   * 					cs5:1�������Զ��ϱ���ȷ�ϱ�־,1��ʾ�����ն���Ҫ��վȷ��
   * 					cs6:2�������Զ��ϱ���ȷ�ϱ�־,1��ʾ�����ն���Ҫ��վȷ��
   * 					cs7:3�������Զ��ϱ���ȷ�ϱ�־,1��ʾ�����ն���Ҫ��վȷ��
   * 					cs8:��������:1-60��
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������վIP��ַ�Ͷ˿�����F3(AFN=04H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param csz  		String 		����ֵ(cs1;cs2;cs3)
   * 					cs1:����IP(xxx.xxx.xxx.xxx:nnnnn)
   * 					cs2:����IP(xxx.xxx.xxx.xxx:nnnnn)
   * 					cs3:APN(16�ֽڣ�ASCII;��λ��00H��������)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������վ�绰����Ͷ������ĺ�������F4(AFN=04H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param csz  		String 		����ֵ(cs1;cs2)
   * 					cs1:��վ�绰����
   * 					cs2:�������ĺ���
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * �������������ˮ�ÿ��Ʋ���F5(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;cs2;cs3;cs4;cs5)
	 *            cs1:������������ˮ�ÿ�������ǰ�� ��λ������
	 *            cs2:���ˮ���Զ����������־ 0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
	 *            cs3:����������Զ����������־   0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
	 *            cs4:����������������п���ʱ�� ��λ������
	 *            cs5:�����������������ֹͣʱ�� ��λ������
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն����ַ����F6(AFN=04H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param csz  		String 		����ֵ(cs1;...;cs8)
   * 					cs1:���ַ1(���ַΪ0ʱ����ʾ�������ַ)
   * 					...
   * 					cs8:���ַ8(���ַΪ0ʱ����ʾ�������ַ)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն�IP��ַ�Ͷ˿� F7(AFN=04H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param csz  		String 		����ֵ(cs1;...;cs9)
   * 					cs1:�ն�IP��ַ(xxx.xxx.xxx.xxx)
   * 					cs2:���������ַ(xxx.xxx.xxx.xxx)
   * 					cs3:���ص�ַ(xxx.xxx.xxx.xxx)
   * 					cs4:��������(0~3,���α�ʾ;��ʹ�ô���http connect����socks4����socks5����)
   * 					cs5:�����������ַ���˿ں�(xxx.xxx.xxx.xxx:nnnnn)
   * 					cs6:������������ӷ�ʽ(0~1,���α�ʾ:������֤����Ҫ�û���/����)
   * 					cs7:�û���(ASCII)
   * 					cs8:����(ASCII)
   * 					cs9:�ն������˿ں�(nnnnn)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն�����ͨ�Ź�����ʽF8(AFN=04H)
   * @param 	xzqxm 	String ����������
   * @param 	zddz  	String �ն˵�ַ
   * @param 	csz  	String ����ֵ(cs1;...;cs7)
   * 					cs1:TCP/UDP(0:TCP;1:UDP)
   * 					cs2:����ģʽ(1:���ģʽ;2:�ͻ���ģʽ;3:������ģʽ)
   * 					cs3:����ģʽ(1:��������ģʽ;2:��������ģʽ;3:ʱ������ģʽ)
   * 					cs4:�ز����(��λ:��,ȡֵ0~65535)--��������ģʽ��ʱ������ģʽ
   * 					cs5:�ز�����(0~255)--��������ģʽ
   * 					cs6:��ͨ���Զ�����ʱ��(��λ:min,0~255)--��������ģʽ
   * 					cs7:��������ʱ�α�־(0-23��,�м���"#"����)--ʱ������ģʽ
   * 						����"0#3#5"��ʾ0�㡢3���5����������
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������ˮ��ˮλ���Ʋ���������ռ��������նˣ�F9(AFN=04H)
   * @param 	xzqxm 	String ����������
   * @param 	zddz  	String �ն˵�ַ
   * @param 	csz  	String ����ֵ(cs1;cs2;cs3;cs4)
   * 				   cs1:�����л�����1-ʹ��1��ˮ��2-ʹ��2��ˮ��3- 1��2�Ż�Ϊ��
					   cs2:�����л�ʱ��  һ���ֽ� Сʱ
					   cs3:����ʱ�� �����ֽ� ����
					   cs4:ֹͣʱ��   �����ֽ� ����';
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն˵��ܱ�/��������װ�ò�������F10(AFN=04H)
   * @param 	xzqxm 	String 	����������
   * @param 	zddz  	String 	�ն˵�ַ
   * @param 	csz  	String ����ֵ(cs1;...;csn)--N���������ID						
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն������������F11(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	csz		String 		����ֵ(cs1;...;csn)--N����������
   * 					csn:���ܱ�����(pz1#pz2#pz3#pz4)
   * 						pz1:����˿ں�
   * 						pz2:����������(1-64)
   * 						pz3:��������(0~3���α�ʾ�����й��������޹��������й��������޹�)
   * 						pz4:�����
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������������������豸װ�����ò���F12(AFN=04H)
   * @param xzqxm		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param csz  		String ����ֵ(cs1;...;csn)--N�����õ�ID		
   * 					
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**���������������豸���Ƶ����F13(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	csz		String ����ֵ(cs1;...;csn)--N�����õ�ID	
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ���������������豸��ͣ���Ʋ���F14(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������           
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N�������豸��ͣ���Ʋ�������
	 *            csn:�ܼ�������(pz1,...,pz7)--N�����豸��ͣ���Ʋ������� 
	 *                pz1:��n�׿��Ʋ���ִ������
	 *                pz2:��n�׿��Ʋ�����С�¶�
	 *                pz3:��n�׿��Ʋ�������¶�
	 *                pz4:��n�׿��Ʋ�����Ч��ʼ����
	 *                pz5:��n�׿��Ʋ�����Ч��ֹ����
	 *                pz6:��n�׿��Ʋ�������ʱ��
	 *                pz7:��n�׿��Ʋ���ֹͣʱ��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ����������ˮ��ˮλ���Ʋ���F15(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������           
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N��ˮ��ˮλ���Ʋ�������
	 *            csn:���Ʋ�������(pz1,...,pz14)--��Nˮ��ˮλ���Ʋ������� 
	 *                pz1:��n�׿��Ʋ���-���Ӳ����
	 *                pz2:��n�׿��Ʋ���-�������Ӳ����
	 *                pz3:��n�׿��Ʋ���-ͬʱ����ʹ��
	 *                pz4:��n�׿��Ʋ���-�����л�ʱ��
	 *                pz5:��n�׿��Ʋ���-�������
	 *                pz6:��n�׿��Ʋ���-ˮλ��λ
	 *                pz7:��n�׿��Ʋ���-�߼���ϵ
	 *                pz8:��n�׿��Ʋ���-�������(��һ������)
	 *                pz9:��n�׿��Ʋ���-ˮλ��λ(��һ������)
	 *                pz10:��n�׿��Ʋ���-���ƶ���
	 *                pz11:��n�׿��Ʋ���-��С�¶�
	 *                pz12:��n�׿��Ʋ���-����¶�
	 *                pz13:��n�׿��Ʋ���-����ʱ��
	 *                pz14:��n�׿��Ʋ���-ֹͣʱ��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ���������������ŷ����Ʋ���F16(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������           
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N��������Ʋ�������
	 *            csn:���Ʋ�������(pz1,...,pz10)--��N̨������Ʋ������� 
	 *                pz1:��n�׿��Ʋ���-���Ӳ����
	 *                pz2:��n�׿��Ʋ���-�������Ӳ����
	 *                pz3:��n�׿��Ʋ���-�����л�ʱ��
	 *                pz4:��n�׿��Ʋ���-��ˮ������ʹ��
	 *                pz5:��n�׿��Ʋ���-��С�¶�
	 *                pz6:��n�׿��Ʋ���-����¶�
	 *                pz7:��n�׿��Ʋ���-����ʱ��
	 *                pz8:��n�׿��Ʋ���-ֹͣʱ��
	 *                pz9:��n�׿��Ʋ���-Ƶ��
	 *                pz10:��n�׿��Ʋ���-���ƶ���
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ����������F17��ORP,HP ����������F17(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--	NORP,HP ���������õ�ID  N<=5
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ����������F18��������ˮλ����������(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--	վ������ID  N<=4
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**���������������������������F25(AFN=04H)
   * @param 	xzqxm 	String 	����������
   * @param 	zddz  	String 	�ն˵�ַ
   * @param 	cldh  	int 	�������
   * @param 	csz  	String 	����ֵ(cs1;cs2)
   * 					cs1:PT
   * 					cs2:CT
   * 					
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������������㷨ʹ��F26(AFN=04H)
   * @param 	xzqxm	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	csz  	String		����ֵ 55ʹ��  AA��ʹ��
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  /**����������������ѵ����������F27(AFN=04H)
   * @param 	xzqxm	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	csz  	String		����ֵ cs1;...;cs3
   *                                    ����cs1���¶�
   *                                       cs2:ORP
   *                                       cs3:���Ƶ��
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**���������������㹦�������ֶ���ֵ����F28(AFN=04H)
   * @param 	xzqxm 		String 		����������
   * @param 	zddz  		String 		�ն˵�ַ
   * @param 	cldh  		String 		�������
   * @param 	csz  		String 		����ֵ(cs1;cs2)
   * 						cs1:���������ֶ���ֵ1(pz1#pz2)
   * 							pz1:����(0:��;1:��)
   * 							pz2:��ֵ
   * 						cs2:���������ֶ���ֵ2(pz1#pz2)
   * 							pz1:����(0:��;1:��)
   * 							pz2:��ֵ
   * 							
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն˳������в�������F33(AFN=04H)
   * @param 	xzqxm 	String 	����������
   * @param 	zddz  	String 	�ն˵�ַ
   * @param 	csz  	String 	����ֵ(cs1;...;csn)--N��������
   * 					csn:������(pz1#...#pz7)
   * 						pz1:�ն�ͨ�Ŷ˿ں�(1���ֽ�,1-31)
   * 						pz2:̨�����г������п�����(2���ֽ�,D5D4D3D2D1D0,�磺100101,D15-D6Ϊ����)
   *							D5->�Ƿ�Ҫ���ն˳��������״̬�֡�(1:Ҫ��;0:��Ҫ��)
   *							D4->�Ƿ�Ҫ���ն���Ѱ����������ĵ��(1:Ҫ��;0:��Ҫ��)
   *							D3->�Ƿ�Ҫ���ն˶�ʱ�Ե��㲥Уʱ(1:Ҫ��;0:��Ҫ��)
   *							D2->Ҫ���ն˲��ù㲥���᳭��(1:Ҫ��;0:��Ҫ��)
   *							D1->�Ƿ�Ҫ���ն�ֻ���ص��(1:Ҫ��;0:�����б�)
   *							D0->�Ƿ������Զ�����(1:�������Զ�����;0: Ҫ���ն˸��ݳ���ʱ���Զ�����)
   *						pz3:������ʱ��(12���ֽ�,D95D94...D1D0,��:10...11)
   *							D95->ʱ��23:45~24:00�Զ�����״̬(1:�������Զ�����;0:����)
   *							D94->ʱ��23:30~23:45�Զ�����״̬(1:�������Զ�����;0:����)
   *							...
   *							D1->ʱ��00:15~00:30�Զ�����״̬(1:�������Զ�����;0:����)
   *							D0->ʱ��00:00~00:15�Զ�����״̬(1:�������Զ�����;0:����)
   *						pz4:������-����(4���ֽ�,D30D29...D1D0,��:11...01,D31����)
   *							D30->ÿ��31�յĳ���״̬(1:��Ч;0:��Ч)
   *							D29->ÿ��30�յĳ���״̬(1:��Ч;0:��Ч)
   *							...
   *							D1->ÿ��2�յĳ���״̬(1:��Ч;0:��Ч)
   *							D0->ÿ��1�յĳ���״̬(1:��Ч;0:��Ч)
   *						pz5:������-ʱ��(2���ֽ�,ʱ�֣�hhmm,��:0930��ʾ9��30��)
   *						pz6:�ն˳�����(1���ֽ�,1-60)
   *						pz7:�Ե��㲥Уʱ��ʱʱ��(3���ֽڣ���ʱ��,ddhhmm,����Ϊ00ʱ��ʾÿ��Уʱ,��000930��ʾÿ��9��30��Уʱ)
   *							
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�������������ն˽ӿڵ�ͨ��ģ��Ĳ�������F34(AFN=04H)
   * @param 	xzqxm 	String 	����������
   * @param 	zddz  	String 	�ն˵�ַ
   * @param 	csz  	String 	����ֵ(cs1;...;csn)--N��������
   * 					csn:������(pz1#...#pz7)
   * 						pz1:�ն�ͨ�Ŷ˿ں�(1���ֽ�,1-31)
   * 						pz2:ͨ�Ų�����(0-7�ֱ��ʾ300,600,1200,2400,4800,7200,9600,19200)
   * 						pz3:ֹͣλ(0:1λֹͣλ;1:2λֹͣλ)
   * 						pz4:����У��(0:��;1:��)
   * 						pz5:��żУ��(0:ż;1:��)
   * 						pz6:λ��(0~3�ֱ��ʾ5-8λ)
   *						pz7:���ն˽ӿڶ�Ӧ�˵�ͨ������
   *							
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն�����ͨ��������������F36(AFN=04H)
   * @param 	xzqxm 	String ����������
   * @param 	zddz  	String �ն˵�ַ
   * @param 	csz  	String ����ֵ(cs1)
   * 					cs1:��ͨ����������(0��ʾϵͳ����Ҫ�ն˽�����������)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն������澯��־����F57(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	csz  	String 		����ֵ(cs1) 
   * 					cs1:��������ʱ�α�־(0-23��,�м���"#"����)--ʱ������ģʽ
   * 						����"0#3#5"��ʾ0�㡢3���5����������
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�������������ܱ��쳣�б���ֵ�趨F59(AFN=04H)
   * @param 	xzqxm		String		����������
   * @param 	zddz  		String 		�ն˵�ַ
   * @param 	csz  		String 		����ֵ(cs1;cs2;cs3;cs4)
   * 						cs1:���������ֵ(���ݸ�ʽ22,x.x)
   * 						cs2:���ܱ���߷�ֵ(���ݸ�ʽ22,x.x)
   * 						cs3:���ܱ�ͣ�߷�ֵ(��λ:15min)
   * 						cs4:���ܱ�Уʱ��ֵ(��λ:min)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������г����ֵ����F60(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	csz  	String 		����ֵ(cs1;...;cs8)
   * 					cs1:�ܻ����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
   * 					cs2:���г����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
   * 					cs3:ż��г����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
   * 					cs4:��ż��г����ѹ����������ֵ(pz2#pz4#pz6#...#pz18)
   * 					cs5:�����г����ѹ����������ֵ(pz3#pz5#pz6#...#pz19)
   * 					cs6:�ܻ��������Чֵ����ֵ(��ʽ06,xx.xx,��λ:A)
   * 					cs7:��ż��г��������Чֵ����ֵ(pz2#pz4#pz6#...#pz18)
   * 					cs8:�����г��������Чֵ����ֵ(pz3#pz5#pz6#...#pz19)
   * 
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������ֱ��ģ�����������F61(AFN=04H)
   * @param 	xzqxm		String 		����������
   * @param 	zddz		String 		�ն˵�ַ
   * @param 	csz			String 		����ֵ(cs1)
   * 						cs1:ֱ��ģ���������־(1-8·�����־,1:����;0:������;�磺10100001��ʾ��1/2/8·����)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն�1��������������F65(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	rwh  	String 		�����
   * @param 	csz  	String 		����ֵ(cs1;...;cs4)
   * 					cs1:�ϱ�����(0-31)
   * 					cs2:�ϱ����ڵ�λ(0~3���α�ʾ�֡�ʱ���ա���)
   * 					cs3:�ϱ���׼ʱ��(������ʱ����,yymmddhhmmss)
   * 					cs4:���ñ�־(55����AA����)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն�2��������������F66(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	rwh  	String 		�����
   * @param 	csz  	String 		����ֵ(cs1;...;cs5)
   * 					cs1:�ϱ�����(0-31)
   * 					cs2:�ϱ����ڵ�λ(0~3���α�ʾ�֡�ʱ���ա���)
   * 					cs3:�ϱ���׼ʱ��(������ʱ����,yymmddhhmmss)
   * 					cs4:��ȡ����(1-96)
   * 					cs5:����������(Pm@Fm#...#Pn@Fn)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������1��������������/ֹͣ����F67(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	rwh  	String 		�����
   * @param 	csz  	String 		����ֵ(cs1)
   * 					cs1:����������־(55:������AA��ֹͣ)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������2��������������/ֹͣ����F68(AFN=04H)
   * @param 	xzqxm 	String 		����������
   * @param 	zddz  	String 		�ն˵�ַ
   * @param 	rwh  	String 		�����
   * @param 	csz  	String 		����ֵ(cs1)
   * 					cs1:����������־(55:������AA��ֹͣ)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  							
  
  
  /**����������ֱ��ģ����������F81(AFN=04H)
   * @param 	xzqxm			String 		����������
   * @param 	zddz			String 		�ն˵�ַ
   * @param 	zlmnldkh		String 		ֱ��ģ�����˿ں�
   * @param 	csz				String 		����ֵ(cs1;cs2)
   * 							cs1:ֱ��ģ����������ʼֵ(���ݸ�ʽ02;pz1#pz2#pz3)
   * 								pz1:������־(0:��;1:��)
   * 								pz2:ϵ��(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:��ֵ(xxx)
   * 							cs2:ֱ��ģ����������ֵֹ(���ݸ�ʽ02;pz1#pz2#pz3)
   * 								pz1:������־(0:��;1:��)
   * 								pz2:ϵ��(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:��ֵ(xxx)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������ֱ��ģ����������F82(AFN=04H)
   * @param 	xzqxm			String 		����������
   * @param 	zddz			String 		�ն˵�ַ
   * @param 	zlmnldkh		String 		ֱ��ģ�����˿ں�
   * @param 	csz				String 		����ֵ(cs1;cs2)
   * 							cs1:ֱ��ģ��������(���ݸ�ʽ02;pz1#pz2#pz3)
   * 								pz1:������־(0:��;1:��)
   * 								pz2:ϵ��(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:��ֵ(xxx)
   * 							cs2:ֱ��ģ��������(���ݸ�ʽ02;pz1#pz2#pz3)
   * 								pz1:������־(0:��;1:��)
   * 								pz2:ϵ��(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
   * 								pz3:��ֵ(xxx)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������ֱ��ģ�����������F83(AFN=04H)
   * @param 	xzqxm			String 		����������
   * @param 	zddz			String 		�ն˵�ַ
   * @param 	zlmnldkh		String 		ֱ��ģ�����˿ں�
   * @param 	csz				String 		����ֵ(cs1)
   * 							cs1:ֱ��ģ���������ܶ�
   * 								0:��ʾ������
   * 								1:��ʾ15���Ӷ���һ��
   * 								2:��ʾ30���Ӷ���һ��
   * 								3:��ʾ60���Ӷ���һ��
   * 								254:��ʾ5���Ӷ���һ��
   * 								255:��ʾ1���Ӷ���һ��
   * 								
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ����������ң����բF1(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������(ʮ����)
	 * @param csz
	 *            String csz 0x33:������0xCC:�ر�
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ������������Ƶ������F2(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������(ʮ����)
	 * @param csz
	 *            String csz ����Ƶ��
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
 	 * ������������Ƶ������F2(AFN=05H)
 	 * 
 	 * @param xzqxm
 	 *            String ����������
 	 * @param zddz
 	 *            String �ն˵�ַ
 	 * @param cldh
 	 *            String �������(ʮ����)
 	 * 
 	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
 	 */
   public static String sendAFN05F3(String txfs,String xzqxm, String zddz,String cldh){
 	  try{  	 
 		  return getParse().sendAFN05F3(txfs,xzqxm,zddz,cldh);
 	  }catch(Exception e){
 		  e.printStackTrace();
 		  return null;
 	  }
   }
  

  /**���������������ն������ϱ�F29(AFN=05H)
   * @param xzqxm 	String ����������
   * @param zddz  	String �ն˵�ַ
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendAFN05F29(String txfs,String xzqxm, String zddz){
	  try{  	 
		  return getParse().sendAFN05F29(txfs,xzqxm,zddz);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }
  

  /**�����������ն˶�ʱF31(AFN=05H)
   * @param xzqxm 	String ����������
   * @param zddz  	String �ն˵�ַ
   * @param csz  	String ����(��ʽ��yymmddhhmmss��"XX"��ʾϵͳʱ��)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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

  /**�����������ն˶�ʱF31(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param rq  	String ���� XX��ʾϵͳʱ��
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdds(String txfs,String xzqxm, String zddz,String rq) {
  	try{  	 
  	 return getParse().sendZdds(txfs,xzqxm,zddz,rq);
  	}catch(Exception e){
  		e.printStackTrace();
  		return null;
  	}
  }
  
  /**������������λ����F1/F2/F3(AFN=01H)
   * @param xzqxm 	String ����������
   * @param zddz  	String �ն˵�ַ
   * @param fwlx  	String ��λ����(F1/F2/F3)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdfw(String txfs,String xzqxm, String zddz,String fwlx){
	  try{  	 
		  return getParse().sendZdfw(txfs,xzqxm,zddz,fwlx);
	  }catch(Exception e){
		  e.printStackTrace();
		  return null;
	  }
  }

  /**�����������Ƿ������ն�����վͨ������F27/F35(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sfyx  String �Ƿ�����:1:����0����ֹ
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendSfyxzdyzzth(String txfs,String xzqxm, String zddz, String sfyx) {
  	try{  	 
  	  return getParse().sendSfyxzdyzzth(txfs,xzqxm,zddz,sfyx);
  	}catch(Exception e){
  	  e.printStackTrace();
  	  return null;
  	 }
  }

  /**�����������Ƿ��ն��޳�Ͷ������F28/F36(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sftctr  String �Ƿ��޳�Ͷ��:1:Ͷ�룻0���޳�
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendSfzdtctr(String txfs,String xzqxm, String zddz, String sftctr) {
  	try{  		
  		return getParse().sendSfzdtctr(txfs,xzqxm,zddz,sftctr);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**�����������Ƿ������ն������ϱ�����F29/F37(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sfyxzdsb  String �Ƿ����������ϱ�:1:����0����ֹ
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendSfyxzdzdsb(String txfs,String xzqxm, String zddz, String sfyxzdsb) {
  	try{  		
  		return getParse().sendSfyxzdzdsb(txfs,xzqxm, zddz, sfyxzdsb);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**����������1/2��������������/ֹͣ����F67/F68(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param rwlx  String �������ͣ�1��1�ࣻ2��2�ࣩ
   * @param rwh  String ����� 
   * @param rwqdbz  String ����������־:55:������AA��ֹͣ
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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

  /**�����������ն˱�������F25(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param bdsj  String ����ʱ�� ��ֵ��Χ��0��48����λ��0.5h��0��ʾ�����ڱ��磻AA�����糷��
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdbd(String txfs,String xzqxm, String zddz, String bdsj) {
  	try{  		
  		return getParse().sendZdbd(txfs,xzqxm, zddz, bdsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**�����������ն�ͨ�Ų�������F1(AFN=04H)
   * @param xzqxm 				String 	����������
   * @param zddz  				String 	�ն˵�ַ
   * @param scjyssj  			String 	��������ʱʱ��,��λ:20ms
   * @param fscsyxyssj  		String 	��Ϊ����վ�����ʹ�����ʱʱ��,��λ:����
   * @param ddcdzxycssj  		String 	�ȴ��Ӷ�վ��Ӧ�ĳ�ʱʱ��,0-4095,��λ:��
   * @param cfcs  				String 	�ط�����,0-3;0��ʾ�������ط�
   * @param zdsbzysjjlqrbz  	String 	�����ϱ���Ҫ�¼���¼��ȷ�ϱ�־,1��ʾ��Ҫ��վȷ��
   * @param zdsbybsjjlqrbz  	String 	�����ϱ�һ���¼���¼��ȷ�ϱ�־,1��ʾ��Ҫ��վȷ��
   * @param xtzq  				String 	��������:1-60��
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������վIP��ַ�Ͷ˿�����F2(AFN=04H)
   * @param xzqxm 				String 		����������
   * @param zddz  				String 		�ն˵�ַ
   * @param zyip  				String[] 	����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param byip  				String[] 	����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param wgip  				String[] 	����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param dlip  				String[] 	����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
   * @param apn  				String 		APN(16�ֽڣ�ASCII;��λ��00H��������)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������վ�绰����Ͷ������ĺ�������F4(AFN=04H)
   * @param xzqxm 				String 		����������
   * @param zddz  				String 		�ն˵�ַ
   * @param zzdhhm 				String 		��վ�绰����
   * @param dxzxhm 				String	 	�������ĺ���
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZzdhhm(String txfs,String xzqxm, String zddz, String zzdhhm,String dxzxhm){
  	try{  		
  		return getParse().sendZzdhhm(txfs,xzqxm, zddz, zzdhhm,dxzxhm);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������ն�״̬�������������F12(AFN=04H)
   * @param xzqxm 				String 		����������
   * @param zddz  				String 		�ն˵�ַ
   * @param ztljrbz				String 		״̬�������־(D0-D7��ʾ1-8·,��1:����;��0:������)
   * @param ztlsxbz				String	 	״̬�����Ա�־(D0-D7��ʾ1-8·,��1:a�ʹ���;��0:b�ʹ���)
   * @param ztlgjbz				String	 	״̬���澯��־(D0-D7��ʾ1-8·,��1:��Ҫ�¼�;��0:һ���¼�)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�������������ܱ��쳣�б�ֵ�趨F59(AFN=04H)
   * @param xzqxm 				String 	����������
   * @param zddz  				String 	�ն˵�ַ
   * @param dnlccfz  			String 	���������ֵx.x
   * @param dnbfzfz		  		String 	���ܱ���߷�ֵx.x
   * @param dnbtzfz		  		String 	���ܱ�ͣ�߷�ֵ,��λ:15min
   * @param dnbjsfz 			String 	���ܱ�Уʱ��ֵ,��λ:min
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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

  /**�����������ն���������������F9(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param dnbsl  int ���ܱ�����
   * @param mcsl  int ��������
   * @param mnlsl  int ģ��������
   * @param zjzsl  int �ܼ�������
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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

  /**�����������ն˳���������F24(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param cbjg  int ����������λ�����ӣ�
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdcbjg(String txfs,String xzqxm, String zddz, int cbjg) {
  	try{  		
  		return getParse().sendZdcbjg(txfs,xzqxm, zddz, cbjg);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  
  /**�����������ն˱�����ֵ����F17(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param badz  String ������ֵ��>=1,<=999��
   * @param xs  Sting ϵ�������չ�Լ,�磺000=10E4...��
   * @param zf  String ������0������1����
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdbadz(String txfs,String xzqxm, String zddz, String badz,String xs,String zf) {
  	try{  		
  		return getParse().sendZdbadz(txfs,xzqxm, zddz, badz, xs, zf);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
//  /**�����������ն˹���ʱ������F18(AFN=04H)
//   * @param xzqxm String ����������
//   * @param zddz  String �ն˵�ַ
//   * @param sd  String[][] ʱ��{sd[i][0]:ʱ��(x-y,0-48);
//   * 							sd[i][1]:����״̬(00:�����ƣ�01������1��10������2��11������)}
//   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
//   */
//  public static String sendAFN04F18(String txfs,String xzqxm, String zddz, String[][] sd) {
//  	try{  		
//  		return getParse().sendAFN04F18(txfs,xzqxm, zddz, sd);
//    }catch(Exception e){
//    	e.printStackTrace();
//    	return null;
//    }
//  }
  

 
  
  /**�����������ն˳���������F7(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param day   String �����գ�32�����ȵĶ������ַ���
   * @param time  String ����ʱ�䣨��λ��HHMM��
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն��¼���¼��������F8(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sjjlyxbz  String �¼���¼��Ч��־��64�����ȵĶ������ַ�,�ɸߵ��ͣ�
   * @param sjzyxdjbz  String �¼���Ҫ�Եȼ���־��64�����ȵĶ������ַ�,�ɸߵ��ͣ�
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  
  /**�����������ն�1/2��������������F65/F66(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param rwh   String �����
   * @param fszq  String ��������
   * @param zqdw  String ���ڵ�λ(00���֣�01��ʱ��10���գ�11����)
   * @param fsjzsj  String ���ͻ�׼ʱ��(������ʱ����)
   * @param cqbl  String ��ȡ����
   * @param rwsjx  String[][] ����������(String[i][0]:��Ϣ��Pn;String[i][1]:��Ϣ��Fn)
   * @param rwlx  String ��������(1:1����������;2:2����������)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն˵��ܱ�/��������װ�ò�������F10(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param dnbxx  ArrayList �ն�������ܱ���Ϣ�����ܱ���š����������㡢�˿ںš���Լ���͡�
   *                         ͨѶ��ַ��ͨѶ���롢���ʸ���������λ����[4-7]��С��λ����[1-4]��(��Ϊstring��)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZddnbpz(String txfs,String xzqxm, String zddz, ArrayList dnbxx) {
  	try{  		
  		return getParse().sendZddnbpz(txfs,xzqxm, zddz, dnbxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  /**�����������ն������������F11(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param mcxx  ArrayList �ն������������Ϣ���˿ںš������㡢�������ԡ��������(��Ϊstring��)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdmcpz(String txfs,String xzqxm, String zddz, ArrayList mcxx) {
  	try{  		
  		return getParse().sendZdmcpz(txfs,xzqxm, zddz, mcxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }

  
  /**�����������ն��ܼ�������F14(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzxx  ArrayList �ն������ܼ�����Ϣ
   * 				[1��HashMap:key   = �ܼ����(String)��
   * 						   	value = �ܼӲ�������Ϣ(ArrayList)
   * 				 2���ܼӲ�������ϢArrayList����HashMap,
   * 					�������������(cldh:String);
   * 						 �������־(zfxbz:String)<0:����1:����>;
   * 						 �������־(ysfbz:String)<0:�ӣ�1:��>;
   * 				]
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdzjzpz(String txfs,String xzqxm, String zddz, ArrayList zjzxx) {
  	try{  		
  		return getParse().sendZdzjzpz(txfs,xzqxm, zddz, zjzxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**������������ѯ�ն��¼�F1/F2(AFN=0EH)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sjlx  String �¼�����(1:��Ҫ�¼���2��һ���¼�)
   * @param sjqszz  int �¼���ʼָ��
   * @param sjjszz  int �¼�����ָ��
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String queryZdsj(String txfs,String xzqxm, String zddz,String sjlx,int sjqszz,int sjjszz) {
  	try{  		
  		return getParse().queryZdsj(txfs,xzqxm, zddz,sjlx,sjqszz,sjjszz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
    

  /**���������������������������F25(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param cldh  int �������
   * @param cldjbcs  HashMap ���������������PT��CT�����ѹ�������������߷�ʽ��[��ΪString��]
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������ң��F1/F2(AFN=05H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param lch   	String 	�ִκ�(1-8��)
   * @param ykbz  	String 	ң�ر�־(55:ң����բ��AA�������բ)
   * @param xdsj 	String 	�޵�ʱ��(0-15),��λ:0.5h
   * @param gjyssj 	String 	�澯��ʱʱ��(0-15),��λ:1min
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  
    
  /**�����������ն˵���������F22(AFN=04H)
   * @param xzqxm 	String 			����������
   * @param zddz  	String 			�ն˵�ַ
   * @param fl 		String[14][3] 	fl[0][0]:����1�ķ���(0:��;1:��)
   * 								fl[0][1]:����1�ĵ�λ(0:��;1:Ԫ)
   * 								fl[0][2]:����1��ֵ
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendDnlfl(String txfs,String xzqxm, String zddz, String[][] fl){
  	try{  		
  		return getParse().sendDnlfl(txfs,xzqxm,zddz,fl);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������ն˴߷Ѹ澯����F23(AFN=04H)
   * @param xzqxm 	String 			����������
   * @param zddz  	String 			�ն˵�ַ
   * @param cfgjcs 	String		 	�߷Ѹ澯����:24λ(D23-D0),ÿλ��Ӧ1Сʱ,
   * 								��1�澯,��0���澯,
   * 								����:D0=1��ʾ00:00-01:00�澯
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendCfgjcs(String txfs,String xzqxm, String zddz, String cfgjcs){
  	try{  		
  		return getParse().sendCfgjcs(txfs,xzqxm,zddz,cfgjcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������ն˴߷Ѹ澯Ͷ���־F26/F34(AFN=05H)
   * @param xzqxm 	String 		����������
   * @param zddz  	String 		�ն˵�ַ
   * @param trbz 	String		Ͷ���־��55:Ͷ��;AA:��� 
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendCfgjtrbz(String txfs,String xzqxm, String zddz, String trbz){
  	try{  		
  		return getParse().sendCfgjtrbz(txfs,xzqxm,zddz,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������ն˵���������ʱ�κͷ���������F21(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sd  String[][] ʱ��{sd[i][0]:ʱ��(x-y,0-48);
   * 						   sd[i][1]:����(0000:����1;0001:����2;...;1101:����14)}
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendDnlflsd(String txfs,String xzqxm, String zddz, String[][] sd) {
  	try{  		
  		return getParse().sendDnlflsd(txfs,xzqxm,zddz,sd);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**��������������ִ��趨F48(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  int �ܼ����
   * @param lc String[8] �ִ��ܿ����(lc[0]-lc[7]:��1��-��8��,0:���ܿأ�1���ܿ�)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendDklc(String txfs,String xzqxm, String zddz, int zjzh,String[] lc){
  	try{  		
  		return getParse().sendDklc(txfs,xzqxm,zddz,zjzh,lc);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������µ����ض�ֵ�趨F46(AFN=04H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param zjzh  	int 	�ܼ����
   * @param dz 		String 	��ֵ
   * @param dzfh 	String 	��ֵ����:0:��;1:��
   * @param dzdw	String 	��ֵ��λ:0:kWh;1:MWh
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������������ض�ֵ�趨F47(AFN=04H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param zjzh  	int 	�ܼ����
   * @param gddh 	String 	���絥��
   * @param bz	 	String 	��־:55:׷��;AA:ˢ��
   * @param gdfh	String 	�������:0:��;1:��
   * @param gdz		String 	����ֵ
   * @param bjmxfh	String 	�������޷���:0:��;1:��
   * @param bjmxz	String 	��������ֵ
   * @param tzmxfh	String 	��բ���޷���:0:��;1:��
   * @param tzmxz	String 	��բ����ֵ
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������µ��Ͷ���־�趨F15/F23(AFN=05H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param zjzh  	int 	�ܼ����
   * @param trbz 	String 	Ͷ���־:55:Ͷ��;AA:���
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendYdktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz){
  	try{  		
  		return getParse().sendYdktrbz(txfs,xzqxm,zddz,zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**���������������Ͷ���־�趨F16/F24(AFN=05H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param zjzh  	int 	�ܼ����
   * @param trbz 	String 	Ͷ���־:55:Ͷ��;AA:���
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendGdktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz){
  	try{  		
  		return getParse().sendGdktrbz(txfs,xzqxm,zddz,zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**���������������ִ��趨F45(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  int �ܼ����
   * @param lc String[8] �ִ��ܿ����(lc[0]-lc[7]:��1��-��8��,0:���ܿأ�1���ܿ�)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendGklc(String txfs,String xzqxm, String zddz, int zjzh,String[] lc) {
  	try{  		
  		return getParse().sendGklc(txfs,xzqxm, zddz, zjzh,lc);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�������������ʿ��ƵĹ��ʼ��㻬��ʱ������F43(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  int �ܼ����
   * @param hcsj  String ����ʱ��(1~60)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendAFN04F43(String txfs,String xzqxm, String zddz, int zjzh,String hcsj) {
  	try{  		
  		return getParse().sendAFN04F43(txfs,xzqxm, zddz, zjzh,hcsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**����������ʱ�ι���Ͷ���־����F9/F17(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  int �ܼ����
   * @param trbz  String Ͷ���־(55:Ͷ�룻AA�����)
   * @param fabh  String �������(��trbz=AAʱ��fabh=null)
   * @param trsd  String[] Ͷ��ʱ��(��trbz=AAʱ��trsd=null)(trsd[i]��ʾ��Ͷ���ʱ�κ�)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendAFN05F9F17(String txfs,String xzqxm, String zddz, int zjzh,String trbz,String fabh,String[] trsd) {
  	try{  		
  		return getParse().sendAFN05F9F17(txfs,xzqxm, zddz, zjzh,trbz,fabh,trsd);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  
  /**�������������ݹ���Ͷ���־����F10/F18(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  int �ܼ����
   * @param trbz  String Ͷ���־(55:Ͷ�룻AA�����)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendCxgktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz) {
  	try{  		
  		return getParse().sendCxgktrbz(txfs,xzqxm, zddz, zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**����������Ӫҵ��ͣ��Ͷ���־����F11/F19(AFN=05H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  int �ܼ����
   * @param trbz  String Ͷ���־(55:Ͷ�룻AA�����)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendYybtktrbz(String txfs,String xzqxm, String zddz, int zjzh,String trbz) {
  	try{  		
  		return getParse().sendYybtktrbz(txfs,xzqxm, zddz, zjzh,trbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**����������ʱ�ι��ض�ֵ����F41(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  int �ܼ����
   * @param sd HashMap ʱ�ι��ض�ֵ{key=fah(������ String:1-3);
   * 							  value=sddz(ʱ�ζ�ֵ String[][])
   * 									<
   * 									 �밴ʱ�κ�������;
   * 									 sddz[i][0]:ʱ�κţ�
   * 									 sddz[i][1]:����(0������1����)
   * 									 sddz[i][2]:ʱ�ζ�ֵ��>=1,<=999��
   * 									 sddz[i][3]:ϵ��(���չ�Լ,�磺000=10E4...��
   * 									>}
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendAFN04F41(String txfs,String xzqxm, String zddz, int zjzh,HashMap sd) {
  	try{  		
  		return getParse().sendAFN04F41(txfs,xzqxm, zddz, zjzh,sd);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**�������������ݹ��ز�������F42(AFN=04H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param zjzh  	int 	�ܼ����
   * @param cxkdz  	String 	���ݿض�ֵ��>=1,<=999��
   * @param dzzf  	String 	���ݿض�ֵ����(0������1����)
   * @param dzxs  	String 	���ݿض�ֵϵ��(���չ�Լ,�磺000=10E4...��
   * @param xdqssj  String 	�޵���ʼʱ��(hhmm)
   * @param xdyxsj  String 	�޵�����ʱ��(1~48,��λ��0.5h)
   * @param mzxdr  	String 	ÿ���޵���(7λ�ַ���,D7~D1�ֱ��ʾ���յ���һ)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  
  /**����������Ӫҵ��ͣ�ز�������F44(AFN=04H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param zjzh  	int 	�ܼ����
   * @param btqssj  	String 	��ͣ��ʼʱ��(yymmdd)
   * @param btjssj  	String 	��ͣ����ʱ��(yymmdd)
   * @param btkgldz  	String 	��ͣ�ع��ʶ�ֵ��>=1,<=999��
   * @param dzzf  	String 	��ͣ�ع��ʶ�ֵ����(0������1����)
   * @param dzxs  	String 	��ͣ�ع��ʶ�ֵϵ��(���չ�Լ,�磺000=10E4...��
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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

    
  
  /**������������ѯ��������F1/F2(AFN=0BH)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param rwlx  String ��������(1:1�ࣻ2:2��)
   * @param rwh  int �����
   * @param qssj  String ��ʼʱ��(��ʽ:yymmddhhmm)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String queryRwsj(String txfs,String xzqxm, String zddz, String rwlx,int rwh,String qssj) {
  	try{  		
  		return getParse().queryRwsj(txfs,xzqxm, zddz, rwlx,rwh,qssj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  

  
  /**��������������֪ͨF2(AFN=0FH) �����֣�01
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param wjm  String �ļ���
   * @param wjnr  byte[] �ļ�����
   * @param ip  String IP��ַ
   * @param port  String �˿�
   * @param cxmklx  String ����ģ�����ͣ�01����CPU;02������CPU��
   * @param cxjhsj  String ���򼤻�ʱ�� YYMMDDhhmm
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**��������������ȡ��F2(AFN=0FH) �����֣�02
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param tdlx  String ͨ������ 01������ ��02��GPRS
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static   String sendXzqx(String txfs,String xzqxm, String zddz) {
  	try{  		
  		return getParse().sendXzqx(txfs,xzqxm, zddz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�������������ظ��ĳ��򼤻�ʱ��F2(AFN=0FH) �����֣�03
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param cxjhsj  String ���򼤻�ʱ�� YYMMDDhhmm
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendXzggcxjhsj(String txfs,String xzqxm, String zddz,String cxjhsj) {
  	try{  		
  		return getParse().sendXzggcxjhsj(txfs,xzqxm, zddz,cxjhsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�������������س���汾�л�F2(AFN=0FH) �����֣�04
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static  String sendXzcxbbqh(String txfs,String xzqxm, String zddz) {
  	try{  		
  		return getParse().sendXzcxbbqh(txfs,xzqxm, zddz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**������������ѯ1������(AFN=0AH)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sjxxx  String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ�
   * 								    sjxxx[i][1] ��Ϣ�ࣨFn��
   * ��Ϣ���Ϊ65535��ʾP(FF)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String query_1lsj(String txfs,String xzqxm, String zddz,String[][] sjxxx) {
  	try{  		
  		return getParse().query_1lsj(txfs,xzqxm,zddz,sjxxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**������������ѯ���������ն˵�0CF2(AFN=0CH)
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�1���ɹ���
   */
  public static String query_allzd_0cf2() {
  	try{  		
  		return getParse().query_allzd_0cf2();
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**������������ѯ2����_����(AFN=0DH)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sjxxx  String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ�
   * 								    sjxxx[i][1] ��Ϣ�ࣨFn��
   * ��Ϣ���Ϊ65535��ʾP(FF)
   * @param qssj  String ��ʼʱ�� yymmddhhmm
   * @param sjmd  String �����ܶ� 1��15���ӣ�2��30���ӣ�3��60����
   * @param sjds  String ���ݵ���
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������ѯ2����_�ն���(AFN=0DH)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sjxxx  String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ�
   * 								    sjxxx[i][1] ��Ϣ�ࣨFn��
   * ��Ϣ���Ϊ65535��ʾP(FF)
   * @param rdjsj  String �ն���ʱ�� yymmdd
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������ѯ2����_�¶���(AFN=0DH)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sjxxx  String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ�
   * 								    sjxxx[i][1] ��Ϣ�ࣨFn��
   * ��Ϣ���Ϊ65535��ʾP(FF)
   * @param ydjsj  String �¶���ʱ�� yymm
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������ѯ�ն˲�������(AFN=0AH)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param sjxxx  String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ�
   * 								    sjxxx[i][1] ��Ϣ�ࣨFn��
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String query_zdcspz(String txfs,String xzqxm, String zddz,String[][] sjxxx) {
  	try{  		
  		return getParse().query_zdcspz(txfs,xzqxm,zddz,sjxxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**������������ѯ�м�����(AFN=10H)
   * @param txfs 		String ͨ�ŷ�ʽ(01:����;02:GPRS;06:����)
   * @param xzqxm 		String ����������
   * @param zddz  		String �ն˵�ַ
   * @param dbgylx 		String ����Լ����
   * @param dnbdz  		String ���ܱ��ַ
   * @param dnbsjxdm  	String ���ܱ����������
   * @param btl			String ������(000:��ʾ300;...111:��ʾ19200)
   * @param tzw  		String ֹͣλ(0:1λ;1:2λ)
   * @param jym  		String У����(00:��У��;10:żУ��;11:��У��)
   * @param ws  		String λ��(00-11:��ʾ5-8)
   * @param bwcssj 		String ���ĳ�ʱʱ��(��λ:10ms)
   * @param zjcssj 		String �ֽڳ�ʱʱ��(��λ:10ms)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**�����������ն������澯��־����F57(AFN=04H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param sygjbz  String	�����澯��־:D0-D23��λ��ʾ0-23��,
   * 						ÿλ��ʾһ��Сʱ,�磺0��ʾ00:00-01:00;
   * 						��1��ʾ��Ӧʱ������澯,��0��ʾ��Ӧʱ�β�����澯
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendSygjbz(String txfs,String xzqxm, String zddz, String sygjbz){
  	try{  		
  		return getParse().sendSygjbz(txfs,xzqxm,zddz,sygjbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**����������г����ֵ����F60(AFN=04H)
   * @param xzqxm 	String 		����������
   * @param zddz  	String 		�ն˵�ַ
   * @param xbxz	String[][2]	г����ֵ,����Ϊ:
   * 							�ܻ����ѹ����������ֵ#����; 
   * 							���г����ѹ����������ֵ#����;  
   * 							ż��г����ѹ����������ֵ#����; 
   * 							�ܻ��������Чֵ����ֵ#����;  
   * 							2��г��������Чֵ����ֵ#����;  
   * 								...  
   * 							18��г��������Чֵ����ֵ#����;  
   * 							3��г��������Чֵ����ֵ#����;  
   * 								...  
   * 							19��г��������Чֵ����ֵ#����; 
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendXbxz(String txfs,String xzqxm, String zddz, String[][] xbxz){
  	try{  		
  		return getParse().sendXbxz(txfs,xzqxm,zddz,xbxz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**������������������ֵ��������F26(AFN=04H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param cldh  		String 		�������
   * @param dyhglsx		String 		��ѹ�ϸ�������
   * @param dyhglxx		String 		��ѹ�ϸ�������
   * @param dydxmx		String 		��ѹ��������
   * @param gymx		String 		��ѹ����
   * @param qymx		String 		Ƿѹ����
   * @param glmx		String 		��������#����
   * @param eddlmx		String 		���������#����
   * @param lxdlsx		String 		�����������#����
   * @param szglssx		String 		���ڹ���������
   * @param szglsx		String 		���ڹ�������
   * @param sxdybphxz	String 		�����ѹ��ƽ����ֵ#����
   * @param sxdlbphxz	String 		���������ƽ����ֵ#����
   * @param lxsysjxz	String 		����ʧѹʱ����ֵ
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**���������������㹦�������ֶ���ֵ����F28(AFN=04H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param cldh  		String 		�������
   * @param xz1			String 		��ֵ1#����
   * @param xz2			String 		��ֵ2#����
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**��������������������Ͷ���־F41/F42(AFN=05H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param cldh  		String 		�������
   * @param trbz  		String 		Ͷ���־(55:Ͷ��;AA:�г�)
   * @param drqz		String 		��������(D15-D0,��1��ʾͶ����г�,��0��ʾ����ԭ״)
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������������ϢF32(AFN=05H)
   * @param xzqxm 		String 		����������
   * @param zddz  		String 		�ն˵�ַ
   * @param zl  		String 		������Ϣ����
   * @param bh			String 		������Ϣ���
   * @param hzxx		String 		������Ϣ
   *
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZwxx(String txfs,String xzqxm,String zddz,String zl,String bh,String hzxx){
  	try{  		
  		return getParse().sendZwxx(txfs,xzqxm,zddz,zl,bh,hzxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  
  /**�����������ն��Զ���������F58(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zdbdsj  int �Զ�����ʱ�䣨��λ��Сʱ��
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZdzdbd(String txfs,String xzqxm, String zddz, int zdbdsj) {
  	try{  		
  		return getParse().sendZdzdbd(txfs,xzqxm,zddz,zdbdsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������ܼ������ݶ����������F33(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param zjzh  String �ܼ����
   * @param djcs  String[4] ���������0�������᣻1��15�֣�2��30�֣�3��60�֣�
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZjzsjdjcs(String txfs,String xzqxm, String zddz,String zjzh, String[] djcs) {
  	try{  		
  		return getParse().sendZjzsjdjcs(txfs,xzqxm,zddz,zjzh,djcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�������������������ݶ����������F27(AFN=04H)
   * @param xzqxm String ����������
   * @param zddz  String �ն˵�ַ
   * @param cldh  String �������
   * @param djcs  String[][2] ���������Ͷ�Ӧ�Ķ��������0�������᣻1��15�֣�2��30�֣�3��60�֣�
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendCldsjdjcs(String txfs,String xzqxm, String zddz,String cldh, String[][] djcs) {
  	try{  		
  		return getParse().sendCldsjdjcs(txfs,xzqxm,zddz,cldh,djcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**����������ֱ��ģ�����������F61(AFN=04H)
   * @param xzqxm 				String 		����������
   * @param zddz  				String 		�ն˵�ַ
   * @param jrbz				String 		ֱ��ģ���������־(D0-D7��ʾ1-8·,��1:����;��0:������)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZlmnljrcs(String txfs,String xzqxm, String zddz,String jrbz){
  	try{  		
  		return getParse().sendZlmnljrcs(txfs,xzqxm,zddz,jrbz);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**��������������������F73(AFN=04H)
   * @param xzqxm 	String 			����������
   * @param zddz  	String 			�ն˵�ַ
   * @param cldh  	String 			�������
   * @param drqcs	String[9][5] 	����������(1-9��),����1Ϊ��:
   * 								drqcs[0][0]:���ֱ�־
   * 								drqcs[0][1]:�ֲ����־
   * 								drqcs[0][2]:����װ������
   * 								drqcs[0][3]:����װ������ϵ��
   * 								drqcs[0][4]:����װ����������
   * 								
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendDrqcs(String txfs,String xzqxm, String zddz,String cldh,String[][] drqcs){
  	try{  		
  		return getParse().sendDrqcs(txfs,xzqxm,zddz,cldh,drqcs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**����������������Ͷ�����в���F74(AFN=04H)
   * @param xzqxm 		String 			����������
   * @param zddz  		String 			�ն˵�ַ
   * @param cldh  		String 			�������
   * @param mbglys		String			Ŀ�깦������
   * @param mbglysfh	String			Ŀ�깦����������(0:��;1:��)
   * @param trwgglmx	String			Ͷ���޹���������
   * @param qcwgglmx	String			�г��޹���������
   * @param yssj		String			��ʱʱ��
   * @param dzsjjg		String			����ʱ����
   * 								
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**������������������������F75(AFN=04H)
   * @param xzqxm 		String 			����������
   * @param zddz  		String 			�ն˵�ַ
   * @param cldh  		String 			�������
   * @param gdy			String			����ѹ
   * @param gdyhcz		String			����ѹ�ز�ֵ
   * @param qdy			String			Ƿ��ѹ
   * @param qdyhcz		String			Ƿ��ѹ�ز�ֵ
   * @param dlsx		String			�ܻ����������������
   * @param dlsxfh		String			�ܻ���������������޷���
   * @param dlyxhc		String			�ܻ������������Խ�޻ز�ֵ
   * @param dlyxhcfh	String			�ܻ������������Խ�޻ز�ֵ����
   * @param dysx		String			�ܻ����ѹ����������
   * @param dysxfh		String			�ܻ����ѹ���������޷���
   * @param dyyxhc		String			�ܻ����ѹ������Խ�޻ز�ֵ
   * @param dyyxhczfh	String			�ܻ����ѹ������Խ�޻ز�ֵ����
   * 								
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
  
  /**����������������Ͷ�п��Ʒ�ʽF76(AFN=04H)
   * @param xzqxm 		String 			����������
   * @param zddz  		String 			�ն˵�ַ
   * @param cldh  		String 			�������
   * @param kzfs		String			���Ʒ�ʽ:1:���ؿ���;2:Զ��ң��;3:����;4:����
   * 	
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendDrqtqkzfs(String txfs,String xzqxm, String zddz,String cldh,String kzfs){
  	try{  		
  		return getParse().sendDrqtqkzfs(txfs,xzqxm,zddz,cldh,kzfs);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�������������ظ澯ʱ��F49(AFN=04H)
   * @param xzqxm 	String 	����������
   * @param zddz  	String 	�ն˵�ַ
   * @param lch   	String 	�ִκ�(1-8��)
   * @param gkgjsj 	String 	���ظ澯ʱ��(0-60min)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendGkgjsj(String txfs,String xzqxm,String zddz,String lch,String gkgjsj){
  	try{  		
  		return getParse().sendGkgjsj(txfs,xzqxm,zddz,lch,gkgjsj);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������ն˵�ѹ/����ģ������������F13(AFN=04H)
   * @param xzqxm 	String 		����������
   * @param zddz  	String 		�ն˵�ַ
   * @param mnlxx  	ArrayList 	�ն�����ģ������Ϣ���˿ںš�������š�ģ�������ԣ�(��Ϊstring��)
   * 
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
   */
  public static String sendZddydlmnlpz(String txfs,String xzqxm, String zddz, ArrayList mnlxx){
  	try{  		
  		return getParse().sendZddydlmnlpz(txfs,xzqxm,zddz,mnlxx);
    }catch(Exception e){
    	e.printStackTrace();
    	return null;
    }
  }
  
  /**�����������ն��й��ܵ������Խ���¼���������F15(AFN=04H)
   * @param xzqxm 	String 			����������
   * @param zddz  	String 			�ն˵�ַ
   * @param cs  	String[][7] 	����
   * 								cs[i][0]:�Ա��ܼ����
   * 								cs[i][1]:�����ܼ����
   * 								cs[i][2]:ʱ������
   * 								cs[i][3]:�Աȷ���(0:���;1:����)
   * 								cs[i][4]:�Խ�����ƫ��ֵ(%)
   * 								cs[i][5]:�Խ�޾���ƫ��ֵ(kWh)
   * 								cs[i][6]:�Խ�޾���ƫ��ֵ����(0:��;1:��)
   * 				
   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ������������������������Ϣ F1(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ   �ļ��ľ���·��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
	 * ���������������ļ�����F2(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;cs2;cs3;cs4) 
	 *             cs1:�ܶ���n;
	 *             cs2:��i�α�ʶ��ƫ�ƣ�i=0~n��;
	 *             cs3:��i�����ݳ���Lf;
	 *             cs4:�ļ�����;
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
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
