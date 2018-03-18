package com.viked.commonandroidmvvm.ui.common.delegate.progress

import android.view.View
import android.widget.ProgressBar

/**
 * Created by yevgeniishein on 3/11/18.
 */
class ProgressBarDelegate(private val progressBar: ProgressBar) : BaseProgressDelegate() {

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

}