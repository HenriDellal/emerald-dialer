package ru.henridellal.dialer;

import android.app.Activity;
import android.view.View;

public class Numpad {
	public static boolean isVisible(Activity activity) {
		View panel = activity.findViewById(R.id.panel_number_input);
		return panel.getVisibility() == View.VISIBLE;
	}

	public static void toggle(Activity activity) {
		if (isVisible(activity)) {
			hide(activity);
		} else {
			show(activity);
		}
	}

	public static void hide(Activity activity) {
		activity.findViewById(R.id.panel_number_input).setVisibility(View.GONE);
		activity.findViewById(R.id.numpad).setVisibility(View.GONE);
		activity.findViewById(R.id.btn_call).setVisibility(View.INVISIBLE);
	}

	public static void show(Activity activity) {
		activity.findViewById(R.id.panel_number_input).setVisibility(View.VISIBLE);
		activity.findViewById(R.id.numpad).setVisibility(View.VISIBLE);
		activity.findViewById(R.id.btn_call).setVisibility(View.VISIBLE);
	}
}