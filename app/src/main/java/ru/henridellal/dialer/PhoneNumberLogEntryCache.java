package ru.henridellal.dialer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class PhoneNumberLogEntryCache extends LogEntryCache {
	public final TextView callDetailedInfo;

	public PhoneNumberLogEntryCache(View view) {
		super(view);
		callDetailedInfo = (TextView) view.findViewById(R.id.call_detailed_info);
	}
}
