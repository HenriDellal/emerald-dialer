package ru.henridellal.dialer;

import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_LOOKUP_KEY;
import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NAME;
import static ru.henridellal.dialer.ContactsEntryAdapter.COLUMN_NUMBER;

import android.database.Cursor;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class QueryResult implements Comparable<QueryResult> {
	public static final StyleSpan boldTypeSpan = new StyleSpan(Typeface.BOLD);
	public final int position;
	public final int start;
	public final int end;

	public final int id;
	public final String lookupKey;
	public final String name;
	public final String number;
	public int numberStart;
	public int numberEnd;
	
	public QueryResult(Cursor cursor, int start, int end) {
		this.position = cursor.getPosition();
		this.start = start;
		this.end = end;
		this.id = getId(cursor);
		this.lookupKey = getLookupKey(cursor);
		this.name = getName(cursor);
		this.number = getNumber(cursor);
	}
	
	public void setNumberPlace(int numberStart, int numberEnd) {
		this.numberStart = numberStart;
		this.numberEnd = numberEnd;
	}
	
	@Override
	public int compareTo(QueryResult obj) throws NullPointerException, ClassCastException {
		if (null == obj) {
			throw new NullPointerException();
		}
		int result = Integer.compare(this.start, obj.start);
		return (result != 0) ? result : Integer.compare(this.position, obj.position);
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
	
	public CharSequence getFormattedNumber(ForegroundColorSpan span) {
		if (null != number && !TextUtils.isEmpty(number)
				&& numberStart != numberEnd) {
			SpannableString numberSpanned = new SpannableString(NumberFormatter.format(number));
			if (numberEnd <= numberSpanned.length())
				numberSpanned.setSpan(span, numberStart, numberEnd, 0);
			return numberSpanned;
		} else {
			return NumberFormatter.format(number);
		}
	}
	
	public CharSequence getSpannedName(ForegroundColorSpan span) {
		if (!TextUtils.isEmpty(name) && start != end) {
			SpannableString nameSpanned = new SpannableString(name);
			nameSpanned.setSpan(span, start, end, 0);
			nameSpanned.setSpan(boldTypeSpan, start, end, 0);
			return nameSpanned;
		} else {
			return name;
		}
	}
}
