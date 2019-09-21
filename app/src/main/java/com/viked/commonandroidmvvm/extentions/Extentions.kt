package com.viked.commonandroidmvvm.extentions

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.viked.commonandroidmvvm.ui.common.Cancelable
import com.viked.commonandroidmvvm.ui.fragment.BaseFragment

/**
 * Created by yevgeniishein on 10/7/17.
 */
fun Context.hideKeyboard(): Boolean {
    if (this is Activity) {
        val view = currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            view.clearFocus()
            return true
        }
    }
    return false
}


fun FragmentManager.handleOnBackPressed(): Boolean {
    val fragment = fragments
            .filter { it != null && it.isVisible && it is BaseFragment<*, *> }
            .find { (it is Cancelable && it.handleOnBackPressed()) || it.childFragmentManager.handleOnBackPressed() }
    return fragment != null
}

inline fun <reified T : ViewModel> ViewModelProvider.getViewModel() = get<T>(T::class.java)


inline fun <reified T> className() = T::class.java