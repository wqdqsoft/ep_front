package com.powerhigh.gdfas.module;

import java.util.HashMap;

import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;

import com.powerhigh.gdfas.Context;
import com.powerhigh.gdfas.util.DataObject;


public class ModuleManager {
  //加载日志
  private static final String resource = "log4j.properties";
  private static Category cat =
	  Category.getInstance(com.powerhigh.gdfas.module.ModuleManager.class);
//  static {
//	  PropertyConfigurator.configure(resource);	
//  }
  
  static HashMap moduleMap = null;
  static Class moduleClass = AbstractModule.class;
  private static ModuleManager _instance = null;
  
  
  private ModuleManager() {

  }

  public static synchronized ModuleManager getModuleManager() {
    if (_instance == null) {
      _instance = new ModuleManager();
    }
    return _instance;

  }

  public synchronized boolean register(String moduleID, String moduleName, Class c,
                                       HashMap paramterMap) throws Exception{
    try {
    	
      Object obj = c.newInstance();
      if (moduleClass.isInstance(obj)) {

        AbstractModule module = (AbstractModule) obj;
        if (moduleMap == null) {
        	moduleMap = new HashMap();
        }
        ModuleContainer container = new ModuleContainer(this, module,
            paramterMap);
        container.moduleID = Integer.parseInt(moduleID);
        container.init();
        moduleMap.put(moduleName, container);
        
        return true;
      }
    }
    catch (Exception e) {
      throw e;
    }
    return false;
  }

  public AbstractModule getModule(int moduleID){
  	AbstractModule returnModule = null;
  	if(moduleMap != null) {
        Object[] drivers = moduleMap.values().toArray();
        for (int i = 0; i < drivers.length; i++) {
          ModuleContainer container = (ModuleContainer) drivers[i];
          if (container.moduleID == moduleID) {             
          	returnModule = (AbstractModule)container.getModule();             
          }
        }
     }
  	
  	return returnModule;
  }

  public void sendMessage(DataObject data) throws Exception {
    boolean send = false;
    cat.info("send data:"+data.sjz+"[ModuleID="+data.moduleID+"]");
    if (moduleMap != null) {
      Object[] drivers = moduleMap.values().toArray();
      for (int i = 0; i < drivers.length; i++) {
        ModuleContainer container = (ModuleContainer) drivers[i];
        if (container.moduleID == data.moduleID) {
          try {          	
            container.getModule().send(data);
          }
          catch (Exception e) {
            //handException(container, e);
            throw e;
          }
          send = true;
          break;
        }
      }
    }
    if (!send) {
      Exception e = new Exception("not find module");
      e.printStackTrace();
      return;
    }
  }

  public void handException(ModuleContainer module, Exception e) {
    int moduleID = -1;
    if (module != null) {
    	moduleID = module.moduleID;

    }   
    cat.error("运行错误， 模块ID＝" + moduleID, e);
    System.out.println("运行错误， 模块ID＝" + moduleID );
    e.printStackTrace();
  }

  public boolean process(ModuleContainer module, DataObject data) {
    try {
    	Dispatch dispatch = (Dispatch)Context.ctx.getBean("dispatchService");
    	dispatch.upDispatch(data);
    	return true;
    }
    catch (Exception e) {
      handException(module, e);
      return false;
    }
  }

  
}
