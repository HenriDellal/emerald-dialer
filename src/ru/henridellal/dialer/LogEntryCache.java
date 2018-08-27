package ru.henridellal.dialer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LogEntryCache {
	public final ImageView contactImage;
	public final TextView contactName;
	public final ImageView callTypeImage;
	public final TextView phoneNumber;
	public final TextView callDate;

	public LogEntryCache(View view) {
		contactImage = (ImageView) view.findViewById(R.id.contact_image);
		callTypeImage = (ImageView) view.findViewById(R.id.call_type_image);
		contactName = (TextView) view.findViewById(R.id.contact_name);
		phoneNumber = (TextView) view.findViewById(R.id.phone_number);
		callDate = (TextView) view.findViewById(R.id.call_date);
	}
}
