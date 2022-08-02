package com.viked.commonandroidmvvm.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.viked.commonandroidmvvm.log.log
import timber.log.Timber

abstract class BaseWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    abstract suspend fun work(): Result

    override suspend fun doWork(): Result {
        val name = this::class.java.simpleName
        Timber.i("Work $name started")
        return try {
            work().apply {
                Timber.i("Work $name finished with result $this")
            }
        } catch (e: Exception) {
            Timber.i("Work $name finished unexpectedly with exception")
            e.log()
            Result.failure()
        }
    }
}

interface ListenableWorkerFactory<T : ListenableWorker> {
    fun create(context: Context, workerParams: WorkerParameters): T
}