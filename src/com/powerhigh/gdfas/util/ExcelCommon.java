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
 * Description: excel处理的公共类 <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-8-19
 * @author mohui
 * @version 1.0
 * Modifier：
 * Modify Time：
 */
public class ExcelCommon {
	public ExcelCommon(){
		
	}
	
	/**方法简述：导出excel文件
	 * @param wjm 		String 文件名
	 * @param zd 		String[][] zd[i][0]:字段;zd[i][1]:字段名称
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
		//一、文件名
		Calendar time_C = Calendar.getInstance();
	    Date time_D = time_C.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    String nowTime = formatter.format(time_D);
	    String fileName = wjm+"_"+nowTime+".xls";
	    //应用服务器上的实际URL
	    String fileURL = request.getRealPath("/excel/"+fileName);
	    //String fileRealName = "c:/" + fileName;
	    FileOutputStream fos = new FileOutputStream(fileURL); 
	    
	    //二、根据sql生成数据集
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
	    
	    //三、生成excel
	    //HSSFWorkbook:Excel电子表格中的Book
	    HSSFWorkbook wb = new HSSFWorkbook(); 
	    HSSFSheet sheet = wb.createSheet(); 
	    //若要支持中文，必需指定编码方式
	    wb.setSheetName(0, wjm,HSSFWorkbook.ENCODING_UTF_16);
	    
	    //1、字段名称(第一行)
	    int zdLen = zd.length;//字段数
	    HSSFRow rowOne = sheet.createRow(0);//第一行
	    for(int i=0;i<zdLen;i++){
	    	//第一行的各个列
	    	HSSFCell cell = rowOne.createCell((short)i);
		    
		    //若要支持中文，必需指定编码方式
		    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
		    cell.setCellValue(zd[i][1]);
	    }
	    
	    //2、数据内容(第二行...)
	    int listLen = list.size();
	    for(int i=0;i<listLen;i++){
	    	//第n行
	    	HSSFRow rowN = sheet.createRow(i+1);//第n行
	    	HashMap dataM = (HashMap)list.get(i);
	    	
	    	//按字段顺序
	    	for(int j=0;j<zdLen;j++){
	    		//第n行的各个列
		    	HSSFCell cell = rowN.createCell((short)j);
			    
			    //若要支持中文，必需指定编码方式
			    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
			    cell.setCellValue(String.valueOf(dataM.get(zd[j][0])));
	    	}
	    }
	    
	    //三、输出到文件
	    wb.write(fos); 
	    fos.close();
	    
	    return fileName;
	}
	
	/**方法简述：导出excel文件
	 * @param wjm 		String 文件名
	 * @param zd 		String[][] zd[i][0]:字段;zd[i][1]:字段名称
	 * @param list 		ArrayList list里放HashMap [除了标以外的数据]
	 * @param request 	HttpServletRequest
	 * 
	 * @return fileName String
	 */
	public static String export(String wjm,String[][] zd,
			ArrayList list,HttpServletRequest request) throws Exception{
		
		if(zd == null){
			throw new Exception("Export Excel Error!");
		}
		//一、文件名
		Calendar time_C = Calendar.getInstance();
	    Date time_D = time_C.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    String nowTime = formatter.format(time_D);
	    String fileName = wjm+"_"+nowTime+".xls";
	    //应用服务器上的实际URL
	    String fileURL = request.getRealPath("/excel/"+fileName);
	    //String fileRealName = "c:/" + fileName;
	    FileOutputStream fos = new FileOutputStream(fileURL); 
	    
	    	    
	    //二、生成excel
	    //HSSFWorkbook:Excel电子表格中的Book
	    HSSFWorkbook wb = new HSSFWorkbook(); 
	    HSSFSheet sheet = wb.createSheet(); 
	    //若要支持中文，必需指定编码方式
	    wb.setSheetName(0, wjm,HSSFWorkbook.ENCODING_UTF_16);
	    
	    //1、字段名称(第一行)
	    int zdLen = zd.length;//字段数
	    HSSFRow rowOne = sheet.createRow(0);//第一行
	    for(int i=0;i<zdLen;i++){
	    	//第一行的各个列
	    	HSSFCell cell = rowOne.createCell((short)i);
		    
		    //若要支持中文，必需指定编码方式
		    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
		    cell.setCellValue(zd[i][1]);
	    }
	    
	    //2、数据内容(第二行...)
	    int listLen = list.size();
	    for(int i=0;i<listLen;i++){
	    	//第n行
	    	HSSFRow rowN = sheet.createRow(i+1);//第n行
	    	HashMap dataM = (HashMap)list.get(i);
	    	
	    	//按字段顺序
	    	for(int j=0;j<zdLen;j++){
	    		//第n行的各个列
		    	HSSFCell cell = rowN.createCell((short)j);
			    
			    //若要支持中文，必需指定编码方式
			    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
			    cell.setCellValue(String.valueOf(dataM.get(zd[j][0])));
	    	}
	    }
	    
	    //三、输出到文件
	    wb.write(fos); 
	    fos.close();
	    
	    return fileName;
	}
	
	
	/**方法简述：导出excel文件
	 * @param wjm 		String 文件名
	 * @param list 		ArrayList list里放ArrayList [包括标题的所有数据]
	 * @param request 	HttpServletRequest
	 * 
	 * @return fileName String
	 */
	public static String export(String wjm,
			ArrayList list,HttpServletRequest request) throws Exception{
		
		
		//一、文件名
		Calendar time_C = Calendar.getInstance();
	    Date time_D = time_C.getTime();
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	    String nowTime = formatter.format(time_D);
	    String fileName = wjm+"_"+nowTime+".xls";
	    //应用服务器上的实际URL
	    String fileURL = request.getRealPath("/excel/"+fileName);
	    //String fileRealName = "c:/" + fileName;
	    FileOutputStream fos = new FileOutputStream(fileURL); 
	    
	    	    
	    //二、生成excel
	    //HSSFWorkbook:Excel电子表格中的Book
	    HSSFWorkbook wb = new HSSFWorkbook(); 
	    HSSFSheet sheet = wb.createSheet(); 
	    //若要支持中文，必需指定编码方式
	    wb.setSheetName(0, wjm,HSSFWorkbook.ENCODING_UTF_16);
	    
	    
	    int listLen = list.size();
	    for(int i=0;i<listLen;i++){
	    	//第n行
	    	HSSFRow rowN = sheet.createRow(i);//第n行
	    	ArrayList dataL = (ArrayList)list.get(i);
	    	
	    	//按字段顺序
	    	for(int j=0;j<dataL.size();j++){
	    		//第n行的各个列
		    	HSSFCell cell = rowN.createCell((short)j);
			    
			    //若要支持中文，必需指定编码方式
			    cell.setEncoding(HSSFWorkbook.ENCODING_UTF_16);					    
			    cell.setCellValue(String.valueOf(dataL.get(j)));
	    	}
	    }
	    
	    //三、输出到文件
	    wb.write(fos); 
	    fos.close();
	    
	    return fileName;
	}
	public static void main(String[] args) throws Exception{
		
		String s_sql = "select bjid,fkzdid from biaoji where fkzdid='1912'";
		String[][] zd = {{"bjid","表计"},{"fkzdid","终端"}};
		ExcelCommon.export("测试",zd,s_sql,null);
	}
	
}
