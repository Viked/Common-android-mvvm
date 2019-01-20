package com.viked.commonandroidmvvm.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.viked.commonandroidmvvm.log.log

abstract class BaseWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    abstract fun work(): Result

    override fun doWork(): Result {
        AndroidWorkerInjection.inject(this)
        return try {
            work()
        } catch (e: Exception) {
            e.log()
            Result.FAILURE
        }
    }
}