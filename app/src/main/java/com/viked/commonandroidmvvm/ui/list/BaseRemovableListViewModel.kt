package com.viked.commonandroidmvvm.ui.list

import com.viked.commonandroidmvvm.rx.SubscriptionBuilder

/**
 * Created by yevgeniishein on 1/13/18.
 */
abstract class BaseRemovableListViewModel(titleId: Int = 0) : BaseSelectableListViewModel(titleId) {

    private val removeItemsId = 2

    abstract fun getRemoveSubscriber(list: List<ItemWrapper>): SubscriptionBuilder<Any>

    fun removeItems() {
        subscribe({ getRemoveSubscriber(getSelectedItems()).addOnComplete { loadData() } }, key = removeItemsId)
    }

}