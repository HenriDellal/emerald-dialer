package ru.henridellal.dialer;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DialerApp extends Application {
	
	public static final String LOG_TAG = "ru.henridellal.dialer";
	
	public static void setTheme(Activity activity) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = preferences.getString("theme", "light");
		if (theme.equals("light")) {
			activity.setTheme(R.style.AppTheme_Light);
		} else {
			activity.setTheme(R.style.AppTheme_Dark);
		}
	}

}
