package com.viked.commonandroidmvvm.work

import androidx.work.Worker


object AndroidWorkerInjection {

    fun inject(worker: Worker) {
        checkNotNull(worker) { "worker" }
        val application = worker.applicationContext
        if (application !is HasWorkerInjector) {
            throw RuntimeException(
                    String.format(
                            "%s does not implement %s",
                            application.javaClass.canonicalName,
                            HasWorkerInjector::class.java.canonicalName))
        }

        val workerInjector = (application as HasWorkerInjector).workerInjector()
        checkNotNull(workerInjector) { "${application.javaClass}.workerInjector() returned null" }
        workerInjector.inject(worker)
    }

}