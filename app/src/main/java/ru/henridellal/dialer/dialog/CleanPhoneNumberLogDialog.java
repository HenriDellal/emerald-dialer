package ru.henridellal.dialer.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.henridellal.dialer.CustomSpinnerAdapter;
import ru.henridellal.dialer.DateDiff;
import ru.henridellal.dialer.R;

public class CleanPhoneNumberLogDialog {
	public static String FROM = "from";
	public static String TO = "to";
	public static String PICK_DATE = "pick_date";
	public static void show(final Context context, final String number) {
		Resources res = context.getResources();
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.dialog_clean_log_title);
		final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_clean_number_log, null);
		dialogView.setTag(new HashMap<String, DateDiff>());
		DateDiff[] dateDiffsFrom = new DateDiff[] {
				new DateDiff("-", "oldest"),
				new DateDiff(null, "year_ago", Calendar.YEAR, 1),
				new DateDiff(null, "month_ago", Calendar.MONTH, 1),
				new DateDiff(null, "7_days_ago", Calendar.DAY_OF_MONTH, 7),
				new DateDiff(null, "day_ago", Calendar.DAY_OF_MONTH, 1),
				new DateDiff(res.getString(R.string.dialog_clean_log_pick_date), PICK_DATE)
		};
		for (DateDiff dd: dateDiffsFrom) {
			if (dd.getName() == null)
				dd.generateName(context);
		}
		CustomSpinnerAdapter fromAdapter = new CustomSpinnerAdapter(context, dateDiffsFrom);
		Spinner spinnerFrom = (Spinner)dialogView.findViewById(R.id.spinner_from);
		spinnerFrom.setAdapter(fromAdapter);
		spinnerFrom.setOnItemSelectedListener(
				new OnDateSelectedListener(FROM, dialogView, R.id.pick_oldest_date_row)
		);
		DateDiff[] dateDiffsTo = new DateDiff[] {
				new DateDiff("-", "newest"),
				new DateDiff(null, "day_ago", Calendar.DAY_OF_MONTH, 1),
				new DateDiff(null, "7_days_ago", Calendar.DAY_OF_MONTH, 7),
				new DateDiff(null, "month_ago", Calendar.MONTH, 1),
				new DateDiff(null, "year_ago", Calendar.YEAR, 1),
				new DateDiff(res.getString(R.string.dialog_clean_log_pick_date), PICK_DATE)
		};
		for (DateDiff dd: dateDiffsTo) {
			if (dd.getName() == null)
				dd.generateName(context);
		}
		CustomSpinnerAdapter toAdapter = new CustomSpinnerAdapter(context, dateDiffsTo);
		Spinner spinnerTo = (Spinner)dialogView.findViewById(R.id.spinner_to);
		spinnerTo.setAdapter(toAdapter);
		spinnerTo.setOnItemSelectedListener(
			new OnDateSelectedListener(TO, dialogView, R.id.pick_newest_date_row)
		);
		dialogView.findViewById(R.id.pick_oldest_date_row).setVisibility(View.GONE);
		dialogView.findViewById(R.id.pick_newest_date_row).setVisibility(View.GONE);
		Button buttonPickDateFrom = dialogView.findViewById(R.id.btn_pick_date_from);
		buttonPickDateFrom.setOnClickListener(
				new OnDateButtonClickListener(context, dialogView, FROM)
		);
		Button buttonPickDateTo = dialogView.findViewById(R.id.btn_pick_date_to);
		buttonPickDateTo.setOnClickListener(
				new OnDateButtonClickListener(context, dialogView, TO)
		);
		builder.setView(dialogView);
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Map<String, DateDiff> tag = (Map<String, DateDiff>) dialogView.getTag();
						DateDiff from = tag.get(FROM);
						DateDiff to = tag.get(TO);
						StringBuilder whereBuilder = new StringBuilder(CallLog.Calls.NUMBER).append("=?");

						if (!from.getId().equals("oldest")) {
							whereBuilder.append(" AND ")
									.append(CallLog.Calls.DATE)
									.append(" >= ")
									.append(from.getMillis());
						}
						if (!to.getId().equals("newest")) {
							whereBuilder.append(" AND ")
									.append(CallLog.Calls.DATE)
									.append(" <= ")
									.append(to.getMillis());
						}
						context.getContentResolver().delete(
								CallLog.Calls.CONTENT_URI,
								whereBuilder.toString(),
								new String[]{number});
					}
				}
		);
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}
}

class OnDateSelectedListener implements AdapterView.OnItemSelectedListener {
	private String key;
	private int rowId;
	private SoftReference<View> dialogViewRef;
	public OnDateSelectedListener(String key, View dialogView, int rowId) {
		this.key = key;
		this.rowId = rowId;
		dialogViewRef = new SoftReference<>(dialogView);
	}
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		View dateRow = dialogViewRef.get().findViewById(rowId);
		DateDiff dd = (DateDiff)view.getTag();
		if (dd.getId().equals(CleanPhoneNumberLogDialog.PICK_DATE)) {
			dateRow.setVisibility(View.VISIBLE);
		} else {
			dateRow.setVisibility(View.GONE);
		}
		Map<String, DateDiff> dateRange = (Map<String, DateDiff>)dialogViewRef.get().getTag();
		dateRange.put(key, dd);
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}
}

class OnDateButtonClickListener implements View.OnClickListener {
	private Context context;
	private SoftReference<View> dialogViewRef;
	private String key;
	public OnDateButtonClickListener(Context context, View dialogView, String key) {
		this.context = context;
		dialogViewRef = new SoftReference<>(dialogView);
		this.key = key;
	}

	@Override
	public void onClick(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		final DatePicker dp = new DatePicker(context);
		final int viewId = v.getId();

		builder.setView(dp);
		builder.setPositiveButton(android.R.string.yes,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int day = dp.getDayOfMonth();
						int month = dp.getMonth();
						int year = dp.getYear();
						int hours = 0, minutes = 0, seconds = 0; // 00:00:00
						if (CleanPhoneNumberLogDialog.TO.equals(key)) {
							hours = 23; minutes = 59; seconds = 59;
						}

						Calendar calendar = Calendar.getInstance();
						calendar.set(year, month, day, hours, minutes, seconds);
						long date = calendar.getTimeInMillis();
						Map<String, DateDiff> tag = (Map<String, DateDiff>) dialogViewRef.get().getTag();
						DateDiff dateDiff = tag.get(key);
						dateDiff.setMillis(date);
						tag.put(key, dateDiff);
						dialogViewRef.get().setTag(tag);
						String dateText = DateFormat.getInstance().format(new Date(date));
						((Button)dialogViewRef.get().findViewById(viewId)).setText(dateText);
					}
				}
		);
		builder.setNegativeButton(android.R.string.no,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.create().show();
	}
}
