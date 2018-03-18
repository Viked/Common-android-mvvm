package com.viked.commonandroidmvvm.ui.common.delegate.progress

import android.app.ProgressDialog
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.activity.BaseActivity

/**
 * Created by yevgeniishein on 1/1/18.
 */
class DialogProgressDelegate(private val context: BaseActivity) : BaseProgressDelegate() {

    private val dialog = ProgressDialog(context)
            .apply {
                setCancelable(false)
                setMessage(context.getString(R.string.loading))
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