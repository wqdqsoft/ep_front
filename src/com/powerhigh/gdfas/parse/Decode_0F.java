package com.powerhigh.gdfas.parse;


import java.io.FileInputStream;
import java.util.*;

import org.apache.log4j.Category;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.util.*;

import com.powerhigh.gdfas.module.Dispatch;


/** 
 * Description: 程序版本远程下载<p>
 * Copyright:    Copyright   2015 LongShine<p>
 * 编写时间: 2015-9-14
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
 
public class Decode_0F {	
	//加载日志
	@SuppressWarnings("unused")
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.parse.Decode_0F.class);
//	static {
//	  PropertyConfigurator.configure(resource);
//	}
	
	@SuppressWarnings("rawtypes")
	public static HashMap hm = null;	//key:fileName;
										//value:HashMap(key:xh;value:byte[])
	public static String buffer = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_DOWNLOADBUFFER_KEY);//每次下载数据的大小
	public static String send_dalay = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_SEND_DELAY);//发送延时时间
	public static String next_dalay = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_NEXT_DELAY);//发送下一帧延时时间
	public static String overtime = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_OVERTIME);//超时时间
	public static String resend_count = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_RESEND_COUNT);//重发次数
	
	public static String URL = "D:\\";
//	private  static int i_count = 0;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static int fillHM(String fileName) throws Exception{
		//System.out.println("fileName:"+fileName);
		cat.info("fileName:"+fileName);
		if(hm == null){
			hm = new HashMap();
		}
//		String fileURL = URL + fileName;
		String fileURL = fileName;
		FileInputStream in = new FileInputStream(fileURL);
		int len = in.available();
		////System.out.println(len);
		byte[] bt = new byte[len];
		in.read(bt);
		////System.out.println(len);
		
		int pjz = Integer.parseInt(buffer);
		int zs = len/pjz;
		int xs = len%pjz;
		
		int count = 0;
		if(zs==0&&xs==0){
			count = 0;
		}else if(zs==0&&xs!=0){
			count = 1;
		}else if(zs!=0&&xs==0){
			count = zs;
		}else if(zs!=0&&xs!=0){
			count = zs + 1;
		}
		
		for(int i=0;i<count-1;i++){
			byte[] bt_i = new byte[pjz];
			System.arraycopy(bt,i*pjz,bt_i,0,pjz);
			hm.put(fileName+"["+String.valueOf(i)+"]",bt_i);
		}
		byte[] bt_end = new byte[len-(count-1)*pjz];
		System.arraycopy(bt,(count-1)*pjz,bt_end,0,len-(count-1)*pjz);
		hm.put(fileName+"["+(count-1)+"]",bt_end);
		
//		i_count = count;
//		System.out.println("count-----------"+count);
		return count;
	}
	
	/**
	 * 
	* @Title: getFileVersion
	* @Description: TODO(通过升级文件取得文件的硬件版本号和软件版本号，16进制表示)
	* @param @param fileName
	* @param @return
	* @param @throws Exception    设定文件
	* @return String[]    返回类型 【0】硬件版本号，【1】软件版本号
	* @throws
	 */
	public static String[] getFileVersion(String fileName) throws Exception{
		String fileURL = fileName;
		FileInputStream in = new FileInputStream(fileURL);
		int len = in.available();
		byte[] bt = new byte[len];
		in.read(bt);
		
		//软件版本号
		byte[] yjbb=new byte[2];
		System.arraycopy(bt,768,yjbb,0,2);
		//硬件版本号
		byte[] rjbb=new byte[2];
		System.arraycopy(bt,770,rjbb,0,2);
		
//		System.out.println("yjbb"+Util.bytetostrs(yjbb));
//		System.out.println("rjbb"+Util.bytetostrs(rjbb));
		
		String v[]=new String[2];
		v[0]=Util.bytetostrs(yjbb);
		v[1]=Util.bytetostrs(rjbb);
		return v;
	}
	
