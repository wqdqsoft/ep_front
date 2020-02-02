package com.powerhigh.gdfas.test;

import java.math.BigDecimal;
import java.util.HashMap;

import org.springframework.context.ApplicationContext;

import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.Util;

/** 
 * Description: 电能表规约（省规）帧解析类<p>
 * Copyright:    Copyright   2003 LongShine<p>
 * 编写时间: 2003-11-10
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
 
public class test {		
	public static ApplicationContext ctx = null;
	
	public static String decStrToHexStr(long value,int len){
	   	String s = Long.toHexString(value);
	   	int s_len = s.length();
	   	for(int i=0;i<2*len-s_len;i++){
	   		s = "0" + s;
	   	}
	   	return s.toUpperCase();
	   }
	
	public static void main(String[] args) throws Exception{
		
		/**方法简述：终端1类数据任务设置F65(AFN=04H)
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
		String csz = "1;1;091120120000;1;P1@F25";
//		operation.sendAFN04F65("00", "9680", "07DA","1", csz);
		
		String[][] ss = new String[][]{{"0","F3"},{"0","F4"}};
		ss = new String[][]{{"0","F3"}};
//		operation.query_zdcspz("00", "9680", "07DA",ss );
//		operation.query_1lsj("00", "9680", "07DA",ss);
//		operation.queryZdsj("00","9680", "07DA", "1", 1, 2);
//		operation.query_2lsj_rdj("00", "9680", "07DA", new String[][]{{"1","F1"}}, "091120");		
//		System.out.println(Util.bytetostrs("a".getBytes()));
//		System.out.println(Util.getASCII("61"));
//		System.out.println(Util.hexStrToBinStr(Util.convertStr("61"), 1));
		
		System.out.println(Util.getDT("F81"));
		
		System.out.println(Util.getCS("C88096DA07020B6E010002000100010A001520110801050000110000120000930000940000950100020A00152011080105000011000012000093000094000095"));
		
		System.out.println(Util.getLEN(12));
		
		System.out.println(Util.getDA(2));
		System.out.println(Util.getDA("P1"));
		System.out.println(Util.addMinute("0912062000", 120));
		
		String temps = Util.hexStrToBinStr("40",1);

		long l = 6;
		l = l*10000;
		String s = String.valueOf(l);
		double d =6000000;
		Double D = new Double(d);

        System.out.println(Util.makeFormat02("4","000","0"));


        String ts = "P8";
        int is = 65535;
        System.out.println("P:"+Util.getDA(is));
        
        System.out.println(Util.tranDA(Util.getDA(is)));
        
        HashMap hm = new HashMap();
        hm.values();

	}
				
	
}

