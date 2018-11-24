package com.viked.commonandroidmvvm.billing

interface BillingSecurity {

    fun verifyPurchase(isSubscription: Boolean, token: String, sku: String): Boolean

}