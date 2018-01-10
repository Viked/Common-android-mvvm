package com.viked.commonandroidmvvm.ui.common

import android.databinding.Observable
import android.databinding.ObservableBoolean
import android.view.MenuItem
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