package com.powerhigh.gdfas.parse;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.jdbc.core.JdbcTemplate;


import com.powerhigh.gdfas.util.*;

/**
 * Description: 任务数据解码类(任务数据查询返回、任务数据主动上报) <p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-2
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class Decode_0B{
	//加载日志
	private static final String resource = "log4j.properties";
	private static Category cat =
	      Category.getInstance(com.powerhigh.gdfas.parse.Decode_0B.class);
//	static {
//	  PropertyConfigurator.configure(resource);
//	}
	
	public Decode_0B(){
		
	}
	  
	  /**方法简述：任务数据处理
	   * @param con Connection 数据库连接
       * @param rwsxlx String 任务上行类型：1：查询返回；2：主动上报
       * @param s_sjzfsseq String 数据帧发送序列
       * @param xzqxm String 行政区县码 
       * @param zddz String 终端地址
       * @param rwlx String 任务类型(1:类数据任务；2：2类数据任务)  
       * @param rwh String 任务号
       * @param s_csdata String 报文里用来算CS的数据  
       * @param aux_time String 上行报文时间标签
       * 
       * @return void
       */
	  public static void dispose(String rwsxlx,String s_zdid,
				String s_sjzfsseq,String xzqxm,
				String zddz,String s_csdata,String s_tpv,
				String s_acd,String aux_time,JdbcTemplate jdbcT) throws Exception{
	  	
		String s_sql = "";
		String[] params = null;
		try{
			String fsmxxl = "";
			if(rwsxlx.equals("1")){
				//任务数据查询
				//取"数据祯发送明细序列"
				s_sql = "select sjzfsmxseq from g_sjzfssjdybszb "
					+ "where sjzfsseq=?";
				params = new String[]{s_sjzfsseq};
				List lstFsmxxl = (List)jdbcT.queryForList(s_sql,params);
					
				
				if(lstFsmxxl.size()==0){
					return;
						
				}else if(lstFsmxxl.size()!=0){
					fsmxxl = ((Map)lstFsmxxl.get(0)).get("sjzfsmxseq").toString();
				}
			}
		
		String DADT = "";
		if(s_tpv.equals("1")&&s_acd.equals("1")){
			//时间标签(6字节) and 事件计数器(2字节)
			DADT = s_csdata.substring(16,s_csdata.length()-16);
		}else if(s_tpv.equals("1")&&s_acd.equals("0")){
			//时间标签(6字节)
			DADT = s_csdata.substring(16,s_csdata.length()-12);
		}else if(s_tpv.equals("0")&&s_acd.equals("1")){
			//事件计数器(2字节)
			DADT = s_csdata.substring(16,s_csdata.length()-4);
		}else if(s_tpv.equals("0")&&s_acd.equals("0")){
			//无附加信息
			DADT = s_csdata.substring(16);
		}
		//System.out.println("[taskDecode]DADT:"+DADT);
		cat.info("[taskDecode]DADT:"+DADT);
		
		//DADT的下标
		int index = 0;
		String main_dadt = DADT.substring(index, index+8); 
		index += 8;
		
		//信息点Pn
		String main_da = main_dadt.substring(0,4);
		main_da = Util.tranDA(Util.convertStr(main_da));	     
		//信息类Fn
		String main_dt = main_dadt.substring(4,8);
		main_dt = Util.tranDT(Util.convertStr(main_dt));
		//System.out.println("[taskDecode]main_dadt:"+"P"+main_da+"F"+main_dt);
		cat.info("[taskDecode]main_dadt:"+"P"+main_da+"F"+main_dt);
	      
	    //任务号
	  	String rwh = main_da;      		
	  	//任务类型(1:类数据任务；2：2类数据任务) 
	  	String rwlx = main_dt;
	  	
	  	Map mp = null;
	  	if(rwlx.equals("1")){
	  		//1类数据任务
	  		mp = decodeType1(s_zdid,DADT.substring(index),jdbcT);
	  	}else if(rwlx.equals("2")){
	  		//2类数据任务
	  		mp = decodeType2(DADT.substring(index));
	  	}
		Collection coll = mp.values();
		Object[] datas = coll.toArray();
		// 任务上行类型：1：查询返回；2：主动上报
		if(rwsxlx.equals("1")){
			//任务数据查询返回(写"召测数据返回表")
			for(int i=0;i<datas.length;i++){
				Vector vt = (Vector)datas[i];
				for(int j=0;j<vt.size();j++){
					//序号、数据项代码、数据值、数据时间、信息点类别、信息点号、标志(1:写任务数据)
					String[] ss = (String[])vt.get(j);
					String xh 		= ss[0];
					String sjxdm 	= ss[1];
					String sjz 		= ss[2];
					String sjsj 	= ss[3];
					String xxdlb 	= ss[4];
					String xxdh 	= ss[5];
					String flg 		= ss[6];
					
					String xxdmc = "";
					if(xxdlb.equals("0")){
						xxdmc = "终端";
					}else if(xxdlb.equals("1")){
						xxdmc = "测量点"+xxdh;
					}else if(xxdlb.equals("2")){
						xxdmc = "总加组"+xxdh;
					}else if(xxdlb.equals("3")){
						xxdmc = "直流模拟量"+xxdh;
					}
					
					s_sql = "insert into g_zcsjfhb(sjzfsmxseq,sjxdm,sjz,xxdmc,xh,sjsj) "
		  	            + "values(?,?,?,?,?,?)";
					params = new String[]{fsmxxl,sjxdm,sjz,
							xxdmc,xh,sjsj};
					jdbcT.update(s_sql,params);
				}
			}
			
		}else if(rwsxlx.equals("2")){
			//任务数据主动上报(执行存储过程sp_saveautotaskdata) 
			Vector sp_param = new Vector();
			//1、行政区线码
			sp_param.addElement(xzqxm);
			//2、终端地址
			sp_param.addElement(zddz);
			
			//3、任务号
			sp_param.addElement(rwh);
			
			String nowTime = Util.getNowTime();//yyMMddHHmmss
			
			String array = "";
			for(int i=0;i<datas.length;i++){
				Vector vt = (Vector)datas[i];
				for(int j=0;j<vt.size();j++){
					String[] ss = (String[])vt.get(j);
					if(ss[6]==null||!ss[6].equals("1")){
						continue;
					}
					array += ss[1]+","+ss[2]+","+ss[3]+","+ss[4]+","+ss[5]+";";
				}
			}
			
			//5、数据
			sp_param.addElement(array);

			cat.info("[taskDecode]array:"+array);

			Util.executeProcedure(jdbcT,"sp_saveautotaskdata",sp_param,2);
		}
		
	  }catch(Exception e){
		throw e;
	  }
	}
	  
	//1类数据任务解析
	private static Map decodeType1(String s_zdid,String DADT,JdbcTemplate jdbcT) throws Exception{
		return Decode_0C.decode(s_zdid,DADT,jdbcT);
	}
	  
	//2类数据任务解析
	private static Map decodeType2(String DADT) throws Exception{
		return Decode_0D.decode(DADT);		
	}
		
	  
}