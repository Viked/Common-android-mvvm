package com.viked.commonandroidmvvm.ui.common.delegate.error

import android.databinding.ObservableField
import android.support.v7.app.AlertDialog
import com.viked.commonandroidmvvm.ui.common.BaseActivity
import com.viked.commonandroidmvvm.ui.common.delegate.BaseViewDelegate

/**
 * Created by shein on 1/9/2018.
 */
class DialogErrorDelegate(private val context: BaseActivity) : BaseViewDelegate<ObservableField<BaseError>>(), ErrorDelegate {

    override fun update() {
        val error = observable?.get()
        if (context.active && error != null) {
            AlertDialog.Builder(context)
                    .setTitle(error.title[context])
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