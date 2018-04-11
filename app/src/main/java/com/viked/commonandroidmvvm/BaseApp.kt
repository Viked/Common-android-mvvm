package com.viked.commonandroidmvvm

import android.app.Activity
import android.app.Application
import com.evernote.android.job.JobCreator
import com.evernote.android.job.JobManager
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.NotLoggingTree
import com.viked.commonandroidmvvm.rx.RxHelper
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by yevgeniishein on 1/20/18.
 */
abstract class BaseApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var analytic: Analytic

    @Inject
    lateinit var jobCreator: JobCreator

    @Inject
    lateinit var rxHelper: RxHelper

    abstract fun inject()

    override fun activityInjector(): DispatchingAndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }

    override fun onCreate() {
        super.onCreate()
        inject()
        initFabric()
        initLogger()
        initRx()

        JobManager.create(this).addJobCreator(jobCreator)
    }

    private fun initFabric() {
        if (!BuildConfig.DEBUG) {
            analytic.init()
        }
    }

    private fun initLogger() {
        if (!BuildConfig.DEBUG) {
            Timber.plant(NotLoggingTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initRx() {
        rxHelper.init()
    }

}