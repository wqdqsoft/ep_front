package com.powerhigh.gdfas.test;

import com.powerhigh.gdfas.util.Util;

public class Teat04F18 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String csz = "01#1-1.5;10#1.5-7;01#7-9.5;01#17-23";
//		String cszz = "01#0-5";
//		// 10101010010000
//		new Teat04F18().getCode04F18(cszz);
//		// TODO Auto-generated method stub
//		System.out.println(Util.decStrToHexStr(2342342L, 7));
		try {
			System.out.println(Util.tranFormat28("9482")[1]+"===="+Util.tranFormat28("9482")[0]);
			System.out.println(Util.makeFormat28(0, 294));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param String
	 *            参数值(cs1;....csn;)
	 *            1<=n<=24(形如：01#1-1.5;10#1.5-7;01#7-9.5;01#17-23) csn:(cn#tn)
	 *            cn:控制状态：取值0~3依次表示不控制、控制1、控制2、保留 tn:时间区间：取值0~24 最小间隔0.5
	 *            形如“0-0.5 1-3.5” (1.5表示1:30)
	 * 
	 * @return
	 */
	public String getCode04F18(String csz) {
		// String times[]=new
		// String[]{"00:30","00:30","00:30","00:30","00:30","00:30","00:30"};
		String csns[] = csz.split(";");

		String data = "";
		for (int i = 0; i < csns.length; i++) {
			// 拆解出控制状态和时间区间
			String csn[] = csns[i].split("#");
			// 控制状态
			String cn = csn[0];
			// 时间区间
			String tn = csn[1];
			// 该时间区间的起始时刻
			Double bt = Double.parseDouble(tn.split("-")[0]);
			// 该时间区间的截止时刻
			Double et = Double.parseDouble(tn.split("-")[1]);

			// 第一个
			if (0 == i) {
				int m = (int) ((bt - 0) * 2);
				;
				for (int j = 0; j < m; j++) {
					data = data + "00";
				}
				int n = (int) ((et - bt) * 2);
				for (int j = 0; j < n; j++) {
					data = data + cn;
				}
			} else {
				// 拆解出上一个控制状态和时间区间
				String csm[] = csns[i - 1].split("#");
				// 上一个控制状态
				String cm = csm[0];
				// 上一个时间区间
				String tm = csm[1];
				// 上一个时间区间的起始时刻
				// Double btm=Double.parseDouble(tm.split("-")[0]);
				// 上一个时间区间的截止时刻
				Double etm = Double.parseDouble(tm.split("-")[1]);
				if (bt.equals(etm)) {
					int n = (int) ((et - bt) * 2);
					for (int j = 0; j < n; j++) {
						data = data + cn;
					}
				} else {
					int m = (int) ((bt - etm) * 2);
					for (int j = 0; j < m; j++) {
						data = data + "00";
					}
					int n = (int) ((et - bt) * 2);
					for (int j = 0; j < n; j++) {
						data = data + cn;
					}
				}

			}
			if ((csns.length - 1) == i) {
				int n = (int) ((24 - et) * 2);
				;
				for (int j = 0; j < n; j++) {
					data = data + "00";
				}
			}
		}
		System.out.println(data);
		for (int i = 0; i < 96; i = i + 8) {
			System.out.println(data.substring(data.length() - i - 8, data
					.length()
					- i));
		}
		System.out.println(data.length());
		System.out.println(Util.binStrToHexStr(data, 12));
		// System.out.println();
		// System.out.println(data);
		return "";
	}

}
