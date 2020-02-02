package com.powerhigh.gdfas.parse;


import java.io.FileInputStream;
import java.util.*;

import org.apache.log4j.Category;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.util.*;

import com.powerhigh.gdfas.module.Dispatch;


/** 
 * Description: ����汾Զ������<p>
 * Copyright:    Copyright   2015 LongShine<p>
 * ��дʱ��: 2015-9-14
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */
 
public class Decode_0F {	
	//������־
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
	        CMConfig.SYSTEM_DOWNLOADBUFFER_KEY);//ÿ���������ݵĴ�С
	public static String send_dalay = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_SEND_DELAY);//������ʱʱ��
	public static String next_dalay = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_NEXT_DELAY);//������һ֡��ʱʱ��
	public static String overtime = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_OVERTIME);//��ʱʱ��
	public static String resend_count = CMXmlR.getResource(CMConfig.SYSTEM_SECTION,
	        CMConfig.SYSTEM_RESEND_COUNT);//�ط�����
	
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
	* @Description: TODO(ͨ�������ļ�ȡ���ļ���Ӳ���汾�ź�����汾�ţ�16���Ʊ�ʾ)
	* @param @param fileName
	* @param @return
	* @param @throws Exception    �趨�ļ�
	* @return String[]    �������� ��0��Ӳ���汾�ţ���1������汾��
	* @throws
	 */
	public static String[] getFileVersion(String fileName) throws Exception{
		String fileURL = fileName;
		FileInputStream in = new FileInputStream(fileURL);
		int len = in.available();
		byte[] bt = new byte[len];
		in.read(bt);
		
		//����汾��
		byte[] yjbb=new byte[2];
		System.arraycopy(bt,768,yjbb,0,2);
		//Ӳ���汾��
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
	* @Description: TODO(ͨ���ļ�������ţ�ȡ�ö�Ӧ��֡����)
	* @param @param fileName�ļ�·��
	* @param @param xh���0-n
	* @param @return
	* @param @throws Exception    �趨�ļ�
	* @return byte[]    ��������
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
//			throw new Exception("�޴������ļ�:"+fileName);
//		}
////		HashMap tempH = (HashMap)hm.get(fileName+"["+xh+"]");
//		HashMap tempH = (HashMap)hm.get(fileName);
//		if(tempH == null){
//			throw new Exception("�޴������ļ�:"+fileName);
//		}
//		returnBT = (byte[])tempH.get(xh);
//		if(returnBT == null){
//			throw new Exception("�޴������ļ�:"+fileName);
//		}
		
		if(hm == null){
			throw new Exception("�޴������ļ�:"+fileName);
		}
		
		returnBT = (byte[])hm.get(fileName+"["+xh+"]");
		if(returnBT == null){
			throw new Exception("�޴������ļ�:"+fileName);
		}
		
		return returnBT;
	}
	
	/**��������������
	   * @param xzqxm 		String 		����������
	   * @param zddz  		String 		�ն˵�ַ
	   * @param sSJZ  		String 		������
	   * @param txfs 		String 		ͨ�ŷ�ʽ
	   * 
	   * @return void
	   */
	  private static void send(String xzqxm,String zddz,String sSJZ,String txfs,String seq_sjzfs,JdbcTemplate jdbcT) throws Exception{
	  	Dispatch dispatch = (Dispatch)Context.ctx.getBean("dispatchService");
	  	String gylx = Util.getZdgylx(xzqxm, zddz, jdbcT);// ��Լ����:1:���;2:����;3:������
		String SJZ = "";
		if (gylx.equals("3")) {
			// 2009-10-18������������
			SJZ = Util.addSG(xzqxm, zddz, sSJZ);
		} else {
			SJZ = sSJZ;
		}
	  	dispatch.downDispatch(txfs,gylx,xzqxm,zddz,SJZ,seq_sjzfs,"");
	  	
	  }
	  /**
		 * �����ն�ȡ֡��ż�����ȡ����֡��ţ���֡��ż������ĺ���λ��
		 * 
		 * @param xzqxm
		 *            String ����������
		 * @param zddz
		 *            String �ն˵�ַ
		 * @return zxh ֡���
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
	  
	  /**���������������ļ�����(AFN=0FH F2)
	   * @param xzdqm String  ����������
	   * @param zddz  String �ն˵�ַ
	   * @param fileName  String �����ļ���
	   * @param xh  String ��ǰ�κ�
	   * @param con  Connection
	   * 
	   * @return void
	 * @throws Exception
	   */
	@SuppressWarnings({ "unused"})
	public static void respondDownload(String xzqxm,String zddz, String fileName, String xh,
			JdbcTemplate jdbcT) throws Exception {
		//����״̬
		String sjzt = "";
		//ͨ������ GPRS��02����SERIAL��06��
		String tdlx = "3";

		byte[] fileBuffer = getBuffer(fileName, xh);
		
       //XH��δ��������Ӧ�е�����û�ж�ȡ��
		if (fileBuffer == null) {
            //���¡��ն��������ñ�����ʧ�ܣ��ļ�δ��ȡ
            String s_sql = "update  g_zdsjpzb set zt=3,sj=sysdate,sjzt='�����ȡʧ��' where zdid=(select zdid from g_zdgz where xzqxm='" + xzqxm
					+ "' and zddz='" + zddz + "')";
			jdbcT.update(s_sql);
            return;
		}
		// ȡ��ǰ��ʱ�䣨��ʽ��YYMMDDHHMMSS��
		String rq = Util.getNowTime();

		String seq_sjzfs = null;

		//��������֡(ʮ�������ַ�)
		String sSJZ = "";

		//һ��������
//		String sContr = "02";
		String sContr = "40";

		//������ַ��
		String sAddr = "";
		String sAddr1 = Util.convertStr(xzqxm);
		String sAddr2 = Util.convertStr(zddz);
		String sAddr3 = "00";
		sAddr = sAddr1 + sAddr2 + sAddr3;

		//������·�û�������
		String sUSERDATA = "";
		//1��Ӧ�ù�����
		String sAFN = "0F"; //�ļ�����

		
		// 2��֡������(TpV=1;FIR=1;FIN=1;CON=1)
		String sSEQ = "";
		// ȡ���ն˵�֡��ż�����������֡���
		int iZdpfc = CMContext.getZdpfc(xzqxm, zddz);
		int iZdpseq = getZdpseq(iZdpfc);
		
		sSEQ = "F" + Integer.toHexString(iZdpseq);
//		sSEQ = "F1";//Ϊ���2.0.8����
		String sSEQ1 = Integer.toHexString(iZdpseq);		
		//��Ӧ֡���=����֡���
//		sSEQ = "6" + pseq;

		//3�����ݵ�Ԫ��ʶDADT(DA=P0;DT=F2),����Զ������
		String sDA = Util.getDA("P0");
		sDA = Util.convertStr(sDA);

		String sDT = Util.getDT("F2");
		sDT = Util.convertStr(sDT);

		String sDADT = sDA + sDT;

		//4�����ݵ�ԪDATA
		String sDATA = "";
		//1)�ܶ���
		
//		int count = ((HashMap)hm.get(fileName)).keySet().size();
//		int count=i_count;
		int count=fillHM(fileName);
		sDATA += Util.convertStr(Util.decStrToHexStr(count, 2));
		//2)��i�α�ʶ��ƫ�� i=0~n-1
		sDATA += Util.convertStr(Util.decStrToHexStr(xh, 2));
		//3)�ļ����ݳ���
		sDATA += Util.convertStr(Util.decStrToHexStr(buffer, 2));
		
		//4)�ļ�����,��λ���ȴ�
		String file = Util.bytetostrs(fileBuffer);
		//20170617����1024�ֽڵ�  ����AA��
		if(file.length()<(Integer.parseInt(buffer)*2)){
			int l=2048-file.length();
			for(int m=0;m<l;m++){
				file=file+"A";
			}
		}
		
		//2016-09-12Ϊ�����һ��Ľ������⣬ǿ�ƽ��ļ��еġ�AA����"00"�����滻
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
				//20161212��
				newfile+=s;
			}
			file=newfile;
		}
		//2016-09-12Ϊ��Ϸ�����򣬽��������ÿһ֡����ӡ�0000��
		sDATA += file+"0000";

		sUSERDATA = sAFN + sSEQ + sDADT + sDATA;

		//У��������
		String sCSDATA = sContr + sAddr + sUSERDATA;

		//�ġ�У����
		String sCS = Util.getCS(sCSDATA);

		//�塢���ݳ���
		int iLEN = sCSDATA.length();
		iLEN = iLEN * 2 + 1;
		String sLEN = Util.decStrToHexStr(iLEN, 2);
		sLEN = Util.convertStr(sLEN);

		sSJZ = "68" + sLEN + sLEN + "68" + sContr + sAddr + sUSERDATA + sCS
				+ "16";

		cat.info("sSJZ:" + sSJZ);
		
		// д������֡���ͱ��������ݱ�ʶ�ӱ�
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
				Util.convertStr(sDA) + Util.convertStr(sDT), "AFN0FF2�����ļ�����" };
		jdbcT.update(sSql, params);
		// д���������ݴ�� ��¼��źͷ���ʱ��
		writeCsszzcb(seq_sjzfs, "AFN0FF2", xh, jdbcT);
		//����
		send(xzqxm, zddz, sSJZ, tdlx,seq_sjzfs,jdbcT);

	}
	
	
	/**
	 * �����������·����������ļ�����F2(AFN=0FH)
	 * 
	 * @param s_xzqxm
	 *            String ����������
	 * @param s_zddz
	 *            String �ն˵�ַ
	 * @param s_sjzfsseq
	 *            String ����֡��������
	 * @param con
	 *            Connection ���ݿ�����
	 * 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public static void decodeDownload(String s_xzqxm, String s_zddz,String s_sjzfsseq,JdbcTemplate jdbcT) throws Exception{
//		cat.info("Զ���ļ����䷵�ش���");
//		Thread.sleep(Long.parseLong(send_dalay));
		
		String s_sql = "select *  from g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
		String[] params = new String[] { s_xzqxm, s_zddz};
		List lst = jdbcT.queryForList(s_sql, params);
		//�ܶ���
		String zds=String.valueOf(((Map) lst.get(0))
				.get("zds"));
		// ��ǰ���
		String dqxh = String.valueOf(((Map) lst.get(0))
				.get("dqdh"));
		//�ļ���
		String fileName=String.valueOf(((Map) lst.get(0))
				.get("cxm"));
		if(Integer.parseInt(zds)>Integer.parseInt(dqxh)){
			//������һ֡
			//�ȴ�ʱ��2017-08-07��һ֡��ʱ100
			Thread.sleep(Long.parseLong(next_dalay));
			System.out.println("��ʼ���ͳ����ļ�>>>>>>>���--------------"+dqxh);
			respondDownload(s_xzqxm, s_zddz, fileName, dqxh, jdbcT);
			//�����ط��߳�
			Decode_0F_ReSend ss=new Decode_0F_ReSend(s_xzqxm, s_zddz, jdbcT, dqxh);
			new Thread(ss).start();

			
		}else if(Integer.parseInt(zds)==Integer.parseInt(dqxh)){
			//���η������
			s_sql = "update g_zdsjpzb set zt=?  where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
			params = new String[] { "1",s_xzqxm, s_zddz};
			jdbcT.update(s_sql, params);
		}
		
		
	}
	
	/**
	 * ��������������ط���������(AFN=0FH)
	 * 
	 * @param s_xzqxm
	 *            String ����������
	 * @param s_zddz
	 *            String �ն˵�ַ
	 * @param s_sjzfsseq
	 *            String ����֡��������
	 * @param con
	 *            Connection ���ݿ�����
	 * 
	 * @return void
	 */
	@SuppressWarnings("rawtypes")
	public static void decodeReDownload(String s_xzqxm, String s_zddz,String s_sjzfsseq,JdbcTemplate jdbcT) throws Exception{
//		cat.info("Զ���ļ����䷵�ش���");
//		Thread.sleep(Long.parseLong(send_dalay));
		
		String s_sql = "select *  from g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
		String[] params = new String[] { s_xzqxm, s_zddz};
		List lst = jdbcT.queryForList(s_sql, params);
		//�ܶ���
		String zds=String.valueOf(((Map) lst.get(0))
				.get("zds"));
		// ��ǰ���
		String dqxh = String.valueOf(((Map) lst.get(0))
				.get("dqdh"));
		//�ļ���
		String fileName=String.valueOf(((Map) lst.get(0))
				.get("cxm"));
		if(Integer.parseInt(zds)>Integer.parseInt(dqxh)){
			//������һ֡
			//�ȴ�ʱ��
			//Thread.sleep(Long.parseLong(send_dalay));
			System.out.println("�ط������ļ�>>>>>>>���--------------"+dqxh);
			respondDownload(s_xzqxm, s_zddz, fileName, dqxh, jdbcT);
			
		}else if(Integer.parseInt(zds)==Integer.parseInt(dqxh)){
			//���η������
			s_sql = "update g_zdsjpzb set zt=?  where zdid=(select zdid from g_zdgz where xzqxm=? and zddz=?)";
			params = new String[] { "1",s_xzqxm, s_zddz};
			jdbcT.update(s_sql, params);
		}
		
		
	}

