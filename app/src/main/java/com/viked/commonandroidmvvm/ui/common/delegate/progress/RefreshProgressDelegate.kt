package com.viked.commonandroidmvvm.ui.common.delegate.progress

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Created by yevgeniishein on 3/11/18.
 */
class RefreshProgressDelegate(private val swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout) : BaseProgressDelegate() {

    override fun showProgress() {
        swipeRefreshLayout.isRefreshing = true
    }

    override fun hideProgress() {
        swipeRefreshLayout.isRefreshing = false
    }

}