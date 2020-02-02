package com.powerhigh.gdfas.util;

import java.util.*;

/**
 * Description: ���ݽṹ������<p>
 * Copyright:    Copyright   2004 <p>
 * ��дʱ��: 2004-4-11
 * @author mohui
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */
public class CMQueue {
  private LinkedList list = new LinkedList();

  /**
   *������飺��LinkedListͷ���������
   *@param  Object
   *@return
   */
  public void put(Object v) {
    list.addFirst(v);
  }

  /**
   *������飺��LinkedListβ��ȡ����,ȡ����LinkedListɾ����ȡ�Ķ���
   *@param
   *@return  Object
   */
  public Object get() {
    if (list.size() == 0) {
      return null;
    }
    return list.removeLast();
  }

  /**
   *������飺�ж�LinkedList�Ƿ�Ϊ��
   *@param
   *@return  boolean
   */
  public boolean isEmpty() {
    return list.isEmpty();
  }

  /**
   *������飺����LinkedList�ĳ���
   *@param
   *@return  int
   */
  public int size() {
    return list.size();
  }
}
