package com.viked.commonandroidmvvm.ui.common.delegate.error

import androidx.databinding.ObservableField
import com.viked.commonandroidmvvm.ui.common.delegate.ViewDelegate

/**
 * Created by shein on 1/9/2018.
 */
interface ErrorDelegate : ViewDelegate<ObservableField<BaseError>> {
    fun showError(error: BaseError)
}