package com.viked.commonandroidmvvm.ui.dialog.purchase

import android.support.annotation.DrawableRes
import com.android.billingclient.api.SkuDetails
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper

/**
 * Created by yevgeniishein on 3/12/18.
 */
class PurchaseItemWrapper(val details: SkuDetails, @DrawableRes val iconId: Int, val buttonTitle: TextWrapper) : ItemWrapper(details)