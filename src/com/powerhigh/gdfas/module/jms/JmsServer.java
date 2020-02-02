package com.powerhigh.gdfas.module.jms;


import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

import com.powerhigh.gdfas.module.ThreadPool;
import com.powerhigh.gdfas.module.AbstractModule;
import com.powerhigh.gdfas.util.DataObject;

public class JmsServer
{
	private Context context;
	private String contextFactoryName;
	private AbstractModule module;
	private String password;
	private ThreadPool pool;
	private int threadNum;
	private Hashtable props;
	private Queue queue;
	private QueueConnection queueConnection;
	private String queueConnectionFactoryName;
	private QueueConnectionFactory queueFact;
	private String queueName;
	private QueueReceiver queueReceiver;
	private QueueSender queueSender;
	private QueueSession queueSession;
	private boolean running;
	private String url;
	private String userName;

 public JmsServer(int threadNum, String contextFactoryName, String url, String userName, String password, String queueConnectionFactoryName, 
         String queueName, Hashtable props)
 {
     this.threadNum = threadNum;
     this.contextFactoryName = contextFactoryName;
     this.userName = userName;
     this.password = password;
     this.url = url;
     this.queueConnectionFactoryName = queueConnectionFactoryName;
     this.queueName = queueName;
     this.props = props;
 }

 public void close()
 {
     stop();
     if(queueSender == null){
     	try{
     		queueSender.close();
     	}catch(Exception e){
     		
     	}
     }
     	
    
     if(queueReceiver == null){
     	try{
     		queueReceiver.close();
     	}catch(Exception e){
     		
     	}
     }
     	
    
     if(queueSession == null){
     	try{
     		queueSession.close();
     	}catch(Exception e){
     		
     	}
     }
     	
     if(queueConnection == null){
     	try{
     		queueConnection.close();
     	}catch(Exception e){
     		
     	}
     }
     	
     
     context = null;
  
     return;
 }

 public void init(Class c, Object instance)
     throws Exception
 {
     module = (AbstractModule)instance;     
     pool = new ThreadPool(threadNum, c, instance);
     try{
     	initialContext();
     }catch(Exception e){
     	
     }
 }

 private void initialContext()
     throws Exception
 {
     if(context == null)
     {
         Hashtable env = new Hashtable();
         env.put("java.naming.factory.initial", contextFactoryName);
         env.put("java.naming.security.principal", userName);
         env.put("java.naming.security.credentials", password);
         env.put("java.naming.provider.url", url);
         env.putAll(props);
         context = new InitialContext(env);
         try
         {
             queueFact = (QueueConnectionFactory)context.lookup(queueConnectionFactoryName);
             queue = (Queue)context.lookup(queueName);
             queueConnection = queueFact.createQueueConnection();
             queueConnection.start();
             queueSession = queueConnection.createQueueSession(false, 1);
             queueSender = queueSession.createSender(queue);            
             queueReceiver = queueSession.createReceiver(queue);
         }
         catch(Exception e)
         {
             close();
             throw e;
         }
     }
     running = true;
 }

 private void process(DataObject data)
 {
 	try{
 		pool.performWork(data);
 	}catch(Exception e){
 		this.module.getContext().handException(e);
 	}     
 }
 
 public class innerThread extends Thread{
 	public void run()
 	 {
 	     while(true)
 	     {
 	     	try{
 		     	sleep(10);
// 		     	System.out.println("running:"+running);
 		     	if(running){
 		     		initialContext();
 			        javax.jms.Message msg = queueReceiver.receive(0L);
 			        if(msg instanceof ObjectMessage){
 			        	ObjectMessage message = (ObjectMessage)msg;
 			    	    	
 			    	    DataObject data = (DataObject)message.getObject();
 			
 			    	    process(data);
 			        }
 		     	}
 	     	}catch(Exception e){
 	     		close();
 	            module.getContext().handException(e);
 	     		System.out.println("JmsServer Recv ERROR");
 	     	}
 	     }

 	 }
 }

 public void run()
 {
 	innerThread inner = new innerThread();
 	inner.start();

 }

 public void send(DataObject data)
     throws Exception
 {
 	try{ 		
	    initialContext();
	    ObjectMessage objectMessage = queueSession.createObjectMessage();
	//     objectMessage.setJMSPriority(msg.priority);
	//     objectMessage.setJMSDeliveryMode(2);
	//     objectMessage.setJMSExpiration(0L);
	//     objectMessage.setObject(sobj.data);
	    objectMessage.setObject(data);
	    
	    queueSender.send(objectMessage);
 	}catch(Exception e){
	    close();
	    throw e;
 	}
 }

 public void stop()
 {
     running = false;
 }

 
}