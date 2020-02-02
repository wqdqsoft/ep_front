package com.powerhigh.gdfas.parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.*;

import javax.sql.DataSource;

import org.apache.log4j.*;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.util.*;

/**
 * Description: 数据上行处理类
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * 编写时间: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 修改人： 修改时间：
 */

public class receiveDispose {

	// 加载日志
	private static final String resource = "log4j.properties";
	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.parse.receiveDispose.class);
	// static {
	// PropertyConfigurator.configure(resource);
	// }

	public static String isDebug = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
			CMConfig.SYSTEM_DEBUG_KEY);// 是否显示调试信息(true：显示；其它：不显示)

	public static String buffer = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
			CMConfig.SYSTEM_DOWNLOADBUFFER_KEY);// 每次下载数据的大小

	private DataSource dataSource = null;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource ds) {
		dataSource = ds;
	}

	// Construct
	public receiveDispose() {

	}

	/**
	 * 上行数据帧解析
	 * 
	 * @param data
	 *            Object 上行数据对象
	 * @return
	 */
	public void run(Object data) throws Exception {
		DataObject obj = (DataObject) data;
		String sSJZ = obj.sjz;
		int moduleID = obj.moduleID;

		cat.info("[receiveDispose]SJZ=" + sSJZ + "(moduleID=" + moduleID + ")");

		// //2009-10-18去除浙规壳
		// String gdSJZ = sSJZ.substring(22);
		// gdSJZ = gdSJZ.substring(0,gdSJZ.length()-4);

		// 系统类型：国电
		cat.info("[receiveDispose]国电GPRS数据返回");
		gdProcess(obj);

	}

	/**
	 * 国电系统的返回处理
	 * 
	 * @param sSJZ
	 *            String 上行数据帧
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private void gdProcess(DataObject data) throws Exception {
		String sSJZ = data.sjz;
//		int moduleID = data.moduleID;
		String s_sql = "";
		int sjz_len = 0;
		String zdid = "";
		try {
			sjz_len = sSJZ.length();
			JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
			zdid = Util.getZdid(data, jdbcT);
			// 判断是否是GPRS终端的状态报告
			if (sSJZ.indexOf("state") != -1) {
				// GPRS终端的状态报告
				String[] ztbg = sSJZ.split(",");
				String state = ztbg[3];
				String[] params = new String[] { zdid };
				if (state.equalsIgnoreCase("online")) {
					// 在线
					s_sql = "update G_ZDGZ set zhtxsj=sysdate,zt='1' where zdid=?";
					jdbcT.update(s_sql, params);

				} else if (state.equalsIgnoreCase("outline")) {
					// 离线
					s_sql = "update G_ZDGZ set zhtxsj=sysdate,zt='0' where zdid=?";
					jdbcT.update(s_sql, params);
					
					
					// 2017-4-7加入终端通信记录表
					s_sql = "insert into g_zdtxztjlb values(s_zdtxzt.nextval,?,sysdate,0,sysdate,null,null)";
					jdbcT.update(s_sql, params);
					
				}
				return;
			}
			String s_begin1 = sSJZ.substring(0, 2);
			if (!s_begin1.equals("68")) {
				cat.error("begin1 is error:" + s_begin1);
				return;
			}
			// cat.info("sjz_len:" + sjz_len + "s_begin1:" + s_begin1);
			String s_len1 = sSJZ.substring(2, 6);
			s_len1 = Util.convertStr(s_len1);
			String s_len2 = sSJZ.substring(6, 10);
			s_len2 = Util.convertStr(s_len2);
			// cat.info("len1:" + s_len1 + "len2:" + s_len2);
			if (!s_len1.equals(s_len2)) {
				cat.error("len1!=len2:" + "len1:" + s_len1 + "len2:" + s_len2);
				return;
			}
			String s_begin2 = sSJZ.substring(10, 12);
			if (!s_begin2.equals("68")) {
				cat.error("begin2 is error:" + s_begin2);
				return;
			}
			String s_end = sSJZ.substring(sjz_len - 2, sjz_len);
			if (!s_end.equals("16")) {
				cat.error("end is error:" + s_end);
				return;
			}

			int data_len = Integer.parseInt(s_len1, 16);
			data_len = (data_len - 1) / 4;
			data_len = data_len * 2;
			if (sjz_len != (data_len + 16)) {
				cat.error("sjz_len != (data_len + 16)");
				return;
			}

			String s_csdata = sSJZ.substring(12, sjz_len - 4);
			// cat.info("s_csdata:" + s_csdata);
			String s_cs = sSJZ.substring(sjz_len - 4, sjz_len - 2);
			if (!(Util.getCS(s_csdata)).equalsIgnoreCase(s_cs)) {
				cat.error("CS is error!" + "s_cs:" + s_cs);
				return;
			}

			// 控制码
			String s_control = s_csdata.substring(0, 2);
			// cat.info("s_control:" + s_control);

			// 启动标志
			String s_qdbz = Util.hexStrToBinStr(s_control, 1);
			s_qdbz = s_qdbz.substring(1, 2);
			// cat.info("s_qdbz:" + s_qdbz);

			// 要求访问位ACD
			String s_acd = Util.hexStrToBinStr(s_control, 1);
			s_acd = s_acd.substring(2, 3);
			// cat.info("s_acd:"+s_acd);
			// 行政区县码
			String s_xzqxm = s_csdata.substring(2, 6);
			s_xzqxm = Util.convertStr(s_xzqxm);
			cat.info("s_xzqxm:" + s_xzqxm);

			// 终端地址
			String s_zddz = s_csdata.substring(6, 10);
			s_zddz = Util.convertStr(s_zddz);
			cat.info("s_zddz:" + s_zddz);

			// 应用功能码AFN
			String s_afn = s_csdata.substring(12, 14);
			// cat.info("s_afn:" + s_afn);

			// 帧序列域SEQ
			String s_seq = s_csdata.substring(14, 16);
			// cat.info("s_seq:" + s_seq);

			String fir_fin = Util.hexStrToBinStr(s_seq, 1);
			fir_fin = fir_fin.substring(1, 3);
			// cat.info("FIR_FIN:"+fir_fin);

			// 时间标签有效位TpV
			String s_tpv = Util.hexStrToBinStr(s_seq, 1);
			s_tpv = s_tpv.substring(0, 1);
			// cat.info("s_tpv:"+s_tpv);

			// 请求确认标志位CON
			String s_con = Util.hexStrToBinStr(s_seq, 1);
			s_con = s_con.substring(3, 4);
			// cat.info("s_con:"+s_con);

			// 附加信息域
			// 附加信息域-->启动帧时间(秒、分、时、日)
			String aux_time = "";
			// 附加信息域-->启动帧计数器PFC
			String aux_pfc = "";
			// 附加信息域-->事件计数器EC
			String aux_ec = "";
			if (s_tpv.equals("1") && s_acd.equals("1")) {
				aux_time = s_csdata.substring(data_len - 10, data_len - 2);
				aux_time = Util.convertStr(aux_time);
				// cat.info("aux_time:" + aux_time);

				aux_pfc = s_csdata.substring(data_len - 12, data_len - 10);
				// cat.info("aux_pfc:" + aux_pfc);

				aux_ec = s_csdata.substring(data_len - 16, data_len - 12);
				// cat.info("aux_ec:" + aux_ec);

			} else if (s_tpv.equals("1") && s_acd.equals("0")) {
				aux_time = s_csdata.substring(data_len - 10, data_len - 2);
				aux_time = Util.convertStr(aux_time);
				// cat.info("aux_time:" + aux_time);

				aux_pfc = s_csdata.substring(data_len - 12, data_len - 10);
				// cat.info("aux_pfc:" + aux_pfc);

			} else if (s_tpv.equals("0") && s_acd.equals("1")) {
				aux_ec = s_csdata.substring(data_len - 4, data_len);
				// cat.info("aux_ec:" + aux_ec);

			}

			String s_zdid = Util.getZdid(data, jdbcT);
			String[] params = null;

			// 更新最近通讯时间
			s_sql = "update G_ZDGZ set zhtxsj=sysdate where zdid=?";
			params = new String[] { s_zdid };
			jdbcT.update(s_sql, params);

			// 主动上报
			if (s_qdbz.equals("1")) {
				if (s_afn.equals("0C")) {
					
					cat.info("1类数据主动上报");
					Decode_0C.dispose("2", zdid, s_xzqxm, s_zddz, sSJZ, s_tpv,
							s_acd, s_csdata, "", jdbcT);
				} else if (s_afn.equals("0D")) {
					// 2类数据主动上报
					cat.info("2类数据主动上报");
					Decode_0D.dispose("2", s_xzqxm, s_zddz, sSJZ, fir_fin,
							s_tpv, s_acd, s_csdata, "", jdbcT);
				} else if (s_afn.equals("0A")) {
					// 终端参数主动上报
					cat.info("终端参数主动上报");
					Decode_0A.dispose( s_xzqxm, s_zddz, sSJZ,
							s_tpv, s_acd, s_csdata, "", jdbcT);
				} else if (s_afn.equals("0E")) {
					// 事件主动上报
					cat.info("事件主动上报");
					Decode_0E
							.dispose("2", "", s_xzqxm, s_zddz, s_csdata, jdbcT);
				} else if (s_afn.equals("0F")) {
					s_sql = "select sjzfsseq,isxztz,gnm,dbgylx from "
							+ "(select sjzfsseq,isxztz,gnm,dbgylx from g_sjzfsb "
							+ "where zdid=? " + "and seq=? " + " and zt=? "
							+ "order by fssj desc) where rownum=1 ";
					params = new String[] { s_zdid, s_seq.substring(1, 2), "02" };
					List lst = jdbcT.queryForList(s_sql, params);
					// 数据帧发送序列
					String s_sjzfsseq = String.valueOf(((Map) lst.get(0))
							.get("sjzfsseq"));

					s_sql = "select sjxdm,sjz from g_csszzcb where sjzfsseq=?";
					params = new String[] { s_sjzfsseq };
					lst = jdbcT.queryForList(s_sql, params);

					for (int i = 0; i < lst.size(); i++) {
						Map hm = (Map) lst.get(i);
						String sjxdm = String.valueOf(hm.get("sjxdm"));
						
						// 终端升级返回
						if ("AFN0FF1".equals(sjxdm)) {
							cat.info("终端升级返回");
							String s_dadt = s_csdata.substring(16, 24);
							cat.info("s_dadt:" + s_dadt);

							// 信息点Pn
							String s_da = s_dadt.substring(0, 4);
							s_da = Util.tranDA(Util.convertStr(s_da));
							String s_Pda = "P" + s_da;
							// 信息类Fn
							String s_dt = s_dadt.substring(4, 8);
							s_dt = Util.tranDT(Util.convertStr(s_dt));
							String s_Fdt = "F" + s_dt;
							// PnFn
							String s_PF = s_Pda + s_Fdt;
							/*** 2016-06-23 ***/
							// 终端文件发送的确认
							if (s_PF.equals("P0F1")) {
								s_sql = "update g_sjzfsb set zt='01',fhsj=sysdate,sxsjz='"
										+ sSJZ
										+ "' where sjzfsseq='"
										+ s_sjzfsseq
										+ "'";
								jdbcT.update(s_sql);

								return;
							}
						}
						//2017-06-24注释掉，放到decode_00中处理
