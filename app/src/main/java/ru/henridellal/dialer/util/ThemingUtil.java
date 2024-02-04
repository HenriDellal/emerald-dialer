package ru.henridellal.dialer.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import ru.henridellal.dialer.R;

public class ThemingUtil {
	public static Drawable getDefaultContactDrawable(Context context) {
		TypedValue outValue = new TypedValue();
		context.getTheme().resolveAttribute(R.attr.drawableContactImage, outValue, true);
		return context.getResources().getDrawable(outValue.resourceId, context.getTheme());
	}
}
