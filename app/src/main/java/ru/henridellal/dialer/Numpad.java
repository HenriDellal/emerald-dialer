package ru.henridellal.dialer;

import android.app.Activity;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

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

	private static final Map<Integer, Character> keyToSymMap = new HashMap<Integer, Character>();
	public static char getSymbol(int keyId) {
		return keyToSymMap.get(keyId);
	}

	static {
		keyToSymMap.put(R.id.btn_numpad_0, '0');
		keyToSymMap.put(R.id.btn_numpad_1, '1');
		keyToSymMap.put(R.id.btn_numpad_2, '2');
		keyToSymMap.put(R.id.btn_numpad_3, '3');
		keyToSymMap.put(R.id.btn_numpad_4, '4');
		keyToSymMap.put(R.id.btn_numpad_5, '5');
		keyToSymMap.put(R.id.btn_numpad_6, '6');
		keyToSymMap.put(R.id.btn_numpad_7, '7');
		keyToSymMap.put(R.id.btn_numpad_8, '8');
		keyToSymMap.put(R.id.btn_numpad_9, '9');
		keyToSymMap.put(R.id.btn_numpad_star, '*');
		keyToSymMap.put(R.id.btn_numpad_hash, '#');
	}
}