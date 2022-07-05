package com.viked.commonandroidmvvm.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.viked.commonandroidmvvm.log.log

abstract class BaseWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    abstract suspend fun work(): Result

    override suspend fun doWork(): Result {
        return try {
            work()
        } catch (e: Exception) {
            e.log()
            Result.failure()
        }
    }
}

interface ListenableWorkerFactory<T : ListenableWorker> {
    fun create(context: Context, workerParams: WorkerParameters): T
}