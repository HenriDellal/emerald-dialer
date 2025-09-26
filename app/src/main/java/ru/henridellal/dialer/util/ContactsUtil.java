package ru.henridellal.dialer.util;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import ru.henridellal.dialer.PermissionManager;
import ru.henridellal.dialer.dialog.MissingContactsAppDialog;

public class ContactsUtil {
	public static void createContact(Context context, String number) {
		Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION, ContactsContract.Contacts.CONTENT_URI);
		intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			MissingContactsAppDialog.show(context);
		}
	}

	public static void open(Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW, ContactsContract.Contacts.CONTENT_URI);
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			MissingContactsAppDialog.show(context);
		}
	}

	public static void view(Context context, Uri contentUri, String contactId) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.withAppendedPath(contentUri, contactId);
		intent.setDataAndType(uri, "vnd.android.cursor.dir/contact");
		try {
			context.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			MissingContactsAppDialog.show(context);
		}
	}

	public static String getContactName(Context context, String number) {
		if (!PermissionManager.isPermissionGranted(context, Manifest.permission.READ_CONTACTS)) return null;
		String contactName = null;
		Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME}, ContactsContract.CommonDataKinds.Phone.NUMBER + "=?", new String[]{number}, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				contactName = cursor.getString(0);
				cursor.close();
			}
		}
		return contactName;
	}

	public static String getTypeLabel(Context c, int type) {
		if (c == null) return "";
		return (String) ContactsContract.CommonDataKinds.Phone.getTypeLabel(c.getResources(), type, null);
	}
}
