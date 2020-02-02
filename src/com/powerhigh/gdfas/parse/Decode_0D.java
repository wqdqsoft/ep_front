package com.powerhigh.gdfas.parse;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.util.Util;

/**
 * Description: AFN=0D(请求2类数据的响应————返回处理)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * 编写时间: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 修改人： 修改时间：
 */

public class Decode_0D {
	// 加载日志
	private static final String resource = "log4j.properties";
	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.parse.Decode_0D.class);

	// static {
	// PropertyConfigurator.configure(resource);
	// }
	public Decode_0D() {

	}

	public static void savebeckondata(HashMap hm, String s_xzqxm,
			String s_zddz, String zt, String sSJZ, String s_sjzfsseq,
			JdbcTemplate jdbcT) throws Exception {
		String s_sql = "";
		String[] params = null;

		// 一、取数据桢发送明细序列
		// s_sql = "select sjzfsmxseq,sjdybsdm from g_sjzfssjdybszb "
		// +" where sjzfsseq=?";
		// params = new String[]{s_sjzfsseq};
		// List lstXl = (List)jdbcT.queryForList(s_sql,params);

		// 二、写“召测数据返回表”
		if (zt.equals("01")) {
			// 正确返回
			Set set = hm.keySet();
			Object[] obj = set.toArray();
			for (int i = 0; i < obj.length; i++) {
				String key = obj[i].toString();// PnFn
				Vector vt = (Vector) hm.get(key);

				// String sjzfsmxseq = "";//数据桢发送明细序列
				// for(int j=0;j<lstXl.size();j++){
				// Map tempH = (Map)lstXl.get(j);
				// if(tempH.get("sjdybsdm").equals(key)){
				// sjzfsmxseq = tempH.get("sjzfsmxseq").toString();
				//	      				
				// break;
				// }
				// }
				// if(sjzfsmxseq.equals("")){
				// continue;
				// }

				// 调用存储过程写“召测数据返回表”
				Vector sp_param = new Vector();
				String sp_name = "sp_savebeckondata";
				String array = "";
				// 1、行政区县码
				sp_param.addElement(s_xzqxm);

				// 2、终端地址
				sp_param.addElement(s_zddz);

				// 3、数据桢发送序列
				sp_param.addElement(s_sjzfsseq);

				// 4、AFN
				sp_param.addElement("0D");

				// 5、PNFN
				sp_param.addElement(key);

				for (int j = 0; j < vt.size(); j++) {
					String[] values = (String[]) vt.get(j);
					String xh = values[0];// 序号
					String sjxdm = values[1];// 数据项代码
					String sjz = values[2];// 数据值
					String sjsj = values[3];// 数据时间
					String xxdlx = values[4];// 信息点类型
					String xxdh = values[5];// 信息点号
					String flg = values[6];// 是否进数据主表(0:不进;1:进)

					array += xh + "|" + sjxdm + "|" + sjz + "|" + sjsj + "|"
							+ xxdlx + "|" + xxdh + "#";

				}

				// 4、需插入的记录
				sp_param.addElement(array);
				cat.info("[Decode_0D]array:" + array);

				// 3、调用存储过程
				if (array.equals("")) {
					cat.error("从报文中未解析出合法数据:" + sSJZ);
				} else {
					Util.executeProcedure(jdbcT, sp_name, sp_param, 2);
				}
			}

		}
		// 三、修改数据桢发送表的状态
		s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
				+ "where sjzfsseq=?";
		params = new String[] { zt, sSJZ, s_sjzfsseq };
		jdbcT.update(s_sql, params);

	}

	public static void saveautotaskdata(HashMap hm, String s_xzqxm,
			String s_zddz, String zt, String sSJZ, String sxlb,
			JdbcTemplate jdbcT) throws Exception {
		String s_sql = "";
		String[] params = null;
		Collection coll = hm.values();
		Object[] datas = coll.toArray();
		// 任务数据主动上报(执行存储过程sp_saveautotaskdata)
		Vector sp_param = new Vector();
		// 1、行政区线码
		sp_param.addElement(s_xzqxm);
		// 2、终端地址
		sp_param.addElement(s_zddz);

		// 3、任务号
		sp_param.addElement("");

		String nowTime = Util.getNowTime();// yyMMddHHmmss

		String array = "";
		for (int i = 0; i < datas.length; i++) {
			Vector vt = (Vector) datas[i];
			for (int j = 0; j < vt.size(); j++) {
				String[] ss = (String[]) vt.get(j);
				if (ss[6] == null || !ss[6].equals("1") || ss[2].equals("无效")) {
					continue;
				}
				array += ss[1] + "," + ss[2] + "," + ss[3] + "," + ss[4] + ","
						+ ss[5] + ";";
			}
		}

		// 5、数据
		sp_param.addElement(array);

		cat.info("[taskDecode]array:" + array);
		if (array.equals("")) {
			cat.error("从报文中未解析出合法数据:" + sSJZ);
		} else {
			Util.executeProcedure(jdbcT, "sp_saveautotaskdata", sp_param, 2);
		}
		if (sxlb.equals("2")) {
			// 主动上报
			String sFn = "";
			Set keySet = hm.keySet();
			if (hm.size() > 0) {
				Object[] keys = keySet.toArray();
				for (int i = 0; i < keys.length; i++) {
					sFn += keys[i] + ";";
				}
			}
			sFn = "0D:" + sFn;

			s_sql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,zt,fhsj,sxsjz,fn) "
					+ "values(SEQ_SJZFS.Nextval,"
					+ "(select zdid from G_ZDGZ where xzqxm=? and zddz=?),"
					+ "?,?,sysdate,?,?)";
			params = new String[] { s_xzqxm, s_zddz, "XX", zt, sSJZ, sFn };
			jdbcT.update(s_sql, params);
		}

	}

	// 上行类别：1：查询返回；2：主动上报
	public static void dispose(String sxlb, String s_xzqxm, String s_zddz,
			String sSJZ, String fir_fin, String s_tpv, String s_acd,
			String s_csdata, String s_sjzfsseq, JdbcTemplate jdbcT)
			throws Exception {
		// 请求2类数据的响应
		String DADT = "";
		HashMap hm = new HashMap();
		String zt = "01";// 成功返回
		if (fir_fin.equals("00") || fir_fin.equals("10")) {
			zt = "0A";// 部分返回(中间祯)
		}

		if (s_tpv.equals("1") && s_acd.equals("1")) {
			// 时间标签(6字节) and 事件计数器(2字节)
			DADT = s_csdata.substring(16, s_csdata.length() - 16);
		} else if (s_tpv.equals("1") && s_acd.equals("0")) {
			// 时间标签(6字节)
			DADT = s_csdata.substring(16, s_csdata.length() - 12);
		} else if (s_tpv.equals("0") && s_acd.equals("1")) {
			// 事件计数器(2字节)
			DADT = s_csdata.substring(16, s_csdata.length() - 4);
		} else if (s_tpv.equals("0") && s_acd.equals("0")) {
			// 无附加信息
			DADT = s_csdata.substring(16);
		}
		try {
			hm = decode(DADT);
		} catch (Exception e) {
			zt = "03";// 失败返回
			// e.printStackTrace();
			cat.error("[Decode_0D]ERROR:", e);
		}

		if (sxlb.equals("1")) {
			// 召测返回
			savebeckondata(hm, s_xzqxm, s_zddz, zt, sSJZ, s_sjzfsseq, jdbcT);

			// 同时写数据明细表
			saveautotaskdata(hm, s_xzqxm, s_zddz, zt, sSJZ, sxlb, jdbcT);

		} else if (sxlb.equals("2")) {
			// 主动上报
			saveautotaskdata(hm, s_xzqxm, s_zddz, zt, sSJZ, sxlb, jdbcT);
		}
	}

	public static HashMap decode(String DADT) throws Exception {
		HashMap hm = new HashMap();
		String nowTime = Util.getNowTime().substring(0, 10);// 数据时间YYMMDDhhmm
		int idx_dadt = 0;
		String s_dadt = "";
		String s_da = "";// 信息点Pn
		String s_dt = "";// 信息类Fn
		String s_PF = "";// PnFn

		// 序号、数据项代码、数据值、数据时间、信息点类别、信息点号、标志
		String[] values = null;// new String[7]

		cat.info("[Decode_0D]DADT:" + DADT);
		while (idx_dadt < DADT.length()) {
			// ------------------每个PnFn-----------------
			s_dadt = DADT.substring(idx_dadt, idx_dadt + 8);
			idx_dadt += 8;

			// 信息点Pn
			s_da = s_dadt.substring(0, 4);
			s_da = Util.tranDA(Util.convertStr(s_da));
			String s_Pda = "P" + s_da;
			// 信息类Fn
			s_dt = s_dadt.substring(4, 8);
			s_dt = Util.tranDT(Util.convertStr(s_dt));
			String s_Fdt = "F" + s_dt;
			// PnFn
			s_PF = s_Pda + s_Fdt;
			cat.info("[Decode_0D]s_PF:" + s_PF);

			int i_xh = 1;// 序号

			Vector vt = new Vector();

			if (s_Fdt.equals("F1") || s_Fdt.equals("F9") || s_Fdt.equals("F17")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F1")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F9")) {
					// 抄表日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F17")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")) {
					s_zdcbsj = Util.convertStr(s_zdcbsj);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = s_zdcbsj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					s_zdcbsj = nowTime;
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// 正向有功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				temps = Util.tranFormat14(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zxygzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向有功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 10);
					idx_dadt += 10;
					temps = Util.tranFormat14(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zxygzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);

				}

				// 正向无功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat11(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zxwgzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向无功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat11(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zxwgzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 一象限无功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat11(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "1xxwgzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 一象限无功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat11(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "1xxwgzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 四象限无功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat11(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "4xxwgzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 四象限无功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat11(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "4xxwgzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F2") || s_Fdt.equals("F10")
					|| s_Fdt.equals("F18")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F2")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F10")) {
					// 抄表日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F18")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")) {
					s_zdcbsj = Util.convertStr(s_zdcbsj);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = s_zdcbsj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					s_zdcbsj = nowTime;
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// 反向有功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				temps = Util.tranFormat14(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fxygzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 反向有功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 10);
					idx_dadt += 10;
					temps = Util.tranFormat14(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fxygzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 反向无功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat11(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fxwgzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 反向无功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat11(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fxwgzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 二象限无功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat11(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "2xxwgzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 二象限无功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat11(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "2xxwgzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 三象限无功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat11(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "3xxwgzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 三象限无功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat11(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "3xxwgzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F3") || s_Fdt.equals("F11")
					|| s_Fdt.equals("F19")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F3")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F11")) {
					// 抄表日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F19")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")) {
					s_zdcbsj = Util.convertStr(s_zdcbsj);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = s_zdcbsj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					s_zdcbsj = nowTime;
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// 正向有功总最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zxygzzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向有功费率n最大需量(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					temps = Util.tranFormat23(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zxygfl" + i + "zdxl";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 正向有功总最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zxygzzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向有功费率n最大需量发生时间(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat17(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zxygfl" + i + "zdxlfssj";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 正向无功总最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zxwgzzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向无功费率n最大需量(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					temps = Util.tranFormat23(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zxwgfl" + i + "zdxl";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 正向无功总最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zxwgzzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向无功费率n最大需量发生时间(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat17(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zxwgfl" + i + "zdxlfssj";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F4") || s_Fdt.equals("F12")
					|| s_Fdt.equals("F20")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F4")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F12")) {
					// 抄表日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F20")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")) {
					s_zdcbsj = Util.convertStr(s_zdcbsj);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = s_zdcbsj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					s_zdcbsj = nowTime;
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// 反向有功总最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fxygzzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 反向有功费率n最大需量(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					temps = Util.tranFormat23(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fxygfl" + i + "zdxl";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 反向有功总最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fxygzzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 反向有功费率n最大需量发生时间(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat17(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fxygfl" + i + "zdxlfssj";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 反向无功总最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fxwgzzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 反向无功费率n最大需量(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					temps = Util.tranFormat23(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fxwgfl" + i + "zdxl";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 反向无功总最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fxwgzzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 反向无功费率n最大需量发生时间(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat17(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fxwgfl" + i + "zdxlfssj";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F5") || s_Fdt.equals("F6")
					|| s_Fdt.equals("F7") || s_Fdt.equals("F8")
					|| s_Fdt.equals("F21") || s_Fdt.equals("F22")
					|| s_Fdt.equals("F23") || s_Fdt.equals("F24")) {

				String sjxdm = ""; // 数据项代码
				String flsjxdm = "";// 数据项代码(费率)
				if (s_Fdt.equals("F5")) {
					// F5:日冻结正向有功电能量(总、费率1～M)
					sjxdm = "zxygzdnl";
					flsjxdm = "zxygdnlfl";
				} else if (s_Fdt.equals("F6")) {
					// F6:日冻结正向无功电能量(总、费率1～M)
					sjxdm = "zxwgzdnl";
					flsjxdm = "zxwgdnlfl";
				} else if (s_Fdt.equals("F7")) {
					// F7:日冻结反向有功电能量(总、费率1～M)
					sjxdm = "fxygzdnl";
					flsjxdm = "fxygdnlfl";
				} else if (s_Fdt.equals("F8")) {
					// F8:日冻结反向无功电能量(总、费率1～M)
					sjxdm = "fxwgzdnl";
					flsjxdm = "fxwgdnlfl";
				} else if (s_Fdt.equals("F21")) {
					// F21:月冻结正向有功电能量(总、费率1～M)
					sjxdm = "zxygzdnl";
					flsjxdm = "zxygdnlfl";
				} else if (s_Fdt.equals("F22")) {
					// F22:月冻结正向无功电能量(总、费率1～M)
					sjxdm = "zxwgzdnl";
					flsjxdm = "zxwgdnlfl";
				} else if (s_Fdt.equals("F23")) {
					// F23:月冻结反向有功电能量(总、费率1～M)
					sjxdm = "fxygzdnl";
					flsjxdm = "fxygdnlfl";
				} else if (s_Fdt.equals("F24")) {
					// F24:月冻结反向无功电能量(总、费率1～M)
					sjxdm = "fxwgzdnl";
					flsjxdm = "fxwgdnlfl";
				}

				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F5") || s_Fdt.equals("F6")
						|| s_Fdt.equals("F7") || s_Fdt.equals("F8")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F21") || s_Fdt.equals("F22")
						|| s_Fdt.equals("F23") || s_Fdt.equals("F24")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// 正向有功总电能量
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat13(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sjxdm;
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向有功电能量(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat13(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = flsjxdm + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F25") || s_Fdt.equals("F33")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F25")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F33")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";

				// 总最大有功功率
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zzdyggl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 总最大有功功率发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zzdygglfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相最大有功功率
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axzdyggl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相最大有功功率发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axzdygglfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相最大有功功率
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxzdyggl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相最大有功功率发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxzdygglfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相最大有功功率
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxzdyggl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相最大有功功率发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxzdygglfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 总有功功率为零时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zygglwlsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相有功功率为零时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axygglwlsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相有功功率为零时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxygglwlsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相有功功率为零时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxygglwlsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F26") || s_Fdt.equals("F34")) {

				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F26")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F34")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";

				// 总有功最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zygzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 总有功最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zygzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相有功最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axygzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相有功最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axygzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相有功最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxygzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相有功最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxygzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相有功最大需量
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat23(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxygzdxl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相有功最大需量发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxygzdxlfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F27") || s_Fdt.equals("F35")) {

				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F27")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F35")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";

				// A相电压越上上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyyssxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压越下下限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyyxxxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压越下限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyyxxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压合格累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyhgljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压越上上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyyssxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压越下下限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyyxxxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压越下限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyyxxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压合格累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyhgljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压越上上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyyssxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压越下下限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyyxxxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压越下限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyyxxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压合格累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyhgljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压最小值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyzxz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压最小值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdyzxzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压最小值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyzxz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压最小值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdyzxzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压最小值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyzxz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压最小值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdyzxzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电压平均值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdypjz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电压平均值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdypjz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电压平均值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat07(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdypjz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F28") || s_Fdt.equals("F36")) {

				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F28")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F36")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				// 电流不平衡度越限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "dlbphdyxrljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 电压不平衡度越限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "dybphdyxrljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 电流不平衡最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat05(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "dlbphzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 电流不平衡最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "dlbphzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 电压不平衡最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat05(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "dybphzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 电压不平衡最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "dybphzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F29") || s_Fdt.equals("F37")) {

				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F29")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F37")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				// A相电流越上上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdlyssxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电流越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdlysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电流越上上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdlyssxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电流越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdlysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电流越上上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdlyssxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电流越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdlysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 零序电流越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "lxdlysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电流最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat25(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdlzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相电流最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdlzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电流最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat25(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdlzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相电流最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdlzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电流最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat25(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdlzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相电流最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdlzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 零序电流最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat25(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "lxdlzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 零序电流最大值发生时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat18(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "lxdlzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F30") || s_Fdt.equals("F38")) {

				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F30")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F38")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				// 视在功率越上上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "szglyssxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 视在功率越上限累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "szglysxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F31") || s_Fdt.equals("F39")) {

				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F31")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F39")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				// 负载率最大值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat05(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fzlzdz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 负载率最大值发生时间
				if (s_Fdt.equals("F31")) {
					temps = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					temps = Util.tranFormat18(temps);
				} else if (s_Fdt.equals("F39")) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat17(temps);
				}
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fzlzdzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 负载率最小值
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat05(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fzlzxz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 负载率最小值发生时间
				if (s_Fdt.equals("F31")) {
					temps = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					temps = Util.tranFormat18(temps);
				} else if (s_Fdt.equals("F39")) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.tranFormat17(temps);
				}
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "fzlzxzfssj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

			} else if (s_Fdt.equals("F32")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 日冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "rdjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")) {
					s_zdcbsj = Util.convertStr(s_zdcbsj);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = s_zdcbsj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					s_zdcbsj = nowTime;
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// 总断相次数
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat08(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zdxcs";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相断相次数
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat08(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdxcs";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相断相次数
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat08(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdxcs";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相断相次数
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.tranFormat08(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdxcs";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 断相累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat10(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "dxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相断相累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat10(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axdxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相断相累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat10(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxdxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相断相累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				temps = Util.tranFormat10(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxdxljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 最近一次断相起始时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zjycdxqssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相最近一次断相起始时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axzjycdxqssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相最近一次断相起始时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxzjycdxqssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相最近一次断相起始时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxzjycdxqssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 最近一次断相结束时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zjycdxjssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// A相最近一次断相结束时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "axzjycdxjssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// B相最近一次断相结束时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "bxzjycdxjssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// C相最近一次断相结束时刻
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat17(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cxzjycdxjssk";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F41")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 日冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "rdjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				// 1-9组电容器累计投入时间
				for (int i = 1; i <= 9; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.convertStr(temps);
					temps = String.valueOf(Integer.parseInt(temps, 16));
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "drqz" + i + "ljtrsj";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 1-9组电容器累计投入次数
				for (int i = 1; i <= 9; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					temps = Util.convertStr(temps);
					temps = String.valueOf(Integer.parseInt(temps, 16));
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "drqz" + i + "ljtrcs";
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F42")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 日冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "rdjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				// 日补偿无功电能量
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat13(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "rbcwgdnl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 月补偿无功电能量
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				temps = Util.tranFormat13(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "ybcwgdnl";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F43") || s_Fdt.equals("F44")) {

				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F43")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F44")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				// 功率因数区段1累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "glysqd1ljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 功率因数区段2累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "glysqd2ljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 功率因数区段3累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "glysqd3ljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F49")) {
				// 信息点类别
				String xxdlb = "0";// 0:终端
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 日冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "rdjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端日供电时间
				String s_zdrgdsj = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				s_zdrgdsj = Util.convertStr(s_zdrgdsj);
				s_zdrgdsj = String.valueOf(Integer.parseInt(s_zdrgdsj, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zdrgdsj";
				values[2] = s_zdrgdsj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 终端日复位累计次数
				String s_zdrfwljcs = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				s_zdrfwljcs = Util.convertStr(s_zdrfwljcs);
				s_zdrfwljcs = String.valueOf(Integer.parseInt(s_zdrfwljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zdrfwljcs";
				values[2] = s_zdrfwljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F50")) {
				// 信息点类别
				String xxdlb = "0";// 0:终端
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 日冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "rdjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 月电控跳闸日累计次数
				String s_ydktzrljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_ydktzrljcs = String.valueOf(Integer
						.parseInt(s_ydktzrljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "ydktzrljcs";
				values[2] = s_ydktzrljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 购电控跳闸日累计次数
				String s_gdktzrljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_gdktzrljcs = String.valueOf(Integer
						.parseInt(s_gdktzrljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "gdktzrljcs";
				values[2] = s_gdktzrljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 功控跳闸日累计次数
				String s_gktzrljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_gktzrljcs = String.valueOf(Integer.parseInt(s_gktzrljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "gktzrljcs";
				values[2] = s_gktzrljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 摇控跳闸日累计次数
				String s_yktzrljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_yktzrljcs = String.valueOf(Integer.parseInt(s_yktzrljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "yktzrljcs";
				values[2] = s_yktzrljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F51")) {
				// 信息点类别
				String xxdlb = "0";// 0:终端
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 月冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "ydjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端月供电时间
				String s_zdygdsj = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				s_zdygdsj = Util.convertStr(s_zdygdsj);
				s_zdygdsj = String.valueOf(Integer.parseInt(s_zdygdsj, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zdygdsj";
				values[2] = s_zdygdsj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 终端月复位累计次数
				String s_zdyfwljcs = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				s_zdyfwljcs = Util.convertStr(s_zdyfwljcs);
				s_zdyfwljcs = String.valueOf(Integer.parseInt(s_zdyfwljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zdyfwljcs";
				values[2] = s_zdyfwljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F52")) {
				// 信息点类别
				String xxdlb = "0";// 0:终端
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 月冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "ydjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 月电控跳闸月累计次数
				String s_ydktzyljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_ydktzyljcs = String.valueOf(Integer
						.parseInt(s_ydktzyljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "ydktzyljcs";
				values[2] = s_ydktzyljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 购电控跳闸月累计次数
				String s_gdktzyljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_gdktzyljcs = String.valueOf(Integer
						.parseInt(s_gdktzyljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "gdktzyljcs";
				values[2] = s_gdktzyljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 功控跳闸月累计次数
				String s_gktzyljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_gktzyljcs = String.valueOf(Integer.parseInt(s_gktzyljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "gktzyljcs";
				values[2] = s_gktzyljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 摇控跳闸月累计次数
				String s_yktzyljcs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				s_yktzyljcs = String.valueOf(Integer.parseInt(s_yktzyljcs, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "yktzyljcs";
				values[2] = s_yktzyljcs;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F57")) {
				// 信息点类别
				String xxdlb = "2";// 2:总加组
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 日冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "rdjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 日最大有功功率
				String s_rzdyggl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				s_rzdyggl = String.valueOf(Util.tranFormat02(s_rzdyggl));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "rzdyggl";
				values[2] = s_rzdyggl;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 日最大有功功率发生时间
				String s_rzdygglfssj = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				s_rzdygglfssj = Util.tranFormat18(s_rzdygglfssj);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "rzdygglfssj";
				values[2] = s_rzdygglfssj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 日最小有功功率
				String s_rzxyggl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				s_rzxyggl = String.valueOf(Util.tranFormat02(s_rzxyggl));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "rzxyggl";
				values[2] = s_rzxyggl;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 日最小有功功率发生时间
				String s_rzxygglfssj = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				s_rzxygglfssj = Util.tranFormat18(s_rzxygglfssj);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "rzdxgglfssj";
				values[2] = s_rzxygglfssj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 有功功率为零日累计时间
				String s_ygglwlrljsj = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				s_ygglwlrljsj = Util.convertStr(s_ygglwlrljsj);
				s_ygglwlrljsj = String.valueOf(Integer.parseInt(s_ygglwlrljsj,
						16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "ygglwlrljsj";
				values[2] = s_ygglwlrljsj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F58") || s_Fdt.equals("F61")) {
				// 信息点类别
				String xxdlb = "2";// 2:总加组
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F58")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F61")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String[] ss = null;
				String temps = "";

				// 总加有功总电能量
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				if (!temps.equalsIgnoreCase("EEEEEEEE")) {
					ss = Util.tranFormat03_pure1(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zjygzdnl";
					if (ss[1].equals("0")) {
						values[2] = ss[0];
					} else {
						values[2] = String
								.valueOf(Integer.parseInt(ss[0]) * 1000);
					}
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zjygzdnl";
					values[2] = "-1";// 无效
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 总加有功电能量(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					if (!temps.equalsIgnoreCase("EEEEEEEE")) {
						ss = Util.tranFormat03_pure1(temps);
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = "zjygdnlfl" + i;
						if (ss[1].equals("0")) {
							values[2] = ss[0];
						} else {
							values[2] = String
									.valueOf(Integer.parseInt(ss[0]) * 1000);
						}
						values[3] = sjsj;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					} else {
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = "zjygdnlfl" + i;
						values[2] = "-1";// 无效
						values[3] = sjsj;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					}
				}

			} else if (s_Fdt.equals("F59") || s_Fdt.equals("F62")) {
				// 信息点类别
				String xxdlb = "2";// 2:总加组
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F59")) {
					// 日冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F62")) {
					// 月冻结
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "无效";
					}

					sbdm = "ydjsjsb";
				}

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String[] ss = null;
				String temps = "";

				// 总加无功总电能量
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				if (!temps.equalsIgnoreCase("EEEEEEEE")) {
					ss = Util.tranFormat03_pure1(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zjwgzdnl";
					if (ss[1].equals("0")) {
						values[2] = ss[0];
					} else {
						values[2] = String
								.valueOf(Integer.parseInt(ss[0]) * 1000);
					}
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zjwgzdnl";
					values[2] = "-1";// 无效
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// 总加无功电能量(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					if (!temps.equalsIgnoreCase("EEEEEEEE")) {
						ss = Util.tranFormat03_pure1(temps);
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = "zjwgdnlfl" + i;
						if (ss[1].equals("0")) {
							values[2] = ss[0];
						} else {
							values[2] = String
									.valueOf(Integer.parseInt(ss[0]) * 1000);
						}
						values[3] = sjsj;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					} else {
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = "zjwgdnlfl" + i;
						values[2] = "-1";// 无效
						values[3] = sjsj;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					}
				}

			} else if (s_Fdt.equals("F60")) {
				// 信息点类别
				String xxdlb = "2";// 2:总加组
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 月冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "ydjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 总加组月最大有功功率
				String yzdyggl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				yzdyggl = String.valueOf(Util.tranFormat02(yzdyggl));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zjzyzdyggl";
				values[2] = yzdyggl;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 总加组月最大有功功率发生时间
				String yzdygglfssj = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				yzdygglfssj = Util.tranFormat18(yzdygglfssj);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zjzyzdygglfssj";
				values[2] = yzdygglfssj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 总加组月最小有功功率
				String yzxyggl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				yzxyggl = String.valueOf(Util.tranFormat02(yzxyggl));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zjzyzxyggl";
				values[2] = yzxyggl;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 总加组月最小有功功率发生时间
				String yzxygglfssj = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				yzxygglfssj = Util.tranFormat18(yzxygglfssj);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zjzyzxygglfssj";
				values[2] = yzxygglfssj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 总加组月有功功率为零累计时间
				String yygglwlljsj = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				yygglwlljsj = Util.convertStr(yygglwlljsj);
				yygglwlljsj = String.valueOf(Integer.parseInt(yygglwlljsj, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zjzyygglwlljsj";
				values[2] = yygglwlljsj;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F65")) {
				// 信息点类别
				String xxdlb = "2";// 2:总加组
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 月冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "ydjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				String[] ss = null;

				// 超功率定值月累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cgldzyljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 超功率定值月累计电能量
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				ss = Util.tranFormat03_pure1(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cgldzyljdnl";
				if (ss[1].equals("0")) {
					values[2] = ss[0];
				} else {
					values[2] = String.valueOf(Integer.parseInt(ss[0]) * 1000);
				}
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F66")) {
				// 信息点类别
				String xxdlb = "2";// 2:总加组
				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";
				// 月冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "ydjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				String temps = "";
				String[] ss = null;

				// 超月电能量定值月累计时间
				temps = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				temps = Util.convertStr(temps);
				temps = String.valueOf(Integer.parseInt(temps, 16));
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cydnldzyljsj";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 超月电能量定值月累计电能量
				temps = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				ss = Util.tranFormat03_pure1(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "cydnldzyljdnl";
				if (ss[1].equals("0")) {
					values[2] = ss[0];
				} else {
					values[2] = String.valueOf(Integer.parseInt(ss[0]) * 1000);
				}
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

			} else if (s_Fdt.equals("F73") || s_Fdt.equals("F74")) {
				// 信息点类别
				String xxdlb = "2";// 2:总加组

				String sjxdm = "";
				if (s_Fdt.equals("F73")) {
					// 有功功率
					sjxdm = "yggl";
				} else if (s_Fdt.equals("F74")) {
					// 无功功率
					sjxdm = "wggl";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 4);// 2字节
					sjz = String.valueOf(Util.tranFormat02(sjz));
					idx_dadt += 4;
					// 数据时间(起始时间+时间间隔)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F75") || s_Fdt.equals("F76")) {

				// 信息点类别
				String xxdlb = "2";// 2:总加组

				String sjxdm = "";
				if (s_Fdt.equals("F75")) {
					// 有功电能量
					sjxdm = "ygdnl";
				} else if (s_Fdt.equals("F76")) {
					// 无功电能量
					sjxdm = "wgdnl";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 8);// 4字节
					sjz = String.valueOf(Util.tranFormat03(sjz));
					idx_dadt += 8;
					// 数据时间(起始时间+时间间隔)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F81") || s_Fdt.equals("F82")
					|| s_Fdt.equals("F83") || s_Fdt.equals("F84")
					|| s_Fdt.equals("F85") || s_Fdt.equals("F86")
					|| s_Fdt.equals("F87") || s_Fdt.equals("F88")) {
				// 测量点功率曲线

				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjxdm = "";
				if (s_Fdt.equals("F81")) {
					// 有功功率
					sjxdm = "yggl";
				} else if (s_Fdt.equals("F82")) {
					// A相有功功率
					sjxdm = "axyggl";
				} else if (s_Fdt.equals("F83")) {
					// B相有功功率
					sjxdm = "bxyggl";
				} else if (s_Fdt.equals("F84")) {
					// C相有功功率
					sjxdm = "cxyggl";
				} else if (s_Fdt.equals("F85")) {
					// 无功功率
					sjxdm = "wggl";
				} else if (s_Fdt.equals("F86")) {
					// A相无功功率
					sjxdm = "axwggl";
				} else if (s_Fdt.equals("F87")) {
					// B相无功功率
					sjxdm = "bxwggl";
				} else if (s_Fdt.equals("F88")) {
					// C相无功功率
					sjxdm = "cxwggl";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 6);// 3字节
					idx_dadt += 6;
					sjz = String.valueOf(Util.tranFormat09(sjz));
					// 数据时间(起始时间+时间间隔)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F89") || s_Fdt.equals("F90")
					|| s_Fdt.equals("F91")) {
				// 测量点电压曲线

				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjxdm = "";
				if (s_Fdt.equals("F89")) {
					// A相电压
					sjxdm = "axdy";
				} else if (s_Fdt.equals("F90")) {
					// B相电压
					sjxdm = "bxdy";
				} else if (s_Fdt.equals("F91")) {
					// C相电压
					sjxdm = "cxdy";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 4);// 2字节
					sjz = String.valueOf(Util.tranFormat07(sjz));
					idx_dadt += 4;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}
			} else if (s_Fdt.equals("F92") || s_Fdt.equals("F93")
					|| s_Fdt.equals("F94") || s_Fdt.equals("F95")) {
				// 测量点电流曲线
				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjxdm = "";
				if (s_Fdt.equals("F92")) {
					// A相电流
					sjxdm = "axdl";
				} else if (s_Fdt.equals("F93")) {
					// B相电流
					sjxdm = "bxdl";
				} else if (s_Fdt.equals("F94")) {
					// C相电流
					sjxdm = "cxdl";
				} else if (s_Fdt.equals("F95")) {
					// 零序电流
					sjxdm = "lxdl";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 6);
					sjz = String.valueOf(Util.tranFormat25(sjz));
					idx_dadt += 6;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}
			} else if (s_Fdt.equals("F96")) {
				// @TODO 2010-09-30
				// 漏保曲线数据
				// 信息点类别
				String xxdlb = "1";// 1:测量点
				String sjxdm = "";

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				int mm = 0;// 序号
				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 24);
					idx_dadt += 24;
					String temps = "";
					// 用电电压
					sjxdm = "yddy";
					temps = String.valueOf(Util.tranFormat07(sjz
							.substring(0, 4)));
					values = new String[7];
					values[0] = String.valueOf(mm);// 序号
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// 用电电流
					sjxdm = "yddl";
					temps = String.valueOf(Util.tranFormat25(sjz.substring(4,
							10)));
					values = new String[7];
					values[0] = String.valueOf(mm);// 序号
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// 漏电电流
					sjxdm = "lddl";
					temps = String.valueOf(Util.tranFormat25(sjz.substring(10,
							16)));
					values = new String[7];
					values[0] = String.valueOf(mm);// 序号
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// 动作时漏电电流
					sjxdm = "dzslddl";
					temps = String.valueOf(Util.tranFormat25(sjz.substring(16,
							22)));
					values = new String[7];
					values[0] = String.valueOf(mm);// 序号
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// 漏保开关状态
					sjxdm = "lbkgzt";
					temps = sjz.substring(22, 24);
					if (temps.equalsIgnoreCase("EE")) {
						temps = "无效";
					} else {
						temps = Util.hexStrToBinStr(temps, 1);
					}
					values = new String[7];
					values[0] = String.valueOf(mm);// 序号
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;
				}

			} else if (s_Fdt.equals("F97") || s_Fdt.equals("F98")
					|| s_Fdt.equals("F99") || s_Fdt.equals("F100")) {
				// 测量点总电能量曲线
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				// 数据项代码
				String sjxdm = "";
				if (s_Fdt.equals("F97")) {
					// 正向有功总电能量
					sjxdm = "zxygzdnl";
				} else if (s_Fdt.equals("F98")) {
					// 正向无功总电能量
					sjxdm = "zxwgzdnl";
				} else if (s_Fdt.equals("F99")) {
					// 反向有功总电能量
					sjxdm = "fxygzdnl";
				} else if (s_Fdt.equals("F100")) {
					// 反向无功总电能量
					sjxdm = "zxwgzdnl";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 8);// 4字节
					sjz = String.valueOf(Util.tranFormat13(sjz));
					idx_dadt += 8;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F101") || s_Fdt.equals("F102")
					|| s_Fdt.equals("F103") || s_Fdt.equals("F104")) {
				// 测量点总电能示值曲线
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				// 数据项代码
				String sjxdm = "";
				if (s_Fdt.equals("F101")) {
					// 正向有功总电能示值
					sjxdm = "zxygzdnsz";
				} else if (s_Fdt.equals("F102")) {
					// 正向无功总电能示值
					sjxdm = "zxwgzdnsz";
				} else if (s_Fdt.equals("F103")) {
					// 反向有功总电能示值
					sjxdm = "fxygzdnsz";
				} else if (s_Fdt.equals("F104")) {
					// 反向无功总电能示值
					sjxdm = "zxwgzdnsz";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 8);// 4字节
					sjz = String.valueOf(Util.tranFormat11(sjz));
					idx_dadt += 8;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F105") || s_Fdt.equals("F106")
					|| s_Fdt.equals("F107") || s_Fdt.equals("F108")) {
				// 测量点功率因数曲线
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				// 数据项代码
				String sjxdm = "";
				if (s_Fdt.equals("F105")) {
					// 功率因数
					sjxdm = "glys";
				} else if (s_Fdt.equals("F106")) {
					// A相功率因数
					sjxdm = "axglys";
				} else if (s_Fdt.equals("F107")) {
					// B相功率因数
					sjxdm = "bxglys";
				} else if (s_Fdt.equals("F108")) {
					// C相功率因数
					sjxdm = "cxglys";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 4);
					sjz = String.valueOf(Util.tranFormat05(sjz));
					idx_dadt += 4;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F109")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				String sjz = "";
				for (int i = 0; i < sjds; i++) {
					// 数据时间(起始时间+时间间隔)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);

					// Uab/Ua相位角
					sjz = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					sjz = Util.tranFormat05(sjz);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "uaxwj";
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);

					// Ub相位角
					sjz = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					sjz = Util.tranFormat05(sjz);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ubxwj";
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);

					// Uc相位角
					sjz = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					sjz = Util.tranFormat05(sjz);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ucxwj";
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F110")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				String sjz = "";
				for (int i = 0; i < sjds; i++) {
					// 数据时间(起始时间+时间间隔)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);

					// Ia相位角
					sjz = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					sjz = Util.tranFormat05(sjz);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "iaxwj";
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);

					// Ib相位角
					sjz = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					sjz = Util.tranFormat05(sjz);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ibxwj";
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);

					// Ic相位角
					sjz = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					sjz = Util.tranFormat05(sjz);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "icxwj";
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F129")) {
				
			}else if (s_Fdt.equals("F138")) {
				// 信息点类别
				String xxdlb = "3";// 3:直流模拟量

				String sjxdm = "zlmnldjsj";// 直流模拟量冻结数据

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 4);// 2字节
					sjz = String.valueOf(Util.tranFormat02(sjz));
					idx_dadt += 4;
					// 数据时间(起始时间+时间间隔)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F145") || s_Fdt.equals("F146")
					|| s_Fdt.equals("F147") || s_Fdt.equals("F148")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjxdm = "";
				if (s_Fdt.equals("F145")) {
					// 一象限无功总电能示值曲线
					sjxdm = "1xxwgzdnsz";
				} else if (s_Fdt.equals("F146")) {
					// 四象限无功总电能示值曲线
					sjxdm = "4xxwgzdnsz";
				} else if (s_Fdt.equals("F147")) {
					// 二象限无功总电能示值曲线
					sjxdm = "2xxwgzdnsz";
				} else if (s_Fdt.equals("F148")) {
					// 三象限无功总电能示值曲线
					sjxdm = "3xxwgzdnsz";
				}

				// 1、数据时标（7字节）
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a）数据起始时间YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)数据密度
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// 不冻结
					} else if (sjmd == 1) {
						sjmd = 15;
					} else if (sjmd == 2) {
						sjmd = 30;
					} else if (sjmd == 3) {
						sjmd = 60;
					} else if (sjmd == 254) {
						sjmd = 5;
					} else if (sjmd == 255) {
						sjmd = 1;
					} else {
						sjmd = 0;// 备用
					}

					// c)数据点数
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = String.valueOf(sjds);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

				} else {
					// @data 数据起始时间
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据密度
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data 数据点数
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 8);
					idx_dadt += 8;
					sjz = Util.tranFormat11(sjz);
					// 数据时间(起始时间+时间间隔)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// 序号
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F161")) {
				// 信息点类别
				String xxdlb = "1";// 1:测量点

				String sjsj = nowTime;
				// 数据时标Td
				String s_sjsb = "";
				String sbdm = "";

				// 日冻结
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "无效";
				}

				sbdm = "rdjsjsb";

				// @data 数据时标
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")) {
					s_zdcbsj = Util.convertStr(s_zdcbsj);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = s_zdcbsj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					s_zdcbsj = nowTime;
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 费率个数
				String s_flgs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_flgs = 0;
				if (!s_flgs.equalsIgnoreCase("EE")) {
					i_flgs = Integer.parseInt(s_flgs, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = String.valueOf(i_flgs);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "flgs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// 正向有功总电能示值
				temps = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				temps = Util.tranFormat14(temps);
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = "zxygzdnsz";
				values[2] = temps;
				values[3] = sjsj;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "1";
				vt.add(values);

				// 正向有功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 10);
					idx_dadt += 10;
					temps = Util.tranFormat14(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zxygzdnszfl" + i;
					values[2] = temps;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);

				}

			}

			// 将此次PnFn数据放入HashMap
			hm.put(s_PF, vt);
		}

		return hm;
	}

}