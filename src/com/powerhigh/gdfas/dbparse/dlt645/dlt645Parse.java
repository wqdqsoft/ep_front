package com.powerhigh.gdfas.dbparse.dlt645;

/**
 * Description:  DLT-645部规电能表规约解析<p>
 * Copyright:    Copyright (c) 2003 LongShine<p>
 * Company:      longshine<p>
 * 编写日期：2006-1-6
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

import org.apache.log4j.*;

import com.powerhigh.gdfas.util.Util;

public class dlt645Parse{

   //加载日志
   private static final String resource = "cm.log.properties";
   
//   static{
//   		PropertyConfigurator.configure(resource);
//   }
   private static Category cat =
                   Category.getInstance(com.powerhigh.gdfas.dbparse.dlt645.dlt645Parse.class);

    /**帧的构造函数*/	
    public dlt645Parse() {
                     
    }
		


	/**方法简述 :帧解码<p>
	 * @param all	String	完整的数据报文
	 * 
	 * @return data	String[][]	数据,data[i][0]:数据项代码;data[i][1]:数据值(null:表示异常应答)
	 */
    public static String[][] decode(String all)throws Exception{
    	try{
    		String[][] data = null;
    		//去掉前导字节FE
    		int idx = all.indexOf("68");
    		all = all.substring(idx);
    		
    		//控制码
    		String kzm = all.substring(16,18);
    		
    		if(kzm.equals("81")){
    			//正常应答
    			
    			//数据长度
    			int len = Integer.parseInt(all.substring(18,20),16);
    			
    			//数据项代码
    			String sjxdm = Util.convertStr(all.substring(20,24));
    			sjxdm = kick33(sjxdm);
    			//数据值
    			String sjz = all.substring(24,24+2*(len-2));
    			sjz = kick33(sjz);
    			if(sjxdm.substring(0,1).equalsIgnoreCase("9")){
    				//电能量数据(9XXX),格式:xxxxxx.xx
    				if(sjxdm.substring(3,4).equalsIgnoreCase("F")){
    					//数据块(9XXF)
    					data = new String[14][2];
    					for(int i=0;i<14;i++){
    						String temps = "";
    						temps = sjz.substring(i*8,(i+1)*8);
    						temps = Util.convertStr(temps);
    						temps = Util.getFloat(temps,"xxxxxx.xx");
    						
    						data[i][0] = sjxdm.substring(0,3)+String.valueOf(i).toUpperCase();
    						data[i][1] = temps;
    					}
    				}else{
    					//单个数据项
    					data = new String[1][2];
    					data[0][0] = sjxdm;
    					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xxxxxx.xx");
    				}
    			}else if(sjxdm.substring(0,1).equalsIgnoreCase("A")){
    				//最大需量数据(AXXX),格式:xx.xxxx
    				if(sjxdm.substring(3,4).equalsIgnoreCase("F")){
    					//数据块(AXXF)
    					data = new String[14][2];
    					for(int i=0;i<14;i++){
    						String temps = "";
    						temps = sjz.substring(i*6,(i+1)*6);
    						temps = Util.convertStr(temps);
    						temps = Util.getFloat(temps,"xx.xxxx");
    						
    						data[i][0] = sjxdm.substring(0,3)+String.valueOf(i).toUpperCase();
    						data[i][1] = temps;
    					}
    				}else{
    					//单个数据项
    					data = new String[1][2];
    					data[0][0] = sjxdm;
    					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xxxx");
    				}
    			}else if(sjxdm.substring(0,1).equalsIgnoreCase("B")
    				  && !sjxdm.substring(1,2).equals("2")
    				  && !sjxdm.substring(1,2).equals("3")
    				  && !sjxdm.substring(1,2).equals("4")
    				  && !sjxdm.substring(1,2).equals("6")){
    				//最大需量发生时间数据(BXXX),格式:MMDDhhmm
    				if(sjxdm.substring(3,4).equalsIgnoreCase("F")){
    					//数据块(BXXF)
    					data = new String[14][2];
    					for(int i=0;i<14;i++){
    						String temps = "";
    						temps = sjz.substring(i*8,(i+1)*8);
    						temps = temps.substring(6,8)+"-"
								   +temps.substring(4,6)+" "
							       +temps.substring(2,4)+":"
							       +temps.substring(0,2);
    						
    						data[i][0] = sjxdm.substring(0,3)+String.valueOf(i).toUpperCase();
    						data[i][1] = temps;
    					}
    				}else{
    					//单个数据项
    					data = new String[1][2];
    					data[0][0] = sjxdm;
    					data[0][1] = sjz.substring(6,8)+"-"
									+sjz.substring(4,6)+" "
									+sjz.substring(2,4)+":"
									+sjz.substring(0,2);
    				}
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B61")){
    				//A、B、C三相电压(B611、B612、B613),格式:xxx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = String.valueOf(Integer.parseInt(Util.convertStr(sjz)));
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B62")){
    				//A、B、C三相电流(B621、B622、B623),格式:xx.xx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xx");
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B63")){
    				//瞬时、A、B、C三相有功功率(B630、B631、B632、B633),格式:xx.xxxx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xxxx");
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B64")){
    				//瞬时、A、B、C三相无功功率(B640、B641、B642、B643),格式:xx.xx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xx");
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B65")){
    				//总、A、B、C三相功率因数(B650、B651、B652、B653),格式:x.xxx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"x.xxx");
    			}
    			
    			return data;
    		}else{
    			//异常应答
    			return null;
    		}
    		
              
    	}catch(Exception e){
          throw e; 	
    	}    	
    }
    	
   /**方法简述：帧编码<br>
	 * @param dzy	String	地址域
	 * @param dzy	String	地址域
	 * 
	 * @return all 	String	完整报文
	*/

     public static String encode(String dzy,String sjxdm)throws Exception{
     	//完整的数据报文
      	String all = "";
      	try{
      		all += "68";;
      		//地址域(6字节,不足的高位补A,低位在先传)
      		dzy = Util.add(dzy,6,"0");
      		dzy = Util.convertStr(dzy);
      		all += dzy;
      		
      		all += "68";
      		all += "01";//控制码,01:读数据
      		
      		int len = 2;
      		//数据域长度
      		all += Util.decStrToHexStr(len,1);
      			
      		//数据域
      		sjxdm = add33(sjxdm);
      		all += Util.convertStr(sjxdm);
      		

      		//校验码
      		String cs = Util.getCS(all);
      		all += cs;
      		
      		//结尾
      		all += "16";
    	
      		//4个前导字节FE
      		all = "FEFEFEFE" + all;
      		return all;
    	}catch(Exception e){
    		cat.error("帧编码异常:",e);
    		throw e; 
    	}
     } 
     
     private static String add33(String src){
     	String sjxdm = "";
		byte[] bt = Util.strstobyte(src);
		for(int i=0;i<bt.length;i++){
			bt[i] += 0x33;
		}
		sjxdm = Util.bytetostrs(bt);
		
		return sjxdm;
     }
     
     private static String kick33(String src){
     	String sjxdm = "";
		byte[] bt = Util.strstobyte(src);
		for(int i=0;i<bt.length;i++){
			bt[i] -= 0x33;
		}
		sjxdm = Util.bytetostrs(bt);
		
		return sjxdm;
     }
}


