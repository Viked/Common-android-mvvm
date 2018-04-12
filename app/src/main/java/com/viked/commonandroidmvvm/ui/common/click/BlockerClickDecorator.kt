package com.viked.commonandroidmvvm.ui.common.click

import android.view.View
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BlockerClickDecorator(private val clickComponent: ClickComponent, private val decorator: (View, ItemWrapper) -> Boolean) : ClickComponent {
    override fun handleClick(view: View, itemWrapper: ItemWrapper) = decorator.invoke(view, itemWrapper)
            || clickComponent.handleClick(view, itemWrapper)
}