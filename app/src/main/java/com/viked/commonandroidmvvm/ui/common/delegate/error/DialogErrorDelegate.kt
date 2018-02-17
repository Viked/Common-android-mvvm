package com.viked.commonandroidmvvm.ui.common.delegate.error

import android.databinding.Observable
import android.databinding.ObservableField
import android.support.v7.app.AlertDialog
import com.viked.commonandroidmvvm.ui.activity.BaseActivity
import com.viked.commonandroidmvvm.ui.common.delegate.BaseViewDelegate

/**
 * Created by shein on 1/9/2018.
 */
class DialogErrorDelegate(private val context: BaseActivity) : BaseViewDelegate<ObservableField<BaseError>>(), ErrorDelegate {

    override fun updateWithValue(observables: Set<Observable>) {
        val error = observables.map { it as ObservableField<BaseError> }.find { it.get() != null }
        if (context.active && error != null && error.get() != null) {
            showError(error.get())
            error.set(null)
        }
    }

    override fun showError(error: BaseError) {
        if (context.active) {
            AlertDialog.Builder(context)
                    .setMessage(error.errorMessage[context])
                    .setPositiveButton(android.R.string.ok, { dialog, which ->
                        dialog?.dismiss()
                        error.action?.invoke(context)
                    })
                    .setCancelable(false)
                    .show()
        }
    }
}