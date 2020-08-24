package ru.henridellal.dialer

import android.widget.AbsListView

class OnCallLogScrollListener(private val activity: DialerActivity) : AbsListView.OnScrollListener {
    override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {}
    override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE && activity.isNumpadVisible) {
            activity.hideNumpad()
        }
    }
}