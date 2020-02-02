package com.powerhigh.gdfas.parse;

import java.util.*;

import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.CMXmlR;
import com.powerhigh.gdfas.util.DateUtil;
import com.powerhigh.gdfas.util.Util;

/**
 * Description: AFN=00(�ն���Ӧ��վ�Ŀ����������������������ش���)
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * ��дʱ��: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 �޸��ˣ� �޸�ʱ�䣺
 */

public class Decode_00 {
	public static String buffer = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
			CMConfig.SYSTEM_DOWNLOADBUFFER_KEY);// ÿ���������ݵĴ�С
	public static String overtime = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_OVERTIME);//��ʱʱ��
	public Decode_00() {

	}

	public static void dispose(String s_xzqxm, String s_zddz, String sSJZ,
			String s_csdata, String s_sjzfsseq, JdbcTemplate jdbcT)
			throws Exception {
		//�ն���Ӧ��վ�Ŀ����������������
		String s_dadt = s_csdata.substring(16, 24);

		//��Ϣ��Pn
		String s_da = s_dadt.substring(0, 4);
		s_da = Util.tranDA(Util.convertStr(s_da));
		String s_Pda = "P" + s_da;
		//��Ϣ��Fn
		String s_dt = s_dadt.substring(4, 8);
		s_dt = Util.tranDT(Util.convertStr(s_dt));
		String s_Fdt = "F" + s_dt;
		//PnFn
		String s_PF = s_Pda + s_Fdt;

		String s_sql = "";
		String[] params = null;
		//ȷ��/���ϵ����ݵ�Ԫ��ʶ
		if (s_PF.equalsIgnoreCase("P0F1")) {
			//F1ȫ��ȷ��
			s_sql = "update g_sjzfssjdybszb set cwdm=? where sjzfsseq=?";
			params = new String[] {"XX", s_sjzfsseq };
			jdbcT.update(s_sql, params);

			s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
					+ "where sjzfsseq=?";
			params = new String[] { "01",sSJZ, s_sjzfsseq };
			jdbcT.update(s_sql, params);

			//���ݲ���"�����ݴ��" д "�ն����в������ñ�"
			fromSzzcbToZdyxcspzb(jdbcT, s_xzqxm, s_zddz, s_sjzfsseq);

		} else if (s_PF.equalsIgnoreCase("P0F2")) {
			//F2ȫ������
			s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
					+ "where sjzfsseq=?";
			params = new String[] {"03", sSJZ, s_sjzfsseq };
			jdbcT.update(s_sql, params);

			s_sql = "update g_sjzfssjdybszb set cwdm=? where sjzfsseq=?";
			params = new String[] { "YY",s_sjzfsseq };
			jdbcT.update(s_sql, params);

		} else if (s_PF.equalsIgnoreCase("P0F3")) {
			//F3���ȷ�Ϸ���
			s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
					+ "where sjzfsseq=?";
			params = new String[] { "04",sSJZ, s_sjzfsseq };
			jdbcT.update(s_sql, params);

			s_sql = "select count(1) count from g_sjzfssjdybszb where sjzfsseq='"
					+ s_sjzfsseq + "'";
			int i_count = Util.getRecordCount(s_sql, jdbcT);
			int i_index = 24;
			for (int i = 0; i < i_count; i++) {
				String temp_s = s_csdata.substring(i_index, i_index + 10);
				String temp_dadt = temp_s.substring(2, 4)
						+ temp_s.substring(0, 2) + temp_s.substring(6, 8)
						+ temp_s.substring(4, 6);
				String temp_err = temp_s.substring(8, 10);
				s_sql = "update g_sjzfssjdybszb set cwdm=? "
						+ "where sjzfsseq=? and sjdybsz=?";
				params = new String[] { temp_err, s_sjzfsseq, temp_dadt };
				jdbcT.update(s_sql, params);

				i_index = i_index + 10;
			}
		}
	}
    
	
	/**
	 * 
	* @Title: fromSzzcbToZdyxcspzb
	* @Description: TODO(�ɹ�����֮����Ҫ����������)
	* @param @param jdbcT
	* @param @param s_xzqxm
	* @param @param s_zddz
	* @param @param s_sjzfsseq
	* @param @throws Exception    �趨�ļ�
	* @return void    ��������
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	private static void fromSzzcbToZdyxcspzb(JdbcTemplate jdbcT,
			String s_xzqxm, String s_zddz, String s_sjzfsseq) throws Exception {
		String s_sql = "";
		String[] params = null;
		try {
			//�����ն˵�ַȡ�ն�ID
			s_sql = "select zdid from G_ZDGZ where xzqxm=? and zddz=?";
			params = new String[]{s_xzqxm,s_zddz};
			List zdidlst = jdbcT.queryForList(s_sql, params);
			if(zdidlst==null || zdidlst.size()==0){
				return;
			}
			String s_zdid = String.valueOf(((Map)zdidlst.get(0)).get("zdid"));
			
			s_sql = "select sjxdm,sjz from g_csszzcb where sjzfsseq=?";
			params = new String[] { s_sjzfsseq };
			List lst = jdbcT.queryForList(s_sql, params);
			for (int i = 0; i < lst.size(); i++) {
				Map hm = (Map) lst.get(i);
				String sjxdm = String.valueOf(hm.get("sjxdm"));
				String sjz = String.valueOf(hm.get("sjz"));

				if (sjxdm.equals("AFN05F1")) {
					//�������������(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//д�ն˲�����������ñ�
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					//ss=Util.hexStrToDecStr(ss);
					
					s_sql="select zdxh from g_zdgz where zdid=?";
					params = new String[] { s_zdid };
				    List cldList = jdbcT.queryForList(s_sql, params);
				    Map cldMap = (Map) cldList.get(0);
				    // �ն��ͺ�
				 	String zdxh = String.valueOf(cldMap.get("zdxh"));
				 	String sbqtzt="";
				 	if("33".equalsIgnoreCase(ss)){
				 		sbqtzt="1";
				 	}else if("CC".equalsIgnoreCase(ss)){
				 		sbqtzt="0";
				 	}
				 	if("1".equalsIgnoreCase(zdxh)){
				 		//�����һ���ն�
				 		s_sql = "update g_zdclddqsjb "
								+"set sbqtzt=? "
								+" where cldid=(select id from g_zdcldpzb where zdid=? and cldh=?)";
							params = new String[]{sbqtzt,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}else{
				 		//����Ƕ����ն�
				 		s_sql = "update g_zddqsbpzb "
								+"set zt=? "
								+" where  zdid=? and cldh=? ";
							params = new String[]{sbqtzt,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}
				 	
				 	//���ն˹����ļ�ˮ������Ϊ�ֶ�״̬
				 	s_sql = "update m_sump s set s.isauto=0 where id =(select sumpid from g_zdgz where zdid=?)";
					params = new String[] { s_zdid };
					jdbcT.update(s_sql, params);
					
					

				}else if (sjxdm.equals("AFN05F2")) {
					//�������������(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//д�ն˲�����������ñ�
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					ss=Util.hexStrToDecStr(ss);
					
					s_sql="select zdxh from g_zdgz where zdid=?";
					params = new String[] { s_zdid };
				    List cldList = jdbcT.queryForList(s_sql, params);
				    Map cldMap = (Map) cldList.get(0);
				    // �ն��ͺ�
				 	String zdxh = String.valueOf(cldMap.get("zdxh"));
				 	
				 	if("1".equalsIgnoreCase(zdxh)){
				 		//�����һ���ն�
				 		s_sql = "update g_zdclddqsjb "
								+"set sbyxpl=? "
								+" where cldid=(select id from g_zdcldpzb where zdid=? and cldh=?)";
							params = new String[]{ss,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}else{
				 		//����Ƕ����ն�
				 		s_sql = "update g_zddqsbpzb "
								+"set sbyxpl=? "
								+" where  zdid=? and cldh=? ";
							params = new String[]{ss,s_zdid,cldh};
							jdbcT.update(s_sql, params);
				 	}
					
					

				}else if (sjxdm.equals("AFN04F25")) {
					//�������������(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//д�ն˲�����������ñ�
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String[] ss = cld[1].split(";");
					
					s_sql = "update g_zdcldpzb "
						+"set pt=?,ct=?,ldlljz=? "
						+" where zdid=? and cldh=?";
					params = new String[]{ss[0],ss[1],ss[2],s_zdid,cldh};
					jdbcT.update(s_sql, params);

				}else if (sjxdm.equals("AFN04F15")) {
					//�������������(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//д�ն˲�����������ñ�
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					
					s_sql = "update g_zdcldpzb "
						+"set afn04f15=? "
						+" where zdid=? and cldh=?";
					params = new String[]{ss.substring(4,ss.length()),s_zdid,cldh};
					jdbcT.update(s_sql, params);
					
					///////���¶�������
					s_sql = "select cldh from g_zdsbkzcsb where zdid=?";
	      	        params = new String[]{s_zdid};
	          		List dnbList = jdbcT.queryForList(s_sql,params);
					String[] f15s=ss.split(";");
					String cldIn = "";
	      			for(int n=0;n<f15s.length;n++){
	      			    //1�����
	      				String pz1 = f15s[n].split(",")[0];
	          			
	          			//2���������
	      				String pz2 = f15s[n].split(",")[1];
	          			
	          			//3�����Ӳ����
	      				String pz3 = f15s[n].split(",")[2];
	          			
	          			
	          		    //4���������Ӳ����
	      				String pz4 = f15s[n].split(",")[3];
	          			
	          			//5��ͬʱ����ʹ��
	      				String pz5 = f15s[n].split(",")[4];
	          			
	          		    //6�������л�ʱ��-Сʱ
	      				String pz6 = f15s[n].split(",")[5];
	          			
	          		    //7���������
	      				String pz7 = f15s[n].split(",")[6];

	          		    //8��ˮλ��λ
	      				String pz8 = f15s[n].split(",")[7];

	          		    //9���߼���ϵ
	      				String pz9 = f15s[n].split(",")[8];

	          		    //10����һ���������
	      				String pz10 = f15s[n].split(",")[9];

	          		    //11��ˮλ��λ
	      				String pz11 = f15s[n].split(",")[10];

	          		    //12�����ƶ���
	      				String pz12 = f15s[n].split(",")[11];

	          		    //13����С�¶�
	      				String pz13 = f15s[n].split(",")[12];

	          		    //14������¶�
	      				String pz14 = f15s[n].split(",")[13];
	          			
	          		    //15������ʱ��
	      				String pz15 = f15s[n].split(",")[14];
	          			
	          		    //16��ֹͣʱ��
	      				String pz16 = f15s[n].split(",")[15];
	          			
	          			boolean isIn = false;
	          			for(int j=0;j<dnbList.size();j++){
	          				Map tempHM = (Map)dnbList.get(j);
	          				if(String.valueOf(tempHM.get("cldh")).equals(pz2)){
	          					isIn = true;
	          					break;
	          				}
	          			}
	          			
	          			//д"���ñ�"
	          			if(isIn == true){
	          				//��վ���иò����������
	          				s_sql = "update g_zdsbkzcsb set scyjh=?,byscyjh=?,tsgzsn=?,zbqhsj=?,cthm1=?,swdw1=?,ljgx=?,cthm2=?,swdw2=?,kzdz=?,zxwd=?,zdwd=?,qdsj=?,tzsj=? where zdid=? and cldh=?";
	        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)||"CC".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)||"CC".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)||"CCCC".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)||"CCCC".equalsIgnoreCase(pz16)?"":pz16,s_zdid,pz2};
	          			}else if(isIn == false){
	          				//��վ���޸ò����������
	          				s_sql = "insert into g_zdsbkzcsb(id,zdid,xh,cldh,scyjh,byscyjh,tsgzsn,zbqhsj,cthm1,swdw1,ljgx,cthm2,swdw2,kzdz,zxwd,zdwd,qdsj,tzsj) "
	        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	        	        	params = new String[]{s_zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)||"CC".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)||"CC".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)||"CCCC".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)||"CCCC".equalsIgnoreCase(pz16)?"":pz16};
	          			}
	          			jdbcT.update(s_sql,params);
	          			
	          			cldIn += "'"+pz2+"',";
	          			          			
	      			}
	      			
	                 
	      			//ɾ����������
	      			s_sql = "delete g_zdsbkzcsb where zdid=?  ";
	      			if(f15s.length>0){
	      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
	      			}
	      	        params = new String[]{s_zdid};
	                jdbcT.update(s_sql,params); 
					
					
					

				}else if (sjxdm.equals("AFN04F16")) {
					//�������������(cldh@cs1;cs2;cs3;cs4;cs5;cs6)
					//д�ն˲�����������ñ�
					String[] cld = sjz.split("@");
					String cldh = cld[0];
					String ss = cld[1];
					
					s_sql = "update g_zdcldpzb "
						+"set afn04f16=? "
						+" where zdid=? and cldh=?";
					//ȥ����ǰ��"1,1,"
					params = new String[]{ss.substring(4,ss.length()),s_zdid,cldh};
					jdbcT.update(s_sql, params);
					
				    ///////���¶�������
					s_sql = "select cldh from g_zdfjdcfkzcs where zdid=?";
	      	        params = new String[]{s_zdid};
	          		List dnbList = jdbcT.queryForList(s_sql,params);
					String[] f15s=ss.split(";");
					String cldIn = "";
	      			for(int n=0;n<f15s.length;n++){
	      			    //1�����
	      				String pz1 = f15s[n].split(",")[0];
	          			
	          			//2���������
	      				String pz2 = f15s[n].split(",")[1];
	          			
	          			//3�����Ӳ����
	      				String pz3 = f15s[n].split(",")[2];
	          			
	          			
	          		    //4���������Ӳ����
	      				String pz4 = f15s[n].split(",")[3];
	          			
	      				 //5�������л�ʱ��-Сʱ
	      				String pz5 = f15s[n].split(",")[4];
	          			
	      				//6����ˮ������ʹ��
	      				String pz6 = f15s[n].split(",")[5];
	          			
	      			    //7����С�¶�
	      				String pz7 = f15s[n].split(",")[6];

	      				//8������¶�
	      				String pz8 = f15s[n].split(",")[7];

	      				//9������ʱ��
	      				String pz9 = f15s[n].split(",")[8];

	      				//10��ֹͣʱ��
	      				String pz10 = f15s[n].split(",")[9];

	      				//11��Ƶ��
	      				String pz11 = f15s[n].split(",")[10];

	      				//12�����ƶ���
	      				String pz12 = f15s[n].split(",")[11];

	          		   
	          			
	          			boolean isIn = false;
	          			for(int j=0;j<dnbList.size();j++){
	          				Map tempHM = (Map)dnbList.get(j);
	          				if(String.valueOf(tempHM.get("cldh")).equals(pz2)){
	          					isIn = true;
	          					break;
	          				}
	          			}
	          			
	          		    //д"���ñ�"
	          			if(isIn == true){
	          				//��վ���иò����������
	          				s_sql = "update g_zdfjdcfkzcs set scyjh=?,byscyjh=?,zbqhsj=?,sbldsn=?,zxwd=?,zdwd=?,qdsj=?,tzsj=?,pl=?,kzdz=? where zdid=? and cldh=?";
	        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)||"CC".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)||"CC".equalsIgnoreCase(pz8)?"":pz8,"EEEE".equalsIgnoreCase(pz9)||"CCCC".equalsIgnoreCase(pz9)?"":pz9,"EEEE".equalsIgnoreCase(pz10)||"CCCC".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,s_zdid,pz2};
	          			}else if(isIn == false){
	          				//��վ���޸ò����������
	          				s_sql = "insert into g_zdfjdcfkzcs(id,zdid,xh,cldh,scyjh,byscyjh,zbqhsj,sbldsn,zxwd,zdwd,qdsj,tzsj,pl,kzdz) "
	        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	        	        	params = new String[]{s_zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)||"CC".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)||"CC".equalsIgnoreCase(pz8)?"":pz8,"EEEE".equalsIgnoreCase(pz9)||"CCCC".equalsIgnoreCase(pz9)?"":pz9,"EEEE".equalsIgnoreCase(pz10)||"CCCC".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12};
	          			}
	          			jdbcT.update(s_sql,params);
	          			
	          			cldIn += "'"+pz2+"',";
	          			}
	      			
	                 
	      			//ɾ����������
	      			s_sql = "delete g_zdfjdcfkzcs where zdid=?  ";
	      			if(f15s.length>0){
	      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
	      			}
	      	        params = new String[]{s_zdid};
	                jdbcT.update(s_sql,params); 

				} else {

					if (sjxdm.equals("AFN04F10")) {
						//�ն˵��ܱ�/��������װ�ò�������(cs1;...;csn)--N�����ܱ�����
						//д�ն����в������ñ�
//						s_sql = "update g_zdyxcspzb set AFN04F10=? "
//								+ "where zdid=?";
//						params = new String[] { sjz, s_zdid };
//						jdbcT.update(s_sql, params);
						
//						//д�ն˲��������ñ�
//						String cldIn = "";
//						String[] ss_csz = sjz.split(";");						
//						int dnbNum = ss_csz.length;//���ܱ�����
//					    for (int j = 0; j < dnbNum; j++) {
//					    	//ÿ�����ܱ�����
//					    	String csn = ss_csz[j];
//					    	String[] ss_pz = csn.split("#");
//					        //���ܱ����
//					    	String pz1 = ss_pz[0];//���ܱ����(2�ֽ�)
//					        //���ܱ������������
//					    	String pz2 = ss_pz[1];//���ܱ������������(2�ֽ�)
//					        //ͨ������
//					        String pz3 = ss_pz[2];//ͨ������
//					        //���ܱ������˿ں�
//					        String pz4 = ss_pz[3];//�˿ں�
//					        //���ܱ�������Լ���ͱ��
//					        String pz5 = ss_pz[4];//��Լ����(1�ֽ�) 
//					        //���ܱ�ͨ�ŵ�ַ
//					        String pz6 = ss_pz[5];//ͨ�ŵ�ַ(6�ֽڣ���ʽ12)
//					        //���ܱ�ͨ������
//					        String pz7 = ss_pz[6];//ͨ������(6�ֽڣ�BIN)
//					        //���ܷ��ʸ���
//					        String pz8 = ss_pz[7];//���ܷ��ʸ���(1�ֽ�)	
//					        //�й���������λ����
//					        String pz9 = ss_pz[8];//�й���������λ����(2λ,D3D2)
//					        //�й�����С��λ����
//					        String pz10 = ss_pz[9];//�й�����С��λ����(2λ,D1D0)
//					        //�ɼ���ͨ�ŵ�ַ
//					        String pz11 = ss_pz[10];//ͨ�ŵ�ַ(6�ֽڣ���ʽ12)					        
//					        //�û������
//					        String pz12 = ss_pz[11];//�û������(4λ,D7D6D5D4)
//					        //�û�С���
//					        String pz13 = ss_pz[12];//�û�С���(4λ,D3D2D1D0)
//					        
//					        s_sql = "select 1 from g_zdcldpzb where zdid=? and cldh=?";
//					        params = new String[]{s_zdid,pz2};
//					        List temps = jdbcT.queryForList(s_sql, params);
//					        String cldlx = "01";//����������(01:���ܱ�)
//					        if(temps==null||temps.size()==0){
//					        	//����
//					        	s_sql = "insert into g_zdcldpzb(cldlx,zdid,xh,cldh,txsl,dkh,dbgylx,txdz,txmm,dnflgs,dnzswgs,dnxswgs,cjqtxdz,yhdlh,yhxlh) "
//					        		+"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
//					        	params = new String[]{cldlx,s_zdid,pz1,pz2,pz3,pz4,pz5,pz6,pz7,pz8,pz9,pz10,pz11,pz12,pz13};
//					        }else{
//					        	//����
//					        	s_sql = "update g_zdcldpzb set cldlx=?,xh=?,txsl=?,dkh=?,dbgylx=?,"
//					        		+"txdz=?,txmm=?,dnflgs=?,dnzswgs=?,dnxswgs=?,cjqtxdz=?,"
//					        		+"yhdlh=?,yhxlh=? where zdid=? and cldh=?";
//					        	params = new String[]{cldlx,pz1,pz3,pz4,pz5,pz6,pz7,pz8,pz9,pz10,pz11,pz12,pz13,s_zdid,pz2};
//					        }
//					        jdbcT.update(s_sql, params);  
//					        
//					        cldIn += "'"+pz2+"',";
//					    }
//					    
//					    //ɾ���������ܱ�����
//		      			s_sql = "delete g_zdcldpzb "
//		      				+ "where zdid=? "
//							+ "and cldlxbm='01' ";
//		      			if(dnbNum>0){
//		      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
//		      			}
//		      	        params = new String[]{s_zdid};
//		                jdbcT.update(s_sql,params);
						
					}  else if (sjxdm.equals("AFN04F11")) {
						//�ն˵��ܱ�/��������װ�ò�������(cs1;...;csn)--N����������
						//д�ն����в������ñ�
						s_sql = "update g_zdyxcspzb set AFN04F11=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);
					} else if (sjxdm.equals("AFN04F1")) {
						//��վIP(zyip;byip;apn)
						s_sql = "update g_zdyxcspzb set AFN04F1=? "
								+"where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F3")) {
						//��վIP(zyip;byip;apn)
						s_sql = "update g_zdyxcspzb set AFN04F3=? "
								+"where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					} else if (sjxdm.equals("AFN04F4")) {
						//��վ�绰����Ͷ������ĺ���(zzdhhm;dxzxhm)
						s_sql = "update g_zdyxcspzb set AFN04F4=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F5")) {
						//���ˮ�ÿ��Ʋ�������
//						String ����ֵ(cs1;cs2;cs3;cs4;cs5)
//						 *            cs1:������������ˮ�ÿ�������ǰ�� ��λ������
//						 *            cs2:���ˮ���Զ����������־ 0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
//						 *            cs3:����������Զ����������־   0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
//						 *            cs4:����������������п���ʱ�� ��λ������
//						 *            cs5:�����������������ֹͣʱ�� ��λ������
						s_sql = "update g_zdyxcspzb set AFN04F5=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);
						
						if(null!=sjz&&sjz.length()>0){
							String sj[]=sjz.split(";");
							if("55".equalsIgnoreCase(sj[1])&&"55".equalsIgnoreCase(sj[2])){
								s_sql = "update m_station_statistics_current s set s.isauto=1 where stationid =(select stationid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
								
								s_sql = "update m_sump s set s.isauto=1 where id =(select sumpid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
								
							}else if("AA".equalsIgnoreCase(sj[1])&&"AA".equalsIgnoreCase(sj[2])){
								s_sql = "update m_station_statistics_current s set s.isauto=0 where stationid =(select stationid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
								
								s_sql = "update m_sump s set s.isauto=0 where id =(select sumpid from g_zdgz where zdid=?)";
								params = new String[] { s_zdid };
								jdbcT.update(s_sql, params);
							}
						}

					}else if (sjxdm.equals("AFN04F6")) {
						//�ն����ַ����(cs1;...;cs8)
						s_sql = "update g_zdyxcspzb set AFN04F6=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F7")) {
						//�ն�IP��ַ�Ͷ˿�(cs1;...;cs9)
						s_sql = "update g_zdyxcspzb set AFN04F7=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F9")) {
//						String ����ֵ(cs1;cs2;cs3;cs4)
//						   * 				   cs1:�����л�����1-ʹ��1��ˮ��2-ʹ��2��ˮ��3- 1��2�Ż�Ϊ��
//											   cs2:�����л�ʱ��  һ���ֽ� Сʱ
//											   cs3:����ʱ�� �����ֽ� ����
//											   cs4:ֹͣʱ��   �����ֽ� ����';
						s_sql = "update g_zdyxcspzb set AFN04F9=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);
						
						

					}else if (sjxdm.equals("AFN04F14")) {
						//�����豸��ͣ���Ʋ��� ��1��ר�ã�
//						s_sql = "update g_zdyxcspzb set AFN04F14=? "
//								+ "where zdid=?";
//						params = new String[] { sjz, s_zdid };
//						jdbcT.update(s_sql, params);
						
						s_sql = "update g_zdcldpzb set AFN04F14=? "
								+ "where zdid=? and cldh=?";
						params = new String[] { sjz.split("@")[1], s_zdid, sjz.split("@")[0] };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F37")) {
						
						s_sql = "update g_zdyxcspzb set AFN04F37=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					}else if (sjxdm.equals("AFN04F26")) {
						
						s_sql = "update g_zdyxcspzb set AFN04F26=? "
								+ "where zdid=?";
						params = new String[] { sjz, s_zdid };
						jdbcT.update(s_sql, params);

					} else if (sjxdm.equals("AFN0FF1")) {
						System.out.println("----------��������õ�ȷ�ϡ���������");
//						Decode_0F.decodeDownload(s_xzqxm, s_zddz,s_sjzfsseq, jdbcT);
				    
						
					}else if (sjxdm.equals("AFN0FF2")) {
						//�����ն˵�ַȡ�ն�ID
//						s_sql = "select fssj from g_sjzfsb where sjzfsseq=?";
//						params = new String[]{s_sjzfsseq};
//						List fssjlst = jdbcT.queryForList(s_sql, params);
//						if(fssjlst==null || fssjlst.size()==0){
//							return;
//						}
//						Date s_fssj = (Date)((Map)fssjlst.get(0)).get("fssj");
////						Date fssj=DateUtil.parse(s_fssj);
//						Long sjc=new Date().getTime()-s_fssj.getTime();
//						System.out.println(sjz+"-----"+s_fssj+"-----"+sjc);
						//�·�������֮���ȷ��֡�ظ�
						
						// �ڲ���ʱ��ǰ���£���ǰ�κż�1
						//if(sjc<new Long(overtime)){
							s_sql = "update g_zdsjpzb set sj=sysdate, dqdh=?+1,dqcd="+buffer+",zt=2 where zdid=? and (select fssj from g_sjzfsb where sjzfsseq=?)>sysdate-(6/(24*60*60))";
							params = new String[] {sjz,s_zdid ,s_sjzfsseq};
							jdbcT.update(s_sql,params);
							Decode_0F.decodeDownload(s_xzqxm, s_zddz,s_sjzfsseq, jdbcT);
						//}
						
					}

				}
			}
		} catch (Exception e) {
			throw e;
		}
	}
}