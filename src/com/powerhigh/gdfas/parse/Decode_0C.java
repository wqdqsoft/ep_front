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
 * Description: AFN=0C(����1�����ݵ���Ӧ�����������ش���)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * ��дʱ��: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 �޸��ˣ� �޸�ʱ�䣺
 */

public class Decode_0C {
	// ������־
	@SuppressWarnings("unused")
	private static final String resource = "log4j.properties";
	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.parse.Decode_0C.class);
	
//	private static String supimgdata="0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";

	// static {
	// PropertyConfigurator.configure(resource);
	// }
	public static String file_url = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_FILE_URL);//�ļ����Ŀ¼
	public static String web_url = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_WEB_URL);//�ļ�WEBĿ¼
	
	//���Դ����µ�ͼ��֡�����
	public  static  Map<String, Integer> picnums=new HashMap<String, Integer>();
	
	//���Դ� ������յ�ͼ����������ϸ������+2�����
	public  static  Map<String, Integer> rsnums=new HashMap<String, Integer>();
	
	//���Դ� ������յ�ͼ����������ϸ������+2��ͼ������
	public  static  Map<String, String> rsdata=new HashMap<String, String>();
	
	//�洢����ͼ������   key��  վ����_��ǰ�κ�
	public static Map<String, String> imgdata=new HashMap<String, String>();
	

	
	public Decode_0C() {

	}

	@SuppressWarnings({ "unchecked", "unused", "rawtypes" })
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

				// 3�������巢����ϸ����
				sp_param.addElement(s_sjzfsseq);

				// 4��AFN
				sp_param.addElement("0C");

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
				cat.info("[Decode_0C]array:" + array);

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

	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
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

		cat.info("[Decode_0C]array:" + array);
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
            
			//2016-09-28�޸�  �������������ݷŵ�G_ZDSBSJJLB
			s_sql = "insert into G_ZDSBSJJLB(sjzfsseq,zdid,gnm,zt,fhsj,sxsjz,fn) "
					+ "values(SEQ_ZDSBSJ.Nextval,"
					+ "(select zdid from G_ZDGZ where xzqxm=? and zddz=?),"
					+ "?,?,sysdate,?,?)";
			params = new String[] { s_xzqxm, s_zddz, "XX", zt, sSJZ, sFn };
			jdbcT.update(s_sql, params);
		}

	}

	// �������1����ѯ���أ�2�������ϱ�
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
			hm = decode(s_zdid, DADT, jdbcT);
		} catch (Exception e) {
			zt = "03";
			// e.printStackTrace();
			cat.error("[Decode_0C]ERROR:", e);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static HashMap decode(String s_zdid, String DADT, JdbcTemplate jdbcT)
			throws Exception {
		HashMap hm = new HashMap();

		int idx_dadt = 0;
		String s_dadt = "";
		String s_da = "";// ��Ϣ��Pn
		String s_dt = "";// ��Ϣ��Fn
		String s_PF = "";// PnFn

		String nowTime = Util.getNowTime();

		// ��š���������롢����ֵ������ʱ�䡢��Ϣ�������Ϣ��š���־
		String[] values = null;// new String[7]

		// cat.info("[Decode_0C]DADT:"+DADT);
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
			cat.info("[Decode_0C]s_PF:" + s_PF);

			int i_xh = 1;// ���
			Vector vt = new Vector();
			if (s_Fdt.equals("F1")) {
				// F1:�ն˰汾��Ϣ�Ĳ�ѯ����
				cat.info("[Decode_0C]F1:�ն˰汾��Ϣ�Ĳ�ѯ����");
				// ��Ϣ�����
				String xxdlb = "0";// 0:�ն�

				// ���̴���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// �豸���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ����汾��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// Ӳ���汾��
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
					values[2] = "��Ч";
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

				// F2:�ն�����ʱ�ӵĲ�ѯ����
				cat.info("[Decode_0C]F2:�ն�����ʱ�ӵĲ�ѯ����");

				// ��Ϣ�����
				String xxdlb = "0";// 0:�ն�

				// �ն�����ʱ��
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
				// �ն�﮵�ص�ѹ
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
				
				//2017-05-16����  ����g_zdtxztjlb��ص�ѹ
				s_sql = "insert into G_ZDTXZTJLB(id,zdid,jlsj,zhtxsj,dcdy) values(s_zdtxzt.nextval,?,sysdate,sysdate,?)";      			
      	        params = new String[]{s_zdid,s_zddcdy};
                jdbcT.update(s_sql,params);

			} else if (s_Fdt.equals("F4")) {

				// F4:�����Ĳ�ѯ����
				cat.info("[Decode_0C]F4:�����Ĳ�ѯ����");

				// ��Ϣ�����
				String xxdlb = "0";// 0:�ն�

				// ˲ʱ����
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

				// �ۼ�����
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

				// F5:��ǰˮλ�߶ȵĲ�ѯ����
				cat.info("[Decode_0C]F5:��ǰˮλ�߶ȵĲ�ѯ����");
				// ��Ϣ�����
				String xxdlb = "0";// 0:������

				// ��ǰˮλ�߶�
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ����״̬ ����״̬��1Ϊ��ˮλ��2��3Ϊ��ˮλ��4Ϊ��ˮλ
				String s_fqzt = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				// ��������״̬�ĵ���λ�����Ա�ʾ����ˮ�أ����ڳغʹ���أ��ĸ���״̬
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
						
						
	
						// �洢����״̬������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ˮλ���ޱ�־
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ����ˮλ���ޱ�־
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// 20160223������ڳ�û�г�������ˮλ�������������ն��Զ�����
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
								// 20160222�ر������ն���������Ϊ����Ĳ�����
								for (int jj = 0; jj < clds.size(); jj++) {
									Map cld = (Map) clds.get(jj);
									operation.sendAFN05F1("3",
											String.valueOf(zd.get("xzqxm")),
											String.valueOf(zd.get("zddz")),
											String.valueOf(cld.get("cldh")),
											"CC");
									// 20160222���1��
									Thread.sleep(1000L);
								}

							}

						}
					}
				}

			} else if (s_Fdt.equals("F6")) {

				// F6:��ǰˮ���¶ȵĲ�ѯ����
				cat.info("[Decode_0C]F6:��ǰˮ���¶ȵĲ�ѯ���� ");
				// ��Ϣ�����
				String xxdlb = "0";// 0:������
				
//				String s_sql="select zdxh from g_zdgz where zdid=?";
//        		String[] params = new String[] { s_zdid };
//        	    List cldList = jdbcT.queryForList(s_sql, params);
//        	    Map cldMap = (Map) cldList.get(0);
//        	    // �ն��ͺ�
//        	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
//        	 	
//        	 	if("1".equalsIgnoreCase(zdxh)){
//        	 		
//        	 	}

				// ��ǰ�����¶�
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰˮ���¶�
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F7")) {

				// F7:��ǰˮ��ORP��ֵ�Ĳ�ѯ����
				cat.info("[Decode_0C]F7:��ǰˮ��ORP��ֵ�Ĳ�ѯ���� ");
				// ��Ϣ�����
				String xxdlb = "0";// 0:������

				// ��ǰˮ��ORP��ֵ
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ����ORP��ֵ��־
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}
				

			} else if (s_Fdt.equals("F8")) {

				// F8:��ǰ����豸���ݵĲ�ѯ����
				cat.info("[Decode_0C]F8:��ǰ����豸���ݵĲ�ѯ���� ");
				// ��Ϣ�����
				String xxdlb = "1";// 0:�ն�

				// ��ǰ�����ͣ״̬
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰ����Ƶ�ʣ���Ա�Ƶ�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰ����ۼ�����ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// �ۼ���ͣ����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F9")) {
				// F9:��ǰͼƬץȡ�Ĳ�ѯ����
				cat.info("[Decode_0C]F9:��ǰͼƬץȡ�Ĳ�ѯ����");
				// ��Ϣ�����

				// �ܶκ�
				int zdh = 0;
				// ��ǰ�κ�i
				int dqdh = 0;
				// ��i�����ݳ���
				int sjcd = 0;
				// ����ͼ������
				String bdtxsj = "";
				
				

				// �ܶ���
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

				// ��ǰ�κ�
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

				// ��i�����ݳ���Lf
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

				// ��i���ļ�����
				bdtxsj = DADT.substring(idx_dadt, idx_dadt + sjcd * 2);
				idx_dadt += sjcd * 2;
				
				
				 //===========20170526д�����ļ�================//
				 String s_sql = "select * from M_STATION where stationid=(select stationid from g_zdgz where zdid=?)";
				 Object[] params = new Object[] { s_zdid };
				 List stationlst = jdbcT.queryForList(s_sql, params);
				 
				 String stationid;
				stationid = String.valueOf(((Map) stationlst.get(0))
						.get("stationid"));
				String filename=file_url+stationid+".txt";
				
				//д���
//				File filenum = new File(file_url+stationid+"_first_file_num"+".txt");
//				BufferedWriter fwnum = null;
//				fwnum = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum, true), "UTF-8")); //
//				//ָ�������ʽ�������ȡʱ�����ַ��쳣
//				fwnum.append(dqdh+"");
//				fwnum.newLine();
//				fwnum.flush();
//				fwnum.close();
				
				//2017-06-27����κŴ��ڵ����ܶκ�������
				if(dqdh>=zdh){
					continue;
				}
				
				// ����״̬ 1�������; 2δ�������; 3ʧ����ֹ����
				int rstatus = 0;
				if (dqdh == zdh - 1) {
					imgdata.put(stationid+"_"+dqdh,bdtxsj);
					String imghex="";
					for(int i=0;i<zdh;i++){
						imghex=imghex+imgdata.get(stationid+"_"+i);
					}
					rstatus = 1;
					//ͼƬ����
					String imgname=stationid+"station"+new Date().getTime()+".jpg";
					//��ͼ�ļ�
	                String imgurl=file_url+imgname;
					Util.saveToImgFile(imghex, imgurl);
					
					//���ݴ���remark�ֶθ�����ͼƬ��WEB·��
					String imgweburl=web_url+imgname;
					s_sql = "update M_STATION_PICTURE_FILE_TMP set RSTATUS=?,FILEURL=? where stationid=?";
					params = new Object[] { 1,imgweburl, stationid };
					jdbcT.update(s_sql, params);
				} else {
					
					rstatus = 2;
					
					imgdata.put(stationid+"_"+dqdh,bdtxsj);
					
					//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
					picnums.put(stationid, dqdh);
					
					s_sql = "select * from M_STATION_PICTURE_FILE_TMP where stationid=?";
					params = new Object[] { stationid };
					List lst = jdbcT.queryForList(s_sql, params);
					// ���վ��ͼ���ݴ��M_STATION_PICTURE_FILE_TMPû�и�վ������������ �������
					if (null == lst || lst.size() <= 0) {
					    // ���վ��ͼ���ݴ��M_STATION_PICTURE_FILE_TMPû�и�վ������������ �������
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
				
				 
				//===========end_20170526д�����ļ�================//
				
				
//				//===========20170526д�����ļ�================//
//				 String s_sql = "select * from M_STATION where stationid=(select stationid from g_zdgz where zdid=?)";
//				 Object[] params = new Object[] { s_zdid };
//				 List stationlst = jdbcT.queryForList(s_sql, params);
//				 
//				 String stationid;
//				stationid = String.valueOf(((Map) stationlst.get(0))
//						.get("stationid"));
//				String filename=file_url+stationid+".txt";
////				System.out.println("��ǰ����ţ�"+picnums.get(stationid));
////				System.out.println("��ǰ����ţ�"+dqdh);
//				
//				//д���
//				File filenum = new File(file_url+stationid+"_first_file_num"+".txt");
//				BufferedWriter fwnum = null;
//				fwnum = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum, true), "UTF-8")); //
//				//ָ�������ʽ�������ȡʱ�����ַ��쳣
//				fwnum.append(dqdh+",");
//				fwnum.flush();
//				fwnum.close();
//				
//				
//				
//				// ����״̬ 1�������; 2δ�������; 3ʧ����ֹ����
//				int rstatus = 0;
//				if (dqdh == zdh - 1) {
//					rstatus = 1;
//					String imgcode = Util.txt2String(new File(file_url+stationid+".txt"));
//					//ͼƬ����
//					String imgname=stationid+"station"+new Date().getTime()+".jpg";
//					//��ͼ�ļ�
//	                String imgurl=file_url+imgname;
//					Util.saveToImgFile(imgcode, imgurl);
//					
//					//���ݴ���remark�ֶθ�����ͼƬ��WEB·��
//					String imgweburl=web_url+imgname;
//					s_sql = "update M_STATION_PICTURE_FILE_TMP set RSTATUS=?,FILEURL=? where stationid=?";
//					params = new Object[] { 1,imgweburl, stationid };
//					jdbcT.update(s_sql, params);
//				} else {
//					rstatus = 2;
//					//�����ǰ�κ�==0
//					if(0==dqdh){
//						//����ļ���д�뵱ǰ֡
//						BufferedWriter fw = null;
//						File file = new File(filename);
//						fw = new BufferedWriter(new FileWriter(file)); 
//					    fw.write("");
//					    //ָ�������ʽ�������ȡʱ�����ַ��쳣
//					    fw.append(bdtxsj);
//					    fw.flush();
//						fw.close();
//						
//						//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
//						picnums.put(stationid, dqdh);
//						
//						s_sql = "select * from M_STATION_PICTURE_FILE_TMP where stationid=?";
//						params = new Object[] { stationid };
//						List lst = jdbcT.queryForList(s_sql, params);
//						// ���վ��ͼ���ݴ��M_STATION_PICTURE_FILE_TMPû�и�վ������������ �������
//						if (null == lst || lst.size() <= 0) {
//						    // ���վ��ͼ���ݴ��M_STATION_PICTURE_FILE_TMPû�и�վ������������ �������
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
//						//���map������ȡ������ȡ����
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
//						//д���
//						File filenum3 = new File(file_url+stationid+"_f_dh_file_num"+".txt");
//						BufferedWriter fwnum3 = null;
//						fwnum3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum3, true), "UTF-8")); //
//						//ָ�������ʽ�������ȡʱ�����ַ��쳣
//						fwnum3.append("f_dh="+f_dh+"&isdb="+isdb+",");
//						fwnum3.flush();
//						fwnum3.close();
//						 
//						
//						// �����ǰ�κ�==���еĵ�ǰ�κ�+1��ִ�и��²���
//						if (dqdh ==f_dh + 1) {
//							//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
//							
//							
//							try {
//								BufferedWriter fw = null;
//								File file = new File(filename);
//								fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//								//ָ�������ʽ�������ȡʱ�����ַ��쳣
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
//								System.out.println("=======================================================�ļ�д��ʧ��"+dqdh);
////								throw new Exception("=======================================================�ļ�д��ʧ��"+dqdh);
//								cat.error("=======================================================�ļ�д��ʧ��" + stationid+"||||"+dqdh+"||||"+isdb);
//								// TODO: handle exception
//							}
//								
//								
//								s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
//								params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
//								jdbcT.update(s_sql, params);
//							
//							
//						// �����ǰ�κ�=���еĵ�ǰ�κ�+2����֡�ȴ�����	
//						}else if (dqdh ==f_dh + 2){
//							rsnums.put(stationid, dqdh);
//							rsdata.put(stationid, bdtxsj);
//							
//						// �����ǰ�κ�>���еĵ�ǰ�κ�+3����в�0��ִ�и��²���		
//						}else if (dqdh ==f_dh + 3){
//							BufferedWriter fw = null;
//							File file = new File(filename);
//							fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//							//ָ�������ʽ�������ȡʱ�����ַ��쳣
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
//						// �����ǰ�κ�>���еĵ�ǰ�κ�+2����в�0��ִ�и��²���		
//						}else {
//							// ���Ա�֡����
//							System.out.println("���Ա�֡���Ĳ��ܾ��ظ�ȷ�ϣ���ǰ�����"+dqdh+",�������:"+f_dh);
////							throw new Exception("֡����޷���Ӧ,��ǰ�����"+dqdh+",�������:"+f_dh);
//							
////							BufferedWriter fw = null;
////							File file = new File(filename);
////							fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
////							//ָ�������ʽ�������ȡʱ�����ַ��쳣
////						    fw.append(bdtxsj);
////						    fw.flush();
////							fw.close();
////							
////							//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
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
//				//д���
//				File filenum1 = new File(file_url+stationid+"_last_file_num"+".txt");
//				BufferedWriter fwnum1 = null;
//				fwnum1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum1, true), "UTF-8")); //
//				//ָ�������ʽ�������ȡʱ�����ַ��쳣
//				fwnum1.append(dqdh+",");
//				fwnum1.flush();
//				fwnum1.close();
//				
//				//д���
//				File filenum2 = new File(file_url+stationid+"_map_file_num"+".txt");
//				BufferedWriter fwnum2 = null;
//				fwnum2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum2, true), "UTF-8")); //
//				//ָ�������ʽ�������ȡʱ�����ַ��쳣
//				fwnum2.append(picnums.get(stationid)+",");
//				fwnum2.flush();
//				fwnum2.close();
//				 
//				//===========end_20170526д�����ļ�================//
				
				
				
				

			
				
				
//				// -------------д�����ļ�--------------------//
//				 String s_sql = "select * from M_STATION where stationid=(select stationid from g_zdgz where zdid=?)";
//				 Object[] params = new Object[] { s_zdid };
//				 List stationlst = jdbcT.queryForList(s_sql, params);
//				
//               
//				// վ����
//				String stationid;
//				stationid = String.valueOf(((Map) stationlst.get(0))
//						.get("stationid"));
//				String filename=file_url+stationid+".txt";
//				
//				///////////////20170524��¼ͼ��֡���///////////
//				//��ʱ�ļ�����վ����
//				
////				String filename=file_url+stationid+".txt";
////				
////				BufferedWriter fwnum = null;
////				
////				File filenum = new File(file_url+"file_num"+stationid+".txt");
////				fwnum = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filenum, true), "UTF-8")); //
////				//ָ�������ʽ�������ȡʱ�����ַ��쳣
////				fwnum.append(s_dqdh+",\n");
////				fwnum.flush();
////				fwnum.close();
//				
//				
//				///////////////20170524��¼ͼ��֡���///////////
//				
//				
//				// ����״̬ 1�������; 2δ�������; 3ʧ����ֹ����
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
//				// ���վ��ͼ���ݴ��M_STATION_PICTURE_FILE_TMPû�и�վ������������ �������
//				if (null == lst || lst.size() <= 0) {
//				
//					// ���վ��ͼ���ݴ��M_STATION_PICTURE_FILE_TMPû�и�վ������������ �������
//					
//					s_sql = "insert into M_STATION_PICTURE_FILE_TMP(STATIONID,datatime,SUMNUM,CURNUM,CURLENG,FILEURL,RSTATUS) "
//							+ "values(?,sysdate,?,?,?,?,?)";
//					params = new Object[] { stationid, zdh, dqdh, sjcd, filename, rstatus };
//					jdbcT.update(s_sql, params);
//					 
//					//��ʱ�ļ�����վ����
//					BufferedWriter fw = null;
//					File file = new File(filename);
//					fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//					//ָ�������ʽ�������ȡʱ�����ַ��쳣
//				    fw.append(bdtxsj);
//				    fw.flush();
//					fw.close();
//					
//					//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
//					picnums.put(stationid, dqdh);
//					
////					//2017-03-21������ϸ�����������֮��ù���ȡ��
////					s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
////							+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
////					params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
////					jdbcT.update(s_sql, params);
//					
//					
//				} else {
////					stationid = String.valueOf(((Map) lst.get(0))
////							.get("stationid"));
//					//��ʱ�ļ�����վ����
////					String filename=file_url+stationid+".txt";
//					// ��ǰ���е�rstatusֵ
//					int rs = Integer.parseInt(String.valueOf(((Map) lst.get(0))
//							.get("rstatus")));
//					// �����һ���Ѿ��ɹ����ջ����Ѿ�ʧ�ܽ��գ��͵����½��գ����жϵ�ǰ�κ��Ƿ�Ϊ0
//					if (1 == rs || 3 == rs) {
//						
////						s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
////						params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
////						jdbcT.update(s_sql, params);
//						
//						//��ͼƬ�ļ���Ϊ��
////						RandomAccessFile rf = new RandomAccessFile(filename, "rw");
////						FileChannel fc = rf.getChannel();
////						//���ļ���С��Ϊ0
////						fc.truncate(0);
//						BufferedWriter fw = null;
//						File file = new File(filename);
//						fw = new BufferedWriter(new FileWriter(file)); //
////						fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//						//ָ�������ʽ�������ȡʱ�����ַ��쳣
//					    fw.write("");
//						
//						
//						// �����ǰ�κ�=0 ��ִ�и��²���
//						if (dqdh == 0) {
//							
//							//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
//							picnums.put(stationid, dqdh);
//							
//							//ָ�������ʽ�������ȡʱ�����ַ��쳣
//						    fw.append(bdtxsj);
//						    fw.flush();
//							fw.close();
//							
////							//2017-03-21������ϸ�����������֮��ù���ȡ��
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
//							System.out.println("��һ���Ѿ��ɹ����ջ����Ѿ�ʧ�ܽ���,�����½���,����֡����֡,���Ա�֡���Ĳ��ܾ��ظ�ȷ�ϣ���ǰ�����"+dqdh);
//							throw new Exception("��һ���Ѿ��ɹ����ջ����Ѿ�ʧ�ܽ���,�����½���,����֡����֡,���Ա�֡���Ĳ��ܾ��ظ�ȷ�ϣ���ǰ�����"+dqdh);
//							// ���Ա�֡����
////							continue;
//						}
//
//						// �����һ��δ�ɹ����գ����жϵ�ǰ�κ��Ƿ�>=���еĵ�ǰ�κ�+1
//					} else {
////						// 2017-05-26ȡ��----��ǰ���еĶκ�ֵ
////						int f_dh = Integer.parseInt(String.valueOf(((Map) lst
////								.get(0)).get("CURNUM")));
//						
//						// 2017-05-26��ǰ��¼�Ķκ�ֵ
//						Integer f_dh=null;
//						if(null!=picnums||null!=picnums.get(stationid)){
//							f_dh =picnums.get(stationid);
//						}else{
//							f_dh = Integer.parseInt(String.valueOf(((Map) lst
//									.get(0)).get("CURNUM")));
//						}
//						
//						// �����ǰ�κ�==���еĵ�ǰ�κ�+1��ִ�и��²���
//						if (dqdh ==f_dh + 1) {
//							BufferedWriter fw = null;
//							File file = new File(filename);
//							fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//							//ָ�������ʽ�������ȡʱ�����ַ��쳣
//						    fw.append(bdtxsj);
//						    fw.flush();
//							fw.close();
//							
//							//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
//							picnums.put(stationid, dqdh);
//							
////							//2017-03-21������ϸ�����������֮��ù���ȡ��
////							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
////									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
////							params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
////							jdbcT.update(s_sql, params);
//							
//							s_sql = "update M_STATION_PICTURE_FILE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//						// �����ǰ�κ�>���еĵ�ǰ�κ�+1����в�0��ִ�и��²���	
//						}
////						else if(dqdh >f_dh + 1){
////							//��Ҫ����֡��
////							int bzs=dqdh-f_dh-1;
////							//����������
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
////							//ָ�������ʽ�������ȡʱ�����ַ��쳣
////						    fw.append(str);
////						    fw.flush();
////							fw.close();
////							
////							//2017-03-21������ϸ�����������֮��ù���ȡ��
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
//							// �����ǰ�κ�=0 ������ļ���ִ�и��²���
//							if (dqdh == 0) {
//								
//								//2017-05-25����ǰ�κű��浽ȫ�ֱ�����
//								picnums.put(stationid, dqdh);
//								
//								BufferedWriter fw = null;
//								File file = new File(filename);
////								fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//								fw = new BufferedWriter(new FileWriter(file)); //
//								//ָ�������ʽ�������ȡʱ�����ַ��쳣
//							    fw.write("");
//							    
//								
//								//ָ�������ʽ�������ȡʱ�����ַ��쳣
//							    fw.append(bdtxsj);
//							    fw.flush();
//								fw.close();
//								
////								//2017-03-21������ϸ�����������֮��ù���ȡ��
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
//								// ���Ա�֡����
//								System.out.println("���Ա�֡���Ĳ��ܾ��ظ�ȷ�ϣ���ǰ�����"+dqdh+",�������:"+f_dh);
//								throw new Exception("֡����޷���Ӧ,��ǰ�����"+dqdh+",�������:"+f_dh);
////								System.out.println("���Ա�֡���ģ�������ǣ�"+dqdh);
////								continue;
//							}
//						}
//					}
//				}
//				
//				System.out.println("��ǰ����ţ�"+picnums.get(stationid));
////				
//				//������ճɹ������ļ����Ľ������ļ������ļ����λ�ø��µ���Ƭ�ļ�¼����
//				if(1==rstatus){
//					
//					
//					String imgcode = Util.txt2String(new File(file_url+stationid+".txt"));
//					//ͼƬ����
//					String imgname=new Date().getTime()+"station"+stationid+".jpg";
//					//��ͼ�ļ�
//	                String imgurl=file_url+imgname;
//					Util.saveToImgFile(imgcode, imgurl);
//					
//					//���ݴ���remark�ֶθ�����ͼƬ��WEB·��
//					String imgweburl=web_url+imgname;
//					s_sql = "update M_STATION_PICTURE_FILE_TMP set RSTATUS=?,FILEURL=? where stationid=?";
//					params = new Object[] { 1,imgweburl, stationid };
//					jdbcT.update(s_sql, params);
//					
//				}
//				
//				
//				// -------------endд�����ļ�-----------------//

				
//				// -------------beginд���ݿ�-----------------//
//				// ��ѯվ��ͼ���ݴ��M_STATION_PICTURE_TMP
//				String s_sql = "select * from M_STATION_PICTURE_TMP where stationid=(select stationid from g_zdgz where zdid=?)";
//				Object[] params = new Object[] { s_zdid };
//				List lst = jdbcT.queryForList(s_sql, params);
//				// վ����
//				String stationid;
//				// ����״̬ 1�������; 2δ�������; 3ʧ����ֹ����
//				int rstatus = 0;
//				if (dqdh == zdh - 1) {
//					rstatus = 1;
//				} else {
//					rstatus = 2;
//				}
//				// ���վ��ͼ���ݴ��M_STATION_PICTURE_TMPû�и�վ������������ �������
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
//					//2017-03-21������ϸ�����������֮��ù���ȡ��
//					s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//							+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//					params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//					jdbcT.update(s_sql, params);
//					
//				} else {
//					stationid = String.valueOf(((Map) lst.get(0))
//							.get("stationid"));
//					// ��ǰ���е�rstatusֵ
//					int rs = Integer.parseInt(String.valueOf(((Map) lst.get(0))
//							.get("rstatus")));
//					// �����һ���Ѿ��ɹ����ջ����Ѿ�ʧ�ܽ��գ��͵����½��գ����жϵ�ǰ�κ��Ƿ�Ϊ0
//					if (1 == rs || 3 == rs) {
//						// �����ǰ�κ�=0 ��ִ�и��²���
//						if (dqdh == 0) {
//							s_sql = "update M_STATION_PICTURE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,CURFILE=?,SUNFILE=?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, bdtxsj,
//									bdtxsj, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//							
//							//2017-03-21������ϸ�����������֮��ù���ȡ��
//							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//							params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//							jdbcT.update(s_sql, params);
//						} else {
//							// ���Ա�֡����
//							continue;
//						}
//
//						// �����һ��δ�ɹ����գ����жϵ�ǰ�κ��Ƿ�>=���еĵ�ǰ�κ�+1
//					} else {
//						// ��ǰ���еĶκ�ֵ
//						int f_dh = Integer.parseInt(String.valueOf(((Map) lst
//								.get(0)).get("CURNUM")));
//						// �����ǰ�κ�>=���еĵ�ǰ�κ�+1��ִ�и��²���
//						if (dqdh >= f_dh + 1) {
//							s_sql = "update M_STATION_PICTURE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,CURFILE=?,SUNFILE=sunfile||?,RSTATUS=? where stationid=?";
//							params = new Object[] { zdh, dqdh, sjcd, bdtxsj,
//									bdtxsj, rstatus, stationid };
//							jdbcT.update(s_sql, params);
//							
//							//2017-03-21������ϸ�����������֮��ù���ȡ��
//							s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//									+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//							params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//							jdbcT.update(s_sql, params);
//						} else {
//							// �����ǰ�κ�=0 ��ִ�и��²���
//							if (dqdh == 0) {
//								s_sql = "update M_STATION_PICTURE_TMP set datatime=sysdate,SUMNUM=?,CURNUM=?,CURLENG=?,CURFILE=?,SUNFILE=?,RSTATUS=? where stationid=?";
//								params = new Object[] { zdh, dqdh, sjcd,
//										bdtxsj, bdtxsj, rstatus, stationid };
//								jdbcT.update(s_sql, params);
//								
//								//2017-03-21������ϸ�����������֮��ù���ȡ��
//								s_sql = "insert into M_STATION_PICTURE_DETAIL(ID,ZDID,datatime,SUMNUM,CURNUM,CURLENG,CURFILE,RSTATUS) "
//										+ "values(S_STATION_PICTURE_DETAIL.Nextval,?,sysdate,?,?,?,?,?)";
//								params = new Object[] { s_zdid, zdh, dqdh, sjcd, bdtxsj,rstatus };
//								jdbcT.update(s_sql, params);
//							} else {
//								// ���Ա�֡����
//								continue;
//							}
//						}
//					}
//				}
//				
//				
//				// -------------endд���ݿ�-----------------//
				
				
				
//				//������ճɹ������ļ����Ľ������ļ������ļ����λ�ø��µ���Ƭ�ļ�¼����
//				if(1==rstatus){
//					//�����ļ�
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
////					//���
////					s_sql = " insert into M_STATION_PICTURE "+
////						       "(id,stationid,datatime,pname,Psource,Ptype,Picture0x,weburl) "+
////						       "values "+
////						       "(s_station_picture.nextval,?,sysdate,'Զ��ץ��ͼ'||to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),1,1,?,?)";
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
//					//ͼƬ����
//					String imgname=new Date().getTime()+"station"+stationid+".jpg";
//					//��ͼ�ļ�
//	                String imgurl=file_url+imgname;
//					Util.saveToImgFile(imgcode, imgurl);
//					
//					//���޴����remark�ֶθ�����ͼƬ��WEB·��
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
////						       "(s_station_picture.nextval,"+stationid+",sysdate,'Զ��ץ��'||to_char(sysdate,'yyyy-mm-dd hh24:mi:ss'),1,1,?,'"+imgurl+"')";
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
                // F10:Զ��/�͵�״̬��⣨1�����޴��
				cat.info("[Decode_0C]F10:Զ��/�͵�״̬��ѯ���� ");
				// ��Ϣ�����
				String xxdlb = "1";// 0:�ն�

				//  0x55 : Զ��״̬�� 0xaa ���͵�״̬ 0: ���ź�
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F11")) {

				// F11:��ȡ���ϵ�״̬��1�����޴��
				cat.info("[Decode_0C]F11:���ϵ�״̬��ѯ���� ");
				// ��Ϣ�����
				String xxdlb = "1";// 0:�ն�

				// ���ϵ�״̬ 0x55 : ������ 0xaa ������0: ���ź�
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			}else if (s_Fdt.equals("F12")) {

				// F12:��ǰˮ��PH��ֵ�Ĳ�ѯ����
				cat.info("[Decode_0C]F12:PH��ֵ��һ��=P0  ����=PN=�غţ���ѯ���� ");
				// ��Ϣ�����
				String xxdlb = "0";// 0:������

				
				// ��ǰPH��ֵ
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ������PH��ֵ��־
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F13")) {
				// F13��������ֵ�� ����=PN=�غţ�
				cat.info("[Decode_0C]F13��������ֵ�Ĳ�ѯ����");
				// ��Ϣ�����
				String xxdlb = "0";// 0:������

				// ��������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ������״̬
				String s_jcfqzt = DADT.substring(idx_dadt, idx_dadt + 2);
				idx_dadt += 2;
				// ��������״̬��8λ�����ƣ�
				String s_jcfqzt_byte = "";
				
				if (!s_jcfqzt.equals("EE")) {
					// �洢��⸡��״̬������
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
					values[2] = "��Ч";
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
        	    // ��������汾��
        	 	String d_rjbbh = String.valueOf(cldMap.get("rjbbh"));
				
				// F25:��ǰ���༰����/�޹����ʡ����������������ѹ����������������Ĳ�ѯ����
				cat.info("[Decode_0C]F25:��ǰ���༰����/�޹����ʡ����������������ѹ����������������Ĳ�ѯ����");
				// ��Ϣ�����
				String xxdlb = "0";// 1:������

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// int i_sw = 0;//��ǰһλ��
				// String sw = "";

				// ��ǰ���й�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰA���й�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰB���й�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰC���й�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰ���޹�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰA���޹�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰB���޹�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰC���޹�����
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰ�ܹ�������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰA��������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰB��������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰC��������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰA���ѹ
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰB���ѹ
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰC���ѹ
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰA�����
				String s_dqaxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqaxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29��v2.0.8���ն˵�������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰB�����
				String s_dqbxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqbxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29��v2.0.8���ն˵�������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰC�����
				String s_dqcxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqcxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29��v2.0.8���ն˵�������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰ�������
				String s_dqlxdl = DADT.substring(idx_dadt, idx_dadt + 4);
				idx_dadt += 4;
				if (!s_dqlxdl.equalsIgnoreCase("EEEE")) {
					//2016-10-29��v2.0.8���ն˵�������
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")){
						s_dqlxdl = Util.tranFormat06(s_dqlxdl);
					//2016-10-29��v2.0.9���ն˵�������
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰ�����ڹ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰA�����ڹ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰB�����ڹ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// ��ǰC�����ڹ���
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

			} else if (s_Fdt.equals("F129") || s_Fdt.equals("F131")) {
				String sjxdm = "";
				if (s_Fdt.equals("F129")) {
					// F129:��ǰ�����й�����ʾֵ���ܡ�����1~M��
					cat.info("[Decode_0C]F129:��ǰ�����й�����ʾֵ(�ܡ�����1~M)");
					sjxdm = "zxygzdnsz";
				} else if (s_Fdt.equals("F131")) {
					// F131:��ǰ�����й�����ʾֵ���ܡ�����1~M��
					cat.info("[Decode_0C]F131:��ǰ�����й�����ʾֵ(�ܡ�����1~M)");
					sjxdm = "fxygzdnsz";
				}
				// ��Ϣ�����
				String xxdlb = "0";// 1:������

				// �ն˳���ʱ��
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
					values[2] = "��Ч";
					values[3] = nowTime;
					values[4] = xxdlb;
					values[5] = s_da;
					values[6] = "0";
					vt.add(values);
				}

				// �����й��ܵ���ʾֵ(����1-����n)
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
						values[2] = "��Ч";
						values[3] = nowTime;
						values[4] = xxdlb;
						values[5] = s_da;
						values[6] = "0";
						vt.add(values);
					}
				}

			}
			// ���˴�PnFn���ݷ���Map
			hm.put(s_PF, vt);

		}

		return hm;
	}

	public void setOracleLobHandler(OracleLobHandler oracleLobHandler) {
		
	}

}