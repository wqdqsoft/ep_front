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
 * Description: ������̬������
 * <p>
 * Copyright: Copyright 2015
 * <p>
 * ��дʱ��: 2015-4-2
 * 
 * @author mohui
 * @version 1.0 �޸��ˣ� �޸�ʱ�䣺
 */
public class Util {

	// ���캯��
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
	 * ����������ת������������
	 * <p>
	 * 
	 * @param flag
	 *            String ���� +:��33H; -:��33H
	 * @param sjy
	 *            String ������
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
	 * ����������ȡʡ����վ��ַ(HEX�ַ���)
	 * <p>
	 * 
	 * @param msta
	 *            ��ʾ��վ��ַ
	 * @param iseq��ʾ֡�����
	 * @param fseq��ʾ֡���
	 * 
	 * @return ����ByteBuffer���͵�MSTA&SEQ
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
	 * ����������ȡ֡���
	 * 
	 * @param d
	 *            ��λ�ֽ�
	 * @param g
	 *            ��λ�ֽ�
	 * @return zxh ���ػ�����֡���
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
			zs = Util.addODD(src, zs_len, "0");// zs_len���ܻ�Ϊ����
			for (int i = 0; i < xs_len; i++) {
				xs += "0";
			}
		} else {
			for (int i = 0; i < xs_len - 1; i++) {
				src += "0";
			}
			zs = src.substring(0, src.indexOf("."));
			zs = Util.addODD(zs, zs_len, "0");// zs_len���ܻ�Ϊ����
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
	 *����ת��,8859_1->GB2312 ֻ������������ж�ȡ�����ı���ȡ����
	 * 
	 * @param s_name
	 *            Ҫת�����ַ���
	 *@return ����GB2312��
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
	 * ���ܣ��ַ����滻replace(Ŀ���ַ���,���滻���ַ���,Ӧ�滻���ַ���)<br>�����滻�ɹ����ַ���<br>
	 * ������"123456"��"34"�滻��"89"�����Ϊ"128956"<br> replace("123456","34","89") ==>
	 * "128956"<br> ֻ�滻һ��<br>
	 * 
	 * @param s_source ԭ�����ַ���
	 * 
	 * @param s_old Ҫ���滻���ַ���
	 * 
	 * @param s_new �滻���µ��ַ���
	 * 
	 * @return �滻����ַ���
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
	 * ���������������ֽڴ�ת����������
	 * <p>
	 * ����λ��ǰ��λ�ں�,���ַ�����
	 * <p>
	 * ��offsetλ��ʼ����λ��0������
	 * <p>
	 * 
	 * @param bt
	 *            ��ʾҪת�����ֽڴ�,
	 * @param offset��ʾ��bt�ĵڼ���λ��ʼ
	 *            ,��bt�Ŀ�ʼ�±�
	 * @return st ����ת���õ�������
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
	 * ���������������ֽڴ�ת���ɶ�������
	 * <p>
	 * ����λ��ǰ��λ�ں�,���ַ����ĵ�offsetλ
	 * <p>
	 * 
	 * @param bt
	 *            ��ʾҪת�����ֽڴ�,
	 * @param offset��ʾ��bt�ĵڼ���λ��ʼ
	 *            ,��bt�Ŀ�ʼ�±�
	 * @return st ����ת���õ�������
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
	 * ���������������ֽڴ�ת�����ַ��ͣ����ַ����ĵ�satposλ��ʼ����endposλ����0������
	 * 
	 * @param bt
	 *            Ҫת�����ֽ�����
	 * @param satpos
	 *            �ֽ�����Ŀ�ʼ�±�
	 * @param endpos
	 *            �ֽ�����Ľ����±�
	 * @return ����ת���õ��ַ���
	 */

	public static String byte2str(byte[] bt, int satpos, int endpos) {

		return new String(bt, satpos, endpos - satpos);
	}

	/**
	 * �������� :��ByteBufferת��ΪString
	 * 
	 * @param bb
	 *            Ҫת����ByteBuffer
	 * @return ת���õ�String
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
	 * �������� :��byte[]ת��ΪString
	 * 
	 * @param by
	 *            Ҫת����byte[]
	 * @return ת���õ�String
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
	 * ������������ �ֽڴ�ת����int�����ֽڣ�
	 * 
	 * @param bt
	 *            ��ʾҪת�����ֽ�
	 * @return i ����ת���õ�����
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
	 * �������������ֽڴ�ת����ʮ�����Ƶ��ַ���
	 * <p>
	 * (����˫�ֽ�,������0x68a8��
	 * <p>
	 * ת���Ժ󷵻��ַ���a868)
	 * <p>
	 * 
	 * @param bt
	 *            ��ʾҪת�����ֽڴ�
	 * @return str ����ת���õ��ַ���
	 */

	public static String bytetostr(byte[] bt) {

		int i = byte2int(bt, 0);
		String str = short2str(i, 16);
		return str;
	}

	/**
	 * ������������ byte[] ת����String �����ֽڣ�
	 * 
	 * @param by
	 *            ��ʾҪת�����ֽ�
	 * @return str ����ת���õ�String
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
	 * ������������ byte[] ת����ASCII�ַ�
	 * 
	 * @param bt
	 *            byte[] ��ʾҪת�����ֽ�
	 * 
	 * @return str ����ת���õ�String
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
	 * ����������������ת���ɴ����ֽڴ�����λ��ǰ��λ�ں�
	 * <p>
	 * 
	 * @param st
	 *            ��ʾҪת����������
	 * @return bt ����ת���õ��ֽڴ�(�ĸ��ֽ�)
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
	 * ������������������ת���ɴ����ֽڴ�����λ��ǰ��λ�ں�
	 * <p>
	 * 
	 * @param st
	 *            ��ʾҪת����������
	 * @return bt ����ת���õ��ֽڴ�(�����ֽ�)
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
	 * ������������������ת�����ַ���,DoHΪ���Ƶı�ʽ������
	 * <p>
	 * 16���Ʊ�ʾ��0xFFFF������
	 * <p>
	 * 
	 * @param st
	 *            ��ʾҪת���Ķ�����
	 * @param DoH
	 *            ��ʾҪת���Ľ��Ʊ�ʽ����
	 * @return int ����ת���õ��ַ�
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
	 * ������������10����ת����16���ƣ�ֻ�ṩһλת�����ܣ�
	 * <p>
	 * ����α���С��16
	 * <p>
	 * 
	 * @param st
	 *            ��ʾҪת��������
	 * @return Rs ����ת���õ�16���Ʊ�ʾ
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
	 * �����������ַ���ת���ɴ����ֽڴ�,
	 * <p>
	 * ��ÿһ���ַ�ת������Ӧ��ASCII
	 * <p>
	 * ��:����"aa",bt�洢aa��ASCIIֵ
	 * 
	 * @param st
	 *            ��ʾҪת�����ַ���
	 * @return bt ����ת���õ��ֽڴ�
	 */

	public static byte[] str2byte(String st) {
		byte[] bt = st.getBytes();
		return bt;
	}

	/**
	 * ���ַ���ת����byte[]������"12456f",���0x12,0x45,0x6f,���������ַ�����Ϊ������ ���һ���ַ���������
	 * 
	 * @return ת���õ�byte[]
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
	 * �����������ַ���ת�������֣�16���ƺ�8���ƾ��ɣ�
	 * <p>
	 * 
	 * @param st
	 *            ��ʾҪת�����ַ���,�ַ��������������͵�,
	 *            <p>
	 *            ��:"123",������"abc",�����ʮ�����Ƽ�ǰ׺"0x",�����´�;
	 *            <p>
	 * @return ����ת���õ�����
	 */
	public static int str2int(String st) {
		if (st.substring(0, 2).compareTo("0x") == 0) {
			return Integer.parseInt(st.substring(2), 16);
		} else {
			return Integer.parseInt(st);
		}
	}

	/**
	 * ������������ String ת���� byte[]�����ֽڣ�
	 * 
	 * @param str
	 *            ��ʾҪת����String
	 * @return by ����ת���õ��ֽ�
	 */

	public static byte[] strstobyte(String str) {

		int i_len = str.length();
		byte[] by = new byte[i_len / 2];

		byte[] tmp = new byte[4];
		for (int i = 0; i < i_len / 2; i++) {

			tmp = Util.strtobyte(str.substring(2 * i, 2 * i + 2));
			by[i] = tmp[0];

		}
		// cat.debug("�� String ת���� byte[]�����ֽڣ�:str��"+str+"byte[]");
		// util.print_byte(by);
		return by;

	}

	/**
	 * ������������ String ת���� ByteBuffer�����ֽڣ�
	 * 
	 * @param str
	 *            String ��ʾҪת����String
	 * @return buffer ByteBuffer ����ת���õ��ֽ�
	 */

	public static ByteBuffer strstobytebuf(String str) {
		ByteBuffer buffer = null;

		byte[] bt = Util.strstobyte(str);
		buffer = ByteBuffer.wrap(bt);
		buffer.rewind();
		return buffer;

	}

	/**
	 * �������������Ա�ʶ���ֵ��ַ���ת����String[]
	 * 
	 * @param str
	 *            ԭʼ�ַ���
	 * @param bzw
	 *            ��־λ
	 * @return target[] ����ת���õ��ַ�������
	 */

	public static String[] strtoarr(String str, String bzw) {

		int tempi = 0;
		int i_suffix = 1;
		String temps = str;
		// �������鳤��
		while (temps.indexOf(bzw) != -1) {
			tempi = temps.indexOf(bzw);
			i_suffix = i_suffix + 1;
			temps = temps.substring(tempi + 1, str.length());
		}
		// ת������
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
	 * ������������ʮ�����Ƶ��ַ���ת���ɴ����ִ�
	 * <p>
	 * (����4λʮ�����Ƶ��ַ���ʾ,�����롰68a8����
	 * <p>
	 * ת���Ժ󷵻�0xa868��˫�ֽ�)
	 * <p>
	 * 
	 * @param str
	 *            ��ʾҪת�����ַ�
	 * @return bt ����ת���õ�˫�ֽڴ�
	 */

	public static byte[] strtobyte(String str) {

		int i = Integer.parseInt(str, 16);
		byte[] bt = short2byte(i);
		return bt;
	}

	/**
	 * ������������ʮ�����Ƶ��ַ���ת����int
	 * <p>
	 * (��������λʮ�����Ƶ��ַ���ʾ,�����롰68a8����
	 * <p>
	 * ת���Ժ󷵻�������26792)
	 * <p>
	 * 
	 * @param str
	 *            ��ʾҪת�����ַ�
	 * @return i ����ת���õ�����
	 */

	public static int strtoint(String str) {

		int i = Integer.parseInt(str, 16);
		return i;
	}

	// 2���ַ�Ϊһ����λ�ߵ�
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

	// 1���ַ�Ϊһ����λ�ߵ�
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
		// У���(�����򡢵�ַ����·�û����ݵ��ֽڵİ�λλ�������ͣ����������λ)
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
		// У���(�����򡢵�ַ����·�û����ݵ��ֽڵİ�λλ�������ͣ����������λ)
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
	 * ������������ȡ��ǰʱ��(ms)
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
		if (time_S.equals("����һ")) {
			time_S = "1";
		}
		if (time_S.equals("���ڶ�")) {
			time_S = "2";
		}
		if (time_S.equals("������")) {
			time_S = "3";
		}
		if (time_S.equals("������")) {
			time_S = "4";
		}
		if (time_S.equals("������")) {
			time_S = "5";
		}
		if (time_S.equals("������")) {
			time_S = "6";
		}
		if (time_S.equals("������")) {
			time_S = "7";

		}
		return time_S;
	}

	/**
	 * �����������������ڵõ�����
	 * 
	 * @param rq
	 *            String ����(yymmddhhmmss)
	 * @return sRet String ����String��
	 */
	public static String getWeek(String rq) throws Exception {
		try {
			SimpleDateFormat fm1 = new SimpleDateFormat("yyMMddHHmmss");
			Date time_D = fm1.parse(rq);

			SimpleDateFormat fm2 = new SimpleDateFormat("EE");
			String time_S = fm2.format(time_D);
			if (time_S.equals("����һ")) {
				time_S = "1";
			}
			if (time_S.equals("���ڶ�")) {
				time_S = "2";
			}
			if (time_S.equals("������")) {
				time_S = "3";
			}
			if (time_S.equals("������")) {
				time_S = "4";
			}
			if (time_S.equals("������")) {
				time_S = "5";
			}
			if (time_S.equals("������")) {
				time_S = "6";
			}
			if (time_S.equals("������")) {
				time_S = "7";

			}
			return time_S;
		} catch (Exception e) {
			throw e;
		}

	}

	/**
	 * ����������������ŵó�ʱ���
	 * 
	 * @param xh
	 *            String ���(1-48)
	 * @return sRet String ����String��(hh:mm)
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
			sRet = "��";
		} else if (str.equals("01")) {
			sRet = "ʱ";
		} else if (str.equals("10")) {
			sRet = "��";
		} else if (str.equals("11")) {
			sRet = "��";
		}

		return sRet;
	}

	/**
	 *����������ȡ����
	 * 
	 * @return seq String ����
	 */
	public static String getSeq(Connection con, String seqName)
			throws Exception {

		String seq = "";

		try {
			// ����֡��������
			String s_sql = "select " + seqName + ".NEXTVAL seq FROM DUAL";
			ArrayList lst = (ArrayList) CMDb.getCollection(con, s_sql);
			seq = String.valueOf(((HashMap) lst.get(0)).get("seq"));
		} catch (Exception e) {
			System.out.println("ȡ����" + seqName + "����");
			e.printStackTrace();
			throw e;
		}
		return seq;

	}

	/**
	 *����������ȡ����
	 * 
	 * @return seq String ����
	 */
	public static String getSeq(JdbcTemplate jdbcT, String seqName)
			throws Exception {

		String seq = "";

		try {
			// ����֡��������
			String s_sql = "select " + seqName.toUpperCase()
					+ ".NEXTVAL seq FROM DUAL";
			List lst = jdbcT.queryForList(s_sql);
			seq = String.valueOf(((HashMap) lst.get(0)).get("seq"));
		} catch (Exception e) {
			System.out.println("ȡ����" + seqName + "����");
			e.printStackTrace();
			throw e;
		}
		return seq;

	}

	/**
	 *����������ȡ����֡���ͱ�ķ�������SEQ_SJZFS
	 * 
	 * @return seq_sjzfs String ����֡��������
	 */
	public static String getSeqSjzfs(Connection con) throws Exception {

		String seq_sjzfs = "";

		try {
			// ����֡��������
			String s_sql = "select SEQ_SJZFS.NEXTVAL seq_sjzfs FROM DUAL";
			ArrayList lst = (ArrayList) CMDb.getCollection(con, s_sql);
			seq_sjzfs = String.valueOf(((HashMap) lst.get(0)).get("seq_sjzfs"));
		} catch (Exception e) {
			System.out.println("ȡ����SEQ_SJZFS����");
			e.printStackTrace();
			throw e;
		}
		return seq_sjzfs;

	}

	/**
	 *����������ȡ����֡���ͱ�ķ�������SEQ_SJZFS
	 * 
	 * @return seq_sjzfs String ����֡��������
	 */
	public static String getSeqSjzfs(JdbcTemplate jdbcT) throws Exception {

		String seq_sjzfs = "";

		try {
			// ����֡��������
			String s_sql = "select SEQ_SJZFS.NEXTVAL seq_sjzfs FROM DUAL";
			List lst = (List) jdbcT.queryForList(s_sql);
			seq_sjzfs = String.valueOf(((Map) lst.get(0)).get("seq_sjzfs"));
		} catch (Exception e) {
			System.out.println("ȡ����SEQ_SJZFS����");
			e.printStackTrace();
			throw e;
		}
		return seq_sjzfs;

	}

	/**
	 *����������ȡ����֡���ͱ�ķ�������SEQ_SJZFS
	 * 
	 * @return seq_sjzfs String ����֡��������
	 */
	public static String getSeqRwid(JdbcTemplate jdbcT) throws Exception {

		String seq_sjzfs = "";

		try {
			// ����֡��������
			String s_sql = "select SEQ_RWID.NEXTVAL seq FROM DUAL";
			List lst = (List) jdbcT.queryForList(s_sql);
			seq_sjzfs = String.valueOf(((Map) lst.get(0)).get("seq"));
		} catch (Exception e) {
			System.out.println("ȡ����SEQ_RWID����");
			e.printStackTrace();
			throw e;
		}
		return seq_sjzfs;

	}

	/**
	 *����������ȡ�ն��¼���¼����¼�����SEQ_EXCEPTION
	 * 
	 * @return seq_exception String �¼�����
	 */
	public static String getSeqException(Connection con) throws Exception {

		String seq_exception = "";

		try {
			// �¼�����
			String s_sql = "select SEQ_EXCEPTION.NEXTVAL seq_exception FROM DUAL";
			ArrayList lst = (ArrayList) CMDb.getCollection(con, s_sql);
			seq_exception = String.valueOf(((HashMap) lst.get(0))
					.get("seq_exception"));
		} catch (Exception e) {
			System.out.println("ȡ����SEQ_EXCEPTION����");
			e.printStackTrace();
			throw e;
		}
		return seq_exception;

	}

	/**
	 *����������ȡ�ն��¼���¼����¼�����SEQ_EXCEPTION
	 * 
	 * @return seq_exception String �¼�����
	 */
	public static String getSeqException(JdbcTemplate jdbcT) throws Exception {

		String seq_exception = "";

		try {
			// �¼�����
			String s_sql = "select SEQ_EXCEPTION.NEXTVAL seq_exception FROM DUAL";
			List lst = (List) jdbcT.queryForList(s_sql);
			seq_exception = String.valueOf(((Map) lst.get(0))
					.get("seq_exception"));
		} catch (Exception e) {
			System.out.println("ȡ����SEQ_EXCEPTION����");
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
			s_mcsx = "�����й�";
		} else if (i_mcsx == 1) {
			s_mcsx = "�����޹�";
		} else if (i_mcsx == 2) {
			s_mcsx = "�����й�";
		} else if (i_mcsx == 3) {
			s_mcsx = "�����޹�";
		}

		return s_mcsx;

	}

	/**
	 * ������������ʮ�����ַ�ת����ʮ�������ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int �ֽ���
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
	 * ������������ʮ�����ַ�ת����ʮ�������ַ�
	 * 
	 * @param value
	 *            int Ҫת��������
	 * @param len
	 *            int �ֽ���
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
	 * ������������ʮ�������ַ�ת���ɶ������ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int �ֽ���
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
	 * ������������ʮ�������ַ�ת����ʮ�����ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int �ֽ���
	 * @return s String
	 */
	public static String hexStrToDecStr(String str) {
		long tempi = Long.parseLong(str, 16);

		return String.valueOf(tempi);
	}

	/**
	 * ������������ʮ�����ַ�ת���ɶ������ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int �ֽ���
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
	 * ������������ʮ�����ַ�ת���ɶ������ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int �ֽ���
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
	 * ������������ʮ�����ַ�ת���ɶ������ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int λ��
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
	 * ������������ʮ�����ַ�ת���ɶ������ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int λ��
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
	 * �������������������ַ�ת����ʮ�����ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int �ֽ���
	 * @return s String
	 */
	public static String binStrToDecStr(String str) {
		return String.valueOf(Long.parseLong(str, 2));
	}

	/**
	 * �������������������ַ�ת����ʮ�������ַ�
	 * 
	 * @param str
	 *            String Ҫת�����ַ�
	 * @param len
	 *            int �ֽ���
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
	 * ����������������ת����ʮ�������ַ�
	 * 
	 * @param num
	 *            int Ҫת��������
	 * @param len
	 *            int �ֽ���
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
	 * ����������ȡ��Ϣ��DT
	 * 
	 * @param iDA
	 *            int ���͵�DA
	 * @return sDA String ����ʮ�����Ƶ�DA
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
				iDT1 = (iDT - 1) / 8;// ���ֽ�
				iDT2 = (iDT % 8 == 0) ? 8 : (iDT % 8);// ���ֽ�

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
	 * ����������ȡ��Ϣ��DA
	 * 
	 * @param iDA
	 *            int ���͵�DA
	 * @return sDA String ����ʮ�����Ƶ�DA
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
			iDA1 = (iDA - 1) / 8 + 1;// ���ֽ�
			iDA2 = (iDA % 8 == 0) ? 8 : (iDA % 8);// ���ֽ�

			iDA2 = (int) Math.pow(2, iDA2 - 1);
			sDA1 = Util.intToHexStr(iDA1, 1);
			sDA2 = Util.intToHexStr(iDA2, 1);

			sDA = sDA1 + sDA2;
		}
		return sDA;
	}

	/**
	 * ����������ȡ��Ϣ��DA
	 * 
	 * @param str
	 *            String ��Ϣ��:"Pn"
	 * @return sDA String ����ʮ�����Ƶ�DA
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
				iDA1 = (iDA - 1) / 8 + 1;// ���ֽ�
				iDA2 = (iDA % 8 == 0) ? 8 : (iDA % 8);// ���ֽ�

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
	 * ����������ȡ��Ϣ��DT
	 * 
	 * @param str
	 *            String ��Ϣ��:"Fn"
	 * @return sDA String ����ʮ�����Ƶ�DA
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
				iDT1 = (iDT - 1) / 8;// ���ֽ�
				iDT2 = (iDT % 8 == 0) ? 8 : (iDT % 8);// ���ֽ�

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

	// /**����������ת����Ϣ��DA
	// * @param DA String ���ֽڵ�ʮ�������ַ�(��Ϣ���)
	// * @return sRet String ����ʮ���Ƶ��ַ�
	// */
	// public static String tranDA(String DA) throws Exception{
	// if(DA.equals("0000")){
	// return "0";
	// }
	//          	
	// String sRet = "";
	// String DA1 = DA.substring(0,2);//��λ
	// int iDA1 = Integer.parseInt(DA1,16);
	// iDA1 = (int)(Math.log(iDA1)/Math.log(2));
	//          	
	// String DA2 = DA.substring(2,4);//��λ
	// int iDA2 = Integer.parseInt(DA2,16);
	// iDA2 = (int)(Math.log(iDA2)/Math.log(2));
	//        	
	// sRet = String.valueOf((iDA2+1) + (iDA1)*8);
	//        	
	// return sRet;
	// }

	// 2010-05-19
	/**
	 * ����������ת����Ϣ��DA
	 * 
	 * @param DA
	 *            String ���ֽڵ�ʮ�������ַ�(��Ϣ���)
	 * @return sRet String ����ʮ���Ƶ��ַ�
	 */
	public static String tranDA(String DA) throws Exception {
		if (DA.equals("0000")) {
			return "0";
		}

		String sRet = "";
		String DA1 = DA.substring(0, 2);// ��λ
		int iDA1 = Integer.parseInt(DA1, 16);

//		System.out.println(iDA1);

		String DA2 = DA.substring(2, 4);// ��λ
		int iDA2 = Integer.parseInt(DA2, 16);
		iDA2 = (int) (Math.log(iDA2) / Math.log(2));

		sRet = String.valueOf((iDA2 + 1) + (iDA1 - 1) * 8);

		return sRet;
	}

	/**
	 * ����������ת����Ϣ��DT
	 * 
	 * @param DT
	 *            String ���ֽڵ�ʮ�������ַ�(��Ϣ��)
	 * @return sRet String ����ʮ���Ƶ��ַ�
	 */
	public static String tranDT(String DT) throws Exception {
		String sRet = "";
		String DT1 = DT.substring(0, 2);// ��λ
		int iDT1 = Integer.parseInt(DT1, 16);

		String DT2 = DT.substring(2, 4);// ��λ
		int iDT2 = Integer.parseInt(DT2, 16);
		iDT2 = (int) (Math.log(iDT2) / Math.log(2));

		sRet = String.valueOf((iDT2 + 1) + (iDT1) * 8);

		return sRet;
	}

	/**
	 * ������������ȡ�ն�����վͨ��״̬
	 * 
	 * @param str
	 *            String �������ַ�
	 * @return sRet String ����״̬
	 */
	public static String getZdyzzthzt(String str) throws Exception {
		String sRet = "��Ч";

		if (str.equals("01")) {
			sRet = "��������վͨ��";
		} else if (str.equals("10")) {
			sRet = "��ֹ����վͨ��";
		}

		return sRet;
	}

	/**
	 * ������������ȡ�ն������ϱ�״̬
	 * 
	 * @param str
	 *            String �������ַ�
	 * @return sRet String ����״̬
	 */
	public static String getZdzdsbzt(String str) throws Exception {
		String sRet = "��Ч";

		if (str.equals("01")) {
			sRet = "���������ϱ�";
		} else if (str.equals("10")) {
			sRet = "��ֹ�����ϱ�";
		}

		return sRet;
	}

	/**
	 * ������������ȡ����֡�ĳ�����
	 * 
	 * @param iLen
	 *            int
	 * @return sRet String ����ʮ�������ַ�
	 */
	public static String getLEN(int iLen) throws Exception {
		// 04��
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
	 * ������������ȡASCII���ַ�
	 * 
	 * @param str
	 *            String ʮ�������ַ�
	 * @return sRet String ����ASCII���ַ�
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
	 * ������������ȡASCII���ַ�
	 * 
	 * @param bt
	 *            byte[] �ֽ���
	 * @return sRet String ����ASCII���ַ�
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
	 * ����������ת����ʽ01
	 * 
	 * @param str
	 *            String ʮ�������ַ�(6�ֽ�)
	 * @return sRet String ����String��
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

			sRet = s_yy + "��" + s_mm + "��" + s_dd + "��" + s_hh + "��" + s_ff
					+ "��" + s_ss + "��(����" + i_xq + ")";

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}
	
	
	/**
	 * ����������ת����ʽ01
	 * 
	 * @param str
	 *            String ʮ�������ַ�(6�ֽ�)
	 * @return sRet String ����String��  yy-MM-dd HH:mm:ss
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
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ01
	 * 
	 * @param str
	 *            String ʮ�������ַ�(6�ֽ�)
	 * @return sRet String ����String��
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
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ02
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return dRet double ����double��
	 */
	public static String tranFormat02(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8λ
			temps = temps.substring(4, 8);// ��4λ

			// ֵ
			String sValue = temp_str.substring(1, 4);
			double dValue = Double.parseDouble(sValue);

			// �˷�
			String cf = temps.substring(0, 3);
			if (cf.equals("000")) {
				// 10��4�η�
				dValue = dValue * 10000;

			} else if (cf.equals("001")) {
				// 10��3�η�
				dValue = dValue * 1000;

			} else if (cf.equals("010")) {
				// 10��2�η�
				dValue = dValue * 100;

			} else if (cf.equals("011")) {
				// 10��1�η�
				dValue = dValue * 10;

			} else if (cf.equals("100")) {
				// 10��0�η�
				dValue = dValue * 1;

			} else if (cf.equals("101")) {
				// 10��-1�η�
				dValue = dValue * 0.1;

			} else if (cf.equals("110")) {
				// 10��-2�η�
				dValue = dValue * 0.01;

			} else if (cf.equals("111")) {
				// 10��-3�η�
				dValue = dValue * 0.001;
			}

			// ��������(��ʱ����)
			String glzf = temps.substring(3, 4);
			if (glzf.equals("1")) {
				// ��ֵ
				dValue = dValue * (-1);
			}
			return DoubleToString(dValue, 3);
		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}

	}

	/**
	 * ����������ת����ʽ02
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String[] sRet[0]:ֵ;sRet[1]:����;sRet[2]:ϵ��
	 */
	public static String[] tranFormat02_pure(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8λ
			temps = temps.substring(4, 8);// ��4λ

			// ֵ
			String sValue = temp_str.substring(1, 4);
			Integer dValue = Integer.parseInt(sValue);

			// ϵ��
			String xs = temps.substring(0, 3);

			// ����
			String fh = temps.substring(3, 4);

			String[] sRet = new String[3];
			sRet[0] = String.valueOf(dValue);
			sRet[1] = fh;
			sRet[2] = xs;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч", "��Ч" };
		}

	}

	/**
	 * ����������ת����ʽ03
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String[] sRet[0]:ֵ��sRet[1]:��λ
	 */
	public static String[] tranFormat03(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8λ
			temps = temps.substring(4, 8);// ��4λ

			// ֵ
			String sValue = temp_str.substring(1, 8);
			// double dValue = Double.parseDouble(sValue);
			int iValue = Integer.parseInt(sValue);

			// ��λ
			String dw = temps.substring(1, 2);
			if (dw.equals("0")) {
				dw = "kWh(��)";
			} else if (dw.equals("1")) {
				dw = "MWh(Ԫ)";
			}

			// ����(��ʱ����)
			String zf = temps.substring(3, 4);
			if (zf.equals("1")) {
				// ��ֵ
				iValue = iValue * (-1);
			}

			String[] sRet = new String[2];
			sRet[0] = String.valueOf(iValue);
			sRet[1] = dw;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч" };
		}
	}

	/**
	 * ����������ת����ʽ03
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String[] sRet[0]:ֵ��sRet[1]:��λ��sRet[2]:����
	 */
	public static String[] tranFormat03_pure(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8λ
			temps = temps.substring(4, 8);// ��4λ

			// ֵ
			String sValue = temp_str.substring(1, 8);
			// double dValue = Double.parseDouble(sValue);
			int iValue = Integer.parseInt(sValue);

			// ��λ
			String dw = temps.substring(1, 2);

			// ����
			String fh = temps.substring(3, 4);

			String[] sRet = new String[3];
			sRet[0] = String.valueOf(iValue);
			sRet[1] = dw;
			sRet[2] = fh;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч", "��Ч" };
		}
	}

	/**
	 * ����������ת����ʽ03
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String[] sRet[0]:ֵ��sRet[1]:��λ
	 */
	public static String[] tranFormat03_pure1(String str) throws Exception {

		try {
			String temp_str = Util.convertStr(str);

			String temps = temp_str.substring(0, 1);
			temps = Util.hexStrToBinStr(temps, 1);// 8λ
			temps = temps.substring(4, 8);// ��4λ

			// ֵ
			String sValue = temp_str.substring(1, 8);
			// double dValue = Double.parseDouble(sValue);
			int iValue = Integer.parseInt(sValue);

			// ��λ
			String dw = temps.substring(1, 2);

			// ����
			String fh = temps.substring(3, 4);
			if (fh.equals("1")) {
				// ��ֵ
				iValue = iValue * (-1);
			}

			String[] sRet = new String[2];
			sRet[0] = String.valueOf(iValue);
			sRet[1] = dw;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч", "��Ч" };
		}
	}

	/**
	 * ����������ת����ʽ04
	 * 
	 * @param str
	 *            String ʮ�������ַ�(1�ֽ�)
	 * @return sRet String[] sRet[0]:ֵ��sRet[1]:������־
	 */
	public static String[] tranFormat04(String str) throws Exception {

		try {
			String temp_str = Util.hexStrToBinStr(str, 1);
			String sValue = Util.binStrToHexStr(temp_str.substring(1), 1);

			String fdbz = "";
			if (temp_str.substring(0, 1).equals("0")) {
				fdbz = "�ϸ�";
			} else if (temp_str.substring(0, 1).equals("1")) {
				fdbz = "�¸�";
			}

			String[] sRet = new String[2];
			sRet[0] = sValue;
			sRet[1] = fdbz;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч" };
		}
	}

	/**
	 * ����������ת����ʽ05
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat05(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);

			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr("0" + temp_str.substring(0, 1), 1);
			String zf = sw.substring(4, 5);// ����
			sw = sw.substring(5, 8);// ������
			i_sw = Integer.parseInt(sw, 2);// ʮ����

			sRet = String.valueOf(i_sw) + temp_str.substring(1, 3) + "."
					+ temp_str.substring(3, 4);

			// ȥ��ͷβ����
			double d = Double.parseDouble(sRet);

			if (zf.equals("1")) {
				d = d * (-1);
			}

			sRet = DoubleToString(d, 1);
			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ05
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * 
	 * @return sRet String[] sRet[0]:ֵ;sRet[1]:����
	 */
	public static String[] tranFormat05_pure(String str) throws Exception {

		try {
			String[] sRet = new String[2];

			String temp_str = Util.convertStr(str);

			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			String fh = sw.substring(4, 5);// ����
			sw = sw.substring(5, 8);// ������
			i_sw = Integer.parseInt(sw, 2);// ʮ����

			String temps = String.valueOf(i_sw) + temp_str.substring(1, 3)
					+ "." + temp_str.substring(3, 4);

			// ȥ��ͷβ����
			double d = Double.parseDouble(temps);

			sRet[0] = DoubleToString(d, 1);

			sRet[1] = fh;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч" };
		}
	}

	/**
	 * ����������ת����ʽ06
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat06(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr("0" + temp_str.substring(0, 1), 1);
			// ����
			String zf = sw.substring(4, 5);

			sw = sw.substring(5, 8);// ������
			i_sw = Integer.parseInt(sw, 2);// ʮ����

			sRet = String.valueOf(i_sw) + temp_str.substring(1, 2) + "."
					+ temp_str.substring(2, 4);

			// ȥ��ͷβ����
			double d = Double.parseDouble(sRet);
			if (zf.equals("1")) {
				d = d * (-1);
			}
			sRet = DoubleToString(d, 2);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ06
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * 
	 * @return sRet String[] sRet[0]:ֵ;sRet[1]:����
	 */
	public static String[] tranFormat06_pure(String str) throws Exception {

		try {
			String[] sRet = new String[2];
			String temp_str = Util.convertStr(str);
			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			// ����
			String zf = sw.substring(4, 5);

			sw = sw.substring(5, 8);// ������
			i_sw = Integer.parseInt(sw, 2);// ʮ����

			String temps = String.valueOf(i_sw) + temp_str.substring(1, 2)
					+ "." + temp_str.substring(2, 4);

			sRet[0] = DoubleToString(Double.parseDouble(temps), 2);

			sRet[1] = zf;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч" };
		}
	}

	/**
	 * ����������ת����ʽ07
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat07(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = temp_str.substring(0, 3) + "." + temp_str.substring(3, 4);

			sRet = DoubleToString(Double.parseDouble(sRet), 1);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ08
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat08(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = String.valueOf(Integer.parseInt(temp_str));

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ09
	 * 
	 * @param str
	 *            String ʮ�������ַ�(3�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat09(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			String sw = "";
			int i_sw = 0;
			sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);

			// ����
			String zf = sw.substring(4, 5);

			sw = sw.substring(5, 8);// ������
			i_sw = Integer.parseInt(sw, 2);// ʮ����

			sRet = String.valueOf(i_sw) + temp_str.substring(1, 2) + "."
					+ temp_str.substring(2, 4) + temp_str.substring(4, 6);

			// ȥ��ͷβ����
			double d = Double.parseDouble(sRet);
			if (zf.equals("1")) {
				d = d * (-1);
			}
			sRet = DoubleToString(d, 4);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ10
	 * 
	 * @param str
	 *            String ʮ�������ַ�(3�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat10(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = String.valueOf(Integer.parseInt(temp_str));

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ11
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat11(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 6) + "." + temp_str.substring(6, 8);

			// ȥ��ͷβ����
			sRet = DoubleToString(Double.parseDouble(sRet), 2);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ12
	 * 
	 * @param str
	 *            String ʮ�������ַ�(6�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat12(String str) throws Exception {

		try {
			String sRet = "";

			sRet = String.valueOf(Long.valueOf(Util.convertStr(str)));

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ13
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat13(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = temp_str.substring(0, 4) + "." + temp_str.substring(4, 8);

			// ȥ��ͷβ����
			sRet = DoubleToString(Double.parseDouble(sRet), 4);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ14
	 * 
	 * @param str
	 *            String ʮ�������ַ�(5�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat14(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 6) + "." + temp_str.substring(6, 10);

			// ȥ��ͷβ����
			sRet = DoubleToString(Double.parseDouble(sRet), 4);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ15
	 * 
	 * @param str
	 *            String ʮ�������ַ�(5�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat15(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEEEEEE")) {
				return "��Ч";
			}
			sRet = temp_str.substring(0, 2) + "��" + temp_str.substring(2, 4)
					+ "��" + temp_str.substring(4, 6) + "��"
					+ temp_str.substring(6, 8) + "ʱ"
					+ temp_str.substring(8, 10) + "��";

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ17
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat17(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEEEE")) {
				return "��Ч";
			}
			sRet = temp_str.substring(0, 2) + "��" + temp_str.substring(2, 4)
					+ "��" + temp_str.substring(4, 6) + "ʱ"
					+ temp_str.substring(6, 8) + "��";

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ18
	 * 
	 * @param str
	 *            String ʮ�������ַ�(3�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat18(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEE")) {
				return "��Ч";
			}
			sRet = temp_str.substring(0, 2) + "��" + temp_str.substring(2, 4)
					+ "ʱ" + temp_str.substring(4, 6) + "��";

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ20
	 * 
	 * @param str
	 *            String ʮ�������ַ�(3�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat20(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEEEE")) {
				return "��Ч";
			}
			sRet = temp_str.substring(0, 2) + "��" + temp_str.substring(2, 4)
					+ "��" + temp_str.substring(4, 6) + "��";

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ21
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat21(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEE")) {
				return "��Ч";
			}
			sRet = temp_str.substring(0, 2) + "��" + temp_str.substring(2, 4)
					+ "��";

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ22
	 * 
	 * @param str
	 *            String ʮ�������ַ�(1�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat22(String str) throws Exception {

		try {
			String sRet = "";

			sRet = Integer.parseInt(str.substring(0, 1)) + "." + Integer.parseInt(str.substring(1, 2));

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ23
	 * 
	 * @param str
	 *            String ʮ�������ַ�(3�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat23(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 2) + "." + temp_str.substring(2, 6);

			sRet = DoubleToString(Double.parseDouble(sRet), 4);
			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ25(+/-799.999)
	 * 
	 * @param str
	 *            String ʮ�������ַ�(3�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat25(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			// ��λ
			String bw = "";
			bw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			// ����
			String zf = bw.substring(4, 5);

			bw = bw.substring(5, 8);// ������
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
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ25(+/-799.999)
	 * 
	 * @param str
	 *            String ʮ�������ַ�(3�ֽ�)
	 * 
	 * @return sRet String[] sRet[0]:ֵ;sRet[1]:����
	 */
	public static String[] tranFormat25_pure(String str) throws Exception {

		try {
			String[] sRet = new String[2];
			String temp_str = Util.convertStr(str);
			// ��λ
			String bw = "";
			bw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
			// ����
			String zf = bw.substring(4, 5);

			bw = bw.substring(5, 8);// ������
			bw = Util.binStrToDecStr(bw);

			String temps = bw + temp_str.substring(1, 2)
					+ temp_str.substring(2, 3) + "." + temp_str.substring(3, 4)
					+ temp_str.substring(4, 6);

			sRet[0] = DoubleToString(Double.parseDouble(temps), 3);

			sRet[1] = zf;

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return new String[] { "��Ч", "��Ч" };
		}
	}
	
	/**
	 * ����������ת����ʽ26
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat26(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 1) + "." + temp_str.substring(1, 4);

			sRet = DoubleToString(Double.parseDouble(sRet), 3);
			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}

	/**
	 * ����������ת����ʽ27
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat27(String str) throws Exception {

		try {
			String sRet = "";
			String temp_str = Util.convertStr(str);

			sRet = String.valueOf(Long.parseLong(temp_str));

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}
	
	/**
	 * ����������ת����ʽ28
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String[] sRet[0]:ֵ��sRet[1]:������־
	 */
	public static String[] tranFormat28(String str) throws Exception {

		  try {
			  String[] sRet = new String[2];

				String temp_str = Util.convertStr(str);
				String sw = "";
				int i_sw = 0;
				sw = Util.hexStrToBinStr(temp_str.substring(0, 1), 1);
				String fh = sw.substring(4, 5);// ����
				sw = sw.substring(5, 8);// ������
				i_sw = Integer.parseInt(sw, 2);// ʮ����

				String temps = String.valueOf(i_sw) + temp_str.substring(1, 4);
				
				sRet[0] = String.valueOf(Integer.parseInt(temps));
				sRet[1] = fh;

				return sRet;

			} catch (Exception e) {
				cat.error("���ݸ�ʽ����:", e);
				return new String[] { "��Ч", "��Ч" };
			}
	}
	
	/**
	 * ����������ת����ʽ21
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat29(String str) throws Exception {

		try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			if (temp_str.equalsIgnoreCase("EEEE")) {
				return "��Ч";
			}
			sRet = temp_str.substring(0, 2) + "-" + temp_str.substring(2, 4);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}
	
	/**
	 * ����������ת����ʽ30
	 * 
	 * @param str
	 *            String ʮ�������ַ�(2�ֽ�)
	 * @return sRet String ����String��
	 */
	public static String tranFormat30(String str) throws Exception {
        try {
			String sRet = "";

			String temp_str = Util.convertStr(str);
			sRet = temp_str.substring(0, 2) + "." + temp_str.substring(2, 4);

			// ȥ��ͷβ����
			sRet = DoubleToString(Double.parseDouble(sRet), 2);

			return sRet;

		} catch (Exception e) {
			cat.error("���ݸ�ʽ����:", e);
			return "��Ч";
		}
	}
	
	/**
	 * ����������04F15��F16������¶�
	 * 
	 * @param str
	 *            String ʮ�������ַ�(4�ֽ�)
	 * @return sRet String[] sRet[0]:ֵ��sRet[1]:������־ 1Ϊ������0Ϊ����
	 */
	public static String[] tranFormatTemperture(String str) throws Exception {

		  try {
			  String[] sRet = new String[2];

				String sw = "";
				int i_sw = 0;
				sw = Util.hexStrToBinStr(str, 1);
				String fh = sw.substring(0, 1);// ����
				sw = "0"+sw.substring(1, 8);// ������
				i_sw = Integer.parseInt(Util.binStrToDecStr(sw));

				sRet[0] = String.valueOf(i_sw);
				sRet[1] = fh;

				return sRet;

			} catch (Exception e) {
				cat.error("���ݸ�ʽ����:", e);
				return new String[] { "��Ч", "��Ч" };
			}
	}
	

	/**
	 * ������������װ��ʽ01
	 * 
	 * @param rq
	 *            String ����(yymmddhhmmss)
	 * @param xq
	 *            String ����
	 * @return sRet String ����String��
	 */
	public static String makeFormat01(String rq, String xq) throws Exception {

		try {
			String sRet = "";
			String month = rq.substring(2, 3);// �·ݵ�ʮλ
			String week = Util.decStrToBinStr(xq, 1);
			week = week.substring(5, 8);

			String week_month = week + month;// ����+�·ݵ�ʮλ
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
	 * ������������װ��ʽ01
	 * 
	 * @param rq
	 *            String ����(yymmddhhmmss)
	 * @return sRet String ����String��
	 */
	public static String makeFormat01(String rq) throws Exception {

		try {
			String sRet = "";
			// �������ڵõ�����
			String xq = Util.getWeek(rq);

			String month = rq.substring(2, 3);// �·ݵ�ʮλ
			String week = Util.decStrToBinStr(xq, 1);
			week = week.substring(5, 8);

			String week_month = week + month;// ����+�·ݵ�ʮλ
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
	 * ������������װ��ʽ02
	 * 
	 * @param value
	 *            String ֵ��>=1,<=999��
	 * @param xs
	 *            Sting ϵ�������չ�Լ,�磺000=10E4...��
	 * @param zf
	 *            String ������0������1����
	 * @return sRet String ����String��
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

			// ��λ
			String bw = s_value.substring(0, 1);
			bw = Util.decStrToBinStr(bw, 1);
			bw = bw.substring(4, 8);

			// ʮλ
			String sw = s_value.substring(1, 2);
			sw = Util.decStrToBinStr(sw, 1);
			sw = sw.substring(4, 8);

			// ��λ
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
	 * ������������װ��ʽ03
	 * 
	 * @param value
	 *            String ֵ��>=0,<=99999999��
	 * @param dw
	 *            String ��λ��0:kWh/��;1:MWh/Ԫ��
	 * @param fh
	 *            String ���ţ�0������1����
	 * 
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ04
	 * 
	 * @param s0
	 *            int ��־��0���ϸ���1���¸���
	 * @param value
	 *            int ֵ(>=0,<=99)
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ05
	 * 
	 * @param fh
	 *            String ���ţ�0������1������
	 * @param value
	 *            String ֵ(>=0.0,<=799.9)
	 * 
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ08
	 * 
	 * @param value
	 *            int ֵ(>=0,<=9999)
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ25
	 * 
	 * @param fh
	 *            String ���ţ�0������1������
	 * @param value
	 *            String ֵ(>=0.0,<=799.999)
	 * 
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ06
	 * 
	 * @param fh
	 *            String ���ţ�0������1������
	 * @param value
	 *            String ֵ(>=0.0,<=79.99)
	 * 
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ07
	 * 
	 * @param value
	 *            String ֵ(>=0.0,<=999.9)
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ22
	 * 
	 * @param value
	 *            String ֵ(>=0.0,<=9.9)
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ23
	 * 
	 * @param value
	 *            String ֵ(>=0.0,<=99.9999)
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ28
	 * 
	 * @param s0
	 *            int ��־��0���ϸ���1���¸���
	 * @param value
	 *            int ֵ(>=0,<=9999)
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ29
	 * 
	 * @param rq
	 *            String ���� 3��15��:0315
	 * @param value
	 *            int ֵ(>=0,<=9999)
	 * @return sRet String ����String��
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
	 * ������������װ��ʽ30
	 * 
	 * @param value
	 *            String ֵ(>=0.0,<=99.99)
	 * @return sRet String ����String��
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
	 * ������������װ04F15��F16���¶ȵĸ�ʽ
	 * 
	 * @param s0
	 *            int ��־��0���ϸ���1���¸���
	 * @param value
	 *            int ֵ(>=0,<=9999)
	 * @return sRet String ����String��
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
	 * �����������¼�ʱ��ת��
	 * 
	 * @param value
	 *            String mmhhddmmyy
	 * @return sRet String ����String��
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
	 * �����������¼���־ת��
	 * 
	 * @param value
	 *            String BS64
	 * @return sRet String ����String��
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
							sRet += "[ERC1]���ݳ�ʼ���Ͱ汾�����¼:���¼�<br>";
						} else {
							sRet += "[ERC1]���ݳ�ʼ���Ͱ汾�����¼:���¼�<br>";
						}
						break;
					case 2:
						if (bz.equals("1")) {
							sRet += "[ERC2]������ʧ��¼:���¼�<br>";
						} else {
							sRet += "[ERC2]������ʧ��¼:���¼�<br>";
						}
						break;
					case 3:
						if (bz.equals("1")) {
							sRet += "[ERC3]���������¼:���¼�<br>";
						} else {
							sRet += "[ERC3]���������¼:���¼�<br>";
						}
						break;
					case 4:
						if (bz.equals("1")) {
							sRet += "[ERC4]״̬����λ��¼:���¼�<br>";
						} else {
							sRet += "[ERC4]״̬����λ��¼:���¼�<br>";
						}
						break;
					case 5:
						if (bz.equals("1")) {
							sRet += "[ERC5]ң����բ��¼:���¼�<br>";
						} else {
							sRet += "[ERC5]ң����բ��¼:���¼�<br>";
						}
						break;
					case 6:
						if (bz.equals("1")) {
							sRet += "[ERC6]������բ��¼:���¼�<br>";
						} else {
							sRet += "[ERC6]������բ��¼:���¼�<br>";
						}
						break;
					case 7:
						if (bz.equals("1")) {
							sRet += "[ERC7]�����բ��¼:���¼�<br>";
						} else {
							sRet += "[ERC7]�����բ��¼:���¼�<br>";
						}
						break;
					case 8:
						if (bz.equals("1")) {
							sRet += "[ERC8]���ܱ�������:���¼�<br>";
						} else {
							sRet += "[ERC8]���ܱ�������:���¼�<br>";
						}
						break;
					case 9:
						if (bz.equals("1")) {
							sRet += "[ERC9]������·�쳣:���¼�<br>";
						} else {
							sRet += "[ERC9]������·�쳣:���¼�<br>";
						}
						break;
					case 10:
						if (bz.equals("1")) {
							sRet += "[ERC10]��ѹ��·�쳣:���¼�<br>";
						} else {
							sRet += "[ERC10]��ѹ��·�쳣:���¼�<br>";
						}
						break;
					case 11:
						if (bz.equals("1")) {
							sRet += "[ERC11]�����쳣:���¼�<br>";
						} else {
							sRet += "[ERC11]�����쳣:���¼�<br>";
						}
						break;
					case 12:
						if (bz.equals("1")) {
							sRet += "[ERC12]���ܱ�ʱ�䳬��:���¼�<br>";
						} else {
							sRet += "[ERC12]���ܱ�ʱ�䳬��:���¼�<br>";
						}
						break;
					case 13:
						if (bz.equals("1")) {
							sRet += "[ERC13]��������Ϣ:���¼�<br>";
						} else {
							sRet += "[ERC13]��������Ϣ:���¼�<br>";
						}
						break;
					case 14:
						if (bz.equals("1")) {
							sRet += "[ERC14]�ն�ͣ/�ϵ��¼�:���¼�<br>";
						} else {
							sRet += "[ERC14]�ն�ͣ/�ϵ��¼�:���¼�<br>";
						}
						break;
					case 15:
						if (bz.equals("1")) {
							sRet += "[ERC15]г��Խ�޸澯:���¼�<br>";
						} else {
							sRet += "[ERC15]г��Խ�޸澯:���¼�<br>";
						}
						break;
					case 16:
						if (bz.equals("1")) {
							sRet += "[ERC16]ֱ��ģ����Խ�޼�¼:���¼�<br>";
						} else {
							sRet += "[ERC16]ֱ��ģ����Խ�޼�¼:���¼�<br>";
						}
						break;
					case 17:
						if (bz.equals("1")) {
							sRet += "[ERC17]��ѹ/������ƽ��Խ��:���¼�<br>";
						} else {
							sRet += "[ERC17]��ѹ/������ƽ��Խ��:���¼�<br>";
						}
						break;
					case 18:
						if (bz.equals("1")) {
							sRet += "[ERC18]������Ͷ��������¼:���¼�<br>";
						} else {
							sRet += "[ERC18]������Ͷ��������¼:���¼�<br>";
						}
						break;
					case 19:
						if (bz.equals("1")) {
							sRet += "[ERC19]����������ü�¼:���¼�<br>";
						} else {
							sRet += "[ERC19]����������ü�¼:���¼�<br>";
						}
						break;
					case 20:
						if (bz.equals("1")) {
							sRet += "[ERC20]��������¼:���¼�<br>";
						} else {
							sRet += "[ERC20]��������¼:���¼�<br>";
						}
						break;
					case 21:
						if (bz.equals("1")) {
							sRet += "[ERC21]�ն˹��ϼ�¼:���¼�<br>";
						} else {
							sRet += "[ERC21]�ն˹��ϼ�¼:���¼�<br>";
						}
						break;
					case 22:
						if (bz.equals("1")) {
							sRet += "[ERC22]�й��ܵ��ܲԽ���¼���¼:���¼�<br>";
						} else {
							sRet += "[ERC22]�й��ܵ��ܲԽ���¼���¼:���¼�<br>";
						}
						break;
					case 24:
						if (bz.equals("1")) {
							sRet += "[ERC24]��ѹԽ�޼�¼:���¼�<br>";
						} else {
							sRet += "[ERC24]��ѹԽ�޼�¼:���¼�<br>";
						}
						break;
					case 25:
						if (bz.equals("1")) {
							sRet += "[ERC25]����Խ�޼�¼:���¼�<br>";
						} else {
							sRet += "[ERC25]����Խ�޼�¼:���¼�<br>";
						}
						break;
					case 26:
						if (bz.equals("1")) {
							sRet += "[ERC26]���ڹ���Խ�޼�¼:���¼�<br>";
						} else {
							sRet += "[ERC26]���ڹ���Խ�޼�¼:���¼�<br>";
						}
						break;
					case 27:
						if (bz.equals("1")) {
							sRet += "[ERC27]���ܱ�ʾ���½�:���¼�<br>";
						} else {
							sRet += "[ERC27]���ܱ�ʾ���½�:���¼�<br>";
						}
						break;
					case 28:
						if (bz.equals("1")) {
							sRet += "[ERC28]����������:���¼�<br>";
						} else {
							sRet += "[ERC28]����������:���¼�<br>";
						}
						break;
					case 29:
						if (bz.equals("1")) {
							sRet += "[ERC29]���ܱ����:���¼�<br>";
						} else {
							sRet += "[ERC29]���ܱ����:���¼�<br>";
						}
						break;
					case 30:
						if (bz.equals("1")) {
							sRet += "[ERC30]���ܱ�ͣ��:���¼�<br>";
						} else {
							sRet += "[ERC30]���ܱ�ͣ��:���¼�<br>";
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
	
	* @Description: TODO(ͨ���ն˵�ַ�������������ȡ�ն˱��) 
	
	* @param @param xzqxm����������
	* @param @param zddz�ն˵�ַ
	* @param @param jdbcT
	* @param @return    �趨�ļ� 
	
	* @return String    �������� 
	
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
	
	* @Description: TODO(ȡ��ĳ��Sequences.Nextval) 
	
	* @param @param sequences
	* @param @param jdbcT
	* @param @return    �趨�ļ� 
	
	* @return String    �������� 
	
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
	 * ȡ���û����ƺ��û��ֻ�����
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
	 * ͨ���ն˱�Ų�ѯʱ���Ѵ����ն˵�ǰ״̬��¼
	 * 
	 * @param zdid
	 * @param jdbcT
	 * @return true:���ڣ�false:������
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
		
		//2015-11-19���£�
		int i_zddz=Integer.parseInt(Util.hexStrToDecStr(zddz));
		//2016-07-20����
		int i_xzqxm=Integer.parseInt(Util.hexStrToDecStr(xzqxm));
		//2015-11-19���£�
//		System.out.println("�ն��Զ���¼>>>>>>>>��ַΪ��"+i_zddz);
		

		String s_sql = "select zdid from G_ZDGZ where xzqxm=? and zddz=?";
		String[] params = new String[] { xzqxm, zddz };
		List lst = jdbcT.queryForList(s_sql, params);
		String zdid = "";
		if (lst.size() == 0) {
			//�޸���2012-07-19���� SEQ_ZDID.NEXTVAL
//			s_sql = "select (max(zdid)+1) zdid from G_ZDGZ";
			s_sql = "select SEQ_ZDID.NEXTVAL as zdid from dual";
			List templst = jdbcT.queryForList(s_sql);
			zdid = String.valueOf(((Map) templst.get(0)).get("zdid"));
			//2015-11-19���£�Ĭ��Ϊ�ǻ��ն�
			String zdlx="1";
			//2015-11-19���£�Լ����ַ5000�Ժ��Ϊ�����ն�
			if(i_zddz>5000){
				zdlx="2";
			}
			//2016-07-20���£�Լ���ն��ͺ�����������������32767��Ϊ1���նˣ�С����Ϊ2���ն�
			String zdxh="2";
			if(i_xzqxm>32767){
				zdxh="1";
			}

//			s_sql = "insert into G_ZDGZ(zdid,xzqxm,zddz,zdmc,txfsdm,gylx) "
//					+ "values(?,?,?,?,?,?)";
			s_sql = "insert into G_ZDGZ(zdid,stationid,xzqxm,zddz,zdlx,zdxh,zdmc,txfsdm,gylx) "
				+ "values(?,0,?,?,?,?,?,?,?)";
			params = new String[] {zdid,xzqxm, zddz,zdlx,zdxh,
					xzqxm + zddz + "[�Զ����]", txfs, gylx };
			jdbcT.update(s_sql, params);
            
			//����g_zdyxcspzb
			s_sql = "insert into g_zdyxcspzb(zdid) values(?)";
			params = new String[] { zdid };
			jdbcT.update(s_sql, params);
			
			    //20130517��������װ�ǻ��ն�Ĭ�ϵĲ�����������Ϣ z�����ն�Ϊһ�����ն�//
				if("1".equalsIgnoreCase(zdlx)){
					//����0�Ų�����
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,0,'1','����','1','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����1�Ų����㡾���á�
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,1,'2','����','','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����2�Ų����㡾ˮ��1��
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,2,'3','ˮ��1','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����3�Ų����㡾ˮ��2��
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,3,'4','ˮ��2','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����4�Ų����㡾���1��
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,4,'5','���1','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����5�Ų����㡾���2��
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,5,'6','���2','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����6�Ų����㡾�����ã����ᷧ����
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,6,'7','������(���ᷧ)','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "0" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "1" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "2" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "3" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "4" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "5" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "6" };
					jdbcT.update(s_sql, params);
					
					//2016-08-25�޸� ������G_ZDCJDPZB ��һ·PH
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,1,'1','PH��1','2',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//������G_ZDCJDPZB �ڶ�·PH
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,2,'2','PH��2','2',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//������G_ZDCJDPZB ��һ·ORP
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,1,'1','ORP1','4',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//������G_ZDCJDPZB �ڶ�·ORP
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,2,'2','ORP2','4',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					//������G_ZDCJDPZB ����·ORP
					s_sql = "insert into G_ZDCJDPZB(id,cjdh,xh,cjdmc,cjdlx,zt,zdid)  values(S_ZDCJDID.Nextval,3,'3','ORP3','4',0,?) ";
					params = new String[] {zdid};
					jdbcT.update(s_sql, params);
					
				}else{
					//����0�Ų�����
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,0,'1','����','1','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����2�Ų����㡾ˮ��1��
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,2,'3','ˮ��1','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//����3�Ų����㡾ˮ��2��
					s_sql = "insert into g_zdcldpzb(id,cldh,xh,cldmc,cldlx,txdz,gylx,txsl,zdid) values(SEQ_CLDID.Nextval,3,'4','ˮ��2','6','000000000000','0','0',?)";
					params = new String[] { zdid};
					jdbcT.update(s_sql, params);
					
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "0" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "2" };
					jdbcT.update(s_sql, params);
					//������G_ZDCLDDQSJB
					s_sql = "insert into G_ZDCLDDQSJB(cldid)  select id from g_zdcldpzb where zdid=? and cldh=? ";
					params = new String[] { zdid, "3" };
					jdbcT.update(s_sql, params);
				}
			
//			//20130517������Ĭ����G_ZDDQZTJLB������¼
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
		String sgSJZ = "";// ʡ��������
		sgSJZ += "68";// 68
		sgSJZ += xzqxm + Util.convertStr(zddz);// �ն��߼���ַ
		sgSJZ += "4200";// ��վ��ַ�������к�
		sgSJZ += "68";// 68
		sgSJZ += "8F";// ������:8F
		// ���ݳ���(DATA.length+5)
		String len = Util.decStrToHexStr(sjz.length() / 2 + 5, 2);
		len = Util.convertStr(len);
		sgSJZ += len;// ���ݳ���
		sgSJZ += "0000000000";// �Ǳ걸�ñ�ʶ
		sgSJZ += sjz;// ������
		// У����
		String cs = Util.getCS(sgSJZ);
		sgSJZ += cs;// У����
		sgSJZ += "16";// 16

		return sgSJZ;
	}

	public static String addFront(String sjz, String gylx, String txfs)
			throws Exception {
		String sSJZ = "";
		sSJZ += "FE";
		sSJZ += gylx;// ��Լ����:1:���;2:����;3:������
		sSJZ += txfs;// 0:������;1:COM;2:ƽ̨;3:GPRS;4:SMS
		sSJZ += "000000000000";// ͨ������(SIM/IP��)
		String len = Util.decStrToHexStr(sjz.length() / 2, 2);
		len = Util.convertStr(len);
		sSJZ += len;// ����
		sSJZ += sjz;

		return sSJZ;
	}

	/**
	 *ִ�д洢����(�з���ֵ,���롢���������Ϊ String ��)
	 * 
	 *@param dataSource
	 *            DataSource ����Դ
	 *@param sp_name
	 *            String �洢��������
	 *@param sp_param
	 *            Vector ���
	 *@param ret_num
	 *            int ���صĲ�������
	 * 
	 *@return retV Vector ����:����Vecor;����:�׳��쳣
	 */
	public static Vector executeProcedure(DataSource dataSource,
			String sp_name, Vector sp_param, int ret_num) throws Exception {
		int row_num = 0;
		int i_len = 0;
		String s_sp_str = "";
		String s_parm_str = "";
		Vector retV = new Vector();
		CallableStatement callableSTMT = null;
		i_len = sp_param.size();// ��θ���
		Connection con = null;
		try {
			con = dataSource.getConnection();
			for (int i = 0; i < (i_len + ret_num); i++) {
				s_parm_str += "?,";
			}

			if (s_parm_str.length() > 0) {
				// ȥ������','��
				s_parm_str = s_parm_str.substring(0, s_parm_str.length() - 1);
			}

			callableSTMT = (CallableStatement) con.prepareCall("{call "
					+ sp_name + "(" + s_parm_str + ")}");

			callableSTMT.clearParameters();
			// �������
			for (int i = 0; i < i_len; i++) {
				// String temps = String.valueOf(sp_param.get(i));
				// callableSTMT.setString(i+1,temps);
				callableSTMT.setObject(i + 1, sp_param.get(i));
			}

			// ���ó���
			for (int i = 1; i <= ret_num; i++) {
				callableSTMT.registerOutParameter(i + i_len, Types.VARCHAR);
			}
			// ִ��
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
	 *ִ�д洢����(�з���ֵ,���롢���������Ϊ String ��)
	 * 
	 *@param jdbcT
	 *            JdbcTemplate ����Դ
	 *@param sp_name
	 *            String �洢��������
	 *@param sp_param
	 *            Vector ���
	 *@param ret_num
	 *            int ���صĲ�������
	 * 
	 *@return retV Vector ����:����Vecor;����:�׳��쳣
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
				i_len = sp_param.size();// ��θ���
				try {
					for (int i = 0; i < (i_len + ret_num); i++) {
						s_parm_str += "?,";
					}

					if (s_parm_str.length() > 0) {
						// ȥ������','��
						s_parm_str = s_parm_str.substring(0, s_parm_str
								.length() - 1);
					}

					callableSTMT = (CallableStatement) con.prepareCall("{call "
							+ sp_name + "(" + s_parm_str + ")}");

					callableSTMT.clearParameters();
					// �������
					for (int i = 0; i < i_len; i++) {
						// String temps = String.valueOf(sp_param.get(i));
						// callableSTMT.setString(i+1,temps);
						callableSTMT.setObject(i + 1, sp_param.get(i));
					}

					// ���ó���
					for (int i = 1; i <= ret_num; i++) {
						callableSTMT.registerOutParameter(i + i_len,
								Types.VARCHAR);
					}
					// ִ��
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
	 * ����������ִ������SQL
	 * 
	 * @param conn
	 *            Connection ���ݿ�����
	 * @param vSql
	 *            Vector Ҫִ�е�SQL
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
	
	* @Description: TODO(Ϊ�ַ������油��Ӧ��ch�ַ�) 
	
	* @param @param str
	* @param @param ch
	* @param @param length
	* @param @return    �趨�ļ� 
	
	* @return String    �������� 
	
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
	
	* @Description: TODO(Ϊ�ַ���ǰ�油��Ӧ��ch�ַ�) 
	
	* @param @param str
	* @param @param ch
	* @param @param length
	* @param @return    �趨�ļ� 
	
	* @return String    �������� 
	
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
	* @Description: TODO(���汾��ת��Ϊ�ĸ��ֽڵ�16�����ַ���)
	* @param @param bbh�汾�ţ�Ӳ���汾�Ż�������汾��  ��v1.4.3��
	* @param @return    �趨�ļ�
	* @return String    ��������
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
	* @Description: TODO(��һ��ȥ�����ն��������ñ�)
	* @param @param xzqxm
	* @param @param zddz
	* @param @param fileName
	* @param @param jdbcT    �趨�ļ�
	* @return void    ��������
	* @throws
	 */
	public static void updateZdsjpzb(String xzqxm, String zddz, String fileName,JdbcTemplate jdbcT) {
		String s_sql = "delete g_zdsjpzb where zdid=(select zdid from g_zdgz where xzqxm='" + xzqxm
				+ "' and zddz='" + zddz + "')";
		jdbcT.update(s_sql);
		//�ܶ���
		int zds=0;
		try {
			zds = Decode_0F.fillHM(fileName);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//��֡����
		String dqcd=Decode_0F.buffer;

		s_sql = "insert into g_zdsjpzb(zdid,cxm,zds,dqdh,dqcd,zt,sj) " + 
		"values((select zdid from g_zdgz where xzqxm='" + xzqxm+ "' and zddz='" + zddz + "'),'"+fileName+"',"+zds+",0,"+dqcd+",2,sysdate)";
		jdbcT.update(s_sql);	
		}
	
	/**
	 * 
	* @Title: saveToImgFile
	* @Description: TODO(16����תͼƬ)
	* @param @param src 16��������
	* @param @param output  �ļ�����·��
	* @return void    ��������
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
	* @Description: TODO(�ֽ�ת����)
	* @param @param ch
	* @param @return    �趨�ļ�
	* @return int    ��������
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
    * @Description: TODO(TXTתString)
    * @param @param file �ļ�
    * @param @return    �趨�ļ�
    * @return String    ��������
    * @throws
     */
    public static String txt2String(File file){
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//����һ��BufferedReader������ȡ�ļ�
            String s = null;
            while((s = br.readLine())!=null){//ʹ��readLine������һ�ζ�һ��
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
