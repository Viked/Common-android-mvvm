package com.viked.commonandroidmvvm.ui

import android.view.MenuItem
import androidx.lifecycle.Observer

class MenuCheckedLiveItem(private val menuItem: MenuItem) : Observer<Boolean> {
    override fun onChanged(value: Boolean) {
        menuItem.isChecked = value == true
    }
}