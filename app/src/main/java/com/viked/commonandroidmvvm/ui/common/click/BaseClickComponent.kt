package com.viked.commonandroidmvvm.ui.common.click

import android.view.View
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BaseClickComponent(private val click: (ItemWrapper, View) -> Boolean) : ClickComponent {

    override fun handleClick(view: View, itemWrapper: ItemWrapper) = click.invoke(itemWrapper, view)

}