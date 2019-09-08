package com.viked.commonandroidmvvm.ui.common.delegate.progress

import android.app.ProgressDialog
import androidx.lifecycle.Lifecycle
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.ui.activity.BaseActivity

/**
 * Created by yevgeniishein on 1/1/18.
 */
class DialogProgressDelegate(private val context: BaseActivity) : BaseProgressDelegate() {

    private fun isActive(): Boolean = context.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)

    private val dialog = ProgressDialog(context)
            .apply {
                setCancelable(false)
                setMessage(context.getString(R.string.loading))
            }

    override fun showProgress() {
        if (isActive()) {
            dialog.show()
        }
    }

    override fun hideProgress() {
        if (isActive()) {
            dialog.dismiss()
        }
    }
}