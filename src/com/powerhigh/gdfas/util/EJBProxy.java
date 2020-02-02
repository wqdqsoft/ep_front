package com.powerhigh.gdfas.util;

import javax.ejb.EJBHome;
import  javax.naming.NamingException;
import  javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import  java.util.Hashtable;
import  java.util.HashMap;



public class EJBProxy {
  static private  String INITIAL_CONTEXT_FACTORY= "com.evermind.server.rmi.RMIInitialContextFactory";
  static private  String INITIAL_CONTEXT_URL = "ormi://127.0.0.1:23791/pgd";
  static private  String INITIAL_CONTEXT_USER = "admin";
  static private  String INITIAL_CONTEXT_PASSWORD = "admin";
  
  static private EJBProxy instance = null;
  private  InitialContext context=null;
  private  HashMap      homeCache= new HashMap(); 
  private  Hashtable    props = new Hashtable();

  private EJBProxy() throws Exception{
  	this.context = getInitialContext();
  }
  
  public static EJBProxy getInstance() throws Exception{
  	if(instance==null){
  		instance = new EJBProxy();
  	}
  	return instance;
  	
  }
  private  InitialContext getInitialContext() throws NamingException
   {
  	props.put(InitialContext.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
    props.put(InitialContext.PROVIDER_URL, INITIAL_CONTEXT_URL);      
    props.put(InitialContext.SECURITY_PRINCIPAL, INITIAL_CONTEXT_USER);
    props.put(InitialContext.SECURITY_CREDENTIALS, INITIAL_CONTEXT_PASSWORD);

    return new InitialContext(props);
    
  }
  
  public void addExternProp(String name , Object obj){
    props.put(name,obj);
  }
  
  public  EJBHome getRemoteHome(String ejbName,Class homeName) throws Exception{  
       EJBHome home = (EJBHome)homeCache.get(ejbName);
       if(home != null) return home;

       if( context == null){
       	context = this.getInitialContext();
       }
       Object objref =  context.lookup(ejbName);
       home = (EJBHome)PortableRemoteObject.narrow(objref, homeName);
       if(home != null){
          homeCache.put(ejbName,home);
       }
       
       return home;
    
  }

}