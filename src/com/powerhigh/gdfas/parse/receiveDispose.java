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
 * Description: �������д�����
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * ��дʱ��: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 �޸��ˣ� �޸�ʱ�䣺
 */

public class receiveDispose {

	// ������־
	private static final String resource = "log4j.properties";
	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.parse.receiveDispose.class);
	// static {
	// PropertyConfigurator.configure(resource);
	// }

	public static String isDebug = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
			CMConfig.SYSTEM_DEBUG_KEY);// �Ƿ���ʾ������Ϣ(true����ʾ������������ʾ)

	public static String buffer = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
			CMConfig.SYSTEM_DOWNLOADBUFFER_KEY);// ÿ���������ݵĴ�С

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
	 * ��������֡����
	 * 
	 * @param data
	 *            Object �������ݶ���
	 * @return
	 */
	public void run(Object data) throws Exception {
		DataObject obj = (DataObject) data;
		String sSJZ = obj.sjz;
		int moduleID = obj.moduleID;

		cat.info("[receiveDispose]SJZ=" + sSJZ + "(moduleID=" + moduleID + ")");

		// //2009-10-18ȥ������
		// String gdSJZ = sSJZ.substring(22);
		// gdSJZ = gdSJZ.substring(0,gdSJZ.length()-4);

		// ϵͳ���ͣ�����
		cat.info("[receiveDispose]����GPRS���ݷ���");
		gdProcess(obj);

	}

	/**
	 * ����ϵͳ�ķ��ش���
	 * 
	 * @param sSJZ
	 *            String ��������֡
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
			// �ж��Ƿ���GPRS�ն˵�״̬����
			if (sSJZ.indexOf("state") != -1) {
				// GPRS�ն˵�״̬����
				String[] ztbg = sSJZ.split(",");
				String state = ztbg[3];
				String[] params = new String[] { zdid };
				if (state.equalsIgnoreCase("online")) {
					// ����
					s_sql = "update G_ZDGZ set zhtxsj=sysdate,zt='1' where zdid=?";
					jdbcT.update(s_sql, params);

				} else if (state.equalsIgnoreCase("outline")) {
					// ����
					s_sql = "update G_ZDGZ set zhtxsj=sysdate,zt='0' where zdid=?";
					jdbcT.update(s_sql, params);
					
					
					// 2017-4-7�����ն�ͨ�ż�¼��
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

			// ������
			String s_control = s_csdata.substring(0, 2);
			// cat.info("s_control:" + s_control);

			// ������־
			String s_qdbz = Util.hexStrToBinStr(s_control, 1);
			s_qdbz = s_qdbz.substring(1, 2);
			// cat.info("s_qdbz:" + s_qdbz);

			// Ҫ�����λACD
			String s_acd = Util.hexStrToBinStr(s_control, 1);
			s_acd = s_acd.substring(2, 3);
			// cat.info("s_acd:"+s_acd);
			// ����������
			String s_xzqxm = s_csdata.substring(2, 6);
			s_xzqxm = Util.convertStr(s_xzqxm);
			cat.info("s_xzqxm:" + s_xzqxm);

			// �ն˵�ַ
			String s_zddz = s_csdata.substring(6, 10);
			s_zddz = Util.convertStr(s_zddz);
			cat.info("s_zddz:" + s_zddz);

			// Ӧ�ù�����AFN
			String s_afn = s_csdata.substring(12, 14);
			// cat.info("s_afn:" + s_afn);

			// ֡������SEQ
			String s_seq = s_csdata.substring(14, 16);
			// cat.info("s_seq:" + s_seq);

			String fir_fin = Util.hexStrToBinStr(s_seq, 1);
			fir_fin = fir_fin.substring(1, 3);
			// cat.info("FIR_FIN:"+fir_fin);

			// ʱ���ǩ��ЧλTpV
			String s_tpv = Util.hexStrToBinStr(s_seq, 1);
			s_tpv = s_tpv.substring(0, 1);
			// cat.info("s_tpv:"+s_tpv);

			// ����ȷ�ϱ�־λCON
			String s_con = Util.hexStrToBinStr(s_seq, 1);
			s_con = s_con.substring(3, 4);
			// cat.info("s_con:"+s_con);

			// ������Ϣ��
			// ������Ϣ��-->����֡ʱ��(�롢�֡�ʱ����)
			String aux_time = "";
			// ������Ϣ��-->����֡������PFC
			String aux_pfc = "";
			// ������Ϣ��-->�¼�������EC
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

			// �������ͨѶʱ��
			s_sql = "update G_ZDGZ set zhtxsj=sysdate where zdid=?";
			params = new String[] { s_zdid };
			jdbcT.update(s_sql, params);

			// �����ϱ�
			if (s_qdbz.equals("1")) {
				if (s_afn.equals("0C")) {
					
					cat.info("1�����������ϱ�");
					Decode_0C.dispose("2", zdid, s_xzqxm, s_zddz, sSJZ, s_tpv,
							s_acd, s_csdata, "", jdbcT);
				} else if (s_afn.equals("0D")) {
					// 2�����������ϱ�
					cat.info("2�����������ϱ�");
					Decode_0D.dispose("2", s_xzqxm, s_zddz, sSJZ, fir_fin,
							s_tpv, s_acd, s_csdata, "", jdbcT);
				} else if (s_afn.equals("0A")) {
					// �ն˲��������ϱ�
					cat.info("�ն˲��������ϱ�");
					Decode_0A.dispose( s_xzqxm, s_zddz, sSJZ,
							s_tpv, s_acd, s_csdata, "", jdbcT);
				} else if (s_afn.equals("0E")) {
					// �¼������ϱ�
					cat.info("�¼������ϱ�");
					Decode_0E
							.dispose("2", "", s_xzqxm, s_zddz, s_csdata, jdbcT);
				} else if (s_afn.equals("0F")) {
					s_sql = "select sjzfsseq,isxztz,gnm,dbgylx from "
							+ "(select sjzfsseq,isxztz,gnm,dbgylx from g_sjzfsb "
							+ "where zdid=? " + "and seq=? " + " and zt=? "
							+ "order by fssj desc) where rownum=1 ";
					params = new String[] { s_zdid, s_seq.substring(1, 2), "02" };
					List lst = jdbcT.queryForList(s_sql, params);
					// ����֡��������
					String s_sjzfsseq = String.valueOf(((Map) lst.get(0))
							.get("sjzfsseq"));

					s_sql = "select sjxdm,sjz from g_csszzcb where sjzfsseq=?";
					params = new String[] { s_sjzfsseq };
					lst = jdbcT.queryForList(s_sql, params);

					for (int i = 0; i < lst.size(); i++) {
						Map hm = (Map) lst.get(i);
						String sjxdm = String.valueOf(hm.get("sjxdm"));
						
						// �ն���������
						if ("AFN0FF1".equals(sjxdm)) {
							cat.info("�ն���������");
							String s_dadt = s_csdata.substring(16, 24);
							cat.info("s_dadt:" + s_dadt);

							// ��Ϣ��Pn
							String s_da = s_dadt.substring(0, 4);
							s_da = Util.tranDA(Util.convertStr(s_da));
							String s_Pda = "P" + s_da;
							// ��Ϣ��Fn
							String s_dt = s_dadt.substring(4, 8);
							s_dt = Util.tranDT(Util.convertStr(s_dt));
							String s_Fdt = "F" + s_dt;
							// PnFn
							String s_PF = s_Pda + s_Fdt;
							/*** 2016-06-23 ***/
							// �ն��ļ����͵�ȷ��
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
						//2017-06-24ע�͵����ŵ�decode_00�д���
