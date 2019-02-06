package com.viked.commonandroidmvvm.ui.common

import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import android.text.InputFilter
import android.view.MenuItem
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by yevgeniishein on 11/23/17.
 */
fun MenuItem.setVisibilityBinding(visibility: ObservableBoolean) {
    isVisible = visibility.get()
    visibility.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            isVisible = visibility.get()
        }
    })
}

fun Long.getFormattedDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yy")
    return dateFormat.format(Date(this))
}

fun EditText.addFilter(inputFilter: InputFilter) {
    filters += inputFilter
}