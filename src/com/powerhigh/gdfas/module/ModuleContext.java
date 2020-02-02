package com.powerhigh.gdfas.module;

import com.powerhigh.gdfas.util.DataObject;

public interface   ModuleContext {
  
  public String    getParameter(String name);
  public boolean      process(DataObject data);
  public String[]  getParameterNames();
  public void      handException(Exception e);
}