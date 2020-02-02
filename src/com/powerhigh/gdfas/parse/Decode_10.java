package com.powerhigh.gdfas.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.dbparse.dlt645.dlt645Parse;
import com.powerhigh.gdfas.util.CMContext;
import com.powerhigh.gdfas.util.Util;

/**
 * Description: AFN=10(请求读中继数据的响应————返回处理) <p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-2
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class Decode_10 {
	//加载日志
	private static final String resource = "log4j.properties";

	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.parse.Decode_10.class);

	//	static {
	//	   PropertyConfigurator.configure(resource);
	//	}

	public Decode_10() {

	}

	public static void dispose(String s_xzqxm, String s_zddz, String sSJZ,
			String s_tpv, String s_acd, String s_csdata, String s_sjzfsseq,
			String dbgylxdm,JdbcTemplate jdbcT) throws Exception {
		String s_sql = "";
		String DADT = "";

		if (s_tpv.equals("1") && s_acd.equals("1")) {
			//时间标签(6字节) and 事件计数器(2字节)
			DADT = s_csdata.substring(16, s_csdata.length() - 16);
		} else if (s_tpv.equals("1") && s_acd.equals("0")) {
			//时间标签(6字节)
			DADT = s_csdata.substring(16, s_csdata.length() - 12);
		} else if (s_tpv.equals("0") && s_acd.equals("1")) {
			//事件计数器(2字节)
			DADT = s_csdata.substring(16, s_csdata.length() - 4);
		} else if (s_tpv.equals("0") && s_acd.equals("0")) {
			//无附加信息
			DADT = s_csdata.substring(16);
		}
		String[] params = null;
		//一、取数据桢发送明细序列
		s_sql = "select sjzfsmxseq,sjdybsdm from sjzfssjdybszb "
				+ " where sjzfsseq=?";
	    params = new String[]{s_sjzfsseq};
		List lstXl = jdbcT.queryForList(s_sql,params);

		//二、写“召测数据返回表”
		HashMap hm = null;

		String zt = "01";
		try {
			hm = decode(DADT,dbgylxdm);
		} catch (Exception e) {
			zt = "03";
//			e.printStackTrace();
			cat.error("[Decode_10]ERROR:", e);
		}

		if (zt.equals("01")) {
			//正确返回
			Set set = hm.keySet();
			Object[] obj = set.toArray();
			for (int i = 0; i < obj.length; i++) {
				String key = obj[i].toString();//PnFn
				String[][] value = (String[][]) hm.get(key);

				String sjzfsmxseq = "";//数据桢发送明细序列
				for (int j = 0; j < lstXl.size(); j++) {
					Map tempH = (Map) lstXl.get(j);
					if (tempH.get("sjdybsdm").equals(key)) {
						sjzfsmxseq = tempH.get("sjzfsmxseq").toString();

						break;
					}
				}
				if (sjzfsmxseq.equals("")) {
					continue;
				}

				//调用存储过程写“召测数据返回表”
				Vector sp_param = new Vector();
				String sp_name = "sp_savebeckondata";
				String array = "";
				//1、行政区县码
				sp_param.addElement(s_xzqxm);

				//2、终端地址
				sp_param.addElement(s_zddz);

				//3、数据桢发送明细序列
				sp_param.addElement(sjzfsmxseq);
				cat.info("[Decode_0C]PnFn:" + key);
				cat.info("[Decode_0C]sjzfsmxseq:" + sjzfsmxseq);
				for (int j = 0; j < value.length; j++) {
					String xh = value[j][0];//序号
					String sjxdm = value[j][1];//数据项代码
					String sjz = value[j][2];//数据值
					String xxdh = value[j][3];//信息点号
					String sjsj = Util.getNowTime().substring(0, 10);//数据时间YYMMDDhhmm
					String sjjg = "0";//时间间隔

					array += xh + "|" + xxdh + "|" + sjxdm + "|" + sjz + "|"
							+ sjsj + "|" + sjjg + "#";

				}

				//4、需插入的记录
				sp_param.addElement(array);
				cat.info("[Decode_0C]array:" + array);

				//5、调用存储过程
				Util.executeProcedure(jdbcT, sp_name, sp_param,
						2);
			}
		}

		//三、修改“数据祯发送表”的状态标志
		s_sql = "update sjzfsb set zt=?,fhsj=sysdate,sxsjz=? where sjzfsseq=?";
	    params = new String[]{zt,sSJZ,s_sjzfsseq};
        jdbcT.update(s_sql,params); 
	}

	private static HashMap decode(String DADT,String dbgylxdm) throws Exception {
		HashMap hm = new HashMap();

		int idx_dadt = 0;
		String s_dadt = "";
		String s_da = "";//信息点Pn
		String s_dt = "";//信息类Fn
		String s_PF = "";//PnFn

		String[][] values = null;//new String[i][3]:
		//序号、数据项代码、数据值

		s_dadt = DADT.substring(idx_dadt, idx_dadt + 8);
		idx_dadt += 8;

		//信息点Pn
		s_da = s_dadt.substring(0, 4);
		s_da = Util.tranDA(Util.convertStr(s_da));
		String s_Pda = "P" + s_da;
		//信息类Fn
		s_dt = s_dadt.substring(4, 8);
		s_dt = Util.tranDT(Util.convertStr(s_dt));
		String s_Fdt = "F" + s_dt;
		//PnFn
		s_PF = s_Pda + s_Fdt;

		int i_xh = 1;//序号
		int i_idx = 0;//values数组下标

		if (s_Fdt.equals("F1")) {
			//F1:转发命令的返回
			cat.info("[Decode_10]F1:转发命令的返回");
			//转发数据长度
			String len = DADT.substring(idx_dadt, idx_dadt + 2);
			idx_dadt += 2;
			int iLen = Integer.parseInt(len,16);
			
			//转发数据
			String zfsj = DADT.substring(idx_dadt, idx_dadt + iLen*2);
			idx_dadt += iLen*2;
			String[][] data = null;
			if(dbgylxdm.equalsIgnoreCase("01")){
				//DLT-645电表规约
				data = dlt645Parse.decode(zfsj);
				
				values = new String[data.length][4];
				for(int i=0;i<data.length;i++){
					values[i][0] = String.valueOf(i+1);
					values[i][1] = CMContext.getDnbsjx01(data[i][0]);
					values[i][2] = data[i][1];
					values[i][3] = "0";
				}
			}

		}

		//将此次PnFn数据放入Map
		hm.put(s_PF, values);
		
		return hm;
	}

}