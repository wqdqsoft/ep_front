package com.powerhigh.gdfas.test;

import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.parse.Decode_0A;
import com.powerhigh.gdfas.parse.Decode_0F_ReSend;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.CMXmlR;
import com.powerhigh.gdfas.util.Util;

public class Test0A {
	public static String overtime = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_OVERTIME);//超时时间
	public static String resend_count = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_RESEND_COUNT);//重发次数

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		//System.out.println(Util.tranFormat02("85E3"));
		//等待时间
//		int ddsj=Integer.parseInt(overtime)/Integer.parseInt(resend_count);
//		System.out.println(ddsj);
//		int i_count=Integer.parseInt(resend_count);
//		for(int i=0;i<i_count;i++){
//			Thread.sleep(new Long(ddsj));
//			System.out.println("重发>>>>>>>"+i);
//		}
		
//		Decode_0F_ReSend ss=new Decode_0F_ReSend(s_xzqxm, s_zddz, jdbcT, dqxh);
//		ss.run();
		
		
	}

}
