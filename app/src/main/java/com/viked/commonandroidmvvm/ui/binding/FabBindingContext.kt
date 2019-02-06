package com.viked.commonandroidmvvm.ui.binding

import androidx.annotation.DrawableRes

/**
 * Created by Marshall Banana on 08.07.2017.
 */
class FabBindingContext(@DrawableRes val iconId: Int, val callback: () -> Unit) {
    fun onClick() {
        callback.invoke()
    }
}