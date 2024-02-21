package com.viked.commonandroidmvvm.billing

import android.app.Activity
import android.app.Application
import android.text.format.DateUtils.SECOND_IN_MILLIS
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetailsResponseListener
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.data.Resource
import com.viked.commonandroidmvvm.ui.dialog.purchase.PurchaseItemWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


const val MAX_CURRENT_PURCHASES_ALLOWED = 1
const val accountId = ""
const val profileId = ""

/**
 * Created by yevgeniishein on 3/2/18.
 */
@Singleton
class BillingRepository @Inject constructor(
    private val application: Application,
    private val billingSecurity: BillingSecurity,
    private val billingItems: Set<@JvmSuppressWildcards BillingItem>
) :
    DefaultLifecycleObserver, PurchasesUpdatedListener, BillingClientStateListener,
    ProductDetailsResponseListener, PurchasesResponseListener {

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    private val externalScope: CoroutineScope by lazy { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    private val billingOffers: BillingOffers by lazy { BillingOffers() }

    /** A reference to BillingClient  */
    private lateinit var billingClient: BillingClient
    private var connectionAttempt = 0

    private val products = MutableLiveData<List<ProductDetails>>()
    private val purchases = MutableLiveData<List<Purchase>>()

    val list = MediatorLiveData<Resource<List<ItemWrapper>>>().apply {
        addSource(products) { updateList() }
        addSource(purchases) { updateList() }
    }

    val hasValidSubscription = MediatorLiveData<Boolean>().apply {
        value = billingItems.any { it.isSubscription }
        addSource(purchases) {
            val value = it.any { p -> p.purchaseState == Purchase.PurchaseState.PURCHASED }
            postValue(value)
            updateValidSubscription(it)
        }
    }

    private fun updateList() {
        val productsValue = products.value ?: listOf()
        val purchasesValue = purchases.value ?: listOf()

        val productMap = productsValue.associateBy { it.productId }
        val purchasesMap =
            purchasesValue.map { p -> p.products.map { Pair(it, p) } }.flatten().toMap()
        val billingItemsMap = billingItems.associateBy { it.productId }

        val listWrappers = billingItemsMap.keys.filter { productMap.containsKey(it) }.map {
            PurchaseItemWrapper(
                productMap.getValue(it),
                billingItemsMap.getValue(it),
                purchasesMap[it]
            )
        }.sortedBy { it.billingItem.order }

        list.postValue(Resource.success(listWrappers))
    }

    private fun updateValidSubscription(purchaseList: List<Purchase>) {
        externalScope.launch {
            val hasValid = try {
                hasValidSubscription(purchaseList)
            } catch (e: Exception) {
                e.log()
                false
            }
            hasValidSubscription.postValue(hasValid)
        }
    }

    private fun hasValidSubscription(purchases: List<Purchase>): Boolean {
        if (purchases.isEmpty()) {
            return false
        }

        for (purchase in purchases) {
            for (productId in purchase.products) {
                val billingItem = billingItems.firstOrNull { it.productId == productId }
                if (billingItem != null && billingItem.isSubscription) {
                    if (billingSecurity.verifyPurchase(
                            true,
                            purchase.purchaseToken,
                            productId
                        )
                    ) {
                        if (!purchase.isAcknowledged) {
                            acknowledgePurchase(purchase.purchaseToken)
                        }
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.i("BillingRepository - onPurchasesUpdated: $responseCode $debugMessage")

        when (responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases == null) {
                    Timber.i("BillingRepository - onPurchasesUpdated: null purchase list")
                    processPurchases(null)
                } else {
                    processPurchases(purchases)
                    updateValidSubscription(purchases)
                }
            }

            BillingClient.BillingResponseCode.USER_CANCELED -> {
                Timber.i("BillingRepository - onPurchasesUpdated: User canceled the purchase")
            }

            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                Timber.i("BillingRepository - onPurchasesUpdated: The user already owns this item")
            }

            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> {
                Timber.i(
                    "BillingRepository - onPurchasesUpdated: Developer error means that Google Play " +
                            "does not recognize the configuration. If you are just getting started, " +
                            "make sure you have configured the application correctly in the " +
                            "Google Play Console. The product ID must match and the APK you " +
                            "are using must be signed with release keys."
                )
            }

            else -> Timber.i("BillingRepository - onPurchasesUpdated: Got unknown resultCode")
        }
    }

    /**
     * Instantiate a new BillingClient instance.
     */
    override fun onCreate(owner: LifecycleOwner) {
        Timber.i("BillingRepository - onCreate")
        billingClient =
            BillingClient.newBuilder(application).enablePendingPurchases().setListener(this).build()
        if (!billingClient.isReady) {
            Timber.i("BillingRepository - start connection")
            billingClient.startConnection(this)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Timber.i("BillingRepository - onDestroy")
        if (billingClient.isReady) {
            Timber.i("BillingRepository - end connection")
            // BillingClient can only be used once.
            // After calling endConnection(), we must create a new BillingClient.
            billingClient.endConnection()
        }
    }

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.i("BillingRepository - Billing setup finished: $responseCode $debugMessage")
        if (responseCode == BillingClient.BillingResponseCode.OK) {
            connectionAttempt = 0
            // The billing client is ready.
            queryProductDetails()
            queryPurchases()
        }
    }

    override fun onBillingServiceDisconnected() {
        Timber.i("BillingRepository - Billing service disconnected")
        externalScope.launch {
            delay((++connectionAttempt) * SECOND_IN_MILLIS)
            billingClient.startConnection(this@BillingRepository)
        }
    }

    /**
     * In order to make purchases, you need the [ProductDetails] for the item or subscription.
     * This is an asynchronous call that will receive a result in [onProductDetailsResponse].
     *
     * queryProductDetails uses method calls from GPBL 5.0.0. PBL5, released in May 2022,
     * is backwards compatible with previous versions.
     * To learn more about this you can read https://developer.android.com/google/play/billing/compatibility
     */
    private fun queryProductDetails() {
        Timber.i("BillingRepository - query product details")
        val params = QueryProductDetailsParams.newBuilder()

        val productList: MutableList<QueryProductDetailsParams.Product> = arrayListOf()
        for (item in billingItems) {
            val type = if (item.isSubscription) {
                BillingClient.ProductType.SUBS
            } else {
                BillingClient.ProductType.INAPP
            }
            productList.add(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(item.productId)
                    .setProductType(type)
                    .build()
            )
        }

        params.setProductList(productList).let { productDetailsParams ->
            Timber.i("BillingRepository - query product details async")
            billingClient.queryProductDetailsAsync(productDetailsParams.build(), this)
        }
    }

    /**
     * Receives the result from [queryProductDetails].
     *
     * Store the ProductDetails and post them in the [products]. This allows other parts
     * of the app to use the [ProductDetails] to show product information and make purchases.
     *
     * onProductDetailsResponse() uses method calls from GPBL 5.0.0. PBL5, released in May 2022,
     * is backwards compatible with previous versions.
     * To learn more about this you can read https://developer.android.com/google/play/billing/compatibility
     */
    override fun onProductDetailsResponse(
        billingResult: BillingResult,
        productDetailsList: List<ProductDetails?>
    ) {
        Timber.i("BillingRepository - product details response")
        val response = BillingResponse(billingResult.responseCode)
        val debugMessage = billingResult.debugMessage
        when {
            response.isOk -> {
                val expectedProductDetailsCount = billingItems.size
                if (productDetailsList.isEmpty()) {
                    products.postValue(emptyList())
                    Timber.e(
                        "BillingRepository - product details response: " +
                                "Expected ${expectedProductDetailsCount}, " +
                                "Found null ProductDetails. " +
                                "Check to see if the products you requested are correctly published " +
                                "in the Google Play Console."
                    )
                } else {
                    products.postValue(productDetailsList.filterNotNull().also { postedValue ->
                        val productDetailsCount = postedValue.size
                        if (productDetailsCount == expectedProductDetailsCount) {
                            Timber.i("BillingRepository - product details response: Found $productDetailsCount ProductDetails")
                        } else {
                            Timber.e(
                                "BillingRepository - product details response: " +
                                        "Expected ${expectedProductDetailsCount}, " +
                                        "Found $productDetailsCount ProductDetails. " +
                                        "Check to see if the products you requested are correctly published " +
                                        "in the Google Play Console."
                            )
                        }
                    }
                    )
                }
            }

            response.isTerribleFailure -> {
                // These response codes are not expected.
                Timber.i("BillingRepository - product details response: : ${response.code} $debugMessage")
            }

            else -> {
                Timber.e("BillingRepository - product details response: : ${response.code} $debugMessage")
            }

        }
    }

    /**
     * Callback from the billing library when queryPurchasesAsync is called.
     */
    override fun onQueryPurchasesResponse(p0: BillingResult, p1: List<Purchase?>) {
        processPurchases(p1.filterNotNull())
    }

    /**
     * Send purchase to LiveData, which will trigger network call to verify the subscriptions
     * on the sever.
     */
    private fun processPurchases(purchasesList: List<Purchase>?) {
        Timber.i("BillingRepository - process purchases: ${purchasesList?.size} purchase(s)")
        if (purchasesList.isNullOrEmpty()) {
            Timber.i("BillingRepository - process purchases: No purchases found")
            return
        }
        purchases.postValue(purchasesList)
        logAcknowledgementStatus(purchasesList)
    }

    /**
     * Log the number of purchases that are acknowledge and not acknowledged.
     *
     * https://developer.android.com/google/play/billing/billing_library_releases_notes#2_0_acknowledge
     *
     * When the purchase is first received, it will not be acknowledge.
     * This application sends the purchase token to the server for registration. After the
     * purchase token is registered to an account, the Android app acknowledges the purchase token.
     * The next time the purchase list is updated, it will contain acknowledged purchases.
     */
    private fun logAcknowledgementStatus(purchasesList: List<Purchase>) {
        var acknowledgedCounter = 0
        var unacknowledgedCounter = 0
        for (purchase in purchasesList) {
            if (purchase.isAcknowledged) {
                acknowledgedCounter++
            } else {
                unacknowledgedCounter++
            }
        }
        Timber.i(
            "BillingRepository - log acknowledgement status: ${purchasesList.size} purchase(s)" +
                    " acknowledged $acknowledgedCounter unacknowledged $unacknowledgedCounter"
        )
    }

    /**
     * Use the Google Play Billing Library to make a purchase.
     *
     * @param productDetails ProductDetails object returned by the library.
     * @param activity [Activity] instance.
     */
    fun buy(
        productDetails: ProductDetails,
        activity: Activity
    ) {
        val currentPurchases = purchases.value ?: emptyList()
        val offerToken = billingOffers.getOfferToken(productDetails)
        val oldPurchaseToken: String

        // Get current purchase. In this app, a user can only have one current purchase at
        // any given time.
        if (currentPurchases.isNotEmpty() &&
            currentPurchases.size == MAX_CURRENT_PURCHASES_ALLOWED
        ) {
            // This either an upgrade, downgrade, or conversion purchase.
            val currentPurchase = currentPurchases.first()

            // Get the token from current purchase.
            oldPurchaseToken = currentPurchase.purchaseToken

            val billingParams = offerToken?.let {
                upDowngradeBillingFlowParamsBuilder(
                    productDetails = productDetails,
                    offerToken = it,
                    oldToken = oldPurchaseToken
                )
            }

            if (billingParams != null) {
                launchBillingFlow(
                    activity,
                    billingParams
                )
            }
        } else if (currentPurchases.isEmpty()) {
            // This is a normal purchase.
            val billingParams = offerToken?.let {
                billingFlowParamsBuilder(
                    productDetails = productDetails,
                    offerToken = it
                )
            }

            if (billingParams != null) {
                launchBillingFlow(
                    activity,
                    billingParams.build()
                )
            }
        } else {
            // The developer has allowed users  to have more than 1 purchase, so they need to
            /// implement a logic to find which one to use.
            Timber.i("BillingRepository - launch billing flow: User has more than $MAX_CURRENT_PURCHASES_ALLOWED current purchase.")
        }
    }

    /**
     * Start a purchase or subscription replace flow
     */
    private fun launchBillingFlow(activity: Activity, params: BillingFlowParams): Int {
        if (!billingClient.isReady) {
            Timber.i("BillingRepository - launch billing flow: BillingClient is not ready")
            return BillingClient.BillingResponseCode.DEVELOPER_ERROR
        }

        val billingResult = billingClient.launchBillingFlow(activity, params)
        val responseCode = billingResult.responseCode
        val debugMessage = billingResult.debugMessage
        Timber.i("BillingRepository - launch billing flow: BillingResponse $responseCode $debugMessage")
        return responseCode
    }

    /**
     * Query Google Play Billing for existing purchases.
     *
     * New purchases will be provided to the PurchasesUpdatedListener.
     * You still need to check the Google Play Billing API to know when purchase tokens are removed.
     */
    private fun queryPurchases() {
        Timber.i("BillingRepository - query purchases")
        if (!billingClient.isReady) {
            Timber.e("BillingRepository - query purchases: BillingClient is not ready")
            billingClient.startConnection(this)
            return
        }
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build(), this
        )
    }


    /**
     * BillingFlowParams Builder for upgrades and downgrades.
     *
     * @param productDetails ProductDetails object returned by the library.
     * @param offerToken the least priced offer's offer id token returned by
     * @param oldToken the purchase token of the subscription purchase being upgraded or downgraded.
     *
     * @return [BillingFlowParams] builder.
     */
    private fun upDowngradeBillingFlowParamsBuilder(
        productDetails: ProductDetails,
        offerToken: String,
        oldToken: String
    ): BillingFlowParams {
        return BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        ).setSubscriptionUpdateParams(
            BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                .setOldPurchaseToken(oldToken)
                .setSubscriptionReplacementMode(BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE)
                .build()
        )
            .setObfuscatedAccountId(accountId)
            .setObfuscatedProfileId(profileId)
            .build()
    }

    /**
     * BillingFlowParams Builder for normal purchases.
     *
     * @param productDetails ProductDetails object returned by the library.
     * @param offerToken the least priced offer's offer id token returned by
     *
     * @return [BillingFlowParams] builder.
     */
    private fun billingFlowParamsBuilder(
        productDetails: ProductDetails,
        offerToken: String
    ): BillingFlowParams.Builder {
        return BillingFlowParams.newBuilder().setProductDetailsParamsList(
            listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(productDetails)
                    .setOfferToken(offerToken)
                    .build()
            )
        )
            .setObfuscatedAccountId(accountId)
            .setObfuscatedProfileId(profileId)
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        Timber.i("BillingRepository - acknowledge purchase")
        val params = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchaseToken)
            .build()
        billingClient.acknowledgePurchase(
            params
        ) { billingResult ->
            val responseCode = billingResult.responseCode
            val debugMessage = billingResult.debugMessage
            Timber.i("BillingRepository - acknowledge purchase:  $responseCode $debugMessage")
        }
    }
}