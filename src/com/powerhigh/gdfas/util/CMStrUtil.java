package com.powerhigh.gdfas.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

/**
 * Description: �ַ�ת�������� <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier��
 * Modify Time��
 */

public class CMStrUtil
{

   public static String formatNullStr(String srcStr)
   {
     if(srcStr==null)srcStr="";
     return srcStr;
   }

   public static int parseInt(String srcStr,int defaultValue,int minValue)
   {
      int resultValue = defaultValue;
      if(srcStr.equals(""))return defaultValue;
      try {
        minValue = Integer.parseInt(srcStr);
      } catch (Exception e) {
      }
      if(resultValue<=minValue)resultValue=defaultValue;
      return minValue;

   }

   public static Timestamp parseTimestamp(String strTimestamp,String format) throws Exception
   {
     SimpleDateFormat sdf=new SimpleDateFormat();
     sdf.applyPattern(format);
     Date date=sdf.parse(strTimestamp);
     sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
     return Timestamp.valueOf(sdf.format(date));
   }

  /**
   *
   * @param str ����0���ַ���
   * @param length����0��ĳ���
   * @return ������length<������str�ĳ��ȣ�ֱ�ӷ���str,������length������str�ĳ��ȣ�����strǰ�油0
   * ��addZero("1234",3)="1234", addZero("1234", 8)="00001234"
   * @throws java.lang.Exception
   */
   public static String addZero(String str, int length) throws Exception
   {
     String result = str;
     for (int i = 0; i <= length-str.length()-1; i++)
     {
       result = "0" + result;
     }
     return result;
   }


}
