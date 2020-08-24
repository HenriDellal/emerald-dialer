package ru.henridellal.dialer

import android.content.Context
import android.preference.PreferenceManager

object SpeedDial {
    private const val KEY = "speed_dial_key_"
    @JvmStatic
	fun getNumber(context: Context?, order: String): String? {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(KEY + order, "")
    }

    @JvmStatic
	fun setNumber(context: Context?, order: String?, number: String?) {
        if (number == null || number.isEmpty() || null == order) {
            return
        }
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(KEY + order, number).apply()
    }

    @JvmStatic
	fun clearSlot(context: Context?, order: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(KEY + order, "").apply()
    }
}