package ru.henridellal.dialer.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.ContactsContract;

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
}
