package com.powerhigh.gdfas.util;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Description: ���ݿ������� <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier��
 * Modify Time��
 */

public class CMDb {

  private static Context context = null;
  public static final Collection getCollection(Connection conn, String s_sql,
                                               Object[] pm) throws Exception{
    List ls = null;
    ResultSet rs = null;
    try {
      rs = executeQuery(conn, s_sql, pm);
      ls = resultSetToList(rs);
    }
    catch (Exception e) {
      throw e;
    }
    finally {
      try {
        if (rs != null) {
          close(rs, rs.getStatement());
        }
      }
      catch (Exception e) {
        throw e;
      }
    }

    return (Collection) ls;
  }


  public static final Collection getCollection(String s_sql,
                                               Object[] pm) throws Exception {
    List ls = null;
    ResultSet rs = null;
    Connection conn = null;

    try {
      conn = GetConnection();
      rs = executeQuery(conn, s_sql, pm);
      ls = resultSetToList(rs);
    }
    catch (Exception e) {
      throw e;
    }
    finally {
      try {
        if (rs != null) {
          close(rs, rs.getStatement(), conn);
        }
        else {
          close(null, null, conn);
        }
      }
      catch (Exception e) {
        throw e;

      }
    }

    return (Collection) ls;
  }

  public static final Map getMap(String s_sql,
                                 Object[] pm) throws Exception{
    Map map = null;
    ResultSet rs = null;
    Connection conn = null;
    try {
      conn = GetConnection();
      rs = executeQuery(conn, s_sql, pm);
      map = resultSetToMap(rs);
    }
    catch (Exception e) {
      throw e;
    }
    finally {
      try {
        if (rs != null) {
          close(rs, rs.getStatement(), conn);
        }
        else {
          close(null, null, conn);
        }
      }
      catch (Exception e) {
        throw e;
      }
    }
    return map;
  }




  public static final Collection getCollection(String s_sql, Object ob) throws Exception

  {
    Object[] pm = new Object[1];
    pm[0] = ob;
    return getCollection(s_sql, pm);
  }


  public static final Collection getCollection(String s_sql, Object ob1,
                                               Object ob2) throws Exception{
    Object[] pm = new Object[2];
    pm[0] = ob1;
    pm[1] = ob2;
    return getCollection(s_sql, pm);
  }

  public static final Collection getCollection(Connection conn, String s_sql) throws Exception

  {
    return getCollection(conn, s_sql, null);
  }


  public static final Collection getCollection(String s_sql) throws Exception

  {
    return getCollection(s_sql, null);
  }


//  public static final Collection getCollectionLike(Connection connection,
//      String s_sql, String key) {
//    Object[] parameters = new Object[1];
//    parameters[0] = new String("%" + key + "%");
//    return getCollection(s_sql, parameters);
//  }


  public static final Connection getJdbcConnection(String s_url,
      String s_user, String s_password, String s_driver)

  {
    try {
      Class.forName(s_driver);
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("���ݿ�����" + s_driver+"δ�ҵ�!", e);
    }

    Connection conn = null;
    try {
      conn = DriverManager.getConnection(s_url, s_user, s_password);
    }
    catch (SQLException e) {
      throw new RuntimeException("���ݿ�����" + s_url + "����!", e);
    }
    return conn;
  }

  /**
   * ����˵�� ȡJDBC����,����fetchsize,batchsize
   *
   * @param as_DBurl sdfsdfsd
   * @param as_DBuser ˵��
   * @param as_DBpass ˵��
   * @param as_DBdriver ˵��
   *
   * @return ˵��
   *
   * @ ˵��
   */
  public static final Connection getJdbcConnection(String s_RowPrefetch,
      String s_ExecuteBatch)

