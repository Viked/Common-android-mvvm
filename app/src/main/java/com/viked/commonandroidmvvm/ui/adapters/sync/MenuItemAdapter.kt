package com.viked.commonandroidmvvm.ui.adapters.sync

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import android.view.MenuItem

import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate

/**
 * Created by yevgeniishein on 3/8/18.
 */
class MenuItemAdapter(private val menuItem: MenuItem, private val state: ObservableField<MenuItemState>) : AdapterDelegate {

    override fun subscribe() {
        state.addOnPropertyChangedCallback(callback)
        update()
    }

    override fun unsubscribe() {
        state.removeOnPropertyChangedCallback(callback)
    }

    override fun update() {
        val value = state.get()
        if (value == null || value == hideItem) {
            menuItem.isVisible = false
        } else {
            menuItem.isVisible = true
            menuItem.setIcon(value.iconId)
            menuItem.setTitle(value.titleId)
        }
    }

    private val callback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(p0: Observable?, p1: Int) {
            update()
        }
    }
}