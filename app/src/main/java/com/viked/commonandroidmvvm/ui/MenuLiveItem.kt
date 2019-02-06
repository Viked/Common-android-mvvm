package com.viked.commonandroidmvvm.ui

import androidx.lifecycle.Observer
import android.view.MenuItem

class MenuLiveItem(private val menuItem: MenuItem) : Observer<Boolean> {
    override fun onChanged(t: Boolean?) {
        menuItem.isEnabled = t == true
    }
}