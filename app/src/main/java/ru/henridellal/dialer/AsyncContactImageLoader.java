package ru.henridellal.dialer;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;

class AsyncContactImageLoader {
	public interface ImageCallback {
		void imageLoaded(Drawable imageDrawable, String phoneNumber);
	}

	private static class BackgroundImageLoader extends Thread {
		public Handler mHandler;
		
		public BackgroundImageLoader() {
			super();
		}
		@Override
		public void run() {
			Looper.prepare();
			mHandler = new Handler();
			Looper.loop();
		}
	}
	private static final int IMAGECACHE_INITIAL_CAPACITY = 256; // this is just a guess, should be tuned for a good number of contacts 
	
	private final Context mContext;
	private final Drawable mDefaultDrawable;
	private final HashMap<String, SoftReference<Drawable>> mImageCache;
	private final Handler mHandler;
	private final BackgroundImageLoader mBackgroundImageLoader;

	public AsyncContactImageLoader(Context context, Drawable defaultDrawable) {
		mContext = context;
		mDefaultDrawable = defaultDrawable;
		mImageCache = new HashMap<String, SoftReference<Drawable>>(IMAGECACHE_INITIAL_CAPACITY);
		mHandler = new Handler();
		mBackgroundImageLoader = new BackgroundImageLoader();
		mBackgroundImageLoader.start();
	}

	Drawable loadImageForContact(String lookupKey) {
		Uri contactUri = Contacts.lookupContact(mContext.getContentResolver(), Uri.withAppendedPath(Contacts.CONTENT_LOOKUP_URI, lookupKey));
		
		if (null == contactUri) {
			return mDefaultDrawable;
		}
		
		InputStream contactImageStream = Contacts.openContactPhotoInputStream(mContext.getContentResolver(), contactUri);
		if (contactImageStream != null) {
			return Drawable.createFromStream(contactImageStream, "contact_image");
		} else {
			return mDefaultDrawable;
		}
	}
	
	Drawable loadImageForNumber(String number) {
		if (null == number || number.isEmpty()) {
			return mDefaultDrawable;
		}
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
		Cursor cursor = mContext.getContentResolver().query(uri, new String[] {PhoneLookup.LOOKUP_KEY}, null, null, null);
		if (cursor == null || !cursor.moveToFirst()) {
			return mDefaultDrawable;
		}
		String lookupKey = cursor.getString(0);
		cursor.close();
		return loadImageForContact(lookupKey);
		
	}

	public Drawable loadDrawableForContact(final String lookupKey, final ImageCallback imageCallback) {
		SoftReference<Drawable> softReference = mImageCache.get(lookupKey);
		Drawable drawable = softReference != null ? softReference.get() : null;
		if (drawable != null) {
			return drawable;
		}
		mBackgroundImageLoader.mHandler.postAtFrontOfQueue(new Runnable() {
			
			@Override
			public void run() { // Run in the background thread
				final Drawable d = loadImageForContact(lookupKey);
				AsyncContactImageLoader.this.mHandler.post(new Runnable() {
					
					@Override
					public void run() { // Run in the UI-thread
						mImageCache.put(lookupKey, new SoftReference<Drawable>(d));
						imageCallback.imageLoaded(d, lookupKey);
					}
				});
			}
		});
		return mDefaultDrawable;
	}
	public Drawable loadDrawableForNumber(final String number, final ImageCallback imageCallback) {
		SoftReference<Drawable> softReference = mImageCache.get(number);
		Drawable drawable = softReference != null ? softReference.get() : null;
		if (drawable != null) {
			return drawable;
		}
		mBackgroundImageLoader.mHandler.postAtFrontOfQueue(new Runnable() {
			
			@Override
			public void run() { // Run in the background thread
				final Drawable d = loadImageForNumber(number);
				AsyncContactImageLoader.this.mHandler.post(new Runnable() {
					
					@Override
					public void run() { // Run in the UI-thread
						mImageCache.put(number, new SoftReference<Drawable>(d));
						imageCallback.imageLoaded(d, number);
					}
				});
			}
		});
		return mDefaultDrawable;
	}
}
