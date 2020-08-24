package ru.henridellal.dialer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SpeedDial {
	
	private static final String KEY = "speed_dial_key_";
	
	public static String getNumber(Context context, String order) {
		return PreferenceManager.getDefaultSharedPreferences(context)
				.getString(KEY + order, "");
	}
	
	public static void setNumber(Context context, String order, String number) {
		if (number == null || number.length() == 0 || null == order) {
			return;
		}
		
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(KEY + order, number).apply();
	}
	
	public static void clearSlot(Context context, String order) {
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
		editor.putString(KEY + order, "").apply();
	}
	
}
