package com.powerhigh.gdfas.module.front;

import org.apache.log4j.Category;

public class pingFront extends Thread{
	private static final String resource = "log4j.properties";
	private static Category cat =
	    Category.getInstance(com.powerhigh.gdfas.module.front.pingFront.class);
	
	
	long pingInterval=60;
	
	public pingFront(long l){
		pingInterval = l;
	}
	
	public void run(){
		while(true){
			
			try{
				sleep(10);
				
				communicateWithFront.SendHexToFront(communicateWithFront.ping);
				
				sleep(pingInterval*1000);
			}catch(Exception e){
				cat.error("ping前置机出错：",e);
				System.out.println("ping前置机出错："+e.toString());
			}
		}
	}
}
