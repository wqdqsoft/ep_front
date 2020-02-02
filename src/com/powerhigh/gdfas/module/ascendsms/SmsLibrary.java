package com.powerhigh.gdfas.module.ascendsms;

import com.sun.jna.Library;
import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface SmsLibrary extends Library {
	public static SmsLibrary INSTANCE = (SmsLibrary) Native.loadLibrary((Platform.isWindows() ? "AscendSMS" : "c"), SmsLibrary.class);

	/**
	 * 打开模块
	 * 
	 * @param commIndex
	 *            模块所连接的COM口编号
	 * @return 0 成功 -5 模块注册失败 -6 打开COM口失败 -9 未检测到GSM模块 -10 设置模块参数失败 -11 保存模块参数失败
	 */
	public Integer OpenComm(Integer commIndex);

	/**
	 * 关闭模块
	 * 
	 * @param commIndex
	 *            模块所连接的COM口编号
	 * @return 0 成功 -1 发送队列中还有未发消息待发 -7 关闭COM口失败 -8 模块未打开
	 */
	public Integer CloseComm(Integer commIndex);
	
	/**
	 * 强制关闭模块
	 * @param commIndex 模块所连接的COM口编号
	 * @return 0 成功 -7 关闭COM口失败 -8 模块未打开
	 * 	1) 若队列中还有未处理消息，调用该函数将会丢失所以未处理短信
		2) 建议调用该函数前先循环调用GetNextSendMsg将所有未发的短信依次取出，以便保存所有未发短信的信息，提高系统的可靠性
	 */
	public Integer ForceCloseComm(Integer commIndex);
	
	/**
	 * 读取模块的序列号（IMEI）
	 * @param commIndex 模块所连接的COM口编号
	 * @param sn 取得的序列号的引用
	 * @return True 成功 False 失败
	 */
	public boolean GetSerialNo(Integer commIndex,Memory sn);

	/**
	 * 发送短信息
	 * 
	 * @param commIndex
	 *            模块所连接的COM口编号
	 * @param msg
	 *            短消息内容
	 * @param mobileNo
	 *            接收端手机号码，包括普通手机号码和特殊号码
	 * @param msgIndex
	 *            消息序号，开发商自己定义，方便检索
	 * @param isChinese
	 *            是否为中文信息（中文为True，否则为False）
	 * @return 0 成功，消息已插入发送队列 -2 消息长度超过最大长度 -3 手机号码不正确 -4 发送队列已满 -8 模块未打开
	 */
	public Integer SendMsg(Integer commIndex, String msg, String mobileNo,
			Integer msgIndex, boolean isChinese);

	/**
	 * 接收短信
	 * 
	 * @param comIndex
	 *            模块所连接的COM口编号
	 * @param newMsg
	 *            取得的短信息的引用
	 * @return true:成功接收到新的短信 false:未接收到新的短信 1) 返回值为True时，NewMsg为接收到的短信；
	 *         返回值为False且NewMsg为：“Module Error”时，表示模块当前无响应，可能是断电或模块出故障；
	 *         返回值为False且NewMsg为空时表示模块在正常工作但没有收到短信 2)
	 *         短信息格式：发送端号码+“|”+接收时间+“|”+短信内容。
	 *         此处接收时间指的是短信中心接收到该短信的时间，例如向168发送“GP 0001”，接收到的信息为：168|01-09-28
	 *         12:33:39|<0001>深发展Ａ买12.66卖12.68现12.68高12.75低12.55昨12.49量488.87K12:32
	 */
	public boolean GetNewMsg(Integer comIndex, Memory newMsg);

	/**
	 * 取得发送队列中未发短消息的数目
	 * 
	 * @param comIndex
	 *            模块所连接的COM口编号
	 * @return 发送队列中未发短消息的数目
	 */
	public Integer GetUnSendCount(Integer comIndex);

	/**
	 * 读取发送队列中下一条要发送的短信息，通常用于监控实际的发送情况。
	 * 
	 * @param comIndex
	 *            模块所连接的COM口编号
	 * @param msg
	 *            取得的短信息的引用
	 * @param deleteAfterRead
	 *            读取该短信后是否将它从队列中删除，True表示读取后删除该短信，False表示读取后保留该短信
	 * @return True 成功读取到下一条要发送短信 False 未读到下一条要发送的短信
	 * 	1) 该函数只返回发送队列中的第1条未发短信
		2) 如果要读取后面的第2条未发短信，可以等到第1条短信发送完毕，或调用本函数时设置参数DeleteAfterRead为True，
		即读取第1条未发短信后将其删除，这样当前的第2条未发短信将成为第1条未发短信，如此循环可取出所有未发的短信
	 */
	public boolean GetNextSendMsg(Integer comIndex, Memory msg,boolean deleteAfterRead);
	
	/**
	 * 读取发送失败的短信息
	 * @param comIndex 模块所连接的COM口编号
	 * @param msg 取得的短信息的引用
	 * @return True 成功读取发送失败的短信 False 未读到发送失败的短信
	 * 	短信息格式：发送序列号+“|”发送端号码+“|”+短信内容
		例如向168发送“GP 0001”，假设序列号为1，若该短信发送失败，则本函数读到的信息为：1|168|GP 0001
		1) 若有发送失败的短信，本函数返回最先发送失败的短信
		2) 成功读取发送失败的短信后，该短信就从队列中删除
	 */
	public boolean GetFailedMsg(Integer comIndex,Memory msg);
	
	/**
	 * 取得短信中心号码
	 * @param comIndex 模块所连接的COM口编号
	 * @param SCA 取得的短信中心号码的引用
	 * @return True 成功取得短信中心的号码 False 取短信中心号码失败
	 * 此函数只能在模块已打开且模块空闲时调用，否则将返回False,模块正在收发短信或开机后的初始化过程中，调用该函数都将返回False
	 */
	public boolean GetSCA(Integer comIndex,Memory SCA);
	
	/**
	 * 设置短信中心号码
	 * @param comIndex 模块所连接的COM口编号
	 * @param SCA 短信中心的号码
	 * @return True 成功设置了短信中心号码 False 设置短信中心号码失败
	 * 此函数只能在模块已打开且模块空闲时调用，否则将返回False模块正在收发短信或开机后的初始化过程中，调用该函数都将返回False
	 */
	public boolean SetSCA(Integer comIndex,String SCA);
}
