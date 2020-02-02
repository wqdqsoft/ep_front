package com.powerhigh.gdfas.util;

import java.util.*;
import java.sql.*;

import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Description: 数据初始化：将表中的数据取到上下文（context） <p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-2
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
public class CMContext {
	private static final String resource = "log4j.properties";
    private static Category cat =
                    Category.getInstance(com.powerhigh.gdfas.util.CMContext.class);
//    static{
//      PropertyConfigurator.configure(resource);
//    }

  private static Hashtable zdpfc = null; 	//终端帧序号计数器
  private static Hashtable zdmm = null; 	//终端密码
  private static Hashtable sjxxx = null;	//数据信息项
  private static Hashtable dnbsjx01 = null;	//电能表数据项(DLT-645)
  
  private DataSource dataSource = null;
  public DataSource getDataSource() {
	return dataSource;
  }
  public void setDataSource(DataSource ds) {
	dataSource = ds;
  }
  public CMContext(){
  	
  }
  public void init() throws Exception{
  	JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
    String s_sql = "";
    try{      
      //一、终端帧序号计数器、终端密码
      Hashtable ht_zdpfc = new Hashtable();//终端帧序号计数器
      Hashtable ht_zdmm = new Hashtable();//终端密码
      s_sql = "select xzqxm,zddz,zdmy,zdmmsfbh from G_ZDGZ";
      List lstZddz = jdbcT.queryForList(s_sql);     
      for(int i=0;i<lstZddz.size();i++){
        Map hm = (Map)lstZddz.get(i);
        String xzqxm = String.valueOf(hm.get("xzqxm"));
        String zddz = String.valueOf(hm.get("zddz"));
        String zdmy = String.valueOf(hm.get("zdmy"));
        String zdmmsfbh = String.valueOf(hm.get("zdmmsfbh"));

        ht_zdpfc.put(xzqxm+zddz,"0");
        ht_zdmm.put(xzqxm+zddz,zdmy+","+zdmmsfbh);

      }
      
      //二、数据信息项
      Hashtable ht_sjxxx = new Hashtable();//数据信息项
      String[][] xxxlb = new String[][]{{"1","1lsj"},{"2","2lsj_qx"},
      									{"3","2lsj_rdj"},{"4","2lsj_ydj"}};      
      for(int i=0;i<xxxlb.length;i++){
      	s_sql = "select xxxdm,xxxmc,xxdlb from g_sjxxx where xxxlb='"
      		+xxxlb[i][0]+"'";
        List lstXXX = jdbcT.queryForList(s_sql);     
      	
      	ht_sjxxx.put(xxxlb[i][1],lstXXX);
      }
      
      //三、电能表数据项(DLT-645)
      Hashtable ht_dnbsjx01 = new Hashtable();//电能表数据项(DLT-645)
      s_sql = "select dnbsjxdm,sjxdm from G_DNBSJX";
      List lstDnbsjx01 = jdbcT.queryForList(s_sql);     
      for(int i=0;i<lstDnbsjx01.size();i++){
        Map hm = (Map)lstDnbsjx01.get(i);
      	String dnbsjxdm = String.valueOf(hm.get("dnbsjxdm"));
      	String sjxdm = String.valueOf(hm.get("sjxdm"));
      	ht_dnbsjx01.put(dnbsjxdm,sjxdm);
      }
     
      
      zdpfc = ht_zdpfc;
      zdmm = ht_zdmm;      
      sjxxx = ht_sjxxx;
      dnbsjx01 = ht_dnbsjx01;
    }catch (Exception e) {
      e.printStackTrace();
      throw e;
    }

  }

  /**
   * 根据电能表数据项(DLT-645)取终端数据项
   * @param src String 行政区县码
   * @return des String 终端数据项
   */
  public static String getDnbsjx01(String src) throws Exception {
    return String.valueOf(dnbsjx01.get(src));
  }
  
