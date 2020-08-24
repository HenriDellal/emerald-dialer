package ru.henridellal.dialer

import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ContactsEntryCache(view: View) {
    @JvmField
	val contactImage: ImageView = view.findViewById<View>(R.id.contact_entry_image) as ImageView

    @JvmField
	val contactName: TextView = view.findViewById<View>(R.id.contact_entry_name) as TextView

    @JvmField
	val phoneNumber: TextView = view.findViewById<View>(R.id.contact_phone_number) as TextView

}