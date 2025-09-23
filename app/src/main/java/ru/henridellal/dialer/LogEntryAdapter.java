package ru.henridellal.dialer;

import static android.content.Context.TELEPHONY_SUBSCRIPTION_SERVICE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.CallLog.Calls;
import android.telephony.PhoneNumberUtils;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ru.henridellal.dialer.AsyncContactImageLoader.ImageCallback;
import ru.henridellal.dialer.util.DateUtil;
import ru.henridellal.dialer.util.RTLUtil;
import ru.henridellal.dialer.util.ThemingUtil;

public class LogEntryAdapter extends CursorAdapter implements View.OnClickListener
{
	public static final String[] PROJECTION = {
			Calls._ID,
			Calls.DATE,
			Calls.TYPE,
			Calls.DURATION,
			Calls.PHONE_ACCOUNT_ID,
			Calls.CACHED_NAME,
			Calls.NUMBER
	};

	public static final String[] PROJECTION_FOR_NUMBER = {
			Calls._ID,
			Calls.DATE,
			Calls.TYPE,
			Calls.DURATION,
			Calls.PHONE_ACCOUNT_ID
	};

	private static final int LOG_ENTRY_LAYOUT_ID = R.layout.contact_log_entry;
	private static final int PHONE_NUMBER_LOG_ENTRY_LAYOUT_ID = R.layout.phone_number_log_entry;

	public static final int COLUMN_DATE = 1;
	public static final int COLUMN_TYPE = 2;
	public static final int COLUMN_DURATION = 3;
	public static final int COLUMN_PHONE_ACCOUNT_ID = 4;
	public static final int COLUMN_NAME = 5;
	public static final int COLUMN_NUMBER = 6;

	private static Map<Integer, Integer> callTypes = new HashMap<Integer, Integer>();
	private static Map<Integer, Integer> callTypeStrings = new HashMap<Integer, Integer>();

	static {
		callTypes.put(Calls.INCOMING_TYPE, R.attr.drawableCallIncoming);
		callTypeStrings.put(Calls.INCOMING_TYPE, R.string.call_type_incoming);
		callTypes.put(Calls.MISSED_TYPE, R.attr.drawableCallMissed);
		callTypeStrings.put(Calls.MISSED_TYPE, R.string.call_type_missed);
		callTypes.put(Calls.OUTGOING_TYPE, R.attr.drawableCallOutgoing);
		callTypeStrings.put(Calls.OUTGOING_TYPE, R.string.call_type_outgoing);
		if (Build.VERSION.SDK_INT >= 24) {
			callTypes.put(Calls.REJECTED_TYPE, R.attr.drawableCallRejected);
			callTypeStrings.put(Calls.REJECTED_TYPE, R.string.call_type_rejected);
		}
	}
	private Map<Integer, Integer> callTypeDrawableIds;
	
	private AsyncContactImageLoader mAsyncContactImageLoader;
	private Drawable defaultContactDrawable;
	private SoftReference<Context> contextRef;
	private boolean isRtlLayout, isSingleNumberLog, loadContactImages;
	private List<SubscriptionInfo> subscriptionInfoList;
	private Map<String, BitmapDrawable> subIdToIconMap;

	@SuppressLint("MissingPermission")
	public LogEntryAdapter(
			Context context,
			Cursor cursor,
			AsyncContactImageLoader loader,
			boolean isSingleNumberLog
	) {
		super(context, cursor, 0);
		contextRef = new SoftReference<Context>(context);
		subIdToIconMap = new HashMap<>();

		if (Build.VERSION.SDK_INT >= 22) {
			SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE);
			subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
			if (subscriptionInfoList != null) {
				for (SubscriptionInfo s : subscriptionInfoList) {
					subIdToIconMap.put(
							String.valueOf(s.getSubscriptionId()),
							new BitmapDrawable(context.getResources(),
									s.createIconBitmap(context))
					);
				}
			}
		}
		mAsyncContactImageLoader = loader;
		loadContactImages = null != mAsyncContactImageLoader;
		if (!loadContactImages) {
			defaultContactDrawable = ThemingUtil.getDefaultContactDrawable(context);
		}
		TypedValue tv = new TypedValue();
		Resources.Theme theme = context.getTheme();
		callTypeDrawableIds = new HashMap<Integer, Integer>();
		for (Map.Entry<Integer, Integer> entry : callTypes.entrySet()) {
			theme.resolveAttribute(entry.getValue(), tv, true);
			callTypeDrawableIds.put(entry.getKey(), tv.resourceId);
		}

