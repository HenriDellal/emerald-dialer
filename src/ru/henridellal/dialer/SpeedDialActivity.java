package ru.henridellal.dialer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class SpeedDialActivity extends Activity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
	
	private static final int PICK_CONTACT_NUMBER = 1;
	public static final String CONTACT_NUMBER = "ru.henridellal.dialer.contact_number";
	public static final String SPEED_DIAL_SLOT = "ru.henridellal.dialer.speed_dial_slot";
	
	private ListView list;
	private SpeedDialAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		DialerApp.setTheme(this);
		setContentView(R.layout.activity_speed_dial);
		mAdapter = new SpeedDialAdapter(this);
		list = findViewById(R.id.speed_dial_entries);
		list.setAdapter(mAdapter);
		list.setOnItemClickListener(this);
		list.setOnItemLongClickListener(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PICK_CONTACT_NUMBER && resultCode == RESULT_OK && null != data) {
			String number = data.getStringExtra(CONTACT_NUMBER);
			String speedDialSlot = data.getStringExtra(SPEED_DIAL_SLOT);
			SpeedDial.setNumber(this, speedDialSlot, number);
			mAdapter.update();
		}
	}
	 
	public SpeedDialAdapter getAdapter() {
		return mAdapter;
	}
	
	private void clearSpeedDialSlotDialog(final String order, String contactInfo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(contactInfo);
		builder.setMessage(getResources().getString(R.string.remove_speed_dial_entry));
		builder.setPositiveButton(android.R.string.yes,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					SpeedDial.clearSlot(SpeedDialActivity.this, order);
					getAdapter().update();
				}
			});
		builder.setNegativeButton(android.R.string.no,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					
				}
			});
		
		builder.create().show();
	}
	
	private void openSpeedDialPreferenceDialog(final String order) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final View dialogView = LayoutInflater.from(this).inflate(R.layout.speed_dial_pref_dialog, null);
		builder.setView(dialogView);
		((TextView)dialogView.findViewById(R.id.speed_dial_number_field)).setText(SpeedDial.getNumber(this, order));
		builder.setPositiveButton(android.R.string.ok,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					SpeedDial.setNumber(SpeedDialActivity.this, order, ((TextView)dialogView.findViewById(R.id.speed_dial_number_field)).getText().toString());
					getAdapter().update();
				}
			});
		builder.setNeutralButton(R.string.pick_contact_number,
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface di, int which) {
					Intent intent = new Intent(SpeedDialActivity.this, PickContactNumberActivity.class);
					intent.putExtra(SPEED_DIAL_SLOT, order);
					startActivityForResult(intent, PICK_CONTACT_NUMBER);
				}
			});
		
		builder.create().show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (position == 0) {
			if (Build.VERSION.SDK_INT >= 23) {
				Intent intent = new Intent(TelephonyManager.ACTION_CONFIGURE_VOICEMAIL);
				startActivity(intent);
			}
			return;
		}
		String order = ((TextView)view.findViewById(R.id.entry_order)).getText().toString();
		openSpeedDialPreferenceDialog(order);
	}
	
	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
		if (position == 0) {
			return false;
		}
		String order = ((TextView)view.findViewById(R.id.entry_order)).getText().toString();
		if (SpeedDial.getNumber(this, order).length() != 0) {
			String speedDialData = ((TextView)view.findViewById(R.id.entry_title)).getText().toString();
			clearSpeedDialSlotDialog(order, speedDialData);
			return true;
		}
		return false;
	}
	
}