  {
    String s_url = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                       CMConfig.DATASOURCE_URL_KEY);
    String s_user = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                        CMConfig.DATASOURCE_USER_KEY);
    String s_password = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                        CMConfig.DATASOURCE_PASS_KEY);
    String s_driver = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                          CMConfig.DATASOURCE_DRIVER_KEY);
    try {
      Class.forName(s_driver);
    }
    catch (ClassNotFoundException e) {
      throw new RuntimeException("���ݿ�����" + s_driver +"δ�ҵ�!", e);
    }

     Properties info = new Properties();
    info.put("user", s_user);
    info.put("password", s_password);
    if (!CMStrUtil.formatNullStr(s_RowPrefetch).equals("")) {
      info.put("sRowPrefetch", s_RowPrefetch);

    }
    if (!CMStrUtil.formatNullStr(s_ExecuteBatch).equals("")) {
      info.put("sExecuteBatch", s_ExecuteBatch);

    }
    Connection conn = null;
    try {
      conn = DriverManager.getConnection(s_url, info);
    }
    catch (SQLException e) {
      throw new RuntimeException("���ݿ�����" + s_url + "����!", e);
    }
    return conn;
  }

  public static final Connection GetConnection() throws Exception {
    String s_dbtype = null;
    s_dbtype = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                CMConfig.DATASOURCE_TYPE_KEY);

    if ( (s_dbtype != null) &&
        (s_dbtype.equalsIgnoreCase("jndi"))) {
     //ʹ�����ӳ�
     String s_jndiname = null;

     try {
       s_jndiname = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                       CMConfig.DATASOURCE_JNDINAME_KEY);

       if ( (s_jndiname == null) || s_jndiname.equals("")) {
         s_jndiname = CMConfig.DEFAULT_DB_JNDINAME;
       }
     }
     catch (Exception e) {
       e.printStackTrace();
       throw e;
     }
     return GetConnection(s_jndiname);

    }
    else {
      //��ʹ�����ӳ�
      String db_url = null;
      String db_user = null;
      String db_pass = null;
      String db_driver = null;

      try {
        db_url = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                    CMConfig.DATASOURCE_URL_KEY);
        db_user = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                     CMConfig.DATASOURCE_USER_KEY);
        db_pass = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                     CMConfig.DATASOURCE_PASS_KEY);
        db_driver = CMXmlR.getResource(CMConfig.DATASOURCE_SECTION,
                                       CMConfig.DATASOURCE_DRIVER_KEY);

      }
      catch (Exception e) {
        e.printStackTrace();
        throw e;
      }
      return getJdbcConnection(db_url, db_user, db_pass, db_driver);

    }
  }


  public static final Connection GetConnection(String s_jndiname) throws Exception

  {
    DataSource ds = null;
    try {

//         Hashtable env = new Hashtable();
//         env.put(Context.INITIAL_CONTEXT_FACTORY,"com.evermind.server.rmi.RMIInitialContextFactory");
//         env.put(Context.SECURITY_PRINCIPAL,"admin");
//         env.put(Context.SECURITY_CREDENTIALS,"admin");
//         env.put(Context.PROVIDER_URL,"ormi://127.0.0.1:23791");
//         if(context == null){
//         	context = new InitialContext(env);
//         }
//    	 ds = (DataSource) context.lookup(s_jndiname);
         
      Context ic = new InitialContext();
//      Context env = (Context)ic.lookup("java:comp/env");
//      
//      ds = (DataSource) env.lookup(s_jndiname);
      ds = (DataSource) ic.lookup(s_jndiname);
    }
    catch (NamingException ex) {
      throw new RuntimeException("������:" + s_jndiname + "���Ҵ���!", ex);
    }
    Connection conn = null;
    try {
      conn = ds.getConnection();
    }
    catch (Exception e) {
      throw new RuntimeException("�����ӳ�:" + s_jndiname + "��ȡ���ӳ���", e);
    }

    return conn;
  }

  /**
   * ����˵��  ִ�в�ѯ��䣬 ����ResultSet,ResultSet��Statement��ConnectionδClose
   *
   * @param connection ����
   * @param s_sql SQL���
   * @param parameters ��������
   *
   * @return ˵�� �����
   *
   * @ ˵��
   */
  public static final ResultSet executeQuery(Connection conn,
                                             String s_sql,
                                             Object[] pm) throws Exception {
    Statement stmt = null;
    PreparedStatement preparedStatement = null;
    ResultSet rs = null;

    try {
      if ( (pm == null) || (pm.length == 0)) {
//                statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
//                        ResultSet.CONCUR_READ_ONLY);
        stmt = conn.createStatement();
        rs = stmt.executeQuery(s_sql);
      }
      else {
//        preparedStatement = conn.prepareStatement(s_sql,
//            ResultSet.TYPE_SCROLL_SENSITIVE,
//            ResultSet.CONCUR_READ_ONLY);
        preparedStatement = conn.prepareStatement(s_sql);

        for (int i = 0; i < pm.length; i++) {
          preparedStatement.setObject(i + 1, pm[i]);
        }
        
        rs = preparedStatement.executeQuery();
      }
    }
    catch (Exception e) {
      throw e;
    }
    return rs;
  }


  public static final ResultSet executeQuery(Connection conn,
                                             String s_sql, Object ob) throws Exception {
    ResultSet rs = null;
    try{
      Object[] pm = new Object[1];
      pm[0] = ob;
      rs = executeQuery(conn, s_sql, pm);
    }catch(Exception e){
      throw e;
    }
    return rs;
  }

  public static final ResultSet executeQuery(Connection conn,
                                          String s_sql, int i) throws Exception{
  Object[] pm = null;
 try{
      pm = new Object[1];
      pm[0] = new Integer(i);
    }catch(Exception e){
      throw e;
    }
    return executeQuery(conn, s_sql, pm);
  }

  /**
   * ����˵�� ִ�в�ѯ��䣬����ResultSet
   *
   * @param connection ����
   * @param s_sql SQL���
   *
   * @return ResultSet
   *
   * @ ˵��
   */
  public static final ResultSet executeQuery(Connection conn,
                                             String s_sql) throws Exception {
    return executeQuery(conn, s_sql, null);
  }

  /**
   * ����˵�� ִ�в�ѯ��䣬����ResultSet
   *
   * @param connection ����
   * @param s_sql SQL���
   * @param key LIKE�еĹؼ���
   *
   * @return ˵�� ����ResultSet
   *
   * @ ˵��
   */
