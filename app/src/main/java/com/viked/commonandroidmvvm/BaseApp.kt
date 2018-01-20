package com.viked.commonandroidmvvm

import android.app.Activity
import android.app.Application
import com.viked.commonandroidmvvm.log.Analytic
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by yevgeniishein on 1/20/18.
 */
abstract class BaseApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var analytic: Analytic

    abstract fun inject()

    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        inject()
        initFabric()
    }

    private fun initFabric() {
        if (!BuildConfig.DEBUG) {
            analytic.init()
        }
    }

}