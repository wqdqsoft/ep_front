package com.powerhigh.gdfas.module;

import org.apache.log4j.Category;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.parse.receiveDispose;


public class Dispatch{
	//加载日志
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.Dispatch.class);
//	static {
//	  PropertyConfigurator.configure(resource);
//	}
	
	
	private receiveDispose receiveService = null;
	
	public receiveDispose getReceiveService() {
		return receiveService;
	}
	public void setReceiveService(receiveDispose dis) {
		receiveService = dis;
	}
	  
	public Dispatch(){
		
	}
	
	public void sedAscendSms(String sim,String content,Boolean isChinese)throws Exception{
		DataObject data = new DataObject("down");
//		data.port=port;
		data.SIM=sim;
		data.content=content;
		data.isChinses=isChinese;
		data.setModuleID(CMConfig.ASCENDSMS_MODULE_ID);
		try{
			ModuleManager.getModuleManager().sendMessage(data);
				
		}catch(Exception e){			
//			throw new Exception("发送失败:模块ID＝"+data.moduleID);	
			throw e;
		}
	}
		
	
	//国电系统
	public void downDispatch(String txfs,String gylx,String xzqxm,String zddz,String sSJZ,String sjzfsseq,String SIM) throws Exception{
		DataObject data = new DataObject("down",sSJZ);
		//根据行政区县码和终端地址从库里取出通讯方式、通讯地址等
		//待完善		
		
		//发送，若失败则重发CMConfig.RESEND_COUNT次
		//boolean flag = true;
		data.xzqxm = xzqxm;
		data.zddz = zddz;
		data.sjzfsseq = sjzfsseq;
		data.SIM = SIM;
		data.txfs = txfs;//0:不发送;1:COM;2:平台;3:GPRS;4:SMS
		data.gylx = gylx;//规约类型:1:浙规;2:国规;3:浙版国规
		data.setModuleID(CMConfig.FRONT_MODULE_ID);
		
//		if(txfs.equalsIgnoreCase("01")){
//			//短信
//			data.setModuleID(CMConfig.CMSMS_MODULE_ID);
//		}else if(txfs.equalsIgnoreCase("02")){
//			//GPRS
//			data.setModuleID(CMConfig.GPRS_MODULE_ID);
//		}else if(txfs.equalsIgnoreCase("06")){
//			//串口
//			data.setModuleID(CMConfig.SERIAL_MODULE_ID);
//		}else{
//			//其它(默认FRONT)
//			data.setModuleID(CMConfig.FRONT_MODULE_ID);
//		}
		
		
		
		try{
			ModuleManager.getModuleManager().sendMessage(data);
				
		}catch(Exception e){			
//			throw new Exception("发送失败:模块ID＝"+data.moduleID);	
			throw e;
		}
		
	
		
	}
	
	
	
	public  void upDispatch(DataObject data) throws Exception{
		switch(data.moduleID){
			//从前置机模块收到数据(放入前置机接收队列)
			case(CMConfig.FRONT_MODULE_ID):
//				data.setModuleID(CMConfig.FRONT_RECEIVE_JMS_MODULE_ID);
//				ModuleManager.getModuleManager().sendMessage(data);
				data.setModuleID(CMConfig.FRONT_MODULE_ID);
				receiveService.run(data);
				break;
					
			//从前置机接收队列收到数据(直接处理)
			case(CMConfig.FRONT_RECEIVE_JMS_MODULE_ID):
				data.moduleID = CMConfig.FRONT_MODULE_ID;
				receiveService.run(data);
				break;
				
			//从前置机发送队列收到数据(发送到前置机)
			case(CMConfig.FRONT_SEND_JMS_MODULE_ID):
				data.setModuleID(CMConfig.FRONT_MODULE_ID);
				ModuleManager.getModuleManager().sendMessage(data);
				break;
				
			//GPRS模块收到数据(放入GPRS接收队列)
			case(CMConfig.GPRS_MODULE_ID):
				data.setModuleID(CMConfig.GPRS_RECEIVE_JMS_MODULE_ID);
				ModuleManager.getModuleManager().sendMessage(data);
				break;
					
			//从GPRS接收队列收到数据(直接处理)
			case(CMConfig.GPRS_RECEIVE_JMS_MODULE_ID):
				data.moduleID = CMConfig.GPRS_MODULE_ID;				
				receiveService.run(data);
				break;
				
			//从GPRS发送队列收到数据(发送到GPRS模块)
			case(CMConfig.GPRS_SEND_JMS_MODULE_ID):
				data.setModuleID(CMConfig.GPRS_MODULE_ID);
				ModuleManager.getModuleManager().sendMessage(data);
				break;
				
			//从SERIAL模块收到数据(直接处理)
			case(CMConfig.SERIAL_MODULE_ID):
				data.moduleID = CMConfig.SERIAL_MODULE_ID;				
				receiveService.run(data);
				break;
				
		    //从CMSMS模块收到数据(直接处理)
			case(CMConfig.CMSMS_MODULE_ID):
				data.moduleID = CMConfig.CMSMS_MODULE_ID;				
				receiveService.run(data);
				break;
				
//			//从AscendSMS模块收到数据(直接处理)
//			case(CMConfig.ASCENDSMS_MODULE_ID):
//				data.moduleID = CMConfig.ASCENDSMS_MODULE_ID;			
//			    ModuleManager.getModuleManager().sendMessage(data);
//				break;
				
			default:
				break;
		}
		
	}
}