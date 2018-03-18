package com.viked.commonandroidmvvm.ui.common.delegate.progress

import android.support.v4.widget.SwipeRefreshLayout

/**
 * Created by yevgeniishein on 3/11/18.
 */
class RefreshProgressDelegate(private val swipeRefreshLayout: SwipeRefreshLayout) : BaseProgressDelegate() {

    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }

}