package ru.henridellal.dialer;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

public class OnCallLogScrollListener implements OnScrollListener {

	private DialerActivity activity;

	public OnCallLogScrollListener(DialerActivity activity) {
		this.activity = activity;
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState != SCROLL_STATE_IDLE && Numpad.isVisible(activity)) {
			Numpad.hide(activity);
		}
	}
}
