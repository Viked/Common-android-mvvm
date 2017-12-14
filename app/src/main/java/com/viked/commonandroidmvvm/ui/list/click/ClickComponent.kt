package com.viked.commonandroidmvvm.ui.list.click

import android.view.View
import com.viked.commonandroidmvvm.ui.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
interface ClickComponent {

    fun handleClick(view: View, itemWrapper: ItemWrapper): Boolean

}