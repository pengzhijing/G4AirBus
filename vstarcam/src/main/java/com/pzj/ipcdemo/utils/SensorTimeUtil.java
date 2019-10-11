package com.pzj.ipcdemo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SensorTimeUtil {

	static StringBuffer sb;
	// public static ArrayList<Map<String, String>> Planlist = new
	// ArrayList<Map<String, String>>();

	public static Map<String, String> planMap = new HashMap<String, String>();

	public static boolean compare_date(String DATE1, String DATE2) {

		DateFormat df = new SimpleDateFormat("hh:mm");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return false;
			} else if (dt1.getTime() < dt2.getTime()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	public static boolean checkTime(String s1, String s2, String day) {
		int size1 = trsforTimes(s1);
		int size2 = trsforTimes(s2) - 1;
		String newkey = getBinary(size1, size2);

		if (planMap.containsKey(day)) {
			String value = planMap.get(day);
//			LogTools.logW("checkTime value:" + value);
			for (int j = 0; j < 96; j++) {
				char c1 = value.charAt(j);
				char c2 = newkey.charAt(j);
				if (c1 == '1' && c2 == '1') {
					System.out.println("有冲突:" + j);
					return true;
				}

			}
		} else {
//			LogTools.logW("没有该日期的计划");
			return false;
		}

		// Map<String, String> map = Planlist.get(i);
		// LogTools.logW("planListMap:"+map.toString());
		// String listZone = map.get(day);
		// if(listZone==null){
		// LogTools.logW("没有该日期的计划");
		// return false;
		// }
		// if(map.containsKey(day)){
		// for (int j = 0; j < 96; j++) {
		// char c1 = listZone.charAt(j);
		// char c2 = newkey.charAt(j);
		// if (c1 == '1' && c2 == '1') {
		// System.out.println("有冲突:" + j);
		// return true;
		// }
		//
		// }
		// }

		System.out.println("没有冲突");
		return false;

	}

	public static void addNewDayToMap(String key, String value) {
		if (planMap.containsKey(key)) {
			String mapvalue = planMap.get(key);
		//	LogTools.logW("addNewDayToMap key:" +key+",value:"+ value);
			StringBuffer sbs = new StringBuffer();
			for (int j = 0; j < 96; j++) {
				char c1 = value.charAt(j);
				char c2 = mapvalue.charAt(j);
				if (c1 == '1' || c2 == '1') {
					sbs.append("" + 1);
				} else {
					sbs.append("" + 0);
				}
			}
			planMap.put(key, sbs.toString());
		} else {
			planMap.put(key, value);
		//	LogTools.logW("没有该日期的计划");
		}
	}

	public static String getOneDay96Strings(String startTime, String endTime) {
		int size1 = trsforTimes(startTime);
		int size2 = trsforTimes(endTime) - 1;
		String newkey = getBinary(size1, size2);
		return newkey;

	}

	public static int getMinute(String time) {
		String array[] = time.split(":");
		int h = Integer.parseInt(array[1]);
		return h;

	}

	public static int getHour(String time) {
		String array[] = time.split(":");
		int h = Integer.parseInt(array[0]);
		return h;

	}

	public static String op2(String s) {
		int len = s.length();
		sb = new StringBuffer();
		for (int i = 0; i < len; i++) {
			char si = s.charAt(len - i - 1);
			int ss = si & 1;
			sb.append(ss + "");
		}
		return sb.toString();
	}

	public static String comString(String s1, String s2) {
		StringBuffer sbs = new StringBuffer();
		for (int i = 0; i < 96; i++) {
			char c1 = s1.charAt(i);
			char c2 = s2.charAt(i);
			if (c1 == '1' || c2 == '1') {
				sbs.append("1");
			} else {
				sbs.append("0");
			}

		}
		return sbs.toString();

	}

	public static String getBinary(int size1, int size2) {
		System.out.println("s1:" + size1 + ",s2:" + size2);
		sb = new StringBuffer();
		for (int i = 0; i < 96; i++) {

			if (i >= size1 && i <= size2) {
				sb.append("1");
			} else {
				sb.append("0");
			}
		}

		return sb.toString();
	}

	public static int trsforTimes(String time) {
		String array[] = time.split(":");
		int h = Integer.parseInt(array[0]);
		int m = Integer.parseInt(array[1]);
		int sum = 0;
		sum = h * 4 + m / 15;

		return sum;

	}
	
	
	
	
	public static int string32toInt(String string){
//		StringBuffer sb=new StringBuffer();
//		int index=0;
//		for (int i = 31; i < 32; i--) {
//			char char_i=string.charAt(i);
//			int stringint=Integer.parseInt(char_i+"")&1;
//			if(stringint==1){
//				index=i;
//				break;
//			}
//			
//		}
//		String validString=string.substring(0, index+1);
//		LogTools.LogWe("validString："+validString);
//		
		return Integer.valueOf(string, 2);
		
	}
	
	
	
	public static String getMinutesString(int starttime, int endtime) {
		String Stime = Integer.toBinaryString(starttime);
		String Etime = Integer.toBinaryString(endtime);
		String string1 = "";
		String string2 = "";
		int size1 = Stime.length();
		int size2 = Etime.length();

		if (size1 != 12) {
			string1 = getInvalidBit(12 - Stime.length()) + Stime;
		}
		if (size2 != 12) {
			string2 = getInvalidBit(12 - Etime.length()) + Etime;
		}

		if (starttime == 0) {
			string1 = "000000000000";
		}
		if (endtime == 0) {
			string2 = "000000000000";
		}

		// int ss1 = starttime <<(12-Stime.length());
		// int ss2 = endtime << (12-Etime.length());
		//
		// if (ss1 == 0) {
		// string1 = "000000000000";
		// } else {
		// string1 = Integer.toBinaryString(ss1);
		// }
		// if (ss2 == 0) {
		// string2 = "000000000000";
		// } else {
		// string2 = Integer.toBinaryString(ss2);
		// }
		System.out.println("s1:" + string1 + ",s2:" + string2);
		return string1 + string2;
	}
	
	public static String getInvalidBit(int num) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sb.append("0");
		}
		return sb.toString();
	}

	public static String replaceIndex(int index, String res) {
		return res.substring(0, index) + 1 + res.substring(index + 1);
	}

	public static String get2Strings(String bit32, String week, String bit23) {

		return bit32 + week  + bit23;

	}

	public static int getPassMinutes(int hour, int minute) {
		return hour * 60 + minute;

	}

	
	

}