		isRtlLayout = RTLUtil.isRtlLayout();
		this.isSingleNumberLog = isSingleNumberLog;
	}
	
	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.contact_image) {
			String number = (String)view.getTag();
			if (null == number) {
				return;
			}
			Intent intent = new Intent(contextRef.get(), PhoneNumberActivity.class);
			intent.putExtra(IntentExtras.PHONE_NUMBER, number);

			contextRef.get().startActivity(intent);
		}
	}
	
	public void update() {
		notifyDataSetChanged();
	}
	
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view;
		if (isSingleNumberLog) {
			view = LayoutInflater.from(context).inflate(PHONE_NUMBER_LOG_ENTRY_LAYOUT_ID, null);
			PhoneNumberLogEntryCache viewCache = new PhoneNumberLogEntryCache(view);
			view.setTag(viewCache);
		} else {
			view = LayoutInflater.from(context).inflate(LOG_ENTRY_LAYOUT_ID, null);
			CallLogEntryCache viewCache = new CallLogEntryCache(view);
			view.setTag(viewCache);
		}
		return view;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (isSingleNumberLog) {
			bindPhoneNumberLogView(view, context, cursor);
		} else {
			bindLogView(view, context, cursor);
		}
	}

	private void setSimCardImage(Cursor cursor, LogEntryCache viewCache) {
		if (Build.VERSION.SDK_INT >= 22) {
			String phoneAccountId = cursor.getString(COLUMN_PHONE_ACCOUNT_ID);
			for (String subId: subIdToIconMap.keySet()) {
				if (phoneAccountId.startsWith(subId)) {
					viewCache.callSimCard.setImageDrawable(subIdToIconMap.get(subId));
					break;
				}
			}
		}
	}

	private void bindPhoneNumberLogView(View view, Context context, Cursor cursor) {
		final PhoneNumberLogEntryCache viewCache = (PhoneNumberLogEntryCache) view.getTag();
		if (viewCache == null) {
			return;
		}

		long date = cursor.getLong(COLUMN_DATE);
		viewCache.callDate.setText(DateUtil.getCallDateText(context, date));

		int callType = cursor.getInt(COLUMN_TYPE);
		setCallTypeForView(context, viewCache, callType);

		String detailedInfo;
		switch (callType) {
			case Calls.MISSED_TYPE:
			case Calls.REJECTED_TYPE:
				detailedInfo = context.getResources().getString(callTypeStrings.get(callType));
				break;
			case Calls.INCOMING_TYPE:
			case Calls.OUTGOING_TYPE:
				long duration = cursor.getInt(COLUMN_DURATION);
				detailedInfo = String.format("%1s, %2s: %3s",
						context.getResources().getString(callTypeStrings.get(callType)),
						context.getResources().getString(R.string.duration).toLowerCase(),
						DateUtils.formatElapsedTime(duration)
				);
				break;
			default:
				detailedInfo = context.getResources().getString(R.string.call_type_other);
		}
		viewCache.callDetailedInfo.setText(detailedInfo);
		setSimCardImage(cursor, viewCache);
	}

	private void bindLogView(View view, Context context, Cursor cursor) {
		final CallLogEntryCache viewCache = (CallLogEntryCache) view.getTag();
		if (viewCache == null) {
			return;
		}
		String name = cursor.getString(COLUMN_NAME);
		String phoneNumber = cursor.getString(COLUMN_NUMBER);
		if (null == phoneNumber) {
			phoneNumber = "";
		}

		String formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().getCountry());
		if (isRtlLayout) {
			formattedNumber = RTLUtil.format(formattedNumber);
		}
		if (!TextUtils.isEmpty(name)) {
			viewCache.contactName.setText(name);
			viewCache.phoneNumber.setText(formattedNumber);
		} else if (!TextUtils.isEmpty(formattedNumber)) {
			viewCache.contactName.setText(formattedNumber);
			viewCache.phoneNumber.setText("");
		} else {
			viewCache.contactName.setText("no number");
			viewCache.phoneNumber.setText("");
		}
		long date = cursor.getLong(COLUMN_DATE);
		viewCache.callDate.setText(DateUtil.getCallDateText(context, date));
		int callType = cursor.getInt(COLUMN_TYPE);
		setCallTypeForView(context, viewCache, callType);

		viewCache.contactImage.setTag(phoneNumber); // set a tag for the callback to be able to check, so we don't set the contact image of a reused view
		viewCache.contactImage.setImageDrawable(getContactImageDrawable(phoneNumber, viewCache));
		if (!phoneNumber.isEmpty()) {
			viewCache.contactImage.setOnClickListener(this);
		}

		if (callType == Calls.INCOMING_TYPE || callType == Calls.OUTGOING_TYPE) {
			viewCache.callDuration.setText(
					DateUtils.formatElapsedTime(cursor.getInt(COLUMN_DURATION))
			);
		} else {
			viewCache.callDuration.setText("");
		}

		setSimCardImage(cursor, viewCache);
	}
	
	public String getPhoneNumber(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		return cursor.getString(COLUMN_NUMBER);
	}

	public String getName(int position) {
		Cursor cursor = getCursor();
		cursor.moveToPosition(position);
		return cursor.getString(COLUMN_NAME);
	}

	private int getCallTypeDrawableId(int type) {
		return (callTypeDrawableIds.containsKey(type)) ?
			callTypeDrawableIds.get(type) : 0;
	}

	public void setCallTypeForView(Context context, LogEntryCache viewCache, int callType) {
		int callTypeDrawableId = getCallTypeDrawableId(callType);

		if (callTypeDrawableId != 0) {
			viewCache.callTypeImage.setImageDrawable(context.getResources().getDrawable(callTypeDrawableId, context.getTheme()));
		}
	}

	private Drawable getContactImageDrawable(String phoneNumber, final CallLogEntryCache viewCache) {
		return (loadContactImages) ?
				mAsyncContactImageLoader.loadDrawable(phoneNumber, new ImageCallback() {

					@Override
					public void imageLoaded(Drawable imageDrawable, String number) {
						if (TextUtils.equals(number, (String) viewCache.contactImage.getTag())) {
							viewCache.contactImage.setImageDrawable(imageDrawable);
						}
					}
				}, AsyncContactImageLoader.QUERY_TYPE_PHONE_NUMBER)
				: defaultContactDrawable;
	}
}
