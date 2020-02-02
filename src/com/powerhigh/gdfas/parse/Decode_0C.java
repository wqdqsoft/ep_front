package com.powerhigh.gdfas.parse;

//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.OutputStreamWriter;
//import java.io.RandomAccessFile;
//import java.io.Writer;
//import java.nio.channels.FileChannel;
//import java.sql.ResultSet;
//import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;

import org.apache.log4j.Category;
//import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.support.AbstractLobStreamingResultSetExtractor;
import org.springframework.jdbc.support.lob.OracleLobHandler;
//import org.springframework.util.FileCopyUtils;

import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.CMXmlR;
import com.powerhigh.gdfas.util.EPService;
import com.powerhigh.gdfas.util.Util;

/**
 * Description: AFN=0C(请求1类数据的响应————返回处理)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * 编写时间: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 修改人： 修改时间：
 */

public class Decode_0C {
	// 加载日志
	@SuppressWarnings("unused")
	private static final String resource = "log4j.properties";
	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.parse.Decode_0C.class);
	
//	private static String supimgdata="0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

	// static {
	// PropertyConfigurator.configure(resource);
	// }
	public static String file_url = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_FILE_URL);//文件存放目录
	public static String web_url = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_WEB_URL);//文件WEB目录
	
	//用以存最新的图像帧的序号
	public  static  Map<String, Integer> picnums=new HashMap<String, Integer>();
	
	//用以存 如果接收的图像贞序号是上个贞序号+2的序号
	public  static  Map<String, Integer> rsnums=new HashMap<String, Integer>();
	
	//用以存 如果接收的图像贞序号是上个贞序号+2的图像数据
	public  static  Map<String, String> rsdata=new HashMap<String, String>();
	
	//存储所有图像数据   key是  站点编号_当前段号
	public static Map<String, String> imgdata=new HashMap<String, String>();
	

	
	public Decode_0C() {

	}

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
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

				// 3、数据桢发送明细序列
				sp_param.addElement(s_sjzfsseq);

				// 4、AFN
				sp_param.addElement("0C");

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
				cat.info("[Decode_0C]array:" + array);

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

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
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

		cat.info("[Decode_0C]array:" + array);
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
            
			//2016-09-28修改  将主动任务数据放到G_ZDSBSJJLB
			s_sql = "insert into G_ZDSBSJJLB(sjzfsseq,zdid,gnm,zt,fhsj,sxsjz,fn) "
					+ "values(SEQ_ZDSBSJ.Nextval,"
					+ "(select zdid from G_ZDGZ where xzqxm=? and zddz=?),"
					+ "?,?,sysdate,?,?)";
			params = new String[] { s_xzqxm, s_zddz, "XX", zt, sSJZ, sFn };
			jdbcT.update(s_sql, params);
		}

	}

	// 上行类别：1：查询返回；2：主动上报
	@SuppressWarnings({ "rawtypes", "unused" })
	public static void dispose(String sxlb, String s_zdid, String s_xzqxm,
			String s_zddz, String sSJZ, String s_tpv, String s_acd,
			String s_csdata, String s_sjzfsseq, JdbcTemplate jdbcT)
			throws Exception {
		String s_sql = "";
		String[] params = null;
		String DADT = "";
		HashMap hm = new HashMap();

		String zt = "01";

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
			hm = decode(s_zdid, DADT, jdbcT);
		} catch (Exception e) {
			zt = "03";
			// e.printStackTrace();
			cat.error("[Decode_0C]ERROR:", e);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap decode(String s_zdid, String DADT, JdbcTemplate jdbcT)
			throws Exception {
		HashMap hm = new HashMap();

		int idx_dadt = 0;
		String s_dadt = "";
		String s_da = "";// 信息点Pn
		String s_dt = "";// 信息类Fn
		String s_PF = "";// PnFn

		String nowTime = Util.getNowTime();

		// 序号、数据项代码、数据值、数据时间、信息点类别、信息点号、标志
		String[] values = null;// new String[7]

		// cat.info("[Decode_0C]DADT:"+DADT);
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
			cat.info("[Decode_0C]s_PF:" + s_PF);

			int i_xh = 1;// 序号
			Vector vt = new Vector();
			if (s_Fdt.equals("F1")) {
				// F1:终端版本信息的查询返回
				cat.info("[Decode_0C]F1:终端版本信息的查询返回");
				// 信息点类别
				String xxdlb = "0";// 0:终端

				// 厂商代码
				String tmp_csdh = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				String s_csdh = "";
				String tmp_cs = "";
				if (!tmp_csdh.equals("EEEEEEEE")) {
					for (int i = 0; i < 4; i++) {
						String s = tmp_csdh.substring(i * 2, (i + 1) * 2);
						if (s.equals("00")) {
							break;
						}
						tmp_cs += s;
					}
					byte[] bt = Util.strstobyte(tmp_cs);
					s_csdh = Util.getASCII(bt);

					// tmp_csdh = Util.convertStr(tmp_csdh);
					// s_csdh = Util.getASCII(tmp_csdh);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "csdh";
					values[2] = s_csdh;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "csdh";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 设备编号
				String tmp_sbbh = DADT.substring(idx_dadt, idx_dadt + 12);
				idx_dadt += 12;
				String s_sbbh = "";
				String tmp_sb = "";
				if (!tmp_sbbh.equals("EEEEEEEEEEEE")) {
					for (int i = 0; i < 6; i++) {
						String s = tmp_sbbh.substring(i * 2, (i + 1) * 2);
						if (s.equals("00")) {
							break;
						}
						tmp_sb += s;
					}
					byte[] bt = Util.strstobyte(tmp_sb);
					s_sbbh = Util.getASCII(bt);
					// tmp_sbbh = Util.convertStr(tmp_sbbh);
					// s_sbbh = Util.getASCII(tmp_sbbh);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sbbh";
					values[2] = s_sbbh;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sbbh";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 软件版本号
				String s_rjbbh = DADT.substring(idx_dadt, idx_dadt + 4);
				s_rjbbh = "v" + s_rjbbh.substring(1, 2) + "."
						+ s_rjbbh.substring(2, 3) + "."
						+ s_rjbbh.substring(3, 4);
				idx_dadt += 4;
				if (!s_rjbbh.equals("EEEE")) {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "rjbbh";
					values[2] = s_rjbbh;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "rjbbh";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 硬件版本号
				String s_yjbbh = DADT.substring(idx_dadt, idx_dadt + 4);
				s_yjbbh = "v" + s_yjbbh.substring(1, 2) + "."
						+ s_yjbbh.substring(2, 3) + "."
						+ s_yjbbh.substring(3, 4);
				idx_dadt += 4;
				if (!s_yjbbh.equals("EEEE")) {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "yjbbh";
					values[2] = String.valueOf(s_yjbbh);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "yjbbh";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String s_sql = "update g_zdgz set csdh=? ,sbbh=?, rjbbh=?, yjbbh=? where zdid=?";
				String[] params = new String[] { s_csdh, s_sbbh, s_rjbbh,
						s_yjbbh, s_zdid };
				jdbcT.update(s_sql, params);

			} else if (s_Fdt.equals("F2")) {

				// F2:终端日历时钟的查询返回
				cat.info("[Decode_0C]F2:终端日历时钟的查询返回");

				// 信息点类别
				String xxdlb = "0";// 0:终端

				// 终端日历时钟
				String s_zdrlsz = DADT.substring(idx_dadt, idx_dadt + 12);
				idx_dadt += 12;
				if (!s_zdrlsz.equals("EEEEEEEEEEEE")) {
					s_zdrlsz = Util.tranFormat01_1(s_zdrlsz);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdrlsz";
					values[2] = s_zdrlsz;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdrlsz";
					values[2] = null;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				// 终端锂电池电压
				String s_zddcdy = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_zddcdy.equals("EEEE")) {
					s_zddcdy = Util.tranFormat30(s_zddcdy);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zddcdy";
					values[2] = s_zddcdy;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zddcdy";
					values[2] = null;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				String s_sql = "update g_zdgz set zdrlsz=to_date(?,'yy-mm-dd hh24:mi:ss'),zddcdy=?  where zdid=?";
				String[] params = new String[] { s_zdrlsz, s_zddcdy, s_zdid };
				jdbcT.update(s_sql, params);
				
				//2017-05-16更新  插入g_zdtxztjlb电池电压
				s_sql = "insert into G_ZDTXZTJLB(id,zdid,jlsj,zhtxsj,dcdy) values(s_zdtxzt.nextval,?,sysdate,sysdate,?)";      			
      	        params = new String[]{s_zdid,s_zddcdy};
                jdbcT.update(s_sql,params);

			} else if (s_Fdt.equals("F4")) {

				// F4:流量的查询返回
				cat.info("[Decode_0C]F4:流量的查询返回");

				// 信息点类别
				String xxdlb = "0";// 0:终端

				// 瞬时流量
				String s_ssll = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_ssll.equals("EEEE")) {
					s_ssll = Util.tranFormat06(s_ssll);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ssll";
					values[2] = s_ssll;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ssll";
					values[2] = null;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 累计流量
				String s_ljll = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_ljll.equals("EEEEEE")) {
					s_ljll = Util.tranFormat10(s_ljll);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ljll";
					values[2] = s_ljll;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ljll";
					values[2] = null;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F5")) {

				// F5:当前水位高度的查询返回
				cat.info("[Decode_0C]F5:当前水位高度的查询返回");
				// 信息点类别
				String xxdlb = "0";// 0:测量点

				// 当前水位高度
				String s_dqswgd = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqswgd.equalsIgnoreCase("EEEE")) {
					s_dqswgd = Util.tranFormat06(s_dqswgd);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqswgd";
					values[2] = s_dqswgd;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqswgd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 浮球状态 浮球状态：1为低水位；2、3为中水位；4为高水位
				String s_fqzt = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				// 解析浮球状态的低四位，用以表示两个水池（调节池和处理池）的浮球状态
				String s_fqzt_byte = "";
				
				if (!s_fqzt.equals("EE")) {
					if("CC".equalsIgnoreCase(s_fqzt)){
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = "fqzt";
						values[2] = String.valueOf(-1);
						values[3] = nowTime;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					}else{
						int i_fqzt= Integer.parseInt(Util.hexStrToDecStr(s_fqzt));
						if(i_fqzt<=16){
							values = new String[7];
							values[0] = String.valueOf(i_xh++);
							values[1] = "fqzt";
							values[2] = String.valueOf(i_fqzt);
							values[3] = nowTime;
							values[4] = xxdlb;
							values[5] = s_da;
							values[6] = "1";
							vt.add(values);
						}
						
						
	
						// 存储浮球状态二进制
						s_fqzt_byte = Util.hexStrToBinStr(s_fqzt, 1);
						s_fqzt_byte = s_fqzt_byte.substring(4, 8);
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = "fqzt_byte";
						values[2] = String.valueOf(s_fqzt_byte);
						values[3] = nowTime;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					}
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fqzt";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 超水位上限标志
				String s_dqcjjswbz = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				int i_dqcjjswbz = 0;
				if (!s_dqcjjswbz.equals("EE")) {
					i_dqcjjswbz = Integer.parseInt(s_dqcjjswbz, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqcjjswbz";
					values[2] = String.valueOf(i_dqcjjswbz);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqcjjswbz";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 低于水位下限标志
				String s_dyswxxbz = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				if (!s_dyswxxbz.equals("EE")) {
					int i_dyswxxbz = Integer.parseInt(s_dyswxxbz, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dyswxxbz";
					values[2] = String.valueOf(i_dyswxxbz);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dyswxxbz";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 20160223如果调节池没有超过警戒水位，则允许智能终端自动运行
				if (i_dqcjjswbz == 0) {
					List znzds = EPService.getZnzd(s_zdid, jdbcT);
					if (null != znzds && znzds.size() > 0) {
						for (int ii = 0; ii < znzds.size(); ii++) {
							Map zd = (Map) znzds.get(ii);
							operation.sendAFN04F5("3",
									String.valueOf(zd.get("xzqxm")),
									String.valueOf(zd.get("zddz")),
									"1;55;55;1;1");
							Thread.sleep(1000L);
						}
					}
				}
				
				if (i_dqcjjswbz == 1) {
					List znzds = EPService.getZnzd(s_zdid, jdbcT);
					if (null != znzds && znzds.size() > 0) {
						for (int ii = 0; ii < znzds.size(); ii++) {
							Map zd = (Map) znzds.get(ii);
							List clds = EPService.getZnzdDjcld(
									String.valueOf(zd.get("zdid")), jdbcT);
							if (null != clds && clds.size() > 0) {
								// 20160222关闭智能终端所有类型为电机的测量点
								for (int jj = 0; jj < clds.size(); jj++) {
									Map cld = (Map) clds.get(jj);
									operation.sendAFN05F1("3",
											String.valueOf(zd.get("xzqxm")),
											String.valueOf(zd.get("zddz")),
											String.valueOf(cld.get("cldh")),
											"CC");
									// 20160222间隔1秒
									Thread.sleep(1000L);
								}

							}

						}
					}
				}

			} else if (s_Fdt.equals("F6")) {

				// F6:当前水体温度的查询返回
				cat.info("[Decode_0C]F6:当前水体温度的查询返回 ");
				// 信息点类别
				String xxdlb = "0";// 0:测量点
				
//				String s_sql="select zdxh from g_zdgz where zdid=?";
//        		String[] params = new String[] { s_zdid };
//        	    List cldList = jdbcT.queryForList(s_sql, params);
//        	    Map cldMap = (Map) cldList.get(0);
//        	    // 终端型号
//        	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
//        	 	
//        	 	if("1".equalsIgnoreCase(zdxh)){
//        	 		
//        	 	}

				// 当前环境温度
				String s_dqhjwd = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqhjwd.equalsIgnoreCase("EEEE")) {
					s_dqhjwd = Util.tranFormat05(s_dqhjwd);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqhjwd";
					values[2] = s_dqhjwd;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqhjwd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前水体温度
				String s_dqstwd = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqstwd.equalsIgnoreCase("EEEE")) {
					s_dqstwd = Util.tranFormat05(s_dqstwd);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqstwd";
					values[2] = s_dqstwd;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqstwd";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F7")) {

				// F7:当前水体ORP数值的查询返回
				cat.info("[Decode_0C]F7:当前水体ORP数值的查询返回 ");
				// 信息点类别
				String xxdlb = "0";// 0:测量点

				// 当前水体ORP数值
				String s_dqstorp = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqstorp.equalsIgnoreCase("EEEE")&&!s_dqstorp.equalsIgnoreCase("EE6E")) {
					String fh = Util.tranFormat28(s_dqstorp)[1];
					s_dqstorp = Util.tranFormat28(s_dqstorp)[0];
					if ("0".equalsIgnoreCase(fh)) {
						fh = "";
					} else {
						fh = "-";
					}
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqstorp";
					values[2] = fh + s_dqstorp;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqstorp";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 超警ORP数值标志
				String s_cjjorpszbz = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				if (!s_cjjorpszbz.equals("EE")) {
					int i_cjjorpszbz = Integer.parseInt(s_cjjorpszbz, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cjjorpszbz";
					values[2] = String.valueOf(i_cjjorpszbz);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cjjorpszbz";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				

			} else if (s_Fdt.equals("F8")) {

				// F8:当前电机设备数据的查询返回
				cat.info("[Decode_0C]F8:当前电机设备数据的查询返回 ");
				// 信息点类别
				String xxdlb = "1";// 0:终端

				// 当前电机启停状态
				String s_dqdjqtzt = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				if (!s_dqdjqtzt.equals("EE")) {
					int i_dqdjqtzt = Integer.parseInt(s_dqdjqtzt, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjqtzt";
					values[2] = String.valueOf(i_dqdjqtzt);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjqtzt";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前运行频率（针对变频风机）
				String s_dqdjyxpl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqdjyxpl.equalsIgnoreCase("EEEE")) {
					s_dqdjyxpl = Util.tranFormat06(s_dqdjyxpl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjyxpl";
					values[2] = s_dqdjyxpl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjyxpl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前电机累计运行时长
				String s_dqdjljyxsc = DADT.substring(idx_dadt, idx_dadt + 8);
				idx_dadt += 8;
				if (!s_dqdjljyxsc.equalsIgnoreCase("EEEEEEEE")) {
					s_dqdjljyxsc = Util.tranFormat27(s_dqdjljyxsc);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjljyxsc";
					values[2] = s_dqdjljyxsc;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjljyxsc";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 累计启停次数
				String s_dqdjljqtcs = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqdjljqtcs.equalsIgnoreCase("EEEE")) {
					s_dqdjljqtcs = Util.tranFormat27(s_dqdjljqtcs);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjljqtcs";
					values[2] = s_dqdjljqtcs;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqdjljqtcs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F9")) {
				// F9:当前图片抓取的查询返回
				cat.info("[Decode_0C]F9:当前图片抓取的查询返回");
				// 信息点类别

				// 总段号
				int zdh = 0;
				// 当前段号i
				int dqdh = 0;
				// 第i段数据长度
				int sjcd = 0;
				// 本段图像数据
				String bdtxsj = "";
				
				

				// 总段数
				String s_zds = DADT.substring(idx_dadt, idx_dadt + 4);
				s_zds = Util.convertStr(s_zds);
				idx_dadt += 4;
				if (!s_zds.equals("EEEE")) {
					s_zds = Util.hexStrToDecStr(s_zds);
					zdh = Integer.parseInt(s_zds);
				} else {
					s_zds = "0";
					continue;
				}

				// 当前段号
				String s_dqdh = DADT.substring(idx_dadt, idx_dadt + 4);
				s_dqdh = Util.convertStr(s_dqdh);
				
				

				idx_dadt += 4;
				if (!s_dqdh.equals("EEEE")) {
					s_dqdh = Util.hexStrToDecStr(s_dqdh);
					dqdh = Integer.parseInt(s_dqdh);
				} else {
					s_dqdh = "0";
					continue;
				}

				// 第i段数据长度Lf
				String s_didsjcd = DADT.substring(idx_dadt, idx_dadt + 4);
				s_didsjcd = Util.convertStr(s_didsjcd);
				idx_dadt += 4;

				if (!s_didsjcd.equals("EEEE")) {
					s_didsjcd = Util.hexStrToDecStr(s_didsjcd);
					sjcd = Integer.parseInt(s_didsjcd);
				} else {
					s_didsjcd = "0";
					continue;
				}

				// 第i段文件数据
				bdtxsj = DADT.substring(idx_dadt, idx_dadt + sjcd * 2);
				idx_dadt += sjcd * 2;
				
				
				 //===========20170526写本地文件================//
				 String s_sql = "select * from M_STATION where stationid=(select stationid from g_zdgz where zdid=?)";
				 Object[] params = new Object[] { s_zdid };
				 List stationlst = jdbcT.queryForList(s_sql, params);
				 
				 String stationid;
				stationid = String.valueOf(((Map) stationlst.get(0))
						.get("stationid"));
				String filename=file_url+stationid+".txt";
				
				//写编号
//				File filenum = new File(file_url+stationid+"_first_file_num"+".txt");
//				BufferedWriter fwnum = null;
//				fwnum = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum, true), "UTF-8")); //
//				//指定编码格式，以免读取时中文字符异常
//				fwnum.append(dqdh+"");
//				fwnum.newLine();
//				fwnum.flush();
//				fwnum.close();
				
				//2017-06-27如果段号大于等于总段号则跳出
				if(dqdh>=zdh){
					continue;
				}
				
				// 接收状态 1接收完成; 2未接收完成; 3失败终止接收
				int rstatus = 0;
				if (dqdh == zdh - 1) {
					imgdata.put(stationid+"_"+dqdh,bdtxsj);
					String imghex="";
					for(int i=0;i<zdh;i++){
						imghex=imghex+imgdata.get(stationid+"_"+i);
					}
					rstatus = 1;
					//图片名称
					String imgname=stationid+"station"+new Date().getTime()+".jpg";
					//存图文件
	                String imgurl=file_url+imgname;
					Util.saveToImgFile(imghex, imgurl);
					
					//将暂存表的remark字段更新问图片的WEB路径
					String imgweburl=web_url+imgname;
					s_sql = "update M_STATION_PICTURE_FILE_TMP set RSTATUS=?,FILEURL=? where stationid=?";
					params = new Object[] { 1,imgweburl, stationid };
					jdbcT.update(s_sql, params);
				} else {
					
					rstatus = 2;
					
					imgdata.put(stationid+"_"+dqdh,bdtxsj);
					
					//2017-05-25将当前段号保存到全局变量中
					picnums.put(stationid, dqdh);
					
					s_sql = "select * from M_STATION_PICTURE_FILE_TMP where stationid=?";
					params = new Object[] { stationid };
					List lst = jdbcT.queryForList(s_sql, params);
					// 如果站点图像暂存表M_STATION_PICTURE_FILE_TMP没有该站点数据则新增 否则更新
					if (null == lst || lst.size() <= 0) {
					    // 如果站点图像暂存表M_STATION_PICTURE_FILE_TMP没有该站点数据则新增 否则更新
						s_sql = "insert into M_STATION_PICTURE_FILE_TMP(STATIONID,datatime,SUMNUM,CURNUM,CURLENG,FILEURL,RSTATUS) "
								+ "values(?,sysdate,?,?,?,?,?)";
						params = new Object[] { stationid, zdh, dqdh, sjcd, filename, rstatus };
						jdbcT.update(s_sql, params);
					} else {
						s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
						params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
						jdbcT.update(s_sql, params);
					}
					
					
				}
				
				 
				//===========end_20170526写本地文件================//
				
				
//				//===========20170526写本地文件================//
//				 String s_sql = "select * from M_STATION where stationid=(select stationid from g_zdgz where zdid=?)";
//				 Object[] params = new Object[] { s_zdid };
//				 List stationlst = jdbcT.queryForList(s_sql, params);
//				 
//				 String stationid;
//				stationid = String.valueOf(((Map) stationlst.get(0))
//						.get("stationid"));
//				String filename=file_url+stationid+".txt";
////				System.out.println("当前贞序号："+picnums.get(stationid));
////				System.out.println("当前贞序号："+dqdh);
//				
//				//写编号
//				File filenum = new File(file_url+stationid+"_first_file_num"+".txt");
//				BufferedWriter fwnum = null;
//				fwnum = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum, true), "UTF-8")); //
//				//指定编码格式，以免读取时中文字符异常
//				fwnum.append(dqdh+",");
//				fwnum.flush();
//				fwnum.close();
//				
//				
//				
//				// 接收状态 1接收完成; 2未接收完成; 3失败终止接收
//				int rstatus = 0;
//				if (dqdh == zdh - 1) {
//					rstatus = 1;
//					String imgcode = Util.txt2String(new File(file_url+stationid+".txt"));
//					//图片名称
//					String imgname=stationid+"station"+new Date().getTime()+".jpg";
//					//存图文件
//	                String imgurl=file_url+imgname;
//					Util.saveToImgFile(imgcode, imgurl);
//					
//					//将暂存表的remark字段更新问图片的WEB路径
//					String imgweburl=web_url+imgname;
//					s_sql = "update M_STATION_PICTURE_FILE_TMP set RSTATUS=?,FILEURL=? where stationid=?";
//					params = new Object[] { 1,imgweburl, stationid };
//					jdbcT.update(s_sql, params);
//				} else {
//					rstatus = 2;
//					//如果当前段号==0
//					if(0==dqdh){
//						//清空文件，写入当前帧
//						BufferedWriter fw = null;
//						File file = new File(filename);
//						fw = new BufferedWriter(new FileWriter(file)); 
//					    fw.write("");
//					    //指定编码格式，以免读取时中文字符异常
//					    fw.append(bdtxsj);
//					    fw.flush();
//						fw.close();
//						
//						//2017-05-25将当前段号保存到全局变量中
//						picnums.put(stationid, dqdh);
//						
//						s_sql = "select * from M_STATION_PICTURE_FILE_TMP where stationid=?";
//						params = new Object[] { stationid };
//						List lst = jdbcT.queryForList(s_sql, params);
//						// 如果站点图像暂存表M_STATION_PICTURE_FILE_TMP没有该站点数据则新增 否则更新
//						if (null == lst || lst.size() <= 0) {
//						    // 如果站点图像暂存表M_STATION_PICTURE_FILE_TMP没有该站点数据则新增 否则更新
//							s_sql = "insert into M_STATION_PICTURE_FILE_TMP(STATIONID,datatime,SUMNUM,CURNUM,CURLENG,FILEURL,RSTATUS) "
//									+ "values(?,sysdate,?,?,?,?,?)";
//							params = new Object[] { stationid, zdh, dqdh, sjcd, filename, rstatus };
//							jdbcT.update(s_sql, params);
//						} else {
//							s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//						}
//						
//						
//					}else{
//						Integer f_dh=null;
//						//如果map中有则取，否则取库中
//						Integer isdb=null;
//						if(null!=picnums&&null!=picnums.get(stationid)){
//							isdb=1;
//							f_dh =picnums.get(stationid);
//						}else{
//							s_sql = "select * from M_STATION_PICTURE_FILE_TMP where stationid=?";
//							params = new Object[] { stationid };
//							List lst = jdbcT.queryForList(s_sql, params);
//							f_dh = Integer.parseInt(String.valueOf(((Map) lst
//									.get(0)).get("CURNUM")));
//							isdb=3;
//						}
//						
//
//						//写编号
//						File filenum3 = new File(file_url+stationid+"_f_dh_file_num"+".txt");
//						BufferedWriter fwnum3 = null;
//						fwnum3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum3, true), "UTF-8")); //
//						//指定编码格式，以免读取时中文字符异常
//						fwnum3.append("f_dh="+f_dh+"&isdb="+isdb+",");
//						fwnum3.flush();
//						fwnum3.close();
//						 
//						
//						// 如果当前段号==库中的当前段号+1则执行更新操作
//						if (dqdh ==f_dh + 1) {
//							//2017-05-25将当前段号保存到全局变量中
//							
//							
//							try {
//								BufferedWriter fw = null;
//								File file = new File(filename);
//								fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//								//指定编码格式，以免读取时中文字符异常
//							    if(null!=rsnums&&null!=rsnums.get(stationid)&&rsnums.get(stationid)==dqdh+1){
//							    	fw.append(bdtxsj+rsdata.get(stationid));
//								    fw.flush();
//									fw.close();
//									picnums.put(stationid, dqdh+1);
//							    }else{
//									fw.append(bdtxsj);
//								    fw.flush();
//									fw.close();
//									picnums.put(stationid, dqdh);
//								}
//							} catch (Exception e) {
//								System.out.println("=======================================================文件写入失败"+dqdh);
////								throw new Exception("=======================================================文件写入失败"+dqdh);
//								cat.error("=======================================================文件写入失败" + stationid+"||||"+dqdh+"||||"+isdb);
//								// TODO: handle exception
//							}
//								
//								
//								s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
//								params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
//								jdbcT.update(s_sql, params);
//							
//							
//						// 如果当前段号=库中的当前段号+2则将这帧先存起来	
//						}else if (dqdh ==f_dh + 2){
//							rsnums.put(stationid, dqdh);
//							rsdata.put(stationid, bdtxsj);
//							
//						// 如果当前段号>库中的当前段号+3则进行补0后执行更新操作		
//						}else if (dqdh ==f_dh + 3){
//							BufferedWriter fw = null;
//							File file = new File(filename);
//							fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//							//指定编码格式，以免读取时中文字符异常
//						    if(null!=rsnums&&null!=rsnums.get(stationid)&&rsnums.get(stationid)==dqdh-1){
//						    	fw.append(bdtxsj+rsdata.get(stationid)+bdtxsj);
//							    fw.flush();
//								fw.close();
//								picnums.put(stationid, dqdh+3);
//						    }else{
//								fw.append(bdtxsj+bdtxsj+bdtxsj);
//							    fw.flush();
//								fw.close();
//								picnums.put(stationid, dqdh);
//							}
//							
//						// 如果当前段号>库中的当前段号+2则进行补0后执行更新操作		
//						}else {
//							// 忽略本帧报文
//							System.out.println("忽略本帧报文并拒绝回复确认，当前贞序号"+dqdh+",库中序号:"+f_dh);
////							throw new Exception("帧序号无法对应,当前贞序号"+dqdh+",库中序号:"+f_dh);
//							
////							BufferedWriter fw = null;
////							File file = new File(filename);
////							fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
////							//指定编码格式，以免读取时中文字符异常
////						    fw.append(bdtxsj);
////						    fw.flush();
////							fw.close();
////							
////							//2017-05-25将当前段号保存到全局变量中
////							picnums.put(stationid, dqdh);
////							
////							s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
////							params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
////							jdbcT.update(s_sql, params);
//							
//						}
//					}
//					
//					
//				}
//				
//				
//				//写编号
//				File filenum1 = new File(file_url+stationid+"_last_file_num"+".txt");
//				BufferedWriter fwnum1 = null;
//				fwnum1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum1, true), "UTF-8")); //
//				//指定编码格式，以免读取时中文字符异常
//				fwnum1.append(dqdh+",");
//				fwnum1.flush();
//				fwnum1.close();
//				
//				//写编号
//				File filenum2 = new File(file_url+stationid+"_map_file_num"+".txt");
//				BufferedWriter fwnum2 = null;
//				fwnum2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum2, true), "UTF-8")); //
//				//指定编码格式，以免读取时中文字符异常
//				fwnum2.append(picnums.get(stationid)+",");
//				fwnum2.flush();
//				fwnum2.close();
//				 
//				//===========end_20170526写本地文件================//
				
				
				
				

			
				
				
//				// -------------写本地文件--------------------//
//				 String s_sql = "select * from M_STATION where stationid=(select stationid from g_zdgz where zdid=?)";
//				 Object[] params = new Object[] { s_zdid };
//				 List stationlst = jdbcT.queryForList(s_sql, params);
//				
//               
//				// 站点编号
//				String stationid;
//				stationid = String.valueOf(((Map) stationlst.get(0))
//						.get("stationid"));
//				String filename=file_url+stationid+".txt";
//				
//				///////////////20170524记录图像帧序号///////////
//				//临时文件名即站点编号
//				
////				String filename=file_url+stationid+".txt";
////				
////				BufferedWriter fwnum = null;
////				
////				File filenum = new File(file_url+"file_num"+stationid+".txt");
////				fwnum = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum, true), "UTF-8")); //
////				//指定编码格式，以免读取时中文字符异常
////				fwnum.append(s_dqdh+",\n");
////				fwnum.flush();
////				fwnum.close();
//				
//				
//				///////////////20170524记录图像帧序号///////////
//				
//				
//				// 接收状态 1接收完成; 2未接收完成; 3失败终止接收
//				int rstatus = 0;
//				if (dqdh == zdh - 1) {
//					rstatus = 1;
//				} else {
//					rstatus = 2;
//				}
//				
//			    s_sql = "select * from M_STATION_PICTURE_FILE_TMP where stationid=?";
//				params = new Object[] { stationid };
//				List lst = jdbcT.queryForList(s_sql, params);
//				// 如果站点图像暂存表M_STATION_PICTURE_FILE_TMP没有该站点数据则新增 否则更新
//				if (null == lst || lst.size() <= 0) {
//				
//					// 如果站点图像暂存表M_STATION_PICTURE_FILE_TMP没有该站点数据则新增 否则更新
//					
//					s_sql = "insert into M_STATION_PICTURE_FILE_TMP(STATIONID,datatime,SUMNUM,CURNUM,CURLENG,FILEURL,RSTATUS) "
//							+ "values(?,sysdate,?,?,?,?,?)";
//					params = new Object[] { stationid, zdh, dqdh, sjcd, filename, rstatus };
//					jdbcT.update(s_sql, params);
//					 
//					//临时文件名即站点编号
//					BufferedWriter fw = null;
//					File file = new File(filename);
//					fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//					//指定编码格式，以免读取时中文字符异常
//				    fw.append(bdtxsj);
//				    fw.flush();
//					fw.close();
//					
//					//2017-05-25将当前段号保存到全局变量中
//					picnums.put(stationid, dqdh);
//					
////					//2017-03-21放入明细表，待测试完好之后该功能取消
////					s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
////							+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
////					params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
////					jdbcT.update(s_sql, params);
//					
//					
//				} else {
////					stationid = String.valueOf(((Map) lst.get(0))
////							.get("stationid"));
//					//临时文件名即站点编号
////					String filename=file_url+stationid+".txt";
//					// 当前库中的rstatus值
//					int rs = Integer.parseInt(String.valueOf(((Map) lst.get(0))
//							.get("rstatus")));
//					// 如果上一次已经成功接收或者已经失败接收，就得重新接收，即判断当前段号是否为0
//					if (1 == rs || 3 == rs) {
//						
////						s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
////						params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
////						jdbcT.update(s_sql, params);
//						
//						//将图片文件至为空
////						RandomAccessFile rf = new RandomAccessFile(filename, "rw");
////						FileChannel fc = rf.getChannel();
////						//将文件大小截为0
////						fc.truncate(0);
//						BufferedWriter fw = null;
//						File file = new File(filename);
//						fw = new BufferedWriter(new FileWriter(file)); //
////						fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//						//指定编码格式，以免读取时中文字符异常
//					    fw.write("");
//						
//						
//						// 如果当前段号=0 则执行更新操作
//						if (dqdh == 0) {
//							
//							//2017-05-25将当前段号保存到全局变量中
//							picnums.put(stationid, dqdh);
//							
//							//指定编码格式，以免读取时中文字符异常
//						    fw.append(bdtxsj);
//						    fw.flush();
//							fw.close();
//							
////							//2017-03-21放入明细表，待测试完好之后该功能取消
////							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
////									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
////							params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
////							jdbcT.update(s_sql, params);
//							
//							s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//						} else {
//							fw.close();
//							System.out.println("上一次已经成功接收或者已经失败接收,需重新接收,但本帧非首帧,忽略本帧报文并拒绝回复确认，当前贞序号"+dqdh);
//							throw new Exception("上一次已经成功接收或者已经失败接收,需重新接收,但本帧非首帧,忽略本帧报文并拒绝回复确认，当前贞序号"+dqdh);
//							// 忽略本帧报文
////							continue;
//						}
//
//						// 如果上一次未成功接收，则判断当前段号是否>=库中的当前段号+1
//					} else {
////						// 2017-05-26取消----当前库中的段号值
////						int f_dh = Integer.parseInt(String.valueOf(((Map) lst
////								.get(0)).get("CURNUM")));
//						
//						// 2017-05-26当前记录的段号值
//						Integer f_dh=null;
//						if(null!=picnums||null!=picnums.get(stationid)){
//							f_dh =picnums.get(stationid);
//						}else{
//							f_dh = Integer.parseInt(String.valueOf(((Map) lst
//									.get(0)).get("CURNUM")));
//						}
//						
//						// 如果当前段号==库中的当前段号+1则执行更新操作
//						if (dqdh ==f_dh + 1) {
//							BufferedWriter fw = null;
//							File file = new File(filename);
//							fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//							//指定编码格式，以免读取时中文字符异常
//						    fw.append(bdtxsj);
//						    fw.flush();
//							fw.close();
//							
//							//2017-05-25将当前段号保存到全局变量中
//							picnums.put(stationid, dqdh);
//							
////							//2017-03-21放入明细表，待测试完好之后该功能取消
////							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
////									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
////							params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
////							jdbcT.update(s_sql, params);
//							
//							s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//						// 如果当前段号>库中的当前段号+1则进行补0后执行更新操作	
//						}
////						else if(dqdh >f_dh + 1){
////							//需要补的帧数
////							int bzs=dqdh-f_dh-1;
////							//补的贞内容
////							String bz="";
////							for(int i=0;i<bzs;i++){
////								bz=bz+supimgdata;
////							}
////							bdtxsj=bz+bdtxsj;
////							
////							String str="";
////							
////							Pattern p = Pattern.compile("\\s*|\t|\r|\n");
////							Matcher m = p.matcher(bdtxsj);
////							str = m.replaceAll("");
////							
////							BufferedWriter fw = null;
////							File file = new File(filename);
////							fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
////							//指定编码格式，以免读取时中文字符异常
////						    fw.append(str);
////						    fw.flush();
////							fw.close();
////							
////							//2017-03-21放入明细表，待测试完好之后该功能取消
////							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
////									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
////							params = new Object[] { s_zdid, zdh, dqdh, sjcd, str,rstatus };
////							jdbcT.update(s_sql, params);
////							
////							s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
////							params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
////							jdbcT.update(s_sql, params);
////						} 
//						else {
//							// 如果当前段号=0 先清空文件则执行更新操作
//							if (dqdh == 0) {
//								
//								//2017-05-25将当前段号保存到全局变量中
//								picnums.put(stationid, dqdh);
//								
//								BufferedWriter fw = null;
//								File file = new File(filename);
////								fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//								fw = new BufferedWriter(new FileWriter(file)); //
//								//指定编码格式，以免读取时中文字符异常
//							    fw.write("");
//							    
//								
//								//指定编码格式，以免读取时中文字符异常
//							    fw.append(bdtxsj);
//							    fw.flush();
//								fw.close();
//								
////								//2017-03-21放入明细表，待测试完好之后该功能取消
////								s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
////										+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
////								params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
////								jdbcT.update(s_sql, params);
//								
//								s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
//								params = new Object[] { zdh, dqdh, sjcd,rstatus, stationid };
//								jdbcT.update(s_sql, params);
//								
//								
//							} else {
//								// 忽略本帧报文
//								System.out.println("忽略本帧报文并拒绝回复确认，当前贞序号"+dqdh+",库中序号:"+f_dh);
//								throw new Exception("帧序号无法对应,当前贞序号"+dqdh+",库中序号:"+f_dh);
////								System.out.println("忽略本帧报文，贞序号是："+dqdh);
////								continue;
//							}
//						}
//					}
//				}
//				
//				System.out.println("当前贞序号："+picnums.get(stationid));
////				
//				//如果接收成功，则将文件报文解析成文件并将文件存放位置更新到照片的记录表中
//				if(1==rstatus){
//					
//					
//					String imgcode = Util.txt2String(new File(file_url+stationid+".txt"));
//					//图片名称
//					String imgname=new Date().getTime()+"station"+stationid+".jpg";
//					//存图文件
//	                String imgurl=file_url+imgname;
//					Util.saveToImgFile(imgcode, imgurl);
//					
//					//将暂存表的remark字段更新问图片的WEB路径
//					String imgweburl=web_url+imgname;
//					s_sql = "update M_STATION_PICTURE_FILE_TMP set RSTATUS=?,FILEURL=? where stationid=?";
//					params = new Object[] { 1,imgweburl, stationid };
//					jdbcT.update(s_sql, params);
//					
//				}
//				
//				
//				// -------------end写本地文件-----------------//

				
//				// -------------begin写数据库-----------------//
//				// 查询站点图像暂存表M_STATION_PICTURE_TMP
//				String s_sql = "select * from M_STATION_PICTURE_TMP where stationid=(select stationid from g_zdgz where zdid=?)";
//				Object[] params = new Object[] { s_zdid };
//				List lst = jdbcT.queryForList(s_sql, params);
//				// 站点编号
//				String stationid;
//				// 接收状态 1接收完成; 2未接收完成; 3失败终止接收
//				int rstatus = 0;
//				if (dqdh == zdh - 1) {
//					rstatus = 1;
//				} else {
//					rstatus = 2;
//				}
//				// 如果站点图像暂存表M_STATION_PICTURE_TMP没有该站点数据则新增 否则更新
//				if (null == lst || lst.size() <= 0) {
//					s_sql = "select * from m_station  where stationid=(select stationid from g_zdgz where zdid=?)";
//					List templst = jdbcT.queryForList(s_sql, params);
//					stationid = String.valueOf(((Map) templst.get(0))
//							.get("stationid"));
//
//					s_sql = "insert into M_STATION_PICTURE_TMP(STATIONID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,SUNFILE,RSTATUS) "
//							+ "values(?,sysdate,?,?,?,?,?,?)";
//					params = new Object[] { stationid, zdh, dqdh, sjcd, bdtxsj,
//							bdtxsj, rstatus };
//					jdbcT.update(s_sql, params);
//					
//					//2017-03-21放入明细表，待测试完好之后该功能取消
//					s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//							+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//					params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//					jdbcT.update(s_sql, params);
//					
//				} else {
//					stationid = String.valueOf(((Map) lst.get(0))
//							.get("stationid"));
//					// 当前库中的rstatus值
//					int rs = Integer.parseInt(String.valueOf(((Map) lst.get(0))
//							.get("rstatus")));
//					// 如果上一次已经成功接收或者已经失败接收，就得重新接收，即判断当前段号是否为0
//					if (1 == rs || 3 == rs) {
//						// 如果当前段号=0 则执行更新操作
//						if (dqdh == 0) {
//							s_sql = "update M_STATION_PICTURE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,CURFILE=?,SUNFILE=?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, bdtxsj,
//									bdtxsj, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//							
//							//2017-03-21放入明细表，待测试完好之后该功能取消
//							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//							params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//							jdbcT.update(s_sql, params);
//						} else {
//							// 忽略本帧报文
//							continue;
//						}
//
//						// 如果上一次未成功接收，则判断当前段号是否>=库中的当前段号+1
//					} else {
//						// 当前库中的段号值
//						int f_dh = Integer.parseInt(String.valueOf(((Map) lst
//								.get(0)).get("CURNUM")));
//						// 如果当前段号>=库中的当前段号+1则执行更新操作
//						if (dqdh >= f_dh + 1) {
//							s_sql = "update M_STATION_PICTURE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,CURFILE=?,SUNFILE=sunfile||?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, bdtxsj,
//									bdtxsj, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//							
//							//2017-03-21放入明细表，待测试完好之后该功能取消
//							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//							params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//							jdbcT.update(s_sql, params);
//						} else {
//							// 如果当前段号=0 则执行更新操作
//							if (dqdh == 0) {
//								s_sql = "update M_STATION_PICTURE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,CURFILE=?,SUNFILE=?,RSTATUS=? where stationid=?";
//								params = new Object[] { zdh, dqdh, sjcd,
//										bdtxsj, bdtxsj, rstatus, stationid };
//								jdbcT.update(s_sql, params);
//								
//								//2017-03-21放入明细表，待测试完好之后该功能取消
//								s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//										+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//								params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//								jdbcT.update(s_sql, params);
//							} else {
//								// 忽略本帧报文
//								continue;
//							}
//						}
//					}
//				}
//				
//				
//				// -------------end写数据库-----------------//
				
				
				
//				//如果接收成功，则将文件报文解析成文件并将文件存放位置更新到照片的记录表中
//				if(1==rstatus){
//					//保存文件
////					s_sql = "select * from M_STATION_PICTURE_TMP where stationid=? and rstatus=1";
////					params = new Object[] { stationid };
////					lst = jdbcT.queryForList(s_sql, params);
////					
////					InputStream in = (InputStream)((Map) lst.get(0)).get("sunfile");
////	                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
////	                byte[] data = new byte[4096];
////	                int count = -1;
////	                while((count = in.read(data,0,4096)) != -1)
////	                    outStream.write(data, 0, count);
////	                  
////	                String imgcode = new String(outStream.toByteArray(),"utf-8");
////	                
//////					String imgcode = String.valueOf(((Map) lst.get(0)).get("sunfile"));
////					String imgurl=file_url+new Date().getTime()+"station"+stationid+".jpg";
////					Util.saveToImgFile(imgcode, imgurl);
////					//存库
////					s_sql = " insert into M_STATION_PICTURE "+
////						       "(id,stationid,datatime,pname,Psource,Ptype,Picture0x,weburl) "+
////						       "values "+
////						       "(s_station_picture.nextval,?,sysdate,'远传抓拍图'||to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),1,1,?,?)";
////					params = new Object[] { stationid,imgcode,imgurl};
////					jdbcT.update(s_sql, params);
//					
//					final Writer writer=new FileWriter(file_url+"imgtmp.txt");  
//					final OracleLobHandler oracleLobHandler=new OracleLobHandler();
//					s_sql = "select sunfile from M_STATION_PICTURE_TMP where stationid="+stationid+" and rstatus=1";
//					jdbcT.query(s_sql,new AbstractLobStreamingResultSetExtractor() {
//						@Override
//						 protected void streamData(ResultSet rs) throws SQLException,IOException,DataAccessException{  
//                            FileCopyUtils.copy(oracleLobHandler.getClobAsCharacterStream(rs,1),writer);
//                         }
//                      });  
//					writer.close();
//					
//					String imgcode = Util.txt2String(new File(file_url+"imgtmp.txt"));
//					//图片名称
//					String imgname=new Date().getTime()+"station"+stationid+".jpg";
//					//存图文件
//	                String imgurl=file_url+imgname;
//					Util.saveToImgFile(imgcode, imgurl);
//					
//					//将赞纯表的remark字段更新问图片的WEB路径
//					String imgweburl=web_url+imgname;
//					s_sql = "update M_STATION_PICTURE_TMP set remark=? where stationid=?";
//					params = new Object[] { imgweburl, stationid };
//					jdbcT.update(s_sql, params);
//					
//					
////					final String imgstr = Util.txt2String(new File(file_url+"imgtmp.txt"));
////					s_sql = " insert into M_STATION_PICTURE "+
////						       "(id,stationid,datatime,pname,Psource,Ptype,Picture0x,weburl) "+
////						       "values "+
////						       "(s_station_picture.nextval,"+stationid+",sysdate,'远传抓拍'||to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),1,1,?,'"+imgurl+"')";
////					jdbcT.execute(s_sql,  
////                            new AbstractLobCreatingPreparedStatementCallback(oracleLobHandler) {
////								@Override
////								protected void setValues(PreparedStatement pstmt, LobCreator lobCreator)
////										throws SQLException, DataAccessException {
//////									lobCreator.setClobAsCharacterStream(pstmt,1,reader,(int)txtFile.length());  
////									lobCreator.setClobAsString(pstmt, 1, imgstr);
////									
////								}
////							});  
//					
//					
//				}
			} else if (s_Fdt.equals("F10")) {
                // F10:远程/就地状态检测（1代中无此项）
				cat.info("[Decode_0C]F10:远程/就地状态查询返回 ");
				// 信息点类别
				String xxdlb = "1";// 0:终端

				//  0x55 : 远程状态， 0xaa ：就地状态 0: 无信号
				String s_ycjdzt = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				if (!s_ycjdzt.equals("EE")) {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ycjdzt";
					values[2] = String.valueOf(s_ycjdzt);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "ycjdzt";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F11")) {

				// F11:读取故障灯状态（1代中无此项）
				cat.info("[Decode_0C]F11:故障灯状态查询返回 ");
				// 信息点类别
				String xxdlb = "1";// 0:终端

				// 故障灯状态 0x55 : 正常， 0xaa ：故障0: 无信号
				String s_gzdzt = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				if (!s_gzdzt.equals("EE")) {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "gzdzt";
					values[2] = String.valueOf(s_gzdzt);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "gzdzt";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			}else if (s_Fdt.equals("F12")) {

				// F12:当前水体PH数值的查询返回
				cat.info("[Decode_0C]F12:PH数值（一代=P0  二代=PN=池号）查询返回 ");
				// 信息点类别
				String xxdlb = "0";// 0:测量点

				
				// 当前PH数值
				String s_dqstph = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqstph.equalsIgnoreCase("EEEE")) {
					s_dqstph = Util.tranFormat30(s_dqstph);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqstph";
					values[2] = s_dqstph;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "dqstph";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 超警戒PH数值标志
				String s_cjjphszbz = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				if (!s_cjjphszbz.equals("EE")) {
					int i_cjjphszbz = Integer.parseInt(s_cjjphszbz, 16);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cjjphszbz";
					values[2] = String.valueOf(i_cjjphszbz);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cjjphszbz";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F13")) {
				// F13：浮球检测值（ 二代=PN=池号）
				cat.info("[Decode_0C]F13：浮球检测值的查询返回");
				// 信息点类别
				String xxdlb = "0";// 0:测量点

				// 浮球总数
				String s_fqzs = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				if (!s_fqzs.equalsIgnoreCase("EE")) {
					s_fqzs = Util.hexStrToDecStr(s_fqzs);
					int fqzs = Integer.parseInt(s_fqzs);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fqzs";
					values[2] = String.valueOf(fqzs);;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
                    values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "fqzs";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 浮球检测状态
				String s_jcfqzt = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				// 解析浮球状态的8位二进制，
				String s_jcfqzt_byte = "";
				
				if (!s_jcfqzt.equals("EE")) {
					// 存储检测浮球状态二进制
					s_jcfqzt_byte = Util.hexStrToBinStr(s_jcfqzt, 1);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "jcfqzt_byte";
					values[2] = String.valueOf(s_jcfqzt_byte);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					
					
					s_jcfqzt = Util.hexStrToDecStr(s_jcfqzt);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "jcfqzt";
					values[2] = String.valueOf(s_jcfqzt);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "jcfqzt_byte";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			
			}else if (s_Fdt.equals("F25")) {
				String s_sql="select rjbbh from g_zdgz where zdid=?";
        		String[] params = new String[] { s_zdid };
        	    List cldList = jdbcT.queryForList(s_sql, params);
        	    Map cldMap = (Map) cldList.get(0);
        	    // 本地软件版本号
        	 	String d_rjbbh = String.valueOf(cldMap.get("rjbbh"));
				
				// F25:当前三相及总有/无功功率、功率因数、三相电压、电流、零序电流的查询返回
				cat.info("[Decode_0C]F25:当前三相及总有/无功功率、功率因数、三相电压、电流、零序电流的查询返回");
				// 信息点类别
				String xxdlb = "0";// 1:测量点

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")
						&& !s_zdcbsj.equalsIgnoreCase("0000000000")) {
					s_zdcbsj = Util.convertStr(s_zdcbsj);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "zdcbsj";
					values[2] = s_zdcbsj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
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

				// int i_sw = 0;//最前一位数
				// String sw = "";

				// 当前总有功功率
				String s_dqzyggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqzyggl.equalsIgnoreCase("EEEEEE")) {
					s_dqzyggl = Util.tranFormat09(s_dqzyggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "yggl";
					values[2] = s_dqzyggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {

					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "yggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前A向有功功率
				String s_dqaxyggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqaxyggl.equalsIgnoreCase("EEEEEE")) {
					s_dqaxyggl = Util.tranFormat09(s_dqaxyggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axyggl";
					values[2] = s_dqaxyggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axyggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前B向有功功率
				String s_dqbxyggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqbxyggl.equalsIgnoreCase("EEEEEE")) {
					s_dqbxyggl = Util.tranFormat09(s_dqbxyggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxyggl";
					values[2] = s_dqbxyggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxyggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前C向有功功率
				String s_dqcxyggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqcxyggl.equalsIgnoreCase("EEEEEE")) {
					s_dqcxyggl = Util.tranFormat09(s_dqcxyggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxyggl";
					values[2] = s_dqcxyggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxyggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前总无功功率
				String s_dqzwggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqzwggl.equalsIgnoreCase("EEEEEE")) {
					s_dqzwggl = Util.tranFormat09(s_dqzwggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "wggl";
					values[2] = s_dqzwggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "wggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前A向无功功率
				String s_dqaxwggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqaxwggl.equalsIgnoreCase("EEEEEE")) {
					s_dqaxwggl = Util.tranFormat09(s_dqaxwggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axwggl";
					values[2] = s_dqaxwggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axwggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前B向无功功率
				String s_dqbxwggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqbxwggl.equalsIgnoreCase("EEEEEE")) {
					s_dqbxwggl = Util.tranFormat09(s_dqbxwggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxwggl";
					values[2] = s_dqbxwggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxwggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前C向无功功率
				String s_dqcxwggl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_dqcxwggl.equalsIgnoreCase("EEEEEE")) {
					s_dqcxwggl = Util.tranFormat09(s_dqcxwggl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxwggl";
					values[2] = s_dqcxwggl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxwggl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前总功率因数
				String s_dqzglys = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqzglys.equalsIgnoreCase("EEEE")) {
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqzglys = Util.tranFormat05(s_dqzglys);
					}else{
						s_dqzglys = Util.tranFormat30(s_dqzglys);
					}
					
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "glys";
					values[2] = s_dqzglys;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "glys";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前A向功率因数
				String s_dqaxglys = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqaxglys.equalsIgnoreCase("EEEE")) {
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqaxglys = Util.tranFormat05(s_dqaxglys);
					}else{
						s_dqaxglys = Util.tranFormat30(s_dqaxglys);
					}
//					s_dqaxglys = Util.tranFormat30(s_dqaxglys);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axglys";
					values[2] = s_dqaxglys;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axglys";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前B向功率因数
				String s_dqbxglys = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqbxglys.equalsIgnoreCase("EEEE")) {
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqbxglys = Util.tranFormat05(s_dqbxglys);
					}else{
						s_dqbxglys = Util.tranFormat30(s_dqbxglys);
					}
//					s_dqbxglys = Util.tranFormat30(s_dqbxglys);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxglys";
					values[2] = s_dqbxglys;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxglys";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前C向功率因数
				String s_dqcxglys = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqcxglys.equalsIgnoreCase("EEEE")) {
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqcxglys = Util.tranFormat05(s_dqcxglys);
					}else{
						s_dqcxglys = Util.tranFormat30(s_dqcxglys);
					}
//					s_dqcxglys = Util.tranFormat30(s_dqcxglys);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxglys";
					values[2] = s_dqcxglys;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxglys";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前A向电压
				String s_dqaxdy = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqaxdy.equalsIgnoreCase("EEEE")) {
					s_dqaxdy = Util.tranFormat07(s_dqaxdy);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axdy";
					values[2] = s_dqaxdy;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axdy";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前B向电压
				String s_dqbxdy = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqbxdy.equalsIgnoreCase("EEEE")) {
					s_dqbxdy = Util.tranFormat07(s_dqbxdy);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxdy";
					values[2] = s_dqbxdy;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxdy";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前C向电压
				String s_dqcxdy = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqcxdy.equalsIgnoreCase("EEEE")) {
					s_dqcxdy = Util.tranFormat07(s_dqcxdy);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxdy";
					values[2] = s_dqcxdy;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxdy";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前A向电流
				String s_dqaxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqaxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29对v2.0.8的终端单独解析
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqaxdl = Util.tranFormat05(s_dqaxdl);
					}else{
						s_dqaxdl = Util.tranFormat30(s_dqaxdl);
					}
					
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axdl";
					values[2] = s_dqaxdl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axdl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前B向电流
				String s_dqbxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqbxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29对v2.0.8的终端单独解析
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqbxdl = Util.tranFormat05(s_dqbxdl);
					}else{
						s_dqbxdl = Util.tranFormat30(s_dqbxdl);
					}
//					s_dqbxdl = Util.tranFormat30(s_dqbxdl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxdl";
					values[2] = s_dqbxdl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxdl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前C向电流
				String s_dqcxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqcxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29对v2.0.8的终端单独解析
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqcxdl = Util.tranFormat05(s_dqcxdl);
					}else{
						s_dqcxdl = Util.tranFormat30(s_dqcxdl);
					}
//					s_dqcxdl = Util.tranFormat30(s_dqcxdl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxdl";
					values[2] = s_dqcxdl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxdl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前零序电流
				String s_dqlxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqlxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29对v2.0.8的终端单独解析
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")){
						s_dqlxdl = Util.tranFormat06(s_dqlxdl);
					//2016-10-29对v2.0.9的终端单独解析
					}else if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						s_dqlxdl = Util.tranFormat05(s_dqlxdl);
					}else{
						s_dqlxdl = Util.tranFormat08(s_dqlxdl);
					}
//					s_dqlxdl = Util.tranFormat08(s_dqlxdl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "lxdl";
					values[2] = s_dqlxdl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "lxdl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前总视在功率
				String s_szgl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_szgl.equalsIgnoreCase("EEEEEE")) {
					s_szgl = Util.tranFormat09(s_szgl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "szgl";
					values[2] = s_szgl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "szgl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前A相视在功率
				String s_axszgl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_axszgl.equalsIgnoreCase("EEEEEE")) {
					s_axszgl = Util.tranFormat09(s_axszgl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axszgl";
					values[2] = s_axszgl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "axszgl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前B相视在功率
				String s_bxszgl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_bxszgl.equalsIgnoreCase("EEEEEE")) {
					s_bxszgl = Util.tranFormat09(s_bxszgl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxszgl";
					values[2] = s_bxszgl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "bxszgl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 当前C相视在功率
				String s_cxszgl = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_cxszgl.equalsIgnoreCase("EEEEEE")) {
					s_cxszgl = Util.tranFormat09(s_cxszgl);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxszgl";
					values[2] = s_cxszgl;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "cxszgl";
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F129") || s_Fdt.equals("F131")) {
				String sjxdm = "";
				if (s_Fdt.equals("F129")) {
					// F129:当前正向有功电能示值（总、费率1~M）
					cat.info("[Decode_0C]F129:当前正向有功电能示值(总、费率1~M)");
					sjxdm = "zxygzdnsz";
				} else if (s_Fdt.equals("F131")) {
					// F131:当前反向有功电能示值（总、费率1~M）
					cat.info("[Decode_0C]F131:当前反向有功电能示值(总、费率1~M)");
					sjxdm = "fxygzdnsz";
				}
				// 信息点类别
				String xxdlb = "0";// 1:测量点

				// 终端抄表时间
				String s_zdcbsj = DADT.substring(idx_dadt, idx_dadt + 10);
				idx_dadt += 10;
				if (!s_zdcbsj.equalsIgnoreCase("EEEEEEEEEE")
						&& !s_zdcbsj.equalsIgnoreCase("0000000000")) {
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
				if (!temps.equalsIgnoreCase("EEEEEEEEEE")) {
					temps = Util.tranFormat14(temps);
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				} else {
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = sjxdm;
					values[2] = "无效";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 正向有功总电能示值(费率1-费率n)
				for (int i = 1; i <= i_flgs; i++) {
					temps = DADT.substring(idx_dadt, idx_dadt + 10);
					idx_dadt += 10;
					if (!temps.equalsIgnoreCase("EEEEEEEEEE")) {
						temps = Util.tranFormat14(temps);
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = sjxdm + "fl" + i;
						values[2] = temps;
						values[3] = nowTime;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					} else {
						values = new String[7];
						values[0] = String.valueOf(i_xh++);
						values[1] = sjxdm + "fl" + i;
						values[2] = "无效";
						values[3] = nowTime;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "0";
						vt.add(values);
					}
				}

			}
			// 将此次PnFn数据放入Map
			hm.put(s_PF, vt);

		}

		return hm;
	}

	public void setOracleLobHandler(OracleLobHandler oracleLobHandler) {
		
	}

}