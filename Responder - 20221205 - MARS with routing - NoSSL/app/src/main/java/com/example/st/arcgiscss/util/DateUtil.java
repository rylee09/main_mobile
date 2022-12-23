package com.example.st.arcgiscss.util;


import java.util.Date;

public class DateUtil {
	public final static String yyyy = "yyyy";
	public final static String MM_dd = "MM-dd";
	public final static String dd = "dd";
	public final static String yyyy_MM_dd = "yyyy-MM-dd";
	public final static String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
	public final static String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
	public final static String yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss SSS";
	public final static String MM_dd_HH_mm_ss = "MM-dd  HH:mm:ss";
	public final static String MM_dd_HH_mm = "MM-dd  HH:mm";


		/**
		 * @param dateString
		 * @return
		 */
		public static String changeTimeType(String dateString) {
			long timestr = Long.valueOf(dateString);
			Date date = new Date(timestr);
			int hours = date.getHours();
			int min = date.getMinutes();
			String minustr = date.getMinutes()+"";
			if (min<10){
				minustr = "0"+minustr;
			}
			String time = "";
			if (hours>12){
				time = 	hours-12+":"+minustr+"PM";
			}else {
				time = hours+":"+minustr+"AM";
			}
			return time;
		}


}