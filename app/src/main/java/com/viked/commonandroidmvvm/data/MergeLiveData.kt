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
import com.viked.commonandroidmvvm.utils.doIf
import kotlinx.coroutines.*

class MergeLiveData(private val sources: List<LiveData<*>>,
                    private val progress: LiveData<List<Progress>>,
                    private val builder: Strategy,
                    private val comparator: (() -> Comparator<ItemWrapper>)? = null) : MediatorLiveData<Resource<List<ItemWrapper>>>(), Observer<Any?> {

    init {
        addSource(Transformations.map(progress) { p -> p as Any? }, this)
        listenSources()
    }

    private var updateJob: Job? = null

    private fun listenSources() = sources.forEach { addSource(Transformations.map(it) { p -> p }, this) }

    override fun onChanged(t: Any?) {
        val active = progress.value ?: listOf()
        if (active.isEmpty()) {
            setNewValue()
        } else {
            updateJob?.cancel()
            postValue(Resource.status((active.minBy { it.value }!!).getMessage()))
        }
    }

    private fun setNewValue() {
        updateJob?.cancel()
        updateJob = GlobalScope.launch {
            try {
                val values = sources.mapNotNull { it.value }
                val comparator = comparator?.invoke()
                val r = builder.convert(values).doIf(comparator != null) { it.sortedWith(comparator!!) }
                withContext(Dispatchers.Main) { postValue(Resource.success(r)) }
            } catch (e: Exception) {
                e.log()
                withContext(Dispatchers.Main) {
                    postValue(Resource.error(TextWrapper(e.localizedMessage ?: e.message
                    ?: "Error")))
                }
            }
        }
    }

    interface Strategy {
        fun convert(list: List<*>): List<ItemWrapper>
    }

}