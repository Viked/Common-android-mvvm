package com.viked.commonandroidmvvm.ui.common.click

import android.view.View

/**
 * Created by yevgeniishein on 10/22/17.
 */
class ClickDecorator(private val clickComponent: ClickComponent, private val decorator: (View, Int) -> Boolean) : ClickComponent {
    override fun handleClick(view: View, id: Int): Boolean {
        val superValue = clickComponent.handleClick(view, id)
        val decoratorValue = decorator.invoke(view, id)
        return superValue || decoratorValue
    }
}