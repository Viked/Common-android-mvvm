package com.viked.commonandroidmvvm.extentions

import android.app.Activity
import android.content.Context
import android.support.v4.app.FragmentManager
import android.view.inputmethod.InputMethodManager

/**
 * Created by yevgeniishein on 10/7/17.
 */
fun Context.hideKeyboard() {
    if (this is Activity) {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
        }
    }
}


fun FragmentManager.handleOnBackPressed(): Boolean {
    val fragment = fragments
            ?.filter { it != null && it.isVisible && it is BaseFragment<*, *> }
            ?.find { (it is BaseFragment<*, *> && it.handleOnBackPressed()) || it.childFragmentManager.handleOnBackPressed() }
    return fragment != null
}