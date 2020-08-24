package ru.henridellal.dialer

import android.app.Activity
import android.app.Application
import android.preference.PreferenceManager

object DialerApp : Application() {
    const val LOG_TAG = "ru.henridellal.dialer"
    @JvmStatic
	fun setTheme(activity: Activity) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val theme = preferences.getString("theme", "light")
        if (theme == "light") {
            activity.setTheme(R.style.AppTheme_Dark)
        } else {
            activity.setTheme(R.style.AppTheme_Light)
        }
    }
}