//						else if ("AFN0FF2".equals(sjxdm)) {
//							cat.info("�ն���������");
//							String s_dadt = s_csdata.substring(16, 24);
//							cat.info("s_dadt:" + s_dadt);
//
//							// ��Ϣ��Pn
//							String s_da = s_dadt.substring(0, 4);
//							s_da = Util.tranDA(Util.convertStr(s_da));
//							String s_Pda = "P" + s_da;
//							// ��Ϣ��Fn
//							String s_dt = s_dadt.substring(4, 8);
//							s_dt = Util.tranDT(Util.convertStr(s_dt));
//							String s_Fdt = "F" + s_dt;
//							// PnFn
//							String s_PF = s_Pda + s_Fdt;
//							/*** 2016-06-23 ***/
//							// �ն��ļ����͵�ȷ��
//							if (s_PF.equals("P0F1")) {
//								// ��ǰ�κż�1
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

			// �������������롢�ն˵�ַ��Ӧ�ù����롢֡����������֡ʱ�䡢״̬(02:������)��ȡ��Ӧ�����м�¼
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

			// �ġ���Ӧ�¼��Ĳ�ѯF1/F2
			if (s_afn.equals("0E") && lst.size() != 0 && s_qdbz.equals("0")) {
				cat.info("��Ӧ�¼��Ĳ�ѯ");
				// ����֡��������
				String s_sjzfsseq = String.valueOf(((Map) lst.get(0))
						.get("sjzfsseq"));
				// cat.info("s_sjzfsseq:" + s_sjzfsseq);

				Decode_0E.dispose("1", s_sjzfsseq, s_xzqxm, s_zddz, s_csdata,
						jdbcT);

				// ����"����֡���ͱ�"��״̬
				s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
						+ "where sjzfsseq=?";
				params = new String[] { "01", sSJZ, s_sjzfsseq };
				jdbcT.update(s_sql, params);

				return;
			}

