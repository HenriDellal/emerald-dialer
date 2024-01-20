package ru.henridellal.dialer.util;

import android.text.format.DateUtils;

import java.text.DateFormat;

public class DateUtil {
	public static CharSequence getCallDateText(long date) {
		return DateUtils.formatSameDayTime(
				date,
				System.currentTimeMillis(),
				DateFormat.MEDIUM,
				DateFormat.SHORT
		);
	}

	public static int getDateFormat(long date) {
		return (DateUtils.isToday(date)) ? DateFormat.SHORT : DateFormat.LONG;
	}
}
