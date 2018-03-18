package com.viked.commonandroidmvvm.ui.common.delegate.progress

import android.databinding.Observable
import android.databinding.ObservableBoolean
import com.viked.commonandroidmvvm.ui.common.delegate.BaseViewDelegate

/**
 * Created by yevgeniishein on 3/11/18.
 */
abstract class BaseProgressDelegate() : BaseViewDelegate<ObservableBoolean>(), ProgressDelegate {
    override fun unsubscribe(observable: ObservableBoolean) {
        super.unsubscribe(observable)
        hideProgress()
    }

    override fun setProgress(progress: Boolean) {
        if (progress) {
            showProgress()
        } else {
            hideProgress()
        }
    }

    override fun updateWithValue(observables: Set<Observable>) {
        setProgress(observables.map { it as ObservableBoolean }.find { it.get() }?.get() == true)
    }
}