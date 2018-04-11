package com.viked.commonandroidmvvm.rx

import com.viked.commonandroidmvvm.log.log
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RxHelper @Inject constructor() : Consumer<Throwable> {
    override fun accept(t: Throwable?) {
        if (t is UndeliverableException) {
            Timber.e(t.cause)
        } else {
            t?.log()
        }
    }

    fun init() {
        RxJavaPlugins.setErrorHandler(this)
    }

}