package com.powerhigh.gdfas.util;

import java.io.Serializable;

public class DataObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type = "";	//���ͣ�down�����У�up������
	public String sjz = "";		//����֡��ʮ�������ַ����� 
	public int moduleID = -1;	//ģ��ID
	
	public String xzqxm = "";	//����������
	public String zddz = "";	//�ն˵�ַ
	
	public String sjzfsseq = "";	//�������У�ͨ����⣩
	public Integer port=1;//����ģ�����ӵĴ��ں�
	public String SIM = "";		//Ŀ��SIM����
	public String dxzxhm = "";	//�������ĺ���
	public String  content="";//�������ݣ����ڰ�����ģ�飩
	public Boolean isChinses=true;//���������Ƿ�Ϊ���ģ����ڰ�����ģ�飩
	
	public String txfs = "";//0:������;1:COM;2:ƽ̨;3:GPRS;4:SMS
	public String gylx = "";//��Լ����:1:���;2:����;3:������
	public DataObject(){
		
	}
	public DataObject(String type,String sjz){
		this.type = type;
		this.sjz = sjz;
	}
	public DataObject(String type){
		this.type = type;
	}
	
	public void setModuleID(int id){
		this.moduleID = id;
	}
	
	
}