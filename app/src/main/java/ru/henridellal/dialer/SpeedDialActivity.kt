package ru.henridellal.dialer

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.ListView
import android.widget.TextView
import ru.henridellal.dialer.DialerApp.setTheme
import ru.henridellal.dialer.SpeedDial.clearSlot
import ru.henridellal.dialer.SpeedDial.getNumber
import ru.henridellal.dialer.SpeedDial.setNumber

class SpeedDialActivity : Activity(), OnItemClickListener, OnItemLongClickListener {
    private lateinit var list: ListView
    var adapter: SpeedDialAdapter? = null
        private set

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setTheme(this)
        setContentView(R.layout.activity_speed_dial)
        adapter = SpeedDialAdapter(this)
        list = findViewById(R.id.speed_dial_entries)
        list.adapter = adapter
        list.onItemClickListener = this
        list.onItemLongClickListener = this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == PICK_CONTACT_NUMBER && resultCode == RESULT_OK) {
            val number = data.getStringExtra(CONTACT_NUMBER)
            val speedDialSlot = data.getStringExtra(SPEED_DIAL_SLOT)
            setNumber(this, speedDialSlot, number)
            adapter!!.update()
        }
    }

    private fun speedDialSlotDialog(order: String, contactInfo: String?) {
        val builder = AlertDialog.Builder(this)
        // TODO Fix localization with String.format
        builder.setTitle("$order: $contactInfo")
        val items = arrayOf(
                resources.getString(R.string.remove),
                resources.getString(R.string.make_a_call)
        )
        builder.setItems(items,
                DialogInterface.OnClickListener { di, which ->
                    when (which) {
                        0 -> {
                            clearSlot(this@SpeedDialActivity, order)
                            adapter!!.update()
                        }
                        1 -> {
                            //TODO avoid duplication, see DialerActivity.callNumber
                            if (TextUtils.isEmpty(contactInfo) || null == contactInfo) {
                                return@OnClickListener
                            }
                            val uri = Uri.parse("tel:" + Uri.encode(contactInfo))
                            val intent = Intent(Intent.ACTION_CALL, uri)
                            startActivity(intent)
                            finish()
                        }
                    }
                })
        builder.create().show()
    }

    private fun openSpeedDialPreferenceDialog(order: String) {
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.speed_dial_pref_dialog, null)
        builder.setView(dialogView)
        (dialogView.findViewById<View>(R.id.speed_dial_number_field) as TextView).text = getNumber(this, order)
        builder.setPositiveButton(android.R.string.ok
        ) { di, which ->
            setNumber(this@SpeedDialActivity, order, (dialogView.findViewById<View>(R.id.speed_dial_number_field) as TextView).text.toString())
            adapter!!.update()
        }
        builder.setNeutralButton(R.string.pick_contact_number
        ) { di, which ->
            val intent = Intent(this@SpeedDialActivity, PickContactNumberActivity::class.java)
            intent.putExtra(SPEED_DIAL_SLOT, order)
            startActivityForResult(intent, PICK_CONTACT_NUMBER)
        }
        builder.create().show()
    }

    override fun onItemClick(adapterView: AdapterView<*>?, view: View, position: Int, id: Long) {
        if (position == 0) {
            if (Build.VERSION.SDK_INT >= 23) {
                val intent = Intent(TelephonyManager.ACTION_CONFIGURE_VOICEMAIL)
                startActivity(intent)
            }
            return
        }
        val order = (view.findViewById<View>(R.id.entry_order) as TextView).text.toString()
        openSpeedDialPreferenceDialog(order)
    }

    override fun onItemLongClick(adapterView: AdapterView<*>?, view: View, position: Int, id: Long): Boolean {
        if (position == 0) {
            return false
        }
        val order = (view.findViewById<View>(R.id.entry_order) as TextView).text.toString()
        if (getNumber(this, order)!!.isNotEmpty()) {
            val speedDialData = (view.findViewById<View>(R.id.entry_title) as TextView).text.toString()
            speedDialSlotDialog(order, speedDialData)
            return true
        }
        return false
    }

    companion object {
        private const val PICK_CONTACT_NUMBER = 1
        const val CONTACT_NUMBER = "ru.henridellal.dialer.contact_number"
        const val SPEED_DIAL_SLOT = "ru.henridellal.dialer.speed_dial_slot"
    }
}