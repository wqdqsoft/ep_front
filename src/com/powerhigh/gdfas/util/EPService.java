package com.powerhigh.gdfas.util;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
/**
 * 
*    
* ��Ŀ���ƣ�ep_front   
* �����ƣ�EPService   
* ��������  ҵ���� 
* �����ˣ�admin   
* ����ʱ�䣺2016-2-22 ����12:30:58   
* �޸��ˣ�admin   
* �޸�ʱ�䣺2016-2-22 ����12:30:58   
* �޸ı�ע��   
* @version    
*
 */
public class EPService {
	public EPService(){
		
	}
	
	/**
	 * 
	
	* @Title: getZnzd 
	
	* @Description: TODO(�����ǻ��ն�ȡ���������������ն���Ϣ) 
	
	* @param @param xzqxm
	* @param @param zddz
	* @param @param jdbcT
	* @param @return    �趨�ļ� 
	
	* @return List    �������� 
	
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	public static List getZnzd(String xzqxm,String zddz,JdbcTemplate jdbcT){
		String s_sql = "select * from g_zdgz   g where g.sumpid in(select sumpid from m_sump s where s.stationid=(select stationid from g_zdgz where xzqxm=? and zddz=? and zdlx=1))";
		String[] params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(s_sql, params);
		return lst;
	}
	
	/**
	 * 
	
	* @Title: getZnzd 
	
	* @Description: TODO(�����ǻ��ն�ȡ���������������ն���Ϣ) 
	
	* @param @param zdid
	* @param @param jdbcT
	* @param @return    �趨�ļ� 
	
	* @return List    �������� 
	
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	public static List getZnzd(String zdid,JdbcTemplate jdbcT){
		String s_sql = "select * from g_zdgz   g where g.sumpid in(select id from m_sump s where s.stationid=(select stationid from g_zdgz where zdid=? and zdlx=1))";
		String[] params = new String[] { zdid };
		List lst = jdbcT.queryForList(s_sql, params);
		return lst;
	}
	
	
	
	
	/**
	 * 
	
	* @Title: getZnzdDjcld 
	
	* @Description: TODO(ȡ�������ն˵ĵ���������) 
	
	* @param @param zdid
	* @param @param jdbcT
	* @param @return    �趨�ļ� 
	
	* @return List    �������� 
	
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	public static List getZnzdDjcld(String zdid,JdbcTemplate jdbcT){
		String s_sql = "select * from g_zdcldpzb c where c.zdid=? and c.cldlx=6 ";
		String[] params = new String[] {zdid};
		List lst = jdbcT.queryForList(s_sql, params);
		return lst;
	}

}
