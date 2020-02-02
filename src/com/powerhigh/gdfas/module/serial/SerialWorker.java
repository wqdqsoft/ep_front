package com.powerhigh.gdfas.module.serial;

import com.powerhigh.gdfas.parse.receiveDispose;
import com.powerhigh.gdfas.module.PoolWorker;
import com.powerhigh.gdfas.util.CMConfig;
import com.powerhigh.gdfas.util.DataObject;

public class SerialWorker
 extends PoolWorker
{

 public SerialWorker()
 {
 }



 public void run(Object data)
 {
 	this.moudle.process((DataObject)data);
 }

 
}
