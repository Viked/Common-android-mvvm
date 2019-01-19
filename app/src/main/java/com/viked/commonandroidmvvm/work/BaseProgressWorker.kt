package com.viked.commonandroidmvvm.work

import android.content.Context
import android.support.annotation.StringRes
import androidx.work.WorkerParameters
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.progress.Progress
import com.viked.commonandroidmvvm.progress.ProgressDao
import javax.inject.Inject

abstract class BaseProgressWorker(context: Context, workerParams: WorkerParameters) : BaseWorker(context, workerParams) {

    @Inject
    lateinit var progressDao: ProgressDao

    private var progress = 0

    @get:StringRes
    open val messageId: Int = R.string.message_valued_loading

    protected fun updateProgress(progress: Int) {
        if (this.progress != progress) {
            this.progress = progress
            progressDao.set(Progress(this::class.java.simpleName, progress, messageId))
        }
    }

    protected fun updateProgress(current: Int, max: Int) {
        val progress = Math.round(current.toFloat() / max.toFloat() * 100f)
        updateProgress(progress)
    }

    protected fun finish() {
        progressDao.delete(Progress(this::class.java.simpleName, progress, messageId))
    }

}