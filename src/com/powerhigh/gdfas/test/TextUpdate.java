package com.powerhigh.gdfas.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class TextUpdate {

	/**
	 * @Title: main
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param args    设定文件
	 * @return void    返回类型
	 * @throws
	 */
	
	public static void main(String[] args) throws Exception{
//		Connection con=ConnectOracle.getcon();
//		String sql="select * from M_STATION_PICTURE_TMP where stationid=640 and rstatus=1";
//		PreparedStatement ps=con.prepareStatement(sql);
//		ResultSet rs=ps.executeQuery();
//		while(rs.next()){
//			String file=rs.getString("sunfile");
//			System.out.println(file);
//		}
//            
//            rs.close();
//            ps.close();
//			
//		
//		con.close();
		
//		BufferedWriter fw = null;
//		File file = new File("D:/apache-tomcat-7.0.73/webapps/ROOT/ep_photo/-1.txt");
//		fw = new BufferedWriter(new FileWriter(file)); //
//		//指定编码格式，以免读取时中文字符异常
//		for(int i=0;i<512;i++){
//			fw.write("00");
//		}
//	    fw.close();
//	    file = new File("D:/apache-tomcat-7.0.73/webapps/ROOT/ep_photo/600.txt");
//		fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8")); //
//	    fw.append("112233445566----");
//	    fw.flush();
//		fw.close();
		String str1="C127DFBD66A7CDA1AA945BB225668ADB4E4942EE65C3063C927838A26952E6D19D64E4B2920AF39FF26B2577BF733D5E87FFD7CCB5DAD728D34CB1E3392BF2F34B04AED72D226D2C5701A43DBDA93364EC85954F9D852186DE71DAAA2E252E992A5063A75A4E370A9253E53FFFD0CDB4549E3F286E251724E73C54F24EB04624662B8C6D1EA73594BE2E51A95A561AF299A38A48A454123804FF0010152A4735BB8F331217E47B01D3F1AA6D27A9ACB63FFFD1C5B5849BA05A4201276FCD5761B693CF78D5C120F53F4E9594ECD34CA9ADD13C700831B89C924FCBC5525958286F3363B3119F4029C1C6CAC38C972F2A3FFFD2A02526D1A5797BF3F37DEF6A8A79D118032796CC410A454277764524864D1CCC3744C855B3F31A642F32C4026D2483C83FAD4A5A092B5CFFD3C28AD1F7BB48CBCAE76934B6CD29C23AAAE5B8623902A6D7D4D6D665FB8B357B758C6D662E5727A8155618C425A06EC00073FA534267FFD4CEB864C34A0925405514C9127312CFC2A39C7B93D2896A5B499524F314A3B32E598AE0FA7AD5E70B047E6C47381DCD4A77DC6A563FFFD5C8F39BCFDCE06F7C120532DA440CD23671B8E4629245B771AF307876052DD703D2AB4EA62DB824E3DE877B113D7547FFD6E6111E59D4B91C0E01ED4C92106E376EC82306A2FEEDCCEFA0E62A0F1D860D58471F6720E064E3159F3342BD91FFD7C798C22ED5";
		String str2="0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
		System.out.println(str2+str1);

	}

}
