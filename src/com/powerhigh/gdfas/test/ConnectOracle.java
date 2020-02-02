package com.powerhigh.gdfas.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectOracle {
	public static Connection getcon() {
		String driver = "oracle.jdbc.driver.OracleDriver";
//		String url = "jdbc:oracle:thin:@192.168.16.251:1521:wqpb";
//		String url = "jdbc:oracle:thin:@192.168.16.14:1521:wqdq";
//		String url = "jdbc:oracle:thin:@180.153.41.136:1521:wqdq";
		String url = "jdbc:oracle:thin:@180.153.58.245:1521:wqdq";
		Connection con = null;
		try {
			Class.forName(driver);
//			con = DriverManager.getConnection(url, "wqpb", "light");
			con = DriverManager.getConnection(url, "wqep", "vqor314400");

		} catch (Exception e) {

			e.printStackTrace();
		}
		return con;
	}

}
