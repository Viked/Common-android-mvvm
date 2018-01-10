package com.viked.commonandroidmvvm.ui.common.delegate.progress

import android.app.ProgressDialog
import android.databinding.ObservableBoolean
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.common.BaseActivity
import com.viked.commonandroidmvvm.ui.common.delegate.BaseViewDelegate

/**
 * Created by yevgeniishein on 1/1/18.
 */
class DialogProgressDelegate(private val context: BaseActivity) : BaseViewDelegate<ObservableBoolean>(), ProgressDelegate {

    private val dialog = ProgressDialog(context)
            .apply {
                setCancelable(false)
                setMessage(context.getString(R.string.loading))
            }

    override fun unsubscribe() {
        super.unsubscribe()
        hideProgress()
    }

    override fun setProgress(progress: Boolean) {
        if (progress) {
            showProgress()
        } else {
            hideProgress()
        }
    }

    override fun update() {
        setProgress(observable?.get() == true)
    }

    override fun showProgress() {
        if (context.active) {
            dialog.show()
        }
    }

    override fun hideProgress() {
        if (context.active) {
            dialog.dismiss()
        }
    }
}