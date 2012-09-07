package org.jint.bmy.sightnavi.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jintian
 *
 */
public class DateUtil {

	public static String getCurrentDate(String pattern) {
		String currentDate = null;
		Date now = new Date();
		SimpleDateFormat dataFormat = new SimpleDateFormat(pattern);
		currentDate = dataFormat.format(now);
		return currentDate;
	}

	public static String[] getDateSplite(String date, boolean autoFill) {
		String[] splite = new String[3];
		splite = date.split("-");
		String todayYear = splite[0];
		String todayMonth = splite[1];
		String todayDate = splite[2];
		if (autoFill) {
			if (todayMonth.length() == 1) {
				todayMonth = "0" + todayMonth;
			}

			if (todayDate.length() == 1) {
				todayDate = "0" + todayDate;
			}
		}

		splite[0] = todayYear;
		splite[1] = todayMonth;
		splite[2] = todayDate;

		return splite;
	}

	
	public static String getBeforeDate(String date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date beforeDate = null;
		String yesterDay = "";
		String monthString = "";
		String dateString = "";
		try {
			beforeDate = new Date(df.parse(date).getTime() - 24 * 3600 * 1000);
			int year = beforeDate.getYear();
			if (year < 2000) {
				year += 1900;
			}
			if (year > 3000) {
				year -= 1900;
			}
			int month = beforeDate.getMonth() + 1;
			if (month < 10) {
				monthString = "0" + month;
			} else {
				monthString = month + "";
			}
			int day = beforeDate.getDate();
			if (day < 10) {
				dateString = "0" + day;
			} else {
				dateString = day + "";
			}
			yesterDay = year + "-" + monthString + "-" + dateString;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return yesterDay;
	}

	public static int days(int year, int month) {
		int days = 0;
		if (month != 2) {
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				days = 31;
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				days = 30;

			}
		} else {

			if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0)
				days = 29;
			else
				days = 28;

		}
		return days;
	}

	public static String parseDate(String pattern, Date date) {
		String str = null;
		SimpleDateFormat dataFormat = new SimpleDateFormat(pattern);
		str = dataFormat.format(date);
		return str;
	}

	public static String seconds2Time(int seconds) {

		String time = "";

		int hours = seconds / 3600;
		int restSeconds = 0;
		if (hours > 0) {
			restSeconds = seconds % 3600;
		} else {
			restSeconds = seconds;
		}

		int minutes = restSeconds / 60;
		if (minutes > 0) {
			restSeconds = restSeconds % 60;
		}

		time = hours + ":" + minutes + ":" + restSeconds;

		return time;
	}

	public static String addSecond(String time) {
		if ("".equals(time) || null == time) {
			time = "0:0:0";
		}

		String newTime = "";

		String[] times = time.split(":");

		int hours = Integer.parseInt(times[0]);
		int minutes = Integer.parseInt(times[1]);
		int seconds = Integer.parseInt(times[2]);

		seconds++;

		if (seconds == 60) {
			seconds = 0;
			minutes++;
		}

		if (minutes == 60) {
			minutes = 0;
			hours++;
		}

		newTime = hours + ":" + minutes + ":" + seconds;

		return newTime;
	}
}
