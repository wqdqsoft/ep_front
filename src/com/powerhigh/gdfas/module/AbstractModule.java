package com.powerhigh.gdfas.module;

import com.powerhigh.gdfas.util.DataObject;

public abstract class AbstractModule {
  private ModuleContext context = null;
  public boolean active;
  public boolean isActive() {
    return active;
  }
  public String getParameter(String name) {
    if (context != null) {
      return context.getParameter(name);
    }
    System.out.println("ModuleContext is null!");
    return null;
  }
  public String[]   getParameterNames(){
    return this.context.getParameterNames();
}

  public ModuleContext getContext() {
    return this.context;
  }

  public void setContext(ModuleContext context) {
    this.context = context;
  }
  public boolean process(DataObject data){
     return this.context.process(data);
  }
  public void init() throws Exception { };
  public void start()throws Exception{ };
  public void stop()throws Exception { }

  public void destroy(){  }
  abstract public void send(DataObject data) throws Exception;

  public boolean getBoolean(String name) {
    return (name!=null)&&name.equalsIgnoreCase("true");
  }

}