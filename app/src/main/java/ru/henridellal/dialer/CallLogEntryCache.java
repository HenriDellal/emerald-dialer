package ru.henridellal.dialer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class CallLogEntryCache extends LogEntryCache {

	public final TextView callDuration;
	public final ImageView contactImage;
	public final TextView contactName;
	public final TextView phoneNumber;

	public CallLogEntryCache(View view) {
		super(view);
		callDuration = (TextView) view.findViewById(R.id.call_duration);
		contactImage = (ImageView) view.findViewById(R.id.contact_image);
		contactName = (TextView) view.findViewById(R.id.contact_name);
		phoneNumber = (TextView) view.findViewById(R.id.phone_number);
	}
}
