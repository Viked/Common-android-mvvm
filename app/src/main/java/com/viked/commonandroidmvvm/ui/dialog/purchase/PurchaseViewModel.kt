package com.viked.commonandroidmvvm.ui.dialog.purchase

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.data.Resource
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel
import javax.inject.Inject

/**
 * Created by yevgeniishein on 3/11/18.
 */
class PurchaseViewModel @Inject constructor(private val billingRepository: BillingRepository) : BaseViewModel() {


    val list: LiveData<Resource<List<ItemWrapper>>> = Transformations.map(billingRepository.list)
    { it -> Resource.map(it) { l -> l.filterIsInstance(ItemWrapper::class.java) } }

    fun startFlow(sku: String) {
        billingRepository.initiatePurchaseFlow(sku)
    }
}