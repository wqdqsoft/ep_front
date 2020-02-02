package com.powerhigh.gdfas.parse;


import java.util.*;

import org.apache.log4j.*;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.module.Dispatch;
import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.*;

/**
 * Description: 事件解码类(事件查询返回、事件主动上报)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * 编写时间: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 修改人： 修改时间：
 */

public class Decode_0E {
	// 加载日志
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

		// 起/止标志
		String qzbz = str.substring(0, 1);
		if (qzbz.equals("0")) {
			qzbz = "恢复";
		} else {
			qzbz = "发生";
		}
		ss[0] = qzbz;
		ss[1] = Util.hexStrToDecStr(str.substring(4));

		return ss;
	}

	/**
	 * 方法简述：事件处理
	 * 
	 * @param con
	 *            Connection 数据库连接
	 * @param sjsxlx
	 *            String 事件上行类型：1：查询返回；2：主动上报   0:不记录事件
	 * @param s_sjzfsseq
	 *            String 数据帧发送序列
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param s_csdata
	 *            String 报文里用来算CS的数据
	 * 
	 * @return void
	 */
	@SuppressWarnings({ "rawtypes", "unused" })
	public static void dispose(String sjsxlx, String s_sjzfsseq, String xzqxm,
			String zddz, String s_csdata, JdbcTemplate jdbcT) throws Exception {
		String s_sql = "";
		String[] params = null;
		try {
			// 取"数据祯发送明细序列"
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
			// 信息点Pn
			String main_da = mian_dadt.substring(0, 4);
			main_da = Util.tranDA(Util.convertStr(main_da));
			// 信息类Fn
			String main_dt = mian_dadt.substring(4, 8);
			main_dt = Util.tranDT(Util.convertStr(main_dt));

			// 事件类型：1：重要事件；2：一般事件
			String sjlx = main_dt;

			String pnfn = "P" + main_da + "F" + sjlx;

			int index = 24;
			int xh = 0;

			// 当前重要事件计数器
			String zysjjsq = s_csdata.substring(index, index + 2);
			index += 2;
			zysjjsq = Util.hexStrToDecStr(zysjjsq);
			cat.info("zysjjsq:" + zysjjsq);
			if (sjsxlx.equals("1")) {
				// 事件查询返回
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "zdzysjjsq",
						zysjjsq, "终端", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// 当前一般事件计数器
			String ybsjjsq = s_csdata.substring(index, index + 2);
			index += 2;
			ybsjjsq = Util.hexStrToDecStr(ybsjjsq);
			cat.info("ybsjjsq:" + ybsjjsq);

			if (sjsxlx.equals("1")) {
				// 事件查询返回
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "zdybsjjsq",
						ybsjjsq, "终端", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// 事件记录起始指针
			String sjjlqszz = s_csdata.substring(index, index + 2);
			index += 2;
			int i_begin = Integer.parseInt(sjjlqszz, 16);
			cat.info("i_begin:" + i_begin);
			if (sjsxlx.equals("1")) {
				// 事件查询返回
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "sjjlqszz",
						String.valueOf(i_begin), "终端", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// 事件记录结束指针
			String sjjljszz = s_csdata.substring(index, index + 2);
			index += 2;
			int i_end = Integer.parseInt(sjjljszz, 16);
			cat.info("i_end:" + i_end);

			if (sjsxlx.equals("1")) {
				// 事件查询返回
				xh++;
				s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
						+ "values(?,?,?,?,?,?,?,sysdate)";
				params = new String[] { s_sjzfsseq, "0E", pnfn, "sjjljszz",
						String.valueOf(i_end), "终端", String.valueOf(xh) };
				jdbcT.update(s_sql, params);
				// System.out.println("[exceptionDecode]s_sql:"+s_sql);
				cat.info("[exceptionDecode]s_sql:" + s_sql);
			}

			// 事件个数
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
				// <--------------每个事件下--------------->
				// 事件代码ERC
				String sjdm = s_csdata.substring(index, index + 2);
				// 2012-07-25修改，新增对事件代码EE的判断
				if ("EE".equalsIgnoreCase(sjdm)) {
					continue;
				}

				index += 2;
				sjdm = String.valueOf(Integer.parseInt(sjdm, 16));

				// 事件记录长度
				String sjjlcd = s_csdata.substring(index, index + 2);
				index += 2;
				int i_sjjlcd = Integer.parseInt(sjjlcd, 16);

				if (sjdm.equals("0") || i_sjjlcd == 0) {
					continue;
				}
				// 事件内容
				String sjContent = s_csdata.substring(index, index + 2
						* i_sjjlcd);
				index += 2 * i_sjjlcd;

				// 事件发生时间(yymmddhhmm)
				String sjfssj = "";
				// 信息点类别(0：终端；1：测量点；2：总加组；3：直流模拟量)
				String xxdlb = "";
				// 信息点号
				String xxdh = "";
				// 事件摘要
				String sjzy = "";

				if (sjdm.equals("1")) {
					// ERC1:数据初始化和版本变更记录

					// 初始化/版本更新时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 事件标志
					String sjbz = sjContent.substring(10, 12);
					sjbz = Util.hexStrToBinStr(sjbz, 1);

					// 版本变更标志
					String bbbgbz = sjbz.substring(6, 7);

					// 初始化标志
					String cshbz = sjbz.substring(7, 8);

					// 变更前软件版本号(ASCII)
					String temp_bgqrjbbh = sjContent.substring(12, 20);
					temp_bgqrjbbh = Util.convertStr(temp_bgqrjbbh);
					String bgqrjbbh = Util.getASCII(temp_bgqrjbbh);

					// 变更后软件版本号(ASCII)
					String temp_bghrjbbh = sjContent.substring(20, 28);
					temp_bghrjbbh = Util.convertStr(temp_bghrjbbh);
					String bghrjbbh = Util.getASCII(temp_bghrjbbh);

					// 事件摘要
					sjzy = "版本变更标志:" + bbbgbz + ";  初始化标志:" + cshbz
							+ ";  变更前软件版本号:" + bgqrjbbh + ";  变更后软件版本号:"
							+ bghrjbbh;

				} else if (sjdm.equals("2")) {
					// ERC2:参数丢失记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 事件标志
					String sjbz = sjContent.substring(10, 14);
					sjbz = Util.hexStrToBinStr(sjbz, 2);

					// 终端参数丢失标志
					String zdcsdsbz = sjbz.substring(15, 16);

					// 测量点参数丢失标志
					String cldcsdsbz = sjbz.substring(14, 15);

					// 事件摘要
					sjzy = "终端参数丢失标志:" + zdcsdsbz + ";  测量点参数丢失标志:"
							+ cldcsdsbz;

				} else if (sjdm.equals("3")) {
					// ERC3:参数变更记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 启动站地址
					String qdzdz = sjContent.substring(10, 12);
					qdzdz = String.valueOf(Integer.parseInt(qdzdz, 16));

					sjContent = sjContent.substring(12);
					int num = sjContent.length();

					String s_dadt = "";
					for (int j = 0; j < num / 8; j++) {
						// 变更参数数据单元标识n
						String temp_dadt = sjContent.substring(j * 8,
								(j + 1) * 8);

						// 信息点Pn
						String s_da = temp_dadt.substring(0, 4);
						s_da = Util.tranDA(Util.convertStr(s_da));
						s_da = "P" + s_da;

						// 信息类Fn
						String s_dt = temp_dadt.substring(4, 8);
						s_dt = Util.tranDT(Util.convertStr(s_dt));
						s_dt = "F" + s_dt;

						s_dadt = s_dadt + s_da + s_dt + ";";
					}

					// 事件摘要
					sjzy = "启动站地址:" + qdzdz + ";  参数变更过的数据单元标识:" + s_dadt;

				} else if (sjdm.equals("4")) {
					// ERC4:流量异常
					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";
					 
					//当前流量
					String dqll=sjContent.substring(10, 14);
					if("EEEE".equalsIgnoreCase(dqll)){
						sjzy="流量计通信异常";
					}else{
						dqll = Util.tranFormat06(dqll);
						sjzy="流量异常,当前流量："+dqll+"M3/H";
					}

				} else if (sjdm.equals("5")) {
					// ERC5:水位超限
					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";
					 
					//当前水位
					String dqsw=sjContent.substring(10, 14);
					if("EEEE".equalsIgnoreCase(dqsw)){
						sjzy="水位传感器通信异常;";
					}else{
						dqsw = Util.tranFormat06(dqsw);
						sjzy="水位超限,当前水位："+dqsw+"M;";
					}
					
					//当前浮球状态
					String dqfqzt=sjContent.substring(14, 16);
					dqfqzt = Util.hexStrToDecStr(dqfqzt);
					sjzy=sjzy+"水位超限,当前浮球状态："+dqfqzt+"档";
					
					
					if("3".equalsIgnoreCase(dqfqzt)){
						//20160222取得该智慧终端所关联的智能终端
						List znzds=EPService.getZnzd(xzqxm, zddz, jdbcT);
						if(null!=znzds&&znzds.size()>0){
							for(int ii=0;ii<znzds.size();ii++){
								Map zd=(Map)znzds.get(ii);
								List clds=EPService.getZnzdDjcld(String.valueOf(zd.get("zdid")), jdbcT);
								if(null!=clds&&clds.size()>0){
									//20160222关闭智能终端所有类型为电机的测量点
									for(int jj=0;jj<clds.size();jj++){
										Map cld=(Map)clds.get(jj);
										operation.sendAFN05F1("3", String.valueOf(zd.get("xzqxm")), String.valueOf(zd.get("zddz")), String.valueOf(cld.get("cldh")), "CC");
										//20160222间隔1秒
										Thread.sleep(1000L);
									}
									
								}
								
							}
						}
					}else{
						//20160223如果调节池没有超过警戒水位，则允许智能终端自动运行
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
					// ERC6:功控跳闸记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（总加组）
					xxdlb = "2";
					// 信息点号
					xxdh = "1";

					// 总加组号
					String zjzh = sjContent.substring(10, 12);
					zjzh = Util.hexStrToBinStr(zjzh, 1);
					int i_zjzh = Integer.parseInt(zjzh.substring(2, 8), 2);
					xxdh = String.valueOf(i_zjzh);

					// 跳闸轮次
					String tzlc = sjContent.substring(12, 14);
					tzlc = Util.hexStrToBinStr(tzlc, 1);

					// 功控类别
					String gklb = sjContent.substring(14, 16);
					gklb = Util.hexStrToBinStr(gklb, 1);
					gklb = gklb.substring(4, 8);// 当前功率下浮控、营业报停控、厂休控、时段控
					if (gklb.equals("1000")) {
						gklb = "当前功率下浮控";
					} else if (gklb.equals("0100")) {
						gklb = "营业报停控";
					} else if (gklb.equals("0010")) {
						gklb = "厂休控";
					} else if (gklb.equals("0001")) {
						gklb = "时段控";
					}

					// 跳闸前功率(总加功率)
					String tzqgl = sjContent.substring(16, 20);
					String s_tzqgl = Util.tranFormat02(tzqgl);

					// 跳闸后2分钟的功率(总加功率)
					String tzhgl = sjContent.substring(20, 24);
					String s_tzhgl = Util.tranFormat02(tzhgl);

					// 跳闸时功率定值
					String tzsgldz = sjContent.substring(24, 28);
					String s_tzsgldz = Util.tranFormat02(tzsgldz);

					// 事件摘要
					sjzy = "跳闸轮次:" + tzlc + ";  功控类别:" + gklb
							+ ";  跳闸前功率(总加功率):" + s_tzqgl
							+ ";  跳闸后2分钟的功率(总加功率):" + s_tzhgl
							+ ";  跳闸时功率定值:" + s_tzsgldz;
					// Dispatch dispatch =
					// (Dispatch)Context.ctx.getBean("dispatchService");
					// //取得用户名称和用户手机号码
					// String[] hm_yhsjhm=Util.getHmAndYhsjhm(xzqxm, zddz,
					// jdbcT);
					// //发送短信
					// dispatch.sedAscendSms(hm_yhsjhm[1],
					// "尊敬的用户"+hm_yhsjhm[0]+":您没有在规定时间内降低负荷(当前"+s_tzqgl+"kW),本时段的功率定值"+s_tzsgldz+"kW,将执行跳闸操作!",
					// true);

				} else if (sjdm.equals("7")) {
					// ERC7:ORP数值异常
					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";
					 
					//当前ORP
					String dqorp=sjContent.substring(10, 14);
					if("EEEE".equalsIgnoreCase(dqorp)){
						sjzy="ORP传感器通信异常";
					}else{
						dqorp = Util.tranFormat28(dqorp)[0];
						sjzy="ORP数值异常,当前ORP："+dqorp+"MV";
					}
				} else if (sjdm.equals("8")) {
					// ERC8:电机异常事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
      				String cldh = sjContent.substring(10,14);
      				cldh = Util.hexStrToDecStr(Util.convertStr(cldh));
      				xxdh = String.valueOf(Integer.parseInt(cldh));

					// 异常类型
					String yclx = sjContent.substring(14, 16);
					if("01".equalsIgnoreCase(yclx)){
						sjzy="异常类型:过载"+";测量点号:"+cldh;
					}else if("02".equalsIgnoreCase(yclx)){
						sjzy="异常类型:断相"+";测量点号:"+cldh;
					}else if("03".equalsIgnoreCase(yclx)){
						sjzy="异常类型:三相不平衡"+";测量点号:"+cldh;
					}else if("04".equalsIgnoreCase(yclx)){
						sjzy="异常类型:欠载"+";测量点号:"+cldh;
					}else if("05".equalsIgnoreCase(yclx)){
						sjzy="异常类型:接地/漏电"+";测量点号:"+cldh;
					}else if("06".equalsIgnoreCase(yclx)){
						sjzy="异常类型:堵转"+";测量点号:"+cldh;
					}else if("07".equalsIgnoreCase(yclx)){
						sjzy="异常类型:三相监控器通讯异常"+";测量点号:"+cldh;
					}

				} else if (sjdm.equals("9")) {
					// ERC8:摄像头异常事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 变更标志
					String yclx = sjContent.substring(10, 12);
					if("01".equalsIgnoreCase(yclx)){
						sjzy="摄像头异常事件记录:摄像头无响应";
					}else if("02".equalsIgnoreCase(yclx)){
						sjzy="摄像头异常事件记录:CDMA图像报文传输中断";
					}else if("03".equalsIgnoreCase(yclx)){
						sjzy="摄像头异常事件记录:CRC校验错误";
					}else if("04".equalsIgnoreCase(yclx)){
						sjzy="摄像头异常事件记录:485通讯超时";
					}
					

				} else if (sjdm.equals("10")) {
					// ERC10:门禁事件上报
					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";
					 
					//当前门开关状态
					String mkgzt=sjContent.substring(10, 12);
					int i_mkgzt=0;
	      			i_mkgzt = Integer.parseInt(mkgzt,16);
					if("01".equalsIgnoreCase(mkgzt)){
						sjzy="当前门开关状态:开门;";
					}
					if("00".equalsIgnoreCase(mkgzt)){
						sjzy="当前门开关状态:关门;";
					}
					
					//ID卡卡号
					String idkh=sjContent.substring(14, 28);
					idkh=Util.convertStr(idkh);
					idkh=Util.hexStrToDecStr(idkh);
//					sjzy=sjzy+"ID卡号:"+idkh;
					
					//开门合法性标志
	      			String s_kmhfxbz = sjContent.substring(12, 14);
	      			int i_kmhfxbz=0;
	      			i_kmhfxbz = Integer.parseInt(s_kmhfxbz,16);
	      			if(1==i_kmhfxbz){
	      				//设置时间类型为0，不记录到终端事件记录表中
	      				sjsxlx="0";
	      				sjzy=sjzy+"ID卡号:"+idkh+"正常巡检;";
	      				//如果为正常巡更,插入终端门禁记录表
	      				String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
      	      	    	s_sql="insert into M_TERMINAL_ACCESS_RECORD(id,TERMINALID,datatime,ACCESSSTATUS,doorstatus,idcard,status) values(S_ACCESS_RECORD.nextval,?,to_date(?,'yymmddhh24miss'),?,?,?,0)";
      	        		params = new String[] { zdid,sjfssj,String.valueOf(i_kmhfxbz),String.valueOf(i_mkgzt),idkh};
      	        		jdbcT.update(s_sql, params);
	      	        	
	      			}
	      			if(0==i_kmhfxbz){
	      				sjzy=sjzy+"非法入侵;";
	      			}
					
				} else if (sjdm.equals("11")) {
					// ERC11:相序异常

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 起/止标志
					String qzbz = ss[1];

					// 单位：度
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

					// 发生时电能表正向有功总电能示值
					String zxygzdnsz = sjContent.substring(38, 48);
					zxygzdnsz = Util.tranFormat14(zxygzdnsz);

					// 事件摘要
					sjzy = "起/止标志:" + qzbz + ";" + "  Ua/Uab=" + Ua + "(度);"
							+ "  Ub=" + Ub + "(度);" + "  Uc/Ucb=" + Uc
							+ "(度);" + "  Ia=" + Ia + "(度);" + "  Ib=" + Ib
							+ "(度);" + "  Ib=" + Ib + "(度);"
							+ "  正向有功总电能示值=" + zxygzdnsz + ";";

				} else if (sjdm.equals("12")) {
					// ERC12:电能表时间超差

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 起/止标志
					String qzbz = ss[1];

					// 事件摘要
					sjzy = "起/止标志:" + qzbz + ";";

				} else if (sjdm.equals("13")) {
					// ERC13:终端漏电流超限事件记录
					String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
					s_sql="select rjbbh from g_zdgz where zdid=?";
	        		params = new String[] { zdid };
	        	    List cldList = jdbcT.queryForList(s_sql, params);
	        	    Map cldMap = (Map) cldList.get(0);
	        	    // 本地软件版本号
	        	 	String d_rjbbh = String.valueOf(cldMap.get("rjbbh"));

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";
                    
					//超限漏电流
					String cxldl=sjContent.substring(10, 14);
					//2016-10-29对v2.0.8的终端单独解析
					if(null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.8")||null!=d_rjbbh&&d_rjbbh.equalsIgnoreCase("v2.0.9")){
						cxldl = Util.tranFormat05(cxldl);
					}else{
						cxldl = Util.tranFormat08(cxldl);
					}
//					String cxldl=sjContent.substring(10, 14);
//					cxldl=Util.tranFormat08(cxldl);
					
					// 事件摘要
					sjzy = "超限漏电流值:" + cxldl ;

				} else if (sjdm.equals("14")) {
					// ERC14:终端停电事件

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 停电发生时间
					String tdsj = Util.tranFormat15(sjContent.substring(0, 10));
					
					// 事件摘要
					sjzy = "停电发生时间:" + tdsj ;

				} else if (sjdm.equals("15")) {
					// ERC15:就地控制上报
					// 发生时间
					sjfssj = sjContent.substring(2, 12);
					sjfssj = Util.convertStr(sjfssj);

					// 设备类型
      				String sblx = sjContent.substring(0,2);
      				
      				sjzy="现场站点处于就地控制模式";
					

				} else if (sjdm.equals("16")) {
					//ERC16：故障灯报警上报

					// 发生时间
					sjfssj = sjContent.substring(4, 14);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
      				String cldh = sjContent.substring(0,4);
      				cldh = Util.hexStrToDecStr(Util.convertStr(cldh));
      				xxdh = String.valueOf(Integer.parseInt(cldh));

					sjzy="电机异常事件记录:电机故障,所属测量点："+cldh;
					
					//更新电机的状态
					String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
  	      	    	s_sql="update g_zddqsbpzb set zt=? where zdid=? and cldh=?";
  	        		params = new String[] { "2",zdid,cldh};
  	        		jdbcT.update(s_sql, params);
					

				} else if (sjdm.equals("17")) {
					// ERC17：终端上电事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 停电发生时间
					String tdsj = Util.tranFormat15(sjContent.substring(0, 10));
					
					// 事件摘要
					sjzy = "上电发生时间:" + tdsj ;

				} else if (sjdm.equals("18")) {
					// ERC18:电容器投切自锁记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 起/止标志
					String qzbz = ss[1];

					// 异常标志
					String ycbz = sjContent.substring(14, 16);
					ycbz = Util.hexStrToBinStr(ycbz, 1);
					ycbz = ycbz.substring(5, 8);

					if (ycbz.equals("001")) {
						ycbz = "过压";
					} else if (ycbz.equals("010")) {
						ycbz = "装置故障";
					} else if (ycbz.equals("100")) {
						ycbz = "执行回路故障";
					}

					// 电容器组标志
					String temp_drqzbz = sjContent.substring(16, 20);
					temp_drqzbz = Util.convertStr(temp_drqzbz);
					temp_drqzbz = Util.hexStrToBinStr(temp_drqzbz, 2);
					temp_drqzbz = temp_drqzbz.substring(7, 16);// 9-1
					String drqzbz = "";
					for (int j = 1; j <= 9; j++) {
						String sfzs = drqzbz.substring(9 - j, 9 - (j - 1));
						sfzs = sfzs.equals("1") ? "自锁" : "未自锁";
						drqzbz = drqzbz + "电容器组" + j + ":" + sfzs + ";";
					}

					// 越限发生时功率因数
					String glys = sjContent.substring(20, 24);
					glys = Util.tranFormat05(glys);

					// 越限发生时无功功率
					String wggl = sjContent.substring(24, 28);
					wggl = Util.tranFormat23(wggl);

					// 越限发生时电压
					String dy = sjContent.substring(28, 32);
					dy = Util.tranFormat07(dy);

					// 事件摘要
					sjzy = "起/止标志:" + qzbz + ";" + "  异常标志:" + ycbz + ";"
							+ "  " + drqzbz + "  越限发生时功率因数:" + glys + ";"
							+ "  越限发生时无功功率:" + wggl + ";" + "  越限发生时电压:"
							+ dy + ";";

				} else if (sjdm.equals("19")) {
					// ERC19:购电参数设置记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 购电单号
					String gddh = sjContent.substring(10, 18);
					gddh = Util.convertStr(gddh);
					gddh = String.valueOf(Integer.parseInt(gddh, 16));

					// 追加/刷新标志
					String zjsxbz = sjContent.substring(18, 20);
					if (zjsxbz.equals("55")) {
						zjsxbz = "追加";
					} else if (zjsxbz.equals("AA")) {
						zjsxbz = "刷新";
					}

					// 购电量值
					String gdlz = sjContent.substring(20, 28);
					String[] ss_gdlz = Util.tranFormat03(gdlz);
					gdlz = ss_gdlz[0] + ss_gdlz[1];

					// 报警门限
					String bjmx = sjContent.substring(28, 36);
					String[] ss_bjmx = Util.tranFormat03(bjmx);
					bjmx = ss_bjmx[0] + ss_bjmx[1];

					// 跳闸门限
					String tzmx = sjContent.substring(36, 44);
					String[] ss_tzmx = Util.tranFormat03(tzmx);
					tzmx = ss_tzmx[0] + ss_tzmx[1];

					// 本次购电前剩余电能量(费)
					String gdq = sjContent.substring(44, 52);
					String[] ss_gdq = Util.tranFormat03(gdq);
					gdq = ss_gdq[0] + ss_gdq[1];

					// 本次购电后剩余电能量(费)
					String gdh = sjContent.substring(52, 60);
					String[] ss_gdh = Util.tranFormat03(gdh);
					gdh = ss_gdh[0] + ss_gdh[1];

					// 事件摘要
					sjzy = "购电单号:" + gddh + ";" + "  追加/刷新标志:" + zjsxbz + ";"
							+ "  购电量值:" + gdlz + ";" + "  报警门限:" + bjmx
							+ ";" + "  跳闸门限:" + tzmx + ";"
							+ "  本次购电前剩余电能量(费):" + gdq + ";"
							+ "  本次购电后剩余电能量(费):" + gdh + ";";

				} else if (sjdm.equals("20")) {
					// ERC20:密码错误记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 错误密码
					String cwmm = sjContent.substring(10, 14);
					cwmm = Util.convertStr(cwmm);
					cwmm = String.valueOf(Integer.parseInt(cwmm, 16));

					// 启动站地址
					String qdzdz = sjContent.substring(14, 16);
					qdzdz = String.valueOf(Integer.parseInt(qdzdz, 16));

					// 事件摘要
					sjzy = "错误密码:" + cwmm + ";" + "  启动站地址:" + qdzdz + ";";

				} else if (sjdm.equals("21")) {
					// ERC21:终端故障记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 终端故障类型
					String gzlx = sjContent.substring(10, 12);
					int i_gzlx = Integer.parseInt(gzlx, 16);

					if (i_gzlx == 1) {
						gzlx = "终端主板内存故障";
					} else if (i_gzlx == 2) {
						gzlx = "时钟故障";
					} else if (i_gzlx == 3) {
						gzlx = "主板通信故障";
					} else if (i_gzlx == 4) {
						gzlx = "485抄表故障";
						// 插入或更新终端当前状态记录表
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
						gzlx = "显示板故障";
					} else if (i_gzlx == 7) {
						gzlx = "485抄表正常";
						// 插入或更新终端当前状态记录表
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

					// 事件摘要
					sjzy = "终端故障类型:" + gzlx + ";";

				} else if (sjdm.equals("22")) {
					// ERC22:有功总电能量差动越限事件记录

					int indx = 0;
					// 发生时间
					sjfssj = sjContent.substring(indx, indx + 10);
					indx += 10;
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（总加组）
					xxdlb = "2";
					// 信息点号
					xxdh = "1";

					// 总加组号
					String temps = sjContent.substring(indx, indx + 2);
					indx += 2;
					temps = Util.hexStrToBinStr(temps, 1);
					int i_zjzh = Integer.parseInt(temps.substring(2, 8), 2);
					xxdh = String.valueOf(i_zjzh);

					// 起/止标志
					String qzbz = temps.substring(0, 1);
					if (qzbz.equals("1")) {
						qzbz = "发生";
					} else if (qzbz.equals("0")) {
						qzbz = "恢复";
					}

					// 越限时对比总加组有功总电能量
					String dbzjzygz = sjContent.substring(indx, indx + 8);
					indx += 8;
					String[] ss_dbzjzygz = Util.tranFormat03(dbzjzygz);
					dbzjzygz = ss_dbzjzygz[0] + ss_dbzjzygz[1];

					// 越限时参照总加组有功总电能量
					String czzjzygz = sjContent.substring(indx, indx + 8);
					indx += 8;
					String[] ss_czzjzygz = Util.tranFormat03(czzjzygz);
					czzjzygz = ss_czzjzygz[0] + ss_czzjzygz[1];

					// 越限时差动越限相对偏差值
					String xdpcz = sjContent.substring(indx, indx + 2);
					indx += 2;
					xdpcz = String.valueOf(Integer.parseInt(xdpcz, 16));

					// 越限时差动越限绝对偏差值
					String jdpcz = sjContent.substring(indx, indx + 8);
					indx += 8;
					String[] ss_jdpcz = Util.tranFormat03(jdpcz);
					jdpcz = ss_jdpcz[0] + ss_jdpcz[1];

					// 对比总加组测量点数量
					String sDbnum = sjContent.substring(indx, indx + 2);
					indx += 2;
					int iDbnum = Integer.parseInt(sDbnum, 16);

					String db_yczy = "";
					for (int j = 1; j <= iDbnum; j++) {
						String ygzdnsz = sjContent.substring(indx, indx + 10);
						indx += 10;
						ygzdnsz = Util.tranFormat14(ygzdnsz);

						db_yczy = db_yczy + "越限时对比总加组第" + j + "个测量点有功总电能示值:"
								+ ygzdnsz + ";";

					}

					// 参照总加组测量点数量
					String sCznum = sjContent.substring(indx, indx + 2);
					indx += 2;
					int iCznum = Integer.parseInt(sCznum, 16);

					String cz_yczy = "";
					for (int j = 1; j <= iCznum; j++) {
						String ygzdnsz = sjContent.substring(indx, indx + 10);
						indx += 10;
						ygzdnsz = Util.tranFormat14(ygzdnsz);

						cz_yczy = cz_yczy + "越限时参照总加组第" + j + "个测量点有功总电能示值:"
								+ ygzdnsz + ";";

					}

					// 事件摘要
					sjzy = "起/止标志:" + qzbz + ";" + "  越限时对比总加组有功总电能量:"
							+ dbzjzygz + ";" + "  越限时参照总加组有功总电能量:" + czzjzygz
							+ ";" + "  越限时差动越限相对偏差值:" + xdpcz + ";"
							+ "  越限时差动越限绝对偏差值:" + jdpcz + ";" + "  "
							+ db_yczy + "  " + cz_yczy;

				} else if (sjdm.equals("24")) {
					// ERC24:电压越限记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 起/止标志
					String qzbz = ss[1];

					// 越限标志
					String temp_yxbz = sjContent.substring(14, 16);
					temp_yxbz = Util.hexStrToBinStr(temp_yxbz, 1);

					// 越限类型
					String yxlx = temp_yxbz.substring(0, 2);
					if (yxlx.equals("01")) {
						yxlx = "越上限";
					} else if (yxlx.equals("10")) {
						yxlx = "越下限";
					} else {
						yxlx = "备用";
					}

					// 相位
					String temp_xw = temp_yxbz.substring(5, 8);// 相位
					String xw = "";

					if ((temp_xw.substring(2, 3)).equals("1")) {
						xw = xw + "A相、";
					}
					if ((temp_xw.substring(1, 2)).equals("1")) {
						xw = xw + "B相、";
					}
					if ((temp_xw.substring(0, 1)).equals("1")) {
						xw = xw + "C相、";
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

					// 事件摘要
					sjzy = "起/止标志:" + qzbz + ";" + "  越限类型:" + yxlx + ";"
							+ "  越限相位:" + xw + ";" + "  Ua/Uab=" + Ua + ";"
							+ "  Ub=" + Ub + ";" + "  Uc/Ucb=" + Uc + ";";

				} else if (sjdm.equals("25")) {
					// ERC25:电流越限记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 起/止标志
					String qzbz = ss[1];

					// 越限标志
					String temp_yxbz = sjContent.substring(14, 16);
					temp_yxbz = Util.hexStrToBinStr(temp_yxbz, 1);

					// 越限类型
					String yxlx = temp_yxbz.substring(0, 2);
					if (yxlx.equals("01")) {
						yxlx = "越上限";
					} else if (yxlx.equals("10")) {
						yxlx = "越下限";
					} else {
						yxlx = "备用";
					}

					// 相位
					String temp_xw = temp_yxbz.substring(5, 8);// 相位
					String xw = "";

					if ((temp_xw.substring(2, 3)).equals("1")) {
						xw = xw + "A相、";
					}
					if ((temp_xw.substring(1, 2)).equals("1")) {
						xw = xw + "B相、";
					}
					if ((temp_xw.substring(0, 1)).equals("1")) {
						xw = xw + "C相、";
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

					// 事件摘要
					sjzy = "起/止标志:" + qzbz + ";" + "  越限类型:" + yxlx + ";"
							+ "  越限相位:" + xw + ";" + "  Ia/Iab=" + Ia + ";"
							+ "  Ib=" + Ib + ";" + "  Ic/Icb=" + Ic + ";";

				} else if (sjdm.equals("26")) {
					// ERC26:视在功率越限记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 起/止标志
					String qzbz = ss[1];

					// 越限标志
					String temp_yxbz = sjContent.substring(14, 16);
					temp_yxbz = Util.hexStrToBinStr(temp_yxbz, 1);

					// 越限类型
					String yxlx = temp_yxbz.substring(0, 2);
					if (yxlx.equals("01")) {
						yxlx = "越上限";
					} else if (yxlx.equals("10")) {
						yxlx = "越下限";
					} else {
						yxlx = "备用";
					}

					// 发生时的视在功率
					String szgl = sjContent.substring(16, 20);
					szgl = Util.tranFormat23(szgl);

					// 发生时的视在功率限值
					String szglxz = sjContent.substring(20, 24);
					szglxz = Util.tranFormat23(szglxz);

					// 事件摘要
					sjzy = "起/止标志:" + qzbz + ";" + "  越限类型:" + yxlx + ";"
							+ "  发生时的视在功率:" + szgl + ";" + "  发生时的视在功率限值:"
							+ szglxz + ";";

				} else if (sjdm.equals("27")) {
					// ERC27:电能表示度下降记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 下降前电能表正向有功总电能示值
					String xjq = sjContent.substring(14, 24);
					xjq = Util.tranFormat14(xjq);

					// 下降后电能表正向有功总电能示值
					String xjh = sjContent.substring(24, 34);
					xjh = Util.tranFormat14(xjh);

					// 事件摘要
					sjzy = "下降前电能表正向有功总电能示值:" + xjq + ";"
							+ "  下降后电能表正向有功总电能示值:" + xjh + ";";

				} else if (sjdm.equals("28")) {
					// ERC28:电能量超差记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 超差前正向有功总电能示值
					String xjq = sjContent.substring(14, 24);
					xjq = Util.tranFormat14(xjq);

					// 超差后正向有功总电能示值
					String xjh = sjContent.substring(24, 34);
					xjh = Util.tranFormat14(xjh);

					// 电能量超差阀值
					String ccfz = sjContent.substring(34, 36);
					ccfz = String.valueOf(Integer.parseInt(ccfz, 16));

					// 事件摘要
					sjzy = "超差前正向有功总电能示值:" + xjq + ";" + "  超差后正向有功总电能示值:"
							+ xjh + ";" + "  电能量超差阀值:" + ccfz + ";";

				} else if (sjdm.equals("29")) {
					// ERC29:电能量飞走记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 飞走前正向有功总电能示值
					String fzq = sjContent.substring(14, 24);
					fzq = Util.tranFormat14(fzq);

					// 飞走后正向有功总电能示值
					String fzh = sjContent.substring(24, 34);
					fzh = Util.tranFormat14(fzh);

					// 电能表飞走阀值
					String fzfz = sjContent.substring(34, 36);
					fzfz = String.valueOf(Integer.parseInt(fzfz, 16));

					// 事件摘要
					sjzy = "飞走前正向有功总电能示值:" + fzq + ";" + "  飞走后正向有功总电能示值:"
							+ fzh + ";" + "  电能表飞走阀值:" + fzfz + ";";

				} else if (sjdm.equals("30")) {
					// ERC30:电能表停走记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[0];

					// 停走时正向有功总电能示值
					String tz = sjContent.substring(14, 24);
					tz = Util.tranFormat14(tz);

					// 电能表停走阀值
					String tzfz = sjContent.substring(24, 26);
					tzfz = String.valueOf(Integer.parseInt(tzfz, 16));

					// 事件摘要
					sjzy = "停走时正向有功总电能示值:" + tz + ";" + "  电能表停走阀值:" + tzfz
							+ ";";

				} else if (sjdm.equals("31")) {
					// ERC31:485抄表失败事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[1];

					// 最近一次抄表成功正向有功总电能示值
					String cs1 = sjContent.substring(14, 24);
					cs1 = Util.tranFormat14(cs1);

					// 最近一次抄表成功正向无功总电能示值
					String cs2 = sjContent.substring(24, 32);
					cs2 = Util.tranFormat11(cs2);

					// 事件摘要
					sjzy = "起/止标志:" + ss[0] + ";" + "  最近一次抄表成功正向有功总电能示值:"
							+ cs1 + ";" + "  最近一次抄表成功正向无功总电能示值:" + cs2 + ";";

				} else if (sjdm.equals("32")) {
					// ERC32:终端与主站通信流量超门限事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 当月已发生的通信流量
					String cs1 = sjContent.substring(10, 18);
					try {
						cs1 = Util.hexStrToDecStr(Util.convertStr(cs1));
					} catch (Exception e) {
						cs1 = "无效";
					}
					// 月通信流量门限
					String cs2 = sjContent.substring(18, 26);
					try {
						cs2 = Util.hexStrToDecStr(Util.convertStr(cs2));
					} catch (Exception e) {
						cs2 = "无效";
					}

					// 事件摘要
					sjzy = "当月已发生的通信流量:" + cs1 + ";" + "  月通信流量门限:" + cs2
							+ ";";

				} else if (sjdm.equals("33")) {
					// ERC33:电能表运行状态字变位事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[1];

					// 电表运行状态字变位标志1
					String cs1 = sjContent.substring(14, 18);
					cs1 = Util.convertStr(cs1);
					cs1 = Util.hexStrToBinStr(cs1, 2);

					// 电表运行状态字变位标志2
					String cs2 = sjContent.substring(18, 22);
					cs2 = Util.convertStr(cs2);
					cs2 = Util.hexStrToBinStr(cs2, 2);

					// 电表运行状态字变位标志3
					String cs3 = sjContent.substring(22, 26);
					cs3 = Util.convertStr(cs3);
					cs3 = Util.hexStrToBinStr(cs3, 2);

					// 电表运行状态字变位标志4
					String cs4 = sjContent.substring(26, 30);
					cs4 = Util.convertStr(cs4);
					cs4 = Util.hexStrToBinStr(cs4, 2);

					// 电表运行状态字变位标志5
					String cs5 = sjContent.substring(30, 34);
					cs5 = Util.convertStr(cs5);
					cs5 = Util.hexStrToBinStr(cs5, 2);

					// 电表运行状态字变位标志6
					String cs6 = sjContent.substring(34, 38);
					cs6 = Util.convertStr(cs6);
					cs6 = Util.hexStrToBinStr(cs6, 2);

					// 电表运行状态字变位标志7
					String cs7 = sjContent.substring(38, 42);
					cs7 = Util.convertStr(cs7);
					cs7 = Util.hexStrToBinStr(cs7, 2);

					// 电表运行状态字1
					String cs8 = sjContent.substring(42, 46);
					cs8 = Util.convertStr(cs8);
					cs8 = Util.hexStrToBinStr(cs8, 2);

					// 电表运行状态字2
					String cs9 = sjContent.substring(42, 46);
					cs9 = Util.convertStr(cs9);
					cs9 = Util.hexStrToBinStr(cs9, 2);

					// 电表运行状态字3
					String cs10 = sjContent.substring(46, 50);
					cs10 = Util.convertStr(cs10);
					cs10 = Util.hexStrToBinStr(cs10, 2);

					// 电表运行状态字4
					String cs11 = sjContent.substring(50, 54);
					cs11 = Util.convertStr(cs11);
					cs11 = Util.hexStrToBinStr(cs11, 2);

					// 电表运行状态字5
					String cs12 = sjContent.substring(54, 58);
					cs12 = Util.convertStr(cs12);
					cs12 = Util.hexStrToBinStr(cs12, 2);

					// 电表运行状态字6
					String cs13 = sjContent.substring(58, 62);
					cs13 = Util.convertStr(cs13);
					cs13 = Util.hexStrToBinStr(cs13, 2);

					// 电表运行状态字7
					String cs14 = sjContent.substring(62, 66);
					cs14 = Util.convertStr(cs14);
					cs14 = Util.hexStrToBinStr(cs14, 2);

					// 事件摘要
					sjzy = "电表运行状态字变位标志1:" + cs1 + ";" + "  电表运行状态字变位标志2:"
							+ cs2 + ";" + "  电表运行状态字变位标志3:" + cs3 + ";"
							+ "  电表运行状态字变位标志4:" + cs4 + ";"
							+ "  电表运行状态字变位标志5:" + cs5 + ";"
							+ "  电表运行状态字变位标志6:" + cs6 + ";"
							+ "  电表运行状态字变位标志7:" + cs7 + ";" + "  电表运行状态字1:"
							+ cs8 + ";" + "  电表运行状态字2:" + cs9 + ";"
							+ "  电表运行状态字3:" + cs10 + ";" + "  电表运行状态字4:"
							+ cs11 + ";" + "  电表运行状态字5:" + cs12 + ";"
							+ "  电表运行状态字6:" + cs13 + ";" + "  电表运行状态字7:"
							+ cs14 + ";";

				} else if (sjdm.equals("34")) {
					// ERC34:CT异常事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 14);
					String[] ss = tranCld(temps);
					xxdh = ss[1];

					// 异常标志
					String cs1 = sjContent.substring(14, 16);
					cs1 = Util.hexStrToBinStr(cs1, 1);

					// 事件摘要
					sjzy = "起/止标志:" + ss[0] + ";" + "  异常标志:" + cs1 + ";";

				} else if (sjdm.equals("35")) {
					// ERC35:发现未知电表事件记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（测量点）
					xxdlb = "1";
					// 信息点号
					xxdh = "1";

					// 测量点号
					String temps = sjContent.substring(10, 12);
					temps = Util.hexStrToBinStr(temps, 1);
					xxdh = Util.binStrToDecStr(temps.substring(2));

					// 发现块数
					String sNum = sjContent.substring(12, 14);
					sNum = Util.hexStrToDecStr(sNum);
					int iNum = Integer.parseInt(sNum);

					// 事件摘要
					sjzy = "发现块数:" + sNum + ";";
					int idx = 14;
					for (int m = 1; m <= iNum; m++) {
						// 第m块表
						// 通信地址
						String cs1 = sjContent.substring(idx, idx + 12);
						idx += 12;
						cs1 = Util.tranFormat12(cs1);
						sjzy += "  第" + m + "块未知电表通信地址:" + cs1 + ";";

						// 所在相别及发现者接收到的信号品质
						String cs2 = sjContent.substring(idx, idx + 2);
						idx += 2;
						cs2 = Util.hexStrToBinStr(cs2, 1);
						sjzy += "  第" + m + "块未知电表所在相别及发现者接收到的信号品质:" + cs2
								+ ";";

						// 通信协议
						String cs3 = sjContent.substring(idx, idx + 2);
						idx += 2;
						cs3 = Util.hexStrToBinStr(cs3, 1);
						cs3 = cs3.substring(6, 8);
						if (cs3.equals("00")) {
							cs3 = "DL/T645-1997";
						} else if (cs3.equals("01")) {
							cs3 = "DL/T645-2007";
						} else {
							cs3 = "备用协议";
						}
						sjzy += "  第" + m + "块未知电表通信协议:" + cs3 + ";";

					}

				} else if (sjdm.equals("50")) {
					// ERC50:接触器开合记录

					// 发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（总加组）
					xxdlb = "2";
					// 信息点号
					xxdh = "1";

					// 总加组号
					String zjzh = sjContent.substring(10, 12);
					zjzh = Util.hexStrToBinStr(zjzh, 1);
					int i_zjzh = Integer.parseInt(zjzh.substring(2, 8), 2);
					xxdh = String.valueOf(i_zjzh);

					// 接触器开合状态
					String jcqkhzt = sjContent.substring(12, 14);
					// if(!"EE".equalsIgnoreCase(jcqkhzt)){
					// if("55".equalsIgnoreCase(jcqkhzt)){
					// jcqkhzt="跳闸";
					// }else{
					// jcqkhzt="合闸";
					// }
					// }else{
					// jcqkhzt="无效";
					// }

					// 功控类别
					String gklb = sjContent.substring(14, 16);
					gklb = Util.hexStrToBinStr(gklb, 1);
					gklb = gklb.substring(4, 8);// 当前功率下浮控、营业报停控、厂休控、时段控
					if (gklb.equals("1000")) {
						gklb = "当前功率下浮控";
					} else if (gklb.equals("0100")) {
						gklb = "营业报停控";
					} else if (gklb.equals("0010")) {
						gklb = "厂休控";
					} else if (gklb.equals("0001")) {
						gklb = "时段控";
					}

					// 跳闸前功率(总加功率)
					String tzqgl = sjContent.substring(16, 20);
					String s_tzqgl = Util.tranFormat02(tzqgl);

					// 跳闸后2分钟的功率(总加功率)
					String tzhgl = sjContent.substring(20, 24);
					String s_tzhgl = Util.tranFormat02(tzhgl);

					// 跳闸时功率定值
					String tzsgldz = sjContent.substring(24, 28);
					String s_tzsgldz = Util.tranFormat02(tzsgldz);
					// 接触器开合状态
					String tem_jcqkhzt = jcqkhzt;
					if (!"EE".equalsIgnoreCase(jcqkhzt)) {

						if ("55".equalsIgnoreCase(jcqkhzt)) {
							jcqkhzt = "跳闸";
						} else {
							jcqkhzt = "合闸";
						}
					} else {
						jcqkhzt = "无效";
					}

					// 插入或更新终端当前状态记录表
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

					// 事件摘要
					sjzy = "接触器开合状态:" + jcqkhzt + ";  功控类别:" + gklb
							+ ";  开合前功率(总加功率):" + s_tzqgl
							+ ";  开合后2分钟的功率(总加功率):" + s_tzhgl
							+ ";  开合时功率定值:" + s_tzsgldz;

				} else if (sjdm.equals("51")) {
					// ERC51:负荷超限告警

					// 告警发生时间
					sjfssj = sjContent.substring(0, 10);
					sjfssj = Util.convertStr(sjfssj);

					// 信息点类别（终端）
					xxdlb = "0";
					// 信息点号
					xxdh = "0";

					// 告警级别
					String gjjb = sjContent.substring(10, 12);
					// 瞬时有功功率
					String ssyggl = Util.tranFormat02(sjContent.substring(12,
							16));
					String hcsj = sjContent.substring(16, 18);
					hcsj = String.valueOf(Integer.parseInt(hcsj, 16));
					String gldz = Util
							.tranFormat02(sjContent.substring(18, 22));

					// 事件摘要
					sjzy = "负荷超限告警:" + "  当前瞬时有功功率：" + ssyggl + ";"
							+ "  跳闸执行剩余时间：" + Double.parseDouble(hcsj) / 2
							+ "  当前功率定值：" + gldz;
					//如果是主动上报事件,则向用户发送短信
					if (sjsxlx.equals("2")) {
						Dispatch dispatch = (Dispatch) Context.ctx
								.getBean("dispatchService");
						// 取得用户名称和用户手机号码
						String[] hm_yhsjhm = Util.getHmAndYhsjhm(xzqxm, zddz,
								jdbcT);
						// 短信内容
						String content = "";
						String content1="";
						if ("01".equalsIgnoreCase(gjjb)) {
							content = "尊敬的用户" + hm_yhsjhm[0] + ":您当前用电负荷("
									+ ssyggl + "kW)持续"
									+ Double.parseDouble(hcsj) / 2 + "分钟超过规定值"
									+ gldz + "kW,请立即降低负荷,将在"
									+ Double.parseDouble(hcsj) / 2 + "分钟后执行跳闸!";
							content1 = "用户["+hm_yhsjhm[0]+"]当前用电负荷("
							+ ssyggl + "kW)持续"
							+ Double.parseDouble(hcsj) / 2 + "分钟超过规定值"
							+ gldz + "kW,将在"
							+ Double.parseDouble(hcsj) / 2 + "分钟后执行跳闸!";
						} else {
							content = "尊敬的用户" + hm_yhsjhm[0]
									+ ":您未在规定时间内降低负荷(当前" + ssyggl
									+ "kW),本时段的功率定值" + gldz + "kW,将执行跳闸操作!";
							content1 = "用户["+hm_yhsjhm[0]+"]未在规定时间内降低负荷(当前" + ssyggl
															+ "kW),本时段的功率定值" + gldz + "kW,将执行跳闸操作!";
						}
						// 发送短信
						dispatch.sedAscendSms(hm_yhsjhm[1], content, true);
						dispatch.sedAscendSms(hm_yhsjhm[3], content1, true);
					}
				}

				if (sjsxlx.equals("1")) {
					// 事件查询返回(写"召测数据返回表")
					String temp_sjzy = "";
					String temp_xxdlb = "";

					if (xxdlb.equals("1")) {
						temp_xxdlb = "测量点";
					} else if (xxdlb.equals("2")) {
						temp_xxdlb = "总加组";
					} else if (xxdlb.equals("3")) {
						temp_xxdlb = "直流模拟量";
					}

					if (!xxdlb.equals("0")) {
						temp_sjzy = temp_xxdlb + xxdh + ";";
					}

//					temp_sjzy = temp_sjzy + "事件发生时间:" + Util.getSJSJ(sjfssj)
//							+ "; " + sjzy;
					//2017-06-21取消发生时间
					temp_sjzy = temp_sjzy + "; " + sjzy;
					xh++;
					s_sql = "insert into g_zcsjfhb(SJZFSSEQ,afn,pnfn,sjxdm,sjz,xxdmc,xh,sjsj) "
							+ "values(?,?,?,?,?,?,?,sysdate)";
					params = new String[] { s_sjzfsseq, "0E", pnfn,
							"ERC" + sjdm, temp_sjzy, "终端", String.valueOf(xh) };
					jdbcT.update(s_sql, params);
					cat.info("[exceptionDecode]s_sql:" + s_sql);
				} else if (sjsxlx.equals("2")) {
					// 事件主动上报
					String temp_sjzy = "";
					// 1、写"终端事件记录表"
					// 事件序列
					int i_sjid = Integer.parseInt(Util.getSeqException(jdbcT));
					String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
//					temp_sjzy = temp_sjzy + "事件发生时间:" + Util.getSJSJ(sjfssj)
//							+ ";  " + sjzy;
					//2017-06-21取消发生时间
					temp_sjzy = temp_sjzy + "; " + sjzy;
					s_sql = "insert into G_ZDSJJLB(sjid,zdid,xxdlb,xxdh,"
							+ "sjdm,sjzy,fssj,jssj) "
							+ "values(SEQ_EXCEPTION.NEXTVAL,?,?,?,?,?,"
							+ "to_date(?,'yymmddhh24mi'),sysdate)";
					params = new String[] { zdid, xxdlb, xxdh, "ERC" + sjdm,
							temp_sjzy, sjfssj };
					jdbcT.update(s_sql, params);
					cat.info("[exceptionDecode]s_sql:" + s_sql);

					// 2、update终端当前状态表
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