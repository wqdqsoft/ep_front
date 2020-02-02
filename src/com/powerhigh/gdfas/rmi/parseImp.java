package com.powerhigh.gdfas.rmi;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.dbparse.dlt645.dlt645Parse;
import com.powerhigh.gdfas.module.Dispatch;
import com.powerhigh.gdfas.parse.Decode_0F;
import com.powerhigh.gdfas.parse.Decode_0F_ReSend;
import com.powerhigh.gdfas.util.CMContext;
import com.powerhigh.gdfas.util.DateUtil;
import com.powerhigh.gdfas.util.Util;

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

public class parseImp implements parse {

	// ������־
	private static final String resource = "log4j.properties";

	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.rmi.parseImp.class);

	// static {
	// PropertyConfigurator.configure(resource);
	// }

	private static String sBegin = "68";

	private static String sEnd = "16";

	private DataSource dataSource = null;

	private Dispatch dispatchService = null;
	
	private static int i_count = 0;

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource ds) {
		dataSource = ds;
	}

	public Dispatch getDispatchService() {
		return dispatchService;
	}

	public void setDispatchService(Dispatch dis) {
		dispatchService = dis;
	}

	// Construct
	public parseImp() {
	}

	public void test() throws Exception {
		// System.out.println("t1");
		// JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// String s_sql = "update G_ZDGZ set zdmc='lhmmmx' where zdid=1";
		// jdbcT.update(s_sql);
		// System.out.println("t2");
		// Vector sp_param = new Vector();
		// Util.executeProcedure(jdbcT,"sp_test",sp_param,0);
		// System.out.println("t3");
	}

	/**
	 * ��������������
	 * 
	 * @param txfs
	 *            String ͨ�ŷ�ʽ(����txfsb��ȡ)
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sSJZ
	 *            String ������
	 * @param seq_sjzfs
	 *            String ���ݷ��ͱ�����
	 * @param SIM
	 *            String ���ݷ��ͱ�����
	 * 
	 * @return void
	 */
	private void send(String txfs, String xzqxm, String zddz, String sSJZ,
			String seq_sjzfs, String SIM, JdbcTemplate jdbcT) throws Exception {

		String gylx = Util.getZdgylx(xzqxm, zddz, jdbcT);// ��Լ����:1:���;2:����;3:������
		String SJZ = "";
		if (gylx.equals("3")) {
			// 2009-10-18������������
			SJZ = Util.addSG(xzqxm, zddz, sSJZ);
		} else {
			SJZ = sSJZ;
		}
		dispatchService.downDispatch(txfs, gylx, xzqxm, zddz, SJZ, seq_sjzfs,
				SIM);
	}

	/**
	 * ������������������parse
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param kzm
	 *            String ������
	 * @param afn
	 *            String Ӧ�ù�����
	 * @param xxd
	 *            String ��Ϣ��(Pn)
	 * @param xxl
	 *            String ��Ϣ��(Fn)
	 * @param data
	 *            String ���ݵ�Ԫ
	 * @param mc
	 *            String ����
	 * @param jdbcT
	 *            JdbcTemplate ���ݿ�����
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	@SuppressWarnings("rawtypes")
	private String parse(String txfs, String xzqxm, String zddz, String kzm,
			String afn, String xxd, String xxl, String data, String mc,
			JdbcTemplate jdbcT) throws Exception {

		String seq_sjzfs = null;

		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();
		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = kzm;

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = afn;

		// 2��֡������(TpV=1;FIR=1;FIN=1;CON=1)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);
		int iZdpseq = getZdpseq(iZdpfc);
		//2017-01-12 ȡ��05����SEQ��ʱ��
		if (afn.equals("04")) {
//		if (afn.equals("04") || afn.equals("05")) {
//			sSEQ = "F" + Integer.toHexString(iZdpseq);
			sSEQ = "7" + Integer.toHexString(iZdpseq);
		}else if (afn.equals("05")) {
//			if (afn.equals("04") || afn.equals("05")) {
				sSEQ = "7" + Integer.toHexString(iZdpseq);
		}else {
			sSEQ = "E" + Integer.toHexString(iZdpseq);
		}
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3�����ݵ�Ԫ��ʶDADT
		String sDA = Util.getDA(xxd);
		String sDT = Util.getDT(xxl);
		String sDADT = Util.convertStr(sDA) + Util.convertStr(sDT);

		// 4�����ݵ�ԪDATA
		String sDATA = data;

		// 5��������ϢAUX
		String sAUX = "";
		String sPassword = CMContext.getZdmm(xzqxm, zddz);

		String sTime = Util.getTp(iZdpfc, rq, 0);

		// 2009-10-21 ??�Ƿ��������ж����Դ������ʱ��??
		if (afn.equals("05")||afn.equals("04")){
			sAUX = sPassword;
		}else{
			sAUX = sPassword + sTime;
		}
		

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		// 04��
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698��
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		String temp_AFN = "00";
		if (sAFN.equalsIgnoreCase("0B") || sAFN.equalsIgnoreCase("0C")
				|| sAFN.equalsIgnoreCase("0D") || sAFN.equalsIgnoreCase("0E")|| sAFN.equalsIgnoreCase("0F")) {
			temp_AFN = sAFN;
		}
		// д������֡���ͱ��������ݱ�ʶ�ӱ�
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		// String sSql = "insert into
		// g_sjzfsb(sjzfsseq,xzqxm,zddz,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
		// + "values(?,?,?,?,?,?,'02',?,sysdate,?)";
		// String[] params = new String[]{
		// seq_sjzfs,xzqxm,zddz,temp_AFN,sSEQ1.toUpperCase(),
		// Util.decStrToHexStr(iZdpfc,1),rq.substring(4, 12),sSJZ};
		// jdbcT.update(sSql,params);

		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,(select zdid from G_ZDGZ where xzqxm=? and zddz=?),"
				+ "?,?,?,'02',?,sysdate,?)";
		String[] params = new String[] { seq_sjzfs, xzqxm, zddz, temp_AFN,
				sSEQ1.toUpperCase(), Util.decStrToHexStr(iZdpfc, 1),
				rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,sjdybsz,sjdybsmc) "
				+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
		params = new String[] { seq_sjzfs, sAFN, xxd + xxl,
				Util.convertStr(sDA) + Util.convertStr(sDT), mc };
		jdbcT.update(sSql, params);

		// ����
		sSql = "select sim from G_ZDGZ where xzqxm=? and zddz=?";
		params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));

		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˶�ʱF31(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rq
	 *            String ���� XX��ʾϵͳʱ��
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendZdds(String txfs, String xzqxm, String zddz, String rq)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F31"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		if (rq.equalsIgnoreCase("XX")) {
			data = Util.makeFormat01(Util.getNowTime(), Util.getNowWeek());
		} else {
			data = Util.makeFormat01(rq, Util.getWeek(rq));
		}

		// �塢����
		String mc = "�ն˶�ʱ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������λ����F1/F2/F3(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param fwlx
	 *            String ��λ����(F1/F2/F3)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendZdfw(String txfs, String xzqxm, String zddz, String fwlx)
			throws Exception {
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "01"; // ��λ

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = fwlx; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "��λ����";
		if (fwlx.equalsIgnoreCase("F1")) {
			mc += "Ӳ����ʼ��";
		} else if (fwlx.equalsIgnoreCase("F2")) {
			mc += "��������ʼ��";
		} else if (fwlx.equalsIgnoreCase("F3")) {
			mc += "������ȫ����������ʼ��";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������Ƿ������ն�����վͨ������F27/F35(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sfyx
	 *            String �Ƿ�����:1:����0����ֹ
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendSfyxzdyzzth(String txfs, String xzqxm, String zddz,
			String sfyx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (sfyx.equals("1")) {
			// ����F27
			xxl = "F27";
		} else if (sfyx.equals("0")) {
			// ��ֹF35
			xxl = "F35";
		}

		// �ġ����ݵ�Ԫ
		String data = "";// �����ݵ�Ԫ

		// �塢����
		String mc = "";
		if (sfyx.equals("1")) {
			// ����
			mc = "�����ն�����վͨ������";
		} else if (sfyx.equals("0")) {
			// ��ֹ
			mc = "��ֹ�ն�����վͨ������";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "sfyxzdyzzth", sfyx, jdbcT); // �Ƿ������ն�����վͨ��

		return seq_sjzfs;
	}

	/**
	 * �����������Ƿ��ն��޳�Ͷ������F28/F36(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sftctr
	 *            String �Ƿ��޳�Ͷ��:1:Ͷ�룻0���޳�
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendSfzdtctr(String txfs, String xzqxm, String zddz,
			String sftctr) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (sftctr.equals("1")) {
			// Ͷ��F28
			xxl = "F28";
		} else if (sftctr.equals("0")) {
			// ���F36
			xxl = "F36";
		}

		// �ġ����ݵ�Ԫ
		String data = "";// �����ݵ�Ԫ

		// �塢����
		String mc = "";
		if (sftctr.equals("1")) {
			mc = "�ն��޳�Ͷ������";
		} else if (sftctr.equals("0")) {
			mc = "�ն��޳��������";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "sfzdtctr", sftctr, jdbcT); // �Ƿ��ն��޳�Ͷ��

		return seq_sjzfs;
	}

	/**
	 * �����������Ƿ������ն������ϱ�����F29/F37(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sfyxzdsb
	 *            String �Ƿ����������ϱ�:1:����0����ֹ
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendSfyxzdzdsb(String txfs, String xzqxm, String zddz,
			String sfyxzdsb) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (sfyxzdsb.equals("1")) {
			// ����F29
			xxl = "F29";
		} else if (sfyxzdsb.equals("0")) {
			// ��ֹF37
			xxl = "F37";
		}

		// �ġ����ݵ�Ԫ
		String data = "";// �����ݵ�Ԫ

		// �塢����
		String mc = "";
		if (sfyxzdsb.equals("1")) {
			mc = "�����ն������ϱ�����";
		} else if (sfyxzdsb.equals("0")) {
			mc = "��ֹ�ն������ϱ�����";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "sfyxzdzdsb", sfyxzdsb, jdbcT); // �Ƿ������ն������ϱ�

		return seq_sjzfs;
	}

	/**
	 * ����������1/2��������������/ֹͣ����F67/F68(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rwlx
	 *            String �������ͣ�1��1�ࣻ2��2�ࣩ
	 * @param rwh
	 *            String �����
	 * @param rwqdbz
	 *            String ����������־:55:������AA��ֹͣ
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendZdrwqybz(String txfs, String xzqxm, String zddz,
			String rwlx, String rwh, String rwqybz) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// д���ն��������ñ�
		sSql = "update zdrwpzb " + "set qybz=? " + "where xzqxm=? and zddz=? "
				+ "and rwlx=? and rwh=?";
		String[] params = new String[] { rwqybz, xzqxm, zddz, rwlx, rwh };
		jdbcT.update(sSql, params);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + rwh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (rwlx.equals("1")) {
			// 1��F67
			xxl = "F67";
		} else if (rwlx.equals("2")) {
			// 2��F68
			xxl = "F68";
		}

		// �ġ����ݵ�Ԫ
		String data = rwqybz;// ����������־

		// �塢����
		String mc = rwh + "������������־[" + rwqybz + "](" + rwlx + "����������)";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˱�������F25(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param bdsj
	 *            String ����ʱ�� ��ֵ��Χ��0��48����λ��0.5h��0��ʾ�����ڱ��磻AA�����糷��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdbd(String txfs, String xzqxm, String zddz, String bdsj)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (bdsj.equalsIgnoreCase("AA")) {
			// ������F33
			xxl = "F33";
		} else {
			// ����ʱ������F25
			xxl = "F25";
		}

		// �ġ����ݵ�Ԫ
		String data = "";
		if (!bdsj.equalsIgnoreCase("AA")) {
			// ����ʱ������F25
			data = Util.decStrToHexStr(bdsj, 1);
		}

		// �塢����
		String mc = "";
		if (bdsj.equalsIgnoreCase("AA")) {
			mc = "�ն˱���������";
		} else {
			mc = "�ն˱�������";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "zdbdsj", bdsj, jdbcT); // �ն˱���ʱ��

		return seq_sjzfs;
	}

	/**
	 * �����������ն���������������F9(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param dnbsl
	 *            int ���ܱ�����
	 * @param mcsl
	 *            int ��������
	 * @param mnlsl
	 *            int ģ��������
	 * @param zjzsl
	 *            int �ܼ�������
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdpzslb(String txfs, String xzqxm, String zddz,
			int dnbsl, int mcsl, int mnlsl, int zjzsl) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F9"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ���ܱ�����
		String sDNBSL = Integer.toHexString(dnbsl);
		if (sDNBSL.length() < 2) {
			sDNBSL = "0" + sDNBSL;
		}

		// ��������
		String sMCSL = Integer.toHexString(mcsl);
		if (sMCSL.length() < 2) {
			sMCSL = "0" + sMCSL;
		}

		// ģ��������
		String sMNLSL = Integer.toHexString(mnlsl);
		if (sMNLSL.length() < 2) {
			sMNLSL = "0" + sMNLSL;
		}

		// �ܼ�������
		String sZJZSL = Integer.toHexString(zjzsl);
		if (sZJZSL.length() < 2) {
			sZJZSL = "0" + sZJZSL;
		}

		data = sDNBSL + sMCSL + sMNLSL + sZJZSL;

		// �塢����
		String mc = "�ն���������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "dnbsl", String.valueOf(dnbsl), jdbcT); // ���ܱ�����
		writeCsszzcb(seq_sjzfs, "mcsl", String.valueOf(mcsl), jdbcT); // ��������
		writeCsszzcb(seq_sjzfs, "mnlsl", String.valueOf(mnlsl), jdbcT); // ģ��������
		writeCsszzcb(seq_sjzfs, "zjzsl", String.valueOf(zjzsl), jdbcT); // �ܼ�����������

		return seq_sjzfs;
	}

	/**
	 * �����������ն˳���������F24(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cbjg
	 *            int ����������λ�����ӣ�
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdcbjg(String txfs, String xzqxm, String zddz, int cbjg)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F24"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String sCBJG = Integer.toHexString(cbjg);
		if (sCBJG.length() < 2) {
			sCBJG = "0" + sCBJG;
		}
		data = sCBJG;

		// �塢����
		String mc = "�ն˳���������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "zdcbjg", String.valueOf(cbjg), jdbcT); // �ն˳�����

		return seq_sjzfs;
	}

	/**
	 * �����������ն��Զ���������F58(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zdbdsj
	 *            int �Զ�����ʱ�䣨��λ��Сʱ��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdzdbd(String txfs, String xzqxm, String zddz, int zdbdsj)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F58"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data = Util.decStrToHexStr(zdbdsj, 1);

		// �塢����
		String mc = "�ն��Զ���������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "zdzdbdsj", String.valueOf(zdbdsj), jdbcT); // �ն��Զ�����

		return seq_sjzfs;
	}

	/**
	 * �������������������ݶ����������F27(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param djcs
	 *            String[][2] ���������Ͷ�Ӧ�Ķ��������0�������᣻1��15�֣�2��30�֣�3��60�֣�
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCldsjdjcs(String txfs, String xzqxm, String zddz,
			String cldh, String[][] djcs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F27"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String temp_djcs = cldh + "#";// cldh#Fn,djn#Fn,djn#
		data = Util.decStrToHexStr(djcs.length, 1);// ������Ϣ�����
		for (int i = 0; i < djcs.length; i++) {
			// ÿ����Ϣ����
			String xxx = djcs[i][0];// ��Ϣ��Fn
			String djmd = djcs[i][1];// �����ܶ�
			temp_djcs += xxx + "," + djmd + "#";
			data += Util.decStrToHexStr(xxx.substring(1), 1)
					+ Util.decStrToHexStr(djmd, 1);
		}

		// �塢����
		String mc = "���������ݶ����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "cldsjdjcs", temp_djcs, jdbcT); // ���������ݶ������

		return seq_sjzfs;
	}

	/**
	 * �����������ܼ������ݶ����������F33(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            String �ܼ����
	 * @param djcs
	 *            String[4] ���������0�������᣻1��15�֣�2��30�֣�3��60�֣�
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZjzsjdjcs(String txfs, String xzqxm, String zddz,
			String zjzh, String[] djcs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F33"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String temp_djcs = zjzh + "#";
		for (int i = 0; i < 4; i++) {
			temp_djcs += djcs[i] + "#";
			data += Util.decStrToHexStr(djcs[i], 1);
		}

		// �塢����
		String mc = "�ܼ������ݶ����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "zjzsjdjcs", temp_djcs, jdbcT); // �ܼ������ݶ������

		return seq_sjzfs;
	}

	/**
	 * �����������ն�ͨ�Ų�������F1(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param scjyssj
	 *            String ��������ʱʱ��,��λ:20ms
	 * @param fscsyxyssj
	 *            String ��Ϊ����վ�����ʹ�����ʱʱ��,��λ:����
	 * @param ddcdzxycssj
	 *            String �ȴ��Ӷ�վ��Ӧ�ĳ�ʱʱ��,0-4095,��λ:��
	 * @param cfcs
	 *            String �ط�����,0-3;0��ʾ�������ط�
	 * @param zdsbzysjjlqrbz
	 *            String �����ϱ���Ҫ�¼���¼��ȷ�ϱ�־,1��ʾ��Ҫ��վȷ��
	 * @param zdsbybsjjlqrbz
	 *            String �����ϱ�һ���¼���¼��ȷ�ϱ�־,1��ʾ��Ҫ��վȷ��
	 * @param xtzq
	 *            String ��������:1-60��
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdtxcs(String txfs, String xzqxm, String zddz,
			String scjyssj, String fscsyxyssj, String ddcdzxycssj, String cfcs,
			String zdsbzysjjlqrbz, String zdsbybsjjlqrbz, String xtzq)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F1"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ��������ʱʱ��
		data += Util.decStrToHexStr(scjyssj, 1);
		// ��Ϊ����վ�����ʹ�����ʱʱ��
		data += Util.decStrToHexStr(fscsyxyssj, 1);
		// �ȴ��Ӷ�վ��Ӧ�ĳ�ʱʱ�估�ط�����
		ddcdzxycssj = Util.decStrToBinStr(ddcdzxycssj, 2);
		ddcdzxycssj = ddcdzxycssj.substring(4, 16);
		cfcs = Util.decStrToBinStr(cfcs, 1);
		cfcs = cfcs.substring(6, 8);
		data += Util.convertStr(Util.binStrToHexStr(cfcs + ddcdzxycssj, 2));
		// ��Ҫ��վȷ�ϵı�־
		data += Util.binStrToHexStr(zdsbybsjjlqrbz + zdsbzysjjlqrbz, 1);
		// ��������
		data += Util.decStrToHexStr(xtzq, 1);

		// �塢����
		String mc = "�ն�ͨ�Ų�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String txcs = scjyssj + ";" + fscsyxyssj + ";" + ddcdzxycssj + ";"
				+ cfcs + ";" + zdsbzysjjlqrbz + "#" + zdsbybsjjlqrbz + ";"
				+ xtzq;
		writeCsszzcb(seq_sjzfs, "txcs", txcs, jdbcT); // �ն�ͨ�Ų���

		return seq_sjzfs;
	}

	/**
	 * �����������ն˸�λ��F1Ӳ����ʼ��(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(Ԥ��)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN01F1(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "01"; // ��λ

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F1"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "�ն˸�λ��F1Ӳ����ʼ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˸�λ��F2��������ʼ��(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(Ԥ��)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN01F2(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "01"; // ��λ

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F2"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "�ն˸�λ��F2��������ʼ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˸�λ��F3������ȫ����������ʼ�������ָ����������ã�(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(Ԥ��)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN01F3(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "01"; // ��λ

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F3"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "�ն˸�λ��F3������ȫ����������ʼ�������ָ����������ã�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˸�λ��F4����������ϵͳ��վͨ���йصģ���ȫ����������ʼ��(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(Ԥ��)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN01F4(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "01"; // ��λ

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F4"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "�ն˸�λ��F4����������ϵͳ��վͨ���йصģ���ȫ����������ʼ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն�ͨ�Ų�������F1(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;cs2;cs3;cs4;cs5) 
	 *            cs1:��������ʱʱ��,��λ:20ms
	 *            cs2:�ն�ͨ��ģ����ź�ǿ��,��λ:db 
	 *            cs3:�ն��������������65535��,��λ:��
	 *            cs4:���� Ĭ��FF
	 *            cs5:��������:1-60��
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F1(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F1"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		String[] ss_csz = csz.split(";");
		// ��������ʱʱ��
		String cs1 = ss_csz[0];
		data += Util.decStrToHexStr(cs1, 1);
		
		// �ն�ͨ��ģ����ź�ǿ��
		String cs2 = ss_csz[1];
		data += Util.makeFormat04(Integer.parseInt(cs2));
		// �ն���������
		String cs3 = ss_csz[2];
		cs3 = Util.convertStr(Util.decStrToHexStr(cs3, 2));
		data += cs3;
		// ��Ƶ���ͺţ�1-JAC780, 2-JAC580B.
		String cs4=ss_csz[3];
		data += Util.decStrToHexStr(cs4, 1);
		
		// ��������
		String cs5 = ss_csz[4];
		data += Util.decStrToHexStr(cs5, 1);

		// �塢����
		String mc = "�ն�ͨ�Ų�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F1", csz, jdbcT); // �ն�ͨ�Ų���

		return seq_sjzfs;
	}

	/**
	 * �������������ܱ��쳣�б�ֵ�趨F59(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param dnlccfz
	 *            String ���������ֵx.x
	 * @param dnbfzfz
	 *            String ���ܱ���߷�ֵx.x
	 * @param dnbtzfz
	 *            String ���ܱ�ͣ�߷�ֵ,��λ:15min
	 * @param dnbjsfz
	 *            String ���ܱ�Уʱ��ֵ,��λ:min
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDnbycpbfz(String txfs, String xzqxm, String zddz,
			String dnlccfz, String dnbfzfz, String dnbtzfz, String dnbjsfz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F59"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.makeFormat22(dnlccfz);
		data += Util.makeFormat22(dnbfzfz);
		data += Util.decStrToHexStr(dnbtzfz, 1);
		data += Util.decStrToHexStr(dnbjsfz, 1);

		// �塢����
		String mc = "���ܱ��쳣�б�ֵ����";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String dnbycpbfz = dnlccfz + ";" + dnbfzfz + ";" + dnbtzfz + ";"
				+ dnbjsfz;
		writeCsszzcb(seq_sjzfs, "dnbycpbfz", dnbycpbfz, jdbcT); // ���ܱ��쳣�б�ֵ

		return seq_sjzfs;
	}

	/**
	 * ������������վIP��ַ�Ͷ˿�����F3(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zyip
	 *            String[] ����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param byip
	 *            String[] ����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param wgip
	 *            String[] ����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param dlip
	 *            String[] ����IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param apn
	 *            String APN(16�ֽڣ�ASCII;��λ��00H��������)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZzip(String txfs, String xzqxm, String zddz,
			String[] zyip, String[] byip, String[] wgip, String[] dlip,
			String apn) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F3"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ��վ����IP
		String temp_zyip = zyip[0] + "." + zyip[1] + "." + zyip[2] + "."
				+ zyip[3] + ":" + zyip[4];
		data += Util.decStrToHexStr(zyip[0], 1);
		data += Util.decStrToHexStr(zyip[1], 1);
		data += Util.decStrToHexStr(zyip[2], 1);
		data += Util.decStrToHexStr(zyip[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(zyip[4], 2));

		// ��վ����IP
		String temp_byip = byip[0] + "." + byip[1] + "." + byip[2] + "."
				+ byip[3] + ":" + byip[4];
		data += Util.decStrToHexStr(byip[0], 1);
		data += Util.decStrToHexStr(byip[1], 1);
		data += Util.decStrToHexStr(byip[2], 1);
		data += Util.decStrToHexStr(byip[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(byip[4], 2));

		// ��վ����IP
		String temp_wgip = wgip[0] + "." + wgip[1] + "." + wgip[2] + "."
				+ wgip[3] + ":" + wgip[4];
		data += Util.decStrToHexStr(wgip[0], 1);
		data += Util.decStrToHexStr(wgip[1], 1);
		data += Util.decStrToHexStr(wgip[2], 1);
		data += Util.decStrToHexStr(wgip[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(wgip[4], 2));

		// ��վ����IP
		String temp_dlip = dlip[0] + "." + dlip[1] + "." + dlip[2] + "."
				+ dlip[3] + ":" + dlip[4];
		data += Util.decStrToHexStr(dlip[0], 1);
		data += Util.decStrToHexStr(dlip[1], 1);
		data += Util.decStrToHexStr(dlip[2], 1);
		data += Util.decStrToHexStr(dlip[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(dlip[4], 2));

		// APN
		byte[] bt = apn.getBytes();
		data += Util.bytetostrs(bt);
		for (int i = 0; i < 16 - bt.length; i++) {
			data += "00";
		}

		// �塢����
		String mc = "��վIP��ַ����";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String zzip = temp_zyip + ";" + temp_byip + ";" + temp_wgip + ";"
				+ temp_dlip + ";" + apn;
		writeCsszzcb(seq_sjzfs, "zzip", zzip, jdbcT); // ��վIP

		return seq_sjzfs;
	}

	/**
	 * ������������վIP��ַ�Ͷ˿�����F3(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;cs2;cs3) 
	 *            cs1:����IP(xxx.xxx.xxx.xxx:nnnnn)
	 *            cs2:����IP(xxx.xxx.xxx.xxx:nnnnn)
	 *            cs3:APN(16�ֽڣ�ASCII;��λ��00H��������)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F3(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F3"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");

		// ��վ����IP
		String cs1 = ss_csz[0];
		cs1 = cs1.replace(".", "#");
		String[] tempss1 = cs1.split(":");
		String[] ss1 = tempss1[0].split("#");
		data += Util.decStrToHexStr(ss1[0], 1);
		data += Util.decStrToHexStr(ss1[1], 1);
		data += Util.decStrToHexStr(ss1[2], 1);
		data += Util.decStrToHexStr(ss1[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(tempss1[1], 2));

		// ��վ����IP
		String cs2 = ss_csz[1];
		cs2 = cs2.replace(".", "#");
		String[] tempss2 = cs2.split(":");
		String[] ss2 = tempss2[0].split("#");
		data += Util.decStrToHexStr(ss2[0], 1);
		data += Util.decStrToHexStr(ss2[1], 1);
		data += Util.decStrToHexStr(ss2[2], 1);
		data += Util.decStrToHexStr(ss2[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(tempss2[1], 2));

		// APN
		String cs3 = ss_csz[2];
		byte[] bt = cs3.getBytes();
		data += Util.addAfter(Util.bytetostrs(bt), 16, "0");

		// �塢����
		String mc = "��վIP��ַ�Ͷ˿�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F3", csz, jdbcT); // ��վIP

		return seq_sjzfs;
	}

	/**
	 * ������������վ�绰����Ͷ������ĺ�������F4(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zzdhhm
	 *            String ��վ�绰����
	 * @param dxzxhm
	 *            String �������ĺ���
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZzdhhm(String txfs, String xzqxm, String zddz,
			String zzdhhm, String dxzxhm) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F4"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ��վ�绰����
		if (zzdhhm == null || zzdhhm.equals("")) {
			for (int i = 0; i < 16; i++) {
				data += "F";
			}
		} else {
			data += zzdhhm;
			for (int i = 0; i < 16 - zzdhhm.length(); i++) {
				data += "F";
			}
		}

		// �������ĺ���
		if (dxzxhm == null || dxzxhm.equals("")) {
			for (int i = 0; i < 16; i++) {
				data += "F";
			}
		} else {
			data += dxzxhm;
			for (int i = 0; i < 16 - dxzxhm.length(); i++) {
				data += "F";
			}
		}

		// �塢����
		String mc = "��վ�绰����Ͷ������ĺ�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String dhhm = zzdhhm + ";" + dxzxhm;
		writeCsszzcb(seq_sjzfs, "zzdhhm", dhhm, jdbcT); // ��վ�绰����Ͷ������ĺ���

		return seq_sjzfs;
	}

	/**
	 * ��������������ػ�������F4(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;cs2;.....cs6) 
	 *            [-----�����һ���ն�-----]
	 *            cs1:��������
	 *            cs2:�����ģ
	 *            cs3:����
	 *            cs4:��������
	 *            cs5:��������(ˮ������)
	 *            cs6:�ȵ���Сʱ��
	 *            cs7:��Ƚ���ģʽʹ��
	 *            cs8:һ���նˣ����ڳ�/�ռ��ظ��������������նˣ�����������
	 *            cs9:���ڳ�ˮλ���ޣ�����嶥�����룩
	 *            cs10:���ڳ�ˮλ���ޣ�����嶥�����룩
	 *            cs11:Ŀ��ORP��ֵ��Χ����
	 *            cs12:Ŀ��ORP��ֵ��Χ����
	 *            cs13:ˮ�ÿ����ж�����
	 *            [-----end �����һ���ն�-----]
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F4(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F4"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		
		String[] ss_csz = csz.split(";");
		
		String s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
		String[] params = new String[] { xzqxm,zddz };
	    List cldList = jdbcT.queryForList(s_sql, params);
	    Map cldMap = (Map) cldList.get(0);
	    // �ն��ͺ�
	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
	 	//�����һ���ն�
	 	if("1".equalsIgnoreCase(zdxh)){
	 	    // ��������
			String cs1 = ss_csz[0];
			data += Util.decStrToHexStr(cs1, 1);

			// �����ģ
			String cs2 = ss_csz[1];
			data += Util.makeFormat04( Integer.parseInt(cs2));
			
			// �������
			String cs3 = ss_csz[2];
			data += Util.makeFormat22(cs3);
			
			
			// һ���նˣ����ڳ�/�ռ��ظ�������
			String cs4 = ss_csz[3];
			data += Util.decStrToHexStr(cs4, 1);
			
			//���ڳ�ˮλ���ޣ�����嶥�����룩
			String cs5 = ss_csz[4];
			data += Util.makeFormat22(cs5);
			
			//���ڳ�ˮλ���ޣ�����嶥�����룩
			String cs6 = ss_csz[5];
			data += Util.makeFormat22(cs6);
			
			//Ŀ��ORP��ֵ��Χ����
			String cs7 = ss_csz[6];
			if("EEEE".equalsIgnoreCase(cs7)){
				data +=cs7;
			}else{
				int c7=Integer.parseInt(cs7);
				if(c7<0){
					data += Util.makeFormat28(1,Math.abs(c7));
				}else{
					data += Util.makeFormat28(0,Math.abs(c7));
				}
			}
			
			
			//Ŀ��ORP��ֵ��Χ����
			String cs8 = ss_csz[7];
			if("EEEE".equalsIgnoreCase(cs8)){
				data +=cs8;
			}else{
				int c8=Integer.parseInt(cs8);
				if(c8<0){
					data += Util.makeFormat28(1,Math.abs(c8));
				}else{
					data += Util.makeFormat28(0,Math.abs(c8));
				}
			}
			
			String cs9 = ss_csz[8];
			data+=Util.decStrToHexStr(cs9, 1);
			//����Ƕ������ն�
	 	}else{
	 	    // ��������
			String cs1 = ss_csz[0];
			data += Util.decStrToHexStr(cs1, 1);

			// �����ģ
			String cs2 = ss_csz[1];
			data += Util.makeFormat04( Integer.parseInt(cs2));
			
			// �������
			String cs3 = ss_csz[2];
			data += Util.makeFormat22(cs3);
			
			// ��������
			String cs4 = ss_csz[3];
			data += Util.decStrToHexStr(cs4, 1);
			
			// ��������(ˮ������)
			String cs5 = ss_csz[4];
			data += Util.decStrToHexStr(cs5, 1);
			
			// �ȵ���Сʱ��
			String cs6 = ss_csz[5];
			data += Util.decStrToHexStr(cs6, 1);
			
			// ��Ƚ���ģʽʹ��
			String cs7 = ss_csz[6];
			data += cs7;
			
			// �����նˣ�����������
			String cs8 = ss_csz[7];
			data += cs8;
			
//			//���ڳ�ˮλ���ޣ�����嶥�����룩
//			String cs9 = ss_csz[8];
//			data += Util.makeFormat22(cs9);
//			
//			//���ڳ�ˮλ���ޣ�����嶥�����룩
//			String cs10 = ss_csz[9];
//			data += Util.makeFormat22(cs10);
//			
//			//Ŀ��ORP��ֵ��Χ����
//			String cs11 = ss_csz[10];
//			if("EEEE".equalsIgnoreCase(cs11)){
//				data +=cs11;
//			}else{
//				int c11=Integer.parseInt(cs11);
//				if(c11<0){
//					data += Util.makeFormat28(1,Math.abs(c11));
//				}else{
//					data += Util.makeFormat28(0,Math.abs(c11));
//				}
//			}
//			
//			
//			//Ŀ��ORP��ֵ��Χ����
//			String cs12 = ss_csz[11];
//			if("EEEE".equalsIgnoreCase(cs12)){
//				data +=cs12;
//			}else{
//				int c12=Integer.parseInt(cs12);
//				if(c12<0){
//					data += Util.makeFormat28(1,Math.abs(c12));
//				}else{
//					data += Util.makeFormat28(0,Math.abs(c12));
//				}
//			}
			//ˮλ��ⷽʽ��0��������ƣ�1��������ˮλ����
			String cs9 = ss_csz[8];
			data+=Util.decStrToHexStr(cs9, 1);
	 	}

		

		// �塢����
		String mc = "����ػ�����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F4", csz, jdbcT); // ��վ�绰����Ͷ������ĺ���

		return seq_sjzfs;
	}

	/**
	 * �������������ˮ�ÿ��Ʋ���F5(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;cs2;cs3;cs4;cs5)
	 *            cs1:������������ˮ�ÿ�������ǰ�� ��λ������
	 *            cs2:���ˮ���Զ����������־ 0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
	 *            cs3:����������Զ����������־   0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
	 *            cs4:����������������п���ʱ�� ��λ������
	 *            cs5:�����������������ֹͣʱ�� ��λ������
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F5(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F5"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");

		// ������������ˮ�ÿ�������ǰ��
		String s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
 		String[] params = new String[] { xzqxm,zddz };
 	    List cldList = jdbcT.queryForList(s_sql, params);
 	    Map cldMap = (Map) cldList.get(0);
 	    // �ն��ͺ�
 	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
 	 	
		String cs1 = ss_csz[0];
		if("EEEE".equalsIgnoreCase(cs1)){
			data+="EEEE";
		}else{
			if("1".equalsIgnoreCase(zdxh)){
				data +=Util.convertStr(Util.decStrToHexStr(cs1, 2));
			}else{
				data += Util.makeFormat08( Integer.parseInt(cs1));
			}
		}
		
		// ���ˮ���Զ����������־ 0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
		String cs2 = ss_csz[1];
		data +=cs2;
		
		// ����������Զ����������־   0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
		String cs3 = ss_csz[2];
		data +=cs3;
		
		// ����������������п���ʱ�� ��λ������
		String cs4 = ss_csz[3];
		if("EEEE".equalsIgnoreCase(cs4)){
			data+="EEEE";
		}else{
//		    data += Util.makeFormat08( Integer.parseInt(cs4));
			data +=Util.convertStr(Util.decStrToHexStr(cs4, 2));
		}
		
		// �����������������ֹͣʱ�� ��λ������
		String cs5 = ss_csz[4];
		if("EEEE".equalsIgnoreCase(cs4)){
			data+="EEEE";
		}else{
//		    data += Util.makeFormat08( Integer.parseInt(cs5));
			data +=Util.convertStr(Util.decStrToHexStr(cs5, 2));
		}

		// �塢����
		String mc = "���ˮ�ÿ��Ʋ�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F5", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������Ž�����������F6(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;cs3) 
	 *            cs1:ˢ����֤�����Чʱ��       ��λ:����
	 *            cs2:��������ʱ��       ��λ:����
	 *            cs3:�ƹⱨ��ʱ��       ��λ:����
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F6(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F6"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");

		//ˢ����֤�����Чʱ��
		String cs1 = ss_csz[0];
		data += Util.makeFormat04( Integer.parseInt(cs1));
		//��������ʱ��
		String cs2 = ss_csz[1];
		data += Util.makeFormat04( Integer.parseInt(cs2));
		//�ƹⱨ��ʱ��
		String cs3 = ss_csz[2];
		data += Util.makeFormat04( Integer.parseInt(cs3));
		
		// �塢����
		String mc = "�Ž�����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F6", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������Ƶ�����кſ� F7(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn) 
	 *            csn:��ƵID��n�����
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F7(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F7"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		
		//��Ƶ������
		int n=ss_csz.length;
		data +=Util.convertStr(Util.decStrToHexStr(n, 2));

		// 1-n����Ƶ�����(�û�ֱ����д�ľ���7���ֽڵ�16����)
		for (int i = 0; i < n; i++) {
			String csn = ss_csz[i];
			csn=Util.decStrToHexStr(Long.parseLong(csn), 7);
			csn=Util.convertStr(csn);
//			data += Util.behindChrForStr(csn, "0", 14);
			data +=csn;
		}

		// �塢����
		String mc = "��Ƶ�����кſ�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F7", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն�״̬�������������F12(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param ztljrbz
	 *            String ״̬�������־(D0-D7��ʾ1-8·,��1:����;��0:������)
	 * @param ztlsxbz
	 *            String ״̬�����Ա�־(D0-D7��ʾ1-8·,��1:a�ʹ���;��0:b�ʹ���)
	 * @param ztlgjbz
	 *            String ״̬���澯��־(D0-D7��ʾ1-8·,��1:��Ҫ�¼�;��0:һ���¼�)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZtlsrcs(String txfs, String xzqxm, String zddz,
			String ztljrbz, String ztlsxbz, String ztlgjbz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F12"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.binStrToHexStr(ztljrbz, 1);
		data += Util.binStrToHexStr(ztlsxbz, 1);
		data += Util.binStrToHexStr(ztlgjbz, 1);

		// �塢����
		String mc = "�ն�״̬�������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String ztl = ztljrbz + ";" + ztlsxbz + ";" + ztlgjbz;
		writeCsszzcb(seq_sjzfs, "ztlsrcs", ztl, jdbcT); // �ն�״̬���������

		return seq_sjzfs;
	}

	/**
	 * ����������ֱ��ģ�����������F61(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param jrbz
	 *            String ֱ��ģ���������־(D0-D7��ʾ1-8·,��1:����;��0:������)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZlmnljrcs(String txfs, String xzqxm, String zddz,
			String jrbz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F61"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.binStrToHexStr(jrbz, 1);

		// �塢����
		String mc = "ֱ��ģ���������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "zlmnljrbz", jrbz, jdbcT); // ֱ��ģ���������־

		return seq_sjzfs;
	}

	/**
	 * ��������������������F73(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param drqcs
	 *            String[9][5] ����������(1-9��),����1Ϊ��: drqcs[0][0]:���ֱ�־
	 *            drqcs[0][1]:�ֲ����־ drqcs[0][2]:����װ������ drqcs[0][3]:����װ������ϵ��
	 *            drqcs[0][4]:����װ����������
	 * 
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDrqcs(String txfs, String xzqxm, String zddz,
			String cldh, String[][] drqcs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F73"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String temps_cs = cldh + "@";
		for (int i = 0; i < 9; i++) {
			data += Util.binStrToHexStr(drqcs[i][0] + "000" + drqcs[i][1], 1);
			data += Util.makeFormat02(drqcs[i][2], drqcs[i][3], drqcs[i][4]);

			temps_cs = drqcs[i][0] + "#" + drqcs[i][1] + ";" + drqcs[i][2]
					+ "#" + drqcs[i][3] + "#" + drqcs[i][4] + "@";
		}

		// �塢����
		String mc = "����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "drqcs", temps_cs, jdbcT); // ����������

		return seq_sjzfs;
	}

	/**
	 * ����������������Ͷ�����в���F74(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param mbglys
	 *            String Ŀ�깦������
	 * @param mbglysfh
	 *            String Ŀ�깦����������(0:��;1:��)
	 * @param trwgglmx
	 *            String Ͷ���޹���������
	 * @param qcwgglmx
	 *            String �г��޹���������
	 * @param yssj
	 *            String ��ʱʱ��
	 * @param dzsjjg
	 *            String ����ʱ����
	 * 
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDrqtqyxcs(String txfs, String xzqxm, String zddz,
			String cldh, String mbglys, String mbglysfh, String trwgglmx,
			String qcwgglmx, String yssj, String dzsjjg) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F74"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.makeFormat05(mbglysfh, mbglys);
		data += Util.makeFormat23(trwgglmx);
		data += Util.makeFormat23(qcwgglmx);
		data += Util.decStrToHexStr(yssj, 1);
		data += Util.decStrToHexStr(dzsjjg, 1);

		// �塢����
		String mc = "������Ͷ�����в���";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String temps_cs = cldh + "@" + mbglys + "#" + mbglysfh + ";" + trwgglmx
				+ ";" + qcwgglmx + ";" + yssj + ";" + dzsjjg;
		writeCsszzcb(seq_sjzfs, "drqtqyxcs", temps_cs, jdbcT); // ������Ͷ�����в���

		return seq_sjzfs;
	}

	/**
	 * ������������������������F75(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param gdy
	 *            String ����ѹ
	 * @param gdyhcz
	 *            String ����ѹ�ز�ֵ
	 * @param qdy
	 *            String Ƿ��ѹ
	 * @param qdyhcz
	 *            String Ƿ��ѹ�ز�ֵ
	 * @param dlsx
	 *            String �ܻ����������������
	 * @param dlsxfh
	 *            String �ܻ���������������޷���
	 * @param dlyxhc
	 *            String �ܻ������������Խ�޻ز�ֵ
	 * @param dlyxhcfh
	 *            String �ܻ������������Խ�޻ز�ֵ����
	 * @param dysx
	 *            String �ܻ����ѹ����������
	 * @param dysxfh
	 *            String �ܻ����ѹ���������޷���
	 * @param dyyxhc
	 *            String �ܻ����ѹ������Խ�޻ز�ֵ
	 * @param dyyxhczfh
	 *            String �ܻ����ѹ������Խ�޻ز�ֵ����
	 * 
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDrqbhcs(String txfs, String xzqxm, String zddz,
			String cldh, String gdy, String gdyhcz, String qdy, String qdyhcz,
			String dlsx, String dlsxfh, String dlyxhc, String dlyxhcfh,
			String dysx, String dysxfh, String dyyxhc, String dyyxhcfh)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F75"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.makeFormat07(gdy);
		data += Util.makeFormat07(gdyhcz);
		data += Util.makeFormat07(qdy);
		data += Util.makeFormat07(qdyhcz);
		data += Util.makeFormat05(dlsxfh, dlsx);
		data += Util.makeFormat05(dlyxhcfh, dlyxhc);
		data += Util.makeFormat05(dysxfh, dysx);
		data += Util.makeFormat05(dyyxhcfh, dyyxhc);

		// �塢����
		String mc = "��������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String temps_cs = cldh + "@" + gdy + ";" + gdyhcz + ";" + qdy + ";"
				+ qdyhcz + ";" + dlsx + "#" + dlsxfh + ";" + dlyxhc + "#"
				+ dlyxhcfh + ";" + dysx + "#" + dysxfh + ";" + dyyxhc + "#"
				+ dyyxhcfh;
		writeCsszzcb(seq_sjzfs, "drqbhcs", temps_cs, jdbcT); // ��������������

		return seq_sjzfs;
	}

	/**
	 * ����������������Ͷ�п��Ʒ�ʽF76(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param kzfs
	 *            String ���Ʒ�ʽ:1:���ؿ���;2:Զ��ң��;3:����;4:����
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDrqtqkzfs(String txfs, String xzqxm, String zddz,
			String cldh, String kzfs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F75"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.decStrToHexStr(kzfs, 1);

		// �塢����
		String mc = "������Ͷ�п��Ʒ�ʽ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "drqtqkzfs", cldh + "@" + kzfs, jdbcT); // ������Ͷ�п��Ʒ�ʽ

		return seq_sjzfs;
	}

	/**
	 * �����������ն˱�����ֵ����F17(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param badz
	 *            String ������ֵ��>=1,<=999��
	 * @param xs
	 *            Sting ϵ�������չ�Լ,�磺000=10E4...��
	 * @param zf
	 *            String ������0������1����
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdbadz(String txfs, String xzqxm, String zddz,
			String badz, String xs, String zf) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F17"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data = Util.makeFormat02(badz, xs, zf);

		// �塢����
		String mc = "�ն˱�����ֵ����";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "zdbadz", badz, jdbcT); // �ն˱�����ֵ
		writeCsszzcb(seq_sjzfs, "zdbadzfh", zf, jdbcT); // �ն˱�����ֵ����
		writeCsszzcb(seq_sjzfs, "zdbadzxs", xs, jdbcT); // �ն˱�����ֵϵ��

		return seq_sjzfs;
	}

//	/**
//	 * �����������ն˹���ʱ������F18(AFN=04H)
//	 * 
//	 * @param xzqxm
//	 *            String ����������
//	 * @param zddz
//	 *            String �ն˵�ַ
//	 * @param sd
//	 *            String[][] ʱ��{sd[i][0]:ʱ��(x-y,0-48);
//	 *            sd[i][1]:����״̬(00:�����ƣ�01������1��10������2��11������)}
//	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
//	 * @throws Exception
//	 */
//	public String sendAFN04F18(String txfs, String xzqxm, String zddz,
//			String[][] sd) throws Exception {
//		String seq_sjzfs = null;
//		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//
//		// һ��������
//		String kzm = "4A";
//
//		// ����Ӧ�ù�����
//		String afn = "04"; // ��������
//
//		// �������ݵ�Ԫ��ʶ
//		String xxd = "P0"; // ��Ϣ��
//		String xxl = "F18"; // ��Ϣ��
//
//		// �ġ����ݵ�Ԫ
//		String data = "";
//		String temp_data = "";
//		int sd_len = sd.length;
//		for (int i = 0; i < sd_len; i++) {
//			//ʱ�����
//			String sd_xh = sd[i][0];
//			//ʱ�ζ�ֵ
//			String sd_kzbz = sd[i][1];
//            String[] xh = sd_xh.split("-");
//            int xh_len = Integer.parseInt(xh[1]) - Integer.parseInt(xh[0]);
//            //����ǵ�һ������ʱ��
//            if(0==i){
//            	int len_0=Integer.parseInt(xh[0]);
//            	for (int j = 0; j < len_0; j++) {
//    				temp_data = temp_data + "00";
//    			}
//            	for (int j = 0; j < xh_len; j++) {
//    				temp_data = temp_data + sd_kzbz;
//    			}
//            }else{
//            	//��һ��ʱ�����
//    			String sd_xh_front = sd[i-1][0];
//    			//��һ��ʱ����ŵĽ�ֹʱ�κ�
//                int xh_front1=  Integer.parseInt(sd_xh_front.split("-")[1]);
//                //�����������ʱ��
//                if(Integer.parseInt(xh[0])!=xh_front1){
//                	int len_i=Integer.parseInt(xh[0])-xh_front1;
//                	for (int j = 0; j < len_i; j++) {
//        				temp_data = temp_data + "00";
//        			}
//                }
//                
//                for (int j = 0; j < xh_len; j++) {
//    				temp_data = temp_data + sd_kzbz;
//    			}
//            }
//            if(sd_len-1==i){
//            	int len_e=48-Integer.parseInt(xh[1]);
//            	for (int j = 0; j < len_e; j++) {
//    				temp_data = temp_data + "00";
//    			}
//            }
//            
//			
//			
//		}
////		int data_len = temp_data.length();
////		for (int i = 0; i < 96 - data_len; i++) {
////			temp_data = temp_data+"0";
////		}
////        System.out.println(temp_data);
//		for (int i = 0; i < 12; i++) {
//			String temps = temp_data.substring(i * 8, (i + 1) * 8);
//			temps = Util.convertStr(temps);
//			temps = Util.binStrToHexStr(temps, 1);// һ���ֽ�
//            data = data + temps;// ��λ���ȴ�
//		}
//
//		// �塢����
//		String mc = "�ն˹���ʱ������";
//
//		// ���ù����ӿ�
//		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
//				jdbcT);
//
//		// д"�ն˹���ʱ��"��
//		// ɾ���ɼ�¼
//		String sSql = "delete g_zdgksd where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?)";
//		String[] params = new String[] { xzqxm, zddz };
//		jdbcT.update(sSql, params);
//
//		// �����¼�¼
//		int sdh = 0;// ʱ�κ�
//		for (int i = 0; i < sd_len; i++) {
//			sdh++;
//			String sd_xh = sd[i][0];
//			String sd_kzbz = sd[i][1];
//
//			String[] xh = sd_xh.split("-");
//			String sd_sjd = Util.getSD(xh[0]) + "-" + Util.getSD(xh[1]);
//
//			sSql = "insert into g_zdgksd(zdid,sdh,sjd,kzzt) "
//					+ "values((select zdid from G_ZDGZ where xzqxm=? and zddz=?),?,?,?)";
//			params = new String[] { xzqxm, zddz, String.valueOf(sdh), sd_sjd,
//					sd_kzbz };
//			jdbcT.update(sSql, params);
//		}
//
//		return seq_sjzfs;
//	}

	

	/**
	 * �����������ն˳���������F7(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param day
	 *            String �����գ�32�����ȵĶ������ַ���
	 * @param time
	 *            String ����ʱ�䣨��λ��HHMM��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdcbr(String txfs, String xzqxm, String zddz, String day,
			String time) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F7"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ������
		int iDay = Integer.parseInt(day, 2);
		String sDay = Integer.toHexString(iDay);
		int len_day = sDay.length();
		for (int i = 0; i < 8 - len_day; i++) {
			sDay = "0" + sDay;
		}
		sDay = Util.convertStr(sDay);

		// ����ʱ��

		int len_time = time.length();
		for (int i = 0; i < 4 - len_time; i++) {
			time = "0" + time;
		}
		String temp_time = Util.convertStr(time);

		data = sDay + temp_time;

		// �塢����
		String mc = "�ն˳���������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "zdcbrq", day, jdbcT); // �ն˳�������
		writeCsszzcb(seq_sjzfs, "zdcbsj", time, jdbcT); // �ն˳���ʱ��

		return seq_sjzfs;
	}

	/**
	 * �����������ն��¼���¼��������F8(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sjjlyxbz
	 *            String �¼���¼��Ч��־��64�����ȵĶ������ַ�,�ɸߵ��ͣ�
	 * @param sjzyxdjbz
	 *            String �¼���Ҫ�Եȼ���־��64�����ȵĶ������ַ�,�ɸߵ��ͣ�
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdsjjl(String txfs, String xzqxm, String zddz,
			String sjjlyxbz, String sjzyxdjbz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F8"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// �¼���¼��Ч��־
		int len_yxbz = sjjlyxbz.length();
		for (int i = 0; i < 64 - len_yxbz; i++) {
			sjjlyxbz = "0" + sjjlyxbz;
		}
		String temp_sjjlyxbz = "";
		for (int i = 0; i < 8; i++) {
			String temps = sjjlyxbz.substring(i * 8, (i + 1) * 8);
			temps = Util.binStrToHexStr(temps, 1);

			temp_sjjlyxbz += temps;
		}

		// �¼���Ҫ�Եȼ���־
		int len_zyxdj = sjzyxdjbz.length();
		for (int i = 0; i < 64 - len_zyxdj; i++) {
			sjzyxdjbz = "0" + sjzyxdjbz;
		}
		String temp_sjzyxdjbz = "";
		for (int i = 0; i < 8; i++) {
			String temps = sjzyxdjbz.substring(i * 8, (i + 1) * 8);
			temps = Util.binStrToHexStr(temps, 1);

			temp_sjzyxdjbz += temps;
		}
		data = Util.convertStr(temp_sjjlyxbz) + Util.convertStr(temp_sjzyxdjbz);

		// �塢����
		String mc = "�ն��¼���¼��������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "sjjlyxbz", sjjlyxbz, jdbcT); // �¼���¼��Ч��־
		writeCsszzcb(seq_sjzfs, "sjzyxdjbz", sjzyxdjbz, jdbcT); // �¼���Ҫ�Եȼ���־

		return seq_sjzfs;
	}

	/**
	 * ���������������ֻ���������F8(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;cs3) 
	 *            cs1:�������ĺ���
	 *            cs2:���ڳص�UIM����
	 *            cs3:��cc1,cc2,cc3,.....ccn��
	 *                  cc1:�ռ���1��UIM����,
	 *                  cc2:�ռ���2��UIM����,
	 *                  ........
	 *                  ccn:�ռ���n��UIM����
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F8(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F8"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		
		// �ռ��ظ���
		String cs1 = ss_csz[2];
		//�ռ��ص�UIM����
		String[] sjcs=cs1.split(",");
		data += Util.decStrToHexStr(sjcs.length, 1);
		
		// �������ĺ���
		String cs2 = ss_csz[0];
		if (cs2 == null || cs2.equals("")) {
			for (int i = 0; i < 16; i++) {
				data += "F";
			}
		} else {
			data += cs2;
			for (int i = 0; i < 16 - cs2.length(); i++) {
				data += "F";
			}
		}
		
		// ���ڳ�UIM����
		String cs3 = ss_csz[1];
		if (cs3 == null || cs3.equals("")) {
			for (int i = 0; i < 16; i++) {
				data += "F";
			}
		} else {
			data += cs3;
			for (int i = 0; i < 16 - cs3.length(); i++) {
				data += "F";
			}
		}
		
		for(int n=0;n<sjcs.length;n++){
			// �ռ��ء�n����UIM����
			String sjcuim_n = sjcs[n];
			if (sjcuim_n == null || sjcuim_n.equals("")) {
				for (int i = 0; i < 16; i++) {
					data += "F";
				}
			} else {
				data += sjcuim_n;
				for (int i = 0; i < 16 - sjcuim_n.length(); i++) {
					data += "F";
				}
			}
		}

		

		// �塢����
		String mc = "[AFN04F8]�����ֻ���������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F8", csz, jdbcT);

		return seq_sjzfs;
	}

	/**����������ˮ��ˮλ���Ʋ���������ռ��������նˣ�F9(AFN=04H)
	   * @param 	xzqxm 	String ����������
	   * @param 	zddz  	String �ն˵�ַ
	   * @param 	csz  	String ����ֵ(cs1;cs2;cs3;cs4)
	   * 				   cs1:�����л�����1-ʹ��1��ˮ��2-ʹ��2��ˮ��3- 1��2�Ż�Ϊ��
						   cs2:�����л�ʱ��  һ���ֽ� Сʱ
						   cs3:����ʱ�� �����ֽ� ����
						   cs4:ֹͣʱ��   �����ֽ� ����';
	   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	   */
	public String sendAFN04F9(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F9"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		// �����л�����
		String cs1 = ss_csz[0];
		data += Util.decStrToHexStr(cs1, 1);
		
		

		//�����л�ʱ��
		if(null==ss_csz[1]||ss_csz[1].length()<=0){
			data+="CC";
		}else if("EE".equalsIgnoreCase(ss_csz[1])||"CC".equalsIgnoreCase(ss_csz[1])){
			data+=ss_csz[1];
		}else{
		    data+=Util.decStrToHexStr(ss_csz[1], 1);
		}
		
		//����ʱ��
		if(null==ss_csz[2]||ss_csz[2].length()<=0){
			data+="CCCC";
		}else if("EEEE".equalsIgnoreCase(ss_csz[2])||"CCCC".equalsIgnoreCase(ss_csz[2])){
			data+=ss_csz[2];
		}else{
		    data+=Util.convertStr(Util.decStrToHexStr(ss_csz[2], 2));
		}
		
		//ֹͣʱ��
		if(null==ss_csz[3]||ss_csz[3].length()<=0){
			data+="CCCC";
		}else if("EEEE".equalsIgnoreCase(ss_csz[3])||"CCCC".equalsIgnoreCase(ss_csz[3])){
			data+=ss_csz[3];
		}else{
		    data+=Util.convertStr(Util.decStrToHexStr(ss_csz[3], 2));
		}

		// �塢����
		String mc = "ˮ��ˮλ���Ʋ���������ռ��������նˣ�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F9", csz, jdbcT);


        return seq_sjzfs;
	}

	/**
	 * �����������ն�1/2��������������F65/F66(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rwh
	 *            String �����
	 * @param fszq
	 *            String ��������
	 * @param zqdw
	 *            String ���ڵ�λ(00���֣�01��ʱ��10���գ�11����)
	 * @param fsjzsj
	 *            String ���ͻ�׼ʱ��(������ʱ����)
	 * @param cqbl
	 *            String ��ȡ����
	 * @param rwsjx
	 *            String[][] ����������(String[i][0]:��Ϣ��Pn;String[i][1]:��Ϣ��Fn)
	 * @param rwlx
	 *            String ��������(1:1����������;2:2����������)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdrw(String txfs, String xzqxm, String zddz, String rwh,
			String fszq, String zqdw, String fsjzsj, String cqbl,
			String[][] rwsjx, String rwlx) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + rwh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (rwlx.equals("1")) {
			xxl = "F65";
		} else {
			xxl = "F66";
		}
		// �ġ����ݵ�Ԫ
		String data = "";
		int len_rwsjx = rwsjx.length;
		// �������ڼ����ڵ�λ
		String sZq = "";
		String temp_fszq = Util.decStrToBinStr(fszq, 1);
		sZq = zqdw + temp_fszq.substring(2, 8);
		sZq = Util.binStrToHexStr(sZq, 1);

		// ���ͻ�׼ʱ��
		String sJzsj = "";
		sJzsj = Util.convertStr(fsjzsj);

		// ��ȡ����
		String sCqbl = "";
		sCqbl = Util.decStrToHexStr(cqbl, 1);

		// ���ݵ�Ԫ��ʶ����
		String sDybsgs = Util.intToHexStr(len_rwsjx, 1);

		data = sZq + sJzsj + sCqbl + sDybsgs;

		// ���ݵ�Ԫ��ʶ
		for (int i = 0; i < len_rwsjx; i++) {
			// <--------------ÿ�����ݵ�Ԫ��ʶ��-------------->
			String temp_da = rwsjx[i][0];// ��Ϣ��DA
			String temp_dt = rwsjx[i][1];// ��Ϣ��DT
			temp_da = Util.getDA(temp_da);
			temp_dt = Util.getDT(temp_dt);

			data += Util.convertStr(temp_da) + Util.convertStr(temp_dt);
		}

		// �塢����
		String mc = "�ն�" + rwlx + "��������������(" + rwh + "������)";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		String sSql = "";

		// �����ն��������ñ���Ӧ�����Ƿ��Ѵ���
		sSql = "select * from zdrwpzb " + "where xzqxm='" + xzqxm
				+ "' and zddz='" + zddz + "' " + "and rwlx='" + rwlx
				+ "' and rwh='" + rwh + "'";
		int count = Util.getRecordCount(sSql, jdbcT);

		// д���ն��������ñ�
		String[] params = null;
		if (count > 0) {
			// update
			sSql = "update zdrwpzb " + "set fszq=?,zqdw=?,fsjzsj=?"
					+ ",cqbl=?,qybz='AA',sjzfsseq=? "
					+ "where xzqxm=? and zddz=? " + "and rwlx=? and rwh=?";
			params = new String[] { fszq, zqdw, fsjzsj, cqbl, seq_sjzfs, xzqxm,
					zddz, rwlx, rwh };
			jdbcT.update(sSql, params);

		} else {
			// insert
			sSql = "insert into zdrwpzb(xzqxm,zddz,rwlx,rwh,fszq,zqdw,fsjzsj,cqbl,qybz,sjzfsseq) "
					+ "values(?,?,?,?,?,?,?,?,'AA',?)";
			params = new String[] { xzqxm, zddz, rwlx, rwh, fszq, zqdw, fsjzsj,
					cqbl, seq_sjzfs };
			jdbcT.update(sSql, params);

		}

		// д��������Ϣ���
		sSql = "delete rwxxx " + "where xzqxm=? and zddz=? "
				+ "and rwlx=? and rwh=?";
		params = new String[] { xzqxm, zddz, rwlx, rwh };
		jdbcT.update(sSql, params);

		for (int i = 0; i < len_rwsjx; i++) {
			String temp_xh = String.valueOf(i + 1);
			String temp_da = rwsjx[i][0];// ��Ϣ��DA
			String temp_dt = rwsjx[i][1];// ��Ϣ��DT
			sSql = "insert into rwxxx(xzqxm,zddz,rwlx,rwh,xxdh,xxxdm,xxxlb,xh) "
					+ "values(?,?,?,?,?,?,?,?)";
			params = new String[] { xzqxm, zddz, rwlx, rwh, temp_da, temp_dt,
					rwlx, temp_xh };
			jdbcT.update(sSql, params);
		}

		return seq_sjzfs;
	}

	/**
	 * �����������ն˵��ܱ�/��������װ�ò�������F10(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param dnbxx
	 *            ArrayList �ն�������ܱ���Ϣ�����ܱ���š����������㡢�˿ںš���Լ���͡�
	 *            ͨѶ��ַ��ͨѶ���롢���ʸ���������λ����[4-7]��С��λ����[1-4]��(��Ϊstring��)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZddnbpz(String txfs, String xzqxm, String zddz,
			ArrayList dnbxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F10"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		int dnbNum = dnbxx.size();
		String sDnbnum = Integer.toHexString(dnbNum);
		if (sDnbnum.length() < 2) {
			sDnbnum = "0" + sDnbnum;
		}
		data = sDnbnum;

		for (int i = 0; i < dnbNum; i++) {
			Map hm = (Map) dnbxx.get(i);
			// ���ܱ����
			String xh = String.valueOf(hm.get("xh"));
			xh = Integer.toHexString(Integer.parseInt(xh));
			if (xh.length() < 2) {
				xh = "0" + xh;
			}

			// ���ܱ������������
			String cldh = String.valueOf(hm.get("cldh"));
			cldh = Integer.toHexString(Integer.parseInt(cldh));
			if (cldh.length() < 2) {
				cldh = "0" + cldh;
			}

			// ���ܱ������˿ں�
			String dkh = String.valueOf(hm.get("dkh"));
			dkh = Integer.toHexString(Integer.parseInt(dkh));
			if (dkh.length() < 2) {
				dkh = "0" + dkh;
			}

			// ���ܱ�������Լ��ź�
			String gybh = String.valueOf(hm.get("gybh"));
			gybh = Integer.toHexString(Integer.parseInt(gybh));
			if (gybh.length() < 2) {
				gybh = "0" + gybh;
			}

			// ���ܱ�ͨѶ��ַ
			String txdz = String.valueOf(hm.get("txdz"));
			int tempi = 12 - txdz.length();
			for (int j = 0; j < tempi; j++) {
				txdz = "0" + txdz;
			}
			txdz = Util.convertStr(txdz);

			// ���ܱ�ͨѶ����
			String txmm = String.valueOf(hm.get("txmm"));
			txmm = Integer.toHexString(Integer.parseInt(txmm));
			tempi = 12 - txmm.length();
			for (int j = 0; j < tempi; j++) {
				txmm = "0" + txmm;
			}
			txmm = Util.convertStr(txmm);

			// ���ʸ���
			String flgs = String.valueOf(hm.get("flgs"));
			flgs = Integer.toHexString(Integer.parseInt(flgs));
			// ����λ����
			String zswgs = String.valueOf(hm.get("zswgs"));
			zswgs = Integer.toBinaryString(Integer.parseInt(zswgs) - 4);
			if (zswgs.length() < 2) {
				zswgs = "0" + zswgs;
			}

			// С��λ����
			String xswgs = String.valueOf(hm.get("xswgs"));
			xswgs = Integer.toBinaryString(Integer.parseInt(xswgs) - 1);
			if (xswgs.length() < 2) {
				xswgs = "0" + xswgs;
			}

			int i_zsxs = Integer.parseInt(zswgs + xswgs, 2);
			String flzsgs = flgs + Integer.toHexString(i_zsxs);

			data = data + xh + cldh + dkh + gybh + txdz + txmm + flzsgs;
		}

		// �塢����
		String mc = "�����ն˵��ܱ��������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˲ɼ��豸��������F10(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N���������ID
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F10(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F10"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
//		String cldIn = "";
		int dnbNum = ss_csz.length;// �ɼ��豸����
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		
//		s_sql = "select cldh from g_zdcldpzb where zdid=?";
//		params = new String[] { zdid };
//		List dnbList = jdbcT.queryForList(s_sql, params);
		
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// ÿ�����õĲ������ID
			String cldid = ss_csz[i];
			s_sql="select * from g_zdcldpzb where id=?";
			params = new String[] { cldid };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// �豸���
			String pz1 = String.valueOf(cldMap.get("xh"));// �豸���(1�ֽ�)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// �ɼ��豸�����������
			String pz2 = String.valueOf(cldMap.get("cldh"));;// �ɼ��豸�����������(2�ֽ�)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// �豸����
			String pz3 = String.valueOf(cldMap.get("cldlx"));// �豸����
			String data3 = Util.decStrToHexStr(pz3, 1);
			
			// ����Լ���ͱ��
			String pz4 = String.valueOf(cldMap.get("gylx"));// ��Լ����(1�ֽ�)
			String data4 = Util.decStrToHexStr(pz4, 1);

			// ͨ������
			String pz5 = String.valueOf(cldMap.get("txsl"));// ͨ������
			String data5 = Util.decStrToHexStr(pz5, 1);
			
			// ���ܱ�ͨ�ŵ�ַ
			String pz6 = String.valueOf(cldMap.get("txdz"));// ͨ�ŵ�ַ(6�ֽڣ���ʽ12)
			String data6 = Util.add(pz6, 6, "0");
			data5 = Util.convertStr(data5);

			

			data += data1 + data2 + data3 + data4 + data5 + data6;

//			if (pz2.equals("0")) {
//				// ɾ����Ӧ�Ĳ�����
//				continue;
//			}

//			boolean isIn = false;
//			for (int j = 0; j < dnbList.size(); j++) {
//				Map tempHM = (Map) dnbList.get(j);
//				if (String.valueOf(tempHM.get("cldh")).equals(pz2)) {
//					isIn = true;
//					break;
//				}
//			}

//			if (isIn == false) {
//				// �������������ñ�
//				String seq=Util.getSequences("SEQ_CLDID", jdbcT);
//				s_sql = "insert into g_zdcldpzb(id,zdid,cldh,xh,cldlx,gylx,txsl,txdz) "
//						+ "values(?,?,?,?,?,?,?,?)";
//				
//				params = new String[] {seq,zdid, pz2, pz1, pz3, pz4, pz5,pz6};
//				jdbcT.update(s_sql, params);
//				
//				//���������㵱ǰ���ݱ�
//				String s_sql_clddysj="insert into G_ZDCLDDQSJB(cldid) values(?)";
//				String params2[]=new String[]{seq};
//				jdbcT.update(s_sql_clddysj,params2);
//			} else {
//				//���²��������ñ�
//				s_sql = "update g_zdcldpzb set xh=?,cldlx=?,gylx=?,txsl=?,txdz=? where zdid=? and cldh=?";
//				params = new String[] { pz1, pz3, pz4, pz5,pz6, zdid, pz2 };
//				jdbcT.update(s_sql, params);
//			}
			

//			cldIn += "'" + pz2 + "',";
		}
		
		//�����ն˲����㵱ǰ���ݱ��е���Ч��¼
		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		if (dnbNum > 0) {
//			s_sql += " and cldh not in("
//					+ cldIn.substring(0, cldIn.length() - 1) + "))";
//		}
//		params = new String[] { zdid };
		jdbcT.update(s_sql, params);
		

		//ɾ�������ɼ��豸����
//		s_sql = "delete g_zdcldpzb  where zdid=?  ";
//		if (dnbNum > 0) {
//			s_sql += "and cldh not in("
//					+ cldIn.substring(0, cldIn.length() - 1) + ")";
//		}
//		params = new String[] { zdid };
//		jdbcT.update(s_sql, params);

		// �塢����
		String mc = "�ն˲ɼ��豸��������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F10", csz, jdbcT); // �ն˵��ܱ�/��������װ�ò�������

		return seq_sjzfs;
	}

	/**
	 * �����������ն������������F11(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N���������� csn:��������(pz1#pz2#pz3#pz4)
	 *            pz1:����˿ں� pz2:����������(1-64) pz3:��������(0~3���α�ʾ�����й��������޹��������й��������޹�)
	 *            pz4:�����
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F11(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F11"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		int mcNum = ss_csz.length;
		data = Util.decStrToHexStr(mcNum, 1); // ����·��
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		String cldIn = "";

		// ��"�ն˲��������ñ�"ȡ����������Ϣ(cldlx=02)
		String cldlx = "02";// ����������(02:����)
		s_sql = "select cldh from g_zdcldpzb where zdid=? ";
		params = new String[] { zdid };
		List mcList = jdbcT.queryForList(s_sql, params);

		for (int i = 0; i < mcNum; i++) {
			String csn = ss_csz[i];
			String[] ss_pz = csn.split("#");

			// ��������˿ں�
			String pz1 = ss_pz[0];
			String temp_pz1 = Util.decStrToHexStr(pz1, 1);

			// �����������
			String pz2 = ss_pz[1];
			String temp_pz2 = Util.decStrToHexStr(pz2, 1);

			// ��������
			String pz3 = ss_pz[2];
			String temp_pz3 = Util.decStrToHexStr(pz3, 1);

			// �����
			String pz4 = ss_pz[3];
			String temp_pz4 = Util.decStrToHexStr(pz4, 2);
			temp_pz4 = Util.convertStr(temp_pz4);

			data = data + temp_pz1 + temp_pz2 + temp_pz3 + temp_pz4;

			if (pz2.equals("0")) {
				// ɾ����Ӧ�Ĳ�����
				continue;
			}

			// д�ն˲�����������ñ�
			boolean isIn = false;
			for (int j = 0; j < mcList.size(); j++) {
				Map tempHM = (Map) mcList.get(j);
				if (String.valueOf(tempHM.get("cldh")).equals(pz2)) {
					isIn = true;
					break;
				}
			}

			if (isIn == false) {
				// ����
				s_sql = "insert into g_zdcldpzb(cldlx,zdid,dkh,cldh,mcsx,mccs) "
						+ "values(?,?,?,?,?,?)";
				params = new String[] { cldlx, zdid, pz1, pz2, pz3, pz4 };
			} else {
				// ����
				s_sql = "update g_zdcldpzb set cldlx=?,dkh=?,mcsx=?,mccs=? "
						+ " where zdid=? and cldh=?";
				params = new String[] { cldlx, pz1, pz3, pz4, zdid, pz2 };
			}
			jdbcT.update(s_sql, params);

			cldIn += "'" + pz2 + "',";
		}

		// ɾ������������
		s_sql = "delete g_zdcldpzb " + "where zdid=? and cldlx=?";
		if (mcNum > 0) {
			s_sql += " and cldh not in("
					+ cldIn.substring(0, cldIn.length() - 1) + ")";
		}
		params = new String[] { zdid, cldlx };
		jdbcT.update(s_sql, params);

		// �塢����
		String mc = "[AFN04F11]�����ն������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F11", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������������������豸װ�����ò���F12(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N�����õ�ID	
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F12(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F12"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// �ɼ��豸����
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
//		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// ÿ������ID
			String cs_id = ss_csz[i];
			s_sql="select * from g_zdkglsrsbpzb where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// �豸���
			String pz1 = String.valueOf(cldMap.get("xh"));// �豸���(1�ֽ�)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// �ɼ��豸�����������
			String pz2 = String.valueOf(cldMap.get("cldh"));;// �ɼ��豸�����������(2�ֽ�)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// �豸����
			String pz3 = String.valueOf(cldMap.get("sblx"));// �豸����
			String data3 = Util.decStrToHexStr(pz3, 1);
			
			// Ӳ������ӿں�
			String pz4 = String.valueOf(cldMap.get("yjsrjkh"));// Ӳ������ӿں�
			String data4 = Util.decStrToHexStr(pz4, 1);

			// �豸���������
			String pz5 = String.valueOf(cldMap.get("sbcszxh"));// �豸���������
			String data5 = Util.decStrToHexStr(pz5, 1);
			
			// �豸���������
			String pz6 = String.valueOf(cldMap.get("sbznbh"));// �豸���������
			String data6 = Util.decStrToHexStr(pz6, 1);

			

			data += data1 + data2 + data3 + data4 + data5 + data6;
         }
		
		//ɾ���ն˲����㵱ǰ���ݱ��е���Ч��¼
//		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		jdbcT.update(s_sql, params);
		



		// �塢����
		String mc = "�������������豸װ�����ò���F12";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F12", csz, jdbcT); // �ն˵��ܱ�/��������װ�ò�������

		return seq_sjzfs;
	}

	/**
	 * ���������������豸���Ƶ����F13(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N�����õ�ID
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F13(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F13"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// �ɼ��豸����
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
//		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// ÿ������ID
			String cs_id = ss_csz[i];
			s_sql="select * from g_zddqsbpzb where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// �豸���
			String pz1 = String.valueOf(cldMap.get("xh"));// �豸���(1�ֽ�)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// �ɼ��豸�����������
			String pz2 = String.valueOf(cldMap.get("cldh"));;// �ɼ��豸�����������(2�ֽ�)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// �豸����
			String pz3 = String.valueOf(cldMap.get("sblx"));// �豸����
			String data3 = Util.decStrToHexStr(pz3, 1);
			
			//�豸�����
			String pz4 = String.valueOf(cldMap.get("edgl"));; 
			String data4 = Util.convertStr(Util.decStrToHexStr(pz4, 2));
			
			// ��������
			String pz5 = String.valueOf(cldMap.get("clnl"));
			String data5 = Util.decStrToHexStr(pz5, 1);
			
			// ���߷�ʽ
			String pz6 = String.valueOf(cldMap.get("jxfs"));
			String data6 = Util.decStrToHexStr(pz6, 1);
			
			// Ӳ������ӿں�
			String pz7 = String.valueOf(cldMap.get("yjscjkh"));
			String data7 = Util.decStrToHexStr(pz7, 1);
			
			// ��������ӿں�
			
			String pz8 = String.valueOf(cldMap.get("fzcdjkh"));
			String data8="";
            if("CC".equalsIgnoreCase(pz8)){
            	data8= "CC";
			}else{
				data8 = Util.decStrToHexStr(pz8, 1);
			}

			// ���ϵ�Ӳ���ӿں�
			String pz9 = String.valueOf(cldMap.get("gzdyjjkh"));
			String data9="";
            if("CC".equalsIgnoreCase(pz9)){
            	data9= "CC";
			}else{
				data9 = Util.decStrToHexStr(pz9, 1);
			}
			
			// ��Ƶ�����Ƶ���
			String pz10 = String.valueOf(cldMap.get("sfbp"));
			String data10 = pz10;
			
			

			

			data += data1 + data2 + data3 + data4 + data5 + data6+ data7+ data8+ data9+ data10;
         }
		
		//ɾ���ն˲����㵱ǰ���ݱ��е���Ч��¼
//		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		jdbcT.update(s_sql, params);
		



		// �塢����
		String mc = "�����豸���Ƶ����F12";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F13", csz, jdbcT); //�����豸���Ƶ����

		return seq_sjzfs;
	}

	/**
	 * ���������������豸��ͣ���Ʋ���F14(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������           
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N�������豸��ͣ���Ʋ�������
	 *            csn:�ܼ�������(pz1,...,pz7)--N�����豸��ͣ���Ʋ������� 
	 *                pz1:��n�׿��Ʋ���ִ������
	 *                pz2:��n�׿��Ʋ�����С�¶�   EE��Ч
	 *                pz3:��n�׿��Ʋ�������¶�   EE��Ч
	 *                pz4:��n�׿��Ʋ�����Ч��ʼ����   EEEE��Ч
	 *                pz5:��n�׿��Ʋ�����Ч��ֹ����   EEEE��Ч
	 *                pz6:��n�׿��Ʋ�������ʱ��
	 *                pz7:��n�׿��Ʋ���ֹͣʱ��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F14(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception {

		String seq_sjzfs = null;
//		String s_sql = "";
//		String[] params = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P"+cldh; // ��Ϣ��
		String xxl = "F14"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		String[] ss_csz = csz.split(";");
		//���Ʋ�����������
		data+=Util.decStrToHexStr(ss_csz.length, 1);
		
		for(int i=0;i<ss_csz.length;i++){
			String csn[]=ss_csz[i].split(",");
			
			//��n�׿��Ʋ���ִ������
			String pz1=csn[0];
			if("1".equals(pz1)){
				//��n�׿��Ʋ���ִ������
				data+=Util.decStrToHexStr(pz1, 1);
				//��n�׿��Ʋ�����С�¶�
				String pz2=csn[1];
				String fh="0";
				if(Integer.parseInt(pz2)<0){
					fh="1";
				}
				data+=Util.makeFormat05( fh,String.valueOf(Math.abs(Integer.parseInt(pz2))));
				//��n�׿��Ʋ�������¶�
				String pz3=csn[2];
				fh="0";
				if(Integer.parseInt(pz3)<0){
					fh="1";
				}
				data+=Util.makeFormat05(fh, String.valueOf(Math.abs(Integer.parseInt(pz3))));
				
				// pz4:��n�׿��Ʋ�����Ч��ʼ����
				data+="EEEE";
				//pz5:��n�׿��Ʋ�����Ч��ֹ����
				data+="EEEE";
				
				
				//pz6:��n�׿��Ʋ�������ʱ��
				String pz6=csn[5];
				data+=Util.decStrToHexStr(pz6, 1);
				//pz7:��n�׿��Ʋ���ֹͣʱ��
				String pz7=csn[6];
				data+=Util.decStrToHexStr(pz7, 1);
				
			}else if("2".equalsIgnoreCase(pz1)){
				//��n�׿��Ʋ���ִ������
				data+=Util.decStrToHexStr(pz1, 1);
				
				//��n�׿��Ʋ�����С�¶�
				data+="EE";
				//��n�׿��Ʋ�������¶�
				data+="EE";
				
				// pz4:��n�׿��Ʋ�����Ч��ʼ����
				String pz4=csn[3].replace("-", "");
				data+=Util.makeFormat29(pz4);
				//pz5:��n�׿��Ʋ�����Ч��ֹ����
				String pz5=csn[4].replace("-", "");
				data+=Util.makeFormat29(pz5);
				
				
				//pz6:��n�׿��Ʋ�������ʱ��
				String pz6=csn[5];
				data+=Util.decStrToHexStr(pz6, 1);
				//pz7:��n�׿��Ʋ���ֹͣʱ��
				String pz7=csn[6];
				data+=Util.decStrToHexStr(pz7, 1);
			}else{
				//��n�׿��Ʋ���ִ������
				data+=Util.decStrToHexStr(pz1, 1);
				
				//��n�׿��Ʋ�����С�¶�
				data+="EE";
				//��n�׿��Ʋ�������¶�
				data+="EE";
				
				// pz4:��n�׿��Ʋ�����Ч��ʼ����
				data+="EEEE";
				//pz5:��n�׿��Ʋ�����Ч��ֹ����
				data+="EEEE";
				
				
				//pz6:��n�׿��Ʋ�������ʱ��
				String pz6=csn[5];
				data+=Util.decStrToHexStr(pz6, 1);
				//pz7:��n�׿��Ʋ���ֹͣʱ��
				String pz7=csn[6];
				data+=Util.decStrToHexStr(pz7, 1);
			}
			
			
		}

		
		// �塢����
		String mc = "[AFN04F14]�����豸��ͣ���Ʋ�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F14",  cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	
	/**
	 * ����������ˮ��ˮλ���Ʋ���F15(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������           
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N��ˮ��ˮλ���Ʋ�������
	 *            csn:���Ʋ�������(pz1,...,pz14)--��Nˮ��ˮλ���Ʋ������� 
	 *                pz1:��n�׿��Ʋ���-���Ӳ����
	 *                pz2:��n�׿��Ʋ���-�������Ӳ����
	 *                pz3:��n�׿��Ʋ���-ͬʱ����ʹ��
	 *                pz4:��n�׿��Ʋ���-�����л�ʱ��
	 *                pz5:��n�׿��Ʋ���-�������
	 *                pz6:��n�׿��Ʋ���-ˮλ��λ
	 *                pz7:��n�׿��Ʋ���-�߼���ϵ
	 *                pz8:��n�׿��Ʋ���-�������(��һ������)
	 *                pz9:��n�׿��Ʋ���-ˮλ��λ(��һ������)
	 *                pz10:��n�׿��Ʋ���-���ƶ���
	 *                pz11:��n�׿��Ʋ���-��С�¶�
	 *                pz12:��n�׿��Ʋ���-����¶�
	 *                pz13:��n�׿��Ʋ���-����ʱ��
	 *                pz14:��n�׿��Ʋ���-ֹͣʱ��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F15(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P"+cldh; // ��Ϣ��
		String xxl = "F15"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		String[] ss_csz = csz.split(";");
		
		//�豸��������n
		data+=Util.decStrToHexStr(ss_csz.length, 1);
		
		for(int i=0;i<ss_csz.length;i++){
			
			String csn[]=ss_csz[i].split(",");
			
			//�豸���
			data+=Util.decStrToHexStr(csn[0], 1);
			
			//�豸����������ţ���ţ�
			data+=Util.convertStr(Util.decStrToHexStr(csn[1], 2));
			
			//���Ӳ����
			if("EE".equalsIgnoreCase(csn[2])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[2])||"CC".equalsIgnoreCase(csn[2])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[2], 1);
			}
			
			//�������Ӳ����
			if("EE".equalsIgnoreCase(csn[3])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[3])||"CC".equalsIgnoreCase(csn[3])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[3], 1);
			}
			
			//ͬʱ����ʹ��
			data+=csn[4];
			
			//�����л�ʱ��
			if(null==csn[5]||csn[5].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[5])||"CC".equalsIgnoreCase(csn[5])){
				data+=csn[5];
			}else{
			    data+=Util.decStrToHexStr(csn[5], 1);
			}
			
			//�������
			if("EE".equalsIgnoreCase(csn[6])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[6], 1);
			}
			
			//ˮλ��λ
			if("EE".equalsIgnoreCase(csn[7])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[7], 1);
			}
			
			//�߼���ϵ
			if("EE".equalsIgnoreCase(csn[8])||"CC".equalsIgnoreCase(csn[8])){
				data+=csn[8];
			}else{
			    data+=Util.decStrToHexStr(csn[8], 1);
			}
			
			//�������(��һ������)
			if("EE".equalsIgnoreCase(csn[9])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[9], 1);
			}
			
			//ˮλ��λ(��һ������)
			if("EE".equalsIgnoreCase(csn[10])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[10], 1);
			}
			
			//���ƶ���
			if("EE".equalsIgnoreCase(csn[11])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[11], 1);
			}
			
			//��С�¶�
			if(null==csn[12]||csn[12].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[12])||"CC".equalsIgnoreCase(csn[12])){
				data+=csn[12];
			}else{
				if(Integer.parseInt(csn[12])>0){
					 data+=Util.makeFormatTemperture(0, Integer.parseInt(csn[12]));
				}else{
					 data+=Util.makeFormatTemperture(1, Math.abs(Integer.parseInt(csn[12])));
				}
			}
			
			//����¶�
			if(null==csn[13]||csn[13].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[13])||"CC".equalsIgnoreCase(csn[13])){
				data+=csn[13];
			}else{
				if(Integer.parseInt(csn[13])>0){
					 data+=Util.makeFormatTemperture(0, Integer.parseInt(csn[13]));
				}else{
					 data+=Util.makeFormatTemperture(1, Math.abs(Integer.parseInt(csn[13])));
				}
			}
			
			//����ʱ��
			if(null==csn[14]||csn[14].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[14])||"CCCC".equalsIgnoreCase(csn[14])){
				data+=csn[14];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[14], 2));
			}
			
			//ֹͣʱ��
			if(null==csn[15]||csn[15].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[15])||"CCCC".equalsIgnoreCase(csn[15])){
				data+=csn[15];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[15], 2));
			}
		}

		
		// �塢����
		String mc = "[AFN04F15]ˮ��ˮλ���Ʋ���";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F15", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ���������������ŷ����Ʋ���F16(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������           
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N��������Ʋ�������
	 *            csn:���Ʋ�������(pz1,...,pz10)--��N̨������Ʋ������� 
	 *                pz1:��n�׿��Ʋ���-���Ӳ����
	 *                pz2:��n�׿��Ʋ���-�������Ӳ����
	 *                pz3:��n�׿��Ʋ���-�����л�ʱ��
	 *                pz4:��n�׿��Ʋ���-��ˮ������ʹ��
	 *                pz5:��n�׿��Ʋ���-��С�¶�
	 *                pz6:��n�׿��Ʋ���-����¶�
	 *                pz7:��n�׿��Ʋ���-����ʱ��
	 *                pz8:��n�׿��Ʋ���-ֹͣʱ��
	 *                pz9:��n�׿��Ʋ���-Ƶ��
	 *                pz10:��n�׿��Ʋ���-���ƶ���
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F16(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P"+cldh; // ��Ϣ��
		String xxl = "F16"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		String[] ss_csz = csz.split(";");
		
		//�豸��������n
		data+=Util.decStrToHexStr(ss_csz.length, 1);
		
		for(int i=0;i<ss_csz.length;i++){
			String csn[]=ss_csz[i].split(",");
			
			//�豸���
			data+=Util.decStrToHexStr(csn[0], 1);
			
			//�豸����������ţ���ţ�
			data+=Util.convertStr(Util.decStrToHexStr(csn[1], 2));
			
			//���Ӳ����
			if("EE".equalsIgnoreCase(csn[2])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[2])||"CC".equalsIgnoreCase(csn[2])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[2], 1);
			}
			
			//�������Ӳ����
			if("EE".equalsIgnoreCase(csn[3])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[3])||"CC".equalsIgnoreCase(csn[3])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[3], 1);
			}
			
			
			//�����л�ʱ��
			if(null==csn[4]||csn[4].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[4])||"CC".equalsIgnoreCase(csn[4])){
				data+=csn[4];
			}else{
			    data+=Util.decStrToHexStr(csn[4], 1);
			}
			
			//��ˮ������ʹ��
			data+=csn[5];
			
			//��С�¶�
			if(null==csn[6]||csn[6].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[6])||"CC".equalsIgnoreCase(csn[6])){
				data+=csn[6];
			}else{
				if(Integer.parseInt(csn[6])>0){
					 data+=Util.makeFormatTemperture(0, Integer.parseInt(csn[6]));
				}else{
					 data+=Util.makeFormatTemperture(1, Math.abs(Integer.parseInt(csn[6])));
				}
			}
			
			//����¶�
			if(null==csn[7]||csn[7].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[7])||"CC".equalsIgnoreCase(csn[7])){
				data+=csn[7];
			}else{
				if(Integer.parseInt(csn[7])>0){
					 data+=Util.makeFormatTemperture(0, Integer.parseInt(csn[7]));
				}else{
					 data+=Util.makeFormatTemperture(1, Math.abs(Integer.parseInt(csn[7])));
				}
			}
			
			//����ʱ��
			if(null==csn[8]||csn[8].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[8])||"CCCC".equalsIgnoreCase(csn[8])){
				data+=csn[8];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[8], 2));
			}
			
			//ֹͣʱ��
			if(null==csn[9]||csn[9].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[9])||"CCCC".equalsIgnoreCase(csn[9])){
				data+=csn[9];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[9], 2));
			}
			
			//Ƶ��
			if(null==csn[10]||csn[10].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[10])||"CC".equalsIgnoreCase(csn[10])){
				data+=csn[10];
			}else{
			    data+=Util.decStrToHexStr(csn[10], 1);
			}
			
			//���ƶ���
			if("EE".equalsIgnoreCase(csn[11])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[11], 1);
			}
		}

		
		// �塢����
		String mc = "[AFN04F16]�����ŷ����Ʋ���";

		//���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F16", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	
	/**
	 * ����������F17��ORP,HP ����������(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N�����õ�ID
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F17(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F17"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// �ɼ��豸����
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// ÿ������ID
			String cs_id = ss_csz[i];
			s_sql="select * from g_zdorpphxzb where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// �豸���
			String pz1 = String.valueOf(cldMap.get("xh"));// �豸���(1�ֽ�)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// �ɼ��豸�����������
			String pz2 = String.valueOf(cldMap.get("cldh"));;// �ɼ��豸�����������(2�ֽ�)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// ORP����
			String pz3 = String.valueOf(cldMap.get("orpsx"));// ORP����
			if(!"EEEE".equalsIgnoreCase(pz3)){
				int i_pz3=Integer.parseInt(pz3);
				if(i_pz3<0){
					pz3= Util.makeFormat28(1,Math.abs(i_pz3));
				}else{
					pz3= Util.makeFormat28(0,Math.abs(i_pz3));
				}
			}
			
			// ORP����
			String pz4 = String.valueOf(cldMap.get("orpxx"));// ORP����
			if(!"EEEE".equalsIgnoreCase(pz4)){
				int i_pz4=Integer.parseInt(pz4);
				if(i_pz4<0){
					pz4= Util.makeFormat28(1,Math.abs(i_pz4));
				}else{
					pz4= Util.makeFormat28(0,Math.abs(i_pz4));
				}
			}
			
			// PH��ֵ����
			String pz5 = String.valueOf(cldMap.get("phsx"));// PH��ֵ����
			if(!"EEEE".equalsIgnoreCase(pz5)){
				pz5=Util.makeFormat30(pz5);
			}
			
			//  PH��ֵ����
			String pz6 = String.valueOf(cldMap.get("phxx"));// PH��ֵ����
			if(!"EEEE".equalsIgnoreCase(pz6)){
				pz6=Util.makeFormat30(pz6);
			}
			

			data += data1 + data2 + pz3 + pz4 + pz5 + pz6;
         }
		
		//ɾ���ն˲����㵱ǰ���ݱ��е���Ч��¼
//		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		jdbcT.update(s_sql, params);
		



		// �塢����
		String mc = "ORP,HP ����������F17";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F17", csz, jdbcT); //ORP,HP ����������

		return seq_sjzfs;
	}
	
	/**
	 * ����������F18��������ˮλ����������(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N��վ��ˮ�ص�ID
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F18(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F18"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// �ɼ��豸����
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// ÿ������ID
			String cs_id = ss_csz[i];
			s_sql="select * from m_station_pool where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// �豸���
			String pz1 = String.valueOf(cldMap.get("pooleid"));// �豸���(1�ֽ�)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// �ɼ��豸�����������
			String pz2 = String.valueOf(cldMap.get("pooleid"));;// �ɼ��豸�����������(2�ֽ�)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// ����
			String pz3 = String.valueOf(cldMap.get("deep"));
			String data3=Util.makeFormat22(pz3);
			
			//����ˮλ����
			String pz4 = String.valueOf(cldMap.get("upper"));
			String data4=Util.makeFormat22(pz4);
			//����ˮλ����
			String pz5 = String.valueOf(cldMap.get("lower"));
			String data5=Util.makeFormat22(pz5);
			
			data += data1 + data2 + data3 + data4 + data5;
        }


		// �塢����
		String mc = "������ˮλ����������F18";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F13", csz, jdbcT); //�����豸���Ƶ����

		return seq_sjzfs;
	}

	/**
	 * �����������ն������������F11(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param mcxx
	 *            ArrayList �ն������������Ϣ���˿ںš������㡢�������ԡ��������(��Ϊstring��)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdmcpz(String txfs, String xzqxm, String zddz,
			ArrayList mcxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F11"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		int mcNum = mcxx.size();
		String sMcnum = Integer.toHexString(mcNum);
		if (sMcnum.length() < 2) {
			sMcnum = "0" + sMcnum;
		}
		data = sMcnum; // ����·��
		for (int i = 0; i < mcNum; i++) {
			Map hm = (Map) mcxx.get(i);

			// ��������˿ں�
			String dkh = String.valueOf(hm.get("dkh"));
			dkh = Integer.toHexString(Integer.parseInt(dkh));
			if (dkh.length() < 2) {
				dkh = "0" + dkh;
			}

			// �����������
			String cldh = String.valueOf(hm.get("cldh"));
			cldh = Integer.toHexString(Integer.parseInt(cldh));
			if (cldh.length() < 2) {
				cldh = "0" + cldh;
			}

			// ��������
			String mcsx = String.valueOf(hm.get("mcsx"));
			mcsx = Integer.toHexString(Integer.parseInt(mcsx));
			if (mcsx.length() < 2) {
				mcsx = "0" + mcsx;
			}

			// �����
			String dbcs = String.valueOf(hm.get("dbcs"));
			dbcs = Integer.toHexString(Integer.parseInt(dbcs));
			int len_dbcs = dbcs.length();
			for (int j = 0; j < 4 - len_dbcs; j++) {
				dbcs = "0" + dbcs;
			}
			dbcs = Util.convertStr(dbcs);

			data = data + dkh + cldh + mcsx + dbcs;
		}

		// �塢����
		String mc = "�����ն������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˵�ѹ/����ģ������������F13(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param mnlxx
	 *            ArrayList �ն�����ģ������Ϣ���˿ںš�������š�ģ�������ԣ�(��Ϊstring��)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZddydlmnlpz(String txfs, String xzqxm, String zddz,
			ArrayList mnlxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F13"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		int mnlNum = mnlxx.size();
		String sMnlnum = Util.decStrToHexStr(mnlNum, 1);
		data = sMnlnum;

		for (int i = 0; i < mnlNum; i++) {
			Map hm = (Map) mnlxx.get(i);
			// �˿ں�
			String dkh = String.valueOf(hm.get("dkh"));
			dkh = Util.decStrToHexStr(dkh, 1);

			// �������
			String cldh = String.valueOf(hm.get("cldh"));
			cldh = Util.decStrToHexStr(cldh, 1);

			// ģ��������
			String mnlsx = String.valueOf(hm.get("mnlsx"));
			mnlsx = Util.binStrToHexStr(mnlsx, 1);

			data += dkh + cldh + mnlsx;
		}

		// �塢����
		String mc = "���ã��ն˵�ѹ/����ģ������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն��ܼ�������F14(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzxx
	 *            ArrayList �ն������ܼ�����Ϣ [1��Map:key = �ܼ����(String)�� value =
	 *            �ܼӲ�������Ϣ(ArrayList) 2���ܼӲ�������ϢArrayList����Map,
	 *            �������������(cldh:String); �������־(zfxbz:String)<0:����1:����>;
	 *            �������־(ysfbz:String)<0:�ӣ�1:��>; ]
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdzjzpz(String txfs, String xzqxm, String zddz,
			ArrayList zjzxx) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F14"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] params = new String[] {};
		// �ܼ�������
		int zjzNum = zjzxx.size();
		if (zjzNum > 0) {
			// ɾ����"�ն��ܼӲ��������ñ�(ZDZJCLDPZB)"�и��ն˵ļ�¼
			sSql = "delete zdzjcldpzb " + "where xzqxm=? and zddz=?";
			params = new String[] { xzqxm, zddz };
			jdbcT.update(sSql, params);

			// ɾ����"�ն��ܼ������ñ�(ZDZJZPZB)"�и��ն˵ļ�¼
			sSql = "delete zdzjzpzb " + "where xzqxm=? and zddz=?";
			jdbcT.update(sSql, params);
		}
		// ---data---
		data = Util.intToHexStr(zjzNum, 1);
		for (int i = 0; i < zjzNum; i++) {
			// <--------------ÿ���ܼ�����--------------->
			Map hm_zjz = (Map) zjzxx.get(i);
			// �ܼ����
			String sZjzh = (String) (hm_zjz.keySet().toArray())[0];

			// д��"�ն��ܼ������ñ�(ZDZJZPZB)"
			sSql = "insert into zdzjzpzb(xzqxm,zddz,zjzxh) " + "values(?,?,?)";
			params = new String[] { xzqxm, zddz, sZjzh };
			jdbcT.update(sSql, params);

			// ---data---
			data += Util.decStrToHexStr(sZjzh, 1);

			// �ܼӲ�����
			ArrayList lst_zjcld = (ArrayList) hm_zjz.get(sZjzh);
			int cldNum = lst_zjcld.size();

			// ---data---
			data += Util.intToHexStr(cldNum, 1);

			for (int j = 0; j < cldNum; j++) {
				// <--------------ÿ����������--------------->
				Map hm_cld = (Map) lst_zjcld.get(j);
				// �������
				String cldh = String.valueOf(hm_cld.get("cldh"));
				// �������־
				String zfxbz = String.valueOf(hm_cld.get("zfxbz"));
				// �������־
				String ysfbz = String.valueOf(hm_cld.get("ysfbz"));

				// д��"�ն��ܼӲ��������ñ�(ZDZJCLDPZB)"
				sSql = "insert into zdzjcldpzb(xzqxm,zddz,zjzxh,cldh,ysfbz,zfxbz) "
						+ "values(?,?,?,?,?,?)";
				params = new String[] { xzqxm, zddz, sZjzh, cldh, ysfbz, zfxbz };
				jdbcT.update(sSql, params);

				// ������š��������־���������־�ϳ�һ���ֽ�
				String cldAll = "";
				cldh = Util.decStrToBinStr(String.valueOf(Integer
						.parseInt(cldh) - 1), 1);
				cldAll = ysfbz + zfxbz + cldh.substring(2, 8);
				cldAll = Util.binStrToHexStr(cldAll, 1);

				// ---data---
				data += cldAll;

			}

		}

		// �塢����
		String mc = "�����ն��ܼ����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն��й��ܵ������Խ���¼���������F15(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cs
	 *            String[][7] ���� cs[i][0]:�Ա��ܼ���� cs[i][1]:�����ܼ���� cs[i][2]:ʱ������
	 *            cs[i][3]:�Աȷ���(0:���;1:����) cs[i][4]:�Խ�����ƫ��ֵ(%)
	 *            cs[i][5]:�Խ�޾���ƫ��ֵ(kWh) cs[i][6]:�Խ�޾���ƫ��ֵ����(0:��;1:��)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZdygzdnlcdyxsjcspz(String txfs, String xzqxm,
			String zddz, String[][] cs) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F15"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] params = null;
		// ɾ��ԭ���Ĳ����
		sSql = "delete ZDYGZDNLCDYXSJCSPZB " + "where xzqxm=? and zddz=?";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		// �����
		data += Util.decStrToHexStr(cs.length, 1);
		// ÿ�������
		for (int i = 0; i < cs.length; i++) {
			String dbzjzh = cs[i][0]; // �Ա��ܼ����
			String czzjzh = cs[i][1]; // �����ܼ����
			String sjqj = cs[i][2]; // ʱ������
			String dbff = cs[i][3]; // �Աȷ���(0:���;1:����)
			String cdyxxdpcz = cs[i][4]; // �Խ�����ƫ��ֵ(%)
			String cdyxjdpcz = cs[i][5]; // �Խ�޾���ƫ��ֵ(kWh)
			String cdyxjdpczfh = cs[i][6]; // �Խ�޾���ƫ��ֵ����(0:��;1:��)

			data += Util.decStrToHexStr(dbzjzh, 1);
			data += Util.decStrToHexStr(czzjzh, 1);
			data += Util.binStrToHexStr(dbff + "00000" + sjqj, 1);
			data += Util.decStrToHexStr(cdyxxdpcz, 1);
			data += Util.makeFormat03(cdyxjdpcz, "0", cdyxjdpczfh);

			sSql = "insert into ZDYGZDNLCDYXSJCSPZB(xzqxm,zddz,xh,dbzjzh,"
					+ "czzjzh,sjqj,dbff,cdyxxdpcz,cdyxjdpcz) "
					+ "values(?,?,?,?,?,?,?,?,?)";
			params = new String[] { xzqxm, zddz, String.valueOf(i + 1), dbzjzh,
					czzjzh, sjqj, dbff, cdyxxdpcz,
					cdyxjdpcz + "#" + cdyxjdpczfh };
			jdbcT.update(sSql, params);

		}

		// �塢����
		String mc = "�ն��й��ܵ������Խ���¼���������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ�м�����(AFN=10H)
	 * 
	 * @param txfs
	 *            String ͨ�ŷ�ʽ(01:����;02:GPRS;06:����)
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param dbgylx
	 *            String ����Լ����
	 * @param dnbdz
	 *            String ���ܱ��ַ
	 * @param dnbsjxdm
	 *            String ���ܱ����������
	 * @param btl
	 *            String ������(000:��ʾ300;...111:��ʾ19200)
	 * @param tzw
	 *            String ֹͣλ(0:1λ;1:2λ)
	 * @param jym
	 *            String У����(00:��У��;10:żУ��;11:��У��)
	 * @param ws
	 *            String λ��(00-11:��ʾ5-8)
	 * @param bwcssj
	 *            String ���ĳ�ʱʱ��(��λ:10ms)
	 * @param zjcssj
	 *            String �ֽڳ�ʱʱ��(��λ:10ms)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String query_zj(String txfs, String xzqxm, String zddz,
			String dbgylx, String dnbdz, String dnbsjxdm, String btl,
			String tzw, String jym, String ws, String bwcssj, String zjcssj)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4B";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "10"; // ��ѯ�м�����

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);// ����ʱ��
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn)
		String sDADT = "";
		String sDA = Util.getDA("P0");
		String sDT = Util.getDT("F1");
		sDADT = Util.convertStr(sDA) + Util.convertStr(sDT);

		// 4�����ݵ�Ԫ
		String sDATA = "";
		// �˿ں�
		sDATA += "01";
		// ת��������
		sDATA += Util.binStrToHexStr(btl + tzw + jym + ws, 1);
		// ���ĳ�ʱʱ��
		sDATA += Util.decStrToHexStr(bwcssj, 1);
		// �ֽڳ�ʱʱ��
		sDATA += Util.decStrToHexStr(zjcssj, 1);
		// �м�����
		String zjml = "";
		if (dbgylx.equalsIgnoreCase("01")) {
			// DLT-645��Լ
			zjml = dlt645Parse.encode(dnbdz, dnbsjxdm);
			cat.info("�м�����:" + zjml);
		}
		sDATA += Util.decStrToHexStr(zjml.length() / 2, 1);
		sDATA += zjml;

		// 5��������ϢAUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		int iLEN = sCSDATA.length();
		iLEN = iLEN * 2 + 1;
		String sLEN = Util.decStrToHexStr(iLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// д������֡���ͱ�
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz,dbgylxdm) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?,?)";
		String[] params = new String[] { seq_sjzfs, zdid, sAFN,
				sSEQ1.toUpperCase(), Util.decStrToHexStr(iZdpfc, 1),
				rq.substring(4, 12), sSJZ, dbgylx };
		jdbcT.update(sSql, params);

		// д�����ݱ�ʶ�ӱ�
		sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,sjdybsz,sjdybsmc) "
				+ "values(seq_sjzfsmx.nextval,?,?,'P0F1',?,?)";
		params = new String[] { seq_sjzfs, sAFN,
				Util.convertStr(sDA) + Util.convertStr(sDT), "���м�����" };
		jdbcT.update(sSql, params);

		// ����
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ�ն˲�������(AFN=0AH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sjxxx
	 *            String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ� sjxxx[i][1]
	 *            ��Ϣ�ࣨFn��
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String query_zdcspz(String txfs, String xzqxm, String zddz,
			String[][] sjxxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		String s_sql = "";
		String[] params = null;

		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4B";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "0A"; // ��ѯ�ն˲�������

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);// ����ʱ��
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3��n�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn),�����ݵ�Ԫ
		String sDADT = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ���ݵ��
			String xxl = sjxxx[i][1];// ������Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			String tempDADT = sDA + sDT;

			sDADT += tempDADT;

			if (xxl.equalsIgnoreCase("F10")) {
				String cldlx = "01";// ����������(01:���ܱ�)

				s_sql = "select xh from g_zdcldpzb where zdid=? and cldlx=?";
				params = new String[] { zdid, cldlx };
				List lst = jdbcT.queryForList(s_sql, params);
				int num = lst.size();
				sDADT += Util.convertStr(Util.decStrToHexStr(Long.valueOf(num),
						2));
				for (int m = 0; m < num; m++) {
					String xh = String.valueOf(m + 1);
					sDADT += Util.convertStr(Util.decStrToHexStr(xh, 2));
				}
			}
//			else if (xxl.equalsIgnoreCase("F11")
//					|| xxl.equalsIgnoreCase("F13")) {
//				String cldlx = "";
//				if (xxl.equalsIgnoreCase("F11")) {
//					cldlx = "02";// ����������(02:����)
//				} else if (xxl.equalsIgnoreCase("F13")) {
//					cldlx = "03";// ����������(03:ģ����)
//				}
//				s_sql = "select xh from g_zdcldpzb where zdid=? and cldlx=?";
//				params = new String[] { zdid, cldlx };
//				List lst = jdbcT.queryForList(s_sql, params);
//				int num = lst.size();
//				sDADT += Util.decStrToHexStr(Long.valueOf(num), 1);
//				for (int m = 0; m < num; m++) {
//					String xh = String.valueOf(m + 1);
//					sDADT += Util.decStrToHexStr(xh, 1);
//				}
//			} else if (xxl.equalsIgnoreCase("F14")) {
//				// �ܼ�������
//				s_sql = "select zjzxh from g_zdzjzpzb where zdid=?";
//				params = new String[] { zdid };
//				List lst = jdbcT.queryForList(s_sql, params);
//				int num = lst.size();
//				sDADT += Util.decStrToHexStr(Long.valueOf(num), 1);
//				for (int m = 0; m < num; m++) {
//					String xh = String.valueOf(((Map) lst.get(m)).get("zjzxh"));
//					sDADT += Util.decStrToHexStr(xh, 1);
//				}
//			} 
			else if (xxl.equalsIgnoreCase("F33")) {
				// �ն˳������в���
				s_sql = "select dkh from g_zdcldpzb where zdid=? and dkh>0";
				params = new String[] { zdid };
				List lst = jdbcT.queryForList(s_sql, params);
				int num = lst.size();
				sDADT += Util.decStrToHexStr(Long.valueOf(num), 1);
				for (int m = 0; m < num; m++) {
					String xh = String.valueOf(((Map) lst.get(m)).get("dkh"));
					sDADT += Util.decStrToHexStr(xh, 1);
				}
			} else if (xxl.equalsIgnoreCase("F34")) {
				// ���ն˽ӿڵ�ͨ��ģ��Ĳ�������
				s_sql = "select dkh from g_zdcldpzb where zdid=? and dkh>0";
				params = new String[] { zdid };
				List lst = jdbcT.queryForList(s_sql, params);
				int num = lst.size();
				sDADT += Util.decStrToHexStr(Long.valueOf(num), 1);
				for (int m = 0; m < num; m++) {
					String xh = String.valueOf(((Map) lst.get(m)).get("dkh"));
					sDADT += Util.decStrToHexStr(xh, 1);
				}
			}
		}

		// 5��������ϢAUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698��
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// д������֡���ͱ�
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		s_sql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(s_sql, params);

		// д�����ݱ�ʶ�ӱ�
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			s_sql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT), "��ѯ�ն˲�������" };
			jdbcT.update(s_sql, params);
		}

		// ����
		s_sql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(s_sql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));

		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ�ն˲�������(AFN=0AH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param ksxh
	 *            int ��ʼװ�����
	 * @param jsxh
	 *            int ����װ�����
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN0AF10(String txfs, String xzqxm, String zddz,
			int ksxh, int jsxh) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		String s_sql = "";
		String[] params = null;

		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4B";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "0A"; // ��ѯ�ն˲�������

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);// ����ʱ��
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3��n�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn),�����ݵ�Ԫ
		String sDADT = "";
		String xxdh = "0";// ���ݵ��
		String xxl = "F10";// ������Fn

		String sDA = Util.getDA(Integer.parseInt(xxdh));
		sDA = Util.convertStr(sDA);

		String sDT = Util.getDT(xxl);
		sDT = Util.convertStr(sDT);

		String tempDADT = sDA + sDT;

		sDADT += tempDADT;

		// ��ѯ����������
		sDADT += Util.convertStr(Util.decStrToHexStr(jsxh - ksxh + 1, 2));
		for (int m = ksxh; m <= jsxh; m++) {
			// װ�����
			sDADT += Util.convertStr(Util.decStrToHexStr(m, 2));
		}

		// 5��������ϢAUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698��
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// д������֡���ͱ�
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		s_sql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(s_sql, params);

		// д�����ݱ�ʶ�ӱ�

		s_sql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
				+ "sjdybsz,sjdybsmc) "
				+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
		params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
				Util.convertStr(sDA) + Util.convertStr(sDT), "��ѯ�ն˲�������" };
		jdbcT.update(s_sql, params);

		// ����
		s_sql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(s_sql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));

		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ�ն��¼�F1/F2(AFN=0EH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sjlx
	 *            String �¼�����(1:��Ҫ�¼���2��һ���¼�)
	 * @param sjqszz
	 *            int �¼���ʼָ��
	 * @param sjjszz
	 *            int �¼�����ָ��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String queryZdsj(String txfs, String xzqxm, String zddz,
			String sjlx, int sjqszz, int sjjszz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4B";

		// ����Ӧ�ù�����
		String afn = "0E"; // ��ѯ�¼�

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (sjlx.equals("1")) {
			xxl = "F1";
		} else if (sjlx.equals("2")) {
			xxl = "F2";
		}

		// �ġ����ݵ�Ԫ
		String data = "";
		data = Util.intToHexStr(sjqszz, 1) + Util.intToHexStr(sjjszz, 1);

		// �塢����
		String mc = "";
		if (sjlx.equals("1")) {
			mc = "��ѯ�ն���Ҫ�¼�";
		} else if (sjlx.equals("2")) {
			mc = "��ѯ�ն�һ���¼�";
		}
		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ���������������������������F25(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            int �������
	 * @param cldjbcs
	 *            HashMap ���������������PT��CT�����ѹ�������������߷�ʽ��[��ΪString��]
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCldjbcspz(String txfs, String xzqxm, String zddz,
			int cldh, HashMap cldjbcs) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F25"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String sPT = String.valueOf(cldjbcs.get("pt")); // BIN
		sPT = Util.decStrToHexStr(sPT, 2);
		sPT = Util.convertStr(sPT);

		String sCT = String.valueOf(cldjbcs.get("ct")); // BIN
		sCT = Util.decStrToHexStr(sCT, 2);
		sCT = Util.convertStr(sCT);

		String sEDDY = String.valueOf(cldjbcs.get("eddy")); // ���ѹ��BCD�룬2���ֽ�,һλС�������λΪ��λ��
		if (sEDDY.indexOf(".") == -1) {
			sEDDY = sEDDY + ".0";
		}
		String sEDDY1 = sEDDY.substring(0, sEDDY.indexOf("."));
		int iEDDY1 = sEDDY1.length();
		for (int i = 0; i < 3 - iEDDY1; i++) {
			sEDDY1 = "0" + sEDDY1;
		}
		String sEDDY2 = sEDDY.substring(sEDDY.indexOf(".") + 1, sEDDY
				.indexOf(".") + 2);
		sEDDY = sEDDY1 + sEDDY2;
		sEDDY = Util.convertStr(sEDDY);

		String sZDDL = String.valueOf(cldjbcs.get("zddl")); // ��������BCD�룬1���ֽ�,һλС�������λΪʮλ��
		if (sZDDL.indexOf(".") == -1) {
			sZDDL = sZDDL + ".0";
		}
		String sZDDL1 = sZDDL.substring(0, sZDDL.indexOf("."));
		String sZDDL2 = sZDDL.substring(sZDDL.indexOf(".") + 1, sZDDL
				.indexOf(".") + 2);
		sZDDL = sZDDL1 + sZDDL2;

		String sJXFS = "0" + String.valueOf(cldjbcs.get("jxfs")); // ���߷�ʽ

		data = sPT + sCT + sEDDY + sZDDL + sJXFS;

		// �塢����
		String mc = "���ò�����" + String.valueOf(cldh) + "�Ļ�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ���������������������������F25(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            int �������
	 * @param csz
	 *            String ����ֵ(cs1;cs2;cs3) cs1:PT cs2:CT cs3:©�����ٽ�ֵ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F25(String txfs, String xzqxm, String zddz,
			int cldh, String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F25"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		String[] ss_csz = csz.split(";");

		// 1����ѹ����������PT
		String cs1 = ss_csz[0];
		String pt = Util.decStrToHexStr(cs1, 2);
		pt = Util.convertStr(pt);

		// 2����������������CT
		String cs2 = ss_csz[1];
		String ct = Util.decStrToHexStr(cs2, 2);
		ct = Util.convertStr(ct);
		
		// 3��©�����ٽ�ֵ
		String cs3 = ss_csz[2];
		String ldlljz = Util.decStrToHexStr(cs3, 2);
		ldlljz = Util.convertStr(ldlljz);

		

		data = pt + ct +ldlljz;

		// �塢����
		String mc = "���ò�����" + String.valueOf(cldh) + "�Ļ�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F25", cldh + "@" + csz, jdbcT); // �������������

		return seq_sjzfs;
	}

	/**
	 * ����������ң��F1/F2(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param lch
	 *            String �ִκ�(1-8��)
	 * @param ykbz
	 *            String ң�ر�־(55:ң����բ��AA�������բ)
	 * @param xdsj
	 *            String �޵�ʱ��(0-15),��λ:0.5h
	 * @param gjyssj
	 *            String �澯��ʱʱ��(0-15),��λ:1min
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendYk(String txfs, String xzqxm, String zddz, String lch,
			String ykbz, String xdsj, String gjyssj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + lch; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (ykbz.equals("55")) {
			xxl = "F1";
		} else if (ykbz.equals("AA")) {
			xxl = "F2";
		}

		// �ġ����ݵ�Ԫ
		String data = "";
		if (ykbz.equals("55")) {
			String temp_xdsj = Util.decStrToBinStr(xdsj, 1);
			String temp_gjyssj = Util.decStrToBinStr(gjyssj, 1);
			data = temp_gjyssj.substring(4, 8) + temp_xdsj.substring(4, 8);

			data = Util.binStrToHexStr(data, 1);
		}

		// �塢����
		String mc = "";
		if (ykbz.equals("55")) {
			mc = "ң����բ(�ִ�" + lch + ")";
		} else if (ykbz.equals("AA")) {
			mc = "�����բ(�ִ�" + lch + ")";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String value = "";
		if (ykbz.equals("55")) {
			value = lch + ";" + xdsj + "#" + gjyssj + "#" + Util.getNowTime();
		} else if (ykbz.equals("AA")) {
			value = lch + ";" + "AA";
		}
		writeCsszzcb(seq_sjzfs, "yktzzt", value, jdbcT); // ң����բ״̬

		return seq_sjzfs;
	}

	/**
	 * �������������ظ澯ʱ��F49(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param lch
	 *            String �ִκ�(1-8��)
	 * @param gkgjsj
	 *            String ���ظ澯ʱ��(0-60min)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendGkgjsj(String txfs, String xzqxm, String zddz,
			String lch, String gkgjsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + lch; // ��Ϣ��
		String xxl = "F49"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.decStrToHexStr(gkgjsj, 1);

		// �塢����
		String mc = "���ظ澯ʱ��(�ִ�" + lch + ")";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "lcgkgjsj", lch + ";" + gkgjsj, jdbcT); // �ִι��ظ澯ʱ��

		return seq_sjzfs;
	}

	

	/**
	 * �����������ն˵���������F22(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param fl
	 *            String[14][3] fl[0][0]:����1�ķ���(0:��;1:��)
	 *            fl[0][1]:����1�ĵ�λ(0:��;1:Ԫ) fl[0][2]:����1��ֵ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDnlfl(String txfs, String xzqxm, String zddz,
			String[][] fl) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F22"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		for (int i = 0; i < fl.length; i++) {
			String flfh = fl[i][0];
			String fldw = fl[i][1];
			String flz = fl[i][2];

			data += Util.makeFormat03(flz, fldw, flfh);
		}

		// �塢����
		String mc = "�ն˵�������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String value = "";
		for (int i = 0; i < fl.length; i++) {
			String flfh = fl[i][0];
			String fldw = fl[i][1];
			String flz = fl[i][2];
			value += flfh + "#" + fldw + "#" + flz + ";";
		}
		writeCsszzcb(seq_sjzfs, "fl", value, jdbcT); // �ն˵���������

		return seq_sjzfs;
	}

	/**
	 * �����������ն˴߷Ѹ澯����F23(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cfgjcs
	 *            String �߷Ѹ澯����:24λ(D23-D0),ÿλ��Ӧ1Сʱ, ��1�澯,��0���澯,
	 *            ����:D0=1��ʾ00:00-01:00�澯
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCfgjcs(String txfs, String xzqxm, String zddz,
			String cfgjcs) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F23"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		for (int i = 0; i < 3; i++) {
			String temps = cfgjcs.substring((3 - i - 1) * 8, (3 - i) * 8);
			data += Util.binStrToHexStr(temps, 1);
		}

		// �塢����
		String mc = "�ն˴߷Ѹ澯��������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "cfgjcs", cfgjcs, jdbcT); // �ն˴߷Ѹ澯����

		return seq_sjzfs;
	}

	/**
	 * �����������ն˴߷Ѹ澯Ͷ���־F26/F34(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param trbz
	 *            String Ͷ���־��55:Ͷ��;AA:���
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCfgjtrbz(String txfs, String xzqxm, String zddz,
			String trbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (trbz.equals("55")) {
			xxl = "F26";
		} else if (trbz.equals("AA")) {
			xxl = "F34";
		}

		// �ġ����ݵ�Ԫ
		String data = ""; // �����ݵ�Ԫ

		// �塢����
		String mc = "";
		if (trbz.equals("55")) {
			mc = "�ն˴߷Ѹ澯Ͷ��";
		} else if (trbz.equals("AA")) {
			mc = "�ն˴߷Ѹ澯���";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "cfgjtrbz", trbz, jdbcT); // �ն˴߷Ѹ澯Ͷ���־

		return seq_sjzfs;
	}

	/**
	 * �����������ն˵���������ʱ�κͷ���������F21(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sd
	 *            String[][] ʱ��{sd[i][0]:ʱ��(x-y,0-48);
	 *            sd[i][1]:����(0000:����1;0001:����2;...;1101:����14)}
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDnlflsd(String txfs, String xzqxm, String zddz,
			String[][] sd) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F21"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String temp_data = "";
		Map hmFL = new HashMap();
		int sd_len = sd.length;
		for (int i = 0; i < sd_len; i++) {
			String sd_xh = sd[i][0]; // ʱ�����
			String sd_fl = sd[i][1]; // ʱ�η���
			hmFL.put(sd_fl, "0");

			String[] xh = sd_xh.split("-");
			int xh_len = Integer.parseInt(xh[1]) - Integer.parseInt(xh[0]);
			for (int j = 0; j < xh_len; j++) {
				temp_data = temp_data + sd_fl;
			}
		}
		int data_len = temp_data.length();
		for (int i = 0; i < 192 - data_len; i++) {// 24�ֽ�
			temp_data = "0" + temp_data;
		}

		for (int i = 0; i < 24; i++) {
			String temps = temp_data.substring(i * 8, (i + 1) * 8);
			temps = temps.substring(4) + temps.substring(0, 4);// ����4�ַ���ת
			temps = Util.binStrToHexStr(temps, 1);// һ���ֽ�

			data = data + temps;// ��λ���ȴ�
		}
		// ���ʸ���
		int flgs = hmFL.keySet().size();
		data += Util.decStrToHexStr(flgs, 1);

		// �塢����
		String mc = "�ն˵���������ʱ�κͷ���������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д"�ն˵���������ʱ��"��
		String[] params = null;
		// ɾ���ɼ�¼
		String sSql = "delete ZDDNLFLSD where xzqxm=? and zddz=?";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		// �����¼�¼
		int sdh = 0;// ʱ�κ�
		for (int i = 0; i < sd_len; i++) {
			sdh++;
			String sd_xh = sd[i][0];
			String sd_fl = sd[i][1];

			String[] xh = sd_xh.split("-");
			String sd_sjd = Util.getSD(xh[0]) + "-" + Util.getSD(xh[1]);

			sSql = "insert into ZDDNLFLSD(xzqxm,zddz,sdh,sjd,flh) "
					+ "values(?,?,?,?,?)";
			params = new String[] { xzqxm, zddz, String.valueOf(sdh), sd_sjd,
					sd_fl };
			jdbcT.update(sSql, params);
		}

		return seq_sjzfs;
	}

	/**
	 * �����������µ����ض�ֵ�趨F46(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param dz
	 *            String ��ֵ
	 * @param dzfh
	 *            String ��ֵ����:0:��;1:��
	 * @param dzdw
	 *            String ��ֵ��λ:0:kWh;1:MWh
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendYdkdz(String txfs, String xzqxm, String zddz, int zjzh,
			String dz, String dzfh, String dzdw) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F46"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data = Util.makeFormat03(dz, dzdw, dzfh);
		String[] params = null;
		// ɾ��"�ն��µ�����ñ�"�и��ܼ�����µ������
		String sSql = "delete ZDYDKPZB "
				+ "where xzqxm='?' and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// ����������
		sSql = "insert into zdydkpzb(xzqxm,zddz,zjzxh,dz,dzfh,dzdw) "
				+ "values(?,?,?,?,?,?)";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), dz, dzfh,
				dzdw };
		jdbcT.update(sSql, params);

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "���µ����ض�ֵ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������������ض�ֵ�趨F47(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param gddh
	 *            String ���絥��
	 * @param bz
	 *            String ��־:55:׷��;AA:ˢ��
	 * @param gdfh
	 *            String �������:0:��;1:��
	 * @param gdz
	 *            String ����ֵ
	 * @param bjmxfh
	 *            String �������޷���:0:��;1:��
	 * @param bjmxz
	 *            String ��������ֵ
	 * @param tzmxfh
	 *            String ��բ���޷���:0:��;1:��
	 * @param tzmxz
	 *            String ��բ����ֵ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendGdkdz(String txfs, String xzqxm, String zddz, int zjzh,
			String gddh, String bz, String gdfh, String gdz, String bjmxfh,
			String bjmxz, String tzmxfh, String tzmxz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F47"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data += Util.convertStr(Util.decStrToHexStr(gddh, 4));
		data += bz;
		data += Util.makeFormat03(gdz, "0", gdfh);
		data += Util.makeFormat03(bjmxz, "0", bjmxfh);
		data += Util.makeFormat03(tzmxz, "0", tzmxfh);
		String[] params = null;

		// �鵵"�ն˹�������ñ�"�и��ܼ���Ĺ��������
		String sSql = "update ZDGDKPZB set flag='0' "
				+ "where xzqxm=? and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// ����������
		sSql = "insert into zdgdkpzb(xzqxm,zddz,zjzxh,gddh,bz,gdfh,gdz,"
				+ "bjmxfh,bjmxz,tzmxfh,tzmxz,flag) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,'1')";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), gddh, bz,
				gdfh, gdz, bjmxfh, bjmxz, tzmxfh, tzmxz };
		jdbcT.update(sSql, params);

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "�Ĺ������ض�ֵ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������µ��Ͷ���־�趨F15/F23(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param trbz
	 *            String Ͷ���־:55:Ͷ��;AA:���
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendYdktrbz(String txfs, String xzqxm, String zddz, int zjzh,
			String trbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (trbz.equals("55")) {
			xxl = "F15";
		} else if (trbz.equals("AA")) {
			xxl = "F23";
		}

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "";
		if (trbz.equals("55")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "�µ��Ͷ��";
		} else if (trbz.equals("AA")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "�µ�ؽ��";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "ydktrbz", zjzh + "#" + trbz, jdbcT); // �µ��Ͷ���־

		return seq_sjzfs;
	}

	/**
	 * ���������������Ͷ���־�趨F16/F24(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param trbz
	 *            String Ͷ���־:55:Ͷ��;AA:���
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendGdktrbz(String txfs, String xzqxm, String zddz, int zjzh,
			String trbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (trbz.equals("55")) {
			xxl = "F16";
		} else if (trbz.equals("AA")) {
			xxl = "F24";
		}

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "";
		if (trbz.equals("55")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "�����Ͷ��";
		} else if (trbz.equals("AA")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "����ؽ��";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "gdktrbz", zjzh + "#" + trbz, jdbcT); // �����Ͷ���־

		return seq_sjzfs;
	}

	/**
	 * ��������������ִ��趨F48(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param lc
	 *            String[8] �ִ��ܿ����(lc[0]-lc[7]:��1��-��8��,0:���ܿأ�1���ܿ�)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDklc(String txfs, String xzqxm, String zddz, int zjzh,
			String[] lc) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F48"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		for (int i = 0; i < 8; i++) {
			data = lc[i] + data;
		}
		data = Util.binStrToHexStr(data, 1);

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "�ĵ���ִ�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д"�ն��ܼ������ñ�"
		String sSql = "update zdzjzpzb set dklc1=?,dklc2=?,"
				+ "dklc3=?,dklc4=?,dklc5=?," + "dklc6=?,dklc7=?,dklc8=? "
				+ "where xzqxm=? and zddz=? and zjzxh=?";
		String[] params = new String[] { lc[0], lc[1], lc[2], lc[3], lc[4],
				lc[5], lc[6], lc[7], xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		return seq_sjzfs;
	}

	/**
	 * �����������ն������澯��־����F57(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sygjbz
	 *            String �����澯��־:D0-D23��λ��ʾ0-23��, ÿλ��ʾһ��Сʱ,�磺0��ʾ00:00-01:00;
	 *            ��1��ʾ��Ӧʱ������澯,��0��ʾ��Ӧʱ�β�����澯
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendSygjbz(String txfs, String xzqxm, String zddz,
			String sygjbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F57"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		for (int i = 0; i < 3; i++) {
			data += Util
					.binStrToHexStr(sygjbz.substring(8 * i, 8 * (i + 1)), 1);
		}
		data = Util.convertStr(data);

		// �塢����
		String mc = "���������澯��־";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "sygjbz", sygjbz, jdbcT); // �����澯��־

		return seq_sjzfs;
	}

	/**
	 * ����������г����ֵ����F60(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param xbxz
	 *            String[][2] г����ֵ,����Ϊ: �ܻ����ѹ����������ֵ#����; ���г����ѹ����������ֵ#����;
	 *            ż��г����ѹ����������ֵ#����; �ܻ��������Чֵ����ֵ#����; 2��г��������Чֵ����ֵ#����; ...
	 *            18��г��������Чֵ����ֵ#����; 3��г��������Чֵ����ֵ#����; ... 19��г��������Чֵ����ֵ#����;
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendXbxz(String txfs, String xzqxm, String zddz,
			String[][] xbxz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F60"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		for (int i = 0; i < xbxz.length; i++) {
			if (i <= 2) {
				data += Util.makeFormat05(xbxz[i][1], xbxz[i][0]);
			} else {
				data += Util.makeFormat06(xbxz[i][1], xbxz[i][0]);
			}
		}

		// �塢����
		String mc = "����г����ֵ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String temps = "";
		for (int i = 0; i < xbxz.length; i++) {
			temps += xbxz[i][0] + "#" + xbxz[i][1] + ";";
		}
		writeCsszzcb(seq_sjzfs, "xbxz", temps, jdbcT); // г����ֵ

		return seq_sjzfs;
	}

	/**
	 * ������������������ֵ��������F26(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param dyhglsx
	 *            String ��ѹ�ϸ�������
	 * @param dyhglxx
	 *            String ��ѹ�ϸ�������
	 * @param dydxmx
	 *            String ��ѹ��������
	 * @param gymx
	 *            String ��ѹ����
	 * @param qymx
	 *            String Ƿѹ����
	 * @param glmx
	 *            String ��������#����
	 * @param eddlmx
	 *            String ���������#����
	 * @param lxdlsx
	 *            String �����������#����
	 * @param szglssx
	 *            String ���ڹ���������
	 * @param szglsx
	 *            String ���ڹ�������
	 * @param sxdybphxz
	 *            String �����ѹ��ƽ����ֵ#����
	 * @param sxdlbphxz
	 *            String ���������ƽ����ֵ#����
	 * @param lxsysjxz
	 *            String ����ʧѹʱ����ֵ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCldxzcs(String txfs, String xzqxm, String zddz,
			String cldh, String dyhglsx, String dyhglxx, String dydxmx,
			String gymx, String qymx, String glmx, String eddlmx,
			String lxdlsx, String szglssx, String szglsx, String sxdybphxz,
			String sxdlbphxz, String lxsysjxz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F26"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss = null;

		// ��ѹ�ϸ�������
		data += Util.makeFormat07(dyhglsx);
		// ��ѹ�ϸ�������
		data += Util.makeFormat07(dyhglxx);
		// ��ѹ��������
		data += Util.makeFormat07(dydxmx);
		// ��ѹ����
		data += Util.makeFormat07(gymx);
		// Ƿѹ����
		data += Util.makeFormat07(qymx);
		// ��������
		ss = glmx.split("#");
		data += Util.makeFormat06(ss[1], ss[0]);
		// ���������
		ss = eddlmx.split("#");
		data += Util.makeFormat06(ss[1], ss[0]);
		// �����������
		ss = lxdlsx.split("#");
		data += Util.makeFormat06(ss[1], ss[0]);
		// ���ڹ���������
		data += Util.makeFormat23(szglssx);
		// ���ڹ�������
		data += Util.makeFormat23(szglsx);
		// �����ѹ��ƽ����ֵ
		ss = sxdybphxz.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);
		// ���������ƽ����ֵ
		ss = sxdlbphxz.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);
		// ����ʧѹʱ����ֵ
		data += Util.decStrToHexStr(lxsysjxz, 1);

		// �塢����
		String mc = "���ò�������ֵ����";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String temps = cldh + "@" + dyhglsx + ";" + dyhglxx + ";" + dydxmx
				+ ";" + gymx + ";" + qymx + ";" + glmx + ";" + eddlmx + ";"
				+ lxdlsx + ";" + szglssx + ";" + szglsx + ";" + sxdybphxz + ";"
				+ sxdlbphxz + ";" + lxsysjxz + ";";
		writeCsszzcb(seq_sjzfs, "cldxzcs", temps, jdbcT); // ��������ֵ����

		return seq_sjzfs;
	}

	/**�����������������㷨ʹ��F26(AFN=04H)
	   * @param 	xzqxm	String 		����������
	   * @param 	zddz  	String 		�ն˵�ַ
	   * @param 	csz  	String		����ֵ 55ʹ��  AA��ʹ��
	   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	   */
	public String sendAFN04F26(String txfs, String xzqxm, String zddz,String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F26"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = csz;

		// �塢����
		String mc = "�������㷨ʹ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F26",  csz, jdbcT); // �������㷨ʹ��

		return seq_sjzfs;

	}
	
	/**����������������ѵ����������F27(AFN=04H)
	   * @param 	xzqxm	String 		����������
	   * @param 	zddz  	String 		�ն˵�ַ
	   * @param 	csz  	String		����ֵ cs1;...;cs3
	   *                                    ����cs1���¶�
	   *                                       cs2:ORP
	   *                                       cs3:���Ƶ��
	   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	   */
	public String sendAFN04F27(String txfs, String xzqxm, String zddz,String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F27"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		
		String[] ss_csz = csz.split(";");
		//�¶�
		String cs1 = ss_csz[0];
		if(cs1.startsWith("-")){
			data += Util.makeFormat05("1",cs1.replace("-", ""));
		}else{
			data += Util.makeFormat05("0",cs1);
		}
		
		//ORP
		String cs2 = ss_csz[1];
		if(cs2.startsWith("-")){
			data += Util.makeFormat28(1,Math.abs(Integer.parseInt(cs2)));
		}else{
			data += Util.makeFormat28(0,Integer.parseInt(cs2));
		}
		
		//Ƶ��
		String cs3 = ss_csz[2];
		if(cs3.startsWith("-")){
			data += Util.makeFormat06("1",cs3.replace("-", ""));
		}else{
			data += Util.makeFormat06("0",cs3);
		}

		// �塢����
		String mc = "������ѵ����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F27",  csz, jdbcT); // ������ѵ����������

		return seq_sjzfs;

	}

	/**
	 * ���������������㹦�������ֶ���ֵ����F28(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param csz
	 *            String ����ֵ(cs1;cs2) cs1:���������ֶ���ֵ1(pz1#pz2) pz1:����(0:��;1:��)
	 *            pz2:��ֵ cs2:���������ֶ���ֵ2(pz1#pz2) pz1:����(0:��;1:��) pz2:��ֵ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F28(String txfs, String xzqxm, String zddz,
			String cldh, String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F28"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		String[] ss_csz = csz.split(";");
		String[] ss = null;

		// ��ֵ1
		String cs1 = ss_csz[0];
		ss = cs1.split("#");
		data += Util.makeFormat05(ss[0], ss[1]);
		// ��ֵ2
		String cs2 = ss_csz[1];
		ss = cs2.split("#");
		data += Util.makeFormat05(ss[0], ss[1]);

		// �塢����
		String mc = "[AFN04F28]���ò����㹦�������ֶ���ֵ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F28", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˳������в�������F33(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N�������� csn:������(pz1#...#pz7)
	 *            pz1:�ն�ͨ�Ŷ˿ں�(1���ֽ�,1-31)
	 *            pz2:̨�����г������п�����(2���ֽ�,D5D4D3D2D1D0,�磺100101,D15-D6Ϊ����)
	 *            D5->�Ƿ�Ҫ���ն˳��������״̬�֡�(1:Ҫ��;0:��Ҫ��)
	 *            D4->�Ƿ�Ҫ���ն���Ѱ����������ĵ��(1:Ҫ��;0:��Ҫ��)
	 *            D3->�Ƿ�Ҫ���ն˶�ʱ�Ե��㲥Уʱ(1:Ҫ��;0:��Ҫ��) D2->Ҫ���ն˲��ù㲥���᳭��(1:Ҫ��;0:��Ҫ��)
	 *            D1->�Ƿ�Ҫ���ն�ֻ���ص��(1:Ҫ��;0:�����б�) D0->�Ƿ������Զ�����(1:�������Զ�����;0:
	 *            Ҫ���ն˸��ݳ���ʱ���Զ�����) pz3:������ʱ��(12���ֽ�,D95D94...D1D0,��:10...11)
	 *            D95->ʱ��23:45~24:00�Զ�����״̬(1:�������Զ�����;0:����)
	 *            D94->ʱ��23:30~23:45�Զ�����״̬(1:�������Զ�����;0:����) ...
	 *            D1->ʱ��00:15~00:30�Զ�����״̬(1:�������Զ�����;0:����)
	 *            D0->ʱ��00:00~00:15�Զ�����״̬(1:�������Զ�����;0:����)
	 *            pz4:������-����(4���ֽ�,D30D29...D1D0,��:11...01,D31����)
	 *            D30->ÿ��31�յĳ���״̬(1:��Ч;0:��Ч) D29->ÿ��30�յĳ���״̬(1:��Ч;0:��Ч) ...
	 *            D1->ÿ��2�յĳ���״̬(1:��Ч;0:��Ч) D0->ÿ��1�յĳ���״̬(1:��Ч;0:��Ч)
	 *            pz5:������-ʱ��(2���ֽ�,ʱ�֣�hhmm,��:0930��ʾ9��30��) pz6:�ն˳�����(1���ֽ�,1-60)
	 *            pz7
	 *            :�Ե��㲥Уʱ��ʱʱ��(3���ֽڣ���ʱ��,ddhhmm,����Ϊ00ʱ��ʾÿ��Уʱ,��000930��ʾÿ��9��30��Уʱ)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F33(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F33"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		int num = ss_csz.length;
		// ����������
		data += Util.decStrToHexStr(num, 1);
		for (int i = 0; i < num; i++) {
			// ÿ��������
			String csn = ss_csz[i];
			String[] ss_pz = csn.split("#");

			// �ն�ͨ�Ŷ˿ں�(1���ֽ�,1-31)
			String pz1 = ss_pz[0];
			data += Util.decStrToHexStr(pz1, 1);

			// ̨�����г������п�����(2���ֽ�,D5D4D3D2D1D0,�磺100101,D15-D6Ϊ����)
			String pz2 = ss_pz[1];
			data += Util.convertStr(Util.binStrToHexStr(pz2, 2));

			// ������ʱ��(12���ֽ�,D95D94...D1D0,��:10...11)
			String pz3 = ss_pz[2];
			data += Util.convertStr(Util.binStrToHexStr(pz3, 12));

			// ������-����(4���ֽ�,D30D29...D1D0,��:11...01,D31����)
			String pz4 = ss_pz[3];
			data += Util.convertStr(Util.binStrToHexStr(pz4, 4));

			// ������-ʱ��(2���ֽ�,ʱ�֣�hhmm,��:0930��ʾ9��30��)
			String pz5 = ss_pz[4];
			data += Util.convertStr(pz5);

			// �ն˳�����(1���ֽ�,1-60)
			String pz6 = ss_pz[5];
			data += Util.decStrToHexStr(pz6, 1);

			// �Ե��㲥Уʱ��ʱʱ��(3���ֽ�,��ʱ��,ddhhmm,����Ϊ00ʱ��ʾÿ��Уʱ,��000930��ʾÿ��9��30��Уʱ)
			String pz7 = ss_pz[6];
			data += Util.convertStr(pz7);
		}

		// �塢����
		String mc = "�ն˳������в�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F33", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �������������ն˽ӿڵ�ͨ��ģ��Ĳ�������F34(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;csn)--N�������� csn:������(pz1#...#pz7)
	 *            pz1:�ն�ͨ�Ŷ˿ں�(1���ֽ�,1-31)
	 *            pz2:ͨ�Ų�����(0-7�ֱ��ʾ300,600,1200,2400,4800,7200,9600,19200)
	 *            pz3:ֹͣλ(0:1λֹͣλ;1:2λֹͣλ) 
	 *            pz4:����У��(0:��;1:��) 
	 *            pz5:��żУ��(0:ż;1:��)
	 *            pz6:λ��(0~3�ֱ��ʾ5-8λ) 
	 *            pz7:���ն˽ӿڶ�Ӧ�˵�ͨ������
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F34(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F34"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		int num = ss_csz.length;
		// ����������
		data += Util.decStrToHexStr(num, 1);
		for (int i = 0; i < num; i++) {
			// ÿ��������
			String csn = ss_csz[i];
			String[] ss_pz = csn.split("#");

			// �ն�ͨ�Ŷ˿ں�(1���ֽ�,1-31)
			String pz1 = ss_pz[0];
			data += Util.decStrToHexStr(pz1, 1);

			// ͨ�Ų�����(0-7�ֱ��ʾ300,600,1200,2400,4800,7200,9600,19200)
			String pz2 = ss_pz[1];
			// ֹͣλ(0:1λֹͣλ;1:2λֹͣλ)
			String pz3 = ss_pz[2];
			// ����У��(0:��;1:��)
			String pz4 = ss_pz[3];
			// ��żУ��(0:ż;1:��)
			String pz5 = ss_pz[4];
			// λ��(0~3�ֱ��ʾ5-8λ)
			String pz6 = ss_pz[5];
			data += Util.binStrToHexStr(pz2 + pz3 + pz4 + pz5 + pz6, 1);

			// ���ն˽ӿڶ�Ӧ�˵�ͨ������
			String pz7 = ss_pz[6];
			data += Util.convertStr(Util.decStrToHexStr(pz7, 4));
		}

		// �塢����
		String mc = "���ն˽ӿڵ�ͨ��ģ��Ĳ�������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F34", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն�����ͨ��������������F36(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1) cs1:��ͨ����������(0��ʾϵͳ����Ҫ�ն˽�����������)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F36(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F36"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		// ��ͨ����������
		String cs1 = ss_csz[0];
		data += Util.convertStr(Util.decStrToHexStr(cs1, 4));

		// �塢����
		String mc = "�ն�����ͨ��������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F36", csz, jdbcT);

		return seq_sjzfs;
	}
//	/**
//	* ����������ʱ�ι��ض�ֵF41(AFN=04H)
//	 * 
//	 * @param xzqxm
//	 *            String ����������
//	 * @param zddz
//	 *            String �ն˵�ַ
//	 *            
//	 * @param csz ����ֵ(cs1;cs2.....csn)1<=n<=24
//	 *        csn(zfn#dzn#xsn)
//	 *           zfn ����:0����,1����
//	 *           dzn ��ֵ����:1-999
//	 *           xsn ϵ��:000:10E4
//	 *                    001:10E3
//	 *                    010:10E2
//	 *                    011:10E1
//	 *                    100:10E0
//	 *                    101:10E-1
//	 *                    110:10E-2
//	 *                    111:10E-3
//	 * 
//	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
//	 * @throws Exception
//	 */
//	public String sendAFN04F41(String txfs, String xzqxm, String zddz,
//			String  csz) throws Exception {
//
//		String seq_sjzfs = null;
//		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//
//		// һ��������
//		String kzm = "4A";
//
//		// ����Ӧ�ù�����
//		String afn = "04"; // ��������
//
//		// �������ݵ�Ԫ��ʶ
//		String xxd = "P0"; // ��Ϣ��
//		String xxl = "F41"; // ��Ϣ��
//
//		// �ġ����ݵ�Ԫ
//		String data = "";
//		String[] ss_csz = csz.split(";");
//		
//		//������־
//		int fnbz;
//		if(ss_csz.length%8!=0){
//			fnbz=((int)(ss_csz.length/8))+1;
//		}else{
//			fnbz=(int)(ss_csz.length/8);
//		}
//		data+=Util.decStrToHexStr(fnbz, 1);
//		//��ֵ
//		String dzs="";
//		for(int n=0;n<ss_csz.length;n++){
//			String cszn[]=ss_csz[n].split("#");
//			dzs=Util.makeFormat02(cszn[1], cszn[2], cszn[0])+dzs;
//		}
//		if(1==fnbz){
//			//ʱ�κ�1
//			String sdh1="";
//			for(int i=0;i<ss_csz.length;i++){
//				sdh1="1"+sdh1;
//			}
//			sdh1=Util.binStrToHexStr(sdh1, 1);
//			data=data+sdh1+dzs;
//		}
//		if(2==fnbz){
//			//ʱ��1��ֵ
//			String sd1dz=dzs.substring(0,64);
//			//ʱ��2��ֵ
//			String sd2dz=dzs.substring(64);
//			//ʱ�κ�1,2
//			String sdh1="";
//			String sdh2="";
//			for(int i=0;i<ss_csz.length-8;i++){
//				sdh2="1"+sdh2;
//			}
//			sdh1=Util.binStrToHexStr("11111111", 1);
//			sdh2=Util.binStrToHexStr(sdh2, 1);
//			data=data+sdh1+sd1dz+sdh2+sd2dz;
//			
//		}
//		if(3==fnbz){
//			//ʱ��1��ֵ
//			String sd1dz=dzs.substring(0,64);
//			//ʱ��2��ֵ
//			String sd2dz=dzs.substring(64,128);
//			//ʱ��3��ֵ
//			String sd3dz=dzs.substring(128);
//			//ʱ�κ�1,2,3
//			String sdh1="";
//			String sdh2="";
//			String sdh3="";
//			for(int i=0;i<ss_csz.length-16;i++){
//				sdh3="1"+sdh3;
//			}
//			sdh1=Util.binStrToHexStr("11111111", 1);
//			sdh2=Util.binStrToHexStr("11111111", 1);
//			sdh3=Util.binStrToHexStr(sdh3, 1);
//			data=data+sdh1+sd1dz+sdh2+sd2dz+sdh3+sd3dz;
//		}
//		
//        
//		// �塢����
//		String mc = "ʱ�ι��ض�ֵ";
//
//		// ���ù����ӿ�
//		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
//				jdbcT);
//
//		// д���������ݴ��
//		writeCsszzcb(seq_sjzfs, "AFN04F41", csz, jdbcT);
//
//		return seq_sjzfs;
//	}
	
