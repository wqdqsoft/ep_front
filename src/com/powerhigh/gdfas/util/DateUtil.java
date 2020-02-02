/**
 * File: DateUtil.java
 *
 * Created on 2007-3-15
 */

package com.powerhigh.gdfas.util;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 时间、字符串转换工具类
 * 
 * @author 林宗靠
 */
public abstract class DateUtil {
	private static final String sdf1reg = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";

	private static final SimpleDateFormat sdf1 = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static final String sdf2reg = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2}$";

	private static final SimpleDateFormat sdf2 = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static final String sdf3reg = "^\\d{2,4}\\/\\d{1,2}\\/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";
	private static final SimpleDateFormat sdf3 = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	private static final String sdf4reg = "^\\d{2,4}\\/\\d{1,2}\\/\\d{1,2}$";

	private static final SimpleDateFormat sdf4 = new SimpleDateFormat(
			"yyyy/MM/dd");
	
	private static final String sdf5reg = "^\\d{2,4}\\-\\d{1,2}$";

	private static final SimpleDateFormat sdf5 = new SimpleDateFormat(
			"yyyy-MM");
//	private static final String sdf6reg = "^\\d{2,4}\\-\\d{1,2}\\-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$";
//
//	private static final SimpleDateFormat sdf6 = new SimpleDateFormat(
//			"yyyy-MM-dd HH:mm:ss");

	/**
	 * <p>
	 * </p>
	 */
	public static final String pattern1 = "yyyy-MM-dd";

	/**
	 * <p>
	 * </p>
	 */
	public static final String pattern2 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * <p>
	 * </p>
	 */
	public static final String pattern3 = "yyyy/MM/dd";

	/**
	 * <p>
	 * </p>
	 */
	public static final String pattern4 = "yyyy/MM/dd HH:mm:ss";
	/**
	 * <p>
	 * </p>
	 */
	public static final String pattern5 = "yyyyMMddHHmmss";
	/**
	 * <p>
	 * </p>
	 */
	public static final String pattern6 = "yyyy-MM";

	/**
	 * <p>
	 * 将日期字符串解析成日期对象，支持一下格式
	 * <li>yyyy-MM-dd HH:mm:ss
	 * <li>yyyy-MM-dd
	 * <li>yyyy/MM/dd HH:mm:ss
	 * <li>yyyy/MM/dd
	 * <li>yyyy-MM
	 * </p>
	 * 
	 * @param str
	 * @return
	 */
	public static Date parse(String str) {
		Date date = null;
		Pattern p1 = Pattern.compile(sdf1reg);
		Matcher m1 = p1.matcher(str);
		Pattern p2 = Pattern.compile(sdf2reg);
		Matcher m2 = p2.matcher(str);
		Pattern p3 = Pattern.compile(sdf3reg);
		Matcher m3 = p3.matcher(str);
		Pattern p4 = Pattern.compile(sdf4reg);
		Matcher m4 = p4.matcher(str);
		Pattern p5 = Pattern.compile(sdf5reg);
		Matcher m5 = p5.matcher(str);
//		Pattern p6 = Pattern.compile(sdf6reg);
//		Matcher m6 = p6.matcher(str);

		try {
			if (m1.matches()) {
				date = sdf1.parse(str);
			} else if (m2.matches()) {
				date = sdf2.parse(str);
			} else if (m3.matches()) {
				date = sdf3.parse(str);
			} else if (m4.matches()) {
				date = sdf4.parse(str);
			} else if (m5.matches()) {
				date = sdf5.parse(str);
			}
//			else if (m6.matches()) {
//				date = sdf6.parse(str);
//			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return date;
	}

	/**
	 * <p>
	 * 将日期格式化成字符串：yyyy-MM-dd
	 * </p>
	 * 
	 * @param date
	 * @return
	 */
	public static String format(Date date) {
		if (date != null) {
			return sdf2.format(date);
		}

		return "";
	}

	/**
	 * <p>
	 * 将日期格式化成相应格式的字符串，如：
	 * <li>yyyy-MM-dd HH:mm:ss
	 * <li>yyyy-MM-dd
	 * <li>yyyy/MM/dd HH:mm:ss
	 * <li>yyyy/MM/dd
	 * </p>
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date date, String pattern) {
		if ((date == null) || (pattern == null)) {
			return "";
		}

		final SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		return sdf.format(date);
	}

	/**
	 * 根据给定的pattern格式化字符串
	 * 
	 * @param value
	 * @return
	 */
	public static String format(Object value, String pattern) {
		if (value == null) {
			return "";
		} else if (pattern != null) {
			return value.toString();
		}

		return new DecimalFormat(pattern).format(value);
	}

	/**
	 * 获得两个日期之间相差的月份<br>
	 * 
	 * @param start
	 *            起始日期
	 * @param end
	 *            截止日期
	 * @return
	 */
	public static int getMonth(Date start, Date end) {
		if (start.after(end)) {
			Date t = start;
			start = end;
			end = t;
		}
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(start);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(end);
		Calendar temp = Calendar.getInstance();
		temp.setTime(end);
		temp.add(Calendar.DATE, 1);

		int year = endCalendar.get(Calendar.YEAR)
				- startCalendar.get(Calendar.YEAR);
		int month = endCalendar.get(Calendar.MONTH)
				- startCalendar.get(Calendar.MONTH);

		if ((startCalendar.get(Calendar.DATE) == 1)
				&& (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month + 1;
		} else if ((startCalendar.get(Calendar.DATE) != 1)
				&& (temp.get(Calendar.DATE) == 1)) {
			return year * 12 + month;
		} else if ((startCalendar.get(Calendar.DATE) == 1)
				&& (temp.get(Calendar.DATE) != 1)) {
			return year * 12 + month;
		} else {
			return (year * 12 + month - 1) < 0 ? 0 : (year * 12 + month);
		}
	}

	/**
	 * 取得该日期所在月份的第一天的日期
	 * 
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.DATE, 1);// 设为当前月的1号
		return calendar.getTime();
	}
    /**
     * 取得该日期所在月份的最后一天的日期
     * @param date
     * @return
     */
	public static Date getLastDayOfMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int value = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.DAY_OF_MONTH, value);
		return cal.getTime();
	}
	/**
	 * 取得该日期所在上个月份的第一天的日期
	 * @param date
	 * @return
	 */
    public static Date getFirstDayOfPreviousMonth(Date date){    
    	Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.set(Calendar.DATE,1);//设为当前月的1号  
		cal.add(Calendar.MONTH,-1);//减一个月，变为上月的1号  
		return cal.getTime();
    }  
    /**
	 * 取得该日期所在上两个月份的第一天的日期
	 * @param date
	 * @return
	 */
    public static Date getFirstDayOfPreviousTwoMonth(Date date){    
    	Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.set(Calendar.DATE,1);//设为当前月的1号  
		cal.add(Calendar.MONTH,-2);//减两个月，变为上上月的1号  
		return cal.getTime();
    } 
	/**
	 * 取得该日期所在下个月份的第一天的日期
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfNextMonth(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.set(Calendar.DATE,1);//设为当前月的1号  
		cal.add(Calendar.MONTH,1);//加一个月，变为下月的1号  
		return cal.getTime();
	}
	/**
	 * 取得该日期所在下三个月份的第一天的日期
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfNextThreeMonth(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.set(Calendar.DATE,1);//设为当前月的1号  
		cal.add(Calendar.MONTH,3);//加三个月，变为下月的1号  
		return cal.getTime();
	}
	/**
	 * 取得该日期加减D个月后的第一天的日期
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfThatMonth(Date date ,int d) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.set(Calendar.DATE,1);//设为当前月的1号  
		cal.add(Calendar.MONTH,d);
		return cal.getTime();
	}
	/**
	 * 取得该日期所在下一年的第一天的日期
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfNextYear(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		
		cal.add(Calendar.YEAR,1);//加一年，变为下月的1号  
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}
	/**
	 * 取得该日期所在年的第一天的日期
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfYear(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}
	
	/**
	 * 取得该日期所在年的上一年的最后一天
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfPreYear(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}
	
	/**
	 * 取得该日期所在年的最后一天的日期
	 * @param date
	 * @return
	 */
	public static Date getLastDayOfThisYear(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		
		cal.add(Calendar.YEAR,1);//加一年，变为下月的1号  
		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.DATE, -1);
		return cal.getTime();
	}
	
	public static Date addYearOfThisDate(Date date) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.add(Calendar.YEAR,1);//加一年，变为下月的1号  
//		cal.set(Calendar.DAY_OF_YEAR, 1);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
	}
	
	/**
	 * 取得该日期date加减d天后的日期
	 * @param date想要运算的日期
	 * @param d 加减的天数
	 * @return
	 */
	public static Date getDay(Date date,int d) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.add(Calendar.DATE, d);   
		return cal.getTime();
	}
	
	/**
	 * 取得加减N年后那年的第一天的日期
	 * @param date
	 * @return
	 */
	public static Date getFirstDayOfThatYear(Date date,Integer y) {
		Calendar cal = Calendar.getInstance();  
		cal.setTime(date);
		cal.add(Calendar.YEAR,y);//日期加减Y年
		cal.set(Calendar.DAY_OF_YEAR, 1);
		return cal.getTime();
	}
	
	/**
	 * 
	
	* @Title: getDaysBetween 
	
	* @Description: TODO(取得两个日期间的天数) 
	
	* @param @param startDate起始日期
	* @param @param endDate截止日期
	* @param @return    设定文件 
	
	* @return Long    返回类型 
	
	* @throws
	 */
	public static Long getDaysBetween(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}

}
