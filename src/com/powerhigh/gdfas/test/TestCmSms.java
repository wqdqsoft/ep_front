package com.powerhigh.gdfas.test;


import com.powerhigh.gdfas.module.ascendsms.ReceiveSmsThread;
import com.powerhigh.gdfas.module.ascendsms.SerialParameters;
import com.powerhigh.gdfas.module.ascendsms.SmsLibrary;
import com.sun.jna.Memory;



public class TestCmSms {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		SerialParameters parameters = new SerialParameters();
		parameters.setPortName("COM1");
		parameters.setBaudRate(9600);
		parameters.setDatabits(8);
		parameters.setStopbits(1);
		parameters.setParity(0);
		Integer v=SmsLibrary.INSTANCE.OpenComm(1);
		if(v==0){
			System.out.println("短信模块打开成功!");
		}
		
		Memory memory=new Memory(20);
		boolean result=SmsLibrary.INSTANCE.GetSerialNo(1, memory);
		System.out.println(String.valueOf(result));
		System.out.println(memory.getString(0));
//		ReceiveSmsThread thread=new ReceiveSmsThread();
//		thread.start();
//		System.out.println(SmsLibrary.INSTANCE.SendMsg(1, "aaaa", "15967397199", 0, true));
	}
	

}
