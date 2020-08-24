package ru.henridellal.dialer

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ru.henridellal.dialer.SpeedDial.getNumber
import java.lang.ref.SoftReference

class SpeedDialAdapter(context: Context) : BaseAdapter() {
    private val contextRef: SoftReference<Context> = SoftReference(context)
    fun update() {
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val view: View = convertView
                ?: LayoutInflater.from(contextRef.get()).inflate(R.layout.speed_dial_entry, null)
        val order = (position + 1).toString()
        (view.findViewById<View>(R.id.entry_order) as TextView).text = order
        val number = getNumber(contextRef.get(), order)
        val result: String?
        if (position == 0) {
            result = contextRef.get()!!.resources.getString(R.string.voice_mail)
            (view.findViewById<View>(R.id.entry_title) as TextView).text = result
            return view
        }
        var contactName: String? = null
        val cursor = contextRef.get()!!.contentResolver.query(Phone.CONTENT_URI, arrayOf(Phone.DISPLAY_NAME), Phone.NUMBER + "=?", arrayOf(number), null)
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                contactName = cursor.getString(0)
                cursor.close()
            }
        }
        result = if (null == contactName) {
            if (number != "") number else contextRef.get()!!.resources.getString(R.string.tap_for_addition)
        } else {
            "$contactName ($number)"
        }
        (view.findViewById<View>(R.id.entry_title) as TextView).text = result
        return view
    }

    override fun getCount(): Int {
        return 9
    }

}