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
 * Description: AFN=10(������м����ݵ���Ӧ�����������ش���) <p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-4-2
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class Decode_10 {
	//������־
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
			//ʱ���ǩ(6�ֽ�) and �¼�������(2�ֽ�)
			DADT = s_csdata.substring(16, s_csdata.length() - 16);
		} else if (s_tpv.equals("1") && s_acd.equals("0")) {
			//ʱ���ǩ(6�ֽ�)
			DADT = s_csdata.substring(16, s_csdata.length() - 12);
		} else if (s_tpv.equals("0") && s_acd.equals("1")) {
			//�¼�������(2�ֽ�)
			DADT = s_csdata.substring(16, s_csdata.length() - 4);
		} else if (s_tpv.equals("0") && s_acd.equals("0")) {
			//�޸�����Ϣ
			DADT = s_csdata.substring(16);
		}
		String[] params = null;
		//һ��ȡ�����巢����ϸ����
		s_sql = "select sjzfsmxseq,sjdybsdm from sjzfssjdybszb "
				+ " where sjzfsseq=?";
	    params = new String[]{s_sjzfsseq};
		List lstXl = jdbcT.queryForList(s_sql,params);

		//����д���ٲ����ݷ��ر�
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
			//��ȷ����
			Set set = hm.keySet();
			Object[] obj = set.toArray();
			for (int i = 0; i < obj.length; i++) {
				String key = obj[i].toString();//PnFn
				String[][] value = (String[][]) hm.get(key);

				String sjzfsmxseq = "";//�����巢����ϸ����
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

				//���ô洢����д���ٲ����ݷ��ر�
				Vector sp_param = new Vector();
				String sp_name = "sp_savebeckondata";
				String array = "";
				//1������������
				sp_param.addElement(s_xzqxm);

				//2���ն˵�ַ
				sp_param.addElement(s_zddz);

				//3�������巢����ϸ����
				sp_param.addElement(sjzfsmxseq);
				cat.info("[Decode_0C]PnFn:" + key);
				cat.info("[Decode_0C]sjzfsmxseq:" + sjzfsmxseq);
				for (int j = 0; j < value.length; j++) {
					String xh = value[j][0];//���
					String sjxdm = value[j][1];//���������
					String sjz = value[j][2];//����ֵ
					String xxdh = value[j][3];//��Ϣ���
					String sjsj = Util.getNowTime().substring(0, 10);//����ʱ��YYMMDDhhmm
					String sjjg = "0";//ʱ����

					array += xh + "|" + xxdh + "|" + sjxdm + "|" + sjz + "|"
							+ sjsj + "|" + sjjg + "#";

				}

				//4�������ļ�¼
				sp_param.addElement(array);
				cat.info("[Decode_0C]array:" + array);

				//5�����ô洢����
				Util.executeProcedure(jdbcT, sp_name, sp_param,
						2);
			}
		}

		//�����޸ġ����������ͱ���״̬��־
		s_sql = "update sjzfsb set zt=?,fhsj=sysdate,sxsjz=? where sjzfsseq=?";
	    params = new String[]{zt,sSJZ,s_sjzfsseq};
        jdbcT.update(s_sql,params); 
	}

	private static HashMap decode(String DADT,String dbgylxdm) throws Exception {
		HashMap hm = new HashMap();

		int idx_dadt = 0;
		String s_dadt = "";
		String s_da = "";//��Ϣ��Pn
		String s_dt = "";//��Ϣ��Fn
		String s_PF = "";//PnFn

		String[][] values = null;//new String[i][3]:
		//��š���������롢����ֵ

		s_dadt = DADT.substring(idx_dadt, idx_dadt + 8);
		idx_dadt += 8;

		//��Ϣ��Pn
		s_da = s_dadt.substring(0, 4);
		s_da = Util.tranDA(Util.convertStr(s_da));
		String s_Pda = "P" + s_da;
		//��Ϣ��Fn
		s_dt = s_dadt.substring(4, 8);
		s_dt = Util.tranDT(Util.convertStr(s_dt));
		String s_Fdt = "F" + s_dt;
		//PnFn
		s_PF = s_Pda + s_Fdt;

		int i_xh = 1;//���
		int i_idx = 0;//values�����±�

		if (s_Fdt.equals("F1")) {
			//F1:ת������ķ���
			cat.info("[Decode_10]F1:ת������ķ���");
			//ת�����ݳ���
			String len = DADT.substring(idx_dadt, idx_dadt + 2);
			idx_dadt += 2;
			int iLen = Integer.parseInt(len,16);
			
			//ת������
			String zfsj = DADT.substring(idx_dadt, idx_dadt + iLen*2);
			idx_dadt += iLen*2;
			String[][] data = null;
			if(dbgylxdm.equalsIgnoreCase("01")){
				//DLT-645����Լ
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

		//���˴�PnFn���ݷ���Map
		hm.put(s_PF, values);
		
		return hm;
	}

}