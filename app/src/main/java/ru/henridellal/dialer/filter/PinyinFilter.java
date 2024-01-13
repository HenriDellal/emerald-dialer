package ru.henridellal.dialer.filter;

import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NAME;
import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NUMBER;

import android.database.Cursor;
import android.text.TextUtils;

import com.pinyinsearch.model.PinyinSearchUnit;
import com.pinyinsearch.util.PinyinUtil;
import com.pinyinsearch.util.T9Util;

import java.util.ArrayList;

import ru.henridellal.dialer.QueryResult;

public class PinyinFilter {
	public static void filter(Cursor cursor, ArrayList<QueryResult> results, CharSequence constraint) {
		cursor.moveToFirst();
		String constraintString = constraint.toString().toLowerCase();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(COLUMN_NAME);
			String number = cursor.getString(COLUMN_NUMBER);
			QueryResult queryResult = null;
			if (null != name) {
				PinyinSearchUnit psu = new PinyinSearchUnit(name);
				PinyinUtil.parse(psu);
				T9Util.match(psu, constraintString);
				String keyword = psu.getMatchKeyword().toString();

				int pinyinIndexOfConstraint = TextUtils.isEmpty(keyword) ? -1 : name.indexOf(keyword);

				if (pinyinIndexOfConstraint != -1) {
					queryResult = new QueryResult(
							cursor,
							pinyinIndexOfConstraint,
							pinyinIndexOfConstraint+keyword.length()
					);
				}

			}
			if (null != number) {
				number = number.toLowerCase();
				int numberIndexOfConstraint = number.indexOf(constraintString);
				if (numberIndexOfConstraint != -1) {
					if (null == queryResult) {
						queryResult = new QueryResult(
								cursor,
								Integer.MAX_VALUE,
								Integer.MAX_VALUE
						);
					}
					queryResult.setNumberPlace(numberIndexOfConstraint, numberIndexOfConstraint+constraintString.length());
				}

			}
			if (null != queryResult) {
				results.add(queryResult);
			}
			cursor.moveToNext();
		}
	}
}
