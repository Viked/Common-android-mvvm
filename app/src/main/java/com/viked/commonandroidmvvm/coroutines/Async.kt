package com.viked.commonandroidmvvm.coroutines

import androidx.arch.core.util.Function
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.data.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Async<X, Y>(private val function: Function<X, Y>) : Function<X, LiveData<Resource<Y>>> {

    override fun apply(input: X): LiveData<Resource<Y>> {
        val result = MutableLiveData<Resource<Y>>()

        GlobalScope.launch {
            try {
                val r = function.apply(input)
                withContext(Dispatchers.Main) { result.value = Resource.success(r) }
            } catch (e: Exception) {
                e.log()
                withContext(Dispatchers.Main) { result.value = Resource.error(TextWrapper(e.localizedMessage?: e.message?: "Error")) }
            }
        }
        return result
    }

}