package com.viked.commonandroidmvvm.ui.fragment.list

import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.rx.SubscriptionBuilder
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 1/13/18.
 */
abstract class BaseRemovableListViewModel<T>(billingRepository: BillingRepository, titleId: Int = 0) : BaseSelectableListViewModel<T>(billingRepository, titleId) {

    private val removeItemsId = 2

    abstract fun getRemoveSubscriber(list: List<ItemWrapper>): SubscriptionBuilder<Any>

    fun removeItems() {
        subscribe({
            getRemoveSubscriber(getSelectedItems())
                    .addOnSubscribe { list.clear() }
                    .addOnComplete { loadData() }
        }, key = removeItemsId)
    }

}