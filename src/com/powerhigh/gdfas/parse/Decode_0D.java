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
 * Description: AFN=0D(����2�����ݵ���Ӧ�����������ش���)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * ��дʱ��: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 �޸��ˣ� �޸�ʱ�䣺
 */

public class Decode_0D {
	// ������־
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

		// һ��ȡ�����巢����ϸ����
		// s_sql = "select sjzfsmxseq,sjdybsdm from g_sjzfssjdybszb "
		// +" where sjzfsseq=?";
		// params = new String[]{s_sjzfsseq};
		// List lstXl = (List)jdbcT.queryForList(s_sql,params);

		// ����д���ٲ����ݷ��ر�
		if (zt.equals("01")) {
			// ��ȷ����
			Set set = hm.keySet();
			Object[] obj = set.toArray();
			for (int i = 0; i < obj.length; i++) {
				String key = obj[i].toString();// PnFn
				Vector vt = (Vector) hm.get(key);

				// String sjzfsmxseq = "";//�����巢����ϸ����
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

				// ���ô洢����д���ٲ����ݷ��ر�
				Vector sp_param = new Vector();
				String sp_name = "sp_savebeckondata";
				String array = "";
				// 1������������
				sp_param.addElement(s_xzqxm);

				// 2���ն˵�ַ
				sp_param.addElement(s_zddz);

				// 3�������巢������
				sp_param.addElement(s_sjzfsseq);

				// 4��AFN
				sp_param.addElement("0D");

				// 5��PNFN
				sp_param.addElement(key);

				for (int j = 0; j < vt.size(); j++) {
					String[] values = (String[]) vt.get(j);
					String xh = values[0];// ���
					String sjxdm = values[1];// ���������
					String sjz = values[2];// ����ֵ
					String sjsj = values[3];// ����ʱ��
					String xxdlx = values[4];// ��Ϣ������
					String xxdh = values[5];// ��Ϣ���
					String flg = values[6];// �Ƿ����������(0:����;1:��)

					array += xh + "|" + sjxdm + "|" + sjz + "|" + sjsj + "|"
							+ xxdlx + "|" + xxdh + "#";

				}

				// 4�������ļ�¼
				sp_param.addElement(array);
				cat.info("[Decode_0D]array:" + array);

				// 3�����ô洢����
				if (array.equals("")) {
					cat.error("�ӱ�����δ�������Ϸ�����:" + sSJZ);
				} else {
					Util.executeProcedure(jdbcT, sp_name, sp_param, 2);
				}
			}

		}
		// �����޸������巢�ͱ��״̬
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
		// �������������ϱ�(ִ�д洢����sp_saveautotaskdata)
		Vector sp_param = new Vector();
		// 1������������
		sp_param.addElement(s_xzqxm);
		// 2���ն˵�ַ
		sp_param.addElement(s_zddz);

		// 3�������
		sp_param.addElement("");

		String nowTime = Util.getNowTime();// yyMMddHHmmss

		String array = "";
		for (int i = 0; i < datas.length; i++) {
			Vector vt = (Vector) datas[i];
			for (int j = 0; j < vt.size(); j++) {
				String[] ss = (String[]) vt.get(j);
				if (ss[6] == null || !ss[6].equals("1") || ss[2].equals("��Ч")) {
					continue;
				}
				array += ss[1] + "," + ss[2] + "," + ss[3] + "," + ss[4] + ","
						+ ss[5] + ";";
			}
		}

		// 5������
		sp_param.addElement(array);

