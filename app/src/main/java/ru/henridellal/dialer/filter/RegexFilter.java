package ru.henridellal.dialer.filter;

import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NAME;
import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NUMBER;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.henridellal.dialer.NumberFormatter;
import ru.henridellal.dialer.QueryResult;
import ru.henridellal.dialer.T9Manager;

public class RegexFilter {
	public static void filter(Cursor cursor, ArrayList<QueryResult> results, CharSequence constraint) {
		String nameRegex = formContactNameRegex(constraint.toString());
		String numberRegex = formNumberRegex(constraint.toString());
		Pattern namePattern = Pattern.compile(nameRegex);
		Pattern wordStartPattern = Pattern.compile("\\s+" + nameRegex);
		Pattern numberPattern = Pattern.compile(numberRegex);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(COLUMN_NAME);
			String number = cursor.getString(COLUMN_NUMBER);
			Matcher nameMatcher = (null != name) ? namePattern.matcher(name) : null;
			Matcher numberMatcher = (null != number) ? numberPattern.matcher(NumberFormatter.format(number)) : null;
			QueryResult queryResult = null;
			if (null != nameMatcher && nameMatcher.find()) {
				if (nameMatcher.start() == 0) {
					queryResult = new QueryResult(
							cursor,
							nameMatcher.start(),
							nameMatcher.end()
					);
				} else {
					Matcher wordStartMatcher = wordStartPattern.matcher(name);
					if (wordStartMatcher.find()) {
						queryResult = new QueryResult(
								cursor,
								wordStartMatcher.start(),
								wordStartMatcher.end()
						);
					}
				}
			}
			if (null != numberMatcher && numberMatcher.find()) {
				if (null == queryResult) {
					queryResult = new QueryResult(
							cursor,
							256+numberMatcher.start(),
							256+numberMatcher.start()
					);
				}
				queryResult.setNumberPlace(numberMatcher.start(), numberMatcher.end());
			}
			if (null != queryResult) {
				results.add(queryResult);
			}
			cursor.moveToNext();
		}
	}

	private static boolean isRegexIdentifier(char c) {
		return c == '*' || c == '#' || c == '+';
	}

	public static String formNumberRegex(String s) {
		StringBuilder result = new StringBuilder();
		char numberChar = s.charAt(0);
		result.append(isRegexIdentifier(numberChar)? Pattern.quote(Character.toString(numberChar)) : numberChar);
		for (int i = 1; i < s.length(); i++) {
			result.append("[\\W]*");
			numberChar = s.charAt(i);
			result.append(isRegexIdentifier(numberChar) ? Pattern.quote(Character.toString(numberChar)) : numberChar);
		}
		return result.toString();
	}

	public static String formContactNameRegex(String s) {
		StringBuilder result = new StringBuilder();
		char mChar;
		for (int i = 0; i < s.length(); i++) {
			mChar = s.charAt(i);
			result.append(T9Manager.getInstance().getPatterns().get(mChar));
		}
		return result.toString();
	}
}
