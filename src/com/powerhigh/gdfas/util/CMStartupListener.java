package com.powerhigh.gdfas.util;

import java.sql.Connection;

import javax.servlet.*;
import org.apache.log4j.*;

import com.powerhigh.gdfas.module.ModuleManager;
import com.powerhigh.gdfas.module.gprs.GprsModule;
import com.powerhigh.gdfas.util.CMDb;

/**
 * Description: Ӧ�÷������������� <p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-4-2
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class CMStartupListener
    implements
    ServletContextListener {

    //������־
    private static Category cat =
                    Category.getInstance(com.powerhigh.gdfas.util.CMStartupListener.class);

  /**
   * The servlet context with which we are associated.
   */
  private ServletContext context = null;

  /**
   * Record the fact that this web application has been initialized.
   * @param event The servlet context event
   */
  public void contextInitialized(ServletContextEvent event) {
    this.context = event.getServletContext();

    //һ���������ݽ��ն���QueueR
//    DataQueue QueueR = new DataQueue();
//    context.setAttribute("QueueR",QueueR);
//    System.out.println("CMStartupListener-->>QueueR Created!");
//    cat.info("CMStartupListener-->>QueueR Created!");

//    //�����������ݷ��Ͷ���QueueS
//    DataQueue QueueS = new DataQueue();
//    context.setAttribute("QueueS",QueueS);
//    System.out.println("QueueS Created!");

    //������������֡�����߳�
//    try{
//      DataReceiveThread drt = new DataReceiveThread(QueueR);
//      drt.start();
//      System.out.println("CMStartupListener-->>DataReceiveThread Started!");
//      cat.info("CMStartupListener-->>DataReceiveThread Started!");
//    }catch(Exception e1){
//      e1.printStackTrace();
//      System.out.println("CMStartupListener-->>DataReceiveThread Failed!");
//      cat.info("CMStartupListener-->>DataReceiveThread Failed!");
//    }


    //�������ݳ�ʼ��
//    try{
//      //1��CMContext
//      CMContext cmc = new CMContext();
//      //2��operation
//      //operation.setContext(context);
//      System.out.println("CMStartupListener-->>CMContext Initialized Successfully");
//      cat.info("CMStartupListener-->>CMContext Initialized Successfully");
//    }catch(Exception e2){
//      e2.printStackTrace();
//      System.out.println("CMStartupListener-->>CMContext Initialized Failed");
//      cat.info("CMStartupListener-->>CMContext Initialized Failed");
//    }


    //�ġ�������ǰ�û�������
//    int iFlag = -1;
//    try{
//      communicateWithFront CWF = new communicateWithFront();
//      iFlag = CWF.Initialize();
//
//      if(iFlag == 1){
//        System.out.println("CMStartupListener-->>��ǰ�û����ӳɹ�");
//        cat.info("CMStartupListener-->>��ǰ�û����ӳɹ�");
//        context.setAttribute("CWF",CWF);
//
//      }else if(iFlag==-1){
//        System.out.println("CMStartupListener-->>��ǰ�û�����ʧ��");
//        cat.info("CMStartupListener-->>��ǰ�û�����ʧ��");
//      }
//
//    }catch(Exception e3){
//      System.out.println("CMStartupListener-->>��ǰ�û�����ʧ��");
//      cat.info("CMStartupListener-->>��ǰ�û�����ʧ��");
//      e3.printStackTrace();
//    }
//
//    //�塢��������ǰ�û������߳�
//    try{
//      readFromFront rff = new readFromFront(context);
//      rff.start();
//      System.out.println("CMStartupListener-->>��������ǰ�û������̳߳ɹ�");
//      cat.info("CMStartupListener-->>��������ǰ�û������̳߳ɹ�");
//    }catch(Exception e4){
//      System.out.println("CMStartupListener-->>��������ǰ�û������߳�ʧ��");
//      cat.info("CMStartupListener-->>��������ǰ�û������߳�ʧ��");
//      e4.printStackTrace();
//    }
    //һ����GPRS������״̬��Ϊ������
    Connection con = null;
    String s_sql = "";
    try{    	
    	con = CMDb.GetConnection();
    	//��GPRS������״̬��Ϊ������
    	s_sql = "update zddqztb set sfzx='0'";
    	CMDb.executeUpdate(con,s_sql);
    	
    	
    }catch(Exception e){
    	e.printStackTrace();
    }finally{
    	CMDb.close(con);
    }
    
    
    System.out.println("CMStartupListener-->> Initialized Successfully!");
    cat.info("CMStartupListener-->> Initialized Successfully!");

  }

  /**
   * Record the fact that this web application has been destroyed.
   * @param event The servlet context event
   */
  public void contextDestroyed(ServletContextEvent event) {
    this.context = null;
    
    //ϵͳ�˳�ʱ�������ر�gprs����
    GprsModule module = (GprsModule)ModuleManager.getModuleManager().
											getModule(CMConfig.GPRS_MODULE_ID);
    module.kickClient();
    
    this.context = null;
    
    System.out.println("[CMStartupListener]ϵͳ�˳�,�����ر�gprs����");

  }

  /**
   * Log a message to the servlet context application log.
   *
   * @param message Message to be logged
   */
  public void log(String msg) {
    if (context != null) {
      context.log("CMStartupListener: " + msg);
    }
  }

}
