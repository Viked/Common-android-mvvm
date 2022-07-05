package com.viked.commonandroidmvvm.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InjectionWorkerFactory @Inject constructor(creators: Map<Class<out ListenableWorker>, @JvmSuppressWildcards ListenableWorkerFactory<out ListenableWorker>>) :
    WorkerFactory() {

    private val stringClassCreators = creators.mapKeys { it.key.name }

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        val creator = stringClassCreators[workerClassName]
        return creator?.create(appContext, workerParameters)
    }

}
