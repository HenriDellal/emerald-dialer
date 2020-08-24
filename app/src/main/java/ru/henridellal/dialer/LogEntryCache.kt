package ru.henridellal.dialer

import android.view.View
import android.widget.ImageView
import android.widget.TextView

class LogEntryCache(view: View) {
    @JvmField
	val contactImage: ImageView = view.findViewById<View>(R.id.contact_image) as ImageView

    @JvmField
	val contactName: TextView = view.findViewById<View>(R.id.contact_name) as TextView

    @JvmField
	val callTypeImage: ImageView = view.findViewById<View>(R.id.call_type_image) as ImageView

    @JvmField
	val phoneNumber: TextView = view.findViewById<View>(R.id.phone_number) as TextView

    @JvmField
	val callDate: TextView = view.findViewById<View>(R.id.call_date) as TextView

}