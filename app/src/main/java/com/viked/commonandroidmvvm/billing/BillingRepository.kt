package com.viked.commonandroidmvvm.billing

import com.android.billingclient.api.Purchase

/**
 * Created by yevgeniishein on 3/2/18.
 */
interface BillingRepository {
    fun onPurchasesUpdated(purchases: List<Purchase>)
}