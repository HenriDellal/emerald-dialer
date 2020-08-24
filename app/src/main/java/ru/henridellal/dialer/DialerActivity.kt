package ru.henridellal.dialer

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.LoaderManager.LoaderCallbacks
import android.content.*
import android.content.SharedPreferences.Editor
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.provider.CallLog.Calls
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.support.v7.app.AppCompatActivity
import android.telephony.TelephonyManager
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.TypedValue
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import ru.henridellal.dialer.DialerApp.setTheme
import ru.henridellal.dialer.SpeedDial.getNumber
import ru.henridellal.dialer.SpeedDialActivity
import java.text.DateFormat
import java.util.*

class DialerActivity : AppCompatActivity(), View.OnClickListener, OnLongClickListener, LoaderCallbacks<Cursor>, TextWatcher, OnItemClickListener, PopupMenu.OnMenuItemClickListener, OnItemLongClickListener {
    private var mode = 0
    private lateinit var mAsyncContactImageLoader: AsyncContactImageLoader
    private var contactsEntryAdapter: ContactsEntryAdapter? = null
    private var numberField: EditText? = null
    private var list: ListView? = null
    private var logEntryAdapter: LogEntryAdapter? = null
    private var onCallLogScrollListener: OnCallLogScrollListener? = null
    private var telephonyManager: TelephonyManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(this)
        setContentView(R.layout.main)
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if (!preferences.getBoolean("privacy_policy", false)) {
            showPrivacyPolicyDialog(preferences.edit())
        }
        if (Build.VERSION.SDK_INT >= 23 && !hasRequiredPermissions()) {
            requestPermissions(PERMISSIONS, 0)
            for (i in 0..4) {
                if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                    finish()
                }
            }
        }
        numberField = findViewById<View>(R.id.number_field) as EditText
        parseIntent(intent)
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager
        setButtonListeners()
        numberField!!.isCursorVisible = false
        numberField!!.requestFocus()
        numberField!!.addTextChangedListener(this)
        list = findViewById<View>(R.id.log_entries_list) as ListView
        onCallLogScrollListener = OnCallLogScrollListener(this)
        list!!.setOnScrollListener(onCallLogScrollListener)
        val outValue = TypedValue()
        theme.resolveAttribute(R.attr.drawableContactImage, outValue, true)
        val defaultContactImageId = outValue.resourceId
        mAsyncContactImageLoader = AsyncContactImageLoader(this, resources.getDrawable(defaultContactImageId, theme))
        logEntryAdapter = LogEntryAdapter(this, null, mAsyncContactImageLoader!!)
        list!!.adapter = logEntryAdapter
        list!!.onItemClickListener = this
        list!!.onItemLongClickListener = this
        val t9Locale = preferences.getString("t9_locale", "system")
        var t9LocaleContext: Context? = null
        if (t9Locale != "system") {
            val t9Configuration = resources.configuration
            t9Configuration.setLocale(Locale(t9Locale, t9Locale))
            t9LocaleContext = createConfigurationContext(t9Configuration)
            val t9Resources = t9LocaleContext.resources
            // For numpad buttons (2...9)
            val numpadLettersIds = intArrayOf(
                    R.string.numpad_2,
                    R.string.numpad_3,
                    R.string.numpad_4,
                    R.string.numpad_5,
                    R.string.numpad_6,
                    R.string.numpad_7,
                    R.string.numpad_8,
                    R.string.numpad_9
            )
            for (i in 2..9) {
                (findViewById<View>(buttonIds[i]) as NumpadButton)
                        .setLetters(t9Resources.getString(numpadLettersIds[i - 2]))
            }
        }
        contactsEntryAdapter = ContactsEntryAdapter(this, mAsyncContactImageLoader, t9LocaleContext)
        val keyboardType = resources.configuration.keyboard
        if (keyboardType == Configuration.KEYBOARD_QWERTY) {
            numberField!!.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            contactsEntryAdapter!!.setRawFiltering(true)
            findViewById<View>(R.id.btn_toggle_numpad).visibility = View.INVISIBLE
            findViewById<View>(R.id.numpad).visibility = View.GONE
        } else if (keyboardType == Configuration.KEYBOARD_12KEY) {
            findViewById<View>(R.id.btn_toggle_numpad).visibility = View.INVISIBLE
            findViewById<View>(R.id.numpad).visibility = View.GONE
        }
        loaderManager.initLoader(0, null, this)
        loaderManager.initLoader(1, null, this)
        loaderManager.getLoader<Any>(0).forceLoad()
        loaderManager.getLoader<Any>(1).forceLoad()
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        bundle.putCharSequence(BUNDLE_KEY_NUMBER, numberField!!.text)
        super.onSaveInstanceState(bundle)
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        val savedNumber = bundle.getCharSequence(BUNDLE_KEY_NUMBER)
        if (savedNumber != null || !TextUtils.isEmpty(savedNumber)) {
            numberField!!.setText(savedNumber.toString())
            numberField!!.isCursorVisible = true
            numberField!!.setSelection(savedNumber!!.length)
        }
        super.onSaveInstanceState(bundle)
    }

    override fun onClick(view: View?) {
        if (view != null) {
            when (view.id) {
                R.id.btn_numpad_0 -> addSymbolInNumber('0')
                R.id.btn_numpad_1 -> addSymbolInNumber('1')
                R.id.btn_numpad_2 -> addSymbolInNumber('2')
                R.id.btn_numpad_3 -> addSymbolInNumber('3')
                R.id.btn_numpad_4 -> addSymbolInNumber('4')
                R.id.btn_numpad_5 -> addSymbolInNumber('5')
                R.id.btn_numpad_6 -> addSymbolInNumber('6')
                R.id.btn_numpad_7 -> addSymbolInNumber('7')
                R.id.btn_numpad_8 -> addSymbolInNumber('8')
                R.id.btn_numpad_9 -> addSymbolInNumber('9')
                R.id.btn_numpad_star -> addSymbolInNumber('*')
                R.id.btn_numpad_hash -> addSymbolInNumber('#')
                R.id.btn_remove_number -> removeSymbolInNumber()
                R.id.btn_toggle_numpad -> toggleNumpad()
                R.id.btn_call -> callNumber(numberField!!.text.toString())
                R.id.btn_add_contact -> createContact(numberField!!.text.toString())
                R.id.btn_options -> showPopupMenu(findViewById(R.id.btn_options))
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (event != null) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val focusedViewTag = currentFocus!!.tag
                var number: String
                if (focusedViewTag is LogEntryCache) {
                    val tag = focusedViewTag
                    number = tag.phoneNumber.text.toString()
                    if (number.isEmpty()) {
                        number = tag.contactName.text.toString()
                    }
                } else if (focusedViewTag is ContactsEntryCache) {
                    number = focusedViewTag.phoneNumber.text.toString()
                } else {
                    number = numberField!!.text.toString()
                }
                callNumber(number)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun hasRequiredPermissions(): Boolean {
        for (i in 0..4) {
            if (checkSelfPermission(PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        for (i in 0..4) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                finish()
                break
            }
        }
    }

    private fun parseIntent(intent: Intent) {
        if (Intent.ACTION_VIEW == intent.action || Intent.ACTION_DIAL == intent.action) {
            val data = intent.data
            if (data != null) {
                val scheme = data.scheme
                if (scheme != null && scheme == "tel") {
                    val number = data.schemeSpecificPart
                    number?.let { dialNumber(it) }
                }
            }
        }
    }

    private fun removeSymbolInNumber() {
        val text = StringBuilder(numberField!!.text)
        if (text.isEmpty()) return
        val selectionStart = numberField!!.selectionStart
        val selectionEnd = numberField!!.selectionEnd
        if (selectionStart != selectionEnd) {
            text.delete(selectionStart, selectionEnd)
            numberField!!.setText(text)
            numberField!!.setSelection(selectionStart)
        } else {
            if (selectionStart == 0) return
            text.deleteCharAt(selectionEnd - 1)
            numberField!!.setText(text)
            numberField!!.setSelection(selectionStart - 1)
        }
        if (text.length == 0) {
            numberField!!.isCursorVisible = false
        }
    }

    private fun addSymbolInNumber(symbol: Char) {
        val text = StringBuilder(numberField!!.text)
        val selectionStart = numberField!!.selectionStart
        val selectionEnd = numberField!!.selectionEnd
        if (selectionStart != selectionEnd) {
            text.delete(selectionStart, selectionEnd)
        }
        text.insert(selectionStart, symbol)
        numberField!!.setText(text)
        numberField!!.setSelection(selectionStart + 1)
        if (!numberField!!.isCursorVisible) {
            numberField!!.isCursorVisible = true
        }
    }

    private fun clearNumber() {
        numberField!!.setText("")
        numberField!!.isCursorVisible = false
    }

    private fun setButtonListeners() {
        for (i in buttonIds.indices) {
            findViewById<View>(buttonIds[i]).setOnClickListener(this)
            findViewById<View>(buttonIds[i]).setOnLongClickListener(this)
        }
    }

    val isNumpadVisible: Boolean
        get() {
            val panel = findViewById<View>(R.id.panel_number_input)
            return panel.visibility == View.VISIBLE
        }

    private fun toggleNumpad() {
        if (isNumpadVisible) {
            hideNumpad()
        } else {
            showNumpad()
        }
    }

    fun hideNumpad() {
        findViewById<View>(R.id.panel_number_input).visibility = View.GONE
        findViewById<View>(R.id.numpad).visibility = View.GONE
        findViewById<View>(R.id.btn_call).visibility = View.INVISIBLE
    }

    private fun showNumpad() {
        findViewById<View>(R.id.panel_number_input).visibility = View.VISIBLE
        findViewById<View>(R.id.numpad).visibility = View.VISIBLE
        findViewById<View>(R.id.btn_call).visibility = View.VISIBLE
    }

    private fun openMessagingApp(number: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse("smsto:$number")
        try {
            startActivity(intent)
        } catch (e: Exception) {
        }
    }

    private fun callNumber(number: String?) {
        if (TextUtils.isEmpty(number) || null == number) {
            return
        }
        val uri = Uri.parse("tel:" + Uri.encode(number))
        val intent = Intent(Intent.ACTION_CALL, uri)
        startActivity(intent)
        finish()
    }

    fun createContact(number: String?) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION, ContactsContract.Contacts.CONTENT_URI)
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showMissingContactsAppDialog()
        }
    }

    private fun dialNumber(number: String) {
        showNumpad()
        numberField!!.setText(number)
        numberField!!.isCursorVisible = true
    }

    private fun setContactsMode() {
        if (mode == CONTACTS_MODE) return
        mode = CONTACTS_MODE
        list!!.adapter = contactsEntryAdapter
    }

    private fun setCallLogMode() {
        if (mode == CALL_LOG_MODE) return
        mode = CALL_LOG_MODE
        list!!.adapter = logEntryAdapter
    }

    private fun clearCallLog() {
        contentResolver.delete(Calls.CONTENT_URI, null, null)
        logEntryAdapter!!.update()
    }

    private fun showDeviceId() {
        val builder = AlertDialog.Builder(this)
        val deviceId: String = if (Build.VERSION.SDK_INT < 26) {
            telephonyManager!!.deviceId
        } else {
            when (telephonyManager!!.phoneType) {
                TelephonyManager.PHONE_TYPE_GSM -> telephonyManager!!.imei
                TelephonyManager.PHONE_TYPE_CDMA -> telephonyManager!!.meid
                else -> "null"
            }
        }
        builder.setMessage(deviceId)
        builder.create().show()
    }

    private fun showPrivacyPolicyDialog(editor: Editor) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.privacy_policy_title))
        builder.setMessage(resources.getString(R.string.privacy_policy))
        builder.setPositiveButton(R.string.accept
        ) { _, _ -> editor.putBoolean("privacy_policy", true).commit() }
        builder.setNegativeButton(android.R.string.no
        ) { _, _ -> finish() }
        builder.create().show()
    }

    private fun clearCallLogDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.clear_call_log_question))
        builder.setPositiveButton(android.R.string.yes
        ) { di, which -> clearCallLog() }
        builder.setNegativeButton(android.R.string.no
        ) { di, which -> }
        builder.create().show()
    }

    private fun showInformationDialog(id: Long) {
        val cursor = contentResolver.query(Calls.CONTENT_URI, CALL_INFORMATION_PROJECTION, Calls._ID + "=?", arrayOf(id.toString()), null)
        if (cursor!!.count == 0) return
        cursor.moveToNext()
        val date = cursor.getLong(1)
        val duration = cursor.getLong(2)
        val dateInstance = DateFormat.getDateInstance(DateFormat.LONG)
        val timeInstance = DateFormat.getTimeInstance(DateFormat.MEDIUM)
        val builder = AlertDialog.Builder(this)
        val message = String.format("%1s: %2s, %3s\n%4s: %5s",
                resources.getString(R.string.date), timeInstance.format(date),
                dateInstance.format(date), resources.getString(R.string.duration),
                DateUtils.formatElapsedTime(duration))
        builder.setMessage(message)
        builder.setCancelable(true)
        builder.create().show()
        cursor.close()
    }

    private fun deleteCallLogEntry(id: Long) {
        contentResolver.delete(Calls.CONTENT_URI, Calls._ID + "=?", arrayOf(id.toString()))
        logEntryAdapter!!.update()
    }

    private fun deleteCallLogEntryDialog(id: Long) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.delete_call_log_entry_question))
        builder.setPositiveButton(android.R.string.yes
        ) { _, _ -> deleteCallLogEntry(id) }
        builder.setNegativeButton(android.R.string.no
        ) { _, _ -> }
        builder.create().show()
    }

    private fun showLogEntryDialog(position: Int, id: Long) {
        val builder = AlertDialog.Builder(this)
        val number = logEntryAdapter!!.getPhoneNumber(position)
        builder.setCancelable(true)
        builder.setTitle(number)
        val commands = arrayOf<String?>(
                resources.getString(R.string.show_info),
                resources.getString(R.string.make_a_call),
                resources.getString(R.string.send_message),
                resources.getString(R.string.delete_log_entry),
                resources.getString(R.string.copy_number)
        )
        val dialogAdapter: ArrayAdapter<*> = ArrayAdapter<Any?>(this, android.R.layout.simple_list_item_1, commands)
        val onDialogItemClick = DialogInterface.OnClickListener { di, which ->
            when (which) {
                0 -> showInformationDialog(id)
                1 -> callNumber(number)
                2 -> openMessagingApp(number)
                3 -> deleteCallLogEntry(id)
                4 -> {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("label", number)
                    clipboard.setPrimaryClip(clip)
                }
            }
        }
        builder.setAdapter(dialogAdapter, onDialogItemClick)
        builder.create().show()
    }

    fun showMissingContactsAppDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(resources.getString(R.string.contacts_app_is_missing))
        builder.setPositiveButton(android.R.string.yes
        ) { _, _ -> }
        builder.create().show()
    }

    override fun onItemClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
        if (adapterView.adapter is LogEntryAdapter) {
            callNumber(logEntryAdapter!!.getPhoneNumber(position))
        } else if (adapterView.adapter is ContactsEntryAdapter) {
            callNumber(contactsEntryAdapter!!.getPhoneNumber(position))
        }
    }

    override fun onItemLongClick(adapterView: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        if (adapterView.adapter is LogEntryAdapter) {
            showLogEntryDialog(position, id)
            return true
        }
        return false
    }

    override fun afterTextChanged(s: Editable) {}
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        val number = s.toString()
        if (start == 0 && before == 0 && count == 0) {
            return
        } else if (TextUtils.isEmpty(s) && before > 0) {
            setCallLogMode()
            contactsEntryAdapter!!.resetFilter()
            list!!.setSelection(0)
        } else if (number == "*#06#") {
            showDeviceId()
        } else if (number.startsWith("*#*#") && number.endsWith("#*#*")) {
            val secretCode = StringBuilder(number).substring(4, number.length - 4)
            sendBroadcast(Intent("android.provider.Telephony.SECRET_CODE", Uri.parse("android_secret_code://$secretCode")))
        } else {
            setContactsMode()
            contactsEntryAdapter!!.filter.filter(s)
            list!!.setSelection(0)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        popup.setOnMenuItemClickListener(this)
        popup.inflate(R.menu.dialer_options)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.clear_call_log -> {
                clearCallLogDialog()
                true
            }
            R.id.fast_dial_preferences -> {
                startActivity(Intent(this, SpeedDialActivity::class.java))
                true
            }
            R.id.dialer_preferences -> {
                startActivity(Intent(this, DialerPreferencesActivity::class.java))
                true
            }
            R.id.dialer_about_screen -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> false
        }
    }

    override fun onLongClick(view: View?): Boolean {
        if (view != null) {
            when (view.id) {
                R.id.btn_numpad_0 -> addSymbolInNumber('+')
                R.id.btn_numpad_1 -> {
                    val voiceMailNumber: String?
                    try {
                        voiceMailNumber = telephonyManager!!.voiceMailNumber
                        voiceMailNumber?.let { callNumber(it) }
                    } catch (exception: SecurityException) {
                        Toast.makeText(this, R.string.permission_not_granted, Toast.LENGTH_SHORT).show()
                        addSymbolInNumber('1')
                    }
                }
                R.id.btn_numpad_2 -> callNumber(getNumber(this, "2"))
                R.id.btn_numpad_3 -> callNumber(getNumber(this, "3"))
                R.id.btn_numpad_4 -> callNumber(getNumber(this, "4"))
                R.id.btn_numpad_5 -> callNumber(getNumber(this, "5"))
                R.id.btn_numpad_6 -> callNumber(getNumber(this, "6"))
                R.id.btn_numpad_7 -> callNumber(getNumber(this, "7"))
                R.id.btn_numpad_8 -> callNumber(getNumber(this, "8"))
                R.id.btn_numpad_9 -> callNumber(getNumber(this, "9"))
                R.id.btn_remove_number -> clearNumber()
                else -> return false
            }
        }
        return true
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return if (id == 0) {
            CursorLoader(this, Calls.CONTENT_URI, LogEntryAdapter.PROJECTION, null, null, Calls.DEFAULT_SORT_ORDER)
        } else {
            CursorLoader(this, Phone.CONTENT_URI, ContactsEntryAdapter.PROJECTION, Phone.HAS_PHONE_NUMBER + "=1", null, Phone.DISPLAY_NAME)
        }
    }

    override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor) {
        val id = loader.id
        if (id == 0) {
            logEntryAdapter!!.swapCursor(data)
        } else {
            contactsEntryAdapter!!.setCursor(data)
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        val id = loader.id
        if (id == 0) {
            logEntryAdapter!!.swapCursor(null)
        } else {
            contactsEntryAdapter!!.setCursor(null)
        }
    }

    companion object {
        private val buttonIds = intArrayOf(
                R.id.btn_numpad_0, R.id.btn_numpad_1,
                R.id.btn_numpad_2, R.id.btn_numpad_3,
                R.id.btn_numpad_4, R.id.btn_numpad_5,
                R.id.btn_numpad_6, R.id.btn_numpad_7,
                R.id.btn_numpad_8, R.id.btn_numpad_9,
                R.id.btn_numpad_star, R.id.btn_numpad_hash,
                R.id.btn_add_contact, R.id.btn_remove_number,
                R.id.btn_toggle_numpad, R.id.btn_options,
                R.id.btn_call)
        private const val CALL_LOG_MODE = 0
        private const val CONTACTS_MODE = 1
        private const val BUNDLE_KEY_NUMBER = "number"
        private val PERMISSIONS = arrayOf(
                Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_CALL_LOG
        )
        private val CALL_INFORMATION_PROJECTION = arrayOf(
                Calls._ID,
                Calls.DATE,
                Calls.DURATION
        )
    }
}