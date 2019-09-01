package com.viked.commonandroidmvvm.ui.common.click

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BaseClickComponent(private val click: (Int) -> Boolean) : ClickComponent {

    override fun handleClick(id: Int) = click(id)

}