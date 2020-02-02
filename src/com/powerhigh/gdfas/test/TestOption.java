package com.powerhigh.gdfas.test;



import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.Util;

public class TestOption {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		System.out.println(Util.tranFormat06("9529"));
//		String [][] sd=new String[][]{new String[]{"0-4","01"},new String[]{"4-15","10"},new String[]{"40-48","01"}};
//		operation.sendAFN04F18("3", "9601", "1234",sd);
		/**
		* @param sd
		 *            Map 时段功控定值{key=fah(方案号 String:1-3); value=sddz(时段定值
		 *            String[][]) < 须按时段号升序排; sddz[i][0]:时段号； sddz[i][1]:正负(0：正；1：负)
		 *            sddz[i][2]:时段定值（>=1,<=999） sddz[i][3]:系数(遵照规约,如：000=10E4...）
		 *            >}
		 */
//		String [][] ssd=new String[][]{new String[]{"1","0","234","000"},
//				 new String[]{"2","0","123","000"},
//			 new String[]{"3","0","122","000"}};
//		HashMap<String, String[][]> sd=new HashMap<String, String[][]>();
//		sd.put("1", ssd);
//		operation.sendAFN04F1("2", "9602", "0001","1;34;99;FF;15");
//		operation.query_zdcspz("3", "9602", "0002", new String[][]{new String []{"F",""}});
		
		
		
//		Map<String, String> map=new HashMap<String, String>();
//		map.put("1", "");
//		map.put("2", "");
//		map.put("3", "");
//		map.put("1", "");
//		map.put("3", "");
//		map.put("1", "");
//		map.put("2", "");
//		map.put("4", "");
//		map.put("5", "");
//		System.out.println(map.keySet().toArray());
//		System.out.println(Util.tranFormat02("2301"));
//		System.out.println("0E">"04");

	}

}
