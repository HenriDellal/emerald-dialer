package ru.henridellal.dialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

public class PrivacyPolicyDialog {
	public static void show(final Activity activity, final SharedPreferences.Editor editor) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(activity.getResources().getString(R.string.privacy_policy_title));
		builder.setMessage(activity.getResources().getString(R.string.privacy_policy));
		builder.setPositiveButton(R.string.accept,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {
						editor.putBoolean("privacy_policy", true).commit();
					}
				});
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {
						activity.finish();
					}
				});

		builder.create().show();
	}
}
