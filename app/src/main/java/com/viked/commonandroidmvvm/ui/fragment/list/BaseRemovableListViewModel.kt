package com.viked.commonandroidmvvm.ui.fragment.list

import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel


/**
 * Created by yevgeniishein on 1/13/18.
 */
abstract class BaseRemovableListViewModel<T>(billingRepository: BillingRepository, titleId: Int = 0) : BaseViewModel(titleId) {

//    private val removeItemsId = 2
//
//    abstract fun getRemoveSubscriber(list: List<ItemWrapper>): SubscriptionBuilder<Any>
//
//    fun removeItems() {
//        subscribe({
//            getRemoveSubscriber(getSelectedItems())
//                    .addOnSubscribe { list.clear() }
//                    .addOnComplete { loadData() }
//        }, key = removeItemsId)
//    }

}