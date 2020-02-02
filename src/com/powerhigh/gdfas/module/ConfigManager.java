package com.powerhigh.gdfas.module;


import org.apache.log4j.Category;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.lang.Class;
import java.util.HashMap;
import com.powerhigh.gdfas.util.CMXmlR;


public  class ConfigManager {
	
	//加载日志
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.ConfigManager.class);
//	static {
//	  PropertyConfigurator.configure(resource);	
//	}
	
	static final String  MODULE_TAG_NAME   =   "module-config";
	static final String  MODULE_NODE_NAME   =   "module";
	static final String  PARAMETER_NODE_NAME =   "parameter";
	
	
	public ConfigManager(){
		cat.info("MODULE_TAG_NAME="+MODULE_TAG_NAME);
		cat.info("MODULE_NODE_NAME="+MODULE_NODE_NAME);
		cat.info("PARAMETER_NODE_NAME="+PARAMETER_NODE_NAME);
	}
  
  private  String getAttributeValue(Node nd,String attrName){
    String result =null;
     try {
        result =   nd.getAttributes().getNamedItem(attrName).getNodeValue();
     }
     catch(Exception e){
     }
     return result;
   }
  public Class  loadClass(String className){
    Class result=null;
   try {
     result = Class.forName(className);
   }
   catch(Exception e){
     result = null;
   }
   return result;
  }
  private void processModule(Node nd) throws Exception{
    String moduleName = this.getAttributeValue(nd, "name");
    String moduleClass = this.getAttributeValue(nd, "class");
    String moduleID  =  this.getAttributeValue(nd, "id");

    cat.info("moduleName="+moduleName);
    cat.info("moduleClass="+moduleClass);
    cat.info("moduleID="+moduleID);
    
    Class c = this.loadClass(moduleClass);
    if(c == null) {
        cat.error("调入模块:name="+moduleName+";class="+ moduleClass+";id="+moduleID+"[失败]!");
        return;
    }
    HashMap parameters = new HashMap();
    NodeList list = nd.getChildNodes();

    int len = list.getLength();
    for (int i = 0; i < len; i++) {
      nd = list.item(i);
      if (nd.getNodeName().equals(PARAMETER_NODE_NAME)) {
        String name = this.getAttributeValue(nd, "name");
        String value = this.getAttributeValue(nd, "value");
        parameters.put(name,value);
      }
    }
    
    ModuleManager.getModuleManager().register(moduleID,moduleName,c,parameters);
    System.out.println("调入模块:name="+moduleName+";class="+ moduleClass+";id="+moduleID+"[成功]!");    
    cat.info("调入模块:name="+moduleName+";class="+ moduleClass+";id="+moduleID+"[成功]!");
    
  }

  public void excute(NodeList list) {
    Node nd = null;
    int len = list.getLength();
    for (int i = 0; i < len; i++) {
      nd = list.item(i);
      if (nd.getNodeName().equals(MODULE_NODE_NAME)) {
      	try{
      		processModule(nd);
      	}catch(Exception e){
      		cat.info("Module Initial Error");
      		cat.error(e);
      	}
      }
    }
  }
  
  public void excute() {
  	NodeList top_list = CMXmlR.getElementsByTagName(MODULE_TAG_NAME);
  	
  	NodeList list = top_list.item(0).getChildNodes();
  	Node nd = null;
    int len = list.getLength();
    for (int i = 0; i < len; i++) {
      nd = list.item(i);
      if (nd.getNodeName().equals(MODULE_NODE_NAME)) {
      	try{
      		processModule(nd);
      	}catch(Exception e){
      		cat.info("Module Initial Error");
      		cat.error(e);
      	}
      }
    }
  }

}