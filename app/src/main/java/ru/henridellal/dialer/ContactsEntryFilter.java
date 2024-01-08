package ru.henridellal.dialer;

import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Filter;

import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.T9Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_LOOKUP_KEY;
import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NAME;
import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NUMBER;
import static ru.henridellal.dialer.ContactsEntryAdapter.FILTERING_MODE_PINYIN;
import static ru.henridellal.dialer.ContactsEntryAdapter.FILTERING_MODE_RAW;
import static ru.henridellal.dialer.ContactsEntryAdapter.FILTERING_MODE_REGEX;

public class ContactsEntryFilter extends Filter {
	private ContactsEntryAdapter adapter;

	private int mode;
	public ContactsEntryFilter(ContactsEntryAdapter adapter, int mode) {
		super();
		this.adapter = adapter;
		this.mode = mode;
	}

	private int getId(Cursor cursor) {
		return cursor.getInt(0);
	}

	private String getLookupKey(Cursor cursor) {
		return cursor.getString(COLUMN_LOOKUP_KEY);
	}

	private String getName(Cursor cursor) {
		return cursor.getString(COLUMN_NAME);
	}

	private String getNumber(Cursor cursor) {
		return cursor.getString(COLUMN_NUMBER);
	}

	private void filterPinyin(ArrayList<RegexQueryResult> resultsList, CharSequence constraint) {
		Cursor cursor = adapter.getCursor();
		cursor.moveToFirst();
		String constraintString = constraint.toString().toLowerCase();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(COLUMN_NAME);
			String number = cursor.getString(COLUMN_NUMBER);
			RegexQueryResult queryResult = null;
			if (null != name) {
				PinyinSearchUnit psu = new PinyinSearchUnit(name);
				PinyinUtil.parse(psu);
				T9Util.match(psu, constraintString);
				String keyword = psu.getMatchKeyword().toString();

				int pinyinIndexOfConstraint = TextUtils.isEmpty(keyword) ? -1 : name.indexOf(keyword);

				if (pinyinIndexOfConstraint != -1) {
					queryResult = new RegexQueryResult(
							cursor.getPosition(),
							pinyinIndexOfConstraint,
							pinyinIndexOfConstraint+keyword.length(),
							getId(cursor),
							getLookupKey(cursor),
							getName(cursor),
							getNumber(cursor)
					);
				}

			}
			if (null != number) {
				number = number.toLowerCase();
				int numberIndexOfConstraint = number.indexOf(constraintString);
				if (numberIndexOfConstraint != -1) {
					if (null == queryResult) {
						queryResult = new RegexQueryResult(
								cursor.getPosition(),
								Integer.MAX_VALUE,
								Integer.MAX_VALUE,
								getId(cursor),
								getLookupKey(cursor),
								getName(cursor),
								getNumber(cursor)
						);
					}
					queryResult.setNumberPlace(numberIndexOfConstraint, numberIndexOfConstraint+constraintString.length());
				}

			}
			if (null != queryResult) {
				resultsList.add(queryResult);
			}
			cursor.moveToNext();
		}
	}

	private void filterRaw(ArrayList<RegexQueryResult> resultsList, CharSequence constraint) {
		Cursor cursor = adapter.getCursor();
		cursor.moveToFirst();
		String constraintString = constraint.toString().toLowerCase();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(COLUMN_NAME);
			String number = cursor.getString(COLUMN_NUMBER);
			RegexQueryResult queryResult = null;
			if (null != name) {
				name = name.toLowerCase();
				int nameIndexOfConstraint = name.indexOf(constraintString);
				if (nameIndexOfConstraint != -1)
					queryResult = new RegexQueryResult(
							cursor.getPosition(),
							nameIndexOfConstraint,
							nameIndexOfConstraint+constraintString.length(),
							getId(cursor),
							getLookupKey(cursor),
							getName(cursor),
							getNumber(cursor)
					);
			}
			if (null != number) {
				number = number.toLowerCase();
				int numberIndexOfConstraint = number.indexOf(constraintString);
				if (numberIndexOfConstraint != -1) {
					if (null == queryResult) {
						queryResult = new RegexQueryResult(
								cursor.getPosition(),
								0,
								0,
								getId(cursor),
								getLookupKey(cursor),
								getName(cursor),
								getNumber(cursor)
						);
					}
					queryResult.setNumberPlace(numberIndexOfConstraint, numberIndexOfConstraint+constraintString.length());
				}
			}

			if (null != queryResult) {
				resultsList.add(queryResult);
			}
			cursor.moveToNext();
		}
	}

	private void filterWithRegex(ArrayList<RegexQueryResult> resultsList, CharSequence constraint) {
		String nameRegex = formContactNameRegex(constraint.toString());
		String numberRegex = formNumberRegex(constraint.toString());
		Pattern namePattern = Pattern.compile(nameRegex);
		Pattern wordStartPattern = Pattern.compile("\\s+" + nameRegex);
		Pattern numberPattern = Pattern.compile(numberRegex);
		Cursor cursor = adapter.getCursor();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(COLUMN_NAME);
			String number = cursor.getString(COLUMN_NUMBER);
			Matcher nameMatcher = (null != name) ? namePattern.matcher(name) : null;
			Matcher numberMatcher = (null != number) ? numberPattern.matcher(adapter.formatNumber(number)) : null;
			RegexQueryResult queryResult = null;
			if (null != nameMatcher && nameMatcher.find()) {
				if (nameMatcher.start() == 0) {
					queryResult = new RegexQueryResult(
							cursor.getPosition(),
							nameMatcher.start(),
							nameMatcher.end(),
							getId(cursor),
							getLookupKey(cursor),
							getName(cursor),
							getNumber(cursor)
					);
				} else {
					Matcher wordStartMatcher = wordStartPattern.matcher(name);
					if (wordStartMatcher.find()) {
						queryResult = new RegexQueryResult(
								cursor.getPosition(),
								wordStartMatcher.start(),
								wordStartMatcher.end(),
								getId(cursor),
								getLookupKey(cursor),
								getName(cursor),
								getNumber(cursor)
						);
					}
				}
			}
			if (null != numberMatcher && numberMatcher.find()) {
				if (null == queryResult) {
					queryResult = new RegexQueryResult(
							cursor.getPosition(),
							256+numberMatcher.start(),
							256+numberMatcher.start(),
							getId(cursor),
							getLookupKey(cursor),
							getName(cursor),
							getNumber(cursor)
					);
				}
				queryResult.setNumberPlace(numberMatcher.start(), numberMatcher.end());
			}
			if (null != queryResult) {
				resultsList.add(queryResult);
			}
			cursor.moveToNext();
		}
	}

	@Override
	protected Filter.FilterResults performFiltering(CharSequence constraint) {
		Filter.FilterResults results = new FilterResults();
		ArrayList<RegexQueryResult> resultsList = new ArrayList<RegexQueryResult>();
		switch (mode) {
			case FILTERING_MODE_RAW:
				filterRaw(resultsList, constraint);
				break;
			case FILTERING_MODE_PINYIN:
				filterPinyin(resultsList, constraint);
				break;
			case FILTERING_MODE_REGEX:
				filterWithRegex(resultsList, constraint);
				break;
		}
		Collections.sort(resultsList);
		results.values = resultsList;
		results.count = resultsList.size();
		return results;
	}

	protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
		adapter.update((ArrayList<RegexQueryResult>) results.values);
	}

	private boolean isRegexIdentifier(char c) {
		return c == '*' || c == '#' || c == '+';
	}

	public String formNumberRegex(String s) {
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

	public String formContactNameRegex(String s) {
		StringBuilder result = new StringBuilder();
		char mChar;
		for (int i = 0; i < s.length(); i++) {
			mChar = s.charAt(i);
			result.append(adapter.getT9NumberPatterns().get(mChar));
		}
		return result.toString();
	}

}
