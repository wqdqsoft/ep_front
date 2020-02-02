package com.powerhigh.gdfas.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.DateUtil;

public class clientSocket {

	public static void main(String[] args) throws Exception {
//		String s="select xzqxm,zddz from G_ZDGZ z where z.zt=1 and z.xzqxm " +
//				"in('0715','1815')  and zdid in(select zdid from g_zdrwpzb pp) " +
//				"and zdid not in(select distinct zdid from G_ZDCLDDQSJB where dtime is not null)";
//		clientSocket.qdRW();
//		clientSocket.fwZD(s);
//		clientSocket.setRW();
		// operation.sendZdds("02","1111","1111","XX");
		
//		List<String []> zddzs=clientSocket.getNOCLDZD();
//		List<String []> zddzs=clientSocket.getNORWZD();
//		
////		operation.sendAFN04F65("3", "0715", "0041", "1", "1;0;150412000000;24;P1@F25");
////		operation.sendAFN04F67("3", "0715", "0041", "1","55");
//		
//		for(int i=0;i<zddzs.size();i++){
//			String[] zddz=zddzs.get(i);
//			Thread.sleep(1000);
////			operation.sendAFN01F1("3", zddz[0], zddz[1], "1");//ÖÕ¶Ë¸´Î»
////			operation.sendAFN0AF10("3", zddz[0], zddz[1], 1, 5);
////			operation.sendAFN04F3("3", "0715", zddz,
////					"180.153.41.136:11001;180.153.41.136:11001;CMNET");
////			operation.sendAFN04F65("3", "0715", zddz, "1", "15;0;"+jzsj+";24;P1@F25");
////			operation.sendAFN04F67("3", zddz[0], zddz[1], "1","55");
//		}
//		String sql="select * from G_ZDGZ where zt=1";
//		List<String []> zddzs=clientSocket.getZD(sql);
//		for(int i=0;i<zddzs.size();i++){
//			String[] zddz=zddzs.get(i);
//			Thread.sleep(500);
//			System.out.println("send>>>>>"+zddz[0]+"-"+zddz[1]);
//	//		operation.sendAFN04F3("3", zddz[0], zddz[1],
//	//				"180.153.41.136:11001;180.153.41.136:11001;CMNET");
//			
//			operation.sendAFN0AF10("3", zddz[0], zddz[1], 1, 5);
		String param[][]=new String [1][2];
		param[0][0]="0000";
		param[0][1]="F7";
		operation.query_1lsj("3", "9603", "0005", param); 
//	}
//		operation.sendAFN04F3("3", "0715", "0247",
//				"180.153.41.136:11001;180.153.41.136:11001;CMNET");
		
		
		
		
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
		String sql = "select xzqxm,zddz from G_ZDGZ z where z.zt=1 and z.xzqxm in('0715','1815')   and zdid not in (select zdid from g_zdrwpzb pp) and  zdid not in(select distinct zdid from G_ZDCLDDQSJB where dtime is not null)";
		List<String []> zddzs = new ArrayList<String[]>();
		PreparedStatement ps = con.prepareStatement(sql);
		String [] r=new String [2];
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
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
		String [] r=new String [2];
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
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
		List<String[]> zddzs=clientSocket.getNORWZD();
		Date date=DateUtil.parse("2015-04-12 04:08:56");
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			date=new Date(date.getTime()+(i+1)*1000);
			String jzsj=DateUtil.formatDate(date, "yyMMddHHmmss");
			operation.sendAFN04F65("3", zddz[0], zddz[1], "1", "15;0;"+jzsj+";24;P1@F25");
//			operation.sendAFN04F67("3", "0715", zddz, "1","55");
		}
	}
	
	public static void qdRW() throws Exception{
         List<String []> zddzs=clientSocket.getNORWZD();
		
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			operation.sendAFN04F67("3", zddz[0], zddz[1], "1","55");
		}
	}
	
	public static void fwZD(String sql) throws Exception{
		
		List<String[]> zddzs=clientSocket.getZD(sql);
		for(int i=0;i<zddzs.size();i++){
			String[] zddz=zddzs.get(i);
			Thread.sleep(1000);
			operation.sendAFN01F1("3", zddz[0], zddz[1], "1");//ÖÕ¶Ë¸´Î»
		}
	}
}