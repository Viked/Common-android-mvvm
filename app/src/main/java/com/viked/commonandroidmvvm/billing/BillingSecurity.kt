package com.viked.commonandroidmvvm.billing

import io.reactivex.Single

interface BillingSecurity {

    fun verifyPurchase(isSubscription: Boolean, token: String, sku: String): Single<Boolean>

}