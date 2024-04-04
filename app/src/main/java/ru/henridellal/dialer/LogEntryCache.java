package ru.henridellal.dialer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class LogEntryCache {
	public final ImageView callTypeImage;
	public final TextView callDate;

	public LogEntryCache(View view) {
		callTypeImage = (ImageView) view.findViewById(R.id.call_type_image);
		callDate = (TextView) view.findViewById(R.id.call_date);
	}
}
