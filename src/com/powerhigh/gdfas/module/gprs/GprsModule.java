package com.powerhigh.gdfas.module.gprs;


import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.DataObject;

public class GprsModule extends AbstractModule
 implements Runnable
{
 //º”‘ÿ»’÷æ
 private static final String resource = "log4j.properties";
 private static Category cat =
     Category.getInstance(com.powerhigh.gdfas.module.gprs.GprsModule.class);
// static {
//     PropertyConfigurator.configure(resource);
// }
 
 private Thread runThread;
 private GprsServer server;	
 public GprsModule()
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
     int port = Integer.parseInt(this.getParameter("serverPort"));
     cat.info("GprsModule:workerThreadNum="+workerThreadNum);
     cat.info("GprsModule:serverPort="+port);
     
     
     server = new GprsServer(workerThreadNum, port);
     server.init(GprsWorker.class, this);
     runThread = new Thread(this);
 }

 public void run()
 {
     server.run();
 }
 
 public void kickClient(){
 	server.kickClient();
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