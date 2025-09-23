package ru.henridellal.dialer.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import ru.henridellal.dialer.R;

public class ThemingUtil {

	public static Drawable getDefaultContactDrawable(Context context) {
		return getDefaultContactDrawable(context, R.attr.drawableContactImage);
	}

	public static Drawable getDefaultContactDrawable(Context context, int attrId) {
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(attrId, outValue, true);
		return context.getResources().getDrawable(outValue.resourceId, context.getTheme());
	}
}
