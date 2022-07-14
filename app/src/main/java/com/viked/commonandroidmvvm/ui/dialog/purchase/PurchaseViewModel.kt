package com.viked.commonandroidmvvm.ui.dialog.purchase

import android.app.Activity
import androidx.lifecycle.LiveData
import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.data.Resource
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel
import javax.inject.Inject

/**
 * Created by yevgeniishein on 3/11/18.
 */
class PurchaseViewModel @Inject constructor(private val billingRepository: BillingRepository) : BaseViewModel() {

    val list: LiveData<Resource<List<ItemWrapper>>> = billingRepository.list

    fun startFlow(itemWrapper: PurchaseItemWrapper, activity: Activity) {
        billingRepository.buy(itemWrapper.details, activity)
    }
}