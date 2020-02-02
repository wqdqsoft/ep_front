package com.powerhigh.gdfas.test;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.module.Dispatch;
import com.powerhigh.gdfas.module.ascendsms.SmsLibrary;

public class TestAscendSms {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
//		System.out.println(SmsLibrary.INSTANCE.SendMsg(1, "aaaa", "15967397199", 0, true));
//		System.out.println(SmsLibrary.INSTANCE.OpenComm(1));
//		System.out.println(SmsLibrary.INSTANCE.SendMsg(1, "aaaa", "15967397199", 0, true));
//		System.out.println(SmsLibrary.INSTANCE.CloseComm(1));
//		try {
//			new Dispatch().sedAscendSms("15967397199", "15967397199", true);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Dispatch dispatch = (Dispatch) Context.ctx.getBean("dispatchService");
		dispatch.sedAscendSms("15967397199", "111", true);

	}

}
