package com.viked.commonandroidmvvm.ui.common.click

import android.view.View
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 10/22/17.
 */
class BaseClickComponent(private val click: (Int, View) -> Boolean) : ClickComponent {

    override fun handleClick(view: View, id: Int) = click.invoke(id, view)

}