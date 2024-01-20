package ru.henridellal.dialer.util;

import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

public class RTLUtil {
	public static String format(String s) {
		if (s.isEmpty()) return s;
		return new StringBuilder()
				.append('\u200e')
				.append(s)
				.append('\u202c')
				.toString();
	}

	public static boolean isRtlLayout() {
		return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
	}
}
