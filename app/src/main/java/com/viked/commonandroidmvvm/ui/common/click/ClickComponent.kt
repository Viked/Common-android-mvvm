package com.zfort.nexter.ui.list.click

import android.view.View
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
interface ClickComponent {

    fun handleClick(view: View, itemWrapper: ItemWrapper): Boolean

}