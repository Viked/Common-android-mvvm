package com.viked.commonandroidmvvm.log

import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 1/20/18.
 */
@Singleton
class Analytic @Inject constructor(val context: Application) {

    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }

    fun log(name: String, params: Bundle? = null) {
        analytics.logEvent(name, params)
    }

    fun setScreen(name: String, clazz: Class<*>) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, name)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, clazz.simpleName)
        }
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}