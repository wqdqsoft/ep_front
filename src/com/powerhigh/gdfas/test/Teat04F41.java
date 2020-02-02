package com.powerhigh.gdfas.test;

import com.powerhigh.gdfas.util.Util;

public class Teat04F41 {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
//		String csz = "1234567891234";
//		System.out.println(csz.substring(8));
//		System.out.println(Util.binStrToHexStr("111", 1));
		
//		String csz="0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000;0#1#000";
//		Teat04F41.getCode04F18(csz);
		byte[] ss=new byte[8];
		String s=Util.hexStrToBinStr("08", 1);
		System.out.println(s.substring(4,8));
//		for(int i=0;i<ss.length;i++){
//			System.out.println(ss[i]);
//		}

	}
	/**
	 *       @param csz 参数值(cs1;cs2.....csn)1<=n<=24
	 *        csn(zfn#dzn#xsn)
	 *           zfn 正负:0正数,1负数
	 *           dzn 定值整数:1-999
	 *           xsn 系数:000:10E4
	 *                    001:10E3
	 *                    010:10E2
	 *                    011:10E1
	 *                    100:10E0
	 *                    101:10E-1
	 *                    110:10E-2
	 *                    111:10E-3
	 */
	public static void getCode04F18(String csz) throws Exception{
		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		
		//方案标志
		int fnbz;
		if(ss_csz.length%8!=0){
			fnbz=((int)(ss_csz.length/8))+1;
		}else{
			fnbz=(int)(ss_csz.length/8);
		}
		data+=Util.decStrToHexStr(fnbz, 1);
		//定值
		String dzs="";
		for(int n=0;n<ss_csz.length;n++){
			String cszn[]=ss_csz[n].split("#");
			dzs+=Util.makeFormat02(cszn[1], cszn[2], cszn[0]);
		}
		if(1==fnbz){
			//时段号1
			String sdh1="";
			for(int i=0;i<ss_csz.length;i++){
				sdh1="1"+sdh1;
			}
			sdh1=Util.binStrToHexStr(sdh1, 1);
			data=data+sdh1+dzs;
		}
		if(2==fnbz){
			//时段1定值
			String sd1dz=dzs.substring(0,32);
			//时段2定值
			String sd2dz=dzs.substring(32);
			//时段号1,2
			String sdh1="";
			String sdh2="";
			for(int i=0;i<ss_csz.length-8;i++){
				sdh2="1"+sdh2;
			}
			sdh1=Util.binStrToHexStr("11111111", 1);
			sdh2=Util.binStrToHexStr(sdh2, 1);
			data=data+sdh1+sd1dz+sdh2+sd2dz;
			
		}
		if(3==fnbz){
			//时段1定值
			String sd1dz=dzs.substring(0,32);
			//时段2定值
			String sd2dz=dzs.substring(32,64);
			//时段3定值
			String sd3dz=dzs.substring(64);
			//时段号1,2,3
			String sdh1="";
			String sdh2="";
			String sdh3="";
			for(int i=0;i<ss_csz.length-16;i++){
				sdh3="1"+sdh3;
			}
			sdh1=Util.binStrToHexStr("11111111", 1);
			sdh2=Util.binStrToHexStr("11111111", 1);
			sdh3=Util.binStrToHexStr(sdh3, 1);
			data=data+sdh1+sd1dz+sdh2+sd2dz+sdh3+sd3dz;
		}
		System.out.println(data);
	}

}
