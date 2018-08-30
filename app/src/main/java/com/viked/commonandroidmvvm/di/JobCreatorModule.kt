package com.viked.commonandroidmvvm.di

import com.evernote.android.job.JobCreator
import com.viked.commonandroidmvvm.bg.JobHelper
import dagger.Binds
import dagger.Module

@Module
abstract class JobCreatorModule {

    @Binds
    abstract fun bindJobHelper(jobHelper: JobHelper): JobCreator

}