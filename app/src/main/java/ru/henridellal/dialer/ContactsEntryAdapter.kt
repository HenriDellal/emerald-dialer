package ru.henridellal.dialer

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.telephony.PhoneNumberUtils
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import ru.henridellal.dialer.AsyncContactImageLoader.ImageCallback
import java.lang.ref.SoftReference
import java.util.*
import java.util.regex.Pattern

class ContactsEntryAdapter(activity: DialerActivity, asyncContactImageLoader: AsyncContactImageLoader, t9LocaleContext: Context?) : BaseAdapter(), Filterable, View.OnClickListener {
    private val span: ForegroundColorSpan
    private val boldStyleSpan: StyleSpan
    private lateinit var t9NumberPatterns: MutableMap<Char, String>
    private var regexQueryResults: ArrayList<RegexQueryResult>?
    private val mAsyncContactImageLoader: AsyncContactImageLoader
    private var mCursor: Cursor? = null
    private var mFilter: ContactsFilter? = null
    private var rawFiltering = false
    private val activityRef: SoftReference<DialerActivity> = SoftReference(activity)
    private fun initT9NumberPatterns(res: Resources) {
        t9NumberPatterns = HashMap()
        var i = '0'
        while (i <= '9') {
            t9NumberPatterns[i] = res.getString(t9NumberPatternIds[Character.getNumericValue(i)])
            i++
        }
        t9NumberPatterns['*'] = res.getString(R.string.regex_star)
        t9NumberPatterns['#'] = res.getString(R.string.regex_hash)
        t9NumberPatterns['+'] = Pattern.quote("+")
    }

    fun setCursor(cursor: Cursor?) {
        if (mCursor != null) {
            mCursor!!.close()
        }
        mCursor = cursor
    }

    override fun getFilter(): Filter {
        if (mFilter == null) {
            mFilter = ContactsFilter()
        }
        return mFilter!!
    }

    fun resetFilter() {
        mFilter = null
        if (null != regexQueryResults) regexQueryResults!!.clear()
    }

    override fun getCount(): Int {
        return if (null != regexQueryResults) regexQueryResults!!.size else 0
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun onClick(view: View) {
        if (view.id == R.id.contact_entry_image) {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.withAppendedPath(Phone.CONTENT_URI, (view.tag as ContactImageTag).contactId)
            intent.setDataAndType(uri, "vnd.android.cursor.dir/contact")
            try {
                activityRef.get()!!.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                activityRef.get()!!.showMissingContactsAppDialog()
            }
        }
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = LayoutInflater.from(activityRef.get()).inflate(R.layout.contacts_entry, parent, false)
            view.tag = ContactsEntryCache(view)
        } else {
            view = convertView
        }
        val queryResult = regexQueryResults!![position]
        val viewCache = view.tag as ContactsEntryCache
        mCursor!!.moveToPosition(queryResult.position)
        val name = mCursor!!.getString(COLUMN_NAME)
        if (!TextUtils.isEmpty(name) && queryResult.start != queryResult.end) {
            val nameSpanned = SpannableString(name)
            nameSpanned.setSpan(span, queryResult.start, queryResult.end, 0)
            nameSpanned.setSpan(boldStyleSpan, queryResult.start, queryResult.end, 0)
            viewCache.contactName.text = nameSpanned
        } else {
            viewCache.contactName.text = name
        }
        val phoneNumber = mCursor!!.getString(COLUMN_NUMBER)
        if (!TextUtils.isEmpty(phoneNumber) && queryResult.numberStart != queryResult.numberEnd) {
            val numberSpanned = SpannableString(formatNumber(phoneNumber))
            numberSpanned.setSpan(span, queryResult.numberStart, queryResult.numberEnd, 0)
            viewCache.phoneNumber.text = numberSpanned
        } else {
            viewCache.phoneNumber.text = formatNumber(phoneNumber)
        }
        val lookupKey = mCursor!!.getString(COLUMN_LOOKUP_KEY)
        val tag = ContactImageTag(mCursor!!.getInt(0).toString(), lookupKey)
        viewCache.contactImage.tag = tag // set a tag for the callback to be able to check, so we don't set the contact image of a reused view
        val d = mAsyncContactImageLoader.loadDrawableForContact(lookupKey, object : ImageCallback {
            override fun imageLoaded(imageDrawable: Drawable?, lookupKey: String?) {
                if (lookupKey == (viewCache.contactImage.tag as ContactImageTag).lookupKey) {
                    viewCache.contactImage.setImageDrawable(imageDrawable)
                }
            }
        })
        viewCache.contactImage.setImageDrawable(d)
        /*String thumbnailUri = mCursor.getString(COLUMN_THUMBNAIL_URI);
		if ( null == thumbnailUri) {
			viewCache.contactImage.setImageDrawable(null);
		} else {
		try {
		InputStream input = activityRef.get().getContentResolver().openInputStream(Uri.parse(thumbnailUri));
		Drawable mDrawable = Drawable.createFromStream(input, thumbnailUri);
		viewCache.contactImage.setImageDrawable(mDrawable);
		input.close();
		} catch (Exception e) {
			viewCache.contactImage.setImageDrawable(null);
		}
		}*/viewCache.contactImage.setOnClickListener(this)
        return view
    }

    fun setRawFiltering(rawFiltering: Boolean) {
        this.rawFiltering = rawFiltering
    }

    fun getPhoneNumber(position: Int): String {
        mCursor!!.moveToPosition(regexQueryResults!![position].position)
        return mCursor!!.getString(COLUMN_NUMBER)
    }

