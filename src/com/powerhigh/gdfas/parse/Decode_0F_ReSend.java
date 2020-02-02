package com.powerhigh.gdfas.parse;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.module.front.communicateWithFront;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.CMXmlR;





/** 
 * Description: 程序版本远程下载<p>
 * Copyright:    Copyright   2015 LongShine<p>
 * 编写时间: 2015-9-14
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
 
public class Decode_0F_ReSend implements Runnable{	
	private String dqxh;//当前序号
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
	        CMConfig.SYSTEM_OVERTIME);//超时时间
	public static String send_delay = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_SEND_DELAY);//单帧的发送延时
	public static String resend_count = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_RESEND_COUNT);//重发次数
	@SuppressWarnings("rawtypes")
	@Override
	public void run() {
		
		
		//进入重发流程 
//		System.out.println("进入重发流程,当前序号>>>>>>>>>>"+dqxh);
		//重发的等待时间，即等待上一帧的超时时间
		Long ddsj=Long.parseLong(overtime);
		//重发的间隔时间，即等待上一帧的超时时间
		Long jgsj=Long.parseLong(send_delay);
		try {
			Thread.sleep(new Long(ddsj));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//重发次数
		int i_count=Integer.parseInt(resend_count);
		//开始重发
		for(int i=0;i<i_count;i++){
			
			
			
			String s_sql = "select *  from g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
			String[] params = new String[] { xzqxm, zddz};
			List lst = jdbcT.queryForList(s_sql, params);
			
			
			// 当前序号
			String i_dqxh = String.valueOf(((Map) lst.get(0)).get("dqdh"));
			
			if(i_dqxh.equalsIgnoreCase(dqxh)){
				//2017-06-23校验当前贞序号，如果是奇数帧则发上一帧
				int ii_dqxh=Integer.parseInt(i_dqxh);
				if(ii_dqxh%2==1){
					ii_dqxh=ii_dqxh-1;
					
					// 当数据库中的前段号恢复到上一个段号
					s_sql = "update g_zdsjpzb set dqdh=?,zt=2 where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
					params = new String[] {ii_dqxh+"", xzqxm, zddz};
					jdbcT.update(s_sql,params);
					
					//将dqxh恢复到上一个段号
					dqxh=""+(Integer.parseInt(dqxh)-1);
				}
				System.out.println("开始重发"+i+">>>当前贞序号>>>"+ii_dqxh);
				//比较看下当前段号有没有变化，如果没有变化，则重新发送
				String i_fileName=String.valueOf(((Map) lst.get(0)).get("cxm"));
				try {
					
					communicateWithFront.SendHexToFront("");
					Decode_0F.respondDownload(xzqxm, zddz, i_fileName, ii_dqxh+"", jdbcT);
					//等待一个超时时间
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

