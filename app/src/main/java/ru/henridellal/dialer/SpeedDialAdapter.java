package ru.henridellal.dialer;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.lang.ref.SoftReference;

public class SpeedDialAdapter extends BaseAdapter {
	
	private SoftReference<Context> contextRef;
	
	public SpeedDialAdapter(Context context) {
		super();
		contextRef = new SoftReference<Context>(context);
	}
	
	public void update() {
		notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int position) {
		return null;
	}
	
	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		
		if (convertView == null) {
			view = LayoutInflater.from(contextRef.get()).inflate(R.layout.speed_dial_entry, null);
		} else {
			view = convertView;
		}
		
		String order = (new Integer(position+1)).toString();
		((TextView)view.findViewById(R.id.entry_order)).setText(order);
		String number = SpeedDial.getNumber(contextRef.get(), order);
		String result;
		if (position == 0) {
			result = contextRef.get().getResources().getString(R.string.voice_mail);
			((TextView)view.findViewById(R.id.entry_title)).setText(result);
			return view;
		}
		String contactName = getContactName(number);
		if (null == contactName) {
			result = !number.equals("") ? number : contextRef.get().getResources().getString(R.string.tap_for_addition);
		} else {
			result = String.format("%s (%s)", contactName, number);
		}
		((TextView)view.findViewById(R.id.entry_title)).setText(result);
		return view;
	}

	private String getContactName(String number) {
		if (!PermissionManager.isPermissionGranted(contextRef.get(), Manifest.permission.READ_CONTACTS)) return null;
		String contactName = null;
		Cursor cursor = contextRef.get().getContentResolver().query(Phone.CONTENT_URI, new String[]{Phone.DISPLAY_NAME}, Phone.NUMBER + "=?", new String[]{number}, null);
		if (cursor != null) {
			if (cursor.getCount() > 0) {
				cursor.moveToFirst();
				contactName = cursor.getString(0);
				cursor.close();
			}
		}
		return contactName;
	}
	
	@Override
	public int getCount() {
		return 9;
	}
}
