package com.powerhigh.gdfas.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import com.powerhigh.gdfas.rmi.operation;


public class ChangeIPPort {

	/**
	 * @Title: main
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param args    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	public static void main(String[] args) throws Exception{
		Connection con = ChangeIPPort.getcon();
		String sql = "select * from g_zdgz where zt=1 and zdlx=1";
		PreparedStatement ps = con.prepareStatement(sql);
		ResultSet rs=ps.executeQuery();
		String ipport="180.153.58.245:29001;180.153.58.245:29001;1";
		while (rs.next()) {
			operation.sendAFN04F3(rs.getString("txfsdm"), rs.getString("xzqxm"), rs.getString("zddz"), ipport);
//		    System.out.println(rs.getString("zddz"));
		}
		rs.close();
		ps.close();
		con.close();

	}
	
	public static Connection getcon() {
		String driver = "oracle.jdbc.driver.OracleDriver";
//		String url = "jdbc:oracle:thin:@180.153.58.245:1521:wqdq";
		String url = "jdbc:oracle:thin:@180.153.41.136:1521:wqdq";
		Connection con = null;
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, "wqep", "vqor314400");

		} catch (Exception e) {

			e.printStackTrace();
		}
		return con;
	}

}
