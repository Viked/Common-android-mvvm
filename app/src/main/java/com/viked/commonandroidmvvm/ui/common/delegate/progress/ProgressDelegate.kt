package com.viked.commonandroidmvvm.ui.common.delegate.progress

import android.databinding.ObservableBoolean
import com.viked.commonandroidmvvm.ui.common.delegate.ViewDelegate

/**
 * Created by yevgeniishein on 1/1/18.
 */
interface ProgressDelegate : ViewDelegate<ObservableBoolean> {

    fun setProgress(progress: Boolean)

    fun showProgress()

    fun hideProgress()

}