//  public static final ResultSet executeQueryLike(Connection connection,
//                                                 String s_sql, String key) {
//    Object[] parameters = new Object[1];
//    parameters[0] = new String("%" + key + "%");
//    return executeQuery(connection, s_sql, parameters);
//  }

  /**
   * �ر�ResultSet��Statement��Connection
   *
   * @param rs ResultSet to be closed
   * @param stmt Statement or PreparedStatement  to be closed
   * @param conn Connection  to be closed
   */
  public static void close(ResultSet rs, Statement stmt, Connection conn) {
    if (rs != null) {
      try {
        if (rs != null) {
          rs.close();
        }
      }
      catch (java.sql.SQLException e) {
        e.printStackTrace();
      }
    }

    if (stmt != null) {
      try {
        if (stmt != null) {
          stmt.close();
        }
      }
      catch (java.sql.SQLException e) {
        e.printStackTrace();
      }
    }

    if (conn != null) {
      try {
        if (conn != null) {
          conn.close();
        }
      }
      catch (java.sql.SQLException e) {
        e.printStackTrace();
      }
    }
  }


  public static void close(ResultSet rs, Statement stmt) {
    close(rs, stmt, null);
  }

  /**
   * ����˵��
   *
   * @param rs ˵��
   *
   * @ ˵��
   */
  public static void close(ResultSet rs) {
    if (rs != null) {
      try {
        close(rs, rs.getStatement());
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }


  public static void close(Connection conn) {
    if (conn != null) {
      close(null, null, conn);
    }
  }


  public static void close(ResultSet rs, Connection conn)

  {
    try {
      if (rs != null) {
        close(rs, rs.getStatement(), conn);
      }
      else {
        close(null, null, conn);
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * ����ѯ�����װ��List��<br> List��Ԫ������Ϊ��װһ�����ݵ�Map��Map keyΪ�ֶ�������д����valueΪ��Ӧ�ֶ�ֵ
   *
   * @param rs ResultSet
   *
   * @return List
   *
   * @throws java.sql.SQLException
   */
  public static List resultSetToList(ResultSet rs) throws Exception{
    try {
      List ls = new ArrayList();
      if (rs == null) {
        //return Collections.EMPTY_LIST;
      	return ls;
      }

      ResultSetMetaData md = rs.getMetaData();
      int columnCount = md.getColumnCount();
      
      Map rowM;
      //rs.first();

      while (rs.next()) {
        rowM = new HashMap(columnCount);

        for (int i = 1; i <= columnCount; i++) {
          //System.out.println("Column Name = "+md.getColumnName(i)+",type="+md.getColumnTypeName(i));
          if (rs.getObject(i) != null) {
            rowM.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
          }
          else {
            rowM.put(md.getColumnName(i).toLowerCase(), " ");
          }
        }
        ls.add(rowM);

      }
      return ls;
    }
    catch (Exception e) {
      throw e;
    }
  }

  public static long getRecordCount(String s_sql) throws Exception{
    long recordCount = 0;
    Connection conn = null;
    ResultSet rs = null;
    try {
      conn = CMDb.GetConnection();
      rs = CMDb.executeQuery(conn, "select count(*) from (" + s_sql + ")");
      if (rs.next()) {
        recordCount = rs.getLong(1);
      }

    }
    catch (Exception e) {
      throw e;
    }finally{
      CMDb.close(rs, conn);
    }

    return recordCount;
  }
  
  public static long getRecordCount(Connection conn,String s_sql) throws Exception{
    long recordCount = 0;
    ResultSet rs = null;
    try {
      rs = CMDb.executeQuery(conn, "select count(*) from (" + s_sql + ")");
      if (rs.next()) {
        recordCount = rs.getLong(1);
      }
    }
    catch (Exception e) {
      throw e;
    }finally{
      CMDb.close(rs);
    }    

    return recordCount;
  }

  public static void executeUpdate(String s_sql) throws Exception{
    Connection conn = null;
    try {
      conn = CMDb.GetConnection();
      executeUpdate(conn,s_sql,null);
    }catch(Exception e){
      throw e;
    }
    finally {
      try {
        conn.close();
      }
      catch (Exception e) {
        e.printStackTrace();
      }

    }
  }

  public static void executeUpdate(Connection conn, String s_sql) throws Exception {

//    Statement stmt = null;
//    try {
//      stmt = conn.createStatement();      
//      stmt.executeUpdate(s_sql);
//    }
//    catch (Exception e) {
//      throw e;
//    }
//    finally {
//      try {
//        stmt.close();
//      }
//      catch (Exception e) {
//        e.printStackTrace(); ;
//      }
//
//    }
  	executeUpdate(conn,s_sql,null);
  }
  
  public static void executeUpdate(Connection conn,
        String s_sql,
        Object[] pm) throws Exception {
		Statement stmt = null;
		PreparedStatement preparedStatement = null;
		
		try {
			if ( (pm == null) || (pm.length == 0)) {
				//statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
				//ResultSet.CONCUR_READ_ONLY);
				stmt = conn.createStatement();
				stmt.executeUpdate(s_sql);
			}else {
				//preparedStatement = conn.prepareStatement(s_sql,
				//ResultSet.TYPE_SCROLL_SENSITIVE,
				//ResultSet.CONCUR_READ_ONLY);
				preparedStatement = conn.prepareStatement(s_sql);
				
				for (int i = 0; i < pm.length; i++) {
					preparedStatement.setObject(i + 1, pm[i]);
				}

				preparedStatement.executeUpdate();
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(stmt != null){
				try{
					stmt.close();
				}catch(Exception e1){
					e1.printStackTrace();
				}
			}
			
			if(preparedStatement != null){
				try{
					preparedStatement.close();
				}catch(Exception e2){
					e2.printStackTrace();
				}
			}
		}
  }
  
  /**
   *ִ�д洢����(�з���ֵ,���롢���������Ϊ String ��)
   *
   *@param   con       Connection  �洢��������
   *@param   sp_name   String      �洢��������
   *@param   sp_param  Vector      ��� 
   *@param   ret_num   int         ���صĲ�������
   *
   *@return  retV      Vector      ����:����Vecor;����:�׳��쳣
   */
 public static Vector executeProcedure(Connection con,String sp_name,Vector sp_param ,int ret_num) throws Exception{
     int row_num =0;
     int i_len = 0;
     String s_sp_str = "";
     String s_parm_str ="";
     Vector retV = new Vector();
     CallableStatement callableSTMT  = null ;
     i_len=sp_param.size();// ��θ���
    
     try{
       for(int i = 0 ;i<(i_len+ret_num);i ++){
         s_parm_str +="?,";
       }
       
       if (s_parm_str.length () > 0){
       	 //ȥ������','��
         s_parm_str =s_parm_str.substring (0,s_parm_str.length ()-1);
       }
      
       callableSTMT =(CallableStatement) con.prepareCall("{call "+sp_name+"("+s_parm_str+")}");
     
       callableSTMT.clearParameters();
       //�������
       for (int i = 0 ;i<i_len;i ++){
       	String temps = String.valueOf(sp_param.get(i));
       	callableSTMT.setString(i+1,temps);
       }
       
       //���ó���
       for (int i = 1 ;i<=ret_num;i ++){
       	callableSTMT.registerOutParameter(i+i_len,Types.VARCHAR);
       }
       //ִ��
       callableSTMT.execute();
        for (int i = 1 ;i<=ret_num;i ++){ 
            retV.addElement(callableSTMT.getString(i+i_len));
        }
        
        //�ر�
        callableSTMT.close();
   }catch(Exception e){       
       throw e ;
       
    }
    return retV;
  } 

  /**
   * ����ѯ����ĵ�һ����¼��װ��HashMap��
   *
   * @param rs ResultSet
   *
   * @return HashMap
   *
   * @throws java.sql.SQLException
   */
  public static Map resultSetToMap(ResultSet rs) throws Exception{
    if (rs == null) {
      return null;
    }
    try {
      ResultSetMetaData md = rs.getMetaData();
      int columnCount = md.getColumnCount();

      if (rs.next()) {
        Map rowM = new HashMap(columnCount);
        for (int i = 1; i <= columnCount; i++) {
          if (rs.getObject(i) != null) {
            rowM.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
          }
          else {
            rowM.put(md.getColumnName(i).toLowerCase(), "");
          }
        }
        return rowM;
      }
      else {
        return null;
      }
    }
    catch (Exception e) {
      throw e;
    }
  }

}
