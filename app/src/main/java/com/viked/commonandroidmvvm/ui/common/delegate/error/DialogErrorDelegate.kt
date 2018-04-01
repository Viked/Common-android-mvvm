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
        val errorObserver = observables.filter { it is ObservableField<*> }.map { it as ObservableField<*> }.find { it.get() is BaseError }
        val error = errorObserver?.get() as? BaseError
        if (context.active && errorObserver != null && error != null) {
            showError(error)
            errorObserver.set(null)
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