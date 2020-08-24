package ru.henridellal.dialer

import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.preference.ListPreference
import android.preference.PreferenceActivity
import android.preference.PreferenceManager

class DialerPreferencesActivity : PreferenceActivity(), OnSharedPreferenceChangeListener {
    private var restartTriggered = false
    private var preferences: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preferences)
        val themePreference = findPreference("theme") as ListPreference
        themePreference.summary = themePreference.entry
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
    }

    override fun onResume() {
        super.onResume()
        preferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferences!!.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onBackPressed() {
        if (restartTriggered) {
            startActivity(Intent(this, DialerActivity::class.java))
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }

    override fun onSharedPreferenceChanged(prefs: SharedPreferences, key: String) {
        val pref = findPreference(key)
        if (pref is ListPreference) {
            pref.summary = pref.entry
        }
        if ("theme" == key || "t9_locale" == key) {
            restartTriggered = true
        }
    }
}