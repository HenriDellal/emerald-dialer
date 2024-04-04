package ru.henridellal.dialer;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import ru.henridellal.dialer.dialog.CleanPhoneNumberLogDialog;
import ru.henridellal.dialer.dialog.UnknownNumberDialog;
import ru.henridellal.dialer.util.ContactsUtil;
import ru.henridellal.dialer.util.ThemingUtil;

public class PhoneNumberActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>,
		View.OnClickListener {
	private LogEntryAdapter logAdapter;
	private String number;
	private AsyncContactImageLoader mAsyncContactImageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DialerApp.setTheme(this);
		setContentView(R.layout.phone_number_activity);
		Intent intent = getIntent();
		number = intent.getStringExtra(IntentExtras.PHONE_NUMBER);
		logAdapter = new LogEntryAdapter(this, null, null, true);
		((ListView)findViewById(R.id.number_log)).setAdapter(logAdapter);
		LoaderManager manager = getLoaderManager();
		manager.initLoader(0, null, this);
		manager.getLoader(0).forceLoad();
		findViewById(R.id.btn_cleanup).setOnClickListener(this);
		findViewById(R.id.contact_image).setOnClickListener(this);
		String contactName = ContactsUtil.getContactName(this, number);
		if (null != contactName) {
			((TextView)findViewById(R.id.secondary_text)).setText(number);
		} else {
			contactName = number;
		}
		((TextView)findViewById(R.id.main_text)).setText(contactName);
		loadContactImage();
	}

	private void loadContactImage() {
		if (PermissionManager.isPermissionGranted(this, Manifest.permission.READ_CONTACTS)) {
			mAsyncContactImageLoader = new AsyncContactImageLoader(this);
		} else {
			((ImageView) findViewById(R.id.contact_image)).setImageDrawable(ThemingUtil.getDefaultContactDrawable(this));
			return;
		}

		mAsyncContactImageLoader.loadDrawable(number, new AsyncContactImageLoader.ImageCallback() {
			@Override
			public void imageLoaded(final Drawable imageDrawable, final String number) {
				if (TextUtils.equals(number, PhoneNumberActivity.this.number)) {
					((ImageView) findViewById(R.id.contact_image)).setImageDrawable(imageDrawable);
				}
			}
		}, AsyncContactImageLoader.QUERY_TYPE_PHONE_NUMBER);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(
				this,
				Calls.CONTENT_URI,
				LogEntryAdapter.PROJECTION_FOR_NUMBER,
				"number=?",
				new String[] {number},
				Calls.DEFAULT_SORT_ORDER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.contact_image:
				Uri contactIdUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
				String[] projection = new String[] {(Build.VERSION.SDK_INT >= 24) ? PhoneLookup.CONTACT_ID : PhoneLookup._ID};
				Cursor cursor = getContentResolver().query(contactIdUri, projection, null, null, null);
				if (cursor == null || !cursor.moveToFirst()) {
					UnknownNumberDialog.show(this, number);
					return;
				}
				String contactId = cursor.getString(0);
				cursor.close();
				ContactsUtil.view(this, Contacts.CONTENT_URI, contactId);
				break;
			case R.id.btn_cleanup:
				CleanPhoneNumberLogDialog.show(this, number);
				break;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		logAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		logAdapter.swapCursor(null);
	}
}
