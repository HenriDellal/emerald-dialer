package ru.henridellal.dialer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import ru.henridellal.dialer.R;

public class MissingContactsAppDialog {
	public static void show(Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(context.getResources().getString(R.string.contacts_app_is_missing));
		builder.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {
					}
				});
		builder.create().show();
	}
}
