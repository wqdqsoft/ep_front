package com.powerhigh.gdfas.util;

import org.apache.log4j.*;
/**
 * Description: 线程:从队列取数据帧并解析<p>
 * Copyright:    Copyright   2015 <p>
 * 编写时间: 2015-4-5
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class DataReceiveThread
    extends Thread {

  //加载日志
  private static final String resource = "cm.log.properties";
  private static Category cat =
      Category.getInstance(com.powerhigh.gdfas.util.DataReceiveThread.class);
  static {
    PropertyConfigurator.configure(resource);
  }

  private DataQueue queueR = null; //数据接收队列

  public DataReceiveThread(DataQueue queue) {
    this.queueR = queue;
  }

  public void run() {

    while (true) {
      try {
        //从队列取数据帧并解析
        sleep(1);
        //System.out.println("queueR.size():"+queueR.size());
        if (queueR.size() <= 0) {
          continue;
        }
        //接收数据帧，十六进制字符
        String sSJZ = (String) queueR.get();
        cat.info("终端返回数据:" + sSJZ);
        //数据帧处理
        boolean bl = false;
        if(sSJZ != null){
         // bl = receiveDispose.receiveData(sSJZ);
        }

        cat.info("处理结果:" + bl);
      }
      catch (Exception e) {
        e.printStackTrace();
        continue;
      }

    }

  }

}
