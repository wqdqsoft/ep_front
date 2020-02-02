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
 * Description: 数据下行处理类
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * 编写时间: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 修改人： 修改时间：
 */

public class parseImp implements parse {

	// 加载日志
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
	 * 方法简述：发送
	 * 
	 * @param txfs
	 *            String 通信方式(根据txfsb表取)
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sSJZ
	 *            String 数据祯
	 * @param seq_sjzfs
	 *            String 数据发送表序列
	 * @param SIM
	 *            String 数据发送表序列
	 * 
	 * @return void
	 */
	private void send(String txfs, String xzqxm, String zddz, String sSJZ,
			String seq_sjzfs, String SIM, JdbcTemplate jdbcT) throws Exception {

		String gylx = Util.getZdgylx(xzqxm, zddz, jdbcT);// 规约类型:1:浙规;2:国规;3:浙版国规
		String SJZ = "";
		if (gylx.equals("3")) {
			// 2009-10-18外面套上浙规壳
			SJZ = Util.addSG(xzqxm, zddz, sSJZ);
		} else {
			SJZ = sSJZ;
		}
		dispatchService.downDispatch(txfs, gylx, xzqxm, zddz, SJZ, seq_sjzfs,
				SIM);
	}

	/**
	 * 方法简述：公共方法parse
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param kzm
	 *            String 控制码
	 * @param afn
	 *            String 应用功能码
	 * @param xxd
	 *            String 信息点(Pn)
	 * @param xxl
	 *            String 信息类(Fn)
	 * @param data
	 *            String 数据单元
	 * @param mc
	 *            String 名称
	 * @param jdbcT
	 *            JdbcTemplate 数据库连接
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	@SuppressWarnings("rawtypes")
	private String parse(String txfs, String xzqxm, String zddz, String kzm,
			String afn, String xxd, String xxl, String data, String mc,
			JdbcTemplate jdbcT) throws Exception {

		String seq_sjzfs = null;

		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();
		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = kzm;

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = afn;

		// 2、帧序列域(TpV=1;FIR=1;FIN=1;CON=1)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);
		int iZdpseq = getZdpseq(iZdpfc);
		//2017-01-12 取消05命令SEQ的时标
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

		// 3、数据单元标识DADT
		String sDA = Util.getDA(xxd);
		String sDT = Util.getDT(xxl);
		String sDADT = Util.convertStr(sDA) + Util.convertStr(sDT);

		// 4、数据单元DATA
		String sDATA = data;

		// 5、附加信息AUX
		String sAUX = "";
		String sPassword = CMContext.getZdmm(xzqxm, zddz);

		String sTime = Util.getTp(iZdpfc, rq, 0);

		// 2009-10-21 ??是否所有下行都可以带密码和时间??
		if (afn.equals("05")||afn.equals("04")){
			sAUX = sPassword;
		}else{
			sAUX = sPassword + sTime;
		}
		

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		// 04版
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698版
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
		// 写“数据帧发送表”及“数据标识子表”
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

		// 发送
		sSql = "select sim from G_ZDGZ where xzqxm=? and zddz=?";
		params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));

		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端对时F31(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rq
	 *            String 日期 XX表示系统时间
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendZdds(String txfs, String xzqxm, String zddz, String rq)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F31"; // 信息类

		// 四、数据单元
		String data = "";
		if (rq.equalsIgnoreCase("XX")) {
			data = Util.makeFormat01(Util.getNowTime(), Util.getNowWeek());
		} else {
			data = Util.makeFormat01(rq, Util.getWeek(rq));
		}

		// 五、名称
		String mc = "终端对时";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：复位命令F1/F2/F3(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param fwlx
	 *            String 复位类型(F1/F2/F3)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendZdfw(String txfs, String xzqxm, String zddz, String fwlx)
			throws Exception {
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "01"; // 复位

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = fwlx; // 信息类

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "复位命令";
		if (fwlx.equalsIgnoreCase("F1")) {
			mc += "硬件初始化";
		} else if (fwlx.equalsIgnoreCase("F2")) {
			mc += "数据区初始化";
		} else if (fwlx.equalsIgnoreCase("F3")) {
			mc += "参数及全体数据区初始化";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：是否允许终端与主站通话设置F27/F35(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sfyx
	 *            String 是否允许:1:允许；0：禁止
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendSfyxzdyzzth(String txfs, String xzqxm, String zddz,
			String sfyx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = ""; // 信息类
		if (sfyx.equals("1")) {
			// 允许F27
			xxl = "F27";
		} else if (sfyx.equals("0")) {
			// 禁止F35
			xxl = "F35";
		}

		// 四、数据单元
		String data = "";// 无数据单元

		// 五、名称
		String mc = "";
		if (sfyx.equals("1")) {
			// 允许
			mc = "允许终端与主站通话设置";
		} else if (sfyx.equals("0")) {
			// 禁止
			mc = "禁止终端与主站通话设置";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "sfyxzdyzzth", sfyx, jdbcT); // 是否允许终端与主站通话

		return seq_sjzfs;
	}

	/**
	 * 方法简述：是否终端剔除投入设置F28/F36(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sftctr
	 *            String 是否剔除投入:1:投入；0：剔除
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendSfzdtctr(String txfs, String xzqxm, String zddz,
			String sftctr) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = ""; // 信息类
		if (sftctr.equals("1")) {
			// 投入F28
			xxl = "F28";
		} else if (sftctr.equals("0")) {
			// 解除F36
			xxl = "F36";
		}

		// 四、数据单元
		String data = "";// 无数据单元

		// 五、名称
		String mc = "";
		if (sftctr.equals("1")) {
			mc = "终端剔除投入设置";
		} else if (sftctr.equals("0")) {
			mc = "终端剔除解除设置";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "sfzdtctr", sftctr, jdbcT); // 是否终端剔除投入

		return seq_sjzfs;
	}

	/**
	 * 方法简述：是否允许终端主动上报设置F29/F37(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sfyxzdsb
	 *            String 是否允许主动上报:1:允许；0：禁止
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendSfyxzdzdsb(String txfs, String xzqxm, String zddz,
			String sfyxzdsb) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = ""; // 信息类
		if (sfyxzdsb.equals("1")) {
			// 允许F29
			xxl = "F29";
		} else if (sfyxzdsb.equals("0")) {
			// 禁止F37
			xxl = "F37";
		}

		// 四、数据单元
		String data = "";// 无数据单元

		// 五、名称
		String mc = "";
		if (sfyxzdsb.equals("1")) {
			mc = "允许终端主动上报设置";
		} else if (sfyxzdsb.equals("0")) {
			mc = "禁止终端主动上报设置";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "sfyxzdzdsb", sfyxzdsb, jdbcT); // 是否允许终端主动上报

		return seq_sjzfs;
	}

	/**
	 * 方法简述：1/2类数据任务启动/停止设置F67/F68(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rwlx
	 *            String 任务类型（1：1类；2：2类）
	 * @param rwh
	 *            String 任务号
	 * @param rwqdbz
	 *            String 任务启动标志:55:启动；AA：停止
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendZdrwqybz(String txfs, String xzqxm, String zddz,
			String rwlx, String rwh, String rwqybz) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 写“终端任务配置表”
		sSql = "update zdrwpzb " + "set qybz=? " + "where xzqxm=? and zddz=? "
				+ "and rwlx=? and rwh=?";
		String[] params = new String[] { rwqybz, xzqxm, zddz, rwlx, rwh };
		jdbcT.update(sSql, params);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + rwh; // 信息点
		String xxl = ""; // 信息类
		if (rwlx.equals("1")) {
			// 1类F67
			xxl = "F67";
		} else if (rwlx.equals("2")) {
			// 2类F68
			xxl = "F68";
		}

		// 四、数据单元
		String data = rwqybz;// 任务启动标志

		// 五、名称
		String mc = rwh + "号任务启动标志[" + rwqybz + "](" + rwlx + "类数据任务)";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端保电设置F25(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param bdsj
	 *            String 保电时间 数值范围：0－48；单位：0.5h；0表示无限期保电；AA：保电撤出
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdbd(String txfs, String xzqxm, String zddz, String bdsj)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = ""; // 信息类
		if (bdsj.equalsIgnoreCase("AA")) {
			// 保电解除F33
			xxl = "F33";
		} else {
			// 保电时间设置F25
			xxl = "F25";
		}

		// 四、数据单元
		String data = "";
		if (!bdsj.equalsIgnoreCase("AA")) {
			// 保电时间设置F25
			data = Util.decStrToHexStr(bdsj, 1);
		}

		// 五、名称
		String mc = "";
		if (bdsj.equalsIgnoreCase("AA")) {
			mc = "终端保电解除设置";
		} else {
			mc = "终端保电设置";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "zdbdsj", bdsj, jdbcT); // 终端保电时间

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端配置数量表设置F9(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param dnbsl
	 *            int 电能表数量
	 * @param mcsl
	 *            int 脉冲数量
	 * @param mnlsl
	 *            int 模拟量数量
	 * @param zjzsl
	 *            int 总加组数量
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdpzslb(String txfs, String xzqxm, String zddz,
			int dnbsl, int mcsl, int mnlsl, int zjzsl) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F9"; // 信息类

		// 四、数据单元
		String data = "";
		// 电能表数量
		String sDNBSL = Integer.toHexString(dnbsl);
		if (sDNBSL.length() < 2) {
			sDNBSL = "0" + sDNBSL;
		}

		// 脉冲数量
		String sMCSL = Integer.toHexString(mcsl);
		if (sMCSL.length() < 2) {
			sMCSL = "0" + sMCSL;
		}

		// 模拟量数量
		String sMNLSL = Integer.toHexString(mnlsl);
		if (sMNLSL.length() < 2) {
			sMNLSL = "0" + sMNLSL;
		}

		// 总加组数量
		String sZJZSL = Integer.toHexString(zjzsl);
		if (sZJZSL.length() < 2) {
			sZJZSL = "0" + sZJZSL;
		}

		data = sDNBSL + sMCSL + sMNLSL + sZJZSL;

		// 五、名称
		String mc = "终端配置数量表设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "dnbsl", String.valueOf(dnbsl), jdbcT); // 电能表数量
		writeCsszzcb(seq_sjzfs, "mcsl", String.valueOf(mcsl), jdbcT); // 脉冲数量
		writeCsszzcb(seq_sjzfs, "mnlsl", String.valueOf(mnlsl), jdbcT); // 模拟量数量
		writeCsszzcb(seq_sjzfs, "zjzsl", String.valueOf(zjzsl), jdbcT); // 总加组数量数量

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端抄表间隔设置F24(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cbjg
	 *            int 抄表间隔（单位：分钟）
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdcbjg(String txfs, String xzqxm, String zddz, int cbjg)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F24"; // 信息类

		// 四、数据单元
		String data = "";
		String sCBJG = Integer.toHexString(cbjg);
		if (sCBJG.length() < 2) {
			sCBJG = "0" + sCBJG;
		}
		data = sCBJG;

		// 五、名称
		String mc = "终端抄表间隔设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "zdcbjg", String.valueOf(cbjg), jdbcT); // 终端抄表间隔

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端自动保电设置F58(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zdbdsj
	 *            int 自动保电时间（单位：小时）
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdzdbd(String txfs, String xzqxm, String zddz, int zdbdsj)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F58"; // 信息类

		// 四、数据单元
		String data = "";
		data = Util.decStrToHexStr(zdbdsj, 1);

		// 五、名称
		String mc = "终端自动保电设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "zdzdbdsj", String.valueOf(zdbdsj), jdbcT); // 终端自动保电

		return seq_sjzfs;
	}

	/**
	 * 方法简述：测量点数据冻结参数设置F27(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param djcs
	 *            String[][2] 数据项代码和对应的冻结参数（0：不冻结；1：15分；2：30分；3：60分）
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCldsjdjcs(String txfs, String xzqxm, String zddz,
			String cldh, String[][] djcs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F27"; // 信息类

		// 四、数据单元
		String data = "";
		String temp_djcs = cldh + "#";// cldh#Fn,djn#Fn,djn#
		data = Util.decStrToHexStr(djcs.length, 1);// 冻结信息项个数
		for (int i = 0; i < djcs.length; i++) {
			// 每个信息项下
			String xxx = djcs[i][0];// 信息项Fn
			String djmd = djcs[i][1];// 冻结密度
			temp_djcs += xxx + "," + djmd + "#";
			data += Util.decStrToHexStr(xxx.substring(1), 1)
					+ Util.decStrToHexStr(djmd, 1);
		}

		// 五、名称
		String mc = "测量点数据冻结参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "cldsjdjcs", temp_djcs, jdbcT); // 测量点数据冻结参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：总加组数据冻结参数设置F33(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            String 总加组号
	 * @param djcs
	 *            String[4] 冻结参数（0：不冻结；1：15分；2：30分；3：60分）
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZjzsjdjcs(String txfs, String xzqxm, String zddz,
			String zjzh, String[] djcs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F33"; // 信息类

		// 四、数据单元
		String data = "";
		String temp_djcs = zjzh + "#";
		for (int i = 0; i < 4; i++) {
			temp_djcs += djcs[i] + "#";
			data += Util.decStrToHexStr(djcs[i], 1);
		}

		// 五、名称
		String mc = "总加组数据冻结参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "zjzsjdjcs", temp_djcs, jdbcT); // 总加组数据冻结参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端通信参数设置F1(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param scjyssj
	 *            String 数传机延时时间,单位:20ms
	 * @param fscsyxyssj
	 *            String 作为启动站允许发送传输延时时间,单位:分钟
	 * @param ddcdzxycssj
	 *            String 等待从动站响应的超时时间,0-4095,单位:秒
	 * @param cfcs
	 *            String 重发次数,0-3;0表示不允许重发
	 * @param zdsbzysjjlqrbz
	 *            String 主动上报重要事件记录的确认标志,1表示需要主站确认
	 * @param zdsbybsjjlqrbz
	 *            String 主动上报一般事件记录的确认标志,1表示需要主站确认
	 * @param xtzq
	 *            String 心跳周期:1-60分
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdtxcs(String txfs, String xzqxm, String zddz,
			String scjyssj, String fscsyxyssj, String ddcdzxycssj, String cfcs,
			String zdsbzysjjlqrbz, String zdsbybsjjlqrbz, String xtzq)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F1"; // 信息类

		// 四、数据单元
		String data = "";
		// 数传机延时时间
		data += Util.decStrToHexStr(scjyssj, 1);
		// 作为启动站允许发送传输延时时间
		data += Util.decStrToHexStr(fscsyxyssj, 1);
		// 等待从动站响应的超时时间及重发次数
		ddcdzxycssj = Util.decStrToBinStr(ddcdzxycssj, 2);
		ddcdzxycssj = ddcdzxycssj.substring(4, 16);
		cfcs = Util.decStrToBinStr(cfcs, 1);
		cfcs = cfcs.substring(6, 8);
		data += Util.convertStr(Util.binStrToHexStr(cfcs + ddcdzxycssj, 2));
		// 需要主站确认的标志
		data += Util.binStrToHexStr(zdsbybsjjlqrbz + zdsbzysjjlqrbz, 1);
		// 心跳周期
		data += Util.decStrToHexStr(xtzq, 1);

		// 五、名称
		String mc = "终端通信参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String txcs = scjyssj + ";" + fscsyxyssj + ";" + ddcdzxycssj + ";"
				+ cfcs + ";" + zdsbzysjjlqrbz + "#" + zdsbybsjjlqrbz + ";"
				+ xtzq;
		writeCsszzcb(seq_sjzfs, "txcs", txcs, jdbcT); // 终端通信参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端复位：F1硬件初始化(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(预留)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN01F1(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "01"; // 复位

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F1"; // 信息类

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "终端复位：F1硬件初始化";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端复位：F2数据区初始化(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(预留)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN01F2(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "01"; // 复位

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F2"; // 信息类

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "终端复位：F2数据区初始化";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端复位：F3参数及全体数据区初始化（即恢复至出厂配置）(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(预留)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN01F3(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "01"; // 复位

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F3"; // 信息类

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "终端复位：F3参数及全体数据区初始化（即恢复至出厂配置）";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端复位：F4参数（除与系统主站通信有关的）及全体数据区初始化(AFN=01H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(预留)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN01F4(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "01"; // 复位

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F4"; // 信息类

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "终端复位：F4参数（除与系统主站通信有关的）及全体数据区初始化";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端通信参数设置F1(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;cs2;cs3;cs4;cs5) 
	 *            cs1:数传机延时时间,单位:20ms
	 *            cs2:终端通信模块的信号强度,单位:db 
	 *            cs3:终端启动次数（最大65535）,单位:次
	 *            cs4:备用 默认FF
	 *            cs5:心跳周期:1-60分
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F1(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F1"; // 信息类

		// 四、数据单元
		String data = "";

		String[] ss_csz = csz.split(";");
		// 数传机延时时间
		String cs1 = ss_csz[0];
		data += Util.decStrToHexStr(cs1, 1);
		
		// 终端通信模块的信号强度
		String cs2 = ss_csz[1];
		data += Util.makeFormat04(Integer.parseInt(cs2));
		// 终端启动次数
		String cs3 = ss_csz[2];
		cs3 = Util.convertStr(Util.decStrToHexStr(cs3, 2));
		data += cs3;
		// 变频器型号：1-JAC780, 2-JAC580B.
		String cs4=ss_csz[3];
		data += Util.decStrToHexStr(cs4, 1);
		
		// 心跳周期
		String cs5 = ss_csz[4];
		data += Util.decStrToHexStr(cs5, 1);

		// 五、名称
		String mc = "终端通信参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F1", csz, jdbcT); // 终端通信参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电能表异常判别阀值设定F59(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param dnlccfz
	 *            String 电能量超差阀值x.x
	 * @param dnbfzfz
	 *            String 电能表飞走阀值x.x
	 * @param dnbtzfz
	 *            String 电能表停走阀值,单位:15min
	 * @param dnbjsfz
	 *            String 电能表校时阀值,单位:min
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDnbycpbfz(String txfs, String xzqxm, String zddz,
			String dnlccfz, String dnbfzfz, String dnbtzfz, String dnbjsfz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F59"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.makeFormat22(dnlccfz);
		data += Util.makeFormat22(dnbfzfz);
		data += Util.decStrToHexStr(dnbtzfz, 1);
		data += Util.decStrToHexStr(dnbjsfz, 1);

		// 五、名称
		String mc = "电能表异常判别阀值设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String dnbycpbfz = dnlccfz + ";" + dnbfzfz + ";" + dnbtzfz + ";"
				+ dnbjsfz;
		writeCsszzcb(seq_sjzfs, "dnbycpbfz", dnbycpbfz, jdbcT); // 电能表异常判别阀值

		return seq_sjzfs;
	}

	/**
	 * 方法简述：主站IP地址和端口设置F3(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zyip
	 *            String[] 主用IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param byip
	 *            String[] 备用IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param wgip
	 *            String[] 网关IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param dlip
	 *            String[] 代理IP,ip[0]-ip[3]:ip1-ip4;ip[4]:port
	 * @param apn
	 *            String APN(16字节；ASCII;低位补00H；按正序传)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZzip(String txfs, String xzqxm, String zddz,
			String[] zyip, String[] byip, String[] wgip, String[] dlip,
			String apn) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F3"; // 信息类

		// 四、数据单元
		String data = "";
		// 主站主用IP
		String temp_zyip = zyip[0] + "." + zyip[1] + "." + zyip[2] + "."
				+ zyip[3] + ":" + zyip[4];
		data += Util.decStrToHexStr(zyip[0], 1);
		data += Util.decStrToHexStr(zyip[1], 1);
		data += Util.decStrToHexStr(zyip[2], 1);
		data += Util.decStrToHexStr(zyip[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(zyip[4], 2));

		// 主站备用IP
		String temp_byip = byip[0] + "." + byip[1] + "." + byip[2] + "."
				+ byip[3] + ":" + byip[4];
		data += Util.decStrToHexStr(byip[0], 1);
		data += Util.decStrToHexStr(byip[1], 1);
		data += Util.decStrToHexStr(byip[2], 1);
		data += Util.decStrToHexStr(byip[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(byip[4], 2));

		// 主站网关IP
		String temp_wgip = wgip[0] + "." + wgip[1] + "." + wgip[2] + "."
				+ wgip[3] + ":" + wgip[4];
		data += Util.decStrToHexStr(wgip[0], 1);
		data += Util.decStrToHexStr(wgip[1], 1);
		data += Util.decStrToHexStr(wgip[2], 1);
		data += Util.decStrToHexStr(wgip[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(wgip[4], 2));

		// 主站代理IP
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

		// 五、名称
		String mc = "主站IP地址设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String zzip = temp_zyip + ";" + temp_byip + ";" + temp_wgip + ";"
				+ temp_dlip + ";" + apn;
		writeCsszzcb(seq_sjzfs, "zzip", zzip, jdbcT); // 主站IP

		return seq_sjzfs;
	}

	/**
	 * 方法简述：主站IP地址和端口设置F3(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;cs2;cs3) 
	 *            cs1:主用IP(xxx.xxx.xxx.xxx:nnnnn)
	 *            cs2:备用IP(xxx.xxx.xxx.xxx:nnnnn)
	 *            cs3:APN(16字节；ASCII;低位补00H；按正序传)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F3(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F3"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");

		// 主站主用IP
		String cs1 = ss_csz[0];
		cs1 = cs1.replace(".", "#");
		String[] tempss1 = cs1.split(":");
		String[] ss1 = tempss1[0].split("#");
		data += Util.decStrToHexStr(ss1[0], 1);
		data += Util.decStrToHexStr(ss1[1], 1);
		data += Util.decStrToHexStr(ss1[2], 1);
		data += Util.decStrToHexStr(ss1[3], 1);
		data += Util.convertStr(Util.decStrToHexStr(tempss1[1], 2));

		// 主站备用IP
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

		// 五、名称
		String mc = "主站IP地址和端口";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F3", csz, jdbcT); // 主站IP

		return seq_sjzfs;
	}

	/**
	 * 方法简述：主站电话号码和短信中心号码设置F4(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zzdhhm
	 *            String 主站电话号码
	 * @param dxzxhm
	 *            String 短信中心号码
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZzdhhm(String txfs, String xzqxm, String zddz,
			String zzdhhm, String dxzxhm) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F4"; // 信息类

		// 四、数据单元
		String data = "";
		// 主站电话号码
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

		// 短信中心号码
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

		// 五、名称
		String mc = "主站电话号码和短信中心号码设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String dhhm = zzdhhm + ";" + dxzxhm;
		writeCsszzcb(seq_sjzfs, "zzdhhm", dhhm, jdbcT); // 主站电话号码和短信中心号码

		return seq_sjzfs;
	}

	/**
	 * 方法简述：处理池基本参数F4(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;cs2;.....cs6) 
	 *            [-----如果是一代终端-----]
	 *            cs1:池体类型
	 *            cs2:处理规模
	 *            cs3:池深
	 *            cs4:池体横截面
	 *            cs5:处理能力(水泵流量)
	 *            cs6:谷电总小时数
	 *            cs7:峰谷节能模式使能
	 *            cs8:一代终端：调节池/收集池浮球数量；二代终端：流量计配置
	 *            cs9:调节池水位上限（距池体顶部距离）
	 *            cs10:调节池水位上限（距池体顶部距离）
	 *            cs11:目标ORP数值范围下限
	 *            cs12:目标ORP数值范围上限
	 *            cs13:水泵控制判断依据
	 *            [-----end 如果是一代终端-----]
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F4(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F4"; // 信息类

		// 四、数据单元
		String data = "";
		
		String[] ss_csz = csz.split(";");
		
		String s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
		String[] params = new String[] { xzqxm,zddz };
	    List cldList = jdbcT.queryForList(s_sql, params);
	    Map cldMap = (Map) cldList.get(0);
	    // 终端型号
	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
	 	//如果是一代终端
	 	if("1".equalsIgnoreCase(zdxh)){
	 	    // 池体类型
			String cs1 = ss_csz[0];
			data += Util.decStrToHexStr(cs1, 1);

			// 处理规模
			String cs2 = ss_csz[1];
			data += Util.makeFormat04( Integer.parseInt(cs2));
			
			// 池体深度
			String cs3 = ss_csz[2];
			data += Util.makeFormat22(cs3);
			
			
			// 一代终端：调节池/收集池浮球数量
			String cs4 = ss_csz[3];
			data += Util.decStrToHexStr(cs4, 1);
			
			//调节池水位上限（距池体顶部距离）
			String cs5 = ss_csz[4];
			data += Util.makeFormat22(cs5);
			
			//调节池水位下限（距池体顶部距离）
			String cs6 = ss_csz[5];
			data += Util.makeFormat22(cs6);
			
			//目标ORP数值范围下限
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
			
			
			//目标ORP数值范围上限
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
			//如果是二代的终端
	 	}else{
	 	    // 池体类型
			String cs1 = ss_csz[0];
			data += Util.decStrToHexStr(cs1, 1);

			// 处理规模
			String cs2 = ss_csz[1];
			data += Util.makeFormat04( Integer.parseInt(cs2));
			
			// 池体深度
			String cs3 = ss_csz[2];
			data += Util.makeFormat22(cs3);
			
			// 池体横截面
			String cs4 = ss_csz[3];
			data += Util.decStrToHexStr(cs4, 1);
			
			// 处理能力(水泵流量)
			String cs5 = ss_csz[4];
			data += Util.decStrToHexStr(cs5, 1);
			
			// 谷电总小时数
			String cs6 = ss_csz[5];
			data += Util.decStrToHexStr(cs6, 1);
			
			// 峰谷节能模式使能
			String cs7 = ss_csz[6];
			data += cs7;
			
			// 二代终端：流量计配置
			String cs8 = ss_csz[7];
			data += cs8;
			
//			//调节池水位上限（距池体顶部距离）
//			String cs9 = ss_csz[8];
//			data += Util.makeFormat22(cs9);
//			
//			//调节池水位下限（距池体顶部距离）
//			String cs10 = ss_csz[9];
//			data += Util.makeFormat22(cs10);
//			
//			//目标ORP数值范围下限
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
//			//目标ORP数值范围上限
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
			//水位监测方式：0：浮球控制；1：超声波水位控制
			String cs9 = ss_csz[8];
			data+=Util.decStrToHexStr(cs9, 1);
	 	}

		

		// 五、名称
		String mc = "处理池基本参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F4", csz, jdbcT); // 主站电话号码和短信中心号码

		return seq_sjzfs;
	}

	/**
	 * 方法简述：风机水泵控制参数F5(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;cs2;cs3;cs4;cs5)
	 *            cs1:风机开启相对于水泵开启的提前量 单位：分钟
	 *            cs2:风机水泵自动控制允许标志 0x55允许自动控制；0xAA禁止自动控制
	 *            cs3:污泥回流泵自动控制允许标志   0x55允许自动控制；0xAA禁止自动控制
	 *            cs4:污泥回流泵周期运行开启时长 单位：分钟
	 *            cs5:污泥回流泵周期运行停止时长 单位：分钟
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F5(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F5"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");

		// 风机开启相对于水泵开启的提前量
		String s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
 		String[] params = new String[] { xzqxm,zddz };
 	    List cldList = jdbcT.queryForList(s_sql, params);
 	    Map cldMap = (Map) cldList.get(0);
 	    // 终端型号
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
		
		// 风机水泵自动控制允许标志 0x55允许自动控制；0xAA禁止自动控制
		String cs2 = ss_csz[1];
		data +=cs2;
		
		// 污泥回流泵自动控制允许标志   0x55允许自动控制；0xAA禁止自动控制
		String cs3 = ss_csz[2];
		data +=cs3;
		
		// 污泥回流泵周期运行开启时长 单位：分钟
		String cs4 = ss_csz[3];
		if("EEEE".equalsIgnoreCase(cs4)){
			data+="EEEE";
		}else{
//		    data += Util.makeFormat08( Integer.parseInt(cs4));
			data +=Util.convertStr(Util.decStrToHexStr(cs4, 2));
		}
		
		// 污泥回流泵周期运行停止时长 单位：分钟
		String cs5 = ss_csz[4];
		if("EEEE".equalsIgnoreCase(cs4)){
			data+="EEEE";
		}else{
//		    data += Util.makeFormat08( Integer.parseInt(cs5));
			data +=Util.convertStr(Util.decStrToHexStr(cs5, 2));
		}

		// 五、名称
		String mc = "风机水泵控制参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F5", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：门禁及报警参数F6(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;cs3) 
	 *            cs1:刷卡验证后的有效时长       单位:分钟
	 *            cs2:声音报警时长       单位:分钟
	 *            cs3:灯光报警时长       单位:分钟
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F6(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F6"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");

		//刷卡验证后的有效时长
		String cs1 = ss_csz[0];
		data += Util.makeFormat04( Integer.parseInt(cs1));
		//声音报警时长
		String cs2 = ss_csz[1];
		data += Util.makeFormat04( Integer.parseInt(cs2));
		//灯光报警时长
		String cs3 = ss_csz[2];
		data += Util.makeFormat04( Integer.parseInt(cs3));
		
		// 五、名称
		String mc = "门禁及报警参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F6", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：射频卡序列号库 F7(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn) 
	 *            csn:射频ID卡n的序号
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F7(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F7"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		
		//射频卡个数
		int n=ss_csz.length;
		data +=Util.convertStr(Util.decStrToHexStr(n, 2));

		// 1-n组射频卡序号(用户直接填写的就是7个字节的16进制)
		for (int i = 0; i < n; i++) {
			String csn = ss_csz[i];
			csn=Util.decStrToHexStr(Long.parseLong(csn), 7);
			csn=Util.convertStr(csn);
//			data += Util.behindChrForStr(csn, "0", 14);
			data +=csn;
		}

		// 五、名称
		String mc = "射频卡序列号库";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F7", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端状态量输入参数设置F12(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param ztljrbz
	 *            String 状态量接入标志(D0-D7表示1-8路,置1:接入;置0:不接入)
	 * @param ztlsxbz
	 *            String 状态量属性标志(D0-D7表示1-8路,置1:a型触点;置0:b型触点)
	 * @param ztlgjbz
	 *            String 状态量告警标志(D0-D7表示1-8路,置1:重要事件;置0:一般事件)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZtlsrcs(String txfs, String xzqxm, String zddz,
			String ztljrbz, String ztlsxbz, String ztlgjbz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F12"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.binStrToHexStr(ztljrbz, 1);
		data += Util.binStrToHexStr(ztlsxbz, 1);
		data += Util.binStrToHexStr(ztlgjbz, 1);

		// 五、名称
		String mc = "终端状态量输入参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String ztl = ztljrbz + ";" + ztlsxbz + ";" + ztlgjbz;
		writeCsszzcb(seq_sjzfs, "ztlsrcs", ztl, jdbcT); // 终端状态量输入参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：直流模拟量接入参数F61(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param jrbz
	 *            String 直流模拟量接入标志(D0-D7表示1-8路,置1:接入;置0:不接入)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZlmnljrcs(String txfs, String xzqxm, String zddz,
			String jrbz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F61"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.binStrToHexStr(jrbz, 1);

		// 五、名称
		String mc = "直流模拟量接入参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "zlmnljrbz", jrbz, jdbcT); // 直流模拟量接入标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电容器参数F73(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param drqcs
	 *            String[9][5] 电容器参数(1-9组),以组1为例: drqcs[0][0]:共分标志
	 *            drqcs[0][1]:分补相标志 drqcs[0][2]:电容装见容量 drqcs[0][3]:电容装见容量系数
	 *            drqcs[0][4]:电容装见容量符号
	 * 
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDrqcs(String txfs, String xzqxm, String zddz,
			String cldh, String[][] drqcs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F73"; // 信息类

		// 四、数据单元
		String data = "";
		String temps_cs = cldh + "@";
		for (int i = 0; i < 9; i++) {
			data += Util.binStrToHexStr(drqcs[i][0] + "000" + drqcs[i][1], 1);
			data += Util.makeFormat02(drqcs[i][2], drqcs[i][3], drqcs[i][4]);

			temps_cs = drqcs[i][0] + "#" + drqcs[i][1] + ";" + drqcs[i][2]
					+ "#" + drqcs[i][3] + "#" + drqcs[i][4] + "@";
		}

		// 五、名称
		String mc = "电容器参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "drqcs", temps_cs, jdbcT); // 电容器参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电容器投切运行参数F74(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param mbglys
	 *            String 目标功率因数
	 * @param mbglysfh
	 *            String 目标功率因数符号(0:正;1:负)
	 * @param trwgglmx
	 *            String 投入无功功率门限
	 * @param qcwgglmx
	 *            String 切除无功功率门限
	 * @param yssj
	 *            String 延时时间
	 * @param dzsjjg
	 *            String 动作时间间隔
	 * 
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDrqtqyxcs(String txfs, String xzqxm, String zddz,
			String cldh, String mbglys, String mbglysfh, String trwgglmx,
			String qcwgglmx, String yssj, String dzsjjg) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F74"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.makeFormat05(mbglysfh, mbglys);
		data += Util.makeFormat23(trwgglmx);
		data += Util.makeFormat23(qcwgglmx);
		data += Util.decStrToHexStr(yssj, 1);
		data += Util.decStrToHexStr(dzsjjg, 1);

		// 五、名称
		String mc = "电容器投切运行参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String temps_cs = cldh + "@" + mbglys + "#" + mbglysfh + ";" + trwgglmx
				+ ";" + qcwgglmx + ";" + yssj + ";" + dzsjjg;
		writeCsszzcb(seq_sjzfs, "drqtqyxcs", temps_cs, jdbcT); // 电容器投切运行参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电容器保护参数F75(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param gdy
	 *            String 过电压
	 * @param gdyhcz
	 *            String 过电压回差值
	 * @param qdy
	 *            String 欠电压
	 * @param qdyhcz
	 *            String 欠电压回差值
	 * @param dlsx
	 *            String 总畸变电流含有率上限
	 * @param dlsxfh
	 *            String 总畸变电流含有率上限符号
	 * @param dlyxhc
	 *            String 总畸变电流含有率越限回差值
	 * @param dlyxhcfh
	 *            String 总畸变电流含有率越限回差值符号
	 * @param dysx
	 *            String 总畸变电压含有率上限
	 * @param dysxfh
	 *            String 总畸变电压含有率上限符号
	 * @param dyyxhc
	 *            String 总畸变电压含有率越限回差值
	 * @param dyyxhczfh
	 *            String 总畸变电压含有率越限回差值符号
	 * 
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDrqbhcs(String txfs, String xzqxm, String zddz,
			String cldh, String gdy, String gdyhcz, String qdy, String qdyhcz,
			String dlsx, String dlsxfh, String dlyxhc, String dlyxhcfh,
			String dysx, String dysxfh, String dyyxhc, String dyyxhcfh)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F75"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.makeFormat07(gdy);
		data += Util.makeFormat07(gdyhcz);
		data += Util.makeFormat07(qdy);
		data += Util.makeFormat07(qdyhcz);
		data += Util.makeFormat05(dlsxfh, dlsx);
		data += Util.makeFormat05(dlyxhcfh, dlyxhc);
		data += Util.makeFormat05(dysxfh, dysx);
		data += Util.makeFormat05(dyyxhcfh, dyyxhc);

		// 五、名称
		String mc = "电容器保护参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String temps_cs = cldh + "@" + gdy + ";" + gdyhcz + ";" + qdy + ";"
				+ qdyhcz + ";" + dlsx + "#" + dlsxfh + ";" + dlyxhc + "#"
				+ dlyxhcfh + ";" + dysx + "#" + dysxfh + ";" + dyyxhc + "#"
				+ dyyxhcfh;
		writeCsszzcb(seq_sjzfs, "drqbhcs", temps_cs, jdbcT); // 电容器保护参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电容器投切控制方式F76(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param kzfs
	 *            String 控制方式:1:当地控制;2:远方遥控;3:闭锁;4:解锁
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDrqtqkzfs(String txfs, String xzqxm, String zddz,
			String cldh, String kzfs) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F75"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.decStrToHexStr(kzfs, 1);

		// 五、名称
		String mc = "电容器投切控制方式";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "drqtqkzfs", cldh + "@" + kzfs, jdbcT); // 电容器投切控制方式

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端保安定值设置F17(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param badz
	 *            String 保安定值（>=1,<=999）
	 * @param xs
	 *            Sting 系数（遵照规约,如：000=10E4...）
	 * @param zf
	 *            String 正负：0：正；1：负
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdbadz(String txfs, String xzqxm, String zddz,
			String badz, String xs, String zf) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F17"; // 信息类

		// 四、数据单元
		String data = "";
		data = Util.makeFormat02(badz, xs, zf);

		// 五、名称
		String mc = "终端保安定值设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "zdbadz", badz, jdbcT); // 终端保安定值
		writeCsszzcb(seq_sjzfs, "zdbadzfh", zf, jdbcT); // 终端保安定值符号
		writeCsszzcb(seq_sjzfs, "zdbadzxs", xs, jdbcT); // 终端保安定值系数

		return seq_sjzfs;
	}

//	/**
//	 * 方法简述：终端功控时段设置F18(AFN=04H)
//	 * 
//	 * @param xzqxm
//	 *            String 行政区县码
//	 * @param zddz
//	 *            String 终端地址
//	 * @param sd
//	 *            String[][] 时段{sd[i][0]:时段(x-y,0-48);
//	 *            sd[i][1]:控制状态(00:不控制；01：控制1；10：控制2；11：保留)}
//	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
//	 * @throws Exception
//	 */
//	public String sendAFN04F18(String txfs, String xzqxm, String zddz,
//			String[][] sd) throws Exception {
//		String seq_sjzfs = null;
//		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//
//		// 一、控制码
//		String kzm = "4A";
//
//		// 二、应用功能码
//		String afn = "04"; // 设置命令
//
//		// 三、数据单元标识
//		String xxd = "P0"; // 信息点
//		String xxl = "F18"; // 信息类
//
//		// 四、数据单元
//		String data = "";
//		String temp_data = "";
//		int sd_len = sd.length;
//		for (int i = 0; i < sd_len; i++) {
//			//时段序号
//			String sd_xh = sd[i][0];
//			//时段定值
//			String sd_kzbz = sd[i][1];
//            String[] xh = sd_xh.split("-");
//            int xh_len = Integer.parseInt(xh[1]) - Integer.parseInt(xh[0]);
//            //如果是第一个控制时段
//            if(0==i){
//            	int len_0=Integer.parseInt(xh[0]);
//            	for (int j = 0; j < len_0; j++) {
//    				temp_data = temp_data + "00";
//    			}
//            	for (int j = 0; j < xh_len; j++) {
//    				temp_data = temp_data + sd_kzbz;
//    			}
//            }else{
//            	//上一个时段序号
//    			String sd_xh_front = sd[i-1][0];
//    			//上一个时段序号的截止时段号
//                int xh_front1=  Integer.parseInt(sd_xh_front.split("-")[1]);
//                //如果不是连续时段
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
//			temps = Util.binStrToHexStr(temps, 1);// 一个字节
//            data = data + temps;// 低位在先传
//		}
//
//		// 五、名称
//		String mc = "终端功控时段设置";
//
//		// 调用公共接口
//		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
//				jdbcT);
//
//		// 写"终端功控时段"表
//		// 删除旧记录
//		String sSql = "delete g_zdgksd where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?)";
//		String[] params = new String[] { xzqxm, zddz };
//		jdbcT.update(sSql, params);
//
//		// 插入新记录
//		int sdh = 0;// 时段号
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
	 * 方法简述：终端抄表日设置F7(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param day
	 *            String 抄表日（32个长度的二进制字符）
	 * @param time
	 *            String 抄表时间（单位：HHMM）
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdcbr(String txfs, String xzqxm, String zddz, String day,
			String time) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F7"; // 信息类

		// 四、数据单元
		String data = "";
		// 抄表日
		int iDay = Integer.parseInt(day, 2);
		String sDay = Integer.toHexString(iDay);
		int len_day = sDay.length();
		for (int i = 0; i < 8 - len_day; i++) {
			sDay = "0" + sDay;
		}
		sDay = Util.convertStr(sDay);

		// 抄表时间

		int len_time = time.length();
		for (int i = 0; i < 4 - len_time; i++) {
			time = "0" + time;
		}
		String temp_time = Util.convertStr(time);

		data = sDay + temp_time;

		// 五、名称
		String mc = "终端抄表日设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "zdcbrq", day, jdbcT); // 终端抄表日期
		writeCsszzcb(seq_sjzfs, "zdcbsj", time, jdbcT); // 终端抄表时间

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端事件记录配置设置F8(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sjjlyxbz
	 *            String 事件记录有效标志（64个长度的二进制字符,由高到低）
	 * @param sjzyxdjbz
	 *            String 事件重要性等级标志（64个长度的二进制字符,由高到低）
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdsjjl(String txfs, String xzqxm, String zddz,
			String sjjlyxbz, String sjzyxdjbz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F8"; // 信息类

		// 四、数据单元
		String data = "";
		// 事件记录有效标志
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

		// 事件重要性等级标志
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

		// 五、名称
		String mc = "终端事件记录配置设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "sjjlyxbz", sjjlyxbz, jdbcT); // 事件记录有效标志
		writeCsszzcb(seq_sjzfs, "sjzyxdjbz", sjzyxdjbz, jdbcT); // 事件重要性等级标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：从属手机号码设置F8(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;cs3) 
	 *            cs1:短信中心号码
	 *            cs2:调节池的UIM卡号
	 *            cs3:（cc1,cc2,cc3,.....ccn）
	 *                  cc1:收集池1的UIM卡号,
	 *                  cc2:收集池2的UIM卡号,
	 *                  ........
	 *                  ccn:收集池n的UIM卡号
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F8(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F8"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		
		// 收集池个数
		String cs1 = ss_csz[2];
		//收集池的UIM数组
		String[] sjcs=cs1.split(",");
		data += Util.decStrToHexStr(sjcs.length, 1);
		
		// 短信中心号码
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
		
		// 调节池UIM号码
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
			// 收集池【n】的UIM号码
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

		

		// 五、名称
		String mc = "[AFN04F8]从属手机号码设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F8", csz, jdbcT);

		return seq_sjzfs;
	}

	/**方法简述：水泵水位控制参数（针对收集池智能终端）F9(AFN=04H)
	   * @param 	xzqxm 	String 行政区县码
	   * @param 	zddz  	String 终端地址
	   * @param 	csz  	String 参数值(cs1;cs2;cs3;cs4)
	   * 				   cs1:主备切换设置1-使用1号水泵2-使用2号水泵3- 1号2号互为主
						   cs2:主备切换时间  一个字节 小时
						   cs3:启动时间 两个字节 分钟
						   cs4:停止时间   两个字节 分钟';
	   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	   */
	public String sendAFN04F9(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F9"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		// 主备切换设置
		String cs1 = ss_csz[0];
		data += Util.decStrToHexStr(cs1, 1);
		
		

		//主备切换时间
		if(null==ss_csz[1]||ss_csz[1].length()<=0){
			data+="CC";
		}else if("EE".equalsIgnoreCase(ss_csz[1])||"CC".equalsIgnoreCase(ss_csz[1])){
			data+=ss_csz[1];
		}else{
		    data+=Util.decStrToHexStr(ss_csz[1], 1);
		}
		
		//启动时间
		if(null==ss_csz[2]||ss_csz[2].length()<=0){
			data+="CCCC";
		}else if("EEEE".equalsIgnoreCase(ss_csz[2])||"CCCC".equalsIgnoreCase(ss_csz[2])){
			data+=ss_csz[2];
		}else{
		    data+=Util.convertStr(Util.decStrToHexStr(ss_csz[2], 2));
		}
		
		//停止时间
		if(null==ss_csz[3]||ss_csz[3].length()<=0){
			data+="CCCC";
		}else if("EEEE".equalsIgnoreCase(ss_csz[3])||"CCCC".equalsIgnoreCase(ss_csz[3])){
			data+=ss_csz[3];
		}else{
		    data+=Util.convertStr(Util.decStrToHexStr(ss_csz[3], 2));
		}

		// 五、名称
		String mc = "水泵水位控制参数（针对收集池智能终端）";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F9", csz, jdbcT);


        return seq_sjzfs;
	}

	/**
	 * 方法简述：终端1/2类数据任务设置F65/F66(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rwh
	 *            String 任务号
	 * @param fszq
	 *            String 发送周期
	 * @param zqdw
	 *            String 周期单位(00：分；01：时；10：日；11：月)
	 * @param fsjzsj
	 *            String 发送基准时间(年月日时分秒)
	 * @param cqbl
	 *            String 抽取倍率
	 * @param rwsjx
	 *            String[][] 任务数据项(String[i][0]:信息点Pn;String[i][1]:信息类Fn)
	 * @param rwlx
	 *            String 任务类型(1:1类数据任务;2:2类数据任务)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdrw(String txfs, String xzqxm, String zddz, String rwh,
			String fszq, String zqdw, String fsjzsj, String cqbl,
			String[][] rwsjx, String rwlx) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + rwh; // 信息点
		String xxl = ""; // 信息类
		if (rwlx.equals("1")) {
			xxl = "F65";
		} else {
			xxl = "F66";
		}
		// 四、数据单元
		String data = "";
		int len_rwsjx = rwsjx.length;
		// 发送周期及周期单位
		String sZq = "";
		String temp_fszq = Util.decStrToBinStr(fszq, 1);
		sZq = zqdw + temp_fszq.substring(2, 8);
		sZq = Util.binStrToHexStr(sZq, 1);

		// 发送基准时间
		String sJzsj = "";
		sJzsj = Util.convertStr(fsjzsj);

		// 抽取倍率
		String sCqbl = "";
		sCqbl = Util.decStrToHexStr(cqbl, 1);

		// 数据单元标识个数
		String sDybsgs = Util.intToHexStr(len_rwsjx, 1);

		data = sZq + sJzsj + sCqbl + sDybsgs;

		// 数据单元标识
		for (int i = 0; i < len_rwsjx; i++) {
			// <--------------每个数据单元标识下-------------->
			String temp_da = rwsjx[i][0];// 信息点DA
			String temp_dt = rwsjx[i][1];// 信息类DT
			temp_da = Util.getDA(temp_da);
			temp_dt = Util.getDT(temp_dt);

			data += Util.convertStr(temp_da) + Util.convertStr(temp_dt);
		}

		// 五、名称
		String mc = "终端" + rwlx + "类数据任务设置(" + rwh + "号任务)";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		String sSql = "";

		// 看“终端任务配置表”对应任务是否已存在
		sSql = "select * from zdrwpzb " + "where xzqxm='" + xzqxm
				+ "' and zddz='" + zddz + "' " + "and rwlx='" + rwlx
				+ "' and rwh='" + rwh + "'";
		int count = Util.getRecordCount(sSql, jdbcT);

		// 写“终端任务配置表”
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

		// 写“任务信息项”表
		sSql = "delete rwxxx " + "where xzqxm=? and zddz=? "
				+ "and rwlx=? and rwh=?";
		params = new String[] { xzqxm, zddz, rwlx, rwh };
		jdbcT.update(sSql, params);

		for (int i = 0; i < len_rwsjx; i++) {
			String temp_xh = String.valueOf(i + 1);
			String temp_da = rwsjx[i][0];// 信息点DA
			String temp_dt = rwsjx[i][1];// 信息类DT
			sSql = "insert into rwxxx(xzqxm,zddz,rwlx,rwh,xxdh,xxxdm,xxxlb,xh) "
					+ "values(?,?,?,?,?,?,?,?)";
			params = new String[] { xzqxm, zddz, rwlx, rwh, temp_da, temp_dt,
					rwlx, temp_xh };
			jdbcT.update(sSql, params);
		}

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端电能表/交流采样装置参数配置F10(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param dnbxx
	 *            ArrayList 终端所配电能表信息（电能表序号、所属测量点、端口号、规约类型、
	 *            通讯地址、通讯密码、费率个数、整数位个数[4-7]、小数位个数[1-4]）(均为string型)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZddnbpz(String txfs, String xzqxm, String zddz,
			ArrayList dnbxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F10"; // 信息类

		// 四、数据单元
		String data = "";
		int dnbNum = dnbxx.size();
		String sDnbnum = Integer.toHexString(dnbNum);
		if (sDnbnum.length() < 2) {
			sDnbnum = "0" + sDnbnum;
		}
		data = sDnbnum;

		for (int i = 0; i < dnbNum; i++) {
			Map hm = (Map) dnbxx.get(i);
			// 电能表序号
			String xh = String.valueOf(hm.get("xh"));
			xh = Integer.toHexString(Integer.parseInt(xh));
			if (xh.length() < 2) {
				xh = "0" + xh;
			}

			// 电能表所属测量点号
			String cldh = String.valueOf(hm.get("cldh"));
			cldh = Integer.toHexString(Integer.parseInt(cldh));
			if (cldh.length() < 2) {
				cldh = "0" + cldh;
			}

			// 电能表所属端口号
			String dkh = String.valueOf(hm.get("dkh"));
			dkh = Integer.toHexString(Integer.parseInt(dkh));
			if (dkh.length() < 2) {
				dkh = "0" + dkh;
			}

			// 电能表所属规约编号号
			String gybh = String.valueOf(hm.get("gybh"));
			gybh = Integer.toHexString(Integer.parseInt(gybh));
			if (gybh.length() < 2) {
				gybh = "0" + gybh;
			}

			// 电能表通讯地址
			String txdz = String.valueOf(hm.get("txdz"));
			int tempi = 12 - txdz.length();
			for (int j = 0; j < tempi; j++) {
				txdz = "0" + txdz;
			}
			txdz = Util.convertStr(txdz);

			// 电能表通讯密码
			String txmm = String.valueOf(hm.get("txmm"));
			txmm = Integer.toHexString(Integer.parseInt(txmm));
			tempi = 12 - txmm.length();
			for (int j = 0; j < tempi; j++) {
				txmm = "0" + txmm;
			}
			txmm = Util.convertStr(txmm);

			// 费率个数
			String flgs = String.valueOf(hm.get("flgs"));
			flgs = Integer.toHexString(Integer.parseInt(flgs));
			// 整数位个数
			String zswgs = String.valueOf(hm.get("zswgs"));
			zswgs = Integer.toBinaryString(Integer.parseInt(zswgs) - 4);
			if (zswgs.length() < 2) {
				zswgs = "0" + zswgs;
			}

			// 小数位个数
			String xswgs = String.valueOf(hm.get("xswgs"));
			xswgs = Integer.toBinaryString(Integer.parseInt(xswgs) - 1);
			if (xswgs.length() < 2) {
				xswgs = "0" + xswgs;
			}

			int i_zsxs = Integer.parseInt(zswgs + xswgs, 2);
			String flzsgs = flgs + Integer.toHexString(i_zsxs);

			data = data + xh + cldh + dkh + gybh + txdz + txmm + flzsgs;
		}

		// 五、名称
		String mc = "设置终端电能表参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端采集设备参数配置F10(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个测量点的ID
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F10(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F10"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
//		String cldIn = "";
		int dnbNum = ss_csz.length;// 采集设备数量
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		
//		s_sql = "select cldh from g_zdcldpzb where zdid=?";
//		params = new String[] { zdid };
//		List dnbList = jdbcT.queryForList(s_sql, params);
		
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// 每个配置的测量点的ID
			String cldid = ss_csz[i];
			s_sql="select * from g_zdcldpzb where id=?";
			params = new String[] { cldid };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// 设备序号
			String pz1 = String.valueOf(cldMap.get("xh"));// 设备序号(1字节)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// 采集设备所属测量点号
			String pz2 = String.valueOf(cldMap.get("cldh"));;// 采集设备所属测量点号(2字节)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// 设备类型
			String pz3 = String.valueOf(cldMap.get("cldlx"));// 设备类型
			String data3 = Util.decStrToHexStr(pz3, 1);
			
			// 属规约类型编号
			String pz4 = String.valueOf(cldMap.get("gylx"));// 规约类型(1字节)
			String data4 = Util.decStrToHexStr(pz4, 1);

			// 通信速率
			String pz5 = String.valueOf(cldMap.get("txsl"));// 通信速率
			String data5 = Util.decStrToHexStr(pz5, 1);
			
			// 电能表通信地址
			String pz6 = String.valueOf(cldMap.get("txdz"));// 通信地址(6字节，格式12)
			String data6 = Util.add(pz6, 6, "0");
			data5 = Util.convertStr(data5);

			

			data += data1 + data2 + data3 + data4 + data5 + data6;

//			if (pz2.equals("0")) {
//				// 删除相应的测量点
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
//				// 新增测量点配置表
//				String seq=Util.getSequences("SEQ_CLDID", jdbcT);
//				s_sql = "insert into g_zdcldpzb(id,zdid,cldh,xh,cldlx,gylx,txsl,txdz) "
//						+ "values(?,?,?,?,?,?,?,?)";
//				
//				params = new String[] {seq,zdid, pz2, pz1, pz3, pz4, pz5,pz6};
//				jdbcT.update(s_sql, params);
//				
//				//新增测量点当前数据表
//				String s_sql_clddysj="insert into G_ZDCLDDQSJB(cldid) values(?)";
//				String params2[]=new String[]{seq};
//				jdbcT.update(s_sql_clddysj,params2);
//			} else {
//				//更新测量点配置表
//				s_sql = "update g_zdcldpzb set xh=?,cldlx=?,gylx=?,txsl=?,txdz=? where zdid=? and cldh=?";
//				params = new String[] { pz1, pz3, pz4, pz5,pz6, zdid, pz2 };
//				jdbcT.update(s_sql, params);
//			}
			

//			cldIn += "'" + pz2 + "',";
		}
		
		//配置终端测量点当前数据表中的无效记录
		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		if (dnbNum > 0) {
//			s_sql += " and cldh not in("
//					+ cldIn.substring(0, cldIn.length() - 1) + "))";
//		}
//		params = new String[] { zdid };
		jdbcT.update(s_sql, params);
		

		//删除其它采集设备配置
//		s_sql = "delete g_zdcldpzb  where zdid=?  ";
//		if (dnbNum > 0) {
//			s_sql += "and cldh not in("
//					+ cldIn.substring(0, cldIn.length() - 1) + ")";
//		}
//		params = new String[] { zdid };
//		jdbcT.update(s_sql, params);

		// 五、名称
		String mc = "终端采集设备参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F10", csz, jdbcT); // 终端电能表/交流采样装置参数配置

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端脉冲参数配置F11(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个脉冲配置 csn:脉冲配置(pz1#pz2#pz3#pz4)
	 *            pz1:输入端口号 pz2:所属测量点(1-64) pz3:脉冲属性(0~3依次表示正向有功、正向无功、反向有功、反向无功)
	 *            pz4:电表常数
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F11(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F11"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		int mcNum = ss_csz.length;
		data = Util.decStrToHexStr(mcNum, 1); // 脉冲路数
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		String cldIn = "";

		// 从"终端测量点配置表"取脉冲配置信息(cldlx=02)
		String cldlx = "02";// 测量点类型(02:脉冲)
		s_sql = "select cldh from g_zdcldpzb where zdid=? ";
		params = new String[] { zdid };
		List mcList = jdbcT.queryForList(s_sql, params);

		for (int i = 0; i < mcNum; i++) {
			String csn = ss_csz[i];
			String[] ss_pz = csn.split("#");

			// 脉冲输入端口号
			String pz1 = ss_pz[0];
			String temp_pz1 = Util.decStrToHexStr(pz1, 1);

			// 所属测量点号
			String pz2 = ss_pz[1];
			String temp_pz2 = Util.decStrToHexStr(pz2, 1);

			// 脉冲属性
			String pz3 = ss_pz[2];
			String temp_pz3 = Util.decStrToHexStr(pz3, 1);

			// 电表常数
			String pz4 = ss_pz[3];
			String temp_pz4 = Util.decStrToHexStr(pz4, 2);
			temp_pz4 = Util.convertStr(temp_pz4);

			data = data + temp_pz1 + temp_pz2 + temp_pz3 + temp_pz4;

			if (pz2.equals("0")) {
				// 删除相应的测量点
				continue;
			}

			// 写终端测量点参数配置表
			boolean isIn = false;
			for (int j = 0; j < mcList.size(); j++) {
				Map tempHM = (Map) mcList.get(j);
				if (String.valueOf(tempHM.get("cldh")).equals(pz2)) {
					isIn = true;
					break;
				}
			}

			if (isIn == false) {
				// 新增
				s_sql = "insert into g_zdcldpzb(cldlx,zdid,dkh,cldh,mcsx,mccs) "
						+ "values(?,?,?,?,?,?)";
				params = new String[] { cldlx, zdid, pz1, pz2, pz3, pz4 };
			} else {
				// 更新
				s_sql = "update g_zdcldpzb set cldlx=?,dkh=?,mcsx=?,mccs=? "
						+ " where zdid=? and cldh=?";
				params = new String[] { cldlx, pz1, pz3, pz4, zdid, pz2 };
			}
			jdbcT.update(s_sql, params);

			cldIn += "'" + pz2 + "',";
		}

		// 删除脉冲量配置
		s_sql = "delete g_zdcldpzb " + "where zdid=? and cldlx=?";
		if (mcNum > 0) {
			s_sql += " and cldh not in("
					+ cldIn.substring(0, cldIn.length() - 1) + ")";
		}
		params = new String[] { zdid, cldlx };
		jdbcT.update(s_sql, params);

		// 五、名称
		String mc = "[AFN04F11]设置终端脉冲参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F11", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：开关量类输入设备装置配置参数F12(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个配置的ID	
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F12(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F12"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// 采集设备数量
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
//		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// 每个配置ID
			String cs_id = ss_csz[i];
			s_sql="select * from g_zdkglsrsbpzb where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// 设备序号
			String pz1 = String.valueOf(cldMap.get("xh"));// 设备序号(1字节)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// 采集设备所属测量点号
			String pz2 = String.valueOf(cldMap.get("cldh"));;// 采集设备所属测量点号(2字节)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// 设备类型
			String pz3 = String.valueOf(cldMap.get("sblx"));// 设备类型
			String data3 = Util.decStrToHexStr(pz3, 1);
			
			// 硬件输入接口号
			String pz4 = String.valueOf(cldMap.get("yjsrjkh"));// 硬件输入接口号
			String data4 = Util.decStrToHexStr(pz4, 1);

			// 设备从属组序号
			String pz5 = String.valueOf(cldMap.get("sbcszxh"));// 设备从属组序号
			String data5 = Util.decStrToHexStr(pz5, 1);
			
			// 设备从属组序号
			String pz6 = String.valueOf(cldMap.get("sbznbh"));// 设备从属组序号
			String data6 = Util.decStrToHexStr(pz6, 1);

			

			data += data1 + data2 + data3 + data4 + data5 + data6;
         }
		
		//删除终端测量点当前数据表中的无效记录
//		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		jdbcT.update(s_sql, params);
		



		// 五、名称
		String mc = "开关量类输入设备装置配置参数F12";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F12", csz, jdbcT); // 终端电能表/交流采样装置参数配置

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电气设备控制点参数F13(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个配置的ID
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F13(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F13"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// 采集设备数量
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
//		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// 每个配置ID
			String cs_id = ss_csz[i];
			s_sql="select * from g_zddqsbpzb where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// 设备序号
			String pz1 = String.valueOf(cldMap.get("xh"));// 设备序号(1字节)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// 采集设备所属测量点号
			String pz2 = String.valueOf(cldMap.get("cldh"));;// 采集设备所属测量点号(2字节)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// 设备类型
			String pz3 = String.valueOf(cldMap.get("sblx"));// 设备类型
			String data3 = Util.decStrToHexStr(pz3, 1);
			
			//设备额定功率
			String pz4 = String.valueOf(cldMap.get("edgl"));; 
			String data4 = Util.convertStr(Util.decStrToHexStr(pz4, 2));
			
			// 处理能力
			String pz5 = String.valueOf(cldMap.get("clnl"));
			String data5 = Util.decStrToHexStr(pz5, 1);
			
			// 接线方式
			String pz6 = String.valueOf(cldMap.get("jxfs"));
			String data6 = Util.decStrToHexStr(pz6, 1);
			
			// 硬件输出接口号
			String pz7 = String.valueOf(cldMap.get("yjscjkh"));
			String data7 = Util.decStrToHexStr(pz7, 1);
			
			// 辅助触点接口号
			
			String pz8 = String.valueOf(cldMap.get("fzcdjkh"));
			String data8="";
            if("CC".equalsIgnoreCase(pz8)){
            	data8= "CC";
			}else{
				data8 = Util.decStrToHexStr(pz8, 1);
			}

			// 故障灯硬件接口号
			String pz9 = String.valueOf(cldMap.get("gzdyjjkh"));
			String data9="";
            if("CC".equalsIgnoreCase(pz9)){
            	data9= "CC";
			}else{
				data9 = Util.decStrToHexStr(pz9, 1);
			}
			
			// 变频电机或定频电机
			String pz10 = String.valueOf(cldMap.get("sfbp"));
			String data10 = pz10;
			
			

			

			data += data1 + data2 + data3 + data4 + data5 + data6+ data7+ data8+ data9+ data10;
         }
		
		//删除终端测量点当前数据表中的无效记录
//		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		jdbcT.update(s_sql, params);
		



		// 五、名称
		String mc = "电气设备控制点参数F12";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F13", csz, jdbcT); //电气设备控制点参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电气设备启停控制参数F14(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号           
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个电气设备启停控制参数配置
	 *            csn:总加组配置(pz1,...,pz7)--N电气设备启停控制参数配置 
	 *                pz1:第n套控制参数执行依据
	 *                pz2:第n套控制参数最小温度   EE无效
	 *                pz3:第n套控制参数最大温度   EE无效
	 *                pz4:第n套控制参数生效起始日期   EEEE无效
	 *                pz5:第n套控制参数生效截止日期   EEEE无效
	 *                pz6:第n套控制参数启动时长
	 *                pz7:第n套控制参数停止时长
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F14(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception {

		String seq_sjzfs = null;
//		String s_sql = "";
//		String[] params = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P"+cldh; // 信息点
		String xxl = "F14"; // 信息类

		// 四、数据单元
		String data = "";

		String[] ss_csz = csz.split(";");
		//控制参数方案数量
		data+=Util.decStrToHexStr(ss_csz.length, 1);
		
		for(int i=0;i<ss_csz.length;i++){
			String csn[]=ss_csz[i].split(",");
			
			//第n套控制参数执行依据
			String pz1=csn[0];
			if("1".equals(pz1)){
				//第n套控制参数执行依据
				data+=Util.decStrToHexStr(pz1, 1);
				//第n套控制参数最小温度
				String pz2=csn[1];
				String fh="0";
				if(Integer.parseInt(pz2)<0){
					fh="1";
				}
				data+=Util.makeFormat05( fh,String.valueOf(Math.abs(Integer.parseInt(pz2))));
				//第n套控制参数最大温度
				String pz3=csn[2];
				fh="0";
				if(Integer.parseInt(pz3)<0){
					fh="1";
				}
				data+=Util.makeFormat05(fh, String.valueOf(Math.abs(Integer.parseInt(pz3))));
				
				// pz4:第n套控制参数生效起始日期
				data+="EEEE";
				//pz5:第n套控制参数生效截止日期
				data+="EEEE";
				
				
				//pz6:第n套控制参数启动时长
				String pz6=csn[5];
				data+=Util.decStrToHexStr(pz6, 1);
				//pz7:第n套控制参数停止时长
				String pz7=csn[6];
				data+=Util.decStrToHexStr(pz7, 1);
				
			}else if("2".equalsIgnoreCase(pz1)){
				//第n套控制参数执行依据
				data+=Util.decStrToHexStr(pz1, 1);
				
				//第n套控制参数最小温度
				data+="EE";
				//第n套控制参数最大温度
				data+="EE";
				
				// pz4:第n套控制参数生效起始日期
				String pz4=csn[3].replace("-", "");
				data+=Util.makeFormat29(pz4);
				//pz5:第n套控制参数生效截止日期
				String pz5=csn[4].replace("-", "");
				data+=Util.makeFormat29(pz5);
				
				
				//pz6:第n套控制参数启动时长
				String pz6=csn[5];
				data+=Util.decStrToHexStr(pz6, 1);
				//pz7:第n套控制参数停止时长
				String pz7=csn[6];
				data+=Util.decStrToHexStr(pz7, 1);
			}else{
				//第n套控制参数执行依据
				data+=Util.decStrToHexStr(pz1, 1);
				
				//第n套控制参数最小温度
				data+="EE";
				//第n套控制参数最大温度
				data+="EE";
				
				// pz4:第n套控制参数生效起始日期
				data+="EEEE";
				//pz5:第n套控制参数生效截止日期
				data+="EEEE";
				
				
				//pz6:第n套控制参数启动时长
				String pz6=csn[5];
				data+=Util.decStrToHexStr(pz6, 1);
				//pz7:第n套控制参数停止时长
				String pz7=csn[6];
				data+=Util.decStrToHexStr(pz7, 1);
			}
			
			
		}

		
		// 五、名称
		String mc = "[AFN04F14]电气设备启停控制参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F14",  cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	
	/**
	 * 方法简述：水泵水位控制参数F15(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号           
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个水泵水位控制参数配置
	 *            csn:控制参数配置(pz1,...,pz14)--第N水泵水位控制参数配置 
	 *                pz1:第n套控制参数-输出硬件号
	 *                pz2:第n套控制参数-备用输出硬件号
	 *                pz3:第n套控制参数-同时工作使能
	 *                pz4:第n套控制参数-主备切换时间
	 *                pz5:第n套控制参数-池体号码
	 *                pz6:第n套控制参数-水位档位
	 *                pz7:第n套控制参数-逻辑关系
	 *                pz8:第n套控制参数-池体号码(另一个池体)
	 *                pz9:第n套控制参数-水位档位(另一个池体)
	 *                pz10:第n套控制参数-控制动作
	 *                pz11:第n套控制参数-最小温度
	 *                pz12:第n套控制参数-最大温度
	 *                pz13:第n套控制参数-启动时间
	 *                pz14:第n套控制参数-停止时间
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F15(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P"+cldh; // 信息点
		String xxl = "F15"; // 信息类

		// 四、数据单元
		String data = "";

		String[] ss_csz = csz.split(";");
		
		//设备配置数量n
		data+=Util.decStrToHexStr(ss_csz.length, 1);
		
		for(int i=0;i<ss_csz.length;i++){
			
			String csn[]=ss_csz[i].split(",");
			
			//设备序号
			data+=Util.decStrToHexStr(csn[0], 1);
			
			//设备所属测量点号（编号）
			data+=Util.convertStr(Util.decStrToHexStr(csn[1], 2));
			
			//输出硬件号
			if("EE".equalsIgnoreCase(csn[2])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[2])||"CC".equalsIgnoreCase(csn[2])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[2], 1);
			}
			
			//备用输出硬件号
			if("EE".equalsIgnoreCase(csn[3])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[3])||"CC".equalsIgnoreCase(csn[3])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[3], 1);
			}
			
			//同时工作使能
			data+=csn[4];
			
			//主备切换时间
			if(null==csn[5]||csn[5].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[5])||"CC".equalsIgnoreCase(csn[5])){
				data+=csn[5];
			}else{
			    data+=Util.decStrToHexStr(csn[5], 1);
			}
			
			//池体号码
			if("EE".equalsIgnoreCase(csn[6])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[6], 1);
			}
			
			//水位档位
			if("EE".equalsIgnoreCase(csn[7])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[7], 1);
			}
			
			//逻辑关系
			if("EE".equalsIgnoreCase(csn[8])||"CC".equalsIgnoreCase(csn[8])){
				data+=csn[8];
			}else{
			    data+=Util.decStrToHexStr(csn[8], 1);
			}
			
			//池体号码(另一个池体)
			if("EE".equalsIgnoreCase(csn[9])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[9], 1);
			}
			
			//水位档位(另一个池体)
			if("EE".equalsIgnoreCase(csn[10])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[10], 1);
			}
			
			//控制动作
			if("EE".equalsIgnoreCase(csn[11])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[11], 1);
			}
			
			//最小温度
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
			
			//最大温度
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
			
			//启动时间
			if(null==csn[14]||csn[14].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[14])||"CCCC".equalsIgnoreCase(csn[14])){
				data+=csn[14];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[14], 2));
			}
			
			//停止时间
			if(null==csn[15]||csn[15].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[15])||"CCCC".equalsIgnoreCase(csn[15])){
				data+=csn[15];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[15], 2));
			}
		}

		
		// 五、名称
		String mc = "[AFN04F15]水泵水位控制参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F15", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：风机电磁阀控制参数F16(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号           
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个风机控制参数配置
	 *            csn:控制参数配置(pz1,...,pz10)--第N台风机控制参数配置 
	 *                pz1:第n套控制参数-输出硬件号
	 *                pz2:第n套控制参数-备用输出硬件号
	 *                pz3:第n套控制参数-主备切换时间
	 *                pz4:第n套控制参数-与水泵联动使能
	 *                pz5:第n套控制参数-最小温度
	 *                pz6:第n套控制参数-最大温度
	 *                pz7:第n套控制参数-启动时间
	 *                pz8:第n套控制参数-停止时间
	 *                pz9:第n套控制参数-频率
	 *                pz10:第n套控制参数-控制动作
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F16(String txfs, String xzqxm, String zddz,String cldh,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P"+cldh; // 信息点
		String xxl = "F16"; // 信息类

		// 四、数据单元
		String data = "";

		String[] ss_csz = csz.split(";");
		
		//设备配置数量n
		data+=Util.decStrToHexStr(ss_csz.length, 1);
		
		for(int i=0;i<ss_csz.length;i++){
			String csn[]=ss_csz[i].split(",");
			
			//设备序号
			data+=Util.decStrToHexStr(csn[0], 1);
			
			//设备所属测量点号（编号）
			data+=Util.convertStr(Util.decStrToHexStr(csn[1], 2));
			
			//输出硬件号
			if("EE".equalsIgnoreCase(csn[2])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[2])||"CC".equalsIgnoreCase(csn[2])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[2], 1);
			}
			
			//备用输出硬件号
			if("EE".equalsIgnoreCase(csn[3])){
				data+="EE";
			}else if("0".equalsIgnoreCase(csn[3])||"CC".equalsIgnoreCase(csn[3])){
				data+="CC";
			}else{
			    data+=Util.decStrToHexStr(csn[3], 1);
			}
			
			
			//主备切换时间
			if(null==csn[4]||csn[4].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[4])||"CC".equalsIgnoreCase(csn[4])){
				data+=csn[4];
			}else{
			    data+=Util.decStrToHexStr(csn[4], 1);
			}
			
			//与水泵联动使能
			data+=csn[5];
			
			//最小温度
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
			
			//最大温度
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
			
			//启动时间
			if(null==csn[8]||csn[8].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[8])||"CCCC".equalsIgnoreCase(csn[8])){
				data+=csn[8];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[8], 2));
			}
			
			//停止时间
			if(null==csn[9]||csn[9].length()<=0){
				data+="CCCC";
			}else if("EEEE".equalsIgnoreCase(csn[9])||"CCCC".equalsIgnoreCase(csn[9])){
				data+=csn[9];
			}else{
			    data+=Util.convertStr(Util.decStrToHexStr(csn[9], 2));
			}
			
			//频率
			if(null==csn[10]||csn[10].length()<=0){
				data+="CC";
			}else if("EE".equalsIgnoreCase(csn[10])||"CC".equalsIgnoreCase(csn[10])){
				data+=csn[10];
			}else{
			    data+=Util.decStrToHexStr(csn[10], 1);
			}
			
			//控制动作
			if("EE".equalsIgnoreCase(csn[11])){
				data+="EE";
			}else{
			    data+=Util.decStrToHexStr(csn[11], 1);
			}
		}

		
		// 五、名称
		String mc = "[AFN04F16]风机电磁阀控制参数";

		//调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F16", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	
	/**
	 * 方法简述：F17：ORP,HP 上下限设置(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个配置的ID
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F17(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F17"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// 采集设备数量
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// 每个配置ID
			String cs_id = ss_csz[i];
			s_sql="select * from g_zdorpphxzb where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// 设备序号
			String pz1 = String.valueOf(cldMap.get("xh"));// 设备序号(1字节)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// 采集设备所属测量点号
			String pz2 = String.valueOf(cldMap.get("cldh"));;// 采集设备所属测量点号(2字节)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// ORP上限
			String pz3 = String.valueOf(cldMap.get("orpsx"));// ORP上限
			if(!"EEEE".equalsIgnoreCase(pz3)){
				int i_pz3=Integer.parseInt(pz3);
				if(i_pz3<0){
					pz3= Util.makeFormat28(1,Math.abs(i_pz3));
				}else{
					pz3= Util.makeFormat28(0,Math.abs(i_pz3));
				}
			}
			
			// ORP下限
			String pz4 = String.valueOf(cldMap.get("orpxx"));// ORP下限
			if(!"EEEE".equalsIgnoreCase(pz4)){
				int i_pz4=Integer.parseInt(pz4);
				if(i_pz4<0){
					pz4= Util.makeFormat28(1,Math.abs(i_pz4));
				}else{
					pz4= Util.makeFormat28(0,Math.abs(i_pz4));
				}
			}
			
			// PH数值上限
			String pz5 = String.valueOf(cldMap.get("phsx"));// PH数值上限
			if(!"EEEE".equalsIgnoreCase(pz5)){
				pz5=Util.makeFormat30(pz5);
			}
			
			//  PH数值下限
			String pz6 = String.valueOf(cldMap.get("phxx"));// PH数值下限
			if(!"EEEE".equalsIgnoreCase(pz6)){
				pz6=Util.makeFormat30(pz6);
			}
			

			data += data1 + data2 + pz3 + pz4 + pz5 + pz6;
         }
		
		//删除终端测量点当前数据表中的无效记录
//		s_sql = "delete g_zdclddqsjb  where cldid not in(select id from g_zdcldpzb)";
//		jdbcT.update(s_sql, params);
		



		// 五、名称
		String mc = "ORP,HP 上下限设置F17";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F17", csz, jdbcT); //ORP,HP 上下限设置

		return seq_sjzfs;
	}
	
	/**
	 * 方法简述：F18：超声波水位上下限设置(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个站点水池的ID
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F18(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		String s_sql = "";
		String[] params = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F18"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		
		int dnbNum = ss_csz.length;// 采集设备数量
		data = Util.convertStr(Util.decStrToHexStr(dnbNum, 1));
		
		
		List cldList = null;
		Map cldMap = null;
		for (int i = 0; i < dnbNum; i++) {
			// 每个配置ID
			String cs_id = ss_csz[i];
			s_sql="select * from m_station_pool where id=?";
			params = new String[] { cs_id };
			cldList = jdbcT.queryForList(s_sql, params);
			if (cldList.size() == 0) {
				continue;
			}
			cldMap = (Map) cldList.get(0);
			
			// 设备序号
			String pz1 = String.valueOf(cldMap.get("pooleid"));// 设备序号(1字节)
			String data1 = Util.convertStr(Util.decStrToHexStr(pz1, 1));

			// 采集设备所属测量点号
			String pz2 = String.valueOf(cldMap.get("pooleid"));;// 采集设备所属测量点号(2字节)
			String data2 = Util.convertStr(Util.decStrToHexStr(pz2, 2));
			
			// 池深
			String pz3 = String.valueOf(cldMap.get("deep"));
			String data3=Util.makeFormat22(pz3);
			
			//池体水位上限
			String pz4 = String.valueOf(cldMap.get("upper"));
			String data4=Util.makeFormat22(pz4);
			//池体水位下限
			String pz5 = String.valueOf(cldMap.get("lower"));
			String data5=Util.makeFormat22(pz5);
			
			data += data1 + data2 + data3 + data4 + data5;
        }


		// 五、名称
		String mc = "超声波水位上下限设置F18";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F13", csz, jdbcT); //电气设备控制点参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端脉冲参数配置F11(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param mcxx
	 *            ArrayList 终端所配脉冲的信息（端口号、测量点、脉冲属性、电表常数）(均为string型)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdmcpz(String txfs, String xzqxm, String zddz,
			ArrayList mcxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F11"; // 信息类

		// 四、数据单元
		String data = "";
		int mcNum = mcxx.size();
		String sMcnum = Integer.toHexString(mcNum);
		if (sMcnum.length() < 2) {
			sMcnum = "0" + sMcnum;
		}
		data = sMcnum; // 脉冲路数
		for (int i = 0; i < mcNum; i++) {
			Map hm = (Map) mcxx.get(i);

			// 脉冲输入端口号
			String dkh = String.valueOf(hm.get("dkh"));
			dkh = Integer.toHexString(Integer.parseInt(dkh));
			if (dkh.length() < 2) {
				dkh = "0" + dkh;
			}

			// 所属测量点号
			String cldh = String.valueOf(hm.get("cldh"));
			cldh = Integer.toHexString(Integer.parseInt(cldh));
			if (cldh.length() < 2) {
				cldh = "0" + cldh;
			}

			// 脉冲属性
			String mcsx = String.valueOf(hm.get("mcsx"));
			mcsx = Integer.toHexString(Integer.parseInt(mcsx));
			if (mcsx.length() < 2) {
				mcsx = "0" + mcsx;
			}

			// 电表常数
			String dbcs = String.valueOf(hm.get("dbcs"));
			dbcs = Integer.toHexString(Integer.parseInt(dbcs));
			int len_dbcs = dbcs.length();
			for (int j = 0; j < 4 - len_dbcs; j++) {
				dbcs = "0" + dbcs;
			}
			dbcs = Util.convertStr(dbcs);

			data = data + dkh + cldh + mcsx + dbcs;
		}

		// 五、名称
		String mc = "设置终端脉冲参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端电压/电流模拟量参数配置F13(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param mnlxx
	 *            ArrayList 终端所配模拟量信息（端口号、测量点号、模拟量属性）(均为string型)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZddydlmnlpz(String txfs, String xzqxm, String zddz,
			ArrayList mnlxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F13"; // 信息类

		// 四、数据单元
		String data = "";
		int mnlNum = mnlxx.size();
		String sMnlnum = Util.decStrToHexStr(mnlNum, 1);
		data = sMnlnum;

		for (int i = 0; i < mnlNum; i++) {
			Map hm = (Map) mnlxx.get(i);
			// 端口号
			String dkh = String.valueOf(hm.get("dkh"));
			dkh = Util.decStrToHexStr(dkh, 1);

			// 测量点号
			String cldh = String.valueOf(hm.get("cldh"));
			cldh = Util.decStrToHexStr(cldh, 1);

			// 模拟量属性
			String mnlsx = String.valueOf(hm.get("mnlsx"));
			mnlsx = Util.binStrToHexStr(mnlsx, 1);

			data += dkh + cldh + mnlsx;
		}

		// 五、名称
		String mc = "设置：终端电压/电流模拟量参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端总加组配置F14(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzxx
	 *            ArrayList 终端所配总加组信息 [1、Map:key = 总加组号(String)； value =
	 *            总加测量点信息(ArrayList) 2、总加测量点信息ArrayList里是Map,
	 *            包括：测量点号(cldh:String); 正反向标志(zfxbz:String)<0:正向；1:反向>;
	 *            运算符标志(ysfbz:String)<0:加；1:减>; ]
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdzjzpz(String txfs, String xzqxm, String zddz,
			ArrayList zjzxx) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F14"; // 信息类

		// 四、数据单元
		String data = "";
		String[] params = new String[] {};
		// 总加组数量
		int zjzNum = zjzxx.size();
		if (zjzNum > 0) {
			// 删除表"终端总加测量点配置表(ZDZJCLDPZB)"中该终端的记录
			sSql = "delete zdzjcldpzb " + "where xzqxm=? and zddz=?";
			params = new String[] { xzqxm, zddz };
			jdbcT.update(sSql, params);

			// 删除表"终端总加组配置表(ZDZJZPZB)"中该终端的记录
			sSql = "delete zdzjzpzb " + "where xzqxm=? and zddz=?";
			jdbcT.update(sSql, params);
		}
		// ---data---
		data = Util.intToHexStr(zjzNum, 1);
		for (int i = 0; i < zjzNum; i++) {
			// <--------------每个总加组下--------------->
			Map hm_zjz = (Map) zjzxx.get(i);
			// 总加组号
			String sZjzh = (String) (hm_zjz.keySet().toArray())[0];

			// 写表"终端总加组配置表(ZDZJZPZB)"
			sSql = "insert into zdzjzpzb(xzqxm,zddz,zjzxh) " + "values(?,?,?)";
			params = new String[] { xzqxm, zddz, sZjzh };
			jdbcT.update(sSql, params);

			// ---data---
			data += Util.decStrToHexStr(sZjzh, 1);

			// 总加测量点
			ArrayList lst_zjcld = (ArrayList) hm_zjz.get(sZjzh);
			int cldNum = lst_zjcld.size();

			// ---data---
			data += Util.intToHexStr(cldNum, 1);

			for (int j = 0; j < cldNum; j++) {
				// <--------------每个测量点下--------------->
				Map hm_cld = (Map) lst_zjcld.get(j);
				// 测量点号
				String cldh = String.valueOf(hm_cld.get("cldh"));
				// 正反向标志
				String zfxbz = String.valueOf(hm_cld.get("zfxbz"));
				// 运算符标志
				String ysfbz = String.valueOf(hm_cld.get("ysfbz"));

				// 写表"终端总加测量点配置表(ZDZJCLDPZB)"
				sSql = "insert into zdzjcldpzb(xzqxm,zddz,zjzxh,cldh,ysfbz,zfxbz) "
						+ "values(?,?,?,?,?,?)";
				params = new String[] { xzqxm, zddz, sZjzh, cldh, ysfbz, zfxbz };
				jdbcT.update(sSql, params);

				// 测量点号、正反向标志、运算符标志合成一个字节
				String cldAll = "";
				cldh = Util.decStrToBinStr(String.valueOf(Integer
						.parseInt(cldh) - 1), 1);
				cldAll = ysfbz + zfxbz + cldh.substring(2, 8);
				cldAll = Util.binStrToHexStr(cldAll, 1);

				// ---data---
				data += cldAll;

			}

		}

		// 五、名称
		String mc = "设置终端总加组参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端有功总电能量差动越限事件参数配置F15(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cs
	 *            String[][7] 参数 cs[i][0]:对比总加组号 cs[i][1]:参照总加组号 cs[i][2]:时间区间
	 *            cs[i][3]:对比方法(0:相对;1:绝对) cs[i][4]:差动越限相对偏差值(%)
	 *            cs[i][5]:差动越限绝对偏差值(kWh) cs[i][6]:差动越限绝对偏差值符号(0:正;1:负)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZdygzdnlcdyxsjcspz(String txfs, String xzqxm,
			String zddz, String[][] cs) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F15"; // 信息类

		// 四、数据单元
		String data = "";
		String[] params = null;
		// 删除原来的差动配置
		sSql = "delete ZDYGZDNLCDYXSJCSPZB " + "where xzqxm=? and zddz=?";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		// 差动数量
		data += Util.decStrToHexStr(cs.length, 1);
		// 每个差动配置
		for (int i = 0; i < cs.length; i++) {
			String dbzjzh = cs[i][0]; // 对比总加组号
			String czzjzh = cs[i][1]; // 参照总加组号
			String sjqj = cs[i][2]; // 时间区间
			String dbff = cs[i][3]; // 对比方法(0:相对;1:绝对)
			String cdyxxdpcz = cs[i][4]; // 差动越限相对偏差值(%)
			String cdyxjdpcz = cs[i][5]; // 差动越限绝对偏差值(kWh)
			String cdyxjdpczfh = cs[i][6]; // 差动越限绝对偏差值符号(0:正;1:负)

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

		// 五、名称
		String mc = "终端有功总电能量差动越限事件参数配置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询中继数据(AFN=10H)
	 * 
	 * @param txfs
	 *            String 通信方式(01:短信;02:GPRS;06:串口)
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param dbgylx
	 *            String 电表规约类型
	 * @param dnbdz
	 *            String 电能表地址
	 * @param dnbsjxdm
	 *            String 电能表数据项代码
	 * @param btl
	 *            String 波特率(000:表示300;...111:表示19200)
	 * @param tzw
	 *            String 停止位(0:1位;1:2位)
	 * @param jym
	 *            String 校验码(00:无校验;10:偶校验;11:齐校验)
	 * @param ws
	 *            String 位数(00-11:表示5-8)
	 * @param bwcssj
	 *            String 报文超时时间(单位:10ms)
	 * @param zjcssj
	 *            String 字节超时时间(单位:10ms)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String query_zj(String txfs, String xzqxm, String zddz,
			String dbgylx, String dnbdz, String dnbsjxdm, String btl,
			String tzw, String jym, String ws, String bwcssj, String zjcssj)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4B";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "10"; // 查询中继数据

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);// 不带时标
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、数据单元标识DADT(DA=Pn;DT=Fn)
		String sDADT = "";
		String sDA = Util.getDA("P0");
		String sDT = Util.getDT("F1");
		sDADT = Util.convertStr(sDA) + Util.convertStr(sDT);

		// 4、数据单元
		String sDATA = "";
		// 端口号
		sDATA += "01";
		// 转发控制字
		sDATA += Util.binStrToHexStr(btl + tzw + jym + ws, 1);
		// 报文超时时间
		sDATA += Util.decStrToHexStr(bwcssj, 1);
		// 字节超时时间
		sDATA += Util.decStrToHexStr(zjcssj, 1);
		// 中继命令
		String zjml = "";
		if (dbgylx.equalsIgnoreCase("01")) {
			// DLT-645规约
			zjml = dlt645Parse.encode(dnbdz, dnbsjxdm);
			cat.info("中继命令:" + zjml);
		}
		sDATA += Util.decStrToHexStr(zjml.length() / 2, 1);
		sDATA += zjml;

		// 5、附加信息AUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		int iLEN = sCSDATA.length();
		iLEN = iLEN * 2 + 1;
		String sLEN = Util.decStrToHexStr(iLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// 写“数据帧发送表”
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz,dbgylxdm) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?,?)";
		String[] params = new String[] { seq_sjzfs, zdid, sAFN,
				sSEQ1.toUpperCase(), Util.decStrToHexStr(iZdpfc, 1),
				rq.substring(4, 12), sSJZ, dbgylx };
		jdbcT.update(sSql, params);

		// 写“数据标识子表”
		sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,sjdybsz,sjdybsmc) "
				+ "values(seq_sjzfsmx.nextval,?,?,'P0F1',?,?)";
		params = new String[] { seq_sjzfs, sAFN,
				Util.convertStr(sDA) + Util.convertStr(sDT), "读中继数据" };
		jdbcT.update(sSql, params);

		// 发送
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询终端参数配置(AFN=0AH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sjxxx
	 *            String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号） sjxxx[i][1]
	 *            信息类（Fn）
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String query_zdcspz(String txfs, String xzqxm, String zddz,
			String[][] sjxxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		String s_sql = "";
		String[] params = null;

		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4B";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "0A"; // 查询终端参数配置

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);// 不带时标
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、n个数据单元标识DADT(DA=Pn;DT=Fn),无数据单元
		String sDADT = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 数据点号
			String xxl = sjxxx[i][1];// 数据类Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			String tempDADT = sDA + sDT;

			sDADT += tempDADT;

			if (xxl.equalsIgnoreCase("F10")) {
				String cldlx = "01";// 测量点类型(01:电能表)

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
//					cldlx = "02";// 测量点类型(02:脉冲)
//				} else if (xxl.equalsIgnoreCase("F13")) {
//					cldlx = "03";// 测量点类型(03:模拟量)
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
//				// 总加组配置
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
				// 终端抄表运行参数
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
				// 与终端接口的通信模块的参数设置
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

		// 5、附加信息AUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698版
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// 写“数据帧发送表”
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		s_sql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(s_sql, params);

		// 写“数据标识子表”
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			s_sql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT), "查询终端参数配置" };
			jdbcT.update(s_sql, params);
		}

		// 发送
		s_sql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(s_sql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));

		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询终端参数配置(AFN=0AH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param ksxh
	 *            int 开始装置序号
	 * @param jsxh
	 *            int 结束装置序号
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN0AF10(String txfs, String xzqxm, String zddz,
			int ksxh, int jsxh) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		String s_sql = "";
		String[] params = null;

		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4B";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "0A"; // 查询终端参数配置

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);// 不带时标
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、n个数据单元标识DADT(DA=Pn;DT=Fn),无数据单元
		String sDADT = "";
		String xxdh = "0";// 数据点号
		String xxl = "F10";// 数据类Fn

		String sDA = Util.getDA(Integer.parseInt(xxdh));
		sDA = Util.convertStr(sDA);

		String sDT = Util.getDT(xxl);
		sDT = Util.convertStr(sDT);

		String tempDADT = sDA + sDT;

		sDADT += tempDADT;

		// 查询测量点数量
		sDADT += Util.convertStr(Util.decStrToHexStr(jsxh - ksxh + 1, 2));
		for (int m = ksxh; m <= jsxh; m++) {
			// 装置序号
			sDADT += Util.convertStr(Util.decStrToHexStr(m, 2));
		}

		// 5、附加信息AUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698版
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// 写“数据帧发送表”
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		s_sql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(s_sql, params);

		// 写“数据标识子表”

		s_sql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
				+ "sjdybsz,sjdybsmc) "
				+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
		params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
				Util.convertStr(sDA) + Util.convertStr(sDT), "查询终端参数配置" };
		jdbcT.update(s_sql, params);

		// 发送
		s_sql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(s_sql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));

		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询终端事件F1/F2(AFN=0EH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sjlx
	 *            String 事件类型(1:重要事件；2：一般事件)
	 * @param sjqszz
	 *            int 事件起始指针
	 * @param sjjszz
	 *            int 事件结束指针
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String queryZdsj(String txfs, String xzqxm, String zddz,
			String sjlx, int sjqszz, int sjjszz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4B";

		// 二、应用功能码
		String afn = "0E"; // 查询事件

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = ""; // 信息类
		if (sjlx.equals("1")) {
			xxl = "F1";
		} else if (sjlx.equals("2")) {
			xxl = "F2";
		}

		// 四、数据单元
		String data = "";
		data = Util.intToHexStr(sjqszz, 1) + Util.intToHexStr(sjjszz, 1);

		// 五、名称
		String mc = "";
		if (sjlx.equals("1")) {
			mc = "查询终端重要事件";
		} else if (sjlx.equals("2")) {
			mc = "查询终端一般事件";
		}
		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：测量点基本参数配置F25(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            int 测量点号
	 * @param cldjbcs
	 *            HashMap 测量点基本参数（PT、CT、额定电压、最大电流、接线方式）[均为String型]
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCldjbcspz(String txfs, String xzqxm, String zddz,
			int cldh, HashMap cldjbcs) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F25"; // 信息类

		// 四、数据单元
		String data = "";
		String sPT = String.valueOf(cldjbcs.get("pt")); // BIN
		sPT = Util.decStrToHexStr(sPT, 2);
		sPT = Util.convertStr(sPT);

		String sCT = String.valueOf(cldjbcs.get("ct")); // BIN
		sCT = Util.decStrToHexStr(sCT, 2);
		sCT = Util.convertStr(sCT);

		String sEDDY = String.valueOf(cldjbcs.get("eddy")); // 额定电压（BCD码，2个字节,一位小数，最大位为百位）
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

		String sZDDL = String.valueOf(cldjbcs.get("zddl")); // 最大电流（BCD码，1个字节,一位小数，最大位为十位）
		if (sZDDL.indexOf(".") == -1) {
			sZDDL = sZDDL + ".0";
		}
		String sZDDL1 = sZDDL.substring(0, sZDDL.indexOf("."));
		String sZDDL2 = sZDDL.substring(sZDDL.indexOf(".") + 1, sZDDL
				.indexOf(".") + 2);
		sZDDL = sZDDL1 + sZDDL2;

		String sJXFS = "0" + String.valueOf(cldjbcs.get("jxfs")); // 接线方式

		data = sPT + sCT + sEDDY + sZDDL + sJXFS;

		// 五、名称
		String mc = "设置测量点" + String.valueOf(cldh) + "的基本参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：测量点基本参数配置F25(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            int 测量点号
	 * @param csz
	 *            String 参数值(cs1;cs2;cs3) cs1:PT cs2:CT cs3:漏电流临界值
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F25(String txfs, String xzqxm, String zddz,
			int cldh, String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F25"; // 信息类

		// 四、数据单元
		String data = "";

		String[] ss_csz = csz.split(";");

		// 1、电压互感器倍率PT
		String cs1 = ss_csz[0];
		String pt = Util.decStrToHexStr(cs1, 2);
		pt = Util.convertStr(pt);

		// 2、电流互感器倍率CT
		String cs2 = ss_csz[1];
		String ct = Util.decStrToHexStr(cs2, 2);
		ct = Util.convertStr(ct);
		
		// 3、漏电流临界值
		String cs3 = ss_csz[2];
		String ldlljz = Util.decStrToHexStr(cs3, 2);
		ldlljz = Util.convertStr(ldlljz);

		

		data = pt + ct +ldlljz;

		// 五、名称
		String mc = "设置测量点" + String.valueOf(cldh) + "的基本参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F25", cldh + "@" + csz, jdbcT); // 测量点基本参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：遥控F1/F2(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param lch
	 *            String 轮次号(1-8轮)
	 * @param ykbz
	 *            String 遥控标志(55:遥控跳闸；AA：允许合闸)
	 * @param xdsj
	 *            String 限电时间(0-15),单位:0.5h
	 * @param gjyssj
	 *            String 告警延时时间(0-15),单位:1min
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendYk(String txfs, String xzqxm, String zddz, String lch,
			String ykbz, String xdsj, String gjyssj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + lch; // 信息点
		String xxl = ""; // 信息类
		if (ykbz.equals("55")) {
			xxl = "F1";
		} else if (ykbz.equals("AA")) {
			xxl = "F2";
		}

		// 四、数据单元
		String data = "";
		if (ykbz.equals("55")) {
			String temp_xdsj = Util.decStrToBinStr(xdsj, 1);
			String temp_gjyssj = Util.decStrToBinStr(gjyssj, 1);
			data = temp_gjyssj.substring(4, 8) + temp_xdsj.substring(4, 8);

			data = Util.binStrToHexStr(data, 1);
		}

		// 五、名称
		String mc = "";
		if (ykbz.equals("55")) {
			mc = "遥控跳闸(轮次" + lch + ")";
		} else if (ykbz.equals("AA")) {
			mc = "允许合闸(轮次" + lch + ")";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String value = "";
		if (ykbz.equals("55")) {
			value = lch + ";" + xdsj + "#" + gjyssj + "#" + Util.getNowTime();
		} else if (ykbz.equals("AA")) {
			value = lch + ";" + "AA";
		}
		writeCsszzcb(seq_sjzfs, "yktzzt", value, jdbcT); // 遥控跳闸状态

		return seq_sjzfs;
	}

	/**
	 * 方法简述：功控告警时间F49(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param lch
	 *            String 轮次号(1-8轮)
	 * @param gkgjsj
	 *            String 功控告警时间(0-60min)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendGkgjsj(String txfs, String xzqxm, String zddz,
			String lch, String gkgjsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + lch; // 信息点
		String xxl = "F49"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.decStrToHexStr(gkgjsj, 1);

		// 五、名称
		String mc = "功控告警时间(轮次" + lch + ")";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "lcgkgjsj", lch + ";" + gkgjsj, jdbcT); // 轮次功控告警时间

		return seq_sjzfs;
	}

	

	/**
	 * 方法简述：终端电能量费率F22(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param fl
	 *            String[14][3] fl[0][0]:费率1的符号(0:正;1:负)
	 *            fl[0][1]:费率1的单位(0:厘;1:元) fl[0][2]:费率1的值
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDnlfl(String txfs, String xzqxm, String zddz,
			String[][] fl) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F22"; // 信息类

		// 四、数据单元
		String data = "";
		for (int i = 0; i < fl.length; i++) {
			String flfh = fl[i][0];
			String fldw = fl[i][1];
			String flz = fl[i][2];

			data += Util.makeFormat03(flz, fldw, flfh);
		}

		// 五、名称
		String mc = "终端电能量费率设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String value = "";
		for (int i = 0; i < fl.length; i++) {
			String flfh = fl[i][0];
			String fldw = fl[i][1];
			String flz = fl[i][2];
			value += flfh + "#" + fldw + "#" + flz + ";";
		}
		writeCsszzcb(seq_sjzfs, "fl", value, jdbcT); // 终端电能量费率

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端催费告警参数F23(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cfgjcs
	 *            String 催费告警参数:24位(D23-D0),每位对应1小时, 置1告警,置0不告警,
	 *            比如:D0=1表示00:00-01:00告警
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCfgjcs(String txfs, String xzqxm, String zddz,
			String cfgjcs) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F23"; // 信息类

		// 四、数据单元
		String data = "";
		for (int i = 0; i < 3; i++) {
			String temps = cfgjcs.substring((3 - i - 1) * 8, (3 - i) * 8);
			data += Util.binStrToHexStr(temps, 1);
		}

		// 五、名称
		String mc = "终端催费告警参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "cfgjcs", cfgjcs, jdbcT); // 终端催费告警参数

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端催费告警投入标志F26/F34(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param trbz
	 *            String 投入标志：55:投入;AA:解除
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCfgjtrbz(String txfs, String xzqxm, String zddz,
			String trbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = ""; // 信息类
		if (trbz.equals("55")) {
			xxl = "F26";
		} else if (trbz.equals("AA")) {
			xxl = "F34";
		}

		// 四、数据单元
		String data = ""; // 无数据单元

		// 五、名称
		String mc = "";
		if (trbz.equals("55")) {
			mc = "终端催费告警投入";
		} else if (trbz.equals("AA")) {
			mc = "终端催费告警解除";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "cfgjtrbz", trbz, jdbcT); // 终端催费告警投入标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端电能量费率时段和费率数设置F21(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sd
	 *            String[][] 时段{sd[i][0]:时段(x-y,0-48);
	 *            sd[i][1]:费率(0000:费率1;0001:费率2;...;1101:费率14)}
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDnlflsd(String txfs, String xzqxm, String zddz,
			String[][] sd) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F21"; // 信息类

		// 四、数据单元
		String data = "";
		String temp_data = "";
		Map hmFL = new HashMap();
		int sd_len = sd.length;
		for (int i = 0; i < sd_len; i++) {
			String sd_xh = sd[i][0]; // 时段序号
			String sd_fl = sd[i][1]; // 时段费率
			hmFL.put(sd_fl, "0");

			String[] xh = sd_xh.split("-");
			int xh_len = Integer.parseInt(xh[1]) - Integer.parseInt(xh[0]);
			for (int j = 0; j < xh_len; j++) {
				temp_data = temp_data + sd_fl;
			}
		}
		int data_len = temp_data.length();
		for (int i = 0; i < 192 - data_len; i++) {// 24字节
			temp_data = "0" + temp_data;
		}

		for (int i = 0; i < 24; i++) {
			String temps = temp_data.substring(i * 8, (i + 1) * 8);
			temps = temps.substring(4) + temps.substring(0, 4);// 两个4字符倒转
			temps = Util.binStrToHexStr(temps, 1);// 一个字节

			data = data + temps;// 低位在先传
		}
		// 费率个数
		int flgs = hmFL.keySet().size();
		data += Util.decStrToHexStr(flgs, 1);

		// 五、名称
		String mc = "终端电能量费率时段和费率数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写"终端电能量费率时段"表
		String[] params = null;
		// 删除旧记录
		String sSql = "delete ZDDNLFLSD where xzqxm=? and zddz=?";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		// 插入新记录
		int sdh = 0;// 时段号
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
	 * 方法简述：月电量控定值设定F46(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param dz
	 *            String 定值
	 * @param dzfh
	 *            String 定值符号:0:正;1:负
	 * @param dzdw
	 *            String 定值单位:0:kWh;1:MWh
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendYdkdz(String txfs, String xzqxm, String zddz, int zjzh,
			String dz, String dzfh, String dzdw) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F46"; // 信息类

		// 四、数据单元
		String data = "";
		data = Util.makeFormat03(dz, dzdw, dzfh);
		String[] params = null;
		// 删除"终端月电控配置表"中该总加组的月电控配置
		String sSql = "delete ZDYDKPZB "
				+ "where xzqxm='?' and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// 插入新配置
		sSql = "insert into zdydkpzb(xzqxm,zddz,zjzxh,dz,dzfh,dzdw) "
				+ "values(?,?,?,?,?,?)";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), dz, dzfh,
				dzdw };
		jdbcT.update(sSql, params);

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的月电量控定值";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：购电量控定值设定F47(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param gddh
	 *            String 购电单号
	 * @param bz
	 *            String 标志:55:追加;AA:刷新
	 * @param gdfh
	 *            String 购电符号:0:正;1:负
	 * @param gdz
	 *            String 购电值
	 * @param bjmxfh
	 *            String 报警门限符号:0:正;1:负
	 * @param bjmxz
	 *            String 报警门限值
	 * @param tzmxfh
	 *            String 跳闸门限符号:0:正;1:负
	 * @param tzmxz
	 *            String 跳闸门限值
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendGdkdz(String txfs, String xzqxm, String zddz, int zjzh,
			String gddh, String bz, String gdfh, String gdz, String bjmxfh,
			String bjmxz, String tzmxfh, String tzmxz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F47"; // 信息类

		// 四、数据单元
		String data = "";
		data += Util.convertStr(Util.decStrToHexStr(gddh, 4));
		data += bz;
		data += Util.makeFormat03(gdz, "0", gdfh);
		data += Util.makeFormat03(bjmxz, "0", bjmxfh);
		data += Util.makeFormat03(tzmxz, "0", tzmxfh);
		String[] params = null;

		// 归档"终端购电控配置表"中该总加组的购电控配置
		String sSql = "update ZDGDKPZB set flag='0' "
				+ "where xzqxm=? and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// 插入新配置
		sSql = "insert into zdgdkpzb(xzqxm,zddz,zjzxh,gddh,bz,gdfh,gdz,"
				+ "bjmxfh,bjmxz,tzmxfh,tzmxz,flag) "
				+ "values(?,?,?,?,?,?,?,?,?,?,?,'1')";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), gddh, bz,
				gdfh, gdz, bjmxfh, bjmxz, tzmxfh, tzmxz };
		jdbcT.update(sSql, params);

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的购电量控定值";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：月电控投入标志设定F15/F23(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param trbz
	 *            String 投入标志:55:投入;AA:解除
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendYdktrbz(String txfs, String xzqxm, String zddz, int zjzh,
			String trbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = ""; // 信息类
		if (trbz.equals("55")) {
			xxl = "F15";
		} else if (trbz.equals("AA")) {
			xxl = "F23";
		}

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "";
		if (trbz.equals("55")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "月电控投入";
		} else if (trbz.equals("AA")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "月电控解除";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "ydktrbz", zjzh + "#" + trbz, jdbcT); // 月电控投入标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：购电控投入标志设定F16/F24(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param trbz
	 *            String 投入标志:55:投入;AA:解除
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendGdktrbz(String txfs, String xzqxm, String zddz, int zjzh,
			String trbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = ""; // 信息类
		if (trbz.equals("55")) {
			xxl = "F16";
		} else if (trbz.equals("AA")) {
			xxl = "F24";
		}

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "";
		if (trbz.equals("55")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "购电控投入";
		} else if (trbz.equals("AA")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "购电控解除";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "gdktrbz", zjzh + "#" + trbz, jdbcT); // 购电控投入标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电控轮次设定F48(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param lc
	 *            String[8] 轮次受控情况(lc[0]-lc[7]:第1轮-第8轮,0:不受控，1：受控)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDklc(String txfs, String xzqxm, String zddz, int zjzh,
			String[] lc) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F48"; // 信息类

		// 四、数据单元
		String data = "";
		for (int i = 0; i < 8; i++) {
			data = lc[i] + data;
		}
		data = Util.binStrToHexStr(data, 1);

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的电控轮次";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写"终端总加组配置表"
		String sSql = "update zdzjzpzb set dklc1=?,dklc2=?,"
				+ "dklc3=?,dklc4=?,dklc5=?," + "dklc6=?,dklc7=?,dklc8=? "
				+ "where xzqxm=? and zddz=? and zjzxh=?";
		String[] params = new String[] { lc[0], lc[1], lc[2], lc[3], lc[4],
				lc[5], lc[6], lc[7], xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端声音告警标志设置F57(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sygjbz
	 *            String 声音告警标志:D0-D23按位表示0-23点, 每位表示一个小时,如：0表示00:00-01:00;
	 *            置1表示相应时段允许告警,置0表示相应时段不允许告警
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendSygjbz(String txfs, String xzqxm, String zddz,
			String sygjbz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F57"; // 信息类

		// 四、数据单元
		String data = "";
		for (int i = 0; i < 3; i++) {
			data += Util
					.binStrToHexStr(sygjbz.substring(8 * i, 8 * (i + 1)), 1);
		}
		data = Util.convertStr(data);

		// 五、名称
		String mc = "设置声音告警标志";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "sygjbz", sygjbz, jdbcT); // 声音告警标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：谐波限值设置F60(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param xbxz
	 *            String[][2] 谐波限值,依次为: 总畸变电压含有率上限值#符号; 奇次谐波电压含有率上限值#符号;
	 *            偶次谐波电压含有率上限值#符号; 总畸变电流有效值上限值#符号; 2次谐波电流有效值上限值#符号; ...
	 *            18次谐波电流有效值上限值#符号; 3次谐波电流有效值上限值#符号; ... 19次谐波电流有效值上限值#符号;
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendXbxz(String txfs, String xzqxm, String zddz,
			String[][] xbxz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F60"; // 信息类

		// 四、数据单元
		String data = "";
		for (int i = 0; i < xbxz.length; i++) {
			if (i <= 2) {
				data += Util.makeFormat05(xbxz[i][1], xbxz[i][0]);
			} else {
				data += Util.makeFormat06(xbxz[i][1], xbxz[i][0]);
			}
		}

		// 五、名称
		String mc = "设置谐波限值";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String temps = "";
		for (int i = 0; i < xbxz.length; i++) {
			temps += xbxz[i][0] + "#" + xbxz[i][1] + ";";
		}
		writeCsszzcb(seq_sjzfs, "xbxz", temps, jdbcT); // 谐波限值

		return seq_sjzfs;
	}

	/**
	 * 方法简述：测量点限值参数设置F26(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param dyhglsx
	 *            String 电压合格率上限
	 * @param dyhglxx
	 *            String 电压合格率下限
	 * @param dydxmx
	 *            String 电压断相门限
	 * @param gymx
	 *            String 过压门限
	 * @param qymx
	 *            String 欠压门限
	 * @param glmx
	 *            String 过流门限#符号
	 * @param eddlmx
	 *            String 额定电流门限#符号
	 * @param lxdlsx
	 *            String 零序电流上限#符号
	 * @param szglssx
	 *            String 视在功率上上限
	 * @param szglsx
	 *            String 视在功率上限
	 * @param sxdybphxz
	 *            String 三相电压不平衡限值#符号
	 * @param sxdlbphxz
	 *            String 三相电流不平衡限值#符号
	 * @param lxsysjxz
	 *            String 连续失压时间限值
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCldxzcs(String txfs, String xzqxm, String zddz,
			String cldh, String dyhglsx, String dyhglxx, String dydxmx,
			String gymx, String qymx, String glmx, String eddlmx,
			String lxdlsx, String szglssx, String szglsx, String sxdybphxz,
			String sxdlbphxz, String lxsysjxz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F26"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss = null;

		// 电压合格率上限
		data += Util.makeFormat07(dyhglsx);
		// 电压合格率下限
		data += Util.makeFormat07(dyhglxx);
		// 电压断相门限
		data += Util.makeFormat07(dydxmx);
		// 过压门限
		data += Util.makeFormat07(gymx);
		// 欠压门限
		data += Util.makeFormat07(qymx);
		// 过流门限
		ss = glmx.split("#");
		data += Util.makeFormat06(ss[1], ss[0]);
		// 额定电流门限
		ss = eddlmx.split("#");
		data += Util.makeFormat06(ss[1], ss[0]);
		// 零序电流上限
		ss = lxdlsx.split("#");
		data += Util.makeFormat06(ss[1], ss[0]);
		// 视在功率上上限
		data += Util.makeFormat23(szglssx);
		// 视在功率上限
		data += Util.makeFormat23(szglsx);
		// 三相电压不平衡限值
		ss = sxdybphxz.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);
		// 三相电流不平衡限值
		ss = sxdlbphxz.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);
		// 连续失压时间限值
		data += Util.decStrToHexStr(lxsysjxz, 1);

		// 五、名称
		String mc = "设置测量点限值参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String temps = cldh + "@" + dyhglsx + ";" + dyhglxx + ";" + dydxmx
				+ ";" + gymx + ";" + qymx + ";" + glmx + ";" + eddlmx + ";"
				+ lxdlsx + ";" + szglssx + ";" + szglsx + ";" + sxdybphxz + ";"
				+ sxdlbphxz + ";" + lxsysjxz + ";";
		writeCsszzcb(seq_sjzfs, "cldxzcs", temps, jdbcT); // 测量点限值参数

		return seq_sjzfs;
	}

	/**方法简述：神经网络算法使能F26(AFN=04H)
	   * @param 	xzqxm	String 		行政区县码
	   * @param 	zddz  	String 		终端地址
	   * @param 	csz  	String		参数值 55使能  AA不使能
	   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	   */
	public String sendAFN04F26(String txfs, String xzqxm, String zddz,String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F26"; // 信息类

		// 四、数据单元
		String data = csz;

		// 五、名称
		String mc = "神经网络算法使能";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F26",  csz, jdbcT); // 神经网络算法使能

		return seq_sjzfs;

	}
	
	/**方法简述：神经网络训练样本参数F27(AFN=04H)
	   * @param 	xzqxm	String 		行政区县码
	   * @param 	zddz  	String 		终端地址
	   * @param 	csz  	String		参数值 cs1;...;cs3
	   *                                    其中cs1：温度
	   *                                       cs2:ORP
	   *                                       cs3:风机频率
	   * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	   */
	public String sendAFN04F27(String txfs, String xzqxm, String zddz,String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F27"; // 信息类

		// 四、数据单元
		String data = "";
		
		String[] ss_csz = csz.split(";");
		//温度
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
		
		//频率
		String cs3 = ss_csz[2];
		if(cs3.startsWith("-")){
			data += Util.makeFormat06("1",cs3.replace("-", ""));
		}else{
			data += Util.makeFormat06("0",cs3);
		}

		// 五、名称
		String mc = "神经网络训练样本参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F27",  csz, jdbcT); // 神经网络训练样本参数

		return seq_sjzfs;

	}

	/**
	 * 方法简述：测量点功率因数分段限值设置F28(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param csz
	 *            String 参数值(cs1;cs2) cs1:功率因数分段限值1(pz1#pz2) pz1:符号(0:正;1:负)
	 *            pz2:限值 cs2:功率因数分段限值2(pz1#pz2) pz1:符号(0:正;1:负) pz2:限值
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F28(String txfs, String xzqxm, String zddz,
			String cldh, String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F28"; // 信息类

		// 四、数据单元
		String data = "";

		String[] ss_csz = csz.split(";");
		String[] ss = null;

		// 限值1
		String cs1 = ss_csz[0];
		ss = cs1.split("#");
		data += Util.makeFormat05(ss[0], ss[1]);
		// 限值2
		String cs2 = ss_csz[1];
		ss = cs2.split("#");
		data += Util.makeFormat05(ss[0], ss[1]);

		// 五、名称
		String mc = "[AFN04F28]设置测量点功率因数分段限值";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F28", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端抄表运行参数设置F33(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个参数块 csn:参数块(pz1#...#pz7)
	 *            pz1:终端通信端口号(1个字节,1-31)
	 *            pz2:台区集中抄表运行控制字(2个字节,D5D4D3D2D1D0,如：100101,D15-D6为备用)
	 *            D5->是否要求终端抄读“电表状态字”(1:要求;0:不要求)
	 *            D4->是否要求终端搜寻新增或更换的电表(1:要求;0:不要求)
	 *            D3->是否要求终端定时对电表广播校时(1:要求;0:不要求) D2->要求终端采用广播冻结抄表(1:要求;0:不要求)
	 *            D1->是否要求终端只抄重点表(1:要求;0:抄所有表) D0->是否允许自动抄表(1:不允许自动抄表;0:
	 *            要求终端根据抄表时段自动抄表) pz3:允许抄表时段(12个字节,D95D94...D1D0,如:10...11)
	 *            D95->时段23:45~24:00自动抄表状态(1:不允许自动抄表;0:允许)
	 *            D94->时段23:30~23:45自动抄表状态(1:不允许自动抄表;0:允许) ...
	 *            D1->时段00:15~00:30自动抄表状态(1:不允许自动抄表;0:允许)
	 *            D0->时段00:00~00:15自动抄表状态(1:不允许自动抄表;0:允许)
	 *            pz4:抄表日-日期(4个字节,D30D29...D1D0,如:11...01,D31备用)
	 *            D30->每月31日的抄表状态(1:有效;0:无效) D29->每月30日的抄表状态(1:有效;0:无效) ...
	 *            D1->每月2日的抄表状态(1:有效;0:无效) D0->每月1日的抄表状态(1:有效;0:无效)
	 *            pz5:抄表日-时间(2个字节,时分，hhmm,如:0930表示9点30分) pz6:终端抄表间隔(1个字节,1-60)
	 *            pz7
	 *            :对电表广播校时定时时间(3个字节，日时分,ddhhmm,当日为00时表示每天校时,如000930表示每天9点30分校时)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F33(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F33"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		int num = ss_csz.length;
		// 参数块数量
		data += Util.decStrToHexStr(num, 1);
		for (int i = 0; i < num; i++) {
			// 每个参数块
			String csn = ss_csz[i];
			String[] ss_pz = csn.split("#");

			// 终端通信端口号(1个字节,1-31)
			String pz1 = ss_pz[0];
			data += Util.decStrToHexStr(pz1, 1);

			// 台区集中抄表运行控制字(2个字节,D5D4D3D2D1D0,如：100101,D15-D6为备用)
			String pz2 = ss_pz[1];
			data += Util.convertStr(Util.binStrToHexStr(pz2, 2));

			// 允许抄表时段(12个字节,D95D94...D1D0,如:10...11)
			String pz3 = ss_pz[2];
			data += Util.convertStr(Util.binStrToHexStr(pz3, 12));

			// 抄表日-日期(4个字节,D30D29...D1D0,如:11...01,D31备用)
			String pz4 = ss_pz[3];
			data += Util.convertStr(Util.binStrToHexStr(pz4, 4));

			// 抄表日-时间(2个字节,时分，hhmm,如:0930表示9点30分)
			String pz5 = ss_pz[4];
			data += Util.convertStr(pz5);

			// 终端抄表间隔(1个字节,1-60)
			String pz6 = ss_pz[5];
			data += Util.decStrToHexStr(pz6, 1);

			// 对电表广播校时定时时间(3个字节,日时分,ddhhmm,当日为00时表示每天校时,如000930表示每天9点30分校时)
			String pz7 = ss_pz[6];
			data += Util.convertStr(pz7);
		}

		// 五、名称
		String mc = "终端抄表运行参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F33", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：与终端接口的通信模块的参数设置F34(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;csn)--N个参数块 csn:参数块(pz1#...#pz7)
	 *            pz1:终端通信端口号(1个字节,1-31)
	 *            pz2:通信波特率(0-7分别表示300,600,1200,2400,4800,7200,9600,19200)
	 *            pz3:停止位(0:1位停止位;1:2位停止位) 
	 *            pz4:有无校验(0:无;1:有) 
	 *            pz5:奇偶校验(0:偶;1:奇)
	 *            pz6:位数(0~3分别表示5-8位) 
	 *            pz7:与终端接口对应端的通信速率
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F34(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F34"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		int num = ss_csz.length;
		// 参数块数量
		data += Util.decStrToHexStr(num, 1);
		for (int i = 0; i < num; i++) {
			// 每个参数块
			String csn = ss_csz[i];
			String[] ss_pz = csn.split("#");

			// 终端通信端口号(1个字节,1-31)
			String pz1 = ss_pz[0];
			data += Util.decStrToHexStr(pz1, 1);

			// 通信波特率(0-7分别表示300,600,1200,2400,4800,7200,9600,19200)
			String pz2 = ss_pz[1];
			// 停止位(0:1位停止位;1:2位停止位)
			String pz3 = ss_pz[2];
			// 有无校验(0:无;1:有)
			String pz4 = ss_pz[3];
			// 奇偶校验(0:偶;1:奇)
			String pz5 = ss_pz[4];
			// 位数(0~3分别表示5-8位)
			String pz6 = ss_pz[5];
			data += Util.binStrToHexStr(pz2 + pz3 + pz4 + pz5 + pz6, 1);

			// 与终端接口对应端的通信速率
			String pz7 = ss_pz[6];
			data += Util.convertStr(Util.decStrToHexStr(pz7, 4));
		}

		// 五、名称
		String mc = "与终端接口的通信模块的参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F34", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端上行通信流量门限设置F36(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1) cs1:月通信流量门限(0表示系统不需要终端进行流量控制)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F36(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F36"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		// 月通信流量门限
		String cs1 = ss_csz[0];
		data += Util.convertStr(Util.decStrToHexStr(cs1, 4));

		// 五、名称
		String mc = "终端上行通信流量门限设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F36", csz, jdbcT);

		return seq_sjzfs;
	}
//	/**
//	* 方法简述：时段功控定值F41(AFN=04H)
//	 * 
//	 * @param xzqxm
//	 *            String 行政区县码
//	 * @param zddz
//	 *            String 终端地址
//	 *            
//	 * @param csz 参数值(cs1;cs2.....csn)1<=n<=24
//	 *        csn(zfn#dzn#xsn)
//	 *           zfn 正负:0正数,1负数
//	 *           dzn 定值整数:1-999
//	 *           xsn 系数:000:10E4
//	 *                    001:10E3
//	 *                    010:10E2
//	 *                    011:10E1
//	 *                    100:10E0
//	 *                    101:10E-1
//	 *                    110:10E-2
//	 *                    111:10E-3
//	 * 
//	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
//	 * @throws Exception
//	 */
//	public String sendAFN04F41(String txfs, String xzqxm, String zddz,
//			String  csz) throws Exception {
//
//		String seq_sjzfs = null;
//		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//
//		// 一、控制码
//		String kzm = "4A";
//
//		// 二、应用功能码
//		String afn = "04"; // 设置命令
//
//		// 三、数据单元标识
//		String xxd = "P0"; // 信息点
//		String xxl = "F41"; // 信息类
//
//		// 四、数据单元
//		String data = "";
//		String[] ss_csz = csz.split(";");
//		
//		//方案标志
//		int fnbz;
//		if(ss_csz.length%8!=0){
//			fnbz=((int)(ss_csz.length/8))+1;
//		}else{
//			fnbz=(int)(ss_csz.length/8);
//		}
//		data+=Util.decStrToHexStr(fnbz, 1);
//		//定值
//		String dzs="";
//		for(int n=0;n<ss_csz.length;n++){
//			String cszn[]=ss_csz[n].split("#");
//			dzs=Util.makeFormat02(cszn[1], cszn[2], cszn[0])+dzs;
//		}
//		if(1==fnbz){
//			//时段号1
//			String sdh1="";
//			for(int i=0;i<ss_csz.length;i++){
//				sdh1="1"+sdh1;
//			}
//			sdh1=Util.binStrToHexStr(sdh1, 1);
//			data=data+sdh1+dzs;
//		}
//		if(2==fnbz){
//			//时段1定值
//			String sd1dz=dzs.substring(0,64);
//			//时段2定值
//			String sd2dz=dzs.substring(64);
//			//时段号1,2
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
//			//时段1定值
//			String sd1dz=dzs.substring(0,64);
//			//时段2定值
//			String sd2dz=dzs.substring(64,128);
//			//时段3定值
//			String sd3dz=dzs.substring(128);
//			//时段号1,2,3
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
//		// 五、名称
//		String mc = "时段功控定值";
//
//		// 调用公共接口
//		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
//				jdbcT);
//
//		// 写参数设置暂存表
//		writeCsszzcb(seq_sjzfs, "AFN04F41", csz, jdbcT);
//
//		return seq_sjzfs;
//	}
	
//	/**
//	 * 方法简述：功率控制的功率计算滑差时间设置F43(AFN=04H)
//	 * 
//	 * @param xzqxm
//	 *            String 行政区县码
//	 * @param zddz
//	 *            String 终端地址
//	 * @param csz
//	 *            String 滑差时间(1~60)
//	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
//	 * @throws Exception
//	 */
//	public String sendAFN04F43(String txfs, String xzqxm, String zddz,
//			String  csz) throws Exception {
//
//		String seq_sjzfs = null;
//
//		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//
//		// 一、控制码
//		String kzm = "4A";
//
//		// 二、应用功能码
//		String afn = "04"; // 参数设置
//
//		// 三、数据单元标识
//		String xxd = "P0"; // 信息点
//		String xxl = "F43"; // 信息类
//
//		// 四、数据单元
//		String data = "";
//		data = Util.decStrToHexStr(csz, 1);
//
//		// 五、名称
//		String mc = "功率控制的功率计算滑差时间";
//
//		// 调用公共接口
//		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
//				jdbcT);
//
//		// 写参数设置暂存表
//		writeCsszzcb(seq_sjzfs, "AFN04F43", csz, jdbcT);
//
//		return seq_sjzfs;
//	}
	

	/**
	 * 方法简述：终端声音告警允许/禁止设置F57(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1) cs1:允许在线时段标志(0-23点,中间以"#"隔开)--时段在线模式
	 *            比如"0#3#5"表示0点、3点和5点允许在线
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F57(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F57"; // 信息类

		// 四、数据单元
		String data = "";
		// 允许在线时段标志(0-23点,中间以"#"隔开)--时段在线模式
		// 如果"0#3#5"表示0点、3点和5点允许在线
		String[] ss = csz.split("#");
		String temps = "000000000000000000000000";
		for (int i = 0; i < ss.length; i++) {
			int tempi = Integer.parseInt(ss[i]);
			temps = temps.substring(0, 23 - tempi) + "1"
					+ temps.substring(24 - tempi);
		}
		data += Util.convertStr(Util.binStrToHexStr(temps, 3));

		// 五、名称
		String mc = "[AFN04F57]终端声音告警允许/禁止设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F57", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电能表异常判别阈值设定F59(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;cs2;cs3;cs4) cs1:电能量超差阀值(数据格式22,x.x)
	 *            cs2:电能表飞走阀值(数据格式22,x.x) cs3:电能表停走阀值(单位:15min)
	 *            cs4:电能表校时阀值(单位:min)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F59(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F59"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");

		// 电能量超差阀值(数据格式22,x.x)
		String cs1 = ss_csz[0];
		data += Util.makeFormat22(cs1);

		// 电能表飞走阀值(数据格式22,x.x)
		String cs2 = ss_csz[1];
		data += Util.makeFormat22(cs2);

		// 电能表停走阀值(单位:15min)
		String cs3 = ss_csz[2];
		data += Util.decStrToHexStr(cs3, 1);

		// 电能表校时阀值(单位:min)
		String cs4 = ss_csz[3];
		data += Util.decStrToHexStr(cs4, 1);

		// 五、名称
		String mc = "[AFN04F59]电能表异常判别阈值设定";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F59", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：谐波限值设置F60(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1;...;cs8) cs1:总畸变电压含有率上限值(格式05,xxx.x,单位:%)
	 *            cs2:奇次谐波电压含有率上限值(格式05,xxx.x,单位:%)
	 *            cs3:偶次谐波电压含有率上限值(格式05,xxx.x,单位:%)
	 *            cs4:各偶次谐波电压含有率上限值(pz2#pz4#pz6#...#pz18)
	 *            cs5:各奇次谐波电压含有率上限值(pz3#pz5#pz6#...#pz19)
	 *            cs6:总畸变电流有效值上限值(格式06,xx.xx,单位:A)
	 *            cs7:各偶次谐波电流有效值上限值(pz2#pz4#pz6#...#pz18)
	 *            cs8:各奇次谐波电流有效值上限值(pz3#pz5#pz6#...#pz19)
	 * 
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F60(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F60"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");
		String[] ss = null;

		// 总畸变电压含有率上限值(格式05,xxx.x,单位:%)
		String cs1 = ss_csz[0];
		data += Util.makeFormat05("0", cs1);

		// 奇次谐波电压含有率上限值(格式05,xxx.x,单位:%)
		String cs2 = ss_csz[1];
		data += Util.makeFormat05("0", cs2);

		// 偶次谐波电压含有率上限值(格式05,xxx.x,单位:%)
		String cs3 = ss_csz[2];
		data += Util.makeFormat05("0", cs3);

		// 各偶次谐波电压含有率上限值(pz2#pz4#pz6#...#pz18)
		String cs4 = ss_csz[3];
		ss = cs4.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat05("0", ss[i]);
		}

		// 各奇次谐波电压含有率上限值(pz3#pz5#pz6#...#pz19)
		String cs5 = ss_csz[4];
		ss = cs5.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat05("0", ss[i]);
		}

		// 总畸变电流有效值上限值(格式06,xx.xx,单位:A)
		String cs6 = ss_csz[5];
		data += Util.makeFormat06("0", cs6);

		// 各偶次谐波电流有效值上限值(pz2#pz4#pz6#...#pz18)
		String cs7 = ss_csz[6];
		ss = cs7.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat06("0", ss[i]);
		}

		// 各奇次谐波电流有效值上限值(pz3#pz5#pz6#...#pz19)
		String cs8 = ss_csz[7];
		ss = cs8.split("#");
		for (int i = 0; i < ss.length; i++) {
			data += Util.makeFormat06("0", ss[i]);
		}

		// 五、名称
		String mc = "[AFN04F60]谐波限值设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F60", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：直流模拟量接入参数F61(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值(cs1)
	 *            cs1:直流模拟量接入标志(1-8路接入标志,1:接入;0:不接入;如：10100001表示第1/2/8路接入)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F61(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String s_sql = "";
		String[] params = null;
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F61"; // 信息类

		// 四、数据单元
		String data = "";
		String cs1 = csz;
		data += Util.binStrToHexStr(Util.convertStrODD(cs1), 1);

		// 从"终端直流模拟量配置表"取直流模拟量配置信息
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

		// 删除其它模拟量配置
		s_sql = "delete g_zdzlmnlpzb " + "where zdid=? ";
		if (zlmnlIn.length() > 0) {
			s_sql += "and zlmnldkh not in("
					+ zlmnlIn.substring(0, zlmnlIn.length() - 1) + ")";
		}
		params = new String[] { zdid };
		jdbcT.update(s_sql, params);

		// 五、名称
		String mc = "[AFN04F61]直流模拟量接入参数设置";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F61", csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端1类数据任务设置F65(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rwh
	 *            String 任务号
	 * @param csz
	 *            String 参数值(cs1;...;cs5) 
	 *            cs1:上报周期(0-31)
	 *            cs2:上报周期单位(0~3依次表示分、时、日、月) 
	 *            cs3:上报基准时间(年月日时分秒,yymmddhhmmss)
	 *            cs4:任务启动/停止标志： 置“55”：启动；置“AA”停止
	 *            cs5:任务信息项 P1@F1#P1F2#....PnFn
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String sendAFN04F65(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + rwh; // 信息点
		String xxl = "F65"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");

		// 上报周期
		String cs1 = ss_csz[0];
		String data1 = Util.decStrToBinStr(cs1, 1).substring(2);
		// 上报周期单位
		String cs2 = ss_csz[1];
		String data2 = Util.decStrToBinStr(cs2, 1).substring(6, 8);
		data += Util.binStrToHexStr(data2 + data1, 1);

		// 上报基准时间
		String cs3 = ss_csz[2];
		Date fsjzsj= DateUtil.parse(cs3);
		String bw_cs3=DateUtil.formatDate(fsjzsj, "yyMMddHHmmss");
		data += Util.convertStr(bw_cs3);
		
		// 任务启动/停止标志： 置“55H”：启动；置“AAH”停止
		String cs4 = ss_csz[3];
		data += cs4;// 任务启动标志
		int num=0;
		String[] ss=null;
		if(ss_csz.length<5){
			data +="";
		}else{
			// 任务数据项
			String cs5 = ss_csz[4];
			ss= cs5.split("#");
	
			// 数据单元标识个数
			num = ss.length;
			data += Util.decStrToHexStr(num, 1);
	
			// 各数据单元标识
			for (int i = 0; i < num; i++) {
				// <--------------每个数据单元标识下-------------->
				String each = ss[i];
				String[] dadt = each.split("@");
				String da = Util.getDA(dadt[0]);// 信息点DA
				String dt = Util.getDT(dadt[1]);// 信息类DT
	            data += Util.convertStr(da) + Util.convertStr(dt);
			}
		}
		

		// 五、名称
		String mc = "[AFN04F65]终端1类数据任务设置(" + rwh + "号任务)";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		String sSql = "";

		// 看“终端任务配置表”对应任务是否已存在
		sSql = "select rwid from g_zdrwpzb "
				+ "where zdid=? and rwlx=? and rwh=?";
		List lst = jdbcT.queryForList(sSql, new String[] { zdid, "1", rwh });
		int count = lst.size();
		String rwid = "";
		// 写“终端任务配置表”
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
		// 写“任务信息项”表
		sSql = "delete g_rwxxx where rwid=?";
		params = new String[] { rwid };
		jdbcT.update(sSql, params);

		for (int i = 0; i < num; i++) {
			// <--------------每个数据单元标识下-------------->
			String each = ss[i];
			String[] dadt = each.split("@");
			String da = dadt[0];// 信息点DA
			String dt = dadt[1];// 信息类DT
			String xh = String.valueOf(i + 1);
			sSql = "insert into g_rwxxx(rwid,xxdh,xxxdm,xh) "
					+ "values(?,?,?,?)";
			params = new String[] { rwid, da, dt, xh };
			jdbcT.update(sSql, params);
		}
		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端2类数据任务设置F66(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rwh
	 *            String 任务号
	 * @param csz
	 *            String 参数值(cs1;...;cs5) cs1:上报周期(0-31)
	 *            cs2:上报周期单位(0~3依次表示分、时、日、月) cs3:上报基准时间(年月日时分秒,yymmddhhmmss)
	 *            cs4:抽取倍率(1-96) cs5:任务数据项(Pm@Fm#...#Pn@Fn)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F66(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + rwh; // 信息点
		String xxl = "F66"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss_csz = csz.split(";");

		// 上报周期
		String cs1 = ss_csz[0];
		String data1 = Util.decStrToBinStr(cs1, 1).substring(2);
		// 上报周期单位
		String cs2 = ss_csz[1];
		String data2 = Util.decStrToBinStr(cs2, 1).substring(6, 8);
		data += Util.binStrToHexStr(data2 + data1, 1);

		// 上报基准时间
		String cs3 = ss_csz[2];
		data += Util.convertStr(cs3);

		// 抽取倍率
		String cs4 = ss_csz[3];
		data += Util.decStrToHexStr(cs4, 1);

		// 任务数据项
		String cs5 = ss_csz[4];
		String[] ss = cs5.split("#");

		// 数据单元标识个数
		int num = ss.length;
		data += Util.decStrToHexStr(num, 1);

		// 各数据单元标识
		for (int i = 0; i < num; i++) {
			// <--------------每个数据单元标识下-------------->
			String each = ss[i];
			String[] dadt = each.split("@");
			String da = Util.getDA(dadt[0]);// 信息点DA
			String dt = Util.getDT(dadt[1]);// 信息类DT

			data += Util.convertStr(da) + Util.convertStr(dt);
		}

		// 五、名称
		String mc = "[AFN04F66]终端2类数据任务设置(" + rwh + "号任务)";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		String sSql = "";

		// 看“终端任务配置表”对应任务是否已存在
		sSql = "select rwid from g_zdrwpzb "
				+ "where zdid=? and rwlx=? and rwh=?";
		List lst = jdbcT.queryForList(sSql, new String[] { zdid, "2", rwh });
		int count = lst.size();
		String rwid = "";
		if (count > 0) {
			rwid = String.valueOf(((Map) lst.get(0)).get("rwid"));
		}
		// 写“终端任务配置表”
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

		// 写“任务信息项”表
		sSql = "delete g_rwxxx where rwid=?";
		params = new String[] { rwid };
		jdbcT.update(sSql, params);

		for (int i = 0; i < num; i++) {
			// <--------------每个数据单元标识下-------------->
			String each = ss[i];
			String[] dadt = each.split("@");
			String da = dadt[0];// 信息点DA
			String dt = dadt[1];// 信息类DT
			String xh = String.valueOf(i + 1);
			sSql = "insert into g_rwxxx(rwid,xxdh,xxxdm,xh) "
					+ "values(?,?,?,?)";
			params = new String[] { rwid, da, dt, xh };
			jdbcT.update(sSql, params);
		}

		return seq_sjzfs;
	}

	/**
	 * 方法简述：1类数据任务启动/停止设置F67(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rwh
	 *            String 任务号
	 * @param csz
	 *            String 参数值(cs1) cs1:任务启动标志(55:启动；AA：停止)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendAFN04F67(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 写“终端任务配置表”
		sSql = "update g_zdrwpzb " + "set qybz=? "
				+ "where zdid=? and rwlx=? and rwh=?";
		String[] params = new String[] { csz, zdid, "1", rwh };
		jdbcT.update(sSql, params);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + rwh; // 信息点
		String xxl = "F67"; // 信息类

		// 四、数据单元
		String data = csz;// 任务启动标志

		// 五、名称
		String mc = "[AFN04F67]" + rwh + "号任务启动标志[" + csz + "](1类数据任务)";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：2类数据任务启动/停止设置F68(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rwh
	 *            String 任务号
	 * @param csz
	 *            String 参数值(cs1) cs1:任务启动标志(55:启动；AA：停止)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendAFN04F68(String txfs, String xzqxm, String zddz,
			String rwh, String csz) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 写“终端任务配置表”
		sSql = "update g_zdrwpzb " + "set qybz=? "
				+ "where zdid=? and rwlx=? and rwh=?";
		String[] params = new String[] { csz, zdid, "2", rwh };
		jdbcT.update(sSql, params);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + rwh; // 信息点
		String xxl = "F68"; // 信息类

		// 四、数据单元
		String data = csz;// 任务启动标志

		// 五、名称
		String mc = "[AFN04F68]" + rwh + "号任务启动标志[" + csz + "](2类数据任务)";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：直流模拟量输入变比F81(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zlmnldkh
	 *            String 直流模拟量端口号
	 * @param csz
	 *            String 参数值(cs1;cs2) cs1:直流模拟量量程起始值(数据格式02;pz1#pz2#pz3)
	 *            pz1:正负标志(0:正;1:负)
	 *            pz2:系数(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10
	 *            ^-1;6:10^-2;7:10^-3) pz3:数值(xxx)
	 *            cs2:直流模拟量量程终止值(数据格式02;pz1#pz2#pz3) pz1:正负标志(0:正;1:负)
	 *            pz2:系数(0:10
	 *            ^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
	 *            pz3:数值(xxx)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F81(String txfs, String xzqxm, String zddz,
			String zlmnldkh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + zlmnldkh; // 信息点
		String xxl = "F81"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss = null;
		String[] ss_csz = csz.split(";");

		// 直流模拟量量程起始值(数据格式02;pz1#pz2#pz3)
		String cs1 = ss_csz[0];
		ss = cs1.split("#");
		cs1 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += Util.convertStr(cs1);

		// 直流模拟量量程终止值(数据格式02;pz1#pz2#pz3)
		String cs2 = ss_csz[1];
		ss = cs2.split("#");
		cs2 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += Util.convertStr(cs2);

		// 五、名称
		String mc = "[AFN04F81]直流模拟量输入变比";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F81", zlmnldkh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：直流模拟量限值F82(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zlmnldkh
	 *            String 直流模拟量端口号
	 * @param csz
	 *            String 参数值(cs1;cs2) cs1:直流模拟量上限(数据格式02;pz1#pz2#pz3)
	 *            pz1:正负标志(0:正;1:负)
	 *            pz2:系数(0:10^4;1:10^3;2:10^2;3:10^1;4:10^0;5;10
	 *            ^-1;6:10^-2;7:10^-3) pz3:数值(xxx)
	 *            cs2:直流模拟量下限(数据格式02;pz1#pz2#pz3) pz1:正负标志(0:正;1:负)
	 *            pz2:系数(0:10^4
	 *            ;1:10^3;2:10^2;3:10^1;4:10^0;5;10^-1;6:10^-2;7:10^-3)
	 *            pz3:数值(xxx)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F82(String txfs, String xzqxm, String zddz,
			String zlmnldkh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + zlmnldkh; // 信息点
		String xxl = "F82"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss = null;
		String[] ss_csz = csz.split(";");

		// 直流模拟量上限(数据格式02;pz1#pz2#pz3)
		String cs1 = ss_csz[0];
		ss = cs1.split("#");
		cs1 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += cs1;

		// 直流模拟量下限(数据格式02;pz1#pz2#pz3)
		String cs2 = ss_csz[1];
		ss = cs2.split("#");
		cs2 = Util.makeFormat02(ss[2], Util.decStrToBinStrByBit(ss[1], 3),
				ss[0]);
		data += cs2;

		// 五、名称
		String mc = "[AFN04F82]直流模拟量限值";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F82", zlmnldkh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：直流模拟量冻结参数F83(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zlmnldkh
	 *            String 直流模拟量端口号
	 * @param csz
	 *            String 参数值(cs1) cs1:直流模拟量冻结密度 0:表示不冻结 1:表示15分钟冻结一次
	 *            2:表示30分钟冻结一次 3:表示60分钟冻结一次 254:表示5分钟冻结一次 255:表示1分钟冻结一次
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F83(String txfs, String xzqxm, String zddz,
			String zlmnldkh, String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 设置命令

		// 三、数据单元标识
		String xxd = "P" + zlmnldkh; // 信息点
		String xxl = "F83"; // 信息类

		// 四、数据单元
		String data = "";

		// 直流模拟量冻结参数
		String cs1 = csz;
		data += Util.decStrToHexStr(cs1, 1);

		// 五、名称
		String mc = "[AFN04F83]直流模拟量冻结参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN04F83", zlmnldkh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	/**
	 * 方法简述：远程控制F1(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号(十进制)
	 * @param csz
	 *            String csz(csz )0x33:开启，0xCC:关闭。
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendAFN05F1(String txfs, String xzqxm, String zddz,String cldh, String csz)
			throws Exception {
		
		
//	  	dispatch.sedAscendSms();
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
//		Dispatch dispatch = (Dispatch)Context.ctx.getBean("dispatchService");
//		//取得用户名称和用户手机号码
//		String[] hm_yhsjhm=Util.getHmAndYhsjhm(xzqxm, zddz, jdbcT);
//		//发送短信
//		dispatch.sedAscendSms(hm_yhsjhm[1], hm_yhsjhm[0]+"，即将跳闸[桐乡供电局]", true);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P"+cldh; // 信息点
		String xxl = "F1"; // 信息类
		
		

		// 五、名称
		String mc = "远程控制";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, csz, mc,
				jdbcT);
		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN05F1", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
    
	/**
	 * 方法简述：变频器控制F2(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号(十进制)
	 * @param csz
	 *            String csz(csz )运行频率。
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendAFN05F2(String txfs, String xzqxm, String zddz,String cldh, String csz)
			throws Exception {
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P"+cldh; // 信息点
		String xxl = "F2"; // 信息类

		// 控制的频率
		csz= Util.decStrToHexStr(csz, 1);

		// 五、名称
		String mc = "远程控制";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, csz, mc,
				jdbcT);
		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN05F2", cldh + "@" + csz, jdbcT);

		return seq_sjzfs;
	}
	
	/**
	 * 方法简述：远程图像抓拍F3(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号(十进制)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendAFN05F3(String txfs, String xzqxm, String zddz,String cldh)
			throws Exception {
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P"+cldh; // 信息点
		String xxl = "F3"; // 信息类

	

		// 五、名称
		String mc = "远程图像抓拍";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, "", mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：允许终端主动上报F29(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendAFN05F29(String txfs, String xzqxm, String zddz)
			throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F29"; // 信息类

		// 四、数据单元
		String data = "";

		// 五、名称
		String mc = "允许终端主动上报";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：终端对时F31(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 日期(格式：yymmddhhmmss，"XX"表示系统时间)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 */
	public String sendAFN05F31(String txfs, String xzqxm, String zddz,
			String csz) throws Exception {

		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F31"; // 信息类

		// 四、数据单元
		String data = "";
		if (csz.equalsIgnoreCase("XX")) {
			data = Util.makeFormat01(Util.getNowTime(), Util.getNowWeek());
		} else {
			data = Util.makeFormat01(csz, Util.getWeek(csz));
		}

		// 五、名称
		String mc = "终端对时";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：测量点功率因数分段限值设置F28(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param xz1
	 *            String 限值1#符号
	 * @param xz2
	 *            String 限值2#符号
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCldglysfdxz(String txfs, String xzqxm, String zddz,
			String cldh, String xz1, String xz2) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = "F28"; // 信息类

		// 四、数据单元
		String data = "";
		String[] ss = null;

		// 限值1
		ss = xz1.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);
		// 限值2
		ss = xz2.split("#");
		data += Util.makeFormat05(ss[1], ss[0]);

		// 五、名称
		String mc = "设置测量点功率因数分段限值";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String temps = cldh + "@" + xz1 + ";" + xz2 + ";";
		writeCsszzcb(seq_sjzfs, "cldglysfdxz", temps, jdbcT); // 测量点功率因数分段限值

		return seq_sjzfs;
	}

	/**
	 * 方法简述：电容器控制投入标志F41/F42(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cldh
	 *            String 测量点号
	 * @param trbz
	 *            String 投入标志(55:投入;AA:切除)
	 * @param drqz
	 *            String 电容器组(D15-D0,置1表示投入或切除,置0表示保持原状)
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendDrqkztrbz(String txfs, String xzqxm, String zddz,
			String cldh, String trbz, String drqz) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + cldh; // 信息点
		String xxl = ""; // 信息类
		if (trbz.equals("55")) {
			xxl = "F41";
		} else if (trbz.equals("AA")) {
			xxl = "F42";
		}

		// 四、数据单元
		String data = "";
		data = Util.binStrToHexStr(drqz, 2);

		// 五、名称
		String mc = "";
		if (trbz.equals("55")) {
			mc = "电容器控制投入";
		} else if (trbz.equals("AA")) {
			mc = "电容器控制切除";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		String temps = cldh + "@" + trbz + ";" + drqz;
		writeCsszzcb(seq_sjzfs, "drqkztrbz", temps, jdbcT); // 电容器控制投入标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：中文信息F32(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zl
	 *            String 中文信息种类
	 * @param bh
	 *            String 中文信息编号
	 * @param hzxx
	 *            String 汉字信息
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendZwxx(String txfs, String xzqxm, String zddz, String zl,
			String bh, String hzxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F32"; // 信息类

		// 四、数据单元
		String data = "";
		// 中文信息种类
		String zwxxzl = Util.decStrToBinStr(zl, 1);
		zwxxzl = zwxxzl.substring(4);
		// 中文信息编号
		String zwxxbh = Util.decStrToBinStr(bh, 1);
		zwxxbh = zwxxbh.substring(4);

		data += Util.binStrToHexStr(zwxxzl + zwxxbh, 1);

		// 中文信息内容
		String zwxxnr = "";
		byte[] bt = hzxx.getBytes("GB2312");
		data += Util.decStrToHexStr(bt.length, 1);

		zwxxnr = Util.bytetostrs(bt);
		// data += Util.convertStr(zwxxnr);
		data += zwxxnr;

		// 五、名称
		String mc = "中文信息";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：功控轮次设定F45(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param lc
	 *            String[8] 轮次受控情况(lc[0]-lc[7]:第1轮-第8轮,0:不受控，1：受控)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendGklc(String txfs, String xzqxm, String zddz, int zjzh,
			String[] lc) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F45"; // 信息类

		// 四、数据单元
		String data = "";
		for (int i = 0; i < 8; i++) {
			data = lc[i] + data;
		}
		data = Util.binStrToHexStr(data, 1);

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的功控轮次";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写"终端总加组配置表"
		String sSql = "update zdzjzpzb set gklc1=?,gklc2=?,"
				+ "gklc3=?,gklc4=?,gklc5=?," + "gklc6=?,gklc7=?,gklc8=? "
				+ "where xzqxm=? and zddz=? and zjzxh=?";
		String[] params = new String[] { lc[0], lc[1], lc[2], lc[3], lc[4],
				lc[5], lc[6], lc[7], xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：功率控制的功率计算滑差时间设置F43(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param hcsj
	 *            String 滑差时间(1~60)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F43(String txfs, String xzqxm, String zddz,
			int zjzh, String hcsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F43"; // 信息类

		// 四、数据单元
		String data = "";
		data = Util.decStrToHexStr(hcsj, 1);
		
		// 写"终端总加组配置表"
		String this_sql = "update g_zdzjzpzb set GLJSHCSJ=? where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?)  and zjzxh=?";
		String[] this_params = new String[] {hcsj,xzqxm, zddz, String.valueOf(zjzh)};
		jdbcT.update(this_sql, this_params);

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的功率计算滑差时间";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "gljshcsj", zjzh + "#" + hcsj, jdbcT); // 功率计算滑差时间

		return seq_sjzfs;
	}

	/**
	 * 方法简述：时段功控投入标志设置F9/F17(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param trbz
	 *            String 投入标志(55:投入；AA：解除)
	 * @param fabh
	 *            String 方案编号(当trbz=AA时，fabh=null)
	 * @param trsd
	 *            String[] 投入时段(当trbz=AA时，trsd=null)(trsd[i]表示已投入的时段号)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN05F9F17(String txfs, String xzqxm, String zddz,
			int zjzh, String trbz, String fabh, String[] trsd) throws Exception {

		String seq_sjzfs = null;
		String temp_dadt = "";
		String temp_sjdybsmc = "";
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = ""; // 信息类
		if (trbz.equals("55")) {
			xxl = "F9";
		} else if (trbz.equals("AA")) {
			xxl = "F17";
		}

		// 四、数据单元
		String data = "";
		String[] params = null;
		if (trbz.equals("55")) {
			// 写"终端总加组配置表"(时段功控投入)
			sSql = "update g_zdzjzpzb set sdgktrbz=? "
					+ "where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?) " + "and zjzxh=? ";
			params = new String[] { fabh, xzqxm, zddz, String.valueOf(zjzh) };
			jdbcT.update(sSql, params);

			String sdgktrbz = "00000000";
			// 时段的投入标志设置为"解除"
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

					// 将相应时段投入标志设置为"投入"
					sSql = "update zdsdgkpzb set sdtrbz='55' "
							+ "where xzqxm=? and zddz=? "
							+ "and zjzxh=? and fah=? and sdh=? ";
					params = new String[] { xzqxm, zddz, String.valueOf(zjzh),
							fabh, String.valueOf(i_trsd) };
					jdbcT.update(sSql, params);
				}

			}
			// 0-2表示方案号1-3
			data = Util.binStrToHexStr(sdgktrbz, 1)
					+ Util.decStrToHexStr(Integer.parseInt(fabh) - 1, 1);

		} else if (trbz.equals("AA")) {
			// 写"终端总加组配置表"(时段功控解除)
			sSql = "update zdzjzpzb set sdgktrbz='AA' "
					+ "where xzqxm=? and zddz=? and zjzxh=? ";
			params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
			jdbcT.update(sSql, params);

		}

		// 五、名称
		String mc = "";
		if (trbz.equals("55")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "时段功控投入";
		} else if (trbz.equals("AA")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "时段功控解除";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	

	/**
	 * 方法简述：厂休功控投入标志设置F10/F18(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param trbz
	 *            String 投入标志(55:投入；AA：解除)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCxgktrbz(String txfs, String xzqxm, String zddz,
			int zjzh, String trbz) throws Exception {

		String seq_sjzfs = null;
		String temp_dadt = "";
		String temp_sjdybsmc = "";
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = ""; // 信息类
		if (trbz.equals("55")) {
			xxl = "F10";
		} else if (trbz.equals("AA")) {
			xxl = "F18";
		}

		// 四、数据单元
		String data = ""; // 无数据单元

		// 五、名称
		String mc = "";
		if (trbz.equals("55")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "厂休功控投入";
		} else if (trbz.equals("AA")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "厂休功控解除";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "cxgktrbz", zjzh + "#" + trbz, jdbcT); // 厂休功控投入标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：营业报停控投入标志设置F11/F19(AFN=05H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param trbz
	 *            String 投入标志(55:投入；AA：解除)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendYybtktrbz(String txfs, String xzqxm, String zddz,
			int zjzh, String trbz) throws Exception {

		String seq_sjzfs = null;
		String temp_dadt = "";
		String temp_sjdybsmc = "";
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "05"; // 控制命令

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = ""; // 信息类
		if (trbz.equals("55")) {
			xxl = "F11";
		} else if (trbz.equals("AA")) {
			xxl = "F19";
		}

		// 四、数据单元
		String data = ""; // 无数据单元

		// 五、名称
		String mc = "";
		if (trbz.equals("55")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "营业报停控投入";
		} else if (trbz.equals("AA")) {
			mc = "设置总加组" + String.valueOf(zjzh) + "营业报停控解除";
		}

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "yybtktrbz", zjzh + "#" + trbz, jdbcT); // 营业报停控投入标志

		return seq_sjzfs;
	}

	/**
	 * 方法简述：时段功控定值设置F41(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param sd
	 *            Map 时段功控定值{key=fah(方案号 String:1-3); value=sddz(时段定值
	 *            String[][]) < 须按时段号升序排; sddz[i][0]:时段号； sddz[i][1]:正负(0：正；1：负)
	 *            sddz[i][2]:时段定值（>=1,<=999） sddz[i][3]:系数(遵照规约,如：000=10E4...）
	 *            >}
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN04F41(String txfs, String xzqxm, String zddz, int zjzh,
			HashMap sd) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F41"; // 信息类

		// 四、数据单元
		String data = "";
		// 删除"终端时段功控配置表"中该总加组的时段功控配置
		sSql = "delete g_zdsdgkpzb " + "where zdid=(select zdid from G_ZDGZ where xzqxm=? and zddz=?) and zjzxh=?";
		String[] params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// 方案标志
		String fabz = "000";// 方案标志(D0~D2表示方案1~3)
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
		data = data + Util.binStrToHexStr(fabz, 1);// 方案标志

		for (int i = 1; i <= 3; i++) {
			if (fabz.substring(3 - i, 3 - i + 1).equals("0")) {
				continue;
			}
			String temp_fah = String.valueOf(i);// 方案号
			String[][] array_sddz = (String[][]) sd.get(temp_fah);
			int array_len = array_sddz.length;

			String sdbz = "00000000";// 时段标志
			String sddz = "";// 各时段定值

			for (int j = 0; j < array_len; j++) {
				String temp_sdh = array_sddz[j][0];// 时段号
				String temp_zf = array_sddz[j][1];// 正负
				String temp_sddz = array_sddz[j][2];// 时段定值
				String temp_xs = array_sddz[j][3];// 系数

				String hex_sddz = Util
						.makeFormat02(temp_sddz, temp_xs, temp_zf);
				int i_sdh = Integer.parseInt(temp_sdh);
				sdbz = sdbz.substring(0, 8 - i_sdh) + "1"
						+ sdbz.substring(8 - i_sdh + 1);

				sddz += hex_sddz;

				// 写"终端时段功控配置表"
				sSql = "insert into g_zdsdgkpzb(id,zdid,zjzxh,fah,sdh,sdgkdz,sdgkdzfh,sdgkdzxs) "
						+ "values( SEQ_GZDSDGKPZID.nextVal,(select zdid from G_ZDGZ where xzqxm=? and zddz=?),?,?,?,?,?,?)";
				params = new String[] { xzqxm, zddz, String.valueOf(zjzh),
						temp_fah, temp_sdh, temp_sddz, temp_zf, temp_xs };
				jdbcT.update(sSql, params);
			}

			data += Util.binStrToHexStr(sdbz, 1) + sddz;// 时段标志及各时段定值
		}

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的时段功控定值";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：厂休功控参数设置F42(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param cxkdz
	 *            String 厂休控定值（>=1,<=999）
	 * @param dzzf
	 *            String 厂休控定值正负(0：正；1：负)
	 * @param dzxs
	 *            String 厂休控定值系数(遵照规约,如：000=10E4...）
	 * @param xdqssj
	 *            String 限电起始时间(hhmm)
	 * @param xdyxsj
	 *            String 限电延续时间(1~48,单位：0.5h)
	 * @param mzxdr
	 *            String 每周限电日(7位字符串,D7~D1分别表示周日到周一)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendCxgkcs(String txfs, String xzqxm, String zddz, int zjzh,
			String cxkdz, String dzzf, String dzxs, String xdqssj,
			String xdyxsj, String mzxdr) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F42"; // 信息类

		// 四、数据单元
		String data = "";
		String hex_cxkdz = Util.makeFormat02(cxkdz, dzxs, dzzf);
		data = data + hex_cxkdz;// 厂休控定值
		data = data + Util.convertStr(xdqssj);// 限电起始时间
		data = data + Util.decStrToHexStr(xdyxsj, 1);// 限电延续时间
		data = data + Util.binStrToHexStr(mzxdr + "0", 1);// 每周限电日

		String[] params = null;
		// 删除"终端厂休控控配置表"中该总加组的厂休控配置
		sSql = "delete zdcxgkpzb " + "where xzqxm=? and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// 写"终端厂休控控配置表"
		sSql = "insert into zdcxgkpzb(xzqxm,zddz,zjzxh,cxkdz,cxkdzfh,cxkdzxs,xdqssj,xdyxsj,mzxdr) "
				+ "values(?,?,?,?,?,?,?,?,?)";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), cxkdz, dzzf,
				dzxs, xdqssj, xdyxsj, mzxdr };
		jdbcT.update(sSql, params);

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的厂休控参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：营业报停控参数设置F44(AFN=04H)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param zjzh
	 *            int 总加组号
	 * @param btqssj
	 *            String 报停起始时间(yymmdd)
	 * @param btjssj
	 *            String 报停结束时间(yymmdd)
	 * @param btkgldz
	 *            String 报停控功率定值（>=1,<=999）
	 * @param dzzf
	 *            String 报停控功率定值正负(0：正；1：负)
	 * @param dzxs
	 *            String 报停控功率定值系数(遵照规约,如：000=10E4...）
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendYybtkcs(String txfs, String xzqxm, String zddz, int zjzh,
			String btqssj, String btjssj, String btkgldz, String dzzf,
			String dzxs) throws Exception {

		String seq_sjzfs = null;
		String sSql = "";

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "04"; // 参数设置

		// 三、数据单元标识
		String xxd = "P" + zjzh; // 信息点
		String xxl = "F44"; // 信息类

		// 四、数据单元
		String data = "";
		String hex_btkgldz = Util.makeFormat02(btkgldz, dzxs, dzzf);
		data = data + Util.convertStr(btqssj);// 报停起始时间
		data = data + Util.convertStr(btjssj);// 报停结束时间
		data = data + hex_btkgldz;// 报停功率定值

		String[] params = null;
		// 删除"终端营业报停控配置表"中该总加组的营业报停控配置
		sSql = "delete zdyybtkpzb " + "where xzqxm=? and zddz=? and zjzxh=?";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh) };
		jdbcT.update(sSql, params);

		// 写"终端营业报停控配置表"
		sSql = "insert into zdyybtkpzb(xzqxm,zddz,zjzxh,btkgldz,btkgldzfh,btkgldzxs,btqssj,btjssj) "
				+ "values(?,?,?,?,?,?,?,?)";
		params = new String[] { xzqxm, zddz, String.valueOf(zjzh), btkgldz,
				dzzf, dzxs, btqssj, btjssj };
		jdbcT.update(sSql, params);

		// 五、名称
		String mc = "设置总加组" + String.valueOf(zjzh) + "的营业报停控参数";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询任务数据F1/F2(AFN=0BH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param rwlx
	 *            String 任务类型(1:1类；2:2类)
	 * @param rwh
	 *            int 任务号
	 * @param qssj
	 *            String 起始时间(格式:yymmddhhmm)
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String queryRwsj(String txfs, String xzqxm, String zddz,
			String rwlx, int rwh, String qssj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4B";

		// 二、应用功能码
		String afn = "0B"; // 任务数据查询
		String temp_afn = "";

		// 三、数据单元标识
		String xxd = "P" + rwh; // 信息点
		String xxl = ""; // 信息类
		if (rwlx.equals("1")) {
			xxl = "F1";
			temp_afn = "0C";
		} else if (rwlx.equals("2")) {
			xxl = "F2";
			temp_afn = "0D";
		}

		// 四、数据单元
		String data = "";
		if (rwlx.equals(2)) {
			data = Util.convertStr(qssj);
		}

		// 五、名称
		String mc = "查询" + String.valueOf(rwh) + "号任务数据(" + rwlx + "类数据任务)";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, temp_afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：下载通知F2(AFN=0FH) 命令字＝01
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param wjm
	 *            String 文件名
	 * @param wjnr
	 *            byte[] 文件内容
	 * @param ip
	 *            String IP地址
	 * @param port
	 *            String 端口
	 * @param cxmklx
	 *            String 程序模块类型（01：主CPU;02：交采CPU）
	 * @param cxjhsj
	 *            String 程序激活时间 YYMMDDhhmm
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendXztz(String txfs, String xzqxm, String zddz, String wjm,
			byte[] wjnr, String ip, String port, String cxmklx, String cxjhsj)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 取当前的时间（格式：YYMMDDHHMMSS）和星期
		String rq = Util.getNowTime();
		String xq = Util.getNowWeek();
		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4A";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "0F"; // 文件传输

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=1)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);
		int iZdpseq = getZdpseq(iZdpfc);
		sSEQ = "7" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、数据单元标识DADT(DA=P0;DT=F2),程序远程下载
		String sDA = Util.getDA("P0");
		sDA = Util.convertStr(sDA);

		String sDT = Util.getDT("F2");
		sDT = Util.convertStr(sDT);

		String sDADT = sDA + sDT;

		// 4、数据单元DATA
		String sDATA = "";
		// 1)下载通知
		sDATA += "01";

		// 2)程序模块类型
		sDATA += Util.add(cxmklx, 1, "0");

		// 3)超时时间
		sDATA += "FF";

		// 4)ip地址
		ip = ip.replace('.', ';');
		String[] IP = ip.split(";");
		sDATA += Util.decStrToHexStr(IP[0], 1) + Util.decStrToHexStr(IP[1], 1)
				+ Util.decStrToHexStr(IP[2], 1) + Util.decStrToHexStr(IP[3], 1);

		// 5)端口号,低位在先传
		sDATA += Util.convertStr(Util.decStrToHexStr(port, 2));

		// 6)下载程序总段数,低位在先传,2字节
//		int count = Decode_0F.fillHM(wjm, wjnr);
		int count=0;
		sDATA += Util.convertStr(Util.decStrToHexStr(count, 2));

		// 7)程序激活时间
		sDATA += Util.convertStr(Util.add(cxjhsj, 5, "0"));

		// 8)下载文件名ASCII,32字节,低位在先传
		byte[] bt_wjm = wjm.getBytes();
		wjm = Util.bytetostrs(bt_wjm);
		for (int i = 0; i < 32 - bt_wjm.length; i++) {
			wjm = "20" + wjm;
		}
		sDATA += Util.convertStr(wjm);

		// 5、附加信息AUX
		String sAUX = "";
		String sPassword = CMContext.getZdmm(xzqxm, zddz);

		sAUX = sPassword;

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		int iLEN = sCSDATA.length();
		iLEN = iLEN * 2 + 1;
		String sLEN = Util.decStrToHexStr(iLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// 写“数据帧发送表”(isxztz='1')
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
				Util.convertStr(sDA) + Util.convertStr(sDT), "远程下载通知" };
		jdbcT.update(sSql, params);

		// 写“终端升级配置表”
		sSql = "delete g_zdsjpzb where zdid= (select zdid from G_ZDGZ where xzqxm=? and zddz=?)";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		sSql = "insert into g_zdsjpzb(zdid,sjzt,sj) "
				+ "values((select zdid from G_ZDGZ where xzqxm=? and zddz=?),'下载通知已下发',sysdate)";
		params = new String[] { xzqxm, zddz };
		jdbcT.update(sSql, params);

		// 发送
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, "", jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：下载取消F2(AFN=0FH) 命令字＝02
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendXzqx(String txfs, String xzqxm, String zddz)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "0F"; // 文件传输

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F2"; // 信息类

		// 四、数据单元
		String data = "";
		// 1)命令字＝下载取消
		data += "02";

		// 2)后面的47个字节补00H
		data = Util.addAfter(data, 48, "0");

		// 五、名称
		String mc = "远程下载取消";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：下载更改程序激活时间F2(AFN=0FH) 命令字＝03
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param cxjhsj
	 *            String 程序激活时间 YYMMDDhhmm
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendXzggcxjhsj(String txfs, String xzqxm, String zddz,
			String cxjhsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "0F"; // 文件传输

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F2"; // 信息类

		// 四、数据单元
		String data = "";
		// 1)命令字＝下载更改程序激活时间
		data += "03";

		// 2)除激活时间外，其它42个字节补00H
		for (int i = 0; i < 10; i++) {
			data += "00";
		}
		data += Util.convertStr(cxjhsj);
		for (int i = 0; i < 32; i++) {
			data += "00";
		}

		// 五、名称
		String mc = "更改程序激活时间";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：下载程序版本切换F2(AFN=0FH) 命令字＝04
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendXzcxbbqh(String txfs, String xzqxm, String zddz)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);

		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "0F"; // 文件传输

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F2"; // 信息类

		// 四、数据单元
		String data = "";
		// 1)命令字＝下载程序版本切换
		data += "04";

		// 2)后面的47个字节补00H
		data = Util.addAfter(data, 48, "0");

		// 五、名称
		String mc = "程序版本切换";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询1类数据(AFN=0CH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sjxxx
	 *            String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号） sjxxx[i][1]
	 *            信息类（Fn）
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String query_1lsj(String txfs, String xzqxm, String zddz,
			String[][] sjxxx) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4B";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "0C"; // 查询1类数据

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、n个数据单元标识DADT(DA=Pn;DT=Fn),无数据单元
		String sDADT = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 数据点号
			String xxl = sjxxx[i][1];// 数据类Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			String tempDADT = sDA + sDT;

			sDADT += tempDADT;
		}

		// 5、附加信息AUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698版
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);
		String[] params = null;
		// 写“数据帧发送表”
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// 写“数据标识子表”
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn

			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			List lstXXX = CMContext.getSjxxx("1lsj");// 1类数据对应的数据信息项
			String xxdlb = "";// 信息点类别：终端、测量点、总加组...
			String xxxmc = "";// 信息项名称
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "终端";
			} else if (xxdlb.equals("1")) {
				xxdlb = "测量点" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "总加组" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "直流模拟量" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"查询" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// 发送
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}
	 
	/**方法简述：查询所有在线终端的0CF2(AFN=0CH)
	   * @return seq_sjzfs String 数据发送表序列（null：失败；1：成功）
	   */
	 public   String query_allzd_0cf2() throws Exception{
		 String seq_sjzfs = null;

			JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
			String sSql = "select xzqxm,zddz from g_zdgz where zt=1";
			List zdlist=jdbcT.queryForList(sSql);
			
			for(int n=0;n<zdlist.size();n++){
				//等待5秒钟
				Thread.sleep(500L);
				String xzqxm = String.valueOf(((Map)zdlist.get(n)).get("xzqxm"));
				String zddz = String.valueOf(((Map)zdlist.get(n)).get("zddz"));
				
				String zdid = Util.getZdid(xzqxm, zddz, jdbcT);

				// 取当前的时间（格式：YYMMDDHHMMSS）
				String rq = Util.getNowTime();

				// 整个数据帧(十六进制字符)
				String sSJZ = "";

				// 一、控制码
				String sContr = "4B";

				// 二、地址域
				String sAddr = "";
				String sAddr1 = Util.convertStr(xzqxm);
				String sAddr2 = Util.convertStr(zddz);
				String sAddr3 = "02";
				sAddr = sAddr1 + sAddr2 + sAddr3;

				// 三、链路用户数据域
				String sUSERDATA = "";
				// 1、应用功能码
				String sAFN = "0C"; // 查询1类数据

				// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
				String sSEQ = "";
				// 取该终端的帧序号计数器和启动帧序号
				int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

				int iZdpseq = getZdpseq(iZdpfc);

				sSEQ = "6" + Integer.toHexString(iZdpseq);
				String sSEQ1 = Integer.toHexString(iZdpseq);

				// 3、n个数据单元标识DADT(DA=Pn;DT=Fn),无数据单元
				String sDADT = "";
				
				String xxdh = "0000";// 数据点号
				String xxl = "F2";// 数据类Fn

				String sDA = Util.getDA(Integer.parseInt(xxdh));
				sDA = Util.convertStr(sDA);

				String sDT = Util.getDT(xxl);
				sDT = Util.convertStr(sDT);

				String tempDADT = sDA + sDT;

				sDADT += tempDADT;
				

				// 5、附加信息AUX
				String sAUX = "";

				sUSERDATA = sAFN + sSEQ + sDADT + sAUX;

				// 校验数据域
				String sCSDATA = sContr + sAddr + sUSERDATA;

				// 四、校验码
				String sCS = Util.getCS(sCSDATA);

				// 五、数据长度
				// 04
				// int iLEN = sCSDATA.length();
				// iLEN = iLEN * 2 + 1;
				// String sLEN = Util.decStrToHexStr(iLEN,2);
				// sLEN = Util.convertStr(sLEN);

				// 698版
				long iLEN = sCSDATA.length() / 2;
				String sLEN = Util.decStrToBinStr(iLEN, 2);
				sLEN = sLEN.substring(2) + "10";
				sLEN = Util.binStrToHexStr(sLEN, 2);
				sLEN = Util.convertStr(sLEN);

				sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
						+ sEnd;

				cat.info("sSJZ:" + sSJZ);
				String[] params = null;
				
				
				// 写“数据帧发送表”
				seq_sjzfs = Util.getSeqSjzfs(jdbcT);
				sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
						+ "values(?,?,?,?,?,'02',?,sysdate,?)";
				params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
						Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
				jdbcT.update(sSql, params);

				// 写“数据标识子表”

				List lstXXX = CMContext.getSjxxx("1lsj");// 1类数据对应的数据信息项
				String xxdlb = "";// 信息点类别：终端、测量点、总加组...
				String xxxmc = "";// 信息项名称
				for (int j = 0; j < lstXXX.size(); j++) {
					Map hm = (Map) lstXXX.get(j);
					if (hm.get("xxxdm").equals(xxl)) {
						xxdlb = hm.get("xxdlb").toString();
						xxxmc = hm.get("xxxmc").toString();

						break;
					}
				}
				
				xxdlb = "终端";
				

				sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
						+ "sjdybsz,sjdybsmc) "
						+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
				params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
						Util.convertStr(sDA) + Util.convertStr(sDT),
						"查询" + xxxmc + "(" + xxdlb + ")" };
				jdbcT.update(sSql, params);
				

				// 发送
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
	 * 方法简述：查询2数据_曲线(AFN=0DH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sjxxx
	 *            String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号） sjxxx[i][1]
	 *            信息类（Fn）
	 * @param qssj
	 *            String 起始时间 yymmddhhmm
	 * @param sjmd
	 *            String 数据密度 1：15分钟；2：30分钟；3：60分钟
	 * @param sjds
	 *            String 数据点数
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String query_2lsj_qx(String txfs, String xzqxm, String zddz,
			String[][] sjxxx, String qssj, String sjmd, String sjds)
			throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4B";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "0D"; // 查询2类数据

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、数据单元标识DADT(DA=Pn;DT=Fn)
		String DADT_DATA = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			DADT_DATA += sDA + sDT;

			// 4、数据单元
			String sDATA = "";
			// 1)起始时间
			sDATA += Util.convertStr(qssj);
			// 2)数据密度
			sDATA += Util.decStrToHexStr(sjmd, 1);
			// 3)数据点数
			sDATA += Util.decStrToHexStr(sjds, 1);
			DADT_DATA += sDATA;
		}

		// 5、附加信息AUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + DADT_DATA + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698版
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// 写“数据帧发送表”
		String[] params = null;
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// 写“数据标识子表”
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);
			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);
			List lstXXX = CMContext.getSjxxx("2lsj_qx");
			String xxdlb = "";// 信息点类别：终端、测量点、总加组...
			String xxxmc = "";// 信息项名称
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "终端";
			} else if (xxdlb.equals("1")) {
				xxdlb = "测量点" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "总加组" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "直流模拟量" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"查询" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// 发送
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询2数据_日冻结(AFN=0DH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sjxxx
	 *            String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号） sjxxx[i][1]
	 *            信息类（Fn）
	 * @param rdjsj
	 *            String 日冻结时间 yymmdd
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String query_2lsj_rdj(String txfs, String xzqxm, String zddz,
			String[][] sjxxx, String rdjsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4B";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "0D"; // 查询2类数据

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、数据单元标识DADT(DA=Pn;DT=Fn)
		String DADT_DATA = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			DADT_DATA += sDA + sDT;

			// 4、数据单元
			String sDATA = "";
			// 1)日冻结时间
			sDATA += Util.convertStr(rdjsj);

			DADT_DATA += sDATA;
		}

		// 5、附加信息AUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + DADT_DATA + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698版
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// 写“数据帧发送表”
		String[] params = null;
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// 写“数据标识子表”
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);
			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);
			List lstXXX = CMContext.getSjxxx("2lsj_rdj");
			String xxdlb = "";// 信息点类别：终端、测量点、总加组...
			String xxxmc = "";// 信息项名称
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "终端";
			} else if (xxdlb.equals("1")) {
				xxdlb = "测量点" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "总加组" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "直流模拟量" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"查询" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// 发送
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 方法简述：查询2数据_月冻结(AFN=0DH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param sjxxx
	 *            String[][2] 数据项信息 sjxxx[i][0] 信息点号（测量点、总加组号） sjxxx[i][1]
	 *            信息类（Fn）
	 * @param ydjsj
	 *            String 月冻结时间 yymm
	 * 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String query_2lsj_ydj(String txfs, String xzqxm, String zddz,
			String[][] sjxxx, String ydjsj) throws Exception {

		String seq_sjzfs = null;

		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		// 整个数据帧(十六进制字符)
		String sSJZ = "";

		// 一、控制码
		String sContr = "4B";

		// 二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "02";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		// 三、链路用户数据域
		String sUSERDATA = "";
		// 1、应用功能码
		String sAFN = "0D"; // 查询2类数据

		// 2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);

		int iZdpseq = getZdpseq(iZdpfc);

		sSEQ = "6" + Integer.toHexString(iZdpseq);
		String sSEQ1 = Integer.toHexString(iZdpseq);

		// 3、数据单元标识DADT(DA=Pn;DT=Fn)
		String DADT_DATA = "";
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);

			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);

			DADT_DATA += sDA + sDT;

			// 4、数据单元
			String sDATA = "";
			// 1)月冻结时间
			sDATA += Util.convertStr(ydjsj);

			DADT_DATA += sDATA;
		}

		// 5、附加信息AUX
		String sAUX = "";

		sUSERDATA = sAFN + sSEQ + DADT_DATA + sAUX;

		// 校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		// 四、校验码
		String sCS = Util.getCS(sCSDATA);

		// 五、数据长度
		// 04
		// int iLEN = sCSDATA.length();
		// iLEN = iLEN * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLEN,2);
		// sLEN = Util.convertStr(sLEN);

		// 698版
		long iLEN = sCSDATA.length() / 2;
		String sLEN = Util.decStrToBinStr(iLEN, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = sBegin + sLEN + sLEN + sBegin + sContr + sAddr + sUSERDATA + sCS
				+ sEnd;

		cat.info("sSJZ:" + sSJZ);

		// 写“数据帧发送表”
		String[] params = null;
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,?,?,?,?,'02',?,sysdate,?)";
		params = new String[] { seq_sjzfs, zdid, sAFN, sSEQ1.toUpperCase(),
				Util.decStrToHexStr(iZdpfc, 1), rq.substring(4, 12), sSJZ };
		jdbcT.update(sSql, params);

		// 写“数据标识子表”
		for (int i = 0; i < sjxxx.length; i++) {
			String xxdh = sjxxx[i][0];// 信息点号
			String xxl = sjxxx[i][1];// 信息类Fn
			String sDA = Util.getDA(Integer.parseInt(xxdh));
			sDA = Util.convertStr(sDA);
			String sDT = Util.getDT(xxl);
			sDT = Util.convertStr(sDT);
			List lstXXX = CMContext.getSjxxx("2lsj_ydj");
			String xxdlb = "";// 信息点类别：终端、测量点、总加组...
			String xxxmc = "";// 信息项名称
			for (int j = 0; j < lstXXX.size(); j++) {
				Map hm = (Map) lstXXX.get(j);
				if (hm.get("xxxdm").equals(xxl)) {
					xxdlb = hm.get("xxdlb").toString();
					xxxmc = hm.get("xxxmc").toString();

					break;
				}
			}
			if (xxdlb.equals("0")) {
				xxdlb = "终端";
			} else if (xxdlb.equals("1")) {
				xxdlb = "测量点" + xxdh;
			} else if (xxdlb.equals("2")) {
				xxdlb = "总加组" + xxdh;
			} else if (xxdlb.equals("3")) {
				xxdlb = "直流模拟量" + xxdh;
			}

			sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,"
					+ "sjdybsz,sjdybsmc) "
					+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
			params = new String[] { seq_sjzfs, sAFN, "P" + xxdh + xxl,
					Util.convertStr(sDA) + Util.convertStr(sDT),
					"查询" + xxxmc + "(" + xxdlb + ")" };
			jdbcT.update(sSql, params);
		}

		// 发送
		sSql = "select sim from G_ZDGZ where zdid=?";
		params = new String[] { zdid };
		List lst = jdbcT.queryForList(sSql, params);
		Map mp = (Map) lst.get(0);
		String SIM = String.valueOf(mp.get("sim"));
		send(txfs, xzqxm, zddz, sSJZ, seq_sjzfs, SIM, jdbcT);

		return seq_sjzfs;
	}

	/**
	 * 按照“格式01”将月份和星期拼成一个字节（16进制字符）
	 * 
	 * @param yf
	 *            String 月份
	 * @param xq
	 *            String 星期
	 * @return sRet 返回十六进制字符
	 */
	private String getXqyf(String yf, String xq) throws Exception {
		String sRet = "";
		if (yf.length() < 2) {
			yf = "0" + yf;
		}
		// 将星期转成十六进制字符
		String b_xq = Integer.toBinaryString(Integer.parseInt(xq));

		if (b_xq.length() == 1) {
			b_xq = "00" + b_xq;
		}
		if (b_xq.length() == 2) {
			b_xq = "0" + b_xq;
		}
		// 将月份的十位跟星期拼成半个字节
		String b_hc = b_xq + yf.substring(0, 1);

		// 将二进制字符转成十六进制字符
		Integer iteger = Integer.valueOf(b_hc, 2);
		String h_hc = Integer.toHexString(iteger.intValue());

		sRet = h_hc + yf.substring(1, 2);
		return sRet;
	}

	/**
	 * 根据终端取帧序号计数器取启动帧序号（即帧序号计数器的后四位）
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @return zxh 帧序号
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
	 * 方法简述：程序升级请求及信息 F1(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param csz
	 *            String 参数值   文件的绝对路径
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN0FF1(String txfs, String xzqxm, String zddz, String csz)
			throws Exception {
		
		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		// 一、控制码
		String kzm = "4A";

		// 二、应用功能码
		String afn = "0F"; // 控制命令

		// 三、数据单元标识
		String xxd = "P0"; // 信息点
		String xxl = "F1"; // 信息类

		// 四、数据单元
		String data = "";
        
		String version[]=new String[2];
		version=Decode_0F.getFileVersion(csz);
		
//		String[] ss_csz = csz.split(";");
		//终端硬件编号
		data +=version[0];
//		String cs1 = ss_csz[0];
//		data += Util.bbhToHexStr(version[0]);
		
		//终端软件编号
		data +=version[1];
//		String cs2 = ss_csz[1];
//		data += Util.bbhToHexStr(cs2);
		
		//起始位置(终端默认)
		data +="00C00300";
//		String cs3 = ss_csz[2];
//		cs3 = Util.convertStr(Util.decStrToHexStr(cs3, 4));
//		data += cs3;

		// 五、名称
		String mc = "程序升级请求及信息";

		// 调用公共接口
		seq_sjzfs = parse(txfs, xzqxm, zddz, kzm, afn, xxd, xxl, data, mc,
				jdbcT);
		// 写参数设置暂存表
		writeCsszzcb(seq_sjzfs, "AFN0FF1", "", jdbcT);
		return seq_sjzfs;
	}

	/**
	 * 方法简述：程序文件传输F2(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param fileName
	 *            String 程序文件绝对路径
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String sendAFN0FF2(String txfs, String xzqxm, String zddz, String fileName)
			throws Exception {
//		String seq_sjzfs = null;
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		Util.updateZdsjpzb(xzqxm, zddz, fileName, jdbcT);
		Decode_0F.respondDownload(xzqxm, zddz, fileName, "0", jdbcT);
		
		//启用重发线程
		Decode_0F_ReSend ss=new Decode_0F_ReSend(xzqxm, zddz, jdbcT, "0");
		new Thread(ss).start();
		
		//Decode_0F.decodeDownload(xzqxm, zddz,"", jdbcT);
		
		return "1";
	}

	/**
	 * 方法简述：单个终端升级
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param fileName
	 *            String 程序文件绝对路径 
	 * @return seq_sjzfs String 数据发送表序列（null：失败；seq_sjzfs：成功）
	 * @throws Exception
	 */
	public String updateSingle(String txfs, String xzqxm, String zddz,
			String fileName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	

	
	
	

	

}