//						else if ("AFN0FF2".equals(sjxdm)) {
//							cat.info("终端升级返回");
//							String s_dadt = s_csdata.substring(16, 24);
//							cat.info("s_dadt:" + s_dadt);
//
//							// 信息点Pn
//							String s_da = s_dadt.substring(0, 4);
//							s_da = Util.tranDA(Util.convertStr(s_da));
//							String s_Pda = "P" + s_da;
//							// 信息类Fn
//							String s_dt = s_dadt.substring(4, 8);
//							s_dt = Util.tranDT(Util.convertStr(s_dt));
//							String s_Fdt = "F" + s_dt;
//							// PnFn
//							String s_PF = s_Pda + s_Fdt;
//							/*** 2016-06-23 ***/
//							// 终端文件发送的确认
//							if (s_PF.equals("P0F1")) {
//								// 当前段号加1
//								s_sql = "update g_zdsjpzb set dqdh=dqdh+1,dqcd="+buffer+",zt=2 where zdid="+s_zdid;
//								jdbcT.update(s_sql);
//								Decode_0F.decodeDownload(s_xzqxm, s_zddz,s_sjzfsseq, jdbcT);
//								return;
//							}
//						}
					}

					
				}
				return;
			}

			// 根据行政区县码、终端地址、应用功能码、帧序列域、启动帧时间、状态(02:待返回)来取对应的下行记录
			s_sql = "select sjzfsseq,isxztz,gnm,dbgylx from "
					+ "(select sjzfsseq,isxztz,gnm,dbgylx from g_sjzfsb "
					+ "where zdid=? "
					// + " and gnm=? "
					+ "and seq=? "
					// + "' and qdzfssb='" + aux_time
					// + "' and pfc='" + aux_pfc
					+ " and zt=? " + "order by fssj desc) where rownum=1 ";
			params = new String[] { s_zdid, s_seq.substring(1, 2), "02" };
			List lst = jdbcT.queryForList(s_sql, params);
			if (isDebug.equalsIgnoreCase("true")) {
				// cat.info("s_sql:" + s_sql);
				// System.out.println("s_sql:" + s_sql);
			}

			// 四、响应事件的查询F1/F2
			if (s_afn.equals("0E") && lst.size() != 0 && s_qdbz.equals("0")) {
				cat.info("响应事件的查询");
				// 数据帧发送序列
				String s_sjzfsseq = String.valueOf(((Map) lst.get(0))
						.get("sjzfsseq"));
				// cat.info("s_sjzfsseq:" + s_sjzfsseq);

				Decode_0E.dispose("1", s_sjzfsseq, s_xzqxm, s_zddz, s_csdata,
						jdbcT);

				// 更改"数据帧发送表"的状态
				s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
						+ "where sjzfsseq=?";
				params = new String[] { "01", sSJZ, s_sjzfsseq };
				jdbcT.update(s_sql, params);

				return;
			}

