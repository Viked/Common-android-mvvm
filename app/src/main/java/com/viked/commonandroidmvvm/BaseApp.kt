package com.viked.commonandroidmvvm

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.WorkManager
import com.viked.commonandroidmvvm.log.Analytic
import com.viked.commonandroidmvvm.log.NotLoggingTree
import com.viked.commonandroidmvvm.preference.PreferenceHelper
import com.viked.commonandroidmvvm.preference.getLivePreference
import com.viked.commonandroidmvvm.work.InjectionWorkerFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject


/**
 * Created by yevgeniishein on 1/20/18.
 */
abstract class BaseApp : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var analytic: Analytic

    @Inject
    lateinit var preferenceHelper: PreferenceHelper

    @Inject
    lateinit var workerFactory: InjectionWorkerFactory

    private val darkThemeLiveData: LiveData<Int> by lazy {
        preferenceHelper.getLivePreference(R.string.preference_dark_mode)
    }

    private val darkThemeLiveDataObserver: Observer<Int> by lazy {
        Observer<Int> {
            when (it) {
                AppCompatDelegate.MODE_NIGHT_YES -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
                AppCompatDelegate.MODE_NIGHT_NO -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }
    }

    abstract fun inject()

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        inject()
        initLogger()
        initWorkManager()
        preferenceHelper.init()
        darkThemeLiveData.observeForever(darkThemeLiveDataObserver)
    }

    private fun initLogger() {
        if (!BuildConfig.DEBUG) {
            Timber.plant(NotLoggingTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initWorkManager() {
        val workManagerConfig = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        WorkManager.initialize(this, workManagerConfig)
    }

    override fun onLowMemory() {
        darkThemeLiveData.removeObserver(darkThemeLiveDataObserver)
        super.onLowMemory()
    }

}