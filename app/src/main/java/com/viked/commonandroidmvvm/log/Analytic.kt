package com.viked.commonandroidmvvm.log

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import io.fabric.sdk.android.Fabric
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 1/20/18.
 */
@Singleton
class Analytic @Inject constructor(val context: Application) {

    private lateinit var answers: Answers
    private var fabric: Fabric? = null

    fun init() {
        fabric = Fabric.with(context, Crashlytics(), Answers())
        answers = Answers.getInstance()
    }

    fun log(event: CustomEvent) {
        fabric?.run { answers.logCustom(event) }
    }

}