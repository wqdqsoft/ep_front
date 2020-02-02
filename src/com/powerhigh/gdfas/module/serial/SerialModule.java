package com.powerhigh.gdfas.module.serial;


import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.DataObject;

public class SerialModule extends AbstractModule
 implements Runnable
{
 //º”‘ÿ»’÷æ
 private static final String resource = "log4j.properties";
 private static Category cat =
     Category.getInstance(com.powerhigh.gdfas.module.serial.SerialModule.class);
// static {
//     PropertyConfigurator.configure(resource);
// }
 
 private Thread runThread;
 private  SerialServer server;	

 public SerialModule()
 {
 }

 

 public void destroy(){
 	try{
 		runThread.destroy();
 		server.close();
 	}catch(Exception e){
 		e.printStackTrace();
 	}
 }

 public void init()
     throws Exception
 {
     int workerThreadNum = Integer.parseInt(this.getParameter("workerThreadNum"));
     int COM = Integer.parseInt(this.getParameter("COM"));
     int btl = Integer.parseInt(this.getParameter("btl"));
     int sjw = Integer.parseInt(this.getParameter("sjw"));
     int tzw = Integer.parseInt(this.getParameter("tzw"));
     int jyw = Integer.parseInt(this.getParameter("jyw"));
     cat.info("[SerialModule]workerThreadNum="+workerThreadNum);
     cat.info("[SerialModule]COM="+COM);
     cat.info("[SerialModule]btl="+btl);
     cat.info("[SerialModule]sjw="+sjw);
     cat.info("[SerialModule]tzw="+tzw);
     cat.info("[SerialModule]jyw="+jyw);
     
     server = new SerialServer(workerThreadNum,COM,btl,sjw,tzw,jyw);
     server.init(SerialWorker.class, this);     
     
     runThread = new Thread(this);
 }

 public void run()
 {
     server.run();
 }

 public void send(DataObject data)
     throws Exception
 {	
 	System.out.println("[SerialModule]sendDATA");
 	
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