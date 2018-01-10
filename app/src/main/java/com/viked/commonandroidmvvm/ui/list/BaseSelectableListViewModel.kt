package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableBoolean
import com.viked.commonandroidmvvm.rx.SubscriptionBuilder

/**
 * Created by yevgeniishein on 10/15/17.
 */
abstract class BaseSelectableListViewModel(titleId: Int = 0) : BaseListViewModel(titleId) {

    val selectMode = ObservableBoolean(false)

    fun hasSelectedItems() = selectMode.get() && getSelectedItems().isNotEmpty()

    fun canSelectAll() = selectMode.get() && list.find { it.selectable && !it.selected.get() } != null

    fun getSelectedItems() = list.filter { it.selectable && it.selected.get() }

    fun selectAll() {
        list.filter { it.selectable }.forEach { it.selected.set(true) }
        selectMode.notifyChange()
    }

    fun handleBackPressed() =
            if (selectMode.get()) {
                list.filter { it.selectable }
                        .forEach { it.selected.set(false) }
                selectMode.set(false)
                true
            } else {
                false
            }

    override fun addListSubscription(subscriptionBuilder: SubscriptionBuilder<List<ItemWrapper>>) =
            super.addListSubscription(subscriptionBuilder).addOnComplete { selectMode.set(false) }
}