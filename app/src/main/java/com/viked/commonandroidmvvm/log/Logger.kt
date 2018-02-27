package com.viked.commonandroidmvvm.log

import android.util.Log

/**
 * Created by yevgeniishein on 10/9/17.
 */

fun Throwable.log() {
    Log.e("Error", message ?: "", this)
    /*
    if (BuildConfig.DEBUG) {
        Log.e("Error", message ?: "", this)
    } else {
        Crashlytics.logException(this)
    }
    */
}