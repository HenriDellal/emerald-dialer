package ru.henridellal.dialer

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import android.widget.TextView

class PickContactNumberAdapter(context: Context?, cursor: Cursor?, flags: Int) : CursorAdapter(context, cursor, flags) {
    override fun bindView(view: View, context: Context, cursor: Cursor) {
        (view.findViewById<View>(R.id.pick_contact_name) as TextView).text = cursor.getString(1)
        val number = cursor.getString(2)
        (view.findViewById<View>(R.id.pick_phone_number) as TextView).text = number
        view.tag = number
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        return LayoutInflater.from(context).inflate(R.layout.pick_number_item, parent, false)
    }
}