package com.viked.commonandroidmvvm.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import com.viked.commonandroidmvvm.text.TextWrapper
import com.viked.commonandroidmvvm.ui.data.Resource

fun <T, Y> LiveData<Resource<T>>.transform(map: (T) -> Y): LiveData<Resource<Y>> = Transformations.map(this) {
    if (it.data != null)
        Resource.success(map.invoke(it.data))
    else
        Resource.error(it.status ?: TextWrapper())
}

fun <T> MutableLiveData<T>.edit(map: (T) -> Unit) {
    val v = value
    if (v != null) {
        map.invoke(v)
        postValue(v)
    }
}

fun <T> MutableLiveData<Resource<T>>.update(map: (T) -> T) = value?.let {
    postValue(
            if (it.data != null)
                Resource.success(map.invoke(it.data))
            else
                Resource.error(TextWrapper()))
}