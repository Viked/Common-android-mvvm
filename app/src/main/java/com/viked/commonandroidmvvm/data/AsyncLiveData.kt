package com.viked.commonandroidmvvm.data

import androidx.lifecycle.MutableLiveData
import com.viked.commonandroidmvvm.log.log
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.data.Resource
import com.viked.commonandroidmvvm.utils.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AsyncLiveData<T>(private val load: () -> T) : MutableLiveData<Resource<T>>() {

    override fun onActive() {
        super.onActive()
        coroutineScope.launch {
            try {
                val r = load.invoke()
                withContext(Dispatchers.Main) { postValue(Resource.success(r)) }
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

}