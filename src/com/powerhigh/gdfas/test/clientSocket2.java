package com.powerhigh.gdfas.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.DateUtil;

public class clientSocket2 {

	public static void main(String[] args) throws Exception {
//		operation.sendAFN04F65("3", "0715", "0186", "1", "15;0;150412000000;24;P1@F25");
//		operation.sendAFN04F67("3", "0715", "0186", "1","55");
//		clientSocket2.setRW();
//		clientSocket2.qdRW(); 
		clientSocket2.fwYRwMsjZD();
//		clientSocket2.reSet();
	}

	public static List<String []> getZD(String s) throws Exception {
		Connection con = ConnectOracle.getcon();
		String sql = s;
		List<String []> zddzs = new ArrayList<String[]>();
		PreparedStatement ps = con.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String [] r=new String [2];
			r[0]=rs.getString("xzqxm");
			r[1]=rs.getString("zddz");
			zddzs.add(r);
		}
		rs.close();
		ps.close();
        con.close();
        return zddzs;
	}
	
	public static List<String []> getNORWZD() throws Exception {
		Connection con = ConnectOracle.getcon();
		String sql = "select xzqxm,zddz from G_ZDGZ z where z.zt=1 and z.xzqxm in('0715','1815')   and zdid not in (select zdid from g_zdrwpzb pp)";
		List<String []> zddzs = new ArrayList<String[]>();
		PreparedStatement ps = con.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String [] r=new String [2];
			r[0]=rs.getString("xzqxm");
			r[1]=rs.getString("zddz");
			zddzs.add(r);
		}
		rs.close();
		ps.close();
        con.close();
        return zddzs;
	}
	
	public static List<String []> getNOCLDZD() throws Exception {
		Connection con = ConnectOracle.getcon();
		String sql = "select xzqxm,zddz from G_ZDGZ z where z.zt=1 and z.zdid not in(select distinct zdid from g_zdcldpzb where cldh=1)";
		List<String []> zddzs = new ArrayList<String[]>();
		PreparedStatement ps = con.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String [] r=new String [2];
			r[0]=rs.getString("xzqxm");
			r[1]=rs.getString("zddz");
			zddzs.add(r);
		}
		rs.close();
		ps.close();
        con.close();
        return zddzs;
	}
	
	public static List<String []> getYrwMsjZD() throws Exception {
		Connection con = ConnectOracle.getcon();
		String sql = "select * from G_ZDGZ z where z.zt=1 and z.xzqxm in('0715','1815')   and zdid not in (select zdid from g_zdrwpzb pp) and zdid not in(select distinct zdid from G_ZDCLDDQSJB where dtime is not null)";
		List<String []> zddzs = new ArrayList<String[]>();
		PreparedStatement ps = con.prepareStatement(sql);
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			String [] r=new String [2];
			r[0]=rs.getString("xzqxm");
			r[1]=rs.getString("zddz");
			zddzs.add(r);
		}
		rs.close();
		ps.close();
        con.close();
        return zddzs;
	}
	
	
	
	public static void setRW() throws Exception{
		List<String[]> zddzs=clientSocket2.getNORWZD();
		Date date=DateUtil.parse("2015-08-24 09:12:37");
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			date=new Date(date.getTime()+(i+1)*1000);
			String jzsj=DateUtil.formatDate(date, "yyMMddHHmmss");
			System.out.println("xzqxm:"+zddz[0]+"--------zddz:"+zddz[1]+"------15;0;"+jzsj+";24;P1@F25");
			operation.sendAFN04F65("3", zddz[0], zddz[1], "1", "15;0;"+jzsj+";24;P1@F25");
//			operation.sendAFN04F67("3", "0715", zddz, "1","55");
		}
	}
	
	public static void qdRW() throws Exception{
		String sql="select z.* from g_zdrwpzb pp,G_ZDGZ z where z.zdid=pp.zdid and pp.qybz='AA' and z.zt=1 order by pp.fsjzsj desc ";
        List<String []> zddzs=clientSocket2.getZD(sql);
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			System.out.println("启动任务：xzqxm:"+zddz[0]+"--------zddz:"+zddz[1]);
			operation.sendAFN04F67("3", zddz[0], zddz[1], "1","55");
		}
	}
	
	public static void fwZD(String sql) throws Exception{
		List<String[]> zddzs=clientSocket2.getZD(sql);
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			operation.sendAFN01F1("3", zddz[0], zddz[1], "1");//终端复位
		}
	}
   /**
    * 	
   
   * @Title: fwYRwMsjZD 
   
   * @Description: TODO(复位有任务但是没有数据的终端) 
   
   * @param @param sql
   * @param @throws Exception    设定文件 
   
   * @return void    返回类型 
   
   * @throws
    */
   public static void fwYRwMsjZD() throws Exception{
		String sql = "select * from G_ZDGZ z where z.zt=1 and z.xzqxm in('0715','1815')   and zdid  in (select zdid from g_zdrwpzb pp) and zdid not in(select distinct zdid from G_ZDCLDDQSJB where dtime is not null)";

		List<String[]> zddzs=clientSocket2.getZD(sql);
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			operation.sendAFN01F1("3", zddz[0], zddz[1], "1");//终端复位
		}
	}
   
   
   public static void reSet() throws Exception{
	   String sql = "select * from G_ZDGZ z where z.zt=1 and z.xzqxm in('0715','1815')   and zdid  in (select zdid from g_zdrwpzb pp) and zdid not in(select distinct zdid from G_ZDCLDDQSJB where dtime is not null)";
       List<String[]> zddzs=clientSocket2.getZD(sql);
       
       Date date=DateUtil.parse("2015-04-28 01:10:10");
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			date=new Date(date.getTime()+(i+1)*1000);
			String jzsj=DateUtil.formatDate(date, "yyMMddHHmmss");
			System.out.println("xzqxm:"+zddz[0]+"--------zddz:"+zddz[1]+"------15;0;"+jzsj+";24;P1@F25");
			operation.sendAFN04F65("3", zddz[0], zddz[1], "1", "15;0;"+jzsj+";24;P1@F25");
		}
		Thread.sleep(5000);
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			System.out.println("启动任务：xzqxm:"+zddz[0]+"--------zddz:"+zddz[1]);
			operation.sendAFN04F67("3", zddz[0], zddz[1], "1","55");
		}
		Thread.sleep(5000);
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			operation.sendAFN01F1("3", zddz[0], zddz[1], "1");//终端复位
		}
		
   }
   
   
}