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
		String sjzfsseq = "";// ����seq
		String R_SMS = "";// �Ӷ���ģ���յ��Ķ���

		DataObject receive = (DataObject) data;
		sjzfsseq = receive.sjzfsseq;
		R_SMS = receive.sjz;
		
		DataSource dataSource = (DataSource)Context.ctx.getBean("dataSource");
		JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
		
		System.out.println("[CmsmsWorker]Message=" + R_SMS);
		if (R_SMS.equals("outTime")) {
			// ��ʱ(07)
			s_sql = "update sjzfsb set zt='07' " + "where sjzfsseq='"
					+ sjzfsseq + "'";
//			jdbcT.update(s_sql);

		} else {
			// ȥ��AABB...DDEEǰ�Ķ����ֽ�
			int idx = R_SMS.indexOf("AABB");
			if (idx < 0) {
				return;
			}
			R_SMS = R_SMS.substring(idx);

			String R_order = R_SMS.substring(14, 18);// ����������
			try {
				R_order = Util.convertStr(R_order);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (R_order.equals("2002")) {
				// 1�����Ͷ��ŵ�״̬����
				String szjg = R_SMS.substring(22, 24);// ���ý��
				if (szjg.equals("88")) {
					// ���ͳɹ�(05)
					szjg = "05";
				} else {
					// ����ʧ��(06)
					szjg = "06";
				}
				s_sql = "update sjzfsb set zt='" + szjg + "' "
						+ "where sjzfsseq='" + sjzfsseq + "'";
//				jdbcT.update(s_sql);
			} else if (R_order.equals("2001")) {
				// 2����ȡ���ŵķ���
				String R_LEN = R_SMS.substring(18, 22);// ���ض��ŵĳ���
				if (!R_LEN.equals("0000")) {
					String temp_sjz = R_SMS.substring(36, R_SMS.length() - 6);// (36=��ʼ->�ն�SIM����)
					//ȥ������ͷ
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