//	/**������������Ӧ�ն��������ص�����F2(AFN=0FH)
//	   * @param xzqxm String ����������
//	   * @param zddz  String �ն˵�ַ
//	   * @param pseq  String ����֡���
//	   * @param cxmklx  String ����ģ������
//	   * @param fileName  String �����ļ���
//	   * @param xh  String ���
//	   * @param moduleID  int ģ��ID
//	   * @param con  Connection
//	   * 
//	   * @return void
//	 * @throws Exception
//	   */
//	private static void respondDownload(String xzqxm, String zddz, String pseq,
//			String cxmklx, String fileName, String xh, int moduleID,
//			JdbcTemplate jdbcT) throws Exception {
//		//����״̬
//		String sjzt = "";
//		//ͨ������ GPRS��02����SERIAL��06��
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
//			//�·�����ȡ������
//			operation.sendXzqx(xzqxm, zddz, tdlx);
//
//			//д���ն��������ñ�
//			sjzt = "����!��ȡ�����γ�������";
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
//		//��������֡(ʮ�������ַ�)
//		String sSJZ = "";
//
//		//һ��������
//		String sContr = "00";
//
//		//������ַ��
//		String sAddr = "";
//		String sAddr1 = Util.convertStr(xzqxm);
//		String sAddr2 = Util.convertStr(zddz);
//		String sAddr3 = "00";
//		sAddr = sAddr1 + sAddr2 + sAddr3;
//
//		//������·�û�������
//		String sUSERDATA = "";
//		//1��Ӧ�ù�����
//		String sAFN = "0F"; //�ļ�����
//
//		//2��֡������(TpV=0;FIR=1;FIN=1;CON=0)
//		String sSEQ = "";
//		//��Ӧ֡���=����֡���
//		sSEQ = "6" + pseq;
//
//		//3�����ݵ�Ԫ��ʶDADT(DA=P0;DT=F3),����Զ������
//		String sDA = Util.getDA("P0");
//		sDA = Util.convertStr(sDA);
//
//		String sDT = Util.getDT("F3");
//		sDT = Util.convertStr(sDT);
//
//		String sDADT = sDA + sDT;
//
//		//4�����ݵ�ԪDATA
//		String sDATA = "";
//		//1)����ģ������
//		sDATA += Util.add(cxmklx, 1, "0");
//		//2)�ļ��κ�
//		sDATA += Util.convertStr(Util.decStrToHexStr(xh, 2));
//		//3)�ļ����ݳ���
//		sDATA += Util.convertStr(Util.decStrToHexStr(fileBuffer.length, 2));
//		//4)�ļ�����,��λ���ȴ�
//		String file = Util.bytetostrs(fileBuffer);
//		sDATA += file;
//
//		sUSERDATA = sAFN + sSEQ + sDADT + sDATA;
//
//		//У��������
//		String sCSDATA = sContr + sAddr + sUSERDATA;
//
//		//�ġ�У����
//		String sCS = Util.getCS(sCSDATA);
//
//		//�塢���ݳ���
//		int iLEN = sCSDATA.length();
//		iLEN = iLEN * 2 + 1;
//		String sLEN = Util.decStrToHexStr(iLEN, 2);
//		sLEN = Util.convertStr(sLEN);
//
//		sSJZ = "68" + sLEN + sLEN + "68" + sContr + sAddr + sUSERDATA + sCS
//				+ "16";
//
//		cat.info("sSJZ:" + sSJZ);
//		//�ܶ���
//		int count = ((HashMap)hm.get(fileName)).keySet().size();
//		//д���ն��������ñ�
//		sjzt = "���������ļ�" + fileName + "�ĵ�" + xh + "��(��" + count + "��)";
//		String s_sql = "delete zdsjpzb where xzqxm='" + xzqxm + "' and zddz='"
//				+ zddz + "'";
//		jdbcT.update(s_sql);
//
//		s_sql = "insert into zdsjpzb(xzqxm,zddz,sjzt,sj) " + "values('" + xzqxm
//				+ "','" + zddz + "','" + sjzt + "',sysdate)";
//		jdbcT.update(s_sql);
//
//		//����
//		send(xzqxm, zddz, sSJZ, tdlx);
//
//	}
	
	/**
	 * ����������Զ�����ط��ش���F1��F2��F3(AFN=0FH)
	 * 
	 * @param xzqxm
	 *            String ����������
	 * @param zddz
	 *            String �ն˵�ַ
	 * @param pseq
	 *            String ����֡���
	 * @param s_PF
	 *            String ���ݵ�Ԫ��ʶPnFn
	 * @param s_sjzfsseq
	 *            String ����֡��������
	 * @param sSJZ
	 *            String ����֡
	 * @param data
	 *            String ���ݵ�Ԫ
	 * @param moduleID
	 *            int ģ��ID
	 * @param con
	 *            Connection ���ݿ�����
	 * 
	 * @return void
	 */
