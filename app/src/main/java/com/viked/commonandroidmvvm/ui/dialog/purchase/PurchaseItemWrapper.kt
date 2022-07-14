package com.viked.commonandroidmvvm.ui.dialog.purchase

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.billing.BillingItem
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 3/12/18.
 */
class PurchaseItemWrapper(
    val details: ProductDetails,
    val billingItem: BillingItem,
    val purchase: Purchase?
) : ItemWrapper(details.productId.hashCode().toLong(), details) {

    private val price: String
        get() {
            for (subscriptionOfferDetails in details.subscriptionOfferDetails ?: emptyList()) {
                for (pricingPhase in subscriptionOfferDetails.pricingPhases.pricingPhaseList) {
                    if (pricingPhase.formattedPrice.isNotEmpty()) {
                        return pricingPhase.formattedPrice
                    }
                }
            }
            return ""
        }

    val buttonTitle: TextWrapper
        get() = if (purchase != null) {
            TextWrapper(R.string.button_own)
        } else {
            TextWrapper(price)
        }

    override fun areContentsTheSame(oldItem: ItemWrapper): Boolean {
        if (oldItem !is PurchaseItemWrapper) return false
        return oldItem.details == details && oldItem.billingItem == billingItem && oldItem.purchase == purchase
    }
}