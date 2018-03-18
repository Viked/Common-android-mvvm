package com.viked.commonandroidmvvm.ui.adapters.sync

import android.support.annotation.DrawableRes
import android.support.annotation.StringRes

/**
 * Created by yevgeniishein on 3/8/18.
 */
class MenuItemState(@StringRes val titleId: Int, @DrawableRes val iconId: Int)

val hideItem = MenuItemState(0, 0)