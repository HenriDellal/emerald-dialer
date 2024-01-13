package ru.henridellal.dialer.filter;

import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NAME;
import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NUMBER;

import android.database.Cursor;

import java.util.ArrayList;

import ru.henridellal.dialer.QueryResult;

public class RawFilter {
	public static void filter(Cursor cursor, ArrayList<QueryResult> results, CharSequence constraint) {
		cursor.moveToFirst();
		String constraintString = constraint.toString().toLowerCase();
		while (!cursor.isAfterLast()) {
			String name = cursor.getString(COLUMN_NAME);
			String number = cursor.getString(COLUMN_NUMBER);
			QueryResult queryResult = null;
			if (null != name) {
				name = name.toLowerCase();
				int nameIndexOfConstraint = name.indexOf(constraintString);
				if (nameIndexOfConstraint != -1)
					queryResult = new QueryResult(
							cursor,
							nameIndexOfConstraint,
							nameIndexOfConstraint+constraintString.length()
					);
			}
			if (null != number) {
				number = number.toLowerCase();
				int numberIndexOfConstraint = number.indexOf(constraintString);
				if (numberIndexOfConstraint != -1) {
					if (null == queryResult) {
						queryResult = new QueryResult(cursor, 0, 0);
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
