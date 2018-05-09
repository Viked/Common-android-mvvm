package com.viked.commonandroidmvvm.ui.dialog.purchase

import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.fragment.list.BaseListViewModel
import javax.inject.Inject

/**
 * Created by yevgeniishein on 3/11/18.
 */
class PurchaseViewModel @Inject constructor(billingRepository: BillingRepository) : BaseListViewModel<List<ItemWrapper>>(billingRepository) {

    override val emptyListMessageId = R.string.empty_list

    override fun getListSubscriptionBuilder() = billingRepository.getPurchasesList()

    override fun getList(model: List<ItemWrapper>) = model

    fun startFlow(sku: String) {
        billingRepository.initiatePurchaseFlow(sku)
    }
}