package ru.henridellal.dialer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class DialerPreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
	
	private boolean restartTriggered;
	private SharedPreferences preferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		ListPreference themePreference = (ListPreference) findPreference("theme");
		themePreference.setSummary(themePreference.getEntry());
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		preferences.registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		preferences.unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onBackPressed() {
		if (restartTriggered) {
			startActivity(new Intent(this, DialerActivity.class));
			finishAffinity();
		} else {
			super.onBackPressed();
		}
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		Preference pref = findPreference(key);
		if (pref instanceof ListPreference) {
			ListPreference listPreference = (ListPreference) pref;
			listPreference.setSummary(listPreference.getEntry());
		}
	
		if ("theme".equals(key)
			|| "t9_locale".equals(key)) {
			restartTriggered = true;
		}
	}
}
