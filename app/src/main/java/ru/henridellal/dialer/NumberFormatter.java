package ru.henridellal.dialer;

import android.telephony.PhoneNumberUtils;

public class NumberFormatter {
	public static String format(String number) {
		PhoneNumberUtils.normalizeNumber(number);
		String result = PhoneNumberUtils.formatNumber(number, T9Manager.getInstance().getLocale().getCountry());
		return (null != result) ? result : number;
	}
}