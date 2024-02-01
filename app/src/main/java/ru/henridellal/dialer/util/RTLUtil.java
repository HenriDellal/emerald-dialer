package ru.henridellal.dialer.util;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;

import java.util.Locale;

public class RTLUtil {
	public static String format(String s) {
		if (null == s || s.isEmpty()) return s;
		return new StringBuilder()
				.append('\u200e')
				.append(s)
				.append('\u202c')
				.toString();
	}

	public static CharSequence formatSpannedText(CharSequence s) {
		if (null == s || s.length() == 0) return s;
		return new SpannableStringBuilder()
				.append('\u200e')
				.append(s)
				.append('\u202c')
				.subSequence(0, s.length() + 2);
	}

	public static boolean isRtlLayout() {
		return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
	}
}
