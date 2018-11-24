package com.viked.commonandroidmvvm.ui.common.click

import android.view.View
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BlockerClickDecorator(private val clickComponent: ClickComponent, private val decorator: (View, Int) -> Boolean) : ClickComponent {
    override fun handleClick(view: View, id: Int) = decorator.invoke(view, id)
            || clickComponent.handleClick(view, id)
}