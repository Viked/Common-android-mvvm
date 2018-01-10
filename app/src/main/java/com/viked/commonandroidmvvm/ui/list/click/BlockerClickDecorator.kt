package com.zfort.nexter.ui.list.click

import android.view.View
import com.viked.commonandroidmvvm.ui.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BlockerClickDecorator(private val clickComponent: ClickComponent, private val decorator: (View, ItemWrapper) -> Boolean) : ClickComponent {
    override fun handleClick(view: View, itemWrapper: ItemWrapper) = decorator.invoke(view, itemWrapper)
            || clickComponent.handleClick(view, itemWrapper)
}