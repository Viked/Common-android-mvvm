package com.viked.commonandroidmvvm

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import androidx.work.Worker
import com.viked.commonandroidmvvm.billing.BillingRepository
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.NotLoggingTree
import com.viked.commonandroidmvvm.preference.PreferenceHelper
import com.viked.commonandroidmvvm.work.HasWorkerInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by yevgeniishein on 1/20/18.
 */
abstract class BaseApp : Application(), HasActivityInjector, HasWorkerInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var workerDispatchingAndroidInjector: DispatchingAndroidInjector<Worker>

    @Inject
    lateinit var analytic: Analytic

    @Inject
    lateinit var billingRepository: BillingRepository

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    abstract fun inject()

    override fun activityInjector() = dispatchingAndroidInjector

    override fun workerInjector() = workerDispatchingAndroidInjector

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        inject()
        initLogger()

        billingRepository.subscribe()
    }

    private fun initLogger() {
        if (!BuildConfig.DEBUG) {
            Timber.plant(NotLoggingTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }

    override fun onLowMemory() {
        billingRepository.unsubscribe()
        super.onLowMemory()
    }

}