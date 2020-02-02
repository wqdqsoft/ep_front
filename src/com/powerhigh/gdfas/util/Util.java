package com.powerhigh.gdfas.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.sql.DataSource;

import org.apache.log4j.Category;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

import com.powerhigh.gdfas.parse.Decode_0F;


/**
 * Description: 公共静态方法类
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * 编写时间: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 修改人： 修改时间：
 */
public class Util {

	// 构造函数
	public Util() {

	}

	private static Category cat = Category
			.getInstance(com.powerhigh.gdfas.util.Util.class);

	public static String DoubleToString(double d, int xsw) {
		BigDecimal original = new BigDecimal(d);
		BigDecimal result = original.setScale(xsw, BigDecimal.ROUND_HALF_DOWN);
		return result.toString();
	}

	/**
	 * 方法简述：转换部规数据域
	 * <p>
	 * 
	 * @param flag
	 *            String 符号 +:加33H; -:减33H
	 * @param sjy
	 *            String 数据域
	 * 
	 * @return String
	 *         <p>
	 */
	public static String tranBgsjy(String flag, String sjy) {
		String rs = "";
		for (int i = 0; i < sjy.length() / 2; i++) {
			String temps = sjy.substring(i * 2, (i + 1) * 2);
			int tempi = Integer.parseInt(temps, 16);
			if (flag.equals("-")) {
				tempi = tempi - 51;
			} else if (flag.equals("+")) {
				tempi = tempi + 51;
			}
			temps = Util.decStrToHexStr(tempi, 1);

			rs = rs + temps;
		}
		return rs;
	}

	/**
	 * 方法简述：取省规主站地址(HEX字符串)
	 * <p>
	 * 
	 * @param msta
	 *            表示主站地址
	 * @param iseq表示帧内序号
	 * @param fseq表示帧序号
	 * 
	 * @return 返回ByteBuffer类型的MSTA&SEQ
	 *         <p>
	 */

	public static String getSgZzdz(String msta, int iseq, int fseq) {

		ByteBuffer bb = ByteBuffer.allocate(2);

		byte[] bt = Util.int2byte((fseq % 4) * 64 + Util.strtoint(msta));
		bb.put((byte) bt[0]);
		bt = Util.int2byte((iseq * 32) + fseq / 4);
		bb.put((byte) bt[0]);

		byte[] by = bb.array();

		return bytetostrs(by);
	}

	/**
	 * 方法简述：取帧序号
	 * 
	 * @param d
	 *            低位字节
	 * @param g
	 *            高位字节
	 * @return zxh 返回换算后的帧序号
	 */

	public static int getZxh(byte d, byte g) throws Exception {

		int zxh = 0;
		try {
			g = (byte) (g & 0x1F);
			g = (byte) (g << 2);
			d = (byte) (d & 0xC0);
			int b_d = bytetoint(d);
			d = (byte) (b_d >> 6);
			zxh = g + d;
		} catch (Exception e) {
			throw e;
		}
		return zxh;

	}

	public static String makeFloat(String src, String format) {
		int zs_len = 0;
		int xs_len = 0;
		int index = format.indexOf(".");
		if (index == -1) {
			zs_len = format.length();
			xs_len = 0;
		} else {
			zs_len = format.indexOf(".");
			xs_len = format.substring(format.indexOf(".") + 1).length();
		}
		String zs = "";
		String xs = "";
		if (src.indexOf(".") == -1) {
			zs = Util.addODD(src, zs_len, "0");// zs_len可能会为奇数
			for (int i = 0; i < xs_len; i++) {
				xs += "0";
			}
		} else {
			for (int i = 0; i < xs_len - 1; i++) {
				src += "0";
			}
			zs = src.substring(0, src.indexOf("."));
			zs = Util.addODD(zs, zs_len, "0");// zs_len可能会为奇数
			xs = src.substring(src.indexOf(".") + 1);
			xs = xs.substring(0, xs_len);

		}
		return zs + xs;
	}

	public static String getFloat(String src, String format) {
		String res = "";
		int idx = format.indexOf(".");
		res = src.substring(0, idx) + "." + src.substring(idx);
		res = String.valueOf(Float.parseFloat(res));

		return res;
	}

	public static String add(String src, int num, String add) {
		String des = src;
		for (int i = 0; i < num * 2 - src.length(); i++) {
			des = add + des;
		}
		return des;
	}

	public static String addODD(String src, int num, String add) {
		String des = src;
		for (int i = 0; i < num - src.length(); i++) {
			des = add + des;
		}
		return des;
	}

	public static String addAfter(String src, int num, String add) {
		String des = src;
		for (int i = 0; i < num * 2 - src.length(); i++) {
			des = des + add;
		}
		return des;
	}

	/**
	 *中文转换,8859_1->GB2312 只能用于浏览器中读取，从文本读取不行
	 * 
	 * @param s_name
	 *            要转换的字符串
	 *@return 返回GB2312码
	 */
	public static String get_gb(String s_name) {
		String s_unicode = "";
		try {
			s_name = s_name.trim();
			byte[] bytes = s_name.getBytes("8859_1"); //
			s_unicode = new String(bytes, "GB2312"); //
		} catch (UnsupportedEncodingException e) {
			// System.out.println(e.toString());
			e.printStackTrace(System.out);
		}
		return s_unicode;
	}

	/*
	 * 功能：字符串替换replace(目标字符串,需替换的字符串,应替换的字符串)<br>返回替换成功的字符串<br>
	 * 例：将"123456"中"34"替换成"89"，结果为"128956"<br> replace("123456","34","89") ==>
	 * "128956"<br> 只替换一次<br>
	 * 
	 * @param s_source 原来的字符串
	 * 
	 * @param s_old 要被替换的字符串
	 * 
	 * @param s_new 替换成新的字符串
	 * 
	 * @return 替换完的字符串
	 */
	public static final String replace_one(String s_source, String s_old,
			String s_new) {
		String ch = "";
		int len = 0;

		if (s_new == null) {
			s_new = "";
		}
		if (s_source.length() == 0) {
			return s_source;
		}
		if (s_old.compareTo(s_new) == 0) {
			return s_source;
		}

		ch = s_source;
		len = ch.length();
		int chlen = ch.indexOf(s_old);

		if (chlen >= 0) {
			s_source = ch.substring(0, chlen) + s_new
					+ ch.substring(chlen + s_old.length(), len);

		}
		return s_source;
	}

	/**
	 * 方法简述：传输字节串转换成整型数
	 * <p>
	 * （低位在前高位在后,从字符串的
	 * <p>
	 * 第offset位开始的四位，0～？）
	 * <p>
	 * 
	 * @param bt
	 *            表示要转换的字节串,
	 * @param offset表示从bt的第几个位开始
	 *            ,即bt的开始下标
	 * @return st 返回转换好的整型数
	 */

	public static int byte2int(byte[] bt, int offset) {
		int st = 0;
		int tmp;
		int mod = 0xff;
		for (int i = offset; i < offset + 4; i++) {
			tmp = bt[i];
			st = st + ((tmp & mod) << (8 * (i - offset)));
		}
		return st;
	}

	/**
	 * 方法简述：传输字节串转换成短整型数
	 * <p>
	 * （低位在前高位在后,从字符串的第offset位
	 * <p>
	 * 
	 * @param bt
	 *            表示要转换的字节串,
	 * @param offset表示从bt的第几个位开始
	 *            ,即bt的开始下标
	 * @return st 返回转换好的整型数
	 */

	public static short byte2short(byte[] bt, int offset) {
		int st = 0;
		int

		tmp;
		int mod = 0xff;
		for (int i = offset; i < offset + 2; i++) {
			tmp = bt[i];
			st = st + ((tmp & mod) << (8 * (i - offset)));
		}
		return (short) st;
	}

	/**
	 * 方法简述：传输字节串转化成字符型（从字符串的第satpos位开始，到endpos位结束0～？）
	 * 
	 * @param bt
	 *            要转换的字节数组
	 * @param satpos
	 *            字节数组的开始下标
	 * @param endpos
	 *            字节数组的结束下标
	 * @return 返回转换好的字符串
	 */

	public static String byte2str(byte[] bt, int satpos, int endpos) {

		return new String(bt, satpos, endpos - satpos);
	}

	/**
	 * 方法简述 :把ByteBuffer转换为String
	 * 
	 * @param bb
	 *            要转换的ByteBuffer
	 * @return 转换好的String
	 */
	public static String bytebuf2str(ByteBuffer bb) {
		int position = bb.position();
		bb.rewind();
		int i_len = bb.remaining();
		byte[] bbs = new byte[i_len];
		bb.get(bbs, 0, i_len);
		bb.position(position);
		return bytetostrs(bbs);
	}

	/**
	 * 方法简述 :把byte[]转换为String
	 * 
	 * @param by
	 *            要转换的byte[]
	 * @return 转换好的String
	 */
	public static String bytes2str(byte[] by) {
		int len = by.length;
		byte[] tmp = new byte[4];
		String str = "";
		for (int i = 0; i < len; i++) {
			tmp[0] = by[i];
			if ((i % 10 == 0) && (i > 0)) {
				str = str + "," + Util.bytetostr(tmp).substring(4, 6);
			} else {
				str = str + " " + Util.bytetostr(tmp).substring(4, 6);
			}

		}
		return str;
	}

	/**
	 * 方法简述：将 字节串转换成int（单字节）
	 * 
	 * @param bt
	 *            表示要转换的字节
	 * @return i 返回转换好的整数
	 */

	public static int bytetoint(byte bt) {

		String str = Byte.toString(bt);

		int i = Integer.parseInt(str);
		if (i < 0) {

			i = 256 + i;

		}

		return i;
	}

	/**
	 * 方法简述：将字节串转换成十六进制的字符串
	 * <p>
	 * (处理双字节,如输入0x68a8，
	 * <p>
	 * 转换以后返回字符：a868)
	 * <p>
	 * 
	 * @param bt
	 *            表示要转换的字节串
	 * @return str 返回转换好的字符串
	 */

	public static String bytetostr(byte[] bt) {

		int i = byte2int(bt, 0);
		String str = short2str(i, 16);
		return str;
	}

	/**
	 * 方法简述：将 byte[] 转换成String （多字节）
	 * 
	 * @param by
	 *            表示要转换的字节
	 * @return str 返回转换好的String
	 */

	public static String bytetostrs(byte[] by) {

		int len = by.length;
		byte[] tmp = new byte[4];
		String str = "";
		for (int i = 0; i < len; i++) {

			tmp[0] = by[i];
			str = str + Util.bytetostr(tmp).substring(4, 6);

		}
		// cat.debug(str);
		return str.toUpperCase();

	}

	/**
	 * 方法简述：将 byte[] 转换成ASCII字符
	 * 
	 * @param bt
	 *            byte[] 表示要转换的字节
	 * 
	 * @return str 返回转换好的String
	 */
	public static String bytestoASCII(byte[] bt) {
		String str = "";
		for (int i = 0; i < bt.length; i++) {
			int ii = (int) bt[i];
			Character d = new Character((char) ii);
			str = str.concat(d.toString());
		}
		return str;
	}

	/**
	 * 方法简述：整型数转换成传输字节串（低位在前高位在后）
	 * <p>
	 * 
	 * @param st
	 *            表示要转换的整型数
	 * @return bt 返回转换好的字节串(四个字节)
	 */

	public static byte[] int2byte(int st) {
		byte[] bt = new byte[4];
		int tmp = st;
		int mod = 0xff;
		for (int i = 0; i < 4; i++, tmp >>= 8) {
			bt[i] = (byte) (tmp & mod);
		}
		return bt;
	}

	/**
	 * 方法简述：短整型数转换成传输字节串（低位在前高位在后）
	 * <p>
	 * 
	 * @param st
	 *            表示要转换的整型数
	 * @return bt 返回转换好的字节串(两个字节)
	 */

	public static byte[] short2byte(int st) {
		byte[] bt = new byte[2];
		int tmp = st;
		int mod = 0xff;
		for (int i = 0; i < 2; i++, tmp >>= 8) {
			bt[i] = (byte) (tmp & mod);
		}
		return bt;
	}

	/**
	 * 方法简述：将短整型转换成字符型,DoH为进制的表式方法，
	 * <p>
	 * 16进制表示成0xFFFF这样的
	 * <p>
	 * 
	 * @param st
	 *            表示要转换的短整数
	 * @param DoH
	 *            表示要转换的进制表式方法
	 * @return int 返回转换好的字符
	 */

	public static String short2str(int st, int DoH) {
		String rstr = "";
		int rint = st;
		if (DoH == 10) {
			rstr = String.valueOf(st);
		}
		if (DoH == 16) {
			for (int i = 0; i < 4; i++) {
				if (rint == 0) {
					rstr = "0" + rstr;
				} else if (rint < 16) {
					rstr = sixteen2ten(rint) + rstr;
					rint = 0;
				} else {
					rstr = sixteen2ten(rint % 16) + rstr;
					rint = (rint - (rint % 16)) / 16;
				}
			}
			rstr = "0x" + rstr;
		}
		return rstr;
	}

