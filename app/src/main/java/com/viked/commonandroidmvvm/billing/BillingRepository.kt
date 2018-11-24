package com.viked.commonandroidmvvm.billing

import android.app.Activity
import android.app.Application
import android.arch.core.util.Function
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.os.Bundle
import com.android.billingclient.api.*
import com.viked.commonandroidmvvm.coroutines.Async
import com.viked.commonandroidmvvm.ui.data.Resource
import com.viked.commonandroidmvvm.ui.dialog.purchase.PurchaseItemWrapper
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 3/2/18.
 */
@Singleton
class BillingRepository @Inject constructor(private val application: Application,
                                            private val billingSecurity: BillingSecurity,
                                            private val billingItems: Set<@JvmSuppressWildcards BillingItem>) : Application.ActivityLifecycleCallbacks, PurchasesUpdatedListener {

    /** A reference to BillingClient  */
    private lateinit var billingClient: BillingClient

    private lateinit var purchaseSkuIds: List<String>
    private lateinit var subscriptionsSkuIds: List<String>

    private var activity: Activity? = null

    val list = MutableLiveData<Resource<List<PurchaseItemWrapper>>>()
    val subscription = Transformations.switchMap(list, Async<Resource<List<PurchaseItemWrapper>>, Boolean>(Function { l ->
        val subscriptionSkus = l.data?.filter { it.billingItem.isSubscription && it.purchase != null }
                ?: emptyList()
        if (subscriptionSkus.isEmpty()) return@Function false
        subscriptionSkus.map { it.purchase!! }.find { billingSecurity.verifyPurchase(true, it.purchaseToken, it.sku) } != null
    }))

    fun subscribe() {
        Timber.i("Creating Billing client.")
        billingClient = BillingClient.newBuilder(application).setListener(this).build()
        purchaseSkuIds = billingItems.filter { it.isSubscription.not() }.map { it.sku }
        subscriptionsSkuIds = billingItems.filter { it.isSubscription }.map { it.sku }

        application.registerActivityLifecycleCallbacks(this)

        billingClient.querySkuDetails()

        Timber.i("End setup.")
    }

    fun unsubscribe() {
        if (billingClient.isReady) {
            billingClient.endConnection()
        }
    }

//    fun hasValidPurchase(sku: String) = purchaseProcessor.flatMapSingle {
//        val p = it.find { it.sku == sku } ?: return@flatMapSingle Single.just(false)
//        billingSecurity.verifyPurchase(subscriptionsSkuIds.contains(p.sku), p.purchaseToken, p.sku)
//    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> {
                val l = list.value?.data
                if (l == null || purchases == null) return
                purchases.forEach { p -> l.find { i -> i.billingItem.sku == p.sku }?.purchase = p }
                list.postValue(Resource.success(l))
            }
            BillingClient.BillingResponse.USER_CANCELED -> Timber.i("onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            else -> Timber.i("onPurchasesUpdated() got unknown resultCode: $responseCode")
        }
    }

    override fun onActivityPaused(activity: Activity?) {
        //Ignore
    }

    override fun onActivityResumed(activity: Activity?) {
        this.activity = activity
    }

    override fun onActivityStarted(activity: Activity?) {
        //Ignore
    }

    override fun onActivityDestroyed(activity: Activity?) {
        //Ignore
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
        //Ignore
    }

    override fun onActivityStopped(activity: Activity?) {
        //Ignore
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
        //Ignore
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(skuId: String) {
        initiatePurchaseFlow(skuId, null, if (subscriptionsSkuIds.contains(skuId)) BillingClient.SkuType.SUBS else BillingClient.SkuType.INAPP)
    }

    /**
     * Start a purchase or subscription replace flow
     */
    fun initiatePurchaseFlow(skuId: String, oldSkus: ArrayList<String>?, @BillingClient.SkuType billingType: String) {
        billingClient.doWithConnection {
            if (activity == null) return@doWithConnection
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
     * Queries for in-app and subscriptions SKU details
     */
    private fun BillingClient.querySkuDetails() {
        val dataList = mutableListOf<SkuDetails>()

        getSku(dataList, subscriptionsSkuIds, BillingClient.SkuType.SUBS, Runnable {
            // Once we added all the subscription items, fill the in-app items rows below
            getSku(dataList, purchaseSkuIds, BillingClient.SkuType.INAPP, Runnable {
                val our = dataList.map { item -> PurchaseItemWrapper(item, billingItems.find { it.sku == item.sku }!!) }
                        .sortedBy { it.billingItem.order }

                list.postValue(Resource.success(our))
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
            it.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
                if (responseCode != BillingClient.BillingResponse.OK) {
                    Timber.i("Unsuccessful query for type: $billingType. Error code: $responseCode")
                } else if (skuDetailsList != null && skuDetailsList.size > 0) {
                    skuDetailsList.forEach { details ->
                        Timber.i("Adding sku: $details")
                        inList.add(details)
                    }
                }

                executeWhenFinished?.run()
            }
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
//                Timber.i("Querying purchases and subscriptions elapsed time: ${System.currentTimeMillis() - time}ms")
//                Timber.i("Querying subscriptions result code: ${subscriptionResult.responseCode} res: ${subscriptionResult.purchasesList?.size
//                        ?: 0}")

                if (subscriptionResult.responseCode == BillingClient.BillingResponse.OK && purchasesResult.purchasesList != null && subscriptionResult.purchasesList != null) {
                    purchasesResult.purchasesList.addAll(subscriptionResult.purchasesList)
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