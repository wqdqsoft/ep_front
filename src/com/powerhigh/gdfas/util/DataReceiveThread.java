package com.powerhigh.gdfas.util;

import org.apache.log4j.*;
/**
 * Description: �߳�:�Ӷ���ȡ����֡������<p>
 * Copyright:    Copyright   2015 <p>
 * ��дʱ��: 2015-4-5
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class DataReceiveThread
    extends Thread {

  //������־
  private static final String resource = "cm.log.properties";
  private static Category cat =
      Category.getInstance(com.powerhigh.gdfas.util.DataReceiveThread.class);
  static {
    PropertyConfigurator.configure(resource);
  }

  private DataQueue queueR = null; //���ݽ��ն���

  public DataReceiveThread(DataQueue queue) {
    this.queueR = queue;
  }

  public void run() {

    while (true) {
      try {
        //�Ӷ���ȡ����֡������
        sleep(1);
        //System.out.println("queueR.size():"+queueR.size());
        if (queueR.size() <= 0) {
          continue;
        }
        //��������֡��ʮ�������ַ�
        String sSJZ = (String) queueR.get();
        cat.info("�ն˷�������:" + sSJZ);
        //����֡����
        boolean bl = false;
        if(sSJZ != null){
         // bl = receiveDispose.receiveData(sSJZ);
        }

        cat.info("������:" + bl);
      }
      catch (Exception e) {
        e.printStackTrace();
        continue;
      }

    }

  }

}
