package com.viked.commonandroidmvvm.ui.preference

import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel

/**
 * Created by yevgeniishein on 1/13/18.
 */
open class BasePreferenceViewModel(titleId: Int = 0, billingRepository: BillingRepository) : BaseViewModel(billingRepository, titleId)