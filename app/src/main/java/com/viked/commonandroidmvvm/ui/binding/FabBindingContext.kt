package com.zfort.nexter.ui.binding

import android.support.annotation.DrawableRes

/**
 * Created by Marshall Banana on 08.07.2017.
 */
class FabBindingContext(@DrawableRes val iconId: Int, val callback: () -> Unit) {
    fun onClick() {
        callback.invoke()
    }
}