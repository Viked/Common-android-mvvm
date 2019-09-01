package com.viked.commonandroidmvvm.ui.common.click

/**
 * Created by yevgeniishein on 10/22/17.
 */
class ClickDecorator(private val clickComponent: ClickComponent, private val decorator: (Int) -> Boolean) : ClickComponent {
    override fun handleClick(id: Int): Boolean {
        val superValue = clickComponent.handleClick(id)
        val decoratorValue = decorator.invoke(id)
        return superValue || decoratorValue
    }
}