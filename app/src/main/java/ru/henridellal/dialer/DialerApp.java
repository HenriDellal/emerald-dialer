package ru.henridellal.dialer;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

public class DialerApp extends Application {
	
	public static final String LOG_TAG = "ru.henridellal.dialer";
	private static Map<String, Integer> themes = new HashMap<String, Integer>();
	static {
		themes.put("light", R.style.AppTheme_Light);
		themes.put("dark", R.style.AppTheme_Dark);
		themes.put("night", R.style.AppTheme_Night);
		themes.put("amoled", R.style.AppTheme_Amoled);
	}

	public static void setTheme(Activity activity) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
		String theme = preferences.getString("theme", "light");
		activity.setTheme(themes.get(theme));
	}

}
