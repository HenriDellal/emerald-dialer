package ru.henridellal.dialer;

import android.content.ActivityNotFoundException;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.*;

import ru.henridellal.dialer.AsyncContactImageLoader.ImageCallback;

public class ContactsEntryAdapter extends BaseAdapter implements Filterable, View.OnClickListener
{
	public static final String[] PROJECTION = {
		Phone._ID,
		Phone.LOOKUP_KEY,
		Phone.DISPLAY_NAME,
		Phone.NUMBER
	};
	
	private static final int[] t9NumberPatternIds = new int[]{
				R.string.regex_0, R.string.regex_1,
				R.string.regex_2, R.string.regex_3,
				R.string.regex_4, R.string.regex_5,
				R.string.regex_6, R.string.regex_7,
				R.string.regex_8, R.string.regex_9 };
	
	private static final int COLUMN_LOOKUP_KEY = 1;
	private static final int COLUMN_NAME = 2;
	private static final int COLUMN_NUMBER = 3;
	private ForegroundColorSpan span;
	private StyleSpan boldStyleSpan;
	
	private Map<Character, String> t9NumberPatterns;
	private ArrayList<RegexQueryResult> regexQueryResults;
	private AsyncContactImageLoader mAsyncContactImageLoader;
	private Cursor mCursor;
	private ContactsFilter mFilter;
	private SoftReference<DialerActivity> activityRef;
	
	public ContactsEntryAdapter(DialerActivity activity, AsyncContactImageLoader asyncContactImageLoader) {
		super();
		activityRef = new SoftReference<DialerActivity>(activity);
		initT9NumberPatterns();
		regexQueryResults = new ArrayList<RegexQueryResult>();
		mAsyncContactImageLoader = asyncContactImageLoader;
		span = new ForegroundColorSpan(activity.getResources().getColor(R.color.green_600));
		boldStyleSpan = new StyleSpan(Typeface.BOLD);
	}
	
	private void initT9NumberPatterns() {
		Resources res = activityRef.get().getResources();
		t9NumberPatterns = new HashMap<Character, String>();
		for (char i = '0'; i <= '9'; i++) {
			t9NumberPatterns.put(new Character(i), res.getString(t9NumberPatternIds[Character.getNumericValue(i)]));
		}
		t9NumberPatterns.put(new Character('*'), res.getString(R.string.regex_star));
		t9NumberPatterns.put(new Character('#'), res.getString(R.string.regex_hash));
		t9NumberPatterns.put(new Character('+'), Pattern.quote("+"));
	}
	
