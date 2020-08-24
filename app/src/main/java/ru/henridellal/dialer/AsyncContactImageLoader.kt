package ru.henridellal.dialer

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.ContactsContract.PhoneLookup
import java.lang.ref.SoftReference
import java.util.*

class AsyncContactImageLoader(private val mContext: Context, private val mDefaultDrawable: Drawable) {
    interface ImageCallback {
        fun imageLoaded(imageDrawable: Drawable?, phoneNumber: String?)
    }

    private class BackgroundImageLoader : Thread() {
        var mHandler: Handler? = null
        override fun run() {
            Looper.prepare()
            mHandler = Handler()
            Looper.loop()
        }
    }

    private val mImageCache: HashMap<String, SoftReference<Drawable>>
    private val mHandler: Handler
    private val mBackgroundImageLoader: BackgroundImageLoader
    fun loadImageForContact(lookupKey: String?): Drawable {
        val contactUri = ContactsContract.Contacts.lookupContact(mContext.contentResolver, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey))
                ?: return mDefaultDrawable
        val contactImageStream = ContactsContract.Contacts.openContactPhotoInputStream(mContext.contentResolver, contactUri)
        return if (contactImageStream != null) {
            Drawable.createFromStream(contactImageStream, "contact_image")
        } else {
            mDefaultDrawable
        }
    }

    fun loadImageForNumber(number: String?): Drawable {
        if (null == number) {
            return mDefaultDrawable
        }
        val uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number))
        val cursor = mContext.contentResolver.query(uri, arrayOf(PhoneLookup.LOOKUP_KEY), null, null, null)
        if (cursor == null || !cursor.moveToFirst()) {
            return mDefaultDrawable
        }
        val lookupKey = cursor.getString(0)
        cursor.close()
        return loadImageForContact(lookupKey)
    }

    fun loadDrawableForContact(lookupKey: String, imageCallback: ImageCallback): Drawable {
        val softReference = mImageCache[lookupKey]
        val drawable = softReference?.get()
        if (drawable != null) {
            return drawable
        }
        mBackgroundImageLoader.mHandler!!.postAtFrontOfQueue {
            // Run in the background thread
            val d = loadImageForContact(lookupKey)
            mHandler.post { // Run in the UI-thread
                mImageCache[lookupKey] = SoftReference(d)
                imageCallback.imageLoaded(d, lookupKey)
            }
        }
        return mDefaultDrawable
    }

    fun loadDrawableForNumber(number: String, imageCallback: ImageCallback): Drawable {
        val softReference = mImageCache[number]
        val drawable = softReference?.get()
        if (drawable != null) {
            return drawable
        }
        mBackgroundImageLoader.mHandler!!.postAtFrontOfQueue {
            // Run in the background thread
            val d = loadImageForNumber(number)
            mHandler.post { // Run in the UI-thread
                mImageCache[number] = SoftReference(d)
                imageCallback.imageLoaded(d, number)
            }
        }
        return mDefaultDrawable
    }

    companion object {
        private const val IMAGECACHE_INITIAL_CAPACITY = 256 // this is just a guess, should be tuned for a good number of contacts
    }

    init {
        mImageCache = HashMap(IMAGECACHE_INITIAL_CAPACITY)
        mHandler = Handler()
        mBackgroundImageLoader = BackgroundImageLoader()
        mBackgroundImageLoader.start()
    }
}