package com.viked.commonandroidmvvm.billing

import android.app.Activity
import android.databinding.ObservableField
import com.android.billingclient.api.*
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.ui.adapters.AdapterDelegate
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 * Created by yevgeniishein on 3/14/18.
 */
class BillingDelegate(private val activity: Activity,
                      private val billingHelper: BillingHelper,
                      private val repository: BillingRepository) : AdapterDelegate, PurchasesUpdatedListener {

    /** A reference to BillingClient  */
    private val billingClient = ObservableField<BillingClient>()

    override fun subscribe() {
        Timber.i("Creating Billing client.")
        billingClient.set(BillingClient.newBuilder(activity).setListener(this).build())
        Timber.i("Starting setup.")
    }

    override fun unsubscribe() {
        val billingClient = billingClient.get() ?: return
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
        this.billingClient.set(null)
    }

    override fun update() {
        val billingClient = billingClient.get() ?: return
        if (activity.isFinishing) return
        billingClient.querySkuDetails()
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(skuId: String, @BillingClient.SkuType billingType: String) {
        initiatePurchaseFlow(skuId, null, billingType)
    }

    /**
     * Start a purchase or subscription replace flow
     */
    fun initiatePurchaseFlow(skuId: String, oldSkus: ArrayList<String>?, @BillingClient.SkuType billingType: String) {
        billingClient.get()?.doWithConnection {
            Timber.i("Launching in-app purchase flow. Replace old SKU? ${(oldSkus != null)}")
            val purchaseParams = BillingFlowParams
                    .newBuilder()
                    .setSku(skuId).setType(billingType)
                    .setOldSkus(oldSkus)
                    .build()
            it.launchBillingFlow(activity, purchaseParams)
        }
    }

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     *
     * Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     *
     */
    private fun verifyValidSignature(key: String, signedData: String, signature: String): Boolean {
        return try {
            verifyPurchase(key, signedData, signature)
        } catch (e: IOException) {
            e.log()
            false
        }
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
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
                repository.updatePurchase(if (purchases?.isNotEmpty() == true) {
                    val key = activity.getString(R.string.key)
                    purchases.filter { verifyValidSignature(key, it.originalJson, it.signature) }
                } else listOf())
            }
            BillingClient.BillingResponse.USER_CANCELED -> Timber.i("onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            else -> Timber.i("onPurchasesUpdated() got unknown resultCode: $responseCode")
        }
    }

    /**
     * Queries for in-app and subscriptions SKU details
     */
    private fun BillingClient.querySkuDetails() {
        val dataList = mutableListOf<SkuDetails>()

        getSku(dataList, billingHelper.suscriptionsSkuIds, BillingClient.SkuType.SUBS, Runnable {
            // Once we added all the subscription items, fill the in-app items rows below
            getSku(dataList, billingHelper.purchaseSkuIds, BillingClient.SkuType.INAPP, Runnable {
                repository.updateSkuDetails(dataList)
                queryPurchases()
            })
        })
    }

    private fun BillingClient.getSku(inList: MutableList<SkuDetails>, skuList: List<String>,
                                     @BillingClient.SkuType billingType: String, executeWhenFinished: Runnable?) {
        doWithConnection {
            val params = SkuDetailsParams
                    .newBuilder()
                    .setSkusList(skuList)
                    .setType(billingType)
                    .build()
            it.querySkuDetailsAsync(params, { responseCode, skuDetailsList ->
                if (responseCode != BillingClient.BillingResponse.OK) {
                    Timber.i("Unsuccessful query for type: $billingType. Error code: $responseCode")
                } else if (skuDetailsList != null && skuDetailsList.size > 0) {
                    skuDetailsList.forEach { details ->
                        Timber.i("Adding sku: $details")
                        inList.add(details)
                    }
                }

                executeWhenFinished?.run()
            })
        }
    }


    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    private fun BillingClient.queryPurchases() {
        doWithConnection {
            val time = System.currentTimeMillis()
            val purchasesResult = it.queryPurchases(BillingClient.SkuType.INAPP)
            Timber.i("Querying purchases elapsed time: ${System.currentTimeMillis() - time}ms")
            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                val subscriptionResult = it.queryPurchases(BillingClient.SkuType.SUBS)
                Timber.i("Querying purchases and subscriptions elapsed time: ${System.currentTimeMillis() - time}ms")
                Timber.i("Querying subscriptions result code: ${subscriptionResult.responseCode} res: ${subscriptionResult.purchasesList.size}")

                if (subscriptionResult.responseCode == BillingClient.BillingResponse.OK) {
                    purchasesResult.purchasesList.addAll(
                            subscriptionResult.purchasesList)
                } else {
                    Timber.e("Got an error response trying to query subscription purchases")
                }
            } else if (purchasesResult.responseCode == BillingClient.BillingResponse.OK) {
                Timber.i("Skipped subscription purchases query since they are not supported")
            } else {
                Timber.i("queryPurchases() got an error response code: ${purchasesResult.responseCode}")
            }
            // Have we been disposed of in the meantime? If so, or bad result code, then quit
            if (purchasesResult.responseCode != BillingClient.BillingResponse.OK) {
                Timber.i("Billing client was null or result code (${purchasesResult.responseCode}) was bad - quitting")
            } else {
                Timber.i("Query inventory was successful.")
                onPurchasesUpdated(BillingClient.BillingResponse.OK, purchasesResult.purchasesList)
            }
        }
    }

    /**
     * Checks if subscriptions are supported for current client
     *
     * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     *
     */
    private fun BillingClient.areSubscriptionsSupported(): Boolean {
        val responseCode = isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (responseCode != BillingClient.BillingResponse.OK) {
            Timber.i("areSubscriptionsSupported() got an error response: $responseCode")
        }
        return responseCode == BillingClient.BillingResponse.OK
    }


    private fun BillingClient.doWithConnection(action: (BillingClient) -> Unit) {
        if (isReady) {
            action.invoke(this)
        } else {
            startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                    Timber.i("Setup finished. Response code: $billingResponseCode")

                    if (billingResponseCode == BillingClient.BillingResponse.OK) {
                        action.invoke(this@doWithConnection)
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })
        }

    }
}