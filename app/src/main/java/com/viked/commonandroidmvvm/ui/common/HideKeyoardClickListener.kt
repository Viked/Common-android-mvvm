package com.viked.commonandroidmvvm.ui.common

import android.view.View
import com.viked.commonandroidmvvm.extentions.hideKeyboard

/**
 * Created by yevgeniishein on 10/7/17.
 */
class HideKeyoardClickListener(val click: (View) -> Unit) : View.OnClickListener {

    override fun onClick(p0: View?) {
        if (p0 == null) return
        p0.context?.hideKeyboard()
        click.invoke(p0)
    }
}