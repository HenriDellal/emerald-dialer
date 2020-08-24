package ru.henridellal.dialer;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.ListView;

public class PickContactNumberActivity extends ListActivity {
	private String speedDialSlot;
	
	private static final String[] PROJECTION = new String[] {
		Phone._ID,
		Phone.DISPLAY_NAME,
		Phone.NUMBER
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DialerApp.setTheme(this);
		Intent intent = getIntent();
		speedDialSlot = intent.getStringExtra(SpeedDialActivity.SPEED_DIAL_SLOT);
		Cursor contactsCursor = getContentResolver().query(Phone.CONTENT_URI, PROJECTION, Phone.HAS_PHONE_NUMBER+"=1", null, Phone.DISPLAY_NAME);
		PickContactNumberAdapter pickContactNumberAdapter = new PickContactNumberAdapter(this, contactsCursor, 0);
		setListAdapter(pickContactNumberAdapter);
	}
	
	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED, null);
		super.onBackPressed();
	}
	
	@Override
	protected void onListItemClick(ListView lv, View view, int position, long id) {
		String number = (String) view.getTag();
		Intent intent = new Intent();
		intent.putExtra(SpeedDialActivity.CONTACT_NUMBER, number);
		intent.putExtra(SpeedDialActivity.SPEED_DIAL_SLOT, speedDialSlot);
		setResult(RESULT_OK, intent);
		finish();
	}
}