	public void setCursor(Cursor cursor) {
		if (mCursor != null) {
			mCursor.close();
		}
		mCursor = cursor;
	}
	
	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ContactsFilter();
		}
		return mFilter;
	}
	
	public void resetFilter() {
		mFilter = null;
		regexQueryResults.clear();
	}
	@Override
	public int getCount() {
		return regexQueryResults.size();
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
		mCursor.moveToPosition(queryResult.position);
		String name = mCursor.getString(COLUMN_NAME);
		if (!TextUtils.isEmpty(name) && queryResult.start != queryResult.end) {
			SpannableString nameSpanned = new SpannableString(name);
			nameSpanned.setSpan(span, queryResult.start, queryResult.end, 0);
			nameSpanned.setSpan(boldStyleSpan, queryResult.start, queryResult.end, 0);
			viewCache.contactName.setText(nameSpanned);
		} else {
			viewCache.contactName.setText(name);
		}
		String phoneNumber = mCursor.getString(COLUMN_NUMBER);
		if (!TextUtils.isEmpty(phoneNumber) && queryResult.numberStart != queryResult.numberEnd) {
			SpannableString numberSpanned = new SpannableString(formatNumber(phoneNumber));
			numberSpanned.setSpan(span, queryResult.numberStart, queryResult.numberEnd, 0);
			viewCache.phoneNumber.setText(numberSpanned);
		} else {
			viewCache.phoneNumber.setText(formatNumber(phoneNumber));
		}
		
		String lookupKey = mCursor.getString(COLUMN_LOOKUP_KEY);
		ContactImageTag tag = new ContactImageTag(String.valueOf(mCursor.getInt(0)), lookupKey);
		viewCache.contactImage.setTag(tag); // set a tag for the callback to be able to check, so we don't set the contact image of a reused view
		Drawable d = mAsyncContactImageLoader.loadDrawableForContact(lookupKey, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String lookupKey) {
				if (lookupKey.equals(((ContactImageTag)viewCache.contactImage.getTag()).lookupKey)) {
					viewCache.contactImage.setImageDrawable(imageDrawable);
				}
			}
		});
		viewCache.contactImage.setImageDrawable(d);
		/*String thumbnailUri = mCursor.getString(COLUMN_THUMBNAIL_URI);
		if ( null == thumbnailUri) {
			viewCache.contactImage.setImageDrawable(null);
		} else {
		try {
		InputStream input = activityRef.get().getContentResolver().openInputStream(Uri.parse(thumbnailUri));
		Drawable mDrawable = Drawable.createFromStream(input, thumbnailUri);
		viewCache.contactImage.setImageDrawable(mDrawable);
		input.close();
		} catch (Exception e) {
			viewCache.contactImage.setImageDrawable(null);
		}
		}*/
		viewCache.contactImage.setOnClickListener(this);
		return view;
	}
	
	public String getPhoneNumber(int position) {
		mCursor.moveToPosition(regexQueryResults.get(position).position);
		return mCursor.getString(COLUMN_NUMBER);
	}
	
	public String formatNumber(String number) {
		return PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
	}
	
	public String formContactNameRegex(String s) {
		StringBuilder result = new StringBuilder();
		char mChar;
		for (int i = 0; i < s.length(); i++) {
			mChar = s.charAt(i);
			result.append(t9NumberPatterns.get(mChar));
		}
		return result.toString();
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
	
	private class ContactsFilter extends Filter {
		
		@Override
		protected Filter.FilterResults performFiltering(CharSequence constraint) {
			Filter.FilterResults results = new FilterResults();
			ArrayList<RegexQueryResult> resultsList = new ArrayList<RegexQueryResult>();
			String nameRegex = formContactNameRegex(constraint.toString());
			String numberRegex = formNumberRegex(constraint.toString());
			Pattern namePattern = Pattern.compile(nameRegex);
			Pattern wordStartPattern = Pattern.compile("\\s+" + nameRegex);
			Pattern numberPattern = Pattern.compile(numberRegex);
			mCursor.moveToFirst();
			while (!mCursor.isAfterLast()) {
				String name = mCursor.getString(COLUMN_NAME);
				String number = mCursor.getString(COLUMN_NUMBER);
				Matcher nameMatcher = namePattern.matcher(name);
				Matcher numberMatcher = numberPattern.matcher(formatNumber(number));
				RegexQueryResult queryResult = null;
				if (nameMatcher.find() ) {
					if (nameMatcher.start() == 0) {
						queryResult = new RegexQueryResult( mCursor.getPosition(), nameMatcher.start(), nameMatcher.end());
					} else {
						Matcher wordStartMatcher = wordStartPattern.matcher(name);
						if (wordStartMatcher.find()) {
							queryResult = new RegexQueryResult( mCursor.getPosition(), wordStartMatcher.start(), wordStartMatcher.end());
						}
					}
				}
				if (numberMatcher.find()) {
					if (null == queryResult) {
						queryResult = new RegexQueryResult( mCursor.getPosition(), 256+numberMatcher.start(), 256+numberMatcher.start());
					}
					queryResult.setNumberPlace(numberMatcher.start(), numberMatcher.end());
				}
				if (null != queryResult) {
					resultsList.add(queryResult);
				}
				mCursor.moveToNext();
			}
			Collections.sort(resultsList);
			results.values = resultsList;
			results.count = resultsList.size();
			return results;
		}
		
		protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
			regexQueryResults = ((ArrayList<RegexQueryResult>) results.values);
			notifyDataSetChanged();
		}
	
	}
	
}
