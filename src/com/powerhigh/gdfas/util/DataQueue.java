package com.powerhigh.gdfas.util;

/**
 * Description: ���ݽṹ�����ݶ���<p>
 * Copyright:    Copyright   2004 <p>
 * ��дʱ��: 2004-4-11
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
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
