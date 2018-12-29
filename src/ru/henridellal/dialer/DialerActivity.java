package ru.henridellal.dialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ActivityNotFoundException;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog.Calls;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.Manifest;


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
				R.id.btn_add_contact, R.id.btn_remove_number,
				R.id.btn_toggle_numpad, R.id.btn_options,
				R.id.btn_call };
	private static final int CALL_LOG_MODE = 0;
	private static final int CONTACTS_MODE = 1;
	private static final String BUNDLE_KEY_NUMBER = "number";
	private static final String[] PERMISSIONS = {
		Manifest.permission.CALL_PHONE,
		Manifest.permission.READ_CALL_LOG,
		Manifest.permission.READ_CONTACTS,
		Manifest.permission.READ_PHONE_STATE,
		Manifest.permission.WRITE_CALL_LOG
	};
	
	private int mode;
	private AsyncContactImageLoader mAsyncContactImageLoader;
	private ContactsEntryAdapter contactsEntryAdapter;
	private EditText numberField;
	private ListView list;
	private LogEntryAdapter logEntryAdapter;
	private OnCallLogScrollListener onCallLogScrollListener;
	private TelephonyManager telephonyManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		DialerApp.setTheme(this);
		setContentView(R.layout.main);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		if (!preferences.getBoolean("privacy_policy", false)) {
			showPrivacyPolicyDialog(preferences.edit());
		}
		if (Build.VERSION.SDK_INT >= 23) {
			requestPermissions(PERMISSIONS, 0);
			for (int i = 0; i < 5; i++) {
				if (checkSelfPermission(PERMISSIONS[i]) == PackageManager.PERMISSION_GRANTED) {
					continue;
				} else {
					finish();
				}
			}
		}
		numberField = (EditText)findViewById(R.id.number_field);
		parseIntent(getIntent());
		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		setButtonListeners();
		numberField.setCursorVisible(false);
		numberField.requestFocus();
		numberField.addTextChangedListener(this);
		list = (ListView) findViewById(R.id.log_entries_list);
		onCallLogScrollListener = new OnCallLogScrollListener(this);
		list.setOnScrollListener(onCallLogScrollListener);
		TypedValue outValue = new TypedValue();
		getTheme().resolveAttribute(R.attr.drawableContactImage, outValue, true);
		int defaultContactImageId = outValue.resourceId;
		
		mAsyncContactImageLoader = new AsyncContactImageLoader(this, getResources().getDrawable(defaultContactImageId, getTheme()));
		logEntryAdapter = new LogEntryAdapter(this, null, mAsyncContactImageLoader);
		contactsEntryAdapter = new ContactsEntryAdapter(this, mAsyncContactImageLoader);
		list.setAdapter(logEntryAdapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
		
		if (getResources().getConfiguration().keyboard == Configuration.KEYBOARD_QWERTY) {
			numberField.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
			contactsEntryAdapter.setRawFiltering(true);
			findViewById(R.id.btn_toggle_numpad).setVisibility(View.INVISIBLE);
			findViewById(R.id.numpad).setVisibility(View.GONE);
		}
		
		getLoaderManager().initLoader(0, null, this);
		getLoaderManager().initLoader(1, null, this);
		getLoaderManager().getLoader(0).forceLoad();
		getLoaderManager().getLoader(1).forceLoad();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		bundle.putCharSequence(BUNDLE_KEY_NUMBER, numberField.getText());
		super.onSaveInstanceState(bundle);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle bundle) {
		CharSequence savedNumber = bundle.getCharSequence(BUNDLE_KEY_NUMBER);
		if (savedNumber != null || !TextUtils.isEmpty(savedNumber)) {
			numberField.setText(savedNumber.toString());
			numberField.setCursorVisible(true);
			numberField.setSelection(savedNumber.length());
		}
		super.onSaveInstanceState(bundle);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_numpad_0: addSymbolInNumber('0'); break;
			case R.id.btn_numpad_1: addSymbolInNumber('1'); break;
			case R.id.btn_numpad_2: addSymbolInNumber('2'); break;
			case R.id.btn_numpad_3: addSymbolInNumber('3'); break;
			case R.id.btn_numpad_4: addSymbolInNumber('4'); break;
			case R.id.btn_numpad_5: addSymbolInNumber('5'); break;
			case R.id.btn_numpad_6: addSymbolInNumber('6'); break;
			case R.id.btn_numpad_7: addSymbolInNumber('7'); break;
			case R.id.btn_numpad_8: addSymbolInNumber('8'); break;
			case R.id.btn_numpad_9: addSymbolInNumber('9'); break;
			case R.id.btn_numpad_star: addSymbolInNumber('*'); break;
			case R.id.btn_numpad_hash: addSymbolInNumber('#'); break;
			case R.id.btn_remove_number: removeSymbolInNumber(); break;
			case R.id.btn_toggle_numpad: toggleNumpad(); break;
			case R.id.btn_call: callNumber(numberField.getText().toString()); break;
			case R.id.btn_add_contact: createContact(numberField.getText().toString()); break;
			case R.id.btn_options: showPopupMenu(findViewById(R.id.btn_options)); break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
			Object focusedViewTag = getCurrentFocus().getTag();
			String number;
			if (focusedViewTag instanceof LogEntryCache) {
				LogEntryCache tag = (LogEntryCache) focusedViewTag;
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

	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		for (int i = 0; i < 5; i++) {
			if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
				finish();
				break;
			}
		}
	}
	
	private void parseIntent(Intent intent) {
		if (Intent.ACTION_VIEW.equals(intent.getAction())
				|| Intent.ACTION_DIAL.equals(intent.getAction())) {
			Uri data = intent.getData();
			if (data != null) {
				String scheme = data.getScheme();
				if (scheme != null && scheme.equals("tel")) {
					String number = data.getSchemeSpecificPart();
					if (number != null) {
						dialNumber(number);
					}
				}
			}
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
	public boolean isNumpadVisible() {
		View panel = findViewById(R.id.panel_number_input);
		return panel.getVisibility() == View.VISIBLE;
	}

	private void toggleNumpad() {
		if (isNumpadVisible()) {
			hideNumpad();
		} else {
			showNumpad();
		}
	}
	
	public void hideNumpad() {
		findViewById(R.id.panel_number_input).setVisibility(View.GONE);
		findViewById(R.id.numpad).setVisibility(View.GONE);
		findViewById(R.id.btn_call).setVisibility(View.INVISIBLE);
	}
	
	private void showNumpad() {
		findViewById(R.id.panel_number_input).setVisibility(View.VISIBLE);
		findViewById(R.id.numpad).setVisibility(View.VISIBLE);
		findViewById(R.id.btn_call).setVisibility(View.VISIBLE);
	}
	
	private void callNumber(String number) {
		if (TextUtils.isEmpty(number) || null == number) {
			return;
		}
		
		Uri uri = Uri.parse("tel:" + Uri.encode(number));
		Intent intent = new Intent(Intent.ACTION_CALL, uri);
		startActivity(intent);
		finish();
	}
	
	public void createContact(String number) {
		Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION, ContactsContract.Contacts.CONTENT_URI);
		intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
		try {
			startActivity(intent);
		} catch (ActivityNotFoundException e) {
			showMissingContactsAppDialog();
		}
	}
	
	private void dialNumber(String number) {
		showNumpad();
		numberField.setText(number);
		numberField.setCursorVisible(true);
	}
	
	private void setContactsMode() {
		if (mode == CONTACTS_MODE)
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
	
	private void clearCallLog() {
		getContentResolver().delete(Calls.CONTENT_URI, null, null);
		logEntryAdapter.update();
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

	private void showPrivacyPolicyDialog(final SharedPreferences.Editor editor) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.privacy_policy_title));
		builder.setMessage(getResources().getString(R.string.privacy_policy));
		builder.setPositiveButton(R.string.accept,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					editor.putBoolean("privacy_policy", true).commit();
				}
			});
		builder.setNegativeButton(android.R.string.no,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					finish();
				}
			});
		
		builder.create().show();
	}
	
	private void clearCallLogDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.clear_call_log_question));
		builder.setPositiveButton(android.R.string.yes,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					clearCallLog();
				}
			});
		builder.setNegativeButton(android.R.string.no,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					
				}
			});
		
		builder.create().show();
	}
	
	private void deleteCallLogEntry(long id) {
		getContentResolver().delete(Calls.CONTENT_URI, Calls._ID+"=?", new String[]{((Long)id).toString()});
		logEntryAdapter.update();
	}
	
	private void deleteCallLogEntryDialog(final long id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.delete_call_log_entry_question));
		builder.setPositiveButton(android.R.string.yes,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					deleteCallLogEntry(id);
				}
			});
		builder.setNegativeButton(android.R.string.no,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					
				}
			});
		
		builder.create().show();
	}
	
	public void showMissingContactsAppDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.contacts_app_is_missing));
		builder.setPositiveButton(android.R.string.yes,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
				}
			});
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
			deleteCallLogEntryDialog(id);
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
			setCallLogMode();
			contactsEntryAdapter.resetFilter();
			list.setSelection(0);
		} else if (number.equals("*#06#")) {
			showDeviceId();
		} else if (number.startsWith("*#*#") && number.endsWith("#*#*")) {
			String secretCode = new StringBuilder(number).substring(4, number.length()-4);
			sendBroadcast(new Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://" + secretCode)));
		} else {
			setContactsMode();
			contactsEntryAdapter.getFilter().filter(s);
			list.setSelection(0);
		}
	}
	
	private void showPopupMenu(View view) {
		PopupMenu popup = new PopupMenu(this, view);
		popup.setOnMenuItemClickListener(this);
		popup.inflate(R.menu.dialer_options);
		popup.show();
	}
	
	@Override
	public boolean onMenuItemClick(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.clear_call_log:
				clearCallLogDialog();
				return true;
			case R.id.fast_dial_preferences:
				startActivity(new Intent(this, SpeedDialActivity.class));
				return true;
			case R.id.dialer_preferences:
				startActivity(new Intent(this, DialerPreferencesActivity.class));
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public boolean onLongClick(View view) {
		int id = view.getId();
		switch (id) {
			case R.id.btn_numpad_0: addSymbolInNumber('+'); break;
			case R.id.btn_numpad_1:
				String voiceMailNumber = telephonyManager.getVoiceMailNumber();
				if (null != voiceMailNumber) {
					callNumber(voiceMailNumber);
				} else {
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
			return new CursorLoader(this, Phone.CONTENT_URI, ContactsEntryAdapter.PROJECTION, Phone.HAS_PHONE_NUMBER+"=1", null, Phone.DISPLAY_NAME);
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
