package com.powerhigh.gdfas.parse;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.util.Util;

/**
 * Description: AFN=0A(�ն���Ӧ������ѯ�����������ش���) <p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-4-2
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class Decode_0A{
	//������־
	@SuppressWarnings("unused")
	private static final String resource = "log4j.properties";
	private static Category cat =
	Category.getInstance(com.powerhigh.gdfas.parse.Decode_0A.class);
//	static {
//	   PropertyConfigurator.configure(resource);
//	}
	
	public Decode_0A(){
		
	}	

	public static void dispose(String s_xzqxm,String s_zddz,
					String sSJZ,String s_tpv,String s_acd,String s_csdata,
					String s_sjzfsseq,JdbcTemplate jdbcT) 
				throws Exception{
		String s_sql = "";
		String[] params = null;
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
		
		String zt = "01";
		try{
			decode(s_sjzfsseq,s_xzqxm,s_zddz,DADT,jdbcT);
		}catch(Exception e){
			zt = "03";
//			e.printStackTrace();
			cat.error("[Decode_0A]ERROR:",e);
		}
		
		//�޸ġ����������ͱ���״̬��־
		if(null!=s_sjzfsseq){
			 s_sql = "update g_sjzfsb set zt=?,fhsj=sysdate,sxsjz=? "
						+ "where sjzfsseq=?";
			        params = new String[]{zt,sSJZ,s_sjzfsseq};
			          jdbcT.update(s_sql,params);
		}
	   
	}
	
	@SuppressWarnings({ "rawtypes",  "unused" })
	private static void decode(String s_sjzfsseq,String xzqxm,String zddz,String DADT,JdbcTemplate jdbcT) 
				throws Exception{
		
		int idx_dadt = 0;
		String s_dadt = "";
		String s_da = "";//��Ϣ��Pn
		String s_dt = "";//��Ϣ��Fn
		String s_PF = "";//PnFn
      	String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
      	String s_sql = "";
      	String[] params = null;
      	cat.info("DTDT:"+DADT );
//      	System.out.println("DADT:"+DADT);
//      	System.out.println("step1: 0A");
      	while(idx_dadt<DADT.length()){
      		//------------------ÿ��PnFn-----------------
      		s_dadt = DADT.substring(idx_dadt, idx_dadt+8);
    		idx_dadt += 8;
    		
          	//��Ϣ��Pn
          	s_da = s_dadt.substring(0,4);
          	s_da = Util.tranDA(Util.convertStr(s_da));
          	String s_Pda = "P" + s_da;
          	//��Ϣ��Fn
          	s_dt = s_dadt.substring(4,8);
          	s_dt = Util.tranDT(Util.convertStr(s_dt));
          	String s_Fdt = "F" + s_dt;
          	//PnFn
          	s_PF = s_Pda + s_Fdt;

//          	System.out.println("step2: "+s_Fdt);
          	
          	if(s_Fdt.equals("F1")){
          		//F1:�ն�ͨ�Ų�����ѯ����
          		cat.info("[Decode_0A]F1:�ն�ͨ�Ų�����ѯ����");
          		//����ֵ(cs1;cs2;cs3;cs4;cs5)
//          		 cs1:��������ʱʱ��,��λ:20ms
//          		 cs2:�ն�ͨ��ģ����ź�ǿ��,��λ:db 
//          		 cs3:�ն��������������65535��,��λ:��
//          		 cs4:���� Ĭ��FF
//          		 cs5:��������:1-60��
          		
          		String csz = "";
          		
          		//��������ʱʱ��,��λ:20ms
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 = Util.hexStrToDecStr(cs1);
      			
      			//�ն�ͨ��ģ����ź�ǿ��,��λ:db 
      			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;      			
      			cs2=Util.tranFormat04(Util.convertStr(cs2))[0];
      	        
      			
      			//�ն��������������65535��,��λ:��
      			String cs3 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs3= Util.hexStrToDecStr(Util.convertStr(cs3));
      			
      			//���� Ĭ��FF
      			String cs4=DADT.substring(idx_dadt,idx_dadt+2);;
      			idx_dadt += 2;
      			cs4= Util.hexStrToDecStr(Util.convertStr(cs4));
      			
      			//��������:1-60��
      			String cs5 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs5 = Util.hexStrToDecStr(cs5);
      			

      			csz = cs1+";"+cs2+";"+cs3+";"
      				+cs4+";"+cs5;
      			
      	        //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F1=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
                
                //д�ն����в������ñ�
      			s_sql = "update g_zdgz set xhqd=? where zdid=?";      			
      	        params = new String[]{cs2,zdid};
                jdbcT.update(s_sql,params);
                
                //д�ն����в������ñ�
      			s_sql = "insert into G_ZDTXZTJLB(id,zdid,jlsj,zhtxsj,xhqd) values(s_zdtxzt.nextval,?,sysdate,sysdate,?)";      			
      	        params = new String[]{zdid,cs2};
                jdbcT.update(s_sql,params);
                
          	}else if(s_Fdt.equals("F3")){
          		//F3:��վIP��ַ��ѯ����
          		//����ֵ(cs1;cs2;cs3)
          	    // 	cs1:����IP(xxx.xxx.xxx.xxx:nnnnn)
          	    //	cs2:����IP(xxx.xxx.xxx.xxx:nnnnn)
          	    //	cs3:APN(16�ֽڣ�ASCII;��λ��00H��������)
          		cat.info("[Decode_0A]F3:��վIP��ַ��ѯ����");
          		String csz = "";
          		String temps = "";
          		//��վ����IP
          		String cs1 = "";
          		temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 += Util.hexStrToDecStr(temps)+".";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 += Util.hexStrToDecStr(temps)+".";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 += Util.hexStrToDecStr(temps)+".";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 += Util.hexStrToDecStr(temps)+":";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs1 += Util.hexStrToDecStr(Util.convertStr(temps));
      			
      			//��վ����IP
          		String cs2 = "";
          		temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs2 += Util.hexStrToDecStr(temps)+".";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs2 += Util.hexStrToDecStr(temps)+".";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs2 += Util.hexStrToDecStr(temps)+".";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs2 += Util.hexStrToDecStr(temps)+":";
      			
      			temps = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs2 += Util.hexStrToDecStr(Util.convertStr(temps));
      			
      			//APN
      			String cs3 = "";
      			temps = DADT.substring(idx_dadt,idx_dadt+32);
      			idx_dadt += 32;      			
      			String temp_cs1 = "";
      			for(int i=0;i<16;i++){
      				String s = temps.substring(i*2,(i+1)*2);
      				if(s.equals("00")){
      					break;
      				}
      				temp_cs1 += s;
      			}
      			byte[] bt = Util.strstobyte(temp_cs1);
      			cs3 = Util.getASCII(bt); 
      			
      			csz = cs1+";"+cs2+";"+cs3;
      			
      	        //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F3=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F4")){
          		//F4:����ػ���������ѯ����
//          		����ػ���������cs1;cs2;......cs6��
//          		cs1:�������� 1. A2/O����2. ΢��̬�˴�  3. ����������˳�
//          		cs2:�����ģ
//          		cs3:����
//          		cs4:���������
//          		cs5:��������(ˮ������) ����վ�����������������������������Ϊ׼��
//          		cs6:�ȵ���Сʱ��
//          		cs7:��Ƚ���ģʽʹ�� 0x55 ��ʹ�ܣ� 0xaa ����ʹ��
//          		cs8��һ�������ڳ�/�ռ��ظ�������������������������0x55:�������ƣ� 0xaa:��������
//          		cs9:����ˮλ���ޣ�ˮλ��ȣ�
//          		cs10:����ˮλ���ޣ�ˮλ��ȣ�
//          		cs11:Ŀ��ORP��ֵ��Χ����
//          		cs12:Ŀ��ORP��ֵ��Χ����
//          		cs13:ˮλ��ⷽʽ 0��������ƣ�1��������ˮλ����
          		cat.info("[Decode_0A]F4:����ػ���������ѯ����");
          		
                String csz = "";
          		
                s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
        		params = new String[] { xzqxm,zddz };
        	    List cldList = jdbcT.queryForList(s_sql, params);
        	    Map cldMap = (Map) cldList.get(0);
        	    // �ն��ͺ�
        	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
        	 	
        	 	if("1".equalsIgnoreCase(zdxh)){
        	 		//��������
              		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs1 = Util.hexStrToDecStr(cs1);
          			
          			//�����ģ ��/��
          			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs2=Util.tranFormat04(cs2)[0];
          			
          		    //����
          			String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs3=Util.tranFormat22(cs3);
          			
          			//һ�������ڳ�/�ռ��ظ�������
          			String cs8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs8=Util.hexStrToDecStr(cs8);
          			
          			 //����ˮλ���ޣ�����嶥�����룩
          			String cs9 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs9=Util.tranFormat22(cs9);
          			
          			 //����ˮλ���ޣ�����嶥�����룩
          			String cs10 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs10=Util.tranFormat22(cs10);
          			
          			 //Ŀ��ORP��ֵ��Χ����
          			String cs11 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			String cs11str[]=Util.tranFormat28(cs11);
          			String fh="";
          			if("1".equalsIgnoreCase(cs11str[1])){
          				fh="-";
          			}
          			cs11=fh+cs11str[0];
          			
          			 //Ŀ��ORP��ֵ��Χ����
          			String cs12 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;      			
          			String cs12str[]=Util.tranFormat28(cs12);
          			fh="";
          			if("1".equalsIgnoreCase(cs12str[1])){
          				fh="-";
          			}
          			cs12=fh+cs12str[0];
          			
          			 //ˮ�ÿ����ж�����
          			String cs13 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;   
          			cs13=Util.hexStrToDecStr(cs13);

          			csz = cs1+";"+cs2+";"+cs3+";"+cs8+";"+cs9+";"+cs10+";"+cs11+";"+cs12+";"+cs13;
          			
          			//�����ն�
        	 	}else{
        	 		//��������
              		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs1 = Util.hexStrToDecStr(cs1);
          			
          			//�����ģ ��/��
          			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs2=Util.tranFormat04(cs2)[0];
          			
          		    //����
          			String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs3=Util.tranFormat22(cs3);
          			
          			//���������
              		String cs4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs4 = Util.hexStrToDecStr(cs4);
          			
          			//��������(ˮ������) ����վ�����������������������������Ϊ׼��
              		String cs5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs5 = Util.hexStrToDecStr(cs5);
          			
          			//�ȵ���Сʱ��
              		String cs6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs6 = Util.hexStrToDecStr(cs6);
          			
          			//��Ƚ���ģʽʹ�� 0x55 ��ʹ�ܣ� 0xaa ����ʹ��
              		String cs7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
//          			cs7 = Util.hexStrToDecStr(cs7);
          			
          			
          			 //����������0x55:�������ƣ� 0xaa:��������
          			String cs8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			//cs8=Util.hexStrToDecStr(cs8);
          			
//          			 //����ˮλ���ޣ�����嶥�����룩
//          			String cs9 = DADT.substring(idx_dadt,idx_dadt+2);
//          			idx_dadt += 2;      			
//          			cs9=Util.tranFormat22(cs9);
//          			
//          			 //����ˮλ���ޣ�����嶥�����룩
//          			String cs10 = DADT.substring(idx_dadt,idx_dadt+2);
//          			idx_dadt += 2;      			
//          			cs10=Util.tranFormat22(cs10);
//          			
//          			 //Ŀ��ORP��ֵ��Χ����
//          			String cs11 = DADT.substring(idx_dadt,idx_dadt+4);
//          			idx_dadt += 4;
//          			String cs11str[]=Util.tranFormat28(cs11);
//          			String fh="";
//          			if("1".equalsIgnoreCase(cs11str[1])){
//          				fh="-";
//          			}
//          			cs11=fh+cs11str[0];
//          			
//          			 //Ŀ��ORP��ֵ��Χ����
//          			String cs12 = DADT.substring(idx_dadt,idx_dadt+4);
//          			idx_dadt += 4;      			
//          			String cs12str[]=Util.tranFormat28(cs12);
//          			fh="";
//          			if("1".equalsIgnoreCase(cs12str[1])){
//          				fh="-";
//          			}
//          			cs12=fh+cs12str[0];
          			
          			 //ˮ�ÿ����ж�����
          			String cs13 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;   
          			cs13=Util.hexStrToDecStr(cs13);

          			csz = cs1+";"+cs2+";"+cs3+";"
          				+cs4+";"+cs5+";"+cs6+";"+cs7+";"+cs8+";"+cs13;
        	 	}
        	 	
        	 	
          		
      			
      	        //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F4=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F5")){
          		//F5:���ˮ�ÿ��Ʋ�����ѯ����
//          		���ˮ�ÿ��Ʋ���(cs1;cs2;cs3;cs4;))
//          		*cs1:������������ˮ�ÿ�������ǰ��
//          		*cs2:���ˮ���Զ����������־   0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
//          		*cs3:����������Զ����������־    0x55�����Զ����ƣ�0xAA��ֹ�Զ�����
//          		*cs4:����������������п���ʱ��
//          		*cs5:�����������������ֹͣʱ��
          		cat.info("[Decode_0A]F5:���ˮ�ÿ��Ʋ�����ѯ����");
          		
          		String csz = "";

                s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
        		params = new String[] { xzqxm,zddz };
        	    List cldList = jdbcT.queryForList(s_sql, params);
        	    Map cldMap = (Map) cldList.get(0);
        	    // �ն��ͺ�
        	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
        	 	
        	 	
          		
          	    //������������ˮ�ÿ�������ǰ��
      			String cs1 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			if("EEEE".equalsIgnoreCase(cs1)){
      				cs1="��Ч";
      			}else{
      				if("1".equalsIgnoreCase(zdxh)){
      					cs1 = Util.hexStrToDecStr(Util.convertStr(cs1));
      				}else{
      					cs1=Util.tranFormat08(cs1);
      				}
      			    
      			}
      			
      			//���ˮ���Զ����������־
          		String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
//      			cs2 = Util.hexStrToDecStr(cs2);
          		
          		//����������Զ����������־
          		String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
//      			cs3 = Util.hexStrToDecStr(cs3);

      			//����������������п���ʱ��
      			String cs4 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;      			
      			if("EEEE".equalsIgnoreCase(cs4)){
      				cs4="��Ч";
      			}else{
//      			    cs4=Util.tranFormat08(cs4);
      				cs4=Util.hexStrToDecStr(Util.convertStr(cs4));
      			}
      			
      		    //�����������������ֹͣʱ��
      			String cs5 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;      			
      			if("EEEE".equalsIgnoreCase(cs5)){
      				cs5="��Ч";
      			}else{
//      			    cs5=Util.tranFormat08(cs5);
      				cs5=Util.hexStrToDecStr(Util.convertStr(cs5));
      			}
      			
      			csz = cs1+";"+cs2+";"+cs3+";"+cs4+";"+cs5;
      			
      	        //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F5=? where zdid=?";
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F6")){
          		//F6:�Ž�������������ѯ����
          		//����ֵ(cs1;...;cs8)
//	          	   cs1:ˢ����֤�����Чʱ��
//	          	   cs2:��������ʱ��
//	          	   cs3:�ƹⱨ��ʱ��
          		cat.info("[Decode_0A]F6:�Ž�������������ѯ����");
          		
          		String csz = "";
          		
          		//ˢ����֤�����Чʱ��
      			String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			if(!"FF".equalsIgnoreCase(cs1)){
      				cs1=Util.tranFormat04(cs1)[0];
      				cs1=String.valueOf(Integer.parseInt(cs1));
      			}else{
      				cs1="��Ч";
      			}
      			
      			
      			//��������ʱ��
      			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2; 
      			if(!"FF".equalsIgnoreCase(cs2)){
      				cs2=Util.tranFormat04(cs2)[0];
      				cs2=String.valueOf(Integer.parseInt(cs2));
      			}else{
      				cs2="��Ч";
      			}
      			
      			
      			//�ƹⱨ��ʱ��
      			String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2; 
      			if(!"FF".equalsIgnoreCase(cs3)){
      				cs3=Util.tranFormat04(cs3)[0];
      				cs3=String.valueOf(Integer.parseInt(cs3));
      			}else{
      				cs3="��Ч";
      			}
      			
      			
      			csz = cs1+";"+cs2+";"+cs3;
      			
      	        //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F6=? where zdid=?";
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F7")){
          		//F7: ��Ƶ�����кſ��ѯ����
          		cat.info("[Decode_0A]F7: ��Ƶ�����кſ��ѯ����");
          		
          	   
          		String csz = "";
          		
          		//ID�����кŸ���
          		String idnum = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			idnum= Util.hexStrToDecStr(Util.convertStr(idnum));
      			
      			int i_idnum=Integer.parseInt(idnum);
      			
      		    //N ��ID��
          		for(int m=0;m<i_idnum;m++){
          			String idn = DADT.substring(idx_dadt,idx_dadt+14);
          			idx_dadt += 14;
          			idn=Util.convertStr(idn);
          			if(m==i_idnum-1){
          				csz += Util.hexStrToDecStr(idn) ;
          			}else{
          				csz += Util.hexStrToDecStr(idn)+";";
          			}
          			
          		}
      			
      	        //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F7=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F8")){
          		//F8:�����ֻ������ѯ����
          		//����ֵ�����ֻ����루cs1;cs2;cs3;cs4��
//          		cs1:�ռ��ظ���n
//          		cs2:�������ĺ���
//          		cs3:���ڳ�uim����
//          		cs4(����1,����2,.........����n)
          		cat.info("[Decode_0A]F8:�����ֻ������ѯ���ز�ѯ����");
          		String temps = "";
          		int index = 0;
          		String csz = "";
          		
          	    //�ռ��ظ���n
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 = Util.hexStrToDecStr(cs1);
      			
      			//�������ĺ���
      			String cs2 = "";
          		temps = DADT.substring(idx_dadt,idx_dadt+16);
      			idx_dadt += 16;
      			index = temps.indexOf("F");
      			if(index == -1){
      				cs2 = temps;
      			}else{
      				cs2 = temps.substring(0,index);
      			}
      			
      		    //���ڳ�uim����
      			String cs3 = "";
          		temps = DADT.substring(idx_dadt,idx_dadt+16);
      			idx_dadt += 16;
      			index = temps.indexOf("F");
      			if(index == -1){
      				cs3 = temps;
      			}else{
      				cs3 = temps.substring(0,index);
      			}
      			
      			String cs4="";
      			int i_n=Integer.parseInt(cs1);
      			
      		    //N���ռ��ص�uim����(����1,����2,.........����n)
          		for(int m=0;m<i_n;m++){
          			String idn = DADT.substring(idx_dadt,idx_dadt+16);
          			String sub_cs4="";
          			idx_dadt += 16;
          			index = idn.indexOf("F");
          			if(index == -1){
          				sub_cs4 += idn;
          			}else{
          				sub_cs4 += idn.substring(0,index);
          			}
          			if(m==i_n-1){
          				cs4 += sub_cs4;
          			}else{
          				cs4 += sub_cs4+",";
          			}
//          			
//          			
//          			cs4 += idn+",";
          		}
      			
      			
      			
      			csz = cs2+";"+cs3+";"+cs4;
      			
      			s_sql = "update g_zdyxcspzb set AFN04F8=? where zdid=?";
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      			
          	}else if(s_Fdt.equals("F9")){
          		//F9:ˮ��ˮλ���Ʋ���������ռ��������նˣ�
//				String ����ֵ(cs1;cs2;cs3;cs4)
//				   * 				   cs1:�����л�����1-ʹ��1��ˮ��2-ʹ��2��ˮ��3- 1��2�Ż�Ϊ��
//									   cs2:�����л�ʱ��  һ���ֽ� Сʱ
//									   cs3:����ʱ�� �����ֽ� ����
//									   cs4:ֹͣʱ��   �����ֽ� ����';
          		cat.info("[Decode_0A]F9:ˮ��ˮλ���Ʋ���������ռ��������նˣ�");
          		
          		String csz = "";
          		
          		//�����л�����
      			String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			if(!"EE".equalsIgnoreCase(cs1)){
      				cs1 = Util.hexStrToDecStr(Util.convertStr(cs1));
      			}
      			
      			
      			//�����л�ʱ��
      			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			if(!"EE".equalsIgnoreCase(cs2)){
      				if("CC".equalsIgnoreCase(cs2)){
      					cs2="0";
      				}else
      					cs2 = Util.hexStrToDecStr(Util.convertStr(cs2));
      			}
      			
      			
      		    //15������ʱ��
  				String cs3 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			if(!"EEEE".equalsIgnoreCase(cs3)){
      				if("CCCC".equalsIgnoreCase(cs3)){
      					cs3="";
      				}else{
      					cs3 = Util.hexStrToDecStr(Util.convertStr(cs3));
      					cs3 = String.valueOf(Integer.parseInt(cs3));
      				}
      				
      			}
      			
      		    //16��ֹͣʱ��
  				String cs4 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			if(!"EEEE".equalsIgnoreCase(cs4)){
      				if("CCCC".equalsIgnoreCase(cs4)){
      					cs4="";
      				}else{
      					cs4 = Util.hexStrToDecStr(Util.convertStr(cs4));
      					cs4 = String.valueOf(Integer.parseInt(cs4));
      				}
      				
      			}
      			
      			
      			csz = cs1+";"+cs2+";"+cs3+";"+cs4;
      			
      	        //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F9=? where zdid=?";
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F10")){
          		//F10:�ն˲��������ò�����ѯ����
          		cat.info("[Decode_0A]F10:�ն˲��������ò�����ѯ����");
          		
          		//����ֵ(cs1;...;csn)--N�����ܱ�����
          	   	//csn:���ܱ�����(pz1#...#pz13)
          		//pz1:���ܱ����
          		//pz2:����������
          		//pz3:ͨ������(0-7)
          		//pz4:�˿ں�(1-31)
          		//pz5:��Լ����(0-255)
          		//pz6:ͨѶ��ַ
          		//pz7:ͨѶ����
          		//pz8:���ʸ���(1-48)
          		//pz9:����λ����[4-7]
          		//pz10:С��λ����[1-4]
          		//pz11:�����ɼ���ͨ�ŵ�ַ
          		//pz12:�����û������[0-15]
          		//pz13:�����û�С���[0-15]
          		
          		//��"�ն˲��������ñ�"ȡ���ܱ�������Ϣ(cldlx=01)
        	    String cldlx = "01";//����������(01:���ܱ�)
          		s_sql = "select cldh from g_zdcldpzb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1�����
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2���������
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3���豸����
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			
          			//4��ͨ�Ź�Լ
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			
          			//5��ͨ������
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
          			
          			
          			//6��ͨ�ŵ�ַ
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+12);
          			idx_dadt += 12;
          			pz6 = Util.convertStr(pz6);
          			pz6 = String.valueOf(Long.parseLong(pz6));
          			
          			csz += pz1+"#"+pz2+"#"+pz3+"#"+pz4+"#"
          				  +pz5+"#"+pz6+";";
          			
          			boolean isIn = false;
          			for(int j=0;j<dnbList.size();j++){
          				Map tempHM = (Map)dnbList.get(j);
          				if(String.valueOf(tempHM.get("cldh")).equals(pz2)){
          					isIn = true;
          					break;
          				}
          			}
          			
          			//д"�ն˲��������ñ�"
          			if(isIn == true){
          				//��վ���иò����������
          				s_sql = "update g_zdcldpzb set xh=?,cldlx=?,gylx=?,txsl=?,txdz=?";
        	        	params = new String[]{pz1,pz3,pz4,pz5,pz6,zdid,pz2};
          			}else if(isIn == false){
          				//��վ���޸ò����������
          				s_sql = "insert into g_zdcldpzb(id,zdid,cldh,xh,cldlx,gylx,txsl,txdz) "
        	        		+"values(SEQ_CLDID.nextVal,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz2,pz1,pz3,pz4,pz5,pz6};
          			}
          			jdbcT.update(s_sql,params);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      			
      			//ɾ�����������������
      			s_sql = "delete g_zdcldpzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params);  
          	}else if(s_Fdt.equals("F12")){
          		//F12:�������������豸װ�����ò���
          		cat.info("[Decode_0A]F12:�������������豸װ�����ò���");
          		
          		//��ʼ��g_zdgz���е�p_in
          		String param="0000000000000000";
          		
          		s_sql = "select cldh from g_zdkglsrsbpzb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1�����
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2���������
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3���豸����
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			
          			//4��Ӳ������ӿں�
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			
          			//5���豸���������
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
          			
          			
          		    //6���豸�����ڱ��
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
          			
          			csz += pz1+"#"+pz2+"#"+pz3+"#"+pz4+"#"
          				  +pz5+"#"+pz6+";";
          			
          			boolean isIn = false;
          			for(int j=0;j<dnbList.size();j++){
          				Map tempHM = (Map)dnbList.get(j);
          				if(String.valueOf(tempHM.get("cldh")).equals(pz2)){
          					isIn = true;
          					break;
          				}
          			}
          			
          			//д"�ն˲��������ñ�"
          			if(isIn == true){
          				//��վ���иò����������
          				s_sql = "update g_zdkglsrsbpzb set sblx=?,yjsrjkh=?,sbcszxh=?,sbznbh=? where zdid=? and cldh=?";
        	        	params = new String[]{pz3,pz4,pz5,pz6,zdid,pz2};
          			}else if(isIn == false){
          				//��վ���޸ò����������
          				String sbmc="Ĭ���豸"+pz1;
          				if("1".equalsIgnoreCase(pz3)){
          					sbmc="����"+pz1;
          				}else if("3".equalsIgnoreCase(pz3)){
          					sbmc="Զ��/�͵��ź�"+pz1;
          				}
          				s_sql = "insert into g_zdkglsrsbpzb(id,zdid,xh,cldh,sbmc,sblx,yjsrjkh,sbcszxh,sbznbh) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,sbmc,pz3,pz4,pz5,pz6};
          			}
          			jdbcT.update(s_sql,params);
          			
          			param=param.substring(0, Integer.parseInt(pz4)-1)+"1"+param.substring(Integer.parseInt(pz4), 16);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      		    //��ѯ���ն�g_zddqsbpzb�����ÿһ·�����������  ���յó����յ�������
      			s_sql = "select * from g_zddqsbpzb where zdid=?";
      	        params = new String[]{zdid};
          		List zdList = jdbcT.queryForList(s_sql,params);
          		
          		for(int j=0;j<zdList.size();j++){
      				Map tempHM = (Map)zdList.get(j);
      				String fzcd_pin=String.valueOf(tempHM.get("fzcdjkh"));
      				if(!"CC".equalsIgnoreCase(fzcd_pin)){
						param=param.substring(0, Integer.parseInt(fzcd_pin)-1)+"1"+param.substring(Integer.parseInt(fzcd_pin), 16);
					}
      				String gzd_pin=String.valueOf(tempHM.get("gzdyjjkh"));
      				if(!"CC".equalsIgnoreCase(gzd_pin)){
						param=param.substring(0, Integer.parseInt(gzd_pin)-1)+"1"+param.substring(Integer.parseInt(gzd_pin), 16);
					}
      			}
      			
      			//ɾ�����������������
      			s_sql = "delete g_zdkglsrsbpzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                //����g_zdgz�е�pin�ֶ� ����Ч��·����1
                s_sql="update g_zdgz set pin=? where zdid=?";
                params = new String[]{param,zdid};
                jdbcT.update(s_sql,params); 
                
          	}else if(s_Fdt.equals("F13")){
          		//F13:�����豸���Ƶ������ѯ����
          		cat.info("[Decode_0A]F13:�����豸���Ƶ����");
          		
          		//��ʼpin��g_zdgz���е�p_in
          		String pin="0000000000000000";
          	    //��ʼ��pout��g_zdgz���е�p_out
          		String pout="00000000";
          		
          		s_sql = "select cldh from g_zddqsbpzb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1�����
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2���������
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3���豸����
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			
          			//�豸����
          			String sbmc="Ĭ���豸";
          			if("1".equalsIgnoreCase(pz3)){
          				sbmc="ˮ��"+pz2;
          			}else if("2".equalsIgnoreCase(pz3)){
          				sbmc="���"+pz2;
          			}else if("3".equalsIgnoreCase(pz3)){
          				sbmc="��ŷ�"+pz2;
          				if("5".equalsIgnoreCase(pz2)){
          					sbmc="������";
          				}
          				if("6".equalsIgnoreCase(pz2)){
          					sbmc="���ᷧ";
          				}
          				
          			}else if("4".equalsIgnoreCase(pz3)){
          				sbmc="ɢ����"+pz2;
          			}else if("5".equalsIgnoreCase(pz3)){
          				sbmc="�������"+pz2;
          			}
          			
          		    //4�������
          			String pz4 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			pz4 = String.valueOf(Integer.parseInt(pz4));
          			
          			//5����������
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
      				
          		    //6�����߷�ʽ
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
          			
          		    //7��Ӳ������ӿں�
      				String pz7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz7 = Util.hexStrToDecStr(Util.convertStr(pz7));
          			
          		    //8����������ӿں�
      				String pz8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"CC".equalsIgnoreCase(pz8)){
          				pz8 = Util.hexStrToDecStr(Util.convertStr(pz8));
          			}
          			
          		    //9�����ϵ�Ӳ���ӿں�
      				String pz9 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"CC".equalsIgnoreCase(pz9)){
          				pz9 = Util.hexStrToDecStr(Util.convertStr(pz9));
          			}
          			    
          			
          		    //10����Ƶ�����Ƶ���
      				String pz10 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			
          			
          			csz += pz1+"#"+pz2+"#"+pz3+"#"+pz4+"#"
          				  +pz5+"#"+pz6+"#"+pz7+"#"+pz8+"#"+pz9+"#"+pz10+";";
          			
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
          				s_sql = "update g_zddqsbpzb set sblx=?,edgl=?,clnl=?,jxfs=?,yjscjkh=?,fzcdjkh=?,gzdyjjkh=?,sfbp=? where zdid=? and cldh=?";
        	        	params = new String[]{pz3,pz4,pz5,pz6,pz7,pz8,pz9,pz10,zdid,pz2};
          			}else if(isIn == false){
          				//��վ���޸ò����������
          				s_sql = "insert into g_zddqsbpzb(id,zdid,xh,cldh,sbmc,sblx,edgl,clnl,jxfs,yjscjkh,fzcdjkh,gzdyjjkh,sfbp) "
//        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,34734,5,5,3,200,5,3,6,5,null,'AA')";
          						+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,sbmc,pz3,pz4,pz5,pz6,pz7,pz8,pz9,pz10};
          			}
          			jdbcT.update(s_sql,params);
          			
          			pout=pout.substring(0, Integer.parseInt(pz7)-1)+"1"+pout.substring(Integer.parseInt(pz7), 8);
          			
//          			if("".equalsIgnoreCase(pz8)){
//						pin=pin.substring(0, Integer.parseInt(pz8)-1)+"0"+pin.substring(Integer.parseInt(pz8), 16);
//					}else{
//						pin=pin.substring(0, Integer.parseInt(pz8)-1)+"1"+pin.substring(Integer.parseInt(pz8), 16);
//					}
          			if(!"CC".equalsIgnoreCase(pz8)){
          				pin=pin.substring(0, Integer.parseInt(pz8)-1)+"1"+pin.substring(Integer.parseInt(pz8), 16);
					}
          			if(!"CC".equalsIgnoreCase(pz9)){
          				pin=pin.substring(0, Integer.parseInt(pz9)-1)+"1"+pin.substring(Integer.parseInt(pz9), 16);
					}
//          			if("".equalsIgnoreCase(pz9)){
//						pin=pin.substring(0, Integer.parseInt(pz9)-1)+"0"+pin.substring(Integer.parseInt(pz9), 16);
//					}else{
//						pin=pin.substring(0, Integer.parseInt(pz9)-1)+"1"+pin.substring(Integer.parseInt(pz9), 16);
//					}
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      			
      			//��ѯ���ն�g_zdkglsrsbpzb�����ÿһ·�����������  ���յó����յ�������
      			s_sql = "select * from g_zdkglsrsbpzb where zdid=?";
      	        params = new String[]{zdid};
          		List zdList = jdbcT.queryForList(s_sql,params);
          		
          		for(int j=0;j<zdList.size();j++){
      				Map tempHM = (Map)zdList.get(j);
      				String kgl_pin=String.valueOf(tempHM.get("yjsrjkh"));
      				if(!"CC".equalsIgnoreCase(kgl_pin)){
						pin=pin.substring(0, Integer.parseInt(kgl_pin)-1)+"1"+pin.substring(Integer.parseInt(kgl_pin), 16);
					}
      			}
      			
      			//ɾ����������
      			s_sql = "delete g_zddqsbpzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                //����g_zdgz�е�pin�ֶ� ����Ч��·����1
                s_sql="update g_zdgz set pin=?,pout=? where zdid=?";
                params = new String[]{pin,pout,zdid};
                jdbcT.update(s_sql,params); 
                
          	}else if(s_Fdt.equals("F14")){
          		
          		//F14�������豸��ͣ���Ʋ��� ��1��ר�ã����ò�ѯ����
          		cat.info("[Decode_0A]F14�������豸��ͣ���Ʋ��� ��1��ר�ã����ò�ѯ����");
          		
          		
          		String csz = "";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1��ִ������
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
                    if("EE".equalsIgnoreCase(pz1)||"FF".equalsIgnoreCase(pz1)){
      					pz1="0";
      				}else{
      					pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
      				}
          			
          			
          			//2����С�¶�
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz2)&&!"FF".equalsIgnoreCase(pz2)){
          				pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          				pz2 = String.valueOf(Integer.parseInt(pz2));
          			}else{
          				pz2="";	
          			}
          			
          		    //3������¶�
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz3)&&!"FF".equalsIgnoreCase(pz3)){
          				pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          				pz3 = String.valueOf(Integer.parseInt(pz3));
          			}else{
          				pz3="";
          			}
          			
          		    //3����ʼ����
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz4)&&!"FFFF".equalsIgnoreCase(pz4)){
          				pz4 = Util.tranFormat29(pz4);
          			}else{
          				pz4="";
          			}
          			
          			 //4����ֹ����
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz5)&&!"FFFF".equalsIgnoreCase(pz5)){
          				pz5 = Util.tranFormat29(pz5);
          			}else{
          				pz5="";
          			}
          			
          		    //5������ʱ��
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz6)&&!"FF".equalsIgnoreCase(pz6)){
          				pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
          				pz6 = String.valueOf(Integer.parseInt(pz6));
          			}else{
          				pz6="";
          			}
          			
          			 //6��ֹͣʱ��
      				String pz7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz7)&&!"FF".equalsIgnoreCase(pz7)){
          				pz7 = Util.hexStrToDecStr(Util.convertStr(pz7));
          				pz7 = String.valueOf(Integer.parseInt(pz7));
          			}else{
          				pz7="";
          			}
          			
          			
          			
          			
          			
          			csz += pz1+","+pz2+","+pz3+","+pz4+","
          				  +pz5+","+pz6+","+pz7+";";
          			
          		
          			          			
      			}
      			
      			//��������
      			s_sql = "update g_zdcldpzb set afn04f14=? where zdid=? and cldh=? ";
      			
      	        params = new String[]{csz,zdid,s_da};
                jdbcT.update(s_sql,params); 
                
                
                
          	}else if(s_Fdt.equals("F15")){
          	    //F15:�ն˲��������ò�����ѯ����
          		cat.info("[Decode_0A]F15:ˮ��ˮλ���Ʋ�����ѯ����");
          		
          		s_sql = "select cldh from g_zdsbkzcsb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		String cs_f="";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      			//1�����
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2���������
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3�����Ӳ����
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if("EE".equalsIgnoreCase(pz3)){
          				pz3 = "";
          			}else if("CC".equalsIgnoreCase(pz3)){
          				pz3="CC";
          			}else{
          				pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			}
          			
          			
          		    //4���������Ӳ����
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if("EE".equalsIgnoreCase(pz4)){
          				pz4 = "";
          			}else if("CC".equalsIgnoreCase(pz4)){
          				pz4="CC";
          			}else{
          				pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			}
          			
          			//5��ͬʱ����ʹ��
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
//          			if(!"EE".equalsIgnoreCase(pz5)){
//          				pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
//          			}
          			
          		    //6�������л�ʱ��-Сʱ
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz6)){
          				if("CC".equalsIgnoreCase(pz6)){
          					pz6="0";
          				}else
          				    pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
          			}
          			
          		    //7���������
      				String pz7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz7)){
          				pz7 = Util.hexStrToDecStr(Util.convertStr(pz7));
          			}

          		    //8��ˮλ��λ
      				String pz8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz8)){
          				pz8 = Util.hexStrToDecStr(Util.convertStr(pz8));
          			}

          		    //9���߼���ϵ
      				String pz9 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz9)&&!"CC".equalsIgnoreCase(pz9)){
          				pz9 = Util.hexStrToDecStr(Util.convertStr(pz9));
          			}

          		    //10����һ���������
      				String pz10 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz10)){
          				if("CC".equalsIgnoreCase(pz10)){
          					pz10="";
          				}else
          				    pz10 = Util.hexStrToDecStr(Util.convertStr(pz10));
          			}

          		    //11��ˮλ��λ
      				String pz11 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz11)){
          				if("CC".equalsIgnoreCase(pz11)){
          					pz11="";
          				}else
          				    pz11 = Util.hexStrToDecStr(Util.convertStr(pz11));
          			}

          		    //12�����ƶ���
      				String pz12 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz12)){
          				pz12 = Util.hexStrToDecStr(Util.convertStr(pz12));
          			}

          		    //13����С�¶�
      				String pz13 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz13)){
          				if("CC".equalsIgnoreCase(pz13)){
          					pz13="";
          				}else{
          					String[] p=Util.tranFormatTemperture(pz13);
          					if("1".equalsIgnoreCase(p[1])){
          						pz13="-"+p[0];
          					}else{
          						pz13=p[0];
          					}
          				}
          			}

          		    //14������¶�
      				String pz14 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz14)){
          				if("CC".equalsIgnoreCase(pz14)){
          					pz14="";
          				}else{
          					String[] p=Util.tranFormatTemperture(pz14);
          					if("1".equalsIgnoreCase(p[1])){
          						pz14="-"+p[0];
          					}else{
          						pz14=p[0];
          					}
          				}
          			}
          			
          		    //15������ʱ��
      				String pz15 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz15)){
          				if("CCCC".equalsIgnoreCase(pz15)){
          					pz15="";
          				}else{
          					pz15 = Util.hexStrToDecStr(Util.convertStr(pz15));
                  			pz15 = String.valueOf(Integer.parseInt(pz15));
          				}
          				
          			}
          			
          		    //16��ֹͣʱ��
      				String pz16 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz16)){
          				if("CCCC".equalsIgnoreCase(pz16)){
          					pz16="";
          				}else{
          					pz16 = Util.hexStrToDecStr(Util.convertStr(pz16));
                  			pz16 = String.valueOf(Integer.parseInt(pz16));
          				}
          				
          			}
          			
          			
          			csz += pz3+"#"+pz4+"#"
            				  +pz5+"#"+pz6+"#"+pz7+"#"+pz8+"#"+pz9+"#"+pz10+"#"+pz11+"#"+pz12+"#"+pz13+"#"+pz14+"#"+pz15+"#"+pz16+";";
          			cs_f += pz3+","+pz4+","
          				  +pz5+","+pz6+","+pz7+","+pz8+","+pz9+","+pz10+","+pz11+","+pz12+","+pz13+","+pz14+","+pz15+","+pz16+";";
          			
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
        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)?"":pz16,zdid,pz2};
          			}else if(isIn == false){
          				//��վ���޸ò����������
          				s_sql = "insert into g_zdsbkzcsb(id,zdid,xh,cldh,scyjh,byscyjh,tsgzsn,zbqhsj,cthm1,swdw1,ljgx,cthm2,swdw2,kzdz,zxwd,zdwd,qdsj,tzsj) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)?"":pz16};
          			}
          			jdbcT.update(s_sql,params);
          			
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
          	    //дg_zdcldpzb��f15�ֶ�
      			s_sql="update g_zdcldpzb set afn04f15=? where zdid=? and cldh=0";
      			 params = new String[]{cs_f,zdid};
                 jdbcT.update(s_sql,params); 
                 
      			//ɾ����������
      			s_sql = "delete g_zdsbkzcsb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
               
      			
          	}else if(s_Fdt.equals("F16")){
          		//F16:�ն˲��������ò�����ѯ����
          		cat.info("[Decode_0A]F16:�����ŷ����Ʋ���");
          		
          		s_sql = "select cldh from g_zdfjdcfkzcs where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		String cs_f="";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      			    //1�����
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2���������
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3�����Ӳ����
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz3)&&!"CC".equalsIgnoreCase(pz3)){
          				pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			}
          			
          			
          		    //4���������Ӳ����
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz4)&&!"CC".equalsIgnoreCase(pz4)){
          				pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			}
          			
          		    //5�������л�ʱ��-Сʱ
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz5)){
          				if("CC".equalsIgnoreCase(pz5)){
          					pz5="0";
          				}else
          				    pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
          			}
          			
          			//6����ˮ������ʹ��
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
//          			if(!"EE".equalsIgnoreCase(pz6)){
//          				pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
//          			}
          			
          		    
          		    //7����С�¶�
      				String pz7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz7)){
          				if("CC".equalsIgnoreCase(pz7)){
          					pz7="";
          				}else{
          					String[] p=Util.tranFormatTemperture(pz7);
          					if("1".equalsIgnoreCase(p[1])){
          						pz7="-"+p[0];
          					}else{
          						pz7=p[0];
          					}
          				}
          				
          			}

          		    //8������¶�
      				String pz8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz8)){
          				if("CC".equalsIgnoreCase(pz8)){
          					pz8="";
          				}else{
          					String[] p=Util.tranFormatTemperture(pz8);
          					if("1".equalsIgnoreCase(p[1])){
          						pz8="-"+p[0];
          					}else{
          						pz8=p[0];
          					}
          				}
          			}
          			
          		    //9������ʱ��
      				String pz9 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz9)){
          				if("CCCC".equalsIgnoreCase(pz9)){
          					pz9="";
          				}else{
          				pz9 = Util.hexStrToDecStr(Util.convertStr(pz9));
          				pz9 = String.valueOf(Integer.parseInt(pz9));
          				}
          			}
          			
          		    //10��ֹͣʱ��
      				String pz10 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz10)){
          				if("CCCC".equalsIgnoreCase(pz10)){
          					pz10="";
          				}else{
          				pz10 = Util.hexStrToDecStr(Util.convertStr(pz10));
          				pz10 = String.valueOf(Integer.parseInt(pz10));
          				}
          			}
          			
          		    //11��Ƶ��
      				String pz11 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz11)&&!"CC".equalsIgnoreCase(pz11)){
          				pz11 = Util.hexStrToDecStr(Util.convertStr(pz11));
          			}
          			
          		    //12�����ƶ���
      				String pz12 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz12)){
          				pz12 = Util.hexStrToDecStr(Util.convertStr(pz12));
          			}
          			
          			csz += pz3+"#"+pz4+"#"
            				  +pz5+"#"+pz6+"#"+pz7+"#"+pz8+"#"+pz9+"#"+pz10+"#"+pz11+"#"+pz12+";";
          			cs_f +=  pz3+","+pz4+","
            				  +pz5+","+pz6+","+pz7+","+pz8+","+pz9+","+pz10+","+pz11+","+pz12+";";
          			
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
        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,zdid,pz2};
          			}else if(isIn == false){
          				//��վ���޸ò����������
          				s_sql = "insert into g_zdfjdcfkzcs(id,zdid,xh,cldh,scyjh,byscyjh,zbqhsj,sbldsn,zxwd,zdwd,qdsj,tzsj,pl,kzdz) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12};
          			}
          			jdbcT.update(s_sql,params);
          			
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
          	    //дg_zdcldpzb��f16�ֶ�
      			s_sql="update g_zdcldpzb set afn04f16=? where zdid=? and cldh=0";
      			 params = new String[]{cs_f,zdid};
                 jdbcT.update(s_sql,params); 
                 
      			//ɾ����������
      			s_sql = "delete g_zdfjdcfkzcs where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
          		
      			
      			
          	}else if(s_Fdt.equals("F17")){
          		
          		//F17:ORP,HP ���������ò�ѯ����
          		cat.info("[Decode_0A]F17:ORP,HP ����������");
          		s_sql = "select cldh from g_zdorpphxzb where zdid=?";
          		
          		params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1�����
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2���������
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3��ORP��ֵ����
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			
          			String pz3s[] = Util.tranFormat28(pz3);
          			if("1".equalsIgnoreCase(pz3s[1])){
          				pz3="-"+pz3s[0];
          			}else{
          				pz3=pz3s[0];
          			}
          			
          			//4��ORP��ֵ����
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			String pz4s[] = Util.tranFormat28(pz4);
          			if("1".equalsIgnoreCase(pz4s[1])){
          				pz4="-"+pz4s[0];
          			}else{
          				pz4=pz4s[0];
          			}
          			
          			//5��PH��ֵ����
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz5 = Util.tranFormat30(pz5);
          			
          			
          		    //6��PH��ֵ����
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz6 = Util.tranFormat30(pz6);
          			
          			csz += pz1+"#"+pz2+"#"+pz3+"#"+pz4+"#"
          				  +pz5+"#"+pz6+";";
          			
          			boolean isIn = false;
          			for(int j=0;j<dnbList.size();j++){
          				Map tempHM = (Map)dnbList.get(j);
          				if(String.valueOf(tempHM.get("cldh")).equals(pz2)){
          					isIn = true;
          					break;
          				}
          			}
          			
          			//д"�ն˲��������ñ�"
          			if(isIn == true){
          				//��վ���иò����������
          				s_sql = "update g_zdorpphxzb set orpsx=?,orpxx=?,phsx=?,phxx=? where zdid=? and cldh=?";
        	        	params = new String[]{pz3,pz4,pz5,pz6,zdid,pz2};
          			}else if(isIn == false){
          				//��վ���޸ò����������
          				s_sql = "insert into g_zdorpphxzb(id,zdid,xh,cldh,orpsx,orpxx,phsx,phxx) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,pz3,pz4,pz5,pz6};
          			}
          			jdbcT.update(s_sql,params);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      			//ɾ�����������������
      			s_sql = "delete g_zdorpphxzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                
                
          	}else if(s_Fdt.equals("F18")){
          		
          		//F18:������ˮλ���������ò�ѯ����
          		cat.info("[Decode_0A]F18:������ˮλ����������");
          		s_sql = "select pooleid from m_station_pool where stationid=(select stationid from g_zdgz where zdid=?)";
          		
          		params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//���� bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1�����
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2���������
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3������
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3= Util.tranFormat22(pz3);
          			
          			
          			//4��ˮλ����
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz4= Util.tranFormat22(pz4);
          			
          			//5��ˮλ����
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz5= Util.tranFormat22(pz5);
          			
          			
          		  
          			
          			csz += pz1+"#"+pz2+"#"+pz3+"#"+pz4+"#"
          				  +pz5+";";
          			
          			boolean isIn = false;
          			for(int j=0;j<dnbList.size();j++){
          				Map tempHM = (Map)dnbList.get(j);
          				if(String.valueOf(tempHM.get("pooleid")).equals(pz2)){
          					isIn = true;
          					break;
          				}
          			}
          			
          			//д"�ն˲��������ñ�"
          			if(isIn == true){
          				//��վ���иò����������
          				s_sql = "update m_station_pool set deep=?,upper=?,lower=?  where stationid=(select stationid from g_zdgz where zdid=?) and pooleid=?";
        	        	params = new String[]{pz3,pz4,pz5,zdid,pz2};
          			}else if(isIn == false){
          				//��վ���޸ò����������
          				s_sql = "insert into m_station_pool(id,stationid,pooleid,pname,deep,upper,lower) "
        	        		+"values(S_POOLID.nextVal,(select stationid from g_zdgz where zdid=?),?,?,?,?,?)";
          				String pname="";
          				if("1".equalsIgnoreCase(pz2)){
          					pname="�ۺ��ռ���";
          				}else if("2".equalsIgnoreCase(pz2)){
          					pname="һ�廯��ˮ�����";
          				}else if("3".equalsIgnoreCase(pz2)){
          					pname="�����";
          				}else if("4".equalsIgnoreCase(pz2)){
          					pname="�м�ˮ��";
          				}
        	        	params = new String[]{zdid,pz2,pname,pz3,pz4,pz5};
          			}
          			jdbcT.update(s_sql,params);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      			//ɾ�����������������
      			s_sql = "delete m_station_pool where stationid=(select stationid from g_zdgz where zdid=?)  ";
      			if(i_sl>0){
      				s_sql += "and pooleid not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                
                
          	}else if(s_Fdt.equals("F25")){
          	//F1:�ն�ͨ�Ų�����ѯ����
          		cat.info("[Decode_0A]F25:����ģ���Ȳ���");
          		//����ֵ(cs1;cs2;cs3)
//          		cs1:PT 
//	          		cs2:CT 
//	          		cs3:©�����ٽ�ֵ
          		
          		String csz = "";
          		
          		//PT
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+4);
          		cs1=Util.convertStr(cs1);
      			idx_dadt += 4;
      			cs1 = Util.hexStrToDecStr(cs1);
      			
      			//CT 
      			String cs2 = DADT.substring(idx_dadt,idx_dadt+4);
      			cs2=Util.convertStr(cs2);
      			idx_dadt += 4;      			
      			cs2= Util.hexStrToDecStr(cs2);
      	        
      			
      		    //©�����ٽ�ֵ 
      			String cs3 = DADT.substring(idx_dadt,idx_dadt+4);
      			cs3=Util.convertStr(cs3);
      			idx_dadt += 4;      			
      			cs3= Util.hexStrToDecStr(cs3);
      			

//      			csz = cs1+";"+cs2+";"+cs3;
      			
//      	        //д�ն����в������ñ�
//      			s_sql = "update g_zdyxcspzb set AFN04F25=? where zdid=?";      			
//      	        params = new String[]{csz,zdid};
//                jdbcT.update(s_sql,params);
      			 //д�ն����в������ñ�
      			s_sql = "update g_zdcldpzb "
						+"set pt=?,ct=?,ldlljz=? "
						+" where zdid=? and cldh=0";
				params = new String[]{cs1,cs2,cs3,zdid};
                jdbcT.update(s_sql,params);
          	}else if(s_Fdt.equals("F26")){
          	    //F26:�������㷨ʹ�ܲ���
          		cat.info("[Decode_0A]F26:�������㷨ʹ�ܲ���");
          		
          		String csz = "";
          		
          		//������ѵʹ��
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			
      			csz=cs1;
      			
      		    //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F26=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
                
          	}else if(s_Fdt.equals("F27")){
          	    //F27:������ѵ����������
          		cat.info("[Decode_0A]F27:������ѵ����������");
          		
          		String csz = "";
          		
          		//�������1���¶ȣ�
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs1= Util.tranFormat05(cs1);
      			
      			//�������2��OPR��
          		String cs2 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			String cs2_array[]=Util.tranFormat28(cs2);
      			cs2= cs2_array[1]+cs2_array[0];
      			
      			//�������3�����Ƶ�ʣ�
          		String cs3 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs3= Util.tranFormat06(cs3);
      			
      			csz=cs1+";"+cs2+";"+cs3;
      			
      		    //д�ն����в������ñ�
      			s_sql = "update g_zdyxcspzb set AFN04F27=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
                
          	}else if (s_Fdt.equalsIgnoreCase("F65")) { 
          		
    	        //F65:�ն�1�������������õĲ�ѯ����
          		//����ֵ(cs1;...;cs5)
          	   	//cs1:�ϱ�����(0-31)
          	   	//cs2:�ϱ����ڵ�λ(0~3���α�ʾ�֡�ʱ���ա���)
          	   	//cs3:�ϱ���׼ʱ��(������ʱ����,yymmddhhmmss)
          	   	//cs4:��ͣ��־(55���á�AAͣ��)
          	   	//cs5:����������(P1@F1#P2@F2#...#Pn@Fn)
    	    	
          		String csz = "";
    	    	String rwh = s_da;//�����
    	    	
    	    	String rwlx = "";//��������(1��)
	    		cat.info("[Decode_0A]F65:�ն�1�������������õĲ�ѯ����(�����"+rwh+")");
	    		rwlx = "1";
    	    	
    	    	   	       
    	        //�����ϱ����ڼ���λ
    	        String rwzqjdw = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			rwzqjdw = Util.hexStrToBinStr(rwzqjdw,1);  
      			
    	        //�ϱ�����(0-31)
    	        String cs1 =rwzqjdw.substring(2,8);
    	        cs1 = Util.binStrToDecStr(cs1);    	        
    	        
    	        //�ϱ����ڵ�λ(0~3���α�ʾ�֡�ʱ���ա���)
    	        String cs2 = rwzqjdw.substring(0,2);
    	        cs2 = Util.binStrToDecStr(cs2);
    	        
    	        //�ϱ���׼ʱ��(������ʱ����,yymmddhhmmss)
    	        String cs3 = DADT.substring(idx_dadt,idx_dadt+12);
      			idx_dadt += 12;
      			cs3 = Util.tranFormat01_1(cs3);
      			
    	        //��ͣ��־(55���á�AAͣ��)
    	        String cs4 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;     			    	        
    	        
    	        //�������ݵ�Ԫ��ʶ����
    	        String sjdygs = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
    	        int i_sjdygs = Integer.parseInt(sjdygs,16);
    	        

    			//�����ն��������ñ���Ӧ�����Ƿ��Ѵ���
    			s_sql = "select rwid from g_zdrwpzb "
    				+"where zdid=? and rwlx=? and rwh=?";
    			List lst = jdbcT.queryForList(s_sql,new String[]{zdid,rwlx,rwh});
    			int count = lst.size();
    			String rwid = "";
    			if(count>0){
    				rwid = String.valueOf(((Map)lst.get(0)).get("rwid"));
    			}
    			//SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
    			//д���ն��������ñ�
    			if(count>0){
    				//update	
    				rwid = String.valueOf(((Map)lst.get(0)).get("rwid"));
    				s_sql = "update g_zdrwpzb "
    					+"set fszq=?,zqdw=?,fsjzsj=to_date(?,'yy-mm-dd hh24:mi:ss'),qybz=? ,SJZFSSEQ=?"
    					+"where rwid=?";
    				params = new String[]{
    						cs1,cs2,cs3,cs4,s_sjzfsseq,rwid};
    				jdbcT.update(s_sql,params);

    			}else{
    				//insert
    				rwid = Util.getSeqRwid(jdbcT);
    				s_sql = "insert into g_zdrwpzb(rwid,zdid,rwlx,rwh,fszq,zqdw,fsjzsj,qybz,SJZFSSEQ) "
    					+"values(?,?,?,?,?,?,to_date(?,'yy-mm-dd hh24:mi:ss'),?,?)";
//    				DateUtil.parse(str)
    				params = new String[]{
    						rwid,zdid,rwlx,rwh,cs1,cs2,cs3,cs4,s_sjzfsseq};
    				jdbcT.update(s_sql,params);

    			}
    			//ɾ������������
    			s_sql = "delete g_zdrwpzb where zdid=? and rwh<>?";
    			params = new String[]{zdid,rwh};
    			jdbcT.update(s_sql,params);   	
    			
      	        
      	        //ɾ���������������Ϣ��
    			s_sql = "delete g_rwxxx where rwid=?";
    			params = new String[]{rwid};
    			jdbcT.update(s_sql,params);   	        
      	        
    	        for (int i = 1; i <= i_sjdygs; i++) {              	
    	        	//<-------------��i�����ݵ�Ԫ��ʶ��---------------->
    	            String dadt = DADT.substring(idx_dadt,idx_dadt+8);
          			idx_dadt += 8;
    	            
    	            String da = dadt.substring(0,4);
    	            da = Util.tranDA(Util.convertStr(da));
    	            da = "P" + da;
    	            String dt = dadt.substring(4,8);
    	            dt = Util.tranDT(Util.convertStr(dt));
    	            dt= "F" + dt;
    	            
    	            //д"������Ϣ���"
    	            s_sql = "insert into g_rwxxx(rwid,xxdh,xxxdm,xh) "
    					+"values(?,?,?,?)";
    				params = new String[]{rwid,da,dt,String.valueOf(i)};
    				jdbcT.update(s_sql,params); 
    	                	            
    	        }
    	
    	    }
      	}
	}
	
	
}