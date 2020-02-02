package com.powerhigh.gdfas.module.front;

import com.powerhigh.gdfas.module.PoolWorker;
import com.powerhigh.gdfas.util.DataObject;

public class FrontWorker extends PoolWorker{
	
	public void run(Object obj){
		DataObject data = (DataObject)obj;
		this.moudle.process(data);
	}
}