package com.powerhigh.gdfas.module.cmsms;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.module.PoolWorker;
import com.powerhigh.gdfas.util.DataObject;
import com.powerhigh.gdfas.util.CMDb;
import com.powerhigh.gdfas.util.Util;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.parse.receiveDispose;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

public class CmsmsWorker
 extends PoolWorker
{

 public CmsmsWorker()
 {
 }



 public void run(Object data)
 {
		String s_sql = "";
		Connection con = null;
		String sjzfsseq = "";// 设置seq
		String R_SMS = "";// 从短信模块收到的短信

		DataObject receive = (DataObject) data;
		sjzfsseq = receive.sjzfsseq;
		R_SMS = receive.sjz;
		
		DataSource dataSource = (DataSource)Context.ctx.getBean("dataSource");
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		
		System.out.println("[CmsmsWorker]Message=" + R_SMS);
		if (R_SMS.equals("outTime")) {
			// 超时(07)
			s_sql = "update sjzfsb set zt='07' " + "where sjzfsseq='"
					+ sjzfsseq + "'";
//			jdbcT.update(s_sql);

		} else {
			// 去掉AABB...DDEE前的多余字节
			int idx = R_SMS.indexOf("AABB");
			if (idx < 0) {
				return;
			}
			R_SMS = R_SMS.substring(idx);

			String R_order = R_SMS.substring(14, 18);// 返回命令字
			try {
				R_order = Util.convertStr(R_order);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (R_order.equals("2002")) {
				// 1、发送短信的状态返回
				String szjg = R_SMS.substring(22, 24);// 设置结果
				if (szjg.equals("88")) {
					// 发送成功(05)
					szjg = "05";
				} else {
					// 发送失败(06)
					szjg = "06";
				}
				s_sql = "update sjzfsb set zt='" + szjg + "' "
						+ "where sjzfsseq='" + sjzfsseq + "'";
//				jdbcT.update(s_sql);
			} else if (R_order.equals("2001")) {
				// 2、读取短信的返回
				String R_LEN = R_SMS.substring(18, 22);// 返回短信的长度
				if (!R_LEN.equals("0000")) {
					String temp_sjz = R_SMS.substring(36, R_SMS.length() - 6);// (36=起始->终端SIM号码)
					//去掉短信头
					temp_sjz = temp_sjz.substring(4);
					
					receive.sjz = temp_sjz;
					// CMSMS
					receive.moduleID = CMConfig.CMSMS_MODULE_ID;

					this.moudle.process(receive);
				}
			}
		}
 	
     
     
 }

 
}
