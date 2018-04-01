package com.viked.commonandroidmvvm.ui.activity

import android.os.Bundle
import com.viked.commonandroidmvvm.billing.BillingDelegate
import com.viked.commonandroidmvvm.billing.BillingHelper
import com.viked.commonandroidmvvm.billing.BillingRepository
import javax.inject.Inject

/**
 * Created by yevgeniishein on 3/2/18.
 */
abstract class BaseBillingActivity : BaseActivity() {

    @Inject
    lateinit var billingRepository: BillingRepository

    @Inject
    lateinit var billingHelper: BillingHelper

    lateinit var billingDelegate: BillingDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingDelegate = BillingDelegate(this,billingHelper, billingRepository)
    }

    override fun onResume() {
        super.onResume()
        billingDelegate.update()
    }

    public override fun onDestroy() {
        billingDelegate.unsubscribe()
        super.onDestroy()
    }

}