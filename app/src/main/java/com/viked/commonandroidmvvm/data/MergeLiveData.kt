package com.viked.commonandroidmvvm.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.progress.Progress
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MergeLiveData(private val sources: List<LiveData<*>>,
                    private val progress: LiveData<List<Progress>>,
                    private val builder: (List<*>) -> List<ItemWrapper>,
                    private val progressBuilder: (Progress) -> TextWrapper) : MediatorLiveData<Resource<List<ItemWrapper>>>(), Observer<Any?> {

    init {
        addSource(Transformations.map(progress) { p -> p as Any? }, this)
        listenSources()
    }

    private fun listenSources() = sources.forEach { addSource(Transformations.map(it) { p -> p }, this) }

    override fun onChanged(t: Any?) {
        val active = progress.value ?: listOf()
        if (active.isEmpty()) {
            val values = sources.mapNotNull { it.value }
            GlobalScope.launch {
                try {
                    val r = builder.invoke(values)
                    withContext(Dispatchers.Main) { postValue(Resource.success(r)) }
                } catch (e: Exception) {
                    e.log()
                    withContext(Dispatchers.Main) {
                        postValue(Resource.error(TextWrapper(e.localizedMessage ?: e.message
                        ?: "Error")))
                    }
                }
            }
        } else {
            postValue(Resource.status(progressBuilder(active.minBy { it.value }!!)))
        }
    }
}