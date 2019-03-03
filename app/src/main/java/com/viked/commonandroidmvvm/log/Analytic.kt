package com.viked.commonandroidmvvm.log

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.crashlytics.android.answers.PurchaseEvent
import com.viked.commonandroidmvvm.BuildConfig
import io.fabric.sdk.android.Fabric
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 1/20/18.
 */
@Singleton
class Analytic @Inject constructor(val context: Application) {

    private val answers: Answers by lazy { Answers.getInstance() }
    private val fabric: Fabric? by lazy {
        if (!BuildConfig.DEBUG) {
            Fabric.with(context, Crashlytics(), Answers())
        } else null
    }

    fun log(event: CustomEvent) {
        fabric?.run { answers.logCustom(event) }
    }

    fun logPurchase(event: PurchaseEvent) {
        fabric?.run { answers.logPurchase(event) }
    }

}