package com.powerhigh.gdfas.util;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.sql.Timestamp;

/**
 * Description: 字符转换公共类 <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier：
 * Modify Time：
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
   * @param str 待补0的字符串
   * @param length　补0后的长度
   * @return 若参数length<＝参数str的长度，直接返回str,若参数length＞参数str的长度，则在str前面补0
   * 如addZero("1234",3)="1234", addZero("1234", 8)="00001234"
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
