package com.viked.commonandroidmvvm.billing

import android.support.annotation.DrawableRes

/**
 * Created by yevgeniishein on 5/7/18.
 */
class BillingItem(val sku: String, val isSubscription: Boolean, @DrawableRes val iconId: Int, val order: Int)