/*
 * Created on 2015-8-19
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.powerhigh.gdfas.util;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Description: excel����Ĺ����� <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-8-19
 * @author mohui
 * @version 1.0
 * Modifier��
 * Modify Time��
 */
public class ExcelCommon {
	public ExcelCommon(){
		
	}
	
	/**��������������excel�ļ�
	 * @param wjm 		String �ļ���
	 * @param zd 		String[][] zd[i][0]:�ֶ�;zd[i][1]:�ֶ�����
	 * @param s_sql 	String 
	 * @param request 	HttpServletRequest
	 * 
	 * @return fileName String
	 */
	public static String export(String wjm,String[][] zd,
			String s_sql,HttpServletRequest request) throws Exception{
		
		if(zd == null){
			throw new Exception("Export Excel Error!");
		}
		//һ���ļ���
		Calendar time_C = Calendar.getInstance();
	    Date time_D = time_C.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    String nowTime = formatter.format(time_D);
	    String fileName = wjm+"_"+nowTime+".xls";
	    //Ӧ�÷������ϵ�ʵ��URL
	    String fileURL = request.getRealPath("/excel/"+fileName);
	    //String fileRealName = "c:/" + fileName;
	    FileOutputStream fos = new FileOutputStream(fileURL); 
	    
	    //��������sql�������ݼ�
	    Connection con = null;
	    ArrayList list = null;
	    try{
	    	con = CMDb.GetConnection();
	    	list = (ArrayList)CMDb.getCollection(con,s_sql);
	    }catch(Exception e){
	    	throw e;
	    }finally{
	    	CMDb.close(con);
	    }
	    
	    //��������excel
	    //HSSFWorkbook:Excel���ӱ���е�Book
	    HSSFWorkbook wb = new HSSFWorkbook(); 
	    HSSFSheet sheet = wb.createSheet(); 
	    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
	    wb.setSheetName(0, wjm,HSSFWorkbook.ENCODING_UTF_16);
	    
	    //1���ֶ�����(��һ��)
	    int zdLen = zd.length;//�ֶ���
	    HSSFRow rowOne = sheet.createRow(0);//��һ��
	    for(int i=0;i<zdLen;i++){
	    	//��һ�еĸ�����
	    	HSSFCell cell = rowOne.createCell((short)i);
		    
		    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
		    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
		    cell.setCellValue(zd[i][1]);
	    }
	    
	    //2����������(�ڶ���...)
	    int listLen = list.size();
	    for(int i=0;i<listLen;i++){
	    	//��n��
	    	HSSFRow rowN = sheet.createRow(i+1);//��n��
	    	HashMap dataM = (HashMap)list.get(i);
	    	
	    	//���ֶ�˳��
	    	for(int j=0;j<zdLen;j++){
	    		//��n�еĸ�����
		    	HSSFCell cell = rowN.createCell((short)j);
			    
			    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
			    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
			    cell.setCellValue(String.valueOf(dataM.get(zd[j][0])));
	    	}
	    }
	    
	    //����������ļ�
	    wb.write(fos); 
	    fos.close();
	    
	    return fileName;
	}
	
