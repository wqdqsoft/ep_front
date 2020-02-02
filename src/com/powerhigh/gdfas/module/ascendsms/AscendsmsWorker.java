package com.powerhigh.gdfas.module.ascendsms;

import com.powerhigh.gdfas.module.PoolWorker;
import com.powerhigh.gdfas.util.DataObject;
import com.sun.jna.Memory;


public class AscendsmsWorker extends PoolWorker {
	public AscendsmsWorker() {
	}

	public void run(Object data) {
		DataObject receive = (DataObject) data;
			Memory msg = new Memory(200);
			boolean value = SmsLibrary.INSTANCE.GetNewMsg(receive.port, msg);
			if (value) {
				String receiveMsg = msg.getString(0);
				System.out.println(" ’µΩ∂Ã–≈:" + receiveMsg);
			}
			
	}

}
