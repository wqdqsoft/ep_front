package com.powerhigh.gdfas.module;

import com.powerhigh.gdfas.module.AbstractModule;

public abstract class PoolWorker
{
	public AbstractModule moudle = null;
	public void setModule(Object obj){
		this.moudle= (AbstractModule)obj;
	}
	public abstract void run(Object data);       
}

