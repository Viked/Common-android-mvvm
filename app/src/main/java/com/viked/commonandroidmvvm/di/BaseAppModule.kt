package com.viked.commonandroidmvvm.di

import android.arch.lifecycle.ViewModelProvider
import androidx.work.Worker
import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.multibindings.Multibinds


@Module
abstract class BaseAppModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Multibinds
    internal abstract fun workerInjectorFactories(): Map<Class<out Worker>, @JvmSuppressWildcards AndroidInjector.Factory<Worker>>
}