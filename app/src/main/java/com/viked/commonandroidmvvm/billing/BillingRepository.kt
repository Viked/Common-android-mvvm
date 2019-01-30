package com.viked.commonandroidmvvm.billing

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import com.android.billingclient.api.*
import com.viked.commonandroidmvvm.data.lazySuspendFun
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.ui.data.Resource
import com.viked.commonandroidmvvm.ui.dialog.purchase.PurchaseItemWrapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by yevgeniishein on 3/2/18.
 */
@Singleton
class BillingRepository @Inject constructor(private val application: Application,
                                            private val billingSecurity: BillingSecurity,
                                            private val billingItems: Set<@JvmSuppressWildcards BillingItem>) :
        Application.ActivityLifecycleCallbacks, PurchasesUpdatedListener {

    /** A reference to BillingClient  */
    private val billingClient = GlobalScope.lazySuspendFun<BillingClient?> {
        val client = BillingClient.newBuilder(application).setListener(this).build()
        suspendCoroutine { continuation ->
            client.startConnection(object : BillingClientStateListener {

                var done = false

                override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                    Timber.i("Setup finished. Response code: $billingResponseCode")
                    if (!done && billingResponseCode == BillingClient.BillingResponse.OK) {
                        done = true
                        continuation.resume(client)
                    }
                }

                override fun onBillingServiceDisconnected() {

                }
            })
        }
    }

    private val purchaseSkuIds = billingItems.filter { it.isSubscription.not() }.map { it.sku }
    private val subscriptionsSkuIds = billingItems.filter { it.isSubscription }.map { it.sku }

    private var activity: Activity? = null

    private val products = MutableLiveData<List<SkuDetails>>()
    private val subscriptions = MutableLiveData<List<SkuDetails>>()
    private val purchases = MutableLiveData<List<Purchase>>()


    val list = MediatorLiveData<Resource<List<PurchaseItemWrapper>>>().apply {
        addSource(products) { updateList() }
        addSource(subscriptions) { updateList() }
        addSource(purchases) { updateList() }
    }

    val hasValidSubscription = MediatorLiveData<Boolean>().apply {
        value = billingItems.any { it.isSubscription }
        addSource(purchases) { updateValidSubscription() }
        addSource(subscriptions) { updateValidSubscription() }
    }

    private fun updateList() {
        val productsValue = products.value ?: emptyList()
        val subscriptionsValue = subscriptions.value ?: emptyList()
        val purchasesValue = purchases.value ?: emptyList()

        val skuMap = (productsValue + subscriptionsValue).map { Pair(it.sku, it) }.toMap()
        val purchasesMap = purchasesValue.map { Pair(it.sku, it) }.toMap()
        val billingItemsMap = billingItems.map { Pair(it.sku, it) }.toMap()

        val listWrappers = billingItemsMap.keys.filter { skuMap.containsKey(it) }.map {
            PurchaseItemWrapper(skuMap.getValue(it), billingItemsMap.getValue(it), purchasesMap[it])
        }.sortedBy { it.billingItem.order }

        list.postValue(Resource.success(listWrappers))
    }

    private fun updateValidSubscription() {
        val subscriptionList = subscriptions.value?.map { it.sku } ?: return
        val purchaseList = purchases.value ?: return

        if (subscriptionList.isEmpty() || purchaseList.isEmpty()) {
            hasValidSubscription.postValue(false)
        } else {
            GlobalScope.launch {
                val hasValid = try {
                    purchaseList.filter { subscriptionList.contains(it.sku) }
                            .any { billingSecurity.verifyPurchase(true, it.purchaseToken, it.sku) }
                } catch (e: Exception) {
                    e.log()
                    false
                }
                hasValidSubscription.postValue(hasValid)
            }
        }
    }


    fun subscribe() {
        Timber.i("Creating Billing client.")
        application.registerActivityLifecycleCallbacks(this)

        querySkuDetails()

        Timber.i("End setup.")
    }

    fun unsubscribe() = GlobalScope.launch {
        val client = billingClient() ?: return@launch
        if (client.isReady) {
            client.endConnection()
        }
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: List<Purchase>?) {
        when (responseCode) {
            BillingClient.BillingResponse.OK -> this.purchases.postValue(purchases ?: emptyList())
            BillingClient.BillingResponse.USER_CANCELED -> Timber.i("onPurchasesUpdated() - user cancelled the purchase flow - skipping")
            BillingClient.BillingResponse.DEVELOPER_ERROR,
            BillingClient.BillingResponse.ERROR,
            BillingClient.BillingResponse.BILLING_UNAVAILABLE,
            BillingClient.BillingResponse.SERVICE_DISCONNECTED,
            BillingClient.BillingResponse.SERVICE_UNAVAILABLE -> {
                if (this.purchases.value == null) {
                    this.purchases.postValue(emptyList())
                }
            }
            BillingClient.BillingResponse.FEATURE_NOT_SUPPORTED,
            BillingClient.BillingResponse.ITEM_ALREADY_OWNED,
            BillingClient.BillingResponse.ITEM_NOT_OWNED,
            BillingClient.BillingResponse.ITEM_UNAVAILABLE -> Timber.e(RuntimeException("onPurchasesUpdated() got resultCode: $responseCode"))
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
    private fun initiatePurchaseFlow(skuId: String, oldSkus: ArrayList<String>?, @BillingClient.SkuType billingType: String) = GlobalScope.launch {
        val client = billingClient() ?: return@launch
        if (activity == null) return@launch
        Timber.i("Launching in-app purchase flow. Replace old SKU? ${(oldSkus != null)}")
        val purchaseParams = BillingFlowParams
                .newBuilder()
                .setSku(skuId).setType(billingType)
                .setOldSkus(oldSkus)
                .build()
        client.launchBillingFlow(activity, purchaseParams)
    }

    /**
     * Queries for in-app and subscriptions SKU details
     */
    private fun querySkuDetails() {
        if (subscriptionsSkuIds.isNotEmpty()) {
            getSku(subscriptionsSkuIds, BillingClient.SkuType.SUBS) { subscriptions.postValue(it) }
        }
        if (purchaseSkuIds.isNotEmpty()) {
            getSku(purchaseSkuIds, BillingClient.SkuType.INAPP) { products.postValue(it) }
        }
        queryPurchases()
    }

    private fun getSku(skuList: List<String>,
                       @BillingClient.SkuType billingType: String, executeWhenFinished: (List<SkuDetails>) -> Unit) = GlobalScope.launch {
        val client = billingClient()
        if (client == null) {
            executeWhenFinished(emptyList())
            return@launch
        }
        val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(billingType)
                .build()
        client.querySkuDetailsAsync(params) { responseCode, skuDetailsList ->
            if (responseCode != BillingClient.BillingResponse.OK) {
                Timber.i("Unsuccessful query for type: $billingType. Error code: $responseCode")
                executeWhenFinished(skuDetailsList)
            } else if (skuDetailsList != null) {
                Timber.i("Successful query for type: $billingType. Response code: $responseCode")
                executeWhenFinished(skuDetailsList)
            }
        }
    }


    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    private fun queryPurchases() = GlobalScope.launch {
        val client = billingClient()
        if (client == null) {
            onPurchasesUpdated(BillingClient.BillingResponse.OK, emptyList())
            return@launch
        }

        val time = System.currentTimeMillis()
        val purchasesResult = client.queryPurchases(BillingClient.SkuType.INAPP)
        Timber.i("Querying purchases elapsed time: ${System.currentTimeMillis() - time}ms")
        // If there are subscriptions supported, we add subscription rows as well
        if (areSubscriptionsSupported().await()) {
            val subscriptionResult = client.queryPurchases(BillingClient.SkuType.SUBS)

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

    /**
     * Checks if subscriptions are supported for current client
     *
     * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     *
     */
    private fun areSubscriptionsSupported() = GlobalScope.async {
        val client = billingClient() ?: return@async false
        val responseCode = client.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (responseCode != BillingClient.BillingResponse.OK) {
            Timber.i("Error response: $responseCode")
        }
        responseCode == BillingClient.BillingResponse.OK
    }

}