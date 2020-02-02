package com.powerhigh.gdfas.module.jms;


import java.util.Hashtable;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.DataObject;

public class JmsModule extends AbstractModule
 implements Runnable
{
 //º”‘ÿ»’÷æ
 private static final String resource = "log4j.properties";
 private static Category cat =
     Category.getInstance(com.powerhigh.gdfas.module.jms.JmsModule.class);
// static {
//     PropertyConfigurator.configure(resource);
// }
 
 private Thread runThread;
 private JmsServer server;	
 public JmsModule()
 {
 }

 

 public void destroy()
 {
     runThread.destroy();
     server.close();
 }

 public void init()
     throws Exception
 {
     int workerThreadNum = Integer.parseInt(this.getParameter("workerThreadNum"));
     String contextFactoryName = this.getParameter("contextFactoryName");
     String userName = this.getParameter("userName");
     String password = this.getParameter("userPassword");
     String url = this.getParameter("url");
     String queueConnectionFactoryName = this.getParameter("queueConnectionFactoryName");
     String queueName = this.getParameter("queueName");
     Hashtable props = new Hashtable();
     cat.info("JmsModule:workerThreadNum="+workerThreadNum);
     cat.info("JmsModule:contextFactoryName="+contextFactoryName);
     cat.info("JmsModule:userName="+userName);
     cat.info("JmsModule:password="+password);
     cat.info("JmsModule:url="+url);
     cat.info("JmsModule:queueConnectionFactoryName="+queueConnectionFactoryName);
     cat.info("JmsModule:queueName="+queueName);
     
     server = new JmsServer(workerThreadNum, contextFactoryName, url, userName, password, queueConnectionFactoryName, queueName, props);
     server.init(JmsWorker.class, this);
     runThread = new Thread(this);
 }

 public void run()
 {
     server.run();
 }

 public void send(DataObject data)
     throws Exception
 {
     server.send(data);
 }

 public void start()
     throws Exception
 {
     runThread.start();
 }

 public void stop()
     throws Exception
 {
     server.stop();
 }


}