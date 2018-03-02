/*
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.viked.commonandroidmvvm.billing

import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.*
import com.android.billingclient.api.Purchase.PurchasesResult
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.ui.activity.BaseBillingActivity
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
class BillingManager(private val mActivity: BaseBillingActivity) : PurchasesUpdatedListener {

    /** A reference to BillingClient  */
    private var mBillingClient: BillingClient? = null

    /**
     * True if billing service is connected now.
     */
    private var mIsServiceConnected: Boolean = false

    private val mPurchases = mutableListOf<Purchase>()

    private val mTokensToBeConsumed = mutableSetOf<String>()

    /**
     * Returns the value Billing client response code or BILLING_MANAGER_NOT_INITIALIZED if the
     * clien connection response was not received yet.
     */
    var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED
        private set

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    interface BillingUpdatesListener {
        fun onBillingClientSetupFinished()
        fun onConsumeFinished(token: String, @BillingResponse result: Int)
        fun onPurchasesUpdated(purchases: List<Purchase>)
    }

    /**
     * Listener for the Billing client state to become connected
     */
    interface ServiceConnectedListener {
        fun onServiceConnected(@BillingResponse resultCode: Int)
    }

    init {
        Timber.i("Creating Billing client.")
        mBillingClient = BillingClient.newBuilder(mActivity).setListener(this).build()

        Timber.i("Starting setup.")

        // Start setup. This is asynchronous and the specified listener will be called
        // once setup completes.
        // It also starts to report all the new purchases through onPurchasesUpdated() callback.
        startServiceConnection(Runnable {
            // Notifying the listener that billing client is ready
            mActivity.onBillingClientSetupFinished()
            // IAB is fully set up. Now, let's get an inventory of stuff we own.
            Timber.i("Setup successful. Querying inventory.")
            queryPurchases()
        })
    }

    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    override fun onPurchasesUpdated(resultCode: Int, purchases: List<Purchase>?) {
        when (resultCode) {
            BillingResponse.OK -> {
                for (purchase in purchases!!) {
                    handlePurchase(purchase)
                }
                mActivity.onPurchasesUpdated(mPurchases)
            }
            BillingResponse.USER_CANCELED -> Timber.i("onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            else -> Timber.i("onPurchasesUpdated() got unknown resultCode: $resultCode")
        }
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(skuId: String, @SkuType billingType: String) {
        initiatePurchaseFlow(skuId, null, billingType)
    }

    /**
     * Start a purchase or subscription replace flow
     */
    fun initiatePurchaseFlow(skuId: String, oldSkus: ArrayList<String>?, @SkuType billingType: String) {
        val purchaseFlowRequest = Runnable {
            Timber.i("Launching in-app purchase flow. Replace old SKU? ${(oldSkus != null)}")
            val purchaseParams = BillingFlowParams.newBuilder()
                    .setSku(skuId).setType(billingType).setOldSkus(oldSkus).build()
            mBillingClient!!.launchBillingFlow(mActivity, purchaseParams)
        }

        executeServiceRequest(purchaseFlowRequest)
    }

    /**
     * Clear the resources
     */
    fun destroy() {
        Timber.i("Destroying the manager.")

        if (mBillingClient != null && mBillingClient!!.isReady) {
            mBillingClient!!.endConnection()
            mBillingClient = null
        }
    }

    fun querySkuDetailsAsync(@SkuType itemType: String, skuList: List<String>,
                             listener: SkuDetailsResponseListener) {
        // Creating a runnable from the request to use it inside our connection retry policy below
        val queryRequest = Runnable {
            // Query the purchase async
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(itemType)
            mBillingClient!!.querySkuDetailsAsync(params.build()
            ) { responseCode, skuDetailsList -> listener.onSkuDetailsResponse(responseCode, skuDetailsList) }
        }

        executeServiceRequest(queryRequest)
    }

    fun consumeAsync(purchaseToken: String) {
        // If we've already scheduled to consume this token - no action is needed (this could happen
        // if you received the token when querying purchases inside onReceive() and later from
        // onActivityResult()
        if (mTokensToBeConsumed.contains(purchaseToken)) {
            Timber.i("Token was already scheduled to be consumed - skipping...")
            return
        }
        mTokensToBeConsumed.add(purchaseToken)

        // Generating Consume Response listener
        val onConsumeListener = ConsumeResponseListener { responseCode, purchaseToken ->
            // If billing service was disconnected, we try to reconnect 1 time
            // (feel free to introduce your retry policy here).
            mActivity.onConsumeFinished(purchaseToken, responseCode)
        }

        // Creating a runnable from the request to use it inside our connection retry policy below
        val consumeRequest = Runnable {
            // Consume the purchase async
            mBillingClient!!.consumeAsync(purchaseToken, onConsumeListener)
        }

        executeServiceRequest(consumeRequest)
    }

    /**
     * Handles the purchase
     *
     * Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * See [Security.verifyPurchase]
     *
     * @param purchase Purchase to be handled
     */
    private fun handlePurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            Timber.i("Got a purchase: $purchase; but signature is bad. Skipping...")
            return
        }

        Timber.i("Got a verified purchase: $purchase")

        mPurchases.add(purchase)
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private fun onQueryPurchasesFinished(result: PurchasesResult) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (mBillingClient == null || result.responseCode != BillingResponse.OK) {
            Timber.i("""Billing client was null or result code (${result.responseCode}) was bad - quitting""")
            return
        }

        Timber.i("Query inventory was successful.")

        // Update the UI and purchases inventory with new list of purchases
        mPurchases.clear()
        onPurchasesUpdated(BillingResponse.OK, result.purchasesList)
    }

    /**
     * Checks if subscriptions are supported for current client
     *
     * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     *
     */
    fun areSubscriptionsSupported(): Boolean {
        val responseCode = mBillingClient!!.isFeatureSupported(FeatureType.SUBSCRIPTIONS)
        if (responseCode != BillingResponse.OK) {
            Timber.i("areSubscriptionsSupported() got an error response: $responseCode")
        }
        return responseCode == BillingResponse.OK
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    fun queryPurchases() {
        val queryToExecute = Runnable {
            val time = System.currentTimeMillis()
            val purchasesResult = mBillingClient!!.queryPurchases(SkuType.INAPP)
            Timber.i("Querying purchases elapsed time: ${System.currentTimeMillis() - time}ms")
            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                val subscriptionResult = mBillingClient!!.queryPurchases(SkuType.SUBS)
                Timber.i("Querying purchases and subscriptions elapsed time: ${System.currentTimeMillis() - time}ms")
                Timber.i("Querying subscriptions result code: ${subscriptionResult.responseCode} res: ${subscriptionResult.purchasesList.size}")

                if (subscriptionResult.responseCode == BillingResponse.OK) {
                    purchasesResult.purchasesList.addAll(
                            subscriptionResult.purchasesList)
                } else {
                    Timber.e("Got an error response trying to query subscription purchases")
                }
            } else if (purchasesResult.responseCode == BillingResponse.OK) {
                Timber.i("Skipped subscription purchases query since they are not supported")
            } else {
                Timber.i("queryPurchases() got an error response code: ${purchasesResult.responseCode}")
            }
            onQueryPurchasesFinished(purchasesResult)
        }

        executeServiceRequest(queryToExecute)
    }

    fun startServiceConnection(executeOnSuccess: Runnable?) {
        mBillingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
                Timber.i("Setup finished. Response code: $billingResponseCode")

                if (billingResponseCode == BillingResponse.OK) {
                    mIsServiceConnected = true
                    executeOnSuccess?.run()
                }
                billingClientResponseCode = billingResponseCode
            }

            override fun onBillingServiceDisconnected() {
                mIsServiceConnected = false
            }
        })
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (mIsServiceConnected) {
            runnable.run()
        } else {
            // If billing service was disconnected, we try to reconnect 1 time.
            // (feel free to introduce your retry policy here).
            startServiceConnection(runnable)
        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     *
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     *
     */
    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            verifyPurchase(mActivity.getString(R.string.key), signedData, signature)
        } catch (e: IOException) {
            e.log()
            false
        }

    }

    companion object {
        // Default value of mBillingClientResponseCode until BillingManager was not yeat initialized
        val BILLING_MANAGER_NOT_INITIALIZED = -1
    }
}