//	public static void decodeDownload(String xzqxm, String zddz,String pseq,
//	  					String s_PF,String s_sjzfsseq,String sSJZ,
//						String data,int moduleID,JdbcTemplate jdbcT) throws Exception{
//		cat.info("Զ�����ط��ش���");
//		String s_sql = "";
//		if(s_PF.equalsIgnoreCase("P0F1")){			
//	        //�ն���ӦԶ����������[F1ȫ��ȷ��]
//	        s_sql = "update g_sjzfsb set zt='01',fhsj=sysdate,sxsjz='" + sSJZ +
//	            "' where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);
//
//	        s_sql = "update g_sjzfssjdybszb set cwdm='XX' "
//	            + "where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);       
//
//	    }else if(s_PF.equalsIgnoreCase("P0F2")){
//	        //�ն���ӦԶ����������[F2ȫ������]
//	        s_sql = "update sjzfsb set zt='03',fhsj=sysdate,sxsjz='" + sSJZ +
//	             "' where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);       
//
//	        s_sql = "update sjzfssjdybszb set cwdm='YY' "
//	            + "where sjzfsseq='" + s_sjzfsseq + "'";
//	        jdbcT.update(s_sql);       
//
//	    }else if(s_PF.equalsIgnoreCase("P0F3")){
//	    	//�ն������������ݼ�״̬[F3]
//	    	String mlh = data.substring(0,2);//�������ص������
//	    									 //01:�������س���02:�����������;03:�����������
//	    	
//	    	if(mlh.equals("01")){
//	    		String cxmklx = data.substring(2,4);//����ģ������
//	    		byte[] bt = Util.strstobyte(data);
//	    		byte[] bt_wjm = new byte[32];//�ļ�������λ����
//	    		byte[] bt_xh = new byte[2];//����κţ���λ����
//	    		System.arraycopy(bt,2,bt_wjm,0,32);
//	    		System.arraycopy(bt,34,bt_xh,0,2);
//	    		String fileName = Util.bytestoASCII(bt_wjm);//��λ����
//	    		fileName = Util.convertStrODD(fileName);
//	    		fileName = fileName.trim();
//	    		
//	    		int xh = Util.byte2int(new byte[]{bt_xh[0],bt_xh[1],0x00,0x00},0);
//	    		
//	    		respondDownload(xzqxm, zddz,pseq,cxmklx,fileName,String.valueOf(xh),moduleID,jdbcT);
//	    			    		
//	    		
//	    	}else{
//	    		String sjzt = "";//����״̬
//	    		if(mlh.equals("02")){
//	    			sjzt = "�����������";
//	    		}else if(mlh.equals("03")){
//	    			sjzt = "���������ɹ�";
//	    		}
//	    		//д���ն��������ñ�
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
//		String fileName = "E:\\Project\\��ˮ����\\��ˮ1������2.1.5.bin";
//		String fileName = "D:\\Downloads\\Temp\\ep\\2017\\7b\\609\\820cdd7c-d79c-4ea8-9c3a-b571a75cc7c5.bin";//����1.0.5
		String fileName = "D:\\Downloads\\Temp\\ep\\2017\\6b\\707\\8974cf83-cb1b-4a1f-a0ff-be92a0c81cc9.bin";//һ��v2.1.5
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