		cat.info("[taskDecode]array:" + array);
		if (array.equals("")) {
			cat.error("�ӱ�����δ�������Ϸ�����:" + sSJZ);
		} else {
			Util.executeProcedure(jdbcT, "sp_saveautotaskdata", sp_param, 2);
		}
		if (sxlb.equals("2")) {
			// �����ϱ�
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

	// �������1����ѯ���أ�2�������ϱ�
	public static void dispose(String sxlb, String s_xzqxm, String s_zddz,
			String sSJZ, String fir_fin, String s_tpv, String s_acd,
			String s_csdata, String s_sjzfsseq, JdbcTemplate jdbcT)
			throws Exception {
		// ����2�����ݵ���Ӧ
		String DADT = "";
		HashMap hm = new HashMap();
		String zt = "01";// �ɹ�����
		if (fir_fin.equals("00") || fir_fin.equals("10")) {
			zt = "0A";// ���ַ���(�м���)
		}

		if (s_tpv.equals("1") && s_acd.equals("1")) {
			// ʱ���ǩ(6�ֽ�) and �¼�������(2�ֽ�)
			DADT = s_csdata.substring(16, s_csdata.length() - 16);
		} else if (s_tpv.equals("1") && s_acd.equals("0")) {
			// ʱ���ǩ(6�ֽ�)
			DADT = s_csdata.substring(16, s_csdata.length() - 12);
		} else if (s_tpv.equals("0") && s_acd.equals("1")) {
			// �¼�������(2�ֽ�)
			DADT = s_csdata.substring(16, s_csdata.length() - 4);
		} else if (s_tpv.equals("0") && s_acd.equals("0")) {
			// �޸�����Ϣ
			DADT = s_csdata.substring(16);
		}
		try {
			hm = decode(DADT);
		} catch (Exception e) {
			zt = "03";// ʧ�ܷ���
			// e.printStackTrace();
			cat.error("[Decode_0D]ERROR:", e);
		}

		if (sxlb.equals("1")) {
			// �ٲⷵ��
			savebeckondata(hm, s_xzqxm, s_zddz, zt, sSJZ, s_sjzfsseq, jdbcT);

			// ͬʱд������ϸ��
			saveautotaskdata(hm, s_xzqxm, s_zddz, zt, sSJZ, sxlb, jdbcT);

		} else if (sxlb.equals("2")) {
			// �����ϱ�
			saveautotaskdata(hm, s_xzqxm, s_zddz, zt, sSJZ, sxlb, jdbcT);
		}
	}

	public static HashMap decode(String DADT) throws Exception {
		HashMap hm = new HashMap();
		String nowTime = Util.getNowTime().substring(0, 10);// ����ʱ��YYMMDDhhmm
		int idx_dadt = 0;
		String s_dadt = "";
		String s_da = "";// ��Ϣ��Pn
		String s_dt = "";// ��Ϣ��Fn
		String s_PF = "";// PnFn

		// ��š���������롢����ֵ������ʱ�䡢��Ϣ�������Ϣ��š���־
		String[] values = null;// new String[7]

		cat.info("[Decode_0D]DADT:" + DADT);
		while (idx_dadt < DADT.length()) {
			// ------------------ÿ��PnFn-----------------
			s_dadt = DADT.substring(idx_dadt, idx_dadt + 8);
			idx_dadt += 8;

			// ��Ϣ��Pn
			s_da = s_dadt.substring(0, 4);
			s_da = Util.tranDA(Util.convertStr(s_da));
			String s_Pda = "P" + s_da;
			// ��Ϣ��Fn
			s_dt = s_dadt.substring(4, 8);
			s_dt = Util.tranDT(Util.convertStr(s_dt));
			String s_Fdt = "F" + s_dt;
			// PnFn
			s_PF = s_Pda + s_Fdt;
			cat.info("[Decode_0D]s_PF:" + s_PF);

			int i_xh = 1;// ���

			Vector vt = new Vector();

			if (s_Fdt.equals("F1") || s_Fdt.equals("F9") || s_Fdt.equals("F17")) {
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F1")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F9")) {
					// �����ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F17")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// �����й��ܵ���ʾֵ
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

				// �����й��ܵ���ʾֵ(����1-����n)
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

				// �����޹��ܵ���ʾֵ
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

				// �����޹��ܵ���ʾֵ(����1-����n)
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

				// һ�����޹��ܵ���ʾֵ
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

				// һ�����޹��ܵ���ʾֵ(����1-����n)
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

				// �������޹��ܵ���ʾֵ
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

				// �������޹��ܵ���ʾֵ(����1-����n)
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F2")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F10")) {
					// �����ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F18")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// �����й��ܵ���ʾֵ
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

				// �����й��ܵ���ʾֵ(����1-����n)
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

				// �����޹��ܵ���ʾֵ
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

				// �����޹��ܵ���ʾֵ(����1-����n)
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

				// �������޹��ܵ���ʾֵ
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

				// �������޹��ܵ���ʾֵ(����1-����n)
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

				// �������޹��ܵ���ʾֵ
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

				// �������޹��ܵ���ʾֵ(����1-����n)
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F3")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F11")) {
					// �����ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F19")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// �����й����������
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

				// �����й�����n�������(����1-����n)
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

				// �����й��������������ʱ��
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

				// �����й�����n�����������ʱ��(����1-����n)
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

				// �����޹����������
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

				// �����޹�����n�������(����1-����n)
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

				// �����޹��������������ʱ��
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

				// �����޹�����n�����������ʱ��(����1-����n)
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F4")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F12")) {
					// �����ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "cbrdjsjsb";

				} else if (s_Fdt.equals("F20")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// �����й����������
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

				// �����й�����n�������(����1-����n)
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

				// �����й��������������ʱ��
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

				// �����й�����n�����������ʱ��(����1-����n)
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

				// �����޹����������
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

				// �����޹�����n�������(����1-����n)
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

				// �����޹��������������ʱ��
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

				// �����޹�����n�����������ʱ��(����1-����n)
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

				String sjxdm = ""; // ���������
				String flsjxdm = "";// ���������(����)
				if (s_Fdt.equals("F5")) {
					// F5:�ն��������й�������(�ܡ�����1��M)
					sjxdm = "zxygzdnl";
					flsjxdm = "zxygdnlfl";
				} else if (s_Fdt.equals("F6")) {
					// F6:�ն��������޹�������(�ܡ�����1��M)
					sjxdm = "zxwgzdnl";
					flsjxdm = "zxwgdnlfl";
				} else if (s_Fdt.equals("F7")) {
					// F7:�ն��ᷴ���й�������(�ܡ�����1��M)
					sjxdm = "fxygzdnl";
					flsjxdm = "fxygdnlfl";
				} else if (s_Fdt.equals("F8")) {
					// F8:�ն��ᷴ���޹�������(�ܡ�����1��M)
					sjxdm = "fxwgzdnl";
					flsjxdm = "fxwgdnlfl";
				} else if (s_Fdt.equals("F21")) {
					// F21:�¶��������й�������(�ܡ�����1��M)
					sjxdm = "zxygzdnl";
					flsjxdm = "zxygdnlfl";
				} else if (s_Fdt.equals("F22")) {
					// F22:�¶��������޹�������(�ܡ�����1��M)
					sjxdm = "zxwgzdnl";
					flsjxdm = "zxwgdnlfl";
				} else if (s_Fdt.equals("F23")) {
					// F23:�¶��ᷴ���й�������(�ܡ�����1��M)
					sjxdm = "fxygzdnl";
					flsjxdm = "fxygdnlfl";
				} else if (s_Fdt.equals("F24")) {
					// F24:�¶��ᷴ���޹�������(�ܡ�����1��M)
					sjxdm = "fxwgzdnl";
					flsjxdm = "fxwgdnlfl";
				}

				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F5") || s_Fdt.equals("F6")
						|| s_Fdt.equals("F7") || s_Fdt.equals("F8")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F21") || s_Fdt.equals("F22")
						|| s_Fdt.equals("F23") || s_Fdt.equals("F24")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// �����й��ܵ�����
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

				// �����й�������(����1-����n)
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F25")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F33")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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

				// ������й�����
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

				// ������й����ʷ���ʱ��
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

				// A������й�����
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

				// A������й����ʷ���ʱ��
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

				// B������й�����
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

				// B������й����ʷ���ʱ��
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

				// C������й�����
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

				// C������й����ʷ���ʱ��
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

				// ���й�����Ϊ��ʱ��
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

				// A���й�����Ϊ��ʱ��
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

				// B���й�����Ϊ��ʱ��
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

				// C���й�����Ϊ��ʱ��
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

				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F26")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F34")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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

				// ���й��������
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

				// ���й������������ʱ��
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

				// A���й��������
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

				// A���й������������ʱ��
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

				// B���й��������
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

				// B���й������������ʱ��
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

				// C���й��������
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

				// C���й������������ʱ��
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

				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F27")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F35")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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

				// A���ѹԽ�������ۼ�ʱ��
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

				// A���ѹԽ�������ۼ�ʱ��
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

				// A���ѹԽ�����ۼ�ʱ��
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

				// A���ѹԽ�����ۼ�ʱ��
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

				// A���ѹ�ϸ��ۼ�ʱ��
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

				// B���ѹԽ�������ۼ�ʱ��
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

				// B���ѹԽ�������ۼ�ʱ��
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

				// B���ѹԽ�����ۼ�ʱ��
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

				// B���ѹԽ�����ۼ�ʱ��
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

				// B���ѹ�ϸ��ۼ�ʱ��
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

				// C���ѹԽ�������ۼ�ʱ��
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

				// C���ѹԽ�������ۼ�ʱ��
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

				// C���ѹԽ�����ۼ�ʱ��
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

				// C���ѹԽ�����ۼ�ʱ��
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

				// C���ѹ�ϸ��ۼ�ʱ��
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

				// A���ѹ���ֵ
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

				// A���ѹ���ֵ����ʱ��
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

				// A���ѹ��Сֵ
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

				// A���ѹ��Сֵ����ʱ��
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

				// B���ѹ���ֵ
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

				// B���ѹ���ֵ����ʱ��
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

				// B���ѹ��Сֵ
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

				// B���ѹ��Сֵ����ʱ��
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

				// C���ѹ���ֵ
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

				// C���ѹ���ֵ����ʱ��
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

				// C���ѹ��Сֵ
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

				// C���ѹ��Сֵ����ʱ��
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

				// A���ѹƽ��ֵ
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

				// B���ѹƽ��ֵ
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

				// C���ѹƽ��ֵ
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

				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F28")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F36")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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
				// ������ƽ���Խ���ۼ�ʱ��
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

				// ��ѹ��ƽ���Խ���ۼ�ʱ��
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

				// ������ƽ�����ֵ
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

				// ������ƽ�����ֵ����ʱ��
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

				// ��ѹ��ƽ�����ֵ
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

				// ��ѹ��ƽ�����ֵ����ʱ��
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

				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F29")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F37")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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
				// A�����Խ�������ۼ�ʱ��
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

				// A�����Խ�����ۼ�ʱ��
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

				// B�����Խ�������ۼ�ʱ��
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

				// B�����Խ�����ۼ�ʱ��
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

				// C�����Խ�������ۼ�ʱ��
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

				// C�����Խ�����ۼ�ʱ��
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

				// �������Խ�����ۼ�ʱ��
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

				// A��������ֵ
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

				// A��������ֵ����ʱ��
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

				// B��������ֵ
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

				// B��������ֵ����ʱ��
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

				// C��������ֵ
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

				// C��������ֵ����ʱ��
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

				// ����������ֵ
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

				// ����������ֵ����ʱ��
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

				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F30")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F38")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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
				// ���ڹ���Խ�������ۼ�ʱ��
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

				// ���ڹ���Խ�����ۼ�ʱ��
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

				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F31")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F39")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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
				// ���������ֵ
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

				// ���������ֵ����ʱ��
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

				// ��������Сֵ
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

				// ��������Сֵ����ʱ��
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �ն���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "rdjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// �ܶ������
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

				// A��������
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

				// B��������
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

				// C��������
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

				// �����ۼ�ʱ��
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

				// A������ۼ�ʱ��
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

				// B������ۼ�ʱ��
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

				// C������ۼ�ʱ��
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

				// ���һ�ζ�����ʼʱ��
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

				// A�����һ�ζ�����ʼʱ��
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

				// B�����һ�ζ�����ʼʱ��
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

				// C�����һ�ζ�����ʼʱ��
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

				// ���һ�ζ������ʱ��
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

				// A�����һ�ζ������ʱ��
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

				// B�����һ�ζ������ʱ��
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

				// C�����һ�ζ������ʱ��
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �ն���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "rdjsjsb";

				// @data ����ʱ��
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
				// 1-9��������ۼ�Ͷ��ʱ��
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

				// 1-9��������ۼ�Ͷ�����
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �ն���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "rdjsjsb";

				// @data ����ʱ��
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
				// �ղ����޹�������
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

				// �²����޹�������
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

				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F43")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F44")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
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
				// ������������1�ۼ�ʱ��
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

				// ������������2�ۼ�ʱ��
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

				// ������������3�ۼ�ʱ��
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
				// ��Ϣ�����
				String xxdlb = "0";// 0:�ն�
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �ն���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "rdjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն��չ���ʱ��
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

				// �ն��ո�λ�ۼƴ���
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
				// ��Ϣ�����
				String xxdlb = "0";// 0:�ն�
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �ն���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "rdjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �µ����բ���ۼƴ���
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

				// �������բ���ۼƴ���
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

				// ������բ���ۼƴ���
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

				// ҡ����բ���ۼƴ���
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
				// ��Ϣ�����
				String xxdlb = "0";// 0:�ն�
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �¶���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "ydjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն��¹���ʱ��
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

				// �ն��¸�λ�ۼƴ���
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
				// ��Ϣ�����
				String xxdlb = "0";// 0:�ն�
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �¶���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "ydjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �µ����բ���ۼƴ���
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

				// �������բ���ۼƴ���
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

				// ������բ���ۼƴ���
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

				// ҡ����բ���ۼƴ���
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
				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �ն���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "rdjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// ������й�����
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

				// ������й����ʷ���ʱ��
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

				// ����С�й�����
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

				// ����С�й����ʷ���ʱ��
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

				// �й�����Ϊ�����ۼ�ʱ��
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
				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F58")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F61")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String[] ss = null;
				String temps = "";

				// �ܼ��й��ܵ�����
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
					values[2] = "-1";// ��Ч
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// �ܼ��й�������(����1-����n)
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
						values[2] = "-1";// ��Ч
						values[3] = sjsj;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					}
				}

			} else if (s_Fdt.equals("F59") || s_Fdt.equals("F62")) {
				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				if (s_Fdt.equals("F59")) {
					// �ն���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
					idx_dadt += 6;
					if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "0000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "rdjsjsb";

				} else if (s_Fdt.equals("F62")) {
					// �¶���
					s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
					idx_dadt += 4;
					if (!s_sjsb.equalsIgnoreCase("EEEE")) {
						s_sjsb = Util.convertStr(s_sjsb);
						sjsj = s_sjsb + "010000";
					} else {
						s_sjsb = "��Ч";
					}

					sbdm = "ydjsjsb";
				}

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String[] ss = null;
				String temps = "";

				// �ܼ��޹��ܵ�����
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
					values[2] = "-1";// ��Ч
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

				// �ܼ��޹�������(����1-����n)
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
						values[2] = "-1";// ��Ч
						values[3] = sjsj;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "1";
						vt.add(values);
					}
				}

			} else if (s_Fdt.equals("F60")) {
				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �¶���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "ydjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ܼ���������й�����
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

				// �ܼ���������й����ʷ���ʱ��
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

				// �ܼ�������С�й�����
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

				// �ܼ�������С�й����ʷ���ʱ��
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

				// �ܼ������й�����Ϊ���ۼ�ʱ��
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
				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �¶���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "ydjsjsb";

				// @data ����ʱ��
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

				// �����ʶ�ֵ���ۼ�ʱ��
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

				// �����ʶ�ֵ���ۼƵ�����
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
				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���
				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";
				// �¶���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_sjsb.equalsIgnoreCase("EEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "010000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "ydjsjsb";

				// @data ����ʱ��
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

				// ���µ�������ֵ���ۼ�ʱ��
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

				// ���µ�������ֵ���ۼƵ�����
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
				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���

				String sjxdm = "";
				if (s_Fdt.equals("F73")) {
					// �й�����
					sjxdm = "yggl";
				} else if (s_Fdt.equals("F74")) {
					// �޹�����
					sjxdm = "wggl";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 4);// 2�ֽ�
					sjz = String.valueOf(Util.tranFormat02(sjz));
					idx_dadt += 4;
					// ����ʱ��(��ʼʱ��+ʱ����)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F75") || s_Fdt.equals("F76")) {

				// ��Ϣ�����
				String xxdlb = "2";// 2:�ܼ���

				String sjxdm = "";
				if (s_Fdt.equals("F75")) {
					// �й�������
					sjxdm = "ygdnl";
				} else if (s_Fdt.equals("F76")) {
					// �޹�������
					sjxdm = "wgdnl";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 8);// 4�ֽ�
					sjz = String.valueOf(Util.tranFormat03(sjz));
					idx_dadt += 8;
					// ����ʱ��(��ʼʱ��+ʱ����)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
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
				// �����㹦������

				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjxdm = "";
				if (s_Fdt.equals("F81")) {
					// �й�����
					sjxdm = "yggl";
				} else if (s_Fdt.equals("F82")) {
					// A���й�����
					sjxdm = "axyggl";
				} else if (s_Fdt.equals("F83")) {
					// B���й�����
					sjxdm = "bxyggl";
				} else if (s_Fdt.equals("F84")) {
					// C���й�����
					sjxdm = "cxyggl";
				} else if (s_Fdt.equals("F85")) {
					// �޹�����
					sjxdm = "wggl";
				} else if (s_Fdt.equals("F86")) {
					// A���޹�����
					sjxdm = "axwggl";
				} else if (s_Fdt.equals("F87")) {
					// B���޹�����
					sjxdm = "bxwggl";
				} else if (s_Fdt.equals("F88")) {
					// C���޹�����
					sjxdm = "cxwggl";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 6);// 3�ֽ�
					idx_dadt += 6;
					sjz = String.valueOf(Util.tranFormat09(sjz));
					// ����ʱ��(��ʼʱ��+ʱ����)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
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
				// �������ѹ����

				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjxdm = "";
				if (s_Fdt.equals("F89")) {
					// A���ѹ
					sjxdm = "axdy";
				} else if (s_Fdt.equals("F90")) {
					// B���ѹ
					sjxdm = "bxdy";
				} else if (s_Fdt.equals("F91")) {
					// C���ѹ
					sjxdm = "cxdy";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 4);// 2�ֽ�
					sjz = String.valueOf(Util.tranFormat07(sjz));
					idx_dadt += 4;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
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
				// �������������
				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjxdm = "";
				if (s_Fdt.equals("F92")) {
					// A�����
					sjxdm = "axdl";
				} else if (s_Fdt.equals("F93")) {
					// B�����
					sjxdm = "bxdl";
				} else if (s_Fdt.equals("F94")) {
					// C�����
					sjxdm = "cxdl";
				} else if (s_Fdt.equals("F95")) {
					// �������
					sjxdm = "lxdl";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
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
					values[0] = String.valueOf(i + 1);// ���
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
				// ©����������
				// ��Ϣ�����
				String xxdlb = "1";// 1:������
				String sjxdm = "";

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				int mm = 0;// ���
				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 24);
					idx_dadt += 24;
					String temps = "";
					// �õ��ѹ
					sjxdm = "yddy";
					temps = String.valueOf(Util.tranFormat07(sjz
							.substring(0, 4)));
					values = new String[7];
					values[0] = String.valueOf(mm);// ���
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// �õ����
					sjxdm = "yddl";
					temps = String.valueOf(Util.tranFormat25(sjz.substring(4,
							10)));
					values = new String[7];
					values[0] = String.valueOf(mm);// ���
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// ©�����
					sjxdm = "lddl";
					temps = String.valueOf(Util.tranFormat25(sjz.substring(10,
							16)));
					values = new String[7];
					values[0] = String.valueOf(mm);// ���
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// ����ʱ©�����
					sjxdm = "dzslddl";
					temps = String.valueOf(Util.tranFormat25(sjz.substring(16,
							22)));
					values = new String[7];
					values[0] = String.valueOf(mm);// ���
					values[1] = sjxdm;
					values[2] = temps;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
					mm++;

					// ©������״̬
					sjxdm = "lbkgzt";
					temps = sjz.substring(22, 24);
					if (temps.equalsIgnoreCase("EE")) {
						temps = "��Ч";
					} else {
						temps = Util.hexStrToBinStr(temps, 1);
					}
					values = new String[7];
					values[0] = String.valueOf(mm);// ���
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
				// �������ܵ���������
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				// ���������
				String sjxdm = "";
				if (s_Fdt.equals("F97")) {
					// �����й��ܵ�����
					sjxdm = "zxygzdnl";
				} else if (s_Fdt.equals("F98")) {
					// �����޹��ܵ�����
					sjxdm = "zxwgzdnl";
				} else if (s_Fdt.equals("F99")) {
					// �����й��ܵ�����
					sjxdm = "fxygzdnl";
				} else if (s_Fdt.equals("F100")) {
					// �����޹��ܵ�����
					sjxdm = "zxwgzdnl";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 8);// 4�ֽ�
					sjz = String.valueOf(Util.tranFormat13(sjz));
					idx_dadt += 8;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
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
				// �������ܵ���ʾֵ����
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				// ���������
				String sjxdm = "";
				if (s_Fdt.equals("F101")) {
					// �����й��ܵ���ʾֵ
					sjxdm = "zxygzdnsz";
				} else if (s_Fdt.equals("F102")) {
					// �����޹��ܵ���ʾֵ
					sjxdm = "zxwgzdnsz";
				} else if (s_Fdt.equals("F103")) {
					// �����й��ܵ���ʾֵ
					sjxdm = "fxygzdnsz";
				} else if (s_Fdt.equals("F104")) {
					// �����޹��ܵ���ʾֵ
					sjxdm = "zxwgzdnsz";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 8);// 4�ֽ�
					sjz = String.valueOf(Util.tranFormat11(sjz));
					idx_dadt += 8;
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
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
				// �����㹦����������
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				// ���������
				String sjxdm = "";
				if (s_Fdt.equals("F105")) {
					// ��������
					sjxdm = "glys";
				} else if (s_Fdt.equals("F106")) {
					// A�๦������
					sjxdm = "axglys";
				} else if (s_Fdt.equals("F107")) {
					// B�๦������
					sjxdm = "bxglys";
				} else if (s_Fdt.equals("F108")) {
					// C�๦������
					sjxdm = "cxglys";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
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
					values[0] = String.valueOf(i + 1);// ���
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = Util.addMinute(sjqssj, i * sjmd);
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F109")) {
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				String sjz = "";
				for (int i = 0; i < sjds; i++) {
					// ����ʱ��(��ʼʱ��+ʱ����)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);

					// Uab/Ua��λ��
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

					// Ub��λ��
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

					// Uc��λ��
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				String sjz = "";
				for (int i = 0; i < sjds; i++) {
					// ����ʱ��(��ʼʱ��+ʱ����)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);

					// Ia��λ��
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

					// Ib��λ��
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

					// Ic��λ��
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
				// ��Ϣ�����
				String xxdlb = "3";// 3:ֱ��ģ����

				String sjxdm = "zlmnldjsj";// ֱ��ģ������������

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				for (int i = 0; i < sjds; i++) {
					String sjz = DADT.substring(idx_dadt, idx_dadt + 4);// 2�ֽ�
					sjz = String.valueOf(Util.tranFormat02(sjz));
					idx_dadt += 4;
					// ����ʱ��(��ʼʱ��+ʱ����)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
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
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjxdm = "";
				if (s_Fdt.equals("F145")) {
					// һ�����޹��ܵ���ʾֵ����
					sjxdm = "1xxwgzdnsz";
				} else if (s_Fdt.equals("F146")) {
					// �������޹��ܵ���ʾֵ����
					sjxdm = "4xxwgzdnsz";
				} else if (s_Fdt.equals("F147")) {
					// �������޹��ܵ���ʾֵ����
					sjxdm = "2xxwgzdnsz";
				} else if (s_Fdt.equals("F148")) {
					// �������޹��ܵ���ʾֵ����
					sjxdm = "3xxwgzdnsz";
				}

				// 1������ʱ�꣨7�ֽڣ�
				String sjsb = DADT.substring(idx_dadt, idx_dadt + 14);
				idx_dadt += 14;
				String sjqssj = "";
				int sjmd = 0;
				int sjds = 0;
				if (!sjsb.equalsIgnoreCase("EEEEEEEEEEEEEE")) {
					// a��������ʼʱ��YYMMDDhhmm
					sjqssj = Util.convertStr(sjsb.substring(0, 10));

					// b)�����ܶ�
					sjmd = Integer.parseInt(sjsb.substring(10, 12), 16);
					if (sjmd == 0) {
						sjmd = 0;// ������
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
						sjmd = 0;// ����
					}

					// c)���ݵ���
					sjds = Integer.parseInt(sjsb.substring(12, 14), 16);

					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = sjqssj;
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = String.valueOf(sjmd);
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
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
					// @data ������ʼʱ��
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjqssj";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data �����ܶ�
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjmd";
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);

					// @data ���ݵ���
					values = new String[7];
					values[0] = String.valueOf(i_xh++);
					values[1] = "sjds";
					values[2] = "��Ч";
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
					// ����ʱ��(��ʼʱ��+ʱ����)
					String sjsj = Util.addMinute(sjqssj, i * sjmd);
					values = new String[7];
					values[0] = String.valueOf(i + 1);// ���
					values[1] = sjxdm;
					values[2] = sjz;
					values[3] = sjsj;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "1";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F161")) {
				// ��Ϣ�����
				String xxdlb = "1";// 1:������

				String sjsj = nowTime;
				// ����ʱ��Td
				String s_sjsb = "";
				String sbdm = "";

				// �ն���
				s_sjsb = DADT.substring(idx_dadt, idx_dadt + 6);
				idx_dadt += 6;
				if (!s_sjsb.equalsIgnoreCase("EEEEEE")) {
					s_sjsb = Util.convertStr(s_sjsb);
					sjsj = s_sjsb + "0000";
				} else {
					s_sjsb = "��Ч";
				}

				sbdm = "rdjsjsb";

				// @data ����ʱ��
				values = new String[7];
				values[0] = String.valueOf(i_xh++);
				values[1] = sbdm;
				values[2] = s_sjsb;
				values[3] = nowTime;
				values[4] = xxdlb;
				values[5] = s_da;
				values[6] = "0";
				vt.add(values);

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ���ʸ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				String temps = "";

				// �����й��ܵ���ʾֵ
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

				// �����й��ܵ���ʾֵ(����1-����n)
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

			// ���˴�PnFn���ݷ���HashMap
			hm.put(s_PF, vt);
		}

		return hm;
	}

}