//	public synchronized static int fillHM(String fileName,byte[] bt) throws Exception{
//		
//		if(hm == null){
//			hm = new HashMap();
//		}
//		HashMap tempH = new HashMap();
//		int len = bt.length;		
//		int pjz = Integer.parseInt(buffer);
//		int zs = len/pjz;
//		int xs = len%pjz;
//		
//		int count = 0;
//		if(zs==0&&xs==0){
//			count = 0;
//		}else if(zs==0&&xs!=0){
//			count = 1;
//		}else if(zs!=0&&xs==0){
//			count = zs;
//		}else if(zs!=0&&xs!=0){
//			count = zs + 1;
//		}
//		
//		for(int i=0;i<count-1;i++){
//			byte[] bt_i = new byte[pjz];
//			System.arraycopy(bt,i*pjz,bt_i,0,pjz);
//			tempH.put(String.valueOf(i+1),bt_i);
//		}
//		byte[] bt_end = new byte[len-(count-1)*pjz];
//		System.arraycopy(bt,(count-1)*pjz,bt_end,0,len-(count-1)*pjz);
//		tempH.put(String.valueOf(count),bt_end);
//		
//		hm.put(fileName,tempH);
//				
//		return count;
//	}
	
	/**
	 * 
	* @Title: getBuffer
	* @Description: TODO(通过文件名和序号，取得对应的帧数据)
	* @param @param fileName文件路径
	* @param @param xh序号0-n
	* @param @return
	* @param @throws Exception    设定文件
	* @return byte[]    返回类型
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	private synchronized static byte[] getBuffer(String fileName,String xh) throws Exception{
		byte[] returnBT = null;
		if(hm == null){
			hm = new HashMap();
			try{
				fillHM(fileName);
			}catch(Exception e){
				e.printStackTrace();
				return null;
			}
			returnBT = (byte[])hm.get(fileName+"["+xh+"]");
			
		}else{
			returnBT = (byte[])hm.get(fileName+"["+xh+"]");
			if(returnBT == null){
				try{
					fillHM(fileName);
				}catch(Exception e){
					e.printStackTrace();
					return null;
				}
				returnBT = (byte[])hm.get(fileName+"["+xh+"]");
				
			}			
		}
		
//		if(hm == null){
//			throw new Exception("无此下载文件:"+fileName);
//		}
////		HashMap tempH = (HashMap)hm.get(fileName+"["+xh+"]");
//		HashMap tempH = (HashMap)hm.get(fileName);
//		if(tempH == null){
//			throw new Exception("无此下载文件:"+fileName);
//		}
//		returnBT = (byte[])tempH.get(xh);
//		if(returnBT == null){
//			throw new Exception("无此下载文件:"+fileName);
//		}
		
		if(hm == null){
			throw new Exception("无此下载文件:"+fileName);
		}
		
		returnBT = (byte[])hm.get(fileName+"["+xh+"]");
		if(returnBT == null){
			throw new Exception("无此下载文件:"+fileName);
		}
		
		return returnBT;
	}
	
	/**方法简述：发送
	   * @param xzqxm 		String 		行政区县码
	   * @param zddz  		String 		终端地址
	   * @param sSJZ  		String 		数据祯
	   * @param txfs 		String 		通信方式
	   * 
	   * @return void
	   */
	  private static void send(String xzqxm,String zddz,String sSJZ,String txfs,String seq_sjzfs,JdbcTemplate jdbcT) throws Exception{
	  	Dispatch dispatch = (Dispatch)Context.ctx.getBean("dispatchService");
	  	String gylx = Util.getZdgylx(xzqxm, zddz, jdbcT);// 规约类型:1:浙规;2:国规;3:浙版国规
		String SJZ = "";
		if (gylx.equals("3")) {
			// 2009-10-18外面套上浙规壳
			SJZ = Util.addSG(xzqxm, zddz, sSJZ);
		} else {
			SJZ = sSJZ;
		}
	  	dispatch.downDispatch(txfs,gylx,xzqxm,zddz,SJZ,seq_sjzfs,"");
	  	
	  }
	  /**
		 * 根据终端取帧序号计数器取启动帧序号（即帧序号计数器的后四位）
		 * 
		 * @param xzqxm
		 *            String 行政区县码
		 * @param zddz
		 *            String 终端地址
		 * @return zxh 帧序号
		 */
		private static int getZdpseq(int iZdpfc) throws Exception {
			int i_zdpseq = 0;
			String s_pfc = Integer.toHexString(iZdpfc);
			if (s_pfc.length() < 2) {
				s_pfc = "0" + s_pfc;
			}

			String s_pseq = s_pfc.substring(1);
			if (s_pseq.equalsIgnoreCase("a")) {
				s_pseq = "10";
			} else if (s_pseq.equalsIgnoreCase("b")) {
				s_pseq = "11";
			} else if (s_pseq.equalsIgnoreCase("c")) {
				s_pseq = "12";
			} else if (s_pseq.equalsIgnoreCase("d")) {
				s_pseq = "13";
			} else if (s_pseq.equalsIgnoreCase("e")) {
				s_pseq = "14";
			} else if (s_pseq.equalsIgnoreCase("f")) {
				s_pseq = "15";
			}

			i_zdpseq = Integer.parseInt(s_pseq);

			return i_zdpseq;
		}
		
		private static void writeCsszzcb(String seq, String sjxdm, String sjxz,
				JdbcTemplate jdbcT) throws Exception {
			String sSql = "insert into g_csszzcb(sjzfsseq,sjxdm,sjz) "
					+ "values(?,?,?)";
			String[] params = new String[] { seq, sjxdm, sjxz };
			jdbcT.update(sSql, params);

		}
	  
	  /**方法简述：发送文件数据(AFN=0FH F2)
	   * @param xzdqm String  行政区县码
	   * @param zddz  String 终端地址
	   * @param fileName  String 下载文件名
	   * @param xh  String 当前段号
	   * @param con  Connection
	   * 
	   * @return void
	 * @throws Exception
	   */
	@SuppressWarnings({ "unused"})
	public static void respondDownload(String xzqxm,String zddz, String fileName, String xh,
			JdbcTemplate jdbcT) throws Exception {
		//升级状态
		String sjzt = "";
		//通道类型 GPRS（02），SERIAL（06）
		String tdlx = "3";

		byte[] fileBuffer = getBuffer(fileName, xh);
		
       //XH还未结束，但应有的数据没有读取到
		if (fileBuffer == null) {
            //更新“终端升级配置表”升级失败，文件未读取
            String s_sql = "update  g_zdsjpzb set zt=3,sj=sysdate,sjzt='程序读取失败' where zdid=(select zdid from g_zdgz where xzqxm='" + xzqxm
					+ "' and zddz='" + zddz + "')";
			jdbcT.update(s_sql);
            return;
		}
		// 取当前的时间（格式：YYMMDDHHMMSS）
		String rq = Util.getNowTime();

		String seq_sjzfs = null;

		//整个数据帧(十六进制字符)
		String sSJZ = "";

		//一、控制码
//		String sContr = "02";
		String sContr = "40";

		//二、地址域
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "00";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		//三、链路用户数据域
		String sUSERDATA = "";
		//1、应用功能码
		String sAFN = "0F"; //文件传输

		
		// 2、帧序列域(TpV=1;FIR=1;FIN=1;CON=1)
		String sSEQ = "";
		// 取该终端的帧序号计数器和启动帧序号
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);
		int iZdpseq = getZdpseq(iZdpfc);
		
		sSEQ = "F" + Integer.toHexString(iZdpseq);
