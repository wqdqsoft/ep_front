package com.powerhigh.gdfas.parse;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.CMXmlR;
import com.powerhigh.gdfas.util.DateUtil;
import com.powerhigh.gdfas.util.Util;

/**
 * Description: AFN=00(终端响应主站的控制命令和设置命令———返回处理)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * 编写时间: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 修改人： 修改时间：
 */

public class Decode_00 {
	public static String buffer = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
			CMConfig.SYSTEM_DOWNLOADBUFFER_KEY);// 每次下载数据的大小
	public static String overtime = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_OVERTIME);//超时时间
	public Decode_00() {

	}

	public static void dispose(String s_xzqxm, String s_zddz, String sSJZ,
			String s_csdata, String s_sjzfsseq, JdbcTemplate jdbcT)
			throws Exception {
		//终端响应主站的控制命令和设置命令
		String s_dadt = s_csdata.substring(16, 24);

		//信息点Pn
		String s_da = s_dadt.substring(0, 4);
		s_da = Util.tranDA(Util.convertStr(s_da));
		String s_Pda = "P" + s_da;
		//信息类Fn
		String s_dt = s_dadt.substring(4, 8);
		s_dt = Util.tranDT(Util.convertStr(s_dt));
		String s_Fdt = "F" + s_dt;
		//PnFn
		String s_PF = s_Pda + s_Fdt;

		String s_sql = "";
		String[] params = null;
		//确认/否认的数据单元标识
		if (s_PF.equalsIgnoreCase("P0F1")) {
			//F1全部确认
			s_sql = "update g_sjzfssjdybszb set cwdm=? where sjzfsseq=?";
			params = new String[] {"XX", s_sjzfsseq };
			jdbcT.update(s_sql, params);

			s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
					+ "where sjzfsseq=?";
			params = new String[] { "01",sSJZ, s_sjzfsseq };
			jdbcT.update(s_sql, params);

			//根据参数"设置暂存表" 写 "终端运行参数配置表"
			fromSzzcbToZdyxcspzb(jdbcT, s_xzqxm, s_zddz, s_sjzfsseq);

		} else if (s_PF.equalsIgnoreCase("P0F2")) {
			//F2全部否认
			s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
					+ "where sjzfsseq=?";
			params = new String[] {"03", sSJZ, s_sjzfsseq };
			jdbcT.update(s_sql, params);

			s_sql = "update g_sjzfssjdybszb set cwdm=? where sjzfsseq=?";
			params = new String[] { "YY",s_sjzfsseq };
			jdbcT.update(s_sql, params);

		} else if (s_PF.equalsIgnoreCase("P0F3")) {
			//F3逐个确认否认
			s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
					+ "where sjzfsseq=?";
			params = new String[] { "04",sSJZ, s_sjzfsseq };
			jdbcT.update(s_sql, params);

			s_sql = "select count(1) count from g_sjzfssjdybszb where sjzfsseq='"
					+ s_sjzfsseq + "'";
			int i_count = Util.getRecordCount(s_sql, jdbcT);
			int i_index = 24;
			for (int i = 0; i < i_count; i++) {
				String temp_s = s_csdata.substring(i_index, i_index + 10);
				String temp_dadt = temp_s.substring(2, 4)
						+ temp_s.substring(0, 2) + temp_s.substring(6, 8)
						+ temp_s.substring(4, 6);
				String temp_err = temp_s.substring(8, 10);
				s_sql = "update g_sjzfssjdybszb set cwdm=? "
						+ "where sjzfsseq=? and sjdybsz=?";
				params = new String[] { temp_err, s_sjzfsseq, temp_dadt };
				jdbcT.update(s_sql, params);

				i_index = i_index + 10;
			}
		}
	}
    
	
	/**
	 * 
	* @Title: fromSzzcbToZdyxcspzb
	* @Description: TODO(成功返回之后需要操作的事情)
	* @param @param jdbcT
	* @param @param s_xzqxm
	* @param @param s_zddz
	* @param @param s_sjzfsseq
	* @param @throws Exception    设定文件
	* @return void    返回类型
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	private static void fromSzzcbToZdyxcspzb(JdbcTemplate jdbcT,
			String s_xzqxm, String s_zddz, String s_sjzfsseq) throws Exception {
		String s_sql = "";
		String[] params = null;
		try {
			//根据终端地址取终端ID
			s_sql = "select zdid from G_ZDGZ where xzqxm=? and zddz=?";
			params = new String[]{s_xzqxm,s_zddz};
			List zdidlst = jdbcT.queryForList(s_sql, params);
			if(zdidlst==null || zdidlst.size()==0){
				return;
			}
			String s_zdid = String.valueOf(((Map)zdidlst.get(0)).get("zdid"));
			
			s_sql = "select sjxdm,sjz from g_csszzcb where sjzfsseq=?";
			params = new String[] { s_sjzfsseq };
			List lst = jdbcT.queryForList(s_sql, params);
			for (int i = 0; i < lst.size(); i++) {
				Map hm = (Map) lst.get(i);
				String sjxdm = String.valueOf(hm.get("sjxdm"));
				String sjz = String.valueOf(hm.get("sjz"));

				if (sjxdm.equals("AFN05F1")) {
					//测量点基本参数(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//写终端测量点参数配置表
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					//ss=Util.hexStrToDecStr(ss);
					
					s_sql="select zdxh from g_zdgz where zdid=?";
					params = new String[] { s_zdid };
				    List cldList = jdbcT.queryForList(s_sql, params);
				    Map cldMap = (Map) cldList.get(0);
				    // 终端型号
				 	String zdxh = String.valueOf(cldMap.get("zdxh"));
				 	String sbqtzt="";
				 	if("33".equalsIgnoreCase(ss)){
				 		sbqtzt="1";
				 	}else if("CC".equalsIgnoreCase(ss)){
				 		sbqtzt="0";
				 	}
				 	if("1".equalsIgnoreCase(zdxh)){
				 		//如果是一代终端
				 		s_sql = "update g_zdclddqsjb "
								+"set sbqtzt=? "
								+" where cldid=(select id from g_zdcldpzb where zdid=? and cldh=?)";
							params = new String[]{sbqtzt,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}else{
				 		//如果是二代终端
				 		s_sql = "update g_zddqsbpzb "
								+"set zt=? "
								+" where  zdid=? and cldh=? ";
							params = new String[]{sbqtzt,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}
				 	
				 	//将终端关联的集水池设置为手动状态
				 	s_sql = "update m_sump s set s.isauto=0 where id =(select sumpid from g_zdgz where zdid=?)";
					params = new String[] { s_zdid };
					jdbcT.update(s_sql, params);
					
					

				}else if (sjxdm.equals("AFN05F2")) {
					//测量点基本参数(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//写终端测量点参数配置表
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					ss=Util.hexStrToDecStr(ss);
					
					s_sql="select zdxh from g_zdgz where zdid=?";
					params = new String[] { s_zdid };
				    List cldList = jdbcT.queryForList(s_sql, params);
				    Map cldMap = (Map) cldList.get(0);
				    // 终端型号
				 	String zdxh = String.valueOf(cldMap.get("zdxh"));
				 	
				 	if("1".equalsIgnoreCase(zdxh)){
				 		//如果是一代终端
				 		s_sql = "update g_zdclddqsjb "
								+"set sbyxpl=? "
								+" where cldid=(select id from g_zdcldpzb where zdid=? and cldh=?)";
							params = new String[]{ss,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}else{
				 		//如果是二代终端
				 		s_sql = "update g_zddqsbpzb "
								+"set sbyxpl=? "
								+" where  zdid=? and cldh=? ";
							params = new String[]{ss,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}
					
					

				}else if (sjxdm.equals("AFN04F25")) {
					//测量点基本参数(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//写终端测量点参数配置表
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String[] ss = cld[1].split(";");
					
					s_sql = "update g_zdcldpzb "
						+"set pt=?,ct=?,ldlljz=? "
						+" where zdid=? and cldh=?";
					params = new String[]{ss[0],ss[1],ss[2],s_zdid,cldh};
					jdbcT.update(s_sql, params);

				}else if (sjxdm.equals("AFN04F15")) {
					//测量点基本参数(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//写终端测量点参数配置表
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					
					s_sql = "update g_zdcldpzb "
						+"set afn04f15=? "
						+" where zdid=? and cldh=?";
					params = new String[]{ss.substring(4,ss.length()),s_zdid,cldh};
					jdbcT.update(s_sql, params);
					
					///////更新二代配置
					s_sql = "select cldh from g_zdsbkzcsb where zdid=?";
	      	        params = new String[]{s_zdid};
	          		List dnbList = jdbcT.queryForList(s_sql,params);
					String[] f15s=ss.split(";");
					String cldIn = "";
	      			for(int n=0;n<f15s.length;n++){
	      			    //1、序号
	      				String pz1 = f15s[n].split(",")[0];
	          			
	          			//2、测量点号
	      				String pz2 = f15s[n].split(",")[1];
	          			
	          			//3、输出硬件号
	      				String pz3 = f15s[n].split(",")[2];
	          			
	          			
	          		    //4、备用输出硬件号
	      				String pz4 = f15s[n].split(",")[3];
	          			
	          			//5、同时工作使能
	      				String pz5 = f15s[n].split(",")[4];
	          			
	          		    //6、主备切换时间-小时
	      				String pz6 = f15s[n].split(",")[5];
	          			
	          		    //7、池体号码
	      				String pz7 = f15s[n].split(",")[6];

	          		    //8、水位档位
	      				String pz8 = f15s[n].split(",")[7];

	          		    //9、逻辑关系
	      				String pz9 = f15s[n].split(",")[8];

	          		    //10、另一个池体号码
	      				String pz10 = f15s[n].split(",")[9];

	          		    //11、水位档位
	      				String pz11 = f15s[n].split(",")[10];

	          		    //12、控制动作
	      				String pz12 = f15s[n].split(",")[11];

	          		    //13、最小温度
	      				String pz13 = f15s[n].split(",")[12];

	          		    //14、最大温度
	      				String pz14 = f15s[n].split(",")[13];
	          			
	          		    //15、启动时间
	      				String pz15 = f15s[n].split(",")[14];
	          			
	          		    //16、停止时间
	      				String pz16 = f15s[n].split(",")[15];
	          			
	          			boolean isIn = false;
	          			for(int j=0;j<dnbList.size();j++){
	          				Map tempHM = (Map)dnbList.get(j);
	          				if(String.valueOf(tempHM.get("cldh")).equals(pz2)){
	          					isIn = true;
	          					break;
	          				}
	          			}
	          			
	          			//写"配置表"
	          			if(isIn == true){
	          				//主站里有该测量点的配置
	          				s_sql = "update g_zdsbkzcsb set scyjh=?,byscyjh=?,tsgzsn=?,zbqhsj=?,cthm1=?,swdw1=?,ljgx=?,cthm2=?,swdw2=?,kzdz=?,zxwd=?,zdwd=?,qdsj=?,tzsj=? where zdid=? and cldh=?";
	        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)||"CC".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)||"CC".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)||"CCCC".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)||"CCCC".equalsIgnoreCase(pz16)?"":pz16,s_zdid,pz2};
	          			}else if(isIn == false){
	          				//主站里无该测量点的配置
	          				s_sql = "insert into g_zdsbkzcsb(id,zdid,xh,cldh,scyjh,byscyjh,tsgzsn,zbqhsj,cthm1,swdw1,ljgx,cthm2,swdw2,kzdz,zxwd,zdwd,qdsj,tzsj) "
	        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	        	        	params = new String[]{s_zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)||"CC".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)||"CC".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)||"CCCC".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)||"CCCC".equalsIgnoreCase(pz16)?"":pz16};
	          			}
	          			jdbcT.update(s_sql,params);
	          			
	          			cldIn += "'"+pz2+"',";
	          			          			
	      			}
	      			
	                 
	      			//删除其它配置
	      			s_sql = "delete g_zdsbkzcsb where zdid=?  ";
	      			if(f15s.length>0){
	      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
	      			}
	      	        params = new String[]{s_zdid};
	                jdbcT.update(s_sql,params); 
					
					
					

				}else if (sjxdm.equals("AFN04F16")) {
					//测量点基本参数(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//写终端测量点参数配置表
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					
					s_sql = "update g_zdcldpzb "
						+"set afn04f16=? "
						+" where zdid=? and cldh=?";
					//去掉最前面"1,1,"
					params = new String[]{ss.substring(4,ss.length()),s_zdid,cldh};
					jdbcT.update(s_sql, params);
					
				    ///////更新二代配置
					s_sql = "select cldh from g_zdfjdcfkzcs where zdid=?";
	      	        params = new String[]{s_zdid};
	          		List dnbList = jdbcT.queryForList(s_sql,params);
					String[] f15s=ss.split(";");
					String cldIn = "";
	      			for(int n=0;n<f15s.length;n++){
	      			    //1、序号
	      				String pz1 = f15s[n].split(",")[0];
	          			
	          			//2、测量点号
	      				String pz2 = f15s[n].split(",")[1];
	          			
	          			//3、输出硬件号
	      				String pz3 = f15s[n].split(",")[2];
	          			
	          			
	          		    //4、备用输出硬件号
	      				String pz4 = f15s[n].split(",")[3];
	          			
	      				 //5、主备切换时间-小时
	      				String pz5 = f15s[n].split(",")[4];
	          			
	      				//6、与水泵联动使能
	      				String pz6 = f15s[n].split(",")[5];
	          			
	      			    //7、最小温度
	      				String pz7 = f15s[n].split(",")[6];

	      				//8、最大温度
	      				String pz8 = f15s[n].split(",")[7];

	      				//9、启动时间
	      				String pz9 = f15s[n].split(",")[8];

	      				//10、停止时间
	      				String pz10 = f15s[n].split(",")[9];

	      				//11、频率
	      				String pz11 = f15s[n].split(",")[10];

	      				//12、控制动作
	      				String pz12 = f15s[n].split(",")[11];

	          		   
	          			
	          			boolean isIn = false;
	          			for(int j=0;j<dnbList.size();j++){
	          				Map tempHM = (Map)dnbList.get(j);
	          				if(String.valueOf(tempHM.get("cldh")).equals(pz2)){
	          					isIn = true;
	          					break;
	          				}
	          			}
	          			
	          		    //写"配置表"
	          			if(isIn == true){
	          				//主站里有该测量点的配置
	          				s_sql = "update g_zdfjdcfkzcs set scyjh=?,byscyjh=?,zbqhsj=?,sbldsn=?,zxwd=?,zdwd=?,qdsj=?,tzsj=?,pl=?,kzdz=? where zdid=? and cldh=?";
	        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)||"CC".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)||"CC".equalsIgnoreCase(pz8)?"":pz8,"EEEE".equalsIgnoreCase(pz9)||"CCCC".equalsIgnoreCase(pz9)?"":pz9,"EEEE".equalsIgnoreCase(pz10)||"CCCC".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,s_zdid,pz2};
	          			}else if(isIn == false){
	          				//主站里无该测量点的配置
	          				s_sql = "insert into g_zdfjdcfkzcs(id,zdid,xh,cldh,scyjh,byscyjh,zbqhsj,sbldsn,zxwd,zdwd,qdsj,tzsj,pl,kzdz) "
	        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	        	        	params = new String[]{s_zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)||"CC".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)||"CC".equalsIgnoreCase(pz8)?"":pz8,"EEEE".equalsIgnoreCase(pz9)||"CCCC".equalsIgnoreCase(pz9)?"":pz9,"EEEE".equalsIgnoreCase(pz10)||"CCCC".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12};
	          			}
	          			jdbcT.update(s_sql,params);
	          			
	          			cldIn += "'"+pz2+"',";
	          			}
	      			
	                 
	      			//删除其它配置
	      			s_sql = "delete g_zdfjdcfkzcs where zdid=?  ";
	      			if(f15s.length>0){
	      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
	      			}
	      	        params = new String[]{s_zdid};
	                jdbcT.update(s_sql,params); 

				} else {

					if (sjxdm.equals("AFN04F10")) {
						//终端电能表/交流采样装置参数配置(cs1;...;csn)--N个电能表配置
						//写终端运行参数配置表
//						s_sql = "update g_zdyxcspzb set AFN04F10=? "
//								+ "where zdid=?";
//						params = new String[] { sjz, s_zdid };
//						jdbcT.update(s_sql, params);
						
//						//写终端测量点配置表
//						String cldIn = "";
//						String[] ss_csz = sjz.split(";");						
//						int dnbNum = ss_csz.length;//电能表数量
//					    for (int j = 0; j < dnbNum; j++) {
//					    	//每个电能表配置
//					    	String csn = ss_csz[j];
//					    	String[] ss_pz = csn.split("#");
//					        //电能表序号
//					    	String pz1 = ss_pz[0];//电能表序号(2字节)
//					        //电能表所属测量点号
//					    	String pz2 = ss_pz[1];//电能表所属测量点号(2字节)
//					        //通信速率
//					        String pz3 = ss_pz[2];//通信速率
//					        //电能表所属端口号
//					        String pz4 = ss_pz[3];//端口号
//					        //电能表所属规约类型编号
//					        String pz5 = ss_pz[4];//规约类型(1字节) 
//					        //电能表通信地址
//					        String pz6 = ss_pz[5];//通信地址(6字节，格式12)
//					        //电能表通信密码
//					        String pz7 = ss_pz[6];//通信密码(6字节，BIN)
//					        //电能费率个数
//					        String pz8 = ss_pz[7];//电能费率个数(1字节)	
//					        //有功电能整数位个数
//					        String pz9 = ss_pz[8];//有功电能整数位个数(2位,D3D2)
//					        //有功电能小数位个数
//					        String pz10 = ss_pz[9];//有功电能小数位个数(2位,D1D0)
//					        //采集器通信地址
//					        String pz11 = ss_pz[10];//通信地址(6字节，格式12)					        
//					        //用户大类号
//					        String pz12 = ss_pz[11];//用户大类号(4位,D7D6D5D4)
//					        //用户小类号
//					        String pz13 = ss_pz[12];//用户小类号(4位,D3D2D1D0)
//					        
//					        s_sql = "select 1 from g_zdcldpzb where zdid=? and cldh=?";
//					        params = new String[]{s_zdid,pz2};
//					        List temps = jdbcT.queryForList(s_sql, params);
//					        String cldlx = "01";//测量点类型(01:电能表)
//					        if(temps==null||temps.size()==0){
//					        	//新增
//					        	s_sql = "insert into g_zdcldpzb(cldlx,zdid,xh,cldh,txsl,dkh,dbgylx,txdz,txmm,dnflgs,dnzswgs,dnxswgs,cjqtxdz,yhdlh,yhxlh) "
//					        		+"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//					        	params = new String[]{cldlx,s_zdid,pz1,pz2,pz3,pz4,pz5,pz6,pz7,pz8,pz9,pz10,pz11,pz12,pz13};
//					        }else{
//					        	//更新
//					        	s_sql = "update g_zdcldpzb set cldlx=?,xh=?,txsl=?,dkh=?,dbgylx=?,"
//					        		+"txdz=?,txmm=?,dnflgs=?,dnzswgs=?,dnxswgs=?,cjqtxdz=?,"
//					        		+"yhdlh=?,yhxlh=? where zdid=? and cldh=?";
//					        	params = new String[]{cldlx,pz1,pz3,pz4,pz5,pz6,pz7,pz8,pz9,pz10,pz11,pz12,pz13,s_zdid,pz2};
//					        }
//					        jdbcT.update(s_sql, params);  
//					        
//					        cldIn += "'"+pz2+"',";
//					    }
//					    
//					    //删除其它电能表配置
//		      			s_sql = "delete g_zdcldpzb "
//		      				+ "where zdid=? "
//							+ "and cldlxbm='01' ";
//		      			if(dnbNum>0){
//		      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
//		      			}
//		      	        params = new String[]{s_zdid};
//		                jdbcT.update(s_sql,params);
						
					}  else if (sjxdm.equals("AFN04F11")) {
						//终端电能表/交流采样装置参数配置(cs1;...;csn)--N个脉冲配置
						//写终端运行参数配置表
						s_sql = "update g_zdyxcspzb set AFN04F11=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);
					} else if (sjxdm.equals("AFN04F1")) {
						//主站IP(zyip;byip;apn)
						s_sql = "update g_zdyxcspzb set AFN04F1=? "
								+"where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F3")) {
						//主站IP(zyip;byip;apn)
						s_sql = "update g_zdyxcspzb set AFN04F3=? "
								+"where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					} else if (sjxdm.equals("AFN04F4")) {
						//主站电话号码和短信中心号码(zzdhhm;dxzxhm)
						s_sql = "update g_zdyxcspzb set AFN04F4=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F5")) {
						//风机水泵控制参数设置
//						String 参数值(cs1;cs2;cs3;cs4;cs5)
//						 *            cs1:风机开启相对于水泵开启的提前量 单位：分钟
//						 *            cs2:风机水泵自动控制允许标志 0x55允许自动控制；0xAA禁止自动控制
//						 *            cs3:污泥回流泵自动控制允许标志   0x55允许自动控制；0xAA禁止自动控制
//						 *            cs4:污泥回流泵周期运行开启时长 单位：分钟
//						 *            cs5:污泥回流泵周期运行停止时长 单位：分钟
						s_sql = "update g_zdyxcspzb set AFN04F5=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);
						
						if(null!=sjz&&sjz.length()>0){
							String sj[]=sjz.split(";");
							if("55".equalsIgnoreCase(sj[1])&&"55".equalsIgnoreCase(sj[2])){
								s_sql = "update m_station_statistics_current s set s.isauto=1 where stationid =(select stationid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
								
								s_sql = "update m_sump s set s.isauto=1 where id =(select sumpid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
								
							}else if("AA".equalsIgnoreCase(sj[1])&&"AA".equalsIgnoreCase(sj[2])){
								s_sql = "update m_station_statistics_current s set s.isauto=0 where stationid =(select stationid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
								
								s_sql = "update m_sump s set s.isauto=0 where id =(select sumpid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
							}
						}

					}else if (sjxdm.equals("AFN04F6")) {
						//终端组地址设置(cs1;...;cs8)
						s_sql = "update g_zdyxcspzb set AFN04F6=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F7")) {
						//终端IP地址和端口(cs1;...;cs9)
						s_sql = "update g_zdyxcspzb set AFN04F7=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F9")) {
//						String 参数值(cs1;cs2;cs3;cs4)
//						   * 				   cs1:主备切换设置1-使用1号水泵2-使用2号水泵3- 1号2号互为主
//											   cs2:主备切换时间  一个字节 小时
//											   cs3:启动时间 两个字节 分钟
//											   cs4:停止时间   两个字节 分钟';
						s_sql = "update g_zdyxcspzb set AFN04F9=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);
						
						

					}else if (sjxdm.equals("AFN04F14")) {
						//电气设备启停控制参数 （1代专用）
//						s_sql = "update g_zdyxcspzb set AFN04F14=? "
//								+ "where zdid=?";
//						params = new String[] { sjz, s_zdid };
//						jdbcT.update(s_sql, params);
						
						s_sql = "update g_zdcldpzb set AFN04F14=? "
								+ "where zdid=? and cldh=?";
						params = new String[] { sjz.split("@")[1], s_zdid, sjz.split("@")[0] };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F37")) {
						
						s_sql = "update g_zdyxcspzb set AFN04F37=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F26")) {
						
						s_sql = "update g_zdyxcspzb set AFN04F26=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					} else if (sjxdm.equals("AFN0FF1")) {
						System.out.println("----------升级请求得到确认》》》》》");
//						Decode_0F.decodeDownload(s_xzqxm, s_zddz,s_sjzfsseq, jdbcT);
				    
						
					}else if (sjxdm.equals("AFN0FF2")) {
						//根据终端地址取终端ID
//						s_sql = "select fssj from g_sjzfsb where sjzfsseq=?";
//						params = new String[]{s_sjzfsseq};
//						List fssjlst = jdbcT.queryForList(s_sql, params);
//						if(fssjlst==null || fssjlst.size()==0){
//							return;
//						}
//						Date s_fssj = (Date)((Map)fssjlst.get(0)).get("fssj");
////						Date fssj=DateUtil.parse(s_fssj);
//						Long sjc=new Date().getTime()-s_fssj.getTime();
//						System.out.println(sjz+"-----"+s_fssj+"-----"+sjc);
						//下发程序报文之后的确认帧回复
						
						// 在不超时的前提下，当前段号加1
						//if(sjc<new Long(overtime)){
							s_sql = "update g_zdsjpzb set sj=sysdate, dqdh=?+1,dqcd="+buffer+",zt=2 where zdid=? and (select fssj from g_sjzfsb where sjzfsseq=?)>sysdate-(6/(24*60*60))";
							params = new String[] {sjz,s_zdid ,s_sjzfsseq};
							jdbcT.update(s_sql,params);
							Decode_0F.decodeDownload(s_xzqxm, s_zddz,s_sjzfsseq, jdbcT);
						//}
						
					}

				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
}