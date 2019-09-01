package com.viked.commonandroidmvvm.ui.common.click

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BlockerClickDecorator(private val clickComponent: ClickComponent, private val decorator: (Int) -> Boolean) : ClickComponent {
    override fun handleClick(id: Int) = decorator.invoke(id)
            || clickComponent.handleClick(id)
}