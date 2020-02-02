package com.powerhigh.gdfas.module;

import com.powerhigh.gdfas.module.ascendsms.SerialParameters;
import com.powerhigh.gdfas.module.ascendsms.SmsLibrary;
import com.sun.jna.Memory;

/**
 * Description: 初始化爱赛德短信模块；<p>
 * Copyright:    Copyright   2012 <p>
 * 编写时间: 2012-7-26
 * @author 莫辉
 * @version 1.0
 * 修改人：
 * 修改时间：
 */

public class AscendBean {
  public int port;	//端口号
  private int btl;	//波特率
  private int sjw;	//数据位(5;6;7;8)
  private int tzw;	//停止位(1；2)
  private int jyw;	//校验位(0:无；1：奇；2：偶)

  /**
	*方法简介：构造函数
	*@param  port  int 串口
	*@param  btl  int 波特率
	*@param  sjw  int 数据位
	*@param  tzw  int 停止位
	*@param  jyw  int 校验位
	*@return
	*/
  public AscendBean(int port,int btl,int sjw,int tzw,int jyw){
    this.port = port;
    this.btl = btl;
    this.sjw = sjw;
    this.tzw = tzw;
    this.jyw = jyw;
  }

  /**
   *方法简介：构造函数
   *@param
   *@return
   */
  public AscendBean() {

  }

  /**
   *方法简介：初始化模块
   *@return  0:成功；-1：失败
   */
  public int Initialize() {
    SerialParameters parameters = new SerialParameters();
	parameters.setPortName("COM"+this.port);
	parameters.setBaudRate(this.btl);
	parameters.setDatabits(this.sjw);
	parameters.setStopbits(this.tzw);
	parameters.setParity(this.jyw);
	Integer v=SmsLibrary.INSTANCE.OpenComm(this.port);
	if(v==0){
		Memory memory=new Memory(20);
		SmsLibrary.INSTANCE.GetSerialNo(this.port, memory);
		return 0;
	}else{
		return v;
	}
    	
  }

  
  /**
   *方法简介：关闭模块
   *@param
   *@return  void
   */
  public void Close() throws Exception{
	  SmsLibrary.INSTANCE.CloseComm(this.port);
  }
}