//			// 五、终端请求下载  20170624被注释，放到decode_00中处理
//			if (s_afn.equals("0F")) {
//
//				// 数据帧发送序列
//				String s_sjzfsseq = String.valueOf(((Map) lst.get(0))
//						.get("sjzfsseq"));
//
//				s_sql = "select sjxdm,sjz from g_csszzcb where sjzfsseq=?";
//				params = new String[] { s_sjzfsseq };
//				List lst1 = jdbcT.queryForList(s_sql, params);
//
//				for (int i = 0; i < lst1.size(); i++) {
//					Map hm = (Map) lst1.get(i);
//					String sjxdm = String.valueOf(hm.get("sjxdm"));
//					
//					// 终端升级返回
//					if ("AFN0FF2".equals(sjxdm)) {
//						cat.info("终端升级返回");
//						String s_dadt = s_csdata.substring(16, 24);
//						cat.info("s_dadt:" + s_dadt);
//
//						// 信息点Pn
//						String s_da = s_dadt.substring(0, 4);
//						s_da = Util.tranDA(Util.convertStr(s_da));
//						String s_Pda = "P" + s_da;
//						// 信息类Fn
//						String s_dt = s_dadt.substring(4, 8);
//						s_dt = Util.tranDT(Util.convertStr(s_dt));
//						String s_Fdt = "F" + s_dt;
//						// PnFn
//						String s_PF = s_Pda + s_Fdt;
//						/*** 2016-06-23 ***/
//						// 终端文件发送的确认
//						if (s_PF.equals("P0F1")) {
//							// 当前段号加1
//							s_sql = "update g_zdsjpzb set dqdh=dqdh+1,dqcd="+buffer+",zt=2 where zdid="+s_zdid;
//							jdbcT.update(s_sql);
//							Decode_0F.decodeDownload(s_xzqxm, s_zddz,s_sjzfsseq, jdbcT);
//							return;
//						}
//					}
//				}
//			}

			// 以下：发送时均写了“数据帧发送表”
			// 数据帧发送序列
			//2016-08-31如果功能码是0A则允许lst为空
			
			String s_sjzfsseq = null;
			String s_isxztz = null;
			//String s_gnm = String.valueOf(((Map) lst.get(0)).get("gnm"));
			String dbgylx = null;
			
