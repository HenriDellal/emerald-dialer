package ru.henridellal.dialer

import android.app.ListActivity
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.view.View
import android.widget.ListView
import ru.henridellal.dialer.DialerApp.setTheme

class PickContactNumberActivity : ListActivity() {
    private var speedDialSlot: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(this)
        val intent = intent
        speedDialSlot = intent.getStringExtra(SpeedDialActivity.SPEED_DIAL_SLOT)
        val contactsCursor = contentResolver.query(Phone.CONTENT_URI, PROJECTION, Phone.HAS_PHONE_NUMBER + "=1", null, Phone.DISPLAY_NAME)
        val pickContactNumberAdapter = PickContactNumberAdapter(this, contactsCursor, 0)
        listAdapter = pickContactNumberAdapter
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED, null)
        super.onBackPressed()
    }

    override fun onListItemClick(lv: ListView, view: View, position: Int, id: Long) {
        val number = view.tag as String
        val intent = Intent()
        intent.putExtra(SpeedDialActivity.CONTACT_NUMBER, number)
        intent.putExtra(SpeedDialActivity.SPEED_DIAL_SLOT, speedDialSlot)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        private val PROJECTION = arrayOf(
                Phone._ID,
                Phone.DISPLAY_NAME,
                Phone.NUMBER
        )
    }
}