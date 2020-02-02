package com.powerhigh.gdfas.module.gprs;

import com.powerhigh.gdfas.module.PoolWorker;
import com.powerhigh.gdfas.util.DataObject;

public class GprsWorker
 extends PoolWorker
{

 public GprsWorker()
 {
 }



 public void run(Object data)
 {
     this.moudle.process((DataObject)data);
 }

 
}