package com.powerhigh.gdfas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.powerhigh.gdfas.rmi.operation;

public class TimingTask {
	
	//
    // scheduleAtFixedRate(TimerTask task, long delay, long period)  
    public static void sndTimingTask() {  
        Timer timer = new Timer();  
        timer.scheduleAtFixedRate(new TimerTask() {  
            public void run() {
            	String param[][]=new String [1][2];
				param[0][0]="0000";
				param[0][1]="F7";
				
				String param1[][]=new String [1][2];
				param1[0][0]="0000";
				param1[0][1]="F4";
				
				try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                operation.query_1lsj("3", "9603", "0005", param); 
                try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                operation.query_1lsj("3", "9603", "0008", param); 
                try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                operation.query_1lsj("3", "9603", "0011", param);
                try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                operation.query_1lsj("3", "9603", "0005", param1);
                try {
					Thread.sleep(3000L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
            }  
        }, 5000, 900000);  
    }  
    
    
    /**
     * 查询所有终端的0CF2
     */
    public static void sndQueryAllZd0CF2() {  
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date startDate=null;
    	try {
			startDate = dateFormatter.parse("2017/01/01 00:00:00");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
        Timer timer = new Timer();  
        timer.schedule(new TimerTask() {  
            public void run() {
            	
				
				try {
					Thread.sleep(50L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                operation.query_allzd_0cf2();
                
                
            }  
        }, startDate, 3*60*1000);  //每3分钟执行一次
    }  
    
    
    /**
     * 查询所有终端的0CF2
     */
    public static void sndQueryAllZd0AF2() {  
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Date startDate=null;
    	try {
			startDate = dateFormatter.parse("2017/01/01 00:00:00");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
        Timer timer = new Timer();  
        timer.schedule(new TimerTask() {  
            public void run() {
            	
				
				try {
					Thread.sleep(50L);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                operation.query_allzd_0cf2();
                
                
            }  
        }, startDate, 3*60*1000);  //每10分钟执行一次
    }  

}
