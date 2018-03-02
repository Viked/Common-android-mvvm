package com.viked.commonandroidmvvm.ui.activity

import android.os.Bundle
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.viked.commonandroidmvvm.billing.BillingManager
import com.viked.commonandroidmvvm.billing.BillingRepository

/**
 * Created by yevgeniishein on 3/2/18.
 */
abstract class BaseBillingActivity : BaseActivity(), BillingManager.BillingUpdatesListener {

    lateinit var billingRepository: BillingRepository

    private lateinit var mBillingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBillingManager = BillingManager(this)
    }

    override fun onBillingClientSetupFinished() {
//        if (mAcquireFragment != null) {
//            mAcquireFragment.onManagerReady(this)
//        }
    }

    override fun onConsumeFinished(token: String, result: Int) {
//        Log.d(TAG, "Consumption finished. Purchase token: $token, result: $result")
//
//        // Note: We know this is the SKU_GAS, because it's the only one we consume, so we don't
//        // check if token corresponding to the expected sku was consumed.
//        // If you have more than one sku, you probably need to validate that the token matches
//        // the SKU you expect.
//        // It could be done by maintaining a map (updating it every time you call consumeAsync)
//        // of all tokens into SKUs which were scheduled to be consumed and then looking through
//        // it here to check which SKU corresponds to a consumed token.
//        if (result == BillingClient.BillingResponse.OK) {
//            // Successfully consumed, so we apply the effects of the item in our
//            // game world's logic, which in our case means filling the gas tank a bit
//            Log.d(TAG, "Consumption successful. Provisioning.")
//            mTank = if (mTank == TANK_MAX) TANK_MAX else mTank + 1
//            saveData()
//            mActivity.alert(R.string.alert_fill_gas, mTank)
//        } else {
//            mActivity.alert(R.string.alert_error_consuming, result)
//        }
//
//        mActivity.showRefreshedUi()
//        Log.d(TAG, "End consumption flow.")
    }

    override fun onPurchasesUpdated(purchases: List<Purchase>) {
        billingRepository.onPurchasesUpdated(purchases)
    }

    override fun onResume() {
        super.onResume()
        // Note: We query purchases in onResume() to handle purchases completed while the activity
        // is inactive. For example, this can happen if the activity is destroyed during the
        // purchase flow. This ensures that when the activity is resumed it reflects the user's
        // current purchases.
        if (mBillingManager.billingClientResponseCode == BillingClient.BillingResponse.OK) {
            mBillingManager.queryPurchases()
        }
    }

    public override fun onDestroy() {
        mBillingManager.destroy()
        super.onDestroy()
    }

}