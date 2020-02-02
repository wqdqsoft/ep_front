package com.powerhigh.gdfas.module.ascendsms;


import com.sun.jna.Memory;

public class ReceiveSmsThread extends Thread {
	public void run() {
		while (1 == 1) {
			Memory msg = new Memory(200);
			boolean value = SmsLibrary.INSTANCE.GetNewMsg(1, msg);
			if (value) {
				String receiveMsg = msg.getString(0);
				System.out.println(" ’µΩ∂Ã–≈:" + receiveMsg);
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