//		sSEQ = "F1";//为配合2.0.8升级
		String sSEQ1 = Integer.toHexString(iZdpseq);		
		//响应帧序号=启动帧序号
//		sSEQ = "6" + pseq;

		//3、数据单元标识DADT(DA=P0;DT=F2),程序远程下载
		String sDA = Util.getDA("P0");
		sDA = Util.convertStr(sDA);

		String sDT = Util.getDT("F2");
		sDT = Util.convertStr(sDT);

		String sDADT = sDA + sDT;

		//4、数据单元DATA
		String sDATA = "";
		//1)总段数
		
//		int count = ((HashMap)hm.get(fileName)).keySet().size();
//		int count=i_count;
		int count=fillHM(fileName);
		sDATA += Util.convertStr(Util.decStrToHexStr(count, 2));
		//2)第i段标识或偏移 i=0~n-1
		sDATA += Util.convertStr(Util.decStrToHexStr(xh, 2));
		//3)文件内容长度
		sDATA += Util.convertStr(Util.decStrToHexStr(buffer, 2));
		
		//4)文件内容,低位在先传
		String file = Util.bytetostrs(fileBuffer);
		//20170617不足1024字节的  补“AA”
		if(file.length()<(Integer.parseInt(buffer)*2)){
			int l=2048-file.length();
			for(int m=0;m<l;m++){
				file=file+"A";
			}
		}
		
		//2016-09-12为解决第一针的解析问题，强制将文件中的“AA”和"00"互相替换
		if("0".equalsIgnoreCase(xh)){
			String newfile="";
			for(int i=0;i<file.length();i=i+2){
				String s="";
				if((i+2)>=file.length()){
					s=file.substring(i,file.length());
				}else{
					s=file.substring(i,i+2);
				}
//				if("00".equalsIgnoreCase(s)){
//					newfile+="AA";
//				}else if("AA".equalsIgnoreCase(s)){
//					newfile+="00";
//				}else{
//					newfile+=s;
//				}
				//20161212改
				newfile+=s;
			}
			file=newfile;
		}
		//2016-09-12为配合发哥程序，讲错就做，每一帧后面加“0000”
		sDATA += file+"0000";

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA;

		//校验数据域
		String sCSDATA = sContr + sAddr + sUSERDATA;

		//四、校验码
		String sCS = Util.getCS(sCSDATA);

		//五、数据长度
		int iLEN = sCSDATA.length();
		iLEN = iLEN * 2 + 1;
		String sLEN = Util.decStrToHexStr(iLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = "68" + sLEN + sLEN + "68" + sContr + sAddr + sUSERDATA + sCS
				+ "16";

		cat.info("sSJZ:" + sSJZ);
		
		// 写“数据帧发送表”及“数据标识子表”
		seq_sjzfs = Util.getSeqSjzfs(jdbcT);
		String sSql = "insert into g_sjzfsb(sjzfsseq,zdid,gnm,seq,pfc,zt,qdzfssb,fssj,xxsjz) "
				+ "values(?,(select zdid from G_ZDGZ where xzqxm=? and zddz=?),"
				+ "?,?,?,'02',?,sysdate,?)";
		String[] params = new String[] { seq_sjzfs, xzqxm, zddz, "0F",
				sSEQ1.toUpperCase(), Util.decStrToHexStr(iZdpfc, 1),
				rq.substring(4, 12), sSJZ.substring(0, 44) };
		jdbcT.update(sSql, params);

		sSql = "insert into g_sjzfssjdybszb(sjzfsmxseq,sjzfsseq,gnm,sjdybsdm,sjdybsz,sjdybsmc) "
				+ "values(seq_sjzfsmx.nextval,?,?,?,?,?)";
		params = new String[] { seq_sjzfs, sAFN, "P0F2",
				Util.convertStr(sDA) + Util.convertStr(sDT), "AFN0FF2程序文件传输" };
		jdbcT.update(sSql, params);
		// 写参数设置暂存表 记录序号和发送时间
		writeCsszzcb(seq_sjzfs, "AFN0FF2", xh, jdbcT);
		//发送
		send(xzqxm, zddz, sSJZ, tdlx,seq_sjzfs,jdbcT);

	}
	
	
	/**
	 * 方法简述：下发升级程序文件报文F2(AFN=0FH)
	 * 
	 * @param s_xzqxm
	 *            String 行政区县码
	 * @param s_zddz
	 *            String 终端地址
	 * @param s_sjzfsseq
	 *            String 数据帧发送序列
	 * @param con
	 *            Connection 数据库连接
	 * 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public static void decodeDownload(String s_xzqxm, String s_zddz,String s_sjzfsseq,JdbcTemplate jdbcT) throws Exception{
//		cat.info("远程文件传输返回处理");
//		Thread.sleep(Long.parseLong(send_dalay));
		
		String s_sql = "select *  from g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
		String[] params = new String[] { s_xzqxm, s_zddz};
		List lst = jdbcT.queryForList(s_sql, params);
		//总段数
		String zds=String.valueOf(((Map) lst.get(0))
				.get("zds"));
		// 当前序号
		String dqxh = String.valueOf(((Map) lst.get(0))
				.get("dqdh"));
		//文件名
		String fileName=String.valueOf(((Map) lst.get(0))
				.get("cxm"));
		if(Integer.parseInt(zds)>Integer.parseInt(dqxh)){
			//发送下一帧
			//等待时间2017-08-07下一帧延时100
			Thread.sleep(Long.parseLong(next_dalay));
			System.out.println("开始发送程序文件>>>>>>>序号--------------"+dqxh);
			respondDownload(s_xzqxm, s_zddz, fileName, dqxh, jdbcT);
			//启用重发线程
			Decode_0F_ReSend ss=new Decode_0F_ReSend(s_xzqxm, s_zddz, jdbcT, dqxh);
			new Thread(ss).start();

			
		}else if(Integer.parseInt(zds)==Integer.parseInt(dqxh)){
			//本次发送完毕
			s_sql = "update g_zdsjpzb set zt=?  where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
			params = new String[] { "1",s_xzqxm, s_zddz};
			jdbcT.update(s_sql, params);
		}
		
		
	}
	
	/**
	 * 方法简述：针对重发升级报文(AFN=0FH)
	 * 
	 * @param s_xzqxm
	 *            String 行政区县码
	 * @param s_zddz
	 *            String 终端地址
	 * @param s_sjzfsseq
	 *            String 数据帧发送序列
	 * @param con
	 *            Connection 数据库连接
	 * 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public static void decodeReDownload(String s_xzqxm, String s_zddz,String s_sjzfsseq,JdbcTemplate jdbcT) throws Exception{
//		cat.info("远程文件传输返回处理");
//		Thread.sleep(Long.parseLong(send_dalay));
		
		String s_sql = "select *  from g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
		String[] params = new String[] { s_xzqxm, s_zddz};
		List lst = jdbcT.queryForList(s_sql, params);
		//总段数
		String zds=String.valueOf(((Map) lst.get(0))
				.get("zds"));
		// 当前序号
		String dqxh = String.valueOf(((Map) lst.get(0))
				.get("dqdh"));
		//文件名
		String fileName=String.valueOf(((Map) lst.get(0))
				.get("cxm"));
		if(Integer.parseInt(zds)>Integer.parseInt(dqxh)){
			//发送下一帧
			//等待时间
			//Thread.sleep(Long.parseLong(send_dalay));
			System.out.println("重发程序文件>>>>>>>序号--------------"+dqxh);
			respondDownload(s_xzqxm, s_zddz, fileName, dqxh, jdbcT);
			
		}else if(Integer.parseInt(zds)==Integer.parseInt(dqxh)){
			//本次发送完毕
			s_sql = "update g_zdsjpzb set zt=?  where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
			params = new String[] { "1",s_xzqxm, s_zddz};
			jdbcT.update(s_sql, params);
		}
		
		
	}

//	/**方法简述：响应终端数据下载的请求F2(AFN=0FH)
//	   * @param xzqxm String 行政区县码
//	   * @param zddz  String 终端地址
//	   * @param pseq  String 启动帧序号
//	   * @param cxmklx  String 程序模块类型
//	   * @param fileName  String 下载文件名
//	   * @param xh  String 序号
//	   * @param moduleID  int 模块ID
//	   * @param con  Connection
//	   * 
//	   * @return void
//	 * @throws Exception
//	   */
//	private static void respondDownload(String xzqxm, String zddz, String pseq,
//			String cxmklx, String fileName, String xh, int moduleID,
//			JdbcTemplate jdbcT) throws Exception {
//		//升级状态
//		String sjzt = "";
//		//通道类型 GPRS（02），SERIAL（06）
//		String tdlx = "02";
//
//		byte[] fileBuffer = getBuffer(fileName, xh);
//		if (moduleID == CMConfig.GPRS_MODULE_ID) {
//			//GPRS=02
//			tdlx = "02";
//
//		} else if (moduleID == CMConfig.SERIAL_MODULE_ID) {
//			//SERIAL=06
//			tdlx = "06";
//		}
//
//		if (fileBuffer == null) {
//			//下发升级取消命令
//			operation.sendXzqx(xzqxm, zddz, tdlx);
//
//			//写“终端升级配置表”
//			sjzt = "出错!已取消本次程序升级";
//			String s_sql = "delete zdsjpzb where xzqxm='" + xzqxm
//					+ "' and zddz='" + zddz + "'";
//			jdbcT.update(s_sql);
//
//			s_sql = "insert into zdsjpzb(xzqxm,zddz,sjzt,sj) " + "values('"
//					+ xzqxm + "','" + zddz + "','" + sjzt + "',sysdate)";
//			jdbcT.update(s_sql);
//
//			return;
//		}
//
//		String seq_sjzfs = null;
//
//		//整个数据帧(十六进制字符)
//		String sSJZ = "";
//
//		//一、控制码
//		String sContr = "00";
//
//		//二、地址域
//		String sAddr = "";
//		String sAddr1 = Util.convertStr(xzqxm);
//		String sAddr2 = Util.convertStr(zddz);
//		String sAddr3 = "00";
//		sAddr = sAddr1 + sAddr2 + sAddr3;
//
//		//三、链路用户数据域
//		String sUSERDATA = "";
//		//1、应用功能码
//		String sAFN = "0F"; //文件传输
//
//		//2、帧序列域(TpV=0;FIR=1;FIN=1;CON=0)
//		String sSEQ = "";
//		//响应帧序号=启动帧序号
//		sSEQ = "6" + pseq;
//
//		//3、数据单元标识DADT(DA=P0;DT=F3),程序远程下载
//		String sDA = Util.getDA("P0");
//		sDA = Util.convertStr(sDA);
//
//		String sDT = Util.getDT("F3");
//		sDT = Util.convertStr(sDT);
//
//		String sDADT = sDA + sDT;
//
//		//4、数据单元DATA
//		String sDATA = "";
//		//1)程序模块类型
//		sDATA += Util.add(cxmklx, 1, "0");
//		//2)文件段号
//		sDATA += Util.convertStr(Util.decStrToHexStr(xh, 2));
//		//3)文件内容长度
//		sDATA += Util.convertStr(Util.decStrToHexStr(fileBuffer.length, 2));
//		//4)文件内容,低位在先传
//		String file = Util.bytetostrs(fileBuffer);
//		sDATA += file;
//
//		sUSERDATA = sAFN + sSEQ + sDADT + sDATA;
//
//		//校验数据域
//		String sCSDATA = sContr + sAddr + sUSERDATA;
//
//		//四、校验码
//		String sCS = Util.getCS(sCSDATA);
//
//		//五、数据长度
//		int iLEN = sCSDATA.length();
//		iLEN = iLEN * 2 + 1;
//		String sLEN = Util.decStrToHexStr(iLEN, 2);
//		sLEN = Util.convertStr(sLEN);
//
//		sSJZ = "68" + sLEN + sLEN + "68" + sContr + sAddr + sUSERDATA + sCS
//				+ "16";
//
//		cat.info("sSJZ:" + sSJZ);
//		//总段数
//		int count = ((HashMap)hm.get(fileName)).keySet().size();
//		//写“终端升级配置表”
//		sjzt = "已下载完文件" + fileName + "的第" + xh + "段(共" + count + "段)";
//		String s_sql = "delete zdsjpzb where xzqxm='" + xzqxm + "' and zddz='"
//				+ zddz + "'";
//		jdbcT.update(s_sql);
//
//		s_sql = "insert into zdsjpzb(xzqxm,zddz,sjzt,sj) " + "values('" + xzqxm
//				+ "','" + zddz + "','" + sjzt + "',sysdate)";
//		jdbcT.update(s_sql);
//
//		//发送
//		send(xzqxm, zddz, sSJZ, tdlx);
//
//	}
	
	/**
	 * 方法简述：远程下载返回处理F1、F2、F3(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String 行政区县码
	 * @param zddz
	 *            String 终端地址
	 * @param pseq
	 *            String 启动帧序号
	 * @param s_PF
	 *            String 数据单元标识PnFn
	 * @param s_sjzfsseq
	 *            String 数据帧发送序列
	 * @param sSJZ
	 *            String 数据帧
	 * @param data
	 *            String 数据单元
	 * @param moduleID
	 *            int 模块ID
	 * @param con
	 *            Connection 数据库连接
	 * 
	 * @return void
	 */
