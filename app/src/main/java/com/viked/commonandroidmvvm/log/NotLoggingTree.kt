package com.viked.commonandroidmvvm.log

import com.crashlytics.android.Crashlytics
import timber.log.Timber

/**
 * Created by shein on 2/7/2018.
 */
class NotLoggingTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (t != null) {
            Crashlytics.logException(t)
        }
    }
}