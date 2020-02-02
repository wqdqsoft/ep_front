package com.powerhigh.gdfas.parse;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Category;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.util.Util;

/**
 * Description: AFN=0A(终端响应参数查询————返回处理) <p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-2
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class Decode_0A{
	//加载日志
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
			//时间标签(6字节) and 事件计数器(2字节)
			DADT = s_csdata.substring(16,s_csdata.length()-16);
		}else if(s_tpv.equals("1")&&s_acd.equals("0")){
			//时间标签(6字节)
			DADT = s_csdata.substring(16,s_csdata.length()-12);
		}else if(s_tpv.equals("0")&&s_acd.equals("1")){
			//事件计数器(2字节)
			DADT = s_csdata.substring(16,s_csdata.length()-4);
		}else if(s_tpv.equals("0")&&s_acd.equals("0")){
			//无附加信息
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
		
		//修改“数据祯发送表”的状态标志
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
		String s_da = "";//信息点Pn
		String s_dt = "";//信息类Fn
		String s_PF = "";//PnFn
      	String zdid = Util.getZdid(xzqxm, zddz, jdbcT);
      	String s_sql = "";
      	String[] params = null;
      	cat.info("DTDT:"+DADT );
//      	System.out.println("DADT:"+DADT);
//      	System.out.println("step1: 0A");
      	while(idx_dadt<DADT.length()){
      		//------------------每个PnFn-----------------
      		s_dadt = DADT.substring(idx_dadt, idx_dadt+8);
    		idx_dadt += 8;
    		
          	//信息点Pn
          	s_da = s_dadt.substring(0,4);
          	s_da = Util.tranDA(Util.convertStr(s_da));
          	String s_Pda = "P" + s_da;
          	//信息类Fn
          	s_dt = s_dadt.substring(4,8);
          	s_dt = Util.tranDT(Util.convertStr(s_dt));
          	String s_Fdt = "F" + s_dt;
          	//PnFn
          	s_PF = s_Pda + s_Fdt;

//          	System.out.println("step2: "+s_Fdt);
          	
          	if(s_Fdt.equals("F1")){
          		//F1:终端通信参数查询返回
          		cat.info("[Decode_0A]F1:终端通信参数查询返回");
          		//参数值(cs1;cs2;cs3;cs4;cs5)
//          		 cs1:数传机延时时间,单位:20ms
//          		 cs2:终端通信模块的信号强度,单位:db 
//          		 cs3:终端启动次数（最大65535）,单位:次
//          		 cs4:备用 默认FF
//          		 cs5:心跳周期:1-60分
          		
          		String csz = "";
          		
          		//数传机延时时间,单位:20ms
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 = Util.hexStrToDecStr(cs1);
      			
      			//终端通信模块的信号强度,单位:db 
      			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;      			
      			cs2=Util.tranFormat04(Util.convertStr(cs2))[0];
      	        
      			
      			//终端启动次数（最大65535）,单位:次
      			String cs3 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs3= Util.hexStrToDecStr(Util.convertStr(cs3));
      			
      			//备用 默认FF
      			String cs4=DADT.substring(idx_dadt,idx_dadt+2);;
      			idx_dadt += 2;
      			cs4= Util.hexStrToDecStr(Util.convertStr(cs4));
      			
      			//心跳周期:1-60分
      			String cs5 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs5 = Util.hexStrToDecStr(cs5);
      			

      			csz = cs1+";"+cs2+";"+cs3+";"
      				+cs4+";"+cs5;
      			
      	        //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F1=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
                
                //写终端运行参数配置表
      			s_sql = "update g_zdgz set xhqd=? where zdid=?";      			
      	        params = new String[]{cs2,zdid};
                jdbcT.update(s_sql,params);
                
                //写终端运行参数配置表
      			s_sql = "insert into G_ZDTXZTJLB(id,zdid,jlsj,zhtxsj,xhqd) values(s_zdtxzt.nextval,?,sysdate,sysdate,?)";      			
      	        params = new String[]{zdid,cs2};
                jdbcT.update(s_sql,params);
                
          	}else if(s_Fdt.equals("F3")){
          		//F3:主站IP地址查询返回
          		//参数值(cs1;cs2;cs3)
          	    // 	cs1:主用IP(xxx.xxx.xxx.xxx:nnnnn)
          	    //	cs2:备用IP(xxx.xxx.xxx.xxx:nnnnn)
          	    //	cs3:APN(16字节；ASCII;低位补00H；按正序传)
          		cat.info("[Decode_0A]F3:主站IP地址查询返回");
          		String csz = "";
          		String temps = "";
          		//主站主用IP
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
      			
      			//主站备用IP
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
      			
      	        //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F3=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F4")){
          		//F4:处理池基本参数查询返回
//          		处理池基本参数（cs1;cs2;......cs6）
//          		cs1:池体类型 1. A2/O工艺2. 微生态滤床  3. 多介质生物滤池
//          		cs2:处理规模
//          		cs3:池深
//          		cs4:池体横截面积
//          		cs5:处理能力(水泵流量) 计算站点流量（如果有流量计则以流量计为准）
//          		cs6:谷电总小时数
//          		cs7:峰谷节能模式使能 0x55 ：使能， 0xaa ：不使能
//          		cs8：一代：调节池/收集池浮球数量；二代：流量计配置0x55:有流量计， 0xaa:无流量计
//          		cs9:池体水位上限（水位深度）
//          		cs10:池体水位下限（水位深度）
//          		cs11:目标ORP数值范围下限
//          		cs12:目标ORP数值范围上限
//          		cs13:水位检测方式 0：浮球控制；1：超声波水位控制
          		cat.info("[Decode_0A]F4:处理池基本参数查询返回");
          		
                String csz = "";
          		
                s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
        		params = new String[] { xzqxm,zddz };
        	    List cldList = jdbcT.queryForList(s_sql, params);
        	    Map cldMap = (Map) cldList.get(0);
        	    // 终端型号
        	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
        	 	
        	 	if("1".equalsIgnoreCase(zdxh)){
        	 		//池体类型
              		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs1 = Util.hexStrToDecStr(cs1);
          			
          			//处理规模 吨/天
          			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs2=Util.tranFormat04(cs2)[0];
          			
          		    //池深
          			String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs3=Util.tranFormat22(cs3);
          			
          			//一代：调节池/收集池浮球数量
          			String cs8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs8=Util.hexStrToDecStr(cs8);
          			
          			 //池体水位上限（距池体顶部距离）
          			String cs9 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs9=Util.tranFormat22(cs9);
          			
          			 //池体水位下限（距池体顶部距离）
          			String cs10 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs10=Util.tranFormat22(cs10);
          			
          			 //目标ORP数值范围下限
          			String cs11 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			String cs11str[]=Util.tranFormat28(cs11);
          			String fh="";
          			if("1".equalsIgnoreCase(cs11str[1])){
          				fh="-";
          			}
          			cs11=fh+cs11str[0];
          			
          			 //目标ORP数值范围上限
          			String cs12 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;      			
          			String cs12str[]=Util.tranFormat28(cs12);
          			fh="";
          			if("1".equalsIgnoreCase(cs12str[1])){
          				fh="-";
          			}
          			cs12=fh+cs12str[0];
          			
          			 //水泵控制判断依据
          			String cs13 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;   
          			cs13=Util.hexStrToDecStr(cs13);

          			csz = cs1+";"+cs2+";"+cs3+";"+cs8+";"+cs9+";"+cs10+";"+cs11+";"+cs12+";"+cs13;
          			
          			//二代终端
        	 	}else{
        	 		//池体类型
              		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs1 = Util.hexStrToDecStr(cs1);
          			
          			//处理规模 吨/天
          			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs2=Util.tranFormat04(cs2)[0];
          			
          		    //池深
          			String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			cs3=Util.tranFormat22(cs3);
          			
          			//池体横截面积
              		String cs4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs4 = Util.hexStrToDecStr(cs4);
          			
          			//处理能力(水泵流量) 计算站点流量（如果有流量计则以流量计为准）
              		String cs5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs5 = Util.hexStrToDecStr(cs5);
          			
          			//谷电总小时数
              		String cs6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			cs6 = Util.hexStrToDecStr(cs6);
          			
          			//峰谷节能模式使能 0x55 ：使能， 0xaa ：不使能
              		String cs7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
//          			cs7 = Util.hexStrToDecStr(cs7);
          			
          			
          			 //流量计配置0x55:有流量计， 0xaa:无流量计
          			String cs8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;      			
          			//cs8=Util.hexStrToDecStr(cs8);
          			
//          			 //池体水位上限（距池体顶部距离）
//          			String cs9 = DADT.substring(idx_dadt,idx_dadt+2);
//          			idx_dadt += 2;      			
//          			cs9=Util.tranFormat22(cs9);
//          			
//          			 //池体水位下限（距池体顶部距离）
//          			String cs10 = DADT.substring(idx_dadt,idx_dadt+2);
//          			idx_dadt += 2;      			
//          			cs10=Util.tranFormat22(cs10);
//          			
//          			 //目标ORP数值范围下限
//          			String cs11 = DADT.substring(idx_dadt,idx_dadt+4);
//          			idx_dadt += 4;
//          			String cs11str[]=Util.tranFormat28(cs11);
//          			String fh="";
//          			if("1".equalsIgnoreCase(cs11str[1])){
//          				fh="-";
//          			}
//          			cs11=fh+cs11str[0];
//          			
//          			 //目标ORP数值范围上限
//          			String cs12 = DADT.substring(idx_dadt,idx_dadt+4);
//          			idx_dadt += 4;      			
//          			String cs12str[]=Util.tranFormat28(cs12);
//          			fh="";
//          			if("1".equalsIgnoreCase(cs12str[1])){
//          				fh="-";
//          			}
//          			cs12=fh+cs12str[0];
          			
          			 //水泵控制判断依据
          			String cs13 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;   
          			cs13=Util.hexStrToDecStr(cs13);

          			csz = cs1+";"+cs2+";"+cs3+";"
          				+cs4+";"+cs5+";"+cs6+";"+cs7+";"+cs8+";"+cs13;
        	 	}
        	 	
        	 	
          		
      			
      	        //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F4=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F5")){
          		//F5:风机水泵控制参数查询返回
//          		风机水泵控制参数(cs1;cs2;cs3;cs4;))
//          		*cs1:风机开启相对于水泵开启的提前量
//          		*cs2:风机水泵自动控制允许标志   0x55允许自动控制；0xAA禁止自动控制
//          		*cs3:污泥回流泵自动控制允许标志    0x55允许自动控制；0xAA禁止自动控制
//          		*cs4:污泥回流泵周期运行开启时长
//          		*cs5:污泥回流泵周期运行停止时长
          		cat.info("[Decode_0A]F5:风机水泵控制参数查询返回");
          		
          		String csz = "";

                s_sql="select zdxh from g_zdgz where xzqxm=? and zddz=?";
        		params = new String[] { xzqxm,zddz };
        	    List cldList = jdbcT.queryForList(s_sql, params);
        	    Map cldMap = (Map) cldList.get(0);
        	    // 终端型号
        	 	String zdxh = String.valueOf(cldMap.get("zdxh"));
        	 	
        	 	
          		
          	    //风机开启相对于水泵开启的提前量
      			String cs1 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			if("EEEE".equalsIgnoreCase(cs1)){
      				cs1="无效";
      			}else{
      				if("1".equalsIgnoreCase(zdxh)){
      					cs1 = Util.hexStrToDecStr(Util.convertStr(cs1));
      				}else{
      					cs1=Util.tranFormat08(cs1);
      				}
      			    
      			}
      			
      			//风机水泵自动控制允许标志
          		String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
//      			cs2 = Util.hexStrToDecStr(cs2);
          		
          		//污泥回流泵自动控制允许标志
          		String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
//      			cs3 = Util.hexStrToDecStr(cs3);

      			//污泥回流泵周期运行开启时长
      			String cs4 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;      			
      			if("EEEE".equalsIgnoreCase(cs4)){
      				cs4="无效";
      			}else{
//      			    cs4=Util.tranFormat08(cs4);
      				cs4=Util.hexStrToDecStr(Util.convertStr(cs4));
      			}
      			
      		    //污泥回流泵周期运行停止时长
      			String cs5 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;      			
      			if("EEEE".equalsIgnoreCase(cs5)){
      				cs5="无效";
      			}else{
//      			    cs5=Util.tranFormat08(cs5);
      				cs5=Util.hexStrToDecStr(Util.convertStr(cs5));
      			}
      			
      			csz = cs1+";"+cs2+";"+cs3+";"+cs4+";"+cs5;
      			
      	        //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F5=? where zdid=?";
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F6")){
          		//F6:门禁及报警参数查询返回
          		//参数值(cs1;...;cs8)
//	          	   cs1:刷卡验证后的有效时长
//	          	   cs2:声音报警时长
//	          	   cs3:灯光报警时长
          		cat.info("[Decode_0A]F6:门禁及报警参数查询返回");
          		
          		String csz = "";
          		
          		//刷卡验证后的有效时长
      			String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			if(!"FF".equalsIgnoreCase(cs1)){
      				cs1=Util.tranFormat04(cs1)[0];
      				cs1=String.valueOf(Integer.parseInt(cs1));
      			}else{
      				cs1="无效";
      			}
      			
      			
      			//声音报警时长
      			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2; 
      			if(!"FF".equalsIgnoreCase(cs2)){
      				cs2=Util.tranFormat04(cs2)[0];
      				cs2=String.valueOf(Integer.parseInt(cs2));
      			}else{
      				cs2="无效";
      			}
      			
      			
      			//灯光报警时长
      			String cs3 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2; 
      			if(!"FF".equalsIgnoreCase(cs3)){
      				cs3=Util.tranFormat04(cs3)[0];
      				cs3=String.valueOf(Integer.parseInt(cs3));
      			}else{
      				cs3="无效";
      			}
      			
      			
      			csz = cs1+";"+cs2+";"+cs3;
      			
      	        //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F6=? where zdid=?";
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F7")){
          		//F7: 射频卡序列号库查询返回
          		cat.info("[Decode_0A]F7: 射频卡序列号库查询返回");
          		
          	   
          		String csz = "";
          		
          		//ID卡序列号个数
          		String idnum = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			idnum= Util.hexStrToDecStr(Util.convertStr(idnum));
      			
      			int i_idnum=Integer.parseInt(idnum);
      			
      		    //N 个ID卡
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
      			
      	        //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F7=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F8")){
          		//F8:关联手机号码查询返回
          		//参数值关联手机号码（cs1;cs2;cs3;cs4）
//          		cs1:收集池个数n
//          		cs2:短信中心号码
//          		cs3:调节池uim卡号
//          		cs4(卡号1,卡号2,.........卡号n)
          		cat.info("[Decode_0A]F8:关联手机号码查询返回查询返回");
          		String temps = "";
          		int index = 0;
          		String csz = "";
          		
          	    //收集池个数n
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			cs1 = Util.hexStrToDecStr(cs1);
      			
      			//短信中心号码
      			String cs2 = "";
          		temps = DADT.substring(idx_dadt,idx_dadt+16);
      			idx_dadt += 16;
      			index = temps.indexOf("F");
      			if(index == -1){
      				cs2 = temps;
      			}else{
      				cs2 = temps.substring(0,index);
      			}
      			
      		    //调节池uim卡号
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
      			
      		    //N个收集池的uim卡号(卡号1,卡号2,.........卡号n)
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
          		//F9:水泵水位控制参数（针对收集池智能终端）
//				String 参数值(cs1;cs2;cs3;cs4)
//				   * 				   cs1:主备切换设置1-使用1号水泵2-使用2号水泵3- 1号2号互为主
//									   cs2:主备切换时间  一个字节 小时
//									   cs3:启动时间 两个字节 分钟
//									   cs4:停止时间   两个字节 分钟';
          		cat.info("[Decode_0A]F9:水泵水位控制参数（针对收集池智能终端）");
          		
          		String csz = "";
          		
          		//主备切换设置
      			String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			if(!"EE".equalsIgnoreCase(cs1)){
      				cs1 = Util.hexStrToDecStr(Util.convertStr(cs1));
      			}
      			
      			
      			//主备切换时间
      			String cs2 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			if(!"EE".equalsIgnoreCase(cs2)){
      				if("CC".equalsIgnoreCase(cs2)){
      					cs2="0";
      				}else
      					cs2 = Util.hexStrToDecStr(Util.convertStr(cs2));
      			}
      			
      			
      		    //15、启动时间
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
      			
      		    //16、停止时间
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
      			
      	        //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F9=? where zdid=?";
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
      	        
          	}else if(s_Fdt.equals("F10")){
          		//F10:终端测量点配置参数查询返回
          		cat.info("[Decode_0A]F10:终端测量点配置参数查询返回");
          		
          		//参数值(cs1;...;csn)--N个电能表配置
          	   	//csn:电能表配置(pz1#...#pz13)
          		//pz1:电能表序号
          		//pz2:所属测量点
          		//pz3:通信速率(0-7)
          		//pz4:端口号(1-31)
          		//pz5:规约类型(0-255)
          		//pz6:通讯地址
          		//pz7:通讯密码
          		//pz8:费率个数(1-48)
          		//pz9:整数位个数[4-7]
          		//pz10:小数位个数[1-4]
          		//pz11:所属采集器通信地址
          		//pz12:所属用户大类号[0-15]
          		//pz13:所属用户小类号[0-15]
          		
          		//从"终端测量点配置表"取电能表配置信息(cldlx=01)
        	    String cldlx = "01";//测量点类型(01:电能表)
          		s_sql = "select cldh from g_zdcldpzb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1、序号
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2、测量点号
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3、设备类型
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			
          			//4、通信规约
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			
          			//5、通信速率
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
          			
          			
          			//6、通信地址
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
          			
          			//写"终端测量点配置表"
          			if(isIn == true){
          				//主站里有该测量点的配置
          				s_sql = "update g_zdcldpzb set xh=?,cldlx=?,gylx=?,txsl=?,txdz=?";
        	        	params = new String[]{pz1,pz3,pz4,pz5,pz6,zdid,pz2};
          			}else if(isIn == false){
          				//主站里无该测量点的配置
          				s_sql = "insert into g_zdcldpzb(id,zdid,cldh,xh,cldlx,gylx,txsl,txdz) "
        	        		+"values(SEQ_CLDID.nextVal,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz2,pz1,pz3,pz4,pz5,pz6};
          			}
          			jdbcT.update(s_sql,params);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      			
      			//删除其它测量点的配置
      			s_sql = "delete g_zdcldpzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params);  
          	}else if(s_Fdt.equals("F12")){
          		//F12:开关量类输入设备装置配置参数
          		cat.info("[Decode_0A]F12:开关量类输入设备装置配置参数");
          		
          		//初始化g_zdgz表中的p_in
          		String param="0000000000000000";
          		
          		s_sql = "select cldh from g_zdkglsrsbpzb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1、序号
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2、测量点号
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3、设备类型
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			
          			//4、硬件输入接口号
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			
          			//5、设备从属组序号
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
          			
          			
          		    //6、设备的组内编号
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
          			
          			//写"终端测量点配置表"
          			if(isIn == true){
          				//主站里有该测量点的配置
          				s_sql = "update g_zdkglsrsbpzb set sblx=?,yjsrjkh=?,sbcszxh=?,sbznbh=? where zdid=? and cldh=?";
        	        	params = new String[]{pz3,pz4,pz5,pz6,zdid,pz2};
          			}else if(isIn == false){
          				//主站里无该测量点的配置
          				String sbmc="默认设备"+pz1;
          				if("1".equalsIgnoreCase(pz3)){
          					sbmc="浮球"+pz1;
          				}else if("3".equalsIgnoreCase(pz3)){
          					sbmc="远程/就地信号"+pz1;
          				}
          				s_sql = "insert into g_zdkglsrsbpzb(id,zdid,xh,cldh,sbmc,sblx,yjsrjkh,sbcszxh,sbznbh) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,sbmc,pz3,pz4,pz5,pz6};
          			}
          			jdbcT.update(s_sql,params);
          			
          			param=param.substring(0, Integer.parseInt(pz4)-1)+"1"+param.substring(Integer.parseInt(pz4), 16);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      		    //查询该终端g_zddqsbpzb里面的每一路的输入量情况  最终得出最终的输入量
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
      			
      			//删除其它测量点的配置
      			s_sql = "delete g_zdkglsrsbpzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                //更新g_zdgz中的pin字段 将有效的路数置1
                s_sql="update g_zdgz set pin=? where zdid=?";
                params = new String[]{param,zdid};
                jdbcT.update(s_sql,params); 
                
          	}else if(s_Fdt.equals("F13")){
          		//F13:电气设备控制点参数查询返回
          		cat.info("[Decode_0A]F13:电气设备控制点参数");
          		
          		//初始pin，g_zdgz表中的p_in
          		String pin="0000000000000000";
          	    //初始化pout，g_zdgz表中的p_out
          		String pout="00000000";
          		
          		s_sql = "select cldh from g_zddqsbpzb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1、序号
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2、测量点号
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3、设备类型
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			
          			//设备名称
          			String sbmc="默认设备";
          			if("1".equalsIgnoreCase(pz3)){
          				sbmc="水泵"+pz2;
          			}else if("2".equalsIgnoreCase(pz3)){
          				sbmc="风机"+pz2;
          			}else if("3".equalsIgnoreCase(pz3)){
          				sbmc="电磁阀"+pz2;
          				if("5".equalsIgnoreCase(pz2)){
          					sbmc="回流泵";
          				}
          				if("6".equalsIgnoreCase(pz2)){
          					sbmc="气提阀";
          				}
          				
          			}else if("4".equalsIgnoreCase(pz3)){
          				sbmc="散热扇"+pz2;
          			}else if("5".equalsIgnoreCase(pz3)){
          				sbmc="报警输出"+pz2;
          			}
          			
          		    //4、额定功率
          			String pz4 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			pz4 = String.valueOf(Integer.parseInt(pz4));
          			
          			//5、处理能力
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
      				
          		    //6、接线方式
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
          			
          		    //7、硬件输出接口号
      				String pz7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz7 = Util.hexStrToDecStr(Util.convertStr(pz7));
          			
          		    //8、辅助触点接口号
      				String pz8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"CC".equalsIgnoreCase(pz8)){
          				pz8 = Util.hexStrToDecStr(Util.convertStr(pz8));
          			}
          			
          		    //9、故障灯硬件接口号
      				String pz9 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"CC".equalsIgnoreCase(pz9)){
          				pz9 = Util.hexStrToDecStr(Util.convertStr(pz9));
          			}
          			    
          			
          		    //10、变频电机或定频电机
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
          			
          			//写"配置表"
          			if(isIn == true){
          				//主站里有该测量点的配置
          				s_sql = "update g_zddqsbpzb set sblx=?,edgl=?,clnl=?,jxfs=?,yjscjkh=?,fzcdjkh=?,gzdyjjkh=?,sfbp=? where zdid=? and cldh=?";
        	        	params = new String[]{pz3,pz4,pz5,pz6,pz7,pz8,pz9,pz10,zdid,pz2};
          			}else if(isIn == false){
          				//主站里无该测量点的配置
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
      			
      			
      			//查询该终端g_zdkglsrsbpzb里面的每一路的输入量情况  最终得出最终的输入量
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
      			
      			//删除其它配置
      			s_sql = "delete g_zddqsbpzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                //更新g_zdgz中的pin字段 将有效的路数置1
                s_sql="update g_zdgz set pin=?,pout=? where zdid=?";
                params = new String[]{pin,pout,zdid};
                jdbcT.update(s_sql,params); 
                
          	}else if(s_Fdt.equals("F14")){
          		
          		//F14：电气设备启停控制参数 （1代专用）设置查询返回
          		cat.info("[Decode_0A]F14：电气设备启停控制参数 （1代专用）设置查询返回");
          		
          		
          		String csz = "";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1、执行依据
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
                    if("EE".equalsIgnoreCase(pz1)||"FF".equalsIgnoreCase(pz1)){
      					pz1="0";
      				}else{
      					pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
      				}
          			
          			
          			//2、最小温度
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz2)&&!"FF".equalsIgnoreCase(pz2)){
          				pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          				pz2 = String.valueOf(Integer.parseInt(pz2));
          			}else{
          				pz2="";	
          			}
          			
          		    //3、最大温度
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz3)&&!"FF".equalsIgnoreCase(pz3)){
          				pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          				pz3 = String.valueOf(Integer.parseInt(pz3));
          			}else{
          				pz3="";
          			}
          			
          		    //3、起始日期
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz4)&&!"FFFF".equalsIgnoreCase(pz4)){
          				pz4 = Util.tranFormat29(pz4);
          			}else{
          				pz4="";
          			}
          			
          			 //4、截止日期
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			if(!"EEEE".equalsIgnoreCase(pz5)&&!"FFFF".equalsIgnoreCase(pz5)){
          				pz5 = Util.tranFormat29(pz5);
          			}else{
          				pz5="";
          			}
          			
          		    //5、启动时间
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz6)&&!"FF".equalsIgnoreCase(pz6)){
          				pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
          				pz6 = String.valueOf(Integer.parseInt(pz6));
          			}else{
          				pz6="";
          			}
          			
          			 //6、停止时间
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
      			
      			//更新配置
      			s_sql = "update g_zdcldpzb set afn04f14=? where zdid=? and cldh=? ";
      			
      	        params = new String[]{csz,zdid,s_da};
                jdbcT.update(s_sql,params); 
                
                
                
          	}else if(s_Fdt.equals("F15")){
          	    //F15:终端测量点配置参数查询返回
          		cat.info("[Decode_0A]F15:水泵水位控制参数查询返回");
          		
          		s_sql = "select cldh from g_zdsbkzcsb where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		String cs_f="";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      			//1、序号
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2、测量点号
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3、输出硬件号
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if("EE".equalsIgnoreCase(pz3)){
          				pz3 = "";
          			}else if("CC".equalsIgnoreCase(pz3)){
          				pz3="CC";
          			}else{
          				pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			}
          			
          			
          		    //4、备用输出硬件号
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if("EE".equalsIgnoreCase(pz4)){
          				pz4 = "";
          			}else if("CC".equalsIgnoreCase(pz4)){
          				pz4="CC";
          			}else{
          				pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			}
          			
          			//5、同时工作使能
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
//          			if(!"EE".equalsIgnoreCase(pz5)){
//          				pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
//          			}
          			
          		    //6、主备切换时间-小时
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz6)){
          				if("CC".equalsIgnoreCase(pz6)){
          					pz6="0";
          				}else
          				    pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
          			}
          			
          		    //7、池体号码
      				String pz7 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz7)){
          				pz7 = Util.hexStrToDecStr(Util.convertStr(pz7));
          			}

          		    //8、水位档位
      				String pz8 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz8)){
          				pz8 = Util.hexStrToDecStr(Util.convertStr(pz8));
          			}

          		    //9、逻辑关系
      				String pz9 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz9)&&!"CC".equalsIgnoreCase(pz9)){
          				pz9 = Util.hexStrToDecStr(Util.convertStr(pz9));
          			}

          		    //10、另一个池体号码
      				String pz10 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz10)){
          				if("CC".equalsIgnoreCase(pz10)){
          					pz10="";
          				}else
          				    pz10 = Util.hexStrToDecStr(Util.convertStr(pz10));
          			}

          		    //11、水位档位
      				String pz11 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz11)){
          				if("CC".equalsIgnoreCase(pz11)){
          					pz11="";
          				}else
          				    pz11 = Util.hexStrToDecStr(Util.convertStr(pz11));
          			}

          		    //12、控制动作
      				String pz12 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz12)){
          				pz12 = Util.hexStrToDecStr(Util.convertStr(pz12));
          			}

          		    //13、最小温度
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

          		    //14、最大温度
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
          			
          		    //15、启动时间
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
          			
          		    //16、停止时间
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
          			
          			//写"配置表"
          			if(isIn == true){
          				//主站里有该测量点的配置
          				s_sql = "update g_zdsbkzcsb set scyjh=?,byscyjh=?,tsgzsn=?,zbqhsj=?,cthm1=?,swdw1=?,ljgx=?,cthm2=?,swdw2=?,kzdz=?,zxwd=?,zdwd=?,qdsj=?,tzsj=? where zdid=? and cldh=?";
        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)?"":pz16,zdid,pz2};
          			}else if(isIn == false){
          				//主站里无该测量点的配置
          				s_sql = "insert into g_zdsbkzcsb(id,zdid,xh,cldh,scyjh,byscyjh,tsgzsn,zbqhsj,cthm1,swdw1,ljgx,cthm2,swdw2,kzdz,zxwd,zdwd,qdsj,tzsj) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,"EE".equalsIgnoreCase(pz13)?"":pz13,"EE".equalsIgnoreCase(pz14)?"":pz14,"EEEE".equalsIgnoreCase(pz15)?"":pz15,"EEEE".equalsIgnoreCase(pz16)?"":pz16};
          			}
          			jdbcT.update(s_sql,params);
          			
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
          	    //写g_zdcldpzb的f15字段
      			s_sql="update g_zdcldpzb set afn04f15=? where zdid=? and cldh=0";
      			 params = new String[]{cs_f,zdid};
                 jdbcT.update(s_sql,params); 
                 
      			//删除其它配置
      			s_sql = "delete g_zdsbkzcsb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
               
      			
          	}else if(s_Fdt.equals("F16")){
          		//F16:终端测量点配置参数查询返回
          		cat.info("[Decode_0A]F16:风机电磁阀控制参数");
          		
          		s_sql = "select cldh from g_zdfjdcfkzcs where zdid=?";
      	        params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		String cs_f="";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      			    //1、序号
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2、测量点号
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3、输出硬件号
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz3)&&!"CC".equalsIgnoreCase(pz3)){
          				pz3 = Util.hexStrToDecStr(Util.convertStr(pz3));
          			}
          			
          			
          		    //4、备用输出硬件号
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz4)&&!"CC".equalsIgnoreCase(pz4)){
          				pz4 = Util.hexStrToDecStr(Util.convertStr(pz4));
          			}
          			
          		    //5、主备切换时间-小时
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz5)){
          				if("CC".equalsIgnoreCase(pz5)){
          					pz5="0";
          				}else
          				    pz5 = Util.hexStrToDecStr(Util.convertStr(pz5));
          			}
          			
          			//6、与水泵联动使能
      				String pz6 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
//          			if(!"EE".equalsIgnoreCase(pz6)){
//          				pz6 = Util.hexStrToDecStr(Util.convertStr(pz6));
//          			}
          			
          		    
          		    //7、最小温度
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

          		    //8、最大温度
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
          			
          		    //9、启动时间
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
          			
          		    //10、停止时间
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
          			
          		    //11、频率
      				String pz11 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			if(!"EE".equalsIgnoreCase(pz11)&&!"CC".equalsIgnoreCase(pz11)){
          				pz11 = Util.hexStrToDecStr(Util.convertStr(pz11));
          			}
          			
          		    //12、控制动作
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
          			
          			//写"配置表"
          			if(isIn == true){
          				//主站里有该测量点的配置
          				s_sql = "update g_zdfjdcfkzcs set scyjh=?,byscyjh=?,zbqhsj=?,sbldsn=?,zxwd=?,zdwd=?,qdsj=?,tzsj=?,pl=?,kzdz=? where zdid=? and cldh=?";
        	        	params = new String[]{"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12,zdid,pz2};
          			}else if(isIn == false){
          				//主站里无该测量点的配置
          				s_sql = "insert into g_zdfjdcfkzcs(id,zdid,xh,cldh,scyjh,byscyjh,zbqhsj,sbldsn,zxwd,zdwd,qdsj,tzsj,pl,kzdz) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,"EE".equalsIgnoreCase(pz3)?"":pz3,"EE".equalsIgnoreCase(pz4)?"":pz4,"EE".equalsIgnoreCase(pz5)?"":pz5,"EE".equalsIgnoreCase(pz6)?"":pz6,"EE".equalsIgnoreCase(pz7)?"":pz7,"EE".equalsIgnoreCase(pz8)?"":pz8,"EE".equalsIgnoreCase(pz9)?"":pz9,"EE".equalsIgnoreCase(pz10)?"":pz10,"EE".equalsIgnoreCase(pz11)?"":pz11,"EE".equalsIgnoreCase(pz12)?"":pz12};
          			}
          			jdbcT.update(s_sql,params);
          			
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
          	    //写g_zdcldpzb的f16字段
      			s_sql="update g_zdcldpzb set afn04f16=? where zdid=? and cldh=0";
      			 params = new String[]{cs_f,zdid};
                 jdbcT.update(s_sql,params); 
                 
      			//删除其它配置
      			s_sql = "delete g_zdfjdcfkzcs where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
          		
      			
      			
          	}else if(s_Fdt.equals("F17")){
          		
          		//F17:ORP,HP 上下限设置查询返回
          		cat.info("[Decode_0A]F17:ORP,HP 上下限设置");
          		s_sql = "select cldh from g_zdorpphxzb where zdid=?";
          		
          		params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1、序号
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2、测量点号
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3、ORP数值上限
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			
          			String pz3s[] = Util.tranFormat28(pz3);
          			if("1".equalsIgnoreCase(pz3s[1])){
          				pz3="-"+pz3s[0];
          			}else{
          				pz3=pz3s[0];
          			}
          			
          			//4、ORP数值下限
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			String pz4s[] = Util.tranFormat28(pz4);
          			if("1".equalsIgnoreCase(pz4s[1])){
          				pz4="-"+pz4s[0];
          			}else{
          				pz4=pz4s[0];
          			}
          			
          			//5、PH数值上限
      				String pz5 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz5 = Util.tranFormat30(pz5);
          			
          			
          		    //6、PH数值下限
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
          			
          			//写"终端测量点配置表"
          			if(isIn == true){
          				//主站里有该测量点的配置
          				s_sql = "update g_zdorpphxzb set orpsx=?,orpxx=?,phsx=?,phxx=? where zdid=? and cldh=?";
        	        	params = new String[]{pz3,pz4,pz5,pz6,zdid,pz2};
          			}else if(isIn == false){
          				//主站里无该测量点的配置
          				s_sql = "insert into g_zdorpphxzb(id,zdid,xh,cldh,orpsx,orpxx,phsx,phxx) "
        	        		+"values(S_ZDCSPZ_COMMONID.nextVal,?,?,?,?,?,?,?)";
        	        	params = new String[]{zdid,pz1,pz2,pz3,pz4,pz5,pz6};
          			}
          			jdbcT.update(s_sql,params);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      			//删除其它测量点的配置
      			s_sql = "delete g_zdorpphxzb where zdid=?  ";
      			if(i_sl>0){
      				s_sql += "and cldh not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                
                
          	}else if(s_Fdt.equals("F18")){
          		
          		//F18:超声波水位上下限设置查询返回
          		cat.info("[Decode_0A]F18:超声波水位上下限设置");
          		s_sql = "select pooleid from m_station_pool where stationid=(select stationid from g_zdgz where zdid=?)";
          		
          		params = new String[]{zdid};
          		List dnbList = jdbcT.queryForList(s_sql,params);
          		
          		String csz = "";
          		
          		//数量 bin
          		String sl = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			int i_sl = Integer.parseInt(Util.convertStr(sl),16);
      			
      			String cldIn = "";
      			for(int i=0;i<i_sl;i++){
      				//1、序号
      				String pz1 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz1 = Util.hexStrToDecStr(Util.convertStr(pz1));
          			
          			//2、测量点号
      				String pz2 = DADT.substring(idx_dadt,idx_dadt+4);
          			idx_dadt += 4;
          			pz2 = Util.hexStrToDecStr(Util.convertStr(pz2));
          			pz2 = String.valueOf(Integer.parseInt(pz2));
          			
          			//3、池深
      				String pz3 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz3= Util.tranFormat22(pz3);
          			
          			
          			//4、水位上限
      				String pz4 = DADT.substring(idx_dadt,idx_dadt+2);
          			idx_dadt += 2;
          			pz4= Util.tranFormat22(pz4);
          			
          			//5、水位下限
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
          			
          			//写"终端测量点配置表"
          			if(isIn == true){
          				//主站里有该测量点的配置
          				s_sql = "update m_station_pool set deep=?,upper=?,lower=?  where stationid=(select stationid from g_zdgz where zdid=?) and pooleid=?";
        	        	params = new String[]{pz3,pz4,pz5,zdid,pz2};
          			}else if(isIn == false){
          				//主站里无该测量点的配置
          				s_sql = "insert into m_station_pool(id,stationid,pooleid,pname,deep,upper,lower) "
        	        		+"values(S_POOLID.nextVal,(select stationid from g_zdgz where zdid=?),?,?,?,?,?)";
          				String pname="";
          				if("1".equalsIgnoreCase(pz2)){
          					pname="综合收集池";
          				}else if("2".equalsIgnoreCase(pz2)){
          					pname="一体化污水处理池";
          				}else if("3".equalsIgnoreCase(pz2)){
          					pname="沉淀池";
          				}else if("4".equalsIgnoreCase(pz2)){
          					pname="中间水池";
          				}
        	        	params = new String[]{zdid,pz2,pname,pz3,pz4,pz5};
          			}
          			jdbcT.update(s_sql,params);
          			
          			cldIn += "'"+pz2+"',";
          			          			
      			}
      			
      			//删除其它测量点的配置
      			s_sql = "delete m_station_pool where stationid=(select stationid from g_zdgz where zdid=?)  ";
      			if(i_sl>0){
      				s_sql += "and pooleid not in("+cldIn.substring(0,cldIn.length()-1)+")";
      			}
      	        params = new String[]{zdid};
                jdbcT.update(s_sql,params); 
                
                
                
          	}else if(s_Fdt.equals("F25")){
          	//F1:终端通信参数查询返回
          		cat.info("[Decode_0A]F25:电能模块变比参数");
          		//参数值(cs1;cs2;cs3)
//          		cs1:PT 
//	          		cs2:CT 
//	          		cs3:漏电流临界值
          		
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
      	        
      			
      		    //漏电流临界值 
      			String cs3 = DADT.substring(idx_dadt,idx_dadt+4);
      			cs3=Util.convertStr(cs3);
      			idx_dadt += 4;      			
      			cs3= Util.hexStrToDecStr(cs3);
      			

//      			csz = cs1+";"+cs2+";"+cs3;
      			
//      	        //写终端运行参数配置表
//      			s_sql = "update g_zdyxcspzb set AFN04F25=? where zdid=?";      			
//      	        params = new String[]{csz,zdid};
//                jdbcT.update(s_sql,params);
      			 //写终端运行参数配置表
      			s_sql = "update g_zdcldpzb "
						+"set pt=?,ct=?,ldlljz=? "
						+" where zdid=? and cldh=0";
				params = new String[]{cs1,cs2,cs3,zdid};
                jdbcT.update(s_sql,params);
          	}else if(s_Fdt.equals("F26")){
          	    //F26:神经网络算法使能参数
          		cat.info("[Decode_0A]F26:神经网络算法使能参数");
          		
          		String csz = "";
          		
          		//神经网络训使能
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			
      			csz=cs1;
      			
      		    //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F26=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
                
          	}else if(s_Fdt.equals("F27")){
          	    //F27:神经网络训练样本参数
          		cat.info("[Decode_0A]F27:神经网络训练样本参数");
          		
          		String csz = "";
          		
          		//输入参数1（温度）
          		String cs1 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs1= Util.tranFormat05(cs1);
      			
      			//输入参数2（OPR）
          		String cs2 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			String cs2_array[]=Util.tranFormat28(cs2);
      			cs2= cs2_array[1]+cs2_array[0];
      			
      			//输出参数3（风机频率）
          		String cs3 = DADT.substring(idx_dadt,idx_dadt+4);
      			idx_dadt += 4;
      			cs3= Util.tranFormat06(cs3);
      			
      			csz=cs1+";"+cs2+";"+cs3;
      			
      		    //写终端运行参数配置表
      			s_sql = "update g_zdyxcspzb set AFN04F27=? where zdid=?";      			
      	        params = new String[]{csz,zdid};
                jdbcT.update(s_sql,params);
                
          	}else if (s_Fdt.equalsIgnoreCase("F65")) { 
          		
    	        //F65:终端1类数据任务配置的查询返回
          		//参数值(cs1;...;cs5)
          	   	//cs1:上报周期(0-31)
          	   	//cs2:上报周期单位(0~3依次表示分、时、日、月)
          	   	//cs3:上报基准时间(年月日时分秒,yymmddhhmmss)
          	   	//cs4:启停标志(55启用、AA停用)
          	   	//cs5:任务数据项(P1@F1#P2@F2#...#Pn@Fn)
    	    	
          		String csz = "";
    	    	String rwh = s_da;//任务号
    	    	
    	    	String rwlx = "";//任务类型(1类)
	    		cat.info("[Decode_0A]F65:终端1类数据任务配置的查询返回(任务号"+rwh+")");
	    		rwlx = "1";
    	    	
    	    	   	       
    	        //任务上报周期及单位
    	        String rwzqjdw = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
      			rwzqjdw = Util.hexStrToBinStr(rwzqjdw,1);  
      			
    	        //上报周期(0-31)
    	        String cs1 =rwzqjdw.substring(2,8);
    	        cs1 = Util.binStrToDecStr(cs1);    	        
    	        
    	        //上报周期单位(0~3依次表示分、时、日、月)
    	        String cs2 = rwzqjdw.substring(0,2);
    	        cs2 = Util.binStrToDecStr(cs2);
    	        
    	        //上报基准时间(年月日时分秒,yymmddhhmmss)
    	        String cs3 = DADT.substring(idx_dadt,idx_dadt+12);
      			idx_dadt += 12;
      			cs3 = Util.tranFormat01_1(cs3);
      			
    	        //启停标志(55启用、AA停用)
    	        String cs4 = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;     			    	        
    	        
    	        //任务数据单元标识个数
    	        String sjdygs = DADT.substring(idx_dadt,idx_dadt+2);
      			idx_dadt += 2;
    	        int i_sjdygs = Integer.parseInt(sjdygs,16);
    	        

    			//看“终端任务配置表”对应任务是否已存在
    			s_sql = "select rwid from g_zdrwpzb "
    				+"where zdid=? and rwlx=? and rwh=?";
    			List lst = jdbcT.queryForList(s_sql,new String[]{zdid,rwlx,rwh});
    			int count = lst.size();
    			String rwid = "";
    			if(count>0){
    				rwid = String.valueOf(((Map)lst.get(0)).get("rwid"));
    			}
    			//SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
    			//写“终端任务配置表”
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
    			//删除其他的任务
    			s_sql = "delete g_zdrwpzb where zdid=? and rwh<>?";
    			params = new String[]{zdid,rwh};
    			jdbcT.update(s_sql,params);   	
    			
      	        
      	        //删除该任务的任务信息项
    			s_sql = "delete g_rwxxx where rwid=?";
    			params = new String[]{rwid};
    			jdbcT.update(s_sql,params);   	        
      	        
    	        for (int i = 1; i <= i_sjdygs; i++) {              	
    	        	//<-------------第i个数据单元标识下---------------->
    	            String dadt = DADT.substring(idx_dadt,idx_dadt+8);
          			idx_dadt += 8;
    	            
    	            String da = dadt.substring(0,4);
    	            da = Util.tranDA(Util.convertStr(da));
    	            da = "P" + da;
    	            String dt = dadt.substring(4,8);
    	            dt = Util.tranDT(Util.convertStr(dt));
    	            dt= "F" + dt;
    	            
    	            //写"任务信息项表"
    	            s_sql = "insert into g_rwxxx(rwid,xxdh,xxxdm,xh) "
    					+"values(?,?,?,?)";
    				params = new String[]{rwid,da,dt,String.valueOf(i)};
    				jdbcT.update(s_sql,params); 
    	                	            
    	        }
    	
    	    }
      	}
	}
	
	
}