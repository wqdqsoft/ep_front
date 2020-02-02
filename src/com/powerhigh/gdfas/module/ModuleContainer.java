package com.powerhigh.gdfas.module;

import java.util.HashMap;
import com.powerhigh.gdfas.util.DataObject;

public class ModuleContainer
    implements ModuleContext {
  private ModuleManager manager;
  AbstractModule module;
  HashMap paramterMap;
  public int moduleID;

  public ModuleContainer(ModuleManager manager, AbstractModule module,
                         HashMap paramterMap) {
    this.manager = manager;
    this.module = module;
    this.paramterMap = paramterMap;
    module.setContext(this);

  }

 class InitThread extends Thread {

        public void run() {
          try {
          	module.init();
          	module.start();
          }
          catch (Exception e) {
           handException(e);
          }
       }
  }
 
 
 public void init(){
    InitThread  initThread = new InitThread ();
   initThread.start();
  }
  public AbstractModule getModule() {
    return this.module;
  }

  public String getParameter(String name) {
    return (String) paramterMap.get(name);
  }

  public boolean process(DataObject data){
     //msg.moduleID = this.moduleID;
     return manager.process(this,data);
  }
  public String[]   getParameterNames(){
    Object []  keyArray =  paramterMap.keySet().toArray();
    String []  names    =  new String[keyArray.length];
    System.arraycopy(keyArray,0,names,0,keyArray.length);
    return  names;
 }
  public void      handException(Exception e){
       manager.handException(this,e);
  }
}