	/**
	 * 方法简述：将10进制转换成16进制，只提供一位转换功能，
	 * <p>
	 * 即入参必须小于16
	 * <p>
	 * 
	 * @param st
	 *            表示要转换的整数
	 * @return Rs 返回转换好的16进制表示
	 */

	public static String sixteen2ten(int st) {
		String Rs = "";
		switch (st) {
		case 10:
			Rs = "A";
			break;
		case 11:
			Rs = "B";
			break;
		case 12:
			Rs = "C";
			break;
		case 13:
			Rs = "D";
			break;
		case 14:
			Rs = "E";
			break;
		case 15:
			Rs = "F";
			break;
		default:
			Rs = String.valueOf(st);
		}
		return Rs;
	}

	/**
	 * 方法简述：字符形转换成传输字节串,
	 * <p>
	 * 将每一个字符转换成相应的ASCII
	 * <p>
	 * 例:输入"aa",bt存储aa的ASCII值
	 * 
	 * @param st
	 *            表示要转换的字符串
	 * @return bt 返回转换好的字节串
	 */

	public static byte[] str2byte(String st) {
		byte[] bt = st.getBytes();
		return bt;
	}

	/**
	 * 把字符串转化成byte[]，输入"12456f",输出0x12,0x45,0x6f,如果输入的字符总数为奇数， 最后一个字符将被忽略
	 * 
	 * @return 转化好的byte[]
	 */
	public static byte[] str2bytes(String s) {
		char[] cc = s.toCharArray();
		int i_len = cc.length;
		if (i_len % 2 != 0) {
			i_len = i_len - 1;
		}
		byte[] bb = new byte[i_len / 2];
		int j = 0;

		for (int i = 0; i < i_len; i = i + 2) {
			bb[j] = (byte) ((Character.digit(cc[i], 16) << 4) + Character
					.digit(cc[i + 1], 16));
			j++;
		}

		return bb;
	}

	/**
	 * 方法简述：字符串转换成数字（16进制和8进制均可）
	 * <p>
	 * 
	 * @param st
	 *            表示要转换的字符串,字符串必须是数字型的,
	 *            <p>
	 *            如:"123",不能是"abc",如果是十六进制加前缀"0x",其他仿此;
	 *            <p>
	 * @return 返回转换好的数字
	 */
	public static int str2int(String st) {
		if (st.substring(0, 2).compareTo("0x") == 0) {
			return Integer.parseInt(st.substring(2), 16);
		} else {
			return Integer.parseInt(st);
		}
	}

	/**
	 * 方法简述：将 String 转换成 byte[]（多字节）
	 * 
	 * @param str
	 *            表示要转换的String
	 * @return by 返回转换好的字节
	 */

	public static byte[] strstobyte(String str) {

		int i_len = str.length();
		byte[] by = new byte[i_len / 2];

		byte[] tmp = new byte[4];
		for (int i = 0; i < i_len / 2; i++) {

			tmp = Util.strtobyte(str.substring(2 * i, 2 * i + 2));
			by[i] = tmp[0];

		}
		// cat.debug("将 String 转换成 byte[]（多字节）:str＝"+str+"byte[]");
		// util.print_byte(by);
		return by;

	}

	/**
	 * 方法简述：将 String 转换成 ByteBuffer（多字节）
	 * 
	 * @param str
	 *            String 表示要转换的String
	 * @return buffer ByteBuffer 返回转换好的字节
	 */

	public static ByteBuffer strstobytebuf(String str) {
		ByteBuffer buffer = null;

		byte[] bt = Util.strstobyte(str);
		buffer = ByteBuffer.wrap(bt);
		buffer.rewind();
		return buffer;

	}

	/**
	 * 方法简述：将以标识区分的字符串转换成String[]
	 * 
	 * @param str
	 *            原始字符串
	 * @param bzw
	 *            标志位
	 * @return target[] 返回转换好的字符串数组
	 */

	public static String[] strtoarr(String str, String bzw) {

		int tempi = 0;
		int i_suffix = 1;
		String temps = str;
		// 计算数组长度
		while (temps.indexOf(bzw) != -1) {
			tempi = temps.indexOf(bzw);
			i_suffix = i_suffix + 1;
			temps = temps.substring(tempi + 1, str.length());
		}
		// 转换数组
		String[] s_array = new String[i_suffix];
		for (int k = 0; k < i_suffix; k++) {
			tempi = str.indexOf(bzw);
			if (tempi != -1) {
				s_array[k] = str.substring(0, tempi);
				s_array[k + 1] = str.substring(tempi + 1, str.length());
				str = s_array[k + 1];
			}
		}

		return s_array;
	}

	/**
	 * 方法简述：将十六进制的字符形转换成传输字串
	 * <p>
	 * (处理4位十六进制的字符表示,如输入“68a8”，
	 * <p>
	 * 转换以后返回0xa868的双字节)
	 * <p>
	 * 
	 * @param str
	 *            表示要转换的字符
	 * @return bt 返回转换好的双字节串
	 */

	public static byte[] strtobyte(String str) {

		int i = Integer.parseInt(str, 16);
		byte[] bt = short2byte(i);
		return bt;
	}

	/**
	 * 方法简述：将十六进制的字符形转换成int
	 * <p>
	 * (处理任意位十六进制的字符表示,如输入“68a8”，
	 * <p>
	 * 转换以后返回整数：26792)
	 * <p>
	 * 
	 * @param str
	 *            表示要转换的字符
	 * @return i 返回转换好的整数
	 */

	public static int strtoint(String str) {

		int i = Integer.parseInt(str, 16);
		return i;
	}

