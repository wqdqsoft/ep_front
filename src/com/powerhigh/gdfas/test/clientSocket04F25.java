package com.powerhigh.gdfas.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.DateUtil;

public class clientSocket04F25 {

	public static void main(String[] args) throws Exception {
//		operation.sendAFN04F65("3", "0715", "0186", "1", "15;0;150412000000;24;P1@F25");
//		operation.sendAFN04F67("3", "0715", "0186", "1","55");
//		clientSocket2.setRW();
//		clientSocket2.qdRW(); 
//		clientSocket04F25.fwYRwMsjZD();
//		clientSocket2.reSet();
		List<String []> zds=clientSocket04F25.getNOCTZD();
		for(int i=0;i<zds.size();i++){
			String [] r=zds.get(i);
			operation.sendAFN04F25("3", r[0], r[1], 0, "1;6;100");
			System.out.println("++++++++++"+i);
			Thread.sleep(1000L);
		}
	}

	
	
	/**
	 * 
	* @Title: getNOCTZD
	* @Description: TODO(取得一代没有设置CT的终端)
	* @param @return
	* @param @throws Exception    设定文件
	* @return List<String[]>    返回类型
	* @throws
	 */
	public static List<String []> getNOCTZD() throws Exception {
		Connection con = ConnectOracle.getcon();
		String sql = "select xzqxm,zddz from g_zdgz where zdid in(select distinct(zdid) from g_zdcldpzb p where p.cldh=0 and p.ct<>6 and zdid in(select zdid from g_zdgz where zdxh=1 and zdlx=1 and stationid<>0 and zt=1))";
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
	
	
   
}