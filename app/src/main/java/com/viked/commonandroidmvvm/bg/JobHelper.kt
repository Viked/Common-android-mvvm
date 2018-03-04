package com.viked.commonandroidmvvm.bg

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Created by yevgeniishein on 3/4/18.
 */
@Singleton
class JobHelper @Inject constructor(private val creators: Map<String, @JvmSuppressWildcards Provider<Job>>) : JobCreator {

    override fun create(tag: String) = creators[tag]?.get()

}