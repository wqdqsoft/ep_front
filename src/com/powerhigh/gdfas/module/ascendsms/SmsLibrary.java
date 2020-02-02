package com.powerhigh.gdfas.module.ascendsms;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface SmsLibrary extends Library {
	public static SmsLibrary INSTANCE = (SmsLibrary) Native.loadLibrary((Platform.isWindows() ? "AscendSMS" : "c"), SmsLibrary.class);

	/**
	 * ��ģ��
	 * 
	 * @param commIndex
	 *            ģ�������ӵ�COM�ڱ��
	 * @return 0 �ɹ� -5 ģ��ע��ʧ�� -6 ��COM��ʧ�� -9 δ��⵽GSMģ�� -10 ����ģ�����ʧ�� -11 ����ģ�����ʧ��
	 */
	public Integer OpenComm(Integer commIndex);

	/**
	 * �ر�ģ��
	 * 
	 * @param commIndex
	 *            ģ�������ӵ�COM�ڱ��
	 * @return 0 �ɹ� -1 ���Ͷ����л���δ����Ϣ���� -7 �ر�COM��ʧ�� -8 ģ��δ��
	 */
	public Integer CloseComm(Integer commIndex);
	
	/**
	 * ǿ�ƹر�ģ��
	 * @param commIndex ģ�������ӵ�COM�ڱ��
	 * @return 0 �ɹ� -7 �ر�COM��ʧ�� -8 ģ��δ��
	 * 	1) �������л���δ������Ϣ�����øú������ᶪʧ����δ�������
		2) ������øú���ǰ��ѭ������GetNextSendMsg������δ���Ķ�������ȡ�����Ա㱣������δ�����ŵ���Ϣ�����ϵͳ�Ŀɿ���
	 */
	public Integer ForceCloseComm(Integer commIndex);
	
	/**
	 * ��ȡģ������кţ�IMEI��
	 * @param commIndex ģ�������ӵ�COM�ڱ��
	 * @param sn ȡ�õ����кŵ�����
	 * @return True �ɹ� False ʧ��
	 */
	public boolean GetSerialNo(Integer commIndex,Memory sn);

	/**
	 * ���Ͷ���Ϣ
	 * 
	 * @param commIndex
	 *            ģ�������ӵ�COM�ڱ��
	 * @param msg
	 *            ����Ϣ����
	 * @param mobileNo
	 *            ���ն��ֻ����룬������ͨ�ֻ�������������
	 * @param msgIndex
	 *            ��Ϣ��ţ��������Լ����壬�������
	 * @param isChinese
	 *            �Ƿ�Ϊ������Ϣ������ΪTrue������ΪFalse��
	 * @return 0 �ɹ�����Ϣ�Ѳ��뷢�Ͷ��� -2 ��Ϣ���ȳ�����󳤶� -3 �ֻ����벻��ȷ -4 ���Ͷ������� -8 ģ��δ��
	 */
	public Integer SendMsg(Integer commIndex, String msg, String mobileNo,
			Integer msgIndex, boolean isChinese);

	/**
	 * ���ն���
	 * 
	 * @param comIndex
	 *            ģ�������ӵ�COM�ڱ��
	 * @param newMsg
	 *            ȡ�õĶ���Ϣ������
	 * @return true:�ɹ����յ��µĶ��� false:δ���յ��µĶ��� 1) ����ֵΪTrueʱ��NewMsgΪ���յ��Ķ��ţ�
	 *         ����ֵΪFalse��NewMsgΪ����Module Error��ʱ����ʾģ�鵱ǰ����Ӧ�������Ƕϵ��ģ������ϣ�
	 *         ����ֵΪFalse��NewMsgΪ��ʱ��ʾģ��������������û���յ����� 2)
	 *         ����Ϣ��ʽ�����Ͷ˺���+��|��+����ʱ��+��|��+�������ݡ�
	 *         �˴�����ʱ��ָ���Ƕ������Ľ��յ��ö��ŵ�ʱ�䣬������168���͡�GP 0001�������յ�����ϢΪ��168|01-09-28
	 *         12:33:39|<0001>�չ����12.66��12.68��12.68��12.75��12.55��12.49��488.87K12:32
	 */
	public boolean GetNewMsg(Integer comIndex, Memory newMsg);

	/**
	 * ȡ�÷��Ͷ�����δ������Ϣ����Ŀ
	 * 
	 * @param comIndex
	 *            ģ�������ӵ�COM�ڱ��
	 * @return ���Ͷ�����δ������Ϣ����Ŀ
	 */
	public Integer GetUnSendCount(Integer comIndex);

	/**
	 * ��ȡ���Ͷ�������һ��Ҫ���͵Ķ���Ϣ��ͨ�����ڼ��ʵ�ʵķ��������
	 * 
	 * @param comIndex
	 *            ģ�������ӵ�COM�ڱ��
	 * @param msg
	 *            ȡ�õĶ���Ϣ������
	 * @param deleteAfterRead
	 *            ��ȡ�ö��ź��Ƿ����Ӷ�����ɾ����True��ʾ��ȡ��ɾ���ö��ţ�False��ʾ��ȡ�����ö���
	 * @return True �ɹ���ȡ����һ��Ҫ���Ͷ��� False δ������һ��Ҫ���͵Ķ���
	 * 	1) �ú���ֻ���ط��Ͷ����еĵ�1��δ������
		2) ���Ҫ��ȡ����ĵ�2��δ�����ţ����Եȵ���1�����ŷ�����ϣ�����ñ�����ʱ���ò���DeleteAfterReadΪTrue��
		����ȡ��1��δ�����ź���ɾ����������ǰ�ĵ�2��δ�����Ž���Ϊ��1��δ�����ţ����ѭ����ȡ������δ���Ķ���
	 */
	public boolean GetNextSendMsg(Integer comIndex, Memory msg,boolean deleteAfterRead);
	
	/**
	 * ��ȡ����ʧ�ܵĶ���Ϣ
	 * @param comIndex ģ�������ӵ�COM�ڱ��
	 * @param msg ȡ�õĶ���Ϣ������
	 * @return True �ɹ���ȡ����ʧ�ܵĶ��� False δ��������ʧ�ܵĶ���
	 * 	����Ϣ��ʽ���������к�+��|�����Ͷ˺���+��|��+��������
		������168���͡�GP 0001�����������к�Ϊ1�����ö��ŷ���ʧ�ܣ��򱾺�����������ϢΪ��1|168|GP 0001
		1) ���з���ʧ�ܵĶ��ţ��������������ȷ���ʧ�ܵĶ���
		2) �ɹ���ȡ����ʧ�ܵĶ��ź󣬸ö��žʹӶ�����ɾ��
	 */
	public boolean GetFailedMsg(Integer comIndex,Memory msg);
	
	/**
	 * ȡ�ö������ĺ���
	 * @param comIndex ģ�������ӵ�COM�ڱ��
	 * @param SCA ȡ�õĶ������ĺ��������
	 * @return True �ɹ�ȡ�ö������ĵĺ��� False ȡ�������ĺ���ʧ��
	 * �˺���ֻ����ģ���Ѵ���ģ�����ʱ���ã����򽫷���False,ģ�������շ����Ż򿪻���ĳ�ʼ�������У����øú�����������False
	 */
	public boolean GetSCA(Integer comIndex,Memory SCA);
	
	/**
	 * ���ö������ĺ���
	 * @param comIndex ģ�������ӵ�COM�ڱ��
	 * @param SCA �������ĵĺ���
	 * @return True �ɹ������˶������ĺ��� False ���ö������ĺ���ʧ��
	 * �˺���ֻ����ģ���Ѵ���ģ�����ʱ���ã����򽫷���Falseģ�������շ����Ż򿪻���ĳ�ʼ�������У����øú�����������False
	 */
	public boolean SetSCA(Integer comIndex,String SCA);
}
