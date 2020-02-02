package com.powerhigh.gdfas.parse;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.module.front.communicateWithFront;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.CMXmlR;





/** 
 * Description: ����汾Զ������<p>
 * Copyright:    Copyright   2015 LongShine<p>
 * ��дʱ��: 2015-9-14
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */
 
public class Decode_0F_ReSend implements Runnable{	
	private String dqxh;//��ǰ���
	private JdbcTemplate jdbcT;
	private String xzqxm;
	private String zddz;
	
	public Decode_0F_ReSend(String s_xzqxm, String s_zddz,JdbcTemplate jdbcT,String dqxh)
	 {
	     this.dqxh = dqxh;
	     this.xzqxm=s_xzqxm;
	     this.zddz=s_zddz;
	     this.jdbcT=jdbcT;
	 }
	
	public static String overtime = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_OVERTIME);//��ʱʱ��
	public static String send_delay = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_SEND_DELAY);//��֡�ķ�����ʱ
	public static String resend_count = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_RESEND_COUNT);//�ط�����
	@SuppressWarnings("rawtypes")
	@Override
	public void run() {
		
		
		//�����ط����� 
//		System.out.println("�����ط�����,��ǰ���>>>>>>>>>>"+dqxh);
		//�ط��ĵȴ�ʱ�䣬���ȴ���һ֡�ĳ�ʱʱ��
		Long ddsj=Long.parseLong(overtime);
		//�ط��ļ��ʱ�䣬���ȴ���һ֡�ĳ�ʱʱ��
		Long jgsj=Long.parseLong(send_delay);
		try {
			Thread.sleep(new Long(ddsj));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//�ط�����
		int i_count=Integer.parseInt(resend_count);
		//��ʼ�ط�
		for(int i=0;i<i_count;i++){
			
			
			
			String s_sql = "select *  from g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
			String[] params = new String[] { xzqxm, zddz};
			List lst = jdbcT.queryForList(s_sql, params);
			
			
			// ��ǰ���
			String i_dqxh = String.valueOf(((Map) lst.get(0)).get("dqdh"));
			
			if(i_dqxh.equalsIgnoreCase(dqxh)){
				//2017-06-23У�鵱ǰ����ţ����������֡����һ֡
				int ii_dqxh=Integer.parseInt(i_dqxh);
				if(ii_dqxh%2==1){
					ii_dqxh=ii_dqxh-1;
					
					// �����ݿ��е�ǰ�κŻָ�����һ���κ�
					s_sql = "update g_zdsjpzb set dqdh=?,zt=2 where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
					params = new String[] {ii_dqxh+"", xzqxm, zddz};
					jdbcT.update(s_sql,params);
					
					//��dqxh�ָ�����һ���κ�
					dqxh=""+(Integer.parseInt(dqxh)-1);
				}
				System.out.println("��ʼ�ط�"+i+">>>��ǰ�����>>>"+ii_dqxh);
				//�ȽϿ��µ�ǰ�κ���û�б仯�����û�б仯�������·���
				String i_fileName=String.valueOf(((Map) lst.get(0)).get("cxm"));
				try {
					
					communicateWithFront.SendHexToFront("");
					Decode_0F.respondDownload(xzqxm, zddz, i_fileName, ii_dqxh+"", jdbcT);
					//�ȴ�һ����ʱʱ��
					try {
						Thread.sleep(jgsj);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				break;
			}
			
			
		}
		
	}
	
	
	
	
}

