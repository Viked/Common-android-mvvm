package com.viked.commonandroidmvvm.ui.common.click

import android.view.View
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.common.click.ClickComponent

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