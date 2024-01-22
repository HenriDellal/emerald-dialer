package ru.henridellal.dialer.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;

import ru.henridellal.dialer.DialerActivity;
import ru.henridellal.dialer.R;

public class DeleteCallLogEntryDialog {
	public static void show(final DialerActivity activity, final long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage(activity.getResources().getString(R.string.delete_call_log_entry_question));
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {
						activity.deleteCallLogEntry(id);
					}
				});
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {

					}
				});

		builder.create().show();
	}
}
