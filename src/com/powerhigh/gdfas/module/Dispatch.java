package com.powerhigh.gdfas.module;

import org.apache.log4j.Category;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.parse.receiveDispose;


public class Dispatch{
	//������־
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
//			throw new Exception("����ʧ��:ģ��ID��"+data.moduleID);	
			throw e;
		}
	}
		
	
	//����ϵͳ
	public void downDispatch(String txfs,String gylx,String xzqxm,String zddz,String sSJZ,String sjzfsseq,String SIM) throws Exception{
		DataObject data = new DataObject("down",sSJZ);
		//����������������ն˵�ַ�ӿ���ȡ��ͨѶ��ʽ��ͨѶ��ַ��
		//������		
		
		//���ͣ���ʧ�����ط�CMConfig.RESEND_COUNT��
		//boolean flag = true;
		data.xzqxm = xzqxm;
		data.zddz = zddz;
		data.sjzfsseq = sjzfsseq;
		data.SIM = SIM;
		data.txfs = txfs;//0:������;1:COM;2:ƽ̨;3:GPRS;4:SMS
		data.gylx = gylx;//��Լ����:1:���;2:����;3:������
		data.setModuleID(CMConfig.FRONT_MODULE_ID);
		
//		if(txfs.equalsIgnoreCase("01")){
//			//����
//			data.setModuleID(CMConfig.CMSMS_MODULE_ID);
//		}else if(txfs.equalsIgnoreCase("02")){
//			//GPRS
//			data.setModuleID(CMConfig.GPRS_MODULE_ID);
//		}else if(txfs.equalsIgnoreCase("06")){
//			//����
//			data.setModuleID(CMConfig.SERIAL_MODULE_ID);
//		}else{
//			//����(Ĭ��FRONT)
//			data.setModuleID(CMConfig.FRONT_MODULE_ID);
//		}
		
		
		
		try{
			ModuleManager.getModuleManager().sendMessage(data);
				
		}catch(Exception e){			
//			throw new Exception("����ʧ��:ģ��ID��"+data.moduleID);	
			throw e;
		}
		
	
		
	}
	
	
	
	public  void upDispatch(DataObject data) throws Exception{
		switch(data.moduleID){
			//��ǰ�û�ģ���յ�����(����ǰ�û����ն���)
			case(CMConfig.FRONT_MODULE_ID):
//				data.setModuleID(CMConfig.FRONT_RECEIVE_JMS_MODULE_ID);
//				ModuleManager.getModuleManager().sendMessage(data);
				data.setModuleID(CMConfig.FRONT_MODULE_ID);
				receiveService.run(data);
				break;
					
			//��ǰ�û����ն����յ�����(ֱ�Ӵ���)
			case(CMConfig.FRONT_RECEIVE_JMS_MODULE_ID):
				data.moduleID = CMConfig.FRONT_MODULE_ID;
				receiveService.run(data);
				break;
				
			//��ǰ�û����Ͷ����յ�����(���͵�ǰ�û�)
			case(CMConfig.FRONT_SEND_JMS_MODULE_ID):
				data.setModuleID(CMConfig.FRONT_MODULE_ID);
				ModuleManager.getModuleManager().sendMessage(data);
				break;
				
			//GPRSģ���յ�����(����GPRS���ն���)
			case(CMConfig.GPRS_MODULE_ID):
				data.setModuleID(CMConfig.GPRS_RECEIVE_JMS_MODULE_ID);
				ModuleManager.getModuleManager().sendMessage(data);
				break;
					
			//��GPRS���ն����յ�����(ֱ�Ӵ���)
			case(CMConfig.GPRS_RECEIVE_JMS_MODULE_ID):
				data.moduleID = CMConfig.GPRS_MODULE_ID;				
				receiveService.run(data);
				break;
				
			//��GPRS���Ͷ����յ�����(���͵�GPRSģ��)
			case(CMConfig.GPRS_SEND_JMS_MODULE_ID):
				data.setModuleID(CMConfig.GPRS_MODULE_ID);
				ModuleManager.getModuleManager().sendMessage(data);
				break;
				
			//��SERIALģ���յ�����(ֱ�Ӵ���)
			case(CMConfig.SERIAL_MODULE_ID):
				data.moduleID = CMConfig.SERIAL_MODULE_ID;				
				receiveService.run(data);
				break;
				
		    //��CMSMSģ���յ�����(ֱ�Ӵ���)
			case(CMConfig.CMSMS_MODULE_ID):
				data.moduleID = CMConfig.CMSMS_MODULE_ID;				
				receiveService.run(data);
				break;
				
//			//��AscendSMSģ���յ�����(ֱ�Ӵ���)
//			case(CMConfig.ASCENDSMS_MODULE_ID):
//				data.moduleID = CMConfig.ASCENDSMS_MODULE_ID;			
//			    ModuleManager.getModuleManager().sendMessage(data);
//				break;
				
			default:
				break;
		}
		
	}
}