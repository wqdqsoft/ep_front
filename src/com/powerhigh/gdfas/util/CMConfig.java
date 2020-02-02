package com.powerhigh.gdfas.util;

/**
 * Description: config.xml�ĳ����� <p>
 * Copyright:    Copyright   2015 <p>
 * Time: 2015-3-5
 * @author mohui
 * @version 1.0
 * Modifier��
 * Modify Time��
 */

public final class CMConfig
 {
    public static final String DEFAULT_DB_JNDINAME = "jdbc/cm";


    public static final String CONFIG_FILE = "config.xml";
    public static final String CONFIG_ROOT_KEY = "config";
    
    //ϵͳ��������
    public static final String SYSTEM_SECTION = "system-config";
    public static final String SYSTEM_DEBUG_KEY = "debug";
    public static final String SYSTEM_CHANNEL_KEY = "channel";
    public static final String SYSTEM_READBUFFER_KEY = "read_buffer";//��socke���ݵ�buffer��С
    public static final String SYSTEM_QUEUEBUFFER_KEY = "queue_buffer";//ƴ��ʱΪÿ���ն˿�����buffer��С    
    public static final String SYSTEM_DOWNLOADBUFFER_KEY = "downloadBuffer";
    public static final String SYSTEM_RESEND_COUNT = "resend_count";//�ط�����(��)
    public static final String SYSTEM_OVERTIME = "overtime";//��ʱʱ��(����)
    public static final String SYSTEM_SEND_DELAY = "send_delay";//������ʱ(����)
    public static final String SYSTEM_NEXT_DELAY = "next_delay";//��һ֡��ʱ(����)
    public static final String SYSTEM_FILE_URL = "file_url";//�ļ����λ��
    public static final String SYSTEM_WEB_URL = "web_url";//�ļ�wen����·��
    
        
    //���ݿ�����
    public static final String DATASOURCE_SECTION = "datasource-config";
    public static final String DATASOURCE_TYPE_KEY = "db_type";
    public static final String DATASOURCE_JNDINAME_KEY = "jndi_name";
    public static final String DATASOURCE_URL_KEY = "url";
    public static final String DATASOURCE_DRIVER_KEY = "driver";
    public static final String DATASOURCE_USER_KEY = "user";
    public static final String DATASOURCE_PASS_KEY = "password";
    
    
    
    public static final int RESEND_COUNT = 3;//�����ط�����
    public static final Long SEND_DELAY = 500L;//��֡���ݷ���ʱ����һ֡���͵ļ��ʱ��
    
    public static final int FRONT_MODULE_ID = 1;//ǰ�û�ģ���ID   
    
    
    
    public static final int FRONT_SEND_JMS_MODULE_ID = 2;//ǰ�û�����JMSģ���ID
    public static final int FRONT_RECEIVE_JMS_MODULE_ID = 3;//ǰ�û�����JMSģ���ID
    
    public static final int GPRS_MODULE_ID = 4;//GPRSģ���ID    
    public static final int GPRS_SEND_JMS_MODULE_ID = 5;//GPRS����JMSģ���ID
    public static final int GPRS_RECEIVE_JMS_MODULE_ID = 6;//GPRS����JMSģ���ID
    
    public static final int CMSMS_MODULE_ID = 7;//�������Ż�ģ���ID 

    public static final int SERIAL_MODULE_ID = 8;//����ģ���ID 
    
    public static final int ASCENDSMS_MODULE_ID = 9;//���ݰ��������ж���ģ���ID 
    

 }
