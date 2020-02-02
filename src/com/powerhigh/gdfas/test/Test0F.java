package com.powerhigh.gdfas.test;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.powerhigh.gdfas.rmi.operation;
import com.powerhigh.gdfas.util.Util;

public class Test0F {

	 public static void main(String[] args) throws Exception  
	    {  
//	        try  
//	        {  
//	            FileInputStream fis = new FileInputStream("D:/update.bin");  
//	            java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();  
//	            byte[] buff = new byte[1024];  
//	            int len = 0;  
//	            while ((len = fis.read(buff)) != -1)  
//	            {  
//	                bos.write(buff, 0, len);  
//	            }  
//	            // �õ�ͼƬ���ֽ�����  
//	            byte[] result = bos.toByteArray();  
//	            System.out.println("++++" + bytesToHexString(result));  
//	            // �ֽ�����ת��ʮ������  
//	            String str = bytesToHexString(result);  
//	            /* 
//	             * ��ʮ�����ƴ����浽txt�ļ��� 
//	             */  
//	            PrintWriter pw = new PrintWriter(  
//	                    new FileWriter("D:/update.txt"));  
//	            pw.println(str);  
//	            pw.close();  
//	        }  
//	        catch (IOException e)  
//	        {  
//	            e.printStackTrace();  
//	        }  
		 System.out.println(Util.hexStrToDecStr(Util.convertStr("00C00300")));
		 System.out.println(Util.convertStr(Util.decStrToHexStr(245760, 4)));
	    }  
	  
	    /* 
	     * ʵ���ֽ�������ʮ�����Ƶ�ת������һ 
	     */  
	    public static String byte2HexStr(byte[] b)  
	    {  
	        String hs = "";  
	        String stmp = "";  
	        for (int n = 0; n < b.length; n++)  
	        {  
	            stmp = (Integer.toHexString(b[n] & 0XFF));  
	            if (stmp.length() == 1)  
	                hs = hs + "0" + stmp;  
	            else  
	                hs = hs + stmp;  
	        }  
	        return hs.toUpperCase();  
	    }
	    
	    public static String bbhToStr(String bbh){
	    	String bac="";
	    	if(null==bbh||!bbh.contains("v")||bbh.length()!=6){
	    		return "EEEE";
	    	}
	    	bac=(bbh.replace("v", "")).replace(".", "");
	    	bac="0"+bac;
	    	return bac;
	    }
	  
	   
	  
	    /* 
	     * ʵ���ֽ�������ʮ�����Ƶ�ת���ķ����� 
	     */  
	    public static String bytesToHexString(byte[] src)  
	    {  
	        StringBuilder stringBuilder = new StringBuilder("");  
	        if (src == null || src.length <= 0)  
	        {  
	            return null;  
	        }  
	        for (int i = 0; i < src.length; i++)  
	        {  
	            int v = src[i] & 0xFF;  
	            String hv = Integer.toHexString(v);  
	            if (hv.length() < 2)  
	            {  
	                stringBuilder.append(0);  
	            }  
	            stringBuilder.append(hv);  
	        }  
	        return stringBuilder.toString();  
	    }  

}
