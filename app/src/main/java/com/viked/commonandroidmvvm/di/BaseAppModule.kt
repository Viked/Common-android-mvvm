package com.viked.commonandroidmvvm.di

import android.arch.lifecycle.ViewModelProvider
import com.evernote.android.job.JobCreator
import com.viked.commonandroidmvvm.bg.JobHelper
import dagger.Binds
import dagger.Module

@Module
abstract class BaseAppModule {

    @Binds
    abstract fun bindJobHelper(jobHelper: JobHelper): JobCreator

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}