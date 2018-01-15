package com.viked.commonandroidmvvm.ui.list

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.support.v4.widget.SwipeRefreshLayout
import com.viked.commonandroidmvvm.R
import com.viked.commonandroidmvvm.rx.SubscriptionBuilder
import com.viked.commonandroidmvvm.ui.fragment.BaseViewModel

/**
 * Created by yevgeniishein on 10/15/17.
 */
abstract class BaseListViewModel(titleId: Int = 0) : BaseViewModel(titleId), SwipeRefreshLayout.OnRefreshListener {

    private val listLoadKey = 1

    open val emptyListMessageId = R.string.empty_list

    val list: ObservableList<ItemWrapper> = ObservableArrayList<ItemWrapper>()

    var empty: ObservableListEmpty = ObservableListEmpty(list)

    abstract fun getListSubscriptionBuilder(): SubscriptionBuilder<List<ItemWrapper>>

    override fun onCleared() {
        empty.clear()
        super.onCleared()
    }

    override fun loadData() {
        subscribe({ addListSubscription(getListSubscriptionBuilder()) }, listLoadKey, force = list.isNotEmpty())
    }

    open fun addListSubscription(subscriptionBuilder: SubscriptionBuilder<List<ItemWrapper>>) =
            subscriptionBuilder.addOnSubscribe { list.clear() }
                    .addOnError { list.clear() }
                    .addOnNext {
                        list.clear()
                        list.addAll(it)
                    }

    override fun onRefresh() {
        loadData()
    }
}