package com.viked.commonandroidmvvm.ui

import android.arch.lifecycle.Observer
import android.view.MenuItem

class MenuLiveItem(private val menuItem: MenuItem) : Observer<Boolean> {
    override fun onChanged(t: Boolean?) {
        menuItem.isVisible = t == true
    }
}