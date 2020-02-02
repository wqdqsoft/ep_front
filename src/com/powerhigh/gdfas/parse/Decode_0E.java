package com.powerhigh.gdfas.parse;


import java.util.*;

import org.apache.log4j.*;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.module.Dispatch;
import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.*;

/**
 * Description: �¼�������(�¼���ѯ���ء��¼������ϱ�)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * ��дʱ��: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 �޸��ˣ� �޸�ʱ�䣺
 */

public class Decode_0E {
	// ������־
	@SuppressWarnings("unused")
	private static final String resource = "log4j.properties";
	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.parse.Decode_0E.class);

	// static {
	// PropertyConfigurator.configure(resource);
	// }
	public Decode_0E() {

	}

	private static String[] tranCld(String str) throws Exception {
		String[] ss = new String[2];

		str = Util.convertStr(str);
		str = Util.hexStrToBinStr(str, 2);

		// ��/ֹ��־
		String qzbz = str.substring(0, 1);
		if (qzbz.equals("0")) {
			qzbz = "�ָ�";
		} else {
			qzbz = "����";
		}
		ss[0] = qzbz;
		ss[1] = Util.hexStrToDecStr(str.substring(4));

		return ss;
	}

	/**
	 * �����������¼�����
	 * 
	 * @param con
	 *            Connection ���ݿ�����
	 * @param sjsxlx
	 *            String �¼��������ͣ�1����ѯ���أ�2�������ϱ�   0:����¼�¼�
	 * @param s_sjzfsseq
	 *            String ����֡��������
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param s_csdata
	 *            String ������������CS������
	 * 
	 * @return void
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static void dispose(String sjsxlx, String s_sjzfsseq, String xzqxm,
			String zddz, String s_csdata, JdbcTemplate jdbcT) throws Exception {
		String s_sql = "";
		String[] params = null;
		try {
			// ȡ"������������ϸ����"
			s_sql = "select sjzfsmxseq from g_sjzfssjdybszb "
					+ "where sjzfsseq=?";
			params = new String[] { s_sjzfsseq };
			List lstFsmxxl = (List) jdbcT.queryForList(s_sql, params);

			String fsmxxl = "";
			if (sjsxlx.equals("1") && lstFsmxxl.size() == 0) {
				return;

			} else if (sjsxlx.equals("1") && lstFsmxxl.size() != 0) {
				fsmxxl = ((Map) lstFsmxxl.get(0)).get("sjzfsmxseq").toString();
			}

			String mian_dadt = s_csdata.substring(16, 24);
			// ��Ϣ��Pn
			String main_da = mian_dadt.substring(0, 4);
			main_da = Util.tranDA(Util.convertStr(main_da));
			// ��Ϣ��Fn
			String main_dt = mian_dadt.substring(4, 8);
			main_dt = Util.tranDT(Util.convertStr(main_dt));

			// �¼����ͣ�1����Ҫ�¼���2��һ���¼�
			String sjlx = main_dt;

			String pnfn = "P" + main_da + "F" + sjlx;

			int index = 24;
			int xh = 0;

			// ��ǰ��Ҫ�¼�������
			String zysjjsq = s_csdata.substring(index, index + 2);
			index += 2;
			zysjjsq = Util.hexStrToDecStr(zysjjsq);
			cat.info("zysjjsq:" + zysjjsq);
			if (sjsxlx.equals("1")) {
				// �¼���ѯ����
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "zdzysjjsq",
						zysjjsq, "�ն�", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// ��ǰһ���¼�������
			String ybsjjsq = s_csdata.substring(index, index + 2);
			index += 2;
			ybsjjsq = Util.hexStrToDecStr(ybsjjsq);
			cat.info("ybsjjsq:" + ybsjjsq);

			if (sjsxlx.equals("1")) {
				// �¼���ѯ����
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "zdybsjjsq",
						ybsjjsq, "�ն�", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// �¼���¼��ʼָ��
			String sjjlqszz = s_csdata.substring(index, index + 2);
			index += 2;
			int i_begin = Integer.parseInt(sjjlqszz, 16);
			cat.info("i_begin:" + i_begin);
			if (sjsxlx.equals("1")) {
				// �¼���ѯ����
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "sjjlqszz",
						String.valueOf(i_begin), "�ն�", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// �¼���¼����ָ��
			String sjjljszz = s_csdata.substring(index, index + 2);
			index += 2;
			int i_end = Integer.parseInt(sjjljszz, 16);
			cat.info("i_end:" + i_end);

			if (sjsxlx.equals("1")) {
				// �¼���ѯ����
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "sjjljszz",
						String.valueOf(i_end), "�ն�", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// �¼�����
			int sjNum = 0;
			if (i_end >= i_begin) {
				sjNum = (i_end - i_begin)+1;
			} else if (i_end < i_begin) {
				sjNum = 256 + i_end - i_begin;
			}
			if (i_end == 0) {
				sjNum = 0;
			}
			cat.info("sjNum:" + sjNum);
			for (int i = 0; i < sjNum; i++) {
				// <--------------ÿ���¼���--------------->
				// �¼�����ERC
				String sjdm = s_csdata.substring(index, index + 2);
				// 2012-07-25�޸ģ��������¼�����EE���ж�
				if ("EE".equalsIgnoreCase(sjdm)) {
					continue;
				}

				index += 2;
				sjdm = String.valueOf(Integer.parseInt(sjdm, 16));

				// �¼���¼����
				String sjjlcd = s_csdata.substring(index, index + 2);
				index += 2;
				int i_sjjlcd = Integer.parseInt(sjjlcd, 16);

				if (sjdm.equals("0") || i_sjjlcd == 0) {
					continue;
				}
				// �¼�����
				String sjContent = s_csdata.substring(index, index + 2
						* i_sjjlcd);
				index += 2 * i_sjjlcd;

				// �¼�����ʱ��(yymmddhhmm)
				String sjfssj = "";
				// ��Ϣ�����(0���նˣ�1�������㣻2���ܼ��飻3��ֱ��ģ����)
				String xxdlb = "";
				// ��Ϣ���
				String xxdh = "";
				// �¼�ժҪ
				String sjzy = "";

				if (sjdm.equals("1")) {
					// ERC1:���ݳ�ʼ���Ͱ汾�����¼

					// ��ʼ��/�汾����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// �¼���־
					String sjbz = sjContent.substring(10, 12);
					sjbz = Util.hexStrToBinStr(sjbz, 1);

					// �汾�����־
					String bbbgbz = sjbz.substring(6, 7);

					// ��ʼ����־
					String cshbz = sjbz.substring(7, 8);

					// ���ǰ����汾��(ASCII)
					String temp_bgqrjbbh = sjContent.substring(12, 20);
					temp_bgqrjbbh = Util.convertStr(temp_bgqrjbbh);
					String bgqrjbbh = Util.getASCII(temp_bgqrjbbh);

					// ���������汾��(ASCII)
					String temp_bghrjbbh = sjContent.substring(20, 28);
					temp_bghrjbbh = Util.convertStr(temp_bghrjbbh);
					String bghrjbbh = Util.getASCII(temp_bghrjbbh);

					// �¼�ժҪ
					sjzy = "�汾�����־:" + bbbgbz + ";  ��ʼ����־:" + cshbz
							+ ";  ���ǰ����汾��:" + bgqrjbbh + ";  ���������汾��:"
							+ bghrjbbh;

				} else if (sjdm.equals("2")) {
					// ERC2:������ʧ��¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// �¼���־
					String sjbz = sjContent.substring(10, 14);
					sjbz = Util.hexStrToBinStr(sjbz, 2);

					// �ն˲�����ʧ��־
					String zdcsdsbz = sjbz.substring(15, 16);

					// �����������ʧ��־
					String cldcsdsbz = sjbz.substring(14, 15);

					// �¼�ժҪ
					sjzy = "�ն˲�����ʧ��־:" + zdcsdsbz + ";  �����������ʧ��־:"
							+ cldcsdsbz;

				} else if (sjdm.equals("3")) {
					// ERC3:���������¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// ����վ��ַ
					String qdzdz = sjContent.substring(10, 12);
					qdzdz = String.valueOf(Integer.parseInt(qdzdz, 16));

					sjContent = sjContent.substring(12);
					int num = sjContent.length();

					String s_dadt = "";
					for (int j = 0; j < num / 8; j++) {
						// ����������ݵ�Ԫ��ʶn
						String temp_dadt = sjContent.substring(j * 8,
								(j + 1) * 8);

						// ��Ϣ��Pn
						String s_da = temp_dadt.substring(0, 4);
						s_da = Util.tranDA(Util.convertStr(s_da));
						s_da = "P" + s_da;

						// ��Ϣ��Fn
						String s_dt = temp_dadt.substring(4, 8);
						s_dt = Util.tranDT(Util.convertStr(s_dt));
						s_dt = "F" + s_dt;

						s_dadt = s_dadt + s_da + s_dt + ";";
					}

					// �¼�ժҪ
					sjzy = "����վ��ַ:" + qdzdz + ";  ��������������ݵ�Ԫ��ʶ:" + s_dadt;

				} else if (sjdm.equals("4")) {
					// ERC4:�����쳣
					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";
					 
					//��ǰ����
					String dqll=sjContent.substring(10, 14);
					if("EEEE".equalsIgnoreCase(dqll)){
						sjzy="������ͨ���쳣";
					}else{
						dqll = Util.tranFormat06(dqll);
						sjzy="�����쳣,��ǰ������"+dqll+"M3/H";
					}

				} else if (sjdm.equals("5")) {
					// ERC5:ˮλ����
					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";
					 
					//��ǰˮλ
					String dqsw=sjContent.substring(10, 14);
					if("EEEE".equalsIgnoreCase(dqsw)){
						sjzy="ˮλ������ͨ���쳣;";
					}else{
						dqsw = Util.tranFormat06(dqsw);
						sjzy="ˮλ����,��ǰˮλ��"+dqsw+"M;";
					}
					
					//��ǰ����״̬
					String dqfqzt=sjContent.substring(14, 16);
					dqfqzt = Util.hexStrToDecStr(dqfqzt);
					sjzy=sjzy+"ˮλ����,��ǰ����״̬��"+dqfqzt+"��";
					
					
					if("3".equalsIgnoreCase(dqfqzt)){
						//20160222ȡ�ø��ǻ��ն��������������ն�
						List znzds=EPService.getZnzd(xzqxm, zddz, jdbcT);
						if(null!=znzds&&znzds.size()>0){
							for(int ii=0;ii<znzds.size();ii++){
								Map zd=(Map)znzds.get(ii);
								List clds=EPService.getZnzdDjcld(String.valueOf(zd.get("zdid")), jdbcT);
								if(null!=clds&&clds.size()>0){
									//20160222�ر������ն���������Ϊ����Ĳ�����
									for(int jj=0;jj<clds.size();jj++){
										Map cld=(Map)clds.get(jj);
										operation.sendAFN05F1("3", String.valueOf(zd.get("xzqxm")), String.valueOf(zd.get("zddz")), String.valueOf(cld.get("cldh")), "CC");
										//20160222���1��
										Thread.sleep(1000L);
									}
									
								}
								
							}
						}
					}else{
						//20160223������ڳ�û�г�������ˮλ�������������ն��Զ�����
		      			List znzds=EPService.getZnzd(xzqxm, zddz, jdbcT);
		      				if(null!=znzds&&znzds.size()>0){
								for(int ii=0;ii<znzds.size();ii++){
									Map zd=(Map)znzds.get(ii);
									operation.sendAFN04F5("3", String.valueOf(zd.get("xzqxm")), String.valueOf(zd.get("zddz")),  "1;55;55;1;1");
									Thread.sleep(1000L);
								}
		      				
		      			}
					}
					
					
					

				} else if (sjdm.equals("6")) {
					// ERC6:������բ��¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������ܼ��飩
					xxdlb = "2";
					// ��Ϣ���
					xxdh = "1";

					// �ܼ����
					String zjzh = sjContent.substring(10, 12);
					zjzh = Util.hexStrToBinStr(zjzh, 1);
					int i_zjzh = Integer.parseInt(zjzh.substring(2, 8), 2);
					xxdh = String.valueOf(i_zjzh);

					// ��բ�ִ�
					String tzlc = sjContent.substring(12, 14);
					tzlc = Util.hexStrToBinStr(tzlc, 1);

					// �������
					String gklb = sjContent.substring(14, 16);
					gklb = Util.hexStrToBinStr(gklb, 1);
					gklb = gklb.substring(4, 8);// ��ǰ�����¸��ء�Ӫҵ��ͣ�ء����ݿء�ʱ�ο�
					if (gklb.equals("1000")) {
						gklb = "��ǰ�����¸���";
					} else if (gklb.equals("0100")) {
						gklb = "Ӫҵ��ͣ��";
					} else if (gklb.equals("0010")) {
						gklb = "���ݿ�";
					} else if (gklb.equals("0001")) {
						gklb = "ʱ�ο�";
					}

					// ��բǰ����(�ܼӹ���)
					String tzqgl = sjContent.substring(16, 20);
					String s_tzqgl = Util.tranFormat02(tzqgl);

					// ��բ��2���ӵĹ���(�ܼӹ���)
					String tzhgl = sjContent.substring(20, 24);
					String s_tzhgl = Util.tranFormat02(tzhgl);

					// ��բʱ���ʶ�ֵ
					String tzsgldz = sjContent.substring(24, 28);
					String s_tzsgldz = Util.tranFormat02(tzsgldz);

					// �¼�ժҪ
					sjzy = "��բ�ִ�:" + tzlc + ";  �������:" + gklb
							+ ";  ��բǰ����(�ܼӹ���):" + s_tzqgl
							+ ";  ��բ��2���ӵĹ���(�ܼӹ���):" + s_tzhgl
							+ ";  ��բʱ���ʶ�ֵ:" + s_tzsgldz;
					// Dispatch dispatch =
					// (Dispatch)Context.ctx.getBean("dispatchService");
					// //ȡ���û����ƺ��û��ֻ�����
					// String[] hm_yhsjhm=Util.getHmAndYhsjhm(xzqxm, zddz,
					// jdbcT);
					// //���Ͷ���
					// dispatch.sedAscendSms(hm_yhsjhm[1],
					// "�𾴵��û�"+hm_yhsjhm[0]+":��û���ڹ涨ʱ���ڽ��͸���(��ǰ"+s_tzqgl+"kW),��ʱ�εĹ��ʶ�ֵ"+s_tzsgldz+"kW,��ִ����բ����!",
					// true);

				} else if (sjdm.equals("7")) {
					// ERC7:ORP��ֵ�쳣
					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";
					 
					//��ǰORP
					String dqorp=sjContent.substring(10, 14);
					if("EEEE".equalsIgnoreCase(dqorp)){
						sjzy="ORP������ͨ���쳣";
					}else{
						dqorp = Util.tranFormat28(dqorp)[0];
						sjzy="ORP��ֵ�쳣,��ǰORP��"+dqorp+"MV";
					}
				} else if (sjdm.equals("8")) {
					// ERC8:����쳣�¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
      				String cldh = sjContent.substring(10,14);
      				cldh = Util.hexStrToDecStr(Util.convertStr(cldh));
      				xxdh = String.valueOf(Integer.parseInt(cldh));

					// �쳣����
					String yclx = sjContent.substring(14, 16);
					if("01".equalsIgnoreCase(yclx)){
						sjzy="�쳣����:����"+";�������:"+cldh;
					}else if("02".equalsIgnoreCase(yclx)){
						sjzy="�쳣����:����"+";�������:"+cldh;
					}else if("03".equalsIgnoreCase(yclx)){
						sjzy="�쳣����:���಻ƽ��"+";�������:"+cldh;
					}else if("04".equalsIgnoreCase(yclx)){
						sjzy="�쳣����:Ƿ��"+";�������:"+cldh;
					}else if("05".equalsIgnoreCase(yclx)){
						sjzy="�쳣����:�ӵ�/©��"+";�������:"+cldh;
					}else if("06".equalsIgnoreCase(yclx)){
						sjzy="�쳣����:��ת"+";�������:"+cldh;
					}else if("07".equalsIgnoreCase(yclx)){
						sjzy="�쳣����:��������ͨѶ�쳣"+";�������:"+cldh;
					}

				} else if (sjdm.equals("9")) {
					// ERC8:����ͷ�쳣�¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// �����־
					String yclx = sjContent.substring(10, 12);
					if("01".equalsIgnoreCase(yclx)){
						sjzy="����ͷ�쳣�¼���¼:����ͷ����Ӧ";
					}else if("02".equalsIgnoreCase(yclx)){
						sjzy="����ͷ�쳣�¼���¼:CDMAͼ���Ĵ����ж�";
					}else if("03".equalsIgnoreCase(yclx)){
						sjzy="����ͷ�쳣�¼���¼:CRCУ�����";
					}else if("04".equalsIgnoreCase(yclx)){
						sjzy="����ͷ�쳣�¼���¼:485ͨѶ��ʱ";
					}
					

				} else if (sjdm.equals("10")) {
					// ERC10:�Ž��¼��ϱ�
					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";
					 
					//��ǰ�ſ���״̬
					String mkgzt=sjContent.substring(10, 12);
					int i_mkgzt=0;
	      			i_mkgzt = Integer.parseInt(mkgzt,16);
					if("01".equalsIgnoreCase(mkgzt)){
						sjzy="��ǰ�ſ���״̬:����;";
					}
					if("00".equalsIgnoreCase(mkgzt)){
						sjzy="��ǰ�ſ���״̬:����;";
					}
					
					//ID������
					String idkh=sjContent.substring(14, 28);
					idkh=Util.convertStr(idkh);
					idkh=Util.hexStrToDecStr(idkh);
//					sjzy=sjzy+"ID����:"+idkh;
					
					//���źϷ��Ա�־
	      			String s_kmhfxbz = sjContent.substring(12, 14);
	      			int i_kmhfxbz=0;
	      			i_kmhfxbz = Integer.parseInt(s_kmhfxbz,16);
	      			if(1==i_kmhfxbz){
	      				//����ʱ������Ϊ0������¼���ն��¼���¼����
	      				sjsxlx="0";
	      				sjzy=sjzy+"ID����:"+idkh+"����Ѳ��;";
	      				//���Ϊ����Ѳ��,�����ն��Ž���¼��
	      				String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
      	      	    	s_sql="insert into M_TERMINAL_ACCESS_RECORD(id,TERMINALID,datatime,ACCESSSTATUS,doorstatus,idcard,status) values(S_ACCESS_RECORD.nextval,?,to_date(?,'yymmddhh24miss'),?,?,?,0)";
      	        		params = new String[] { zdid,sjfssj,String.valueOf(i_kmhfxbz),String.valueOf(i_mkgzt),idkh};
      	        		jdbcT.update(s_sql, params);
	      	        	
	      			}
	      			if(0==i_kmhfxbz){
	      				sjzy=sjzy+"�Ƿ�����;";
	      			}
					
				} else if (sjdm.equals("11")) {
					// ERC11:�����쳣

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ��/ֹ��־
					String qzbz = ss[1];

					// ��λ����
					// Ua/Uab
					String Ua = sjContent.substring(14, 18);
					Ua = Util.tranFormat05(Ua);

					// Ub
					String Ub = sjContent.substring(18, 22);
					Ub = Util.tranFormat05(Ub);

					// Uc/Ucb
					String Uc = sjContent.substring(22, 26);
					Uc = Util.tranFormat05(Uc);

					// Ia
					String Ia = sjContent.substring(26, 30);
					Ia = Util.tranFormat05(Ia);

					// Ib
					String Ib = sjContent.substring(30, 34);
					Ib = Util.tranFormat05(Ib);

					// Ic
					String Ic = sjContent.substring(34, 38);
					Ic = Util.tranFormat05(Ic);

					// ����ʱ���ܱ������й��ܵ���ʾֵ
					String zxygzdnsz = sjContent.substring(38, 48);
					zxygzdnsz = Util.tranFormat14(zxygzdnsz);

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + qzbz + ";" + "  Ua/Uab=" + Ua + "(��);"
							+ "  Ub=" + Ub + "(��);" + "  Uc/Ucb=" + Uc
							+ "(��);" + "  Ia=" + Ia + "(��);" + "  Ib=" + Ib
							+ "(��);" + "  Ib=" + Ib + "(��);"
							+ "  �����й��ܵ���ʾֵ=" + zxygzdnsz + ";";

				} else if (sjdm.equals("12")) {
					// ERC12:���ܱ�ʱ�䳬��

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ��/ֹ��־
					String qzbz = ss[1];

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + qzbz + ";";

				} else if (sjdm.equals("13")) {
					// ERC13:�ն�©���������¼���¼
					String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
					s_sql="select rjbbh from g_zdgz where zdid=?";
	        		params = new String[] { zdid };
	        	    List cldList = jdbcT.queryForList(s_sql, params);
	        	    Map cldMap = (Map) cldList.get(0);
	        	    // ��������汾��
	        	 	String d_rjbbh = String.valueOf(cldMap.get("rjbbh"));

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";
                    
					//����©����
					String cxldl=sjContent.substring(10, 14);
					//2016-10-29��v2.0.8���ն˵�������
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						cxldl = Util.tranFormat05(cxldl);
					}else{
						cxldl = Util.tranFormat08(cxldl);
					}
//					String cxldl=sjContent.substring(10, 14);
//					cxldl=Util.tranFormat08(cxldl);
					
					// �¼�ժҪ
					sjzy = "����©����ֵ:" + cxldl ;

				} else if (sjdm.equals("14")) {
					// ERC14:�ն�ͣ���¼�

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// ͣ�緢��ʱ��
					String tdsj = Util.tranFormat15(sjContent.substring(0, 10));
					
					// �¼�ժҪ
					sjzy = "ͣ�緢��ʱ��:" + tdsj ;

				} else if (sjdm.equals("15")) {
					// ERC15:�͵ؿ����ϱ�
					// ����ʱ��
					sjfssj = sjContent.substring(2, 12);
					sjfssj = Util.convertStr(sjfssj);

					// �豸����
      				String sblx = sjContent.substring(0,2);
      				
      				sjzy="�ֳ�վ�㴦�ھ͵ؿ���ģʽ";
					

				} else if (sjdm.equals("16")) {
					//ERC16�����ϵƱ����ϱ�

					// ����ʱ��
					sjfssj = sjContent.substring(4, 14);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
      				String cldh = sjContent.substring(0,4);
      				cldh = Util.hexStrToDecStr(Util.convertStr(cldh));
      				xxdh = String.valueOf(Integer.parseInt(cldh));

					sjzy="����쳣�¼���¼:�������,���������㣺"+cldh;
					
					//���µ����״̬
					String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
  	      	    	s_sql="update g_zddqsbpzb set zt=? where zdid=? and cldh=?";
  	        		params = new String[] { "2",zdid,cldh};
  	        		jdbcT.update(s_sql, params);
					

				} else if (sjdm.equals("17")) {
					// ERC17���ն��ϵ��¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// ͣ�緢��ʱ��
					String tdsj = Util.tranFormat15(sjContent.substring(0, 10));
					
					// �¼�ժҪ
					sjzy = "�ϵ緢��ʱ��:" + tdsj ;

				} else if (sjdm.equals("18")) {
					// ERC18:������Ͷ��������¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ��/ֹ��־
					String qzbz = ss[1];

					// �쳣��־
					String ycbz = sjContent.substring(14, 16);
					ycbz = Util.hexStrToBinStr(ycbz, 1);
					ycbz = ycbz.substring(5, 8);

					if (ycbz.equals("001")) {
						ycbz = "��ѹ";
					} else if (ycbz.equals("010")) {
						ycbz = "װ�ù���";
					} else if (ycbz.equals("100")) {
						ycbz = "ִ�л�·����";
					}

					// ���������־
					String temp_drqzbz = sjContent.substring(16, 20);
					temp_drqzbz = Util.convertStr(temp_drqzbz);
					temp_drqzbz = Util.hexStrToBinStr(temp_drqzbz, 2);
					temp_drqzbz = temp_drqzbz.substring(7, 16);// 9-1
					String drqzbz = "";
					for (int j = 1; j <= 9; j++) {
						String sfzs = drqzbz.substring(9 - j, 9 - (j - 1));
						sfzs = sfzs.equals("1") ? "����" : "δ����";
						drqzbz = drqzbz + "��������" + j + ":" + sfzs + ";";
					}

					// Խ�޷���ʱ��������
					String glys = sjContent.substring(20, 24);
					glys = Util.tranFormat05(glys);

					// Խ�޷���ʱ�޹�����
					String wggl = sjContent.substring(24, 28);
					wggl = Util.tranFormat23(wggl);

					// Խ�޷���ʱ��ѹ
					String dy = sjContent.substring(28, 32);
					dy = Util.tranFormat07(dy);

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + qzbz + ";" + "  �쳣��־:" + ycbz + ";"
							+ "  " + drqzbz + "  Խ�޷���ʱ��������:" + glys + ";"
							+ "  Խ�޷���ʱ�޹�����:" + wggl + ";" + "  Խ�޷���ʱ��ѹ:"
							+ dy + ";";

				} else if (sjdm.equals("19")) {
					// ERC19:����������ü�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// ���絥��
					String gddh = sjContent.substring(10, 18);
					gddh = Util.convertStr(gddh);
					gddh = String.valueOf(Integer.parseInt(gddh, 16));

					// ׷��/ˢ�±�־
					String zjsxbz = sjContent.substring(18, 20);
					if (zjsxbz.equals("55")) {
						zjsxbz = "׷��";
					} else if (zjsxbz.equals("AA")) {
						zjsxbz = "ˢ��";
					}

					// ������ֵ
					String gdlz = sjContent.substring(20, 28);
					String[] ss_gdlz = Util.tranFormat03(gdlz);
					gdlz = ss_gdlz[0] + ss_gdlz[1];

					// ��������
					String bjmx = sjContent.substring(28, 36);
					String[] ss_bjmx = Util.tranFormat03(bjmx);
					bjmx = ss_bjmx[0] + ss_bjmx[1];

					// ��բ����
					String tzmx = sjContent.substring(36, 44);
					String[] ss_tzmx = Util.tranFormat03(tzmx);
					tzmx = ss_tzmx[0] + ss_tzmx[1];

					// ���ι���ǰʣ�������(��)
					String gdq = sjContent.substring(44, 52);
					String[] ss_gdq = Util.tranFormat03(gdq);
					gdq = ss_gdq[0] + ss_gdq[1];

					// ���ι����ʣ�������(��)
					String gdh = sjContent.substring(52, 60);
					String[] ss_gdh = Util.tranFormat03(gdh);
					gdh = ss_gdh[0] + ss_gdh[1];

					// �¼�ժҪ
					sjzy = "���絥��:" + gddh + ";" + "  ׷��/ˢ�±�־:" + zjsxbz + ";"
							+ "  ������ֵ:" + gdlz + ";" + "  ��������:" + bjmx
							+ ";" + "  ��բ����:" + tzmx + ";"
							+ "  ���ι���ǰʣ�������(��):" + gdq + ";"
							+ "  ���ι����ʣ�������(��):" + gdh + ";";

				} else if (sjdm.equals("20")) {
					// ERC20:��������¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// ��������
					String cwmm = sjContent.substring(10, 14);
					cwmm = Util.convertStr(cwmm);
					cwmm = String.valueOf(Integer.parseInt(cwmm, 16));

					// ����վ��ַ
					String qdzdz = sjContent.substring(14, 16);
					qdzdz = String.valueOf(Integer.parseInt(qdzdz, 16));

					// �¼�ժҪ
					sjzy = "��������:" + cwmm + ";" + "  ����վ��ַ:" + qdzdz + ";";

				} else if (sjdm.equals("21")) {
					// ERC21:�ն˹��ϼ�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// �ն˹�������
					String gzlx = sjContent.substring(10, 12);
					int i_gzlx = Integer.parseInt(gzlx, 16);

					if (i_gzlx == 1) {
						gzlx = "�ն������ڴ����";
					} else if (i_gzlx == 2) {
						gzlx = "ʱ�ӹ���";
					} else if (i_gzlx == 3) {
						gzlx = "����ͨ�Ź���";
					} else if (i_gzlx == 4) {
						gzlx = "485�������";
						// ���������ն˵�ǰ״̬��¼��
						String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
						String this_sql = "";
						String[] this_params = null;
						if (Util.checkZddqztByZdid(zdid, jdbcT)) {
							this_sql = "update G_ZDDQZTJLB set ZHJSSJ=sysdate,ZD485TXZT=?, ZD485TXSJ_FSSJ=to_date(?,'yymmddhh24mi') where zdid=? ";
							this_params = new String[] { "0", sjfssj, zdid };
						} else {
							this_sql = "insert into G_ZDDQZTJLB (zdid,zhjssj,ZD485TXZT,ZD485TXSJ_FSSJ) values(?,sysdate,?,to_date(?,'yymmddhh24mi'))";
							this_params = new String[] { zdid, "0", sjfssj };
						}
						jdbcT.update(this_sql, this_params);
					} else if (i_gzlx == 5) {
						gzlx = "��ʾ�����";
					} else if (i_gzlx == 7) {
						gzlx = "485��������";
						// ���������ն˵�ǰ״̬��¼��
						String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
						String this_sql = "";
						String[] this_params = null;
						if (Util.checkZddqztByZdid(zdid, jdbcT)) {
							this_sql = "update G_ZDDQZTJLB set ZHJSSJ=sysdate,ZD485TXZT=?, ZD485TXSJ_FSSJ=to_date(?,'yymmddhh24mi') where zdid=? ";
							this_params = new String[] { "1", sjfssj, zdid };
						} else {
							this_sql = "insert into G_ZDDQZTJLB (zdid,zhjssj,ZD485TXZT,ZD485TXSJ_FSSJ) values(?,sysdate,?,to_date(?,'yymmddhh24mi'))";
							this_params = new String[] { zdid, "1", sjfssj };
						}
						jdbcT.update(this_sql, this_params);
					}

					// �¼�ժҪ
					sjzy = "�ն˹�������:" + gzlx + ";";

				} else if (sjdm.equals("22")) {
					// ERC22:�й��ܵ������Խ���¼���¼

					int indx = 0;
					// ����ʱ��
					sjfssj = sjContent.substring(indx, indx + 10);
					indx += 10;
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������ܼ��飩
					xxdlb = "2";
					// ��Ϣ���
					xxdh = "1";

					// �ܼ����
					String temps = sjContent.substring(indx, indx + 2);
					indx += 2;
					temps = Util.hexStrToBinStr(temps, 1);
					int i_zjzh = Integer.parseInt(temps.substring(2, 8), 2);
					xxdh = String.valueOf(i_zjzh);

					// ��/ֹ��־
					String qzbz = temps.substring(0, 1);
					if (qzbz.equals("1")) {
						qzbz = "����";
					} else if (qzbz.equals("0")) {
						qzbz = "�ָ�";
					}

					// Խ��ʱ�Ա��ܼ����й��ܵ�����
					String dbzjzygz = sjContent.substring(indx, indx + 8);
					indx += 8;
					String[] ss_dbzjzygz = Util.tranFormat03(dbzjzygz);
					dbzjzygz = ss_dbzjzygz[0] + ss_dbzjzygz[1];

					// Խ��ʱ�����ܼ����й��ܵ�����
					String czzjzygz = sjContent.substring(indx, indx + 8);
					indx += 8;
					String[] ss_czzjzygz = Util.tranFormat03(czzjzygz);
					czzjzygz = ss_czzjzygz[0] + ss_czzjzygz[1];

					// Խ��ʱ�Խ�����ƫ��ֵ
					String xdpcz = sjContent.substring(indx, indx + 2);
					indx += 2;
					xdpcz = String.valueOf(Integer.parseInt(xdpcz, 16));

					// Խ��ʱ�Խ�޾���ƫ��ֵ
					String jdpcz = sjContent.substring(indx, indx + 8);
					indx += 8;
					String[] ss_jdpcz = Util.tranFormat03(jdpcz);
					jdpcz = ss_jdpcz[0] + ss_jdpcz[1];

					// �Ա��ܼ������������
					String sDbnum = sjContent.substring(indx, indx + 2);
					indx += 2;
					int iDbnum = Integer.parseInt(sDbnum, 16);

					String db_yczy = "";
					for (int j = 1; j <= iDbnum; j++) {
						String ygzdnsz = sjContent.substring(indx, indx + 10);
						indx += 10;
						ygzdnsz = Util.tranFormat14(ygzdnsz);

						db_yczy = db_yczy + "Խ��ʱ�Ա��ܼ����" + j + "���������й��ܵ���ʾֵ:"
								+ ygzdnsz + ";";

					}

					// �����ܼ������������
					String sCznum = sjContent.substring(indx, indx + 2);
					indx += 2;
					int iCznum = Integer.parseInt(sCznum, 16);

					String cz_yczy = "";
					for (int j = 1; j <= iCznum; j++) {
						String ygzdnsz = sjContent.substring(indx, indx + 10);
						indx += 10;
						ygzdnsz = Util.tranFormat14(ygzdnsz);

						cz_yczy = cz_yczy + "Խ��ʱ�����ܼ����" + j + "���������й��ܵ���ʾֵ:"
								+ ygzdnsz + ";";

					}

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + qzbz + ";" + "  Խ��ʱ�Ա��ܼ����й��ܵ�����:"
							+ dbzjzygz + ";" + "  Խ��ʱ�����ܼ����й��ܵ�����:" + czzjzygz
							+ ";" + "  Խ��ʱ�Խ�����ƫ��ֵ:" + xdpcz + ";"
							+ "  Խ��ʱ�Խ�޾���ƫ��ֵ:" + jdpcz + ";" + "  "
							+ db_yczy + "  " + cz_yczy;

				} else if (sjdm.equals("24")) {
					// ERC24:��ѹԽ�޼�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ��/ֹ��־
					String qzbz = ss[1];

					// Խ�ޱ�־
					String temp_yxbz = sjContent.substring(14, 16);
					temp_yxbz = Util.hexStrToBinStr(temp_yxbz, 1);

					// Խ������
					String yxlx = temp_yxbz.substring(0, 2);
					if (yxlx.equals("01")) {
						yxlx = "Խ����";
					} else if (yxlx.equals("10")) {
						yxlx = "Խ����";
					} else {
						yxlx = "����";
					}

					// ��λ
					String temp_xw = temp_yxbz.substring(5, 8);// ��λ
					String xw = "";

					if ((temp_xw.substring(2, 3)).equals("1")) {
						xw = xw + "A�ࡢ";
					}
					if ((temp_xw.substring(1, 2)).equals("1")) {
						xw = xw + "B�ࡢ";
					}
					if ((temp_xw.substring(0, 1)).equals("1")) {
						xw = xw + "C�ࡢ";
					}
					if (xw != null) {
						xw = xw.substring(0, xw.length() - 1);
					}

					// Ua/Uab
					String Ua = sjContent.substring(16, 20);
					Ua = Util.tranFormat07(Ua);

					// Ub
					String Ub = sjContent.substring(20, 24);
					Ub = Util.tranFormat07(Ub);

					// Uc/Ucb
					String Uc = sjContent.substring(24, 28);
					Uc = Util.tranFormat07(Uc);

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + qzbz + ";" + "  Խ������:" + yxlx + ";"
							+ "  Խ����λ:" + xw + ";" + "  Ua/Uab=" + Ua + ";"
							+ "  Ub=" + Ub + ";" + "  Uc/Ucb=" + Uc + ";";

				} else if (sjdm.equals("25")) {
					// ERC25:����Խ�޼�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ��/ֹ��־
					String qzbz = ss[1];

					// Խ�ޱ�־
					String temp_yxbz = sjContent.substring(14, 16);
					temp_yxbz = Util.hexStrToBinStr(temp_yxbz, 1);

					// Խ������
					String yxlx = temp_yxbz.substring(0, 2);
					if (yxlx.equals("01")) {
						yxlx = "Խ����";
					} else if (yxlx.equals("10")) {
						yxlx = "Խ����";
					} else {
						yxlx = "����";
					}

					// ��λ
					String temp_xw = temp_yxbz.substring(5, 8);// ��λ
					String xw = "";

					if ((temp_xw.substring(2, 3)).equals("1")) {
						xw = xw + "A�ࡢ";
					}
					if ((temp_xw.substring(1, 2)).equals("1")) {
						xw = xw + "B�ࡢ";
					}
					if ((temp_xw.substring(0, 1)).equals("1")) {
						xw = xw + "C�ࡢ";
					}
					if (xw != null) {
						xw = xw.substring(0, xw.length() - 1);
					}

					// Ia/Iab
					String Ia = sjContent.substring(16, 20);
					Ia = Util.tranFormat06(Ia);

					// Ib
					String Ib = sjContent.substring(20, 24);
					Ib = Util.tranFormat06(Ib);

					// Ic/Icb
					String Ic = sjContent.substring(24, 28);
					Ic = Util.tranFormat06(Ic);

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + qzbz + ";" + "  Խ������:" + yxlx + ";"
							+ "  Խ����λ:" + xw + ";" + "  Ia/Iab=" + Ia + ";"
							+ "  Ib=" + Ib + ";" + "  Ic/Icb=" + Ic + ";";

				} else if (sjdm.equals("26")) {
					// ERC26:���ڹ���Խ�޼�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ��/ֹ��־
					String qzbz = ss[1];

					// Խ�ޱ�־
					String temp_yxbz = sjContent.substring(14, 16);
					temp_yxbz = Util.hexStrToBinStr(temp_yxbz, 1);

					// Խ������
					String yxlx = temp_yxbz.substring(0, 2);
					if (yxlx.equals("01")) {
						yxlx = "Խ����";
					} else if (yxlx.equals("10")) {
						yxlx = "Խ����";
					} else {
						yxlx = "����";
					}

					// ����ʱ�����ڹ���
					String szgl = sjContent.substring(16, 20);
					szgl = Util.tranFormat23(szgl);

					// ����ʱ�����ڹ�����ֵ
					String szglxz = sjContent.substring(20, 24);
					szglxz = Util.tranFormat23(szglxz);

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + qzbz + ";" + "  Խ������:" + yxlx + ";"
							+ "  ����ʱ�����ڹ���:" + szgl + ";" + "  ����ʱ�����ڹ�����ֵ:"
							+ szglxz + ";";

				} else if (sjdm.equals("27")) {
					// ERC27:���ܱ�ʾ���½���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// �½�ǰ���ܱ������й��ܵ���ʾֵ
					String xjq = sjContent.substring(14, 24);
					xjq = Util.tranFormat14(xjq);

					// �½�����ܱ������й��ܵ���ʾֵ
					String xjh = sjContent.substring(24, 34);
					xjh = Util.tranFormat14(xjh);

					// �¼�ժҪ
					sjzy = "�½�ǰ���ܱ������й��ܵ���ʾֵ:" + xjq + ";"
							+ "  �½�����ܱ������й��ܵ���ʾֵ:" + xjh + ";";

				} else if (sjdm.equals("28")) {
					// ERC28:�����������¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ����ǰ�����й��ܵ���ʾֵ
					String xjq = sjContent.substring(14, 24);
					xjq = Util.tranFormat14(xjq);

					// ����������й��ܵ���ʾֵ
					String xjh = sjContent.substring(24, 34);
					xjh = Util.tranFormat14(xjh);

					// ���������ֵ
					String ccfz = sjContent.substring(34, 36);
					ccfz = String.valueOf(Integer.parseInt(ccfz, 16));

					// �¼�ժҪ
					sjzy = "����ǰ�����й��ܵ���ʾֵ:" + xjq + ";" + "  ����������й��ܵ���ʾֵ:"
							+ xjh + ";" + "  ���������ֵ:" + ccfz + ";";

				} else if (sjdm.equals("29")) {
					// ERC29:���������߼�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ����ǰ�����й��ܵ���ʾֵ
					String fzq = sjContent.substring(14, 24);
					fzq = Util.tranFormat14(fzq);

					// ���ߺ������й��ܵ���ʾֵ
					String fzh = sjContent.substring(24, 34);
					fzh = Util.tranFormat14(fzh);

					// ���ܱ���߷�ֵ
					String fzfz = sjContent.substring(34, 36);
					fzfz = String.valueOf(Integer.parseInt(fzfz, 16));

					// �¼�ժҪ
					sjzy = "����ǰ�����й��ܵ���ʾֵ:" + fzq + ";" + "  ���ߺ������й��ܵ���ʾֵ:"
							+ fzh + ";" + "  ���ܱ���߷�ֵ:" + fzfz + ";";

				} else if (sjdm.equals("30")) {
					// ERC30:���ܱ�ͣ�߼�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// ͣ��ʱ�����й��ܵ���ʾֵ
					String tz = sjContent.substring(14, 24);
					tz = Util.tranFormat14(tz);

					// ���ܱ�ͣ�߷�ֵ
					String tzfz = sjContent.substring(24, 26);
					tzfz = String.valueOf(Integer.parseInt(tzfz, 16));

					// �¼�ժҪ
					sjzy = "ͣ��ʱ�����й��ܵ���ʾֵ:" + tz + ";" + "  ���ܱ�ͣ�߷�ֵ:" + tzfz
							+ ";";

				} else if (sjdm.equals("31")) {
					// ERC31:485����ʧ���¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[1];

					// ���һ�γ���ɹ������й��ܵ���ʾֵ
					String cs1 = sjContent.substring(14, 24);
					cs1 = Util.tranFormat14(cs1);

					// ���һ�γ���ɹ������޹��ܵ���ʾֵ
					String cs2 = sjContent.substring(24, 32);
					cs2 = Util.tranFormat11(cs2);

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + ss[0] + ";" + "  ���һ�γ���ɹ������й��ܵ���ʾֵ:"
							+ cs1 + ";" + "  ���һ�γ���ɹ������޹��ܵ���ʾֵ:" + cs2 + ";";

				} else if (sjdm.equals("32")) {
					// ERC32:�ն�����վͨ�������������¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// �����ѷ�����ͨ������
					String cs1 = sjContent.substring(10, 18);
					try {
						cs1 = Util.hexStrToDecStr(Util.convertStr(cs1));
					} catch (Exception e) {
						cs1 = "��Ч";
					}
					// ��ͨ����������
					String cs2 = sjContent.substring(18, 26);
					try {
						cs2 = Util.hexStrToDecStr(Util.convertStr(cs2));
					} catch (Exception e) {
						cs2 = "��Ч";
					}

					// �¼�ժҪ
					sjzy = "�����ѷ�����ͨ������:" + cs1 + ";" + "  ��ͨ����������:" + cs2
							+ ";";

				} else if (sjdm.equals("33")) {
					// ERC33:���ܱ�����״̬�ֱ�λ�¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[1];

					// �������״̬�ֱ�λ��־1
					String cs1 = sjContent.substring(14, 18);
					cs1 = Util.convertStr(cs1);
					cs1 = Util.hexStrToBinStr(cs1, 2);

					// �������״̬�ֱ�λ��־2
					String cs2 = sjContent.substring(18, 22);
					cs2 = Util.convertStr(cs2);
					cs2 = Util.hexStrToBinStr(cs2, 2);

					// �������״̬�ֱ�λ��־3
					String cs3 = sjContent.substring(22, 26);
					cs3 = Util.convertStr(cs3);
					cs3 = Util.hexStrToBinStr(cs3, 2);

					// �������״̬�ֱ�λ��־4
					String cs4 = sjContent.substring(26, 30);
					cs4 = Util.convertStr(cs4);
					cs4 = Util.hexStrToBinStr(cs4, 2);

					// �������״̬�ֱ�λ��־5
					String cs5 = sjContent.substring(30, 34);
					cs5 = Util.convertStr(cs5);
					cs5 = Util.hexStrToBinStr(cs5, 2);

					// �������״̬�ֱ�λ��־6
					String cs6 = sjContent.substring(34, 38);
					cs6 = Util.convertStr(cs6);
					cs6 = Util.hexStrToBinStr(cs6, 2);

					// �������״̬�ֱ�λ��־7
					String cs7 = sjContent.substring(38, 42);
					cs7 = Util.convertStr(cs7);
					cs7 = Util.hexStrToBinStr(cs7, 2);

					// �������״̬��1
					String cs8 = sjContent.substring(42, 46);
					cs8 = Util.convertStr(cs8);
					cs8 = Util.hexStrToBinStr(cs8, 2);

					// �������״̬��2
					String cs9 = sjContent.substring(42, 46);
					cs9 = Util.convertStr(cs9);
					cs9 = Util.hexStrToBinStr(cs9, 2);

					// �������״̬��3
					String cs10 = sjContent.substring(46, 50);
					cs10 = Util.convertStr(cs10);
					cs10 = Util.hexStrToBinStr(cs10, 2);

					// �������״̬��4
					String cs11 = sjContent.substring(50, 54);
					cs11 = Util.convertStr(cs11);
					cs11 = Util.hexStrToBinStr(cs11, 2);

					// �������״̬��5
					String cs12 = sjContent.substring(54, 58);
					cs12 = Util.convertStr(cs12);
					cs12 = Util.hexStrToBinStr(cs12, 2);

					// �������״̬��6
					String cs13 = sjContent.substring(58, 62);
					cs13 = Util.convertStr(cs13);
					cs13 = Util.hexStrToBinStr(cs13, 2);

					// �������״̬��7
					String cs14 = sjContent.substring(62, 66);
					cs14 = Util.convertStr(cs14);
					cs14 = Util.hexStrToBinStr(cs14, 2);

					// �¼�ժҪ
					sjzy = "�������״̬�ֱ�λ��־1:" + cs1 + ";" + "  �������״̬�ֱ�λ��־2:"
							+ cs2 + ";" + "  �������״̬�ֱ�λ��־3:" + cs3 + ";"
							+ "  �������״̬�ֱ�λ��־4:" + cs4 + ";"
							+ "  �������״̬�ֱ�λ��־5:" + cs5 + ";"
							+ "  �������״̬�ֱ�λ��־6:" + cs6 + ";"
							+ "  �������״̬�ֱ�λ��־7:" + cs7 + ";" + "  �������״̬��1:"
							+ cs8 + ";" + "  �������״̬��2:" + cs9 + ";"
							+ "  �������״̬��3:" + cs10 + ";" + "  �������״̬��4:"
							+ cs11 + ";" + "  �������״̬��5:" + cs12 + ";"
							+ "  �������״̬��6:" + cs13 + ";" + "  �������״̬��7:"
							+ cs14 + ";";

				} else if (sjdm.equals("34")) {
					// ERC34:CT�쳣�¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[1];

					// �쳣��־
					String cs1 = sjContent.substring(14, 16);
					cs1 = Util.hexStrToBinStr(cs1, 1);

					// �¼�ժҪ
					sjzy = "��/ֹ��־:" + ss[0] + ";" + "  �쳣��־:" + cs1 + ";";

				} else if (sjdm.equals("35")) {
					// ERC35:����δ֪����¼���¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ����𣨲����㣩
					xxdlb = "1";
					// ��Ϣ���
					xxdh = "1";

					// �������
					String temps = sjContent.substring(10, 12);
					temps = Util.hexStrToBinStr(temps, 1);
					xxdh = Util.binStrToDecStr(temps.substring(2));

					// ���ֿ���
					String sNum = sjContent.substring(12, 14);
					sNum = Util.hexStrToDecStr(sNum);
					int iNum = Integer.parseInt(sNum);

					// �¼�ժҪ
					sjzy = "���ֿ���:" + sNum + ";";
					int idx = 14;
					for (int m = 1; m <= iNum; m++) {
						// ��m���
						// ͨ�ŵ�ַ
						String cs1 = sjContent.substring(idx, idx + 12);
						idx += 12;
						cs1 = Util.tranFormat12(cs1);
						sjzy += "  ��" + m + "��δ֪���ͨ�ŵ�ַ:" + cs1 + ";";

						// ������𼰷����߽��յ����ź�Ʒ��
						String cs2 = sjContent.substring(idx, idx + 2);
						idx += 2;
						cs2 = Util.hexStrToBinStr(cs2, 1);
						sjzy += "  ��" + m + "��δ֪���������𼰷����߽��յ����ź�Ʒ��:" + cs2
								+ ";";

						// ͨ��Э��
						String cs3 = sjContent.substring(idx, idx + 2);
						idx += 2;
						cs3 = Util.hexStrToBinStr(cs3, 1);
						cs3 = cs3.substring(6, 8);
						if (cs3.equals("00")) {
							cs3 = "DL/T645-1997";
						} else if (cs3.equals("01")) {
							cs3 = "DL/T645-2007";
						} else {
							cs3 = "����Э��";
						}
						sjzy += "  ��" + m + "��δ֪���ͨ��Э��:" + cs3 + ";";

					}

				} else if (sjdm.equals("50")) {
					// ERC50:�Ӵ������ϼ�¼

					// ����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������ܼ��飩
					xxdlb = "2";
					// ��Ϣ���
					xxdh = "1";

					// �ܼ����
					String zjzh = sjContent.substring(10, 12);
					zjzh = Util.hexStrToBinStr(zjzh, 1);
					int i_zjzh = Integer.parseInt(zjzh.substring(2, 8), 2);
					xxdh = String.valueOf(i_zjzh);

					// �Ӵ�������״̬
					String jcqkhzt = sjContent.substring(12, 14);
					// if(!"EE".equalsIgnoreCase(jcqkhzt)){
					// if("55".equalsIgnoreCase(jcqkhzt)){
					// jcqkhzt="��բ";
					// }else{
					// jcqkhzt="��բ";
					// }
					// }else{
					// jcqkhzt="��Ч";
					// }

					// �������
					String gklb = sjContent.substring(14, 16);
					gklb = Util.hexStrToBinStr(gklb, 1);
					gklb = gklb.substring(4, 8);// ��ǰ�����¸��ء�Ӫҵ��ͣ�ء����ݿء�ʱ�ο�
					if (gklb.equals("1000")) {
						gklb = "��ǰ�����¸���";
					} else if (gklb.equals("0100")) {
						gklb = "Ӫҵ��ͣ��";
					} else if (gklb.equals("0010")) {
						gklb = "���ݿ�";
					} else if (gklb.equals("0001")) {
						gklb = "ʱ�ο�";
					}

					// ��բǰ����(�ܼӹ���)
					String tzqgl = sjContent.substring(16, 20);
					String s_tzqgl = Util.tranFormat02(tzqgl);

					// ��բ��2���ӵĹ���(�ܼӹ���)
					String tzhgl = sjContent.substring(20, 24);
					String s_tzhgl = Util.tranFormat02(tzhgl);

					// ��բʱ���ʶ�ֵ
					String tzsgldz = sjContent.substring(24, 28);
					String s_tzsgldz = Util.tranFormat02(tzsgldz);
					// �Ӵ�������״̬
					String tem_jcqkhzt = jcqkhzt;
					if (!"EE".equalsIgnoreCase(jcqkhzt)) {

						if ("55".equalsIgnoreCase(jcqkhzt)) {
							jcqkhzt = "��բ";
						} else {
							jcqkhzt = "��բ";
						}
					} else {
						jcqkhzt = "��Ч";
					}

					// ���������ն˵�ǰ״̬��¼��
					String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
					String this_sql = "";
					Object[] this_params = null;
					if (Util.checkZddqztByZdid(zdid, jdbcT)) {
						this_sql = "update G_ZDDQZTJLB set ZHJSSJ=sysdate, DZKHZT=? ,DZKH_FSSJ=to_date(?,'yymmddhh24mi'), DZKHQGL=?,DZKHHGL=?,DZKHSGLDZ=? where zdid=? ";
						this_params = new Object[] { tem_jcqkhzt, sjfssj,
								s_tzqgl, s_tzhgl, s_tzsgldz, zdid };
					} else {
						this_sql = "insert into G_ZDDQZTJLB (zdid,zhjssj,DZKHZT,DZKH_FSSJ,DZKHQGL,DZKHHGL,DZKHSGLDZ) values(?,sysdate,?,to_date(?,'yymmddhh24mi'),?,?,?)";
						this_params = new String[] { zdid, tem_jcqkhzt, sjfssj,
								s_tzqgl, s_tzhgl, s_tzsgldz };
					}
					jdbcT.update(this_sql, this_params);

					// �¼�ժҪ
					sjzy = "�Ӵ�������״̬:" + jcqkhzt + ";  �������:" + gklb
							+ ";  ����ǰ����(�ܼӹ���):" + s_tzqgl
							+ ";  ���Ϻ�2���ӵĹ���(�ܼӹ���):" + s_tzhgl
							+ ";  ����ʱ���ʶ�ֵ:" + s_tzsgldz;

				} else if (sjdm.equals("51")) {
					// ERC51:���ɳ��޸澯

					// �澯����ʱ��
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// ��Ϣ������նˣ�
					xxdlb = "0";
					// ��Ϣ���
					xxdh = "0";

					// �澯����
					String gjjb = sjContent.substring(10, 12);
					// ˲ʱ�й�����
					String ssyggl = Util.tranFormat02(sjContent.substring(12,
							16));
					String hcsj = sjContent.substring(16, 18);
					hcsj = String.valueOf(Integer.parseInt(hcsj, 16));
					String gldz = Util
							.tranFormat02(sjContent.substring(18, 22));

					// �¼�ժҪ
					sjzy = "���ɳ��޸澯:" + "  ��ǰ˲ʱ�й����ʣ�" + ssyggl + ";"
							+ "  ��բִ��ʣ��ʱ�䣺" + Double.parseDouble(hcsj) / 2
							+ "  ��ǰ���ʶ�ֵ��" + gldz;
					//����������ϱ��¼�,�����û����Ͷ���
					if (sjsxlx.equals("2")) {
						Dispatch dispatch = (Dispatch) Context.ctx
								.getBean("dispatchService");
						// ȡ���û����ƺ��û��ֻ�����
						String[] hm_yhsjhm = Util.getHmAndYhsjhm(xzqxm, zddz,
								jdbcT);
						// ��������
						String content = "";
						String content1="";
						if ("01".equalsIgnoreCase(gjjb)) {
							content = "�𾴵��û�" + hm_yhsjhm[0] + ":����ǰ�õ縺��("
									+ ssyggl + "kW)����"
									+ Double.parseDouble(hcsj) / 2 + "���ӳ����涨ֵ"
									+ gldz + "kW,���������͸���,����"
									+ Double.parseDouble(hcsj) / 2 + "���Ӻ�ִ����բ!";
							content1 = "�û�["+hm_yhsjhm[0]+"]��ǰ�õ縺��("
							+ ssyggl + "kW)����"
							+ Double.parseDouble(hcsj) / 2 + "���ӳ����涨ֵ"
							+ gldz + "kW,����"
							+ Double.parseDouble(hcsj) / 2 + "���Ӻ�ִ����բ!";
						} else {
							content = "�𾴵��û�" + hm_yhsjhm[0]
									+ ":��δ�ڹ涨ʱ���ڽ��͸���(��ǰ" + ssyggl
									+ "kW),��ʱ�εĹ��ʶ�ֵ" + gldz + "kW,��ִ����բ����!";
							content1 = "�û�["+hm_yhsjhm[0]+"]δ�ڹ涨ʱ���ڽ��͸���(��ǰ" + ssyggl
															+ "kW),��ʱ�εĹ��ʶ�ֵ" + gldz + "kW,��ִ����բ����!";
						}
						// ���Ͷ���
						dispatch.sedAscendSms(hm_yhsjhm[1], content, true);
						dispatch.sedAscendSms(hm_yhsjhm[3], content1, true);
					}
				}

				if (sjsxlx.equals("1")) {
					// �¼���ѯ����(д"�ٲ����ݷ��ر�")
					String temp_sjzy = "";
					String temp_xxdlb = "";

					if (xxdlb.equals("1")) {
						temp_xxdlb = "������";
					} else if (xxdlb.equals("2")) {
						temp_xxdlb = "�ܼ���";
					} else if (xxdlb.equals("3")) {
						temp_xxdlb = "ֱ��ģ����";
					}

					if (!xxdlb.equals("0")) {
						temp_sjzy = temp_xxdlb + xxdh + ";";
					}

//					temp_sjzy = temp_sjzy + "�¼�����ʱ��:" + Util.getSJSJ(sjfssj)
//							+ "; " + sjzy;
					//2017-06-21ȡ������ʱ��
					temp_sjzy = temp_sjzy + "; " + sjzy;
					xh++;
					s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
							+ "values(?,?,?,?,?,?,?,sysdate)";
					params = new String[] { s_sjzfsseq, "0E", pnfn,
							"ERC" + sjdm, temp_sjzy, "�ն�", String.valueOf(xh) };
					jdbcT.update(s_sql, params);
					cat.info("[exceptionDecode]s_sql:" + s_sql);
				} else if (sjsxlx.equals("2")) {
					// �¼������ϱ�
					String temp_sjzy = "";
					// 1��д"�ն��¼���¼��"
					// �¼�����
					int i_sjid = Integer.parseInt(Util.getSeqException(jdbcT));
					String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
//					temp_sjzy = temp_sjzy + "�¼�����ʱ��:" + Util.getSJSJ(sjfssj)
//							+ ";  " + sjzy;
					//2017-06-21ȡ������ʱ��
					temp_sjzy = temp_sjzy + "; " + sjzy;
					s_sql = "insert into G_ZDSJJLB(sjid,zdid,xxdlb,xxdh,"
							+ "sjdm,sjzy,fssj,jssj) "
							+ "values(SEQ_EXCEPTION.NEXTVAL,?,?,?,?,?,"
							+ "to_date(?,'yymmddhh24mi'),sysdate)";
					params = new String[] { zdid, xxdlb, xxdh, "ERC" + sjdm,
							temp_sjzy, sjfssj };
					jdbcT.update(s_sql, params);
					cat.info("[exceptionDecode]s_sql:" + s_sql);

					// 2��update�ն˵�ǰ״̬��
					// s_sql =
					// "update zddqztb set dqsj='"+sjdm+"',dqsjsj=sysdate "
					// + "where xzqxm='"+xzqxm+"' and zddz='"+zddz+"'";
					// jdbcT.update(s_sql);
					// cat.info("[exceptionDecode]s_sql:"+s_sql);
				}

			}

		} catch (Exception e) {
			throw e;
		}
	}

}