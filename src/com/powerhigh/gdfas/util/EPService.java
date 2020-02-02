package com.powerhigh.gdfas.util;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
/**
 * 
*    
* 项目名称：ep_front   
* 类名称：EPService   
* 类描述：  业务类 
* 创建人：admin   
* 创建时间：2016-2-22 下午12:30:58   
* 修改人：admin   
* 修改时间：2016-2-22 下午12:30:58   
* 修改备注：   
* @version    
*
 */
public class EPService {
	public EPService(){
		
	}
	
	/**
	 * 
	
	* @Title: getZnzd 
	
	* @Description: TODO(根据智慧终端取得所关联的智能终端信息) 
	
	* @param @param xzqxm
	* @param @param zddz
	* @param @param jdbcT
	* @param @return    设定文件 
	
	* @return List    返回类型 
	
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
	
	* @Description: TODO(根据智慧终端取得所关联的智能终端信息) 
	
	* @param @param zdid
	* @param @param jdbcT
	* @param @return    设定文件 
	
	* @return List    返回类型 
	
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
	
	* @Description: TODO(取得所有终端的电机类测量点) 
	
	* @param @param zdid
	* @param @param jdbcT
	* @param @return    设定文件 
	
	* @return List    返回类型 
	
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
