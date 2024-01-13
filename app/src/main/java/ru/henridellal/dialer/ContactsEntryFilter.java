package ru.henridellal.dialer;

import android.widget.Filter;

import java.util.ArrayList;
import java.util.Collections;

import static ru.henridellal.dialer.ContactsEntryAdapter.FILTERING_MODE_PINYIN;
import static ru.henridellal.dialer.ContactsEntryAdapter.FILTERING_MODE_RAW;
import static ru.henridellal.dialer.ContactsEntryAdapter.FILTERING_MODE_REGEX;

import ru.henridellal.dialer.filter.PinyinFilter;
import ru.henridellal.dialer.filter.RawFilter;
import ru.henridellal.dialer.filter.RegexFilter;

public class ContactsEntryFilter extends Filter {
	private final ContactsEntryAdapter adapter;

	private final int mode;
	public ContactsEntryFilter(ContactsEntryAdapter adapter, int mode) {
		super();
		this.adapter = adapter;
		this.mode = mode;
	}

	@Override
	protected Filter.FilterResults performFiltering(CharSequence constraint) {
		Filter.FilterResults results = new FilterResults();
		ArrayList<QueryResult> resultsList = new ArrayList<QueryResult>();
		switch (mode) {
			case FILTERING_MODE_RAW:
				RawFilter.filter(adapter.getCursor(), resultsList, constraint);
				break;
			case FILTERING_MODE_PINYIN:
				PinyinFilter.filter(adapter.getCursor(), resultsList, constraint);
				break;
			case FILTERING_MODE_REGEX:
				RegexFilter.filter(adapter.getCursor(), resultsList, constraint);
				break;
		}
		Collections.sort(resultsList);
		results.values = resultsList;
		results.count = resultsList.size();
		return results;
	}

	protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
		adapter.update((ArrayList<QueryResult>) results.values);
	}
}
