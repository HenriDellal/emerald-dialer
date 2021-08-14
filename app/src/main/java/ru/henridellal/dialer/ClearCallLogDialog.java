package ru.henridellal.dialer;

import android.app.AlertDialog;
import android.content.DialogInterface;

public class ClearCallLogDialog {
	public static void show(final DialerActivity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(activity.getResources().getString(R.string.clear_call_log_question));
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {
						activity.clearCallLog();
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {

					}
				});

		builder.create().show();
	}
}
