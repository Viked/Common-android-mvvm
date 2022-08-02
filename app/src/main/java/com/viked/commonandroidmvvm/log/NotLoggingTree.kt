package com.viked.commonandroidmvvm.log

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import timber.log.Timber

/**
 * Created by shein on 2/7/2018.
 */
class NotLoggingTree : Timber.Tree() {

    private val crashlytics: FirebaseCrashlytics by lazy { Firebase.crashlytics }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val prefix = if (tag != null) "$tag: " else ""
        crashlytics.log("$prefix$message")
        if (t != null) {
            crashlytics.recordException(t)
        }
    }
}