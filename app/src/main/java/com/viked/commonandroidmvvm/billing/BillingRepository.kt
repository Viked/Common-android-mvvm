package com.viked.commonandroidmvvm.billing

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.viked.commonandroidmvvm.rx.buildSubscription
import io.reactivex.processors.BehaviorProcessor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 3/2/18.
 */
@Singleton
class BillingRepository @Inject constructor() {

    private val purchaseProsessor = BehaviorProcessor.createDefault(listOf<Purchase>()).toSerialized()
    private val skuDetailsProsessor = BehaviorProcessor.createDefault(listOf<SkuDetails>()).toSerialized()

    fun updatePurchase(list: List<Purchase>) {
        purchaseProsessor.onNext(list)
    }

    fun updateSkuDetails(list: List<SkuDetails>) {
        skuDetailsProsessor.onNext(list)
    }

    fun getPurchaseSuscription() = purchaseProsessor.buildSubscription()
    fun getSkuDetailsSuscription() = purchaseProsessor.buildSubscription()

}