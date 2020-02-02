package com.powerhigh.gdfas.util;

/**
 * Description: config.xml的常量类 <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier：
 * Modify Time：
 */

public final class CMConfig
 {
    public static final String DEFAULT_DB_JNDINAME = "jdbc/cm";


    public static final String CONFIG_FILE = "config.xml";
    public static final String CONFIG_ROOT_KEY = "config";
    
    //系统类型配置
    public static final String SYSTEM_SECTION = "system-config";
    public static final String SYSTEM_DEBUG_KEY = "debug";
    public static final String SYSTEM_CHANNEL_KEY = "channel";
    public static final String SYSTEM_READBUFFER_KEY = "read_buffer";//读socke数据的buffer大小
    public static final String SYSTEM_QUEUEBUFFER_KEY = "queue_buffer";//拼祯时为每个终端开出的buffer大小    
    public static final String SYSTEM_DOWNLOADBUFFER_KEY = "downloadBuffer";
    public static final String SYSTEM_RESEND_COUNT = "resend_count";//重发次数(次)
    public static final String SYSTEM_OVERTIME = "overtime";//超时时间(毫秒)
    public static final String SYSTEM_SEND_DELAY = "send_delay";//发送延时(毫秒)
    public static final String SYSTEM_NEXT_DELAY = "next_delay";//下一帧延时(毫秒)
    public static final String SYSTEM_FILE_URL = "file_url";//文件存放位置
    public static final String SYSTEM_WEB_URL = "web_url";//文件wen访问路径
    
        
    //数据库配置
    public static final String DATASOURCE_SECTION = "datasource-config";
    public static final String DATASOURCE_TYPE_KEY = "db_type";
    public static final String DATASOURCE_JNDINAME_KEY = "jndi_name";
    public static final String DATASOURCE_URL_KEY = "url";
    public static final String DATASOURCE_DRIVER_KEY = "driver";
    public static final String DATASOURCE_USER_KEY = "user";
    public static final String DATASOURCE_PASS_KEY = "password";
    
    
    
    public static final int RESEND_COUNT = 3;//数据重发次数
    public static final Long SEND_DELAY = 500L;//多帧数据发送时，下一帧发送的间隔时间
    
    public static final int FRONT_MODULE_ID = 1;//前置机模块的ID   
    
    
    
    public static final int FRONT_SEND_JMS_MODULE_ID = 2;//前置机下行JMS模块的ID
    public static final int FRONT_RECEIVE_JMS_MODULE_ID = 3;//前置机上行JMS模块的ID
    
    public static final int GPRS_MODULE_ID = 4;//GPRS模块的ID    
    public static final int GPRS_SEND_JMS_MODULE_ID = 5;//GPRS下行JMS模块的ID
    public static final int GPRS_RECEIVE_JMS_MODULE_ID = 6;//GPRS上行JMS模块的ID
    
    public static final int CMSMS_MODULE_ID = 7;//创明短信机模块的ID 

    public static final int SERIAL_MODULE_ID = 8;//串口模块的ID 
    
    public static final int ASCENDSMS_MODULE_ID = 9;//杭州爱赛德下行短信模块的ID 
    

 }