    fun formatNumber(number: String?): String {
        return PhoneNumberUtils.formatNumber(number, Locale.getDefault().country)
    }

    fun formContactNameRegex(s: String): String {
        val result = StringBuilder()
        var mChar: Char
        for (i in 0 until s.length) {
            mChar = s[i]
            result.append(t9NumberPatterns!![mChar])
        }
        return result.toString()
    }

    private fun isRegexIdentifier(c: Char): Boolean {
        return c == '*' || c == '#' || c == '+'
    }

    fun formNumberRegex(s: String): String {
        val result = StringBuilder()
        var numberChar = s[0]
        result.append(if (isRegexIdentifier(numberChar)) Pattern.quote(Character.toString(numberChar)) else numberChar)
        for (i in 1 until s.length) {
            result.append("[\\W]*")
            numberChar = s[i]
            result.append(if (isRegexIdentifier(numberChar)) Pattern.quote(Character.toString(numberChar)) else numberChar)
        }
        return result.toString()
    }

    private inner class ContactsFilter : Filter() {
        private fun filterRaw(resultsList: ArrayList<RegexQueryResult>, constraint: CharSequence) {
            mCursor!!.moveToFirst()
            val constraintString = constraint.toString().toLowerCase()
            while (!mCursor!!.isAfterLast) {
                var name = mCursor!!.getString(COLUMN_NAME)
                var number = mCursor!!.getString(COLUMN_NUMBER)
                var queryResult: RegexQueryResult? = null
                if (null != name) {
                    name = name.toLowerCase()
                    val nameIndexOfConstraint = name.indexOf(constraintString)
                    if (nameIndexOfConstraint != -1) queryResult = RegexQueryResult(mCursor!!.position, nameIndexOfConstraint, nameIndexOfConstraint + name.length)
                }
                if (null != number) {
                    number = number.toLowerCase()
                    val numberIndexOfConstraint = number.indexOf(constraintString)
                    if (numberIndexOfConstraint != -1) {
                        if (null == queryResult) {
                            queryResult = RegexQueryResult(mCursor!!.position, 0, 0)
                        }
                        queryResult.setNumberPlace(numberIndexOfConstraint, numberIndexOfConstraint + constraintString.length)
                    }
                }
                if (null != queryResult) {
                    resultsList.add(queryResult)
                }
                mCursor!!.moveToNext()
            }
        }

        private fun filterWithRegex(resultsList: ArrayList<RegexQueryResult>, constraint: CharSequence) {
            val nameRegex = formContactNameRegex(constraint.toString())
            val numberRegex = formNumberRegex(constraint.toString())
            val namePattern = Pattern.compile(nameRegex)
            val wordStartPattern = Pattern.compile("\\s+$nameRegex")
            val numberPattern = Pattern.compile(numberRegex)
            mCursor!!.moveToFirst()
            while (!mCursor!!.isAfterLast) {
                val name = mCursor!!.getString(COLUMN_NAME)
                val number = mCursor!!.getString(COLUMN_NUMBER)
                val nameMatcher = if (null != name) namePattern.matcher(name) else null
                val numberMatcher = if (null != number) numberPattern.matcher(formatNumber(number)) else null
                var queryResult: RegexQueryResult? = null
                if (null != nameMatcher && nameMatcher.find()) {
                    if (nameMatcher.start() == 0) {
                        queryResult = RegexQueryResult(mCursor!!.position, nameMatcher.start(), nameMatcher.end())
                    } else {
                        val wordStartMatcher = wordStartPattern.matcher(name)
                        if (wordStartMatcher.find()) {
                            queryResult = RegexQueryResult(mCursor!!.position, wordStartMatcher.start(), wordStartMatcher.end())
                        }
                    }
                }
                if (null != numberMatcher && numberMatcher.find()) {
                    if (null == queryResult) {
                        queryResult = RegexQueryResult(mCursor!!.position, 256 + numberMatcher.start(), 256 + numberMatcher.start())
                    }
                    queryResult.setNumberPlace(numberMatcher.start(), numberMatcher.end())
                }
                if (null != queryResult) {
                    resultsList.add(queryResult)
                }
                mCursor!!.moveToNext()
            }
        }

        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            val resultsList = ArrayList<RegexQueryResult>()
            if (rawFiltering) {
                filterRaw(resultsList, constraint)
            } else {
                filterWithRegex(resultsList, constraint)
            }
            Collections.sort(resultsList)
            results.values = resultsList
            results.count = resultsList.size
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            regexQueryResults = results.values as ArrayList<RegexQueryResult>
            notifyDataSetChanged()
        }
    }

    companion object {
        val PROJECTION = arrayOf(
                Phone._ID,
                Phone.LOOKUP_KEY,
                Phone.DISPLAY_NAME,
                Phone.NUMBER
        )
        private val t9NumberPatternIds = intArrayOf(
                R.string.regex_0, R.string.regex_1,
                R.string.regex_2, R.string.regex_3,
                R.string.regex_4, R.string.regex_5,
                R.string.regex_6, R.string.regex_7,
                R.string.regex_8, R.string.regex_9)
        private const val COLUMN_LOOKUP_KEY = 1
        private const val COLUMN_NAME = 2
        private const val COLUMN_NUMBER = 3
    }

    init {
        initT9NumberPatterns(if (null != t9LocaleContext) t9LocaleContext.resources else activityRef.get()!!.resources)
        regexQueryResults = ArrayList()
        mAsyncContactImageLoader = asyncContactImageLoader
        span = ForegroundColorSpan(activity.resources.getColor(R.color.green_600))
        boldStyleSpan = StyleSpan(Typeface.BOLD)
    }
}