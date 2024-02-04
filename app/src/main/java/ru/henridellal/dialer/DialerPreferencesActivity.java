package ru.henridellal.dialer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class DialerPreferencesActivity extends Activity {
	
	private static boolean restartTriggered;
	private SharedPreferences preferences;
	private DialerPreferenceFragment fragment;
	public static class DialerPreferenceFragment extends PreferenceFragment
			implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.preferences);
			ListPreference themePreference = (ListPreference) findPreference("theme");
			themePreference.setSummary(themePreference.getEntry());
		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			Preference pref = findPreference(key);
			if (pref instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) pref;
				listPreference.setSummary(listPreference.getEntry());
			}

			if ("theme".equals(key) || "t9_locale".equals(key) || "contact_sources".equals(key)) {
				restartTriggered = true;
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DialerApp.setTheme(this);
		fragment = new DialerPreferenceFragment();
		getFragmentManager().beginTransaction().replace(android.R.id.content, fragment).commit();
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		preferences.registerOnSharedPreferenceChangeListener(fragment);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		preferences.unregisterOnSharedPreferenceChangeListener(fragment);
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
	

}