	// 2个字符为一个单位颠倒
	public static String convertStr(String inStr) throws Exception {
		String outStr = "";
		try {
			int len = inStr.length();
			for (int i = 0; i < len / 2; i++) {
				outStr += inStr.substring(len - 2 * (i + 1), len - 2 * i);
			}
			// outStr = inStr.substring(2, 4) + inStr.substring(0, 2);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return outStr;
	}

	// 1个字符为一个单位颠倒
	public static String convertStrODD(String inStr) throws Exception {
		String outStr = "";
		try {
			int len = inStr.length();
			for (int i = 0; i < len; i++) {
				outStr += inStr.substring(len - (i + 1), len - i);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return outStr;
	}

	public static String getCS(String sjz) {
		// 校验和(控制域、地址域、链路用户数据的字节的八位位组算术和，不考虑溢出位)
		String sCS = "";

		int tempi = 0;
		byte[] bt_temp = str2bytes(sjz);
		int len = bt_temp.length;
		for (int i = 0; i < len; i++) {

			tempi += bytetoint(bt_temp[i]);
		}

		byte[] tempb = new byte[4];
		tempb = int2byte(tempi);

		tempi = tempb[0];
		if (tempi < 0) {
			tempi = 256 + tempi;
		}
		tempi = tempi % 256;

		sCS = Integer.toHexString(tempi);

		if (sCS.length() < 2) {
			sCS = "0" + sCS;

		}
		return sCS.toUpperCase();
	}

	public static int getCS(byte[] bt_temp) {
		// 校验和(控制域、地址域、链路用户数据的字节的八位位组算术和，不考虑溢出位)
		String sCS = "";

		int tempi = 0;
		int len = bt_temp.length;
		for (int i = 0; i < len; i++) {

			tempi += bytetoint(bt_temp[i]);
		}

		byte[] tempb = new byte[4];
		tempb = int2byte(tempi);

		tempi = tempb[0];
		if (tempi < 0) {
			tempi = 256 + tempi;
		}
		tempi = tempi % 256;

		return tempi;
	}

	public static String getNowTime() {
		Calendar time_C = Calendar.getInstance();
		Date time_D = time_C.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmmss");
		String time_S = formatter.format(time_D);
		return time_S;
	}

	public static String addMinute(String srcTime, int minute) throws Exception {
		Calendar c = Calendar.getInstance();
		SimpleDateFormat sf = new SimpleDateFormat("yyMMddHHmm");
		Date date = sf.parse(srcTime);
		c.setTime(date);
		c.add(Calendar.MINUTE, minute);
		return sf.format(c.getTime());
	}

	/**
	 * 方法简述：获取当前时间(ms)
	 * 
	 * @return long
	 */
	public static long getNowTimeInMillis() {
		Calendar C = Calendar.getInstance();
		return C.getTimeInMillis();
	}

	public static String getTp(int iZdpfc, String rq, int iYssj) {
		String tp = "";

		tp = Util.decStrToHexStr(iZdpfc, 1) + rq.substring(10, 12)
				+ rq.substring(8, 10) + rq.substring(6, 8) + rq.substring(4, 6)
				+ Util.decStrToHexStr(iYssj, 1);

		return tp;
	}

	public static String getNowWeek() {
		Calendar time_C = Calendar.getInstance();
		Date time_D = time_C.getTime();
		SimpleDateFormat formatter = new SimpleDateFormat("EE");
		String time_S = formatter.format(time_D);
		if (time_S.equals("星期一")) {
			time_S = "1";
		}
		if (time_S.equals("星期二")) {
			time_S = "2";
		}
		if (time_S.equals("星期三")) {
			time_S = "3";
		}
		if (time_S.equals("星期四")) {
			time_S = "4";
		}
		if (time_S.equals("星期五")) {
			time_S = "5";
		}
		if (time_S.equals("星期六")) {
			time_S = "6";
		}
		if (time_S.equals("星期日")) {
			time_S = "7";

		}
		return time_S;
	}

	/**
	 * 方法简述：根据日期得到星期
	 * 
	 * @param rq
	 *            String 日期(yymmddhhmmss)
	 * @return sRet String 返回String型
	 */
	public static String getWeek(String rq) throws Exception {
		try {
			SimpleDateFormat fm1 = new SimpleDateFormat("yyMMddHHmmss");
			Date time_D = fm1.parse(rq);

			SimpleDateFormat fm2 = new SimpleDateFormat("EE");
			String time_S = fm2.format(time_D);
			if (time_S.equals("星期一")) {
				time_S = "1";
			}
			if (time_S.equals("星期二")) {
				time_S = "2";
			}
			if (time_S.equals("星期三")) {
				time_S = "3";
			}
			if (time_S.equals("星期四")) {
				time_S = "4";
			}
			if (time_S.equals("星期五")) {
				time_S = "5";
			}
			if (time_S.equals("星期六")) {
				time_S = "6";
			}
			if (time_S.equals("星期日")) {
				time_S = "7";

			}
			return time_S;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * 方法简述：根据序号得出时间段
	 * 
	 * @param xh
	 *            String 序号(1-48)
	 * @return sRet String 返回String型(hh:mm)
	 */
	public static String getSD(String xh) throws Exception {
		try {
			String sRet = "";
			int i_xh = Integer.parseInt(xh);
			switch (i_xh) {
			case 0:
				sRet = "00:00";
				break;
			case 1:
				sRet = "00:30";
				break;
			case 2:
				sRet = "01:00";
				break;
			case 3:
				sRet = "01:30";
				break;
			case 4:
				sRet = "02:00";
				break;
			case 5:
				sRet = "02:30";
				break;
			case 6:
				sRet = "03:00";
				break;
			case 7:
				sRet = "03:30";
				break;
			case 8:
				sRet = "04:00";
				break;
			case 9:
				sRet = "04:30";
				break;
			case 10:
				sRet = "05:00";
				break;
			case 11:
				sRet = "05:30";
				break;
			case 12:
				sRet = "06:00";
				break;
			case 13:
				sRet = "06:30";
				break;
			case 14:
				sRet = "07:00";
				break;
			case 15:
				sRet = "07:30";
				break;
			case 16:
				sRet = "08:00";
				break;
			case 17:
				sRet = "08:30";
				break;
			case 18:
				sRet = "09:00";
				break;
			case 19:
				sRet = "09:30";
				break;
			case 20:
				sRet = "10:00";
				break;
			case 21:
				sRet = "10:30";
				break;
			case 22:
				sRet = "11:00";
				break;
			case 23:
				sRet = "11:30";
				break;
			case 24:
				sRet = "12:00";
				break;
			case 25:
				sRet = "12:30";
				break;
			case 26:
				sRet = "13:00";
				break;
			case 27:
				sRet = "13:30";
				break;
			case 28:
				sRet = "14:00";
				break;
			case 29:
				sRet = "14:30";
				break;
			case 30:
				sRet = "15:00";
				break;
			case 31:
				sRet = "15:30";
				break;
			case 32:
				sRet = "16:00";
				break;
			case 33:
				sRet = "16:30";
				break;
			case 34:
				sRet = "17:00";
				break;
			case 35:
				sRet = "17:30";
				break;
			case 36:
				sRet = "18:00";
				break;
			case 37:
				sRet = "18:30";
				break;
			case 38:
				sRet = "19:00";
				break;
			case 39:
				sRet = "19:30";
				break;
			case 40:
				sRet = "20:00";
				break;
			case 41:
				sRet = "20:30";
				break;
			case 42:
				sRet = "21:00";
				break;
			case 43:
				sRet = "21:30";
				break;
			case 44:
				sRet = "22:00";
				break;
			case 45:
				sRet = "22:30";
				break;
			case 46:
				sRet = "23:00";
				break;
			case 47:
				sRet = "23:30";
				break;
			case 48:
				sRet = "24:00";
				break;
			default:
				sRet = "00:00";
				break;

			}
			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	public static String getRwfszqdw(String str) {
		String sRet = "";
		if (str.equals("00")) {
			sRet = "分";
		} else if (str.equals("01")) {
			sRet = "时";
		} else if (str.equals("10")) {
			sRet = "日";
		} else if (str.equals("11")) {
			sRet = "月";
		}

		return sRet;
	}

	/**
	 *方法简述：取序列
	 * 
	 * @return seq String 序列
	 */
	public static String getSeq(Connection con, String seqName)
			throws Exception {

		String seq = "";

		try {
			// 数据帧发送序列
			String s_sql = "select " + seqName + ".NEXTVAL seq FROM DUAL";
			ArrayList lst = (ArrayList) CMDb.getCollection(con, s_sql);
			seq = String.valueOf(((HashMap) lst.get(0)).get("seq"));
		} catch (Exception e) {
			System.out.println("取序列" + seqName + "出错：");
			e.printStackTrace();
			throw e;
		}
		return seq;

	}

	/**
	 *方法简述：取序列
	 * 
	 * @return seq String 序列
	 */
	public static String getSeq(JdbcTemplate jdbcT, String seqName)
			throws Exception {

		String seq = "";

		try {
			// 数据帧发送序列
			String s_sql = "select " + seqName.toUpperCase()
					+ ".NEXTVAL seq FROM DUAL";
			List lst = jdbcT.queryForList(s_sql);
			seq = String.valueOf(((HashMap) lst.get(0)).get("seq"));
		} catch (Exception e) {
			System.out.println("取序列" + seqName + "出错：");
			e.printStackTrace();
			throw e;
		}
		return seq;

	}

	/**
	 *方法简述：取数据帧发送表的发送序列SEQ_SJZFS
	 * 
	 * @return seq_sjzfs String 数据帧发送序列
	 */
	public static String getSeqSjzfs(Connection con) throws Exception {

		String seq_sjzfs = "";

		try {
			// 数据帧发送序列
			String s_sql = "select SEQ_SJZFS.NEXTVAL seq_sjzfs FROM DUAL";
			ArrayList lst = (ArrayList) CMDb.getCollection(con, s_sql);
			seq_sjzfs = String.valueOf(((HashMap) lst.get(0)).get("seq_sjzfs"));
		} catch (Exception e) {
			System.out.println("取序列SEQ_SJZFS出错：");
			e.printStackTrace();
			throw e;
		}
		return seq_sjzfs;

	}

	/**
	 *方法简述：取数据帧发送表的发送序列SEQ_SJZFS
	 * 
	 * @return seq_sjzfs String 数据帧发送序列
	 */
	public static String getSeqSjzfs(JdbcTemplate jdbcT) throws Exception {

		String seq_sjzfs = "";

		try {
			// 数据帧发送序列
			String s_sql = "select SEQ_SJZFS.NEXTVAL seq_sjzfs FROM DUAL";
			List lst = (List) jdbcT.queryForList(s_sql);
			seq_sjzfs = String.valueOf(((Map) lst.get(0)).get("seq_sjzfs"));
		} catch (Exception e) {
			System.out.println("取序列SEQ_SJZFS出错：");
			e.printStackTrace();
			throw e;
		}
		return seq_sjzfs;

	}

	/**
	 *方法简述：取数据帧发送表的发送序列SEQ_SJZFS
	 * 
	 * @return seq_sjzfs String 数据帧发送序列
	 */
	public static String getSeqRwid(JdbcTemplate jdbcT) throws Exception {

		String seq_sjzfs = "";

		try {
			// 数据帧发送序列
			String s_sql = "select SEQ_RWID.NEXTVAL seq FROM DUAL";
			List lst = (List) jdbcT.queryForList(s_sql);
			seq_sjzfs = String.valueOf(((Map) lst.get(0)).get("seq"));
		} catch (Exception e) {
			System.out.println("取序列SEQ_RWID出错：");
			e.printStackTrace();
			throw e;
		}
		return seq_sjzfs;

	}

	/**
	 *方法简述：取终端事件记录表的事件序列SEQ_EXCEPTION
	 * 
	 * @return seq_exception String 事件序列
	 */
	public static String getSeqException(Connection con) throws Exception {

		String seq_exception = "";

		try {
			// 事件序列
			String s_sql = "select SEQ_EXCEPTION.NEXTVAL seq_exception FROM DUAL";
			ArrayList lst = (ArrayList) CMDb.getCollection(con, s_sql);
			seq_exception = String.valueOf(((HashMap) lst.get(0))
					.get("seq_exception"));
		} catch (Exception e) {
			System.out.println("取序列SEQ_EXCEPTION出错：");
			e.printStackTrace();
			throw e;
		}
		return seq_exception;

	}

	/**
	 *方法简述：取终端事件记录表的事件序列SEQ_EXCEPTION
	 * 
	 * @return seq_exception String 事件序列
	 */
	public static String getSeqException(JdbcTemplate jdbcT) throws Exception {

		String seq_exception = "";

		try {
			// 事件序列
			String s_sql = "select SEQ_EXCEPTION.NEXTVAL seq_exception FROM DUAL";
			List lst = (List) jdbcT.queryForList(s_sql);
			seq_exception = String.valueOf(((Map) lst.get(0))
					.get("seq_exception"));
		} catch (Exception e) {
			System.out.println("取序列SEQ_EXCEPTION出错：");
			e.printStackTrace();
			throw e;
		}
		return seq_exception;

	}

	public static String getMcsx(String str) {
		String s_mcsx = "";
		int i_mcsx = Integer.parseInt(str.substring(1, 2), 16);
		s_mcsx = Integer.toBinaryString(i_mcsx);
		int len_mcsx = s_mcsx.length();
		for (int j = 0; j < 4 - len_mcsx; j++) {
			s_mcsx = "0" + s_mcsx;
		}
		s_mcsx = s_mcsx.substring(2, 4);
		i_mcsx = Integer.parseInt(s_mcsx, 2);
		if (i_mcsx == 0) {
			s_mcsx = "正向有功";
		} else if (i_mcsx == 1) {
			s_mcsx = "正向无功";
		} else if (i_mcsx == 2) {
			s_mcsx = "反向有功";
		} else if (i_mcsx == 3) {
			s_mcsx = "反向无功";
		}

		return s_mcsx;

	}

	/**
	 * 方法简述：将十进制字符转换成十六进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String decStrToHexStr(String str, int len) {
		String s = Long.toHexString(Long.parseLong(str));
		int s_len = s.length();
		for (int i = 0; i < 2 * len - s_len; i++) {
			s = "0" + s;
		}
		return s.toUpperCase();
	}

	/**
	 * 方法简述：将十进制字符转换成十六进制字符
	 * 
	 * @param value
	 *            int 要转换的整数
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String decStrToHexStr(long value, int len) {
		String s = Long.toHexString(value);
		int s_len = s.length();
		for (int i = 0; i < 2 * len - s_len; i++) {
			s = "0" + s;
		}
		return s.toUpperCase();
	}

	/**
	 * 方法简述：将十六进制字符转换成二进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String hexStrToBinStr(String str, int len) {
		String sRet = "";
		String temps = "";
		str = Util.add(str, len, "0");
		for (int i = 0; i < len; i++) {
			temps = str.substring(i * 2, (i + 1) * 2);
			temps = Long.toBinaryString(Long.parseLong(temps, 16));
			temps = Util.addODD(temps, 8, "0");
			sRet += temps;
		}

		return sRet;

		// long tempi = Long.parseLong(str,16);
		// String s = Long.toBinaryString(tempi);
		// int s_len = s.length();
		// for(int i=0;i<8*len-s_len;i++){
		// s = "0" + s;
		// }
		// return s.toUpperCase();
	}

	/**
	 * 方法简述：将十六进制字符转换成十进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String hexStrToDecStr(String str) {
		long tempi = Long.parseLong(str, 16);

		return String.valueOf(tempi);
	}

	/**
	 * 方法简述：将十进制字符转换成二进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String decStrToBinStr(long value, int len) {
		String s = Long.toBinaryString(value);
		int s_len = s.length();
		for (int i = 0; i < 8 * len - s_len; i++) {
			s = "0" + s;
		}
		return s.toUpperCase();
	}

	/**
	 * 方法简述：将十进制字符转换成二进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String decStrToBinStr(String str, int len) {
		String s = Long.toBinaryString(Long.parseLong(str));
		int s_len = s.length();
		for (int i = 0; i < 8 * len - s_len; i++) {
			s = "0" + s;
		}
		return s.toUpperCase();
	}

	/**
	 * 方法简述：将十进制字符转换成二进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 位数
	 * @return s String
	 */
	public static String decStrToBinStrByBit(int value, int len) {
		String s = Integer.toBinaryString(value);
		int s_len = s.length();
		for (int i = 0; i < len - s_len; i++) {
			s = "0" + s;
		}
		return s.toUpperCase();
	}

	/**
	 * 方法简述：将十进制字符转换成二进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 位数
	 * @return s String
	 */
	public static String decStrToBinStrByBit(String str, int len) {
		String s = Integer.toBinaryString(Integer.parseInt(str));
		int s_len = s.length();
		for (int i = 0; i < len - s_len; i++) {
			s = "0" + s;
		}
		return s.toUpperCase();
	}

	/**
	 * 方法简述：将二进制字符转换成十进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String binStrToDecStr(String str) {
		return String.valueOf(Long.parseLong(str, 2));
	}

	/**
	 * 方法简述：将二进制字符转换成十六进制字符
	 * 
	 * @param str
	 *            String 要转换的字符
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String binStrToHexStr(String str, int len) {

		String s = Util.addODD(str, len * 8, "0");
		String sRet = "";
		for (int i = 0; i < len; i++) {
			String temps = s.substring(i * 8, (i + 1) * 8);
			sRet += Util.add(Integer.toHexString(Integer.parseInt(temps, 2)),
					1, "0");
		}

		return sRet.toUpperCase();

		// int iStr = Integer.parseInt(str,2);
		// String s = Integer.toHexString(iStr);
		// int s_len = s.length();
		// for(int i=0;i<2*len-s_len;i++){
		// s = "0" + s;
		// }
		// return s.toUpperCase();
	}

	/**
	 * 方法简述：将整型转换成十六进制字符
	 * 
	 * @param num
	 *            int 要转换的整型
	 * @param len
	 *            int 字节数
	 * @return s String
	 */
	public static String intToHexStr(int num, int len) {
		String s = Integer.toHexString(num);
		int s_len = s.length();
		for (int i = 0; i < 2 * len - s_len; i++) {
			s = "0" + s;
		}
		return s.toUpperCase();
	}

	/**
	 * 方法简述：取信息点DT
	 * 
	 * @param iDA
	 *            int 整型的DA
	 * @return sDA String 返回十六进制的DA
	 */
	public static String getDT(int iDT) throws Exception {
		String sDT = "";
		String sDT1 = "";
		String sDT2 = "";
		int iDT1 = 0;
		int iDT2 = 0;
		try {
			if (iDT == 0) {
				sDT = "0000";
			} else {
				iDT1 = (iDT - 1) / 8;// 高字节
				iDT2 = (iDT % 8 == 0) ? 8 : (iDT % 8);// 低字节

				iDT2 = (int) Math.pow(2, iDT2 - 1);
				sDT1 = Util.intToHexStr(iDT1, 1);
				sDT2 = Util.intToHexStr(iDT2, 1);

				sDT = sDT1 + sDT2;
			}
		} catch (Exception e) {
			throw e;
		}

		return sDT;
	}

	/**
	 * 方法简述：取信息点DA
	 * 
	 * @param iDA
	 *            int 整型的DA
	 * @return sDA String 返回十六进制的DA
	 */
	public static String getDA(int iDA) throws Exception {
		// 2010-10-04
		if (iDA == 65535) {
			return "FFFF";
		}
		// 2009-10-21
		String sDA = "";

		String sDA1 = "";
		String sDA2 = "";
		int iDA1 = 0;
		int iDA2 = 0;
		if (iDA == 0) {
			sDA = "0000";
		} else {
			iDA1 = (iDA - 1) / 8 + 1;// 高字节
			iDA2 = (iDA % 8 == 0) ? 8 : (iDA % 8);// 低字节

			iDA2 = (int) Math.pow(2, iDA2 - 1);
			sDA1 = Util.intToHexStr(iDA1, 1);
			sDA2 = Util.intToHexStr(iDA2, 1);

			sDA = sDA1 + sDA2;
		}
		return sDA;
	}

	/**
	 * 方法简述：取信息点DA
	 * 
	 * @param str
	 *            String 信息点:"Pn"
	 * @return sDA String 返回十六进制的DA
	 */
	public static String getDA(String str) throws Exception {

		// 2010-10-04
		if (str.equals("P65535")) {
			return "FFFF";
		}
		// 2009-10-21
		String sDA = "";
		str = str.toUpperCase();
		if (str == null || str.equals(""))
			throw new Exception("InputString is null");
		int indx = str.indexOf("P");
		if (indx == -1)
			throw new Exception("InputString Don't Contain 'P'");

		try {
			String temps = str.substring(indx + 1);
			int iDA = Integer.parseInt(temps);

			String sDA1 = "";
			String sDA2 = "";
			int iDA1 = 0;
			int iDA2 = 0;
			if (iDA == 0) {
				sDA = "0000";
			} else {
				iDA1 = (iDA - 1) / 8 + 1;// 高字节
				iDA2 = (iDA % 8 == 0) ? 8 : (iDA % 8);// 低字节

				iDA2 = (int) Math.pow(2, iDA2 - 1);
				sDA1 = Util.intToHexStr(iDA1, 1);
				sDA2 = Util.intToHexStr(iDA2, 1);

				sDA = sDA1 + sDA2;
			}
		} catch (Exception e) {
			throw e;
		}
		return sDA;
	}

	/**
	 * 方法简述：取信息点DT
	 * 
	 * @param str
	 *            String 信息类:"Fn"
	 * @return sDA String 返回十六进制的DA
	 */
	public static String getDT(String str) throws Exception {
		String sDT = "";
		str = str.toUpperCase();
		if (str == null || str.equals(""))
			throw new Exception("InputString is null");
		int indx = str.indexOf("F");
		if (indx == -1)
			throw new Exception("InputString Don't Contain 'F'");

		try {
			String temps = str.substring(indx + 1);
			int iDT = Integer.parseInt(temps);

			String sDT1 = "";
			String sDT2 = "";
			int iDT1 = 0;
			int iDT2 = 0;
			if (iDT == 0) {
				sDT = "0000";
			} else {
				iDT1 = (iDT - 1) / 8;// 高字节
				iDT2 = (iDT % 8 == 0) ? 8 : (iDT % 8);// 低字节

				iDT2 = (int) Math.pow(2, iDT2 - 1);
				sDT1 = Util.intToHexStr(iDT1, 1);
				sDT2 = Util.intToHexStr(iDT2, 1);

				sDT = sDT1 + sDT2;
			}
		} catch (Exception e) {
			throw e;
		}
		return sDT;
	}

	// /**方法简述：转换信息点DA
	// * @param DA String 两字节的十六进制字符(信息点号)
	// * @return sRet String 返回十进制的字符
	// */
	// public static String tranDA(String DA) throws Exception{
	// if(DA.equals("0000")){
	// return "0";
	// }
	//          	
	// String sRet = "";
	// String DA1 = DA.substring(0,2);//高位
	// int iDA1 = Integer.parseInt(DA1,16);
	// iDA1 = (int)(Math.log(iDA1)/Math.log(2));
	//          	
	// String DA2 = DA.substring(2,4);//低位
	// int iDA2 = Integer.parseInt(DA2,16);
	// iDA2 = (int)(Math.log(iDA2)/Math.log(2));
	//        	
	// sRet = String.valueOf((iDA2+1) + (iDA1)*8);
	//        	
	// return sRet;
	// }

	// 2010-05-19
	/**
	 * 方法简述：转换信息点DA
	 * 
	 * @param DA
	 *            String 两字节的十六进制字符(信息点号)
	 * @return sRet String 返回十进制的字符
	 */
	public static String tranDA(String DA) throws Exception {
		if (DA.equals("0000")) {
			return "0";
		}

		String sRet = "";
		String DA1 = DA.substring(0, 2);// 高位
		int iDA1 = Integer.parseInt(DA1, 16);

//		System.out.println(iDA1);

		String DA2 = DA.substring(2, 4);// 低位
		int iDA2 = Integer.parseInt(DA2, 16);
		iDA2 = (int) (Math.log(iDA2) / Math.log(2));

		sRet = String.valueOf((iDA2 + 1) + (iDA1 - 1) * 8);

		return sRet;
	}

	/**
	 * 方法简述：转换信息类DT
	 * 
	 * @param DT
	 *            String 两字节的十六进制字符(信息类)
	 * @return sRet String 返回十进制的字符
	 */
	public static String tranDT(String DT) throws Exception {
		String sRet = "";
		String DT1 = DT.substring(0, 2);// 高位
		int iDT1 = Integer.parseInt(DT1, 16);

		String DT2 = DT.substring(2, 4);// 低位
		int iDT2 = Integer.parseInt(DT2, 16);
		iDT2 = (int) (Math.log(iDT2) / Math.log(2));

		sRet = String.valueOf((iDT2 + 1) + (iDT1) * 8);

		return sRet;
	}

	/**
	 * 方法简述：获取终端与主站通话状态
	 * 
	 * @param str
	 *            String 二进制字符
	 * @return sRet String 返回状态
	 */
	public static String getZdyzzthzt(String str) throws Exception {
		String sRet = "无效";

		if (str.equals("01")) {
			sRet = "允许与主站通话";
		} else if (str.equals("10")) {
			sRet = "禁止与主站通话";
		}

		return sRet;
	}

	/**
	 * 方法简述：获取终端主动上报状态
	 * 
	 * @param str
	 *            String 二进制字符
	 * @return sRet String 返回状态
	 */
	public static String getZdzdsbzt(String str) throws Exception {
		String sRet = "无效";

		if (str.equals("01")) {
			sRet = "允许主动上报";
		} else if (str.equals("10")) {
			sRet = "禁止主动上报";
		}

		return sRet;
	}

	/**
	 * 方法简述：获取数据帧的长度域
	 * 
	 * @param iLen
	 *            int
	 * @return sRet String 返回十六进制字符
	 */
	public static String getLEN(int iLen) throws Exception {
		// 04版
		// String sRet = "";
		// iLen = iLen * 2 + 1;
		// String sLEN = Util.decStrToHexStr(iLen,2);
		// sLEN = Util.convertStr(sLEN);
		// sRet = sLEN;
		// return sRet;

		// 698
		String sLEN = Util.decStrToBinStr(iLen, 2);
		sLEN = sLEN.substring(2) + "10";
		sLEN = Util.binStrToHexStr(sLEN, 2);
		sLEN = Util.convertStr(sLEN);
		return sLEN;

	}

	/**
	 * 方法简述：获取ASCII码字符
	 * 
	 * @param str
	 *            String 十六进制字符
	 * @return sRet String 返回ASCII码字符
	 */
	public static String getASCII(String str) throws Exception {
		String sRet = "";
		int len = str.length() / 2;
		for (int i = 0; i < len; i++) {
			String temps = str.substring(i * 2, (i + 1) * 2);
			Character d = new Character((char) Integer.parseInt(temps, 16));
			sRet = sRet.concat(d.toString());
		}

		return sRet;
	}

	/**
	 * 方法简述：获取ASCII码字符
	 * 
	 * @param bt
	 *            byte[] 字节流
	 * @return sRet String 返回ASCII码字符
	 */
	public static String getASCII(byte[] bt) throws Exception {

		byte[] btRet = null;
		int btLen = bt.length;
		int idx = 0;
		for (int i = 0; i < btLen; i++) {
			if (bt[i] == 0x00) {
				idx = i;
				break;
			}
		}

		if (idx > 0) {
			btRet = new byte[idx];
			System.arraycopy(bt, 0, btRet, 0, idx);
		} else {
			btRet = bt;
		}

		String sRet = "";
		int btRetlen = btRet.length;
		for (int i = 0; i < btRetlen; i++) {
			Character d = new Character((char) btRet[i]);
			sRet = sRet.concat(d.toString());
		}

		// String sRet = "";
		// int len = bt.length;
		// for(int i = 0;i < len;i++){
		// Character d = new Character((char)bt[i]);
		// sRet = sRet.concat(d.toString());
		// }

		return sRet;
	}

	/**
	 * 方法简述：转换格式01
	 * 
	 * @param str
	 *            String 十六进制字符(6字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat01(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			String s_yy = temp_str.substring(0, 2);
			String s_mm = temp_str.substring(2, 4);
			String s_dd = temp_str.substring(4, 6);
			String s_hh = temp_str.substring(6, 8);
			String s_ff = temp_str.substring(8, 10);
			String s_ss = temp_str.substring(10, 12);

			int i_mm = Integer.parseInt(s_mm.substring(0, 1), 16);
			String s_xq = Integer.toBinaryString(i_mm);
			int len_xq = s_xq.length();
			for (int i = 0; i < 4 - len_xq; i++) {
				s_xq = "0" + s_xq;
			}
			s_mm = s_xq.substring(3, 4) + s_mm.substring(1, 2);
			// s_xq = "0" + s_xq;
			s_xq = s_xq.substring(0, 3);
			int i_xq = Integer.parseInt(s_xq, 2);

			sRet = s_yy + "年" + s_mm + "月" + s_dd + "日" + s_hh + "点" + s_ff
					+ "分" + s_ss + "秒(星期" + i_xq + ")";

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}
	
	
	/**
	 * 方法简述：转换格式01
	 * 
	 * @param str
	 *            String 十六进制字符(6字节)
	 * @return sRet String 返回String型  yy-MM-dd HH:mm:ss
	 */
	public static String tranFormat01_1(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			String s_yy = temp_str.substring(0, 2);
			String s_mm = temp_str.substring(2, 4);
			String s_dd = temp_str.substring(4, 6);
			String s_hh = temp_str.substring(6, 8);
			String s_ff = temp_str.substring(8, 10);
			String s_ss = temp_str.substring(10, 12);

			int i_mm = Integer.parseInt(s_mm.substring(0, 1), 16);
			String s_xq = Integer.toBinaryString(i_mm);
			int len_xq = s_xq.length();
			for (int i = 0; i < 4 - len_xq; i++) {
				s_xq = "0" + s_xq;
			}
			s_mm = s_xq.substring(3, 4) + s_mm.substring(1, 2);
			// s_xq = "0" + s_xq;
			s_xq = s_xq.substring(0, 3);

			sRet = s_yy + "-" + s_mm + "-" + s_dd + " " + s_hh + ":" + s_ff
					+ ":" + s_ss;
			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式01
	 * 
	 * @param str
	 *            String 十六进制字符(6字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat01_pure(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			String s_yy = temp_str.substring(0, 2);
			String s_mm = temp_str.substring(2, 4);
			String s_dd = temp_str.substring(4, 6);
			String s_hh = temp_str.substring(6, 8);
			String s_ff = temp_str.substring(8, 10);
			String s_ss = temp_str.substring(10, 12);

			int i_mm = Integer.parseInt(s_mm.substring(0, 1), 16);
			String s_xq = Integer.toBinaryString(i_mm);
			int len_xq = s_xq.length();
			for (int i = 0; i < 4 - len_xq; i++) {
				s_xq = "0" + s_xq;
			}
			s_mm = s_xq.substring(3, 4) + s_mm.substring(1, 2);
			// s_xq = "0" + s_xq;
			s_xq = s_xq.substring(0, 3);
			int i_xq = Integer.parseInt(s_xq, 2);

			sRet = s_yy + s_mm + s_dd + s_hh + s_ff + s_ss;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式02
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return dRet double 返回double型
	 */
	public static String tranFormat02(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8位
			temps = temps.substring(4, 8);// 后4位

			// 值
			String sValue = temp_str.substring(1, 4);
			double dValue = Double.parseDouble(sValue);

			// 乘方
			String cf = temps.substring(0, 3);
			if (cf.equals("000")) {
				// 10的4次方
				dValue = dValue * 10000;

			} else if (cf.equals("001")) {
				// 10的3次方
				dValue = dValue * 1000;

			} else if (cf.equals("010")) {
				// 10的2次方
				dValue = dValue * 100;

			} else if (cf.equals("011")) {
				// 10的1次方
				dValue = dValue * 10;

			} else if (cf.equals("100")) {
				// 10的0次方
				dValue = dValue * 1;

			} else if (cf.equals("101")) {
				// 10的-1次方
				dValue = dValue * 0.1;

			} else if (cf.equals("110")) {
				// 10的-2次方
				dValue = dValue * 0.01;

			} else if (cf.equals("111")) {
				// 10的-3次方
				dValue = dValue * 0.001;
			}

			// 功率正负(暂时忽略)
			String glzf = temps.substring(3, 4);
			if (glzf.equals("1")) {
				// 负值
				dValue = dValue * (-1);
			}
			return DoubleToString(dValue, 3);
		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}

	}

	/**
	 * 方法简述：转换格式02
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String[] sRet[0]:值;sRet[1]:符号;sRet[2]:系数
	 */
	public static String[] tranFormat02_pure(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8位
			temps = temps.substring(4, 8);// 后4位

			// 值
			String sValue = temp_str.substring(1, 4);
			Integer dValue = Integer.parseInt(sValue);

			// 系数
			String xs = temps.substring(0, 3);

			// 符号
			String fh = temps.substring(3, 4);

			String[] sRet = new String[3];
			sRet[0] = String.valueOf(dValue);
			sRet[1] = fh;
			sRet[2] = xs;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效", "无效" };
		}

	}

	/**
	 * 方法简述：转换格式03
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String[] sRet[0]:值；sRet[1]:单位
	 */
	public static String[] tranFormat03(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8位
			temps = temps.substring(4, 8);// 后4位

			// 值
			String sValue = temp_str.substring(1, 8);
			// double dValue = Double.parseDouble(sValue);
			int iValue = Integer.parseInt(sValue);

			// 单位
			String dw = temps.substring(1, 2);
			if (dw.equals("0")) {
				dw = "kWh(厘)";
			} else if (dw.equals("1")) {
				dw = "MWh(元)";
			}

			// 正负(暂时忽略)
			String zf = temps.substring(3, 4);
			if (zf.equals("1")) {
				// 负值
				iValue = iValue * (-1);
			}

			String[] sRet = new String[2];
			sRet[0] = String.valueOf(iValue);
			sRet[1] = dw;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效" };
		}
	}

	/**
	 * 方法简述：转换格式03
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String[] sRet[0]:值；sRet[1]:单位；sRet[2]:符号
	 */
	public static String[] tranFormat03_pure(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8位
			temps = temps.substring(4, 8);// 后4位

			// 值
			String sValue = temp_str.substring(1, 8);
			// double dValue = Double.parseDouble(sValue);
			int iValue = Integer.parseInt(sValue);

			// 单位
			String dw = temps.substring(1, 2);

			// 符号
			String fh = temps.substring(3, 4);

			String[] sRet = new String[3];
			sRet[0] = String.valueOf(iValue);
			sRet[1] = dw;
			sRet[2] = fh;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效", "无效" };
		}
	}

	/**
	 * 方法简述：转换格式03
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String[] sRet[0]:值；sRet[1]:单位
	 */
	public static String[] tranFormat03_pure1(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8位
			temps = temps.substring(4, 8);// 后4位

			// 值
			String sValue = temp_str.substring(1, 8);
			// double dValue = Double.parseDouble(sValue);
			int iValue = Integer.parseInt(sValue);

			// 单位
			String dw = temps.substring(1, 2);

			// 符号
			String fh = temps.substring(3, 4);
			if (fh.equals("1")) {
				// 负值
				iValue = iValue * (-1);
			}

			String[] sRet = new String[2];
			sRet[0] = String.valueOf(iValue);
			sRet[1] = dw;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效", "无效" };
		}
	}

	/**
	 * 方法简述：转换格式04
	 * 
	 * @param str
	 *            String 十六进制字符(1字节)
	 * @return sRet String[] sRet[0]:值；sRet[1]:浮动标志
	 */
	public static String[] tranFormat04(String str) throws Exception {

		try {
			String temp_str = Util.hexStrToBinStr(str, 1);
			String sValue = Util.binStrToHexStr(temp_str.substring(1), 1);

			String fdbz = "";
			if (temp_str.substring(0, 1).equals("0")) {
				fdbz = "上浮";
			} else if (temp_str.substring(0, 1).equals("1")) {
				fdbz = "下浮";
			}

			String[] sRet = new String[2];
			sRet[0] = sValue;
			sRet[1] = fdbz;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效" };
		}
	}

	/**
	 * 方法简述：转换格式05
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat05(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);

			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr("0" + temp_str.substring(0, 1), 1);
			String zf = sw.substring(4, 5);// 正负
			sw = sw.substring(5, 8);// 二进制
			i_sw = Integer.parseInt(sw, 2);// 十进制

			sRet = String.valueOf(i_sw) + temp_str.substring(1, 3) + "."
					+ temp_str.substring(3, 4);

			// 去掉头尾的零
			double d = Double.parseDouble(sRet);

			if (zf.equals("1")) {
				d = d * (-1);
			}

			sRet = DoubleToString(d, 1);
			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式05
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * 
	 * @return sRet String[] sRet[0]:值;sRet[1]:符号
	 */
	public static String[] tranFormat05_pure(String str) throws Exception {

		try {
			String[] sRet = new String[2];

			String temp_str = Util.convertStr(str);

			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			String fh = sw.substring(4, 5);// 正负
			sw = sw.substring(5, 8);// 二进制
			i_sw = Integer.parseInt(sw, 2);// 十进制

			String temps = String.valueOf(i_sw) + temp_str.substring(1, 3)
					+ "." + temp_str.substring(3, 4);

			// 去掉头尾的零
			double d = Double.parseDouble(temps);

			sRet[0] = DoubleToString(d, 1);

			sRet[1] = fh;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效" };
		}
	}

	/**
	 * 方法简述：转换格式06
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat06(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr("0" + temp_str.substring(0, 1), 1);
			// 正负
			String zf = sw.substring(4, 5);

			sw = sw.substring(5, 8);// 二进制
			i_sw = Integer.parseInt(sw, 2);// 十进制

			sRet = String.valueOf(i_sw) + temp_str.substring(1, 2) + "."
					+ temp_str.substring(2, 4);

			// 去掉头尾的零
			double d = Double.parseDouble(sRet);
			if (zf.equals("1")) {
				d = d * (-1);
			}
			sRet = DoubleToString(d, 2);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式06
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * 
	 * @return sRet String[] sRet[0]:值;sRet[1]:符号
	 */
	public static String[] tranFormat06_pure(String str) throws Exception {

		try {
			String[] sRet = new String[2];
			String temp_str = Util.convertStr(str);
			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			// 正负
			String zf = sw.substring(4, 5);

			sw = sw.substring(5, 8);// 二进制
			i_sw = Integer.parseInt(sw, 2);// 十进制

			String temps = String.valueOf(i_sw) + temp_str.substring(1, 2)
					+ "." + temp_str.substring(2, 4);

			sRet[0] = DoubleToString(Double.parseDouble(temps), 2);

			sRet[1] = zf;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效" };
		}
	}

	/**
	 * 方法简述：转换格式07
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat07(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = temp_str.substring(0, 3) + "." + temp_str.substring(3, 4);

			sRet = DoubleToString(Double.parseDouble(sRet), 1);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式08
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat08(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = String.valueOf(Integer.parseInt(temp_str));

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式09
	 * 
	 * @param str
	 *            String 十六进制字符(3字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat09(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);

			// 正负
			String zf = sw.substring(4, 5);

			sw = sw.substring(5, 8);// 二进制
			i_sw = Integer.parseInt(sw, 2);// 十进制

			sRet = String.valueOf(i_sw) + temp_str.substring(1, 2) + "."
					+ temp_str.substring(2, 4) + temp_str.substring(4, 6);

			// 去掉头尾的零
			double d = Double.parseDouble(sRet);
			if (zf.equals("1")) {
				d = d * (-1);
			}
			sRet = DoubleToString(d, 4);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式10
	 * 
	 * @param str
	 *            String 十六进制字符(3字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat10(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = String.valueOf(Integer.parseInt(temp_str));

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式11
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat11(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 6) + "." + temp_str.substring(6, 8);

			// 去掉头尾的零
			sRet = DoubleToString(Double.parseDouble(sRet), 2);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式12
	 * 
	 * @param str
	 *            String 十六进制字符(6字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat12(String str) throws Exception {

		try {
			String sRet = "";

			sRet = String.valueOf(Long.valueOf(Util.convertStr(str)));

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式13
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat13(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = temp_str.substring(0, 4) + "." + temp_str.substring(4, 8);

			// 去掉头尾的零
			sRet = DoubleToString(Double.parseDouble(sRet), 4);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式14
	 * 
	 * @param str
	 *            String 十六进制字符(5字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat14(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 6) + "." + temp_str.substring(6, 10);

			// 去掉头尾的零
			sRet = DoubleToString(Double.parseDouble(sRet), 4);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式15
	 * 
	 * @param str
	 *            String 十六进制字符(5字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat15(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEEEEEE")) {
				return "无效";
			}
			sRet = temp_str.substring(0, 2) + "年" + temp_str.substring(2, 4)
					+ "月" + temp_str.substring(4, 6) + "日"
					+ temp_str.substring(6, 8) + "时"
					+ temp_str.substring(8, 10) + "分";

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式17
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat17(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEEEE")) {
				return "无效";
			}
			sRet = temp_str.substring(0, 2) + "月" + temp_str.substring(2, 4)
					+ "日" + temp_str.substring(4, 6) + "时"
					+ temp_str.substring(6, 8) + "分";

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式18
	 * 
	 * @param str
	 *            String 十六进制字符(3字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat18(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEE")) {
				return "无效";
			}
			sRet = temp_str.substring(0, 2) + "日" + temp_str.substring(2, 4)
					+ "时" + temp_str.substring(4, 6) + "分";

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式20
	 * 
	 * @param str
	 *            String 十六进制字符(3字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat20(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEE")) {
				return "无效";
			}
			sRet = temp_str.substring(0, 2) + "年" + temp_str.substring(2, 4)
					+ "月" + temp_str.substring(4, 6) + "日";

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式21
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat21(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEE")) {
				return "无效";
			}
			sRet = temp_str.substring(0, 2) + "年" + temp_str.substring(2, 4)
					+ "月";

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式22
	 * 
	 * @param str
	 *            String 十六进制字符(1字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat22(String str) throws Exception {

		try {
			String sRet = "";

			sRet = Integer.parseInt(str.substring(0, 1)) + "." + Integer.parseInt(str.substring(1, 2));

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式23
	 * 
	 * @param str
	 *            String 十六进制字符(3字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat23(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 2) + "." + temp_str.substring(2, 6);

			sRet = DoubleToString(Double.parseDouble(sRet), 4);
			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式25(+/-799.999)
	 * 
	 * @param str
	 *            String 十六进制字符(3字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat25(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			// 百位
			String bw = "";
			bw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			// 正负
			String zf = bw.substring(4, 5);

			bw = bw.substring(5, 8);// 二进制
			bw = Util.binStrToDecStr(bw);

			sRet = bw + temp_str.substring(1, 2) + temp_str.substring(2, 3)
					+ "." + temp_str.substring(3, 4) + temp_str.substring(4, 6);

			double d = Double.parseDouble(sRet);
			if (zf.equals("1")) {
				d = d * (-1);
			}
			sRet = DoubleToString(d, 3);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式25(+/-799.999)
	 * 
	 * @param str
	 *            String 十六进制字符(3字节)
	 * 
	 * @return sRet String[] sRet[0]:值;sRet[1]:符号
	 */
	public static String[] tranFormat25_pure(String str) throws Exception {

		try {
			String[] sRet = new String[2];
			String temp_str = Util.convertStr(str);
			// 百位
			String bw = "";
			bw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			// 正负
			String zf = bw.substring(4, 5);

			bw = bw.substring(5, 8);// 二进制
			bw = Util.binStrToDecStr(bw);

			String temps = bw + temp_str.substring(1, 2)
					+ temp_str.substring(2, 3) + "." + temp_str.substring(3, 4)
					+ temp_str.substring(4, 6);

			sRet[0] = DoubleToString(Double.parseDouble(temps), 3);

			sRet[1] = zf;

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return new String[] { "无效", "无效" };
		}
	}
	
	/**
	 * 方法简述：转换格式26
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat26(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 1) + "." + temp_str.substring(1, 4);

			sRet = DoubleToString(Double.parseDouble(sRet), 3);
			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}

	/**
	 * 方法简述：转换格式27
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat27(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = String.valueOf(Long.parseLong(temp_str));

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}
	
	/**
	 * 方法简述：转换格式28
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String[] sRet[0]:值；sRet[1]:浮动标志
	 */
	public static String[] tranFormat28(String str) throws Exception {

		  try {
			  String[] sRet = new String[2];

				String temp_str = Util.convertStr(str);
				String sw = "";
				int i_sw = 0;
				sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
				String fh = sw.substring(4, 5);// 正负
				sw = sw.substring(5, 8);// 二进制
				i_sw = Integer.parseInt(sw, 2);// 十进制

				String temps = String.valueOf(i_sw) + temp_str.substring(1, 4);
				
				sRet[0] = String.valueOf(Integer.parseInt(temps));
				sRet[1] = fh;

				return sRet;

			} catch (Exception e) {
				cat.error("数据格式错误:", e);
				return new String[] { "无效", "无效" };
			}
	}
	
	/**
	 * 方法简述：转换格式21
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat29(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEE")) {
				return "无效";
			}
			sRet = temp_str.substring(0, 2) + "-" + temp_str.substring(2, 4);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}
	
	/**
	 * 方法简述：转换格式30
	 * 
	 * @param str
	 *            String 十六进制字符(2字节)
	 * @return sRet String 返回String型
	 */
	public static String tranFormat30(String str) throws Exception {
        try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 2) + "." + temp_str.substring(2, 4);

			// 去掉头尾的零
			sRet = DoubleToString(Double.parseDouble(sRet), 2);

			return sRet;

		} catch (Exception e) {
			cat.error("数据格式错误:", e);
			return "无效";
		}
	}
	
	/**
	 * 方法简述：04F15、F16里面的温度
	 * 
	 * @param str
	 *            String 十六进制字符(4字节)
	 * @return sRet String[] sRet[0]:值；sRet[1]:浮动标志 1为负数，0为正数
	 */
	public static String[] tranFormatTemperture(String str) throws Exception {

		  try {
			  String[] sRet = new String[2];

				String sw = "";
				int i_sw = 0;
				sw = Util.hexStrToBinStr(str, 1);
				String fh = sw.substring(0, 1);// 正负
				sw = "0"+sw.substring(1, 8);// 二进制
				i_sw = Integer.parseInt(Util.binStrToDecStr(sw));

				sRet[0] = String.valueOf(i_sw);
				sRet[1] = fh;

				return sRet;

			} catch (Exception e) {
				cat.error("数据格式错误:", e);
				return new String[] { "无效", "无效" };
			}
	}
	

	/**
	 * 方法简述：组装格式01
	 * 
	 * @param rq
	 *            String 日期(yymmddhhmmss)
	 * @param xq
	 *            String 星期
	 * @return sRet String 返回String型
	 */
	public static String makeFormat01(String rq, String xq) throws Exception {

		try {
			String sRet = "";
			String month = rq.substring(2, 3);// 月份的十位
			String week = Util.decStrToBinStr(xq, 1);
			week = week.substring(5, 8);

			String week_month = week + month;// 星期+月份的十位
			week_month = Util.binStrToHexStr(week_month, 1);
			week_month = week_month.substring(1, 2);

			sRet = rq.substring(0, 2) + week_month + rq.substring(3, 12);
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式01
	 * 
	 * @param rq
	 *            String 日期(yymmddhhmmss)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat01(String rq) throws Exception {

		try {
			String sRet = "";
			// 根据日期得到星期
			String xq = Util.getWeek(rq);

			String month = rq.substring(2, 3);// 月份的十位
			String week = Util.decStrToBinStr(xq, 1);
			week = week.substring(5, 8);

			String week_month = week + month;// 星期+月份的十位
			week_month = Util.binStrToHexStr(week_month, 1);
			week_month = week_month.substring(1, 2);

			sRet = rq.substring(0, 2) + week_month + rq.substring(3, 12);
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式02
	 * 
	 * @param value
	 *            String 值（>=1,<=999）
	 * @param xs
	 *            Sting 系数（遵照规约,如：000=10E4...）
	 * @param zf
	 *            String 正负：0：正；1：负
	 * @return sRet String 返回String型
	 */
	public static String makeFormat02(String value, String xs, String zf)
			throws Exception {

		try {
			String sRet = "";
			String s_value = value;
			int len = s_value.length();
			for (int i = 0; i < 3 - len; i++) {
				s_value = "0" + s_value;
			}

			// 百位
			String bw = s_value.substring(0, 1);
			bw = Util.decStrToBinStr(bw, 1);
			bw = bw.substring(4, 8);

			// 十位
			String sw = s_value.substring(1, 2);
			sw = Util.decStrToBinStr(sw, 1);
			sw = sw.substring(4, 8);

			// 个位
			String gw = s_value.substring(2, 3);
			gw = Util.decStrToBinStr(gw, 1);
			gw = gw.substring(4, 8);

			sRet = sw + gw + xs + zf + bw;
			sRet = Util.binStrToHexStr(sRet, 2);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式03
	 * 
	 * @param value
	 *            String 值（>=0,<=99999999）
	 * @param dw
	 *            String 单位（0:kWh/厘;1:MWh/元）
	 * @param fh
	 *            String 符号：0：正；1：负
	 * 
	 * @return sRet String 返回String型
	 */
	public static String makeFormat03(String value, String dw, String fh)
			throws Exception {

		try {
			String sRet = "";
			String s_value = value;
			int len = s_value.length();
			for (int i = 0; i < 7 - len; i++) {
				s_value = "0" + s_value;
			}

			String high = "0" + dw + "0" + fh;
			high = Util.binStrToHexStr(high, 1);
			high = high.substring(1);

			sRet = Util.convertStr(high + s_value);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式04
	 * 
	 * @param s0
	 *            int 标志（0：上浮；1：下浮）
	 * @param value
	 *            int 值(>=0,<=99)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat04(int value) throws Exception {

		try {
			String sRet = "";

			String sValue = String.valueOf(value);
			if (sValue.length() < 2) {
				sValue = "0" + sValue;
			}

			String high = sValue.substring(0, 1);
//			high = Util.decStrToBinStr(high, 1);
//			high = high.substring(5, 8);
//			high = s0 + high;
//			high = Util.binStrToHexStr(high, 1);
//			high = high.substring(1, 2);

			sRet = high + sValue.substring(1, 2);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式05
	 * 
	 * @param fh
	 *            String 符号（0：正；1：负）
	 * @param value
	 *            String 值(>=0.0,<=799.9)
	 * 
	 * @return sRet String 返回String型
	 */
	public static String makeFormat05(String fh, String value) throws Exception {

		try {
			String sRet = "";

			String temps = value;
			if (temps.indexOf(".") != -1) {
				temps = "000" + temps + "0";
				int index = temps.indexOf(".");
				sRet = temps.substring(index - 3, index)
						+ temps.substring(index + 1, index + 2);

			} else {
				temps = "000" + temps;
				sRet = temps.substring(temps.length() - 3, temps.length())
						+ "0";
			}
			temps = sRet.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);
			temps = fh + temps.substring(5);
			temps = Util.binStrToHexStr(temps, 1);

			sRet = temps.substring(1) + sRet.substring(1);
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 方法简述：组装格式08
	 * 
	 * @param value
	 *            int 值(>=0,<=9999)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat08(int value) throws Exception {

		try {
			String sRet = "";

			String sValue = String.valueOf(value);
			int len = sValue.length();
			for (int i = 0; i < 4 - len; i++) {
				sValue = "0" + sValue;
			}
            sRet = Util.convertStr(sValue);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式25
	 * 
	 * @param fh
	 *            String 符号（0：正；1：负）
	 * @param value
	 *            String 值(>=0.0,<=799.999)
	 * 
	 * @return sRet String 返回String型
	 */
	public static String makeFormat25(String fh, String value) throws Exception {

		try {
			String sRet = "";

			String temps = value;
			if (temps.indexOf(".") != -1) {
				temps = "000" + temps + "000";
				int index = temps.indexOf(".");
				sRet = temps.substring(index - 3, index)
						+ temps.substring(index + 1, index + 4);

			} else {
				temps = "000" + temps;
				sRet = temps.substring(temps.length() - 3, temps.length())
						+ "000";
			}
			temps = Util.hexStrToBinStr(sRet.substring(0, 1), 1);
			temps = fh + temps.substring(5);
			temps = Util.binStrToHexStr(temps, 1);

			sRet = temps.substring(1) + sRet.substring(1);
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式06
	 * 
	 * @param fh
	 *            String 符号（0：正；1：负）
	 * @param value
	 *            String 值(>=0.0,<=79.99)
	 * 
	 * @return sRet String 返回String型
	 */
	public static String makeFormat06(String fh, String value) throws Exception {

		try {
			String sRet = "";

			String temps = value;
			if (temps.indexOf(".") != -1) {
				temps = "00" + temps + "00";
				int index = temps.indexOf(".");
				sRet = temps.substring(index - 2, index)
						+ temps.substring(index + 1, index + 3);

			} else {
				temps = "00" + temps;
				sRet = temps.substring(temps.length() - 2, temps.length())
						+ "00";
			}
			temps = Util.hexStrToBinStr(sRet.substring(0, 1), 1);
			temps = fh + temps.substring(5);
			temps = Util.binStrToHexStr(temps, 1);

			sRet = temps.substring(1) + sRet.substring(1);
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式07
	 * 
	 * @param value
	 *            String 值(>=0.0,<=999.9)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat07(String value) throws Exception {

		try {
			String sRet = value;
			if (sRet.indexOf(".") == -1) {
				sRet = sRet + ".0";
			}
			sRet = sRet.replace(".", "");
			sRet = Util.addODD(sRet, 4, "0");
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式22
	 * 
	 * @param value
	 *            String 值(>=0.0,<=9.9)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat22(String value) throws Exception {
		if("EE".equalsIgnoreCase(value)){
			return "EE";
		}

		try {
			String sRet = value;
			if (sRet.indexOf(".") == -1) {
				sRet = sRet + ".0";
			}
			sRet = sRet.replace(".", "");
			sRet = Util.addODD(sRet, 2, "0");

			return sRet;
		} catch (Exception e) {
			
			throw e;
		}
	}

	/**
	 * 方法简述：组装格式23
	 * 
	 * @param value
	 *            String 值(>=0.0,<=99.9999)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat23(String value) throws Exception {

		try {
			String sRet = "";

			String temps = value;
			if (temps.indexOf(".") != -1) {
				temps = "00" + temps + "0000";
				int index = temps.indexOf(".");
				sRet = temps.substring(index - 2, index)
						+ temps.substring(index + 1, index + 5);

			} else {
				temps = "00" + temps;
				sRet = temps.substring(temps.length() - 2, temps.length())
						+ "0000";
			}
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 方法简述：组装格式28
	 * 
	 * @param s0
	 *            int 标志（0：上浮；1：下浮）
	 * @param value
	 *            int 值(>=0,<=9999)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat28(int s0, int value) throws Exception {

		try {
			String sRet = "";

			String sValue = String.valueOf(value);
			int len = sValue.length();
			for (int i = 0; i < 4 - len; i++) {
				sValue = "0" + sValue;
			}

			String high = sValue.substring(0,2);
			high = Util.hexStrToBinStr(high, 1);
			high = s0+high.substring(1);
			high=Util.binStrToHexStr(high, 1);

			sRet = Util.convertStr(high + sValue.substring(2));

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 方法简述：组装格式29
	 * 
	 * @param rq
	 *            String 日期 3月15日:0315
	 * @param value
	 *            int 值(>=0,<=9999)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat29(String rq ) throws Exception {

		try {
			String sRet = "";
			sRet=Util.convertStr(rq);
			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 方法简述：组装格式30
	 * 
	 * @param value
	 *            String 值(>=0.0,<=99.99)
	 * @return sRet String 返回String型
	 */
	public static String makeFormat30(String value) throws Exception {

		try {
			String sRet = "";

			String temps = value;
			if (temps.indexOf(".") != -1) {
				temps = "00" + temps + "00";
				int index = temps.indexOf(".");
				sRet = temps.substring(index - 2, index)
						+ temps.substring(index + 1, index + 3);

			} else {
				temps = "00" + temps;
				sRet = temps.substring(temps.length() - 2, temps.length())
						+ "00";
			}
			sRet = Util.convertStr(sRet);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}
	/**
	 * 方法简述：组装04F15、F16的温度的格式
	 * 
	 * @param s0
	 *            int 标志（0：上浮；1：下浮）
	 * @param value
	 *            int 值(>=0,<=9999)
	 * @return sRet String 返回String型
	 */
	public static String makeFormatTemperture(int s0, int value) throws Exception {

		try {
			String sRet = "";

			sRet=Util.decStrToBinStr(value, 1);
			sRet=s0+sRet.substring(1,8);
			sRet = Util.binStrToHexStr(sRet, 1);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：事件时间转换
	 * 
	 * @param value
	 *            String mmhhddmmyy
	 * @return sRet String 返回String型
	 */
	public static String getSJSJ(String value) throws Exception {

		try {
			String sRet = "";

			sRet = value.substring(0, 2) + "-" + value.substring(2, 4) + "-"
					+ value.substring(4, 6) + " " + value.substring(6, 8) + ":"
					+ value.substring(8, 10);

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 方法简述：事件标志转换
	 * 
	 * @param value
	 *            String BS64
	 * @return sRet String 返回String型
	 */
	public static String getSJBZ(String value) throws Exception {

		try {
			String sRet = "";
			String tempV = Util.convertStr(value);
			for (int i = 0; i < 8; i++) {
				if (i > 3) {
					break;
				}
				String temps = tempV.substring((7 - i) * 2, (8 - i) * 2);
				temps = Util.hexStrToBinStr(temps, 1);
				for (int j = 0; j < 8; j++) {
					int idx = i * 8 + (j + 1);
					String bz = temps.substring(7 - j, 8 - j);

					switch (idx) {
					case 1:
						if (bz.equals("1")) {
							sRet += "[ERC1]数据初始化和版本变更记录:有事件<br>";
						} else {
							sRet += "[ERC1]数据初始化和版本变更记录:无事件<br>";
						}
						break;
					case 2:
						if (bz.equals("1")) {
							sRet += "[ERC2]参数丢失记录:有事件<br>";
						} else {
							sRet += "[ERC2]参数丢失记录:无事件<br>";
						}
						break;
					case 3:
						if (bz.equals("1")) {
							sRet += "[ERC3]参数变更记录:有事件<br>";
						} else {
							sRet += "[ERC3]参数变更记录:无事件<br>";
						}
						break;
					case 4:
						if (bz.equals("1")) {
							sRet += "[ERC4]状态量变位记录:有事件<br>";
						} else {
							sRet += "[ERC4]状态量变位记录:无事件<br>";
						}
						break;
					case 5:
						if (bz.equals("1")) {
							sRet += "[ERC5]遥控跳闸记录:有事件<br>";
						} else {
							sRet += "[ERC5]遥控跳闸记录:无事件<br>";
						}
						break;
					case 6:
						if (bz.equals("1")) {
							sRet += "[ERC6]功控跳闸记录:有事件<br>";
						} else {
							sRet += "[ERC6]功控跳闸记录:无事件<br>";
						}
						break;
					case 7:
						if (bz.equals("1")) {
							sRet += "[ERC7]电控跳闸记录:有事件<br>";
						} else {
							sRet += "[ERC7]电控跳闸记录:无事件<br>";
						}
						break;
					case 8:
						if (bz.equals("1")) {
							sRet += "[ERC8]电能表参数变更:有事件<br>";
						} else {
							sRet += "[ERC8]电能表参数变更:无事件<br>";
						}
						break;
					case 9:
						if (bz.equals("1")) {
							sRet += "[ERC9]电流回路异常:有事件<br>";
						} else {
							sRet += "[ERC9]电流回路异常:无事件<br>";
						}
						break;
					case 10:
						if (bz.equals("1")) {
							sRet += "[ERC10]电压回路异常:有事件<br>";
						} else {
							sRet += "[ERC10]电压回路异常:无事件<br>";
						}
						break;
					case 11:
						if (bz.equals("1")) {
							sRet += "[ERC11]相序异常:有事件<br>";
						} else {
							sRet += "[ERC11]相序异常:无事件<br>";
						}
						break;
					case 12:
						if (bz.equals("1")) {
							sRet += "[ERC12]电能表时间超差:有事件<br>";
						} else {
							sRet += "[ERC12]电能表时间超差:无事件<br>";
						}
						break;
					case 13:
						if (bz.equals("1")) {
							sRet += "[ERC13]电表故障信息:有事件<br>";
						} else {
							sRet += "[ERC13]电表故障信息:无事件<br>";
						}
						break;
					case 14:
						if (bz.equals("1")) {
							sRet += "[ERC14]终端停/上电事件:有事件<br>";
						} else {
							sRet += "[ERC14]终端停/上电事件:无事件<br>";
						}
						break;
					case 15:
						if (bz.equals("1")) {
							sRet += "[ERC15]谐波越限告警:有事件<br>";
						} else {
							sRet += "[ERC15]谐波越限告警:无事件<br>";
						}
						break;
					case 16:
						if (bz.equals("1")) {
							sRet += "[ERC16]直流模拟量越限记录:有事件<br>";
						} else {
							sRet += "[ERC16]直流模拟量越限记录:无事件<br>";
						}
						break;
					case 17:
						if (bz.equals("1")) {
							sRet += "[ERC17]电压/电流不平衡越限:有事件<br>";
						} else {
							sRet += "[ERC17]电压/电流不平衡越限:无事件<br>";
						}
						break;
					case 18:
						if (bz.equals("1")) {
							sRet += "[ERC18]电容器投切自锁记录:有事件<br>";
						} else {
							sRet += "[ERC18]电容器投切自锁记录:无事件<br>";
						}
						break;
					case 19:
						if (bz.equals("1")) {
							sRet += "[ERC19]购电参数设置记录:有事件<br>";
						} else {
							sRet += "[ERC19]购电参数设置记录:无事件<br>";
						}
						break;
					case 20:
						if (bz.equals("1")) {
							sRet += "[ERC20]密码错误记录:有事件<br>";
						} else {
							sRet += "[ERC20]密码错误记录:无事件<br>";
						}
						break;
					case 21:
						if (bz.equals("1")) {
							sRet += "[ERC21]终端故障记录:有事件<br>";
						} else {
							sRet += "[ERC21]终端故障记录:无事件<br>";
						}
						break;
					case 22:
						if (bz.equals("1")) {
							sRet += "[ERC22]有功总电能差动越限事件记录:有事件<br>";
						} else {
							sRet += "[ERC22]有功总电能差动越限事件记录:无事件<br>";
						}
						break;
					case 24:
						if (bz.equals("1")) {
							sRet += "[ERC24]电压越限记录:有事件<br>";
						} else {
							sRet += "[ERC24]电压越限记录:无事件<br>";
						}
						break;
					case 25:
						if (bz.equals("1")) {
							sRet += "[ERC25]电流越限记录:有事件<br>";
						} else {
							sRet += "[ERC25]电流越限记录:无事件<br>";
						}
						break;
					case 26:
						if (bz.equals("1")) {
							sRet += "[ERC26]视在功率越限记录:有事件<br>";
						} else {
							sRet += "[ERC26]视在功率越限记录:无事件<br>";
						}
						break;
					case 27:
						if (bz.equals("1")) {
							sRet += "[ERC27]电能表示度下降:有事件<br>";
						} else {
							sRet += "[ERC27]电能表示度下降:无事件<br>";
						}
						break;
					case 28:
						if (bz.equals("1")) {
							sRet += "[ERC28]电能量超差:有事件<br>";
						} else {
							sRet += "[ERC28]电能量超差:无事件<br>";
						}
						break;
					case 29:
						if (bz.equals("1")) {
							sRet += "[ERC29]电能表飞走:有事件<br>";
						} else {
							sRet += "[ERC29]电能表飞走:无事件<br>";
						}
						break;
					case 30:
						if (bz.equals("1")) {
							sRet += "[ERC30]电能表停走:有事件<br>";
						} else {
							sRet += "[ERC30]电能表停走:无事件<br>";
						}
						break;

					default:
						break;
					}
				}

			}

			return sRet;
		} catch (Exception e) {
			throw e;
		}
	}

	public static int getRecordCount(String s_sql, JdbcTemplate jdbcT) {
		s_sql = "select count(*) count from (" + s_sql + ")";
		List lst = jdbcT.queryForList(s_sql);
		String s_count = String.valueOf(((Map) lst.get(0)).get("count"));
		int i_count = Integer.parseInt(s_count);
		return i_count;
	}

	/**
	 * 
	
	* @Title: getZdid 
	
	* @Description: TODO(通过终端地址、行政区县码获取终端编号) 
	
	* @param @param xzqxm行政区县码
	* @param @param zddz终端地址
	* @param @param jdbcT
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @throws
	 */
	public static String getZdid(String xzqxm, String zddz, JdbcTemplate jdbcT) {
		String s_sql = "select zdid from G_ZDGZ where xzqxm=? and zddz=?";
		String[] params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(s_sql, params);
		String zdid = "";
		if (lst.size() == 0) {
			zdid = null;

		} else {
			zdid = String.valueOf(((Map) lst.get(0)).get("zdid"));
		}
		return zdid;
	}
	
	
	
	/**
	 * 
	
	* @Title: getSequences 
	
	* @Description: TODO(取得某个Sequences.Nextval) 
	
	* @param @param sequences
	* @param @param jdbcT
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @throws
	 */
	@SuppressWarnings("rawtypes")
	public static String getSequences(String sequences,JdbcTemplate jdbcT) {
		String s_sql = "select "+sequences+".Nextval from dual";
		List lst = jdbcT.queryForList(s_sql);
		String nextval = "";
		if (lst.size() == 0) {
			nextval = null;

		} else {
			nextval = String.valueOf(((Map) lst.get(0)).get(1));
		}
		return nextval;
	}
	
	/**
	 * 取得用户名称和用户手机号码
	 * @param xzqxm
	 * @param zddz
	 * @param jdbcT
	 * @return
	 */
	public static String[] getHmAndYhsjhm(String xzqxm, String zddz, JdbcTemplate jdbcT){
		String[] bac=new String[4];
		String s_sql = "select hm,yhsjhm,fzr,fzrsjhm from G_ZDGZ where xzqxm=? and zddz=?";
		String[] params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(s_sql, params);
		if (lst.size() == 0) {
			bac = null;

		} else {
			bac[0] = String.valueOf(((Map) lst.get(0)).get("hm"));
			bac[1]=String.valueOf(((Map) lst.get(0)).get("yhsjhm"));
			bac[2]=String.valueOf(((Map) lst.get(0)).get("fzr"));
			bac[3]=String.valueOf(((Map) lst.get(0)).get("fzrsjhm"));
		}
		return bac;
	}

	/**
	 * 通过终端编号查询时候已存在终端当前状态记录
	 * 
	 * @param zdid
	 * @param jdbcT
	 * @return true:存在；false:不存在
	 */
	public static boolean checkZddqztByZdid(String zdid, JdbcTemplate jdbcT) {
		String s_sql = "select zdid from G_ZDDQZTJLB where zdid=?";
		String[] params = new String[] { zdid };
		List lst = jdbcT.queryForList(s_sql, params);
		if (null != lst && lst.size() > 0) {
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static String getZdid(DataObject data, JdbcTemplate jdbcT) throws Exception{
		String xzqxm = data.xzqxm;
		String zddz = data.zddz;
		String txfs = data.txfs;
		String gylx = data.gylx;
		
		//2015-11-19更新，
		int i_zddz=Integer.parseInt(Util.hexStrToDecStr(zddz));
		//2016-07-20更新
		int i_xzqxm=Integer.parseInt(Util.hexStrToDecStr(xzqxm));
		//2015-11-19更新，
//		System.out.println("终端自动登录>>>>>>>>地址为："+i_zddz);
		

		String s_sql = "select zdid from G_ZDGZ where xzqxm=? and zddz=?";
		String[] params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(s_sql, params);
		String zdid = "";
		if (lst.size() == 0) {
			//修改于2012-07-19新增 SEQ_ZDID.NEXTVAL
//			s_sql = "select (max(zdid)+1) zdid from G_ZDGZ";
			s_sql = "select SEQ_ZDID.NEXTVAL as zdid from dual";
			List templst = jdbcT.queryForList(s_sql);
			zdid = String.valueOf(((Map) templst.get(0)).get("zdid"));
			//2015-11-19更新，默认为智慧终端
			String zdlx="1";
			//2015-11-19更新，约定地址5000以后的为智能终端
			if(i_zddz>5000){
				zdlx="2";
			}
			//2016-07-20更新，约定终端型号如果行政区县码大于32767的为1代终端，小余则为2代终端
			String zdxh="2";
			if(i_xzqxm>32767){
				zdxh="1";
			}

//			s_sql = "insert into G_ZDGZ(zdid,xzqxm,zddz,zdmc,txfsdm,gylx) "
//					+ "values(?,?,?,?,?,?)";
			s_sql = "insert into G_ZDGZ(zdid,stationid,xzqxm,zddz,zdlx,zdxh,zdmc,txfsdm,gylx) "
				+ "values(?,0,?,?,?,?,?,?,?)";
			params = new String[] {zdid,xzqxm, zddz,zdlx,zdxh,
					xzqxm + zddz + "[自动入库]", txfs, gylx };
			jdbcT.update(s_sql, params);
            
			//插入g_zdyxcspzb
			s_sql = "insert into g_zdyxcspzb(zdid) values(?)";
			params = new String[] { zdid };
			jdbcT.update(s_sql, params);
			
			    //20130517新增，下装智慧终端默认的测量点配置信息 z而且终端为一代的终端//
				if("1".equalsIgnoreCase(zdlx)){
					//新增0号测量点
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,0,'1','本机','1','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增1号测量点【备用】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,1,'2','备用','','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增2号测量点【水泵1】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,2,'3','水泵1','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增3号测量点【水泵2】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,3,'4','水泵2','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增4号测量点【风机1】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,4,'5','风机1','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增5号测量点【风机2】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,5,'6','风机2','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增6号测量点【回流泵（气提阀）】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,6,'7','回流泵(气提阀)','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "0" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "1" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "2" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "3" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "4" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "5" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "6" };
					jdbcT.update(s_sql, params);
					
					//2016-08-25修改 新增到G_ZDCJDPZB 第一路PH
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,1,'1','PH计1','2',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//新增到G_ZDCJDPZB 第二路PH
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,2,'2','PH计2','2',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//新增到G_ZDCJDPZB 第一路ORP
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,1,'1','ORP1','4',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//新增到G_ZDCJDPZB 第二路ORP
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,2,'2','ORP2','4',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//新增到G_ZDCJDPZB 第三路ORP
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,3,'3','ORP3','4',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					
				}else{
					//新增0号测量点
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,0,'1','本机','1','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增2号测量点【水泵1】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,2,'3','水泵1','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增3号测量点【水泵2】
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,3,'4','水泵2','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "0" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "2" };
					jdbcT.update(s_sql, params);
					//新增到G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "3" };
					jdbcT.update(s_sql, params);
				}
			
//			//20130517新增，默认在G_ZDDQZTJLB新增记录
//			s_sql = "insert into G_ZDDQZTJLB(zdid) values(?)";
//			params = new String[] { zdid};
//			jdbcT.update(s_sql, params);
			
//			s_sql = "insert into G_ZDZJZPZB(id,zdid,ZJZXH) values(SEQ_GZDZJZPZID.NEXTVAL,?,?)";
//			params = new String[] { zdid, "0" };
//			jdbcT.update(s_sql, params);

		} else {
			zdid = String.valueOf(((Map) lst.get(0)).get("zdid"));
		}
		return zdid;
	}

	@SuppressWarnings("rawtypes")
	public static String getZdgylx(String xzqxm, String zddz, JdbcTemplate jdbcT) {
		String s_sql = "select gylx from G_ZDGZ where xzqxm=? and zddz=?";
		String[] params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(s_sql, params);

		return String.valueOf(((Map) lst.get(0)).get("gylx"));
	}

	public static String addSG(String xzqxm, String zddz, String sjz)
			throws Exception {
		String sgSJZ = "";// 省规数据祯
		sgSJZ += "68";// 68
		sgSJZ += xzqxm + Util.convertStr(zddz);// 终端逻辑地址
		sgSJZ += "4200";// 主站地址及祯序列号
		sgSJZ += "68";// 68
		sgSJZ += "8F";// 控制码:8F
		// 数据长度(DATA.length+5)
		String len = Util.decStrToHexStr(sjz.length() / 2 + 5, 2);
		len = Util.convertStr(len);
		sgSJZ += len;// 数据长度
		sgSJZ += "0000000000";// 非标备用标识
		sgSJZ += sjz;// 数据域
		// 校验码
		String cs = Util.getCS(sgSJZ);
		sgSJZ += cs;// 校验码
		sgSJZ += "16";// 16

		return sgSJZ;
	}

	public static String addFront(String sjz, String gylx, String txfs)
			throws Exception {
		String sSJZ = "";
		sSJZ += "FE";
		sSJZ += gylx;// 规约类型:1:浙规;2:国规;3:浙版国规
		sSJZ += txfs;// 0:不发送;1:COM;2:平台;3:GPRS;4:SMS
		sSJZ += "000000000000";// 通道参数(SIM/IP等)
		String len = Util.decStrToHexStr(sjz.length() / 2, 2);
		len = Util.convertStr(len);
		sSJZ += len;// 长度
		sSJZ += sjz;

		return sSJZ;
	}

	/**
	 *执行存储过程(有返回值,输入、输出参数都为 String 型)
	 * 
	 *@param dataSource
	 *            DataSource 数据源
	 *@param sp_name
	 *            String 存储过程名称
	 *@param sp_param
	 *            Vector 入参
	 *@param ret_num
	 *            int 返回的参数个数
	 * 
	 *@return retV Vector 正常:返回Vecor;出错:抛出异常
	 */
	public static Vector executeProcedure(DataSource dataSource,
			String sp_name, Vector sp_param, int ret_num) throws Exception {
		int row_num = 0;
		int i_len = 0;
		String s_sp_str = "";
		String s_parm_str = "";
		Vector retV = new Vector();
		CallableStatement callableSTMT = null;
		i_len = sp_param.size();// 入参个数
		Connection con = null;
		try {
			con = dataSource.getConnection();
			for (int i = 0; i < (i_len + ret_num); i++) {
				s_parm_str += "?,";
			}

			if (s_parm_str.length() > 0) {
				// 去掉最后的','号
				s_parm_str = s_parm_str.substring(0, s_parm_str.length() - 1);
			}

			callableSTMT = (CallableStatement) con.prepareCall("{call "
					+ sp_name + "(" + s_parm_str + ")}");

			callableSTMT.clearParameters();
			// 设置入参
			for (int i = 0; i < i_len; i++) {
				// String temps = String.valueOf(sp_param.get(i));
				// callableSTMT.setString(i+1,temps);
				callableSTMT.setObject(i + 1, sp_param.get(i));
			}

			// 设置出参
			for (int i = 1; i <= ret_num; i++) {
				callableSTMT.registerOutParameter(i + i_len, Types.VARCHAR);
			}
			// 执行
			callableSTMT.execute();
			for (int i = 1; i <= ret_num; i++) {
				retV.addElement(callableSTMT.getString(i + i_len));
			}

		} catch (Exception e) {
			throw e;

		} finally {
			try {
				if (callableSTMT != null) {
					callableSTMT.close();
					callableSTMT = null;
				}

				if (con != null) {
					con.close();
					con = null;
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return retV;
	}

	/**
	 *执行存储过程(有返回值,输入、输出参数都为 String 型)
	 * 
	 *@param jdbcT
	 *            JdbcTemplate 数据源
	 *@param sp_name
	 *            String 存储过程名称
	 *@param sp_param
	 *            Vector 入参
	 *@param ret_num
	 *            int 返回的参数个数
	 * 
	 *@return retV Vector 正常:返回Vecor;出错:抛出异常
	 */
	public static Vector executeProcedure(JdbcTemplate jdbcT,
			final String sp_name, final Vector sp_param, final int ret_num)
			throws Exception {

		Object obj = jdbcT.execute(new ConnectionCallback() {
			public Object doInConnection(Connection con) {

				int i_len = 0;
				String s_parm_str = "";
				Vector retV = new Vector();
				CallableStatement callableSTMT = null;
				i_len = sp_param.size();// 入参个数
				try {
					for (int i = 0; i < (i_len + ret_num); i++) {
						s_parm_str += "?,";
					}

					if (s_parm_str.length() > 0) {
						// 去掉最后的','号
						s_parm_str = s_parm_str.substring(0, s_parm_str
								.length() - 1);
					}

					callableSTMT = (CallableStatement) con.prepareCall("{call "
							+ sp_name + "(" + s_parm_str + ")}");

					callableSTMT.clearParameters();
					// 设置入参
					for (int i = 0; i < i_len; i++) {
						// String temps = String.valueOf(sp_param.get(i));
						// callableSTMT.setString(i+1,temps);
						callableSTMT.setObject(i + 1, sp_param.get(i));
					}

					// 设置出参
					for (int i = 1; i <= ret_num; i++) {
						callableSTMT.registerOutParameter(i + i_len,
								Types.VARCHAR);
					}
					// 执行
					callableSTMT.execute();
					for (int i = 1; i <= ret_num; i++) {
						retV.addElement(callableSTMT.getString(i + i_len));
					}

				} catch (Exception e) {

				} finally {
					try {
						if (callableSTMT != null) {
							callableSTMT.close();
							callableSTMT = null;
						}
					} catch (Exception e) {
					}
				}
				return retV;
			}
		});

		return (Vector) obj;
	}

	/**
	 * 方法简述：执行事物SQL
	 * 
	 * @param conn
	 *            Connection 数据库连接
	 * @param vSql
	 *            Vector 要执行的SQL
	 * @return s String
	 */
	public static void execTransactionSql(Connection conn, Vector vSql)
			throws Exception {
		Statement stmt = null;
		try {
			conn.setAutoCommit(false);
			stmt = conn.createStatement();
			int iSqlcount = vSql.size();
			for (int i = 0; i < iSqlcount; i++) {
				String sSql = String.valueOf(vSql.get(i));
				stmt.executeUpdate(sSql);
			}
			conn.commit();
			conn.setAutoCommit(true);
		} catch (Exception e1) {
			try {
				conn.rollback();
			} catch (Exception e2) {
				throw e2;
			}
			throw e1;
		} finally {
			try {
				stmt.close();
			} catch (Exception e3) {
				throw e3;
			}
		}

	}
	
	/**
	 * 
	
	* @Title: behindChrForStr 
	
	* @Description: TODO(为字符串后面补对应的ch字符) 
	
	* @param @param str
	* @param @param ch
	* @param @param length
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @throws
	 */
	public static String behindChrForStr(String str,String ch,int length){
		int str_l=str.length();
		for(int i=0;i<length-str_l;i++){
			str+=ch;
		}
		return str;
	}
	
	
	/**
	 * 
	
	* @Title: beforeChrForStr 
	
	* @Description: TODO(为字符串前面补对应的ch字符) 
	
	* @param @param str
	* @param @param ch
	* @param @param length
	* @param @return    设定文件 
	
	* @return String    返回类型 
	
	* @throws
	 */
	public static String beforeChrForStr(String str,String ch,int length){
		int str_l=str.length();
		for(int i=0;i<length-str_l;i++){
			str=ch+str;
		}
		return str;
	}
	
	/**
	 * 
	* @Title: bbhToHexStr
	* @Description: TODO(将版本号转化为四个字节的16进制字符串)
	* @param @param bbh版本号（硬件版本号或者软件版本号  例v1.4.3）
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	 */
	public static String bbhToHexStr(String bbh){
    	String bac="";
    	if(null==bbh||!bbh.contains("v")||bbh.length()!=6){
    		return "EEEE";
    	}
    	bac=(bbh.replace("v", "")).replace(".", "");
    	bac="0"+bac;
    	return bac;
    }
	
	/**
	 * 
	* @Title: updateZdsjpzb
	* @Description: TODO(第一次去更新终端升级配置表)
	* @param @param xzqxm
	* @param @param zddz
	* @param @param fileName
	* @param @param jdbcT    设定文件
	* @return void    返回类型
	* @throws
	 */
	public static void updateZdsjpzb(String xzqxm, String zddz, String fileName,JdbcTemplate jdbcT) {
		String s_sql = "delete g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm='" + xzqxm
				+ "' and zddz='" + zddz + "')";
		jdbcT.update(s_sql);
		//总段数
		int zds=0;
		try {
			zds = Decode_0F.fillHM(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//本帧长度
		String dqcd=Decode_0F.buffer;

		s_sql = "insert into g_zdsjpzb(zdid,cxm,zds,dqdh,dqcd,zt,sj) " + 
		"values((select zdid from g_zdgz where xzqxm='" + xzqxm+ "' and zddz='" + zddz + "'),'"+fileName+"',"+zds+",0,"+dqcd+",2,sysdate)";
		jdbcT.update(s_sql);	
		}
	
	/**
	 * 
	* @Title: saveToImgFile
	* @Description: TODO(16进制转图片)
	* @param @param src 16进制数据
	* @param @param output  文件绝对路径
	* @return void    返回类型
	* @throws
	 */
	public static void saveToImgFile(String src, String output)  
    {  
        if (src == null || src.length() == 0)  
        {  
            return;  
        }  
        try  
        {  
            FileOutputStream out = new FileOutputStream(new File(output));  
            byte[] bytes = src.getBytes();  
            for (int i = 0; i < bytes.length; i += 2)  
            {  
                out.write(charToInt(bytes[i]) * 16 + charToInt(bytes[i + 1]));  
            }  
            out.close();  
        }  
        catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }  
    
	/**
	 * 
	* @Title: charToInt
	* @Description: TODO(字节转整形)
	* @param @param ch
	* @param @return    设定文件
	* @return int    返回类型
	* @throws
	 */
    private static int charToInt(byte ch)  
    {  
        int val = 0;  
        if (ch >= 0x30 && ch <= 0x39)  
        {  
            val = ch - 0x30;  
        }  
        else if (ch >= 0x41 && ch <= 0x46)  
        {  
            val = ch - 0x41 + 10;  
        }  
        return val;  
    }  
    
    /**
     * 
    * @Title: txt2String
    * @Description: TODO(TXT转String)
    * @param @param file 文件
    * @param @return    设定文件
    * @return String    返回类型
    * @throws
     */
    public static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(s);
            }
            br.close();    
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }
	
	public static void main(String arg[]) throws Exception{
		System.out.println(new Date().getTime());
	}
}
