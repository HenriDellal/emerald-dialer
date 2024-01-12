package ru.henridellal.dialer;

public class RTLUtil {
	public static String format(String s) {
		if (s.isEmpty()) return s;
		return new StringBuilder()
				.append('\u200e')
				.append(s)
				.append('\u202c')
				.toString();
	}
}
