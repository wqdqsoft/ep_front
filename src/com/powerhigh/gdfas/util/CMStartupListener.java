package com.powerhigh.gdfas.util;

import java.sql.Connection;

import javax.servlet.*;
import org.apache.log4j.*;

import com.powerhigh.gdfas.module.ModuleManager;
import com.powerhigh.gdfas.module.gprs.GprsModule;
import com.powerhigh.gdfas.util.CMDb;

/**
 * Description: 应用服务器启动监听 <p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-2
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class CMStartupListener
    implements
    ServletContextListener {

    //加载日志
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

    //一、创建数据接收队列QueueR
//    DataQueue QueueR = new DataQueue();
//    context.setAttribute("QueueR",QueueR);
//    System.out.println("CMStartupListener-->>QueueR Created!");
//    cat.info("CMStartupListener-->>QueueR Created!");

//    //二、创建数据发送队列QueueS
//    DataQueue QueueS = new DataQueue();
//    context.setAttribute("QueueS",QueueS);
//    System.out.println("QueueS Created!");

    //二、启动数据帧接收线程
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


    //三、数据初始化
//    try{
//      //1、CMContext
//      CMContext cmc = new CMContext();
//      //2、operation
//      //operation.setContext(context);
//      System.out.println("CMStartupListener-->>CMContext Initialized Successfully");
//      cat.info("CMStartupListener-->>CMContext Initialized Successfully");
//    }catch(Exception e2){
//      e2.printStackTrace();
//      System.out.println("CMStartupListener-->>CMContext Initialized Failed");
//      cat.info("CMStartupListener-->>CMContext Initialized Failed");
//    }


    //四、建立与前置机的连接
//    int iFlag = -1;
//    try{
//      communicateWithFront CWF = new communicateWithFront();
//      iFlag = CWF.Initialize();
//
//      if(iFlag == 1){
//        System.out.println("CMStartupListener-->>与前置机连接成功");
//        cat.info("CMStartupListener-->>与前置机连接成功");
//        context.setAttribute("CWF",CWF);
//
//      }else if(iFlag==-1){
//        System.out.println("CMStartupListener-->>与前置机连接失败");
//        cat.info("CMStartupListener-->>与前置机连接失败");
//      }
//
//    }catch(Exception e3){
//      System.out.println("CMStartupListener-->>与前置机连接失败");
//      cat.info("CMStartupListener-->>与前置机连接失败");
//      e3.printStackTrace();
//    }
//
//    //五、启动接收前置机数据线程
//    try{
//      readFromFront rff = new readFromFront(context);
//      rff.start();
//      System.out.println("CMStartupListener-->>启动接收前置机数据线程成功");
//      cat.info("CMStartupListener-->>启动接收前置机数据线程成功");
//    }catch(Exception e4){
//      System.out.println("CMStartupListener-->>启动接收前置机数据线程失败");
//      cat.info("CMStartupListener-->>启动接收前置机数据线程失败");
//      e4.printStackTrace();
//    }
    //一、将GPRS的在线状态改为：离线
    Connection con = null;
    String s_sql = "";
    try{    	
    	con = CMDb.GetConnection();
    	//将GPRS的在线状态改为：离线
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
    
    //系统退出时，主动关闭gprs连接
    GprsModule module = (GprsModule)ModuleManager.getModuleManager().
											getModule(CMConfig.GPRS_MODULE_ID);
    module.kickClient();
    
    this.context = null;
    
    System.out.println("[CMStartupListener]系统退出,主动关闭gprs连接");

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
