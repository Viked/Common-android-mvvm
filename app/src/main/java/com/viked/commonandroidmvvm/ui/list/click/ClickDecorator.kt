package com.zfort.nexter.ui.list.click

import android.view.View
import com.viked.commonandroidmvvm.ui.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
class ClickDecorator(private val clickComponent: ClickComponent, private val decorator: (View, ItemWrapper) -> Boolean) : ClickComponent {
    override fun handleClick(view: View, itemWrapper: ItemWrapper): Boolean {
        val superValue = clickComponent.handleClick(view, itemWrapper)
        val decoratorValue = decorator.invoke(view, itemWrapper)
        return superValue || decoratorValue
    }
}