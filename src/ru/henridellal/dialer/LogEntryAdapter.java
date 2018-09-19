package ru.henridellal.dialer;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.lang.System;
import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Locale;

import ru.henridellal.dialer.AsyncContactImageLoader.ImageCallback;

public class LogEntryAdapter extends CursorAdapter implements View.OnClickListener
{
	public static final String[] PROJECTION = {
		Calls._ID,
		Calls.CACHED_NAME,
		Calls.NUMBER,
		Calls.DATE,
		Calls.TYPE
	};
	private static final int COLUMN_NAME = 1;
	private static final int COLUMN_NUMBER = 2;
	private static final int COLUMN_DATE = 3;
	private static final int COLUMN_TYPE = 4;
	
	private AsyncContactImageLoader mAsyncContactImageLoader;
	private int callMadeDrawableId;
	private int callReceivedDrawableId;
	private SoftReference<DialerActivity> activityRef;
	
	public LogEntryAdapter(DialerActivity activity, Cursor cursor, AsyncContactImageLoader loader) {
		super(activity, cursor, 0);
		activityRef = new SoftReference<DialerActivity>(activity);
		mAsyncContactImageLoader = loader;
		TypedValue outValue = new TypedValue();
		activity.getTheme().resolveAttribute(R.attr.drawableCallMade, outValue, true);
		callMadeDrawableId = outValue.resourceId;
		activity.getTheme().resolveAttribute(R.attr.drawableCallReceived, outValue, true);
		callReceivedDrawableId = outValue.resourceId;
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.contact_image) {
			String number = (String)view.getTag();
			if (null == number) {
				return;
			}
			Uri contactIdUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
			Cursor cursor = activityRef.get().getContentResolver().query(contactIdUri, new String[] {PhoneLookup.CONTACT_ID}, null, null, null);
			if (cursor == null || !cursor.moveToFirst()) {
				unknownNumberDialog(number);
				return;
			}
			String contactId = cursor.getString(0);
			cursor.close();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.withAppendedPath(Contacts.CONTENT_URI, contactId);
			intent.setDataAndType(uri, "vnd.android.cursor.dir/contact");
			try {
				activityRef.get().startActivity(intent);
			} catch (ActivityNotFoundException e) {
				activityRef.get().showMissingContactsAppDialog();
			}
		}
	}
	
	public void update() {
		notifyDataSetChanged();
	}
	
	public void unknownNumberDialog(final String number) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activityRef.get());
		builder.setTitle(PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry()));
		String[] items = new String[]
				{activityRef.get().getResources().getString(R.string.send_message),
				activityRef.get().getResources().getString(R.string.create_contact)};
		builder.setItems(items, 
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface di, int which) {
					Intent intent;
					switch(which) {
						case 0:
							intent = new Intent(Intent.ACTION_VIEW);
							intent.setData(Uri.parse("smsto:" + number));
							try {
								activityRef.get().startActivity(intent);
							} catch (ActivityNotFoundException e) {}
							break;
						case 1:
							try {
								activityRef.get().createContact(number);
							} catch (ActivityNotFoundException e) {
								activityRef.get().showMissingContactsAppDialog();
							}
							break;
					}
				}
			});
		builder.create().show();
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(R.layout.contact_log_entry, null);
		LogEntryCache viewCache = new LogEntryCache(view);
		view.setTag(viewCache);
		
		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		final LogEntryCache viewCache = (LogEntryCache) view.getTag();
		if (viewCache == null) {
			return;
		}
		String name = cursor.getString(COLUMN_NAME);
		String phoneNumber = cursor.getString(COLUMN_NUMBER);
		String formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
		if (!TextUtils.isEmpty(name)) {
			viewCache.contactName.setText(name);
			viewCache.phoneNumber.setText(formattedNumber);
		} else if (!TextUtils.isEmpty(formattedNumber)) {
			viewCache.contactName.setText(formattedNumber);
			viewCache.phoneNumber.setText("");
		} else {
			viewCache.contactName.setText("no number");
		}
		long date = cursor.getLong(COLUMN_DATE);
		viewCache.callDate.setText(DateUtils.formatSameDayTime(date, System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.SHORT));
	
		int id = cursor.getInt(COLUMN_TYPE);
		int callTypeDrawableId = 0;
		switch (id) {
			case Calls.INCOMING_TYPE:
				callTypeDrawableId = callReceivedDrawableId;
				break;
			case Calls.OUTGOING_TYPE:
				callTypeDrawableId = callMadeDrawableId;
				break;
			case Calls.MISSED_TYPE:
				callTypeDrawableId = R.drawable.ic_call_missed;
				break;
		}
		if (callTypeDrawableId != 0) {
			viewCache.callTypeImage.setImageDrawable(context.getResources().getDrawable(callTypeDrawableId, context.getTheme()));
		}
		viewCache.contactImage.setTag(phoneNumber); // set a tag for the callback to be able to check, so we don't set the contact image of a reused view
		Drawable d = mAsyncContactImageLoader.loadDrawableForNumber(phoneNumber, new ImageCallback() {
			
			@Override
			public void imageLoaded(Drawable imageDrawable, String number) {
				if (TextUtils.equals(number, (String)viewCache.contactImage.getTag())) {
					viewCache.contactImage.setImageDrawable(imageDrawable);
				}
			}
		});
		viewCache.contactImage.setOnClickListener(this);
		viewCache.contactImage.setImageDrawable(d);
	}
	
	public String getPhoneNumber(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		return cursor.getString(COLUMN_NUMBER);
	}
}
