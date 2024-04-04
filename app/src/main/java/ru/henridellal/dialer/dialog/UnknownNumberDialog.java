package ru.henridellal.dialer.dialog;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

import java.util.Locale;

import ru.henridellal.dialer.R;
import ru.henridellal.dialer.util.ContactsUtil;

public class UnknownNumberDialog {
	public static void show(final Context context, final String number) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry()));
		String[] items = new String[]
				{context.getResources().getString(R.string.send_message),
						context.getResources().getString(R.string.create_contact)};
		builder.setItems(items,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface di, int which) {
						Intent intent;
						switch(which) {
							case 0:
								intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse("smsto:" + number));
								try {
									context.startActivity(intent);
								} catch (ActivityNotFoundException e) {}
								break;
							case 1:
								ContactsUtil.createContact(context, number);
								break;
						}
					}
				});
		builder.create().show();
	}
}
