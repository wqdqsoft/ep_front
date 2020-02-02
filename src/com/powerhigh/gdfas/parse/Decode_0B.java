package com.powerhigh.gdfas.parse;

import java.sql.*;
import java.util.*;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.springframework.jdbc.core.JdbcTemplate;


import com.powerhigh.gdfas.util.*;

/**
 * Description: �������ݽ�����(�������ݲ�ѯ���ء��������������ϱ�) <p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-4-2
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class Decode_0B{
	//������־
	private static final String resource = "log4j.properties";
	private static Category cat =
	      Category.getInstance(com.powerhigh.gdfas.parse.Decode_0B.class);
//	static {
//	  PropertyConfigurator.configure(resource);
//	}
	
	public Decode_0B(){
		
	}
	  
	  /**�����������������ݴ���
	   * @param con Connection ���ݿ�����
       * @param rwsxlx String �����������ͣ�1����ѯ���أ�2�������ϱ�
       * @param s_sjzfsseq String ����֡��������
       * @param xzqxm String ���������� 
       * @param zddz String �ն˵�ַ
       * @param rwlx String ��������(1:����������2��2����������)  
       * @param rwh String �����
       * @param s_csdata String ������������CS������  
       * @param aux_time String ���б���ʱ���ǩ
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
				//�������ݲ�ѯ
				//ȡ"������������ϸ����"
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
			//ʱ���ǩ(6�ֽ�) and �¼�������(2�ֽ�)
			DADT = s_csdata.substring(16,s_csdata.length()-16);
		}else if(s_tpv.equals("1")&&s_acd.equals("0")){
			//ʱ���ǩ(6�ֽ�)
			DADT = s_csdata.substring(16,s_csdata.length()-12);
		}else if(s_tpv.equals("0")&&s_acd.equals("1")){
			//�¼�������(2�ֽ�)
			DADT = s_csdata.substring(16,s_csdata.length()-4);
		}else if(s_tpv.equals("0")&&s_acd.equals("0")){
			//�޸�����Ϣ
			DADT = s_csdata.substring(16);
		}
		//System.out.println("[taskDecode]DADT:"+DADT);
		cat.info("[taskDecode]DADT:"+DADT);
		
		//DADT���±�
		int index = 0;
		String main_dadt = DADT.substring(index, index+8); 
		index += 8;
		
		//��Ϣ��Pn
		String main_da = main_dadt.substring(0,4);
		main_da = Util.tranDA(Util.convertStr(main_da));	     
		//��Ϣ��Fn
		String main_dt = main_dadt.substring(4,8);
		main_dt = Util.tranDT(Util.convertStr(main_dt));
		//System.out.println("[taskDecode]main_dadt:"+"P"+main_da+"F"+main_dt);
		cat.info("[taskDecode]main_dadt:"+"P"+main_da+"F"+main_dt);
	      
	    //�����
	  	String rwh = main_da;      		
	  	//��������(1:����������2��2����������) 
	  	String rwlx = main_dt;
	  	
	  	Map mp = null;
	  	if(rwlx.equals("1")){
	  		//1����������
	  		mp = decodeType1(s_zdid,DADT.substring(index),jdbcT);
	  	}else if(rwlx.equals("2")){
	  		//2����������
	  		mp = decodeType2(DADT.substring(index));
	  	}
		Collection coll = mp.values();
		Object[] datas = coll.toArray();
		// �����������ͣ�1����ѯ���أ�2�������ϱ�
		if(rwsxlx.equals("1")){
			//�������ݲ�ѯ����(д"�ٲ����ݷ��ر�")
			for(int i=0;i<datas.length;i++){
				Vector vt = (Vector)datas[i];
				for(int j=0;j<vt.size();j++){
					//��š���������롢����ֵ������ʱ�䡢��Ϣ�������Ϣ��š���־(1:д��������)
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
						xxdmc = "�ն�";
					}else if(xxdlb.equals("1")){
						xxdmc = "������"+xxdh;
					}else if(xxdlb.equals("2")){
						xxdmc = "�ܼ���"+xxdh;
					}else if(xxdlb.equals("3")){
						xxdmc = "ֱ��ģ����"+xxdh;
					}
					
					s_sql = "insert into g_zcsjfhb(sjzfsmxseq,sjxdm,sjz,xxdmc,xh,sjsj) "
		  	            + "values(?,?,?,?,?,?)";
					params = new String[]{fsmxxl,sjxdm,sjz,
							xxdmc,xh,sjsj};
					jdbcT.update(s_sql,params);
				}
			}
			
		}else if(rwsxlx.equals("2")){
			//�������������ϱ�(ִ�д洢����sp_saveautotaskdata) 
			Vector sp_param = new Vector();
			//1������������
			sp_param.addElement(xzqxm);
			//2���ն˵�ַ
			sp_param.addElement(zddz);
			
			//3�������
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
			
			//5������
			sp_param.addElement(array);

			cat.info("[taskDecode]array:"+array);

			Util.executeProcedure(jdbcT,"sp_saveautotaskdata",sp_param,2);
		}
		
	  }catch(Exception e){
		throw e;
	  }
	}
	  
	//1�������������
	private static Map decodeType1(String s_zdid,String DADT,JdbcTemplate jdbcT) throws Exception{
		return Decode_0C.decode(s_zdid,DADT,jdbcT);
	}
	  
	//2�������������
	private static Map decodeType2(String DADT) throws Exception{
		return Decode_0D.decode(DADT);		
	}
		
	  
}