//			// �塢�ն���������  20170624��ע�ͣ��ŵ�decode_00�д���
//			if (s_afn.equals("0F")) {
//
//				// ����֡��������
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
//					// �ն���������
//					if ("AFN0FF2".equals(sjxdm)) {
//						cat.info("�ն���������");
//						String s_dadt = s_csdata.substring(16, 24);
//						cat.info("s_dadt:" + s_dadt);
//
//						// ��Ϣ��Pn
//						String s_da = s_dadt.substring(0, 4);
//						s_da = Util.tranDA(Util.convertStr(s_da));
//						String s_Pda = "P" + s_da;
//						// ��Ϣ��Fn
//						String s_dt = s_dadt.substring(4, 8);
//						s_dt = Util.tranDT(Util.convertStr(s_dt));
//						String s_Fdt = "F" + s_dt;
//						// PnFn
//						String s_PF = s_Pda + s_Fdt;
//						/*** 2016-06-23 ***/
//						// �ն��ļ����͵�ȷ��
//						if (s_PF.equals("P0F1")) {
//							// ��ǰ�κż�1
//							s_sql = "update g_zdsjpzb set dqdh=dqdh+1,dqcd="+buffer+",zt=2 where zdid="+s_zdid;
//							jdbcT.update(s_sql);
//							Decode_0F.decodeDownload(s_xzqxm, s_zddz,s_sjzfsseq, jdbcT);
//							return;
//						}
//					}
//				}
//			}

			// ���£�����ʱ��д�ˡ�����֡���ͱ�
			// ����֡��������
			//2016-08-31�����������0A������lstΪ��
			
			String s_sjzfsseq = null;
			String s_isxztz = null;
			//String s_gnm = String.valueOf(((Map) lst.get(0)).get("gnm"));
			String dbgylx = null;
			
