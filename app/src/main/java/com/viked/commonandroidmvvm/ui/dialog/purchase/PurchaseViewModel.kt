package com.viked.commonandroidmvvm.ui.dialog.purchase

import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel

/**
 * Created by yevgeniishein on 3/11/18.
 */
class PurchaseViewModel(billingRepository: BillingRepository) : BaseViewModel(billingRepository) {

    val emptyListMessageId = R.string.empty_list

}