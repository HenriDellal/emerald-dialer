package ru.henridellal.dialer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactsEntryCache {
	public final ImageView contactImage;
	public final TextView contactName;
	public final TextView phoneNumber;

	public ContactsEntryCache(View view) {
		contactImage = (ImageView) view.findViewById(R.id.contact_entry_image);
		contactName = (TextView) view.findViewById(R.id.contact_entry_name);
		phoneNumber = (TextView) view.findViewById(R.id.contact_phone_number);
	}
}
