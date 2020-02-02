package com.powerhigh.gdfas.module;

import com.powerhigh.gdfas.module.ascendsms.SerialParameters;
import com.powerhigh.gdfas.module.ascendsms.SmsLibrary;
import com.sun.jna.Memory;

/**
 * Description: ��ʼ�������¶���ģ�飻<p>
 * Copyright:    Copyright   2012 <p>
 * ��дʱ��: 2012-7-26
 * @author Ī��
 * @version 1.0
 * �޸��ˣ�
 * �޸�ʱ�䣺
 */

public class AscendBean {
  public int port;	//�˿ں�
  private int btl;	//������
  private int sjw;	//����λ(5;6;7;8)
  private int tzw;	//ֹͣλ(1��2)
  private int jyw;	//У��λ(0:�ޣ�1���棻2��ż)

  /**
	*������飺���캯��
	*@param  port  int ����
	*@param  btl  int ������
	*@param  sjw  int ����λ
	*@param  tzw  int ֹͣλ
	*@param  jyw  int У��λ
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
   *������飺���캯��
   *@param
   *@return
   */
  public AscendBean() {

  }

  /**
   *������飺��ʼ��ģ��
   *@return  0:�ɹ���-1��ʧ��
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
   *������飺�ر�ģ��
   *@param
   *@return  void
   */
  public void Close() throws Exception{
	  SmsLibrary.INSTANCE.CloseComm(this.port);
  }
}