//			�����0A�Ļ�  �����漰����Ļ��������֮���ն���������0A����  ���Կ��ܻ�û�з��µ�seq
			if(!s_afn.equals("0A")){
				if(null!=lst&&lst.size()>0){
				s_sjzfsseq = String.valueOf(((Map) lst.get(0))
						.get("sjzfsseq"));
				s_isxztz = String.valueOf(((Map) lst.get(0)).get("isxztz"));
				//String s_gnm = String.valueOf(((Map) lst.get(0)).get("gnm"));
				dbgylx = String.valueOf(((Map) lst.get(0)).get("dbgylx"));
				}else{
					cat.error("û����Ӧ������֡");
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

			// �����Ƿ�����֪ͨ
			if ("1".equals(s_isxztz)) {
				// Զ������֪ͨ�ķ��أ�д���ն��������ñ�,AFN=00
				String s_dadt = s_csdata.substring(16, 24);
				cat.info("s_dadt:" + s_dadt);

				// ��Ϣ��Pn
				String s_da = s_dadt.substring(0, 4);
				s_da = Util.tranDA(Util.convertStr(s_da));
				String s_Pda = "P" + s_da;
				// ��Ϣ��Fn
				String s_dt = s_dadt.substring(4, 8);
				s_dt = Util.tranDT(Util.convertStr(s_dt));
				String s_Fdt = "F" + s_dt;
				// PnFn
				String s_PF = s_Pda + s_Fdt;
				if (s_PF.equalsIgnoreCase("P0F1")) {
					// F1ȫ��ȷ��
					s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
							+ " where sjzfsseq=?";
					params = new String[] { "01", sSJZ, s_sjzfsseq };
					jdbcT.update(s_sql, params);

					s_sql = "update g_zdsjpzb set sjzt=?,sj=sysdate "
							+ "where zdid=?";
					params = new String[] { "����֪ͨ�ɹ�", s_zdid };
					jdbcT.update(s_sql, params);

				} else if (s_PF.equalsIgnoreCase("P0F2")) {
					// F2ȫ������
					s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
							+ " where sjzfsseq=?";
					params = new String[] { "03", sSJZ, s_sjzfsseq };
					jdbcT.update(s_sql, params);

					s_sql = "update g_zdsjpzb set sjzt=?,sj=sysdate "
							+ "where zdid=?";
					params = new String[] { "����֪ͨʧ��", s_zdid };
					jdbcT.update(s_sql, params);

				}
				return;
			}

			// �ߡ�����
			if (s_afn.equals("0F")) {
//				String s_dadt = s_csdata.substring(16, 24);
//				// cat.info("s_dadt:" + s_dadt);
//
//				// ��Ϣ��Pn
//				String s_da = s_dadt.substring(0, 4);
//				s_da = Util.tranDA(Util.convertStr(s_da));
//				String s_Pda = "P" + s_da;
//				// ��Ϣ��Fn
//				String s_dt = s_dadt.substring(4, 8);
//				s_dt = Util.tranDT(Util.convertStr(s_dt));
//				String s_Fdt = "F" + s_dt;
//				// PnFn
//				String s_PF = s_Pda + s_Fdt;
//
//				if (!s_PF.equals("P0F3")) {
//					// F1:ȫ��ȷ�ϣ�F2:ȫ������
//					// �ն���ӦԶ����������
//					Decode_0F.decodeDownload(s_xzqxm, s_zddz,
//							s_seq.substring(1, 2), s_PF, s_sjzfsseq, sSJZ,
//							s_csdata.substring(24), moduleID, jdbcT);
//				}

			} else if (s_afn.equals("00")) {
				// �ն���Ӧ��վ�Ŀ����������������
				cat.info("afn=00:�ն���Ӧ��վ�Ŀ����������������");
				Decode_00.dispose(s_xzqxm, s_zddz, sSJZ, s_csdata, s_sjzfsseq,
						jdbcT);

			} else if (s_afn.equals("0A")) { // ��ʱֻ֧��һ�����ݵ�Ԫ
				// ��Ӧ������ѯ
				cat.info("afn=0A:��Ӧ������ѯ");
				Decode_0A.dispose(s_xzqxm, s_zddz, sSJZ, s_tpv, s_acd,
						s_csdata, s_sjzfsseq, jdbcT);

			} else if (s_afn.equals("0C")) { // ��ʱֻ֧��һ�����ݵ�Ԫ
				// ����1�����ݵ���Ӧ
				cat.info("afn=0C:����1�����ݵ���Ӧ");
				Decode_0C.dispose("1", zdid, s_xzqxm, s_zddz, sSJZ, s_tpv,
						s_acd, s_csdata, s_sjzfsseq, jdbcT);

			} else if (s_afn.equals("0D")) {
				// ����2�����ݵ���Ӧ
				cat.info("afn=0D:����2�����ݵ���Ӧ");
				Decode_0D.dispose("1", s_xzqxm, s_zddz, sSJZ, fir_fin, s_tpv,
						s_acd, s_csdata, s_sjzfsseq, jdbcT);

			} else if (s_afn.equals("10")) {
				// ������м����ݵ���Ӧ
				cat.info("afn=10:������м����ݵ���Ӧ");
				Decode_10.dispose(s_xzqxm, s_zddz, sSJZ, s_tpv, s_acd,
						s_csdata, s_sjzfsseq, dbgylx, jdbcT);

			}

		} catch (Exception e) {
			// cat.info("receiveData Process Error:",e);
			throw e;
		}
	}

}
