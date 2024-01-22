package ru.henridellal.dialer;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

import ru.henridellal.dialer.AsyncContactImageLoader.ImageCallback;
import ru.henridellal.dialer.util.ContactsUtil;

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

	private ArrayList<QueryResult> queryResults;
	private AsyncContactImageLoader mAsyncContactImageLoader;
	private Cursor mCursor;
	private ContactsEntryFilter filter;
	private SoftReference<Context> contextRef;

	private int filteringMode;

	public ContactsEntryAdapter(Context context, AsyncContactImageLoader asyncContactImageLoader) {
		super();
		contextRef = new SoftReference<Context>(context);
		queryResults = new ArrayList<QueryResult>();
		mAsyncContactImageLoader = asyncContactImageLoader;
		span = new ForegroundColorSpan(context.getResources().getColor(R.color.green_600));
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
		if (null != queryResults)
			queryResults.clear();
	}
	@Override
	public int getCount() {
		return (null != queryResults) ? queryResults.size() : 0;
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
			ContactsUtil.view(contextRef.get(), Phone.CONTENT_URI, ((ContactImageTag)view.getTag()).contactId);
		}
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		
		if (convertView == null) {
			view = LayoutInflater.from(contextRef.get()).inflate(R.layout.contacts_entry, parent, false);
			view.setTag(new ContactsEntryCache(view));
		} else {
			view = convertView;
		}
		final QueryResult queryResult = queryResults.get(position);
		final ContactsEntryCache viewCache = (ContactsEntryCache) view.getTag();

		viewCache.contactName.setText(queryResult.getSpannedName(span));
		viewCache.phoneNumber.setText(queryResult.getFormattedNumber(span));

		ContactImageTag tag = new ContactImageTag(String.valueOf(queryResult.id), queryResult.lookupKey);
		viewCache.contactImage.setTag(tag); // set a tag for the callback to be able to check, so we don't set the contact image of a reused view
		Drawable d = mAsyncContactImageLoader.loadDrawable(queryResult.lookupKey, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String lookupKey) {
				if (lookupKey.equals(((ContactImageTag)viewCache.contactImage.getTag()).lookupKey)) {
					viewCache.contactImage.setImageDrawable(imageDrawable);
				}
			}
		}, AsyncContactImageLoader.QUERY_TYPE_LOOKUP_KEY);
		viewCache.contactImage.setImageDrawable(d);
		viewCache.contactImage.setOnClickListener(this);
		return view;
	}
	
	public void setFilteringMode(int filteringMode) {
		this.filteringMode = filteringMode;
	}
	
	public String getPhoneNumber(int position) {
		mCursor.moveToPosition(queryResults.get(position).position);
		return mCursor.getString(COLUMN_NUMBER);
	}

	public void update(ArrayList<QueryResult> results) {
		queryResults = results;
		notifyDataSetChanged();
	}
}
