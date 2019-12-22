package com.viked.commonandroidmvvm.ui.dialog.purchase

import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.billing.BillingItem
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 3/12/18.
 */
class PurchaseItemWrapper(val details: SkuDetails,
                          val billingItem: BillingItem,
                          val purchase: Purchase?) : ItemWrapper(details.sku.hashCode().toLong(), details) {

    val buttonTitle: TextWrapper
        get() = if (purchase != null) {
            TextWrapper(R.string.button_own)
        } else {
            TextWrapper(details.price)
        }

    override fun areContentsTheSame(oldItem: ItemWrapper): Boolean {
        if (oldItem !is PurchaseItemWrapper) return false
        return oldItem.details == details && oldItem.billingItem == billingItem && oldItem.purchase == purchase
    }
}