//			如果是0A的话  还有涉及到屏幕参数更改之后终端主动发送0A上来  所以可能会没有发下的seq
			if(!s_afn.equals("0A")){
				if(null!=lst&&lst.size()>0){
				s_sjzfsseq = String.valueOf(((Map) lst.get(0))
						.get("sjzfsseq"));
				s_isxztz = String.valueOf(((Map) lst.get(0)).get("isxztz"));
				//String s_gnm = String.valueOf(((Map) lst.get(0)).get("gnm"));
				dbgylx = String.valueOf(((Map) lst.get(0)).get("dbgylx"));
				}else{
					cat.error("没有响应的下行帧");
				}
			}else{
				if(null!=lst&&lst.size()>0){
					s_sjzfsseq = String.valueOf(((Map) lst.get(0))
							.get("sjzfsseq"));
					s_isxztz = String.valueOf(((Map) lst.get(0)).get("isxztz"));
					//String s_gnm = String.valueOf(((Map) lst.get(0)).get("gnm"));
					dbgylx = String.valueOf(((Map) lst.get(0)).get("dbgylx"));
				}
			}

//			if (isDebug.equalsIgnoreCase("true")) {
//				// cat.info("s_sjzfsseq:" + s_sjzfsseq);
//				// cat.info("s_isxztz:" + s_isxztz);
//				// System.out.println("s_sjzfsseq:" + s_sjzfsseq);
//				// System.out.println("s_isxztz:" + s_isxztz);
//			}

			// 六、是否下载通知
			if ("1".equals(s_isxztz)) {
				// 远程下载通知的返回，写“终端升级配置表”,AFN=00
				String s_dadt = s_csdata.substring(16, 24);
				cat.info("s_dadt:" + s_dadt);

				// 信息点Pn
				String s_da = s_dadt.substring(0, 4);
				s_da = Util.tranDA(Util.convertStr(s_da));
				String s_Pda = "P" + s_da;
				// 信息类Fn
				String s_dt = s_dadt.substring(4, 8);
				s_dt = Util.tranDT(Util.convertStr(s_dt));
				String s_Fdt = "F" + s_dt;
				// PnFn
				String s_PF = s_Pda + s_Fdt;
				if (s_PF.equalsIgnoreCase("P0F1")) {
					// F1全部确认
					s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
							+ " where sjzfsseq=?";
					params = new String[] { "01", sSJZ, s_sjzfsseq };
					jdbcT.update(s_sql, params);

					s_sql = "update g_zdsjpzb set sjzt=?,sj=sysdate "
							+ "where zdid=?";
					params = new String[] { "下载通知成功", s_zdid };
					jdbcT.update(s_sql, params);

				} else if (s_PF.equalsIgnoreCase("P0F2")) {
					// F2全部否认
					s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
							+ " where sjzfsseq=?";
					params = new String[] { "03", sSJZ, s_sjzfsseq };
					jdbcT.update(s_sql, params);

					s_sql = "update g_zdsjpzb set sjzt=?,sj=sysdate "
							+ "where zdid=?";
					params = new String[] { "下载通知失败", s_zdid };
					jdbcT.update(s_sql, params);

				}
				return;
			}

			// 七、其它
			if (s_afn.equals("0F")) {
//				String s_dadt = s_csdata.substring(16, 24);
//				// cat.info("s_dadt:" + s_dadt);
//
//				// 信息点Pn
//				String s_da = s_dadt.substring(0, 4);
//				s_da = Util.tranDA(Util.convertStr(s_da));
//				String s_Pda = "P" + s_da;
//				// 信息类Fn
//				String s_dt = s_dadt.substring(4, 8);
//				s_dt = Util.tranDT(Util.convertStr(s_dt));
//				String s_Fdt = "F" + s_dt;
//				// PnFn
//				String s_PF = s_Pda + s_Fdt;
//
//				if (!s_PF.equals("P0F3")) {
//					// F1:全部确认；F2:全部否认
//					// 终端响应远程下载命令
//					Decode_0F.decodeDownload(s_xzqxm, s_zddz,
//							s_seq.substring(1, 2), s_PF, s_sjzfsseq, sSJZ,
//							s_csdata.substring(24), moduleID, jdbcT);
//				}

			} else if (s_afn.equals("00")) {
				// 终端响应主站的控制命令和设置命令
				cat.info("afn=00:终端响应主站的控制命令和设置命令");
				Decode_00.dispose(s_xzqxm, s_zddz, sSJZ, s_csdata, s_sjzfsseq,
						jdbcT);

			} else if (s_afn.equals("0A")) { // 暂时只支持一个数据单元
				// 响应参数查询
				cat.info("afn=0A:响应参数查询");
				Decode_0A.dispose(s_xzqxm, s_zddz, sSJZ, s_tpv, s_acd,
						s_csdata, s_sjzfsseq, jdbcT);

			} else if (s_afn.equals("0C")) { // 暂时只支持一个数据单元
				// 请求1类数据的响应
				cat.info("afn=0C:请求1类数据的响应");
				Decode_0C.dispose("1", zdid, s_xzqxm, s_zddz, sSJZ, s_tpv,
						s_acd, s_csdata, s_sjzfsseq, jdbcT);

			} else if (s_afn.equals("0D")) {
				// 请求2类数据的响应
				cat.info("afn=0D:请求2类数据的响应");
				Decode_0D.dispose("1", s_xzqxm, s_zddz, sSJZ, fir_fin, s_tpv,
						s_acd, s_csdata, s_sjzfsseq, jdbcT);

			} else if (s_afn.equals("10")) {
				// 请求读中继数据的响应
				cat.info("afn=10:请求读中继数据的响应");
				Decode_10.dispose(s_xzqxm, s_zddz, sSJZ, s_tpv, s_acd,
						s_csdata, s_sjzfsseq, dbgylx, jdbcT);

			}

		} catch (Exception e) {
			// cat.info("receiveData Process Error:",e);
			throw e;
		}
	}

}
