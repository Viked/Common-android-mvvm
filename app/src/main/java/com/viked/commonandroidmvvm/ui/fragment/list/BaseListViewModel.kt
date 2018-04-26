package com.viked.commonandroidmvvm.ui.fragment.list

import android.databinding.ObservableArrayList
import android.databinding.ObservableBoolean
import android.databinding.ObservableList
import android.support.v4.widget.SwipeRefreshLayout
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.rx.SubscriptionBuilder
import com.viked.commonandroidmvvm.ui.adapters.list.ItemWrapper
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel

/**
 * Created by yevgeniishein on 10/15/17.
 */
abstract class BaseListViewModel<T>(titleId: Int = 0) : BaseViewModel(titleId), SwipeRefreshLayout.OnRefreshListener {

    private val listLoadKey = 1

    open val emptyListMessageId = R.string.empty_list

    val list: ObservableList<ItemWrapper> = ObservableArrayList<ItemWrapper>()

    val empty = ObservableBoolean(false)

    open val listProgress: ObservableBoolean = progress

    abstract fun getListSubscriptionBuilder(): SubscriptionBuilder<T>

    abstract fun getList(model: T): List<ItemWrapper>

    override fun onCleared() {
        list.clear()
        super.onCleared()
    }

    override fun loadData() {
        super.loadData()
        subscribe({ addListSubscription(getListSubscriptionBuilder()) }, listLoadKey, silent = true)
    }

    open fun addListSubscription(subscriptionBuilder: SubscriptionBuilder<T>) =
            subscriptionBuilder
                    .addOnSubscribe {
                        listProgress.set(true)
                        empty.set(false)
                    }
                    .addOnError {
                        list.clear()
                        empty.set(true)
                        listProgress.set(false)
                    }
                    .addOnNext {
                        list.clear()
                        list.addAll(getList(it))
                        empty.set(list.isEmpty())
                        listProgress.set(false)
                    }
                    .addOnDispose {
                        list.clear()
                        empty.set(true)
                        listProgress.set(false)
                    }


    override fun onRefresh() {
        loadData()
    }

}