//	/**
//	 * �������������ʿ��ƵĹ��ʼ��㻬��ʱ������F43(AFN=04H)
//	 * 
//	 * @param xzqxm
//	 *            String ����������
//	 * @param zddz
//	 *            String �ն˵�ַ
//	 * @param csz
//	 *            String ����ʱ��(1~60)
//	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
//	 * @throws Exception
//	 */
//	public String sendAFN04F43(String txfs, String xzqxm, String zddz,
//			String  csz) throws Exception {
//
//		String seq_sjzfs = null;
//
//		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//
//		// һ��������
//		String kzm = "4A";
//
//		// ����Ӧ�ù�����
//		String afn = "04"; // ��������
//
//		// �������ݵ�Ԫ��ʶ
//		String xxd = "P0"; // ��Ϣ��
//		String xxl = "F43"; // ��Ϣ��
//
//		// �ġ����ݵ�Ԫ
//		String data = "";
//		data = Util.decStrToHexStr(csz, 1);
//
//		// �塢����
//		String mc = "���ʿ��ƵĹ��ʼ��㻬��ʱ��";
//
//		// ���ù����ӿ�
//		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
//				jdbcT);
//
//		// д���������ݴ��
//		writeCsszzcb(seq_sjzfs, "AFN04F43", csz, jdbcT);
//
//		return seq_sjzfs;
//	}
	

	/**
	 * �����������ն������澯����/��ֹ����F57(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1) cs1:��������ʱ�α�־(0-23��,�м���"#"����)--ʱ������ģʽ
	 *            ����"0#3#5"��ʾ0�㡢3���5����������
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F57(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F57"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ��������ʱ�α�־(0-23��,�м���"#"����)--ʱ������ģʽ
		// ���"0#3#5"��ʾ0�㡢3���5����������
		String[] ss = csz.split("#");
		String temps = "000000000000000000000000";
		for (int i = 0; i < ss.length; i++) {
			int tempi = Integer.parseInt(ss[i]);
			temps = temps.substring(0, 23 - tempi) + "1"
					+ temps.substring(24 - tempi);
		}
		data += Util.convertStr(Util.binStrToHexStr(temps, 3));

		// �塢����
		String mc = "[AFN04F57]�ն������澯����/��ֹ����";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F57", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �������������ܱ��쳣�б���ֵ�趨F59(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;cs2;cs3;cs4) cs1:���������ֵ(���ݸ�ʽ22,x.x)
	 *            cs2:���ܱ���߷�ֵ(���ݸ�ʽ22,x.x) cs3:���ܱ�ͣ�߷�ֵ(��λ:15min)
	 *            cs4:���ܱ�Уʱ��ֵ(��λ:min)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F59(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F59"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");

		// ���������ֵ(���ݸ�ʽ22,x.x)
		String cs1 = ss_csz[0];
		data += Util.makeFormat22(cs1);

		// ���ܱ���߷�ֵ(���ݸ�ʽ22,x.x)
		String cs2 = ss_csz[1];
		data += Util.makeFormat22(cs2);

		// ���ܱ�ͣ�߷�ֵ(��λ:15min)
		String cs3 = ss_csz[2];
		data += Util.decStrToHexStr(cs3, 1);

		// ���ܱ�Уʱ��ֵ(��λ:min)
		String cs4 = ss_csz[3];
		data += Util.decStrToHexStr(cs4, 1);

		// �塢����
		String mc = "[AFN04F59]���ܱ��쳣�б���ֵ�趨";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F59", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ����������г����ֵ����F60(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1;...;cs8) cs1:�ܻ����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
	 *            cs2:���г����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
	 *            cs3:ż��г����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
	 *            cs4:��ż��г����ѹ����������ֵ(pz2#pz4#pz6#...#pz18)
	 *            cs5:�����г����ѹ����������ֵ(pz3#pz5#pz6#...#pz19)
	 *            cs6:�ܻ��������Чֵ����ֵ(��ʽ06,xx.xx,��λ:A)
	 *            cs7:��ż��г��������Чֵ����ֵ(pz2#pz4#pz6#...#pz18)
	 *            cs8:�����г��������Чֵ����ֵ(pz3#pz5#pz6#...#pz19)
	 * 
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F60(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F60"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");
		String[] ss = null;

		// �ܻ����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
		String cs1 = ss_csz[0];
		data += Util.makeFormat05("0", cs1);

		// ���г����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
		String cs2 = ss_csz[1];
		data += Util.makeFormat05("0", cs2);

		// ż��г����ѹ����������ֵ(��ʽ05,xxx.x,��λ:%)
		String cs3 = ss_csz[2];
		data += Util.makeFormat05("0", cs3);

		// ��ż��г����ѹ����������ֵ(pz2#pz4#pz6#...#pz18)
		String cs4 = ss_csz[3];
		ss = cs4.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat05("0", ss[i]);
		}

		// �����г����ѹ����������ֵ(pz3#pz5#pz6#...#pz19)
		String cs5 = ss_csz[4];
		ss = cs5.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat05("0", ss[i]);
		}

		// �ܻ��������Чֵ����ֵ(��ʽ06,xx.xx,��λ:A)
		String cs6 = ss_csz[5];
		data += Util.makeFormat06("0", cs6);

		// ��ż��г��������Чֵ����ֵ(pz2#pz4#pz6#...#pz18)
		String cs7 = ss_csz[6];
		ss = cs7.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat06("0", ss[i]);
		}

		// �����г��������Чֵ����ֵ(pz3#pz5#pz6#...#pz19)
		String cs8 = ss_csz[7];
		ss = cs8.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat06("0", ss[i]);
		}

		// �塢����
		String mc = "[AFN04F60]г����ֵ����";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F60", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ����������ֱ��ģ�����������F61(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ(cs1)
	 *            cs1:ֱ��ģ���������־(1-8·�����־,1:����;0:������;�磺10100001��ʾ��1/2/8·����)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F61(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String s_sql = "";
		String[] params = null;
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F61"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String cs1 = csz;
		data += Util.binStrToHexStr(Util.convertStrODD(cs1), 1);

		// ��"�ն�ֱ��ģ�������ñ�"ȡֱ��ģ����������Ϣ
		s_sql = "select zlmnldkh from g_zdzlmnlpzb where zdid=? ";
		params = new String[] { zdid };
		List zlmnlList = jdbcT.queryForList(s_sql, params);

		String zlmnlIn = "";
		for (int m = 0; m < 8; m++) {
			String xh = String.valueOf(m + 1);
			String flg = csz.substring(m, m + 1);

			boolean isIn = false;
			for (int j = 0; j < zlmnlList.size(); j++) {
				Map tempHM = (Map) zlmnlList.get(j);
				if (String.valueOf(tempHM.get("zlmnldkh")).equals(xh)) {
					isIn = true;
					break;
				}
			}

			if (isIn == false && flg.equals("1")) {
				s_sql = "insert into g_zdzlmnlpzb(zdid,zlmnldkh) "
						+ "values(?,?)";
				params = new String[] { zdid, xh };
				jdbcT.update(s_sql, params);
			}

			if (flg.equals("1")) {
				zlmnlIn += "'" + xh + "',";
			}
		}

		// ɾ������ģ��������
		s_sql = "delete g_zdzlmnlpzb " + "where zdid=? ";
		if (zlmnlIn.length() > 0) {
			s_sql += "and zlmnldkh not in("
					+ zlmnlIn.substring(0, zlmnlIn.length() - 1) + ")";
		}
		params = new String[] { zdid };
		jdbcT.update(s_sql, params);

		// �塢����
		String mc = "[AFN04F61]ֱ��ģ���������������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F61", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն�1��������������F65(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rwh
	 *            String �����
	 * @param csz
	 *            String ����ֵ(cs1;...;cs5) 
	 *            cs1:�ϱ�����(0-31)
	 *            cs2:�ϱ����ڵ�λ(0~3���α�ʾ�֡�ʱ���ա���) 
	 *            cs3:�ϱ���׼ʱ��(������ʱ����,yymmddhhmmss)
	 *            cs4:��������/ֹͣ��־�� �á�55�����������á�AA��ֹͣ
	 *            cs5:������Ϣ�� P1@F1#P1F2#....PnFn
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F65(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + rwh; // ��Ϣ��
		String xxl = "F65"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");

		// �ϱ�����
		String cs1 = ss_csz[0];
		String data1 = Util.decStrToBinStr(cs1, 1).substring(2);
		// �ϱ����ڵ�λ
		String cs2 = ss_csz[1];
		String data2 = Util.decStrToBinStr(cs2, 1).substring(6, 8);
		data += Util.binStrToHexStr(data2 + data1, 1);

		// �ϱ���׼ʱ��
		String cs3 = ss_csz[2];
		Date fsjzsj= DateUtil.parse(cs3);
		String bw_cs3=DateUtil.formatDate(fsjzsj, "yyMMddHHmmss");
		data += Util.convertStr(bw_cs3);
		
		// ��������/ֹͣ��־�� �á�55H�����������á�AAH��ֹͣ
		String cs4 = ss_csz[3];
		data += cs4;// ����������־
		int num=0;
		String[] ss=null;
		if(ss_csz.length<5){
			data +="";
		}else{
			// ����������
			String cs5 = ss_csz[4];
			ss= cs5.split("#");
	
			// ���ݵ�Ԫ��ʶ����
			num = ss.length;
			data += Util.decStrToHexStr(num, 1);
	
			// �����ݵ�Ԫ��ʶ
			for (int i = 0; i < num; i++) {
				// <--------------ÿ�����ݵ�Ԫ��ʶ��-------------->
				String each = ss[i];
				String[] dadt = each.split("@");
				String da = Util.getDA(dadt[0]);// ��Ϣ��DA
				String dt = Util.getDT(dadt[1]);// ��Ϣ��DT
	            data += Util.convertStr(da) + Util.convertStr(dt);
			}
		}
		

		// �塢����
		String mc = "[AFN04F65]�ն�1��������������(" + rwh + "������)";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		String sSql = "";

		// �����ն��������ñ���Ӧ�����Ƿ��Ѵ���
		sSql = "select rwid from g_zdrwpzb "
				+ "where zdid=? and rwlx=? and rwh=?";
		List lst = jdbcT.queryForList(sSql, new String[] { zdid, "1", rwh });
		int count = lst.size();
		String rwid = "";
		// д���ն��������ñ�
		Object[] params = null;
		if (count > 0) {
			// update
			rwid = String.valueOf(((Map) lst.get(0)).get("rwid"));
			sSql = "update g_zdrwpzb " + "set fszq=?,zqdw=?,fsjzsj=?"
					+ ",qybz=?,sjzfsseq=? " + "where rwid=?";
			
			params = new Object[] { cs1, cs2, fsjzsj, cs4,seq_sjzfs, rwid };
			jdbcT.update(sSql, params);

		} else {
			// insert
			// rwid = Util.getSeq(jdbcT, "seq_rwid");
			rwid = Util.getSeqRwid(jdbcT);
			sSql = "insert into g_zdrwpzb(rwid,zdid,rwlx,rwh,fszq,zqdw,fsjzsj,qybz,sjzfsseq) "
					+ "values( ?,?,?,?,?,?,?,?,?)";
			params = new Object[] {  rwid,zdid, "1", rwh, cs1, cs2, fsjzsj, 
					cs4, seq_sjzfs };
			jdbcT.update(sSql, params);

		}
		// д��������Ϣ���
		sSql = "delete g_rwxxx where rwid=?";
		params = new String[] { rwid };
		jdbcT.update(sSql, params);

		for (int i = 0; i < num; i++) {
			// <--------------ÿ�����ݵ�Ԫ��ʶ��-------------->
			String each = ss[i];
			String[] dadt = each.split("@");
			String da = dadt[0];// ��Ϣ��DA
			String dt = dadt[1];// ��Ϣ��DT
			String xh = String.valueOf(i + 1);
			sSql = "insert into g_rwxxx(rwid,xxdh,xxxdm,xh) "
					+ "values(?,?,?,?)";
			params = new String[] { rwid, da, dt, xh };
			jdbcT.update(sSql, params);
		}
		return seq_sjzfs;
	}

	/**
	 * �����������ն�2��������������F66(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rwh
	 *            String �����
	 * @param csz
	 *            String ����ֵ(cs1;...;cs5) cs1:�ϱ�����(0-31)
	 *            cs2:�ϱ����ڵ�λ(0~3���α�ʾ�֡�ʱ���ա���) cs3:�ϱ���׼ʱ��(������ʱ����,yymmddhhmmss)
	 *            cs4:��ȡ����(1-96) cs5:����������(Pm@Fm#...#Pn@Fn)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F66(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + rwh; // ��Ϣ��
		String xxl = "F66"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss_csz = csz.split(";");

		// �ϱ�����
		String cs1 = ss_csz[0];
		String data1 = Util.decStrToBinStr(cs1, 1).substring(2);
		// �ϱ����ڵ�λ
		String cs2 = ss_csz[1];
		String data2 = Util.decStrToBinStr(cs2, 1).substring(6, 8);
		data += Util.binStrToHexStr(data2 + data1, 1);

		// �ϱ���׼ʱ��
		String cs3 = ss_csz[2];
		data += Util.convertStr(cs3);

		// ��ȡ����
		String cs4 = ss_csz[3];
		data += Util.decStrToHexStr(cs4, 1);

		// ����������
		String cs5 = ss_csz[4];
		String[] ss = cs5.split("#");

		// ���ݵ�Ԫ��ʶ����
		int num = ss.length;
		data += Util.decStrToHexStr(num, 1);

		// �����ݵ�Ԫ��ʶ
		for (int i = 0; i < num; i++) {
			// <--------------ÿ�����ݵ�Ԫ��ʶ��-------------->
			String each = ss[i];
			String[] dadt = each.split("@");
			String da = Util.getDA(dadt[0]);// ��Ϣ��DA
			String dt = Util.getDT(dadt[1]);// ��Ϣ��DT

			data += Util.convertStr(da) + Util.convertStr(dt);
		}

		// �塢����
		String mc = "[AFN04F66]�ն�2��������������(" + rwh + "������)";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		String sSql = "";

		// �����ն��������ñ���Ӧ�����Ƿ��Ѵ���
		sSql = "select rwid from g_zdrwpzb "
				+ "where zdid=? and rwlx=? and rwh=?";
		List lst = jdbcT.queryForList(sSql, new String[] { zdid, "2", rwh });
		int count = lst.size();
		String rwid = "";
		if (count > 0) {
			rwid = String.valueOf(((Map) lst.get(0)).get("rwid"));
		}
		// д���ն��������ñ�
		String[] params = null;
		if (count > 0) {
			// update
			rwid = String.valueOf(((Map) lst.get(0)).get("rwid"));
			sSql = "update g_zdrwpzb " + "set fszq=?,zqdw=?,fsjzsj=?"
					+ ",cqbl=?,qybz=?,sjzfsseq=? " + "where rwid=?";
			params = new String[] { cs1, cs2, cs3, cs4, "AA", seq_sjzfs, rwid };
			jdbcT.update(sSql, params);

		} else {
			// insert
			// rwid = Util.getSeq(jdbcT, "seq_rwid");
			rwid = Util.getSeqRwid(jdbcT);
			sSql = "insert into g_zdrwpzb(rwid,zdid,rwlx,rwh,fszq,zqdw,fsjzsj,cqbl,qybz,sjzfsseq) "
					+ "values(?,?,?,?,?,?,?,?,?,?)";
			params = new String[] { rwid, zdid, "2", rwh, cs1, cs2, cs3, cs4,
					"AA", seq_sjzfs };
			jdbcT.update(sSql, params);

		}

		// д��������Ϣ���
		sSql = "delete g_rwxxx where rwid=?";
		params = new String[] { rwid };
		jdbcT.update(sSql, params);

		for (int i = 0; i < num; i++) {
			// <--------------ÿ�����ݵ�Ԫ��ʶ��-------------->
			String each = ss[i];
			String[] dadt = each.split("@");
			String da = dadt[0];// ��Ϣ��DA
			String dt = dadt[1];// ��Ϣ��DT
			String xh = String.valueOf(i + 1);
			sSql = "insert into g_rwxxx(rwid,xxdh,xxxdm,xh) "
					+ "values(?,?,?,?)";
			params = new String[] { rwid, da, dt, xh };
			jdbcT.update(sSql, params);
		}

		return seq_sjzfs;
	}

	/**
	 * ����������1��������������/ֹͣ����F67(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rwh
	 *            String �����
	 * @param csz
	 *            String ����ֵ(cs1) cs1:����������־(55:������AA��ֹͣ)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendAFN04F67(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// д���ն��������ñ�
		sSql = "update g_zdrwpzb " + "set qybz=? "
				+ "where zdid=? and rwlx=? and rwh=?";
		String[] params = new String[] { csz, zdid, "1", rwh };
		jdbcT.update(sSql, params);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + rwh; // ��Ϣ��
		String xxl = "F67"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = csz;// ����������־

		// �塢����
		String mc = "[AFN04F67]" + rwh + "������������־[" + csz + "](1����������)";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ����������2��������������/ֹͣ����F68(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rwh
	 *            String �����
	 * @param csz
	 *            String ����ֵ(cs1) cs1:����������־(55:������AA��ֹͣ)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendAFN04F68(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// д���ն��������ñ�
		sSql = "update g_zdrwpzb " + "set qybz=? "
				+ "where zdid=? and rwlx=? and rwh=?";
		String[] params = new String[] { csz, zdid, "2", rwh };
		jdbcT.update(sSql, params);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + rwh; // ��Ϣ��
		String xxl = "F68"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = csz;// ����������־

		// �塢����
		String mc = "[AFN04F68]" + rwh + "������������־[" + csz + "](2����������)";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ����������ֱ��ģ����������F81(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zlmnldkh
	 *            String ֱ��ģ�����˿ں�
	 * @param csz
	 *            String ����ֵ(cs1;cs2) cs1:ֱ��ģ����������ʼֵ(���ݸ�ʽ02;pz1#pz2#pz3)
	 *            pz1:������־(0:��;1:��)
	 *            pz2:ϵ��(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10
	 *            ^-1;6:10^-2;7:10^-3) pz3:��ֵ(xxx)
	 *            cs2:ֱ��ģ����������ֵֹ(���ݸ�ʽ02;pz1#pz2#pz3) pz1:������־(0:��;1:��)
	 *            pz2:ϵ��(0:10
	 *            ^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
	 *            pz3:��ֵ(xxx)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F81(String txfs, String xzqxm, String zddz,
			String zlmnldkh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zlmnldkh; // ��Ϣ��
		String xxl = "F81"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss = null;
		String[] ss_csz = csz.split(";");

		// ֱ��ģ����������ʼֵ(���ݸ�ʽ02;pz1#pz2#pz3)
		String cs1 = ss_csz[0];
		ss = cs1.split("#");
		cs1 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += Util.convertStr(cs1);

		// ֱ��ģ����������ֵֹ(���ݸ�ʽ02;pz1#pz2#pz3)
		String cs2 = ss_csz[1];
		ss = cs2.split("#");
		cs2 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += Util.convertStr(cs2);

		// �塢����
		String mc = "[AFN04F81]ֱ��ģ����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F81", zlmnldkh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ����������ֱ��ģ������ֵF82(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zlmnldkh
	 *            String ֱ��ģ�����˿ں�
	 * @param csz
	 *            String ����ֵ(cs1;cs2) cs1:ֱ��ģ��������(���ݸ�ʽ02;pz1#pz2#pz3)
	 *            pz1:������־(0:��;1:��)
	 *            pz2:ϵ��(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10
	 *            ^-1;6:10^-2;7:10^-3) pz3:��ֵ(xxx)
	 *            cs2:ֱ��ģ��������(���ݸ�ʽ02;pz1#pz2#pz3) pz1:������־(0:��;1:��)
	 *            pz2:ϵ��(0:10^4
	 *            ;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
	 *            pz3:��ֵ(xxx)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F82(String txfs, String xzqxm, String zddz,
			String zlmnldkh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zlmnldkh; // ��Ϣ��
		String xxl = "F82"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss = null;
		String[] ss_csz = csz.split(";");

		// ֱ��ģ��������(���ݸ�ʽ02;pz1#pz2#pz3)
		String cs1 = ss_csz[0];
		ss = cs1.split("#");
		cs1 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += cs1;

		// ֱ��ģ��������(���ݸ�ʽ02;pz1#pz2#pz3)
		String cs2 = ss_csz[1];
		ss = cs2.split("#");
		cs2 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += cs2;

		// �塢����
		String mc = "[AFN04F82]ֱ��ģ������ֵ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F82", zlmnldkh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ����������ֱ��ģ�����������F83(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zlmnldkh
	 *            String ֱ��ģ�����˿ں�
	 * @param csz
	 *            String ����ֵ(cs1) cs1:ֱ��ģ���������ܶ� 0:��ʾ������ 1:��ʾ15���Ӷ���һ��
	 *            2:��ʾ30���Ӷ���һ�� 3:��ʾ60���Ӷ���һ�� 254:��ʾ5���Ӷ���һ�� 255:��ʾ1���Ӷ���һ��
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F83(String txfs, String xzqxm, String zddz,
			String zlmnldkh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zlmnldkh; // ��Ϣ��
		String xxl = "F83"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		// ֱ��ģ�����������
		String cs1 = csz;
		data += Util.decStrToHexStr(cs1, 1);

		// �塢����
		String mc = "[AFN04F83]ֱ��ģ�����������";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN04F83", zlmnldkh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	/**
	 * ����������Զ�̿���F1(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������(ʮ����)
	 * @param csz
	 *            String csz(csz )0x33:������0xCC:�رա�
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendAFN05F1(String txfs, String xzqxm, String zddz,String cldh, String csz)
			throws Exception {
		
		
//	  	dispatch.sedAscendSms();
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//		Dispatch dispatch = (Dispatch)Context.ctx.getBean("dispatchService");
//		//ȡ���û����ƺ��û��ֻ�����
//		String[] hm_yhsjhm=Util.getHmAndYhsjhm(xzqxm, zddz, jdbcT);
//		//���Ͷ���
//		dispatch.sedAscendSms(hm_yhsjhm[1], hm_yhsjhm[0]+"��������բ[ͩ�繩���]", true);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P"+cldh; // ��Ϣ��
		String xxl = "F1"; // ��Ϣ��
		
		

		// �塢����
		String mc = "Զ�̿���";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, csz, mc,
				jdbcT);
		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN05F1", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
    
	/**
	 * ������������Ƶ������F2(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������(ʮ����)
	 * @param csz
	 *            String csz(csz )����Ƶ�ʡ�
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendAFN05F2(String txfs, String xzqxm, String zddz,String cldh, String csz)
			throws Exception {
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P"+cldh; // ��Ϣ��
		String xxl = "F2"; // ��Ϣ��

		// ���Ƶ�Ƶ��
		csz= Util.decStrToHexStr(csz, 1);

		// �塢����
		String mc = "Զ�̿���";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, csz, mc,
				jdbcT);
		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN05F2", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	
	/**
	 * ����������Զ��ͼ��ץ��F3(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������(ʮ����)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendAFN05F3(String txfs, String xzqxm, String zddz,String cldh)
			throws Exception {
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P"+cldh; // ��Ϣ��
		String xxl = "F3"; // ��Ϣ��

	

		// �塢����
		String mc = "Զ��ͼ��ץ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, "", mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ���������������ն������ϱ�F29(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendAFN05F29(String txfs, String xzqxm, String zddz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F29"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";

		// �塢����
		String mc = "�����ն������ϱ�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �����������ն˶�ʱF31(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����(��ʽ��yymmddhhmmss��"XX"��ʾϵͳʱ��)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 */
	public String sendAFN05F31(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F31"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		if (csz.equalsIgnoreCase("XX")) {
			data = Util.makeFormat01(Util.getNowTime(), Util.getNowWeek());
		} else {
			data = Util.makeFormat01(csz, Util.getWeek(csz));
		}

		// �塢����
		String mc = "�ն˶�ʱ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ���������������㹦�������ֶ���ֵ����F28(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param xz1
	 *            String ��ֵ1#����
	 * @param xz2
	 *            String ��ֵ2#����
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCldglysfdxz(String txfs, String xzqxm, String zddz,
			String cldh, String xz1, String xz2) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = "F28"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] ss = null;

		// ��ֵ1
		ss = xz1.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);
		// ��ֵ2
		ss = xz2.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);

		// �塢����
		String mc = "���ò����㹦�������ֶ���ֵ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String temps = cldh + "@" + xz1 + ";" + xz2 + ";";
		writeCsszzcb(seq_sjzfs, "cldglysfdxz", temps, jdbcT); // �����㹦�������ֶ���ֵ

		return seq_sjzfs;
	}

	/**
	 * ��������������������Ͷ���־F41/F42(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cldh
	 *            String �������
	 * @param trbz
	 *            String Ͷ���־(55:Ͷ��;AA:�г�)
	 * @param drqz
	 *            String ��������(D15-D0,��1��ʾͶ����г�,��0��ʾ����ԭ״)
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendDrqkztrbz(String txfs, String xzqxm, String zddz,
			String cldh, String trbz, String drqz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + cldh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (trbz.equals("55")) {
			xxl = "F41";
		} else if (trbz.equals("AA")) {
			xxl = "F42";
		}

		// �ġ����ݵ�Ԫ
		String data = "";
		data = Util.binStrToHexStr(drqz, 2);

		// �塢����
		String mc = "";
		if (trbz.equals("55")) {
			mc = "����������Ͷ��";
		} else if (trbz.equals("AA")) {
			mc = "�����������г�";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		String temps = cldh + "@" + trbz + ";" + drqz;
		writeCsszzcb(seq_sjzfs, "drqkztrbz", temps, jdbcT); // ����������Ͷ���־

		return seq_sjzfs;
	}

	/**
	 * ����������������ϢF32(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zl
	 *            String ������Ϣ����
	 * @param bh
	 *            String ������Ϣ���
	 * @param hzxx
	 *            String ������Ϣ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendZwxx(String txfs, String xzqxm, String zddz, String zl,
			String bh, String hzxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F32"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ������Ϣ����
		String zwxxzl = Util.decStrToBinStr(zl, 1);
		zwxxzl = zwxxzl.substring(4);
		// ������Ϣ���
		String zwxxbh = Util.decStrToBinStr(bh, 1);
		zwxxbh = zwxxbh.substring(4);

		data += Util.binStrToHexStr(zwxxzl + zwxxbh, 1);

		// ������Ϣ����
		String zwxxnr = "";
		byte[] bt = hzxx.getBytes("GB2312");
		data += Util.decStrToHexStr(bt.length, 1);

		zwxxnr = Util.bytetostrs(bt);
		// data += Util.convertStr(zwxxnr);
		data += zwxxnr;

		// �塢����
		String mc = "������Ϣ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ���������������ִ��趨F45(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param lc
	 *            String[8] �ִ��ܿ����(lc[0]-lc[7]:��1��-��8��,0:���ܿأ�1���ܿ�)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendGklc(String txfs, String xzqxm, String zddz, int zjzh,
			String[] lc) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F45"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		for (int i = 0; i < 8; i++) {
			data = lc[i] + data;
		}
		data = Util.binStrToHexStr(data, 1);

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "�Ĺ����ִ�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д"�ն��ܼ������ñ�"
		String sSql = "update zdzjzpzb set gklc1=?,gklc2=?,"
				+ "gklc3=?,gklc4=?,gklc5=?," + "gklc6=?,gklc7=?,gklc8=? "
				+ "where xzqxm=? and zddz=? and zjzxh=?";
		String[] params = new String[] { lc[0], lc[1], lc[2], lc[3], lc[4],
				lc[5], lc[6], lc[7], xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		return seq_sjzfs;
	}

	/**
	 * �������������ʿ��ƵĹ��ʼ��㻬��ʱ������F43(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param hcsj
	 *            String ����ʱ��(1~60)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F43(String txfs, String xzqxm, String zddz,
			int zjzh, String hcsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F43"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		data = Util.decStrToHexStr(hcsj, 1);
		
		// д"�ն��ܼ������ñ�"
		String this_sql = "update g_zdzjzpzb set GLJSHCSJ=? where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?)  and zjzxh=?";
		String[] this_params = new String[] {hcsj,xzqxm, zddz, String.valueOf(zjzh)};
		jdbcT.update(this_sql, this_params);

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "�Ĺ��ʼ��㻬��ʱ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "gljshcsj", zjzh + "#" + hcsj, jdbcT); // ���ʼ��㻬��ʱ��

		return seq_sjzfs;
	}

	/**
	 * ����������ʱ�ι���Ͷ���־����F9/F17(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param trbz
	 *            String Ͷ���־(55:Ͷ�룻AA�����)
	 * @param fabh
	 *            String �������(��trbz=AAʱ��fabh=null)
	 * @param trsd
	 *            String[] Ͷ��ʱ��(��trbz=AAʱ��trsd=null)(trsd[i]��ʾ��Ͷ���ʱ�κ�)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN05F9F17(String txfs, String xzqxm, String zddz,
			int zjzh, String trbz, String fabh, String[] trsd) throws Exception {

		String seq_sjzfs = null;
		String temp_dadt = "";
		String temp_sjdybsmc = "";
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (trbz.equals("55")) {
			xxl = "F9";
		} else if (trbz.equals("AA")) {
			xxl = "F17";
		}

		// �ġ����ݵ�Ԫ
		String data = "";
		String[] params = null;
		if (trbz.equals("55")) {
			// д"�ն��ܼ������ñ�"(ʱ�ι���Ͷ��)
			sSql = "update g_zdzjzpzb set sdgktrbz=? "
					+ "where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?) " + "and zjzxh=? ";
			params = new String[] { fabh, xzqxm, zddz, String.valueOf(zjzh) };
			jdbcT.update(sSql, params);

			String sdgktrbz = "00000000";
			// ʱ�ε�Ͷ���־����Ϊ"���"
			sSql = "update zdsdgkpzb set sdgktrbz='AA' "
					+ "where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?) " + "and zjzxh=? and fah=?";
			params = new String[] { xzqxm, zddz, String.valueOf(zjzh), fabh };
			jdbcT.update(sSql, params);

			if (trsd != null) {
				int trsd_len = trsd.length;

				for (int i = 0; i < trsd_len; i++) {
					int i_trsd = Integer.parseInt(trsd[i]);
					sdgktrbz = sdgktrbz.substring(0, 8 - i_trsd) + "1"
							+ sdgktrbz.substring(8 - i_trsd + 1);

					// ����Ӧʱ��Ͷ���־����Ϊ"Ͷ��"
					sSql = "update zdsdgkpzb set sdtrbz='55' "
							+ "where xzqxm=? and zddz=? "
							+ "and zjzxh=? and fah=? and sdh=? ";
					params = new String[] { xzqxm, zddz, String.valueOf(zjzh),
							fabh, String.valueOf(i_trsd) };
					jdbcT.update(sSql, params);
				}

			}
			// 0-2��ʾ������1-3
			data = Util.binStrToHexStr(sdgktrbz, 1)
					+ Util.decStrToHexStr(Integer.parseInt(fabh) - 1, 1);

		} else if (trbz.equals("AA")) {
			// д"�ն��ܼ������ñ�"(ʱ�ι��ؽ��)
			sSql = "update zdzjzpzb set sdgktrbz='AA' "
					+ "where xzqxm=? and zddz=? and zjzxh=? ";
			params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
			jdbcT.update(sSql, params);

		}

		// �塢����
		String mc = "";
		if (trbz.equals("55")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "ʱ�ι���Ͷ��";
		} else if (trbz.equals("AA")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "ʱ�ι��ؽ��";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	

	/**
	 * �������������ݹ���Ͷ���־����F10/F18(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param trbz
	 *            String Ͷ���־(55:Ͷ�룻AA�����)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCxgktrbz(String txfs, String xzqxm, String zddz,
			int zjzh, String trbz) throws Exception {

		String seq_sjzfs = null;
		String temp_dadt = "";
		String temp_sjdybsmc = "";
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (trbz.equals("55")) {
			xxl = "F10";
		} else if (trbz.equals("AA")) {
			xxl = "F18";
		}

		// �ġ����ݵ�Ԫ
		String data = ""; // �����ݵ�Ԫ

		// �塢����
		String mc = "";
		if (trbz.equals("55")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "���ݹ���Ͷ��";
		} else if (trbz.equals("AA")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "���ݹ��ؽ��";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "cxgktrbz", zjzh + "#" + trbz, jdbcT); // ���ݹ���Ͷ���־

		return seq_sjzfs;
	}

	/**
	 * ����������Ӫҵ��ͣ��Ͷ���־����F11/F19(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param trbz
	 *            String Ͷ���־(55:Ͷ�룻AA�����)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendYybtktrbz(String txfs, String xzqxm, String zddz,
			int zjzh, String trbz) throws Exception {

		String seq_sjzfs = null;
		String temp_dadt = "";
		String temp_sjdybsmc = "";
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "05"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (trbz.equals("55")) {
			xxl = "F11";
		} else if (trbz.equals("AA")) {
			xxl = "F19";
		}

		// �ġ����ݵ�Ԫ
		String data = ""; // �����ݵ�Ԫ

		// �塢����
		String mc = "";
		if (trbz.equals("55")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "Ӫҵ��ͣ��Ͷ��";
		} else if (trbz.equals("AA")) {
			mc = "�����ܼ���" + String.valueOf(zjzh) + "Ӫҵ��ͣ�ؽ��";
		}

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "yybtktrbz", zjzh + "#" + trbz, jdbcT); // Ӫҵ��ͣ��Ͷ���־

		return seq_sjzfs;
	}

	/**
	 * ����������ʱ�ι��ض�ֵ����F41(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param sd
	 *            Map ʱ�ι��ض�ֵ{key=fah(������ String:1-3); value=sddz(ʱ�ζ�ֵ
	 *            String[][]) < �밴ʱ�κ�������; sddz[i][0]:ʱ�κţ� sddz[i][1]:����(0������1����)
	 *            sddz[i][2]:ʱ�ζ�ֵ��>=1,<=999�� sddz[i][3]:ϵ��(���չ�Լ,�磺000=10E4...��
	 *            >}
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN04F41(String txfs, String xzqxm, String zddz, int zjzh,
			HashMap sd) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F41"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// ɾ��"�ն�ʱ�ι������ñ�"�и��ܼ����ʱ�ι�������
		sSql = "delete g_zdsdgkpzb " + "where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?) and zjzxh=?";
		String[] params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// ������־
		String fabz = "000";// ������־(D0~D2��ʾ����1~3)
		Object[] fah = (Object[]) sd.keySet().toArray();
		int fa_len = fah.length;
		for (int i = 0; i < fa_len; i++) {
			String temp_fah = String.valueOf(fah[i]);
			if (temp_fah.equals("1")) {
				fabz = fabz.substring(0, 2) + "1";
			} else if (temp_fah.equals("2")) {
				fabz = fabz.substring(0, 1) + "1" + fabz.substring(2, 3);
			} else if (temp_fah.equals("3")) {
				fabz = "1" + fabz.substring(1, 3);
			}
		}
		data = data + Util.binStrToHexStr(fabz, 1);// ������־

		for (int i = 1; i <= 3; i++) {
			if (fabz.substring(3 - i, 3 - i + 1).equals("0")) {
				continue;
			}
			String temp_fah = String.valueOf(i);// ������
			String[][] array_sddz = (String[][]) sd.get(temp_fah);
			int array_len = array_sddz.length;

			String sdbz = "00000000";// ʱ�α�־
			String sddz = "";// ��ʱ�ζ�ֵ

			for (int j = 0; j < array_len; j++) {
				String temp_sdh = array_sddz[j][0];// ʱ�κ�
				String temp_zf = array_sddz[j][1];// ����
				String temp_sddz = array_sddz[j][2];// ʱ�ζ�ֵ
				String temp_xs = array_sddz[j][3];// ϵ��

				String hex_sddz = Util
						.makeFormat02(temp_sddz, temp_xs, temp_zf);
				int i_sdh = Integer.parseInt(temp_sdh);
				sdbz = sdbz.substring(0, 8 - i_sdh) + "1"
						+ sdbz.substring(8 - i_sdh + 1);

				sddz += hex_sddz;

				// д"�ն�ʱ�ι������ñ�"
				sSql = "insert into g_zdsdgkpzb(id,zdid,zjzxh,fah,sdh,sdgkdz,sdgkdzfh,sdgkdzxs) "
						+ "values( SEQ_GZDSDGKPZID.nextVal,(select zdid from G_ZDGZ where xzqxm=? and zddz=?),?,?,?,?,?,?)";
				params = new String[] { xzqxm, zddz, String.valueOf(zjzh),
						temp_fah, temp_sdh, temp_sddz, temp_zf, temp_xs };
				jdbcT.update(sSql, params);
			}

			data += Util.binStrToHexStr(sdbz, 1) + sddz;// ʱ�α�־����ʱ�ζ�ֵ
		}

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "��ʱ�ι��ض�ֵ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �������������ݹ��ز�������F42(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param cxkdz
	 *            String ���ݿض�ֵ��>=1,<=999��
	 * @param dzzf
	 *            String ���ݿض�ֵ����(0������1����)
	 * @param dzxs
	 *            String ���ݿض�ֵϵ��(���չ�Լ,�磺000=10E4...��
	 * @param xdqssj
	 *            String �޵���ʼʱ��(hhmm)
	 * @param xdyxsj
	 *            String �޵�����ʱ��(1~48,��λ��0.5h)
	 * @param mzxdr
	 *            String ÿ���޵���(7λ�ַ���,D7~D1�ֱ��ʾ���յ���һ)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendCxgkcs(String txfs, String xzqxm, String zddz, int zjzh,
			String cxkdz, String dzzf, String dzxs, String xdqssj,
			String xdyxsj, String mzxdr) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F42"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String hex_cxkdz = Util.makeFormat02(cxkdz, dzxs, dzzf);
		data = data + hex_cxkdz;// ���ݿض�ֵ
		data = data + Util.convertStr(xdqssj);// �޵���ʼʱ��
		data = data + Util.decStrToHexStr(xdyxsj, 1);// �޵�����ʱ��
		data = data + Util.binStrToHexStr(mzxdr + "0", 1);// ÿ���޵���

		String[] params = null;
		// ɾ��"�ն˳��ݿؿ����ñ�"�и��ܼ���ĳ��ݿ�����
		sSql = "delete zdcxgkpzb " + "where xzqxm=? and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// д"�ն˳��ݿؿ����ñ�"
		sSql = "insert into zdcxgkpzb(xzqxm,zddz,zjzxh,cxkdz,cxkdzfh,cxkdzxs,xdqssj,xdyxsj,mzxdr) "
				+ "values(?,?,?,?,?,?,?,?,?)";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), cxkdz, dzzf,
				dzxs, xdqssj, xdyxsj, mzxdr };
		jdbcT.update(sSql, params);

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "�ĳ��ݿز���";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ����������Ӫҵ��ͣ�ز�������F44(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param zjzh
	 *            int �ܼ����
	 * @param btqssj
	 *            String ��ͣ��ʼʱ��(yymmdd)
	 * @param btjssj
	 *            String ��ͣ����ʱ��(yymmdd)
	 * @param btkgldz
	 *            String ��ͣ�ع��ʶ�ֵ��>=1,<=999��
	 * @param dzzf
	 *            String ��ͣ�ع��ʶ�ֵ����(0������1����)
	 * @param dzxs
	 *            String ��ͣ�ع��ʶ�ֵϵ��(���չ�Լ,�磺000=10E4...��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendYybtkcs(String txfs, String xzqxm, String zddz, int zjzh,
			String btqssj, String btjssj, String btkgldz, String dzzf,
			String dzxs) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "04"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + zjzh; // ��Ϣ��
		String xxl = "F44"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		String hex_btkgldz = Util.makeFormat02(btkgldz, dzxs, dzzf);
		data = data + Util.convertStr(btqssj);// ��ͣ��ʼʱ��
		data = data + Util.convertStr(btjssj);// ��ͣ����ʱ��
		data = data + hex_btkgldz;// ��ͣ���ʶ�ֵ

		String[] params = null;
		// ɾ��"�ն�Ӫҵ��ͣ�����ñ�"�и��ܼ����Ӫҵ��ͣ������
		sSql = "delete zdyybtkpzb " + "where xzqxm=? and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// д"�ն�Ӫҵ��ͣ�����ñ�"
		sSql = "insert into zdyybtkpzb(xzqxm,zddz,zjzxh,btkgldz,btkgldzfh,btkgldzxs,btqssj,btjssj) "
				+ "values(?,?,?,?,?,?,?,?)";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), btkgldz,
				dzzf, dzxs, btqssj, btjssj };
		jdbcT.update(sSql, params);

		// �塢����
		String mc = "�����ܼ���" + String.valueOf(zjzh) + "��Ӫҵ��ͣ�ز���";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ��������F1/F2(AFN=0BH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param rwlx
	 *            String ��������(1:1�ࣻ2:2��)
	 * @param rwh
	 *            int �����
	 * @param qssj
	 *            String ��ʼʱ��(��ʽ:yymmddhhmm)
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String queryRwsj(String txfs, String xzqxm, String zddz,
			String rwlx, int rwh, String qssj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4B";

		// ����Ӧ�ù�����
		String afn = "0B"; // �������ݲ�ѯ
		String temp_afn = "";

		// �������ݵ�Ԫ��ʶ
		String xxd = "P" + rwh; // ��Ϣ��
		String xxl = ""; // ��Ϣ��
		if (rwlx.equals("1")) {
			xxl = "F1";
			temp_afn = "0C";
		} else if (rwlx.equals("2")) {
			xxl = "F2";
			temp_afn = "0D";
		}

		// �ġ����ݵ�Ԫ
		String data = "";
		if (rwlx.equals(2)) {
			data = Util.convertStr(qssj);
		}

		// �塢����
		String mc = "��ѯ" + String.valueOf(rwh) + "����������(" + rwlx + "����������)";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, temp_afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ��������������֪ͨF2(AFN=0FH) �����֣�01
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param wjm
	 *            String �ļ���
	 * @param wjnr
	 *            byte[] �ļ�����
	 * @param ip
	 *            String IP��ַ
	 * @param port
	 *            String �˿�
	 * @param cxmklx
	 *            String ����ģ�����ͣ�01����CPU;02������CPU��
	 * @param cxjhsj
	 *            String ���򼤻�ʱ�� YYMMDDhhmm
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendXztz(String txfs, String xzqxm, String zddz, String wjm,
			byte[] wjnr, String ip, String port, String cxmklx, String cxjhsj)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��������
		String rq = Util.getNowTime();
		String xq = Util.getNowWeek();
		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4A";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "0F"; // �ļ�����

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=1)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);
		int iZdpseq = getZdpseq(iZdpfc);
		sSEQ = "7" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3�����ݵ�Ԫ��ʶDADT(DA=P0;DT=F2),����Զ������
		String sDA = Util.getDA("P0");
		sDA = Util.convertStr(sDA);

		String sDT = Util.getDT("F2");
		sDT = Util.convertStr(sDT);

		String sDADT = sDA + sDT;

		// 4�����ݵ�ԪDATA
		String sDATA = "";
		// 1)����֪ͨ
		sDATA += "01";

		// 2)����ģ������
		sDATA += Util.add(cxmklx, 1, "0");

		// 3)��ʱʱ��
		sDATA += "FF";

		// 4)ip��ַ
		ip = ip.replace('.', ';');
		String[] IP = ip.split(";");
		sDATA += Util.decStrToHexStr(IP[0], 1) + Util.decStrToHexStr(IP[1], 1)
				+ Util.decStrToHexStr(IP[2], 1) + Util.decStrToHexStr(IP[3], 1);

		// 5)�˿ں�,��λ���ȴ�
		sDATA += Util.convertStr(Util.decStrToHexStr(port, 2));

		// 6)���س����ܶ���,��λ���ȴ�,2�ֽ�
//		int count = Decode_0F.fillHM(wjm, wjnr);
		int count=0;
		sDATA += Util.convertStr(Util.decStrToHexStr(count, 2));

		// 7)���򼤻�ʱ��
		sDATA += Util.convertStr(Util.add(cxjhsj, 5, "0"));

		// 8)�����ļ���ASCII,32�ֽ�,��λ���ȴ�
		byte[] bt_wjm = wjm.getBytes();
		wjm = Util.bytetostrs(bt_wjm);
		for (int i = 0; i < 32 - bt_wjm.length; i++) {
			wjm = "20" + wjm;
		}
		sDATA += Util.convertStr(wjm);

		// 5��������ϢAUX
		String sAUX = "";
		String sPassword = CMContext.getZdmm(xzqxm, zddz);

		sAUX = sPassword;

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		int iLEN = sCSDATA.length();
		iLEN = iLEN * 2 + 1;
		String sLEN = Util.decStrToHexStr(iLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// д������֡���ͱ�(isxztz='1')
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz,isxztz) "
				+ "values(?,(select zdid from G_ZDGZ where xzqxm=? and zddz=?),?,?,?,'02',?,sysdate,?,'1')";
		String[] params = new String[] { seq_sjzfs, xzqxm, zddz, sAFN,
				sSEQ1.toUpperCase(), Util.decStrToHexStr(iZdpfc, 1),
				rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,sjdybsz,sjdybsmc) "
				+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
		params = new String[] { seq_sjzfs, sAFN, "P0F2",
				Util.convertStr(sDA) + Util.convertStr(sDT), "Զ������֪ͨ" };
		jdbcT.update(sSql, params);

		// д���ն��������ñ�
		sSql = "delete g_zdsjpzb where zdid= (select zdid from G_ZDGZ where xzqxm=? and zddz=?)";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		sSql = "insert into g_zdsjpzb(zdid,sjzt,sj) "
				+ "values((select zdid from G_ZDGZ where xzqxm=? and zddz=?),'����֪ͨ���·�',sysdate)";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		// ����
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, "", jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ��������������ȡ��F2(AFN=0FH) �����֣�02
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendXzqx(String txfs, String xzqxm, String zddz)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "0F"; // �ļ�����

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F2"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// 1)�����֣�����ȡ��
		data += "02";

		// 2)�����47���ֽڲ�00H
		data = Util.addAfter(data, 48, "0");

		// �塢����
		String mc = "Զ������ȡ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �������������ظ��ĳ��򼤻�ʱ��F2(AFN=0FH) �����֣�03
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param cxjhsj
	 *            String ���򼤻�ʱ�� YYMMDDhhmm
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendXzggcxjhsj(String txfs, String xzqxm, String zddz,
			String cxjhsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "0F"; // �ļ�����

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F2"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// 1)�����֣����ظ��ĳ��򼤻�ʱ��
		data += "03";

		// 2)������ʱ���⣬����42���ֽڲ�00H
		for (int i = 0; i < 10; i++) {
			data += "00";
		}
		data += Util.convertStr(cxjhsj);
		for (int i = 0; i < 32; i++) {
			data += "00";
		}

		// �塢����
		String mc = "���ĳ��򼤻�ʱ��";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * �������������س���汾�л�F2(AFN=0FH) �����֣�04
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendXzcxbbqh(String txfs, String xzqxm, String zddz)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "0F"; // �ļ�����

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F2"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
		// 1)�����֣����س���汾�л�
		data += "04";

		// 2)�����47���ֽڲ�00H
		data = Util.addAfter(data, 48, "0");

		// �塢����
		String mc = "����汾�л�";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ1������(AFN=0CH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sjxxx
	 *            String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ� sjxxx[i][1]
	 *            ��Ϣ�ࣨFn��
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String query_1lsj(String txfs, String xzqxm, String zddz,
			String[][] sjxxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4B";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "0C"; // ��ѯ1������

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3��n�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn),�����ݵ�Ԫ
		String sDADT = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ���ݵ��
			String xxl = sjxxx[i][1];// ������Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			String tempDADT = sDA + sDT;

			sDADT += tempDADT;
		}

		// 5��������ϢAUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698��
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);
		String[] params = null;
		// д������֡���ͱ�
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// д�����ݱ�ʶ�ӱ�
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			List lstXXX = CMContext.getSjxxx("1lsj");// 1�����ݶ�Ӧ��������Ϣ��
			String xxdlb = "";// ��Ϣ������նˡ������㡢�ܼ���...
			String xxxmc = "";// ��Ϣ������
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "�ն�";
			} else if (xxdlb.equals("1")) {
				xxdlb = "������" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "�ܼ���" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "ֱ��ģ����" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"��ѯ" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// ����
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}
	 
	/**������������ѯ���������ն˵�0CF2(AFN=0CH)
	   * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�1���ɹ���
	   */
	 public   String query_allzd_0cf2() throws Exception{
		 String seq_sjzfs = null;

			JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
			String sSql = "select xzqxm,zddz from g_zdgz where zt=1";
			List zdlist=jdbcT.queryForList(sSql);
			
			for(int n=0;n<zdlist.size();n++){
				//�ȴ�5����
				Thread.sleep(500L);
				String xzqxm = String.valueOf(((Map)zdlist.get(n)).get("xzqxm"));
				String zddz = String.valueOf(((Map)zdlist.get(n)).get("zddz"));
				
				String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

				// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
				String rq = Util.getNowTime();

				// ��������֡(ʮ�������ַ�)
				String sSJZ = "";

				// һ��������
				String sContr = "4B";

				// ������ַ��
				String sAddr = "";
				String sAddr1 = Util.convertStr(xzqxm);
				String sAddr2 = Util.convertStr(zddz);
				String sAddr3 = "02";
				sAddr = sAddr1 + sAddr2 + sAddr3;

				// ������·�û�������
				String sUSERDATA = "";
				// 1��Ӧ�ù�����
				String sAFN = "0C"; // ��ѯ1������

				// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
				String sSEQ = "";
				// ȡ���ն˵�֡��ż�����������֡���
				int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

				int iZdpseq = getZdpseq(iZdpfc);

				sSEQ = "6" + Integer.toHexString(iZdpseq);
				String sSEQ1 = Integer.toHexString(iZdpseq);

				// 3��n�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn),�����ݵ�Ԫ
				String sDADT = "";
				
				String xxdh = "0000";// ���ݵ��
				String xxl = "F2";// ������Fn

				String sDA = Util.getDA(Integer.parseInt(xxdh));
				sDA = Util.convertStr(sDA);

				String sDT = Util.getDT(xxl);
				sDT = Util.convertStr(sDT);

				String tempDADT = sDA + sDT;

				sDADT += tempDADT;
				

				// 5��������ϢAUX
				String sAUX = "";

				sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

				// У��������
				String sCSDATA = sContr + sAddr + sUSERDATA;

				// �ġ�У����
				String sCS = Util.getCS(sCSDATA);

				// �塢���ݳ���
				// 04
				// int iLEN = sCSDATA.length();
				// iLEN = iLEN * 2 + 1;
				// String sLEN = Util.decStrToHexStr(iLEN,2);
				// sLEN = Util.convertStr(sLEN);

				// 698��
				long iLEN = sCSDATA.length() / 2;
				String sLEN = Util.decStrToBinStr(iLEN, 2);
				sLEN = sLEN.substring(2) + "10";
				sLEN = Util.binStrToHexStr(sLEN, 2);
				sLEN = Util.convertStr(sLEN);

				sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
						+ sEnd;

				cat.info("sSJZ:" + sSJZ);
				String[] params = null;
				
				
				// д������֡���ͱ�
				seq_sjzfs = Util.getSeqSjzfs(jdbcT);
				sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
						+ "values(?,?,?,?,?,'02',?,sysdate,?)";
				params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
						Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
				jdbcT.update(sSql, params);

				// д�����ݱ�ʶ�ӱ�

				List lstXXX = CMContext.getSjxxx("1lsj");// 1�����ݶ�Ӧ��������Ϣ��
				String xxdlb = "";// ��Ϣ������նˡ������㡢�ܼ���...
				String xxxmc = "";// ��Ϣ������
				for (int j = 0; j < lstXXX.size(); j++) {
					Map hm = (Map) lstXXX.get(j);
					if (hm.get("xxxdm").equals(xxl)) {
						xxdlb = hm.get("xxdlb").toString();
						xxxmc = hm.get("xxxmc").toString();

						break;
					}
				}
				
				xxdlb = "�ն�";
				

				sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
						+ "sjdybsz,sjdybsmc) "
						+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
				params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
						Util.convertStr(sDA) + Util.convertStr(sDT),
						"��ѯ" + xxxmc + "(" + xxdlb + ")" };
				jdbcT.update(sSql, params);
				

				// ����
				sSql = "select sim from G_ZDGZ where zdid=?";
				params = new String[] { zdid };
				List lst = jdbcT.queryForList(sSql, params);
				Map mp = (Map) lst.get(0);
				String SIM = String.valueOf(mp.get("sim"));
				send("3", xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);
			}
			

		 return "1";
	 }

	/**
	 * ������������ѯ2����_����(AFN=0DH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sjxxx
	 *            String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ� sjxxx[i][1]
	 *            ��Ϣ�ࣨFn��
	 * @param qssj
	 *            String ��ʼʱ�� yymmddhhmm
	 * @param sjmd
	 *            String �����ܶ� 1��15���ӣ�2��30���ӣ�3��60����
	 * @param sjds
	 *            String ���ݵ���
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String query_2lsj_qx(String txfs, String xzqxm, String zddz,
			String[][] sjxxx, String qssj, String sjmd, String sjds)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4B";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "0D"; // ��ѯ2������

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn)
		String DADT_DATA = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			DADT_DATA += sDA + sDT;

			// 4�����ݵ�Ԫ
			String sDATA = "";
			// 1)��ʼʱ��
			sDATA += Util.convertStr(qssj);
			// 2)�����ܶ�
			sDATA += Util.decStrToHexStr(sjmd, 1);
			// 3)���ݵ���
			sDATA += Util.decStrToHexStr(sjds, 1);
			DADT_DATA += sDATA;
		}

		// 5��������ϢAUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + DADT_DATA + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698��
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// д������֡���ͱ�
		String[] params = null;
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// д�����ݱ�ʶ�ӱ�
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);
			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);
			List lstXXX = CMContext.getSjxxx("2lsj_qx");
			String xxdlb = "";// ��Ϣ������նˡ������㡢�ܼ���...
			String xxxmc = "";// ��Ϣ������
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "�ն�";
			} else if (xxdlb.equals("1")) {
				xxdlb = "������" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "�ܼ���" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "ֱ��ģ����" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"��ѯ" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// ����
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ2����_�ն���(AFN=0DH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sjxxx
	 *            String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ� sjxxx[i][1]
	 *            ��Ϣ�ࣨFn��
	 * @param rdjsj
	 *            String �ն���ʱ�� yymmdd
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String query_2lsj_rdj(String txfs, String xzqxm, String zddz,
			String[][] sjxxx, String rdjsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4B";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "0D"; // ��ѯ2������

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn)
		String DADT_DATA = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			DADT_DATA += sDA + sDT;

			// 4�����ݵ�Ԫ
			String sDATA = "";
			// 1)�ն���ʱ��
			sDATA += Util.convertStr(rdjsj);

			DADT_DATA += sDATA;
		}

		// 5��������ϢAUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + DADT_DATA + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698��
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// д������֡���ͱ�
		String[] params = null;
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// д�����ݱ�ʶ�ӱ�
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);
			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);
			List lstXXX = CMContext.getSjxxx("2lsj_rdj");
			String xxdlb = "";// ��Ϣ������նˡ������㡢�ܼ���...
			String xxxmc = "";// ��Ϣ������
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "�ն�";
			} else if (xxdlb.equals("1")) {
				xxdlb = "������" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "�ܼ���" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "ֱ��ģ����" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"��ѯ" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// ����
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ������������ѯ2����_�¶���(AFN=0DH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param sjxxx
	 *            String[][2] ��������Ϣ sjxxx[i][0] ��Ϣ��ţ������㡢�ܼ���ţ� sjxxx[i][1]
	 *            ��Ϣ�ࣨFn��
	 * @param ydjsj
	 *            String �¶���ʱ�� yymm
	 * 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String query_2lsj_ydj(String txfs, String xzqxm, String zddz,
			String[][] sjxxx, String ydjsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		// ��������֡(ʮ�������ַ�)
		String sSJZ = "";

		// һ��������
		String sContr = "4B";

		// ������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// ������·�û�������
		String sUSERDATA = "";
		// 1��Ӧ�ù�����
		String sAFN = "0D"; // ��ѯ2������

		// 2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3�����ݵ�Ԫ��ʶDADT(DA=Pn;DT=Fn)
		String DADT_DATA = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			DADT_DATA += sDA + sDT;

			// 4�����ݵ�Ԫ
			String sDATA = "";
			// 1)�¶���ʱ��
			sDATA += Util.convertStr(ydjsj);

			DADT_DATA += sDATA;
		}

		// 5��������ϢAUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + DADT_DATA + sAUX;

		// У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// �ġ�У����
		String sCS = Util.getCS(sCSDATA);

		// �塢���ݳ���
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698��
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// д������֡���ͱ�
		String[] params = null;
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// д�����ݱ�ʶ�ӱ�
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// ��Ϣ���
			String xxl = sjxxx[i][1];// ��Ϣ��Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);
			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);
			List lstXXX = CMContext.getSjxxx("2lsj_ydj");
			String xxdlb = "";// ��Ϣ������նˡ������㡢�ܼ���...
			String xxxmc = "";// ��Ϣ������
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "�ն�";
			} else if (xxdlb.equals("1")) {
				xxdlb = "������" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "�ܼ���" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "ֱ��ģ����" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"��ѯ" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// ����
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * ���ա���ʽ01�����·ݺ�����ƴ��һ���ֽڣ�16�����ַ���
	 * 
	 * @param yf
	 *            String �·�
	 * @param xq
	 *            String ����
	 * @return sRet ����ʮ�������ַ�
	 */
	private String getXqyf(String yf, String xq) throws Exception {
		String sRet = "";
		if (yf.length() < 2) {
			yf = "0" + yf;
		}
		// ������ת��ʮ�������ַ�
		String b_xq = Integer.toBinaryString(Integer.parseInt(xq));

		if (b_xq.length() == 1) {
			b_xq = "00" + b_xq;
		}
		if (b_xq.length() == 2) {
			b_xq = "0" + b_xq;
		}
		// ���·ݵ�ʮλ������ƴ�ɰ���ֽ�
		String b_hc = b_xq + yf.substring(0, 1);

		// ���������ַ�ת��ʮ�������ַ�
		Integer iteger = Integer.valueOf(b_hc, 2);
		String h_hc = Integer.toHexString(iteger.intValue());

		sRet = h_hc + yf.substring(1, 2);
		return sRet;
	}

	/**
	 * �����ն�ȡ֡��ż�����ȡ����֡��ţ���֡��ż������ĺ���λ��
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @return zxh ֡���
	 */
	private int getZdpseq(int iZdpfc) throws Exception {
		int i_zdpseq = 0;
		String s_pfc = Integer.toHexString(iZdpfc);
		if (s_pfc.length() < 2) {
			s_pfc = "0" + s_pfc;
		}

		String s_pseq = s_pfc.substring(1);
		if (s_pseq.equalsIgnoreCase("a")) {
			s_pseq = "10";
		} else if (s_pseq.equalsIgnoreCase("b")) {
			s_pseq = "11";
		} else if (s_pseq.equalsIgnoreCase("c")) {
			s_pseq = "12";
		} else if (s_pseq.equalsIgnoreCase("d")) {
			s_pseq = "13";
		} else if (s_pseq.equalsIgnoreCase("e")) {
			s_pseq = "14";
		} else if (s_pseq.equalsIgnoreCase("f")) {
			s_pseq = "15";
		}

		i_zdpseq = Integer.parseInt(s_pseq);

		return i_zdpseq;
	}

	private void writeCsszzcb(String seq, String sjxdm, String sjxz,
			JdbcTemplate jdbcT) throws Exception {
		String sSql = "insert into g_csszzcb(sjzfsseq,sjxdm,sjz) "
				+ "values(?,?,?)";
		String[] params = new String[] { seq, sjxdm, sjxz };
		jdbcT.update(sSql, params);

	}

	public String getDownLen(int iLEN) {

		iLEN = iLEN * 2 + 1;
		String sLEN = Integer.toHexString(iLEN);
		int tempi = 4 - sLEN.length();
		for (int i = 0; i < tempi; i++) {
			sLEN = "0" + sLEN;
		}
		return sLEN;

	}

	/**
	 * ������������������������Ϣ F1(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param csz
	 *            String ����ֵ   �ļ��ľ���·��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN0FF1(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// һ��������
		String kzm = "4A";

		// ����Ӧ�ù�����
		String afn = "0F"; // ��������

		// �������ݵ�Ԫ��ʶ
		String xxd = "P0"; // ��Ϣ��
		String xxl = "F1"; // ��Ϣ��

		// �ġ����ݵ�Ԫ
		String data = "";
        
		String version[]=new String[2];
		version=Decode_0F.getFileVersion(csz);
		
//		String[] ss_csz = csz.split(";");
		//�ն�Ӳ�����
		data +=version[0];
//		String cs1 = ss_csz[0];
//		data += Util.bbhToHexStr(version[0]);
		
		//�ն�������
		data +=version[1];
//		String cs2 = ss_csz[1];
//		data += Util.bbhToHexStr(cs2);
		
		//��ʼλ��(�ն�Ĭ��)
		data +="00C00300";
//		String cs3 = ss_csz[2];
//		cs3 = Util.convertStr(Util.decStrToHexStr(cs3, 4));
//		data += cs3;

		// �塢����
		String mc = "��������������Ϣ";

		// ���ù����ӿ�
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);
		// д���������ݴ��
		writeCsszzcb(seq_sjzfs, "AFN0FF1", "", jdbcT);
		return seq_sjzfs;
	}

	/**
	 * ���������������ļ�����F2(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param fileName
	 *            String �����ļ�����·��
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String sendAFN0FF2(String txfs, String xzqxm, String zddz, String fileName)
			throws Exception {
//		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		Util.updateZdsjpzb(xzqxm, zddz, fileName, jdbcT);
		Decode_0F.respondDownload(xzqxm, zddz, fileName, "0", jdbcT);
		
		//�����ط��߳�
		Decode_0F_ReSend ss=new Decode_0F_ReSend(xzqxm, zddz, jdbcT, "0");
		new Thread(ss).start();
		
		//Decode_0F.decodeDownload(xzqxm, zddz,"", jdbcT);
		
		return "1";
	}

	/**
	 * ���������������ն�����
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param fileName
	 *            String �����ļ�����·�� 
	 * @return seq_sjzfs String ���ݷ��ͱ����У�null��ʧ�ܣ�seq_sjzfs���ɹ���
	 * @throws Exception
	 */
	public String updateSingle(String txfs, String xzqxm, String zddz,
			String fileName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	
	

	

}
