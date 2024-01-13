package ru.henridellal.dialer;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class T9Manager {
	private static final T9Manager INSTANCE = new T9Manager();

	private static final int[] commonPatternIds = new int[]{
			R.string.common_regex_0, R.string.common_regex_1,
			R.string.common_regex_2, R.string.common_regex_3,
			R.string.common_regex_4, R.string.common_regex_5,
			R.string.common_regex_6, R.string.common_regex_7,
			R.string.common_regex_8, R.string.common_regex_9 };

	private static final int[] localPatternIds = new int[]{
			R.string.local_regex_0, R.string.local_regex_1,
			R.string.local_regex_2, R.string.local_regex_3,
			R.string.local_regex_4, R.string.local_regex_5,
			R.string.local_regex_6, R.string.local_regex_7,
			R.string.local_regex_8, R.string.local_regex_9 };

	private T9Manager() {}

	public static T9Manager getInstance() {
		return INSTANCE;
	}

	private Map<Character, String> patterns = null;

	public Map<Character, String> getPatterns() {
		return patterns;
	}

	public void initPatterns(Resources res) {
		patterns = new HashMap<Character, String>();
		for (char i = '0'; i <= '9'; i++) {
			int n = Character.getNumericValue(i);
			String commonPattern = res.getString(commonPatternIds[n]);
			String localPattern = res.getString(localPatternIds[n]);
			String pattern = (localPattern.isEmpty()) ?
					commonPattern : String.format("(%1s|%2s)", commonPattern, localPattern);
			patterns.put(new Character(i), pattern);
		}
		patterns.put(new Character('*'), res.getString(R.string.regex_star));
		patterns.put(new Character('#'), res.getString(R.string.regex_hash));
		patterns.put(new Character('+'), Pattern.quote("+"));
	}

	private Locale locale;

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return (null != locale) ? locale.getLanguage() : null;
	}
}
