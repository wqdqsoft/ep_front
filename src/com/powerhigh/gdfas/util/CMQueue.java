package com.powerhigh.gdfas.util;

import java.util.*;

/**
 * Description: 数据结构：队列<p>
 * Copyright:    Copyright   2004 <p>
 * 编写时间: 2004-4-11
 * @author mohui
 * @version 1.0
 * 修改人：
 * 修改时间：
 */
public class CMQueue {
  private LinkedList list = new LinkedList();

  /**
   *方法简介：往LinkedList头部插入对象
   *@param  Object
   *@return
   */
  public void put(Object v) {
    list.addFirst(v);
  }

  /**
   *方法简介：从LinkedList尾部取对象,取完后从LinkedList删除所取的对象
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
   *方法简介：判断LinkedList是否为空
   *@param
   *@return  boolean
   */
  public boolean isEmpty() {
    return list.isEmpty();
  }

  /**
   *方法简介：返回LinkedList的长度
   *@param
   *@return  int
   */
  public int size() {
    return list.size();
  }
}