	/**��������������excel�ļ�
	 * @param wjm 		String �ļ���
	 * @param zd 		String[][] zd[i][0]:�ֶ�;zd[i][1]:�ֶ�����
	 * @param list 		ArrayList list���HashMap [���˱����������]
	 * @param request 	HttpServletRequest
	 * 
	 * @return fileName String
	 */
	public static String export(String wjm,String[][] zd,
			ArrayList list,HttpServletRequest request) throws Exception{
		
		if(zd == null){
			throw new Exception("Export Excel Error!");
		}
		//һ���ļ���
		Calendar time_C = Calendar.getInstance();
	    Date time_D = time_C.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    String nowTime = formatter.format(time_D);
	    String fileName = wjm+"_"+nowTime+".xls";
	    //Ӧ�÷������ϵ�ʵ��URL
	    String fileURL = request.getRealPath("/excel/"+fileName);
	    //String fileRealName = "c:/" + fileName;
	    FileOutputStream fos = new FileOutputStream(fileURL); 
	    
	    	    
	    //��������excel
	    //HSSFWorkbook:Excel���ӱ���е�Book
	    HSSFWorkbook wb = new HSSFWorkbook(); 
	    HSSFSheet sheet = wb.createSheet(); 
	    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
	    wb.setSheetName(0, wjm,HSSFWorkbook.ENCODING_UTF_16);
	    
	    //1���ֶ�����(��һ��)
	    int zdLen = zd.length;//�ֶ���
	    HSSFRow rowOne = sheet.createRow(0);//��һ��
	    for(int i=0;i<zdLen;i++){
	    	//��һ�еĸ�����
	    	HSSFCell cell = rowOne.createCell((short)i);
		    
		    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
		    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
		    cell.setCellValue(zd[i][1]);
	    }
	    
	    //2����������(�ڶ���...)
	    int listLen = list.size();
	    for(int i=0;i<listLen;i++){
	    	//��n��
	    	HSSFRow rowN = sheet.createRow(i+1);//��n��
	    	HashMap dataM = (HashMap)list.get(i);
	    	
	    	//���ֶ�˳��
	    	for(int j=0;j<zdLen;j++){
	    		//��n�еĸ�����
		    	HSSFCell cell = rowN.createCell((short)j);
			    
			    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
			    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
			    cell.setCellValue(String.valueOf(dataM.get(zd[j][0])));
	    	}
	    }
	    
	    //����������ļ�
	    wb.write(fos); 
	    fos.close();
	    
	    return fileName;
	}
	
	
	/**��������������excel�ļ�
	 * @param wjm 		String �ļ���
	 * @param list 		ArrayList list���ArrayList [�����������������]
	 * @param request 	HttpServletRequest
	 * 
	 * @return fileName String
	 */
	public static String export(String wjm,
			ArrayList list,HttpServletRequest request) throws Exception{
		
		
		//һ���ļ���
		Calendar time_C = Calendar.getInstance();
	    Date time_D = time_C.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    String nowTime = formatter.format(time_D);
	    String fileName = wjm+"_"+nowTime+".xls";
	    //Ӧ�÷������ϵ�ʵ��URL
	    String fileURL = request.getRealPath("/excel/"+fileName);
	    //String fileRealName = "c:/" + fileName;
	    FileOutputStream fos = new FileOutputStream(fileURL); 
	    
	    	    
	    //��������excel
	    //HSSFWorkbook:Excel���ӱ���е�Book
	    HSSFWorkbook wb = new HSSFWorkbook(); 
	    HSSFSheet sheet = wb.createSheet(); 
	    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
	    wb.setSheetName(0, wjm,HSSFWorkbook.ENCODING_UTF_16);
	    
	    
	    int listLen = list.size();
	    for(int i=0;i<listLen;i++){
	    	//��n��
	    	HSSFRow rowN = sheet.createRow(i);//��n��
	    	ArrayList dataL = (ArrayList)list.get(i);
	    	
	    	//���ֶ�˳��
	    	for(int j=0;j<dataL.size();j++){
	    		//��n�еĸ�����
		    	HSSFCell cell = rowN.createCell((short)j);
			    
			    //��Ҫ֧�����ģ�����ָ�����뷽ʽ
			    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
			    cell.setCellValue(String.valueOf(dataL.get(j)));
	    	}
	    }
	    
	    //����������ļ�
	    wb.write(fos); 
	    fos.close();
	    
	    return fileName;
	}
	public static void main(String[] args) throws Exception{
		
		String s_sql = "select bjid,fkzdid from biaoji where fkzdid='1912'";
		String[][] zd = {{"bjid","���"},{"fkzdid","�ն�"}};
		ExcelCommon.export("����",zd,s_sql,null);
	}
	
}
