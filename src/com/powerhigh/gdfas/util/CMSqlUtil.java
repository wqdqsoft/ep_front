package com.powerhigh.gdfas.util;

import com.powerhigh.gdfas.util.CMDb;
import java.util.*;

/**
 * Description: sql二次处理类 <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier：
 * Modify Time：
 */

public class CMSqlUtil {
  public CMSqlUtil(){
  }

//  public static Collection getCollection(String tableName,int pageNum, int pageCount,String whereStr, String orderbyStr) throws Exception
//  {
//    int beginNum=(pageCount-1)*pageNum;
//    int endNum=pageCount*pageNum;
//
//    if(!orderbyStr.equals(""))orderbyStr=" order by " + orderbyStr;
//    if(!whereStr.equals(""))whereStr=" where " + whereStr;
//    String strSql="select * from ("
//          +"select rownum as my_rownum,a.* from"
//            +"  (select * from "+tableName+whereStr+orderbyStr+") a "
//          +" where rownum<="+endNum+")"
//          +" where my_rownum>"+beginNum;
//    //System.out.println(strSql);
//    return CMDb.getCollection(strSql);
//
//  }

  public static Collection getCollection(String sql,int pageNum, int pageCount) throws Exception
  {
    int beginNum=(pageCount-1)*pageNum;
    int endNum=pageCount*pageNum;

    String strSql="select * from ("
          +"select rownum as my_rownum,a.* from"
            +"  ("+sql+") a "
          +" where rownum<="+endNum+")"
          +" where my_rownum>"+beginNum;
    //System.out.println(strSql);
    return CMDb.getCollection(strSql);

  }

//  public static Collection getCollectionByParams(String sql,int pageNum, int pageCount,Object[] params) throws Exception
//  {
//    int beginNum=(pageCount-1)*pageNum;
//    int endNum=pageCount*pageNum;
//
//    String strSql="select * from ("
//          +"select rownum as my_rownum,a.* from"
//            +"  ("+sql+") a "
//          +" where rownum<=?)"
//          +" where my_rownum>?";
//    //System.out.println(strSql);
//    Object[] newParams=new Object[2];
//    if(params==null)
//    {
//      newParams[0]=new Long(endNum);
//      newParams[1]=new Long(beginNum);
//    }else
//    {
//
//      newParams=new Object[params.length+2];
//      for(int i=0;i<params.length;i++)
//        newParams[i]=params[i];
//      newParams[params.length]=new Long(endNum);
//      newParams[params.length+1]=new Long(beginNum);
//    }
//    return CMDb.getCollection(strSql,newParams);
//
//  }
//
//
//  public static long getMaxPage(int recordCount,int pagenum)
//  {
//    if(recordCount==0)
//      return 0;
//    else
//      return (recordCount - 1) / pagenum + 1;
//  }


}
