package ru.henridellal.dialer.preference;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ru.henridellal.dialer.DialerApp;
import ru.henridellal.dialer.PermissionManager;

public class ContactSourcesPreference extends MultiSelectListPreference {

	public ContactSourcesPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (PermissionManager.isPermissionGranted(context, Manifest.permission.READ_CONTACTS)) {
			CharSequence[] sources = getContactSources(context);
			cleanCurrentValues(sources);
			setEntries(sources);
			setEntryValues(sources);
		}
	}

	@Override
	public boolean isEnabled() {
		return (PermissionManager.isPermissionGranted(getContext(), Manifest.permission.READ_CONTACTS))
				&& getEntries().length > 1;
	}

	public void cleanCurrentValues(CharSequence[] sources) {
		Set<String> values = new HashSet<String>();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
		values.addAll(sp.getStringSet(getKey(), new HashSet<String>()));
		List<CharSequence> s = Arrays.asList(sources);
		List<String> toBeRemoved = new ArrayList<String>();
		for (String v: values) {
			Log.e(DialerApp.LOG_TAG, v);
			if (!s.contains(v)) {
				toBeRemoved.add(v);
			}
		}
		values.removeAll(toBeRemoved);
		sp.edit().putStringSet(getKey(), values).commit();
	}

	public static CharSequence[] getContactSources(Context context) {
		Cursor sourcesCursor = context.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
				new String[] {ContactsContract.RawContacts.ACCOUNT_TYPE},
				ContactsContract.RawContacts.ACCOUNT_TYPE+" IS NOT NULL",
				null,
				null
		);
		Set<String> sources = new HashSet<String>();
		if (sourcesCursor != null) {
			sourcesCursor.moveToNext();
			while (!sourcesCursor.isAfterLast()) {
				sources.add(sourcesCursor.getString(0));
				sourcesCursor.moveToNext();
			}
			sourcesCursor.close();
		}
		CharSequence[] result = new CharSequence[sources.size()];
		sources.toArray(result);
		return result;
	}
}
