package com.viked.commonandroidmvvm.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.progress.Progress
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.data.Resource
import com.viked.commonandroidmvvm.utils.doIf
import kotlinx.coroutines.*
import timber.log.Timber

class MergeLiveData(
    private val sources: List<LiveData<*>>,
    private val progress: LiveData<List<Progress>>,
    private val preferences: LiveData<Map<Int, Any>>,
    private val builder: Strategy,
    private val comparator: (() -> Comparator<ItemWrapper>)? = null
) : MediatorLiveData<Resource<List<ItemWrapper>>>(), Observer<Any?> {

    init {
        addSource(preferences, this)
        addSource(progress.map { p -> p as Any? }, this)
        listenSources()
    }

    private var updateJob: Job? = null

    private fun listenSources() =
        sources.forEach { addSource(it.map{ p -> p }, this) }

    override fun onChanged(t: Any?) {
        val active = progress.value ?: listOf()
        if (active.isEmpty()) {
            setNewValue()
        } else {
            updateJob?.cancel()
            postValue(Resource.status((active.minByOrNull { it.value }!!).getMessage()))
        }
    }

    private fun setNewValue() {
        updateJob?.cancel()
        updateJob = GlobalScope.launch {
            try {
                val values = sources.mapNotNull { it.value }
                val comparator = comparator?.invoke()
                val r = builder.convert(
                    values, preferences.value
                        ?: mapOf()
                ).doIf(comparator != null) { it.sortedWith(comparator!!) }
                withContext(Dispatchers.Main) { postValue(Resource.success(r)) }
            } catch (e: CancellationException) {
                Timber.i(e)
            } catch (e: Exception) {
                e.log()
                withContext(Dispatchers.Main) {
                    postValue(
                        Resource.error(
                            TextWrapper(
                                e.localizedMessage ?: e.message
                                ?: "Error"
                            )
                        )
                    )
                }
            }
        }
    }

    interface Strategy {
        fun convert(list: List<*>, preferences: Map<Int, Any>): List<ItemWrapper>
    }

}