package ru.henridellal.dialer.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.DateFormat;

public class DateUtil {
	public static CharSequence getCallDateText(Context context, long date) {
		return DateUtils.getRelativeDateTimeString(
				context,
				date,
				DateUtils.MINUTE_IN_MILLIS,
				DateUtils.WEEK_IN_MILLIS,
				DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
		);
	}

	public static int getDateFormat(long date) {
		return (DateUtils.isToday(date)) ? DateFormat.SHORT : DateFormat.LONG;
	}
}
