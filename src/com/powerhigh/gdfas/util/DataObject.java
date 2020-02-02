package com.powerhigh.gdfas.util;

import java.io.Serializable;

public class DataObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String type = "";	//类型：down：下行；up：上行
	public String sjz = "";		//数据帧（十六进制字符串） 
	public int moduleID = -1;	//模块ID
	
	public String xzqxm = "";	//行政区县码
	public String zddz = "";	//终端地址
	
	public String sjzfsseq = "";	//设置序列（通道检测）
	public Integer port=1;//短信模块连接的串口号
	public String SIM = "";		//目的SIM卡号
	public String dxzxhm = "";	//短信中心号码
	public String  content="";//短信内容（用于爱塞得模块）
	public Boolean isChinses=true;//短信内容是否为中文（用于爱塞得模块）
	
	public String txfs = "";//0:不发送;1:COM;2:平台;3:GPRS;4:SMS
	public String gylx = "";//规约类型:1:浙规;2:国规;3:浙版国规
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