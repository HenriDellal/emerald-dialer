package ru.henridellal.dialer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import ru.henridellal.dialer.AsyncContactImageLoader.ImageCallback;

public class ContactsEntryAdapter extends BaseAdapter implements Filterable, View.OnClickListener
{
	public static final String[] PROJECTION = {
		Phone._ID,
		Phone.LOOKUP_KEY,
		Phone.DISPLAY_NAME,
		Phone.NUMBER
	};
	
	public static final int COLUMN_LOOKUP_KEY = 1;
	public static final int COLUMN_NAME = 2;
	public static final int COLUMN_NUMBER = 3;

	public static final int FILTERING_MODE_REGEX = 0;
	public static final int FILTERING_MODE_RAW = 1;
	public static final int FILTERING_MODE_PINYIN = 2;

	private ForegroundColorSpan span;
	private StyleSpan boldStyleSpan;

	private ArrayList<RegexQueryResult> regexQueryResults;
	private AsyncContactImageLoader mAsyncContactImageLoader;
	private Cursor mCursor;
	private ContactsEntryFilter filter;
	private Locale t9Locale;
	private SoftReference<DialerActivity> activityRef;

	private int filteringMode;

	private static final int[] t9CommonPatternIds = new int[]{
			R.string.common_regex_0, R.string.common_regex_1,
			R.string.common_regex_2, R.string.common_regex_3,
			R.string.common_regex_4, R.string.common_regex_5,
			R.string.common_regex_6, R.string.common_regex_7,
			R.string.common_regex_8, R.string.common_regex_9 };

	private static final int[] t9LocalPatternIds = new int[]{
			R.string.local_regex_0, R.string.local_regex_1,
			R.string.local_regex_2, R.string.local_regex_3,
			R.string.local_regex_4, R.string.local_regex_5,
			R.string.local_regex_6, R.string.local_regex_7,
			R.string.local_regex_8, R.string.local_regex_9 };

	private Map<Character, String> t9NumberPatterns;

	public Map<Character, String> getT9NumberPatterns() {
		return t9NumberPatterns;
	}

	public void initT9NumberPatterns(Resources res) {
		t9NumberPatterns = new HashMap<Character, String>();
		for (char i = '0'; i <= '9'; i++) {
			String commonPattern = res.getString(t9CommonPatternIds[Character.getNumericValue(i)]);
			String localPattern = res.getString(t9LocalPatternIds[Character.getNumericValue(i)]);
			String pattern = (localPattern.isEmpty()) ?
					commonPattern : String.format("(%1s|%2s)", commonPattern, localPattern);
			t9NumberPatterns.put(new Character(i), pattern);
		}
		t9NumberPatterns.put(new Character('*'), res.getString(R.string.regex_star));
		t9NumberPatterns.put(new Character('#'), res.getString(R.string.regex_hash));
		t9NumberPatterns.put(new Character('+'), Pattern.quote("+"));
	}

	public ContactsEntryAdapter(DialerActivity activity, AsyncContactImageLoader asyncContactImageLoader, Context t9LocaleContext, Locale t9Locale) {
		super();
		activityRef = new SoftReference<DialerActivity>(activity);
		initT9NumberPatterns(null != t9LocaleContext ?
				t9LocaleContext.getResources() :
				activity.getResources());
		regexQueryResults = new ArrayList<RegexQueryResult>();
		mAsyncContactImageLoader = asyncContactImageLoader;
		span = new ForegroundColorSpan(activity.getResources().getColor(R.color.green_600));
		boldStyleSpan = new StyleSpan(Typeface.BOLD);
		this.t9Locale = t9Locale;
	}

	public Cursor getCursor() {
		return mCursor;
	}

	public void setCursor(Cursor cursor) {
		if (mCursor != null) {
			mCursor.close();
		}
		mCursor = cursor;
	}
	
	@Override
	public Filter getFilter() {
		if (filter == null) {
			filter = new ContactsEntryFilter(this, filteringMode);
		}
		return filter;
	}
	
	public void resetFilter() {
		filter = null;
		if (null != regexQueryResults)
			regexQueryResults.clear();
	}
	@Override
	public int getCount() {
		return (null != regexQueryResults) ? regexQueryResults.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.contact_entry_image) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.withAppendedPath(Phone.CONTENT_URI, ((ContactImageTag)view.getTag()).contactId);
			intent.setDataAndType(uri, "vnd.android.cursor.dir/contact");
			try {
				activityRef.get().startActivity(intent);
			} catch (ActivityNotFoundException e) {
				activityRef.get().showMissingContactsAppDialog();
			}
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		
		if (convertView == null) {
			view = LayoutInflater.from(activityRef.get()).inflate(R.layout.contacts_entry, parent, false);
			view.setTag(new ContactsEntryCache(view));
		} else {
			view = convertView;
		}
		final RegexQueryResult queryResult = regexQueryResults.get(position);
		final ContactsEntryCache viewCache = (ContactsEntryCache) view.getTag();

		if (!TextUtils.isEmpty(queryResult.name) && queryResult.start != queryResult.end) {
			SpannableString nameSpanned = new SpannableString(queryResult.name);
			nameSpanned.setSpan(span, queryResult.start, queryResult.end, 0);
			nameSpanned.setSpan(boldStyleSpan, queryResult.start, queryResult.end, 0);
			viewCache.contactName.setText(nameSpanned);
		} else {
			viewCache.contactName.setText(queryResult.name);
		}

		if (null != queryResult.number && !TextUtils.isEmpty(queryResult.number)
				&& queryResult.numberStart != queryResult.numberEnd) {
			SpannableString numberSpanned = new SpannableString(formatNumber(queryResult.number));
			if (queryResult.numberEnd <= numberSpanned.length())
				numberSpanned.setSpan(span, queryResult.numberStart, queryResult.numberEnd, 0);
			viewCache.phoneNumber.setText(numberSpanned);
		} else {
			viewCache.phoneNumber.setText(formatNumber(queryResult.number));
		}

		ContactImageTag tag = new ContactImageTag(String.valueOf(queryResult.id), queryResult.lookupKey);
		viewCache.contactImage.setTag(tag); // set a tag for the callback to be able to check, so we don't set the contact image of a reused view
		Drawable d = mAsyncContactImageLoader.loadDrawableForContact(queryResult.lookupKey, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String lookupKey) {
				if (lookupKey.equals(((ContactImageTag)viewCache.contactImage.getTag()).lookupKey)) {
					viewCache.contactImage.setImageDrawable(imageDrawable);
				}
			}
		});
		viewCache.contactImage.setImageDrawable(d);
		viewCache.contactImage.setOnClickListener(this);
		return view;
	}
	
	public void setFilteringMode(int filteringMode) {
		this.filteringMode = filteringMode;
	}
	
	public String getPhoneNumber(int position) {
		mCursor.moveToPosition(regexQueryResults.get(position).position);
		return mCursor.getString(COLUMN_NUMBER);
	}
	
	public String formatNumber(String number) {
		PhoneNumberUtils.normalizeNumber(number);
		String result = PhoneNumberUtils.formatNumber(number, t9Locale.getCountry());
		return (null != result) ? result : number;
	}

	public void update(ArrayList<RegexQueryResult> results) {
		regexQueryResults = results;
		notifyDataSetChanged();
	}
}
