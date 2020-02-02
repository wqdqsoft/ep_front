package com.powerhigh.gdfas.dbparse.dlt645;

/**
 * Description:  DLT-645������ܱ��Լ����<p>
 * Copyright:    Copyright (c) 2003 LongShine<p>
 * Company:      longshine<p>
 * ��д���ڣ�2006-1-6
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

import org.apache.log4j.*;

import com.powerhigh.gdfas.util.Util;

public class dlt645Parse{

   //������־
   private static final String resource = "cm.log.properties";
   
//   static{
//   		PropertyConfigurator.configure(resource);
//   }
   private static Category cat =
                   Category.getInstance(com.powerhigh.gdfas.dbparse.dlt645.dlt645Parse.class);

    /**֡�Ĺ��캯��*/	
    public dlt645Parse() {
                     
    }
		


	/**�������� :֡����<p>
	 * @param all	String	���������ݱ���
	 * 
	 * @return data	String[][]	����,data[i][0]:���������;data[i][1]:����ֵ(null:��ʾ�쳣Ӧ��)
	 */
    public static String[][] decode(String all)throws Exception{
    	try{
    		String[][] data = null;
    		//ȥ��ǰ���ֽ�FE
    		int idx = all.indexOf("68");
    		all = all.substring(idx);
    		
    		//������
    		String kzm = all.substring(16,18);
    		
    		if(kzm.equals("81")){
    			//����Ӧ��
    			
    			//���ݳ���
    			int len = Integer.parseInt(all.substring(18,20),16);
    			
    			//���������
    			String sjxdm = Util.convertStr(all.substring(20,24));
    			sjxdm = kick33(sjxdm);
    			//����ֵ
    			String sjz = all.substring(24,24+2*(len-2));
    			sjz = kick33(sjz);
    			if(sjxdm.substring(0,1).equalsIgnoreCase("9")){
    				//����������(9XXX),��ʽ:xxxxxx.xx
    				if(sjxdm.substring(3,4).equalsIgnoreCase("F")){
    					//���ݿ�(9XXF)
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
    					//����������
    					data = new String[1][2];
    					data[0][0] = sjxdm;
    					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xxxxxx.xx");
    				}
    			}else if(sjxdm.substring(0,1).equalsIgnoreCase("A")){
    				//�����������(AXXX),��ʽ:xx.xxxx
    				if(sjxdm.substring(3,4).equalsIgnoreCase("F")){
    					//���ݿ�(AXXF)
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
    					//����������
    					data = new String[1][2];
    					data[0][0] = sjxdm;
    					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xxxx");
    				}
    			}else if(sjxdm.substring(0,1).equalsIgnoreCase("B")
    				  && !sjxdm.substring(1,2).equals("2")
    				  && !sjxdm.substring(1,2).equals("3")
    				  && !sjxdm.substring(1,2).equals("4")
    				  && !sjxdm.substring(1,2).equals("6")){
    				//�����������ʱ������(BXXX),��ʽ:MMDDhhmm
    				if(sjxdm.substring(3,4).equalsIgnoreCase("F")){
    					//���ݿ�(BXXF)
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
    					//����������
    					data = new String[1][2];
    					data[0][0] = sjxdm;
    					data[0][1] = sjz.substring(6,8)+"-"
									+sjz.substring(4,6)+" "
									+sjz.substring(2,4)+":"
									+sjz.substring(0,2);
    				}
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B61")){
    				//A��B��C�����ѹ(B611��B612��B613),��ʽ:xxx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = String.valueOf(Integer.parseInt(Util.convertStr(sjz)));
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B62")){
    				//A��B��C�������(B621��B622��B623),��ʽ:xx.xx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xx");
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B63")){
    				//˲ʱ��A��B��C�����й�����(B630��B631��B632��B633),��ʽ:xx.xxxx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xxxx");
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B64")){
    				//˲ʱ��A��B��C�����޹�����(B640��B641��B642��B643),��ʽ:xx.xx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"xx.xx");
    			}else if(sjxdm.substring(0,3).equalsIgnoreCase("B65")){
    				//�ܡ�A��B��C���๦������(B650��B651��B652��B653),��ʽ:x.xxx
					data = new String[1][2];
					data[0][0] = sjxdm;
					data[0][1] = Util.getFloat(Util.convertStr(sjz),"x.xxx");
    			}
    			
    			return data;
    		}else{
    			//�쳣Ӧ��
    			return null;
    		}
    		
              
    	}catch(Exception e){
          throw e; 	
    	}    	
    }
    	
   /**����������֡����<br>
	 * @param dzy	String	��ַ��
	 * @param dzy	String	��ַ��
	 * 
	 * @return all 	String	��������
	*/

     public static String encode(String dzy,String sjxdm)throws Exception{
     	//���������ݱ���
      	String all = "";
      	try{
      		all += "68";;
      		//��ַ��(6�ֽ�,����ĸ�λ��A,��λ���ȴ�)
      		dzy = Util.add(dzy,6,"0");
      		dzy = Util.convertStr(dzy);
      		all += dzy;
      		
      		all += "68";
      		all += "01";//������,01:������
      		
      		int len = 2;
      		//�����򳤶�
      		all += Util.decStrToHexStr(len,1);
      			
      		//������
      		sjxdm = add33(sjxdm);
      		all += Util.convertStr(sjxdm);
      		

      		//У����
      		String cs = Util.getCS(all);
      		all += cs;
      		
      		//��β
      		all += "16";
    	
      		//4��ǰ���ֽ�FE
      		all = "FEFEFEFE" + all;
      		return all;
    	}catch(Exception e){
    		cat.error("֡�����쳣:",e);
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


