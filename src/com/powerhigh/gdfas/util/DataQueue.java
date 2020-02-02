package com.powerhigh.gdfas.util;

/**
 * Description: 数据结构：数据队列<p>
 * Copyright:    Copyright   2004 <p>
 * 编写时间: 2004-4-11
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
public class DataQueue extends CMQueue
{
  public synchronized void put(String obj){
    super.put(obj);
  }

  public synchronized Object get(){
    return super.get();
  }
}