  /**
   * 根据行政区县码和终端地址取终端密码(2字节十六进制字符,低位在先)
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @return zdmm   帧序号
   */
  public static String getZdmm(String xzqxm, String zddz) throws Exception {
//    String ret = "0000";
//    try{
//      String temps = (String) zdmm.get(xzqxm + zddz);
//      String[] tempS = temps.split(",");
//      String s_zdmm = tempS[0];
//      ret = Util.decStrToHexStr(s_zdmm,2);
//      ret = Util.convertStr(ret);
//    }catch(Exception e){
//      e.printStackTrace();
//      return "0000";
//    }
//    return ret;
	  
	  //2009-10-21改为16字节
	  String ret = "00000000000000000000000000000000";
//	    try{
//	      String temps = (String) zdmm.get(xzqxm + zddz);
//	      String[] tempS = temps.split(",");
//	      String s_zdmm = tempS[0];
//	      byte[] bt = s_zdmm.getBytes();
//	      ret = Util.bytetostrs(bt);
//	      ret = Util.add(ret, 16, "0");
//	      ret = Util.convertStr(ret);
//	    }catch(Exception e){
//	      e.printStackTrace();
//	      return "00000000000000000000000000000000";
//	    }
	    return ret;
  }



  /**
   * 根据行政区县码和终端地址取帧序号计数
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @return zxh   帧序号
   */
  @SuppressWarnings("unchecked")
public static int getZdpfc(String xzqxm, String zddz) throws Exception {

    int i_zdpfc = 0;
    String s_tmp = "";
    try {
      //取帧序号
      s_tmp = (String) zdpfc.get(xzqxm + zddz);
      //zxh=Integer.parseInt(s_tmp);
      int i_tmp = Integer.parseInt(s_tmp);
      //在同一个终端内从0～255整数循环
      if (i_tmp > 255) {
        i_zdpfc = 0;
        zdpfc.put(xzqxm + zddz, String.valueOf(1));
      }
      else {
        i_zdpfc = i_tmp;
        i_tmp++;
        zdpfc.put(xzqxm + zddz, String.valueOf(i_tmp));
      }

    }
    catch (NumberFormatException ne) {
      zdpfc.put(xzqxm + zddz, "1");
      i_zdpfc = 0;
    }
    catch (Exception e) {
      throw e;
    }
    return i_zdpfc;
  }

  /**
   * 根据信息项类别取数据信息项
   * @param xxxlb String 信息项类别,如：1lsj,2lsj_qx,2lsj_rdj,2lsj_ydj
   *
   * @return lstSjxxx ArrayList 数据信息项
   */
  public static List getSjxxx(String xxxlb) throws Exception {
  	return (List)sjxxx.get(xxxlb);
  }
  
  
  /**
   * 根据行政区县码和终端地址记录登出的终端信息
   * @param xzqxm String 行政区县码
   * @param zddz  String 终端地址
   * @return zxh   帧序号
   */
public  void saveLogOut(String xzqxm, String zddz) throws Exception {
	//退出登录记录
      JdbcTemplate jdbcT = new JdbcTemplate(dataSource);
      String zdid=Util.getZdid(xzqxm, zddz, jdbcT);
      String[] params = new String[] { zdid };
      String s_sql = "insert into g_zdtxztjlb values(s_zdtxzt.nextval,?,sysdate,0,sysdate,null,null)";
		jdbcT.update(s_sql, params);
   
}
  
  
  
  
  public static void main(String[] args){
    try{
      CMContext cmc = new CMContext();
      for (int i = 0; i < 300; i++) {
        int temppfc = CMContext.getZdpfc("3505","0003");

        String s_pfc = Integer.toHexString(temppfc);
        if(s_pfc.length()<2){
          s_pfc = "0"+s_pfc;
        }
        String s_pseq = s_pfc.substring(1);
        if(s_pseq.equalsIgnoreCase("a")){
          s_pseq = "10";
        }else if(s_pseq.equalsIgnoreCase("b")){
          s_pseq = "11";
        }else if(s_pseq.equalsIgnoreCase("c")){
          s_pseq = "12";
        }else if(s_pseq.equalsIgnoreCase("d")){
          s_pseq = "13";
        }else if(s_pseq.equalsIgnoreCase("e")){
          s_pseq = "14";
        }else if(s_pseq.equalsIgnoreCase("f")){
          s_pseq = "15";
        }

        int temppseq = Integer.parseInt(s_pseq);
      }
    }catch(Exception e){
      e.printStackTrace();
    }
  }
}
