package com.powerhigh.gdfas.module.cmsms;


import org.apache.log4j.Category;

import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.DataObject;

public class CmsmsModule extends AbstractModule
 implements Runnable
{
 //加载日志
 private static final String resource = "log4j.properties";
 private static Category cat =
     Category.getInstance(com.powerhigh.gdfas.module.cmsms.CmsmsModule.class);
// static {
//     PropertyConfigurator.configure(resource);
// }
 
 private Thread runThread;
 private  CmsmsServer server;	

 public CmsmsModule()
 {
 }

 

 public void destroy(){
 	try{
 		//2012-07-26修改
 		runThread.interrupt();
// 		runThread.destroy();
 		server.close();
 	}catch(Exception e){
 		e.printStackTrace();
 	}
 }

 public void init()
     throws Exception
 {
     int workerThreadNum = Integer.parseInt(this.getParameter("workerThreadNum"));
//	 int workerThreadNum = 10;
     int COM = Integer.parseInt(this.getParameter("COM"));
     int btl = Integer.parseInt(this.getParameter("btl"));
     int sjw = Integer.parseInt(this.getParameter("sjw"));
     int tzw = Integer.parseInt(this.getParameter("tzw"));
     int jyw = Integer.parseInt(this.getParameter("jyw"));
     String dxzxhm = this.getParameter("dxzxhm");
     
     cat.info("[CmsmsModule]workerThreadNum="+workerThreadNum);
     cat.info("[CmsmsModule]COM="+COM);
     cat.info("[CmsmsModule]btl="+btl);
     cat.info("[CmsmsModule]sjw="+sjw);
     cat.info("[CmsmsModule]tzw="+tzw);
     cat.info("[CmsmsModule]jyw="+jyw);
     cat.info("[CmsmsModule]dxzxhm="+dxzxhm);
     
     server = new CmsmsServer(workerThreadNum,COM,btl,sjw,tzw,jyw,dxzxhm);
     server.init(CmsmsWorker.class, this);     
     
     runThread = new Thread(this);
 }

 public void run()
 {
     server.run();
 }

 public void send(DataObject data)
     throws Exception
 {	
 	System.out.println("[CmsmsModule]sendDATA");
 	if(server == null){
 		System.out.println("server is null");
 	}
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