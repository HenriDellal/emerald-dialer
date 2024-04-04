package ru.henridellal.dialer;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Build;
import android.text.format.DateUtils;

import java.util.Calendar;

public class DateDiff {
	private String name, id;
	private int unit, count;
	private long millis;
	public DateDiff(String name, String id, int unit, int count) {
		this.name = name;
		this.id = id;
		this.unit = unit;
		this.count = count;
	}

	public DateDiff(String name, String id) {
		this(name, id, -1, -1);
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public long getMillis() {
		if (unit == -1 || count == -1) {
			return -1;
		} else if (millis != 0L) {
			return millis;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.add(unit, -1 * count);
		return calendar.getTimeInMillis();
	}

	public void setMillis(long millis) {
		this.millis = millis;
	}

	@Override
	public String toString() {
		return name;
	}

	public void generateName(Context context) {
		if (null != name) return;

		if (Build.VERSION.SDK_INT >= 24) {
			name = RelativeDateTimeFormatter.getInstance()
					.format(
							count,
							RelativeDateTimeFormatter.Direction.LAST,
							getRelativeUnit()
					);
		} else {
			name = DateUtils.getRelativeDateTimeString(
					context,
					getMillis(),
					DateUtils.DAY_IN_MILLIS,
					DateUtils.YEAR_IN_MILLIS * 2L,
					DateUtils.FORMAT_NO_YEAR
			).toString();
		}
	}


	@TargetApi(Build.VERSION_CODES.N)
	private RelativeDateTimeFormatter.RelativeUnit getRelativeUnit() {
		switch (unit) {
			case Calendar.DAY_OF_MONTH:
				return RelativeDateTimeFormatter.RelativeUnit.DAYS;
			case Calendar.MONTH:
				return RelativeDateTimeFormatter.RelativeUnit.MONTHS;
			case Calendar.YEAR:
				return RelativeDateTimeFormatter.RelativeUnit.YEARS;
			default:
				return RelativeDateTimeFormatter.RelativeUnit.SECONDS;
		}
	}
}