//	public static void decodeDownload(String xzqxm, String zddz,String pseq,
//	  					String s_PF,String s_sjzfsseq,String sSJZ,
//						String data,int moduleID,JdbcTemplate jdbcT) throws Exception{
//		cat.info("远程下载返回处理");
//		String s_sql = "";
//		if(s_PF.equalsIgnoreCase("P0F1")){			
//	        //终端响应远程下载命令[F1全部确认]
//	        s_sql = "update g_sjzfsb set zt='01',fhsj=sysdate,sxsjz='" + sSJZ +
//	            "' where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);
//
//	        s_sql = "update g_sjzfssjdybszb set cwdm='XX' "
//	            + "where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);       
//
//	    }else if(s_PF.equalsIgnoreCase("P0F2")){
//	        //终端响应远程下载命令[F2全部否认]
//	        s_sql = "update sjzfsb set zt='03',fhsj=sysdate,sxsjz='" + sSJZ +
//	             "' where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);       
//
//	        s_sql = "update sjzfssjdybszb set cwdm='YY' "
//	            + "where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);       
//
//	    }else if(s_PF.equalsIgnoreCase("P0F3")){
//	    	//终端请求下载数据及状态[F3]
//	    	String mlh = data.substring(0,2);//请求下载的命令号
//	    									 //01:请求下载程序；02:程序下载完成;03:程序升级完成
//	    	
//	    	if(mlh.equals("01")){
//	    		String cxmklx = data.substring(2,4);//程序模块类型
//	    		byte[] bt = Util.strstobyte(data);
//	    		byte[] bt_wjm = new byte[32];//文件名，低位在先
//	    		byte[] bt_xh = new byte[2];//程序段号，低位在先
//	    		System.arraycopy(bt,2,bt_wjm,0,32);
//	    		System.arraycopy(bt,34,bt_xh,0,2);
//	    		String fileName = Util.bytestoASCII(bt_wjm);//低位在先
//	    		fileName = Util.convertStrODD(fileName);
//	    		fileName = fileName.trim();
//	    		
//	    		int xh = Util.byte2int(new byte[]{bt_xh[0],bt_xh[1],0x00,0x00},0);
//	    		
//	    		respondDownload(xzqxm, zddz,pseq,cxmklx,fileName,String.valueOf(xh),moduleID,jdbcT);
//	    			    		
//	    		
//	    	}else{
//	    		String sjzt = "";//升级状态
//	    		if(mlh.equals("02")){
//	    			sjzt = "程序下载完成";
//	    		}else if(mlh.equals("03")){
//	    			sjzt = "程序升级成功";
//	    		}
//	    		//写“终端升级配置表”
//			    s_sql = "delete zdsjpzb where xzqxm='"
//			  		+xzqxm+"' and zddz='"+zddz+"'";
//		        jdbcT.update(s_sql);       
//				  
//				s_sql = "insert into zdsjpzb(xzqxm,zddz,sjzt,sj) "
//				  	+"values('"+xzqxm+"','"+zddz+"','"+sjzt+"',sysdate)";
//		        jdbcT.update(s_sql);       
//	    	}
//	    	
//	    }
//		
//	}
	  
	 
	
	public static void main(String[] args) throws Exception{
//		System.out.println(Util.decStrToHexStr(buffer, 2));
//		System.out.println(Util.hexStrToDecStr("0400"));
//		String fileURL = "D:\\176-A1 V2.0.8 (WQHB7.820.001-1)updata_16071405.bin";
//		FileInputStream in = new FileInputStream(fileURL);
//		int len = in.available();
//		byte[] bt = new byte[len];
//		in.read(bt);
//		
//		byte[] yjbb=new byte[2];
//		System.arraycopy(bt,768,yjbb,0,2);
//		byte[] rjbb=new byte[2];
//		System.arraycopy(bt,770,rjbb,0,2);
//		System.out.println("yjbb"+Util.bytetostrs(yjbb));
//		System.out.println("rjbb"+Util.bytetostrs(rjbb));
//		String fileName = "E:\\Project\\五水共治\\污水1代升级2.1.5.bin";
//		String fileName = "D:\\Downloads\\Temp\\ep\\2017\\7b\\609\\820cdd7c-d79c-4ea8-9c3a-b571a75cc7c5.bin";//二代1.0.5
		String fileName = "D:\\Downloads\\Temp\\ep\\2017\\6b\\707\\8974cf83-cb1b-4a1f-a0ff-be92a0c81cc9.bin";//一代v2.1.5
		System.out.println(Decode_0F.fillHM(fileName));
		
//		
		byte[] bt = Decode_0F.getBuffer(fileName,"10");
//		if(bt == null){
//			System.out.println("0=null");
//		}else{
//			String binstr="";
//			binstr=Util.bytetostrs(bt);
//			System.out.println(2048-binstr.length());
//			
//			if(binstr.length()<2048){
//				int l=2048-binstr.length();
//				for(int m=0;m<l;m++){
//					binstr=binstr+"A";
//				}
//			}
//			System.out.println(binstr);
//		}
//		
//		bt = Decode_0F.getBuffer(fileName,"2");
		if(bt == null){
			System.out.println("10=null");
		}else{
			System.out.println("10="+Util.bytetostrs(bt));
		}
//		
//		bt = Decode_0F.getBuffer(fileName,"3");
//		if(bt == null){
//			System.out.println("3=null");
//		}else{
//			System.out.println("3="+Util.bytetostrs(bt));
//		}
//		
//		bt = Decode_0F.getBuffer(fileName,"68");
//		if(bt == null){
//			System.out.println("68=null");
//		}else{
//			System.out.println("68="+Util.bytetostrs(bt));
//		}
		

	}
				
	
}

