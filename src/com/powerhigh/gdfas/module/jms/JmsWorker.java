package com.powerhigh.gdfas.module.jms;

import com.powerhigh.gdfas.module.PoolWorker;
import com.powerhigh.gdfas.util.DataObject;

public class JmsWorker
 extends PoolWorker
{

 public JmsWorker()
 {
 }



 public void run(Object data)
 {
     this.moudle.process((DataObject)data);
 }

 
}