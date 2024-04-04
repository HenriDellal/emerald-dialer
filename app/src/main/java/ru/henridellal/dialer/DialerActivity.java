package ru.henridellal.dialer;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ru.henridellal.dialer.dialog.DeleteCallLogEntryDialog;
import ru.henridellal.dialer.util.ContactsUtil;
import ru.henridellal.dialer.util.DateUtil;

public class DialerActivity extends Activity implements View.OnClickListener, View.OnLongClickListener,
	LoaderManager.LoaderCallbacks<Cursor>, TextWatcher, AdapterView.OnItemClickListener,
	PopupMenu.OnMenuItemClickListener, AdapterView.OnItemLongClickListener
{
	private static final int[] buttonIds = new int[]{
				R.id.btn_numpad_0, R.id.btn_numpad_1,
				R.id.btn_numpad_2, R.id.btn_numpad_3,
				R.id.btn_numpad_4, R.id.btn_numpad_5,
				R.id.btn_numpad_6, R.id.btn_numpad_7,
				R.id.btn_numpad_8, R.id.btn_numpad_9,
				R.id.btn_numpad_star, R.id.btn_numpad_hash,
				R.id.btn_open_contacts,
				R.id.btn_add_contact, R.id.btn_remove_number,
				R.id.btn_toggle_numpad, R.id.btn_options,
				R.id.btn_call };

	private static final int[] numpadLettersIds = new int[] {
			R.string.numpad_2,
			R.string.numpad_3,
			R.string.numpad_4,
			R.string.numpad_5,
			R.string.numpad_6,
			R.string.numpad_7,
			R.string.numpad_8,
			R.string.numpad_9
	};

	private static final int CALL_LOG_MODE = 0;
	private static final int CONTACTS_MODE = 1;
	private static final String BUNDLE_KEY_NUMBER = "number";
	private static final String[] CALL_INFORMATION_PROJECTION = {
		Calls._ID,
		Calls.DATE,
		Calls.DURATION
	};

	private static final Pattern PHONE_NUMBER_PATTERN =
			Pattern.compile("[\\+]?\\d+ ?\\(?\\d*\\)?[ \\d.-]+");
	
	private int mode;
	private AsyncContactImageLoader mAsyncContactImageLoader;
	private ContactsEntryAdapter contactsEntryAdapter;
	private EditText numberField;
	private ListView list;
	private LogEntryAdapter logEntryAdapter;
	private OnCallLogScrollListener onCallLogScrollListener;
	private PopupMenu popup;
	private SharedPreferences preferences;
	private TelephonyManager telephonyManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		DialerApp.setTheme(this);
		setContentView(R.layout.main);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (!preferences.getBoolean("privacy_policy", false)) {
			PrivacyPolicyDialog.show(this, preferences.edit());
		}
		checkPermissions();
		numberField = (EditText)findViewById(R.id.number_field);
		numberField.setShowSoftInputOnFocus(false);
		numberField.setOnLongClickListener(this);
		parseIntent(getIntent());
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		setButtonListeners();
		numberField.setCursorVisible(false);
		numberField.requestFocus();
		numberField.addTextChangedListener(this);
		list = (ListView) findViewById(R.id.log_entries_list);
		onCallLogScrollListener = new OnCallLogScrollListener(this);

		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);

		String t9LocalePref = preferences.getString("t9_locale", "system");
		Context t9LocaleContext = getT9LocaleContext(t9LocalePref);
		if (null != t9LocaleContext) {
			Resources t9Resources = t9LocaleContext.getResources();
			String letters;

			for (int i = 2; i <= 9; i++) {
				letters = t9Resources.getString(numpadLettersIds[i - 2]);
				((NumpadButton)(findViewById(buttonIds[i]))).setLetters(letters);
			}
			T9Manager.getInstance().setLocale(new Locale(t9LocalePref, t9LocalePref));
		} else {
			T9Manager.getInstance().setLocale(Locale.getDefault());
		}
		T9Manager.getInstance().initPatterns(null != t9LocaleContext ?
				t9LocaleContext.getResources() :
				getResources());

		LoaderManager loaderManager = getLoaderManager();
		if (PermissionManager.isPermissionGranted(this, Manifest.permission.READ_CONTACTS)) {
			mAsyncContactImageLoader = new AsyncContactImageLoader(this);
			contactsEntryAdapter = new ContactsEntryAdapter(this, mAsyncContactImageLoader);
			if (T9Manager.getInstance().getLanguage().startsWith("zh")) {
				contactsEntryAdapter.setFilteringMode(ContactsEntryAdapter.FILTERING_MODE_PINYIN);
			}
			initContactsLoader(loaderManager);
		}

		if (PermissionManager.isPermissionGranted(this, Manifest.permission.READ_CALL_LOG)) {
			logEntryAdapter = new LogEntryAdapter(this, null, mAsyncContactImageLoader, false);
			list.setAdapter(logEntryAdapter);
			initCallLogLoader(loaderManager);
		}
		initPhysicalKeyboard();
		initPopupMenu();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		list.setOnScrollListener(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
		list.setOnScrollListener(isPortrait ? onCallLogScrollListener : null);
	}

	private void checkPermissions() {
		if (Build.VERSION.SDK_INT >= 23 && !PermissionManager.hasRequiredPermissions(this)) {
			requestPermissions(PermissionManager.PERMISSIONS, 0);
		}
	}

	private Context getT9LocaleContext(String t9LocalePref) {
		Context t9LocaleContext = null;
		if (!t9LocalePref.equals("system")) {
			Configuration t9Configuration = new Configuration(getResources().getConfiguration());
			t9Configuration.setLocale(new Locale(t9LocalePref, t9LocalePref));
			t9LocaleContext = createConfigurationContext(t9Configuration);
		}
		return t9LocaleContext;
	}

	private void initPhysicalKeyboard() {
		int keyboardType = getResources().getConfiguration().keyboard;
		if (keyboardType == Configuration.KEYBOARD_QWERTY) {
			numberField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			contactsEntryAdapter.setFilteringMode(ContactsEntryAdapter.FILTERING_MODE_RAW);
			findViewById(R.id.btn_toggle_numpad).setVisibility(View.INVISIBLE);
			findViewById(R.id.numpad).setVisibility(View.GONE);
		} else if (keyboardType == Configuration.KEYBOARD_12KEY) {
			findViewById(R.id.btn_toggle_numpad).setVisibility(View.INVISIBLE);
			findViewById(R.id.numpad).setVisibility(View.GONE);
		}
	}

	private void initCallLogLoader(LoaderManager manager) {
		manager.initLoader(0, null, this);
		manager.getLoader(0).forceLoad();
	}

	private void initContactsLoader(LoaderManager manager) {
		manager.initLoader(1, null, this);
		manager.getLoader(1).forceLoad();
	}

	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putCharSequence(BUNDLE_KEY_NUMBER, numberField.getText());
		super.onSaveInstanceState(bundle);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		CharSequence savedNumber = bundle.getCharSequence(BUNDLE_KEY_NUMBER);
		if (savedNumber != null && !TextUtils.isEmpty(savedNumber)) {
			numberField.setText(savedNumber.toString());
			numberField.setCursorVisible(true);
			numberField.setSelection(savedNumber.length());
		}
		super.onSaveInstanceState(bundle);
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.btn_numpad_0:
			case R.id.btn_numpad_1:
			case R.id.btn_numpad_2:
			case R.id.btn_numpad_3:
			case R.id.btn_numpad_4:
			case R.id.btn_numpad_5:
			case R.id.btn_numpad_6:
			case R.id.btn_numpad_7:
			case R.id.btn_numpad_8:
			case R.id.btn_numpad_9:
			case R.id.btn_numpad_star:
			case R.id.btn_numpad_hash:
				addSymbolInNumber(Numpad.getSymbol(id)); break;
			case R.id.btn_remove_number: removeSymbolInNumber(); break;
			case R.id.btn_toggle_numpad: Numpad.toggle(this); break;
			case R.id.btn_call: callNumber(numberField.getText().toString()); break;
			case R.id.btn_open_contacts: ContactsUtil.open(this); break;
			case R.id.btn_add_contact:
				String number = numberField.getText().toString();
				ContactsUtil.createContact(this, number);
				break;
			case R.id.btn_options: popup.show(); break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
			Object focusedViewTag = getCurrentFocus().getTag();
			String number;
			if (focusedViewTag instanceof CallLogEntryCache) {
				CallLogEntryCache tag = (CallLogEntryCache) focusedViewTag;
				number = tag.phoneNumber.getText().toString();
				if (number.length() == 0) {
					number = tag.contactName.getText().toString();
				}
			} else if (focusedViewTag instanceof ContactsEntryCache) {
				ContactsEntryCache tag = (ContactsEntryCache) focusedViewTag;
				number = tag.phoneNumber.getText().toString();
			} else {
				number = numberField.getText().toString(); 
			}
			callNumber(number);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void parseIntent(Intent intent) {
		Uri data;
		String action = intent.getAction();
		boolean isActionCorrect = Intent.ACTION_VIEW.equals(action) || Intent.ACTION_DIAL.equals(action);
		if (isActionCorrect
				&& null != (data = intent.getData())
				&& "tel".equals(data.getScheme())) {
			dialNumber(data.getSchemeSpecificPart());
		}
	}
	
	private void removeSymbolInNumber() {
		StringBuilder text = new StringBuilder(numberField.getText());
		if (text.length() == 0)
			return;
		int selectionStart = numberField.getSelectionStart();
		int selectionEnd = numberField.getSelectionEnd();
		if (selectionStart != selectionEnd) {
			text.delete(selectionStart, selectionEnd);
			numberField.setText(text);
			numberField.setSelection(selectionStart);
		} else {
			if (selectionStart == 0)
				return;
			text.deleteCharAt(selectionEnd-1);
			numberField.setText(text);
			numberField.setSelection(selectionStart-1);
		}
		if (text.length() == 0) {
			numberField.setCursorVisible(false);
		}
	}
	
	private void addSymbolInNumber(char symbol) {
		StringBuilder text = new StringBuilder(numberField.getText());
		int selectionStart = numberField.getSelectionStart();
		int selectionEnd = numberField.getSelectionEnd();
		if (selectionStart != selectionEnd) {
			text.delete(selectionStart, selectionEnd);
		}
		text.insert(selectionStart, symbol);
		numberField.setText(text);
		numberField.setSelection(selectionStart + 1);
		if (!numberField.isCursorVisible()) {
			numberField.setCursorVisible(true);
		}
	}
	
	private void clearNumber() {
		numberField.setText("");
		numberField.setCursorVisible(false);
	}
	
	private void setButtonListeners() {
		for (int i = 0; i < buttonIds.length; i++) {
			findViewById(buttonIds[i]).setOnClickListener(this);
			findViewById(buttonIds[i]).setOnLongClickListener(this);
		}
	}
	
	private void openMessagingApp(String number) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("smsto:" + number));
		try {
			startActivity(intent);
		} catch (Exception e) {}
	}

	private void callNumber(String number) {
		if (null == number || TextUtils.isEmpty(number)) {
			return;
		}
		if (!PermissionManager.isPermissionGranted(this, Manifest.permission.CALL_PHONE)) {
			Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_LONG).show();
			return;
		}
		
		Uri uri = Uri.parse("tel:" + Uri.encode(PhoneNumberUtils.normalizeNumber(number)));
		Intent intent = new Intent(Intent.ACTION_CALL, uri);
		startActivity(intent);
		finish();
	}
	
	private void dialNumber(String number) {
		if (null == number)
			return;

		Numpad.show(this);
		numberField.setText(number);
		numberField.setCursorVisible(true);
	}

	private void pasteNumber() {
		ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		try {
			String number = cm.getPrimaryClip().getItemAt(0).getText().toString().trim();
			Matcher match = PHONE_NUMBER_PATTERN.matcher(number);
			if (match.matches()) {
				numberField.setText(number);
			} else {
				Toast.makeText(this, "Not a phone number", Toast.LENGTH_LONG).show();
			}
		} catch (NullPointerException npe) {
			Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_LONG).show();
		}
	}
	
	private void setContactsMode() {
		if (mode == CONTACTS_MODE || null == contactsEntryAdapter)
			return;
		
		mode = CONTACTS_MODE;
		list.setAdapter(contactsEntryAdapter);
	}
	
	private void setCallLogMode() {
		if (mode == CALL_LOG_MODE)
			return;
		
		mode = CALL_LOG_MODE;
		list.setAdapter(logEntryAdapter);
	}
	
	public void clearCallLog() {
		if (PermissionManager.isPermissionGranted(this, Manifest.permission.WRITE_CALL_LOG)) {
			getContentResolver().delete(Calls.CONTENT_URI, null, null);
			logEntryAdapter.update();
		}
	}

	private void showDeviceId() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String deviceId;
		
		if (Build.VERSION.SDK_INT < 26) {
			deviceId = telephonyManager.getDeviceId();
		} else {
			switch (telephonyManager.getPhoneType()) {
			case TelephonyManager.PHONE_TYPE_GSM:
				deviceId = telephonyManager.getImei();
				break;
			case TelephonyManager.PHONE_TYPE_CDMA:
				deviceId = telephonyManager.getMeid();
				break;
			default:
				deviceId = "null";
			}
		}
		builder.setMessage(deviceId);
		builder.create().show();
	}
	
	private void showInformationDialog(long id) {
		Cursor cursor = getContentResolver().query(Calls.CONTENT_URI, CALL_INFORMATION_PROJECTION, Calls._ID+"=?", new String[]{((Long)id).toString()}, null);
		if (null == cursor || cursor.getCount() == 0)
			return;

		cursor.moveToNext();
		long date = cursor.getLong(1);
		long duration = cursor.getLong(2);

		DateFormat dateInstance = DateFormat.getDateInstance(DateUtil.getDateFormat(date));
		DateFormat timeInstance = DateFormat.getTimeInstance(DateFormat.SHORT);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String message = String.format("%1s: %2s, %3s\n%4s: %5s",
				getResources().getString(R.string.date), timeInstance.format(date),
				dateInstance.format(date), getResources().getString(R.string.duration),
				DateUtils.formatElapsedTime(duration));
		builder.setMessage(message);
		builder.setCancelable(true);
		builder.create().show();
		cursor.close();
	}

	public void deleteCallLogEntry(long id) {
		if (PermissionManager.isPermissionGranted(this, Manifest.permission.WRITE_CALL_LOG)) {
			getContentResolver().delete(Calls.CONTENT_URI, Calls._ID + "=?", new String[]{((Long) id).toString()});
			logEntryAdapter.update();
		}
	}

	private void showLogEntryDialog(final int position, final long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final String number = logEntryAdapter.getPhoneNumber(position);
		String formattedNumber = PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
		String name = logEntryAdapter.getName(position);
		if (null != name && !name.isEmpty()) {
			formattedNumber = String.format("%1s (%2s)", name, formattedNumber);
		}
		builder.setCancelable(true);
		builder.setTitle(formattedNumber);
		Resources res = getResources();
		String[] commands = new String[]{
			res.getString(R.string.show_info),
			res.getString(R.string.make_a_call),
			res.getString(R.string.send_message),
			res.getString(R.string.delete_log_entry),
			res.getString(R.string.copy_number)
		};
		ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, commands);
		DialogInterface.OnClickListener onDialogItemClick = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface di, int which) {
				switch (which) {
					case 0:
						showInformationDialog(id);
						break;
					case 1:
						callNumber(number);
						break;
					case 2:
						openMessagingApp(number);
						break;
					case 3:
						DeleteCallLogEntryDialog.show(DialerActivity.this, id);
						break;
					case 4:
						ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
						ClipData clip = ClipData.newPlainText("label", number);
						clipboard.setPrimaryClip(clip);
						break;
				}
			}
		};
		builder.setAdapter(dialogAdapter, onDialogItemClick);
		builder.create().show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (adapterView.getAdapter() instanceof LogEntryAdapter) {
			callNumber(logEntryAdapter.getPhoneNumber(position));
		} else if (adapterView.getAdapter() instanceof ContactsEntryAdapter) {
			callNumber(contactsEntryAdapter.getPhoneNumber(position));
		}
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (adapterView.getAdapter() instanceof LogEntryAdapter) {
			showLogEntryDialog(position, id);
			return true;
		}
		return false;
	}
	
	@Override
	public void afterTextChanged(Editable s) {}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		String number = s.toString();
		if (start == 0 && before == 0 && count == 0) {
			return;
		} else if (TextUtils.isEmpty(s) && before > 0) {
			findViewById(R.id.btn_add_contact).setVisibility(View.INVISIBLE);
			findViewById(R.id.btn_open_contacts).setVisibility(View.VISIBLE);
			setCallLogMode();
			if (null != contactsEntryAdapter)
				contactsEntryAdapter.resetFilter();
			list.setSelection(0);
		} else if (number.equals("*#06#")) {
			showDeviceId();
		} else if (number.startsWith("*#*#") && number.endsWith("#*#*")) {
			String secretCode = new StringBuilder(number).substring(4, number.length()-4);
			sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://" + secretCode)));
		} else {
			findViewById(R.id.btn_add_contact).setVisibility(View.VISIBLE);
			findViewById(R.id.btn_open_contacts).setVisibility(View.INVISIBLE);
			if (null != contactsEntryAdapter) {
				setContactsMode();
				contactsEntryAdapter.getFilter().filter(s);
				list.setSelection(0);
			}
		}
	}
	
	private void initPopupMenu() {
		popup = new PopupMenu(this, findViewById(R.id.btn_options));
		popup.setOnMenuItemClickListener(this);
		popup.inflate(R.menu.dialer_options);
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		Class<? extends Activity> activityClass;
		switch (item.getItemId()) {
			case R.id.clear_call_log:
				ClearCallLogDialog.show(this);
				return true;
			case R.id.fast_dial_preferences:
				activityClass = SpeedDialActivity.class;
				break;
			case R.id.dialer_preferences:
				activityClass = DialerPreferencesActivity.class;
				break;
			case R.id.dialer_about_screen:
				activityClass = AboutActivity.class;
				break;
			default:
				return false;
		}
		startActivity(new Intent(this, activityClass));
		return true;
	}
	
	@Override
	public boolean onLongClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.btn_numpad_0: addSymbolInNumber('+'); break;
			case R.id.btn_numpad_1:
				String voiceMailNumber;
				try {
					voiceMailNumber = telephonyManager.getVoiceMailNumber();
					if (null != voiceMailNumber) callNumber(voiceMailNumber);
				} catch (SecurityException exception) {
					Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show();
					addSymbolInNumber('1');
				}
				break;
			case R.id.btn_numpad_2: callNumber(SpeedDial.getNumber(this, "2")); break;
			case R.id.btn_numpad_3: callNumber(SpeedDial.getNumber(this, "3")); break;
			case R.id.btn_numpad_4: callNumber(SpeedDial.getNumber(this, "4")); break;
			case R.id.btn_numpad_5: callNumber(SpeedDial.getNumber(this, "5")); break;
			case R.id.btn_numpad_6: callNumber(SpeedDial.getNumber(this, "6")); break;
			case R.id.btn_numpad_7: callNumber(SpeedDial.getNumber(this, "7")); break;
			case R.id.btn_numpad_8: callNumber(SpeedDial.getNumber(this, "8")); break;
			case R.id.btn_numpad_9: callNumber(SpeedDial.getNumber(this, "9")); break;
			case R.id.btn_remove_number: clearNumber(); break;
			case R.id.number_field: pasteNumber(); break;
			default:
				return false;
		}
		return true;
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if (id == 0) {
			return new CursorLoader(this, Calls.CONTENT_URI, LogEntryAdapter.PROJECTION, null, null, Calls.DEFAULT_SORT_ORDER);
		} else {
			StringBuilder selection = new StringBuilder(Phone.HAS_PHONE_NUMBER).append("=1");
			Object[] accountTypesSources = preferences.getStringSet("contact_sources", new HashSet<String>()).toArray();
			if (accountTypesSources.length > 0) {
				selection.append(" AND ")
					.append(ContactsContract.RawContacts.ACCOUNT_TYPE)
					.append(" IN (");
				for (int i = 0; i < accountTypesSources.length; i++) {
					selection.append("\"")
						.append(((CharSequence)accountTypesSources[i]))
						.append("\"");
					if (i != accountTypesSources.length-1) {
						selection.append(", ");
					}
				}
				selection.append(")");
			}
			return new CursorLoader(this,
					Phone.CONTENT_URI,
					ContactsEntryAdapter.PROJECTION,
					selection.toString(),
					null,
					Phone.DISPLAY_NAME);
		}
	}
	
	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		int id = loader.getId();
		if (id == 0) {
			logEntryAdapter.swapCursor(data);
		} else {
			contactsEntryAdapter.setCursor(data);
		}
	}
	
	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		int id = loader.getId();
		if (id == 0) {
			logEntryAdapter.swapCursor(null);
		} else {
			contactsEntryAdapter.setCursor(null);
		}
	}
	
}
