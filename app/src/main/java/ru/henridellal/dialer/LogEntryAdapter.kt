package ru.henridellal.dialer

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.provider.CallLog.Calls
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import android.telephony.PhoneNumberUtils
import android.text.TextUtils
import android.text.format.DateUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CursorAdapter
import ru.henridellal.dialer.AsyncContactImageLoader.ImageCallback
import java.lang.ref.SoftReference
import java.text.DateFormat
import java.util.*

class LogEntryAdapter(activity: DialerActivity, cursor: Cursor?, loader: AsyncContactImageLoader) : CursorAdapter(activity, cursor, 0), View.OnClickListener {
    private val mAsyncContactImageLoader: AsyncContactImageLoader = loader
    private val callMadeDrawableId: Int
    private val callReceivedDrawableId: Int
    private val activityRef: SoftReference<DialerActivity> = SoftReference(activity)
    override fun onClick(view: View) {
        if (view.id == R.id.contact_image) {
            val number = view.tag as String ?: return
            val contactIdUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
            val projection = arrayOf(if (Build.VERSION.SDK_INT >= 24) PhoneLookup.CONTACT_ID else PhoneLookup._ID)
            val cursor = activityRef.get()!!.contentResolver.query(contactIdUri, projection, null, null, null)
            if (cursor == null || !cursor.moveToFirst()) {
                unknownNumberDialog(number)
                return
            }
            val contactId = cursor.getString(0)
            cursor.close()
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId)
            intent.setDataAndType(uri, "vnd.android.cursor.dir/contact")
            try {
                activityRef.get()!!.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                activityRef.get()!!.showMissingContactsAppDialog()
            }
        }
    }

    fun update() {
        notifyDataSetChanged()
    }

    private fun unknownNumberDialog(number: String) {
        val builder = AlertDialog.Builder(activityRef.get())
        builder.setTitle(PhoneNumberUtils.formatNumber(number, Locale.getDefault().country))
        val items = arrayOf(activityRef.get()!!.resources.getString(R.string.send_message),
                activityRef.get()!!.resources.getString(R.string.create_contact))
        builder.setItems(items
        ) { di, which ->
            val intent: Intent
            when (which) {
                0 -> {
                    intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("smsto:$number")
                    try {
                        activityRef.get()!!.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                    }
                }
                1 -> try {
                    activityRef.get()!!.createContact(number)
                } catch (e: ActivityNotFoundException) {
                    activityRef.get()!!.showMissingContactsAppDialog()
                }
            }
        }
        builder.create().show()
    }

    override fun newView(context: Context, cursor: Cursor, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.contact_log_entry, null)
        val viewCache = LogEntryCache(view)
        view.tag = viewCache
        return view
    }

    override fun bindView(view: View, context: Context, cursor: Cursor) {
        val viewCache = view.tag as LogEntryCache ?: return
        val name = cursor.getString(COLUMN_NAME)
        var phoneNumber = cursor.getString(COLUMN_NUMBER)
        if (null == phoneNumber) {
            phoneNumber = ""
        }
        val formattedNumber = PhoneNumberUtils.formatNumber(phoneNumber, Locale.getDefault().country)
        if (!TextUtils.isEmpty(name)) {
            viewCache.contactName.text = name
            viewCache.phoneNumber.text = formattedNumber
        } else if (!TextUtils.isEmpty(formattedNumber)) {
            viewCache.contactName.text = formattedNumber
            viewCache.phoneNumber.text = ""
        } else {
            viewCache.contactName.text = "no number"
            viewCache.phoneNumber.text = ""
        }
        val date = cursor.getLong(COLUMN_DATE)
        viewCache.callDate.text = DateUtils.formatSameDayTime(date, System.currentTimeMillis(), DateFormat.MEDIUM, DateFormat.SHORT)
        val id = cursor.getInt(COLUMN_TYPE)
        var callTypeDrawableId = 0
        when (id) {
            Calls.INCOMING_TYPE -> callTypeDrawableId = callReceivedDrawableId
            Calls.OUTGOING_TYPE -> callTypeDrawableId = callMadeDrawableId
            Calls.MISSED_TYPE -> callTypeDrawableId = R.drawable.ic_call_missed
        }
        if (callTypeDrawableId != 0) {
            viewCache.callTypeImage.setImageDrawable(context.resources.getDrawable(callTypeDrawableId, context.theme))
        }
        viewCache.contactImage.tag = phoneNumber // set a tag for the callback to be able to check, so we don't set the contact image of a reused view
        val d = mAsyncContactImageLoader.loadDrawableForNumber(phoneNumber, object : ImageCallback {
            override fun imageLoaded(imageDrawable: Drawable?, phoneNumber: String?) {
                if (TextUtils.equals(phoneNumber, viewCache.contactImage.tag as String)) {
                    viewCache.contactImage.setImageDrawable(imageDrawable)
                }
            }
        })
        viewCache.contactImage.setImageDrawable(d)
        if (phoneNumber.isEmpty()) {
            return
        }
        viewCache.contactImage.setOnClickListener(this)
    }

    fun getPhoneNumber(position: Int): String {
        val cursor = cursor
        cursor.moveToPosition(position)
        return cursor.getString(COLUMN_NUMBER)
    }

    companion object {
        @JvmField
		val PROJECTION = arrayOf(
                Calls._ID,
                Calls.CACHED_NAME,
                Calls.NUMBER,
                Calls.DATE,
                Calls.TYPE
        )
        private const val COLUMN_NAME = 1
        private const val COLUMN_NUMBER = 2
        private const val COLUMN_DATE = 3
        private const val COLUMN_TYPE = 4
    }

    init {
        val outValue = TypedValue()
        activity.theme.resolveAttribute(R.attr.drawableCallMade, outValue, true)
        callMadeDrawableId = outValue.resourceId
        activity.theme.resolveAttribute(R.attr.drawableCallReceived, outValue, true)
        callReceivedDrawableId = outValue.resourceId
    }
}